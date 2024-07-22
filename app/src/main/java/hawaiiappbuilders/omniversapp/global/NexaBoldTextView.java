package hawaiiappbuilders.omniversapp.global;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class NexaBoldTextView extends TextView {

    public NexaBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NexaBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NexaBoldTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/nexa-bold.ttf");
            setTypeface(tf);
        }
    }

}