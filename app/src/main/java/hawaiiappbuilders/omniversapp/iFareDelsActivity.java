package hawaiiappbuilders.omniversapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import hawaiiappbuilders.omniversapp.fragment.BaseFragment;
import hawaiiappbuilders.omniversapp.fragment.SenderDeliveriesMapFragment;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class iFareDelsActivity extends BaseActivity implements View.OnClickListener{

    DrawerLayout drawer;
    Toolbar toolbar;
    TextView toolbarTitle;

    BaseFragment currentFragment;

    public static final int FRAGMENT_HOME = 1;
    public static final int FRAGMENT_SENDER_DELIVERY_LIST = 2;
    public static final int FRAGMENT_SENDER_DELIVERY_MAP = 3;
    public static final int FRAGMENT_SENDER_NEW_DELIVERY = 4;
    public static final int FRAGMENT_DRIVER_DELIVERY_LIST = 5;
    public static final int FRAGMENT_DRIVER_DELIVERY_MAP = 6;
    public static final int FRAGMENT_DRIVER_DELIVERY = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ifaredels);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        showRelevantFragment();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setTitle(String title) {
        toolbarTitle.setText(title);
    }

    BroadcastReceiver updateStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", 0);

            Log.e("update", String.format("Received update message. Type is %d", type));
            if (type == 1 || type == 2) {
                if (currentFragment != null && currentFragment instanceof SenderDeliveriesMapFragment) {
                    ((SenderDeliveriesMapFragment)currentFragment).awakeOnDuty();
                    Log.e("update", "Update Delivery Status --------------------------");
                }
            } else if(type == 4) {
                showFragment(FRAGMENT_SENDER_DELIVERY_MAP);
            }
        }
    };

    private void showRelevantFragment() {

        showFragment(FRAGMENT_DRIVER_DELIVERY_MAP);

        /*if (!TextUtils.isEmpty(appSettings.getDriverID())) {
            showFragment(FRAGMENT_DRIVER_DELIVERY_MAP);
        } else {
            // Original Logic
            // showFragment(FRAGMENT_SENDER_DELIVERY_MAP);
            showFragment(FRAGMENT_SENDER_NEW_DELIVERY);
        }*/
    }

    public void showFragment(int fragmentID) {

        // Create fragment and give it an argument specifying the article it should show
        BaseFragment newFragment = null;
        String title = getString(R.string.app_name);
        if (fragmentID == FRAGMENT_DRIVER_DELIVERY_MAP) {
            if (currentFragment != null && currentFragment instanceof SenderDeliveriesMapFragment) {
                return;
            }

            newFragment = SenderDeliveriesMapFragment.newInstance("Driver_Delivery_Map");
            title = "Order Status";
        }

        if (newFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
            currentFragment = newFragment;

            setTitle(title);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregisterReceiver(updateStatusReceiver);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentFragment != null) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
