package hawaiiappbuilders.omniversapp.global;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class NexaLightEditText extends androidx.appcompat.widget.AppCompatEditText {

    public NexaLightEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NexaLightEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NexaLightEditText(Context context) {
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