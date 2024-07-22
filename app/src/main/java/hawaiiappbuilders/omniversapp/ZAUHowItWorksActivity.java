package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import hawaiiappbuilders.omniversapp.adapters.HowItWorksPagerAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.PageNavigator;

public class ZAUHowItWorksActivity extends BaseActivity implements View.OnClickListener {

    ViewPager viewPager;
    PageNavigator navigator;
    TextView btnDone;
    HowItWorksPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zauhowitworks);

        appSettings.setClockedIn();

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        navigator = (PageNavigator) findViewById(R.id.navigator);
        btnDone = (TextView) findViewById(R.id.btnDone);

        pagerAdapter = new HowItWorksPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        //viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position == pagerAdapter.getCount() - 1) {
                    btnDone.setText("Done");
                } else {
                    btnDone.setText("Next");
                }

                navigator.setPosition(position);
                navigator.invalidate();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        navigator.setWhite();
        navigator.setSize(pagerAdapter.getCount());
        navigator.invalidate();

        // Add Button Actions
        findViewById(R.id.btnDone).setOnClickListener(this);
        findViewById(R.id.btnSkip).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnDone) {
            if (viewPager.getCurrentItem() == pagerAdapter.getCount() - 1) {
                Intent intent = new Intent(mContext, ActivityLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        } else if (viewId == R.id.btnSkip) {
            Intent intent = new Intent(mContext, ActivityLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}


