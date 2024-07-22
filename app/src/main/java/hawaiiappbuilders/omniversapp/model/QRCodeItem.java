package hawaiiappbuilders.omniversapp.model;

public class QRCodeItem {
    public String fieldName;
    public String fieldValue;
    public boolean fieldIsLink;

    public QRCodeItem(String name, String value, boolean isLink) {
        fieldName = name;
        fieldValue = value;
        fieldIsLink = isLink;
    }
}
