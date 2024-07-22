package hawaiiappbuilders.omniversapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.crowdfunding.CrowdfundingActivity;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.milestone.ProfessionalsActivity;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.SearchRestaurantHelper;

public class ActivityMenuPurchase extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityMenuPurchase.class.getSimpleName();
    EditText edtEmail;

    Button btnPayGas;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_purchase);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Shop");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        mContext = this;
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        findViewById(R.id.btnMakePurchase).setOnClickListener(this);
        findViewById(R.id.btnSeeYourFavorite).setOnClickListener(this);
        findViewById(R.id.btnVendingMachine).setOnClickListener(this);
        findViewById(R.id.btnReadQR).setOnClickListener(this);
        findViewById(R.id.btnHotelKey).setOnClickListener(this);
        findViewById(R.id.btnMilestoneContracts).setOnClickListener(this);
        findViewById(R.id.btnCrowdFund).setOnClickListener(this);

        btnPayGas = findViewById(R.id.btnSaveGasMoney);
        btnPayGas.setText(HtmlCompat.fromHtml("Pay for Gas<br><small>Save on every gallon!</small>", HtmlCompat.FROM_HTML_MODE_COMPACT));
        btnPayGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ActivityPayForGas.class));
            }
        });

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
        } else if (viewId == R.id.btnMakePurchase) {
            startActivity(new Intent(mContext, ActivityAppointmentBooking.class));
        } else if (viewId == R.id.btnSeeYourFavorite) {
            getFavorites();
        } else if (viewId == R.id.btnReadQR) {
            // startActivity(new Intent(mContext, QRCodeActivity.class));
            Intent lsintent = new Intent(mContext, ActivityLocalShop.class);
            startActivity(lsintent);
        } else if (viewId == R.id.btnHotelKey) {

            showPinDlg();
        } else if (viewId == R.id.btnVendingMachine) {
            startActivity(new Intent(mContext, QRCodeActivity.class));
        } else if (viewId == R.id.btnReadQR) {
            // startActivity(new Intent(mContext, QRCodeActivity.class));
            Intent lsintent = new Intent(mContext, ActivityLocalShop.class);
            startActivity(lsintent);
        } else if (viewId == R.id.btnMilestoneContracts) {
            askMilestoneWorkType();
        } else if (viewId == R.id.btnCrowdFund) {
            startActivity(new Intent(mContext, CrowdfundingActivity.class));
        }

    }

    public void askMilestoneWorkType() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_ask_milestone_user_type, null);
        AlertDialog askDialog = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        Button lookingForWorkToDo = (Button) dialogView.findViewById(R.id.btnWorkToDo);
        Button lookingForWorkToBeDone = (Button) dialogView.findViewById(R.id.btnWorkToBeDone);

        lookingForWorkToDo.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ActivityConfirmMilestoneWork.class));
            askDialog.dismiss();
        });

        lookingForWorkToBeDone.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ProfessionalsActivity.class));
            askDialog.dismiss();
        });

        askDialog.show();
    }

    private void showPinDlg() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_alert_dialog, null);
        final TextView title = alertLayout.findViewById(R.id.dialog_title);
        title.setText("For your security, enter your PIN");
        final EditText pin = alertLayout.findViewById(R.id.pin);
        final EditText cpin = alertLayout.findViewById(R.id.pin_confirm);
        final ImageView grey_line = alertLayout.findViewById(R.id.grey_line);
        cpin.setVisibility(View.GONE);
        grey_line.setVisibility(View.GONE);
        pin.requestFocus();
        final Button submit = alertLayout.findViewById(R.id.pin_submit);
        submit.setText("Continue");

        final Button cancel = alertLayout.findViewById(R.id.pin_cancel);
        cancel.setText("Cancel");

        final androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = alert.create();
        dialog.show();

        submit.setOnClickListener(v -> {

            String appPIN = appSettings.getPIN();
            String pinNumber = pin.getText().toString().trim();
            if (!TextUtils.isEmpty(appPIN) && appPIN.equals(pinNumber)) {
                startActivity(new Intent(mContext, ActivityHotelExperience.class));
                dialog.dismiss();
            } else {
                showToastMessage("Please enter a correct PIN");
            }
        });

        cancel.setOnClickListener(view -> dialog.dismiss());
    }

    private void getFavorites() {
        if (getLocation()) {
            String extraParams = "&industryID=" + "0" +
                    "&Company=" + "" +
                    "&sellerID=" + "0" +
                    "&mode=" + SearchRestaurantHelper.MODE_FAV +
                    "&B=" + "0" +
                    "&L=" + "0" +
                    "&D=" + "0" +
                    "&it=" + "0" +
                    "&mx=" + "0" +
                    "&am=" + "0" +
                    "&asi=" + "0" +
                    "&des=" + "0" +
                    "&fr=" + "0" +
                    "&sal=" + "0" +
                    "&sea=" + "0" +
                    "&sf=" + "0" +
                    "&stk=" + "0" +
                    "&Deli=" + "0" +
                    "&gr=" + "0" +
                    "&ind=" + "0" +
                    "&jew=" + "0" +
                    "&veg=" + "0" +
                    "&gFr=" + "0" +
                    "&cof=" + "0" +
                    "&bar=" + "0" +
                    "&cat=" + "0" +
                    "&res=" + "0" +
                    "&del=" + "0";

            new SearchRestaurantHelper(ActivityMenuPurchase.this, extraParams, new SearchRestaurantHelper.SearchRestaurantCallback() {
                @Override
                public void onFailed(String message) {
                    showAlert("You have no favorites selected! Be sure to mark your favorite stores when you shop!");
                }

                @Override
                public void onSuccess(ArrayList<Restaurant> restaurants, int mode) {
                    if (restaurants.isEmpty()) {
                        showAlert("You have no favorites selected! Be sure to mark your favorite stores when you shop!");
                        return;
                    }

                    Intent intent = new Intent(mContext, ActivityFavorite.class);
                    intent.putExtra("parent", "favorites");
                    intent.putExtra("restaurants", restaurants);
                    startActivity(intent);
                }
            }, SearchRestaurantHelper.MODE_FAV).execute();
        }
    }
}
