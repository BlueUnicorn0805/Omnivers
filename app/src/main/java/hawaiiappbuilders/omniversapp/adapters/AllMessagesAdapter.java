package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.MaterialLetterIcon;
import hawaiiappbuilders.omniversapp.ldb.Message;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

public class AllMessagesAdapter extends RecyclerView.Adapter<AllMessagesAdapter.MyViewHolder> implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        itemListener.onItemClicked(position);
    }

    public interface MessageItemListener {
        void onItemClicked(int position);
    }

    private LayoutInflater inflater;
    public ArrayList<Message> cList;
    private Context ctx;
    private MessageItemListener itemListener;

    private List<Integer> colors = Arrays.asList(
            0xc0f44336, 0xc09c27b0, 0xc0e91e63, 0xc0673ab7,
            0xC03f51b5, 0xC02196f3, 0xC003a9f4, 0xc000bcd4,
            0xC0009688, 0xC04caf50, 0xC08bc34a, 0xc0cddc39,
            0xC0ffeb3b, 0xC0ffc107, 0xC0ff9800, 0xc0ff5722,
            0xC0795548, 0xC09e9e9e, 0xC0607d8b, 0xc0333333
    );

    public AllMessagesAdapter(Context ctx, ArrayList<Message> catList, MessageItemListener listener) {
        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
        this.cList = catList;
        this.itemListener = listener;
    }

    @Override
    public AllMessagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_message_all, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    private int getColor(int position) {
        return colors.get(position % colors.size());
    }

    @Override
    public void onBindViewHolder(final AllMessagesAdapter.MyViewHolder holder, int position) {

        Message itemInfo = cList.get(position);

        holder.tvName.setText(itemInfo.getName());
        holder.tvMessage.setText(itemInfo.getMsg());
        String prefix = "";

        if (!TextUtils.isEmpty(itemInfo.getName())) {
            if (itemInfo.getName().length() >= 2) {
                prefix = itemInfo.getName().substring(0, 2);
            } else {
                prefix = itemInfo.getName();
            }
        }
        holder.ivCircleName.setLetter(prefix);
        holder.ivCircleName.setShapeColor(getColor(position)/*ColorUtils.randomColor()*/);

        if (TextUtils.isEmpty(prefix)) {
            holder.ivUserAvatar.setVisibility(View.VISIBLE);
        } else {
            holder.ivUserAvatar.setVisibility(View.GONE);
        }

        String createDate = itemInfo.getCreateDate();
        createDate = DateUtil.toStringFormat_32(DateUtil.parseDataFromFormat12(createDate));
        holder.tvDate.setText(createDate);

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected MaterialLetterIcon ivCircleName;
        protected TextView tvName;
        protected TextView tvMessage;
        protected ImageView ivUserAvatar;
        protected TextView tvDate;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivCircleName = itemView.findViewById(R.id.ivCircleName);
            tvName = itemView.findViewById(R.id.tvName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
