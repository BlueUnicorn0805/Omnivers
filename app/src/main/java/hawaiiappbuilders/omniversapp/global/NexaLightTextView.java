package hawaiiappbuilders.omniversapp.global;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class NexaLightTextView extends androidx.appcompat.widget.AppCompatTextView {

    public NexaLightTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NexaLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NexaLightTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/nexa-light.ttf");
            setTypeface(tf);
        }
    }

}