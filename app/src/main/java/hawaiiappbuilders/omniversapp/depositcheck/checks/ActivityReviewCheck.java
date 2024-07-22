package hawaiiappbuilders.omniversapp.depositcheck.checks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

public class ActivityReviewCheck extends BaseActivity {

    private static final String TAG = ActivityReviewCheck.class.getSimpleName();
    private EditText textAmount;
    private EditText textName;
    private EditText textAddress;
    private EditText textMemo;
    private EditText textBankName;
    private EditText textAccountNumber;
    private EditText textCheckNumber;
    private EditText textRoutingNumber;

    private ImageView frontImage;
    private ImageView backImage;

    private Button btnIncorrect;
    private Button btnCorrect;

    private Check check;

    MessageDataManager dm;


    private int MODE = 1; // 1=from history, 2=from scanner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_check);
        init();
        dm = new MessageDataManager(this);
        if (getIntent().getExtras() != null) {
            check = getIntent().getExtras().getParcelable("check");
            MODE = getIntent().getExtras().getInt("mode");
            if (MODE == 1) {
                btnIncorrect.setVisibility(View.GONE);
                btnCorrect.setVisibility(View.GONE);
            } else {
                btnIncorrect.setVisibility(View.VISIBLE);
                btnCorrect.setVisibility(View.VISIBLE);
            }
            displayCheckDetails(check);
        }
    }

    private void setEditTextEnabled(EditText editText, boolean isEnabled) {
        editText.setEnabled(isEnabled);
        if (isEnabled) {
            editText.setBackgroundResource(R.drawable.bg_edittext_white_bg_rounded);
            editText.setTextColor(getResources().getColor(R.color.black));
        } else {
            editText.setBackgroundResource(R.drawable.bg_edittext_white_bg_rounded_disabled);
            editText.setTextColor(getResources().getColor(R.color.app_grey_light));
        }
    }

    private void displayCheckDetails(Check check) {
        if (check != null) {
            // editable
            displayAmount(check.getAmount());
            setEditTextEnabled(textAmount, false);
            textName.setText(check.getName());
            setEditTextEnabled(textName, false);
            textAddress.setText(check.getAddress());
            setEditTextEnabled(textAddress, false);
            textMemo.setText(check.getMemo());
            setEditTextEnabled(textMemo, true);
            textBankName.setText(check.getBankName());
            setEditTextEnabled(textBankName, false);
            textAccountNumber.setText(check.getAccountNumber());
            setEditTextEnabled(textAccountNumber, false);
            textCheckNumber.setText(check.getCheckNumber());
            setEditTextEnabled(textCheckNumber, false);
            textRoutingNumber.setText(check.getRoutingNumber());
            setEditTextEnabled(textRoutingNumber, false);

            displayImage(this, frontImage, Uri.fromFile(new File(check.getFrontImage())));
            displayImage(this, backImage, Uri.fromFile(new File(check.getBackImage())));
        }
    }


    public static void displayImage(Context context, ImageView imageView, Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            // context.getContentResolver().delete(uri, null, null);
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            imageView.setImageBitmap(rotatedBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayAmount(double amt) {
        DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
        textAmount.setText(formatter.format(amt));
    }

    private void init() {
        frontImage = findViewById(R.id.frontImage);
        backImage = findViewById(R.id.backImage);

        // check details
        textName = findViewById(R.id.edtWrittenTo);
        textAddress = findViewById(R.id.edtAddress);
        textMemo = findViewById(R.id.edtMemo);
        textAmount = findViewById(R.id.edtAmount);
        textBankName = findViewById(R.id.edtBank);
        textAccountNumber = findViewById(R.id.edtAccountNo);
        textCheckNumber = findViewById(R.id.edtCheckNo);
        textRoutingNumber = findViewById(R.id.edtRoutingNo);

        btnIncorrect = findViewById(R.id.btnIncorrect);
        btnIncorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        btnCorrect = findViewById(R.id.btnCorrect);
        btnCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check != null) {
                    depositCheck(check);
                }
            }
        });
    }

    private void depositCheck(Check check) {
        String baseUrl = BaseFunctions.getBaseUrl(mContext,
                "depositCheck",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());

        String extraParams = "&transactionDate=" + check.getTransactionDate() +
                "&bankName=" + check.getBankName() +
                "&name=" + check.getName() +
                "&memo=" + check.getMemo() +
                "&address=" + check.getAddress() +
                "&checkNumber=" + check.getCheckNumber() +
                "&accountNumber=" + check.getAccountNumber() +
                "&routingNumber=" + check.getRoutingNumber() +
                "&amount=" + check.getAmount();
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        RequestQueue queue = Volley.newRequestQueue(mContext);

        //HttpsTrustManager.allowAllSSL();
        // GoogleCertProvider.install(mContext);
        String finalBaseUrl = baseUrl;
        StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("depositCheck", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    final JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                        showToastMessage(jsonObject.getString("msg"));
                    } else {
                        int transactionID = jsonObject.getInt("transid");
                        check.setTransactionId(transactionID);
                        dm.addCheck(check);
                        // next step: reload check history with newly added check
                        setResult(RESULT_OK);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // hideProgressDialog();
                baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
            }
        });

        sr.setShouldCache(false);
        queue.add(sr);
    }

}
