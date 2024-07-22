package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MenuHeader implements Parcelable {

    private String _headerTitle;
    private String _headerType;

    private ArrayList<MenuItem> menuList;

    public MenuHeader(String _headerTitle, String _headerType) {
        this._headerTitle = _headerTitle;
        this._headerType = _headerType;
    }

    public MenuHeader(String _headerTitle, String _headerType, ArrayList<MenuItem> menuItems) {
        this._headerTitle = _headerTitle;
        this._headerType = _headerType;
        this.menuList = menuItems;
    }

    protected MenuHeader(Parcel in) {
        _headerTitle = in.readString();
        _headerType = in.readString();
        menuList = in.createTypedArrayList(MenuItem.CREATOR);
    }

    public static final Creator<MenuHeader> CREATOR = new Creator<MenuHeader>() {
        @Override
        public MenuHeader createFromParcel(Parcel in) {
            return new MenuHeader(in);
        }

        @Override
        public MenuHeader[] newArray(int size) {
            return new MenuHeader[size];
        }
    };

    public String get_headerTitle() {
        return _headerTitle;
    }

    public void set_headerTitle(String _headerTitle) {
        this._headerTitle = _headerTitle;
    }

    public String get_headerType() {
        return _headerType;
    }

    public void set_headerType(String _headerType) {
        this._headerType = _headerType;
    }

    public ArrayList<MenuItem> getMenuList() {
        return menuList;
    }

    public void setMenuList(ArrayList<MenuItem> menuList) {
        this.menuList = menuList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_headerTitle);
        dest.writeString(_headerType);
        dest.writeTypedList(menuList);
    }
}
