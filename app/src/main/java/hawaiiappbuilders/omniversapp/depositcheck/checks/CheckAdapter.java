package hawaiiappbuilders.omniversapp.depositcheck.checks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.ItemViewHolder> {
    Context context;
    BaseActivity activity;
    ArrayList<Check> mChecks;

    public interface OnClickCheckListener {
        void onClickCheck(Check check);
        void onSendEmail(Check check);
        void onShowCheck(Check check);
    }

    private OnClickCheckListener listener;

    public CheckAdapter(Context context, ArrayList<Check> mChecks, OnClickCheckListener listener) {
        this.context = context;
        this.activity = (BaseActivity) context;
        this.mChecks = mChecks;
        this.listener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public View itemCheck;
        public TextView textTransactionId;
        public TextView textDate;
        public TextView textName;
        public TextView textAmount;
        public TextView textCheckNumber;
        public Button btnSendEmail;
        public Button btnShowCheck;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemCheck = itemView.findViewById(R.id.itemCheck);
            textTransactionId = itemView.findViewById(R.id.textTransactionId);
            textDate = itemView.findViewById(R.id.textDate);
            textName = itemView.findViewById(R.id.textName);
            textAmount = itemView.findViewById(R.id.textAmount);
            textCheckNumber = itemView.findViewById(R.id.textCheckNumber);
            btnSendEmail = itemView.findViewById(R.id.btnSendEmail);
            btnShowCheck = itemView.findViewById(R.id.btnShowCheck);
        }
    }

    @NonNull
    @Override
    public CheckAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_check, parent, false);
        return new ItemViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Check check = mChecks.get(position);
        holder.textTransactionId.setText(check.getTransactionId() + "");
        holder.textDate.setText(check.getTransactionDate());
        holder.textName.setText(check.getBankName());
        holder.textAmount.setText(formatAmount(check.getAmount()));
        holder.textCheckNumber.setText(check.getCheckNumber());
        holder.itemCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check check = mChecks.get(position);
                if (listener != null) {
                    listener.onClickCheck(check);
                }
            }
        });

        holder.btnShowCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onShowCheck(mChecks.get(position));
                }
            }
        });

        holder.btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onSendEmail(mChecks.get(position));
                }
            }
        });
    }

    private String formatAmount(double amt) {
        DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
        return "$ " + formatter.format(amt);
    }


    @Override
    public int getItemCount() {
        return mChecks.size();
    }


}
