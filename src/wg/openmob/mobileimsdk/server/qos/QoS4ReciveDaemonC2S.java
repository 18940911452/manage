package wg.openmob.mobileimsdk.server.qos;


/**
 * 用于服务端的S2C模式下的QoS机制中提供对已收到包进行有限生命周期存储并提供
 * 重复性判断的守护线程。
 * <p>
 * <b>原理是：</b>当收到需QoS机制支持消息包时，会把它的唯一特征码（即指纹id）
 * 存放于本类的“已收到”消息队列中，寿命约为 {@link #MESSAGES_VALID_TIME}指明
 * 的时间，每当{@link #CHECH_INTERVAL}定时检查间隔到来时会对其存活期进行检查
 * ，超期将被移除，否则允许其继续存活。理论情况下，一个包的最大寿命不可能超过2
 * 倍的 {@link #CHECH_INTERVAL}时长。
 * <br>
 * <b><u>补充说明</u>：</b>“超期”即意味着对方要么已收到应答包（这是QoS机制正
 * 常情况下的表现）而无需再次重传、要么是已经达到QoS机制的重试极限而无可能再收
 * 到重复包（那么在本类列表中该表也就没有必要再记录了）。总之，“超期”是队列中
 * 这些消息包的正常生命周期的终止，无需过多解读。
 * <p>
 * <b>本类存在的意义在于：</b>极端情况下QoS机制中存在因网络丢包导致应答包的
 * 丢失而触发重传机制从而导致消息重复，而本类将维护一个有限时间段内收到的所有
 * 需要QoS支持的消息的指纹列表且提供“重复性”判断机制，从而保证应用层绝不会因为
 * QoS的重传机制而导致重复收到消息的情况。
 * <p>
 * 当前MobileIMSDK的QoS机制支持全部的C2C、C2S、S2C共3种消息交互场景下的
 * 消息送达质量保证.
 * <p>
 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 * 
 * @version 1.0
 * @since 2.1
 */
public class QoS4ReciveDaemonC2S extends QoS4ReciveDaemonRoot
{
	private static QoS4ReciveDaemonC2S instance = null;
	
	public static QoS4ReciveDaemonC2S getInstance()
	{
		if(instance == null)
			instance = new QoS4ReciveDaemonC2S();
		return instance;
	}
	
	public QoS4ReciveDaemonC2S()
	{
		super(0 // 检查线程执行间隔（单位：毫秒），本参数<=0表示使用父类的默认值
			, 0// 一个消息放到在列表中（用于判定重复时使用）的生存时长（单位：毫秒），本参数<=0表示使用父类的默认值
			, true
			, "-本机QoS");
	}
}
