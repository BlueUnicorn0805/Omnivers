package hawaiiappbuilders.omniversapp.adapters;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import hawaiiappbuilders.omniversapp.R;

public class MenuRestImageHolder extends RecyclerView.ViewHolder {

    protected ImageView menuRestImage;


    public MenuRestImageHolder(View itemView) {
        super(itemView);

        menuRestImage = (ImageView) itemView.findViewById(R.id.restImage);
    }

}
