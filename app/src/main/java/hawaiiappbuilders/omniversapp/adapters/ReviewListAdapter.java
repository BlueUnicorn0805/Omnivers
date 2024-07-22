package hawaiiappbuilders.omniversapp.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.ReviewInfo;

import java.util.List;

public class ReviewListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ReviewInfo> mDataList;

    public ReviewListAdapter(Context mContext, List<ReviewInfo> dataList) {
        this.mContext = mContext;
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        if (mDataList == null)
            return 0;
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_review,
                    null);
        }

        // Get Item Data
        ReviewInfo itemInfo = (ReviewInfo) getItem(position);

        // Init Controls
        TextView txtReviewMsg = (TextView) convertView.findViewById(R.id.txtReviewMsg);
        TextView txtMark = (TextView) convertView.findViewById(R.id.txtMark);
        TextView txtPrice = (TextView) convertView.findViewById(R.id.txtPrice);
        TextView txtUser = (TextView) convertView.findViewById(R.id.txtUser);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);

        txtReviewMsg.setText(itemInfo.getDescription());
        txtUser.setText(itemInfo.getUserName());
        txtMark.setText(String.format("%.1f", itemInfo.getRatings()));
        ratingBar.setRating(itemInfo.getRatings());

        return convertView;
    }
}


