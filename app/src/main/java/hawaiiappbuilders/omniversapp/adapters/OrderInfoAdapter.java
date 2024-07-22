package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.MenuItem;

import java.util.ArrayList;

public class OrderInfoAdapter extends RecyclerView.Adapter<OrderInfoAdapter.MyViewHolder> {


    private LayoutInflater inflater;
    public ArrayList<MenuItem> itemList;
    private Context ctx;
    private BaseActivity activity;

    public OrderInfoAdapter(Context ctx, ArrayList<MenuItem> itemList) {

        inflater = LayoutInflater.from(ctx);
        this.itemList = itemList;
        this.ctx = ctx;
        this.activity = (BaseActivity) ctx;
    }

    @Override
    public OrderInfoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_data_cart, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }
    public static String rtrim(String str, String trimchar){
        if(str.substring(str.length()-trimchar.length(),str.length()).equalsIgnoreCase(trimchar)){
            str = str.substring(0,str.length() - trimchar.length());
        }
        return str;
    }

    public static String ltrim(String str, String trimchar){
        if(trimchar.length()<=str.length()){
            if(str.substring(0,trimchar.length()).equalsIgnoreCase(trimchar)){
                str = str.substring(trimchar.length(),str.length());
            }
        }
        return str;
    }
    @Override
    public void onBindViewHolder(final OrderInfoAdapter.MyViewHolder holder, final int position) {

        //change image here.  may need to use picasso for ease.
        String name = ltrim(itemList.get(position).get_name(), ":");
        holder.tvNameDesc.setText(name.trim());
        holder.tvQTY.setText(String.valueOf(
                itemList.get(position).get_quantity() == 0 ? 1 : itemList.get(position).get_quantity()
                ));
        holder.tvAmt.setText(itemList.get(position).get_price());
        int itemQty = itemList.get(position).get_quantity();
        String itemPrice = "0";
        if (!TextUtils.isEmpty(itemList.get(position).get_lineTot())) {
            itemPrice = String.valueOf(Float.parseFloat(itemList.get(position).get_lineTot().replace("$","")));
        }
        float fItemPrice = 0;
        try {
            fItemPrice = Float.parseFloat(itemPrice);
        } catch (Exception e) {
        }

        holder.tvTotal.setText(String.format("$%.2f", fItemPrice/* * itemQty*/));
        if (fItemPrice == 0) {
            holder.tvTotal.setVisibility(View.GONE);
        } else {
            holder.tvTotal.setVisibility(View.VISIBLE);
        }
    }

    /*CompoundButton.OnCheckedChangeListener itemSelectListenenr = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            MenuItem item = (MenuItem) buttonView.getTag();
            item.setSelected(isChecked);
            activity.updatePrice();
        }
    };*/

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvNameDesc;
        protected TextView tvQTY;
        protected TextView tvAmt;
        protected TextView tvTotal;


        public MyViewHolder(View itemView) {
            super(itemView);

            tvNameDesc = (TextView) itemView.findViewById(R.id.tvNameDesc);
            tvQTY = (TextView) itemView.findViewById(R.id.tvQTY);
            tvAmt = (TextView) itemView.findViewById(R.id.tvAmt);
            tvTotal = (TextView) itemView.findViewById(R.id.tvTotal);
        }
    }
}
