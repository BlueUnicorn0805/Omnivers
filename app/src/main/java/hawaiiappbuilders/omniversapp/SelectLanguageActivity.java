package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class SelectLanguageActivity extends BaseActivity implements
        View.OnClickListener {

    AppSettings appSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectlang);
        appSettings = new AppSettings(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnLangDefault).setOnClickListener(this);
        findViewById(R.id.btnLangEnglish).setOnClickListener(this);
        findViewById(R.id.btnChinese).setOnClickListener(this);
        findViewById(R.id.btnSpanish).setOnClickListener(this);
        findViewById(R.id.btnJapan).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if(viewId == R.id.btnBack) {
            finish();
        } else if(viewId == R.id.btnLangDefault) {
            selectLang(0, "us");
        } else if(viewId == R.id.btnLangEnglish) {
            selectLang(1, "us");
        } else if(viewId == R.id.btnChinese) {
            selectLang(2, "cn");
        } else if(viewId == R.id.btnSpanish) {
            selectLang(3, "es");
        } else if(viewId == R.id.btnJapan) {
            selectLang(4, "jp");
        }
    }

    private void selectLang(int langID, String countryCode) {
        appSettings.setCountryLangCode(countryCode);
        appSettings.setCountryLangId(langID);
        appSettings.setCountryCode(getPhoneCode(countryCode.toLowerCase()));
        startActivity(new Intent(mContext, SelectTimezoneActivity.class));
        finish();
    }


    private String getPhoneCode(String countryCode) {
        switch (countryCode) {
            default:
                return "1";
            case "cn":
                return "86";
            case "es":
                return "34";
            case "jp":
                return "81";
        }
    }
}
