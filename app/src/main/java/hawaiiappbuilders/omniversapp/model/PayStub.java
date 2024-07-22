package hawaiiappbuilders.omniversapp.model;

import java.util.ArrayList;
import java.util.List;

public class PayStub {

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

    public class Earnings {
        boolean isEarningsHeader = false;
        boolean isEarningsData = false;
        boolean isEarningsGross = false;
        String title;
        String time;
        String amount;

        public Earnings() {
            this.isEarningsHeader = false;
            this.isEarningsData = false;
            this.isEarningsGross = false;
            this.title = "";
            this.time = "";
            this.amount = "";
        }

        public Earnings(String title, String time, String amount) {
            this.title = title;
            this.time = time;
            this.amount = amount;
        }

        public void setEarningsHeader(boolean earningsHeader) {
            isEarningsHeader = earningsHeader;
        }

        public void setEarningsData(boolean earningsData) {
            isEarningsData = earningsData;
        }

        public void setEarningsGross(boolean earningsGross) {
            isEarningsGross = earningsGross;
        }

        public boolean isEarningsHeader() {
            return isEarningsHeader;
        }

        public boolean isEarningsData() {
            return isEarningsData;
        }

        public boolean isEarningsGross() {
            return isEarningsGross;
        }

        public String getTitle() {
            return title;
        }

        public String getTime() {
            return time;
        }

        public String getAmount() {
            return amount;
        }
    }

    public class Deduction {
        boolean isDeductionHeader = false;
        boolean isDeductionData = false;
        boolean isDeductionGross = false;
        boolean isOthers = false;
        String title;
        String current;
        String amount;


        public Deduction() {
            isDeductionHeader = false;
            isDeductionData = false;
            isDeductionGross = false;
            isOthers = false;
            title = "";
            current = "";
            amount = "";
        }

        public Deduction(String title, String current, String amount) {
            this.title = title;
            this.current = current;
            this.amount = amount;
        }

        public void setDeductionHeader(boolean deductionHeader) {
            isDeductionHeader = deductionHeader;
        }

        public void setDeductionData(boolean deductionData) {
            isDeductionData = deductionData;
        }

        public void setDeductionGross(boolean deductionGross) {
            isDeductionGross = deductionGross;
        }

        public boolean isOthers() {
            return isOthers;
        }

        public void setOthers(boolean others) {
            isOthers = others;
        }

        public boolean isDeductionHeader() {
            return isDeductionHeader;
        }

        public boolean isDeductionData() {
            return isDeductionData;
        }

        public boolean isDeductionGross() {
            return isDeductionGross;
        }

        public String getTitle() {
            return title;
        }

        public String getCurrent() {
            return current;
        }

        public String getAmount() {
            return amount;
        }
    }

    public class PayStubModel {
        Earnings earnings;
        Deduction deduction;
        boolean isEarning = false;
        boolean isDeduction = false;
        int viewType = 0;

        public PayStubModel(Earnings earnings, Deduction deduction, int viewType) {
            this.earnings = earnings;
            this.deduction = deduction;
            this.viewType = viewType;
        }

        public PayStubModel(Earnings earnings, int viewType) {
            this.earnings = earnings;
            this.deduction = deduction;
            this.viewType = viewType;
        }

        public PayStubModel(Deduction deduction, int viewType) {
            this.earnings = earnings;
            this.deduction = deduction;
            this.viewType = viewType;
        }

        public Earnings getEarnings() {
            return earnings;
        }

        public Deduction getDeduction() {
            return deduction;
        }

        public boolean isEarning() {
            return isEarning;
        }

        public boolean isDeduction() {
            return isDeduction;
        }

        public int getViewType() {
            return viewType;
        }
    }

    private static List<PayStubModel> payStubModelList = new ArrayList<>();

    public static void setPayStubModelList(List<PayStubModel> payStubModelList) {
        PayStub.payStubModelList = payStubModelList;
    }

    public static List<PayStubModel> getPayStubModelList() {
        return payStubModelList;
    }

    public void setEarnings(Earnings earnings, int type) {
        switch (type) {
            case VIEW_TYPE_EARNING_HEADER:
                earnings.setEarningsHeader(true);
                break;
            case VIEW_TYPE_EARNING_DATA:
                earnings.setEarningsData(true);
                break;
            case VIEW_TYPE_EARNING_GROSS:
                earnings.setEarningsGross(true);
                break;
        }

        payStubModelList.add(new PayStubModel(earnings,type));
    }

    public void setDeduction(Deduction deduction, int type) {
        switch (type) {
            case VIEW_TYPE_DEDUCTION_HEADER:
                deduction.setDeductionHeader(true);
                break;
            case VIEW_TYPE_DEDUCTION_DATA:
                deduction.setDeductionData(true);
                break;
            case VIEW_TYPE_DEDUCTION_GROSS:
                deduction.setDeductionGross(true);
                break;


            case VIEW_TYPE_INSTA_DEPOSIT_HEADER:
            case VIEW_TYPE_CALCULATION_HEADER:
            case VIEW_TYPE_PAY_PERIOD_HEADER:
            case VIEW_TYPE_BENIFITS_HEADER:
            case VIEW_TYPE_INFO_HEADER:
                deduction.setOthers(true);
                break;
            case VIEW_TYPE_INSTA_DEPOSIT_DATA:
            case VIEW_TYPE_CALCULATION_DATA:
            case VIEW_TYPE_PAY_PERIOD_DATA:
            case VIEW_TYPE_BENIFITS_DATA:
            case VIEW_TYPE_INFO_DATA:
                deduction.setOthers(true);
                break;
            case VIEW_TYPE_INSTA_DEPOSIT_GROSS:
            case VIEW_TYPE_CALCULATION_GROSS:
            case VIEW_TYPE_PAY_PERIOD_GROSS:
            case VIEW_TYPE_BENIFITS_GROSS:
            case VIEW_TYPE_INFO_GROSS:
                deduction.setOthers(true);
                break;
        }

        payStubModelList.add(new PayStubModel(deduction,type));
    }
}