package hawaiiappbuilders.omniversapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.OpenSansEditText;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

public class ActivityShowId extends BaseActivity {
    public static final String TAG = ActivityShowId.class.getSimpleName();
    private ImageView ivQRCode;
    private Button closeBtn;

    private OpenSansEditText tvQRMessage;

    NetworkImageView qrCodeNV;
    private static final String QRGenerator = "AuthCode: alph##p5m!!A74*CIDy@@202095437*717947whf*91a2";

    private static final boolean USEURLFORMAT = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_id);
        qrCodeNV = (NetworkImageView) findViewById(R.id.qr_code_niv);
        qrCodeNV.setBackground(null);

        tvQRMessage = findViewById(R.id.tvQRMessage);

        ivQRCode = findViewById(R.id.ivQRCode);
        closeBtn = findViewById(R.id.btnClose);

        tvQRMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                showQrCode();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        showQrCode();
    }

    private void showQrCode() {
//        JSONObject jsonObject = new JSONObject();
//        try {
            String label = "";
            if (tvQRMessage.getText() != null) {
//                if (TextUtils.isEmpty(tvQRMessage.getText())) {
//                    Toast.makeText(mContext, "Please add message!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                String mergedDataString = tvQRMessage.getText().toString().trim();
                try {
                    mergedDataString = URLEncoder.encode(mergedDataString, "UTF-8");
                    label = String.format("%s", mergedDataString.replaceAll("\\+", "%20"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
//     //       jsonObject.put("d", DateUtil.toStringFormat_13(new Date()));
//          //  jsonObject.put("indust", 0);
//          //  jsonObject.put("mode", 701);
//          //  jsonObject.put("appid", 291);
//         //   jsonObject.put("mlid", appSettings.getUserId());
//          //  jsonObject.put("per", 1);
//            jsonObject.put("pmt", 4);
//            jsonObject.put("handle", appSettings.getHandle());
//            jsonObject.put("msg", label);
//         //   String urlString = "https://omnivers.info";
            if (USEURLFORMAT) {
//                Uri.Builder builder = new Uri.Builder();
//                builder.scheme("https").authority("omnivers.info").appendPath("biz");

//                Iterator<String> iter = jsonObject.keys();
//                while (iter.hasNext()) {
//                    String key = iter.next();
//                    String value = jsonObject.optString(key);
//                    if (key.equals("mlid") || key.equals("d") || key.equals("indust") || key.equals("pmt") || key.equals("mode") || key.equals("appid") || key.equals("handle") || key.equals("msg")) {
//                        builder.appendQueryParameter(key, value);
//                    } else {
//                        builder.appendQueryParameter(key, "1");
//                    }
//                }

                String myUrl = "https://omnivers.info/biz/?" +
                        "&pmt=4" +
                        "&handle=" + appSettings.getHandle() +
                        "&msg=" + label;

                // Prevent reencode UTF8
//                String myUrl = builder.build().toString();
                Log.e("Data", myUrl);
                Bitmap bitmap = generateQRCode(myUrl);
                Log.e(TAG, "showQrCode: " + bitmap);
                ivQRCode.setImageBitmap(bitmap);
            }
//        } catch (JSONException e) {
//            zzzLogIt(e.fillInStackTrace(), "", ActivityShowId.class.getSimpleName());
//        }

        // Combine base URL and query string
    }

    private Bitmap generateQRCode(String text) {
        try {
            // Set QR code parameters
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300, hints);

            // Convert bit matrix to bitmap
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

//    private void showQRCode() {
//        // Show PIN
//        try {
//            Calendar calendar = Calendar.getInstance();
//
//            String qrContents = QRGenerator;
//            // replace CID with cID.value
//            qrContents = qrContents.replace("CID", String.valueOf(appSettings.getUserId()));
//
//            // Replace ## with hour, i.e. 15
//            qrContents = qrContents.replace("##", String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)));
//
//            // replace !! with min. 23
//            qrContents = qrContents.replace("!!", String.format("%02d", calendar.get(Calendar.MINUTE)));
//
//            // replace @@ with yr. 2019
//            qrContents = qrContents.replace("@@", String.valueOf(calendar.get(Calendar.YEAR)));
//
//
//            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrContents, BarcodeFormat.QR_CODE, 400, 400);
//            ivQRCode.setImageBitmap(bitmap);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
