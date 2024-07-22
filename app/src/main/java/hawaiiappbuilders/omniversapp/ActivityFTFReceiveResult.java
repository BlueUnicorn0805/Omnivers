package hawaiiappbuilders.omniversapp;

import static hawaiiappbuilders.omniversapp.appointment.ApptUtil.NEW_APPOINTMENT;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.adapters.QRInfoAdapter;
import hawaiiappbuilders.omniversapp.fragment.SelectTimeDailog;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.VolleySingleton;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.QRCodeItem;
import hawaiiappbuilders.omniversapp.services.GpsTracker;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class ActivityFTFReceiveResult extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityFTFReceiveResult.class.getSimpleName();
    private RecyclerView ftf_info;
    QRInfoAdapter qrInfoAdapter;

    private EditText ftfAmount;

    private CheckBox ftfAddFriend;
    private CheckBox ftfAddAmount;
    private CheckBox ftfAddLocation;
    private CheckBox ftfCallLater;
    private CheckBox ftfMakeAppointment;
    private CheckBox ftfSendMail;
    private CheckBox ftfSendMessage;

    private CheckBox[] checkBoxes;

    private GpsTracker gpsTracker;

    JSONObject scanDataObject;
    private JSONObject mJsonObject;
    DataUtil dataUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataUtil = new DataUtil(this, ActivityFTFReceiveResult.class.getSimpleName());

        setContentView(R.layout.activity_ftf_receive_result);

        mJsonObject = new JSONObject();

        try {
            mJsonObject.put("friendid", "");
            mJsonObject.put("addnew", "");
            mJsonObject.put("amt", "");
            mJsonObject.put("saveloc", "");
            mJsonObject.put("calllater", "");
            mJsonObject.put("makeappt", "");
            mJsonObject.put("sendemail", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            setUserLocation();
        }


        ftf_info = (RecyclerView) findViewById(R.id.ftf_info);

        ftfAmount = (EditText) findViewById(R.id.ftf_amt);

        ftfAmount.setText("2");

        ftfAddFriend = (CheckBox) findViewById(R.id.add_friend);
        ftfAddAmount = (CheckBox) findViewById(R.id.add_amt);
        ftfAddLocation = (CheckBox) findViewById(R.id.add_loc);
        ftfCallLater = (CheckBox) findViewById(R.id.call_later);
        ftfMakeAppointment = (CheckBox) findViewById(R.id.mk_apt);
        ftfSendMail = (CheckBox) findViewById(R.id.send_mail);
        ftfSendMessage = (CheckBox) findViewById(R.id.send_message);

        String scannedData = getIntent().getStringExtra("SCANNED_DATA");

        Log.e("ftf", "scanned data -> " + scannedData);

        checkBoxes = new CheckBox[]{ftfAddFriend, ftfAddAmount, ftfAddLocation, ftfCallLater, ftfMakeAppointment, ftfSendMail, ftfSendMessage};
        scannedData = scannedData.toLowerCase();
        if (scannedData.toLowerCase().startsWith("https://omnivers.info")
                || scannedData.toLowerCase().startsWith("https://www.omnivers.info")
                || scannedData.toLowerCase().startsWith("http://omnivers.info")
                || scannedData.toLowerCase().startsWith("http://www.omnivers.info")) {
            UrlQuerySanitizer urlQuery = new UrlQuerySanitizer(scannedData);

            String mlid = "";
            if (scannedData.contains("mlid")) {
                mlid = urlQuery.getValue("mlid");
            }

            String mode = "7"; // urlQuery.getValue("m");
            getInformationFromServer(mlid, mode, scannedData);
        } else {
            Object json = null;
            try {
                json = new JSONTokener(scannedData).nextValue();
                if (json instanceof JSONObject) {
                    showReceivedInformation((JSONObject) json);
                } else if (json instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) json;
                    showReceivedInformation(jsonArray.getJSONObject(0));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        checkChangedListener(ftfAddFriend, "addnew");
        checkChangedListener(ftfAddLocation, "saveloc");
        checkChangedListener(ftfCallLater, "calllater");
        checkChangedListener(ftfMakeAppointment, "makeappt");
        checkChangedListener(ftfSendMail, "sendemail");
        checkChangedListener(ftfSendMessage, "sendsms");

        checkChangedListener();

        Button ftfContinue = (Button) findViewById(R.id.ftf_continue);
        Button ftfCancel = (Button) findViewById(R.id.ftf_cancel);

        ftfContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (ftfAddFriend.isChecked()) {
                    doContinue();
                } else if (ftfSendMail.isChecked()) {
                    // Send Mail
                    String[] supportTeamAddrs = {scanDataObject.optString("email")};
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                    if (intent == null) {
                        intent = new Intent(Intent.ACTION_SENDTO);
                    }
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, supportTeamAddrs);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Mahalo Pay");
                    intent.putExtra(Intent.EXTRA_TEXT, "Regarding the Book.\n\n\n\n\nPowered by Mahalo Pay");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        showToastMessage("Please install Email App to use function");
                    }
                } else if (ftfSendMessage.isChecked()) {
                    // Send Message
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", scanDataObject.optString("ph"), null));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else if (ftfCallLater.isChecked()) {
                    // Call Phone
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + scanDataObject.optString("ph")));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else if (ftfMakeAppointment.isChecked()) {
                    String scannedData = getIntent().getStringExtra("SCANNED_DATA");
                    UrlQuerySanitizer urlQuery = new UrlQuerySanitizer(scannedData);
                    String fn = urlQuery.getValue("fn");
                    new SelectTimeDailog(ActivityFTFReceiveResult.this, fn, "", new SelectTimeDailog.TimeSelectListener() {
                        @Override
                        public void onTimeSelected(String datetime, String eventTitle) {

                            if (getLocation()) {
                                JSONObject jsonObject = new JSONObject();


                                Date start = DateUtil.parseDataFromFormat36(datetime);



                /*jsonObject.put("P1", stringToInf(BaseFunctions.getHackValue(mContext)));
                jsonObject.put("R1", stringToInf(BaseFunctions.R1(mContext)));
                jsonObject.put("R2", BaseFunctions.R2());
                jsonObject.put("D1", stringToInf(BaseFunctions.D1(mContext)));
                jsonObject.put("H1", stringToInf(BaseFunctions.H1(mContext)));*/

                                // Log.e("setAppt", jsonObject.toString());

                                int mlid = 0;
                                if (!scanDataObject.optString("mlid").isEmpty()) {
                                    mlid = Integer.parseInt(scanDataObject.optString("mlid"));
                                }

                                Calendar calendar = Calendar.getInstance();


                                try {
                                    jsonObject.put("promoid", 0);
                                    jsonObject.put("industryID", 0);
                                    jsonObject.put("attendeeMLID", mlid);
                                    // jsonObject.put("workID", appSettings.getWorkid());
                                    jsonObject.put("LDBID", 0); // TODO:  Get ldbid value
                                    jsonObject.put("meetingID", 0);
                                    jsonObject.put("sellerID", 0);
                                    jsonObject.put("orderID", 0);
                                    //jsonObject.put("empID", Integer.parseInt(appSettings.getEmpId()));
                                    jsonObject.put("mode", 0);
                                    jsonObject.put("amt", 0);
                                    jsonObject.put("TZ", 0); // PTZ, MTZ, CTZ, ETZ // TODO: Set correct value for timezone
                                    jsonObject.put("apptTime", DateUtil.toStringFormat_12(start)); // TODO: calStartTime
                                    jsonObject.put("apptTimeEnd", DateUtil.toStringFormat_12(start)); // TODO: calEndTime
                                    jsonObject.put("eventTitle", eventTitle);
                                    jsonObject.put("address", ""); // TODO: Set value later
                                    jsonObject.put("apptLon", 0);
                                    jsonObject.put("apptLat", 0);
                                    jsonObject.put("cp", scanDataObject.optString("ph"));
                                    jsonObject.put("email", scanDataObject.optString("email"));
                                    jsonObject.put("mins", 0);
                                    jsonObject.put("videoMeetingURL", ""); // TODO: Add zoomMeetingURL
                                    jsonObject.put("videoMeetingID", "");
                                    jsonObject.put("videoPasscode", "");
                                    jsonObject.put("videoAutoPhoneDial", "");
                                    jsonObject.put("miscUN", "");
                                    jsonObject.put("miscPW", "");
                                    //jsonObject.put("utc", appSettings.getUTC());
                                    jsonObject.put("qty", 0);
                                    jsonObject.put("editApptID", 0);
                                    jsonObject.put("newStatusID", NEW_APPOINTMENT);
                                    jsonObject.put("share", "");
                                    String company;
                                    if (appSettings.getCompany().isEmpty() && appSettings.getCompany() == null) {
                                        company = "-";
                                    } else {
                                        company = appSettings.getCompany();
                                    }

                                    String name = appSettings.getFN() + " " + appSettings.getLN();
                                    String senderName;
                                    if (company.contentEquals("-")) {
                                        senderName = name;
                                    } else {
                                        senderName = company + "\n" + name;
                                    }

                                    jsonObject.put("senderName", senderName);


                                    String baseUrl = BaseFunctions.getBaseData(jsonObject, getApplicationContext(),
                                            "setAppt", BaseFunctions.MAIN_FOLDER, getUserLat(), getUserLon(), mMyApp.getAndroidId());

                                    showProgressDialog();

                                    GoogleCertProvider.install(mContext);

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            hideProgressDialog();
                                            Log.e("setAppt", response);
                                            try {

                                                JSONArray responseArray = new JSONArray(response);

                                                JSONObject responseObject = responseArray.getJSONObject(0);
                                                if (responseObject.has("status") && !responseObject.getBoolean("status")) {
                                                    showAlert(responseObject.getString("msg"));
                                                } else {
                                                    long newApptId = jsonObject.optLong("NewApptID");
                                                    if (newApptId == 0) {
                                                        newApptId = (int) (System.currentTimeMillis() / 1000);
                                                    }
                                                    // TODO: Add Alarm
                                                    // addAlarm(newApptId);

                                                    // setResult(RESULT_OK, intent);
                                                    finish();

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            networkErrorHandle(mContext, error);
                                            hideProgressDialog();
                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            return new HashMap<>();
                                        }
                                    };
                                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                            25000,
                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    stringRequest.setShouldCache(false);
                                    VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
                                } catch (JSONException e) {
                                    dataUtil.setActivityName(ActivityFTFReceiveResult.class.getSimpleName());
                                    dataUtil.zzzLogIt(e, "setAppt");
                                    throw new RuntimeException(e);
                                }

                            }
                        }
                    }).show();
                } else {
                    if (checkValidity()) {
                        /*showProgressDlg(ActivityFTFReceiveResult.this, "");
                        F2FRec(ActivityFTFReceiveResult.this, mJsonObject, new HttpInterface() {
                            @Override
                            public void onSuccess(String message) {
                                hideProgressDlg();
                                try {
                                    JSONObject jsonObject = new JSONObject(message);
                                    if (jsonObject.getBoolean("status")) {
                                        if (mJsonObject.has("makeappt")
                                                && mJsonObject.getString("makeappt").trim().equalsIgnoreCase("1")) {


                                            String scannedData = getIntent().getStringExtra("SCANNED_DATA");

                                            try {
                                                JSONObject scannedDataJsonObject = new JSONObject(scannedData);

                                                if (scannedDataJsonObject.has("fn")) {
                                                    mJsonObject.put("fn", scannedDataJsonObject.getString("fn"));
                                                    mJsonObject.put("ln", scannedDataJsonObject.getString("ln"));
                                                }

                                                if (scannedDataJsonObject.has("ph")) {
                                                    mJsonObject.put("ph", scannedDataJsonObject.getString("ph"));
                                                }

                                                if (scannedDataJsonObject.has("address")) {
                                                    mJsonObject.put("address", scannedDataJsonObject.getString("address"));
                                                }

                                                if (scannedDataJsonObject.has("email")) {
                                                    mJsonObject.put("email", scannedDataJsonObject.getString("email"));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            Intent intent = new Intent(ActivityFTFReceiveResult.this, ActivityCreateEvent.class);
                                            intent.putExtra("MAKEAPPT", mJsonObject.toString());
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            startActivity(new Intent(ActivityFTFReceiveResult.this, ActivityFTFReceiveSuccessfull.class));
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(ActivityFTFReceiveResult.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });*/

                        //Uncomment the below code to Set the message and title from the strings.xml file
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Create Order").setMessage("Ready to commit all Checked Functions?");

                        //Setting message manually and performing action on button click
                        builder.setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        createOrder();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
        });

        ftfCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                finish();
            }
        });
    }

    private void getInformationFromServer(String mlid, String mode, String url) {
        Log.e("CJL", url);

        getLocation();

        int cID = appSettings.getUserId();
        long empID = appSettings.getEmpId();
        int workID = appSettings.getWorkid();

        HashMap<String, String> params = new HashMap<>();
        String baseUrl = BaseFunctions.getBaseUrl(this,
                "CJL",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());

        String dCo = url.contains("co=1") ? "1" : "0";
        String dFN = url.contains("fn=1") ? "1" : "0";
        String dLN = url.contains("ln=1") ? "1" : "0";
        String daddress = url.contains("address=1") ? "1" : "0";
        String dCity = url.contains("city=1") ? "1" : "0";
        String dSt = url.contains("st=1") ? "1" : "0";
        String dZip = url.contains("zip=1") ? "1" : "0";
        String dLoc = url.contains("loc=1") ? "1" : "0";
        String dCP = url.contains("cp=1") ? "1" : "0";
        String dWP = url.contains("wp=1") ? "1" : "0";
        String dEmail = url.contains("email=1") ? "1" : "0";
        String dWemail = url.contains("we=1") ? "1" : "0";
        String dworkAdd = url.contains("wa=1") ? "1" : "0";
        String dWebsite = url.contains("web=1") ? "1" : "0";
        String dYoutube = url.contains("youtube=1") ? "1" : "0";
        String dFB = url.contains("fb=1") ? "1" : "0";
        String dTwitter = url.contains("twitter=1") ? "1" : "0";
        String dLinkedIn = url.contains("linkedIn=1") ? "1" : "0";
        String dPintrest = url.contains("pintrest=1") ? "1" : "0";
        String dSnapchat = url.contains("snapchat=1") ? "1" : "0";
        String dInstagram = url.contains("instagram=1") ? "1" : "0";
        String dWhatsApp = url.contains("whatsApp=1") ? "1" : "0";

        String extraParams =
                "&mode=" + mode +
                        "&cID=" + cID +
                        "&workID=" + workID +
                        "&sellerID=" + mlid +
                        "&industryID=" + "0" +
                        "&dCo=" + dCo +
                        "&dFN=" + dFN +
                        "&dLN=" + dLN +
                        "&daddress=" + daddress +
                        "&dCity=" + dCity +
                        "&dSt=" + dSt +
                        "&dZip=" + dZip +
                        "&dLoc=" + dLoc +
                        "&dCP=" + dCP +
                        "&dWP=" + dWP +
                        "&dEmail=" + dEmail +
                        "&dWemail=" + dWemail +
                        "&dworkAdd=" + dworkAdd +
                        "&dWebsite=" + dWebsite +
                        "&dYoutube=" + dYoutube +
                        "&dFB=" + dFB +
                        "&dTwitter=" + dTwitter +
                        "&dLinkedIn=" + dLinkedIn +
                        "&dPintrest=" + dPintrest +
                        "&dSnapchat=" + dSnapchat +
                        "&dInstagram=" + dInstagram +
                        "&dWhatsApp=" + dWhatsApp;
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        showProgressDialog();

        GoogleCertProvider.install(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hideProgressDialog();

                Log.e("cjl", response);

                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                            showToastMessage(jsonObject.getString("msg"));
                        } else {
                            JSONObject jsonQRContent = new JSONObject();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);

                                if (jsonObject.has("MLID")) {
                                    jsonQRContent.put("mlid", jsonObject.getString("MLID"));
                                }

                                if (jsonObject.has("Co")) {
                                    jsonQRContent.put("co", jsonObject.getString("Co"));
                                }

                                if (jsonObject.has("FN")) {
                                    jsonQRContent.put("fn", jsonObject.getString("FN"));
                                }

                                if (jsonObject.has("LN")) {
                                    jsonQRContent.put("ln", jsonObject.getString("LN"));
                                }

                                if (jsonObject.has("CP")) {
                                    jsonQRContent.put("cp", jsonObject.getString("CP"));
                                }

                                if (jsonObject.has("WP")) {
                                    jsonQRContent.put("wp", jsonObject.getString("WP"));
                                }

                                if (jsonObject.has("Email")) {
                                    jsonQRContent.put("email", jsonObject.getString("Email"));
                                }

                                if (jsonObject.has("address")) {
                                    jsonQRContent.put("address", jsonObject.getString("address"));
                                }

                                if (jsonObject.has("City")) {
                                    jsonQRContent.put("city", jsonObject.getString("City"));
                                }

                                if (jsonObject.has("STE")) {
                                    jsonQRContent.put("ste", jsonObject.getString("STE"));
                                }

                                if (jsonObject.has("St")) {
                                    jsonQRContent.put("st", jsonObject.getString("St"));
                                }

                                if (jsonObject.has("Zip")) {
                                    jsonQRContent.put("zip", jsonObject.getString("Zip"));
                                }

                                if (jsonObject.has("Lat")) {
                                    jsonQRContent.put("lat", jsonObject.getString("Lat"));
                                }

                                if (jsonObject.has("Lon")) {
                                    jsonQRContent.put("lon", jsonObject.getString("Lon"));
                                }

                                String smTitle = jsonObject.optString("SMTitle");
                                String smLink = jsonObject.optString("SMLink");

                                if (jsonObject.has("YouTube") || smTitle.equals("YouTube")) {
                                    jsonQRContent.put("youtube", jsonObject.optString("YouTube") + smLink);
                                }

                                if (jsonObject.has("FB") || smTitle.equals("FB")) {
                                    jsonQRContent.put("fb", jsonObject.optString("FB") + smLink);
                                }

                                if (jsonObject.has("Twitter") || smTitle.equals("Twitter")) {
                                    jsonQRContent.put("twitter", jsonObject.optString("Twitter") + smLink);
                                }

                                if (jsonObject.has("LinkedIn") || smTitle.equals("LinkedIn")) {
                                    jsonQRContent.put("linkedIn", jsonObject.optString("LinkedIn") + smLink);
                                }

                                if (jsonObject.has("Pintrest") || smTitle.equals("Pintrest")) {
                                    jsonQRContent.put("pintrest", jsonObject.optString("Pintrest") + smLink);
                                }

                                if (jsonObject.has("Snapchat") || smTitle.equals("Snapchat")) {
                                    jsonQRContent.put("snapchat", jsonObject.optString("Snapchat") + smLink);
                                }

                                if (jsonObject.has("Instagram") || smTitle.equals("Instagram")) {
                                    jsonQRContent.put("instagram", jsonObject.optString("Instagram") + smLink);
                                }

                                if (jsonObject.has("WhatsApp") || smTitle.equals("WhatsApp")) {
                                    jsonQRContent.put("whatsApp", jsonObject.optString("WhatsApp") + smLink);
                                }
                            }

                            showReceivedInformation(jsonQRContent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                https://getfood.azurewebsites.net/main/CJL?P1=3431&R1=-1913000035&R2=40924&D1=9&H1=182&devid=1435754&appname=OmniAndroid&utc=-4&cid=1435795&workid=0&empid=0&lon=72.8727253&lat=21.2361111&uuid=99ab5d0e960e5534&mode=701&cID=1435795&workID=0&sellerID=1435795&industryID=0&dCo=0&dFN=1&dLN=1&daddress=1&dCity=0&dSt=0&dZip=0&dLoc=0&dCP=0&dWP=0&dEmail=0&dWemail=0&dworkAdd=0&dWebsite=0&dYoutube=0&dFB=0&dTwitter=0&dLinkedIn=0&dPintrest=0&dSnapchat=0&dInstagram=0&dWhatsApp=0
                hideProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    private void showReceivedInformation(JSONObject scannedData) {
        scanDataObject = scannedData;
        try {
            StringBuilder infoBuilder = new StringBuilder();
            ArrayList<QRCodeItem> qrCodeItems = new ArrayList<>();
            mJsonObject.put("friendid", scanDataObject.getString("mlid"));
            if (scanDataObject.has("fn")) {
                String ln = "";
                ln = scanDataObject.optString("ln");

                String name = String.format("%s %s", scanDataObject.getString("fn"), ln).trim();

                qrCodeItems.add(new QRCodeItem("Name", name, false));
                infoBuilder.append("Name: ").append(name).append("\n");
            }

            if (scanDataObject.has("cp")) {
                infoBuilder.append("Cell: ").append(scanDataObject.getString("cp")).append("\n");
                qrCodeItems.add(new QRCodeItem("Cell", scanDataObject.getString("cp"), false));
            } else {
                ftfSendMessage.setVisibility(View.GONE);
                ftfCallLater.setVisibility(View.GONE);
            }

            if (scanDataObject.has("address")) {
                infoBuilder.append("Address: ").append(scanDataObject.getString("address")).append("\n");
                qrCodeItems.add(new QRCodeItem("Address", scanDataObject.getString("address"), false));
            }

            if (scanDataObject.has("email")) {
                infoBuilder.append("Email: ").append(scanDataObject.getString("email")).append("\n");
                qrCodeItems.add(new QRCodeItem("Email", scanDataObject.getString("email"), false));
            } else {
                ftfSendMail.setVisibility(View.GONE);
            }

            // Company
            if (scanDataObject.has("co")) {
                infoBuilder.append("Co: ").append(scanDataObject.getString("co")).append("\n");
                qrCodeItems.add(new QRCodeItem("Co", scanDataObject.getString("co"), false));
            }

            if (scanDataObject.has("title")) {
                infoBuilder.append("Title: ").append(scanDataObject.getString("title")).append("\n");
                qrCodeItems.add(new QRCodeItem("Title", scanDataObject.getString("title"), false));
            }

            if (scanDataObject.has("wp")) {
                infoBuilder.append("Work Phone: ").append(scanDataObject.getString("wp")).append("\n");
                qrCodeItems.add(new QRCodeItem("Work Phone", scanDataObject.getString("wp"), false));
            }

            if (scanDataObject.has("wa")) {
                infoBuilder.append("Work Address: ").append(scanDataObject.getString("wa")).append("\n");
                qrCodeItems.add(new QRCodeItem("Work Address", scanDataObject.getString("wa"), false));
            }

            if (scanDataObject.has("web")) {
                infoBuilder.append("Website: ").append(scanDataObject.getString("web")).append("\n");
                qrCodeItems.add(new QRCodeItem("Website", scanDataObject.getString("web"), false));
            }

            if (scanDataObject.has("we")) {
                infoBuilder.append("Work Email: ").append(scanDataObject.getString("we")).append("\n");
                qrCodeItems.add(new QRCodeItem("Work Email", scanDataObject.getString("we"), false));
            }

            // Social
            if (scanDataObject.has("youtube")) {
                infoBuilder.append("Youtube: ").append(scanDataObject.getString("youtube")).append("\n");
                qrCodeItems.add(new QRCodeItem("Youtube", scanDataObject.getString("youtube"), true));
            }

            if (scanDataObject.has("fb")) {
                infoBuilder.append("Facebook: ").append(scanDataObject.getString("fb")).append("\n");
                qrCodeItems.add(new QRCodeItem("Facebook", scanDataObject.getString("fb"), true));
            }

            if (scanDataObject.has("twitter")) {
                infoBuilder.append("Twitter: ").append(scanDataObject.getString("twitter")).append("\n");
                qrCodeItems.add(new QRCodeItem("Twitter", scanDataObject.getString("twitter"), true));
            }

            if (scanDataObject.has("linkedIn")) {
                infoBuilder.append("LinkedIn: ").append(scanDataObject.getString("linkedIn")).append("\n");
                qrCodeItems.add(new QRCodeItem("LinkedIn", scanDataObject.getString("linkedIn"), true));
            }

            if (scanDataObject.has("pintrest")) {
                infoBuilder.append("Pintrest: ").append(scanDataObject.getString("pintrest")).append("\n");
                qrCodeItems.add(new QRCodeItem("Pintrest", scanDataObject.getString("pintrest"), true));
            }

            if (scanDataObject.has("snapchat")) {
                infoBuilder.append("Snapchat: ").append(scanDataObject.getString("snapchat")).append("\n");
                qrCodeItems.add(new QRCodeItem("Snapchat", scanDataObject.getString("snapchat"), true));
            }

            if (scanDataObject.has("instagram")) {
                infoBuilder.append("Instagram: ").append(scanDataObject.getString("instagram")).append("\n");
                qrCodeItems.add(new QRCodeItem("Instagram", scanDataObject.getString("instagram"), true));
            }

            if (scanDataObject.has("whatsApp")) {
                infoBuilder.append("WhatsApp: ").append(scanDataObject.getString("whatsApp")).append("\n");
                qrCodeItems.add(new QRCodeItem("WhatsApp", scanDataObject.getString("whatsApp"), true));
            }

            // Show it
            // ftf_info.setText(infoBuilder.toString());
            // todo: InvalidSetHasFixedSize
            // ftf_info.setHasFixedSize(true);
            ftf_info.setLayoutManager(new LinearLayoutManager(mContext));
            qrInfoAdapter = new QRInfoAdapter(mContext, qrCodeItems, new QRInfoAdapter.RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                }
            });
            ftf_info.setAdapter(qrInfoAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkChangedListener() {
        ftfAddAmount.setOnClickListener(checkButtonClickListener);
    }

    private boolean checkValidity() {
        if (ftfAddAmount.isChecked()) {
            if (ftfAmount.getText().toString().trim().isEmpty()) {
                ftfAmount.requestFocus();
                ftfAmount.setError("Please enter amount");
                return false;
            } else {
                try {
                    mJsonObject.put("amt", ftfAmount.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        } else {
            return false;
        }
    }

    private void checkChangedListener(CheckBox checkBox, final String key) {
        checkBox.setOnClickListener(checkButtonClickListener);
    }

    View.OnClickListener checkButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox checkBox = (CheckBox) v;
            for (CheckBox chkBox : checkBoxes) {
                if (chkBox != checkBox) {
                    chkBox.setChecked(false);
                }
            }
            // checkBox.setChecked(!checkBox.isChecked());
        }
    };

    private void doContinue() {
        if (scanDataObject == null) {
            showToastMessage("No Data");
            return;
        }

        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "addFriend",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&editMLID=" + scanDataObject.optString("mlid") +
                            "&email=" + scanDataObject.optString("email") +
                            "&fn=" + scanDataObject.optString("fn") +
                            "&ln=" + scanDataObject.optString("ln") +
                            "&WP=" + scanDataObject.optString("wp") +
                            "&CP=" + scanDataObject.optString("cp") +
                            "&dob=" + appSettings.getDOB() +
                            "&street_number=" + appSettings.getStreetNum() +
                            "&street=" + appSettings.getStreet() +
                            "&ste=" + "" +
                            "&city=" + appSettings.getCity() +
                            "&state=" + appSettings.getSt() +
                            "&zip=" + appSettings.getZip() +
                            "&YouTube=" + scanDataObject.optString("ut") +
                            "&FB=" + scanDataObject.optString("fb") +
                            "&Twitter=" + scanDataObject.optString("tw") +
                            "&LinkedIn=" + scanDataObject.optString("li") +
                            "&Pintrest=" + scanDataObject.optString("pi") +
                            "&Snapchat=" + scanDataObject.optString("sn") +
                            "&Instagram=" + scanDataObject.optString("ig") +
                            "&WhatsApp=" + scanDataObject.optString("wa") +
                            "&Co=" + "" +
                            "&Title=" + "" +
                            "&wEmail=" + "" +
                            "&workAdd=" + "" +
                            "&Website=" + "";
            Log.e("Request", baseUrl);
            baseUrl += extraParams;
            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            // HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("addFriend", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                                alertDialogBuilder.setTitle("Results");
                                alertDialogBuilder.setMessage("This user already exists")
                                        .setCancelable(false).setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            } else {
                                showToastMessage("Add Success");
                                MessageDataManager dm = new MessageDataManager(mContext);
                                boolean isFound = dm.findContact(scanDataObject.optInt("mlid"));
                                if (isFound) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                                    alertDialogBuilder.setTitle("Results");
                                    alertDialogBuilder.setMessage("This user already exists")
                                            .setCancelable(false).setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                } else {
                                    ContactInfo newContact = new ContactInfo();
                                    newContact.setMlid(scanDataObject.optInt("mlid"));
                                    newContact.setFname(scanDataObject.optString("fn"));
                                    newContact.setLname(scanDataObject.optString("ln"));
                                    newContact.setEmail(scanDataObject.optString("email"));
                                    dm.addContact(newContact);
                                    showToastMessage("New contact added");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showAlert(e.getMessage());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    if (TextUtils.isEmpty(error.getMessage())) {
                        showAlert(R.string.error_conn_error);
                    } else {
                        showAlert(error.getMessage());
                    }

                    //showMessage(error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            sr.setShouldCache(false);
            queue.add(sr);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_success_done:
                hideKeyboard();
                finish();
                break;
        }
    }

    private void setUserLocation() {
        if (getLocation()) {
            String lat = getUserLat();
            String lon = getUserLon();
            try {
                mJsonObject.put("lat", lat);
                mJsonObject.put("lon", lon);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void createOrder() {

        String sellerId = mJsonObject.optString("friendid");
        if (TextUtils.isEmpty(sellerId)) {
            showToastMessage("No Seller Information!");
            return;
        }

        String amt = ftfAmount.getText().toString().trim();

        if (getLocation()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("serviceusedid", 2325);
                jsonObject.put("promoid", "0");

                // Convert dateTime To Universal Format
                //jsonObject.put("OrderDueAt", DateUtil.toStringFormat_17(new Date()));  // "7-1-2019 12:01"

                String orderDueDate = "";

                Date oderDueAt = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(oderDueAt);
                calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                orderDueDate = DateUtil.toStringFormat_13(calendar.getTime());
                jsonObject.put("orderdueat", DateUtil.toStringFormat_17(calendar.getTime()));     // Aug 21 11:15 AM,
                jsonObject.put("industryID", "0");
                jsonObject.put("nickid", "70");
                jsonObject.put("totship", "0");
                jsonObject.put("totlabor", "0");

                jsonObject.put("orname", "");
                jsonObject.put("oraddr", "");
                jsonObject.put("orph", "");
                jsonObject.put("delname", "");
                jsonObject.put("deladdr", "");
                jsonObject.put("delzip", "");
                jsonObject.put("delph", "");
                jsonObject.put("deldir", "");

                jsonObject.put("sellerid", sellerId);
                jsonObject.put("buyerid", appSettings.getUserId());
                //double fTotPrice = Float.parseFloat(String.format("%.2f", totPrice));
                //double fTotTax = Float.parseFloat(String.format("%.2f", totTax));
                jsonObject.put("totprice", amt);
                jsonObject.put("tottax", 0);
                //jsonObject.put("PaidWith", "IC");
                // UTC Param
                JSONArray menuItemsArray = new JSONArray();
                JSONObject itemObj = new JSONObject();
                itemObj.put("prodid", 100);
                itemObj.put("name", "Pay Amount");
                itemObj.put("des", "Pay Amount");
                itemObj.put("price", amt);
                itemObj.put("size", "1");
                itemObj.put("quantity", 1/*item.get_quantity()*/);
                itemObj.put("oz", "0");
                itemObj.put("gram", "0");
                menuItemsArray.put(0, itemObj);
                jsonObject.put("menus", menuItemsArray);
                jsonObject.put("paynow", true);

                String baseUrl = BaseFunctions.getBaseData(jsonObject,
                        getApplicationContext(),
                        "AddOrder",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());

                Log.e("request", "request -> " + BaseFunctions.decodeBaseURL(baseUrl));

                showProgressDialog();

                RequestQueue queue = Volley.newRequestQueue(mContext);
                String finalOrderDueDate = orderDueDate;
                StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();

                        Log.e("CreateOrder", response);

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                final JSONObject jsonObject = jsonArray.getJSONObject(0);
                                if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                                    final int orderId = jsonObject.optInt("OrderID");

                                    appSettings.setOrderID(String.valueOf(orderId));
                                    appSettings.setOrderDueDate(finalOrderDueDate);

                                    // TODO: send push Just Ordered
                                    showToastMessage("Success to add order!");
                                    finish();
                                } else {
                                    final int orderId = jsonObject.optInt("OrderID");
                                    if (orderId == -2) {
                                        showAlert("Insufficient Funds");
                                    } else {
                                        showAlert("Order not entered, you may try again.");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        if (TextUtils.isEmpty(error.getMessage())) {
                            showAlert(R.string.error_conn_error);
                        } else {
                            showAlert(error.getMessage());
                        }
                    }
                });

                sr.setShouldCache(false);
                sr.setRetryPolicy(new DefaultRetryPolicy(
                        15000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                );
                queue.add(sr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


