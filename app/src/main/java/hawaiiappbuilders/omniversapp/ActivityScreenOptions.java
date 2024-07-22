package hawaiiappbuilders.omniversapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

import java.io.IOException;

public class ActivityScreenOptions extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityScreenOptions.class.getSimpleName();
    RadioGroup radioGroupHomeMenu;
    RadioButton optionShowBoth;
    RadioButton optionShowIcon;
    RadioButton optionShowTitle;
    RadioButton optionRemoveAll;

    SwitchCompat compatShowAvatar;
    RadioGroup radioDashboardImage;
    RadioButton optionFullscreen;
    RadioButton optionAvatar;

    RadioGroup radioGroupTempUnit;
    RadioButton optionUnitFahrenheit;
    RadioButton optionUnitCelsius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screenoptions);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Screen Options");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        radioGroupHomeMenu = findViewById(R.id.radioGroupHomeMenu);
        optionShowBoth = findViewById(R.id.optionShowBoth);
        optionShowIcon = findViewById(R.id.optionShowIcon);
        optionShowTitle = findViewById(R.id.optionShowTitle);
        optionRemoveAll = findViewById(R.id.optionRemoveAll);

        if (appSettings.getOptionMenu() == 0) {
            optionShowBoth.setChecked(true);
        } else if (appSettings.getOptionMenu() == 1) {
            optionShowIcon.setChecked(true);
        } else if (appSettings.getOptionMenu() == 2) {
            optionShowTitle.setChecked(true);
        } else if (appSettings.getOptionMenu() == 3) {
            optionRemoveAll.setChecked(true);
        }

        compatShowAvatar = findViewById(R.id.compatShowAvatar);
        radioDashboardImage = findViewById(R.id.radioDashboardImage);
        optionFullscreen = findViewById(R.id.optionFullscreen);
        optionAvatar = findViewById(R.id.optionAvatar);

        if (appSettings.isShowAvatar()) {
            compatShowAvatar.setChecked(true);
            if (appSettings.getImageOption() == 0) {
                optionFullscreen.setChecked(true);
            } else if (appSettings.getImageOption() == 1) {
                optionAvatar.setChecked(true);
            }
        } else {
            compatShowAvatar.setChecked(false);
        }

        radioGroupTempUnit = findViewById(R.id.radioGroupTempUnit);
        optionUnitFahrenheit= findViewById(R.id.optionUnitFahrenheit);
        optionUnitCelsius= findViewById(R.id.optionUnitCelsius);
        if (appSettings.getTemperatureUnitStatus() == 0) {
            optionUnitFahrenheit.setChecked(true);
        } else {
            optionUnitCelsius.setChecked(true);
        }

        // Add Picture Button
        findViewById(R.id.btnAddPic).setOnClickListener(this);

        // Save Button
        findViewById(R.id.btnSave).setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnBack) {
            finish();
        } else if(viewId == R.id.btnToolbarHome) {
         backToHome();
        }else if (viewId == R.id.btnAddPic) {
            if (checkPermissions(mContext, PERMISSION_REQUEST_GALLERY_STRING, false, PERMISSION_REQUEST_CODE_GALLERY)) {
                launchGalleryIntent();
            }
        } else if (viewId == R.id.btnSave) {
            if (radioGroupHomeMenu.getCheckedRadioButtonId() == R.id.optionShowBoth) {
                appSettings.setOptionMenu(0);
            } else if (radioGroupHomeMenu.getCheckedRadioButtonId() == R.id.optionShowIcon) {
                appSettings.setOptionMenu(1);
            } else if (radioGroupHomeMenu.getCheckedRadioButtonId() == R.id.optionShowTitle) {
                appSettings.setOptionMenu(2);
            } else if (radioGroupHomeMenu.getCheckedRadioButtonId() == R.id.optionRemoveAll) {
                appSettings.setOptionMenu(3);
            }

            appSettings.setShowAvatar(compatShowAvatar.isChecked());
            if (optionFullscreen.isChecked()) {
                appSettings.setImageOption(0);
            } else if (optionAvatar.isChecked()) {
                appSettings.setImageOption(1);
            } else {
                appSettings.setImageOption(2);
            }

            if (optionUnitFahrenheit.isChecked()) {
                appSettings.settemperatureUnitStatus(0);
            } else {
                appSettings.settemperatureUnitStatus(1);
            }

            finish();
        }
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(mContext, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    appSettings.setAvatarImage(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
