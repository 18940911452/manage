package wg.openmob.mobileimsdk.server.utils;

import io.netty.channel.Channel;
import wg.nettime.mobileimsdk.server.bridge.QoS4ReciveDaemonC2B;
import wg.nettime.mobileimsdk.server.netty.MBObserver;
import wg.openmob.mobileimsdk.server.handler.ServerCoreHandler;
import wg.openmob.mobileimsdk.server.handler.ServerLauncher;
import wg.openmob.mobileimsdk.server.processor.BridgeProcessor;
import wg.openmob.mobileimsdk.server.processor.OnlineProcessor;
import wg.openmob.mobileimsdk.server.protocal.Protocal;
import wg.openmob.mobileimsdk.server.protocal.ProtocalFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本类提供的是公开的数据发送方法（已封装跨机器通信的桥接处理逻辑）。
 * <p>
 * <b>注意：</b>如果已开通与Web版的互通（参见： {@link ServerLauncher#bridgeEnabled} ）
 * ，则服务端消息发送建议使用本类，否则请使用 {@link LocalSendHelper} 。
 * 
 * @version 1.0
 * @since 3.0
 * @see ServerLauncher#bridgeEnabled
 */
public class GlobalSendHelper
{
	private static Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);  
	public static GlobalSendHelper ser=new GlobalSendHelper();
	/**
	 * 用方法用于服务端作为中转发送C2C类型的数据之用，此方法封装了服务端中转发送
	 * C2C消息的所有逻辑（包括当接收者不在本机在线列表时的桥接处理等）。
	 * <p>
	 * <font color="#ff0000"><b>注意：</b>本方法不应由服务端的应用层调用，仅限MobileIMSDK_X框架内部使用。</font>
	 * 
	 * @param bridgeProcessor 跨IM实例互通的桥接器对象
	 * @param session 应答包被回复的回话，也就是C2C消息的发出者对应的session句柄（布满是消息接收者哦）
	 * @param pFromClient
	 * @param remoteAddress
	 * @param serverCoreHandler
	 * @throws Exception
	 * @see BridgeProcessor#publish(String)
	 * @see LocalSendHelper#sendData(IoSession, Protocal)
	 */
	public static void sendDataC2C(final BridgeProcessor bridgeProcessor
			, final Channel session, final Protocal pFromClient, final String remoteAddress
			, final ServerCoreHandler serverCoreHandler) throws Exception
	{
		// TODO just for DEBUG
		OnlineProcessor.getInstance().__printOnline();

		// ** 是否需要发送（给客户端）伪应答包（因为不是真正的接收者应答，所以叫伪应答）
		// 发送伪应答的目的是保证在消息被离线处理或者桥接发送时，也能让发送者知道消息
		// 已被妥善送达（离线或桥接发送也同样是”送达“，只是非实时而已）
		boolean needDelegateACK = false;

		// ** 【已启用与Web端的互通 且 本机不在线的情况下 就尝试转为桥接发送】
		// TODO 第二阶段集群实现时要修改以下在线状态判断为全局所有用户在线列表中的结果（而不只是本机）
		// 接收方不在本地MessageServer在线列表上（按照第一阶段的异构通信算法，直接发往Web服务端）
		if(ServerLauncher.bridgeEnabled && !OnlineProcessor.isOnline(pFromClient.getTo()))
		{
			logger.debug("[IMCORE-netty<C2C>-桥接↑]>> 客户端"+pFromClient.getTo()+"不在线，数据[from:"+pFromClient.getFrom()
					+",fp:"+pFromClient.getFp()+"to:"+pFromClient.getTo()+",content:"+pFromClient.getDataContent()
					+"] 将通过MQ直发Web服务端（彼时在线则通过web实时发送、否则通过Web端进"
					+"行离线存储）【第一阶段APP+WEB跨机通信算法】！");

			// 如果此包需要QoS 且 之前已经成功被”收到“，则本次就不需要额外处理，
			// 只需再次发送伪应答，客户端重复发送的原因可能是因为之前的应答包丢包
			if(pFromClient.isQoS()&& QoS4ReciveDaemonC2B.getInstance().hasRecieved(pFromClient.getFp()))
			{
				needDelegateACK = true;
			}
			else
			{
				// 直发MQ队列（将会由队列那端的Web服务器进行接管和处理）
				boolean toMQ = bridgeProcessor.publish(pFromClient.toGsonString());

				// 消息已成功桥接发送
				if(toMQ)
				{
					logger.debug("[IMCORE-netty<C2C>-桥接↑]>> 客户端"+remoteAddress+"的数据已跨机器送出成功【OK】。(数据[from:"+pFromClient.getFrom()
							+",fp:"+pFromClient.getFp()+",to:"+pFromClient.getTo()+",content:"+pFromClient.getDataContent()
							+"]【第一阶段APP+WEB跨机通信算法】)");

					// 注：此回调不需要吧，在MQ那端的服务器会在发送成功后进行C2C成功回调的（此处再搞就重复了）
					// // 通知回调
					// serverEventListener.onTransBuffer_C2C_CallBack(
					//     pFromClient.getTo(), pFromClient.getFrom(), pFromClient.getDataContent());

					// 【如果该消息包有QoS机制】则将由则服务端代为发送一条伪应答包，因已桥接发送成功
					if(pFromClient.isQoS())
						needDelegateACK = true;
				}
				// 未成功桥接发送
				else
				{
					logger.debug("[IMCORE-netty<C2C>-桥接↑]>> 客户端"+remoteAddress+"的数据已跨机器送出失败，将作离线处理了【NO】。(数据[from:"+pFromClient.getFrom()
							+",fp:"+pFromClient.getFp()+"to:"+pFromClient.getTo()+",content:"+pFromClient.getDataContent()
							+"]【第一阶段APP+WEB跨机通信算法】)");

					//** MQ也发送失败：那就意味着接收发不在本机也不能成功发往MQ，直接离线处理吧
					//*********************** 代码段20160914【1】：与【2】处是一样的，未重用代码的目的是简化代码逻辑
					// 提交回调，由上层应用进行离线处理
					boolean offlineProcessedOK = serverCoreHandler.getServerEventListener()
							.onTransBuffer_C2C_RealTimeSendFaild_CallBack(pFromClient.getTo()
									, pFromClient.getFrom(), pFromClient.getDataContent(), pFromClient.getFp(), pFromClient.getTypeu());

					// 【如果该消息包有QoS机制 且 上层应用成功进行了离线处理】则将由则服务端代为发送一条伪应
					// 答包（伪应答仅意味着不是接收方的实时应答，而只是存储到离线DB中，但在发送方看来也算
					// 是被对方收到，只是延迟收到而已（离线消息嘛））），在有QoS机制但应用层没有处理的情况
					// 下发送方客户端在QoS重传机制超时后将报出消息发送失败的提示
					if(pFromClient.isQoS() && offlineProcessedOK)
					{
						// 离线处理成功，也当然发给发送方一个ACK了，对于
						// 发送方而言，离线处理也是成功送达
						needDelegateACK = true;
					}
					else
					{
						logger.warn("[IMCORE-netty<C2C>-桥接↑]>> 客户端"+remoteAddress+"的通用数据传输消息尝试实时发送没有成功，但上层应用层没有成" +
								"功(或者完全没有)进行离线存储，此消息将被服务端丢弃【第一阶段APP+WEB跨机通信算法】！");
					}
				}
			}

			if(needDelegateACK)
			{
				// Netty的数据发送结果观察者：netty的数据发送结果是通过异步通知来实现的（这就
				// 是异步编程模型，跟Nodejs的promise、Androi里的RxJava、iOS的block道理一样）
				MBObserver resultObserver = new MBObserver(){
					@Override
					public void update(boolean receivedBackSendSucess, Object extraObj)
					{
						if(receivedBackSendSucess)
							logger.debug("[IMCORE-netty<C2C>-桥接↑]【QoS_伪应答_C2S】向"+pFromClient.getFrom()+"发送"+pFromClient.getFp()
									+"的伪应答包成功,伪装from自："+pFromClient.getTo()+"【第一阶段APP+WEB跨机通信算法】.");
					}
				};
				
				// 发送伪应答包，以便发送消息者（它自已其实不知道这条消息是被桥接处理的）知道已经送达
				LocalSendHelper.replyDelegateRecievedBack(session, pFromClient, resultObserver);
			}

			// 【【C2S[桥接]模式下的QoS机制2/4步：将收到的包存入QoS接收方暂存队列中（用于防QoS消息重复）】】
			// @see 客户端LocalUDPDataReciever中的第1/4和第4/4步相关处理
			QoS4ReciveDaemonC2B.getInstance().addRecieved(pFromClient);
		}
		// ** 【本机在线或其它情况下则直接在本机范围内发送和处理】
		// 接收方在本MessageServer的在线列表中
		else
		{
			// Netty的数据发送结果观察者：netty的数据发送结果是通过异步通知来实现的（这就
			// 是异步编程模型，跟Nodejs的promise、Androi里的RxJava、iOS的block道理一样）
				MBObserver resultObserver = new MBObserver(){
				@Override
				public void update(boolean sendOK, Object extraObj)
				{
					// 数据已成功发出（给对方用户）（注意：UDP协议下如果客户端没有丢包保证机机制，服务端代发的数据不意味着用户一定能收的到哦！）
					if(sendOK)
					{
						serverCoreHandler.getServerEventListener().onTransBuffer_C2C_CallBack(
								pFromClient.getTo(), pFromClient.getFrom()
								, pFromClient.getDataContent(), pFromClient.getFp(), pFromClient.getTypeu());
					}
					else
					{
						logger.info("[IMCORE-netty<C2C>]>> 客户端"+remoteAddress+"的通用数据尝试实时发送没有成功，将交给应用层进行离线存储哦...");

						//*********************** 代码段20160914【2】：与【1】处是一样的，未重用代码的目的是简化代码逻辑
						// 提交回调，由上层应用进行离线处理
						boolean offlineProcessedOK = serverCoreHandler.getServerEventListener()
								.onTransBuffer_C2C_RealTimeSendFaild_CallBack(pFromClient.getTo()
										, pFromClient.getFrom(), pFromClient.getDataContent(), pFromClient.getFp(), pFromClient.getTypeu());
						// 【如果该消息包有QoS机制 且 上层应用成功进行了离线处理】则将由则服务端代为发送一条伪应
						// 答包（伪应答仅意味着不是接收方的实时应答，而只是存储到离线DB中，但在发送方看来也算
						// 是被对方收到，只是延迟收到而已（离线消息嘛））），在有QoS机制但应用层没有处理的情况
						// 下发送方客户端在QoS重传机制超时后将报出消息发送失败的提示
						if(pFromClient.isQoS() && offlineProcessedOK)
						{
							try
							{
								// Netty的数据发送结果观察者：netty的数据发送结果是通过异步通知来实现的（这就
								// 是异步编程模型，跟Nodejs的promise、Androi里的RxJava、iOS的block道理一样）
								MBObserver retObserver = new MBObserver(){
									@Override
									public void update(boolean sucess, Object extraObj)
									{
										if(sucess)
										{
											logger.debug("[IMCORE-netty<C2C>]【QoS_伪应答_C2S】向"+pFromClient.getFrom()+"发送"+pFromClient.getFp()
													+"的伪应答包成功,from="+pFromClient.getTo()+".");
										}
									}
								};
								
								// 发送伪应答包
								LocalSendHelper.replyDelegateRecievedBack(session, pFromClient, retObserver);
							}
							catch (Exception e)
							{
								logger.warn(e.getMessage(), e);
							}
						}
						else
						{
							logger.warn("[IMCORE-netty<C2C>]>> 客户端"+remoteAddress+"的通用数据传输消息尝试实时发送没有成功，但上层应用层没有成" +
									"功(或者完全没有)进行离线存储，此消息将被服务端丢弃！");
						}
					}
				}
			};
			
			// 发送数据
			LocalSendHelper.sendData(pFromClient, resultObserver);
		}
	}
	
	/**
	 * 用方法用于服务端向客户端主动发起的数据之用，此方法封装了服务端发送
	 * S2C消息的所有逻辑（包括当接收者不在本机在线列表时的桥接处理等）。
	 * <p>
	 * <font color="#008800"><b>提示：</b>本方法可用于应用层调用，从而实现服务端向客户端主动发送
	 * 消息的能力（且支持跨机器的消息能力）。</font>
	 * 
	 * @param bridgeProcessor 跨IM实例互通的桥接器对象，应用层可通过 
	 * 		<font color="#0000ff"><code>ServerLauncherImpl.getInstance().getServerCoreHandler().getBridgeProcessor()</code></font>
	 * 		来获取此对象引用
	 * @param pFromClient 要发送的数据内容，请使用 {@link ProtocalFactory#createCommonData(String, String, String
	 * 		, boolean, String, int)}来生成Protocal对象
	 * @param resultObserver 因netty的异步化特征，发送数据在API层也是异步的，本参数
     * 用于获得数据发送的结果通知（这是与MINA的区别之一）。服务端为了获得高并发、高性
     * 能，失去传统网络编程同步调用时编码的便利也是在所难免(再也不是直接的函数返回值了
     * )，开发者需适应之。<font color="#0000aa">参数sucess：false表示发送一定没有成功，true表示消息已成功送
     * 成但因异步发送且是UDP的原因能否成功收到还得看服务端QoS的情况</font>
	 * @throws Exception 发生任何异常时都会抛出
	 * @see OnlineProcessor#isOnline(String)
	 * @see BridgeProcessor#publish(String)
	 * @see LocalSendHelper#sendData(Protocal)
	 */
	public static void sendDataS2C(BridgeProcessor bridgeProcessor, Protocal pFromClient
			, final MBObserver resultObserver) throws Exception
	{
		// TODO just for DEBUG
		OnlineProcessor.getInstance().__printOnline();
		
		boolean sucess = false;

		// ** 【本机不在线就尝试转为桥接发送】
		// TODO 第二阶段集群实现时要修改以下在线状态判断为全局所有用户在线列表中的结果（而不只是本机）
		// 接收方不在本地MessageServer在线列表上（按照第一阶段的异构通信算法，直接发往Web服务端）
		if(!OnlineProcessor.isOnline(pFromClient.getTo()))
		{
			logger.debug("[IMCORE-netty<S2C>-桥接↑]>> 客户端"+pFromClient.getTo()+"不在线，数据[from:"+pFromClient.getFrom()
					+",fp:"+pFromClient.getFp()+"to:"+pFromClient.getTo()+",content:"+pFromClient.getDataContent()
					+"] 将通过MQ直发Web服务端（彼时在线则通过web实时发送、否则通过Web端进"
					+"行离线存储）【第一阶段APP+WEB跨机通信算法】！");

				// 直发MQ队列（将会由队列那端的Web服务器进行接管和处理）
				boolean toMQ = bridgeProcessor.publish(pFromClient.toGsonString());
				// 消息已成功桥接发送
				if(toMQ)
				{
					logger.debug("[IMCORE-netty<S2C>-桥接↑]>> 服务端的数据已跨机器送出成功【OK】。(数据[from:"+pFromClient.getFrom()
							+",fp:"+pFromClient.getFp()+",to:"+pFromClient.getTo()+",content:"+pFromClient.getDataContent()
							+"]【第一阶段APP+WEB跨机通信算法】)");
					sucess = true;
				}
				// 未成功桥接发送
				else
				{
					logger.error("[IMCORE-netty<S2C>-桥接↑]>> 服务端的数据已跨机器送出失败，请通知管理员检查MQ中间件是否正常工作【NO】。(数据[from:"+pFromClient.getFrom()
							+",fp:"+pFromClient.getFp()+"to:"+pFromClient.getTo()+",content:"+pFromClient.getDataContent()
							+"]【第一阶段APP+WEB跨机通信算法】)");

					//** MQ发送失败：那就意味着接收发不在本机也不能成功发往MQ，本次发送送结果直接就是false了
				}
		}
		// ** 【本机在线则直接发送】
		// 接收方在本MessageServer的在线列表中
		else
		{
			LocalSendHelper.sendData(pFromClient, new MBObserver(){
				// Netty的数据发送结果观察者：netty的数据发送结果是通过异步通知来实现的（这就
				// 是异步编程模型，跟Nodejs的promise、Androi里的RxJava、iOS的block道理一样）
				@Override
				public void update(boolean _sendSucess, Object extraObj)
				{
					// 数据已成功发出（给对方用户）（注意：UDP协议下如果客户端没有丢包保证机机制，服务端代发的数据不意味着用户一定能收的到哦！）
					if(_sendSucess)
						_sendSucess = true;
					else
						logger.warn("[IMCORE-netty]>> 服务端的通用数据传输消息尝试实时发送没有成功，但上层应用层没有成" +
									"功，请应用层自行决定此条消息的发送【NO】！");
					
					// 通知sendDataS2C方法调用者数据发送的处理结果
					if(resultObserver != null)
						resultObserver.update(_sendSucess, null);
				}
			});
			
			// 注意：异步编程导致了代码的复杂性，本else分支里的处理结果已通过LocalSendHelper.sendData
			// 的数据发送结果回调通知了观察者，无需再进入到下方的代码再执行了，否则将重复通知
			return;
		}
		
//		return sucess;
		// 通知sendDataS2C方法调用者数据发送的处理结果
		if(resultObserver != null)
			resultObserver.update(sucess, null);
		
	}
}
