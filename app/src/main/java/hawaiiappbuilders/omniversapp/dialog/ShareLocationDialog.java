package hawaiiappbuilders.omniversapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.ContactInfo;

public class ShareLocationDialog extends Dialog implements View.OnClickListener {

    ShareLocationButtonClickListener shareLocationButtonClickListener;

    public ShareLocationDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public ShareLocationDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public Activity activity;
    public Dialog dialog;
    public ContactInfo contactInfo;
    public RadioButton shareEmergencyRBtn;
    public RadioButton shareEmailRBtn;
    public RadioButton shareInAppRBtn;
    public TextView toMyLocationBtn;
    public TextView toMapLocationBtn;

    public TextView toMyLocationRepeatBtn;

    public static final int MY_LOCATION = 3;
    public static final int MAP_LOCATION = 4;

    public ShareLocationDialog(Activity a, ContactInfo contactInfo, ShareLocationButtonClickListener shareLocationButtonClickListener) {
        super(a);
        this.activity = a;
        this.contactInfo = contactInfo;
        this.shareLocationButtonClickListener = shareLocationButtonClickListener;
        setupLayout();
    }

    private void setupLayout() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_share_location);

        shareEmergencyRBtn = findViewById(R.id.radio_emagency);
        shareEmailRBtn = findViewById(R.id.radio_email);
        shareInAppRBtn = findViewById(R.id.radio_in_app);

        toMyLocationBtn = findViewById(R.id.button_to_my_location);
        toMyLocationRepeatBtn = findViewById(R.id.button_to_my_location_repeat); // moving current location
        toMapLocationBtn = findViewById(R.id.button_to_map_location);
        toMyLocationBtn.setOnClickListener(this);
        toMapLocationBtn.setOnClickListener(this);

        /*Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);*/
    }

    @Override
    public void onClick(View v) {
        int inApp = -1;
        if (shareEmergencyRBtn.isChecked()) {
            inApp = 0;
        } else if (shareEmailRBtn.isChecked()) {
            inApp = 1;
        } else if (shareInAppRBtn.isChecked()) {
            inApp = 2;
        }

        if (v.getId() == R.id.button_to_my_location) {
            shareLocationButtonClickListener.clickOnItem(contactInfo, inApp, MY_LOCATION);
        } else { // to map location
            shareLocationButtonClickListener.clickOnItem(contactInfo, inApp, MAP_LOCATION);
        }

        dismiss();
    }

    public interface ShareLocationButtonClickListener {
        void clickOnItem(ContactInfo contactInfo, int inAppOption, int wLocation);
    }

}