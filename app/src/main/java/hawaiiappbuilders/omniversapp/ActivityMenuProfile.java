package hawaiiappbuilders.omniversapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import java.io.IOException;

import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityMenuProfile extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityMenuProfile.class.getSimpleName();
    EditText edtEmail;
    Button btnBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_profile);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        btnBusiness = findViewById(R.id.btnBusiness);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        findViewById(R.id.btnMyProfile).setOnClickListener(this);
        findViewById(R.id.btnSetWorkAddr).setOnClickListener(this);

        findViewById(R.id.btnFaceToFace).setOnClickListener(this);

        findViewById(R.id.btnCreateEvent).setOnClickListener(this);

        findViewById(R.id.btnSettings).setOnClickListener(this);
        btnBusiness.setOnClickListener(this);
        findViewById(R.id.btnLogout).setOnClickListener(this);
        findViewById(R.id.btnGroupInvites).setOnClickListener(this);

        findViewById(R.id.btnNotifications).setOnClickListener(this);
        findViewById(R.id.btnAddPic).setOnClickListener(this);
        findViewById(R.id.btnInvite).setOnClickListener(this);


        btnBusiness.setOnClickListener(v -> {
            try {
                Intent businessIntent = new Intent("hawaiiappbuilders.b.business.login");
                startActivity(businessIntent);
            } catch (ActivityNotFoundException e) {
                final String appPackageName = "hawaiiappbuilders.b";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showSuccessDlg() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);

        final AlertDialog errorDlg = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.findViewById(R.id.viewSuccess).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                errorDlg.dismiss();
            }
        });

        errorDlg.show();
        errorDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnBack) {
            finish();
        } else if (viewId == R.id.btnToolbarHome) {
            backToHome();
        } else if (viewId == R.id.btnMyProfile) {
            startActivity(new Intent(mContext, ActivityProfile.class));
        } else if (viewId == R.id.btnSetWorkAddr) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
            AppSettings appSettings = new AppSettings(mContext);
            builder.setTitle("Set Work Address");
            // Set up the input
            final EditText input = new EditText(mContext);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            if (!appSettings.getWorkAddress().isEmpty() && appSettings.getWorkAddress() != null) {
                input.setText(appSettings.getWorkAddress());
            }


            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String workAddress = input.getText().toString();
                    appSettings.setWorkAddress(workAddress);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        } else if (viewId == R.id.btnFaceToFace) {
            startActivity(new Intent(mContext, ActivityFTFSendReceive.class));
        } else if (viewId == R.id.btnCreateEvent) {
            startActivity(new Intent(mContext, ActivityCreateEvent.class));
        } else if (viewId == R.id.btnSettings) {
            startActivity(new Intent(mContext, ActivityMenuSettings.class));
        } else if (viewId == R.id.btnLogout) {
            if (getLocation()) {
                logoutUser(mContext, getUserLat(), getUserLon(), false);
            }
        } else if (viewId == R.id.btnGroupInvites) {
            startActivity(new Intent(mContext, ActivityInviteGroup.class));
        } else if (viewId == R.id.btnNotifications) {
            startActivity(new Intent(mContext, ActivitySettings.class));
        } else if (viewId == R.id.btnAddPic) {
            if (checkPermissions(mContext, PERMISSION_REQUEST_GALLERY_STRING, false, PERMISSION_REQUEST_CODE_GALLERY)) {
                launchGalleryIntent();
            }
        } else if (viewId == R.id.btnInvite) {
            shareApp();
        }
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(mContext, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 16); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 9);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 500);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 500);

        startActivityForResult(intent, REQUEST_IMAGE);
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
