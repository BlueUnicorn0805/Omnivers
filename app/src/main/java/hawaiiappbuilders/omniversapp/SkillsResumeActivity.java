package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.services.GpsTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by RahulAnsari on 23-09-2018.
 */

public class SkillsResumeActivity extends BaseActivity implements View.OnClickListener {


    private static final String TAG = SkillsResumeActivity.class.getSimpleName();
    private Button pSkills;
    private EditText resumeEditText;
    private Button registrationSubmitButton;
    private GpsTracker gpsTracker;
    private JSONObject jsonObject = new JSONObject();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills_resume);
        setToolBar();
        init();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_LOCATION);
        } else {
            getUserLocation();
        }
    }

    private void init() {
        this.pSkills = (Button) findViewById(R.id.pSkills);
        this.resumeEditText = (EditText) findViewById(R.id.resume);
        this.registrationSubmitButton = (Button) findViewById(R.id.registration_submit);
        this.registrationSubmitButton.setOnClickListener(SkillsResumeActivity.this);
        this.pSkills.setOnClickListener(SkillsResumeActivity.this);
    }


    private void setToolBar() {
    }

    private String longitude = "";
    private String latitude = "";

    private void getUserLocation() {
        if (latitude.isEmpty() && longitude.isEmpty()) {
            gpsTracker = new GpsTracker(this);
            if (gpsTracker.canGetLocation()) {
                latitude = String.valueOf(gpsTracker.getLatitude());
                longitude = String.valueOf(gpsTracker.getLongitude());
            } else {
                gpsTracker.showSettingsAlert();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation();
                } else {
                    showMessage(SkillsResumeActivity.this, "You need to grant permission");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private ArrayList<String> selectedItems = new ArrayList<>();
    private ArrayList<String> selectedIds = new ArrayList<>();
    private String selectedJobs = "";
    private String selectedJobsIds = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1050 && data != null) {
            selectedItems = data.getStringArrayListExtra("SELECTED_ITEMS");
            selectedIds = data.getStringArrayListExtra("SELECTED_IDS");
            selectedJobs = selectedItems.toString().replace("[", "").replace("]", "");
            selectedJobsIds = selectedIds.toString().replace("[", "").replace("]", "");
            Log.e(TAG, "onActivityResult: SIDs = " + selectedJobs);
            if (selectedJobs.trim().isEmpty()) {
                selectedJobs = "Select maximum of 5";
            }
            pSkills.setText(selectedJobs);
        }
    }

    private boolean getData() {
        boolean boolResume = getSetValue(resumeEditText, "Enter resume", "resume");
//        boolean boolSkills = getSetValue(pSkills,"Enter Skills","skills");
        boolean boolSkills = false;
        if (!selectedJobsIds.isEmpty()) {
            selectedJobsIds = selectedJobsIds.trim();
            try {
                jsonObject.put("skills", selectedJobsIds);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            boolSkills = true;
        } else {
            boolSkills = false;
        }
        return (boolSkills
                && boolResume) ? true : false;
    }

    private boolean getSetValue(EditText editText, String errorMessage, String jsonKey) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.requestFocus();
            editText.findFocus();
            setError(editText, errorMessage);
            return false;
        } else {
            String value = editText.getText().toString().trim();
            try {
                jsonObject.put(jsonKey, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            editText.setError(null);
            return true;
        }
    }

    private void setError(EditText editText, String textMessage) {
        editText.setError(textMessage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registration_submit:
                if (getData()) {
                    hideKeyboard();
                    if (isOnline(SkillsResumeActivity.this)) {
                        /*showProgressDlg(SkillsResumeActivity.this, "");
                        addSkills(SkillsResumeActivity.this, latitude, longitude,
                                new HttpInterface() {
                                    @Override
                                    public void onSuccess(String message) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(message);
                                            if (jsonObject.getBoolean("status")) {
                                                finish();
                                            } else {
                                                Toast.makeText(SkillsResumeActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });*/
                    }
                }
                break;
            case R.id.pSkills:
                Intent intent = new Intent(SkillsResumeActivity.this, SelectJobTypeActivity.class);
                intent.putStringArrayListExtra("SELECTED_IDS", selectedIds);
                startActivityForResult(intent, 1050);
                break;
        }
    }
}