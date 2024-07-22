package hawaiiappbuilders.omniversapp.global;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class OpenSansBoldEditText extends androidx.appcompat.widget.AppCompatEditText {

    public OpenSansBoldEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OpenSansBoldEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OpenSansBoldEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Bold.ttf");
            setTypeface(tf);
        }

        /*setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_NEXT: // fixing actionNext
                        return false;
                    default:
                        return true;
                }
            }
        });*/
    }
}