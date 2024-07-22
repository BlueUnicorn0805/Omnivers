package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.MenuListActivity;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.MenuItem;

public class RecyclerMenuItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;

    public ArrayList<MenuItem> menuList;

    private Context ctx;
    private MenuListActivity menuListActivity;

    public RecyclerMenuItemAdapter(Context ctx, ArrayList<MenuItem> menuList) {

        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
        this.menuList = menuList;

        this.menuListActivity = (MenuListActivity) ctx;
    }

    @Override
    public int getItemCount() {
        if (menuList == null)
            return 0;

        return menuList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        return new MenuViewNOHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MenuItem menuItem = menuList.get(position);

        ((MenuViewNOHolder) holder).menuItemImg.setImageResource(menuItem.get_imgResId());
        ((MenuViewNOHolder) holder).menuItemName.setText(menuItem.get_name());
        ((MenuViewNOHolder) holder).menuItemPrice.setText(menuItem.get_price());
        ((MenuViewNOHolder) holder).menuItemDescription.setText(menuItem.get_description());
        ((MenuViewNOHolder) holder).txtQuantity.setText(String.valueOf(menuItem.get_quantity()));

        ((MenuViewNOHolder) holder).btnPlus.setTag(position);
        ((MenuViewNOHolder) holder).btnPlus.setOnClickListener(btnPlusClickListener);

        ((MenuViewNOHolder) holder).btnMinus.setTag(position);
        ((MenuViewNOHolder) holder).btnMinus.setOnClickListener(btnMinusClickListener);
    }

    View.OnClickListener btnPlusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();

            int val = menuList.get(position).get_quantity();
            val++;
            menuList.get(position).set_quantity(val);
            notifyItemChanged(position);

            menuListActivity.updatePrice();
        }
    };

    View.OnClickListener btnMinusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();

            int val = menuList.get(position).get_quantity();
            if (val > 0) {
                val--;
                menuList.get(position).set_quantity(val);
                notifyItemChanged(position);

                menuListActivity.updatePrice();
            }
        }
    };
}
