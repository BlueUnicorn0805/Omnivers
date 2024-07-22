package hawaiiappbuilders.omniversapp;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.adapters.FriendsAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.interfaces.HttpInterface;
import hawaiiappbuilders.omniversapp.model.Friends;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by RahulAnsari on 26-09-2018.
 */

public class ActivityContacts extends BaseActivity implements HttpInterface {

    private static final String TAG = ActivityContacts.class.getSimpleName();
    private RecyclerView mFriendsRecyclerView;
    private TextView mEmptyList;
    private Context mContext;
    ArrayList<Friends> mFriends;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friends);
        initView();
    }

    private void initView() {
        this.mContext = this;
        this.mFriends = Friends.getFriendList();
        mFriendsRecyclerView = (RecyclerView) findViewById(R.id.friends_recycler_view);
        mEmptyList = (TextView) findViewById(R.id.emptyList);
        setUpRecyclerView(mFriends);
    }

    private void setUpRecyclerView(ArrayList<Friends> friends) {
        mFriendsRecyclerView.setHasFixedSize(true);
        mFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        FriendsAdapter adapter = new FriendsAdapter(mContext, friends, true, new FriendsAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Friends friend = mFriends.get(position);
                showAlertDialog(friend);
            }
        });
        mFriendsRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccess(String message) {
        if (message != null) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                boolean status = jsonObject.getBoolean("status");
                if (!status) {
                    hideProgressDlg();
                    mFriendsRecyclerView.setVisibility(View.GONE);
                    mEmptyList.setVisibility(View.VISIBLE);
                    showMessage(ActivityContacts.this, jsonObject.getString("message"));
                } else {
                    if (status && jsonObject.getJSONArray("data").length() == 0) {
                        mEmptyList.setVisibility(View.GONE);
                        mEmptyList.setVisibility(View.VISIBLE);
                    } else {
                        mEmptyList.setVisibility(View.GONE);
                        mFriendsRecyclerView.setVisibility(View.VISIBLE);

                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            Friends friend = new Friends();

                            friend.setFriendID(data.getString("FriendID"));
                            friend.setNick(data.getString("Nick"));
                            friend.setEmail(data.getString("Email"));
                            friend.setCP(data.getString("CP"));

                            mFriends.add(friend);
                        }
                    }
                    hideProgressDlg();
                }
            } catch (JSONException e) {
                hideProgressDlg();
                mFriendsRecyclerView.setVisibility(View.GONE);
                mEmptyList.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
        }
        hideProgressDlg();
    }

    private boolean showAlertDialog(final Friends friend) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_alert_dialog, null);
        final TextView dialog_title = alertLayout.findViewById(R.id.dialog_title);
        dialog_title.setVisibility(View.GONE);
        final EditText pin = alertLayout.findViewById(R.id.pin);
        pin.setHint("Enter Nick Name");
        pin.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        final EditText cpin = alertLayout.findViewById(R.id.pin_confirm);
        final ImageView grey_line = alertLayout.findViewById(R.id.grey_line);
        cpin.setVisibility(View.GONE);
        grey_line.setVisibility(View.GONE);

        final Button submit = alertLayout.findViewById(R.id.pin_submit);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(true);
        final AlertDialog dialog = alert.create();
        dialog.show();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pinNumber = pin.getText().toString().trim();
                if (!pinNumber.trim().isEmpty()) {
                    dialog.dismiss();
                    hideKeyboard();
                   /* showProgressDlg(ActivityContacts.this, "Adding Friend");
                    saveNickName(ActivityContacts.this, getUserLat(), getUserLon(), pinNumber,
                            (friend.getEmail() != null && !friend.getEmail().isEmpty()) ? friend.getEmail() : "",
                            friend.getCP(), new HttpInterface() {
                                @Override
                                public void onSuccess(String message) {
                                    hideProgressDlg();
                                    Intent intent = new Intent();
                                    intent.putExtra("FriendID", friend.getFriendID());
                                    intent.putExtra("Nick", pinNumber);
                                    intent.putExtra("Email", friend.getEmail());
                                    intent.putExtra("CP", friend.getCP());
                                    setResult(1011, intent);
                                    finish();
                                }
                            });*/


                } else {
                    pin.setError("Required");
                }
            }
        });
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigateToLoginIfUserIsLoggedOut();
    }
}
