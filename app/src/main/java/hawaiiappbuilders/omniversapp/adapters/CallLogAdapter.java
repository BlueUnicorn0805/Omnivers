package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.CallHistory;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.PhonenumberUtils;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ItemViewHolder> implements View.OnClickListener {

    private Context context;
    CallLogAdapter.RecyclerViewClickListener listener;

    private List<CallHistory> mDataList;
    private String TAG = CallLogAdapter.class.getSimpleName();

    private int selectedItemID = -1;

    public CallLogAdapter(Context context, List<CallHistory> contactInfos, CallLogAdapter.RecyclerViewClickListener listener) {
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
        public TextView tvDate, tvNumber, tvName, tvTime;
        public ImageView ivStatus;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mMainLinearLayout = itemView.findViewById(R.id.main_ll);

            ivStatus = itemView.findViewById(R.id.ivStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calllog, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        CallHistory contactInfo = mDataList.get(position);

        holder.tvDate.setText(DateUtil.toStringFormat_31(contactInfo.getCallDate()));
        holder.tvNumber.setText(PhonenumberUtils.getFormattedPhoneNumber(contactInfo.getPhNumber()));
        holder.tvTime.setText(DateUtil.toStringFormat_10(contactInfo.getCallDate()));
        holder.tvName.setText(contactInfo.getName());

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