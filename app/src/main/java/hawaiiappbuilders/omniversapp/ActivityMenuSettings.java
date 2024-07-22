package hawaiiappbuilders.omniversapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityMenuSettings extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityMenuSettings.class.getSimpleName();
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_settings);

        mContext = this;
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        findViewById(R.id.btnChangePass).setOnClickListener(this);
        findViewById(R.id.btnForgotPIN).setOnClickListener(this);
        findViewById(R.id.btnImportContacts).setOnClickListener(this);
        findViewById(R.id.btnScreenOptions).setOnClickListener(this);
        findViewById(R.id.btnBackup).setOnClickListener(this);
        findViewById(R.id.btnComment).setOnClickListener(this);
        findViewById(R.id.btnViewSupportTickets).setOnClickListener(this);

        // not defined
        findViewById(R.id.btnDeleteAccount).setOnClickListener(this);

        if (appSettings.getUserId() == 1 || appSettings.getUserId() == 1434740) {
            findViewById(R.id.btnSecret).setVisibility(View.VISIBLE);
            findViewById(R.id.btnSecret).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(mContext, ActivitySecret.class));
                }
            });
        }
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
        } else if (viewId == R.id.btnChangePass) {
            startActivity(new Intent(mContext, ActivityChangePassword.class));
        } else if (viewId == R.id.btnForgotPIN) {
            Intent intent = new Intent(mContext, ForgotPwdActivity.class);
            intent.putExtra("forgot_pin", true);
            startActivity(intent);
        } else if (viewId == R.id.btnScreenOptions) {
            startActivity(new Intent(mContext, ActivityScreenOptions.class));
        } else if (viewId == R.id.btnImportContacts) {
            startActivity(new Intent(mContext, ImportContactActivity.class));
        } else if (viewId == R.id.btnComment) {
            startActivity(new Intent(mContext, ActivityComment.class));
        } else if (viewId == R.id.btnViewSupportTickets) {
            showToastMessage("Not Setup");
        } else if(viewId == R.id.btnDeleteAccount) {
            startActivity(new Intent(mContext, ActivityDeleteAccount.class));
        }
    }
}
