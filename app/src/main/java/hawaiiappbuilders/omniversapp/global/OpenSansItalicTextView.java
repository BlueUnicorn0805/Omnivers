package hawaiiappbuilders.omniversapp.global;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class OpenSansItalicTextView extends androidx.appcompat.widget.AppCompatTextView {

    public OpenSansItalicTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OpenSansItalicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OpenSansItalicTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Italic.ttf");
            setTypeface(tf);
        }
    }

}