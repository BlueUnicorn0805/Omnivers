package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hawaiiappbuilders.omniversapp.adapters.ProjectItemAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityWorkProjects extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityWorkProjects.class.getSimpleName();

    RecyclerView rvMilestones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workprojects);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        String userName = intent.getStringExtra("name");

        TextView tvTitle = findViewById(R.id.tvTitle);
        if (!TextUtils.isEmpty(userName)) {
            tvTitle.setText(String.format("Projects for %s", userName));
        }

        Button btnCreateMilestone = findViewById(R.id.btnCreateMilestone);
        btnCreateMilestone.setOnClickListener(this);

        rvMilestones = findViewById(R.id.rvMilestones);
        rvMilestones.setLayoutManager(new GridLayoutManager(getBaseContext(), 1));
        ProjectItemAdapter projectItemAdapter = new ProjectItemAdapter(mContext, null, new ProjectItemAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
        });
        rvMilestones.setAdapter(projectItemAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnCreateMilestone) {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_input_milestone_info, null);

            final android.app.AlertDialog inputDlg = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                    .setView(dialogView)
                    .setCancelable(false)
                    .create();

            final EditText edtDesc = (EditText) dialogView.findViewById(R.id.edtDesc);
            final EditText edtAmt = (EditText) dialogView.findViewById(R.id.edtAmt);
            dialogView.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final String price = edtDesc.getText().toString().trim();
                    final String eta = edtAmt.getText().toString().trim();

                    hideKeyboard(edtDesc);
                    hideKeyboard(edtAmt);

                    if (TextUtils.isEmpty(price)) {
                        edtDesc.setError("Please input description");
                        edtDesc.requestFocus();
                        return;
                    }

                    if (TextUtils.isEmpty(eta)) {
                        edtAmt.setError("Please input amount");
                        edtAmt.requestFocus();
                        return;
                    }

                    inputDlg.dismiss();
                }
            });
            dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    inputDlg.dismiss();
                }
            });

            inputDlg.show();
            inputDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
