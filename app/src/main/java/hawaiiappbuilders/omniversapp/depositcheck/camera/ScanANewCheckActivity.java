package hawaiiappbuilders.omniversapp.depositcheck.camera;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

import static hawaiiappbuilders.omniversapp.depositcheck.camera.TextRecognitionHelper.TESSERACT_PATH;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.depositcheck.checks.Check;
import hawaiiappbuilders.omniversapp.dialog.util.DialogUtil;
import hawaiiappbuilders.omniversapp.dialog.util.OnDialogViewListener;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.server.ApiUtil;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

public class ScanANewCheckActivity extends BaseActivity implements CameraView.OnTakePictureCompleteListener, OCRThread.TextRecognitionListener {
    private static final String TAG = ScanANewCheckActivity.class.getSimpleName();

    Context context;
    private CameraView cameraView;
    ProgressDialog progressDialog;
    Camera mCamera;
    String[] permissions;
    String routingNumber;
    String accountNumber;
    String checkNumber;

    private TextView textFrontBack;

    public static final String FRONT = "FRONT";
    public static final String BACK = "BACK";

    public static final String E13B = "MICR0";
    public static final String ENG = "eng";

    String area;
    AppSettings appSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_a_new_check);
        textFrontBack = findViewById(R.id.textFrontBack);
        appSettings = new AppSettings(this);
        area = getIntent().getStringExtra("mode");

        if(appSettings.getScanCheckId() == 0) {
            appSettings.setScanCheckId(1);
        } else {
            appSettings.setScanCheckId(appSettings.getScanCheckId() + 1);
        }

        textFrontBack.setText(area);
        textFrontBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = TESSERACT_PATH + area + "-" + appSettings.getScanCheckId() + ".jpg";
                cameraView.takePicture(filename);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{Manifest.permission.CAMERA,
                    WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
        }
        context = this;
        progressDialog = new ProgressDialog(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (checkPermissions()) {
            initializeCamera();
        } else {
            showRationaleAndRequestPermission(this, CAMERA, permissions, PERMISSION_REQUEST_CODE_CAMERA);
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_REQUEST_CODE_CAMERA);
            return false;
        }
        return true;
    }

    // uses MICR0.traineddata
    /*private void getNumbers() {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(filePathToDecode);
            // https://github.com/BigPino67/Tesseract-MICR-OCR
            if (!tessBaseAPI.init(filePathPictures, "MICR0", TessBaseAPI.OEM_TESSERACT_ONLY)) {
                // Error initializing Tesseract (wrong/inaccessible data path or not existing language file)
                tessBaseAPI.end();
                return;
            }

            // Specify image and then recognize it and get result (can be called multiple times during Tesseract lifetime)
            tessBaseAPI.setImage(bitmap);
            String text = tessBaseAPI.getUTF8Text();

            String[] regions = text.split("\n");
            String numbers = regions[regions.length - 1];

            // A = ⑆ transit: bank branch delimiter
            // ? = ⑇ amount: transaction amount delimiter
            // C = ⑈ on-us: customer account number delimiter
            // DDD = ⑉ dash: number delimiter (between routing and account number, for example)

            showProgressDialogWithTitle("Extracting text information");
            String[] transit = numbers.split("A");
            String[] customerAcctNumber = transit[2].split("C");
            String routingNumber = transit[1];
            Log.i(TAG, "routing no.: " + routingNumber);
            String accountNumber = customerAcctNumber[0];
            Log.i(TAG, "acct no.: " + accountNumber);
            String checkNumber = customerAcctNumber[1].trim();
            Log.i(TAG, "check no.: " + checkNumber);

            // todo: get amount, then call API
            verifyCheck(routingNumber, accountNumber, checkNumber);

            // Release Tesseract when you don't want to use it anymore
            tessBaseAPI.end();
        } catch (Exception e) {
            hideProgressDialogWithTitle();
            e.printStackTrace();
        }
    }*/

    private void verifyCheck(String routingNumber, String accountNumber, String checkNumber) {
        double amount = 12345.50; // temporary amount
        String baseUrl = BaseFunctions.getBaseUrl(mContext,
                "verifyCheckIsUsable",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());

        String extraParams =
                "&RT=" + routingNumber +
                        "&Acct=" + accountNumber +
                        "&Amt=" + amount +
                        "&Cknum=" + checkNumber;
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        new ApiUtil(mContext).callApi(baseUrl, new ApiUtil.OnHandleApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                if (response != null && !response.isEmpty()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            int status = jsonArray.getJSONObject(0).getInt("status");
                            String message = "";
                            switch (status) {
                                case -1:
                                    message = "USED";
                                    break;
                                case 0:
                                    message = "NOT BEEN USED";
                                    break;
                                case 1:
                                    message = "THERE'S BEEN AN ERROR";
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        showAlert(e.getMessage());
                    }
                }
            }

            @Override
            public void onResponseError(String msg) {
                showToastMessage(msg);
                hideProgressDialogWithTitle();
            }

            @Override
            public void onServerError() {
                hideProgressDialogWithTitle();
            }
        });
    }

    private void initializeCamera() {
        cameraView = (CameraView) findViewById(R.id.cameraView);
        mCamera = Camera.open();
        Camera.Parameters params = mCamera.getParameters();
        ArrayList<String> resolutions = new ArrayList<>();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        for (
                int i = 0; i < sizes.size(); i++) {
            int frameWidth = (int) sizes.get(i).width;
            int frameHeight = (int) sizes.get(i).height;
            // now convert int to string
            String frameWidthStr = Integer.toString(frameWidth);
            String frameHeightStr = Integer.toString(frameHeight);
            resolutions.add(frameWidthStr + "x" + frameHeightStr);
        }
        cameraView.setShowTextBounds(true);
        makeOCR(E13B);
        cameraView.setListeners(this);
    }

    public void makeOCR(String languageCode) {
        cameraView.makeOCR(this, languageCode);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onTextRecognized(String text, String languageCode, Bitmap bitmap) {
        try {
            String[] regions = text.split("\n");

            if (languageCode.contentEquals(E13B)) {
                String numbers = regions[regions.length - 1];

                // A = ⑆ transit: bank branch delimiter
                // ? = ⑇ amount: transaction amount delimiter
                // C = ⑈ on-us: customer account number delimiter
                // DDD = ⑉ dash: number delimiter (between routing and account number, for example)

                String[] transit = numbers.split("A");
                String[] customerAcctNumber = transit[2].split("C");
                routingNumber = transit[1];
                accountNumber = customerAcctNumber[0];
                checkNumber = customerAcctNumber[1].trim();
                Log.i(TAG, "routing no.: " + routingNumber);
                Log.i(TAG, "acct no.: " + accountNumber);
                Log.i(TAG, "check no.: " + checkNumber);

                // todo: get amount, then call API
                // verifyCheck(routingNumber, accountNumber, checkNumber);

                if (!routingNumber.isEmpty() || !accountNumber.isEmpty() || !checkNumber.isEmpty()) {
                    makeOCR(ENG);
                } else {
                    makeOCR(languageCode);
                }
            } else {
                String writtenTo = regions[0];
                String address = regions[2];
                String bankName = regions[4];

                String amountInFigures = "0";
                for (String region : regions) {
                    if (region.contains("$")) {
                        amountInFigures = region.split("\\$")[1].trim();
                    }
                }

                // DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
                String amountInWords = regions[7];

                if (!writtenTo.isEmpty() || !address.isEmpty() || !bankName.isEmpty()) {
                    String checkDetails = "<b>Written To</b>: " + writtenTo + "<br>" +
                            "<b>Address</b>: " + address + "<br>" +
                            "<b>Bank Name</b>: " + bankName + "<br>" +
                            "<b>Amount</b>: $" + amountInFigures + "<br>" +
                            "<b>Amount In Words</b>: " + amountInWords + "<br>" +
                            "<b>Routing No.</b>: " + routingNumber + "<br>" +
                            "<b>Account No.</b>: " + accountNumber + "<br>" +
                            "<b>Check No.</b>: " + checkNumber;
                    Spanned checkDetailsHtml = HtmlCompat.fromHtml(
                            checkDetails, HtmlCompat.FROM_HTML_MODE_COMPACT
                    );

                    // Save front bitmap
                    OCRThread.saveBitmap(this, bitmap, FRONT);

                    new DialogUtil(context).setTitleAndMessage("Check Details", checkDetails)
                            .setPositiveButton("Okay").createDialog(new OnDialogViewListener() {
                                @Override
                                public void onPositiveClick() {
                                    Check check1 = new Check();
                                    check1.setTransactionId(101010);
                                    check1.setTransactionDate(DateUtil.toStringFormat_23(new Date(DateUtil.getCurrentDate().getTimeInMillis())));
                                    check1.setAmount(12500.50);
                                    check1.setAddress(address);
                                    check1.setMemo("");
                                    check1.setBankName(bankName);
                                    check1.setName(writtenTo);
                                    check1.setAccountNumber(accountNumber);
                                    check1.setCheckNumber(checkNumber);
                                    check1.setRoutingNumber(routingNumber);
                                    check1.setFrontImage(""); // get front filepath
                                    check1.setBackImage(""); // get back filepath

                                    // Show review page
                                    Intent intent = new Intent();
                                    intent.putExtra("check", check1);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }

                                @Override
                                public void onNegativeClick() {

                                }

                                @Override
                                public void onNeutralClick() {

                                }
                            }).show();
                } else {
                    makeOCR(languageCode);
                }
            }
        } catch (Exception e) {
            makeOCR(languageCode);
            // showToastMessage("An error occurred while parsing text");
        }
    }

    @Override
    public void onBackPageScanned(Bitmap bitmap) {
        OCRThread.saveBitmap(this, bitmap, BACK);

    }

    // Method to hide/ dismiss Progress bar
    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }

    // Method to show Progress bar
    private void showProgressDialogWithTitle(String substring) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //Without this user can hide loader by tapping outside screen
        progressDialog.setCancelable(false);
        progressDialog.setMessage(substring);
        progressDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // permissions granted.
            initializeCamera();
        } else {
            StringBuilder perStr = new StringBuilder();
            for (String per : permissions) {
                perStr.append("\n").append(per);
            }
        }
    }

    @Override
    public void onTakePictureComplete(String filename) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(filename));
            cameraView.setBitmapToScan(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(int error) {

    }
}
