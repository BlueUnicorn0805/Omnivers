package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.BidsInfoOriginal;

import java.util.List;

public class BidsListAdapter extends BaseAdapter {

    private Context mContext;
    private List<BidsInfoOriginal> mDataList;

    private String driverIDChoosen = "";
    private int colorNormal;
    private int colorHightlight;

    public BidsListAdapter(Context mContext, List<BidsInfoOriginal> dataList) {
        this.mContext = mContext;
        this.mDataList = dataList;

        this.colorNormal = ContextCompat.getColor(mContext, R.color.app_grey_dark);
        this.colorHightlight = ContextCompat.getColor(mContext, R.color.app_red);
    }

    @Override
    public int getCount() {
        if (mDataList == null)
            return 0;
        return mDataList.size();
    }

    public void setDriverIDChoosen(String driverID) {
        driverIDChoosen = driverID;
    }

    public String getDriverIDChoosen() {
        return driverIDChoosen;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bid_data,
                    null);
        }

        // Get Item Data
        BidsInfoOriginal itemInfo = (BidsInfoOriginal) getItem(position);

        // Init Controls
        TextView txtNo = (TextView) convertView.findViewById(R.id.txtNo);
        TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
        TextView txtAmount = (TextView) convertView.findViewById(R.id.txtAmount);
        TextView txtRating = (TextView) convertView.findViewById(R.id.txtRating);
        TextView txtPickupTime = (TextView) convertView.findViewById(R.id.txtPickupTime);

        txtNo.setText(String.format("%d", position + 1));
        txtName.setText(itemInfo.getDFName());
        txtAmount.setText("$" + itemInfo.getBidAmount());
        txtRating.setText("" + itemInfo.getRatings());
        txtPickupTime.setText(itemInfo.getPickTime());

        String bidDriverId = itemInfo.getDriverID();
        if (!TextUtils.isEmpty(bidDriverId) && bidDriverId.equals(driverIDChoosen)) {
            txtNo.setTextColor(colorHightlight);
            txtName.setTextColor(colorHightlight);
            txtAmount.setTextColor(colorHightlight);
            txtRating.setTextColor(colorHightlight);
            txtPickupTime.setTextColor(colorHightlight);
        } else {
            txtNo.setTextColor(colorNormal);
            txtName.setTextColor(colorNormal);
            txtAmount.setTextColor(colorNormal);
            txtRating.setTextColor(colorNormal);
            txtPickupTime.setTextColor(colorNormal);
        }

        return convertView;
    }
}


