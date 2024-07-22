package hawaiiappbuilders.omniversapp;

import static hawaiiappbuilders.omniversapp.orders.OrderStatus.JustOrdered;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.model.CardInfo;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.OrderTypes;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class FoodCheckoutActivity extends BaseActivity {
    public static final String TAG = FoodCheckoutActivity.class.getSimpleName();
    int totItems;
    float totFoodPrice = 0;
    float totTax = 0;
    float totFees = 0;
    float delFees = 0;

    TextView txtItemCount;
    TextView txtSubTotal;
    TextView txtTaxAndFees;
    TextView txtTotal;
    TextView txtDelFees;

    int tableID = 0;

    boolean fromTab = false;

    Restaurant restaurant;
    ArrayList<MenuItem> menuItems;
    float fAvaBalance = 0;
    float fAvaSavings = 0;
    float fLoyalty = 0;
    float fGift = 0;
    float fBogo = 0;

    AppCompatActivity _activity;

    RadioButton radioPayWithAva;
    RadioButton radioPayWithLoyalty;
    RadioButton radioPayWithGiftCard;
    RadioButton radioPayWithBogo;
    RadioButton radioPayWithTab;
    EditText edtTabNum;

    RadioButton radioJoin;
    EditText edtOrderNum;

    Spinner spinnerTimeOpts;
    Spinner spinnerPickupTimeOpts;
    Spinner spinnerCurbsideTimeOpts;
    ArrayList<String> timeOptions = new ArrayList<>();
    ArrayList<String> timeValues = new ArrayList<>();

    RadioButton rbEatInTime;
    RadioButton rbReservation;
    CheckBox chkOnTime;
    RadioButton rbPickupAfter;
    RadioButton rbCurbsidePickup;

    RadioButton rbDelivery;
    RadioGroup groupDel;
    RadioButton rbDelUPX;
    RadioButton rbDelSelf;
    RadioButton rbDel3rdParty;
    float feeDelUPX = 0;
    float feeDelSelf = 0;
    float feeDel3rd = 0;

    RadioButton rbDeliverySports;
    RadioButton rbCatering;
    RadioButton rbParty;
    CompoundButton originalOption;

    ArrayList<CardInfo> pastCardInfoList;

    Button btnSubmit;

    private static final int REQUEST_RESERVETABLE = 100;
    private static final int REQUEST_DELIVERY = 101;
    private static final int REQUEST_DELIVERY_SPORTS = 102;
    private static final int REQUEST_CATERING = 103;
    private static final int REQUEST_PARTY = 104;
    private static final int REQUEST_TRANSFUNDS = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_foodcheckout);

        _activity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_checkout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent data = getIntent();

        totItems = data.getIntExtra("tot_items", 0);
        totFoodPrice = data.getFloatExtra("tot_food_price", 0);
        totTax = data.getFloatExtra("tot_tax", 0);
        totFees = data.getFloatExtra("tot_fee", 0);

        restaurant = data.getParcelableExtra("restaurant");
        menuItems = data.getParcelableArrayListExtra("menus");

        tableID = data.getIntExtra("table_id", 0);

        fromTab = data.getBooleanExtra("from_tabs", false);

        txtItemCount = findViewById(R.id.txtItemCount);
        txtSubTotal = findViewById(R.id.txtSubTotal);
        txtTaxAndFees = findViewById(R.id.txtTaxAndFees);
        txtTotal = findViewById(R.id.txtTotal);
        txtDelFees = findViewById(R.id.txtDelFees);
        // Show Item and Price information
        updatePrice();

        // Additional Type
        spinnerTimeOpts = (Spinner) findViewById(R.id.spinnerEatTimeOpts);
        spinnerPickupTimeOpts = findViewById(R.id.spinnerPickupTimeOpts);
        spinnerCurbsideTimeOpts = findViewById(R.id.spinnerCurbsideTimeOpts);

        // Apply Adapter
        timeOptions.add("ASAP");
        timeValues.add("");

        long curTimeMils = new Date().getTime();
        long milsPer30Mins = 30 * 60000;
        long milsPerHour = 60 * 60000;
        long milsPer5Mins = 5 * 60000;
        long startTime = (curTimeMils / milsPer30Mins + 1) * milsPer30Mins;
        long endTime = (curTimeMils / milsPerHour + 3) * milsPerHour;
        for (long time = startTime; time <= endTime; time += milsPer5Mins) {
            timeOptions.add(DateUtil.toStringFormat_10(new Date(time)));
            timeValues.add(DateUtil.toStringFormat_23(new Date(time)));
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        timeOptions); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);

        spinnerTimeOpts.setAdapter(spinnerArrayAdapter);
        spinnerPickupTimeOpts.setAdapter(spinnerArrayAdapter);
        spinnerCurbsideTimeOpts.setAdapter(spinnerArrayAdapter);

        // Apply Adapter
        rbEatInTime = (RadioButton) findViewById(R.id.rbEatInTime);
        rbReservation = (RadioButton) findViewById(R.id.rbReservation);
        chkOnTime = (CheckBox) findViewById(R.id.chkOnTime);
        chkOnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbReservation.isEnabled()) {
                    if (rbReservation.isChecked()) {
                        totFees = (float) restaurant.getResFee();
                        delFees = 0;
                        if (chkOnTime.isChecked()) {
                            totFees += restaurant.getTableFee();
                        }
                        updatePrice();

                        Intent deliveryIntent = new Intent(mContext, ActivityReserveTable.class);
                        //Intent deliveryIntent = new Intent(mContext, NewDeliveryActivity.class);
                        deliveryIntent.putExtra("restaurant", restaurant);
                        startActivityForResult(deliveryIntent, REQUEST_RESERVETABLE);
                    } else {
                        rbReservation.setChecked(true);
                    }
                }
            }
        });
        rbPickupAfter = (RadioButton) findViewById(R.id.rbPickupAfter);
        rbCurbsidePickup = (RadioButton) findViewById(R.id.rbCurbsidePickup);

        rbDelivery = (RadioButton) findViewById(R.id.rbDelivery);

        groupDel = (RadioGroup) findViewById(R.id.groupDel);
        rbDelUPX = (RadioButton) findViewById(R.id.rbDelUPX);
        rbDelSelf = (RadioButton) findViewById(R.id.rbDelSelf);
        rbDel3rdParty = (RadioButton) findViewById(R.id.rbDel3rdParty);
        CompoundButton.OnCheckedChangeListener delOptionChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (buttonView == rbDelUPX) {
                        delFees = feeDelUPX;
                    } else if (buttonView == rbDelSelf) {
                        delFees = feeDelSelf;
                    } else if (buttonView == rbDel3rdParty) {
                        delFees = feeDel3rd;
                    }

                    updatePrice();
                }
            }
        };
        rbDelUPX.setOnCheckedChangeListener(delOptionChangeListener);
        rbDelSelf.setOnCheckedChangeListener(delOptionChangeListener);
        rbDel3rdParty.setOnCheckedChangeListener(delOptionChangeListener);

        rbDeliverySports = (RadioButton) findViewById(R.id.rbDeliverySports);
        rbCatering = (RadioButton) findViewById(R.id.rbCatering);
        rbParty = (RadioButton) findViewById(R.id.rbParty);

        CompoundButton.OnCheckedChangeListener optionChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) return;

                if (originalOption != null) {
                    originalOption.setChecked(false);
                    originalOption = buttonView;
                } else {
                    originalOption = buttonView;
                }

                int checkedId = buttonView.getId();
                if (checkedId == R.id.rbReservation) {
                    totFees = (float) restaurant.getResFee();
                    delFees = 0;

                    Intent deliveryIntent = new Intent(mContext, ActivityReserveTable.class);
                    //Intent deliveryIntent = new Intent(mContext, NewDeliveryActivity.class);
                    deliveryIntent.putExtra("restaurant", restaurant);
                    startActivityForResult(deliveryIntent, REQUEST_RESERVETABLE);
                } else if (checkedId == R.id.rbDelivery) {
                    totFees = (float) restaurant.getDelFee();

                    delFees = 0;

                    Intent deliveryIntent = new Intent(mContext, ActivityDelivery.class);
                    //Intent deliveryIntent = new Intent(mContext, NewDeliveryActivity.class);
                    deliveryIntent.putExtra("restaurant", restaurant);
                    startActivityForResult(deliveryIntent, REQUEST_DELIVERY);
                } else if (checkedId == R.id.rbDeliverySports) {
                    totFees = (float) restaurant.getDelFee();
                    if (rbDelUPX.isChecked()) {
                        delFees = feeDelUPX;
                    } else if (rbDelSelf.isChecked()) {
                        delFees = feeDelSelf;
                    } else if (rbDel3rdParty.isChecked()) {
                        delFees = feeDel3rd;
                    }

                    Intent deliveryIntent = new Intent(mContext, ActivityDeliverySports.class);
                    //Intent deliveryIntent = new Intent(mContext, NewDeliveryActivity.class);
                    deliveryIntent.putExtra("restaurant", restaurant);
                    startActivityForResult(deliveryIntent, REQUEST_DELIVERY_SPORTS);
                } else if (checkedId == R.id.rbCatering) {
                    totFees = (float) restaurant.getCatFee();
                    delFees = 0;

                    Intent cateringIntent = new Intent(mContext, ActivityCatering.class);
                    cateringIntent.putExtra("restaurant", restaurant);
                    startActivityForResult(cateringIntent, REQUEST_CATERING);
                } else if (checkedId == R.id.rbParty) {
                    totFees = (float) restaurant.getPartyFee();
                    delFees = 0;

                    Intent partyIntent = new Intent(mContext, ActivityParty.class);
                    partyIntent.putExtra("restaurant", restaurant);
                    startActivityForResult(partyIntent, REQUEST_PARTY);
                } else {
                    totFees = 0;
                    delFees = 0;

                    resetOptionTitles();
                    spinnerTimeOpts.setVisibility(View.VISIBLE);
                }

                updatePrice();
            }
        };
        rbEatInTime.setOnCheckedChangeListener(optionChangeListener);
        rbReservation.setOnCheckedChangeListener(optionChangeListener);
        rbPickupAfter.setOnCheckedChangeListener(optionChangeListener);
        rbCurbsidePickup.setOnCheckedChangeListener(optionChangeListener);
        rbDelivery.setOnCheckedChangeListener(optionChangeListener);
        rbDeliverySports.setOnCheckedChangeListener(optionChangeListener);
        rbCatering.setOnCheckedChangeListener(optionChangeListener);
        rbParty.setOnCheckedChangeListener(optionChangeListener);

        if (restaurant.getOption() == 0) {
            if (restaurant.getOrders() > 0) {
                rbEatInTime.setChecked(true);
            } else {
                rbPickupAfter.setChecked(true);
            }
        } else if (restaurant.getOption() == 1) {
            rbCurbsidePickup.setChecked(true);
        } else if (restaurant.getOption() == 2) {
            rbDelivery.setChecked(true);
        } else if (restaurant.getOption() == 3) {
            rbCatering.setChecked(true);
        } else if (restaurant.getOption() == 4) {
            rbParty.setChecked(true);
        } else if (restaurant.getOption() == 5) {
            rbReservation.setChecked(true);
        }

        if (restaurant.getOrders() < 1) {
            rbEatInTime.setEnabled(false);

            // In case of disabled status, disable Reservation too.
            restaurant.setRes(0);
            restaurant.setOnTable(0);

            restaurant.setPu(0);
            restaurant.setCurb(0);
        }

        if (restaurant.getRes() < 1) {
            rbReservation.setEnabled(false);
            chkOnTime.setEnabled(false);
        } else {
            chkOnTime.setEnabled(restaurant.getOnTable() > 0 ? true : false);
        }

        if (restaurant.getPu() < 1) {
            rbPickupAfter.setEnabled(false);

            restaurant.setCurb(0);
        }

        if (restaurant.getParty() < 1) {
            rbParty.setEnabled(false);
        }

        if (restaurant.getCater() < 1) {
            rbCatering.setEnabled(false);
        }

        if (restaurant.getDel() < 1) {
            rbDelivery.setEnabled(false);
        }

        if (restaurant.getCurb() < 1) {
            rbCurbsidePickup.setEnabled(false);
        }

        btnSubmit = (Button) findViewById(R.id.btnClose);

        // Get past Card Info
        pastCardInfoList = getPastCardInfoList();
        updatePaymentChannels();

        btnSubmit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (radioPayWithAva.isChecked()) {
                    if (fAvaBalance < (totFoodPrice + totTax)) {
                        showAlert(R.string.msg_funds_not_enough, new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle(R.string.app_name);
                                builder.setMessage(R.string.msg_trans_funds_now);
                                builder.setCancelable(false);
                                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }

                                });
                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        // Charge Balance Here
                                        startActivityForResult(new Intent(mContext, ActivityTransIntro.class), REQUEST_TRANSFUNDS);
                                    }

                                });
                                builder.show();
                            }
                        });
                    } else {
                        confirmOrder();
                    }
                } else {
                    confirmOrder();
                }
            }
        });

        getAvaBalance();

        groupDel.setVisibility(View.GONE);
    }

    private void updatePrice() {
        txtItemCount.setText(getString(R.string.format_total_item, totItems));
        txtSubTotal.setText(getString(R.string.format_sub_total, totFoodPrice));
        txtTaxAndFees.setText(getString(R.string.format_tax_fees, totTax + totFees));
        txtDelFees.setText(String.format("Delivery Fee: $%.2f", delFees));
        txtTotal.setText(getString(R.string.format_total_price, (totFoodPrice + totTax + totFees + delFees)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void updatePaymentChannels() {

        radioPayWithAva = findViewById(R.id.rbZinta);
        radioPayWithLoyalty = findViewById(R.id.rbLoyal);
        radioPayWithGiftCard = findViewById(R.id.rbGift);
        radioPayWithBogo = findViewById(R.id.rbBogo);
        radioPayWithTab = findViewById(R.id.rbTab);
        edtTabNum = findViewById(R.id.edtTabNum);
        radioJoin = findViewById(R.id.rbJoin);
        edtOrderNum = findViewById(R.id.edtOrderNum);

        View.OnClickListener payOptionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewId = v.getId();
                if (viewId != R.id.rbZinta) {
                    radioPayWithAva.setChecked(false);
                }
                if (viewId != R.id.rbLoyal) {
                    radioPayWithLoyalty.setChecked(false);
                }
                if (viewId != R.id.rbGift) {
                    radioPayWithGiftCard.setChecked(false);
                }
                if (viewId != R.id.rbBogo) {
                    radioPayWithBogo.setChecked(false);
                }
                if (viewId != R.id.rbTab) {
                    radioPayWithTab.setChecked(false);
                }
                if (viewId != R.id.rbJoin) {
                    radioJoin.setChecked(false);
                }
            }
        };

        radioPayWithAva.setOnClickListener(payOptionClickListener);
        radioPayWithLoyalty.setOnClickListener(payOptionClickListener);
        radioPayWithGiftCard.setOnClickListener(payOptionClickListener);
        radioPayWithBogo.setOnClickListener(payOptionClickListener);
        radioPayWithTab.setOnClickListener(payOptionClickListener);
        radioJoin.setOnClickListener(payOptionClickListener);

        // Active Tab Payment
        if (fromTab) {
            radioPayWithTab.setChecked(true);
        }
    }

    private ArrayList<CardInfo> getPastCardInfoList() {

        ArrayList<CardInfo> cardInfos = new ArrayList<>();

        SharedPreferences settings = getSharedPreferences("app_setting", MODE_PRIVATE);
        String cardData = settings.getString("past_cards", "");
        if (!TextUtils.isEmpty(cardData)) {
            try {
                JSONArray jsonPastCards = new JSONArray(cardData);
                if (jsonPastCards != null && jsonPastCards.length() > 0) {

                    for (int i = 0; i < jsonPastCards.length(); i++) {
                        JSONObject jsonCardInfo = jsonPastCards.getJSONObject(i);
                        String name = jsonCardInfo.getString("name");
                        String num = jsonCardInfo.getString("num");
                        String nick = jsonCardInfo.getString("nick");
                        String mm = jsonCardInfo.getString("mm");
                        String yy = jsonCardInfo.getString("yy");
                        String cvv = jsonCardInfo.getString("cvv");
                        String post = jsonCardInfo.getString("post");

                        cardInfos.add(new CardInfo(name, num, nick, mm, yy, cvv, post));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return cardInfos;
    }

    private void putPastCardInfo(ArrayList<CardInfo> cardInfos) {
        if (cardInfos == null || cardInfos.isEmpty())
            return;

        SharedPreferences settings = getSharedPreferences("app_setting", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        JSONArray jsonPastCardInfo = new JSONArray();
        for (int i = 0; i < cardInfos.size(); i++) {
            CardInfo cardInfo = cardInfos.get(i);

            JSONObject jsonCardInfo = new JSONObject();
            try {
                jsonCardInfo.put("name", cardInfo.getCardName());
                jsonCardInfo.put("num", cardInfo.getCardNumber());
                jsonCardInfo.put("nick", cardInfo.getCardNickname());
                jsonCardInfo.put("mm", cardInfo.getCardExpMonth());
                jsonCardInfo.put("yy", cardInfo.getCardExpYear());
                jsonCardInfo.put("cvv", cardInfo.getCardCVV());
                jsonCardInfo.put("post", cardInfo.getCardPostalCode());

                jsonPastCardInfo.put(i, jsonCardInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        editor.putString("past_cards", jsonPastCardInfo.toString()).commit();
    }

    private void getAvaBalance() {
        if (getLocation()) {

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "AllBal" +
                            "&misc=" + String.valueOf(restaurant.get_id());
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(_activity);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(_activity);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("avaBal", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                showToastMessage(jsonObject.getString("msg"));
                            } else {
                                fAvaBalance = (float) jsonObject.getDouble("instaCash");
                                fAvaSavings = (float) jsonObject.getDouble("instaSavings");
                                fLoyalty = (float) jsonObject.getDouble("Loyalty");
                                fGift = (float) jsonObject.getDouble("Gift");
                                fBogo = (float) jsonObject.getDouble("BOGO");

                                //String.format("Pay with Zinta(Current Balance: $%.2f)"
                                radioPayWithAva.setText(getString(R.string.format_pay_with_zinta, fAvaBalance));
                                if (fAvaBalance < totFoodPrice + totTax) {
                                    radioPayWithAva.setEnabled(false);
                                }

                                radioPayWithLoyalty.setText(getString(R.string.format_pay_with_loyalty, fLoyalty));
                                if (fLoyalty < totFoodPrice + totTax) {
                                    radioPayWithLoyalty.setEnabled(false);
                                }

                                radioPayWithGiftCard.setText(getString(R.string.format_pay_with_gift, fGift));
                                if (fGift < totFoodPrice + totTax) {
                                    radioPayWithGiftCard.setEnabled(false);
                                }

                                radioPayWithBogo.setText(getString(R.string.format_pay_with_bogo, (int) fBogo));
                                if (fBogo < totFoodPrice + totTax) {
                                    radioPayWithBogo.setEnabled(false);
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
                    baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
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

    public void hideSoftKeyboard() {
        InputMethodManager keyboard =
                (InputMethodManager) _activity.getSystemService(INPUT_METHOD_SERVICE);
        if (_activity != null && _activity.getCurrentFocus() != null) {
            keyboard.hideSoftInputFromInputMethod(_activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void confirmOrder() {
        //Uncomment the below code to Set the message and title from the strings.xml file
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.title_confirm_purchase).setMessage(R.string.msg_confirm_purchase);

        //Setting message manually and performing action on button click
        builder.setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        createOrder();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    Date dateOrderDue = new Date();

    private boolean createOrder() {
        // Check address
        if (rbDelivery.isChecked() || rbCatering.isChecked()) {
            String address = "", toLat = "", toLon = "";
            if (orderParams != null) {
                address = orderParams.get("address");
                toLat = orderParams.get("tolat");
                toLon = orderParams.get("tolon");
            }

            if (TextUtils.isEmpty(address) || TextUtils.isEmpty(toLat) || TextUtils.isEmpty(toLon)) {
                showAlert("Address is incorrect.");
                return false;
            }
        }

        String orderDueDate = "";
        String orderDueAt = "";

        if (rbEatInTime.isChecked() || rbPickupAfter.isChecked() || rbCurbsidePickup.isChecked() || rbDeliverySports.isChecked()) {
            int timeItemPos = 0;
            if (rbEatInTime.isChecked()) {
                timeItemPos = spinnerTimeOpts.getSelectedItemPosition();
            } else if (rbPickupAfter.isChecked()) {
                timeItemPos = spinnerPickupTimeOpts.getSelectedItemPosition();
            } else if (rbCurbsidePickup.isChecked()) {
                timeItemPos = spinnerCurbsideTimeOpts.getSelectedItemPosition();
            }

            if (timeItemPos == 0) {
                // ASAP
                orderDueAt = DateUtil.toStringFormat_7(new Date());

                orderDueDate = DateUtil.toStringFormat_13(new Date());
            } else {
                orderDueAt = String.format("%s %s", DateUtil.toStringFormat_13(new Date()),
                        timeValues.get(timeItemPos));

                orderDueDate = DateUtil.toStringFormat_13(new Date());
            }
        } else {
            if (orderParams == null) {
                showToastMessage(R.string.msg_input_order_details);
                return false;
            }

            String orderTime = orderParams.get("timeValue");
            String orderDate = orderParams.get("dateValue");

            if (TextUtils.isEmpty(orderTime) || "ASAP".equals(orderTime)) {
                orderDueAt = DateUtil.toStringFormat_7(new Date());

                orderDueDate = DateUtil.toStringFormat_13(new Date());
            } else {
                orderDueAt = String.format("%s %s", orderDate, orderTime);

                orderDueDate = orderDate;
            }
        }

        dateOrderDue = DateUtil.parseDataFromFormat7(orderDueAt);

        if (getLocation()) {
            JSONObject jsonObject = new JSONObject();
            try {
                //jsonObject.put("serviceusedid", 2188);
                jsonObject.put("promoid", "0");

                jsonObject.put("orderdueat", orderDueAt);
                jsonObject.put("industryID", 123);

                if (radioPayWithAva.isChecked()) {
                    jsonObject.put("paidwithid", "70");
                    jsonObject.put("nickid", "70");
                } else if (radioPayWithLoyalty.isChecked()) {
                    jsonObject.put("paidwithid", "71");
                    jsonObject.put("nickid", "2");
                } else if (radioPayWithGiftCard.isChecked()) {
                    jsonObject.put("paidwithid", "72");
                    jsonObject.put("nickid", "3");
                } else if (radioPayWithBogo.isChecked()) {
                    jsonObject.put("paidwithid", "73");
                    jsonObject.put("nickid", "4");
                } else if (radioPayWithTab.isChecked()) {
                    jsonObject.put("paidwithid", "74");
                    jsonObject.put("nickid", "5");
                } else {
                    jsonObject.put("paidwithid", "75");
                    jsonObject.put("nickid", "6");
                }
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

                jsonObject.put("sellerid", restaurant.get_id());
                jsonObject.put("buyerid", appSettings.getUserId());

                //jsonObject.put("PaidWith", "IC");

                JSONArray menuItemsArray = new JSONArray();
                for (int i = 0; i < menuItems.size(); i++) {
                    MenuItem item = menuItems.get(i);
                    if (item.get_quantity() == 0)
                        continue;

                    JSONObject itemObj = new JSONObject();
                    itemObj.put("prodid", item.get_id());
                    itemObj.put("name", item.get_name());
                    itemObj.put("des", ""/*item.get_description()*/); // Currently make the value as blank to send more data.

                    String price = item.get_price();
                    price = price.replace("$", "");
                    float fPrice = 0f;
                    try {
                        fPrice = Float.parseFloat(price);
                    } catch (Exception e) {
                    }

                    itemObj.put("price", price);
                    itemObj.put("size", item.get_size());
                    itemObj.put("quantity", item.get_quantity());

                    itemObj.put("oz", "0");
                    itemObj.put("gram", "0");

                    menuItemsArray.put(i, itemObj);
                }

                if (rbEatInTime.isChecked()) {
                    jsonObject.put("serviceusedid", OrderTypes.EatIN);
                } else if (rbReservation.isChecked()) {
                    if (chkOnTime.isChecked()) {
                        jsonObject.put("serviceusedid", OrderTypes.OnTable);
                    } else {
                        jsonObject.put("serviceusedid", OrderTypes.TableRes);
                    }
                } else if (rbPickupAfter.isChecked()) {
                    jsonObject.put("serviceusedid", OrderTypes.PickUp);
                } else if (rbCurbsidePickup.isChecked()) {
                    jsonObject.put("serviceusedid", OrderTypes.Curbside);
                } else if (rbDelivery.isChecked()) {


                    if (rbDelUPX.isChecked()) {
                        jsonObject.put("serviceusedid", OrderTypes.UPXDelivery);

                        jsonObject.put("delopt", "1");
                        jsonObject.put("delprice", feeDelUPX);
                        delFees = feeDelUPX;
                    } else if (rbDelSelf.isChecked()) {
                        jsonObject.put("serviceusedid", OrderTypes.SelfDelivery);

                        jsonObject.put("delopt", "2");
                        jsonObject.put("delprice", feeDelSelf);
                        delFees = feeDelSelf;
                    } else if (rbDel3rdParty.isChecked()) {
                        jsonObject.put("serviceusedid", OrderTypes.Party3rd);

                        jsonObject.put("delopt", "3");
                        jsonObject.put("delprice", feeDel3rd);
                        delFees = feeDel3rd;
                    }

                    jsonObject.put("tableid", tableID);
                    jsonObject.put("seatid", 0);

                    jsonObject.put("dellon", orderParams.get("tolon"));
                    jsonObject.put("dellat", orderParams.get("tolat"));
                } else if (rbDeliverySports.isChecked()) {
                    jsonObject.put("serviceusedid", OrderTypes.EatIN);
                } else if (rbCatering.isChecked()) {
                    jsonObject.put("serviceusedid", OrderTypes.Cater);
                } else if (rbParty.isChecked()) {
                    jsonObject.put("serviceusedid", OrderTypes.PartyRoom);
                }

                jsonObject.put("tableid", tableID);

                double taxValue = 0;
                if (rbDelivery.isChecked() || rbCatering.isChecked() || rbParty.isChecked() || rbReservation.isChecked()) {
                    JSONObject itemObj = new JSONObject();
                    itemObj.put("prodid", "0");
                    itemObj.put("size", "0");
                    itemObj.put("quantity", "1");
                    itemObj.put("oz", "0");
                    itemObj.put("gram", "0");
                    itemObj.put("des", "0");

                    // Menu :       0
                    // Curbside :   1
                    // Delivery :   2
                    // Catering :   3
                    // Party :      4
                    // Reserve :    5
                    if (rbDelivery.isChecked()) {
                        itemObj.put("name", "Delivery Fee");
                        itemObj.put("price", restaurant.getDelFee());

                        taxValue = restaurant.getDelFee();
                    } else if (rbCatering.isChecked()) {
                        itemObj.put("name", "Catering Fee");
                        itemObj.put("price", restaurant.getCatFee());

                        taxValue = restaurant.getCatFee();
                    } else if (rbParty.isChecked()) {
                        itemObj.put("name", "Party Fee");
                        itemObj.put("price", restaurant.getPartyFee());

                        taxValue = restaurant.getPartyFee();
                    } else if (rbReservation.isChecked()) {
                        itemObj.put("name", "Reserve Fee");
                        itemObj.put("price", restaurant.getResFee() + (chkOnTime.isChecked() ? restaurant.getTableFee() : 0));

                        taxValue = restaurant.getResFee();
                    }

                    menuItemsArray.put(menuItemsArray.length(), itemObj);
                }
                jsonObject.put("menus", menuItemsArray);

                jsonObject.put("totprice", String.format("%.2f", totFoodPrice + totFees + totTax));
                jsonObject.put("tottax", String.format("%.2f", totTax));
                jsonObject.put("totfee", String.format("%.2f", totFees));
                jsonObject.put("delfee", String.format("%.2f", delFees));
                jsonObject.put("paynow", false);
                jsonObject.put("token", appSettings.getDeviceToken());

                String baseUrl = BaseFunctions.getBaseData(jsonObject, getApplicationContext(),
                        "AddOrder", BaseFunctions.MAIN_FOLDER, getUserLat(), getUserLon(), mMyApp.getAndroidId());

                // Try to Creat Transaction
                showProgressDialog();

                RequestQueue queue = Volley.newRequestQueue(mContext);
                final String finalOrderDueDate = orderDueDate;
                StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("CreateOrder", response);

                        hideProgressDialog();

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                final JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                                if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                                    final String orderId = jsonObject.getString("OrderID");

                                    appSettings.setOrderID(orderId);
                                    appSettings.setOrderDueDate(finalOrderDueDate);

                                    if (rbDelivery.isChecked() || rbDelUPX.isChecked() || rbDelSelf.isChecked() || rbDel3rdParty.isChecked() || rbCatering.isChecked()) {
                                        addDel(orderId);
                                    } else if (rbReservation.isChecked() || rbParty.isChecked()) {
                                        callCJLSet(orderId);
                                    } else {
                                        getToken(orderId);
                                    }
                                } else if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    if (jsonObject.has("OrderID")) {
                                        int orderID = jsonObject.getInt("OrderID");
                                        if (orderID == -2) {
                                            showAlert(R.string.msg_insufficient_funds);
                                        } else if (jsonObject.has("msg")) {
                                            showAlert(jsonObject.getString("msg"));
                                        } else {
                                            //showToastMessage("Please try again later");
                                        }
                                    } else if (jsonObject.has("msg")) {
                                        showAlert(jsonObject.getString("msg"));
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
                        baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(baseUrl));
                    }
                });

                sr.setShouldCache(false);
                queue.add(sr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }

        return true;
    }

    private void showSuccessDialog(final String orderId) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);

        final android.app.AlertDialog successDlg = new android.app.AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialogView.findViewById(R.id.viewSuccess).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                successDlg.dismiss();

                // String title = "YEStanrants! Food Order";
                // String message = getString(R.string.success_make_order);
                // showPushNotification(title, message);

                Intent confirmintent = new Intent(_activity, OrderConfirmationActivity.class);
                confirmintent.putExtra("restaurant", restaurant);
                confirmintent.putExtra("menus", menuItems);
                confirmintent.putExtra("tot_items", totItems);
                confirmintent.putExtra("tot_price", totFoodPrice);
                confirmintent.putExtra("tot_tax", totTax);
                confirmintent.putExtra("order_number", orderId);
                confirmintent.putExtra("date", dateOrderDue.getTime());

                if (rbEatInTime.isChecked()) {
                    confirmintent.putExtra("type", "Eat In");
                } else if (rbReservation.isChecked()) {
                    confirmintent.putExtra("type", "Reservation");
                } else if (rbPickupAfter.isChecked()) {
                    confirmintent.putExtra("type", "Pick-up");
                } else if (rbCurbsidePickup.isChecked()) {
                    confirmintent.putExtra("type", "Curbside");
                } else if (rbDelivery.isChecked()) {
                    confirmintent.putExtra("type", "Delivery");
                } else if (rbCatering.isChecked()) {
                    confirmintent.putExtra("type", "Catering");
                } else if (rbParty.isChecked()) {
                    confirmintent.putExtra("type", "Party");
                }

                startActivity(confirmintent);

            }
        });

        successDlg.show();
        successDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void addDel(final String orderID) {

        String orderDueAt = "";
        String orderTime = orderParams.get("timeValue");
        String orderDate = orderParams.get("dateValue");

        if (TextUtils.isEmpty(orderTime) || "ASAP".equals(orderTime)) {
            orderDueAt = DateUtil.toStringFormat_7(new Date());
        } else {
            orderDueAt = String.format("%s %s", orderDate, orderTime);
        }

        if (getLocation()) {

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "DelAdd",
                    BaseFunctions.MAIN_FOLDER,
                    String.valueOf(restaurant.get_lattiude()),
                    String.valueOf(restaurant.get_longitude()),
                    mMyApp.getAndroidId());

            String storeDelFee;
            if (rbDelivery.isChecked()) {
                storeDelFee = String.valueOf(restaurant.getDelFee());
            } else {
                storeDelFee = String.valueOf(restaurant.getCatFee());
            }
            String toServ = rbDelivery.isChecked() ? "0" : orderParams.get("numofpeople");
            String theme = rbDelivery.isChecked() ? "0" : orderParams.get("theme");

            String extraParams =
                    "&orderID=" + orderID +
                    "&tMLID=" + appSettings.getUserId() +
                    "&sellerID=" + String.valueOf(restaurant.get_id()) +
                    "&workid=" + appSettings.getWorkid() +
                    "&DeliveryDue=" + orderDueAt +
                    "&MaxLocalTime=" + orderDueAt +
                    "&currTime=" + DateUtil.toStringFormat_12(new Date()) +
                    "&capabilitiesid=" + "8207" +
                    "&toServ=" + toServ +
                    "&theme=" + theme +
                    "&tolon=" + orderParams.get("tolon") +
                    "&tolat=" + orderParams.get("tolat") +
                    "&paksize=" + "Food" +
                    "&pakwgt=" + "0" +
                    "&instruct=" + orderParams.get("notes") +
                    "&qty=" + "0" +
                    "&fph=" + restaurant.get_wp() +
                    "&fname=" + restaurant.get_name() +
                    "&fadd=" + restaurant.get_address() +
                    "&fapt=" + restaurant.get_ste() +
                    "&ffloor=" + "" +
                    "&fcsz=" + restaurant.get_csz() +
                    "&tph=" + orderParams.get("phone") +
                    "&tname=" + orderParams.get("receipt") +
                    "&tadd=" + orderParams.get("address") +
                    "&fullAddress=" + orderParams.get("address") +
                    "&tapt=" + orderParams.get("apt") +
                    "&tfloor=" + orderParams.get("floor") +
                    "&tcsz=" + orderParams.get("csz") +
                    "&none=" + "0" +
                    "&hot=" + "1" +
                    "&cold=" + "1" +
                    "&storeDelFee=" + storeDelFee;
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            //params.put("statusid", "0");

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("DelAdd", response);

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                        String status = jsonObject.getString("status");
                        if (jsonObject.getBoolean("status")) {
                            showToastMessage(R.string.msg_delivery_has_been_requested);

                            String delID = jsonObject.getString("msg").replace("DelID: ", "").trim();
                            int newdelId = 0;
                            try {
                                newdelId = Integer.parseInt(delID);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            appSettings.setNewDelID(newdelId);

                            getToken(orderID);
                        } else {
                            showAlert(jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
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
        }
    }

    private void callCJLSet(final String orderID) {
        String orderDueAt = "";
        String orderTime = orderParams.get("timeValue");
        String orderDate = orderParams.get("dateValue");

        if (TextUtils.isEmpty(orderTime) || "ASAP".equals(orderTime)) {
            orderDueAt = DateUtil.toStringFormat_7(new Date());
        } else {
            //orderDate = DateUtil.toStringFormat_26(DateUtil.parseDataFromFormat13(orderDate));
            orderDueAt = String.format("%s %s", orderDate, orderTime);
        }

        if (getLocation()) {

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLSet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());

            String Amt;
            if (rbParty.isChecked()) {
                Amt = String.valueOf(restaurant.getPartyFee());
            } else {
                Amt = String.valueOf(restaurant.getResFee());
            }
            String mode = rbReservation.isChecked() ? "4" : "1";
            String onTableFood = chkOnTime.isChecked() ? "1" : "0";
            String extraParams =
                    "&mode=" + mode +
                            "&orderID=" + orderID +
                            "&sellerID=" + String.valueOf(restaurant.get_id()) +
                            "&buyerid=" + appSettings.getUserId() +
                            "&industryID=" + String.valueOf(restaurant.get_industryID()) +
                            "&time=" + orderDueAt +
                            "&NoteID=" + "0" +
                            "&promoID=" + "0" +
                            "&promoid=" + "0" +
                            "&UTC=" + appSettings.getUTC() +
                            "&mins=" + "0" +
                            "&QTY=" + orderParams.get("inparty") +
                            "&PrivateRm=" + orderParams.get("privateRm") +
                            "&RoomName=" + orderParams.get("name") +
                            "&eventname=" + orderParams.get("name") +
                            "&tolat=" + "1" +
                            "&tolon=" + "1" +
                            "&Amt=" + Amt +
                            "&onTableFood=" + onTableFood;
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

                    Log.e("CJLSet", response);

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                        String status = jsonObject.getString("status");
                        if (jsonObject.getBoolean("status")) {
                            getToken(orderID);
                        } else {
                            showAlert(jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
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
        }
    }

    private void getToken(final String orderId) {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGetToken",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "storeowner" +
                            "&TokenMLID=" + String.valueOf(restaurant.get_id());
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(_activity);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(_activity);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("CJLGetToken", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            if (jsonArray.length() == 0) {
                                showSuccessDialog(orderId);
                                return;
                            }

                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                //showMsgAndGo2Home(jsonObject.getString("msg"));
                                showSuccessDialog(orderId);
                            } else {
                                NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                ArrayList<FCMTokenData> tokenList = notificationHelper.getTokenList(jsonArray);
                                // XiaoPOS
                                // tokenList.add(new FCMTokenData("f4RDiMTwSG8:APA91bEIPNcUk3oSjrzraQY4nf_Vc4xK0Pjvm8Ku2iSDYa6QNm1Xd2XvPw_08WD3ejBeBt80Qk9Y4Y5OC1PC4EzcYDFYOtpq-XBDob-MK0UObDsM9X1hXpLgEeq1xLyKVJIXxbAttL68", FCMTokenData.OS_UNKNOWN));
                                if (!tokenList.isEmpty()) {
                                    JSONObject payload = new JSONObject();
                                    payload.put("title", "New Order Request");
                                    payload.put("message", String.format("You got new Order(%s) request from user", orderId));
                                    payload.put("orderId", orderId);
                                    payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                    payload.put("SenderID", appSettings.getUserId());
                                    payload.put("statusID", JustOrdered.statusId);
                                    notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Order_Status, payload);
                                }
                                showSuccessDialog(orderId);
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
                    baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                    showSuccessDialog(orderId);

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

    HashMap<String, String> orderParams;

    private void resetOptionTitles() {
        rbReservation.setText(R.string.title_table_reserv);
        rbDelivery.setText(R.string.title_delivery_around);
        rbCatering.setText(R.string.title_catering_at);
        rbParty.setText(R.string.title_party_at);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String date = "";
            String time = "";

            orderParams = (HashMap<String, String>) data.getSerializableExtra("param");

            if (requestCode == REQUEST_DELIVERY) {

                groupDel.setVisibility(View.VISIBLE);

                date = orderParams.get("date");
                time = orderParams.get("time");

                resetOptionTitles();

                if (time.equals("ASAP")) {
                    rbDelivery.setText(getString(R.string.format_delivery_around1, time));
                } else {
                    rbDelivery.setText(getString(R.string.format_delivery_around2, date, time));
                }

                checkDriverAvailable(0);
            } else if (requestCode == REQUEST_RESERVETABLE) {
                date = orderParams.get("date");
                time = orderParams.get("time");

                resetOptionTitles();

                if (time.equals("ASAP")) {
                    rbReservation.setText(getString(R.string.format_table_reserv1, time));
                } else {
                    rbReservation.setText(getString(R.string.format_table_reserv2, date, time));
                }

                String onTimeOption = orderParams.get("ontime");

                // When change the options, fee is changed according the options
                totFees = (float) restaurant.getResFee();
                delFees = 0;
                if (onTimeOption.equals("1")) {
                    chkOnTime.setChecked(true);
                    totFees += restaurant.getTableFee();
                } else {
                    chkOnTime.setChecked(false);
                }
                updatePrice();
            } else if (requestCode == REQUEST_CATERING) {
                date = orderParams.get("date");
                time = orderParams.get("time");

                resetOptionTitles();

                if (time.equals("ASAP")) {
                    rbCatering.setText(getString(R.string.format_catering_at1, time));
                } else {
                    rbCatering.setText(getString(R.string.format_catering_at2, date, time));
                }
                checkDriverAvailable(1);
            } else if (requestCode == REQUEST_PARTY) {
                date = orderParams.get("date");
                time = orderParams.get("time");

                resetOptionTitles();

                if (time.equals("ASAP")) {
                    rbParty.setText(getString(R.string.format_party_at1, time));
                } else {
                    rbParty.setText(getString(R.string.format_party_at2, date, time));
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            rbEatInTime.setChecked(true);
        }

        if (requestCode == REQUEST_TRANSFUNDS) {
            getAvaBalance();
        }
    }

    private void checkDriverAvailable(final int option) {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "isDriverAvailable",
                    BaseFunctions.MAIN_FOLDER,
                    String.valueOf(restaurant.get_lattiude()),
                    String.valueOf(restaurant.get_longitude()),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&delLon=" + orderParams.get("tolon") +
                            "&delLat=" + orderParams.get("tolat") +
                            "&delOption=" + "0";
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

                    Log.e("isDriverAvailable", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                msg(jsonObject.getString("msg"));

                                if (option == 1) {
                                    rbEatInTime.setChecked(true);
                                    rbCatering.setEnabled(false);
                                }
                            }

                            if (option == 0) {
                                Boolean opt1Avail = jsonObject.optBoolean("opt1Avail");
                                rbDelUPX.setEnabled(opt1Avail);
                                Boolean opt2Avail = jsonObject.optBoolean("opt2Avail");
                                rbDelSelf.setEnabled(opt2Avail);
                                Boolean opt3Avail = jsonObject.optBoolean("opt3Avail");
                                rbDel3rdParty.setEnabled(opt3Avail);

                                feeDelUPX = (float) jsonObject.optDouble("opt1Price");
                                feeDelSelf = (float) jsonObject.optDouble("opt2Price");
                                feeDel3rd = (float) jsonObject.optDouble("opt3Price");

                                rbDelUPX.setText(String.format("UPX Delivery(Fee:$%.2f)", feeDelUPX));
                                rbDelSelf.setText(String.format("Self Delivery(Fee:$%.2f)", feeDelSelf));
                                rbDel3rdParty.setText(String.format("3rd Party Delivery(Fee:$%.2f)", feeDel3rd));

                                if (!opt1Avail & !opt2Avail & !opt2Avail) {
                                    rbEatInTime.setChecked(true);
                                    rbDelivery.setEnabled(false);
                                } else {
                                    updatePrice();
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
                    baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
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
}
