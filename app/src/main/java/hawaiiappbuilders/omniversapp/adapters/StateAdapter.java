package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.StateDataProvider;

import java.util.ArrayList;
import java.util.List;


public class StateAdapter extends RecyclerView.Adapter<StateAdapter.ItemViewHolder> {

    private Context context;
    private RecyclerViewClickListener listener;
    private List<StateDataProvider.StateInfo> mStaffDataList = new ArrayList<>();
    private String currentState;

    public StateAdapter(Context context, List<StateDataProvider.StateInfo> dataList, String currentState, RecyclerViewClickListener listener) {
        this.context = context;
        this.mStaffDataList = dataList;
        this.currentState = currentState;
        this.listener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public ViewGroup viewItemPanel;
        public TextView tvStateName;
        public ImageView selected;

        public ItemViewHolder(View itemView) {
            super(itemView);

            viewItemPanel = itemView.findViewById(R.id.viewItemPanel);
            tvStateName = itemView.findViewById(R.id.tvStateName);
            selected = itemView.findViewById(R.id.selected);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_state, parent, false);

        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.tvStateName.setText(mStaffDataList.get(position).name);;
        holder.selected.setVisibility(View.GONE);

        if (mStaffDataList.get(position).abbr.equals(currentState)) {
            holder.selected.setVisibility(View.VISIBLE);
        } else {
            holder.selected.setVisibility(View.INVISIBLE);
        }

        holder.viewItemPanel.setTag(position);
        holder.viewItemPanel.setOnClickListener(itemClickListener);
    }

    View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if (listener != null) {
                listener.onClick(v, position);
            }
        }
    };

    @Override
    public int getItemCount() {
        return mStaffDataList.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }
}