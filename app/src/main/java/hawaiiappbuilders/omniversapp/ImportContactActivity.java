package hawaiiappbuilders.omniversapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.localdb.ContactsDataSource;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class ImportContactActivity extends BaseActivity implements OnClickListener {
    public static final String TAG = ImportContactActivity.class.getSimpleName();
    private MessageDataManager dm;

    View panelStart;
    CheckBox chkStartWithNewDB;
    CheckBox chkSkipAlreadyExisted;
    CheckBox chkReviewEachItem;
    EditText edtAreaCode;
    CheckBox chkBackupMyContacts;

    View panelImport;
    EditText fCo;
    EditText fName;
    EditText lName;
    EditText eMail;
    EditText pNumber;
    private DataUtil dataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importcontact);
        dataUtil = new DataUtil(this, ImportContactActivity.class.getSimpleName());

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Import Contacts");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);

        dm = new MessageDataManager(mContext);
        ArrayList<ContactInfo> existingContacts = dm.getAlLContacts();
        for (ContactInfo item : existingContacts) {
            if (!TextUtils.isEmpty(item.getEmail()) && !isEmailValid(item.getEmail())) {
                item.setEmail("");
                dm.updateContact(item);
            }
        }

        panelStart = findViewById(R.id.panelStart);

        chkStartWithNewDB = findViewById(R.id.chkStartWithNewDB);
        chkSkipAlreadyExisted = findViewById(R.id.chkSkipAlreadyExisted);
        chkReviewEachItem = findViewById(R.id.chkReviewEachItem);
        chkBackupMyContacts = findViewById(R.id.chkBackupMyContacts);
        edtAreaCode = findViewById(R.id.edtAreaCode);

//        if (dm.getAlLContacts().size() < 75) {
//            chkStartWithNewDB.setChecked(true);
////            chkStartWithNewDB.setEnabled(false);
//        } else {
//            chkStartWithNewDB.setChecked(false);
//            chkStartWithNewDB.setVisibility(View.GONE);
//        }


        findViewById(R.id.btnStart).setOnClickListener(this);

        panelImport = findViewById(R.id.panelImport);
        panelImport.setVisibility(View.GONE);
        fCo = findViewById(R.id.fCo);
        fName = findViewById(R.id.fName);
        lName = findViewById(R.id.lName);
        eMail = findViewById(R.id.eMail);
        pNumber = findViewById(R.id.pNumber);

        findViewById(R.id.btnSkip).setOnClickListener(this);
        findViewById(R.id.btnImport).setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    static final int REQUEST_SELECT_CONTACT = 100;
    String[] permissionsForContact = new String[]{Manifest.permission.READ_CONTACTS};
    static final int PERMISSION_REQUEST_CONTACT = 500;
    ArrayList<ContactInfo> contactInfoArrayList = new ArrayList<>();

    Handler handlerLoadingContact = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0) {
                showProgressDialog();
            } else if (msg.what == 1) {
                hideProgressDialog();

                if (chkSkipAlreadyExisted.isChecked()) {
                    // Want to skip already existed contact
                    ArrayList<ContactInfo> existingContacts = dm.getContactsWithPriValue(0);
                    // Collections.sort(existingContacts, new ContactInfo());
                    contactInfoArrayList.addAll(0, existingContacts);
                } else {
                    ArrayList<ContactInfo> existingContacts = dm.getAlLContacts();
                    // Collections.sort(existingContacts, new ContactInfo());
                    contactInfoArrayList.addAll(0, existingContacts);
                }

                panelStart.setVisibility(View.GONE);
                panelImport.setVisibility(View.VISIBLE);

                if (chkReviewEachItem.isChecked()) {
                    loadNextContactInfo();
                } else {
                    for (ContactInfo contactInfo : contactInfoArrayList) {
                        contactInfo.setPri(0);
                        dm.updateContact(contactInfo);
                    }

                    showToastMessage("Import Complete");
                    finish();
                }
            }
        }
    };

    private void notifyImportContact(boolean ex) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alertDialogBuilder.setMessage("This may take some time depending on how many contacts you have")
                .setCancelable(false).
                setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).
                setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        if (ex) {
                            importContactsExDB();
                        } else {
                            importContacts();
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // import contacts from device
    private void importContacts() {

        final String areaCode = edtAreaCode.getText().toString().trim();

        if (contactInfoArrayList.isEmpty()) { // no existing contacts yet
            // ask permission for contacts, if already allowed, start importing process
            // if not allowed, device will ask permission
            if (checkContactsPermission(mContext, permissionsForContact, true, PERMISSION_REQUEST_CONTACT)) {

                handlerLoadingContact.sendEmptyMessage(0);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ContentResolver cr = getContentResolver();
                        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                                null, null, null, null);

                        if ((cur != null ? cur.getCount() : 0) > 0) {

                            ContactsDataSource contactsDataSource = new ContactsDataSource(mMyApp);
                            contactsDataSource.open();

                            int i = 0;
                            while (cur.moveToNext()) {

                                String id = cur.getString(
                                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                                String name = cur.getString(cur.getColumnIndex(
                                        ContactsContract.Contacts.DISPLAY_NAME));

                                // If Name exists, then try to get other informations
                                if (name != null && !TextUtils.isEmpty(name)) {

                                    ContactInfo newContactInfo = new ContactInfo();
                                    newContactInfo.setName(name);

                                    String phoneNumber = "";
                                    String emailAddr = "";
                                    String website = "";

                                    // Get Phone Numbers
                                    if (cur.getInt(cur.getColumnIndex(
                                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                                        Cursor phoneCur = cr.query(
                                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                null,
                                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                                new String[]{id}, null);

                                        while (phoneCur.moveToNext()) {
                                            String phoneNo = phoneCur.getString(phoneCur.getColumnIndex(
                                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                                            int type = phoneCur.getInt(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                            if (type == ContactsContract.CommonDataKinds.Phone.TYPE_WORK) {
                                                newContactInfo.setCo(name);
                                            }

                                            phoneNo = phoneNo.replaceAll("[^0-9]+", "");
                                            if (TextUtils.isEmpty(phoneNo) || phoneNo.length() < 7)
                                                continue;

                                            if (phoneNo.startsWith("1")) {
                                                phoneNo = phoneNo.substring(1);
                                            }

                                            // Add Default Area Code
                                            if (phoneNo.length() == 7) {
                                                phoneNo = areaCode + phoneNo;
                                            }

                                            String formattedNumber = phoneNo;
                                            if (phoneNo.length() == 10) {
                                                formattedNumber = String.format("(%s) %s-%s", phoneNo.substring(0, 3), phoneNo.substring(3, 6), phoneNo.substring(6));
                                            }
                                            newContactInfo.addNewPhone(formattedNumber);

                                            if (TextUtils.isEmpty(newContactInfo.getCp())) {
                                                newContactInfo.setCp(formattedNumber);
                                            }

                                            phoneNumber += "," + phoneNo;
                                        }
                                        phoneCur.close();
                                    }

                                    // Get Emails
                                    Cursor emailCur = cr.query(
                                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                            null,
                                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                            new String[]{id}, null);

                                    while (emailCur.moveToNext()) {
                                        String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                        int type = emailCur.getInt(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                                        newContactInfo.addNewEmail(email);

                                        if (TextUtils.isEmpty(newContactInfo.getEmail())) {
                                            newContactInfo.setEmail(email);
                                        }

                                        if (type == ContactsContract.CommonDataKinds.Email.TYPE_WORK) {
                                            newContactInfo.setCo(name);
                                        }

                                        emailAddr += "," + email;
                                    }
                                    emailCur.close();

                                    // Get Website
                                    String[] projWeb = new String[]{
                                            ContactsContract.CommonDataKinds.Website.URL,
                                            ContactsContract.CommonDataKinds.Website.TYPE
                                    };
                                    String selectionWeb = ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE + "'";
                                    Cursor websiteCur = cr.query(ContactsContract.Data.CONTENT_URI, projWeb, selectionWeb, null, null);
                                    while (websiteCur.moveToNext()) {
                                        String webUrl = websiteCur.getString(websiteCur.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));

                                        if (!TextUtils.isEmpty(webUrl)) {
                                            newContactInfo.setWebsite(webUrl);
                                        }

                                        website += "," + webUrl;
                                    }
                                    websiteCur.close();

                                    // Get Address
                                    Cursor addrCur = cr.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                                            null,
                                            ContactsContract.Data.CONTACT_ID + "=" + id,
                                            null,
                                            null);
                                    while (addrCur.moveToNext()) {
                                        String strt = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                                        String cty = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                                        String cntry = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                                        String zip = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                                        String addr = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));

                                        newContactInfo.setCity(cty);
                                        newContactInfo.setZip(zip);
                                        newContactInfo.setAddress(addr);
                                    }
                                    addrCur.close();

                                    String userInfo = String.format("Name: %s, Phone: %s, Email: %s", name, phoneNumber, emailAddr);
                                    Log.e("contacts", userInfo);

                                    newContactInfo.setPri(0);

                                    dm.addContact(newContactInfo);

                                    // Add to the list
                                    // contactInfoArrayList.add(newContactInfo);

                                    // Save new contact info
                                    contactsDataSource.createUserInfo(newContactInfo);
                                }

                                try {
                                    Thread.sleep(5);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            contactsDataSource.close();
                        }

                        if (cur != null) {
                            cur.close();
                        }

                        handlerLoadingContact.sendEmptyMessage(1);
                    }
                }).run();
            } else {
                showToastMessage("Please allow permission to read contacts");
            }
        }
    }

    // import contacts from device with existing db
    private void importContactsExDB() {

        final String areaCode = edtAreaCode.getText().toString().trim();

        // ask permission for contacts, if already allowed, start importing process
        // if not allowed, device will ask permission
        if (checkContactsPermission(mContext, permissionsForContact, true, 501)) {

            handlerLoadingContact.sendEmptyMessage(0);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ContentResolver cr = getContentResolver();
                    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                            null, null, null, null);

                    if ((cur != null ? cur.getCount() : 0) > 0) {

                        ContactsDataSource contactsDataSource = new ContactsDataSource(mMyApp);
                        contactsDataSource.open();

                        int i = 0;

                        while (cur.moveToNext()) {

                            String id = cur.getString(
                                    cur.getColumnIndex(ContactsContract.Contacts._ID));
                            String name = cur.getString(cur.getColumnIndex(
                                    ContactsContract.Contacts.DISPLAY_NAME));

                            // If Name exists, then try to get other informations
                            if (name != null && !TextUtils.isEmpty(name)) {

                                ContactInfo newContactInfo = new ContactInfo();
                                newContactInfo.setName(name);

                                String phoneNumber = "";
                                String emailAddr = "";
                                String website = "";

                                // Get Phone Numbers
                                if (cur.getInt(cur.getColumnIndex(
                                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                                    Cursor phoneCur = cr.query(
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                            null,
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                            new String[]{id}, null);

                                    while (phoneCur.moveToNext()) {
                                        String phoneNo = phoneCur.getString(phoneCur.getColumnIndex(
                                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        int type = phoneCur.getInt(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                        if (type == ContactsContract.CommonDataKinds.Phone.TYPE_WORK) {
                                            newContactInfo.setCo(name);
                                        }

                                        phoneNo = phoneNo.replaceAll("[^0-9]+", "");
                                        if (TextUtils.isEmpty(phoneNo) || phoneNo.length() < 7)
                                            continue;

                                        if (phoneNo.startsWith("1")) {
                                            phoneNo = phoneNo.substring(1);
                                        }

                                        // Add Default Area Code
                                        if (phoneNo.length() == 7) {
                                            phoneNo = areaCode + phoneNo;
                                        }

                                        String formattedNumber = phoneNo;
                                        if (phoneNo.length() == 10) {
                                            formattedNumber = String.format("(%s) %s-%s", phoneNo.substring(0, 3), phoneNo.substring(3, 6), phoneNo.substring(6));
                                        }
                                        newContactInfo.addNewPhone(formattedNumber);

                                        if (TextUtils.isEmpty(newContactInfo.getCp())) {
                                            newContactInfo.setCp(formattedNumber);
                                        }

                                        phoneNumber += "," + phoneNo;
                                    }
                                    phoneCur.close();
                                }

                                // Get Emails
                                Cursor emailCur = cr.query(
                                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                        new String[]{id}, null);

                                while (emailCur.moveToNext()) {
                                    String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                    int type = emailCur.getInt(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                                    newContactInfo.addNewEmail(email);

                                    if (TextUtils.isEmpty(newContactInfo.getEmail())) {
                                        newContactInfo.setEmail(email);
                                    }

                                    if (type == ContactsContract.CommonDataKinds.Email.TYPE_WORK) {
                                        newContactInfo.setCo(name);
                                    }

                                    emailAddr += "," + email;
                                }
                                emailCur.close();

                                // Get Website
                                String[] projWeb = new String[]{
                                        ContactsContract.CommonDataKinds.Website.URL,
                                        ContactsContract.CommonDataKinds.Website.TYPE
                                };
                                String selectionWeb = ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE + "'";
                                Cursor websiteCur = cr.query(ContactsContract.Data.CONTENT_URI, projWeb, selectionWeb, null, null);
                                while (websiteCur.moveToNext()) {
                                    String webUrl = websiteCur.getString(websiteCur.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));

                                    if (!TextUtils.isEmpty(webUrl)) {
                                        newContactInfo.setWebsite(webUrl);
                                    }

                                    website += "," + webUrl;
                                }
                                websiteCur.close();

                                // Get Address
                                Cursor addrCur = cr.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                                        null,
                                        ContactsContract.Data.CONTACT_ID + "=" + id,
                                        null,
                                        null);

                                while (addrCur.moveToNext()) {
                                    String strt = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                                    String cty = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                                    String cntry = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                                    String zip = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                                    String addr = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));

                                    newContactInfo.setCity(cty);
                                    newContactInfo.setZip(zip);
                                    newContactInfo.setAddress(addr);
                                }
                                addrCur.close();

                                String userInfo = String.format("Name: %s, Phone: %s, Email: %s", name, phoneNumber, emailAddr);
                                Log.e("contacts", userInfo);

                                newContactInfo.setPri(0);

//                                if(response.email == LDB.email) skip
//                                if(response.cp == LDB.cp) skip
//                                if(response.wp == LDB.wp) skip

                                for (ContactInfo contactInfo : contactInfoArrayList) {
                                    if (!contactInfo.getEmail().equalsIgnoreCase(newContactInfo.getEmail())
                                            && !contactInfo.getCp().equalsIgnoreCase(newContactInfo.getCp())
                                            && !contactInfo.getWp().equalsIgnoreCase(newContactInfo.getWp())) {
                                        dm.addContact(newContactInfo);
                                    }
                                }

                                // Add to the list
                                // contactInfoArrayList.add(newContactInfo);

                                // Save new contact info
                                contactsDataSource.createUserInfo(newContactInfo);
                            }

                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        contactsDataSource.close();
                    }

                    if (cur != null) {
                        cur.close();
                    }

                    handlerLoadingContact.sendEmptyMessage(1);
                }
            }).run();
        } else {
            showToastMessage("Please allow permission to read contacts");
        }
    }

    private void loadNextContactInfo() {
        if (!contactInfoArrayList.isEmpty()) {
            ContactInfo contactInfo = contactInfoArrayList.get(0);

            boolean isComeFromContact = contactInfo.getPri() == -2;
            boolean isAlreadyIncluded = dm.findContact(contactInfo);

            // Avoid dup item
            if (isComeFromContact && isAlreadyIncluded/*&&chkSkipAlreadyExisted.isChecked()*/) {
                contactInfoArrayList.remove(0);
                loadNextContactInfo();
                return;
            }

            fCo.setText(contactInfo.getCo());
            fName.setText(contactInfo.getFname());
            lName.setText(contactInfo.getLname());
            eMail.setText(contactInfo.getEmail());
            pNumber.setText(contactInfo.getCp());
        } else {
            findViewById(R.id.btnSkip).setEnabled(false);
            findViewById(R.id.btnImport).setEnabled(false);

            fCo.setText("");
            fName.setText("");
            lName.setText("");
            eMail.setText("");
            pNumber.setText("");

            showAlert("No more information",
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
        }
    }

    private void includeContactInfo() {
        if (!contactInfoArrayList.isEmpty()) {

            ContactInfo contactInfo = contactInfoArrayList.get(0);

            String contactDetails = String.format("Name: %s\nEmail: %s\nPhone: %s", contactInfo.getName(), contactInfo.getEmailData(), contactInfo.getPhoneData());

            contactInfo.setCo(fCo.getText().toString().trim());
            contactInfo.setFname(fName.getText().toString().trim());
            contactInfo.setLname(lName.getText().toString().trim());
            contactInfo.setEmail(eMail.getText().toString().trim());
            contactInfo.setCp(pNumber.getText().toString().trim());

            if (TextUtils.isEmpty(contactInfo.getEmail())) {
                contactInfo.setPri(0);
                contactInfo.setMlid(0);
                dm.updateContact(contactInfo);

                contactInfoArrayList.remove(0);
                loadNextContactInfo();
                return;
            }

            // Already exist contact, then skip to call api
            if (contactInfo.getMlid() != 0) {
                dm.updateContact(contactInfo);
                contactInfoArrayList.remove(0);
                loadNextContactInfo();
                return;
            }

            if (getLocation()) {

                try {
                    HashMap<String, String> params = new HashMap<>();
                    String baseUrl = BaseFunctions.getBaseUrl(this,
                            "importContacts",
                            BaseFunctions.MAIN_FOLDER,
                            getUserLat(),
                            getUserLon(),
                            mMyApp.getAndroidId());
                    String extraParams =
                            "&mode=" + "0" +
                                    "&userID=" + appSettings.getUserId() +
                                    "&email=" + contactInfo.getEmail() +
                                    "&CO=" + contactInfo.getCo() +
                                    "&FN=" + contactInfo.getFname() +
                                    "&LN=" + contactInfo.getLname() +
                                    "&Zip=" + "0" +
                                    "&LDBMLID=" + contactInfo.getMlid() +
                                    "&LDBID=" + String.valueOf(contactInfo.getId());
                    baseUrl += extraParams;
                    Log.e("Request", baseUrl);

                    showProgressDialog();
                    RequestQueue queue = Volley.newRequestQueue(mContext);

                    //HttpsTrustManager.allowAllSSL();
                    GoogleCertProvider.install(mContext);

                    String finalBaseUrl = baseUrl;
                    StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideProgressDialog();

                            Log.e("importContacts", response);

                            if (!TextUtils.isEmpty(response)) {
                                try {
                                    // Refresh Data

                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject responseStatus = jsonArray.getJSONObject(0);

                                    if (responseStatus.has("MLID")) {
                                        int fMLID = responseStatus.getInt("MLID");
                                        if (fMLID == 0) {
                                            contactInfo.setPri(0);
                                            contactInfo.setMlid(0);
                                            dm.updateContact(contactInfo);

                                            contactInfoArrayList.remove(0);
                                            loadNextContactInfo();
                                        } else {
                                            contactInfo.setPri(0);
                                            contactInfo.setMlid(fMLID);
                                            dm.updateContact(contactInfo);

                                            contactInfoArrayList.remove(0);
                                            loadNextContactInfo();
                                        }
                                    } else {
                                        contactInfoArrayList.remove(0);
                                        loadNextContactInfo();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    showAlert(e.getMessage());
                                }
                            } else {
                                contactInfoArrayList.remove(0);
                                loadNextContactInfo();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgressDialog();

                            contactInfoArrayList.remove(0);
                            loadNextContactInfo();
                            baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            return params;
                        }
                    };

                    sr.setRetryPolicy(new DefaultRetryPolicy(
                            25000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    sr.setShouldCache(false);
                    queue.add(sr);
                } catch (Exception e) {
                    if (dataUtil != null) {
                        dataUtil.setActivityName(ImportContactActivity.class.getSimpleName());
                        dataUtil.zzzLogIt(e, "importContacts");
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        if (viewID == R.id.btnBack) {
            finish();
        } else if (viewID == R.id.btnToolbarHome) {
            backToHome();
        } else if (viewID == R.id.btnStart) {
            String areaCode = edtAreaCode.getText().toString().trim();
            if (!TextUtils.isEmpty(areaCode) && areaCode.length() != 3) {
                showToastMessage("Area code should be 3 digits");
                return;
            }

            ArrayList<ContactInfo> existingContacts = dm.getAlLContacts();

//            if (chkStartWithNewDB.isChecked()) {
//                callResetApi();
//            }

            if (!chkStartWithNewDB.isChecked()) {
                notifyImportContact(true);
            } else if (existingContacts.isEmpty() || chkStartWithNewDB.isChecked()) {
                // no existing contacts from database, import new contacts from device
                notifyImportContact(false);
            } else {
                handlerLoadingContact.sendEmptyMessage(1);
            }
        } else if (viewID == R.id.btnSkip) {
            if (!contactInfoArrayList.isEmpty()) {
                ContactInfo contactInfo = contactInfoArrayList.get(0);
                contactInfo.setPri(-1);
                dm.updateContact(contactInfo);
            }

            contactInfoArrayList.remove(0);
            loadNextContactInfo();
        } else if (viewID == R.id.btnImport) {
            includeContactInfo();
        }
    }

    private void callResetApi() {
        if (getLocation()) {
            try {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "importContacts",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&mode=" + "0" +
                                "&email=" + "" +
                                "&LDBMLID=" + "-2" +
                                "&LDBID=" + "-2";
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                RequestQueue queue = Volley.newRequestQueue(mContext);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(mContext);

                String finalBaseUrl = baseUrl;
                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("importContacts0", response);

                        if (!TextUtils.isEmpty(response)) {
                            try {
                                // Refresh Data

                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject responseStatus = jsonArray.getJSONObject(0);


                            } catch (JSONException e) {
                                e.printStackTrace();
                                showAlert(e.getMessage());
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return params;
                    }
                };

                sr.setRetryPolicy(new DefaultRetryPolicy(
                        25000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                sr.setShouldCache(false);
                queue.add(sr);
            } catch (Exception e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ImportContactActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "importContacts");
                }
            }
        }
    }

    // This will be used in Android6.0(Marshmallow) or above
    public static boolean checkContactsPermission(Context context, String[] permissions, boolean showHintMessage, int requestCode) {

        if (permissions == null || permissions.length == 0)
            return true;

        boolean allPermissionSetted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionSetted = false;
                break;
            }
        }

        if (allPermissionSetted)
            return true;

        // Should we show an explanation?
        boolean shouldShowRequestPermissionRationale = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                shouldShowRequestPermissionRationale = true;
                break;
            }
        }

        if (showHintMessage && shouldShowRequestPermissionRationale) {
            // Show an expanation to the user *asynchronously* -- don't
            // block
            // this thread waiting for the user's response! After the
            // user
            // sees the explanation, try again to request the
            // permission.
            String strPermissionHint = context.getString(R.string.request_permission_hint);
            Toast.makeText(context, strPermissionHint, Toast.LENGTH_SHORT).show();
        }

        ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check All Permission was granted
        boolean bAllGranted = true;
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                bAllGranted = false;
                break;
            }
        }

        if (bAllGranted) { // when user allows contact permission
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
            alertDialogBuilder.setMessage("Are you sure you want to import your contacts?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            // start importing process
                            notifyImportContact(requestCode == 501);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

}
