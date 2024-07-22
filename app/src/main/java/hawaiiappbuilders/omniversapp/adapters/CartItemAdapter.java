package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hawaiiappbuilders.omniversapp.CartListActivity;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.SideDish;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.MyViewHolder> {


    private LayoutInflater inflater;
    public ArrayList<MenuItem> itemList;
    public ArrayList<SideDish> sideList;
    private Context ctx;
    private CartListActivity activity;

    public CartItemAdapter(Context ctx, ArrayList<MenuItem> itemList, ArrayList<SideDish> sides) {

        inflater = LayoutInflater.from(ctx);
        this.itemList = itemList;
        this.sideList = sides;
        this.ctx = ctx;

        if (ctx instanceof CartListActivity) {
            this.activity = (CartListActivity) ctx;
        }

    }

    @Override
    public CartItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_cart, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final CartItemAdapter.MyViewHolder holder, final int position) {

        //change image here.  may need to use picasso for ease.
        holder.cartItemImg.setImageResource(itemList.get(position).get_imgResId());
        holder.cartItemName.setText(itemList.get(position).get_name());
        holder.cartItemPrice.setText(itemList.get(position).get_price());
        holder.cartItemDescription.setText(itemList.get(position).get_description());
        holder.cartQty.setText(Integer.toString(itemList.get(position).get_quantity()));

        holder.txtQuantity.setText(Integer.toString(itemList.get(position).get_quantity()));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ctx, R.array.cookingtemp, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spnCookTemp.setAdapter(adapter);

        holder.rcvSides.setLayoutManager(new GridLayoutManager(ctx, 2));
        sidesListAdapter sidesadapter = new sidesListAdapter(ctx, sideList);
        holder.rcvSides.setAdapter(sidesadapter);

        holder.btnCartUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //show the options if not shown, hide if shown.
                if (holder.clOptions.getVisibility() == View.GONE) {
                    holder.clOptions.setVisibility(View.VISIBLE);
                    holder.btnCartUpdate.setVisibility(View.INVISIBLE);
                } else {
                    holder.clOptions.setVisibility(View.GONE);
                    holder.btnCartUpdate.setText("Update");
                }
            }
        });

        holder.btnUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.clOptions.setVisibility(View.GONE);
                holder.btnCartUpdate.setText("Update");
                holder.btnCartUpdate.setVisibility(View.VISIBLE);
            }
        });


        if (!itemList.get(position).is_hasOptions()) {
            holder.btnCartDuplicate.setVisibility(View.GONE);
            holder.btnCartUpdate.setVisibility(View.GONE);
            holder.cartQty.setVisibility(View.GONE);
            holder.cartQtyLabel.setVisibility(View.GONE);
        } else {
            holder.txtQuantity.setVisibility(View.GONE);
            holder.btnMinus.setVisibility(View.GONE);
            holder.btnPlus.setVisibility(View.GONE);
        }

        holder.btnCartDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MenuItem newItem = itemList.get(position);
                newItem.set_id(itemList.size() + 1);
                itemList.add(newItem);
                notifyDataSetChanged();

                if (activity != null) {
                    activity.updatePrice();
                }
            }
        });


        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.parseInt(holder.txtQuantity.getText().toString());
                val++;
                holder.txtQuantity.setText(Integer.toString(val));

                itemList.get(position).set_quantity(val);

                if (activity != null) {
                    activity.updatePrice();
                }
            }
        });


        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.parseInt(holder.txtQuantity.getText().toString());
                if (val > 0) {
                    val--;
                    holder.txtQuantity.setText(Integer.toString(val));

                    itemList.get(position).set_quantity(val);

                    if (activity != null) {
                        activity.updatePrice();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected ImageView cartItemImg;
        protected TextView cartItemName;
        protected TextView cartItemPrice;
        protected TextView cartItemDescription;
        protected TextView cartQty;
        protected TextView cartQtyLabel;
        protected Button btnCartUpdate;
        protected Button btnCartDuplicate;
        protected ImageButton btnPlus;
        protected ImageButton btnMinus;
        protected EditText txtQuantity;

        protected Spinner spnCookTemp;
        protected RecyclerView rcvSides;
        protected ConstraintLayout clOptions;
        protected Button btnUpdateItem;


        public MyViewHolder(View itemView) {
            super(itemView);

            cartItemImg = (ImageView) itemView.findViewById(R.id.cartItemImg);
            cartItemName = (TextView) itemView.findViewById(R.id.cartItemName);
            cartItemPrice = (TextView) itemView.findViewById(R.id.cartItemPrice);
            cartItemDescription = (TextView) itemView.findViewById(R.id.cartItemDescription);
            cartQty = (TextView) itemView.findViewById(R.id.tvOpQty);
            cartQtyLabel = (TextView) itemView.findViewById(R.id.tvOpQtyLabel);
            btnCartUpdate = (Button) itemView.findViewById(R.id.btnCartUpdate);
            btnCartDuplicate = (Button) itemView.findViewById(R.id.btnCartDuplicate);
            btnPlus = (ImageButton) itemView.findViewById(R.id.btnCartPlusNO);
            btnMinus = (ImageButton) itemView.findViewById(R.id.btnCartMinusNO);
            txtQuantity = (EditText) itemView.findViewById(R.id.txtCartQuantityNO);
            spnCookTemp = (Spinner) itemView.findViewById(R.id.spnCartTemp);
            rcvSides = (RecyclerView) itemView.findViewById(R.id.rcvCartSides);
            clOptions = (ConstraintLayout) itemView.findViewById(R.id.clCartOptions);
            btnUpdateItem = (Button) itemView.findViewById(R.id.btnUpdateItem);

        }

    }
}
