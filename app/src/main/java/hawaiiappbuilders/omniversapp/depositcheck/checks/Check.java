package hawaiiappbuilders.omniversapp.depositcheck.checks;

import android.os.Parcel;
import android.os.Parcelable;

public class Check implements Parcelable {

    private long id;
    private long transactionId;

    private String transactionDate;
    private String bankName;
    private String name;
    private String memo;
    private String address;
    private String checkNumber;
    private String accountNumber;
    private String routingNumber;
    private String frontImage;
    private String backImage;
    private double amount;

    public Check() {
    }

    protected Check(Parcel in) {
        id = in.readLong();
        transactionId = in.readLong();
        transactionDate = in.readString();
        bankName = in.readString();
        name = in.readString();
        memo = in.readString();
        address = in.readString();
        checkNumber = in.readString();
        accountNumber = in.readString();
        routingNumber = in.readString();
        frontImage = in.readString();
        backImage = in.readString();
        amount = in.readDouble();
    }

    public static final Creator<Check> CREATOR = new Creator<Check>() {
        @Override
        public Check createFromParcel(Parcel in) {
            return new Check(in);
        }

        @Override
        public Check[] newArray(int size) {
            return new Check[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(transactionId);
        parcel.writeString(transactionDate);
        parcel.writeString(bankName);
        parcel.writeString(name);
        parcel.writeString(memo);
        parcel.writeString(address);
        parcel.writeString(checkNumber);
        parcel.writeString(accountNumber);
        parcel.writeString(routingNumber);
        parcel.writeString(frontImage);
        parcel.writeString(backImage);
        parcel.writeDouble(amount);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public String getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(String frontImage) {
        this.frontImage = frontImage;
    }

    public String getBackImage() {
        return backImage;
    }

    public void setBackImage(String backImage) {
        this.backImage = backImage;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
