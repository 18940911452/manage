package wg.user.mobileimsdk.server.model.vo;

/**
 * 通知的
 * @author gongxiang.pang
 *
 */
public class UmPushVo {
	
	
	public String uid;//用户id 
	public String title;//通知标题
	public String text;//通知文字描述
	public String path;//
	public String ticker;///通知栏提示文字
	public String icon;////状态栏图标ID, R.drawable.[smallIcon],如果没有, 默认使用应用图标。
	public String largeIcon;///
	public String img;///通知栏大图标的URL链接。该字段的优先级大于largeIcon。该字段要求以http或者https开头。
	public String play_vibrate;////收到通知是否震动,默认为"true"
	public String play_lights;//收到通知是否闪灯,默认为"true"
	public String play_sound;///收到通知是否发出声音,默认为"true"
	public String sound;//通知声音，R.raw.[sound]. 如果该字段为空，采用SDK默认的声音
	
	
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getLargeIcon() {
		return largeIcon;
	}
	public void setLargeIcon(String largeIcon) {
		this.largeIcon = largeIcon;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getPlay_vibrate() {
		return play_vibrate;
	}
	public void setPlay_vibrate(String play_vibrate) {
		this.play_vibrate = play_vibrate;
	}
	public String getPlay_lights() {
		return play_lights;
	}
	public void setPlay_lights(String play_lights) {
		this.play_lights = play_lights;
	}
	public String getPlay_sound() {
		return play_sound;
	}
	public void setPlay_sound(String play_sound) {
		this.play_sound = play_sound;
	}
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
	
	
	
	
	
	
}
