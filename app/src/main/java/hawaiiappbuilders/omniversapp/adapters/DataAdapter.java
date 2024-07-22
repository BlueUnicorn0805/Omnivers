package hawaiiappbuilders.omniversapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.HistoryData;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.StringViewHolder>  {
   private final ArrayList<HistoryData> mDataset;
   RecyclerViewItemClickListener recyclerViewItemClickListener;

   public DataAdapter(ArrayList<HistoryData> history, RecyclerViewItemClickListener listener) {
      mDataset = history;
      this.recyclerViewItemClickListener = listener;
   }

   @NonNull
   @Override
   public StringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_string, parent, false);

      StringViewHolder vh = new StringViewHolder(v);
      return vh;

   }

   @Override
   public void onBindViewHolder(@NonNull StringViewHolder stringViewHolder, int i) {
      String locationData = "<small><b><font color='#BBBBBB'>" + mDataset.get(i).getDate().substring(0, 5) + " " + mDataset.get(i).getTime()  + "</font></b></small><br>";
      String address = mDataset.get(i).getFullAddress();
      stringViewHolder.mTextView.setText(HtmlCompat.fromHtml(locationData + address, HtmlCompat.FROM_HTML_MODE_COMPACT));
   }

   @Override
   public int getItemCount() {
      return mDataset.size();
   }



   public  class StringViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

      public TextView mTextView;

      public StringViewHolder(View v) {
         super(v);
         mTextView = (TextView) v.findViewById(R.id.text_string);
         mTextView.setOnClickListener(this);
         v.setOnClickListener(this);
      }

      @Override
      public void onClick(View v) {
         recyclerViewItemClickListener.clickOnItem(mDataset.get(this.getAdapterPosition()));
      }
   }

   public interface RecyclerViewItemClickListener {
      void clickOnItem(HistoryData data);
   }
}