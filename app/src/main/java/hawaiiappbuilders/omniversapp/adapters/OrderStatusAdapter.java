package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.OrderData;

import java.util.ArrayList;
import java.util.List;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.ItemViewHolder> implements View.OnClickListener {

    private Context context;
    OrderStatusAdapter.RecyclerViewClickListener listener;

    private List<OrderData> mOrderInfoList = new ArrayList<>();
    private String TAG = OrderStatusAdapter.class.getSimpleName();


    public OrderStatusAdapter(Context context, ArrayList<OrderData> orderInfoList, OrderStatusAdapter.RecyclerViewClickListener listener) {
        this.context = context;
        this.mOrderInfoList = orderInfoList;
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        int clickItemID = (int) view.getTag();

        if (listener != null) {
            listener.onClick(view, clickItemID);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout mMainLinearLayout;
        public TextView tvOrderId, tvSellerName, tvStatus;
        public ImageView mItemSelected;

        public ItemViewHolder(View itemView) {
            super(itemView);

            mMainLinearLayout = itemView.findViewById(R.id.mMainLinearLayout);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvSellerName = itemView.findViewById(R.id.tvSellerName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_order,parent,false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        OrderData dataItem = mOrderInfoList.get(position);

        holder.mMainLinearLayout.setTag(position);
        holder.mMainLinearLayout.setOnClickListener(this);
        holder.tvOrderId.setText(dataItem.getID());
        holder.tvSellerName.setText(dataItem.getSeller());
        holder.tvStatus.setText(dataItem.getStatus());
    }

    @Override
    public int getItemCount() {
        return mOrderInfoList.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }
}