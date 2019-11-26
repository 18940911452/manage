package wg.openmob.mobileimsdk.server.qos;

/**
 * S2C模式中QoS数据包质量包证机制之发送队列保证实现类.
 * <br>
 * 本类是QoS机制的核心，极端情况下将弥补因UDP协议天生的不可靠性而带来的
 * 丢包情况。
 * <p>
 * 当前MobileIMSDK的QoS机制支持全部的C2C、C2S、S2C共3种消息交互场景下的
 * 消息送达质量保证.
 * <p>
 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 * 
 * @version 2.1
 */
public class QoS4SendDaemonS2C extends QoS4SendDaemonRoot
{
	private static QoS4SendDaemonS2C instance = null;
	
	public static QoS4SendDaemonS2C getInstance()
	{
		if(instance == null)
			instance = new QoS4SendDaemonS2C();
		return instance;
	}
	
	private QoS4SendDaemonS2C()
	{
		super(0 // QoS质量保证线程心跳间隔（单位：毫秒），本参数<=0表示使用父类的默认值
			, 0 // “刚刚”发出的消息阀值定义（单位：毫秒），本参数<=0表示使用父类的默认值
			, -1// 一个包允许的最大重发次数，本参数<0表示使用父类的默认值
			, true
			, "-本机QoS");
	}
}
