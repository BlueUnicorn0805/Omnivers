package hawaiiappbuilders.omniversapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.fragment.XTabEnterAcct;
import hawaiiappbuilders.omniversapp.fragment.XTabEnterAmount;
import hawaiiappbuilders.omniversapp.fragment.XTabRequestFunds;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

import java.util.ArrayList;

public class ActivityBank extends BaseActivity {
    public static final String TAG = ActivityBank.class.getSimpleName();
    TabLayout tabLayout;
    ArrayList<String> tabTitles;
    ArrayList<Integer> tabIcons;

    ArrayList<Fragment> tabFragments;

    ViewPager2 pager;
    ScreenSlidePagerAdapter pagerAdapter;

    // 0= not seen anything and not setup
    // 1= not set account & routing
    // 2= needs to verify
    // 3= they are verified so stay on tab(1)
    public static final int TRANSFER_NOT_SEEN_SETUP = 0;
    public static final int TRANSFER_NOT_SETUP_ACCT = 1;
    public static final int TRANSFER_NEEDS_VERIFY = 20;
    public static final int TRANSFER_VERIFIED = 30;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Load your HaloPay Account");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        //initList();
        initViews();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initViews() {

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnToolbarHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });
        tabTitles = new ArrayList<>();
        tabIcons = new ArrayList<>();
        tabFragments = new ArrayList<>();

        tabTitles.add("Request Funds");
        tabTitles.add("Enter Account for deposits");
        tabTitles.add("Deposit Funds");

        tabIcons.add(R.drawable.ic_nav_bank);
        tabIcons.add(R.drawable.ic_nav_localshop);
        tabIcons.add(R.drawable.ic_nav_bank);

        tabFragments.add(XTabRequestFunds.newInstance("RequestFunds"));
        tabFragments.add(XTabEnterAcct.newInstance("EnterAcct"));
        tabFragments.add(XTabEnterAmount.newInstance("EnterAmount"));

        pager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab_layout);

        pagerAdapter = new ScreenSlidePagerAdapter(ActivityBank.this);
        pager.setAdapter(pagerAdapter);
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 1) {
                    ((XTabEnterAcct)tabFragments.get(1)).getVerifyStatus(0, false);
                }
            }
        });
        pager.setOffscreenPageLimit(pagerAdapter.getItemCount());
        pager.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout, pager, (tab, position) -> {
            /*if (position == 0) {
                tab.setText(R.string.photos);
                tab.view.setBackground(getResources().getDrawable(R.drawable.ic_rectangle_1345));
            } else {
                tab.setText(R.string.videos);
            }*/

            tab.setText(tabTitles.get(position));
            tab.setIcon(tabIcons.get(position));
        }).attach();
        setupTabIcons();

        if (tabLayout != null) {
            int tabIconColor = ContextCompat.getColor(mContext, R.color.white);
            int tabIconColorInactive = ContextCompat.getColor(mContext, /*R.color.bg_tab*/R.color.bg_tab_transparent);

            for (int i = 1; i < tabTitles.size(); i++) {
                tabLayout.getTabAt(i).getIcon().setColorFilter(tabIconColorInactive, PorterDuff.Mode.SRC_IN);
            }
            tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(mContext, R.color.white);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
//                        pager.setCurrentItem(tab.getPosition());
//                        TextView text = (TextView) tab.getCustomView();
//                        text.setTypeface(Utils.getBold(getActivity()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(mContext, /*R.color.bg_tab*/R.color.bg_tab_transparent);
                //tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
//                        pager.setCurrentItem(tab.getPosition());
//                        TextView text = (TextView) tab.getCustomView();
//                        text.setTypeface(Utils.getNormalFont(getActivity()));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        changeTabsFont(tabLayout);
        tabLayout.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAcctStatus();
            }
        }, 500);

    }

    private void checkAcctStatus() {
        if (appSettings.getTransMoneyStatus() == TRANSFER_NOT_SEEN_SETUP) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);

            builder.setMessage("Bank transfers are not available until the bank account has been confirmed.")
                    .setIcon(R.mipmap.ic_launcher1_foreground)
                    .setCancelable(false)
                    // Set the action buttons
                    .setPositiveButton("SETUP", (dialog, id) -> {

                        dialog.dismiss();

                        // Move to tab1
                        showTab(1);
                    })
                    .setNegativeButton("NOT NOW", (dialog, id) -> dialog.dismiss());

            builder.create().show();

            appSettings.setTransMoneyStatus(ActivityBank.TRANSFER_NOT_SETUP_ACCT);
        } else if (appSettings.getTransMoneyStatus() != TRANSFER_VERIFIED) {
            showTab(1);
        }
    }

    public void showTab(int index) {
        pager.setCurrentItem(index, true);
    }

    private void setupTabIcons() {
        for (int i = 0; i < tabTitles.size(); i++) {
            tabLayout.getTabAt(i).setIcon(tabIcons.get(i));
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {


        public ScreenSlidePagerAdapter(FragmentActivity fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return tabFragments.get(position);
        }

        @Override
        public int getItemCount() {
            return tabTitles.size();
        }
    }

    private void changeTabsFont(TabLayout tabLayout) {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(getNormalFont(mContext));
                }
            }
        }
    }

    public static Typeface getNormalFont(Context c) {
        try {
            return Typeface.createFromAsset(c.getAssets(), "fonts/OpenSans-Regular.ttf");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
