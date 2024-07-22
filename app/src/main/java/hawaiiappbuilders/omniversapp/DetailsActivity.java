package hawaiiappbuilders.omniversapp;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.MyViewPager;
import hawaiiappbuilders.omniversapp.model.Biz;

/**
 * Created by RahulAnsari on 22-09-2018.
 */

public class DetailsActivity extends BaseActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private String instaCash = "";
    private MyViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        instaCash = getIntent().getStringExtra("INSTA_CASH");

        TextView instaCashTitleTextView = (TextView) findViewById(R.id.instaCashTitle);
        TextView instaCashTextView = (TextView) findViewById(R.id.instaCash);
        if (!instaCash.isEmpty()) {
            instaCashTextView.setText("Balance : $ "+formatMoney(instaCash));
        } else {
            instaCashTitleTextView.setVisibility(View.GONE);
            instaCashTextView.setVisibility(View.GONE);
        }
        setToolBar();
        viewPager = (MyViewPager
                ) findViewById(R.id.resume_details_vp);
        viewPager.setAdapter(new CustomPagerAdapter(this));
    }

    private void setToolBar() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return true;
    }

    private class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.details_item, collection, false);
            initiateAndPopulateItems(layout,position);
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return Biz.getBizList().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Biz biz = Biz.getBizList().get(position);
            return biz.getCo();
        }

        private void initiateAndPopulateItems(ViewGroup layout, int position) {
            TextView company = (TextView) layout.findViewById(R.id.company);
            TextView fn = (TextView) layout.findViewById(R.id.fn);
            TextView ln = (TextView) layout.findViewById(R.id.ln);
            TextView dist = (TextView) layout.findViewById(R.id.dist);

            company.setText(""+Biz.getBizList().get(position).getCo());
            fn.setText(""+Biz.getBizList().get(position).getFN());
            ln.setText(""+Biz.getBizList().get(position).getLN());
            dist.setText(""+Biz.getBizList().get(position).getDist());

        }
    }
}