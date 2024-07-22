package hawaiiappbuilders.omniversapp.milestone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class JobDescAdapter extends RecyclerView.Adapter<JobDescAdapter.ItemViewHolder> {
   Context context;
   BaseActivity activity;
   ArrayList<JobDesc> mJobDescs;

   public interface OnClickCheckListener {
      void onClickJobDesc(JobDesc jobDesc);
   }

   private JobDescAdapter.OnClickCheckListener listener;

   public JobDescAdapter(Context context, ArrayList<JobDesc> jobDescs, JobDescAdapter.OnClickCheckListener listener) {
      this.context = context;
      this.activity = (BaseActivity) context;
      this.mJobDescs = jobDescs;
      this.listener = listener;
   }

   public static class ItemViewHolder extends RecyclerView.ViewHolder {

      public View layoutView;
      public TextView textTitle;
      public TextView textDescription;
      public TextView textPostedBy;
      public TextView textTimestamp;
      public TextView textJobCost;

      public ItemViewHolder(View itemView) {
         super(itemView);
         layoutView = itemView.findViewById(R.id.layoutView);
         textTitle = itemView.findViewById(R.id.textJobTitle);
         textDescription = itemView.findViewById(R.id.textJobDescription);
         textPostedBy = itemView.findViewById(R.id.textPostedBy);
         textTimestamp = itemView.findViewById(R.id.textTimestamp);
         textJobCost = itemView.findViewById(R.id.textPayRate);
      }
   }

   @NonNull
   @Override
   public JobDescAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_job_hunt, parent, false);
      return new JobDescAdapter.ItemViewHolder(v);
   }

   @SuppressLint("SetTextI18n")
   @Override
   public void onBindViewHolder(@NonNull JobDescAdapter.ItemViewHolder holder, int position) {
      JobDesc jobDesc = mJobDescs.get(position);
      holder.layoutView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (listener != null) {
               listener.onClickJobDesc(jobDesc);
            }
         }
      });
      holder.textTitle.setText(jobDesc.getJobTitle());
      holder.textDescription.setText(jobDesc.getJobDescription());
      holder.textPostedBy.setText("Posted By: " + jobDesc.getPostedBy());
      holder.textTimestamp.setText(jobDesc.getTimestamp());
      holder.textJobCost.setText(formatAmount(jobDesc.getJobCost()));

   }

   public static String formatAmount(double amt) {
      DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
      return "$ " + formatter.format(amt);
   }


   @Override
   public int getItemCount() {
      return mJobDescs.size();
   }


}

