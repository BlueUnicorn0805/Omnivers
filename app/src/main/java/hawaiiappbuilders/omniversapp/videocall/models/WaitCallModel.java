package hawaiiappbuilders.omniversapp.videocall.models;

import com.google.gson.annotations.SerializedName;

public class WaitCallModel extends BaseApiModel {

	@SerializedName("callstatus")
	private int callStatus;
	@SerializedName("callingIP")
	private String callingIp;

	@SerializedName("callerIP")
	private String callerIp;

	@SerializedName("callerFN")
	private String callerFn;

	@SerializedName("callerLN")
	private String callerLn;
	@SerializedName("callingUTC")
	private int callingUtc;

	public String getCallerFn() {
		return callerFn;
	}

	public void setCallerFn(String callerFn) {
		this.callerFn = callerFn;
	}

	public String getCallerLn() {
		return callerLn;
	}

	public void setCallerLn(String callerLn) {
		this.callerLn = callerLn;
	}

	public String getCallerIp() {
		return callerIp;
	}

	public void setCallerIp(String callerIp) {
		this.callerIp = callerIp;
	}

	public String getCallingIp() {
		return callingIp;
	}

	public void setCallingIp(String callingIp) {
		this.callingIp = callingIp;
	}

	public int getCallingUtc() {
		return callingUtc;
	}

	public void setCallingUtc(int callingUtc) {
		this.callingUtc = callingUtc;
	}

	public int getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(int callStatus) {
		this.callStatus = callStatus;
	}
}