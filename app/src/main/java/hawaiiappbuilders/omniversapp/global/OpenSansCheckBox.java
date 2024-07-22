package hawaiiappbuilders.omniversapp.global;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class OpenSansCheckBox extends androidx.appcompat.widget.AppCompatCheckBox {

    public OpenSansCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OpenSansCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OpenSansCheckBox(Context context) {
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