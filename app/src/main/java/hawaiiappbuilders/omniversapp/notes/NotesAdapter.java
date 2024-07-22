package hawaiiappbuilders.omniversapp.notes;


import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.Note;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ItemViewHolder> {

   Context context;
   ArrayList<Note> mDataList;

   public NotesAdapter(Context context, ArrayList<Note> mNotes) {
      this.context = context;
      this.mDataList = mNotes;
   }

   public static class ItemViewHolder extends RecyclerView.ViewHolder {

      public View panelItem;
      public TextView tvDate;
      public TextView tvNotes;

      public ItemViewHolder(View itemView) {
         super(itemView);
         panelItem = itemView;

         tvDate = itemView.findViewById(R.id.tvDate);
         tvNotes = itemView.findViewById(R.id.tvNotes);
      }
   }

   @NonNull
   @Override
   public NotesAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_note, parent, false);
      return new NotesAdapter.ItemViewHolder(v);
   }

   @Override
   public void onBindViewHolder(@NonNull NotesAdapter.ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
      Note noteItem = mDataList.get(position);

      String dateText = "<small><b><font color='#BBBBBB'>" + DateUtil.toStringFormat_21(DateUtil.parseDataFromFormat20(noteItem.getCreateDate())) + "</font></b></small>";
      String notes = noteItem.getNote().replace("\\r\\n", "<br>") + "";

      holder.tvDate.setText(HtmlCompat.fromHtml(dateText, HtmlCompat.FROM_HTML_MODE_COMPACT));
      holder.tvNotes.setText(HtmlCompat.fromHtml(notes, HtmlCompat.FROM_HTML_MODE_COMPACT));

      holder.panelItem.setTag(position);
      holder.panelItem.setOnLongClickListener(new View.OnLongClickListener() {
         @Override
         public boolean onLongClick(View v) {

            ClipboardManager clipboard = (ClipboardManager) holder.itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Note Item#" + position, noteItem.getNote());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(holder.itemView.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            return false;
         }
      });
   }

   @Override
   public int getItemCount() {
      return mDataList.size();
   }

}
