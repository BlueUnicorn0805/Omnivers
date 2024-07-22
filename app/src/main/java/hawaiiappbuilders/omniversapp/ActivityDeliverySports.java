package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.Restaurant;

public class ActivityDeliverySports extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityDeliverySports.class.getSimpleName();
    Restaurant restaurant;

    EditText editReceiverName;

    EditText editSections;
    EditText editRows;
    EditText editSeat;

    TextView editDelFee;
    EditText editNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_delivery_sports);

        restaurant = getIntent().getParcelableExtra("restaurant");
        if (restaurant == null) {
            finish();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Event Delivery");
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);

        findViewById(R.id.tvHistory).setOnClickListener(this);

        editReceiverName = findViewById(R.id.editReceiverName);
        editReceiverName.setText(appSettings.getFN());

        editSections = findViewById(R.id.editSections);
        editRows = findViewById(R.id.editRow);
        editSeat = findViewById(R.id.editSeat);

        editDelFee = findViewById(R.id.editDelFee);
        editDelFee.setText(String.format("$%.2f Non Refundable", restaurant.getDelFee()));

        editNotes = findViewById(R.id.editNotes);

        findViewById(R.id.btnRequest).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);

        // Restore inputs
        try {
            JSONObject jsonDel = new JSONObject(appSettings.getDelSportsInputs());
            editReceiverName.setText(jsonDel.getString("name"));
            editSections.setText(jsonDel.getString("section"));
            editRows.setText(jsonDel.getString("row"));
            editSeat.setText(jsonDel.getString("seat"));

            editNotes.setText(jsonDel.getString("notes"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnRequest) {
            hideKeyboard(editReceiverName);

            hideKeyboard(editSections);
            hideKeyboard(editSeat);
            hideKeyboard(editRows);

            hideKeyboard(editNotes);

            String name = editReceiverName.getText().toString().trim();

            String section = editSections.getText().toString().trim();
            String row = editRows.getText().toString().trim();
            String seat = editSeat.getText().toString().trim();

            String notes = editNotes.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(section) || TextUtils.isEmpty(row) || TextUtils.isEmpty(seat)) {
                showToastMessage("Pleaes input fields");
                return;
            }

            submitCatering();
        } else if (viewId == R.id.tvHistory) {
            startActivity(new Intent(mContext, ActivityDeliveryHistory.class));
        } else if(viewId == R.id.btnCancel) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void submitCatering() {

        hideKeyboard(editReceiverName);

        hideKeyboard(editSections);
        hideKeyboard(editSeat);
        hideKeyboard(editRows);

        hideKeyboard(editNotes);

        String name = editReceiverName.getText().toString().trim();

        String section = editSections.getText().toString().trim();
        String row = editRows.getText().toString().trim();
        String seat = editSeat.getText().toString().trim();

        String notes = editNotes.getText().toString().trim();

        // Save Input
        JSONObject jsonDel = new JSONObject();
        try {
            jsonDel.put("name", name);
            jsonDel.put("section", section);
            jsonDel.put("row", row);
            jsonDel.put("seat", seat);
            jsonDel.put("notes", notes);

            appSettings.setDelSportsInputs(jsonDel.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Return the inputs
        Intent intent = getIntent();
        HashMap<String, String> paramMap = new HashMap<String, String>();

        paramMap.put("receipt", name);

        paramMap.put("section", section);
        paramMap.put("row", row);
        paramMap.put("seat", seat);

        paramMap.put("notes", notes);

        paramMap.put("tolat", String.valueOf(0));
        paramMap.put("tolon", String.valueOf(0));

        intent.putExtra("param", paramMap);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
