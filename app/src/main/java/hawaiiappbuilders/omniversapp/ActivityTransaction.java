package hawaiiappbuilders.omniversapp;

import android.content.Context;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import hawaiiappbuilders.omniversapp.adapters.TransactionAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.Transaction;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityTransaction extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityTransaction.class.getSimpleName();
    Context context;
    ArrayList<Transaction> mTransactions;
    TransactionAdapter adapter;
    RecyclerView mTransactionList;

    TextView mEmptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.tbar);
        toolbar.setTitle("Transactions");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        initViews();
        if (checkLocationPermission()) {
            getTransactionData();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initViews() {
        context = this;

        mTransactions = new ArrayList<>();

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        mTransactionList = findViewById(R.id.transaction_recyclerView);
        mEmptyList = findViewById(R.id.emptyList);
    }

    private void setUpRecyclerView(ArrayList<Transaction> mTransactions) {
        mTransactionList.setHasFixedSize(true);
        mTransactionList.setLayoutManager(new LinearLayoutManager(context));

        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(getResources().getDrawable(R.drawable.divider));

        mTransactionList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new TransactionAdapter(context, mTransactions);
        mTransactionList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getTransactionData() {
        Log.e(TAG, "getLocation: ");

        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "TXs" +
                            "&sellerID=" + appSettings.getWorkid() +
                            "&misc=" + appSettings.getUserId();
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

                    Log.e("response", "response -> " + response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;

                            if (jsonObject.get("status") instanceof Integer) {
                                if (jsonObject.getInt("status") == 1) {
                                    mTransactionList.setVisibility(View.VISIBLE);
                                    mEmptyList.setVisibility(View.GONE);

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject data = jsonArray.getJSONObject(i);
                                        Transaction transaction = new Transaction();

                                        transaction.setName(data.getString("Name"));
                                        transaction.setAmt(data.getString("Amt"));
                                        transaction.setItemDate(data.getString("ItemDate"));
                                        transaction.setTxID(data.getString("TxID"));
                                        transaction.setToID(data.getString("toID"));
                                        transaction.setNote(data.getString("Note"));
                                        mTransactions.add(transaction);
                                    }

                                    setUpRecyclerView(mTransactions);
                                }
                            } else {
                                showToastMessage(jsonObject.getString("msg"));
                                mTransactionList.setVisibility(View.GONE);
                                mEmptyList.setVisibility(View.VISIBLE);
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

                    mTransactionList.setVisibility(View.GONE);
                    mEmptyList.setVisibility(View.VISIBLE);

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

    @Override
    protected void onResume() {
        super.onResume();
        //navigateToLoginIfUserIsLoggedOut();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tx_btn_dashboard) {
            finish();
        } else if (viewId == R.id.btnToolbarHome) {
            backToHome();
        } else if (viewId == R.id.btnBack) {
            finish();
        }
    }
}
