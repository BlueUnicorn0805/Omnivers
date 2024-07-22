package hawaiiappbuilders.omniversapp.global;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;

public class OpenSansVTextView extends androidx.appcompat.widget.AppCompatTextView {
    boolean topDown;

    public OpenSansVTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public OpenSansVTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public OpenSansVTextView(Context context) {
        super(context);

        init();
    }

    private void init() {
        final int gravity = getGravity();
        if (Gravity.isVertical(gravity) && (gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM) {
            setGravity((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) | Gravity.TOP);
            topDown = false;
        } else
            topDown = true;

        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Regular.ttf");
            setTypeface(tf);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        return super.setFrame(l, t, l + (b - t), t + (r - l));
    }

    @Override
    public void draw(Canvas canvas) {
        if (topDown) {
            canvas.translate(getHeight(), 0);
            canvas.rotate(90);
        } else {
            canvas.translate(0, getWidth());
            canvas.rotate(-90);
        }
        canvas.clipRect(0, 0, getWidth(), getHeight(), android.graphics.Region.Op.INTERSECT);
        super.draw(canvas);
    }
}