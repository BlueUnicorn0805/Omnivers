package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityLoginAuth extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityLoginAuth.class.getSimpleName();
    TextView tvRemainingSecs;

    ArrayList<TextView> arrayListCode1 = new ArrayList<>();
    ArrayList<TextView> arrayListCode2 = new ArrayList<>();

    int rectIndex;
    TextView tvCode51, tvCode52;
    String zaCode = "";

    private static final int TIME_UPDATE_INTERVAL = 15;
    int timeRemainingInSec = TIME_UPDATE_INTERVAL;

    // Menu action Buttons
    Animation mShowMenuButton;
    Animation mHideMenuButton;
    Animation mShowMenuLayout;
    Animation mHideMenuLayout;
    Animation mShowMenuBack;
    Animation mHideMenuBack;
    View panelMenuBackground;
    ViewGroup panelMenus;
    FloatingActionButton fbAddReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_auth);

        tvRemainingSecs = findViewById(R.id.tvRemainingSecs);

        findViewById(R.id.option1).setOnClickListener(this);
        findViewById(R.id.option2).setOnClickListener(this);
        findViewById(R.id.option3).setOnClickListener(this);
        findViewById(R.id.option4).setOnClickListener(this);
        findViewById(R.id.option5).setOnClickListener(this);

        arrayListCode1.add(findViewById(R.id.tvCode11));
        arrayListCode1.add(findViewById(R.id.tvCode21));
        arrayListCode1.add(findViewById(R.id.tvCode31));
        arrayListCode1.add(findViewById(R.id.tvCode41));

        arrayListCode2.add(findViewById(R.id.tvCode12));
        arrayListCode2.add(findViewById(R.id.tvCode22));
        arrayListCode2.add(findViewById(R.id.tvCode32));
        arrayListCode2.add(findViewById(R.id.tvCode42));

        tvCode51 = findViewById(R.id.tvCode51);
        tvCode52 = findViewById(R.id.tvCode52);

        mShowMenuButton = AnimationUtils.loadAnimation(mContext, R.anim.home_menu_button_show);
        mHideMenuButton = AnimationUtils.loadAnimation(mContext, R.anim.home_menu_button_hide);
        mShowMenuLayout = AnimationUtils.loadAnimation(mContext, R.anim.home_menu_layout_show);
        mHideMenuLayout = AnimationUtils.loadAnimation(mContext, R.anim.home_menu_layout_hide);
        mShowMenuBack = AnimationUtils.loadAnimation(mContext, R.anim.home_menu_back_show);
        mHideMenuBack = AnimationUtils.loadAnimation(mContext, R.anim.home_menu_back_hide);
        mHideMenuBack.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                panelMenuBackground.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fbAddReminder = findViewById(R.id.fbAddReminder);
        panelMenuBackground = findViewById(R.id.panelMenuBackground);
        panelMenus = findViewById(R.id.panelMenus);
        panelMenuBackground.setVisibility(View.GONE);
        panelMenus.setVisibility(View.GONE);
        fbAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (panelMenus.getVisibility() == View.GONE) {
                    showMenuPanel();
                } else {
                    hideMenuPanel();
                }
            }
        });
        panelMenuBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenuPanel();
            }
        });

        findViewById(R.id.btnScanQRCode).setOnClickListener(this);
        findViewById(R.id.btnEnterSetupKey).setOnClickListener(this);

        updateCodes();

        updateSeconds();

        // Start Counter Time
        codeUpdateHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private void updateSeconds() {
        String sourceString = "<strong><b> " + timeRemainingInSec + "</b></strong>";
        tvRemainingSecs.setText(Html.fromHtml(sourceString));
    }

    private void showMenuPanel() {
        panelMenus.setVisibility(View.VISIBLE);
        panelMenuBackground.setVisibility(View.VISIBLE);

        // Start Animation
        panelMenus.startAnimation(mShowMenuLayout);
        panelMenuBackground.startAnimation(mShowMenuBack);
        fbAddReminder.startAnimation(mShowMenuButton);
    }

    private void hideMenuPanel() {
        panelMenus.setVisibility(View.GONE);
        panelMenuBackground.setVisibility(View.GONE);

        // Start Animation
        panelMenus.startAnimation(mHideMenuLayout);
        panelMenuBackground.startAnimation(mHideMenuBack);
        fbAddReminder.startAnimation(mHideMenuButton);
    }

    Handler codeUpdateHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (isFinishing()) {
                return;
            }

            timeRemainingInSec--;

            if (timeRemainingInSec == 0) {
                updateCodes();
                timeRemainingInSec = TIME_UPDATE_INTERVAL;
            }

            updateSeconds();

            sendEmptyMessageDelayed(0, 1000);
        }
    };

    private void updateCodes() {
        Random rand = new Random();
        for (int i = 0; i < 4; i++) {
            String code1 = String.format("%04d", rand.nextInt(10000));
            String code2 = String.format("%04d", rand.nextInt(10000));

            arrayListCode1.get(i).setText(code1);
            arrayListCode2.get(i).setText(code2);
        }

        rectIndex = rand.nextInt(4);
        zaCode = String.format("%08d", calcZA(113021, 182));
        //String zaCode1 = zaCode.substring(0, 4);
        //String zaCode2 = zaCode.substring(4, 8);

        String zaCode1 = arrayListCode1.get(rectIndex).getText().toString();
        String zaCode2 = arrayListCode2.get(rectIndex).getText().toString();

        arrayListCode1.get(rectIndex).setText(zaCode1);
        arrayListCode2.get(rectIndex).setText(zaCode2);

        tvCode51.setText(zaCode1);
        tvCode52.setText(zaCode2);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        codeUpdateHandler.removeMessages(0);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("ResList", "Landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.e("ResList", "Portrait");
        }

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("ResList", "Landscape");
        } else {
            Log.e("ResList", "Portrait");
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnBack) {
            finish();
        } else if (viewId == R.id.option1) {
            sendCode(0);
        } else if (viewId == R.id.option2) {
            sendCode(1);
        } else if (viewId == R.id.option3) {
            sendCode(2);
        } else if (viewId == R.id.option4) {
            sendCode(3);
        } else if (viewId == R.id.option5) {
            sendCode(rectIndex);
        } else if(viewId == R.id.btnScanQRCode) {
            startActivity(new Intent(mContext, ZAUQRCodeActivity.class));
            hideMenuPanel();
        } else if(viewId == R.id.btnEnterSetupKey) {

            hideMenuPanel();

            View dialogView = getLayoutInflater().inflate(R.layout.dialog_authactions, null);

            final android.app.AlertDialog authActDlg = new android.app.AlertDialog.Builder(mContext)
                    .setView(dialogView)
                    .setCancelable(false)
                    .create();

            dialogView.findViewById(R.id.btnHaveNewEmail).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    authActDlg.dismiss();

                    startActivity(new Intent(mContext, RegisterEmailActivity.class));
                    finish();
                }
            });

            dialogView.findViewById(R.id.btnHaveNewPhone).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    authActDlg.dismiss();

                    startActivity(new Intent(mContext, ZAUEnterKeyActivity.class));
                    hideMenuPanel();
                }
            });

            dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    authActDlg.cancel();

                    finish();
                }
            });

            authActDlg.show();
            authActDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        }
    }

    private void sendCode(int index) {
        if (index >=4) index = 3;

        // Select Code
        String code1 = arrayListCode1.get(index).getText().toString();
        String code2 = arrayListCode2.get(index).getText().toString();
        String code = code1 + code2;

        /*String za = "hello";
        if (index == 2) {
            za = "";
        }*/

        if (index == rectIndex) { // match code
            Intent intent = new Intent();
            intent.putExtra("code", appSettings.getDeviceId());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            // ActivityLogin.isLoginWithBio = false;
            showToastMessage("Incorrect code.  Please try again.");
            finish();
        }
    }

    private void selectCode(String code) {
        Intent intent = new Intent();
        intent.putExtra("code", code);
        setResult(RESULT_OK, intent);
        finish();
    }

    private long calcZA(int ver, int hostMLID) {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(mContext.getString(R.string.hack_date));
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateString = dateFormat.format(date);
        String[] hacks = dateString.split("-");

        int day = Integer.parseInt(hacks[2]);
        int hour = Integer.parseInt(hacks[3]);
        int min = Integer.parseInt(hacks[4]);
        int sec = Integer.parseInt(hacks[5]);

        long V = 0;

        V = ((long) min) * day * hour * ver + (hostMLID - 123456) + sec;

        return V;
    }
}
