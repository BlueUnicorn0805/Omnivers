package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.UpdateCashBroadcast;
import hawaiiappbuilders.omniversapp.interfaces.HttpInterface;
import hawaiiappbuilders.omniversapp.model.Biz;
import hawaiiappbuilders.omniversapp.model.JobTypes;
import hawaiiappbuilders.omniversapp.services.GpsTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by RahulAnsari on 21-09-2018.
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener, HttpInterface {

    private static final String TAG = SearchActivity.class.getSimpleName();
    private RadioButton rgGreatEmp;
    private RadioButton rgGreatJob;
    private CheckBox remoteCheckBox;
    private CheckBox locationCheckBox;
    private Button jobTypeTV;
    private TextView instaCashTitleTextView;
    private TextView instaCashTextView;

    private int empBool = 1;
    private int remote = 0;
    private int location = 0;
    private GpsTracker gpsTracker;
    boolean isLocationAvailable;
    boolean isJobTypeListFetched;
    private ArrayList<String> selectedItems = new ArrayList<>();
    private ArrayList<String> selectedIds = new ArrayList<>();
    private String selectedJobs = "";
    private String selectedJobsIds = "";
    private String instaCash = "";
    private int isReadyToWork = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_LOCATION);
        } else {
            getSearchData();
        }

        setToolBar();
        init();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void setToolBar() {
        TextView app_bar_add = (TextView) findViewById(R.id.app_bar_add);
        TextView app_bar_logout = (TextView) findViewById(R.id.app_bar_logout);
        app_bar_add.setVisibility(View.VISIBLE);
        app_bar_logout.setVisibility(View.VISIBLE);
        app_bar_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this, SkillsResumeActivity.class));
            }
        });
        app_bar_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser(SearchActivity.this, latitude, longitude, false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            logoutUser(SearchActivity.this, latitude, longitude, false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        instaCashTitleTextView = (TextView) findViewById(R.id.instaCashTitle);
        instaCashTextView = (TextView) findViewById(R.id.instaCash);
        instaCashTitleTextView.setVisibility(View.INVISIBLE);
        instaCashTextView.setVisibility(View.INVISIBLE);

        jobTypeTV = (Button) findViewById(R.id.jobTypeTV);
        jobTypeTV.setOnClickListener(this);

        rgGreatEmp = (RadioButton) findViewById(R.id.rgGreatEmp);
        rgGreatJob = (RadioButton) findViewById(R.id.rgGreatJob);
        remoteCheckBox = (CheckBox) findViewById(R.id.remote_chbx);
        locationCheckBox = (CheckBox) findViewById(R.id.location_chbx);

        rgGreatEmp.setChecked(true);

        rgGreatEmp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    empBool = 1;
                    rgGreatJob.setChecked(false);
                }
            }
        });

        rgGreatJob.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    empBool = 0;
                    rgGreatEmp.setChecked(false);
                }
            }
        });

        remoteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    remote = 1;
                } else {
                    remote = 0;
                }
            }
        });

        locationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    location = 1;
                } else {
                    location = 0;
                }
            }
        });
        locationCheckBox.setChecked(true);

        if (isLocationAvailable) {
            if (isOnline(this)) {
                /*showProgressDlg(SearchActivity.this, "");
                getJobTypes(SearchActivity.this, latitude, longitude,
                        new HttpInterface() {
                            @Override
                            public void onSuccess(String message) {
                                onGetJobTypes(message);
                            }
                        });*/
            }
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_search:
                if (isOnline(this)) {
                    // showProgressDlg(SearchActivity.this, "");
                    // getBizList(SearchActivity.this, latitude, longitude, empBool + "",
                            //location + "", remote + "", selectedJobsIds,
//                            new HttpInterface() {
//                               // @Override
//                                public void onSuccess(String message) {
//                                    onGetBizList(message);
//                                }
//                            });
                }
                break;
            case R.id.jobTypeTV:
                Intent intent = new Intent(SearchActivity.this, SelectJobTypeActivity.class);
                intent.putStringArrayListExtra("SELECTED_IDS", selectedIds);
                startActivityForResult(intent, 1050);
                break;
            case R.id.go_to_dashboard:
                Intent intentD = new Intent(SearchActivity.this, ActivityIFareDashBoard.class);
                intentD.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentD);
                finish();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1050 && data != null) {
            selectedItems = data.getStringArrayListExtra("SELECTED_ITEMS");
            selectedIds = data.getStringArrayListExtra("SELECTED_IDS");
            selectedJobs = selectedItems.toString().replace("[", "").replace("]", "");
            selectedJobsIds = selectedIds.toString().replace("[", "").replace("]", "");
            if (selectedJobs.trim().isEmpty()) {
                selectedJobs = "Select maximum of 5";
            }
            jobTypeTV.setText(selectedJobs);
        } else if (requestCode == 1030) {
            if (resultCode == RESULT_OK) {
                isReadyToWork = 1;
            }
        }
    }

    private void onGetJobTypes(String s) {
        hideProgressDlg();
        isJobTypeListFetched = true;
        if (s != null && !s.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("status")) {
                    JobTypes.clearJobList();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                        dataJsonObject.get("ID");
                        dataJsonObject.get("Title");
                        dataJsonObject.get("sortid");
                        dataJsonObject.get("remote");

                        JobTypes.addJobTypes(new JobTypes(dataJsonObject.getString("ID"),
                                dataJsonObject.getString("Title"),
                                dataJsonObject.getString("sortid"),
                                dataJsonObject.getString("remote")));
                    }

                } else {
                    Toast.makeText(SearchActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        getInstaCash(SearchActivity.this, latitude, longitude);
    }

    @Override
    public void onSuccess(String message) {
        hideProgressDlg();
        if (!TextUtils.isEmpty(message)) {
            try {
                JSONArray jsonArray = new JSONArray(message);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {

                    showMessage(mContext, jsonObject.getString("msg"));
                } else {
                    String instaCash = jsonObject.getString("instaCash");
                    String instaSaving = jsonObject.getString("instaSavings");

                    instaCashTitleTextView.setVisibility(View.VISIBLE);
                    instaCashTextView.setVisibility(View.VISIBLE);
                    instaCashTextView.setText("Balance : $ " + formatMoney(instaCash));
                    instaCashTitleTextView.setText("Balance : $ " + formatMoney(instaSaving));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showMessage(mContext, e.getMessage());
            }
        }
    }

    private void onGetBizList(String s) {
        hideProgressDlg();
        if (s != null && !s.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("status")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject dataJsonObject = jsonArray.getJSONObject(i);

                        Biz.addBizItem(new Biz(dataJsonObject.getString("Resume"),
                                dataJsonObject.getString("co"),
                                dataJsonObject.getString("FN"),
                                dataJsonObject.getString("LN"),
                                dataJsonObject.getString("Dist"),
                                dataJsonObject.getString("Skills")));
                    }
//                        startActivity(new Intent(SearchActivity.this,DetailsActivity.class));
                    Intent intent = new Intent(SearchActivity.this, DetailsActivity.class);
                    intent.putExtra("INSTA_CASH", instaCash);
                    startActivity(intent);

                } else {
                    Toast.makeText(SearchActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private String longitude = "";
    private String latitude = "";

    private void getSearchData() {
        if (latitude.isEmpty() && longitude.isEmpty()) {
            isLocationAvailable = false;
            isJobTypeListFetched = false;
            gpsTracker = new GpsTracker(this);
            isLocationAvailable = false;
            isJobTypeListFetched = false;

            if (getLocation()) {
                latitude = getUserLat();
                longitude = getUserLon();
                UpdateCashBroadcast.bc(this, latitude, longitude, instaCashTextView);
                if (isOnline(SearchActivity.this)) {
                    showProgressDlg(SearchActivity.this, "");
                    getInstaCash(SearchActivity.this, latitude, longitude);
                   /* getJobTypes(SearchActivity.this, latitude, longitude, new HttpInterface() {
                        @Override
                        public void onSuccess(String message) {
                            onGetJobTypes(message);
                        }
                    });*/
                }
            }
        } else {
            isLocationAvailable = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSearchData();
                } else {
                    showMessage(SearchActivity.this, "You need to grant permission");
                }
                break;
        }
    }
}
