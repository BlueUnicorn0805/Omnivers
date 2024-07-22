package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuItem implements Parcelable {

    private int _id;
    private String _name;
    private String _price;
    private String _taxfees;
    private String _totPrice;
    private String _lineTot;
    private String _subTotal;

    private String _description;
    private String _category;
    private boolean _hasOptions;
    private int _imgResId;
    private String _size;
    private int _taxable;

    private int _quantity = 0;

    private boolean selected;

    protected MenuItem(Parcel in) {
        _id = in.readInt();
        _name = in.readString();
        _price = in.readString();
        _taxfees = in.readString();
        _totPrice = in.readString();
        _lineTot = in.readString();
        _subTotal = in.readString();

        _description = in.readString();
        _category = in.readString();
        _hasOptions = in.readByte() != 0;
        _imgResId = in.readInt();
        _size = in.readString();
        _taxable = in.readInt();

        _quantity = in.readInt();
    }

    public static final Creator<MenuItem> CREATOR = new Creator<MenuItem>() {
        @Override
        public MenuItem createFromParcel(Parcel in) {
            return new MenuItem(in);
        }

        @Override
        public MenuItem[] newArray(int size) {
            return new MenuItem[size];
        }
    };

    public int get_id() {
        return _id;
    }
    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }
    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_price() {
        return _price;
    }
    public void set_price(String value) {
        this._price = value;
    }

    public String get_taxfees() { return _taxfees; }
    public void set_taxfees(String _taxfees) { this._taxfees = _taxfees; }

    public String get_totPrice() { return _totPrice; }
    public void set_totPrice(String _totPrice) { this._totPrice = _totPrice; }

    public String get_lineTot() { return _lineTot; }
    public void set_lineTot(String _lineTot) { this._lineTot = _lineTot; }

    public String get_subTotal() { return _subTotal; }
    public void set_subTotal(String _subTotal) { this._subTotal = _subTotal; }

    public String get_description() {
        return _description;
    }
    public void set_description(String _description) {
        this._description = _description;
    }

    public String get_category() {
        return _category;
    }
    public void set_category(String _category) {
        this._category = _category;
    }

    public boolean is_hasOptions() {
        return _hasOptions;
    }
    public void set_hasOptions(boolean _hasOptions) {
        this._hasOptions = _hasOptions;
    }

    public void set_imgResId(int _imgResId) { this._imgResId = _imgResId; }
    public int get_imgResId() { return _imgResId; }

    public String get_size() { return _size; }
    public void set_size(String _size) { this._size = _size; }

    public int get_taxable() { return _taxable; }
    public void set_taxable(int _taxable) { this._taxable = _taxable; }

    public void set_quantity(int _quantity) { this._quantity = _quantity; }
    public int get_quantity() { return _quantity; }

    public String getMenuInfo() {
        return String.format("%s. %s", get_name(), get_price());
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public MenuItem() {}

    public MenuItem(int _id, String _name, String _price, String _description, String _category, boolean _hasOptions, int _imgResId) {
        this._id = _id;
        this._name = _name;
        this._price = _price;
        this._description = _description;
        this._category = _category;
        this._hasOptions = _hasOptions;
        this._imgResId = _imgResId;
        this._quantity = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(_name);
        dest.writeString(_price);
        dest.writeString(_taxfees);
        dest.writeString(_totPrice);
        dest.writeString(_lineTot);
        dest.writeString(_subTotal);

        dest.writeString(_description);
        dest.writeString(_category);
        dest.writeByte((byte) (_hasOptions ? 1 : 0));
        dest.writeInt(_imgResId);
        dest.writeString(_size);
        dest.writeInt(_taxable);

        dest.writeInt(_quantity);
    }
}
