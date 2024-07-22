package hawaiiappbuilders.omniversapp.model;
public class AddInvoiceItem {
   public int qty;
   public double amt;
   public String desc;
   public AddInvoiceItem() {

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

   public String getDesc() {
      return desc;
   }

   public void setDesc(String desc) {
      this.desc = desc;
   }
}