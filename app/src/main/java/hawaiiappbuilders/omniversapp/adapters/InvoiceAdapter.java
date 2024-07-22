package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.InvoiceItem;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.MyViewHolder> implements View.OnClickListener {


    private LayoutInflater inflater;
    public ArrayList<InvoiceItem> itemList;
    private Context ctx;
    private BaseActivity activity;

    public interface OnClickInvoiceItemListener {
        void onClickInvoiceItem(InvoiceItem invoiceItem);
    }

    public OnClickInvoiceItemListener listener;

    public InvoiceAdapter(Context ctx, ArrayList<InvoiceItem> itemList, OnClickInvoiceItemListener listener) {

        inflater = LayoutInflater.from(ctx);
        this.itemList = itemList;
        this.ctx = ctx;
        this.activity = (BaseActivity) ctx;
        this.listener = listener;
    }

    @Override
    public InvoiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_data_invoice, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final InvoiceAdapter.MyViewHolder holder, final int position) {

        //change image here.  may need to use picasso for ease.
        InvoiceItem invoiceItem = itemList.get(position);

        String date = "<small><b><font color='#BBBBBB'>" + invoiceItem.getDt()  + " #" + invoiceItem.getOrderID() + "</font></b></small><br>";
        String desc = invoiceItem.getNameDesc().trim();
        holder.tvNameDesc.setText(HtmlCompat.fromHtml(date + desc, HtmlCompat.FROM_HTML_MODE_COMPACT));
        holder.tvStatus.setText(invoiceItem.getStatus());

        // holder.tvOrderId.setText("#" + itemList.get(position).getOrderID());
        String itemPrice = itemList.get(position).getAmt();
        float fItemPrice = 0;
        try {
            fItemPrice = Float.parseFloat(itemPrice.replace("$", ""));
        } catch (Exception e) {
        }

        holder.tvTotal.setText(String.format("$%.2f", fItemPrice));

        holder.panelMain.setTag(position);
        holder.panelMain.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();

        InvoiceItem invoiceItem = itemList.get(position);
        if(listener != null) {
            listener.onClickInvoiceItem(invoiceItem);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected View panelMain;
        protected TextView tvNameDesc;
        protected TextView tvStatus;
        protected TextView tvTotal;
        protected TextView tvOrderId;

        public MyViewHolder(View itemView) {
            super(itemView);

            panelMain = itemView;
            tvNameDesc = (TextView) itemView.findViewById(R.id.tvNameDesc);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvTotal = (TextView) itemView.findViewById(R.id.tvTotal);
            tvOrderId = (TextView) itemView.findViewById(R.id.tvOrderId);
        }
    }
}
