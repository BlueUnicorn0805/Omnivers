package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ZAUQRCodeActivity extends BaseActivity {

    AppCompatActivity _activity;
    private CodeScanner mCodeScanner;
    boolean processing = false;


    Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            final String scanResult = msg.getData().getString("result");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_zauqrcode);

        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Scan Code");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initStuff();
    }

    private void initStuff() {
        _activity = this;
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

                        if (!processing) {

                            vibrate(200);

                            Log.e("ScanResult", qrcodeString);

                            processing = true;

                            Message message = new Message();
                            message.what = 0;
                            Bundle data = new Bundle();
                            data.putString("result", qrcodeString);
                            message.setData(data);
                            mUIHandler.sendMessage(message);
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
    protected void onDestroy() {
        super.onDestroy();

        mUIHandler.removeMessages(0);
        mUIHandler.removeMessages(1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
