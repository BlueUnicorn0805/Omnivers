package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityScanQRCode extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ActivityScanQRCode.class.getSimpleName();
    private static final int PERMISSION_REQUESTS = 1;

    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        initViews();
    }

    private void initViews() {
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

                        if (qrcodeString.toLowerCase().contains("Name".toLowerCase())
                                && qrcodeString.toLowerCase().contains("Token".toLowerCase())
                                && qrcodeString.toLowerCase().contains("Amt".toLowerCase())) {
                            Intent intent = new Intent(ActivityScanQRCode.this,ActivityDisplayPrice.class);
                            intent.putExtra("SCANNED_DATA", qrcodeString);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });

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
            case R.id.qrc_submit:

                break;
            case R.id.qrc_cancel:
                finish();
                break;
        }
    }
}