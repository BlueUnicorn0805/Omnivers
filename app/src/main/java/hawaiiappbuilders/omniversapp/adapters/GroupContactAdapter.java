package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.MaterialLetterIcon;
import hawaiiappbuilders.omniversapp.model.ContactInfo;

public class GroupContactAdapter extends RecyclerView.Adapter<GroupContactAdapter.MyViewHolder> implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        itemListener.onItemClicked(position);
    }

    public interface GroupContactItemListener {
        void onItemClicked(int position);
    }

    private LayoutInflater inflater;
    public ArrayList<ContactInfo> cList;
    private Context ctx;
    private GroupContactItemListener itemListener;

    public GroupContactAdapter(Context ctx, ArrayList<ContactInfo> catList, GroupContactItemListener listener) {
        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
        this.cList = catList;
        this.itemListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_group_users, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        ContactInfo contactInfo = cList.get(position);
        holder.tvEmail.setText(contactInfo.getEmail());
        String prefix = "";

        if (TextUtils.isEmpty(contactInfo.getName())) {
            holder.tvName.setVisibility(View.GONE);
            prefix = contactInfo.getEmail().substring(0, 2);
        } else {
            holder.tvName.setVisibility(View.VISIBLE);
            holder.tvName.setText(contactInfo.getName());

            if (TextUtils.isEmpty(contactInfo.getLname())) {
                if (contactInfo.getFname().length() >= 2) {
                    prefix = contactInfo.getFname().substring(0, 2);
                } else {
                    prefix = contactInfo.getFname();
                }
            } else {
                prefix = contactInfo.getFname().substring(0, 1) + contactInfo.getLname().substring(0, 1);
            }

            holder.ivCircleName.setLetter(prefix);
        }

        prefix = prefix.toUpperCase(Locale.ROOT);
        holder.ivCircleName.setLetter(prefix);

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected MaterialLetterIcon ivCircleName;
        protected TextView tvName;
        protected TextView tvEmail;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivCircleName = itemView.findViewById(R.id.ivCircleName);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
    }
}
