package wg.nettime.mobileimsdk.server.bridge;

import java.io.IOException;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.RecoveryListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 一个可重用的RabbitMQ服务提供者类，可供处理一个生产者队列和一个消费者队列。
 * <p>
 * 本类的代码经过高度提炼和内聚，不限于MobileIMSDK使用，可用于任意合适场景。
 * <p>
 *  【本类中实现的功能有】：<br>
 *  <ul>
 *  <li>1）一个消息生产者（子类可决定是否使用）；</li>
 *  <li>2）一个消息消费者（子类可决定是否使用）；</li>
 *  <li>3）与MQ服务器的断线重连和恢复能力；</li>
 *  <li>4）发送出错暂存到缓存数组（内存中），并在下次重连正常时自动重发；</li>
 *  <li>5）遵照官方的最佳实践：复用同的是一个连接（connection）、各自两个channel（一个用于生产者、一个用于消费者）；</li>
 *  <li>6）消费者手动ACK能力：业务层处理不成功可重新放回队列。</li>
 *  </ul>
 * 
 * @since 3.0
 */
public class MQProvider
{
	private static Logger logger = LoggerFactory.getLogger(MQProvider.class);  
	
	/** 发出消息的字符编码格式。默认utf-8。 */
	public final static String DEFAULT_ENCODE_CHARSET = "UTF-8";
	/** 收到消息的字符解码格式。默认utf-8。 */
	public final static String DEFAULT_DECODE_CHARSET = "UTF-8";
	
//	/** 发出消息的字符编码格式。默认utf-8。 */
//	public final static String IMMQ_ENCODE_CHARSET = "UTF-8";
//	/** 收到消息的字符解码格式。默认utf-8。 */
//	public final static String IMMQ_DECODE_CHARSET = "UTF-8";
	
//	/** 消息队列服务器连接URI，形如：“amqp://admin:123456789@192.168.1.190” */
//	public static String IMMQ_URI = "amqp://js:19844713@192.168.1.190";
//	/** 消息中转队列：Web端IM转发至APP端的消息 */
//	public static String IMMQ_QUEUE_WEB2APP = "q_web2app";
//    /** 消息中转队列：APP端IM转发至Web端的消息 */
//	public static String IMMQ_QUEUE_APP2WEB = "q_app2web";

	// 连接工厂类，提取出来的目的是为了重用
	protected ConnectionFactory _factory = null;
	// 一旦首次连接成功后，本连接对象将不会被重置，它会依靠自身
	// 的自动恢复功能，在断线后自动恢底层的连接，应用层不需要额外代码
	protected Connection _connection = null;
	// 生产者的Chnannel对象，连接断开再次恢复时，本变量被重新赋值
	protected Channel _pubChannel = null;
	
	/** 
	 * 【说明】：RabittMQ的Java客户端的automaticRecovery只在连接成功后才会启动，像
	 *   这种首次连接时服务器根本就没开或者本地网络故障等，首次无法成功建立Connection的
	 *      ，connction返回直接是null，当然就不存在automaticRecovery能力了，所以需要自已
	 *   来尝试重新start，一定要注意思路哦，别理解乱了。*/
	protected final Timer timerForStartAgain = new Timer();
	/** 此标识仅用于防止首次连接失败重试时因TimeTask的异步执行而发生重复执行的可能，仅此而已 */
	protected boolean startRunning = false;
	
	/** 本定时的作用是当worker启动或运行过程中出错时，可以自动进行恢复，而不至于丧失功能 */
	protected final Timer timerForRetryWorker = new Timer();
	/** 此标识仅用于防止worker失败重试时因TimeTask的异步执行而发生重复执行的可能，仅此而已 */
	protected boolean retryWorkerRunning = false;
	
	/** 
	 * 本地生产者用的暂存消息队列：因为当发送消息时，可能连接等原因导致此次消息没有成功发出，
	 * 那么暂存至此列表中，以备下次连接恢复时，再次由本类自动完成发送，从而确保消息不丢并确保送达。
	 */
	protected ConcurrentLinkedQueue<String[]> publishTrayAgainCache = new ConcurrentLinkedQueue<String[]>();
	/**
	 * 是否支持再次发送：true表示本地生产者发送失败时（比如连接MQ服务器不成功等情况）暂时缓存到本地内
	 * 存  {@link publishTrayAgainCache} 中，等到与MQ中间件的连接成功时，自动再次尝试发送此缓存队列，从而保
	 * 证生产者本来要发生送的消息在异常发生的情况下能再次发送直到成功。默认false。
	 * 
	 * <p>
	 * 【注意】：不是所有情况下都需要在发送失败的情况下保存在缓存中以备再次尝试，比如MobileIMSDKX的跨服桥接消息的发送：
	 * #####    为了让publish调用时能准确地反应出是否发送成功（而不需要在没发送成功时自动再次发送）
	 * #####    ，暂时关闭了此离线再发功能，也许以后的其它逻辑中用的上，但还是暂时取消了相关代码！！
	 * #####    【JS认为】桥接转发如不成功就应该立即由调用者知道，不然有此离线功能的话就会导致为何消息
	 * #####    报已发出，但就是没有收到的问题，这对调试和用户体验都不利！
	 */
	protected boolean publishTrayAgainEnable = false;
	
	/** 本类中消费者收到的消息通过此观察者进行回调通知 */
	protected Observer consumerObserver = null;
	
	/** 发出消息的字符编码格式 */
	protected String encodeCharset = null;
	/** 收到消息的字符解码格式 */
	protected String decodeCharset = null;
	/** 消息队列服务器连接URI，形如：“amqp://admin:123456789@192.168.1.190” */
	protected String mqURI = null;
	/** 生产者消息中转队列名：本类是此队列的生产者，会将消息发送至此 */
	protected String publishToQueue = null;
	/** 消费者消息中转队列名：本类是此队列的消费者，将从其中读取消息 */
	protected String consumFromQueue = null;
	
	/** TAG for log */
	protected String TAG = null;
	
	/**
	 * 新建一个MQProvider对象（使用默认字符编码）。
	 * 
	 * @param mqURI 消息队列服务器连接URI，形如：“amqp://admin:123456789@192.168.1.190”
	 * @param publishToQueue 生产者消息中转队列名：本类是此队列的生产者，会将消息发送至此
	 * @param consumFromQueue 消费者消息中转队列名：本类是此队列的消费者，将从其中读取消息
	 * @param publishTrayAgainEnable 是否支持再次发送：true表示本地生产者发送失败时（比如连接MQ服务器不成功等情况）暂时缓存到本地内
	 * 存  {@link publishTrayAgainCache} 中，等到与MQ中间件的连接成功时，自动再次尝试发送此缓存队列。默认false。
	 * @see #MQProvider(String, String, String, String, String, String, boolean)
	 */
	public MQProvider(String mqURI, String publishToQueue, String consumFromQueue, String TAG, boolean publishTrayAgainEnable)
	{
		this(mqURI, publishToQueue, consumFromQueue, null, null, TAG, publishTrayAgainEnable);
	}
	
	/**
	 * 新建一个MQProvider对象。
	 * 
	 * @param mqURI 消息队列服务器连接URI，形如：“amqp://admin:123456789@192.168.1.190”
	 * @param publishToQueue 生产者消息中转队列名：本类是此队列的生产者，会将消息发送至此
	 * @param consumFromQueue 消费者消息中转队列名：本类是此队列的消费者，将从其中读取消息
	 * @param encodeCharset 发出消息的字符编码格式 
	 * @param decodeCharset 收到消息的字符解码格式
	 * @param TAG 用于log显示时的前缀，仅此而已
	 * @param publishTrayAgainEnable 是否支持再次发送：true表示本地生产者发送失败时（比如连接MQ服务器不成功等情况）暂时缓存到本地内
	 * 存  {@link publishTrayAgainCache} 中，等到与MQ中间件的连接成功时，自动再次尝试发送此缓存队列。默认false。
	 */
	public MQProvider(String mqURI, String publishToQueue, String consumFromQueue
			, String encodeCharset, String decodeCharset, String TAG
			, boolean publishTrayAgainEnable)
	{
		this.mqURI = mqURI;
		this.publishToQueue = publishToQueue;
		this.consumFromQueue = consumFromQueue;
		this.encodeCharset = encodeCharset;
		this.decodeCharset = decodeCharset;
		this.TAG = TAG;
		
		if(this.mqURI == null)
			throw new IllegalArgumentException("["+TAG+"]无效的参数mqURI ！");
		
		if(this.publishToQueue == null && this.consumFromQueue == null)
			throw new IllegalArgumentException("["+TAG+"]无效的参数，publishToQueue和" +
					"consumFromQueue至少应设置其一！");
		
		if(this.encodeCharset == null || this.encodeCharset.trim().length() == 0)
			this.encodeCharset = DEFAULT_ENCODE_CHARSET;
		if(this.decodeCharset == null || this.decodeCharset.trim().length() == 0)
			this.decodeCharset = DEFAULT_DECODE_CHARSET;
		
		init();
	}
	
	protected boolean init()
	{
		String uri = this.mqURI;
		_factory = new ConnectionFactory();

		// 设置连接 uri
		try
		{
			_factory.setUri(uri);
		}
		catch (Exception e)
		{
			logger.error("["+TAG+"] - 【严重】factory.setUri()时出错，Uri格式不对哦，uri="+uri, e);
			return false;
		}

		// connection that will recover automatically
		_factory.setAutomaticRecoveryEnabled(true);
		// 禁用拓扑恢复，这是在开启AutoRecovery的情况下，官方建议的设置，
		// 请参考：http://www.rabbitmq.com/api-guide.html#recovery
		_factory.setTopologyRecoveryEnabled(false);
		// attempt recovery every 5 seconds（官方默认是5秒）
		_factory.setNetworkRecoveryInterval(5000);

		// 设置连接心跳保活（单位：秒）
		_factory.setRequestedHeartbeat(30);
		// 设置连接超时（单位：毫秒）
		_factory.setConnectionTimeout(30 * 1000);
		
		return true;
	}
	
	/**
	 * 返回Connection实例，如果connection不存在则新建一个。
	 * 
	 * @return Connection被成功创建则返回它的实例引用，否则返回null
	 */
	protected Connection tryGetConnection()
	{
		if(_connection == null)
		{
			try
			{
				_connection = _factory.newConnection();
				// 此监听事件在连接恢复后居然会被调用，扯蛋，逻辑代码不要依赖此事件哦
				_connection.addShutdownListener(new ShutdownListener() {
					public void shutdownCompleted(ShutdownSignalException cause)
					{
						logger.warn("["+TAG+"] - 连接已经关闭了。。。。【NO】");
					}
				});

				((Recoverable)_connection).addRecoveryListener(new RecoveryListener(){
					@Override
					public void handleRecovery(Recoverable arg0)
					{
						logger.info("["+TAG+"] - 连接已成功自动恢复了！【OK】");
						
						start();
					}
				});
			}
			catch (Exception e)
			{
				logger.error("["+TAG+"] - 【NO】getConnection()时出错了，原因是："+e.getMessage(), e);
				_connection = null;
				return null;
			}
		}
		
		return _connection;
	}

	/**
	 * 调用者必须显示调用本方法才能启动本provider的整个执行策略。
	 */
	public void start()
	{
		if(startRunning)
			return;
		
		try
		{
			if(_factory != null)
			{
				Connection conn = tryGetConnection();
				if(conn != null)
				{
					whenConnected(conn);
				}
				else
				{
					logger.error("["+TAG+"-↑] - [start()中]【严重】connction还没有准备好" +
							"，conn.createChannel()失败，start()没有继续！(原因：connction==null)【5秒后重新尝试start】");

					/* 
					 * 【说明】：RabittMQ的Java客户端的automaticRecovery只在连接成功后才会启动，像
					 *   这种首次连接时服务器根本就没开或者本地网络故障等，首次无法成功建立Connection的
					 *      ，connction返回直接是null，当然就不存在automaticRecovery能力了，所以需要自已
					 *   来尝试重新start，一定要注意思路哦，别理解乱了。
					 */
					// 暂停5秒（后再重试）（注意：不能使用Thread.sleep那种方法，否则相当于无穷无尽在一个start里嵌套调用start，注意理解）
					timerForStartAgain.schedule(new TimerTask() {
						public void run() {
							// 重新开始
							start();
						}
					}, 5 * 1000);// 暂停5秒后重试
				}
			}
			else
			{
				logger.error("["+TAG+"-↑] - [start()中]【严重】factory还没有准备好，start()失败！(原因：factory==null)");
			}
		}
		// try finally是为了确保无论发生何事的情况下一定要将startRunning置位，确保代码健壮性
		finally
		{
			startRunning = false;
		}
	}
	
	protected void whenConnected(Connection conn)
	{
		this.startPublisher(conn);
		this.startWorker(conn);
	}
	
	protected void startPublisher(Connection conn)
	{
		if(conn != null)
		{
			if(_pubChannel != null && _pubChannel.isOpen())
			{
				try{
					_pubChannel.close();
				}
				catch (Exception e){
					logger.warn("["+TAG+"-↑] - [startPublisher()中]pubChannel.close()时发生错误。", e);
				}
			}

			try
			{
				_pubChannel = conn.createChannel();

				logger.info("["+TAG+"-↑] - [startPublisher()中] 的channel成功创建了，" +
						"马上开始循环publish消息，当前数组队列长度：N/A！【OK】");//"+offlinePubQueue.size()+"！【OK】");

				String queue = this.publishToQueue;     //queue name 
				boolean durable = true;     //durable - RabbitMQ will never lose the queue if a crash occurs
				boolean exclusive = false;  //exclusive - if queue only will be used by one connection
				boolean autoDelete = false; //autodelete - queue is deleted when last consumer unsubscribes

				// 尝试建立队列（这是幂等操作，如果队列存在MQ服务端也不会重复建立的）
				AMQP.Queue.DeclareOk qOK = _pubChannel.queueDeclare(queue, durable, exclusive, autoDelete, null);

				logger.info("["+TAG+"-↑] - [startPublisher中] Queue[当前队列消息数："+qOK.getMessageCount()
						+",消费者："+qOK.getConsumerCount()+"]已成功建立，Publisher初始化成功，"
						+"消息将可publish过去且不怕丢失了。【OK】(当前暂存数组长度:N/A)");//"+offlinePubQueue.size()+")");

				if(publishTrayAgainEnable)
				{
					// 尝试将之前因发送中遇到错误时暂存的消息再次进行发送
					while(publishTrayAgainCache.size()>0)
					{
						// 取出数组第一个单元（并删除原数据中的该单元）
						String[] m = publishTrayAgainCache.poll();
						if(m != null && m.length > 0)
						{
							logger.debug("["+TAG+"-↑] - [startPublisher()中] [...]在channel成功创建后，正在publish之前失败暂存的消息 m[0]="+m[0]
									+"、m[1]="+m[1]+",、m[2]="+m[2]+"，[当前数组队列长度："+publishTrayAgainCache.size()+"]！【OK】");
							publish(m[0], m[1], m[2]);
						}
						else
						{
							logger.debug("["+TAG+"-↑] - [startPublisher()中] [___]在channel成功创建后，" +
									"当前之前失败暂存的数据队列已为空，publish没有继续！[当前数组队列长度："+publishTrayAgainCache.size()+"]！【OK】");
							break;
						}
					}
				}
			}
			catch (Exception e)
			{
				logger.error("["+TAG+"-↑] - [startPublisher()中] conn.createChannel()或pubChannel.queueDeclare()" +
						"出错了，本次startPublisher没有继续！", e);
			}
		}
		else
		{
			logger.error("["+TAG+"-↑] - [startPublisher()中]【严重】connction还没有准备好" +
					"，conn.createChannel()失败！(原因：connction==null)");
		}
	}
	
	/**
	 * Method to publish a message, will queue messages internally 
	 * if the connection is down and resend later.
	 * <p>
	 * 向默认的exchange和队列进行发送。
	 * 
	 * @param message
	 * @return true表示发送成功，否则表示未发送失败
	 * @see #IMMQ_QUEUE_APP2WEB
	 */
	public boolean publish(String message)
	{
		return this.publish("", this.publishToQueue, message);
	}
	
	/**
	 * Method to publish a message, will queue messages internally 
	 * if the connection is down and resend later.
	 * 
	 * @param exchangeName
	 * @param routingKey
	 * @param message
	 * @return true表示发送成功，否则表示未发送失败
	 */
	protected boolean publish(String exchangeName, String routingKey, String message)
	{
		boolean ok = false;
		
		try
		{
			_pubChannel.basicPublish(exchangeName, routingKey
					// 此属性指明消息需要持久化（RabbitMQ规定：队列和消息都
					// 设置持久化才能保证一条消息被持久化，实践证明仅队列持久化是没用的哦）
					, MessageProperties.PERSISTENT_TEXT_PLAIN
					, message.getBytes(this.encodeCharset));
			logger.info("["+TAG+"-↑] - [startPublisher()中] publish()成功了 ！(数据:"
					+exchangeName+","+routingKey+","+message+")");
			ok = true;
		}
		catch (Exception e)
		{
			if(publishTrayAgainEnable)
			{
				// 将没有发送出去的消息先放到本地内存中暂时，等连接恢复后再次发送
				publishTrayAgainCache.add(new String[]{exchangeName, routingKey, message});
			}
			
			logger.error("["+TAG+"-↑] - [startPublisher()中] publish()时Exception了，" +
					"原因："+e.getMessage()+"【数据["+exchangeName+","+routingKey+","+message+"]已重新放回数组首位"+
		            "，当前数组长度：N/A】", e);//"+offlinePubQueue.size()+"】", e);
		}
		return ok;
	}
	
	// A worker that acks messages only if processed succesfully
	protected void startWorker(Connection conn)
	{
		if(this.retryWorkerRunning)
			return;

		try
		{
		if(conn != null)
		{
				final Channel resumeChannel = conn.createChannel();
				
				// 理论上，此队列是应该由生产者创建的，但为了防止生产者尚未启并建立队列时本woker
				// 就已运行而导致错误不能持续监听，从而错过生产者真正启动后并建立队列后产生的消息，
				// 本方法的catch里有重试代码，防止worker因错误再也运行不起来！
				String queueName = this.consumFromQueue;//queue name 
				
//				boolean durable = true;     //durable - RabbitMQ will never lose the queue if a crash occurs
//				boolean exclusive = false;  //exclusive - if queue only will be used by one connection
//				boolean autoDelete = false; //autodelete - queue is deleted when last consumer unsubscribes
//				// 尝试建立队列（这是幂等操作，如果队列存在MQ服务端也不会重复建立的）：不然如果
//				// 本程序启动时生产者还未建起对队列的话，本方法将会出错，也就会导致worker不能正
//				// 常工作，进而当生产者启动后本woker也就收不到即时publish的消息了！
//				AMQP.Queue.DeclareOk qOK = resumeChannel.queueDeclare(queueName, durable, exclusive, autoDelete, null);
//				
//	            logger.info("[IMMQ-↑] - [startPublisher中] Queue[当前队列消息数："+qOK.getMessageCount()
//	            		+",消费者："+qOK.getConsumerCount()+"]已成功建立，Publisher初始化成功，"
//	            		+"消息将可publish过去且不怕丢失了。【OK】(当前暂存数组长度:"+offlinePubQueue.size()+")");
				
				DefaultConsumer dc = new DefaultConsumer(resumeChannel) {
					@Override
					public void handleDelivery(String consumerTag,Envelope envelope,
							AMQP.BasicProperties properties,byte[] body)throws IOException{
						String routingKey = envelope.getRoutingKey();
						String contentType = properties.getContentType();

						long deliveryTag = envelope.getDeliveryTag();
						
						logger.info("["+TAG+"-↓] - [startWorker()中] 收到一条新消息(routingKey="
								+routingKey+",contentType="+contentType+",consumerTag="+consumerTag
								+",deliveryTag="+deliveryTag+")，马上开始处理。。。。");

						// 开始处理消费者新收到的消息
						boolean workOK = work(body);
						if(workOK){
							resumeChannel.basicAck(deliveryTag, false);
						}
						else{
							resumeChannel.basicReject(deliveryTag, true);
						}
					}
				};
				
				// 关闭自动RabittMQ的ACK，改为手动ACK，以便与客户端业务层进行配合
				boolean autoAck = false;
				resumeChannel.basicConsume(queueName, autoAck,dc);
				
				logger.info("["+TAG+"-↓] - [startWorker()中] Worker已经成功开启并运行中...【OK】");
			
		}
		else
		{
			throw new Exception("["+TAG+"-↓] - 【严重】connction还没有准备好，conn.createChannel()失败！(原因：connction==null)");
		}
		}
		catch (Exception e)
		{
			logger.error("["+TAG+"-↓] - [startWorker()中] conn.createChannel()或Consumer操作时" +
					"出错了，本次startWorker没有继续【暂停5秒后重试startWorker()】！", e);
			
			// ** 重试的目的是确保无论发生什么错误的情况下，worker都能自动恢复，保持workder的健壮性
			// 暂停5秒（后再重试）（注意：不能使用Thread.sleep那种方法，否则相当于无穷无尽在一个start里嵌套调用start，注意理解）
			this.timerForRetryWorker.schedule(new TimerTask() {
				public void run() {
					// 重新开始
					startWorker(MQProvider.this._connection);
				}
			}, 5 * 1000);// 暂停5秒后重试
		}
		// try finally是为了确保无论发生何事的情况下一定要将retryWorkerRunning置位，确保代码健壮性
		finally
		{
			retryWorkerRunning = false;
		}
	}
	
	/**
	 * 处理接收到的消息。<b>子类需要重写本方法以便实现自已的处理逻辑，本方法默认只作为log输出使用！</b>
	 * <p>
	 * <b>特别注意：</b>本方法一旦返回false则消息将被MQ服务器重新放入队列，
	 * 请一定注意false是你需要的，不然消息会重复哦。
	 * 
	 * @param contentBody 从MQ服务器取到的消息内容byte数组
	 * @return true表示此消息处理成功(本类将回复ACK给MQ服务端，服务端队列中会交此消息正常删除)，
	 * 否则不成功(本类将通知MQ服务器将该条消息重新放回队列，以备下次再次获取)。
	 */
	protected boolean work(byte[] contentBody)
	{
		try
		{
			String msg = new String(contentBody, this.decodeCharset);
			// just log for debug
			logger.info("["+TAG+"-↓] - [startWorker()中] Got msg："+msg);
			return true;
		}
		catch (Exception e)
		{
			logger.warn("["+TAG+"-↓] - [startWorker()中] work()出现错误，错误将被记录："+e.getMessage(), e);
//			return false;
			return true;// 注意： 尽量不要返回false，不然消息被放回后容易死循环，出错作好记录备查即可！！
		}
	}
	
//	public static void main(String[] args)// throws Exception
//	{
//		MQProvider mqp = MQProvider.getInstance();
//		{
//			mqp.start();
//			
//			while(true)
//			{
//				String message = "Hello AMQP!("+(new Date().toLocaleString()+")-from APP Server");
////				String exchangeName = "";
////				String routingKey = IMMQ_QUEUE_APP2WEB;
//				mqp.publish(message);
//				
////				try
////				{
////					Thread.sleep(15*1000);
////				}
////				catch (Exception e)
////				{
////					e.printStackTrace();
////				}
//			}
//		}
//	}
}
