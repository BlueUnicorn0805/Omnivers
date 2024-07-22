package hawaiiappbuilders.omniversapp.carousel;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adds margin to the left and right sides of the RecyclerView item.
 * Adapted from https://stackoverflow.com/a/27664023/4034572
 * @param horizontalMarginInDp the margin resource, in dp.
 */
public class HorizontalMarginItemDecoration extends RecyclerView.ItemDecoration {
    private final int horizontalMarginInPx;
    Context context;

    public HorizontalMarginItemDecoration(Context context, int horizontalMarginInDp) {
        this.horizontalMarginInPx  = (int) context.getResources().getDimension(horizontalMarginInDp);
        this.context = context;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = horizontalMarginInPx;
        outRect.bottom = horizontalMarginInPx;
    }
}