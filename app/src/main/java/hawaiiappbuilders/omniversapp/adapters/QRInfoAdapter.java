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
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.QRCodeItem;

import java.util.ArrayList;
import java.util.List;

public class QRInfoAdapter extends RecyclerView.Adapter<QRInfoAdapter.ItemViewHolder> implements View.OnClickListener {

    private Context context;
    private BaseActivity activity;
    QRInfoAdapter.RecyclerViewClickListener listener;

    private List<QRCodeItem> mDataList = new ArrayList<>();
    private String TAG = QRInfoAdapter.class.getSimpleName();

    private int selectedItemID = -1;

    public QRInfoAdapter(Context context, List<QRCodeItem> dataList, QRInfoAdapter.RecyclerViewClickListener listener) {
        this.context = context;
        this.activity = (BaseActivity) context;

        mDataList = dataList;
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        int clickItemID = (int) view.getTag();

        QRCodeItem codeItem = mDataList.get(clickItemID);
        if ("Youtube".equals(codeItem.fieldName)) {
            activity.openLink("https://www.youtube.com/embed/" + codeItem.fieldValue);
        } else if ("Facebook".equals(codeItem.fieldName)) {
            activity.openLink(codeItem.fieldValue);
        } else if ("Twitter".equals(codeItem.fieldName)) {
            activity.openLink(codeItem.fieldValue);
        } else if ("LinkedIn".equals(codeItem.fieldName)) {
            activity.openLink(codeItem.fieldValue);
        } else if ("Pintrest".equals(codeItem.fieldName)) {
            activity.openLink(codeItem.fieldValue);
        } else if ("Snapchat".equals(codeItem.fieldName)) {
            activity.openLink(codeItem.fieldValue);
        } else if ("Instagram".equals(codeItem.fieldName)) {
            activity.openLink(codeItem.fieldValue);
        } else if ("WhatsApp".equals(codeItem.fieldName)) {
            activity.openLink(codeItem.fieldValue);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout mMainLinearLayout;
        public TextView tvFieldName, tvFieldValue;
        public ImageView ivGoLink;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mMainLinearLayout = itemView.findViewById(R.id.main_ll);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvFieldValue = itemView.findViewById(R.id.tvFieldValue);
            ivGoLink = itemView.findViewById(R.id.ivSelected);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.qr_list_item,parent,false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        QRCodeItem  dataItem = mDataList.get(position);

        holder.tvFieldName.setText(dataItem.fieldName);
        holder.tvFieldValue.setText(dataItem.fieldValue);
        holder.ivGoLink.setVisibility(dataItem.fieldIsLink ? View.VISIBLE : View.GONE);

        holder.ivGoLink.setTag(position);
        holder.ivGoLink.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

}