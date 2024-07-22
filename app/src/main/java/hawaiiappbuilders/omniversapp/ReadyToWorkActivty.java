package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

import java.util.Arrays;
import java.util.List;

public class ReadyToWorkActivty extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_to_work);

        Button iAgree = (Button) findViewById(R.id.submit_ready_to_work);
        iAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(ReadyToWorkActivty.this)) {
                    showProgressDlg(ReadyToWorkActivty.this,"");
                   /* setReadyToWork(ReadyToWorkActivty.this, "", "",
                            new HttpInterface() {
                                @Override
                                public void onSuccess(String message) {
                                    hideProgressDlg();
                                    if (message!=null && !message.isEmpty()) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(message);
                                            if (jsonObject.getBoolean("status")) {
                                                setResult(RESULT_OK,new Intent());
                                                finish();
                                            } else {
                                                setResult(RESULT_CANCELED,new Intent());
                                                finish();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            setResult(RESULT_CANCELED,new Intent());
                                            finish();
                                        }

                                    }
                                }
                            });*/
                }
            }
        });

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setStartSpinner();
        setEndSpinner();
    }

    Spinner startSpinner;
    Spinner endSpinner;
    Button startButton;
    Button endButton;

    private void setStartSpinner() {
        startButton = (Button) findViewById(R.id.star_time_button);
        startSpinner = (Spinner) findViewById(R.id.star_time_spinner);
        final List<String> paymentMethodsString = Arrays.asList(getResources().getStringArray(R.array.time_entries));

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, R.layout.layout_spinner_payment_method_item,
                paymentMethodsString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpinner.setAdapter(adapter);

        startSpinner.setSelection(0);

        startSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startButton.setText(paymentMethodsString.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setEndSpinner() {
        endButton = (Button) findViewById(R.id.end_time_button);
        endSpinner = (Spinner) findViewById(R.id.end_time_spinner);
        final List<String> paymentMethodsString = Arrays.asList(getResources().getStringArray(R.array.time_entries));

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, R.layout.layout_spinner_payment_method_item,
                paymentMethodsString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endSpinner.setAdapter(adapter);

        endSpinner.setSelection(0);

        endSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endButton.setText(paymentMethodsString.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.star_time_button:
                startSpinner.performClick();
                break;
            case R.id.end_time_button:
                endSpinner.performClick();
                break;
        }
    }
}
