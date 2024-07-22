package hawaiiappbuilders.omniversapp.model;

public class CardInfo {

    private String cardId;
    private String cardName;
    private String cardNumber;
    private String cardNickname;
    private String cardExpMonth;
    private String cardExpYear;
    private String cardCVV;
    private String cardPostalCode;

    public String getCardId() { return cardId; }

    public void setCardId(String cardId) { this.cardId = cardId; }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardNickname() {
        return cardNickname;
    }

    public void setCardNickname(String cardNickname) {
        this.cardNickname = cardNickname;
    }

    public String getCardExpMonth() {
        return cardExpMonth;
    }

    public void setCardExpMonth(String expMonth) {
        this.cardExpMonth = expMonth;
    }

    public String getCardExpYear() {
        return cardExpYear;
    }

    public void setCardExpYear(String expYear) {
        this.cardExpYear = expYear;
    }

    public String getCardCVV() {
        return cardCVV;
    }

    public void setCardCVV(String cvv) {
        this.cardCVV = cvv;
    }

    public String getCardPostalCode() {
        return cardPostalCode;
    }

    public void setCardPostalCode(String postalCode) {
        this.cardPostalCode = postalCode;
    }

    public CardInfo(String name, String number, String nickName, String expMonth, String expYear, String cvv, String postalCode) {
        this.cardName = name;
        this.cardNumber = number;
        this.cardNickname = nickName;
        this.cardExpMonth = expMonth;
        this.cardExpYear = expYear;
        this.cardCVV = cvv;
        this.cardPostalCode = postalCode;
    }

    public CardInfo(String cardId, String nickName) {
        this.cardId = cardId;
        this.cardNickname = nickName;
    }
}
