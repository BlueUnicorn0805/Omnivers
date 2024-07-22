package hawaiiappbuilders.omniversapp.global;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class VideoThumbImageView extends androidx.appcompat.widget.AppCompatImageView {

	public VideoThumbImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Drawable d = getDrawable();

		if (d != null) {
			// ceil not round - avoid thin vertical gaps along the left/right
			// edges
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = (int) Math.ceil((float) width
					* (float) 9
					/ (float) 16);
			setMeasuredDimension(width, height);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}