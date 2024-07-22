package hawaiiappbuilders.omniversapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.adapters.GroupContactAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.GroupInfo;


public class ActivityManageGroups extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityManageGroups.class.getSimpleName();
    MessageDataManager dm;

    Spinner spinnerGroups;
    ArrayList<GroupInfo> groupInfos = new ArrayList<>();
    ArrayAdapter groupAdapter;

    RecyclerView rcvContacts;
    ArrayList<ContactInfo> contactInfos = new ArrayList<>();
    GroupContactAdapter groupContactAdapter;

    View btnAddContact;

    private static final int REQUEST_UPDATE_CONTACT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_groups);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_manage_groups);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);

        dm = new MessageDataManager(mContext);

        spinnerGroups = findViewById(R.id.spinnerGroups);
        groupAdapter = new ArrayAdapter<GroupInfo>(mContext, R.layout.layout_spinner_filter, groupInfos);
        groupAdapter.setDropDownViewResource(R.layout.layout_spinner_filter);
        spinnerGroups.setAdapter(groupAdapter);
        spinnerGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateGroupContactList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        findViewById(R.id.btnAddNewGroup).setOnClickListener(this);

        rcvContacts = findViewById(R.id.rcvContacts);
        rcvContacts.setHasFixedSize(true);
        rcvContacts.setLayoutManager(new LinearLayoutManager(mContext));
        groupContactAdapter = new GroupContactAdapter(mContext, contactInfos, new GroupContactAdapter.GroupContactItemListener() {
            @Override
            public void onItemClicked(int position) {
                ContactInfo contactInfo = contactInfos.get(position);
                GroupInfo selectedGroupInfo = groupInfos.get(spinnerGroups.getSelectedItemPosition());
                Intent intent = new Intent(mContext, ActivityGroupsAddContact.class);
                intent.putExtra("group_info", selectedGroupInfo);
                intent.putExtra("contact_info", contactInfo);
                startActivityForResult(intent, REQUEST_UPDATE_CONTACT);
            }
        });
        rcvContacts.setAdapter(groupContactAdapter);

        btnAddContact = findViewById(R.id.btnAddContact);
        btnAddContact.setOnClickListener(this);

        findViewById(R.id.btnSkip).setOnClickListener(this);

        updateGroupInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateGroupInfo() {

        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGrpname("Choose here");

        groupInfos.clear();
        groupInfos.add(groupInfo);

        groupInfo = new GroupInfo();
        groupInfo.setGrpname("Add New Group");
        groupInfos.add(groupInfo);

        groupInfos.addAll(dm.getAlLUserGroups());

        groupAdapter.notifyDataSetChanged();
    }

    private void updateGroupContactList() {
        contactInfos.clear();
        if (spinnerGroups.getSelectedItemPosition() > 0) {
            if (spinnerGroups.getSelectedItemPosition() == 1) {
                addNewGroup();
            } else {
                GroupInfo selectedGroupInfo = groupInfos.get(spinnerGroups.getSelectedItemPosition());
                if (selectedGroupInfo.getId() > 0) {
                    contactInfos.addAll(dm.getContacts(selectedGroupInfo.getId()));
                }
            }
        }

        // Show Hide Action
        if (spinnerGroups.getSelectedItemPosition() > 1) {
            btnAddContact.setVisibility(View.VISIBLE);
        } else {
            btnAddContact.setVisibility(View.GONE);
        }

        groupContactAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnBack) {
            finish();
        } else if(viewId == R.id.btnToolbarHome) {
         backToHome();
        }else if (viewId == R.id.btnAddNewGroup) {
            addNewGroup();
        } else if (viewId == R.id.btnAddContact) {
            GroupInfo selectedGroupInfo = groupInfos.get(spinnerGroups.getSelectedItemPosition());
            if (selectedGroupInfo.getId() > 0) {
                Intent intent = new Intent(mContext, ActivityGroupsAddContact.class);
                intent.putExtra("group_info", selectedGroupInfo);
                startActivityForResult(intent, REQUEST_UPDATE_CONTACT);
            } else {
                showToastMessage("Please choose group");
            }
        } else if (viewId == R.id.btnSkip) {
            finish();
        }
    }

    private void addNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Group Name");

        // Set up the input
        final EditText input = new EditText(this);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString().trim();
                if (TextUtils.isEmpty(m_Text)) {
                    showToastMessage("Please input group name");
                } else {
                    GroupInfo groupInfo = new GroupInfo();
                    groupInfo.setGrpname(m_Text);

                    dm.addGroup(groupInfo);

                    groupInfos.add(groupInfo);
                    groupAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPDATE_CONTACT && resultCode == RESULT_OK) {
            updateGroupContactList();
        }
    }
}
