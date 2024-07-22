package hawaiiappbuilders.omniversapp.model;

public class OrderParty {
    String ID;
    String DateTime;
    int QTY;
    String Co;
    String WP;

    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    public String getDateTime() { return DateTime; }
    public void setDateTime(String dateTime) { DateTime = dateTime; }

    public String getCo() { return Co; }
    public void setCo(String co) { Co = co; }

    public int getQTY() { return QTY; }
    public void setQTY(int QTY) { this.QTY = QTY; }

    public String getWP() { return WP; }
    public void setWP(String WP) { this.WP = WP; }
}
