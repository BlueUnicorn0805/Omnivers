package hawaiiappbuilders.omniversapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.linkbuilder.Link;
import hawaiiappbuilders.omniversapp.linkbuilder.LinkBuilder;
import hawaiiappbuilders.omniversapp.utils.PhonenumberUtils;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class RegisterPhoneActivity extends BaseActivity implements View.OnClickListener {

    String email;
    CountryCodePicker countryCodePicker;
    EditText edtPhone;
    TextView tvPolicy;
    PhonenumberUtils phonenumberUtils;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);

        phonenumberUtils = new PhonenumberUtils(this);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        mContext = this;
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        //countryCodePicker.registerPhoneNumberTextView(edtPhone);

        countryCodePicker = findViewById(R.id.countryCodePicker);
        countryCodePicker.setCountryForPhoneCode(appSettings.getCountryCode().isEmpty() ? 1 : Integer.parseInt(appSettings.getCountryCode()));
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                setHint();
            }
        });
        setHint();
        tvPolicy = findViewById(R.id.tvPolicy);

        List<Link> links = new ArrayList<>();

        // create a single click link to the github page
        Link agreement = new Link("User Agreement");
        agreement.setTypeface(Typeface.DEFAULT).
                setTextColor(Color.parseColor("#3366cc"))
                .setOnClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String clickedText) {
                        openLink("https://HawaiiAppBuilders.com/privacy-policy/");
                    }
                });

        // create a single click link to the matched twitter profiles
        Link privacy = new Link("Privacy Policy");
        privacy.setTypeface(Typeface.DEFAULT).
                setTextColor(Color.parseColor("#3366cc"))
                .setOnClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String clickedText) {
                        openLink("https://HawaiiAppBuilders.com/privacy-policy/");
                    }
                });
        links.add(agreement);
        links.add(privacy);

        // Add the links and make the links clickable
        LinkBuilder.on(tvPolicy).addLinks(links).build();


        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnNext).setOnClickListener(this);
    }

    private void setHint() {
        PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.createInstance(mContext);
        PhoneNumberUtil.PhoneNumberType mobile = PhoneNumberUtil.PhoneNumberType.MOBILE;
        Phonenumber.PhoneNumber phoneNumber = mPhoneUtil.getExampleNumberForType(countryCodePicker.getSelectedCountryNameCode(), mobile);
        if (phoneNumber == null) {
            edtPhone.setHint("");
            return;
        }
        String hint = mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        edtPhone.setHint(hint);
    }

    @Override
    public void onClick(View view) {
        int viewid = view.getId();
        if (viewid == R.id.btnNext) {
            startRegister();
        } else if (viewid == R.id.btnBack) {
            finish();
        }
    }

    private void startRegister() {

        final String strPhoneNumber = edtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(strPhoneNumber)) {
            showAlert(R.string.error_invalid_credentials);
            return;
        }

//        if (!phonenumberUtils.isValidPhoneNumber(strPhoneNumber)) {
//            showAlert("Use correct number of digits for your country");
//            return;
//        }

        Intent intent = new Intent(mContext, RegisterPhoneVerifyActivity.class);
        //intent.putExtra("phoneCode", countryCodePicker.getDefaultCountryCodeWithPlus());
        intent.putExtra("phoneNumber", strPhoneNumber);
        intent.putExtra("countryCode", countryCodePicker.getSelectedCountryCode());
        startActivityForResult(intent, 100);

    }

    private void gotoNextScreen() {

        //Intent intent = new Intent(mContext, ActivityRegistration.class);
        Intent intent = new Intent(mContext, RegisterCheckNameActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        //intent.putExtra("phoneCode", contryCodePicker.getDefaultCountryCodeWithPlus());
        intent.putExtra("phoneNumber", PhonenumberUtils.getFilteredPhoneNumber(edtPhone.getText().toString().trim()));
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            gotoNextScreen();
        }
    }
}
