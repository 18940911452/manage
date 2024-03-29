package wg.openmob.mobileimsdk.server.qos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import wg.nettime.mobileimsdk.server.netty.MBObserver;
import wg.openmob.mobileimsdk.server.handler.ServerCoreHandler;
import wg.openmob.mobileimsdk.server.handler.ServerLauncher;
import wg.openmob.mobileimsdk.server.event.MessageQoSEventListenerS2C;
import wg.openmob.mobileimsdk.server.protocal.Protocal;
import wg.openmob.mobileimsdk.server.utils.LocalSendHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QoS4SendDaemonRoot
{
	private static Logger logger = LoggerFactory.getLogger(QoS4SendDaemonRoot.class);  
	
	private boolean DEBUG = false;
	
	/**
	 * 用于QoS事件中没有实时重传成功的消息的回调通知。
	 * 
	 * <p>
	 * 请在本类调用 {@link #startup(boolean)} 前设置本参数，否则当实时重传
	 * 没有成功时，无法发出回调通知哦。
	 * 
	 * @see #notifyMessageLost(ArrayList)
	 * @see ServerCoreHandler#getServerMessageQoSEventListener()
	 * @see MessageQoSEventListenerS2C#messagesLost(ArrayList)
	 */
	private ServerLauncher serverLauncher = null;
	
	/**
	 * 已发送的需QoS支持的消息列表，同步Hash实现（因为本类中可能存在不
	 * 同的线程同时remove或遍历之）.
	 * 
	 * <p>
	 * 本列表中的消息在发
	 */
	private ConcurrentHashMap<String, Protocal> sentMessages = new ConcurrentHashMap<String, Protocal>();
	// 同步Hash，因为本类中可能存在不同的线程同时remove或遍历之
	/** 
	 * 本Hash表目前仅用于QoS重传判断是否是“刚刚”发出的消息之用，别无它用。
	 * 
	 * @see MESSAGES_JUST$NOW_TIME
	 * @since 2.1.1
	 */
	private ConcurrentHashMap<String, Long> sendMessagesTimestamp = new ConcurrentHashMap<String, Long>();

	/** 
	 * QoS质量保证线程心跳间隔（单位：毫秒），默认5000ms.
	 * <p>
	 * 间隔越短则为用户重发越即时，但将使得重复发送的可能性增大（因为可能在应答
	 * 包尚在途中时就判定丢包了的错误情况），当然，即使真存在重复发送的可能也是无害的
	 * ，因为MobileIMSDK的QoS机制本身就有防重能力。请根据您的应用所处的网络延迟情况进行
	 * 权衡，本参数为非关键参数，如无特殊情况则建议无需调整本参数。
	 */
	private int CHECH_INTERVAL = 5000;
	
	/**
	 * “刚刚”发出的消息阀值定义（单位：毫秒），默认2000毫秒。
	 * <b>注意：此值通常无需由开发者自行设定，保持默认即可。</b>
	 * <p>
	 * 此阀值的作用在于：在QoS=true的情况下，一条刚刚发出的消息会同时保存到本类中的QoS保证队列，
	 * 在接收方的应答包还未被发出方收到时（已经发出但因为存在数十毫秒的网络延迟，应答包正在路上）
	 * ，恰好遇到本次QoS质量保证心跳间隔的到来，因为之前的QoS队列罗辑是只要存在本队列中还未被去掉
	 * 的包，就意味着是要重传的——那么此逻辑在我们本次讨论的情况下就存在漏洞而导致没有必要的重传了。
	 * 如果有本阀值存在，则即使刚刚发出的消息刚放到QoS队列就遇到QoS心跳到来，则只要当前放入队列的时间
	 * 小于或等于本值，就可以被认为是刚刚放入，那么也就避免被误重传了。
	 * <p>
	 * 基于以上考虑，本值的定义，只要设定这大于一条消息的发出起到收到它应答包为止这样一个时间间隔即可
	 * （其实就相于一个客户端到服务端的网络延迟时间4倍多一点点即可）。
	 * 此处定为2秒其实是为了保守望起见哦。
	 * <p>
	 * 本参数将决定真正因为UDP丢包而重传的即时性问题，即当MobileIMSDK的UDP丢包时，QoS首次重传的
	 * 响应时间为大于 {@link #MESSAGES_JUST$NOW_TIME} 而 小于等于 {@link #CHECH_INTERVAL}。
	 * 
	 * @since 2.1.1
	 */
	private int MESSAGES_JUST$NOW_TIME = 2 * 1000;
	
	/** 
	 * 一个包允许的最大重发次数，默认1次。
	 * <p>
	 * 次数越多，则整个UDP的可靠性越好，但在网络确实很烂的情况下可能会导致重传的泛滥而失去
	 * “即时”的意义。请根据网络状况和应用体验来权衡设定，本参数为0表示不重传，建议使用1到5
	 * 之间的数字。
	 */
	private int QOS_TRY_COUNT = 1;
	
	private boolean _excuting = false;
	
	private Timer timer = null;
	
	/** 仅用于子类继承后输出log时能以此tag为标识进行区分，方便调试，仅此而已 */
	private String debugTag = "";
	
	public QoS4SendDaemonRoot(int CHECH_INTERVAL
			, int MESSAGES_JUST$NOW_TIME
			, int QOS_TRY_COUNT
			, boolean DEBUG, String debugTag)
	{
		if(CHECH_INTERVAL > 0)
			this.CHECH_INTERVAL = CHECH_INTERVAL;
		if(MESSAGES_JUST$NOW_TIME > 0)
			this.MESSAGES_JUST$NOW_TIME = MESSAGES_JUST$NOW_TIME;
		if(QOS_TRY_COUNT >= 0)
			this.QOS_TRY_COUNT = QOS_TRY_COUNT;
		this.DEBUG = DEBUG;
		this.debugTag = debugTag;
	}
	
	private void doTaskOnece()
	{
		// 极端情况下本次循环内可能执行时间超过了时间间隔，此处是防止在前一
		// 次还没有运行完的情况下又重复执行，从而出现无法预知的错误
		if(!_excuting)
		{
			// 丢包列表
			ArrayList<Protocal> lostMessages = new ArrayList<Protocal>();
			_excuting = true;
			try
			{
				if(DEBUG && sentMessages.size() > 0)
					logger.debug("【IMCORE-netty"+this.debugTag+"】【QoS发送方】=========== 消息发送质量保证线程运行中, 当前需要处理的列表长度为"+sentMessages.size()+"...");

				// 开始处理中 ************************************************
				//** 遍历HashMap方法一
//				for(String key : sentMessages.keySet())
//				{
//					final Protocal p = sentMessages.get(key);
				//** 遍历HashMap方法二（在大数据量情况下，方法二的性能要5倍优于方法一）
				Iterator<Entry<String, Protocal>> entryIt = sentMessages.entrySet().iterator();  
			    while(entryIt.hasNext())
			    {  
			        Entry<String, Protocal> entry = entryIt.next();  
			        String key = entry.getKey();  
			        final Protocal p = entry.getValue();
			        
					if(p != null && p.isQoS())
					{
						// 达到或超过了最大重试次数（判定丢包）
						if(p.getRetryCount() >= QOS_TRY_COUNT)
						{
							if(DEBUG)
								logger.debug("【IMCORE-netty"+this.debugTag+"】【QoS发送方】指纹为"+p.getFp()
										+"的消息包重传次数已达"+p.getRetryCount()+"(最多"+QOS_TRY_COUNT+"次)上限，将判定为丢包！");

							// 将这个包加入到丢包列表（该Protocal对象将是一个clone的全新对象而非原来的引用哦！）
							lostMessages.add((Protocal)p.clone());

							// 从列表中称除之
							remove(p.getFp());
						}
						// 没有达到重传上限则开始进行重传
						else
						{
							//### 2015104 Bug Fix: 解决了无线网络延较大时，刚刚发出的消息在其应答包还在途中时被错误地进行重传
							long delta = System.currentTimeMillis() - sendMessagesTimestamp.get(key);
							// 该消息包是“刚刚”发出的，本次不需要重传它哦
							if(delta <= MESSAGES_JUST$NOW_TIME)
							{
								if(DEBUG)
									logger.warn("【IMCORE-netty"+this.debugTag+"】【QoS发送方】指纹为"+key+"的包距\"刚刚\"发出才"+delta
										+"ms(<="+MESSAGES_JUST$NOW_TIME+"ms将被认定是\"刚刚\"), 本次不需要重传哦.");
							}
							//### 2015103 Bug Fix END
							else
							{
								// Netty的数据发送结果观察者：netty的数据发送结果是通过异步通知来实现的（这就
								// 是异步编程模型，跟Nodejs的promise、Androi里的RxJava、iOS的block道理一样）
								MBObserver sendResultObserver = new MBObserver(){
									@Override
									public void update(boolean sendOK, Object extraObj)
									{
										// 已成功重传
										if(sendOK)
										{
											if(DEBUG)
											{
												logger.debug("【IMCORE-netty"+debugTag+"】【QoS发送方】指纹为"+p.getFp()
														+"的消息包已成功进行重传，此次之后重传次数已达"
														+p.getRetryCount()+"(最多"+QOS_TRY_COUNT+"次).");
											}
										}
										else
										{
											if(DEBUG)
											{
												logger.warn("【IMCORE-netty"+debugTag+"】【QoS发送方】指纹为"+p.getFp()
														+"的消息包重传失败，它的重传次数之前已累计为"
														+p.getRetryCount()+"(最多"+QOS_TRY_COUNT+"次).");
											}
										}
									}
								};
								
								// 发送数据
								LocalSendHelper.sendData(p, sendResultObserver);

								//** 【服务端的QoS重传机制与客户端的实现在算法上存在差异】
								//** 服务端为了性能和负载压力能尽快得到释放，只负责尝试重传，无论成功与否，
								//** 重传次数都会+1，这也就意味着，极端情况下可能重传一次都没有成功发出过，
								//** 但这并不影响QoS机制的设计初衷：虽没有实时发出，但将会因重传最大次数
								//** 的到来而抛给离线机制作离线处理，同样也达到了质量保证的目的（并没有丢
								//** 失消息的发送，只是做为离线处理了呢）。尽快完成重传将避免可能存在的因
								//** 重传消息积压而发生雪崩效应的风险，这是服务端编程必须要考虑到的。
								// 重传次数+1
								p.increaseRetryCount();
							}
						}
					}
					// value值为null，从列表中去掉吧
					else
					{
//						sentMessages.remove(key);
						remove(key);
					}
				}
			}
			catch (Exception eee)
			{
				if(DEBUG)
					logger.warn("【IMCORE-netty"+this.debugTag+"】【QoS发送方】消息发送质量保证线程运行时发生异常,"+eee.getMessage(), eee);
			}

			if(lostMessages != null && lostMessages.size() > 0)
				// 通知观察者这些包丢包了（目标接收者没有收到）
				notifyMessageLost(lostMessages);

			//
			_excuting = false;
		}
	}
	
	/**
	 * 将未送达信息反馈给消息监听者。
	 * 
	 * @param lostMessages 已被判定为“消息未送达”的消息列表
	 * @see {@link ServerLauncher.getServerMessageQoSEventListener()}
	 */
	protected void notifyMessageLost(ArrayList<Protocal> lostMessages)
	{
		if(serverLauncher != null && serverLauncher.getServerMessageQoSEventListener() != null)
			serverLauncher.getServerMessageQoSEventListener().messagesLost(lostMessages);
	}
	
	/**
	 * 启动线程。
	 * <p>
	 * 无论本方法调用前线程是否已经在运行中，都会尝试首先调用 {@link #stop()}方法，
	 * 以便确保线程被启动前是真正处于停止状态，这也意味着可无害调用本方法。
	 * <p>
	 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
	 * 
	 * @param immediately true表示立即执行线程作业，否则直到 {@link #CHECH_INTERVAL}
	 * 执行间隔的到来才进行首次作业的执行
	 */
	public QoS4SendDaemonRoot startup(boolean immediately)
	{
		// ** 先确保之前定时任务被停止
		stop();
		
		// ** 启动定时任务
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() 
		{
			@Override
			public void run()
			{
				doTaskOnece();
			}
		}
		// 首次执行时的延迟
		, immediately ? 0 : CHECH_INTERVAL
		// 之后每次执行时的固定间隔时间（使用scheduleAtFixedRate而不
		// 是schedule目的是希望以真正的固定间隔、而非固定延迟）
		, CHECH_INTERVAL);
		
		//
//		running = true;
		logger.debug("【IMCORE-netty"+this.debugTag+"】【QoS发送方】=========== 消息发送质量保证线程已成功启动");
		
		return this;
	}
	
	/**
	 * 无条件中断本线程的运行。
	 * <p>
	 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
	 */
	public void stop()
	{
		if(timer != null)
		{
			try{
				timer.cancel();
			}
			finally{
				timer = null;
			}
		}
	}
	
	/**
	 * 线程是否正在运行中。
	 * 
	 * @return true表示是，否则线路处于停止状态
	 */
	public boolean isRunning()
	{
		return timer != null;
	}
	
	/**
	 * 该包是否已存在于队列中。
	 * 
	 * @param fingerPrint 消息包的特纹特征码（理论上是唯一的）
	 * @return
	 */
	public boolean exist(String fingerPrint)
	{
		return sentMessages.get(fingerPrint) != null;
	}
	
	/**
	 * 推入一个消息包的指纹特征码.
	 * <br>注意：本方法只会将指纹码推入，而不是将整个Protocal对象放入列表中。
	 * 
	 * @param p
	 */
	public void put(Protocal p)
	{
		if(p == null)
		{
			if(DEBUG)
				logger.warn(this.debugTag+"Invalid arg p==null.");
			return;
		}
		if(p.getFp() == null)
		{
			if(DEBUG)
				logger.warn(this.debugTag+"Invalid arg p.getFp() == null.");
			return;
		}
		
		if(!p.isQoS())
		{
			if(DEBUG)
				logger.warn(this.debugTag+"This protocal is not QoS pkg, ignore it!");
			return;
		}
		
		// 如果列表中已经存则仅提示（用于debug）
		if(sentMessages.get(p.getFp()) != null)
		{
			if(DEBUG)
				logger.warn("【IMCORE-netty"+this.debugTag+"】【QoS发送方】指纹为"+p.getFp()+"的消息已经放入了发送质量保证队列，该消息为何会重复？（生成的指纹码重复？还是重复put？）");
		}
		
		// save it
		sentMessages.put(p.getFp(), p);
		// 同时保存时间戳
		sendMessagesTimestamp.put(p.getFp(), System.currentTimeMillis());
	}
	
	/**
	 * 移除一个消息包.
	 * 
	 * @param fingerPrint 消息包的特纹特征码（理论上是唯一的）
	 * @return
	 */
	public void remove(final String fingerPrint)
	{
		//### 20151129 Bug Fix: 解决了之前错误地在服务端实现本remove方法时
		//	使用了SwingWorker而导致一段时间后一定几率下整个Timer不能正常工作了（OOM）
		try
		{
			// remove it
			sendMessagesTimestamp.remove(fingerPrint);
			Object result = sentMessages.remove(fingerPrint);
			if(DEBUG)
				logger.warn("【IMCORE-netty"+this.debugTag+"】【QoS发送方】指纹为"+fingerPrint+"的消息已成功从发送质量保证队列中移除(可能是收到接收方的应答也可能是达到了重传的次数上限)，重试次数="
						+(result != null?((Protocal)result).getRetryCount():"none呵呵."));
		}
		catch (Exception e)
		{
			if(DEBUG)
				logger.warn("【IMCORE-netty"+this.debugTag+"】【QoS发送方】remove(fingerPrint)时出错了：", e);
		}
		//### 20151129 Bug Fix END 
	}
	
	/**
	 * 队列大小.
	 * 
	 * @return
	 * @see HashMap#size()
	 */
	public int size()
	{
		return sentMessages.size();
	}

	/**
	 * 设置用于QoS事件中没有实时重传成功的消息的回调通知事件宿主。
	 * 
	 * <p>
	 * 请在本类调用 {@link #startup(boolean)} 前设置之，否则当实时重传
	 * 没有成功时，无法发出回调通知哦。
	 * 
	 * @see #notifyMessageLost(ArrayList)
	 * @see ServerCoreHandler#getServerMessageQoSEventListener()
	 * @see MessageQoSEventListenerS2C#messagesLost(ArrayList)
	 */
	public void setServerLauncher(ServerLauncher serverLauncher)
	{
		this.serverLauncher = serverLauncher;
	}

	public QoS4SendDaemonRoot setDebugable(boolean debugable)
	{
		this.DEBUG = debugable;
		return this;
	}
	
	public boolean isDebugable()
	{
		return this.DEBUG;
	}
}
