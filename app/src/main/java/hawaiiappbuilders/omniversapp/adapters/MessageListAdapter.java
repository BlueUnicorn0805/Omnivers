package hawaiiappbuilders.omniversapp.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.Message;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Message> mMessageList;
    private AppSettings mAppSettings;

    public MessageListAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
        mAppSettings = new AppSettings(context);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) mMessageList.get(position);

        if (message.getFromID() == mAppSettings.getUserId()) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_me, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_other, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.get(position);
        Message prevMessage = null;
        if (position > 0) {
            prevMessage = mMessageList.get(position - 1);
        }

        boolean bNewDate = false;
        if (prevMessage == null) {
            bNewDate = true;
        } else {
            String prevMsgDate = DateUtil.toStringFormat_1(DateUtil.parseDataFromFormat12(prevMessage.getCreateDate()));
            String newMsgdate = DateUtil.toStringFormat_1(DateUtil.parseDataFromFormat12(message.getCreateDate()));
            if (!prevMsgDate.equals(newMsgdate)) {
                bNewDate = true;
            }
        }

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message, bNewDate);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message, bNewDate);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView dateText, messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            dateText = (TextView) itemView.findViewById(R.id.text_gchat_date_me);
            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_me);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_me);
        }

        void bind(Message message, boolean isNewDate) {
            messageText.setText(message.getMsg());

            messageText.setOnLongClickListener(longClickListener);

            Date date = DateUtil.parseDataFromFormat12(message.getCreateDate());
            if (isNewDate) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtil.toStringFormat_27(date));
            } else{
                dateText.setVisibility(View.GONE);
            }
            // Format the stored timestamp into a readable String using method.
            timeText.setText(DateUtil.toStringFormat_10(date));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView dateText, messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            dateText = (TextView) itemView.findViewById(R.id.text_gchat_date_other);
            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_other);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_other);
            nameText = (TextView) itemView.findViewById(R.id.text_gchat_user_other);
            profileImage = (ImageView) itemView.findViewById(R.id.image_gchat_profile_other);
        }

        void bind(Message message, boolean isNewDate) {

            nameText.setText(message.getName());
            // Insert the profile image from the URL into the ImageView.
            // Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
            profileImage.setVisibility(View.GONE);

            messageText.setText(message.getMsg());

            messageText.setOnLongClickListener(longClickListener);

            Date date = DateUtil.parseDataFromFormat12(message.getCreateDate());
            if (isNewDate) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtil.toStringFormat_27(date));
            } else {
                dateText.setVisibility(View.GONE);
            }

            // Format the stored timestamp into a readable String using method.
            timeText.setText(DateUtil.toStringFormat_10(date));
        }
    }

    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {

            if (view instanceof TextView) {
                String text = ((TextView) view).getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied Text", text);
                    clipboard.setPrimaryClip(clip);

                    // Show Toast
                    ((BaseActivity) mContext).showToastMessage("Copied message!");
                }
            }

            return false;
        }
    };
}
