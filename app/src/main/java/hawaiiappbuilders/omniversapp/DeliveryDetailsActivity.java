package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.DeliveryItem;
import hawaiiappbuilders.omniversapp.model.OpenDeliveryInfo;

public class DeliveryDetailsActivity extends BaseActivity implements View.OnClickListener {

    Object deliveryInfo;

    EditText edtEmail;
    EditText edtPhone;
    EditText edtMessage;

    CheckBox radioNone;
    CheckBox radioCold;
    CheckBox radioHot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newdelivery);

        Intent intent = getIntent();
        deliveryInfo = intent.getParcelableExtra("delivery_info");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("Delivery Information");
        }

        radioNone = findViewById(R.id.radioNone);
        radioCold = findViewById(R.id.radioCold);
        radioHot = findViewById(R.id.radioHot);

        radioNone.setEnabled(false);
        radioCold.setEnabled(false);
        radioHot.setEnabled(false);

        radioNone.setOnClickListener(pkgTypeRadioListener);
        radioCold.setOnClickListener(pkgTypeRadioListener);
        radioHot.setOnClickListener(pkgTypeRadioListener);

        showDeliveryDetails();

        findViewById(R.id.btnOk).setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void showDeliveryDetails() {
        String fPh = "+14589562145";
        String fName = "Alice";
        String fAdd = "6500 EP True Pkwy";
        String fApt = "No.1";
        String fFloor = "10";
        String fCSZ = "110007";

        String tPh = "+14589562145";
        String tName = "Alice";
        String tAdd = "6500 EP True Pkwy";
        String tApt = "No.1";
        String tFloor = "10";
        String tCSZ = "110007";

        String instructions = "Delivery food.";
        String packageSize = "10";
        String weight = "20";
        String qty = "1";

        int none = 1;
        int hot = 0;
        int cold = 0;

        if (deliveryInfo instanceof OpenDeliveryInfo) {
            OpenDeliveryInfo delivery = (OpenDeliveryInfo) deliveryInfo;
            if (!TextUtils.isEmpty(delivery.getfPH())) {
                fPh = delivery.getfPH();
            } else {
                fPh = "";
            }

            if (!TextUtils.isEmpty(delivery.getfName())) {
                fName = delivery.getfName();
            } else {
                fName = "";
            }

            if (!TextUtils.isEmpty(delivery.getfAdd())) {
                fAdd = delivery.getfAdd();
            } else {
                fAdd = "";
            }

            if (!TextUtils.isEmpty(delivery.getfApt())) {
                fApt = delivery.getfApt();
            } else {
                fApt = "";
            }

            if (!TextUtils.isEmpty(delivery.getfFloor())) {
                fFloor = delivery.getfFloor();
            } else {
                fFloor = "";
            }

            if (!TextUtils.isEmpty(delivery.getfCSZ())) {
                fCSZ = delivery.getfCSZ();
            } else {
                fCSZ = "";
            }

            // From Informations
            if (!TextUtils.isEmpty(delivery.gettPH())) {
                tPh = delivery.gettPH();
            } else {
                tPh = "";
            }

            if (!TextUtils.isEmpty(delivery.gettName())) {
                tName = delivery.gettName();
            } else {
                tName = "";
            }

            if (!TextUtils.isEmpty(delivery.gettAdd())) {
                tAdd = delivery.gettAdd();
            } else {
                tAdd = "";
            }

            if (!TextUtils.isEmpty(delivery.gettApt())) {
                tApt = delivery.gettApt();
            } else {
                tApt = "";
            }

            if (!TextUtils.isEmpty(delivery.gettFloor())) {
                tFloor = delivery.gettFloor();
            } else {
                tFloor = "";
            }

            if (!TextUtils.isEmpty(delivery.gettCSZ())) {
                tCSZ = delivery.gettCSZ();
            } else {
                tCSZ = "";
            }

            instructions = delivery.getInstructions();
            packageSize = String.valueOf(delivery.getPakSize());
            weight = String.valueOf(delivery.getPakWgt());
            qty = delivery.getQTY();

            none = delivery.getNone();
            hot = delivery.getHot();
            cold = delivery.getCold();
        } else if (deliveryInfo instanceof DeliveryItem) {
            DeliveryItem delivery = (DeliveryItem) deliveryInfo;
            if (!TextUtils.isEmpty(delivery.getfPH())) {
                fPh = delivery.getfPH();
            } else {
                fPh = "";
            }

            if (!TextUtils.isEmpty(delivery.getfName())) {
                fName = delivery.getfName();
            } else {
                fName = "";
            }

            if (!TextUtils.isEmpty(delivery.getfAdd())) {
                fAdd = delivery.getfAdd();
            } else {
                fAdd = "";
            }

            if (!TextUtils.isEmpty(delivery.getfApt())) {
                fApt = delivery.getfApt();
            } else {
                fApt = "";
            }

            if (!TextUtils.isEmpty(delivery.getfFloor())) {
                fFloor = delivery.getfFloor();
            } else {
                fFloor = "";
            }

            if (!TextUtils.isEmpty(delivery.getfCSZ())) {
                fCSZ = delivery.getfCSZ();
            } else {
                fCSZ = "";
            }

            // To Informations
            if (!TextUtils.isEmpty(delivery.gettPH())) {
                tPh = delivery.gettPH();
            } else {
                tPh = "";
            }

            if (!TextUtils.isEmpty(delivery.gettName())) {
                tName = delivery.gettName();
            } else {
                tName = "";
            }

            if (!TextUtils.isEmpty(delivery.gettAdd())) {
                tAdd = delivery.gettAdd();
            } else {
                tAdd = "";
            }

            if (!TextUtils.isEmpty(delivery.gettApt())) {
                tApt = delivery.gettApt();
            } else {
                tApt = "";
            }

            if (!TextUtils.isEmpty(delivery.gettFloor())) {
                tFloor = delivery.gettFloor();
            } else {
                tFloor = "";
            }

            if (!TextUtils.isEmpty(delivery.gettCSZ())) {
                tCSZ = delivery.gettCSZ();
            } else {
                tCSZ = "";
            }

            instructions = delivery.getInstructions();
            packageSize = String.valueOf(delivery.getPakSize());
            weight = String.valueOf(delivery.getPakWgt());
            qty = delivery.getQTY();

            none = delivery.getNone();
            hot = delivery.getHot();
            cold = delivery.getCold();
        }

        EditText edtPhoneFrom = (EditText) findViewById(R.id.edtPhoneFrom);
        EditText edtNameFrom = (EditText) findViewById(R.id.edtNameFrom);
        EditText edtAddressFrom = (EditText) findViewById(R.id.edtAddressFrom);
        EditText edtApartmentFrom = (EditText) findViewById(R.id.edtApartmentFrom);
        EditText edtFloorFrom = (EditText) findViewById(R.id.edtFloorFrom);
        EditText edtCityStateZipFrom = (EditText) findViewById(R.id.edtCityStateZipFrom);

        EditText edtPhoneTo = (EditText) findViewById(R.id.edtPhoneTo);
        EditText edtNameTo = (EditText) findViewById(R.id.edtNameTo);
        EditText edtAddressTo = (EditText) findViewById(R.id.edtAddressTo);
        EditText edtApartmentTo = (EditText) findViewById(R.id.edtApartmentTo);
        EditText edtFloorTo = (EditText) findViewById(R.id.edtFloorTo);
        EditText edtCityStateZipTo = (EditText) findViewById(R.id.edtCityStateZipTo);

        EditText edtInstructions = (EditText) findViewById(R.id.edtInstructions);
        EditText edtPackageSize = (EditText) findViewById(R.id.edtPackageSize);
        EditText edtWeight = (EditText) findViewById(R.id.edtTotalWeight);
        EditText edtQTY = (EditText) findViewById(R.id.edtQTY);

        // Set Editable to False
        edtPhoneFrom.setFocusable(false);
        edtNameFrom.setFocusable(false);
        edtAddressFrom.setFocusable(false);
        edtApartmentFrom.setFocusable(false);
        edtFloorFrom.setFocusable(false);
        edtCityStateZipFrom.setFocusable(false);

        edtPhoneTo.setFocusable(false);
        edtNameTo.setFocusable(false);
        edtAddressTo.setFocusable(false);
        edtApartmentTo.setFocusable(false);
        edtFloorTo.setFocusable(false);
        edtCityStateZipTo.setFocusable(false);

        edtInstructions.setFocusable(false);
        edtPackageSize.setFocusable(false);
        edtWeight.setFocusable(false);
        edtQTY.setFocusable(false);

        // From
        edtPhoneFrom.setText(fPh);
        edtNameFrom.setText(fName);
        edtAddressFrom.setText(fAdd);
        edtApartmentFrom.setText(fApt);
        edtFloorFrom.setText(fFloor);
        edtCityStateZipFrom.setText(fCSZ);

        // To
        edtPhoneTo.setText(tPh);
        edtNameTo.setText(tName);
        edtAddressTo.setText(tAdd);
        edtApartmentTo.setText(tApt);
        edtFloorTo.setText(tFloor);
        edtCityStateZipTo.setText(tCSZ);

        // Package Information
        edtInstructions.setText(instructions);
        edtPackageSize.setText(packageSize);
        edtWeight.setText(weight);
        edtQTY.setText(qty);

        radioNone.setChecked(none > 0);
        radioHot.setChecked(hot > 0);
        radioCold.setChecked(cold > 0);

    }

    View.OnClickListener pkgTypeRadioListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewID = v.getId();

            if (viewID == R.id.radioNone) {
                radioNone.setChecked(true);
                radioCold.setChecked(false);
                radioHot.setChecked(false);
            } else if (viewID == R.id.radioCold) {
                radioNone.setChecked(false);
                radioCold.setChecked(true);
            } else if (viewID == R.id.radioHot) {
                radioNone.setChecked(false);
                radioHot.setChecked(true);
            }
        }
    };

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnSubmit) {
            sendFeedBack();
        }
    }

    private void sendFeedBack() {

        hideKeyboard(edtEmail);
        hideKeyboard(edtPhone);
        hideKeyboard(edtMessage);

        String strEmail = edtEmail.getText().toString().trim();
        String strPhone = edtPhone.getText().toString().trim();
        String strMessage = edtMessage.getText().toString().trim();

        if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPhone)) {
            showToastMessage(R.string.error_empty_user_fields);
            return;
        }

        // Check Email
        if (!isValidEmail(strEmail)) {
            showToastMessage(R.string.error_invalid_email);
            return;
        }

        // Check Email
        if (TextUtils.isEmpty(strMessage)) {
            showToastMessage(R.string.error_empty_message);
            return;
        }
    }
}
