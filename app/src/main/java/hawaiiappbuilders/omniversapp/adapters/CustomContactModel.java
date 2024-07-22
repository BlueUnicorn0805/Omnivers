package hawaiiappbuilders.omniversapp.adapters;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class CustomContactModel implements Comparator<CustomContactModel>, Parcelable {
   public int id;
   public String fname;
   public String lname;
   public String address;
   public String email;
   public String name;
   public String company;
   public String phone;
   public String wp;
   public int mlid;
   public boolean isSelected;
   public int pri;
   public int type; //for view type
   public String handle; //for view type

   public CustomContactModel() {

   }

   public CustomContactModel(Parcel in) {
      id = in.readInt();
      fname = in.readString();
      lname = in.readString();
      address = in.readString();
      email = in.readString();
      name = in.readString();
      company = in.readString();
      phone = in.readString();
      wp = in.readString();
      mlid = in.readInt();
      isSelected = in.readByte() != 0;
      pri = in.readInt();
      type = in.readInt();
      handle = in.readString();
   }

   public static final Creator<CustomContactModel> CREATOR = new Creator<CustomContactModel>() {
      @Override
      public CustomContactModel createFromParcel(Parcel in) {
         return new CustomContactModel(in);
      }

      @Override
      public CustomContactModel[] newArray(int size) {
         return new CustomContactModel[size];
      }
   };


   @Override
   public int compare(CustomContactModel left, CustomContactModel right) {

      if (left.pri != right.pri) {
         return right.pri - left.pri;
      } else {
         String str1 = left.toString();
         String str2 = right.toString();

         return str1.compareTo(str2);
      }
   }

   public String getHandle() {
      return handle;
   }

   public void setHandle(String handle) {
      this.handle = handle;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getFname() {
      return fname;
   }

   public void setFname(String fname) {
      this.fname = fname;
   }

   public String getLname() {
      return lname;
   }

   public void setLname(String lname) {
      this.lname = lname;
   }

   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
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

   public String getCompany() {
      return company;
   }

   public void setCompany(String company) {
      this.company = company;
   }

   public String getPhone() {
      return phone;
   }

   public void setPhone(String phone) {
      this.phone = phone;
   }

   public String getWp() {
      return wp;
   }

   public void setWp(String wp) {
      this.wp = wp;
   }

   public int getMlid() {
      return mlid;
   }

   public void setMlid(int mlid) {
      this.mlid = mlid;
   }

   public boolean isSelected() {
      return isSelected;
   }

   public void setSelected(boolean selected) {
      isSelected = selected;
   }

   public int getPri() {
      return pri;
   }

   public void setPri(int pri) {
      this.pri = pri;
   }

   public int getType() {
      return type;
   }

   public void setType(int type) {
      this.type = type;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeInt(id);;
      parcel.writeString(fname);
      parcel.writeString(lname);
      parcel.writeString(address);
      parcel.writeString(email);
      parcel.writeString(name);
      parcel.writeString(company);
      parcel.writeString(phone);
      parcel.writeString(wp);
      parcel.writeInt(mlid);
      parcel.writeByte((byte) (isSelected ? 1 : 0));
      parcel.writeInt(pri);
      parcel.writeInt(type);
      parcel.writeString(handle);
   }
}
