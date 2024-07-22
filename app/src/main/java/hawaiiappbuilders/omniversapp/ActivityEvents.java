package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Arrays;
import java.util.List;

import hawaiiappbuilders.omniversapp.adapters.EventsBannerAdsAdapter;
import hawaiiappbuilders.omniversapp.adapters.EventsContentsAdsAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.PageNavigator;

public class ActivityEvents extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityEvents.class.getSimpleName();
    RecyclerView rcvTop;
    PageNavigator pageIndicator;

    RecyclerView rcvBottom;

    BottomSheetBehavior sheetBehavior;

    Spinner spinnerLocation;
    Spinner spinnerDistance;
    Spinner spinnerDateRange;
    Spinner spinnerCategory;
    Spinner spinnerTickets;
    Spinner spinnerPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_events);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("VAULT");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final List<String> topAdsVideoIDs = Arrays.asList("84bPknTEML8", "oauQu9Wm14U", "AOYACk7m7Fk", "8XGUHITLymI", "uMYx5gRQlxQ", "xjPi6IcSH_Q");
        rcvTop = findViewById(R.id.rcvTop);
        rcvTop.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        rcvTop.setItemViewCacheSize(2);
        rcvTop.setAdapter(new EventsBannerAdsAdapter(mContext, topAdsVideoIDs, new EventsBannerAdsAdapter.AdsItemListener() {
            @Override
            public void onItemClicked(int position) {

            }
        }));

        pageIndicator = findViewById(R.id.pageIndicator);
        pageIndicator.setSize(topAdsVideoIDs.size());
        pageIndicator.setWhite();
        rcvTop.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rcvTop.getLayoutManager();
                pageIndicator.setPosition(linearLayoutManager.findFirstVisibleItemPosition());
                pageIndicator.invalidate();
            }
        });

        final List<String> bottomAdsVideoIDs = Arrays.asList("R79CHsNBoVA", "oauQu9Wm14U", "5YOLAXYzR4g", "8XGUHITLymI");
        rcvBottom = findViewById(R.id.rcvBottom);
        rcvBottom.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        rcvBottom.setAdapter(new EventsContentsAdsAdapter(mContext, bottomAdsVideoIDs, new EventsContentsAdsAdapter.AdsItemListener() {
            @Override
            public void onItemClicked(int position) {

            }
        }));

        findViewById(R.id.openFilter).setOnClickListener(this);

        sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        findViewById(R.id.tvSearch).setOnClickListener(this);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        ArrayAdapter adapterLocation = ArrayAdapter.createFromResource(this, R.array.array_filter_location, R.layout.layout_spinner_filter);
        adapterLocation.setDropDownViewResource(R.layout.layout_spinner_filter);
        spinnerLocation.setAdapter(adapterLocation);

        spinnerDistance = findViewById(R.id.spinnerDistance);
        ArrayAdapter adapterDistance = ArrayAdapter.createFromResource(this, R.array.array_filter_distances, R.layout.layout_spinner_filter);
        adapterDistance.setDropDownViewResource(R.layout.layout_spinner_filter);
        spinnerDistance.setAdapter(adapterDistance);

        spinnerDateRange = findViewById(R.id.spinnerDateRange);
        ArrayAdapter adapterDateRange = ArrayAdapter.createFromResource(this, R.array.array_months, R.layout.layout_spinner_filter);
        adapterDateRange.setDropDownViewResource(R.layout.layout_spinner_filter);
        spinnerDateRange.setAdapter(adapterDateRange);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        ArrayAdapter adapterCategory = ArrayAdapter.createFromResource(this, R.array.array_categories, R.layout.layout_spinner_filter);
        adapterCategory.setDropDownViewResource(R.layout.layout_spinner_filter);
        spinnerCategory.setAdapter(adapterCategory);

        spinnerTickets = findViewById(R.id.spinnerTickets);
        ArrayAdapter adapterTickets = ArrayAdapter.createFromResource(this, R.array.array_filter_tickets, R.layout.layout_spinner_filter);
        adapterTickets.setDropDownViewResource(R.layout.layout_spinner_filter);
        spinnerTickets.setAdapter(adapterTickets);

        spinnerPrice = findViewById(R.id.spinnerPrice);
        ArrayAdapter adapterPrice = ArrayAdapter.createFromResource(this, R.array.array_filter_prices, R.layout.layout_spinner_filter);
        adapterPrice.setDropDownViewResource(R.layout.layout_spinner_filter);
        spinnerPrice.setAdapter(adapterPrice);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.openFilter || viewId == R.id.tvSearch) {
            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }
}
