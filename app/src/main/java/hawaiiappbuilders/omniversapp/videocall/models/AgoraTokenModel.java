package hawaiiappbuilders.omniversapp.videocall.models;

import com.google.gson.annotations.SerializedName;

public class AgoraTokenModel extends BaseApiModel {

	@SerializedName("key")
	private String token;

	public String getCallToken() {
		return token;
	}

	public void setCallToken(String token) {
		this.token = token;
	}
}