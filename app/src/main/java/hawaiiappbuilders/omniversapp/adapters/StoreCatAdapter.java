package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.StoreCate;

public class StoreCatAdapter extends RecyclerView.Adapter<StoreCatAdapter.ItemViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    public interface ItemSelectListener {
        void onItemSelected(boolean selected);
        void onItemClicked(int position);
        void onItemLongClicked(int position);
    }

    Context context;
    ArrayList<StoreCate> mDataList;
    ItemSelectListener itemSelectListener;

    public StoreCatAdapter(Context context, ArrayList<StoreCate> mTransactions, ItemSelectListener listener) {
        this.context = context;
        this.mDataList = mTransactions;
        this.itemSelectListener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public View panelItem;
        public TextView tvName, tvCnt;
        public AppCompatCheckBox chkSelect;

        public ItemViewHolder(View itemView) {
            super(itemView);
            panelItem = itemView;
            tvName = itemView.findViewById(R.id.tvName);
            tvCnt = itemView.findViewById(R.id.tvCnt);
            chkSelect = itemView.findViewById(R.id.chkSelect);
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
        StoreCate taskInfo = mDataList.get(position);
        holder.tvName.setText(taskInfo.getDescription());
        int cnt = taskInfo.getItems().size();
        if (cnt == 0) {
            holder.tvCnt.setVisibility(View.GONE);
        } else {
            holder.tvCnt.setVisibility(View.VISIBLE);
            holder.tvCnt.setText(String.valueOf(cnt));
        }

        holder.panelItem.setTag(position);
        holder.panelItem.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
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
