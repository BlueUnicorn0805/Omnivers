package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.Friends;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ItemViewHolder> {

    private static final String TAG = FriendsAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<Friends> mFriends;
    private RecyclerViewClickListener mListener;
    private boolean isFromContactList;

    public FriendsAdapter(Context context, ArrayList<Friends> friends, boolean isFromContactList, FriendsAdapter.RecyclerViewClickListener listener) {
        this.context = context;
        this.mFriends = friends;
        this.isFromContactList = isFromContactList;
        this.mListener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mFriendsName, mFriendsEmail,mTransactionAmount,mTransactionDate;
        private RecyclerViewClickListener mListener;

        public ItemViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mFriendsName = itemView.findViewById(R.id.friends_name);
            mFriendsEmail = itemView.findViewById(R.id.friends_email);
//            mTransactionAmount  = itemView.findViewById(R.id.transaction_amount);
//            mTransactionDate = itemView.findViewById(R.id.transaction_date);

            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_item_row,parent,false);
         return new ItemViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position)
    {
        Friends friend = mFriends.get(position);
        if (isFromContactList) {
            holder.mFriendsName.setText(friend.getFriendID());
            holder.mFriendsEmail.setText(friend.getCP());
        } else {
            holder.mFriendsName.setText(friend.getNick());
            holder.mFriendsEmail.setText(friend.getEmail());
        }

//        holder.mTransactionAmount.setText("$".concat(transaction.getAmt()));
//        holder.mTransactionDate.setText(transaction.getItemDate());
    }

    @Override
    public int getItemCount()
    {
        return mFriends.size();
    }


    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}
