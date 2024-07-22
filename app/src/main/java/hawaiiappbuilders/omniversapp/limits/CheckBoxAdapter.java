package hawaiiappbuilders.omniversapp.limits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class CheckBoxAdapter extends RecyclerView.Adapter<CheckBoxAdapter.MyViewHolder> {

   public OnCheckListener listener;

   public interface OnCheckListener {
      public void onCheckedItem(CheckBoxData data, boolean isChecked);
   }
   public static class CheckBoxData {
      private int id;
      private int titleId;
      private String name;

      private boolean isChecked;
      public CheckBoxData(int id, int titleId, String name, boolean isChecked) {
         this.id = id;
         this.titleId = titleId;
         this.name = name;
         this.isChecked = isChecked;
      }

      public int getId() {
         return id;
      }

      public void setId(int id) {
         this.id = id;
      }

      public int getTitleId() {
         return titleId;
      }

      public void setTitleId(int titleId) {
         this.titleId = titleId;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public boolean isChecked() {
         return isChecked;
      }

      public void setChecked(boolean checked) {
         isChecked = checked;
      }
   }
   private LayoutInflater inflater;
   public ArrayList<CheckBoxData> itemList;
   private Context ctx;
   private BaseActivity activity;

   public CheckBoxAdapter(Context ctx, ArrayList<CheckBoxData> itemList, OnCheckListener listener) {

      inflater = LayoutInflater.from(ctx);
      this.itemList = itemList;
      this.ctx = ctx;
      this.activity = (BaseActivity) ctx;
      this.listener = listener;
   }

   @Override
   public CheckBoxAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = inflater.inflate(R.layout.item_checkbox, parent, false);
      return new MyViewHolder(view);
   }

   @Override
   public void onBindViewHolder(final CheckBoxAdapter.MyViewHolder holder, final int position) {
      CheckBoxData itemData = itemList.get(position);
      holder.mCheckBox.setText(itemData.getName());
      holder.mCheckBox.setChecked(itemData.isChecked());
      holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(listener != null) {
               listener.onCheckedItem(itemData, isChecked);
            }
         }
      });

   }

   public CheckBoxData getItem(int id) {
      return itemList.get(id);
   }

   @Override
   public int getItemCount() {
      return itemList.size();
   }

   static class MyViewHolder extends RecyclerView.ViewHolder {

      protected CheckBox mCheckBox;

      public MyViewHolder(View itemView) {
         super(itemView);
         mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_item);
      }
   }



   private CheckBoxAdapter.CheckBoxData getData(int uniqueId) {
      AppSettings appSettings = new AppSettings(ctx);
      String allSettings = appSettings.getMemberSettings();
      Type type = new TypeToken<ArrayList<CheckBoxData>>(){}.getType();
      ArrayList<CheckBoxData> familyMemberSettings = new Gson().fromJson(allSettings, type);
      for(int i = 0; i < familyMemberSettings.size(); i++) {
         if(familyMemberSettings.get(i).getId() == uniqueId) {
            return familyMemberSettings.get(i);
         }
      }
      return null;
   }
}
