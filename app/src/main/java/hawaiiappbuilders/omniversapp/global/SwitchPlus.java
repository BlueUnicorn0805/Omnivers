package hawaiiappbuilders.omniversapp.global;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Switch;

public class SwitchPlus extends Switch {

    public SwitchPlus(Context context) {
        super(context);
    }

    public SwitchPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchPlus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        changeColor(checked);
    }

    private void changeColor(boolean isChecked) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int thumbColor;
            int trackColor;

            if(isChecked) {
                thumbColor = Color.parseColor("#5bb85d");;
                trackColor = thumbColor;
            } else {
                thumbColor = Color.parseColor("#ffffff");
                trackColor = Color.parseColor("#ffffff");
            }

            try {
                getThumbDrawable().setColorFilter(thumbColor, PorterDuff.Mode.MULTIPLY);
                getTrackDrawable().setColorFilter(trackColor, PorterDuff.Mode.MULTIPLY);
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}