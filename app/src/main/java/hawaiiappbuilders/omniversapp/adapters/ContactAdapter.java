package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.ContactInfo;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ItemViewHolder> implements View.OnClickListener {

    private Context context;
    ContactAdapter.RecyclerViewClickListener listener;

    private List<ContactInfo> mContactDataList;
    private String TAG = ContactAdapter.class.getSimpleName();

    private int selectedItemID = -1;

    public ContactAdapter(Context context, List<ContactInfo> contactInfos, ContactAdapter.RecyclerViewClickListener listener) {
        this.context = context;
        this.mContactDataList = contactInfos;
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
        public TextView tvUsername, tvPhone, tvEmail;
        public ImageView mItemSelected;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mMainLinearLayout = itemView.findViewById(R.id.main_ll);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);

            mItemSelected = itemView.findViewById(R.id.ivSelected);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        ContactInfo contactInfo = mContactDataList.get(position);

        holder.tvUsername.setText(contactInfo.getName());
        String phoneInfo = contactInfo.getPhoneData().trim();
        String emailInfo = contactInfo.getEmailData().trim();
        if (TextUtils.isEmpty(phoneInfo)) {
            holder.tvPhone.setVisibility(View.GONE);
        } else {
            holder.tvPhone.setVisibility(View.VISIBLE);
            holder.tvPhone.setText(phoneInfo);
        }
        if (TextUtils.isEmpty(emailInfo)) {
            holder.tvEmail.setVisibility(View.GONE);
        } else {
            holder.tvEmail.setVisibility(View.VISIBLE);
            holder.tvEmail.setText(emailInfo);
        }

        if (selectedItemID == position) {
            holder.mItemSelected.setVisibility(View.VISIBLE);
        } else {
            holder.mItemSelected.setVisibility(View.GONE);
        }

        holder.mMainLinearLayout.setTag(position);
        holder.mMainLinearLayout.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        if (mContactDataList == null)
            return 0;

        return mContactDataList.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public ContactInfo getSelectedItem() {
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