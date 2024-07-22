package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import hawaiiappbuilders.omniversapp.adapters.IndustryAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.IndustryInfo;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.SearchRestaurantHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityAppointmentBooking extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityAppointmentBooking.class.getSimpleName();
    private EditText edtSearch;

    private LinearLayout recycler_container;
    private LinearLayout ab_no_providers_found_ll;

    ExpandableListView lvIndustries;
    IndustryAdapter industriesAdapter;
    private List<IndustryInfo> industries = new ArrayList<>();
    ArrayList<IndustryInfo> industriesFiltered = new ArrayList<>();

    private DataUtil dataUtil;

    private void initList() {
        industries.add(new IndustryInfo("131", "Spa's", true));
        industries.add(new IndustryInfo("126", "Nail Salon", false));
        industries.add(new IndustryInfo("128", "Pedicure", false));
        industries.add(new IndustryInfo("127", "Hair Barbers and Salons", false));
        industries.add(new IndustryInfo("129", "Massage - Therapy", false));
        industries.add(new IndustryInfo("130", "Massage - Body", false));
        industries.add(new IndustryInfo("132", "Cannabis", true));
        industries.add(new IndustryInfo("133", "Marijuana - Medical", false));
        industries.add(new IndustryInfo("134", "Marijuana - Recreational", false));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_booking);
        dataUtil = new DataUtil(this, ActivityAppointmentBooking.class.getSimpleName());
        //initList();
        initViews();
        recycler_container.setVisibility(RecyclerView.VISIBLE);
        ab_no_providers_found_ll.setVisibility(LinearLayout.GONE);
    }

    private void initViews() {

        /*Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Find A Business");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);

        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);
        searchView.setQueryHint(Html.fromHtml("<font color = #a0a0a0>What can I help you with?</font>"));

        //appointmentRecyclerView = (RecyclerView) findViewById(R.id.ab_recycler_view);
        recycler_container = (LinearLayout) findViewById(R.id.recycler_container);
        ab_no_providers_found_ll = (LinearLayout) findViewById(R.id.ab_no_providers_found_ll);
        setUpDataListView();

        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateIndustryList(edtSearch.getText().toString().trim());
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateIndustryList(newText);
                return false;
            }
        });

        // Get Industry Information
        getIndustries();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setUpDataListView() {

        lvIndustries = (ExpandableListView) findViewById(R.id.lvData);
        industriesAdapter = new IndustryAdapter(mContext, industriesFiltered, new IndustryAdapter.ItemSelectListener() {
            @Override
            public void onItemSelected(int groupPosition, int childPosition) {
                getRestaurantsInfo(industriesFiltered.get(groupPosition).getChildIndustryInfo().get(childPosition));
            }
        });

        lvIndustries.setAdapter(industriesAdapter);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            lvIndustries.setIndicatorBounds(width - GetPixelFromDips(45), width - GetPixelFromDips(15));
        } else {
            lvIndustries.setIndicatorBoundsRelative(width - GetPixelFromDips(45), width - GetPixelFromDips(15));
        }
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private void getIndustries() {
        try {
            if (getLocation()) {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "CJLGet",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&mode=" + "ListIndustries" +
                                "&misc=" + "700000" + // ignored
                                "&industry=" + "123"; // ignored
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

                        if (!TextUtils.isEmpty(response)) {
                            try {
                                // Refresh Data
                                industries.clear();

                                JSONArray jsonArray = new JSONArray(response);
                                int itemCnt = jsonArray.length();
                                for (int i = 0; i < itemCnt; i++) {

                                    // Null Check
                                    if (jsonArray.getString(i).equals("null"))
                                        break;

                                    JSONObject itemObj = jsonArray.getJSONObject(i);

                                    IndustryInfo newRes = new IndustryInfo();
                                    newRes.setIndustryID(itemObj.getString("IndustID"));
                                    newRes.setTypeDesc(itemObj.getString("CatName"));

                                    // Get Child List
                                    if (itemObj.has("items") && !itemObj.isNull("items")) {
                                        ArrayList<IndustryInfo> childItems = new ArrayList<>();
                                        JSONArray childArray = itemObj.getJSONArray("items");

                                        // Parse Child Industry items
                                        for (int j = 0; j < childArray.length(); j++) {

                                            // Null Check
                                            if (childArray.getString(j).equals("null"))
                                                break;

                                            JSONObject childObj = childArray.getJSONObject(j);
                                            IndustryInfo child = new IndustryInfo();
                                            child.setIndustryID(childObj.getString("IndustID"));
                                            child.setTypeDesc(childObj.getString("Name"));

                                            childItems.add(child);
                                        }
                                        newRes.setChildIndustryInfo(childItems);
                                    }

                                    industries.add(newRes);
                                }

                                industriesFiltered.addAll(industries);

                                industriesAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showAlert(e.getMessage());
                            }
                        } else {
                            showAlert("Server Error");
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
        }catch (Exception e){
            if (dataUtil!=null) {
                dataUtil.setActivityName(ActivityAppointmentBooking.class.getSimpleName());
                dataUtil.zzzLogIt(e, "CJLGet");
            }
        }
    }

    private void updateIndustryList(String searchKeyword) {

        industriesFiltered.clear();

        if (TextUtils.isEmpty(searchKeyword)) {
            industriesFiltered.addAll(industries);
        } else {
            for (int i = 0; i < industries.size(); i++) {

                IndustryInfo industryInfoHeader = industries.get(i);

                // Check with Group
                if (industryInfoHeader.getTypeDesc().toLowerCase().contains(searchKeyword.toLowerCase())) {
                    industriesFiltered.add(industryInfoHeader);
                    continue;
                }

                // Check with Child
                ArrayList<IndustryInfo> childIndustries = industryInfoHeader.getChildIndustryInfo();
                if (childIndustries != null) {
                    IndustryInfo industryInfoHeaderFiltered = null;
                    ArrayList<IndustryInfo> childIndustriesFiltered = new ArrayList<>();
                    for (int j = 0; j < childIndustries.size(); j++) {
                        IndustryInfo childInfo = childIndustries.get(j);

                        if (childInfo.getTypeDesc().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            if (industryInfoHeaderFiltered == null) {
                                industryInfoHeaderFiltered = new IndustryInfo();
                                industryInfoHeaderFiltered.setIndustryID(industryInfoHeader.getIndustryID());
                                industryInfoHeaderFiltered.setTypeDesc(industryInfoHeader.getTypeDesc());
                            }
                            childIndustriesFiltered.add(childInfo);
                        }
                    }

                    if (industryInfoHeaderFiltered != null) {
                        industryInfoHeaderFiltered.setChildIndustryInfo(childIndustriesFiltered);
                        industriesFiltered.add(industryInfoHeaderFiltered);
                    }
                }
            }
        }

        // Refresh Data
        industriesAdapter.notifyDataSetChanged();
    }

    /*void filter(String text){
        List<DataHolder> temp = new ArrayList();
        for(DataHolder d: displayedList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getEnglish().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        disp_adapter.updateList(temp);
    }*/

    private void getRestaurantsInfo(final IndustryInfo industryItem) {

        if (getLocation()) {
            try {
                String extraParams = "&sellerID=" + "0" +
                        "&industryID=" + industryItem.getIndustryID() +
                        "&Company=" + "" +
                        "&mode=" + SearchRestaurantHelper.MODE_NEARBY +
                        "&B=" + "0" +
                        "&L=" + "0" +
                        "&D=" + "0" +
                        "&it=" + "0" +
                        "&mx=" + "0" +
                        "&am=" + "0" +
                        "&asi=" + "0" +
                        "&des=" + "0" +
                        "&fr=" + "0" +
                        "&sal=" + "0" +
                        "&sea=" + "0" +
                        "&sf=" + "0" +
                        "&stk=" + "0" +
                        "&Deli=" + "0" +
                        "&gr=" + "0" +
                        "&ind=" + "0" +
                        "&jew=" + "0" +
                        "&veg=" + "0" +
                        "&gFr=" + "0" +
                        "&cof=" + "0" +
                        "&bar=" + "0" +
                        "&cat=" + "0" +
                        "&res=" + "0" +
                        "&del=" + "0";

                new SearchRestaurantHelper(ActivityAppointmentBooking.this, extraParams, new SearchRestaurantHelper.SearchRestaurantCallback() {
                    @Override
                    public void onFailed(String message) {
                        showAlert(message);
                    }

                    @Override
                    public void onSuccess(ArrayList<Restaurant> restaurants, int mode) {
                        if (restaurants.isEmpty()) {
                            showAlert("No search Result");
                            return;
                        }

                        if (industryItem.getIndustryID().equals("123")) {
                            Intent intent = new Intent(mContext, RestaurantListActivity.class);
                            intent.putExtra("parent", "industry");
                            intent.putExtra("industry_info", industryItem);
                            intent.putExtra("restaurants", restaurants);
                            startActivity(intent);
                        } else {
                            //startActivity(new Intent(mContext,ActivityServiceProvider.class));
                            Intent intent = new Intent(mContext, ServiceListActivity.class);
                            intent.putExtra("parent", "industry");
                            intent.putExtra("industry_info", industryItem);
                            intent.putExtra("restaurants", restaurants);
                            startActivity(intent);
                        }
                    }
                }, SearchRestaurantHelper.MODE_TYPE).execute();
            }catch (Exception e){
                if (dataUtil!=null) {
                    dataUtil.setActivityName(ActivityAppointmentBooking.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "RestaurantInfos - bbbb");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.ab_no_providers_found) {
            startActivity(new Intent(mContext,ActivityAppointmentSetting.class));
            finish();
        } else if(viewId == R.id.btnToolbarHome) {
        backToHome();
        }else if (viewId == R.id.btnBack) {
            finish();
        }
    }
}
