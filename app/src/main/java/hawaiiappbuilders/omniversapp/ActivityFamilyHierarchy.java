package hawaiiappbuilders.omniversapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.limits.ActivityLimits;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.ViewUtil;

public class ActivityFamilyHierarchy extends BaseActivity {
    public static final String TAG = ActivityFamilyHierarchy.class.getSimpleName();
    Context mContext;

    TreeView recycler;

    AppSettings appSettings;

    TreeNode rootNode;

    Button addFamilyMember;
    MessageDataManager dm;
    int parentIdSelected;

    String titleSelected;

    int currentPosition;

    Calendar calendarDate = Calendar.getInstance();

    ArrayList<FamilyMember> myFamilyMembers;

    FamilyMember userFamilyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_hierarchy);
        mContext = this;
        dm = new MessageDataManager(mContext);
        userFamilyData = new FamilyMember();

        myFamilyMembers = new ArrayList<>();
        appSettings = new AppSettings(mContext);
        recycler = findViewById(R.id.id_tree);
        ((TextView) findViewById(R.id.text_family_name)).setText(String.format("%s%s", appSettings.getLN(), " Family"));
        addFamilyMember = findViewById(R.id.btn_add_family);
        addFamilyMember.setVisibility(View.GONE);
        setAdapter();
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        myFamilyMembers.clear();
        setAdapter();
    }

    public void showAddFamilyButtons(FamilyMember member) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_family_member_buttons, null);

        Button addSpouse = dialogView.findViewById(R.id.btn_add_spouse);
        Button addDaughter = dialogView.findViewById(R.id.btn_add_daughter);
        Button addSon = dialogView.findViewById(R.id.btn_add_son);
        Button addStepDaughter = dialogView.findViewById(R.id.btn_add_step_daughter);
        Button addStepSon = dialogView.findViewById(R.id.btn_add_step_son);
        Button addGuardian = dialogView.findViewById(R.id.btn_add_guardian);
        Button updateAvatar = dialogView.findViewById(R.id.btn_update_avatar);
        Button updateData = dialogView.findViewById(R.id.btn_update_data);
        Button close = dialogView.findViewById(R.id.btnCancel);

        AlertDialog alert = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(true).create();
        alert.show();

        addGuardian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFamilyMember("Guardian", member);
                alert.dismiss();
            }
        });

        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFamilyMember(member);
                alert.dismiss();
            }
        });

        addStepDaughter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFamilyMember("Step Daughter", member);
                alert.dismiss();
            }
        });

        addStepSon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFamilyMember("Step Son", member);
                alert.dismiss();
            }
        });

        addSpouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFamilyMember("Spouse", member);
                alert.dismiss();
            }
        });

        addDaughter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFamilyMember("Daughter", member);
                alert.dismiss();
            }
        });

        addSon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFamilyMember("Son", member);
                alert.dismiss();
            }
        });

        updateAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGalleryIntent(member);
                alert.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });


    }

    public static String[] prepend(String[] a, String el) {
        String[] c = new String[a.length + 1];
        c[0] = el;
        System.arraycopy(a, 0, c, 1, a.length);
        return c;
    }

    public void updateFamilyMember(FamilyMember member) {

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_user_family_member, null);
        TextView textTitle = dialogView.findViewById(R.id.text_title);
        Spinner spinnerTitle = dialogView.findViewById(R.id.spinner_titles);
        EditText edtFirstName = dialogView.findViewById(R.id.edt_first_name);
        EditText edtLastName = dialogView.findViewById(R.id.edt_last_name);
        EditText edtBirthdate = dialogView.findViewById(R.id.edtBirthdate);
        Button updateFamilyBtn = dialogView.findViewById(R.id.btn_add);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        updateFamilyBtn.setText("Update");
        ViewUtil.setGone(textTitle);
        ViewUtil.setGone(spinnerTitle);

        edtFirstName.setText(member.getFirstName());
        edtLastName.setText(member.getLastName());
        edtBirthdate.setText(member.getBirthdate());
        edtBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(mContext,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                        calendarDate.set(Calendar.YEAR, year);
                                        calendarDate.set(Calendar.MONTH, monthOfYear);
                                        calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        String strDate = DateUtil.toStringFormat_13(calendarDate.getTime());
                                        edtBirthdate.setText(strDate);
                                    }
                                },
                                calendarDate.get(Calendar.YEAR),
                                calendarDate.get(Calendar.MONTH),
                                calendarDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        AlertDialog alert = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(true).create();
        alert.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        updateFamilyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dm = new MessageDataManager(mContext);

                if (edtFirstName.getText().toString().isEmpty()) {
                    showToastMessage("Add firstname");
                    return;
                }

                if (edtBirthdate.getText().toString().isEmpty()) {
                    showToastMessage("Enter birthdate");
                    return;
                }

                member.setFirstName(edtFirstName.getText().toString());
                member.setLastName(edtLastName.getText().toString());
                member.setBirthdate(edtBirthdate.getText().toString());

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executor.execute(() -> {
                    dm.updateFamilyMember(member);
                    handler.post(() -> {
                        hideProgressDialog();
                        myFamilyMembers.clear();
                        setAdapter();
                    });
                });

                alert.dismiss();
            }
        });
    }

    public void addFamilyMember(String title, FamilyMember member) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_user_family_member, null);
        Spinner spinnerTitle = dialogView.findViewById(R.id.spinner_titles);
        EditText edtFirstName = dialogView.findViewById(R.id.edt_first_name);
        EditText edtLastName = dialogView.findViewById(R.id.edt_last_name);
        EditText edtBirthdate = dialogView.findViewById(R.id.edtBirthdate);
        Button addFamilyBtn = dialogView.findViewById(R.id.btn_add);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        String[] titles = mContext.getResources().getStringArray(R.array.spinner_family_member_titles);
        String[] newTitles = prepend(titles, "Please Select Title");
        int indexSelected = 0;
        if (!title.isEmpty()) {
            indexSelected = Arrays.asList(titles).indexOf(title);
            titleSelected = title;
            spinnerTitle.setEnabled(false);
        } else {
            spinnerTitle.setEnabled(true);
        }

        ArrayAdapter<String> titlesAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_list_item, newTitles);
        spinnerTitle.setAdapter(titlesAdapter);
        spinnerTitle.setSelection(indexSelected + 1);
        spinnerTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // titleSelected = titles[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(mContext,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                        calendarDate.set(Calendar.YEAR, year);
                                        calendarDate.set(Calendar.MONTH, monthOfYear);
                                        calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        String strDate = DateUtil.toStringFormat_13(calendarDate.getTime());
                                        edtBirthdate.setText(strDate);
                                    }
                                },
                                calendarDate.get(Calendar.YEAR),
                                calendarDate.get(Calendar.MONTH),
                                calendarDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });


        AlertDialog alert = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(true).create();
        alert.show();
        if (member.getTitle().contentEquals("Guardian") || member.getTitle().contentEquals("Spouse")) {

        } else {
            edtLastName.setText(member.getLastName());
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        addFamilyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dm = new MessageDataManager(mContext);

                FamilyMember newFamilyMember = new FamilyMember();

                if (edtFirstName.getText().toString().isEmpty()) {
                    showToastMessage("Add firstname");
                    return;
                }

                if (edtBirthdate.getText().toString().isEmpty()) {
                    showToastMessage("Enter birthdate");
                    return;
                }

                newFamilyMember.setFirstName(edtFirstName.getText().toString());
                newFamilyMember.setLastName(edtLastName.getText().toString());
                newFamilyMember.setAvatarImg("");
                newFamilyMember.setBirthdate(edtBirthdate.getText().toString());
                newFamilyMember.setTitleId(getTitleId(title));
                newFamilyMember.setTitle(title);
                long id = dm.addFamilyMember(newFamilyMember);
                newFamilyMember.setId(id);

                String childrenString = member.getChildren();
                Type type = new TypeToken<ArrayList<FamilyMember>>() {
                }.getType();
                ArrayList<FamilyMember> children = new Gson().fromJson(childrenString, type);

                if (children != null && children.size() > 0) {
                    children.add(newFamilyMember);
                    member.setChildren(new Gson().toJson(children));
                } else {
                    ArrayList<FamilyMember> newChildren = new ArrayList<>();
                    newChildren.add(newFamilyMember);
                    member.setChildren(new Gson().toJson(newChildren));
                }

                dm.updateFamilyMember(member);

                /*switch (title) {
                    case "Step Daughter":
                    case "Step Son":
                    case "Daughter":
                    case "Son":
                        Child child = new Child();
                        if (userFamilyData.getSpouseId() != null && userFamilyData.getSpouseId() != 0) {
                            child.setMomId(userFamilyData.getSpouseId());
                        } else {
                            child.setMomId(0L);
                        }
                        child.setDadId(appSettings.getFamilyUserId());
                        child.setChildId(id);
                        dm.addChild(child);
                        // showToastMessage("Child added");
                        break;
                    case "Guardian":
                        // showToastMessage("Added Guardian");
                        break;
                    case "Spouse":
                        break;
                }*/
                alert.dismiss();
                setAdapter();
            }
        });
    }

    public void setAdapter() {
        myFamilyMembers.addAll(dm.getFamilyMembers());
        currentPosition = myFamilyMembers.size();
        initializeFamilyHierarchyView();
    }

    public void initializeFamilyHierarchyView() {

        if (appSettings.getFamilyUserId() == 0) {
            // TODO: Create user for family
            userFamilyData.setTitle("root");
            userFamilyData.setFirstName(appSettings.getFN());
            userFamilyData.setLastName(appSettings.getLN());
            userFamilyData.setAvatarImg("");
            userFamilyData.setTitleId(0);
            userFamilyData.setMomId(0L);
            userFamilyData.setDadId(0L);
            userFamilyData.setSpouseId(0L);
            userFamilyData.setBirthdate(appSettings.getDOB());
            long userFamilyId = dm.addFamilyMember(userFamilyData);
            appSettings.setFamilyUserId(userFamilyId);
        } else {
            // TODO: Load user family hierarchy
            long userFamilyId = appSettings.getFamilyUserId();
            userFamilyData = dm.getFamilyMemberById(userFamilyId);
        }

        BaseTreeAdapter<FamilyMemberViewHolder> adapter = new BaseTreeAdapter<FamilyMemberViewHolder>(this, R.layout.item_family_member) {
            @NonNull
            @Override
            public FamilyMemberViewHolder onCreateViewHolder(View view) {
                return new FamilyMemberViewHolder(view);
            }

            @Override
            public void onBindViewHolder(FamilyMemberViewHolder viewHolder, Object data, int position) {
                FamilyMember member = (FamilyMember) data;

                if (member != null) {
                    int age = 0;
                    if (member.getBirthdate() != null) {
                        age = DataUtil.calculateAge(member.getBirthdate());
                    }

                    // name
                    String fullNameText = String.format("%s %s", member.getFirstName(), member.getLastName());
                    ((TextView) viewHolder.itemView.findViewById(R.id.text_name)).setText(fullNameText);

                    // avatar
                    if (member.getAvatarImg() != null && !member.getAvatarImg().isEmpty()) {
                        ImageView avatar = viewHolder.itemView.findViewById(R.id.iv_avatar);
                        try {
                            if (MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(member.getAvatarImg())) != null) {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(member.getAvatarImg()));
                                avatar.setImageBitmap(bitmap);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }

                    // title
                    TextView title = viewHolder.itemView.findViewById(R.id.text_title);
                    if (member.getTitle() != null && member.getTitle().contentEquals("root")) {
                        String titleRoot = member.getBirthdate() + " \n" + DataUtil.formatAgeDisplay(age);
                        title.setText(titleRoot);
                    } else {
                        String titleMember = member.getTitle() + "\n" + member.getBirthdate() + "\n" + DataUtil.formatAgeDisplay(age);
                        title.setText(titleMember);
                    }

                    // item layout
                    viewHolder.itemView.findViewById(R.id.card_family_member).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Intent intent = new Intent(mContext, ActivityLimits.class);
                            intent.putExtra("member", member);
                            intent.putExtra("fn", appSettings.getFN());
                            startActivity(intent);
                            return true;
                        }
                    });

                    viewHolder.itemView.findViewById(R.id.card_family_member).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // launchGalleryIntent();
                            showAddFamilyButtons(member);
                        }
                    });
                }
            }
        };

        // Todo: Load root data
        rootNode = new TreeNode(userFamilyData);
        addNode(rootNode, userFamilyData);
        adapter.setRootNode(rootNode);

        recycler.setAdapter(adapter);
    }

    public void addNode(TreeNode rootNode, FamilyMember curr) {
        if (haveChildren(curr)) { // root have children
            ArrayList<FamilyMember> children = getChildren(curr);
            for (int i = 0; i < children.size(); i++) {
                FamilyMember child = dm.getFamilyMemberById(children.get(i).getId()); // root child item
                TreeNode childNode = new TreeNode(child); // create node for child item
                rootNode.addChild(childNode);
                if(haveChildren(child)) { // check if child item have children before adding it to root
                    addChild(childNode, child);
                }
            }
        }
    }

    public void addChild(TreeNode childNode, FamilyMember child) {
        ArrayList<FamilyMember> children = getChildren(child);
        for (int i = 0; i < children.size(); i++) {
            FamilyMember childItem = dm.getFamilyMemberById(children.get(i).getId()); // root child item
            TreeNode childNodeItem = new TreeNode(childItem); // create node for child item
            childNode.addChild(childNodeItem);
            if(haveChildren(childItem)) { // check if child item have children before adding it to root
                addChild(childNodeItem, childItem);
            }
        }
    }

    public boolean haveChildren(FamilyMember node) {
        String childrenString = node.getChildren();
        Type type = new TypeToken<ArrayList<FamilyMember>>() {
        }.getType();
        ArrayList<FamilyMember> children = new Gson().fromJson(childrenString, type);
        return children != null && children.size() > 0;
    }

    public ArrayList<FamilyMember> getChildren(FamilyMember node) {
        String childrenString = node.getChildren();
        Type type = new TypeToken<ArrayList<FamilyMember>>() {
        }.getType();
        return new Gson().fromJson(childrenString, type);
    }

    private void launchGalleryIntent(FamilyMember member) {
        Intent intent = new Intent(mContext, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        intent.putExtra("member", new Gson().toJson(member));
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                FamilyMember member = new Gson().fromJson(data.getStringExtra("member"), FamilyMember.class);
                String title = member.getTitle();
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    member.setAvatarImg(uri.toString());
                    if (title.contentEquals("root")) {
                        appSettings.setAvatarImage(uri.toString());
                    }
                    showProgressDialog();
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());
                    executor.execute(() -> {
                        dm.updateFamilyMember(member);
                        handler.post(() -> {
                            hideProgressDialog();
                            myFamilyMembers.clear();
                            setAdapter();
                        });
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isParent() {
        for (int i = 0; i < myFamilyMembers.size(); i++) {
            if (myFamilyMembers.get(i).title.contentEquals("Son") || myFamilyMembers.get(i).title.contentEquals("Daughter")) {
                return true;
            }
        }
        return false;
    }

    public int getTitleId(String title) {
        int titleId = 0;
        switch (title) {
            case "Spouse":
                titleId = 322; // ???
                break;
            /*case "Mom":
                titleId = 300;
                break;
            case "Dad":
                titleId = 305;
                break;*/
            case "Daughter":
                titleId = 307;
                break;
            case "Son":
                titleId = 308;
                break;
            case "Step Daughter":
                titleId = 309;
                break;
            case "Step Son":
                titleId = 310;
                break;
            /*case "Uncle":
                titleId = 311;
                break;
            case "Aunt":
                titleId = 312;
                break;
            case "Brother":
                titleId = 313;
                break;
            case "Sister":
                titleId = 314;
                break;
            case "Step Mom":
                titleId = 315;
                break;
            case "Step Dad":
                titleId = 316;
                break;
            case "Husband":
                titleId = 317;
                break;
            case "Wife":
                titleId = 318;
                break;
            case "xWife":
                titleId = 319;
                break;
            case "xHusband":
                titleId = 320;
                break;
            case "Guardian":
                titleId = 321;
                break;*/
        }
        return titleId;
    }

    public static class Child {
        int id;
        Long childId;
        Long momId;
        Long dadId;

        public Child() {
        }

        public Child(int id, Long childId, Long momId, Long dadId) {
            this.id = id;
            this.childId = childId;
            this.momId = momId;
            this.dadId = dadId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Long getChildId() {
            return childId;
        }

        public void setChildId(Long childId) {
            this.childId = childId;
        }

        public Long getMomId() {
            return momId;
        }

        public void setMomId(Long momId) {
            this.momId = momId;
        }

        public Long getDadId() {
            return dadId;
        }

        public void setDadId(Long dadId) {
            this.dadId = dadId;
        }
    }

    public static class FamilyMember implements Parcelable {
        long id;
        String firstName;

        String lastName;
        String birthdate;

        String avatarImg;

        String title;

        Long momId;

        Long dadId;

        Long spouseId;
        String settings;

        int titleId;

        String children;

        public FamilyMember() {

        }

        public FamilyMember(int id, String firstName, String lastName, String birthdate, String avatarImg, String title, Long momId, Long dadId, Long spouseId, String settings, int titleId, String children) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthdate = birthdate;
            this.avatarImg = avatarImg;
            this.title = title;
            this.momId = momId;
            this.dadId = dadId;
            this.spouseId = spouseId;
            this.settings = settings;
            this.titleId = titleId;
            this.children = children;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = birthdate;
        }

        public String getAvatarImg() {
            return avatarImg;
        }

        public void setAvatarImg(String avatarImg) {
            this.avatarImg = avatarImg;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Long getMomId() {
            return momId;
        }

        public void setMomId(Long momId) {
            this.momId = momId;
        }

        public Long getDadId() {
            return dadId;
        }

        public void setDadId(Long dadId) {
            this.dadId = dadId;
        }

        public Long getSpouseId() {
            return spouseId;
        }

        public void setSpouseId(Long spouseId) {
            this.spouseId = spouseId;
        }

        public String getSettings() {
            return settings;
        }

        public void setSettings(String settings) {
            this.settings = settings;
        }

        public int getTitleId() {
            return titleId;
        }

        public void setTitleId(int titleId) {
            this.titleId = titleId;
        }

        public String getChildren() {
            return children;
        }

        public void setChildren(String children) {
            this.children = children;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(firstName);
            dest.writeString(lastName);
            dest.writeString(birthdate);
            dest.writeString(avatarImg);
            dest.writeString(title);
            dest.writeLong(momId);
            dest.writeLong(dadId);
            dest.writeLong(spouseId);
            dest.writeString(settings);
            dest.writeInt(titleId);
            dest.writeString(children);
        }

        @SuppressWarnings("unchecked")
        protected FamilyMember(Parcel in) {
            id = in.readLong();
            firstName = in.readString();
            lastName = in.readString();
            birthdate = in.readString();
            avatarImg = in.readString();
            title = in.readString();
            momId = in.readLong();
            dadId = in.readLong();
            spouseId = in.readLong();
            settings = in.readString();
            titleId = in.readInt();
            children = in.readString();
        }

        public static final Creator<FamilyMember> CREATOR = new Creator<FamilyMember>() {
            @Override
            public FamilyMember createFromParcel(Parcel in) {
                return new FamilyMember(in);
            }

            @Override
            public FamilyMember[] newArray(int size) {
                return new FamilyMember[size];
            }
        };
    }


    public static class FamilyMemberViewHolder extends RecyclerView.ViewHolder {

        public CardView layoutFamilyMember;
        public TextView tvName;
        public ImageView avatarImage;

        public TextView title;

        public FamilyMemberViewHolder(View itemView) {
            super(itemView);
            layoutFamilyMember = itemView.findViewById(R.id.card_family_member);
            tvName = itemView.findViewById(R.id.tv_name);
            avatarImage = itemView.findViewById(R.id.ivAvatar);
            title = itemView.findViewById(R.id.text_title);
        }
    }
}
