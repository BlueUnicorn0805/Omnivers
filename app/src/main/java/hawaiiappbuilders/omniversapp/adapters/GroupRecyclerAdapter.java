package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.GroupInfo;

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.ViewHolder> {

    Context mContext;
    public ArrayList<GroupInfo> datas;
    private RecyclerViewListener mListener;
    private int priValue;

    public GroupRecyclerAdapter(Context context, ArrayList<GroupInfo> datas, RecyclerViewListener listener) {
        this.datas = new ArrayList<>(datas);
        mContext = context;
        mListener = listener;
    }

    public ArrayList<GroupInfo> getSelectedList() {
        ArrayList<GroupInfo> list = new ArrayList<>();
        for (int i = 1; i < datas.size(); i++) {
            if (datas.get(i).isSelected())
                list.add(datas.get(i));
        }
        return list;
    }

    // ******************************class ViewHoler redefinition ***************************//
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_check;
        public TextView tv_name;
        public View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
            iv_check = view.findViewById(R.id.iv_check);
            tv_name = view.findViewById(R.id.tv_name);
        }

        public void setData(final int position) {
            view.setTag(position);
            view.setOnClickListener(itemClickListener);

            tv_name.setText(datas.get(position).getGrpname());
            if (datas.get(position).getPri() == priValue) {
                //iv_check.setVisibility(View.VISIBLE);

                view.setBackgroundResource(R.drawable.background_grp_select);
                tv_name.setTextColor(0xffffffff);
            } else {
                //iv_check.setVisibility(View.INVISIBLE);

                view.setBackgroundResource(R.drawable.background_grp_grey);
                tv_name.setTextColor(0xff000000);
            }
        }
    }

    View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            mListener.onItemClick(position);
        }
    };

    public void setPriValue(int value) {
        priValue = value;
        notifyDataSetChanged();
    }

    // ******************************class ViewHoler redefinition ***************************//
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.rt_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder Vholder, final int position) {
        Vholder.setData(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
}