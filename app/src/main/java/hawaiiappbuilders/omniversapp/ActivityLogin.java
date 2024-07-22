package hawaiiappbuilders.omniversapp;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.interfaces.HttpInterface;
import hawaiiappbuilders.omniversapp.meeting.utilities.Constants;
import hawaiiappbuilders.omniversapp.meeting.utilities.PreferenceManager;
import hawaiiappbuilders.omniversapp.model.User;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import info.hoang8f.android.segmented.SegmentedGroup;


public class ActivityLogin extends BaseActivity implements View.OnClickListener, HttpInterface {
    public static final String TAG = ActivityLogin.class.getSimpleName();
    private Context context;
    private EditText mUsername, mPassword;
    private Button mLoginBtn;
    private View mRegisterBtn;

    private SegmentedGroup segmentedGroup;
    private View panelLoginPassword;
    private View btnFinger;
    RadioButton btnPassword;
    RadioButton btnBioAuth;

    static boolean isLoginWithBio = false;

    private String lat, lon;

    DataUtil dataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        dataUtil = new DataUtil(this, ActivityLogin.class.getSimpleName());
        preferenceManager = new PreferenceManager(getApplicationContext());
        initViews();
        setOnClickListener();
    }

    private void initViews() {
        context = this;
        mUsername = findViewById(R.id.login_username);
        mPassword = findViewById(R.id.login_password);
        mLoginBtn = findViewById(R.id.login_btn);
        mRegisterBtn = findViewById(R.id.register_btn);

        mUsername.setText(appSettings.getLoginInput());

        if (getIntent().getBooleanExtra("LOGOUT_USER", false)) {
            logoutUser(ActivityLogin.this, lat, lon, false);
        }

        findViewById(R.id.btnZintaPay).setOnClickListener(this);

        panelLoginPassword = findViewById(R.id.panelLoginPassword);
        btnFinger = findViewById(R.id.btnFinger);
        btnPassword = findViewById(R.id.btnPassword);
        btnBioAuth = findViewById(R.id.btnBioAuth);

        btnPassword.setChecked(true);

        segmentedGroup = findViewById(R.id.segmentedAuth);
        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.btnPassword) {
                    panelLoginPassword.setVisibility(View.VISIBLE);
                    mLoginBtn.setVisibility(View.VISIBLE);

                    btnFinger.setVisibility(View.INVISIBLE);
                } else if (checkedId == R.id.btnBioAuth) {
                    panelLoginPassword.setVisibility(View.INVISIBLE);
                    mLoginBtn.setVisibility(View.INVISIBLE);

                    btnFinger.setVisibility(View.VISIBLE);
                }
            }
        });

        btnFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBioAuth();
            }
        });
    }

    private void getLocationInfo() {
        if (getLocation()) {
            lat = getUserLat();
            lon = getUserLon();
        }
    }

    private void setOnClickListener() {
        mLoginBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
        findViewById(R.id.btnForgotPwd).setOnClickListener(this);
        findViewById(R.id.viewQRMenu).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                login();
                break;
            case R.id.register_btn:
                //startActivity(new Intent(ActivityLogin.this, ActivityRegistration.class));
                //startActivity(new Intent(mContext, RegisterEmailActivity.class));
                startActivity(new Intent(mContext, SelectLanguageActivity.class));
                break;
            case R.id.btnForgotPwd:
                startActivity(new Intent(ActivityLogin.this, ForgotPwdActivity.class));
                break;
            case R.id.viewQRMenu:
                startActivity(new Intent(this, QRCodeActivity.class));
                break;
            case R.id.btnZintaPay:
                Intent hintIntent = new Intent(this, AboutZintaActivity.class);
                startActivity(hintIntent);
                overridePendingTransition(R.anim.push_top_in, R.anim.push_bottom_out);
                break;
        }
    }

    private void login() {
        if (!isOnline(context)) {
            showMessage(context, "Internet Connection Failed!");
            return;
        }

        // Go to Location Permission
        if (appSettings.getLocationPermission() != 1) {
            startActivity(new Intent(mContext, ActivityPermission.class));
            return;
        }

        if (checkLocationPermission()) {
            getLocation();
            if (validation()) {
                isLoginWithBio = false;
                startActivityForResult(new Intent(mContext, ActivityLoginAuth.class), REQUEST_AUTH);
                /*login(context, mUsername.getText().toString().trim(), mPassword.getText().toString().trim()
                        , lat, lon);*/
            }
        } else {
            startActivity(new Intent(mContext, ActivityPermission.class));
            return;
        }
    }

    private void loginWithBioAuth(String code) {
        if (!isOnline(context)) {
            showMessage(context, "Internet Connection Failed!");
            return;
        }

        // Go to Location Permission
        if (appSettings.getLocationPermission() != 1) {
            startActivity(new Intent(mContext, ActivityPermission.class));
            return;
        }

        if (checkLocationPermission()) {
            getLocation();

            lat = getUserLat();
            lon = getUserLon();

            // Don't change the password
            login(context, mUsername.getText().toString().trim(), mPassword.getText().toString().trim(), code, true);
        } else {
            startActivity(new Intent(mContext, ActivityPermission.class));
            return;
        }
    }

    private boolean validation() {
        if (TextUtils.isEmpty(mUsername.getText().toString().trim())
                || TextUtils.isEmpty(mPassword.getText().toString().trim())) {
            showMessage(context, "Please enter the username and password!");
            return false;
        }

        return true;
    }

    PreferenceManager preferenceManager;

    @Override
    public void onSuccess(String message) {

        Log.e("Login", message);

        hideProgressDlg();
        if (!TextUtils.isEmpty(message)) {
            try {
                JSONArray jsonArray = new JSONArray(message);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {

                    showMessage(context, jsonObject.getString("msg"));
                } else {

                    /*try {
                        String apiKey = CreateApiKey.createApiKey("hawaiihalopaypm");
                        if(!apiKey.isEmpty()) {
                            // TODO: Store api key in secured shared pref, to be used in app multiple times
                            showToastMessage("API Key " + apiKey);
                            appSettings.setKey1(apiKey);
                        }
                    } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
                        showToastMessage("An error occurred while retrieving API KEY");
                    }*/
                    // Save Last Login Information
                    /*appSettings.setLoginInput(mUsername.getText().toString().trim());

                    User user = new User();

                    user.setUserID(jsonObject.getInt("UserID"));
                    user.setFN(jsonObject.getString("FN"));
                    user.setLN(jsonObject.getString("LN"));
                    user.setDOB(jsonObject.getString("ESTAB"));
                    user.setPIN(jsonObject.getString("PIN"));
                  //   user.setWP(jsonObject.getString("WP"));
                    user.setCP(jsonObject.getString("CP"));
                    user.setUTC(jsonObject.getInt("cUTC"));
                    // user.setEmail(jsonObject.getString("wEmail"));
                    user.setEmail(jsonObject.getString("cEmail"));

                    user.setStreetNum(jsonObject.getString("StreetNum"));
                    user.setStreet(jsonObject.getString("Street"));
                    user.setCity(jsonObject.getString("City"));
                    user.setSt(jsonObject.getString("St"));
                    user.setZip(jsonObject.getString("Zip"));
                    user.setWorkID(jsonObject.getInt("WorkID"));

                    //user.setGender(jsonObject.getString("gender"));
                    // user.setMarital(jsonObject.getString("marital"));

                    // Additional Fields for Emp
                    // user.setEmpId(jsonObject.getInt("EmpID"));
                    //user.setDepartID(jsonObject.getString("DepartID"));
                    //user.setDepartName(jsonObject.getString("DepartName"));
                    //user.setAccessLevel(jsonObject.getString("aLev"));
                    //user.setWorkID(jsonObject.getString("WorkID"));
                    //user.setIndustryID(jsonObject.getString("IndustryID"));

                   //  appSettings.putInt("LOGIN_LIMIT", 0);
                    appSettings.setUserId(user.getUserID()); // mlid
                    appSettings.setFN(user.getFN());
                    appSettings.setLN(user.getLN());
                    appSettings.setDOB(user.getDOB());
                    appSettings.setPIN(user.getPIN());

                    // appSettings.setWP(user.getWP());
                    appSettings.setCP(user.getCP());

                    // Save UTC
                    appSettings.setUTC(user.getUTC());

                    appSettings.setEmail(user.getEmail());
                    appSettings.setStreetNum(user.getStreetNum());
                    appSettings.setStreet(user.getStreet());
                    appSettings.setCity(user.getCity());
                    appSettings.setSt(user.getSt());
                    appSettings.setZip(user.getZip());

                    // appSettings.setGendar(user.getGender());
                    // appSettings.setMarital(user.getMarital());

                    // Set EmpID = 0
                    appSettings.setEmpId(user.getEmpId());
                    // Set WorkID = 0
                    appSettings.setWorkid(user.getWorkID());

                    // STE
                 //   appSettings.setSTE(user.getSTE());

                   *//* "Store.un as wEmail, " +
                            "Store.Co, " +
                            "Store.StreetNum, " +
                            "Store.Street, " +
                            "Store.STE, " +
                            "Store.City, " +
                            "Store.St, " +
                            "Store.Zip, " +
                            "Store.utc as wUTC, " +//28
                            "Store.WP, " +
                            "Store.CP," +
                            "COALESCE(Store.DOB,'') as ESTAB, " +//  not used now

                            "Store.IndustryID, " +
                            "Store.Lon as StoreLon, " +
                            "Store.Lat as StoreLat " +
*//*

                            // Save Company information / Business
                    // appSettings.setCompany(jsonObject.optString("Co"));
                    // appSettings.setTitle(jsonObject.optString("Title"));

                    // Save Social information
                    *//*appSettings.setYoutube(jsonObject.optString("YouTube"));
                    appSettings.setFacebook(jsonObject.optString("FB"));
                    appSettings.setTwitter(jsonObject.optString("Twitter"));
                    appSettings.setLinkedIn(jsonObject.optString("LinkedIn"));
                    appSettings.setPintrest(jsonObject.optString("Pintrest"));
                    appSettings.setSnapchat(jsonObject.optString("Snapchat"));
                    appSettings.setInstagram(jsonObject.optString("Instagram"));
                    appSettings.setWhatsApp(jsonObject.optString("WhatsApp"));*//*

                    //appSettings.setDepartId(jsonObject.getString("DepartID"));
                    //appSettings.setDepartName(jsonObject.getString("DepartName"));
                    //appSettings.setALev(jsonObject.getString("aLev"));
                    //appSettings.setIndustryid(jsonObject.getString("IndustryID"));

                    // Save user credentials
                    // Gson gson = new Gson();
                    // String json = gson.toJson(user);
                    // appSettings.putString("USER_OBJECT", json);

                    //GetMyDriverID();

                    // Save Employer credentials
                    *//*appSettings.setPIN(jsonObject.getString("PIN"));
                    appSettings.setEmpId(jsonObject.getLong("EmpID"));
                    appSettings.setDepartId(jsonObject.getString("DepartID"));
                    appSettings.setDepartName(jsonObject.getString("DepartName"));
                    int manager = 500;
                    String adminPIN = "";
                    int accessLevel = Integer.parseInt(user.getAccessLevel());
                    if(accessLevel >= manager) {
                        adminPIN = jsonObject.getString("PIN");
                    }
                    appSettings.setAdminAlev(Integer.parseInt(jsonObject.getString("aLev")));
                    appSettings.setALev(jsonObject.getString("aLev"));

                    appSettings.setAdminPIN(adminPIN);

                    appSettings.setALevName(jsonObject.getString("aLevName"));

                    appSettings.setWorkid(jsonObject.getInt("WorkID"));
                    appSettings.setLoginCID(user.getUserID());
                    appSettings.setIndustryid(jsonObject.getString("IndustryID"));

                    // UTC
                    appSettings.setUTC(jsonObject.getInt("wUTC"));

                    appSettings.setStoreLat(jsonObject.optString("StoreLat"));
                    appSettings.setStoreLon(jsonObject.optString("StoreLon"));*//*

                    appSettings.setLoggedIn();*/

                    appSettings.setLoginInput(mUsername.getText().toString().trim());

                    User user = new User();

                    user.setUserID(jsonObject.getInt("UserID"));
                    user.setFN(jsonObject.getString("FN"));
                    user.setLN(jsonObject.getString("LN"));
                    String dateOfBirth = jsonObject.getString("DOB");
                    Date dobDate = DateUtil.parseDataFromFormat20(dateOfBirth);
                    user.setDOB(DateUtil.toStringFormat_13(dobDate));
                    user.setPIN(jsonObject.getString("PIN"));
                    user.setWP(jsonObject.getString("WP"));
                    user.setCP(jsonObject.getString("CP"));
                    user.setUTC(jsonObject.getString("UTC"));
                    user.setEmail(jsonObject.getString("email"));

                    user.setStreetNum(jsonObject.getString("StreetNum"));
                    user.setStreet(jsonObject.getString("Street"));
                    user.setCity(jsonObject.getString("City"));
                    user.setSt(jsonObject.getString("St"));
                    user.setZip(jsonObject.getString("Zip"));

                    user.setGender(jsonObject.getString("gender"));
                    user.setMarital(jsonObject.getString("marital"));

                    // Additional Fields for Emp
                    //user.setEmpId(jsonObject.getString("EmpID"));
                    //user.setDepartID(jsonObject.getString("DepartID"));
                    //user.setDepartName(jsonObject.getString("DepartName"));
                    //user.setAccessLevel(jsonObject.getString("aLev"));
                    //user.setWorkID(jsonObject.getString("WorkID"));
                    //user.setIndustryID(jsonObject.getString("IndustryID"));

                    appSettings.putInt("LOGIN_LIMIT", 0);
                    appSettings.setUserId(user.getUserID()); // mlid
                    appSettings.setFN(user.getFN());
                    appSettings.setLN(user.getLN());
                    appSettings.setDOB(user.getDOB());
                    appSettings.setPIN(user.getPIN());

                    appSettings.setWP(user.getWP());
                    appSettings.setCP(user.getCP());

                    // Save UTC
                    appSettings.setUTC(user.getUTC());

                    appSettings.setEmail(user.getEmail());
                    appSettings.setStreetNum(user.getStreetNum());
                    appSettings.setStreet(user.getStreet());
                    appSettings.setCity(user.getCity());
                    appSettings.setSt(user.getSt());
                    appSettings.setZip(user.getZip());

                    String homeAddr = String.format("%s %s, %s", user.getStreetNum(), user.getStreet(), user.getZip()).trim();
                    homeAddr = homeAddr.trim().replaceAll(" +", " ");
                    homeAddr = homeAddr.replace(" ,", ",");
                    appSettings.setHomeAddress(homeAddr);
                    Log.e("hAddr", homeAddr);

                    appSettings.setGendar(user.getGender());
                    appSettings.setMarital(user.getMarital());

                    // Set EmpID = 0
                    appSettings.setEmpId(user.getEmpId());
                    // Set WorkID = 0
                    appSettings.setWorkid(user.getWorkID());

                    // Save Company information
                    appSettings.setCompany(jsonObject.optString("Co"));
                    appSettings.setTitle(jsonObject.optString("Title"));

                    // Save Social information
                    appSettings.setYoutube(jsonObject.optString("YouTube"));
                    appSettings.setFacebook(jsonObject.optString("FB"));
                    appSettings.setTwitter(jsonObject.optString("Twitter"));
                    appSettings.setLinkedIn(jsonObject.optString("LinkedIn"));
                    appSettings.setPintrest(jsonObject.optString("Pintrest"));
                    appSettings.setSnapchat(jsonObject.optString("Snapchat"));
                    appSettings.setInstagram(jsonObject.optString("Instagram"));
                    appSettings.setWhatsApp(jsonObject.optString("WhatsApp"));

                    appSettings.setCountryCode(jsonObject.optString("countryCode"));
                    appSettings.setHandle(jsonObject.optString("handle"));

                    //appSettings.setDepartId(jsonObject.getString("DepartID"));
                    //appSettings.setDepartName(jsonObject.getString("DepartName"));
                    //appSettings.setALev(jsonObject.getString("aLev"));
                    //appSettings.setIndustryid(jsonObject.getString("IndustryID"));

                    // Save user credentials
                    Gson gson = new Gson();
                    String json = gson.toJson(user);
                    appSettings.putString("USER_OBJECT", json);


                    appSettings.setMedicaid(jsonObject.optString("medicaid"));
                    appSettings.setMedicare(jsonObject.optString("medicare"));

                    if (jsonObject.has("KG")) {
                        appSettings.setWeight((float) jsonObject.optDouble("KG"));
                    }

                    if (jsonObject.has("CM")) {
                        appSettings.setHeight((float) jsonObject.optDouble("CM"));
                    }

                    if (jsonObject.has("Lang")) {
                        appSettings.setLanguage(jsonObject.getString("Lang"));
                    }

                    if (jsonObject.has("Race")) {
                        appSettings.setRace(jsonObject.getString("Race"));
                    }

                    //GetMyDriverID();

                    appSettings.setLoggedIn();

                    // TODO:  Check if user already exists in firestore
                    hawaiiappbuilders.omniversapp.meeting.models.User loggedInUser = new hawaiiappbuilders.omniversapp.meeting.models.User();
                    loggedInUser.firstName = appSettings.getFN();
                    loggedInUser.lastName = appSettings.getLN();
                    loggedInUser.mlid = appSettings.getUserId();
                    AtomicBoolean isFound = new AtomicBoolean(false);

                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    database.collection(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_COLLECTION_USERS)
                            .get()
                            .addOnCompleteListener(task -> {
                                // String signedInUserId = preferenceManager.getString(hawaiiappbuilders.mahalomeeting.utilities.Constants.KEY_USER_ID);
                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        int currMlid = Integer.parseInt(String.valueOf(documentSnapshot.getLong(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_MLID)));
                                        if (currMlid == loggedInUser.mlid) {
                                            isFound.set(true);
                                            // loggedInUser.token = documentSnapshot.getString(hawaiiappbuilders.mahalomeeting.utilities.Constants.KEY_FCM_TOKEN);
                                            // updateTokenInFirestore();
                                            break;
                                        }
                                    }
                                }

                                if (isFound.get()) {
                                    // TODO:  Retrieve user data from firestore
                                    retrieveFirestoreUser(loggedInUser);
                                } else {
                                    // TODO:  User doesn't exist.  Create user data in firestore
                                    signUp(loggedInUser);
                                }
                            });

                    //startActivity(new Intent(context, ActivityHomeEvents.class));
                    startActivity(new Intent(context, ActivityHomeMenu.class));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                dataUtil.setActivityName(ActivityLogin.class.getSimpleName());
                dataUtil.zzzLogIt(e, "zzzLogin");
            }
        }
    }

    private void retrieveFirestoreUser(hawaiiappbuilders.omniversapp.meeting.models.User user) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_MLID, user.mlid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        // TODO:  Since firestore user data is cleared every time user logs out from the app, we need to update the preferenceManager again with the retrieved user data
                        preferenceManager.putString(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_FIRST_NAME, user.firstName);
                        preferenceManager.putString(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_LAST_NAME, user.lastName);
                        preferenceManager.putInt(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_MLID, user.mlid);
                        updateTokenInFirestore();

                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void signUp(hawaiiappbuilders.omniversapp.meeting.models.User user) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> users = new HashMap<>();
        users.put(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_MLID, user.mlid);
        users.put(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_FIRST_NAME, user.firstName);
        users.put(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_LAST_NAME, user.lastName);
        database.collection(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_COLLECTION_USERS)
                .add(users)
                .addOnSuccessListener(documentReference -> {
                    // TODO:  This data is newly added to firestore, we need to add this user data in preferenceManager
                    preferenceManager.putString(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_FIRST_NAME, user.firstName);
                    preferenceManager.putString(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_LAST_NAME, user.lastName);
                    preferenceManager.putInt(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_MLID, user.mlid);
                    updateTokenInFirestore();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Send fcm to firestore database
    private void updateTokenInFirestore() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // Send fcm to firestore database
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                DocumentReference documentReference =
                        database.collection(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_COLLECTION_USERS).document(
                                preferenceManager.getString(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_USER_ID)
                        );

                HashMap<String, Object> updates = new HashMap<>();
                updates.put(Constants.KEY_FCM_TOKEN, task.getResult());
                documentReference.update(updates);

                documentReference.update(updates)
                        .addOnFailureListener(e -> Toast.makeText(mContext, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationInfo();
                } else {
                    showMessage(context, "You need to grant permission");
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkLocationPermission()) {
            getLocationInfo();
        }
    }

    private void doBioAuth() {
        int authStatus = checkBiometricsAuthentication();
        if (authStatus == BiometricManager.BIOMETRIC_SUCCESS) {
            // Set the authentication

            if (TextUtils.isEmpty(mUsername.getText().toString().trim())) {
                showMessage(context, "Please enter the email to login with Birometric Auth.");
                return;
            }

            doBiometricsAuthentication();
        } else if (authStatus == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            //if(appSettings.getBiometricAuthUseStatus() == 1) {
            //    //doMainAction();
            //} else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
            alertDialogBuilder.setTitle("Please confirm");
            alertDialogBuilder.setMessage("To secure data, would you use biometrics authentication?")
                    .setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            //appSettings.setBiometricAuthStatus(0);

                            requestBiometricsSettings();
                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                            //appSettings.setBiometricAuthStatus(1);

                            //doMainAction();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            //}
        } else {
            // Go to Main Screen
            //doMainAction();
            showToastMessage("Biometrics Authentication is not supported! Please use password.");
        }
    }

    private int checkBiometricsAuthentication() {

        BiometricManager biometricManager = BiometricManager.from(this);
        int biometricsAuthStatus = biometricManager.canAuthenticate(BIOMETRIC_WEAK | DEVICE_CREDENTIAL);
        switch (biometricsAuthStatus) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                Log.e("MY_APP_TAG", "Biometric can be set in the Settings.");
                break;
        }

        return biometricsAuthStatus;
    }

    private void requestBiometricsSettings() {

        try {
            final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
            enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BIOMETRIC_WEAK | DEVICE_CREDENTIAL);
            startActivityForResult(enrollIntent, REQUEST_AUTH);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
            startActivity(intent);
        }
    }

    private void doBiometricsAuthentication() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(ActivityLogin.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                //showToastMessage("Authentication error: " + errString);

                msg("Authentication error: " + errString, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //finish();
                    }
                });
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                showToastMessage("Authentication succeeded!");

                startActivityForResult(new Intent(mContext, ActivityLoginAuth.class), REQUEST_AUTH);
                isLoginWithBio = true;
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                //showToastMessage("Authentication failed, please try again.");

                msg("Authentication failed, please try again.", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //finish();
                    }
                });
            }
        });

        // Allows user to authenticate using either a Class 3 biometric or
        // their lock screen credential (PIN, pattern, or password).
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric authentication")
                .setSubtitle("Please verify authentication to use the app data.")
                // Can't call setNegativeButtonText() and
                // setAllowedAuthenticators(...|DEVICE_CREDENTIAL) at the same time.
                //.setNegativeButtonText("Cancel")
                .setAllowedAuthenticators(BIOMETRIC_WEAK | DEVICE_CREDENTIAL)
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        biometricPrompt.authenticate(promptInfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUTH && resultCode == RESULT_OK) {
            String uuid = appSettings.getDeviceId();
            String code = data.getStringExtra("code");

            if (isLoginWithBio) {
                loginWithBioAuth(code);
            } else {
                login(context, mUsername.getText().toString().trim(), mPassword.getText().toString().trim(), code, false);
            }
        }
    }
}