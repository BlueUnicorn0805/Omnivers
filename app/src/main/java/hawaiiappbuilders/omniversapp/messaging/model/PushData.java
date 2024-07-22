package hawaiiappbuilders.omniversapp.messaging.model;

public class PushData {
    public String siteName;
    public String email;
    public String name;
    public String fn;
    public String ln;
    public String co;
    public String message;
    public String imgURL;
    public String subject;
    public int SenderID;
    public int payloadType;
    public String payloads;

    public String timesent;

    public String title;
    public String body;

    public PushData() {
    }

    public PushData(String siteName, String email, String name, String fn, String ln, String co, String message, String imgURL, String subject, int senderID, int payloadType, String payloads, String timesent) {
        this.siteName = siteName;
        this.email = email;
        this.name = name;
        this.fn = fn;
        this.ln = ln;
        this.co = co;
        this.message = message;
        this.imgURL = imgURL;
        this.subject = subject;
        SenderID = senderID;
        this.payloadType = payloadType;
        this.payloads = payloads;
        this.timesent = timesent;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public String getLn() {
        return ln;
    }

    public void setLn(String ln) {
        this.ln = ln;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getSenderID() {
        return SenderID;
    }

    public void setSenderID(int senderID) {
        SenderID = senderID;
    }

    public int getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(int payloadType) {
        this.payloadType = payloadType;
    }

    public String getPayloads() {
        return payloads;
    }

    public void setPayloads(String payloads) {
        this.payloads = payloads;
    }

    public String getTimesent() {
        return timesent;
    }

    public void setTimesent(String timesent) {
        this.timesent = timesent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}