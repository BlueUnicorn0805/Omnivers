package hawaiiappbuilders.omniversapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.OpenSansEditText;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.QRCodeUtil;
import info.hoang8f.android.segmented.SegmentedGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

public class ActivityFTFSendReceive extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ActivityFTFSendReceive.class.getSimpleName();
    private static final int PERMISSION_REQUESTS = 1;

    private CodeScanner mCodeScanner;

    TextView tvTitle;

    private View mFTFSendTopView;
    private View mFTFReceiveTopView;
    private View mFTFSendBottomView;
    private View mFTFReceiveBottomView;
    private RadioGroup mFTFRadioGroup;
    private RadioButton mSendRadioButton;
    private RadioButton mReceiveRadioButton;

    private SegmentedGroup segmentedGroup;
    RadioButton btnSend;
    RadioButton btnReceive;

    View panelGoCert;

    private AppSettings appSettings;

    private static final boolean USEURLFORMAT = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftf_send_receive);

        initViews();
    }

    private void initViews() {
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        findViewById(R.id.btnToolbarHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                finish();
            }
        });

        findViewById(R.id.ftf_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                finish();
            }
        });


        appSettings = new AppSettings(this);

        tvTitle = findViewById(R.id.tvTitle);

        mFTFSendTopView = (View) findViewById(R.id.ftf_send_top);
        mFTFReceiveTopView = (View) findViewById(R.id.ftf_receive_top);
        mFTFSendBottomView = (View) findViewById(R.id.ftf_send_bottom);
        mFTFReceiveBottomView = (View) findViewById(R.id.ftf_receive_bottom);

        btnSend = findViewById(R.id.btnSend);
        btnReceive = findViewById(R.id.btnReceive);
        btnSend.setChecked(true);
        segmentedGroup = findViewById(R.id.segmentedAuth);
        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.btnSend) {
                    showSend();
                } else if (checkedId == R.id.btnReceive) {
                    showReceived();
                }
            }
        });


        mFTFRadioGroup = (RadioGroup) findViewById(R.id.ftf_rg);
        mSendRadioButton = (RadioButton) findViewById(R.id.ftf_send_rb);
        mReceiveRadioButton = (RadioButton) findViewById(R.id.ftf_receive_rb);
        showSend();
        mFTFRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.ftf_send_rb:
                        showSend();
                        break;
                    case R.id.ftf_receive_rb:
                        showReceived();
                        break;
                }
            }
        });

        View layout_ftf_send_top = (View) findViewById(R.id.ftf_receive_top);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String qrcodeString = result.getText();
                        Log.e("qrcode", qrcodeString);

                        if (qrcodeString.toLowerCase().contains("mlid".toLowerCase()) || qrcodeString.toLowerCase().contains("https://omnivers.info")) {

                            Intent intent = new Intent(mContext, ActivityFTFReceiveResult.class);

                            Log.e("QRCODE", qrcodeString);
                            intent.putExtra("SCANNED_DATA", qrcodeString);
                            startActivity(intent);
                            hideKeyboard();
                            finish();
                        }
                    }
                });
            }
        });
        /*scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });*/


        /*ImageView ivMyCert = findViewById(R.id.ivMyCert);
        ivMyCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitServer(FITSERVER_CERTICON, "", 0);
            }
        });*/

        // panelGoCert = findViewById(R.id.panelGoCert);
        /*Button ftfShowIdt = (Button) findViewById(R.id.ftf_show_identification);
        ftfShowIdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ActivityFTFReceiveCertification.class));
                hitServer(FITSERVER_VIEWCERT, "", 0);
            }
        });*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
            return false;
        } else {
            return true;
        }
    }

    private void showSend() {
        tvTitle.setText("Share My Info");

        mFTFSendTopView.setVisibility(View.VISIBLE);
        mFTFSendBottomView.setVisibility(View.VISIBLE);

        mFTFReceiveTopView.setVisibility(View.GONE);
        mFTFReceiveBottomView.setVisibility(View.GONE);
        onChecked(new SendViewHolder());
    }

    private void showReceived() {
        tvTitle.setText("Scan Personal Info");

        mFTFSendTopView.setVisibility(View.GONE);
        mFTFSendBottomView.setVisibility(View.GONE);

        mFTFReceiveTopView.setVisibility(View.VISIBLE);
        mFTFReceiveBottomView.setVisibility(View.VISIBLE);
    }

    private void startScanning() {
        if (checkPermissions(mContext, PERMISSION_REQUEST_QRSCAN_STRING, false, PERMISSION_REQUEST_CODE_QRSCAN)) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        startScanning();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ftf_amt:
                break;
            case R.id.ftf_cancel:
            case R.id.qrc_cancel:
                hideKeyboard();
                finish();
                break;
            case R.id.ftf_submit:
                hideKeyboard();
                startScanning();
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "Permission granted!");

        // Check All Permission was granted
        boolean bAllGranted = true;
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                bAllGranted = false;
                break;
            }
        }

        if (bAllGranted) {
            startScanning();
        } else {
            showToastMessage("Need permissions to use function.");
        }
    }

    private class SendViewHolder {
        //        NetworkImageView qrCodeNIV;
        private OpenSansEditText tvQRMessage;
        ImageView qrPhoneOnly;
        CheckBox sendFirstName;
        CheckBox sendLastName;
        CheckBox sendCell;
        CheckBox sendAddress;
        CheckBox sendEmail;

        CheckBox sendCompany;
        CheckBox sendTitle;
        CheckBox sendPhone;
        CheckBox sendWorkAddr;
        CheckBox sendWeb;
        CheckBox sendWorkEmail;

        CheckBox sendYoutube;
        CheckBox sendFacebook;
        CheckBox sendTwitter;
        CheckBox sendLinkedIn;
        CheckBox sendPintrest;
        CheckBox sendSnapchat;
        CheckBox sendInstagram;
        CheckBox sendWhatsapp;

        Button sendQRInfo;
        JSONObject jsonObject;

        public SendViewHolder() {
            this.tvQRMessage = findViewById(R.id.tvQRMessage);

//            this.qrCodeNIV = (NetworkImageView) findViewById(R.id.qr_code_niv);
            this.qrPhoneOnly = (ImageView) findViewById(R.id.qr_phone_only);
            // Personal Options
            this.sendFirstName = (CheckBox) findViewById(R.id.send_first_name);
            this.sendLastName = (CheckBox) findViewById(R.id.send_last_name);
            this.sendCell = (CheckBox) findViewById(R.id.send_cell);
            this.sendAddress = (CheckBox) findViewById(R.id.send_address);
            this.sendEmail = (CheckBox) findViewById(R.id.send_email);

            // Work Options
            this.sendCompany = (CheckBox) findViewById(R.id.send_company);
            this.sendTitle = (CheckBox) findViewById(R.id.send_title);
            this.sendPhone = (CheckBox) findViewById(R.id.send_work_phone);
            this.sendWorkAddr = (CheckBox) findViewById(R.id.send_work_address);
            this.sendWeb = (CheckBox) findViewById(R.id.send_website);
            this.sendWorkEmail = (CheckBox) findViewById(R.id.send_work_email);

            // Social Media Options
            this.sendYoutube = (CheckBox) findViewById(R.id.send_youtube);
            this.sendFacebook = (CheckBox) findViewById(R.id.send_facebook);
            this.sendTwitter = (CheckBox) findViewById(R.id.send_twitter);
            this.sendLinkedIn = (CheckBox) findViewById(R.id.send_linkedin);
            this.sendPintrest = (CheckBox) findViewById(R.id.send_pintrest);
            this.sendSnapchat = (CheckBox) findViewById(R.id.send_snapchat);
            this.sendInstagram = (CheckBox) findViewById(R.id.send_instagram);
            this.sendWhatsapp = (CheckBox) findViewById(R.id.send_whatsapp);

            this.sendQRInfo = (Button) findViewById(R.id.send_qr_info);
            jsonObject = new JSONObject();

            sendFirstName.setChecked(false);
            sendLastName.setChecked(false);
            sendCell.setChecked(false);
            sendAddress.setChecked(false);
            sendEmail.setChecked(false);

            sendCompany.setChecked(false);
            sendTitle.setChecked(false);
            sendPhone.setChecked(false);
            sendWorkAddr.setChecked(false);
            sendWeb.setChecked(false);
            sendWorkEmail.setChecked(false);

            sendFacebook.setChecked(false);
            sendTwitter.setChecked(false);
            sendYoutube.setChecked(false);
            sendLinkedIn.setChecked(false);
            sendSnapchat.setChecked(false);
            sendInstagram.setChecked(false);
            sendWhatsapp.setChecked(false);
            sendPintrest.setChecked(false);

//            qrCodeNIV.setBackground(null);
            qrPhoneOnly.setBackground(null);

            sendQRInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard();
                    finish();
                }
            });

            this.tvQRMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (tvQRMessage.getText() != null) {
                        String mergedDataString = tvQRMessage.getText().toString().trim();
                        try {
                            mergedDataString = URLEncoder.encode(mergedDataString, "UTF-8");
                            String label = String.format("%s", mergedDataString.replaceAll("\\+", "%20"));
                            jsonObject.put("msg", label);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (!(sendFirstName.isChecked() || sendLastName.isChecked() || sendCell.isChecked() || sendAddress.isChecked() || sendEmail.isChecked()
                            || sendCompany.isChecked() || sendTitle.isChecked() || sendPhone.isChecked() || sendWorkAddr.isChecked() || sendWeb.isChecked() || sendWorkEmail.isChecked()
                            || sendYoutube.isChecked() || sendFacebook.isChecked() || sendTwitter.isChecked() || sendLinkedIn.isChecked() || sendSnapchat.isChecked() || sendInstagram.isChecked() || sendWhatsapp.isChecked())) {
                        msg("Please choose Options below before typing message");
                    } else {
                        getQRImage(jsonObject, qrPhoneOnly);
                    }
                }
            });
        }
    }

    private void onChecked(SendViewHolder sendViewHolder) {
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendFirstName, sendViewHolder.jsonObject, "fn", appSettings.getFN());
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendLastName, sendViewHolder.jsonObject, "ln", appSettings.getLN());

        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendCell,
                sendViewHolder.jsonObject, "cp", appSettings.getCP());
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendAddress,
                sendViewHolder.jsonObject, "address",
                (appSettings.getStreetNum() != null && !appSettings.getStreetNum().isEmpty()) ? (appSettings.getStreetNum()
                        + ", " + appSettings.getStreet()
                        + ",\n" + appSettings.getCity()
                        + ", " + appSettings.getSt()
                        + ", " + appSettings.getZip())
                        : (appSettings.getStreet()
                        + ",\n" + appSettings.getCity()
                        + ", " + appSettings.getSt()
                        + ", " + appSettings.getZip()));
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendEmail, sendViewHolder.jsonObject, "email", appSettings.getEmail());


        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendCompany, sendViewHolder.jsonObject, "co", appSettings.getCompany());
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendTitle, sendViewHolder.jsonObject, "title", appSettings.getTitle());
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendPhone, sendViewHolder.jsonObject, "wp", appSettings.getWP());
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendWorkAddr, sendViewHolder.jsonObject, "wa", "");
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendWeb, sendViewHolder.jsonObject, "web", "");
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendWorkEmail, sendViewHolder.jsonObject, "we", "");

        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendYoutube, sendViewHolder.jsonObject, "youtube", appSettings.getYoutube());
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendFacebook, sendViewHolder.jsonObject, "fb", appSettings.getFacebook());
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendTwitter, sendViewHolder.jsonObject, "twitter", appSettings.getTwitter());
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendLinkedIn, sendViewHolder.jsonObject, "linkedIn", appSettings.getLinkedIn());
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendPintrest, sendViewHolder.jsonObject, "pintrest", appSettings.getPintrest());
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendSnapchat, sendViewHolder.jsonObject, "snapchat", appSettings.getSnapchat());
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendInstagram, sendViewHolder.jsonObject, "instagram", appSettings.getInstagram());
        onChecked(sendViewHolder.qrPhoneOnly, sendViewHolder.sendWhatsapp, sendViewHolder.jsonObject, "whatsApp", appSettings.getWhatsApp());
    }

    private void onChecked(final ImageView qrCodePhoneImageView, CheckBox checkBox, final JSONObject jsonObject, final String jsonKey, final String jsonValue) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Add New Param
                    try {
                        jsonObject.put(jsonKey, jsonValue);
                        if (!jsonObject.has("mlid")) {
                            jsonObject.put("mlid", new AppSettings(ActivityFTFSendReceive.this).getUserId());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Remove Param
                    jsonObject.remove(jsonKey);
                    jsonObject.remove("mlid");

                    if (jsonObject.length() > 0) {
                        try {
                            jsonObject.put("mlid", new AppSettings(ActivityFTFSendReceive.this).getUserId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                getQRImage(jsonObject, qrCodePhoneImageView);
            }
        });
    }

    private void getQRImage(JSONObject jsonObject, ImageView qrCodePhoneImageView) {

//        String phoneNumber = "";
        String myUrl = "";

//        int sizeOfParams = 0;
        try {
            if (!jsonObject.has("mlid")) {
                jsonObject.put("mlid", new AppSettings(ActivityFTFSendReceive.this).getUserId());
            }
            jsonObject.put("d", DateUtil.toStringFormat_13(new Date()));
//            jsonObject.put("indust", 0);
            jsonObject.put("pmt", 4);
//            jsonObject.put("pmt", 10);
//            jsonObject.put("mode", 701);
            jsonObject.put("appid", 291);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        https://chart.googleapis.com/chart?chs=500x500
        if (USEURLFORMAT) {
            Uri.Builder builder = new Uri.Builder();
//                builder.scheme("https").authority("Z99.io");
            builder.scheme("https").authority("omnivers.info");
            builder.appendPath("biz");
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                String value = jsonObject.optString(key);
                if (key.equals("mlid") || key.equals("d") || key.equals("indust") || key.equals("pmt") || key.equals("mode") || key.equals("appid") || key.equals("msg")) {
                    builder.appendQueryParameter(key, value);
                } else {
//                        if (key.contentEquals("cp")) {
//                            phoneNumber = value;
//                        }
                    builder.appendQueryParameter(key, "1");
                }
            }

            // Prevent reencode UTF8
            myUrl = builder.build().toString();

            Log.e("QRCODE", myUrl);

//                String[] urlSplit = myUrl.split("&");
//                sizeOfParams = urlSplit.length;

//                try {
//                    myUrl = URLEncoder.encode(myUrl, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                myUrl = myUrl.replaceAll("\\+", "%20");
        }

        Log.e("QRCODE", jsonObject.toString());
        Log.e("QRCODE", myUrl);

        Bitmap bm = generateQRCode(myUrl, qrCodePhoneImageView);
        qrCodePhoneImageView.setVisibility(View.VISIBLE);
        qrCodePhoneImageView.setImageBitmap(bm);
//        }
    }

    private Bitmap generateQRCode(String text, ImageView imageView) {
        try {
            // Set QR code parameters
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Log.e("QRCODE", text);
            com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, imageView.getWidth(), imageView.getWidth(), hints);

            // Convert bit matrix to bitmap
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            Log.e("QRCODE", bitmap.getWidth() + " " + bitmap.getHeight());

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean showSuccessDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_success_dialog, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(true);
        final AlertDialog dialog = alert.create();
        dialog.show();
        return false;
    }
}