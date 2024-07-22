package hawaiiappbuilders.omniversapp.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import hawaiiappbuilders.omniversapp.R;


public class MenuViewNOHolder extends RecyclerView.ViewHolder {

    protected ImageView menuItemImg;
    protected TextView menuItemName;
    protected TextView menuItemPrice;
    protected TextView menuItemDescription;
    protected Button btnAddToCart;
    protected ImageButton btnPlus;
    protected ImageButton btnMinus;
    protected EditText txtQuantity;


    public MenuViewNOHolder(View itemView) {
        super(itemView);

        menuItemImg = (ImageView) itemView.findViewById(R.id.menuItemImgNO);
        menuItemName = (TextView) itemView.findViewById(R.id.menuItemNameNO);
        menuItemPrice = (TextView) itemView.findViewById(R.id.menuItemPriceNO);
        menuItemDescription = (TextView) itemView.findViewById(R.id.menuItemDescriptionNO);
        //btnAddToCart = (Button) itemView.findViewById(R.id.btnAddToCartNO);
        btnPlus = (ImageButton) itemView.findViewById(R.id.btnPlusNO);
        btnMinus = (ImageButton) itemView.findViewById(R.id.btnMinusNO);
        txtQuantity = (EditText) itemView.findViewById(R.id.txtQuantityNO);
    }
}
