package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.OrderParty;

import java.util.ArrayList;

public class PartyOrdersAdapter extends RecyclerView.Adapter<PartyOrdersAdapter.MyViewHolder> {


    private LayoutInflater inflater;
    public ArrayList<OrderParty> itemList;
    private Context ctx;
    private BaseActivity activity;

    public PartyOrdersAdapter(Context ctx, ArrayList<OrderParty> itemList) {

        inflater = LayoutInflater.from(ctx);
        this.itemList = itemList;
        this.ctx = ctx;
        this.activity = (BaseActivity) ctx;
    }

    @Override
    public PartyOrdersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_party_info, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final PartyOrdersAdapter.MyViewHolder holder, final int position) {

        //change image here.  may need to use picasso for ease.
        holder.tvDateTime.setText(itemList.get(position).getDateTime().replace("T", "\n"));
        holder.tvResName.setText(itemList.get(position).getCo());
        holder.tvPhone.setText(itemList.get(position).getWP());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvDateTime;
        protected TextView tvResName;
        protected TextView tvPhone;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
            tvResName = (TextView) itemView.findViewById(R.id.tvResName);
            tvPhone = (TextView) itemView.findViewById(R.id.tvPhone);
        }
    }
}
