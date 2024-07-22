package hawaiiappbuilders.omniversapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;


public class BaseFragment extends Fragment {

    protected static final String TEXT_FRAGMENT = "BaseFrg";
    protected static final String DATA_FRAGMENT = "DataFrg";

    protected Context mContext;
    protected BaseActivity parentActivity;
    protected KTXApplication mMyApp;
    protected AppSettings appSettings;

    protected static final int PERMISSION_REQUEST_CODE_LOCATION = 101;
    protected static final String[] PERMISSION_REQUEST_LOCATION_STRING = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    public static BaseFragment newInstance(String text) {
        BaseFragment mFragment = new BaseFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(TEXT_FRAGMENT, text);
        mFragment.setArguments(mBundle);


        return mFragment;
    }

    protected void init(Bundle bundle) {

        mContext = getActivity();
        parentActivity = (BaseActivity) getActivity();
        mMyApp = (KTXApplication) parentActivity.getApplication();
        appSettings = new AppSettings(mContext);

        Log.e("FragClasses", this.getClass().getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_splash, container, false);

        init(getArguments());
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateFields();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveFields();
    }

    protected void updateFields() {
        // Abstract Function
    }

    public void saveFields() {
        // Abstract Function
    }

    public boolean isAllValidField() {
        // Abstract Function
        return true;
    }

    protected void fillEditTextWithValue(TextView edt, String value) {
        if (edt != null && !TextUtils.isEmpty(value.trim())) {
            edt.setText(value.trim());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title bar
    }

    protected void showProgressDialog() {
        parentActivity.showProgressDialog();
    }

    protected void hideProgressDialog() {
        parentActivity.hideProgressDialog();
    }

    protected void showToastMessage(String msg) {
        parentActivity.showToastMessage(msg);
    }

    protected void showToastMessage(int msgId) {
        parentActivity.showToastMessage(msgId);
    }

    protected void showAlert(String msg) {
        parentActivity.showAlert(msg);
    }

    protected void showAlert(String msg, View.OnClickListener listener) {
        parentActivity.showAlert(msg, listener);
    }

    protected void showAlert(int msgId) {
        parentActivity.showAlert(msgId);
    }

    // Remove EditText Keyboard
    protected void hideKeyboard(EditText et) {
        if (et != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        }
    }

    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    // This will be used in Android6.0(Marshmallow) or above
    public static boolean checkPermissions(Context context, String[] permissions, boolean showHintMessage, int requestCode) {

        if (permissions == null || permissions.length == 0)
            return true;

        boolean allPermissionSetted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionSetted = false;
                break;
            }
        }

        if (allPermissionSetted)
            return true;

        // Should we show an explanation?
        boolean shouldShowRequestPermissionRationale = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                shouldShowRequestPermissionRationale = true;
                break;
            }
        }

        if (showHintMessage && shouldShowRequestPermissionRationale) {
            // Show an expanation to the user *asynchronously* -- don't
            // block
            // this thread waiting for the user's response! After the
            // user
            // sees the explanation, try again to request the
            // permission.
            String strPermissionHint = context.getString(R.string.request_permission_hint);
            Toast.makeText(context, strPermissionHint, Toast.LENGTH_SHORT).show();
        }

        ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);

        return false;
    }

}
