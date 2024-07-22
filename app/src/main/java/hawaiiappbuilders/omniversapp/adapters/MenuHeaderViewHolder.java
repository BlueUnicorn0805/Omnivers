package hawaiiappbuilders.omniversapp.adapters;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import hawaiiappbuilders.omniversapp.R;

public class MenuHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final HeaderViewHolderCallback callback;

    TextView MenuHeader;
    RecyclerView rcvItems;

    Drawable arrowUp;
    Drawable arrowDown;

    public MenuHeaderViewHolder(View itemView, HeaderViewHolderCallback callback) {
        super(itemView);

        this.callback = callback;

        MenuHeader = itemView.findViewById(R.id.menuheader);
        rcvItems = itemView.findViewById(R.id.rcvItems);

        arrowUp = ContextCompat.getDrawable(itemView.getContext(), android.R.drawable.arrow_up_float);
        arrowDown = ContextCompat.getDrawable(itemView.getContext(), android.R.drawable.arrow_down_float);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        callback.onHeaderClick(position);
        if (callback.isExpanded(position)) {
            MenuHeader.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowUp, null);
        } else {
            MenuHeader.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDown, null);
        }
    }

    public interface HeaderViewHolderCallback {
        void onHeaderClick(int position);

        boolean isExpanded(int position);
    }

}
