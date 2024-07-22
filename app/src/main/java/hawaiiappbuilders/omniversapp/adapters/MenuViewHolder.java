package hawaiiappbuilders.omniversapp.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import hawaiiappbuilders.omniversapp.R;

public class MenuViewHolder extends RecyclerView.ViewHolder {

    protected ImageView menuItemImg;
    protected TextView menuItemName;
    protected TextView menuItemPrice;
    protected TextView menuItemDescription;
    protected Button btnAddToCart;
    protected Spinner spnCookTemp;
    protected RecyclerView rcvSides;
    protected ConstraintLayout clOptions;
    protected Button btnAddToOrder;

    public MenuViewHolder(View itemView) {
        super(itemView);

        menuItemImg = (ImageView) itemView.findViewById(R.id.menuItemImg);
        menuItemName = (TextView) itemView.findViewById(R.id.menuItemName);
        menuItemPrice = (TextView) itemView.findViewById(R.id.menuItemPrice);
        menuItemDescription = (TextView) itemView.findViewById(R.id.menuItemDescription);
        btnAddToCart = (Button) itemView.findViewById(R.id.btnAddToCart);
        spnCookTemp = (Spinner) itemView.findViewById(R.id.spinner);
        rcvSides = (RecyclerView) itemView.findViewById(R.id.rcvSides);
        clOptions = (ConstraintLayout) itemView.findViewById(R.id.clOptions);
        btnAddToOrder = (Button) itemView.findViewById(R.id.btnAddToOrder);
    }
}
