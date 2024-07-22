package hawaiiappbuilders.omniversapp.videocall.models;

import com.google.gson.annotations.SerializedName;

public class BaseApiModel {

    @SerializedName("msg")
    private String msg;

    @SerializedName("status")
    private boolean status;

    public String getMsg(){
        return msg;
    }

    public boolean isStatus(){
        return status;
    }
}
