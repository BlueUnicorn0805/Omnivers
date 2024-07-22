package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.model.DashBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashBoardAdapter extends RecyclerView.Adapter<DashBoardAdapter.ItemViewHolder> {

    private Context mContext;
    private RecyclerViewClickListener mRecyclerViewClickListener;
    private int currentSel = -1;

    List<DashBoard> dashBoardList = new ArrayList<>();

    HashMap<Integer, View> viewHashMap = new HashMap<>();

    public DashBoardAdapter(Context context, RecyclerViewClickListener recyclerViewClickListener) {
        this.mContext = context;
        mRecyclerViewClickListener = recyclerViewClickListener;
        addDashBoardItems();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View item_title_area;
        TextView item_title;
        ImageView db_item_image;
        CardView db_card;
        CardView db_item_card;
        LinearLayout content_ll;


        public ItemViewHolder(View itemView, final RecyclerViewClickListener recyclerViewClickListener) {
            super(itemView);
            item_title_area = itemView.findViewById(R.id.ll1);

            item_title = (TextView) itemView.findViewById(R.id.item_title);
            db_item_image = (ImageView) itemView.findViewById(R.id.db_item_image);
            db_card = (CardView) itemView.findViewById(R.id.db_card);
            db_item_card = (CardView) itemView.findViewById(R.id.db_item_card);

            content_ll = (LinearLayout) itemView.findViewById(R.id.content_ll);
            content_ll.setVisibility(View.GONE);

            item_title_area.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int categoryIndex = (int) v.getTag();


            /*View originalActiveView = viewHashMap.get(currentSel);
            if (originalActiveView != null) {
                originalActiveView.setVisibility(View.GONE);
                originalActiveView.requestLayout();
            }

            currentSel = categoryIndex;
            View newActiveView = viewHashMap.get(currentSel);
            if (newActiveView != null) {
                newActiveView.setVisibility(View.VISIBLE);
                newActiveView.requestLayout();
            }*/

            currentSel = categoryIndex;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dashboard_item, parent, false);
        return new ItemViewHolder(v, mRecyclerViewClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {

        viewHashMap.put(position, holder.content_ll);

        holder.item_title.setText(dashBoardList.get(position).getCardName());

        switch (position) {
            case 0:
                //holder.db_card.setCardBackgroundColor(mContext.getResources().getColor(R.color.db_5));
                //holder.db_item_card.setCardBackgroundColor(mContext.getResources().getColor(R.color.db_5));
                holder.db_item_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_dash_search));
                break;
            case 1:
                //holder.db_card.setCardBackgroundColor(mContext.getResources().getColor(R.color.db_5));
                //holder.db_item_card.setCardBackgroundColor(mContext.getResources().getColor(R.color.db_5));
                holder.db_item_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_dash_calendar));
                break;
            case 2:
                //holder.db_card.setCardBackgroundColor(mContext.getResources().getColor(R.color.db_5));
                //holder.db_item_card.setCardBackgroundColor(mContext.getResources().getColor(R.color.db_5));
                holder.db_item_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_dash_work));
                break;
            case 3:
                //holder.db_card.setCardBackgroundColor(mContext.getResources().getColor(R.color.db_5));
                //holder.db_item_card.setCardBackgroundColor(mContext.getResources().getColor(R.color.db_5));
                holder.db_item_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_dash_user));

                holder.item_title.setText("Logged in as " + new AppSettings(mContext).getFN());

                break;
        }

        holder.item_title_area.setTag(position);

        holder.content_ll.removeAllViews();

        for (int i = 0; i < dashBoardList.get(position).getDashBoardItemContents().size(); i++) {
            addContent(position, holder.content_ll,
                    dashBoardList.get(position).getDashBoardItemContents().get(i),
                    holder.db_item_card,
                    i < (dashBoardList.get(position).getDashBoardItemContents().size() - 1)
            );
        }

        if (currentSel != position) {
            holder.content_ll.setVisibility(View.GONE);
        } else {
            holder.content_ll.setVisibility(View.VISIBLE);
        }
    }

    View.OnClickListener categoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int categoryIndex = (int) v.getTag();


            /*View originalActiveView = viewHashMap.get(currentSel);
            if (originalActiveView != null) {
                originalActiveView.setVisibility(View.GONE);
                originalActiveView.requestLayout();
            }

            currentSel = categoryIndex;
            View newActiveView = viewHashMap.get(currentSel);
            if (newActiveView != null) {
                newActiveView.setVisibility(View.VISIBLE);
                newActiveView.requestLayout();
            }*/

            currentSel = categoryIndex;
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        return dashBoardList.size();
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }

    private void addContent(final int position, LinearLayout linearLayout, final DashBoard.DashBoardItemContent dashBoardItemContent, CardView cardView, boolean setDivider) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View content = layoutInflater.inflate(R.layout.layout_dashboard_content_item, null);
        TextView item_name = (TextView) content.findViewById(R.id.item_name);
        ImageView db_item_next = (ImageView) content.findViewById(R.id.db_item_next);
        ImageView divider = (ImageView) content.findViewById(R.id.divider);
        RelativeLayout db_card_item_rl = (RelativeLayout) content.findViewById(R.id.db_card_item_rl);

        //item_name.setTextColor(cardView.getCardBackgroundColor().getDefaultColor());
        //if (setDivider) {
        //    divider.setBackgroundColor(cardView.getCardBackgroundColor().getDefaultColor());
        //}

        //item_name.setTextColor(-1);
        //divider.setBackgroundColor(-1);

        db_card_item_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerViewClickListener.onClick(v, dashBoardItemContent.getCarItemId());
            }
        });


        item_name.setText(dashBoardItemContent.getCardName());

        linearLayout.addView(content);
    }

    private void addDashBoardItems() {
        List<DashBoard.DashBoardItemContent> cardContent = new ArrayList<>();

        // My Money
        cardContent.add(new DashBoard.DashBoardItemContent("Pay", "", DashBoard.PAY));
        cardContent.add(new DashBoard.DashBoardItemContent("Face To Face Request/Share Info", "", DashBoard.F2F_REQUEST));
        cardContent.add(new DashBoard.DashBoardItemContent("Local Store Purchase", "", DashBoard.LOCAL_STORE_PURCHASE));
        cardContent.add(new DashBoard.DashBoardItemContent("Transfer Funds", "", DashBoard.TRANSFER_FUNDS));
        cardContent.add(new DashBoard.DashBoardItemContent("Analysis", "", DashBoard.ANALYSIS));
        dashBoardList.add(new DashBoard("My Money", "", cardContent));
        cardContent.clear();

        // My Calendar
        cardContent.add(new DashBoard.DashBoardItemContent("My Calendar", "", DashBoard.MY_CALENDAR));
        cardContent.add(new DashBoard.DashBoardItemContent("Book Appointment", "", DashBoard.BOOK_APPOINTMENT));
        dashBoardList.add(new DashBoard("My Calendar", "", cardContent));
        cardContent.clear();

        // My Work
        cardContent.add(new DashBoard.DashBoardItemContent("Make Money", "", DashBoard.WORK_RELATED));
        cardContent.add(new DashBoard.DashBoardItemContent("Business Owners", "", DashBoard.BUSINESS_OWNER));
        dashBoardList.add(new DashBoard("My Work", "", cardContent));
        cardContent.clear();

        // My Settings
        cardContent.add(new DashBoard.DashBoardItemContent("My Data", "", DashBoard.MY_DATA));
        cardContent.add(new DashBoard.DashBoardItemContent("Logout", "", DashBoard.MY_PROFILE));
        dashBoardList.add(new DashBoard("My Settings", "", cardContent));
        cardContent.clear();
    }
}
