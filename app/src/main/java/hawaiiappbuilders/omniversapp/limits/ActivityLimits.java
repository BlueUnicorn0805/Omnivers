package hawaiiappbuilders.omniversapp.limits;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hawaiiappbuilders.omniversapp.ActivityFamilyHierarchy;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.utils.DataUtil;

public class ActivityLimits extends BaseActivity {
    public static final String TAG = ActivityLimits.class.getSimpleName();
    LinearLayout col1;
    LinearLayout col2;
    Context mContext;

    public String[] titles;

    TextView textBirthdayActual;
    TextView textBirthdayCountdown;

    TextView textYouAre;

    TextView titleText;
    TextView subtitleText;

    TextView descText;


    ArrayList<CheckBoxAdapter.CheckBoxData> familyMemberSettings;
    MessageDataManager dm;

    ActivityFamilyHierarchy.FamilyMember member;

    public class AgeModel {
        int years;
        int months;
        int days;

        public AgeModel() {

        }

        public AgeModel(int years, int months, int days) {
            this.years = years;
            this.months = months;
            this.days = days;
        }

        public int getYears() {
            return years;
        }

        public void setYears(int years) {
            this.years = years;
        }

        public int getMonths() {
            return months;
        }

        public void setMonths(int months) {
            this.months = months;
        }

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limits);
        col1 = findViewById(R.id.layout_dynamic1);
        col2 = findViewById(R.id.layout_dynamic2);
        titleText = findViewById(R.id.text_first_name);
        subtitleText = findViewById(R.id.text_full_name);
        textBirthdayActual = findViewById(R.id.text_birthday_actual);
        textBirthdayCountdown = findViewById(R.id.text_birthday_countdown);
        textYouAre = findViewById(R.id.text_you_are);
        descText = findViewById(R.id.text_desc);
        titles = new String[]{
                "Account for your family",
                "Enforce Limits\n",
                "Reports/Graphs\n",
                "Financial\n",
                "Just Died\n",
                "Learn Financial Strategies",
        };
        mContext = this;
        dm = new MessageDataManager(mContext);
        familyMemberSettings = new ArrayList<>();

        if (getIntent().getExtras() != null) {
            member = getIntent().getExtras().getParcelable("member");
            titleText.setText(String.format("%s's Family", getIntent().getExtras().getString("fn")));

            int age = 0;
            if (member.getBirthdate() != null) {
                age = DataUtil.calculateAge(member.getBirthdate());
                textBirthdayActual.setText(DataUtil.getBirthdaySummary(member.getBirthdate()));
                textBirthdayCountdown.setText(String.valueOf(DataUtil.getNextBirthdayInDays(member.getBirthdate())));
            }

            subtitleText.setText(String.format("%s %s\n%s\n%s", member.getFirstName(), member.getLastName(), member.getBirthdate(), DataUtil.formatAgeDisplay(age)));

            String title;
            if (member.getTitle().contentEquals("Guardian")) {
                textYouAre.setText("You guardian is:");
                title = "Features for Guardian";
                descText.setText(title);
            } else if (member.getTitle().contentEquals("root")) {
                title = "Features for You";
                textYouAre.setText("You are:");
                descText.setText(title);
            }  else if (member.getTitle().contentEquals("Spouse")) {
                title = "Features for your Spouse";
                textYouAre.setText("Your spouse is:");
                descText.setText(title);
            } else {
                title = "Features for your Child";
                textYouAre.setText("Your child is:");
                descText.setText(title);
            }

            if (member.getSettings() == null) {
                initializeSettings();
                new AlertDialog.Builder(mContext).setTitle("Features")
                        .setMessage("Click Okay to load features")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadData();
                            }
                        }).show();
            } else {
                loadData();
            }
        }
    }

    private void loadData() {
        String allSettings = member.getSettings();
        Type type = new TypeToken<ArrayList<CheckBoxAdapter.CheckBoxData>>() {
        }.getType();
        familyMemberSettings = new Gson().fromJson(allSettings, type);
        initializeData();
    }

    private void initializeSettings() {
        ArrayList<CheckBoxAdapter.CheckBoxData> settings = new ArrayList<>();
        settings.add(new CheckBoxAdapter.CheckBoxData(1, 0, "College", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(2, 0, "Retire", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(3, 0, "Wedding", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(4, 0, "Insurance", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(5, 0, "Tip(%,$)", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(6, 0, "Savings", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(7, 0, "Home Buying", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(8, 1, "$/Transaction", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(9, 1, "$/Day", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(10, 1, "$/Week", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(11, 1, "Searches", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(12, 1, "Contact", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(13, 1, "Age", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(14, 1, "Drugs/Alcohol", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(15, 1, "Friends", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(16, 1, "Active", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(17, 5, "Deposit", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(18, 5, "Chores", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(19, 3, "Debt", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(20, 3, "Retirement", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(21, 3, "Budgeting", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(22, 3, "Taxes", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(23, 4, "Email Group", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(24, 4, "Donate Balance to Church", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(25, 2, "AAG", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(26, 2, "Graphs", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(27, 2, "Emergency", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(28, 2, "School Info", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(29, 2, "Swap", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(30, 5, "Investment Products", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(31, 5, "Planning for Retirement", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(32, 5, "Wealth Strategies", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(33, 5, "Education Savings", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(34, 5, "Planning your Estate", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(35, 5, "Savings, Cash & Credit", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(36, 5, "Insurance & Annuities", false));
        settings.add(new CheckBoxAdapter.CheckBoxData(37, 5, "Business Owner Solution", false));
        member.setSettings(new Gson().toJson(settings));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            dm.updateFamilyMember(member);
            handler.post(() -> {
            });
        });
    }

    private void initializeData() {
        ArrayList<CheckBoxAdapter.CheckBoxData> accounts = new ArrayList<>();
        accounts.add(new CheckBoxAdapter.CheckBoxData(1, 0, "College", familyMemberSettings.get(0).isChecked()));
        accounts.add(new CheckBoxAdapter.CheckBoxData(2, 0, "Retire", familyMemberSettings.get(1).isChecked()));
        accounts.add(new CheckBoxAdapter.CheckBoxData(3, 0, "Wedding", familyMemberSettings.get(2).isChecked()));
        accounts.add(new CheckBoxAdapter.CheckBoxData(4, 0, "Insurance", familyMemberSettings.get(3).isChecked()));
        accounts.add(new CheckBoxAdapter.CheckBoxData(5, 0, "Tip(%,$)", familyMemberSettings.get(4).isChecked()));
        accounts.add(new CheckBoxAdapter.CheckBoxData(6, 0, "Savings", familyMemberSettings.get(5).isChecked()));
        accounts.add(new CheckBoxAdapter.CheckBoxData(7, 0, "Home Buying", familyMemberSettings.get(6).isChecked()));
        addView(0, titles[0], R.drawable.cashcredit, accounts);

        ArrayList<CheckBoxAdapter.CheckBoxData> limits = new ArrayList<>();
        limits.add(new CheckBoxAdapter.CheckBoxData(8, 1, "$/Transaction", familyMemberSettings.get(7).isChecked()));
        limits.add(new CheckBoxAdapter.CheckBoxData(9, 1, "$/Day", familyMemberSettings.get(8).isChecked()));
        limits.add(new CheckBoxAdapter.CheckBoxData(10, 1, "$/Week", familyMemberSettings.get(9).isChecked()));
        limits.add(new CheckBoxAdapter.CheckBoxData(11, 1, "Searches", familyMemberSettings.get(10).isChecked()));
        limits.add(new CheckBoxAdapter.CheckBoxData(12, 1, "Contact", familyMemberSettings.get(11).isChecked()));
        limits.add(new CheckBoxAdapter.CheckBoxData(13, 1, "Age", familyMemberSettings.get(12).isChecked()));
        limits.add(new CheckBoxAdapter.CheckBoxData(14, 1, "Drugs/Alcohol", familyMemberSettings.get(13).isChecked()));
        limits.add(new CheckBoxAdapter.CheckBoxData(15, 1, "Friends", familyMemberSettings.get(14).isChecked()));
        limits.add(new CheckBoxAdapter.CheckBoxData(16, 1, "Active", familyMemberSettings.get(15).isChecked()));
        addView(1, titles[1], R.drawable.education, limits);

        ArrayList<CheckBoxAdapter.CheckBoxData> reports = new ArrayList<>();
        reports.add(new CheckBoxAdapter.CheckBoxData(25, 2, "AAG", familyMemberSettings.get(24).isChecked()));
        reports.add(new CheckBoxAdapter.CheckBoxData(26, 2, "Graphs", familyMemberSettings.get(25).isChecked()));
        reports.add(new CheckBoxAdapter.CheckBoxData(27, 2, "Emergency", familyMemberSettings.get(26).isChecked()));
        reports.add(new CheckBoxAdapter.CheckBoxData(28, 2, "School Info", familyMemberSettings.get(27).isChecked()));
        reports.add(new CheckBoxAdapter.CheckBoxData(29, 2, "Swap", familyMemberSettings.get(28).isChecked()));
        addView(2, titles[2], R.drawable.cashcredit, reports);


        ArrayList<CheckBoxAdapter.CheckBoxData> financial = new ArrayList<>();
        financial.add(new CheckBoxAdapter.CheckBoxData(19, 3, "Debt", familyMemberSettings.get(18).isChecked()));
        financial.add(new CheckBoxAdapter.CheckBoxData(20, 3, "Retirement", familyMemberSettings.get(19).isChecked()));
        financial.add(new CheckBoxAdapter.CheckBoxData(21, 3, "Budgeting", familyMemberSettings.get(20).isChecked()));
        financial.add(new CheckBoxAdapter.CheckBoxData(22, 3, "Taxes", familyMemberSettings.get(21).isChecked()));
        addView(3, titles[3], R.drawable.investmentproduct, financial);

        ArrayList<CheckBoxAdapter.CheckBoxData> justDied = new ArrayList<>();
        justDied.add(new CheckBoxAdapter.CheckBoxData(23, 4, "Email Group", familyMemberSettings.get(22).isChecked()));
        justDied.add(new CheckBoxAdapter.CheckBoxData(24, 4, "Donate Balance to Church", familyMemberSettings.get(23).isChecked()));
        addView(4, titles[4], R.drawable.retirement, justDied);

        ArrayList<CheckBoxAdapter.CheckBoxData> learnIncome = new ArrayList<>();
        learnIncome.add(new CheckBoxAdapter.CheckBoxData(17, 5, "Deposit", familyMemberSettings.get(16).isChecked()));
        learnIncome.add(new CheckBoxAdapter.CheckBoxData(18, 5, "Chores", familyMemberSettings.get(17).isChecked()));
        learnIncome.add(new CheckBoxAdapter.CheckBoxData(30, 5, "Investment Products", familyMemberSettings.get(29).isChecked()));
        learnIncome.add(new CheckBoxAdapter.CheckBoxData(31, 5, "Planning for Retirement", familyMemberSettings.get(30).isChecked()));
        learnIncome.add(new CheckBoxAdapter.CheckBoxData(32, 5, "Wealth Strategies", familyMemberSettings.get(31).isChecked()));
        learnIncome.add(new CheckBoxAdapter.CheckBoxData(33, 5, "Education Savings", familyMemberSettings.get(32).isChecked()));
        learnIncome.add(new CheckBoxAdapter.CheckBoxData(34, 5, "Planning your Estate", familyMemberSettings.get(33).isChecked()));
        learnIncome.add(new CheckBoxAdapter.CheckBoxData(35, 5, "Savings, Cash & Credit", familyMemberSettings.get(34).isChecked()));
        learnIncome.add(new CheckBoxAdapter.CheckBoxData(36, 5, "Insurance & Annuities", familyMemberSettings.get(35).isChecked()));
        learnIncome.add(new CheckBoxAdapter.CheckBoxData(37, 5, "Business Owner Solution", familyMemberSettings.get(36).isChecked()));
        addView(5, titles[5], R.drawable.insuranceannuities, learnIncome);
    }

    private void addView(int position, String title, int drawableResource, ArrayList<CheckBoxAdapter.CheckBoxData> checkboxes) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.layout_group_checkbox, null, false);

        // Title
        TextView groupName = layout.findViewById(R.id.text_group_name);
        groupName.setText(title);

        ImageView imageView = layout.findViewById(R.id.imageView8);
        imageView.setBackground(AppCompatResources.getDrawable(mContext, drawableResource));

        // Checkboxes
        RecyclerView recyclerView = layout.findViewById(R.id.rv_checkboxes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CheckBoxAdapter adapter = new CheckBoxAdapter(mContext, checkboxes, new CheckBoxAdapter.OnCheckListener() {
            @Override
            public void onCheckedItem(CheckBoxAdapter.CheckBoxData data, boolean isChecked) {
                int index = getIndex(data.getId());
                if (index != -1) {
                    familyMemberSettings.get(index).setChecked(isChecked);
                    member.setSettings(new Gson().toJson(familyMemberSettings));
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());
                    executor.execute(() -> {
                        dm.updateFamilyMember(member);
                        handler.post(() -> {

                        });
                    });
                }
            }
        });
        recyclerView.setAdapter(adapter);
        if (position % 2 == 0) {
            col1.addView(layout);
        } else {
            col2.addView(layout);
        }
    }

    private int getIndex(int uniqueId) {
        for (int i = 0; i < familyMemberSettings.size(); i++) {
            if (familyMemberSettings.get(i).getId() == uniqueId) {
                return i;
            }
        }
        return -1;
    }
}
