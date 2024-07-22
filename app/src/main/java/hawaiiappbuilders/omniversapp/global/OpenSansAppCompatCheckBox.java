package hawaiiappbuilders.omniversapp.global;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class OpenSansAppCompatCheckBox extends androidx.appcompat.widget.AppCompatCheckBox {

    public OpenSansAppCompatCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OpenSansAppCompatCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OpenSansAppCompatCheckBox(Context context) {
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