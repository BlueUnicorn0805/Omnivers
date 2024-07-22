package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.TransferRequest;

import java.util.ArrayList;

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.ItemViewHolder> {

    Context context;
    ArrayList<TransferRequest> mTransfers;

    public TransferAdapter(Context context, ArrayList<TransferRequest> mTransactions) {
        this.context = context;
        this.mTransfers = mTransactions;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView mID, mAmount, mDate, mStatus;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mID = itemView.findViewById(R.id.tvID);
            mAmount = itemView.findViewById(R.id.tvAmt);
            mDate = itemView.findViewById(R.id.tvDate);
            mStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transfer, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        TransferRequest transaction = mTransfers.get(position);
        holder.mID.setText(transaction.getID());
        holder.mAmount.setText("$".concat(transaction.getAmt()));
        holder.mDate.setText(transaction.getCreateDate().replace("T", " "));
        holder.mStatus.setText(transaction.getStatus());
    }

    @Override
    public int getItemCount() {
        return mTransfers.size();
    }

}
