package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.IndustryInfo;

import java.util.List;

public class IndustryAdapter extends BaseExpandableListAdapter implements View.OnClickListener {

    public interface ItemSelectListener {
        void onItemSelected(int groupPosition, int childPosition);
    }

    private Context mContext;
    private List<IndustryInfo> mDataList;
    private ItemSelectListener mListener;

    public IndustryAdapter(Context mContext, List<IndustryInfo> dataList, ItemSelectListener listener) {
        this.mContext = mContext;
        this.mDataList = dataList;
        this.mListener = listener;
    }

    @Override
    public int getGroupCount() {
        if (mDataList == null)
            return 0;
        return mDataList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mDataList.get(groupPosition).getChildIndustryInfo() == null) {
            return 0;
        } else {
            return mDataList.get(groupPosition).getChildIndustryInfo().size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mDataList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mDataList.get(groupPosition).getChildIndustryInfo().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_industries_header,
                    null);
        }

        // Get Item Data
        IndustryInfo currentItem = (IndustryInfo) getGroup(groupPosition);

        TextView tvIndustryName = convertView.findViewById(R.id.ab_head);

        tvIndustryName.setText(currentItem.getTypeDesc());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_industries,
                    null);
        }

        // Get Item Data
        IndustryInfo currentItem = (IndustryInfo) getChild(groupPosition, childPosition);

        TextView tvIndustryName = convertView.findViewById(R.id.ab_textview);
        tvIndustryName.setText(currentItem.getTypeDesc());

        tvIndustryName.setTag(new ItemIndicator(groupPosition, childPosition));
        tvIndustryName.setOnClickListener(this);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onClick(View v) {
        ItemIndicator itemIndicator = (ItemIndicator) v.getTag();
        if (itemIndicator != null && mListener != null) {
            mListener.onItemSelected(itemIndicator.groupPosition, itemIndicator.childPosition);
        }
    }

    public class ItemIndicator {
        public int groupPosition;
        public int childPosition;

        public ItemIndicator(int groupPosition, int childPosition) {
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
        }
    }
}


