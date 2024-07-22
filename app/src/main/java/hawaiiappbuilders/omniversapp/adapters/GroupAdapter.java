package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.MaterialLetterIcon;
import hawaiiappbuilders.omniversapp.model.GroupInfo;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        itemListener.onItemClicked(position);
    }

    public interface GroupItemListener {
        void onItemClicked(int position);
    }

    private LayoutInflater inflater;
    public ArrayList<GroupInfo> cList;
    private Context ctx;
    private GroupItemListener itemListener;

    public GroupAdapter(Context ctx, ArrayList<GroupInfo> catList, GroupItemListener listener) {
        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
        this.cList = catList;
        this.itemListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_group, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        GroupInfo grpInfo = cList.get(position);
        holder.tvGroupName.setText(grpInfo.getGrpname());
        String prefix = "";

        if (grpInfo.getGrpname().length() >= 2) {
            prefix = grpInfo.getGrpname().substring(0, 2);
        } else {
            prefix = grpInfo.getGrpname();
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
        protected TextView tvGroupName;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivCircleName = itemView.findViewById(R.id.ivCircleName);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
        }
    }
}
