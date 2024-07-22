package hawaiiappbuilders.omniversapp.global;

import android.content.Context;
import android.util.AttributeSet;

public class YoutubeImageView extends androidx.appcompat.widget.AppCompatImageView {

	public YoutubeImageView(Context context) {
		super(context);
	}

	public YoutubeImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public YoutubeImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = getMeasuredWidth();
		setMeasuredDimension(width, width * 9 / 16);
	}
}