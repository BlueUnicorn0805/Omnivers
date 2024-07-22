package hawaiiappbuilders.omniversapp.global;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class OpenSansSwitchCompat extends androidx.appcompat.widget.SwitchCompat {

    public OpenSansSwitchCompat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OpenSansSwitchCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OpenSansSwitchCompat(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Regular.ttf");
            setTypeface(tf);
        }
    }
}