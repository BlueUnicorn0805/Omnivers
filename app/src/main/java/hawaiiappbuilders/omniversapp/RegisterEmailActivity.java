package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.linkbuilder.Link;
import hawaiiappbuilders.omniversapp.linkbuilder.LinkBuilder;

public class RegisterEmailActivity extends BaseActivity implements View.OnClickListener {

    String phoneNumber = "";
    EditText edtEmail;
    TextView tvPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        tvPolicy = (TextView) findViewById(R.id.tvPolicy);

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

    @Override
    public void onClick(View view) {
        int viewid = view.getId();
        if (viewid == R.id.btnNext) {
            startRegister();
        } else if (viewid == R.id.btnBack) {
            finish();
        }
    }

    private  void startRegister() {

        final String email = edtEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
            showAlert(R.string.error_invalid_credentials);
            return;
        }

        if (getLocation()) {
            gotoVerificationScreen();
        }
    }

    private void gotoVerificationScreen() {
        Intent intent = new Intent(mContext, RegisterEmailVerifyActivity.class);
        intent.putExtra("email", edtEmail.getText().toString().trim());
        startActivityForResult(intent, 100);
    }

    private void gotoNextScreen() {
        Intent intent = new Intent(mContext, RegisterPhoneActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        //intent.putExtra("phoneCode", contryCodePicker.getDefaultCountryCodeWithPlus());
        intent.putExtra("email", edtEmail.getText().toString().trim());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            gotoNextScreen();
        } else if (requestCode == REQUEST_LOCATION) {
            // Ask location
            // Make sure call getemaillcode (even if location is still off)
            if (checkLocationPermission()) {
                getLocation();
            }
            gotoVerificationScreen();
        }
    }
}
