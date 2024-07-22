package hawaiiappbuilders.omniversapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.AddInvoiceItem;

public class AddInvoiceItemAdapter extends RecyclerView.Adapter<AddInvoiceItemAdapter.ItemViewHolder> {
   Context context;
   BaseActivity activity;
   ArrayList<AddInvoiceItem> mInvoiceItems;


   public interface OnClickInvoiceItemListener {
      void updateAddInvoiceItem(int index, AddInvoiceItem item);
   }

   private OnClickInvoiceItemListener listener;

   public AddInvoiceItemAdapter(Context context, ArrayList<AddInvoiceItem> mInvoiceItems, OnClickInvoiceItemListener listener) {
      this.context = context;
      this.activity = (BaseActivity) context;
      this.mInvoiceItems = mInvoiceItems;
      this.listener = listener;
   }

   public static class ItemViewHolder extends RecyclerView.ViewHolder {

      public EditText edtQty;
      public EditText edtAmt;
      public EditText edtDesc;

      public ItemViewHolder(View itemView) {
         super(itemView);
         edtQty = itemView.findViewById(R.id.tvInvoiceQTY);
         edtAmt = itemView.findViewById(R.id.tvInvoiceAmt);
         edtDesc = itemView.findViewById(R.id.tvInvoiceDesc);
      }
   }

   @NonNull
   @Override
   public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_invoice_item, parent, false);
      return new ItemViewHolder(v);
   }

   @SuppressLint("SetTextI18n")
   @Override
   public void onBindViewHolder(@NonNull ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
      AddInvoiceItem invoiceItem = mInvoiceItems.get(position);


      holder.edtQty.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

         }

         @Override
         public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (!charSequence.toString().isEmpty()) {
               String qty = holder.edtQty.getText().toString().trim();
               invoiceItem.setQty(Integer.parseInt(qty));
            } else {
               invoiceItem.setQty(0);
            }
            if (listener != null) {
               listener.updateAddInvoiceItem(position, invoiceItem);
            }
         }

         @Override
         public void afterTextChanged(Editable s) {

         }
      });


      holder.edtAmt.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

         }

         @Override
         public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (!charSequence.toString().isEmpty()) {
               if (charSequence.toString().contentEquals(".")) {
                  holder.edtAmt.setText("0.");
                  invoiceItem.setAmt(0.0);
               } else {
                  String amt = holder.edtAmt.getText().toString().trim();
                  double amtFormatted = Double.parseDouble(amt);
                  invoiceItem.setAmt(amtFormatted);
               }
            } else {
               invoiceItem.setAmt(0.0);
            }
            if (listener != null) {
               listener.updateAddInvoiceItem(position, invoiceItem);
            }
         }

         @Override
         public void afterTextChanged(Editable s) {

         }
      });

      holder.edtDesc.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

         }

         @Override
         public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (!charSequence.toString().isEmpty()) {
               String desc = holder.edtDesc.getText().toString().trim();
               invoiceItem.setDesc(desc);
            } else {
               invoiceItem.setDesc("");
            }
            if (listener != null) {
               listener.updateAddInvoiceItem(position, invoiceItem);
            }
         }

         @Override
         public void afterTextChanged(Editable s) {

         }
      });

   }

   @Override
   public int getItemCount() {
      return mInvoiceItems.size();
   }


}
