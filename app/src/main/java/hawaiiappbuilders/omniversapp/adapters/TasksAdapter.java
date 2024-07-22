package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.TaskInfo;


public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ItemViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    public interface ItemSelectListener {
        void onItemSelected(boolean selected);
        void onItemClicked(int position);
        void onItemLongClicked(int position);
    }

    Context context;
    ArrayList<TaskInfo> mDataList;
    ItemSelectListener itemSelectListener;

    public TasksAdapter(Context context, ArrayList<TaskInfo> mTransactions, ItemSelectListener listener) {
        this.context = context;
        this.mDataList = mTransactions;
        this.itemSelectListener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public View panelItem;
        public CheckBox chkSelect;
        public TextView tvID, tvPriority, tvDesc, tvAssigned, tvCompleted;

        public ItemViewHolder(View itemView) {
            super(itemView);
            panelItem = itemView;
            chkSelect = itemView.findViewById(R.id.chkSelect);
            tvID = itemView.findViewById(R.id.tvID);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvAssigned = itemView.findViewById(R.id.tvAssigned);
            tvCompleted = itemView.findViewById(R.id.tvCompleted);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_tasks, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        TaskInfo taskInfo = mDataList.get(position);
        holder.tvID.setText(taskInfo.getID());
        holder.tvPriority.setText(String.format("%.2f", taskInfo.getPriority()));
        holder.tvDesc.setText(taskInfo.getDescription());
        holder.tvAssigned.setText(taskInfo.getAssigned());
        holder.tvCompleted.setText(taskInfo.getCompleted());

        holder.chkSelect.setTag(position);
        holder.chkSelect.setOnClickListener(panelItemClickListener);

        holder.chkSelect.setChecked(taskInfo.isSelected());

        holder.panelItem.setTag(position);
        holder.panelItem.setOnClickListener(this);

        holder.panelItem.setOnLongClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    View.OnClickListener panelItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = (int) v.getTag();

            mDataList.get(position).setSelected(!mDataList.get(position).isSelected());
            notifyDataSetChanged();

            if (itemSelectListener != null) {
                itemSelectListener.onItemSelected(mDataList.get(position).isSelected());
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
