package hawaiiappbuilders.omniversapp.model;

public class MissionItem {

   private Double Price;
   private String Name;
   private String Des;
   private String Size;
   private int prodID;

   public MissionItem(Double price, String name, String des, String size, int prodID) {
      Price = price;
      Name = name;
      Des = des;
      Size = size;
      this.prodID = prodID;
   }

   public Double getPrice() {
      return Price;
   }

   public void setPrice(Double price) {
      Price = price;
   }

   public String getName() {
      return Name;
   }

   public void setName(String name) {
      Name = name;
   }

   public String getDes() {
      return Des;
   }

   public void setDes(String des) {
      Des = des;
   }

   public String getSize() {
      return Size;
   }

   public void setSize(String size) {
      Size = size;
   }

   public int getProdID() {
      return prodID;
   }

   public void setProdID(int prodID) {
      this.prodID = prodID;
   }
}
