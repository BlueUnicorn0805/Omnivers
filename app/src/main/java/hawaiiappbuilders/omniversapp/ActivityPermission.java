package hawaiiappbuilders.omniversapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityPermission extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityPermission.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnDeny).setOnClickListener(this);
        findViewById(R.id.btnAccept).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnBack) {
            finish();
        } else if (viewId == R.id.btnDeny) {
            denyLocation();
        } else if (viewId == R.id.btnAccept) {
            acceptLocation();
        }
    }

    private void denyLocation() {
        appSettings.setLocationPermission(-1);
        finish();
    }

    private void acceptLocation() {
        appSettings.setLocationPermission(0);

        // Check Permissions
        if (checkPermissions(mContext, PERMISSION_REQUEST_LOCATION_STRING, true, PERMISSION_REQUEST_CODE_LOCATION)) {
            appSettings.setLocationPermission(1);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check All Permission was granted
        boolean bAllGranted = true;
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                bAllGranted = false;
                break;
            }
        }

        if (bAllGranted) {
            appSettings.setLocationPermission(1);
            finish();
        } else {
            showLocationSettingsAlert();
        }
    }
}
