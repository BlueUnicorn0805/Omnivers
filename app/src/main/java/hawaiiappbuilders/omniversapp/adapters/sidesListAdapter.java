package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;


import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.SideDish;

import java.util.ArrayList;

public class sidesListAdapter extends RecyclerView.Adapter<sidesListAdapter.MyViewHolder> {


    private LayoutInflater inflater;
    public ArrayList<SideDish> sidesList;
    private Context ctx;

    public sidesListAdapter(Context ctx, ArrayList<SideDish> sideList) {

        inflater = LayoutInflater.from(ctx);
        this.sidesList = sideList;
        this.ctx = ctx;
    }

    @Override
    public sidesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_side_check, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final sidesListAdapter.MyViewHolder holder, int position) {

        String checkText = "";
        if (sidesList.get(position).get_priceBump() > 0){
            checkText = sidesList.get(position).get_SideName() + "  (+" + Double.toString(sidesList.get(position).get_priceBump()) + ")";
        }else {
            checkText = sidesList.get(position).get_SideName();
        }
        holder.checkBox.setText(checkText);
        holder.checkBox.setChecked(sidesList.get(position).is_isSelected());

        // holder.checkBox.setTag(R.integer.btnplusview, convertView);
        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer pos = (Integer) holder.checkBox.getTag();


                if (sidesList.get(pos).is_isSelected()) {
                    sidesList.get(pos).set_isSelected(false);
                } else {
                    sidesList.get(pos).set_isSelected(true);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return sidesList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.cbSide);
        }

    }
}
