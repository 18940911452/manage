package wg.openmob.mobileimsdk.server.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.io.IOException;

import wg.nettime.mobileimsdk.server.bridge.QoS4ReciveDaemonC2B;
import wg.nettime.mobileimsdk.server.bridge.QoS4SendDaemonB2C;
import wg.nettime.mobileimsdk.server.netty.MBUDPClientInboundHandler;
import wg.nettime.mobileimsdk.server.netty.MBUDPServerChannel;
import wg.openmob.mobileimsdk.server.event.MessageQoSEventListenerS2C;
import wg.openmob.mobileimsdk.server.event.ServerEventListener;
import wg.openmob.mobileimsdk.server.qos.QoS4ReciveDaemonC2S;
import wg.openmob.mobileimsdk.server.qos.QoS4SendDaemonS2C;
import wg.openmob.mobileimsdk.server.utils.ServerToolKits;
import wg.openmob.mobileimsdk.server.utils.ServerToolKits.SenseMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.PropKit;

/**
 * MobileIMSDK的服务端入口主类。
 * <p>
 * 为了简化API的调用，理论上使用者使用本类公开的方法即可实现MobileIMSDK
 * 的所有能力。
 * <p>
 * 基于Netty框架，使用UDP协议。本类主要实现服务端的初始化、基本服务的启停以及一些
 * 提供给开发者的公开API。
 * <p>
 * 服务端默认不会自动开启，请调用 {@link #startup()}以便启动MobileIMSDK的服务端, {@link #shutdown()}关闭
 * 服务端，直到再次调用 {@link #startup()} 。
 * <p>
 * <b>提示1：</b>
 * 请重写 {@link #initListeners()}方法，以实现应用层的回调监听器以便实现自定义业务
 * 逻辑。
 * <p>
 * <b>提示2：</b>
 * 如果设置整个MobileIMSDK的算法敏感度类型，请调用 {@link ServerToolKits#setSenseMode(SenseMode)}，
 * MobileIMSDK默认敏感度是 {@link ServerToolKits.SenseMode#MODE_3S}。
 * <p>
 * <b>提示3：</b>
 * 如要开启与MobileIMSDK-Web版的消息互通，请设置 {@link ServerLauncher#bridgeEnabled} == true
 * ，默认为false.
 * 
 * @version 1.0
 * @since 3.1
 * @see ServerCoreHandler
 */
public abstract class ServerLauncher 
{
	private static Logger logger = LoggerFactory.getLogger(ServerLauncher.class); 
	
	/**
	 * 全局设置：服务端Debug日志输出总开关（建议压力测试和生产环境下设为false）。
	 * <p>
	 * 默认值：true。
	 * <p>
	 * <b>特别注意：</b>为了减少过多地使用本标识而增加服务器不必要的if判断开销，自2017年
	 * 12月11日v3.1版起，已废除本标识的作用。<font color="red">为了保证性能，在压力测试或
	 * 生产环境下请直接关闭log4j的日志输出（设置log4j的日志输出级别即可(比如设为WARN级别等
	 * )，请查阅log4j文档），<b>切记！！</b></font>
	 * 
	 * @since 3.0
	 * @deprecated 于2017年12月11日v3.1版废除本字段，为了性能请使用log4j的日志输出级别来控制日志输出
	 */
	public static boolean debug = true;
	
	/**
	 * 全局设置：AppKey。
	 * <p>
	 * <b>友情提示：</b>本字段目前为保留字段，使用时无需独立设置或随意设置均不影响使用。
	 */
	public static String appKey = null;
	
	/** 
	 * 服务端UDP监听端口，默认7901。
	 * <p>
	 * 请在 {@link #startup()}方法被调用前被设置，否则将不起效.
	 */
    public static int PORT = PropKit.getInt("imPort");
    
    /** 
     * UDP Session的空闲超时时长（单位：秒），默认10秒。
     * 
     * <p>
     * 表示一个用户在非正常退出、网络故障等情况下，服务端判定此用户不在线的超时时间。
     * 此参数应与客户端的KeepAliveDaemon.KEEP_ALIVE_INTERVAL配合调整，为防止因心跳丢包
     * 而产生误判，建议本参数设置为“客户端的KeepAliveDaemon.KEEP_ALIVE_INTERVAL * (2或3)
     * + 典型客户网络延迟时间”，比如默认值10就是等于“3 * 3 + 1”（即服务端允许在最最极端的
     * 情况下即使连丢3个包也不会被误判为掉线！）。
     * 
     * <p>
     * 本值不宜过短，太短则会导致客户端因心跳丢包而误判为掉线，进而触发客户端的重登机制。原则
     * 上设置的长一点更有保证，但设置过长的话会导致服务端判定用户真正的非正常退出过于晚了，带
     * 给用户的体验就是明明好友非正常退出了，过了好长时间才能感知到他已退出。
     * 
     * <p>
     * <b>[补充关于会话超时时间值的设定技巧]：</b><br>
     * <pre>
     *    以客户端的心跳间隔是3秒为例，10秒内如果客户端连丢2个包，通常情况是能保住它的UDP连接的，<br>
     *    但如果设为6秒超时，那么在网络超烂时会导致客户端被判定掉线的几率升高，因为6秒超时下，丢<br>
     *    1个包就有可能被判定掉线了，而网络超烂的情况下丢一个包的可能性普通的很高，也就使得掉线变<br>
     *    的频繁而影响用户体验！
     * </pre>
     *   
	 * <p>
	 * 请在 {@link #startup()}方法被调用前被设置，否则将不起效.
     */
    public static int SESION_RECYCLER_EXPIRE = 10;
    
    /**
     * 是否允许与MobileIMSDK Web版进行互通。true表示需要互通，否则不互通，默认false。
     * <p>
	 * 请在 {@link #startup()}方法被调用前被设置，否则将不起效.
	 * 
     * @since 3.0
     */
    public static boolean bridgeEnabled = false;
    
    /** 服务端是否启动并运行中 */
    private boolean running = false;
    
    /**
     * MobileIMSDK框架的核心通信逻辑实现类（实现的是MobileIMSDK服务端的通信处理核心算法）。
     * <p>
     * ServerCoreHandler是Netty的ChannelInboundHandler子类，将作为
     * 处理客户端请求时Netty pipeline中Handler链中的一个Handler来完成
     * 它的数据解析、处理等完整逻辑。
     */
    protected ServerCoreHandler serverCoreHandler = null; 
    
    /** 
     * <font color="#ff0000">Netty框架专用内部变量：</font>
     * bossGroup用来接收进来的连接 (EventLoopGroup是用来处理IO操作的线程池 ) .
     * <p>
     * 作为全局变量的目的，当前仅用于关闭服务器时来释放此连接池对应的资源，别无它用。
     * 
     * @see #startup()
     */
 	private final EventLoopGroup __bossGroup4Netty = new NioEventLoopGroup();
 	
 	/** 
 	 * <font color="#ff0000">Netty框架专用内部变量：</font>
 	 * workerGroup用来处理已经被接收的连接   (EventLoopGroup是用来处理IO操作的线程池 ) .
 	 * 
     * <p>
     * 作为全局变量的目的，当前仅用于关闭服务器时来释放此连接池对应的资源，别无它用。
     * 
 	 * @see #startup()
 	 */
 	private final EventLoopGroup __workerGroup4Netty = new DefaultEventLoopGroup();
 	
 	/**
 	 * <font color="#ff0000">Netty框架专用内部变量：</font>
 	 * Netty服务的服务器Channel引用.
 	 * 
 	 * <p>
 	 * 作为全局变量的目的，当前仅用于关闭服务器时来释放此Channel对应的资源，别无它用。
 	 * 
 	 * @see #shutdown()
 	 */
 	private Channel __serverChannel4Netty = null;
    
    public ServerLauncher() throws IOException 
    {
    	// default do nothing
    }
    
    /**
     * 服务端是否启动并运行中。
     * 
     * @return true表示已正常启动并运行，否则没有启动
     */
    public boolean isRunning()
	{
		return running;
	}
    
    /**
     * 开启服务端。
     * 
     * @throws IOException UDP端口绑定失败将抛出本异常
     * @see #initServerCoreHandler()
     * @see #initListeners()
     * @see #initServerBootstrap4Netty()
     * @see net.openmob.mobileimsdk.server.qos.QoS4ReciveDaemonC2S#startup()
     * @see net.openmob.mobileimsdk.server.qos.QoS4SendDaemonS2C#startup(boolean)
     * @see net.nettime.mobileimsdk.server.bridge.QoS4ReciveDaemonC2B#startup()
     * @see net.nettime.mobileimsdk.server.bridge.QoS4SendDaemonB2C#startup(boolean)
     * @see ServerCoreHandler#lazyStartupBridgeProcessor()
     * @see ServerLauncher#bridgeEnabled
     * @see io.netty.bootstrap.ServerBootstrap#bind(String, int)
     */
    public void startup() throws Exception
    {	
    	// ** 【1】初始化MobileIMSDK的核心通信逻辑实现类
    	serverCoreHandler = initServerCoreHandler();
    	
    	// ** 【2】初始化消息处理事件监听者
    	initListeners();
    	
    	// ** 【3】初始化 Netty的服务辅助启动类  
    	ServerBootstrap bootstrap = initServerBootstrap4Netty();

    	// ** 【4】启动服务端对C2S模式的QoS机制下的防重复检查线程
    	QoS4ReciveDaemonC2S.getInstance().startup();
    	// ** 【5】启动服务端对S2C模式下QoS机制的丢包重传和离线通知线程
    	QoS4SendDaemonS2C.getInstance().startup(true).setServerLauncher(this);
    	
    	// 如果需要与Web版互通
    	if(ServerLauncher.bridgeEnabled){
    		
    		// ** 【6】启动桥接模式下服务端的QoS机制下的防重复检查线程(since 3.0)
        	QoS4ReciveDaemonC2B.getInstance().startup();
        	// ** 【7】启动桥接模式下服务端的QoS机制的丢包重传和离线通知线程(since 3.0)
        	QoS4SendDaemonB2C.getInstance().startup(true).setServerLauncher(this);
        	
    		// ** 【8】上面initServerCoreHandler不立即启动跨服桥接处理器而放在此处在
    		//    所有初始化完成后（放置于initListeners后，是防止交叉引用产生不可预知的错误）
    		serverCoreHandler.lazyStartupBridgeProcessor();
    		
    		logger.info("[IMCORE-netty] 配置项：已开启与MobileIMSDK Web的互通.");
    	}
    	else{
    		logger.info("[IMCORE-netty] 配置项：未开启与MobileIMSDK Web的互通.");
    	}
       
    	// ** 【9】UDP服务端开始侦听
    	// 绑定端口，开始接收进来的连接  
    	ChannelFuture cf = bootstrap.bind("0.0.0.0", PORT).syncUninterruptibly();
    	// 把Netty服务的服务器Channel引用保存起来备用
    	__serverChannel4Netty = cf.channel();
    	// 等待服务器 socket 关闭 ，此时并不会发生，但可以保证优雅地关闭服务器  
    	// （主线程到这里就 wait 子线程退出了，子线程才是真正监听和接受请求的）
    	//__serverChannel4Netty.closeFuture().await();
    	
    	// ** 【10】设置启动标识
    	this.running = true;
       
    	logger.info("[IMCORE-netty] 基于MobileIMSDK的UDP服务正在端口" + PORT+"上监听中...");
    }

	/**
     * 关闭服务端。
     * 
     * @see io.netty.channel.Channel#close()
     * @see io.netty.channel.EventLoopGroup#shutdownGracefully()
     * @see net.openmob.mobileimsdk.server.qos.QoS4ReciveDaemonC2S#stop()
     * @see net.openmob.mobileimsdk.server.qos.QoS4SendDaemonS2C#stop()
     * @see net.nettime.mobileimsdk.server.bridge.QoS4ReciveDaemonC2B#stop()
     * @see net.nettime.mobileimsdk.server.bridge.QoS4SendDaemonB2C#stop()
     */
    public void shutdown()
    {
    	//******************************************** 【1】释放netty框架的资源 START
    	// 关闭netty的服务端channel
    	if (__serverChannel4Netty != null) 
    		__serverChannel4Netty.close();

		// 优雅地退出netty的线程组
		__bossGroup4Netty.shutdownGracefully();
		__workerGroup4Netty.shutdownGracefully();
		//******************************************** 【1】释放netty框架的资源 END
    	
		//******************************************** 【2】释放MobileIMSDK框架的资源 START
    	// ** 停止QoS机制（目前服务端只支持C2S模式的QoS）下的防重复检查线程
    	QoS4ReciveDaemonC2S.getInstance().stop();
    	// ** 停止服务端对S2C模式下QoS机制的丢包重传和离线通知线程
    	QoS4SendDaemonS2C.getInstance().stop();
    	
    	// 需要与Web版互通时
    	if(ServerLauncher.bridgeEnabled){
    		QoS4ReciveDaemonC2B.getInstance().stop();
    		QoS4SendDaemonB2C.getInstance().stop();
    	}
    	//******************************************** 【2】释放MobileIMSDK框架的资源 END
    	
    	// ** 设置启动标识
    	this.running = false;
    }
    
    /**
     * 初始化MobileIMSDK的ServerCoreHandler实现类。
     * <p>
     * 本类是MobileIMSDK的服务端网络调度算法核心所在，其性能将决定整个
     * 即时通讯架构的数据交换性能、负载能力、吞吐效率等。
     * 
     * @return 初始化完成后的ServerCoreHandler实现对象
     * @since 2.1.3
     */
    protected ServerCoreHandler initServerCoreHandler()
    {
    	return new ServerCoreHandler();
    }
    
    /**
     * 初始化回调处理事件监听器。
     * <p>
     * 请重写 {@link #initListeners()}方法，以实现应用层的回调监听器以便实现自定义业务
     * 逻辑，可以设置的回调监听器有： {@link #setServerEventListener(ServerEventListener)}
     * 和 {@link #setServerMessageQoSEventListener(MessageQoSEventListenerS2C)}。
     */
    protected abstract void initListeners();
    
    /**
     * 初始化 Netty的服务辅助启动类。
     * <p>
     * 因本框架中为了应用层编码的易用性，赋予了Netty中UDP的“会话”（或说“连接”）
     * 的能力，因而本方法中使用了跟TCP一样的ServerBootstrap而非Bootstrap。
     * 有关Bootstrap的官方API说明，请见：http://docs.52im.net/extend/docs/api/netty4_1/io/netty/bootstrap/Bootstrap.html
     *
     * @see #initChildChannelHandler4Netty()
     */
    protected ServerBootstrap initServerBootstrap4Netty()
    {
    	return new ServerBootstrap()
    		// 设置并绑定Reactor线程池
    		.group(__bossGroup4Netty, __workerGroup4Netty)
    		// 设置并绑定服务端Channel
    		.channel(MBUDPServerChannel.class)
    		// 初始化针对客户端的handler链
    		.childHandler(initChildChannelHandler4Netty());
    }
    
    /**
	 * 初始化针对Netty客户端的handler链，本方法在{@link #initServerBootstrap4Netty()}中被调用。
	 * <p>
	 * 如有需要，子类可以重写此类实现自已的Inbound Hander链接逻辑。
	 * 
	 * @return handler链对象
	 * @see #initServerBootstrap4Netty()
	 * @see net.nettime.mobileimsdk.server.netty.MBUDPClientInboundHandler
	 * @see io.netty.handler.timeout.ReadTimeoutHandler.ReadTimeoutHandler
	 * @see io.netty.channel.ChannelInitializer.ChannelInitializer
	 */
	protected ChannelHandler initChildChannelHandler4Netty()
	{
		// 返回Netty的Inbound Hanndler链
		return new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				channel.pipeline()
					// 设置会话超时处理handler
					.addLast(new ReadTimeoutHandler(SESION_RECYCLER_EXPIRE))
					// 设置客户端的会话逻辑handler
					.addLast(new MBUDPClientInboundHandler(serverCoreHandler));
			}
		};
	}
    
    /**
     * 返回服务端通用事件回调监听器对象引用。
     * 
     * @return ServerEventListener对象
     * @see ServerCoreHandler#getServerEventListener()
     */
    public ServerEventListener getServerEventListener()
	{
		return serverCoreHandler.getServerEventListener();
	}
    /**
     * 设置服务端通用事件回调监听器。
     * 
     * @param serverEventListener ServerEventListener对象
     * @see ServerCoreHandler#setServerEventListener(ServerEventListener)
     */
	public void setServerEventListener(ServerEventListener serverEventListener)
	{
		this.serverCoreHandler.setServerEventListener(serverEventListener);
	}
	
	/**
	 * 返回QoS机制的Server主动消息发送之质量保证事件监听器对象。
	 * 
	 * @return MessageQoSEventListenerS2C对象
	 * @see ServerCoreHandler#getServerMessageQoSEventListener()
	 */
	public MessageQoSEventListenerS2C getServerMessageQoSEventListener()
	{
		return serverCoreHandler.getServerMessageQoSEventListener();
	}
	/**
	 * 设置QoS机制的Server主动消息发送之质量保证事件监听器。
	 * 
	 * @param serverMessageQoSEventListener MessageQoSEventListenerS2C对象
	 * @see ServerCoreHandler#setServerMessageQoSEventListener(MessageQoSEventListenerS2C)
	 */
	public void setServerMessageQoSEventListener(MessageQoSEventListenerS2C serverMessageQoSEventListener)
	{
		this.serverCoreHandler.setServerMessageQoSEventListener(serverMessageQoSEventListener);
	}

	/**
	 * 获取 ServerCoreHandler 对象引用。
	 * 
	 * @return 返回对象引用
	 * @since 3.0
	 */
	public ServerCoreHandler getServerCoreHandler()
	{
		return serverCoreHandler;
	}
	
//	public static void main(String[] args) throws IOException 
//    {
//        new ServerLauncher().startup();
//    }
}
