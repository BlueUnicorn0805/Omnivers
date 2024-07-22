package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hawaiiappbuilders.omniversapp.R;

public class SearchContactAdapter extends RecyclerView.Adapter<SearchContactAdapter.ItemViewHolder> implements View.OnClickListener {

    private Context context;
    SearchContactAdapter.RecyclerViewClickListener listener;

    private List<CustomContactModel> mDataList;
    private String TAG = SearchContactAdapter.class.getSimpleName();

    private int selectedItemID = -1;

    public SearchContactAdapter(Context context, List<CustomContactModel> contactInfos, SearchContactAdapter.RecyclerViewClickListener listener) {
        this.context = context;
        this.mDataList = contactInfos;
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        int clickItemID = (int) view.getTag();

        if (clickItemID == selectedItemID) {
            selectedItemID = -1;
        } else {
            selectedItemID = clickItemID;
        }

        notifyDataSetChanged();

        if (listener != null) {
            listener.onClick(view, clickItemID);
        }

        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mMainLinearLayout;
        public TextView tvName, tvEmail;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mMainLinearLayout = itemView.findViewById(R.id.main_ll);

            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchcontact, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        CustomContactModel contactInfo = mDataList.get(position);

        if (TextUtils.isEmpty(contactInfo.company)) {
            holder.tvName.setText(contactInfo.getName());
        } else {
            holder.tvName.setText(contactInfo.company);
        }

        String email = contactInfo.getEmail();
        if (TextUtils.isEmpty(email)) {
            email = contactInfo.phone;
        }
        if (TextUtils.isEmpty(email)) {
            holder.tvEmail.setVisibility(View.GONE);
        } else {
            holder.tvEmail.setText(email);
            holder.tvEmail.setVisibility(View.VISIBLE);
        }

        holder.mMainLinearLayout.setTag(position);
        holder.mMainLinearLayout.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        if (mDataList == null)
            return 0;

        return mDataList.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }
}