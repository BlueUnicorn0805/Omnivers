package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.DeliveryItem;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

public class RecentDelsAdapter extends RecyclerView.Adapter<RecentDelsAdapter.MyViewHolder> {


    private LayoutInflater inflater;
    public ArrayList<DeliveryItem> itemList;
    private Context ctx;
    private BaseActivity activity;

    public RecentDelsAdapter(Context ctx, ArrayList<DeliveryItem> itemList) {

        inflater = LayoutInflater.from(ctx);
        this.itemList = itemList;
        this.ctx = ctx;
        this.activity = (BaseActivity) ctx;
    }

    @Override
    public RecentDelsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_dels_data, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final RecentDelsAdapter.MyViewHolder holder, final int position) {
        DeliveryItem itemData = itemList.get(position);
        //change image here.  may need to use picasso for ease.
        Date delDate = DateUtil.parseDataFromFormat12(itemData.getPuLatestTime().replace("T", " "));
        holder.tvTime.setText(DateUtil.toStringFormat_18(delDate));
        holder.tvAddr.setText(String.format("%s %s", itemData.gettAdd(), itemData.gettCSZ()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvTime;
        protected TextView tvAddr;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTime = (TextView) itemView.findViewById(R.id.tvDelTime);
            tvAddr = (TextView) itemView.findViewById(R.id.tvDelAddr);
        }
    }
}
