package hawaiiappbuilders.omniversapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.adapters.RecentDelsAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.DeliveryItem;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class ActivityRecentDels extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityRecentDels.class.getSimpleName();
    Context context;

    ArrayList<DeliveryItem> deliveriesInfoBySender = new ArrayList<>();
    RecyclerView recyclerView;
    RecentDelsAdapter delsAdapter;
    TextView mEmptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_dels);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Recent Deliveries");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initViews();

        getDeliveriesBySender();
    }

    private void initViews() {
        findViewById(R.id.btnAddNew).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);

        context = this;
        recyclerView = findViewById(R.id.transaction_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        // itemDecorator.setDrawable(getResources().getDrawable(R.drawable.divider));
        // recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        delsAdapter = new RecentDelsAdapter(mContext, deliveriesInfoBySender);
        recyclerView.setAdapter(delsAdapter);
        mEmptyList = findViewById(R.id.emptyList);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getDeliveriesBySender() {
        deliveriesInfoBySender.clear();

        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=DelsBySenderID" +
                    "&misc=" + appSettings.getUserId() +
                    "&industryID=80" +
                    "&sellerID=0";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("DelsBySenderID", response);

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;

                        if (jsonObject.has("status")) {
                            showToastMessage(jsonObject.getString("msg"));
                        } else {
                            deliveriesInfoBySender.clear();

                            Gson gson = new Gson();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject favDataObj = jsonArray.getJSONObject(i);
                                DeliveryItem newDelInfo = gson.fromJson(favDataObj.toString(), DeliveryItem.class);

                                deliveriesInfoBySender.add(newDelInfo);
                            }
                            Log.e("mydels", String.format("There is(are) %d items of driver dels", deliveriesInfoBySender.size()));
                        }

                        if (deliveriesInfoBySender.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            mEmptyList.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            mEmptyList.setVisibility(View.GONE);
                            delsAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    if (TextUtils.isEmpty(error.getMessage())) {
                        showAlert("Server error!");
                    } else {
                        showAlert(error.getMessage());
                    }
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
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnAddNew) {
            startActivity(new Intent(mContext, NewDeliveryActivity.class));
        } else if (viewId == R.id.btnCancel) {
            finish();
        }
    }
}
