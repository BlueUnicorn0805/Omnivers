package hawaiiappbuilders.omniversapp.videocall.models;

import com.google.gson.annotations.SerializedName;

public class StartVideoModel extends BaseApiModel {

	@SerializedName("callid")
	private Integer callID;

	public Integer getCallId() {
		return callID;
	}

	public void setCallId(int callId) {
		this.callID = callId;
	}
}