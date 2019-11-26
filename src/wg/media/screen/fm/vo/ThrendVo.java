package wg.media.screen.fm.vo;

import java.io.Serializable;

public class ThrendVo implements Serializable{
		private String content;
		private String updated;
		private String signature;
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getUpdated() {
			return updated;
		}
		public void setUpdated(String updated) {
			this.updated = updated;
		}
		public String getSignature() {
			return signature;
		}
		public void setSignature(String signature) {
			this.signature = signature;
		}
		
}
