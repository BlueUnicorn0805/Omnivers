package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.StoreItem;

public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemAdapter.ItemViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    public interface ItemSelectListener {
        void onItemSelected(boolean selected);
        void onItemClicked(int position);
        void onItemLongClicked(int position);
    }

    Context context;
    ArrayList<StoreItem> mDataList;
    ItemSelectListener itemSelectListener;

    public StoreItemAdapter(Context context, ItemSelectListener listener) {
        this.context = context;
        this.itemSelectListener = listener;
    }

    public void setData(ArrayList<StoreItem> newStoreItems) {
        mDataList = newStoreItems;

        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public View panelItem;
        public TextView tvName, tvCnt;

        public ItemViewHolder(View itemView) {
            super(itemView);
            panelItem = itemView;
            tvName = itemView.findViewById(R.id.tvName);
            tvCnt = itemView.findViewById(R.id.tvCnt);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_projects, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        StoreItem taskInfo = mDataList.get(position);
        holder.tvName.setText(taskInfo.getDescription());
        /*int cnt = new Random().nextInt(10);
        if (cnt == 0) {
            holder.tvCnt.setVisibility(View.GONE);
        } else {
            holder.tvCnt.setVisibility(View.VISIBLE);
            holder.tvCnt.setText(String.valueOf(cnt));
        }*/

        holder.tvCnt.setVisibility(View.GONE);

        holder.panelItem.setTag(position);
        holder.panelItem.setOnClickListener(this);

    }

    @Override
    public int getItemCount() {
        if (mDataList == null)
            return 0;
        return mDataList.size();
    }

    View.OnClickListener panelItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = (int) v.getTag();

            if (itemSelectListener != null) {
                itemSelectListener.onItemSelected(true);
            }
        }
    };

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        if (itemSelectListener != null) {
            itemSelectListener.onItemClicked(position);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int position = (int) view.getTag();
        if (itemSelectListener != null) {
            itemSelectListener.onItemLongClicked(position);
        }

        return true;
    }
}
