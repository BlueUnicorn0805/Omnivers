package hawaiiappbuilders.omniversapp;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import hawaiiappbuilders.omniversapp.adapters.PayStubAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.PayStub;

public class ActivityPayStub extends BaseActivity {
    public static final String TAG = ActivityPayStub.class.getSimpleName();
    private RecyclerView mPSRecyclerview;
    private Context mContext;

    private static final int VIEW_TYPE_EARNING_HEADER = 101;
    private static final int VIEW_TYPE_EARNING_DATA = 102;
    private static final int VIEW_TYPE_EARNING_GROSS = 103;
    private static final int VIEW_TYPE_DEDUCTION_HEADER = 104;
    private static final int VIEW_TYPE_DEDUCTION_DATA = 105;
    private static final int VIEW_TYPE_DEDUCTION_GROSS = 106;



    private static final int VIEW_TYPE_INSTA_DEPOSIT_HEADER = 107;
    private static final int VIEW_TYPE_INSTA_DEPOSIT_DATA = 108;
    private static final int VIEW_TYPE_INSTA_DEPOSIT_GROSS = 109;

    private static final int VIEW_TYPE_CALCULATION_HEADER = 110;
    private static final int VIEW_TYPE_CALCULATION_DATA = 111;
    private static final int VIEW_TYPE_CALCULATION_GROSS = 112;

    private static final int VIEW_TYPE_PAY_PERIOD_HEADER = 113;
    private static final int VIEW_TYPE_PAY_PERIOD_DATA = 114;
    private static final int VIEW_TYPE_PAY_PERIOD_GROSS = 115;

    private static final int VIEW_TYPE_BENIFITS_HEADER = 116;
    private static final int VIEW_TYPE_BENIFITS_DATA = 117;
    private static final int VIEW_TYPE_BENIFITS_GROSS = 118;

    private static final int VIEW_TYPE_INFO_HEADER = 119;
    private static final int VIEW_TYPE_INFO_DATA = 120;
    private static final int VIEW_TYPE_INFO_GROSS = 121;

    private static final int VIEW_TYPE_TOTAL = 122;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_stub);

        mContext = this;
        initData();
        initViews();
    }

    private void initData() {
        new PayStub().setEarnings(new PayStub().new Earnings(),101);
        new PayStub().setEarnings(new PayStub().new Earnings("Salary Earnings","1","1"),102);
        new PayStub().setEarnings(new PayStub().new Earnings("Reg Earnings","1","1"),102);
        new PayStub().setEarnings(new PayStub().new Earnings("Overtime Earnings","1","1"),102);
        new PayStub().setEarnings(new PayStub().new Earnings("Vacation Used","1","1"),102);
        new PayStub().setEarnings(new PayStub().new Earnings("Sick time Used","1","1"),102);
        new PayStub().setEarnings(new PayStub().new Earnings("Gross Earnings","",""),103);

        new PayStub().setDeduction(new PayStub().new Deduction(),104);
        new PayStub().setDeduction(new PayStub().new Deduction("Fed","",""),105);
        new PayStub().setDeduction(new PayStub().new Deduction("State","",""),105);
        new PayStub().setDeduction(new PayStub().new Deduction("Social Security","",""),105);
        new PayStub().setDeduction(new PayStub().new Deduction("Medicare","",""),105);
        new PayStub().setDeduction(new PayStub().new Deduction("Health Ins","",""),105);
        new PayStub().setDeduction(new PayStub().new Deduction("Dental","",""),105);
        new PayStub().setDeduction(new PayStub().new Deduction("Parking","",""),105);
        new PayStub().setDeduction(new PayStub().new Deduction("401K","",""),105);
        new PayStub().setDeduction(new PayStub().new Deduction("401K Employer Match","",""),105);
        new PayStub().setDeduction(new PayStub().new Deduction("Advances","",""),105);


        new PayStub().setDeduction(new PayStub().new Deduction(),107);
        new PayStub().setDeduction(new PayStub().new Deduction("Deposit Date","","200"),108);
        new PayStub().setDeduction(new PayStub().new Deduction("Deposit Time","","200"),108);
        new PayStub().setDeduction(new PayStub().new Deduction("Deposit Amount","","200"),108);
        new PayStub().setDeduction(new PayStub().new Deduction("PayStubID","","200"),108);
//        new PayStub().setDeduction(new PayStub().new Deduction("Deposit","",""),109);



        new PayStub().setDeduction(new PayStub().new Deduction(),VIEW_TYPE_CALCULATION_HEADER);
        new PayStub().setDeduction(new PayStub().new Deduction("Pay Rate","","200"),VIEW_TYPE_CALCULATION_DATA);
        new PayStub().setDeduction(new PayStub().new Deduction("Pay Frequency","","200"),VIEW_TYPE_CALCULATION_DATA);
        new PayStub().setDeduction(new PayStub().new Deduction("Filling Status:","","200"),VIEW_TYPE_CALCULATION_DATA);
        new PayStub().setDeduction(new PayStub().new Deduction("Fed","","200"),VIEW_TYPE_CALCULATION_DATA);
        new PayStub().setDeduction(new PayStub().new Deduction("State","","200"),VIEW_TYPE_CALCULATION_DATA);
        new PayStub().setDeduction(new PayStub().new Deduction("Allowances/Extra:","","200"),VIEW_TYPE_CALCULATION_DATA);
        new PayStub().setDeduction(new PayStub().new Deduction("Fed","","200"),VIEW_TYPE_CALCULATION_DATA);
        new PayStub().setDeduction(new PayStub().new Deduction("State","","200"),VIEW_TYPE_CALCULATION_DATA);

        new PayStub().setDeduction(new PayStub().new Deduction(),VIEW_TYPE_PAY_PERIOD_HEADER);
        new PayStub().setDeduction(new PayStub().new Deduction("Perid End","","200"),VIEW_TYPE_PAY_PERIOD_DATA);
        new PayStub().setDeduction(new PayStub().new Deduction("Prepared Date","","200"),VIEW_TYPE_PAY_PERIOD_DATA);

        new PayStub().setDeduction(new PayStub().new Deduction(),VIEW_TYPE_BENIFITS_HEADER);
        new PayStub().setDeduction(new PayStub().new Deduction("Vaca Avail","","200"),VIEW_TYPE_BENIFITS_DATA);
        new PayStub().setDeduction(new PayStub().new Deduction("Sick Avail","","200"),VIEW_TYPE_BENIFITS_DATA);

        new PayStub().setDeduction(new PayStub().new Deduction(),VIEW_TYPE_INFO_HEADER);
        new PayStub().setDeduction(new PayStub().new Deduction("SSN","","200"),VIEW_TYPE_INFO_DATA);









        new PayStub().setDeduction(new PayStub().new Deduction("Total","",""),106);
        new PayStub().setDeduction(new PayStub().new Deduction("Net Pay","",""),122);


    }

    private void initViews() {
        mPSRecyclerview = (RecyclerView) findViewById(R.id.ps_recyclerview);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        mPSRecyclerview.setHasFixedSize(true);
        mPSRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));

        PayStubAdapter adapter = new PayStubAdapter(mContext, PayStub.getPayStubModelList(), new PayStubAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
        });
        mPSRecyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
