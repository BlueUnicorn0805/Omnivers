package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.PayStub;

import java.util.List;

public class PayStubAdapter extends RecyclerView.Adapter<PayStubAdapter.ItemViewHolder> {

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

    private List<PayStub.PayStubModel> payStubModels;

    public PayStubAdapter(Context context, List<PayStub.PayStubModel> payStubModels, RecyclerViewClickListener listener) {
        this.mContext = context;
        this.payStubModels = payStubModels;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        int viewType;
        TextView psEarning, psEarningHrs, psEarningAmount, psEarningHrs2, psEarningAmount2;
        ImageView psUnderline;
        TextView psDeduction, psDeductionCurrent, psDeductionYTD;

        public ItemViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            switch (viewType) {
                case VIEW_TYPE_EARNING_HEADER:
                    psEarning = (TextView) itemView.findViewById(R.id.ps_earning);
                    psEarningHrs = (TextView) itemView.findViewById(R.id.ps_earning_hrs);
                    psEarningAmount = (TextView) itemView.findViewById(R.id.ps_earning_amt);
                    psEarningHrs2 = (TextView) itemView.findViewById(R.id.ps_earning_hrs_2);
                    psEarningAmount2 = (TextView) itemView.findViewById(R.id.ps_earning_amt_2);
                    psUnderline = (ImageView) itemView.findViewById(R.id.ps_ul);
                    psEarning.setTypeface(psEarning.getTypeface(), Typeface.BOLD);
                    psUnderline.setVisibility(View.GONE);
                    break;
                case VIEW_TYPE_EARNING_DATA:
                    psEarning = (TextView) itemView.findViewById(R.id.ps_earning);
                    psEarningHrs = (TextView) itemView.findViewById(R.id.ps_earning_hrs);
                    psEarningAmount = (TextView) itemView.findViewById(R.id.ps_earning_amt);
                    psEarningHrs2 = (TextView) itemView.findViewById(R.id.ps_earning_hrs_2);
                    psEarningAmount2 = (TextView) itemView.findViewById(R.id.ps_earning_amt_2);
                    psUnderline = (ImageView) itemView.findViewById(R.id.ps_ul);
                    psUnderline.setVisibility(View.VISIBLE);
                    break;
                case VIEW_TYPE_EARNING_GROSS:
                    psEarning = (TextView) itemView.findViewById(R.id.ps_earning);
                    psEarningHrs = (TextView) itemView.findViewById(R.id.ps_earning_hrs);
                    psEarningAmount = (TextView) itemView.findViewById(R.id.ps_earning_amt);
                    psEarningHrs2 = (TextView) itemView.findViewById(R.id.ps_earning_hrs_2);
                    psEarningAmount2 = (TextView) itemView.findViewById(R.id.ps_earning_amt_2);
                    psUnderline = (ImageView) itemView.findViewById(R.id.ps_ul);
                    psUnderline.setVisibility(View.VISIBLE);
                    break;
                case VIEW_TYPE_DEDUCTION_HEADER:
                    psDeduction = (TextView) itemView.findViewById(R.id.ps_deduction);
                    psDeductionCurrent = (TextView) itemView.findViewById(R.id.ps_deduction_current);
                    psDeductionYTD = (TextView) itemView.findViewById(R.id.ps_deduction_ytd);
                    psUnderline = (ImageView) itemView.findViewById(R.id.ps_ul);
                    psDeduction.setTypeface(psDeduction.getTypeface(),Typeface.BOLD);
                    psUnderline.setVisibility(View.GONE);
                    break;
                case VIEW_TYPE_DEDUCTION_DATA:
                    psDeduction = (TextView) itemView.findViewById(R.id.ps_deduction);
                    psDeductionCurrent = (TextView) itemView.findViewById(R.id.ps_deduction_current);
                    psDeductionYTD = (TextView) itemView.findViewById(R.id.ps_deduction_ytd);
                    psUnderline = (ImageView) itemView.findViewById(R.id.ps_ul);
                    psUnderline.setVisibility(View.VISIBLE);
                    break;

                case VIEW_TYPE_DEDUCTION_GROSS:
                    psDeduction = (TextView) itemView.findViewById(R.id.ps_deduction);
                    psDeductionCurrent = (TextView) itemView.findViewById(R.id.ps_deduction_current);
                    psDeductionYTD = (TextView) itemView.findViewById(R.id.ps_deduction_ytd);
                    psUnderline = (ImageView) itemView.findViewById(R.id.ps_ul);
                    psUnderline.setVisibility(View.INVISIBLE);
                    psDeduction.setTypeface(psDeduction.getTypeface(),Typeface.BOLD);
                    break;
                case VIEW_TYPE_TOTAL:

                    psDeduction = (TextView) itemView.findViewById(R.id.ps_deduction);
                    psDeductionCurrent = (TextView) itemView.findViewById(R.id.ps_deduction_current);
                    psDeductionYTD = (TextView) itemView.findViewById(R.id.ps_deduction_ytd);
                    psUnderline = (ImageView) itemView.findViewById(R.id.ps_ul);
                    psUnderline.setVisibility(View.INVISIBLE);
                    psDeduction.setTypeface(psDeduction.getTypeface(),Typeface.BOLD);
                    break;


                case VIEW_TYPE_INSTA_DEPOSIT_HEADER:
                case VIEW_TYPE_CALCULATION_HEADER:
                case VIEW_TYPE_PAY_PERIOD_HEADER:
                case VIEW_TYPE_BENIFITS_HEADER:
                case VIEW_TYPE_INFO_HEADER:
                    psDeduction = (TextView) itemView.findViewById(R.id.ps_deduction);
                    psDeductionCurrent = (TextView) itemView.findViewById(R.id.ps_deduction_current);
                    psDeductionYTD = (TextView) itemView.findViewById(R.id.ps_deduction_ytd);
                    psUnderline = (ImageView) itemView.findViewById(R.id.ps_ul);
                    psUnderline.setVisibility(View.GONE);
                    psDeduction.setTypeface(psDeduction.getTypeface(),Typeface.BOLD);
                    break;
                case VIEW_TYPE_INSTA_DEPOSIT_DATA:
                case VIEW_TYPE_CALCULATION_DATA:
                case VIEW_TYPE_PAY_PERIOD_DATA:
                case VIEW_TYPE_BENIFITS_DATA:
                case VIEW_TYPE_INFO_DATA:
                    psDeduction = (TextView) itemView.findViewById(R.id.ps_deduction);
                    psDeductionCurrent = (TextView) itemView.findViewById(R.id.ps_deduction_current);
                    psDeductionYTD = (TextView) itemView.findViewById(R.id.ps_deduction_ytd);
                    psUnderline = (ImageView) itemView.findViewById(R.id.ps_ul);
                    psUnderline.setVisibility(View.VISIBLE);
                    break;
                case VIEW_TYPE_INSTA_DEPOSIT_GROSS:
                case VIEW_TYPE_CALCULATION_GROSS:
                case VIEW_TYPE_PAY_PERIOD_GROSS:
                case VIEW_TYPE_BENIFITS_GROSS:
                case VIEW_TYPE_INFO_GROSS:

                    psDeduction = (TextView) itemView.findViewById(R.id.ps_deduction);
                    psDeductionCurrent = (TextView) itemView.findViewById(R.id.ps_deduction_current);
                    psDeductionYTD = (TextView) itemView.findViewById(R.id.ps_deduction_ytd);
                    psUnderline = (ImageView) itemView.findViewById(R.id.ps_ul);
                    psUnderline.setVisibility(View.INVISIBLE);
                    psDeduction.setTypeface(psDeduction.getTypeface(),Typeface.BOLD);
                    break;
                default:
                    break;
            }
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_earnings_heading,parent,false);
        switch (viewType) {
            case VIEW_TYPE_EARNING_HEADER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_earnings_heading,parent,false);
                break;
            case VIEW_TYPE_EARNING_DATA:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_earnings_heading,parent,false);
                break;
            case VIEW_TYPE_EARNING_GROSS:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_earnings_heading,parent,false);
                break;
            case VIEW_TYPE_DEDUCTION_HEADER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_deduction_heading,parent,false);
                break;
            case VIEW_TYPE_DEDUCTION_DATA:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_deduction_heading,parent,false);
                break;
            case VIEW_TYPE_DEDUCTION_GROSS:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_deduction_heading,parent,false);
                break;
            case VIEW_TYPE_TOTAL:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_deduction_heading,parent,false);
                break;


            case VIEW_TYPE_INSTA_DEPOSIT_HEADER:
            case VIEW_TYPE_CALCULATION_HEADER:
            case VIEW_TYPE_PAY_PERIOD_HEADER:
            case VIEW_TYPE_BENIFITS_HEADER:
            case VIEW_TYPE_INFO_HEADER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_deduction_heading,parent,false);
                break;


            case VIEW_TYPE_INSTA_DEPOSIT_DATA:
            case VIEW_TYPE_CALCULATION_DATA:
            case VIEW_TYPE_PAY_PERIOD_DATA:
            case VIEW_TYPE_BENIFITS_DATA:
            case VIEW_TYPE_INFO_DATA:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_deduction_heading,parent,false);
                break;


            case VIEW_TYPE_INSTA_DEPOSIT_GROSS:
            case VIEW_TYPE_CALCULATION_GROSS:
            case VIEW_TYPE_PAY_PERIOD_GROSS:
            case VIEW_TYPE_BENIFITS_GROSS:
            case VIEW_TYPE_INFO_GROSS:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_deduction_heading,parent,false);
                break;
            default:
                break;
        }


        return new ItemViewHolder(v,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        switch (holder.viewType) {
            case VIEW_TYPE_EARNING_HEADER:
                break;
            case VIEW_TYPE_EARNING_DATA:
                holder.psEarning.setText(payStubModels.get(position).getEarnings().getTitle());
                holder.psEarningHrs.setText(payStubModels.get(position).getEarnings().getTime());
                holder.psEarningAmount.setText(payStubModels.get(position).getEarnings().getAmount());
                holder.psEarningHrs2.setText(payStubModels.get(position).getEarnings().getTime());
                holder.psEarningAmount2.setText(payStubModels.get(position).getEarnings().getAmount());
                break;
            case VIEW_TYPE_EARNING_GROSS:
                holder.psEarning.setText(payStubModels.get(position).getEarnings().getTitle());
                holder.psEarningHrs.setText(payStubModels.get(position).getEarnings().getTime());
                holder.psEarningAmount.setText(payStubModels.get(position).getEarnings().getAmount());
                holder.psEarningHrs2.setText(payStubModels.get(position).getEarnings().getTime());
                holder.psEarningAmount2.setText(payStubModels.get(position).getEarnings().getAmount());
                break;
            case VIEW_TYPE_DEDUCTION_HEADER:
                break;
            case VIEW_TYPE_DEDUCTION_DATA:
                holder.psDeduction.setText(payStubModels.get(position).getDeduction().getTitle());
                holder.psDeductionCurrent.setText(payStubModels.get(position).getDeduction().getCurrent());
                holder.psDeductionYTD.setText(payStubModels.get(position).getDeduction().getAmount());
                break;
            case VIEW_TYPE_DEDUCTION_GROSS:
                holder.psDeduction.setText(payStubModels.get(position).getDeduction().getTitle());
                holder.psDeductionCurrent.setText(payStubModels.get(position).getDeduction().getCurrent());
                holder.psDeductionYTD.setText(payStubModels.get(position).getDeduction().getAmount());
                break;
            case VIEW_TYPE_TOTAL:
                holder.psDeduction.setText(payStubModels.get(position).getDeduction().getTitle());
                holder.psDeductionCurrent.setText(payStubModels.get(position).getDeduction().getCurrent());
                holder.psDeductionYTD.setText(payStubModels.get(position).getDeduction().getAmount());
                break;

            case VIEW_TYPE_INSTA_DEPOSIT_HEADER:
                holder.psDeduction.setText("Insta Deposit Form");
                holder.psDeductionCurrent.setVisibility(View.GONE);
                holder.psDeductionYTD.setVisibility(View.GONE);
                break;
            case VIEW_TYPE_CALCULATION_HEADER:
                holder.psDeduction.setText("Calculation based On");
                holder.psDeductionCurrent.setVisibility(View.GONE);
                holder.psDeductionYTD.setVisibility(View.GONE);
                break;
            case VIEW_TYPE_PAY_PERIOD_HEADER:
                holder.psDeduction.setText("Pay Period");
                holder.psDeductionCurrent.setVisibility(View.GONE);
                holder.psDeductionYTD.setVisibility(View.GONE);
                break;
            case VIEW_TYPE_BENIFITS_HEADER:
                holder.psDeduction.setText("Benifits");
                holder.psDeductionCurrent.setVisibility(View.GONE);
                holder.psDeductionYTD.setVisibility(View.GONE);
                break;
            case VIEW_TYPE_INFO_HEADER:
                holder.psDeduction.setText("Info");
                holder.psDeductionCurrent.setVisibility(View.GONE);
                holder.psDeductionYTD.setVisibility(View.GONE);
                break;


            case VIEW_TYPE_INSTA_DEPOSIT_DATA:
            case VIEW_TYPE_CALCULATION_DATA:
            case VIEW_TYPE_PAY_PERIOD_DATA:
            case VIEW_TYPE_BENIFITS_DATA:
            case VIEW_TYPE_INFO_DATA:
                holder.psDeduction.setText(payStubModels.get(position).getDeduction().getTitle());
                holder.psDeductionCurrent.setText(payStubModels.get(position).getDeduction().getCurrent());
                holder.psDeductionYTD.setText(payStubModels.get(position).getDeduction().getAmount());
                break;


            case VIEW_TYPE_INSTA_DEPOSIT_GROSS:
            case VIEW_TYPE_CALCULATION_GROSS:
            case VIEW_TYPE_PAY_PERIOD_GROSS:
            case VIEW_TYPE_BENIFITS_GROSS:
            case VIEW_TYPE_INFO_GROSS:
                holder.psDeduction.setText(payStubModels.get(position).getDeduction().getTitle());
                holder.psDeductionCurrent.setText(payStubModels.get(position).getDeduction().getCurrent());
                holder.psDeductionYTD.setText(payStubModels.get(position).getDeduction().getAmount());
                break;

            default:
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return payStubModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return payStubModels.get(position).getViewType();
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}
