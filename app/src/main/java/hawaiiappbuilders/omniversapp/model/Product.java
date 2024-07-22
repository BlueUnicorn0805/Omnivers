package hawaiiappbuilders.omniversapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Product {

    @SerializedName("Co")
    private String company;

    @SerializedName("Address")
    private String address;

    @SerializedName("UserID")
    int userID;

    @SerializedName("ID")
    int id;

    @SerializedName("QTY")
    private int qty;

    @SerializedName("Price")
    private double amt;

    @SerializedName("Name")
    private String name;

    @SerializedName("Des")
    private String description;

    @SerializedName("Taxable")
    private int taxable;

    public Product(String company, String address, int userID, int id, int qty, double amt, String name, String description, int taxable) {
        this.company = company;
        this.address = address;
        this.userID = userID;
        this.id = id;
        this.qty = qty;
        this.amt = amt;
        this.name = name;
        this.description = description;
        this.taxable = taxable;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getAmt() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTaxable() {
        return taxable;
    }

    public void setTaxable(int taxable) {
        this.taxable = taxable;
    }
}
