package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.CountDownView;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class ActivityLocalShop extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityLocalShop.class.getSimpleName();
    private TextView tvBalances;
    private ImageView ivQRCode;
    private View panelInputPIN;
    private EditText edtPIN;
    private CountDownView countDown;

    private static final String QRGenerator = "AuthCode: alph##p5m!!A74*CIDy@@202095437*717947whf*91a2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localshop);

        tvBalances = findViewById(R.id.tvBalances);

        ivQRCode = findViewById(R.id.ivQRCode);
        panelInputPIN = findViewById(R.id.panelInputPIN);
        edtPIN = findViewById(R.id.edtPIN);
        edtPIN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String appPIN = appSettings.getPIN();
                String inputPIN = edtPIN.getText().toString().trim();
                if (!TextUtils.isEmpty(appPIN) && appPIN.equals(inputPIN)) {
                    showQRCode();
                    hideKeyboard(edtPIN);
                    edtPIN.setText("");
                }
            }
        });
        findViewById(R.id.btnCancel).setOnClickListener(this);
        countDown = findViewById(R.id.countDown);
        countDown.setVisibility(View.GONE);
        countDown.setOnCountDownListener(new CountDownView.OnCountDownListener() {
            @Override
            public void countDownFinished() {
                // HIDE PIN
                ivQRCode.setImageBitmap(null);
                panelInputPIN.setVisibility(View.VISIBLE);
                countDown.setVisibility(View.GONE);
            }
        });

        getAvaBalance();

        // Show Guide Page
        if (appSettings.isShowedIntroZintaPay()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent hintIntent = new Intent(mContext, AboutZintaPayActivity.class);
                    startActivity(hintIntent);
                    overridePendingTransition(R.anim.push_top_in, R.anim.push_bottom_out);

                    appSettings.setShowedIntroZintaPay(true);
                }
            }, 500);
        }
    }

    private void getAvaBalance() {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "AllBal" +
                            "&misc=" + "0";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("avaBal", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                showToastMessage(jsonObject.getString("msg"));
                            } else {
                                float fAvaBalance = (float) jsonObject.getDouble("instaCash");
                                float fAvaSavings = (float) jsonObject.getDouble("instaSavings");
                                float fLoyalty = (float) jsonObject.getDouble("Loyalty");
                                float fGift = (float) jsonObject.getDouble("Gift");
                                int fBogo = jsonObject.getInt("BOGO");

                                DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
                                tvBalances.setText(String.format("Available Funds: $%s\nRewards: $%s\nGift Card: $%s",
                                        formatter.format(fAvaBalance), formatter.format(fLoyalty), formatter.format(fGift)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showAlert(e.getMessage());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    if (TextUtils.isEmpty(error.getMessage())) {
                        showAlert(R.string.error_invalid_credentials);
                    } else {
                        showAlert(error.getMessage());
                    }

                    //showMessage(error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            sr.setShouldCache(false);
            queue.add(sr);
        }
    }

    private void showQRCode() {
        // Show PIN
        try {
            Calendar calendar = Calendar.getInstance();

            String qrContents = QRGenerator;
            // replace CID with cID.value
            qrContents = qrContents.replace("CID", String.valueOf(appSettings.getUserId()));

            // Replace ## with hour, i.e. 15
            qrContents = qrContents.replace("##", String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)));

            // replace !! with min. 23
            qrContents = qrContents.replace("!!", String.format("%02d", calendar.get(Calendar.MINUTE)));

            // replace @@ with yr. 2019
            qrContents = qrContents.replace("@@", String.valueOf(calendar.get(Calendar.YEAR)));


            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrContents, BarcodeFormat.QR_CODE, 400, 400);
            ivQRCode.setImageBitmap(bitmap);

            panelInputPIN.setVisibility(View.GONE);
            countDown.setVisibility(View.VISIBLE);
            countDown.startCountDown();
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnCancel) {
            finish();
        }
    }
}