package hawaiiappbuilders.omniversapp.depositcheck.checks;

import static hawaiiappbuilders.omniversapp.depositcheck.camera.ScanANewCheckActivity.FRONT;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;

public class ActivityCheckHistory extends BaseActivity {
    public static final String TAG = ActivityCheckHistory.class.getSimpleName();
    Context context;
    ArrayList<Check> mChecks;
    CheckAdapter adapter;
    RecyclerView rvChecks;

    TextView mEmptyList;

    Button btnScan;

    MessageDataManager dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_history);

        initViews();
        dm = new MessageDataManager(this);

        // Display list of checks from database
        mChecks.clear();
        if (dm.getCheckHistory().isEmpty()) {
            // dummy check
            Check check1 = new Check();
            check1.setTransactionId(1);
            check1.setTransactionDate("03-24-2023");
            check1.setAmount(4500.50);
            check1.setBankName("Bank of the Philippines");
            check1.setName("John Doe");
            check1.setAccountNumber("9283749");
            check1.setCheckNumber("1234567890");
            check1.setRoutingNumber("4801");
            check1.setFrontImage("");
            check1.setBackImage("");
            dm.addCheck(check1);
        }

        mChecks.addAll(dm.getCheckHistory());
        setUpRecyclerView();
        adapter.notifyDataSetChanged();
    }

    private void initViews() {
        context = this;
        mChecks = new ArrayList<>();
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btnToolbarHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });
        rvChecks = findViewById(R.id.rvCheckHistory);
        mEmptyList = findViewById(R.id.emptyList);
        btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (BuildConfig.DEBUG) {
                    Intent scanIntent = new Intent("hawaiiappbuilders.CHECK_SCAN");
                    scanIntent.putExtra("mode", FRONT);
                    startActivityForResult(scanIntent, 100);
                } else {
                    new DialogUtil(context).setTitleAndMessage("Scan a New Check", "Not setup")
                            .setPositiveButton("Okay").createDialog(new OnDialogViewListener() {
                                @Override
                                public void onPositiveClick() {

                                }

                                @Override
                                public void onNegativeClick() {

                                }

                                @Override
                                public void onNeutralClick() {

                                }
                            }).show();
                }*/
                Intent scanIntent = new Intent("hawaiiappbuilders.CHECK_SCAN");
                scanIntent.putExtra("mode", FRONT);
                startActivityForResult(scanIntent, 100);
            }
        });
    }

    private void setUpRecyclerView() {
        rvChecks.setHasFixedSize(true);
        rvChecks.setLayoutManager(new LinearLayoutManager(context));

        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(getResources().getDrawable(R.drawable.divider));

        rvChecks.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new CheckAdapter(context, mChecks, new CheckAdapter.OnClickCheckListener() {
            @Override
            public void onClickCheck(Check check) {
                // todo: pass check data
                Intent intent = new Intent(context, ActivityReviewCheck.class);
                intent.putExtra("check", check);
                intent.putExtra("mode", 1);
                startActivity(intent);
            }

            @Override
            public void onSendEmail(Check check) {
                sendEmail(check);
            }

            @Override
            public void onShowCheck(Check check) {
                showCheckImages(check);
            }
        });
        rvChecks.setAdapter(adapter);
    }

    public void showCheckImages(Check check) {
        View dialogView = getLayoutInflater().inflate(R.layout.layout_check_images, null);
        android.app.AlertDialog askDialog = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        ImageView front = dialogView.findViewById(R.id.imgFront);
        ImageView back = dialogView.findViewById(R.id.imgBack);
        ActivityReviewCheck.displayImage(this, front, Uri.fromFile(new File(check.getFrontImage())));
        ActivityReviewCheck.displayImage(this, back, Uri.fromFile(new File(check.getBackImage())));
        ImageView close = dialogView.findViewById(R.id.iClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askDialog.dismiss();
            }
        });
        askDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if(resultCode == RESULT_OK) {
                if (data != null) {
                    Check check = data.getParcelableExtra("check");
                    Intent reviewIntent = new Intent(mContext, ActivityReviewCheck.class);
                    reviewIntent.putExtra("check", check);
                    reviewIntent.putExtra("mode", 2); // from scanner
                    startActivityForResult(reviewIntent, 300);
                }
            } else {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setTitle("Check is invalid.");
                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        // next step:  review check
                        Intent scanIntent = new Intent("hawaiiappbuilders.CHECK_SCAN");
                        scanIntent.putExtra("mode", FRONT);
                        startActivityForResult(scanIntent, 100);
                    }
                });
                dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        mChecks.clear();
                        mChecks.addAll(dm.getCheckHistory());
                        adapter.notifyDataSetChanged();
                    }
                });
                dialogBuilder.setMessage(
                        "Scan again?");
                dialogBuilder.create().show();
            }

        } else if (requestCode == 300 && resultCode == RESULT_OK) {
            mChecks.clear();
            mChecks.addAll(dm.getCheckHistory());
            adapter.notifyDataSetChanged();
        }
    }

    public void sendEmail(Check check) {
        try {
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"checks@halopay.app"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Record ID:" + check.getTransactionId());
            if (Uri.parse(check.getFrontImage()) != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(check.getFrontImage()));
            }
            if (Uri.parse(check.getBackImage()) != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(check.getBackImage()));
            }
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Review Deposit Check");
            this.startActivity(Intent.createChooser(emailIntent, "Sending email..."));
        } catch (Throwable t) {
            Toast.makeText(this, "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
