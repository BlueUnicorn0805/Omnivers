package hawaiiappbuilders.omniversapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.localdb.HistoryDataSource;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.HistoryData;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

public class AboveAddressHistoryDialog extends Dialog implements View.OnClickListener {

    public AboveAddressHistoryDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public AboveAddressHistoryDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public Activity activity;
    public Dialog dialog;
    public TextView toAboveAddressBtn;
    public TextView btnHomeAddress;
    public TextView btnWorkAddress;
    RecyclerView recyclerView;
    public ContactInfo selectedContact;
    public String address;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter adapter;


    public AboveAddressHistoryDialog(Activity a, String address, ContactInfo contactInfo, RecyclerView.Adapter adapter) {
        super(a);
        this.activity = a;
        this.adapter = adapter;
        this.address = address;
        this.selectedContact = contactInfo;
        setupLayout();
    }

    private void setupLayout() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_directions_to);
        toAboveAddressBtn = findViewById(R.id.button_to_above_address);
        toAboveAddressBtn.setOnClickListener(this);

        btnHomeAddress = findViewById(R.id.button_home_address);
        btnHomeAddress.setOnClickListener(this);

        btnWorkAddress = findViewById(R.id.button_work_address);
        btnWorkAddress.setOnClickListener(this);
        recyclerView = findViewById(R.id.rv_history);
        mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        AppSettings appSettings = new AppSettings(activity);
        if (v.getId() == R.id.button_to_above_address) {
            if (address.isEmpty()) {
                Toast.makeText(activity, "No address has been entered.  Please update selected contact's address to proceed", Toast.LENGTH_LONG).show();
            } else {
                if(selectedContact != null || !address.isEmpty()) {
                    HistoryDataSource historyDataSource = new HistoryDataSource(activity);
                    HistoryData returnData = new HistoryData();
                    // Create new location data to be saved in local database
                    Calendar calendar = Calendar.getInstance();

                    // Return Address
                    returnData.setDate(DateUtil.toStringFormat_34(calendar.getTime()));
                    returnData.setTime(DateUtil.toStringFormat_10(calendar.getTime()));
                    returnData.setLat(Double.parseDouble(((BaseActivity) activity).getUserLat()));
                    returnData.setLon(Double.parseDouble(((BaseActivity) activity).getUserLon()));
                    // todo: geocode current coordinates
                    returnData.setStreetAddress("");
                    returnData.setZip("");
                    returnData.setCity("");
                    returnData.setState("");
                    returnData.setFullAddress("Return to the beginning");

                    historyDataSource.open();
                    historyDataSource.createLocationHistory(returnData);
                    historyDataSource.close();


                    HistoryData data = new HistoryData();
                    // Get date and time from selected contactInfo
                    String date = DateUtil.toStringFormat_34(calendar.getTime());
                    String time = DateUtil.toStringFormat_10(calendar.getTime());

                    if(selectedContact != null) {
                        // Destination Address of Selected Contact
                        String lat = selectedContact.getLat();
                        String lng = selectedContact.getLon();
                        String streetNumber = selectedContact.getStreetNum();
                        String zip = selectedContact.getZip();
                        String city = selectedContact.getCity();
                        String state = selectedContact.getState();

                        // Create new location data to be saved in local database
                        data.setDate(date);
                        data.setTime(time);
                        data.setLat(Double.parseDouble(lat));
                        data.setLon(Double.parseDouble(lng));
                        data.setStreetAddress(streetNumber);
                        data.setZip(zip);
                        data.setCity(city);
                        data.setState(state);
                        data.setFullAddress(address);
                        historyDataSource.open();
                        historyDataSource.createLocationHistory(data);
                        historyDataSource.close();
                    }  else {
                        // Destination Address for Entered Address when no Contact is selected
                        // todo: geocode entered address
                        data.setDate(date);
                        data.setTime(time);
                        data.setLat(0.0);
                        data.setLon(0.0);
                        data.setStreetAddress("");
                        data.setZip("");
                        data.setCity("");
                        data.setState("");
                        data.setFullAddress(address);
                        historyDataSource.open();
                        historyDataSource.createLocationHistory(data);
                        historyDataSource.close();
                    }

                    // Uri gmmIntentUri = Uri.parse(String.format("geo:%f,%f", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                    openAddress(address);
                }
            }
        } else if (v.getId() == R.id.button_home_address) {
            if(appSettings.getHomeAddress().isEmpty()) {
                askForHomeAddress();
            }
            openAddress(appSettings.getHomeAddress());
        } else if (v.getId() == R.id.button_work_address) {
            if(appSettings.getWorkAddress().isEmpty()) {
                askForWorkAddress();
            }
            openAddress(appSettings.getWorkAddress());
        }
        dismiss();
    }

    public void askForWorkAddress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AppSettings appSettings = new AppSettings(activity);
        builder.setTitle("Set Work Address");
        // Set up the input
        final EditText input = new EditText(activity);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (!appSettings.getWorkAddress().isEmpty() && appSettings.getWorkAddress() != null) {
            input.setText(appSettings.getWorkAddress());
        }


        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String workAddress = input.getText().toString();
                appSettings.setWorkAddress(workAddress);
                openAddress(appSettings.getWorkAddress());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void askForHomeAddress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AppSettings appSettings = new AppSettings(activity);
        builder.setTitle("Set Home Address");
        // Set up the input
        final EditText input = new EditText(activity);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (!appSettings.getHomeAddress().isEmpty() && appSettings.getHomeAddress() != null) {
            input.setText(appSettings.getHomeAddress());
        }


        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String homeAddress = input.getText().toString();
                appSettings.setHomeAddress(homeAddress);
                openAddress(appSettings.getHomeAddress());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void openAddress(String address) {
        if(address.isEmpty()) {
            ((BaseActivity)activity).showToastMessage("Please set an address");
        } else {
            Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%s&mode=d", address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            activity.startActivity(mapIntent);
        }
    }
}