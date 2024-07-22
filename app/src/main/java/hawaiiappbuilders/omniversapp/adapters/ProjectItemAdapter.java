package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hawaiiappbuilders.omniversapp.R;

public class ProjectItemAdapter extends RecyclerView.Adapter<ProjectItemAdapter.ItemViewHolder> implements View.OnClickListener {

    private Context context;
    ProjectItemAdapter.RecyclerViewClickListener listener;

    private List<String> mContactDataList;
    private String TAG = ProjectItemAdapter.class.getSimpleName();

    private int selectedItemID = -1;

    public ProjectItemAdapter(Context context, List<String> contactInfos, ProjectItemAdapter.RecyclerViewClickListener listener) {
        this.context = context;
        this.mContactDataList = contactInfos;
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        int clickItemID = (int) view.getTag();

        if (listener != null) {
            listener.onClick(view, clickItemID);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mMainLinearLayout;
        public TextView tvUsername;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mMainLinearLayout = itemView.findViewById(R.id.main_ll);

            tvUsername = itemView.findViewById(R.id.tvUsername);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_milestones_data, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        /*String contactInfo = mContactDataList.get(position);

        holder.tvUsername.setText(contactInfo);

        holder.mMainLinearLayout.setTag(position);
        holder.mMainLinearLayout.setOnClickListener(this);*/
    }

    @Override
    public int getItemCount() {
        /*if (mContactDataList == null)
            return 0;

        return mContactDataList.size();*/

        return 3;
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public String getSelectedItem() {
        if (selectedItemID == -1) {
            return null;
        }

        try {
            return mContactDataList.get(selectedItemID);
        } catch (Exception e) {
            return null;
        }
    }
}