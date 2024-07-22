package hawaiiappbuilders.omniversapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.JobTypes;

import java.util.ArrayList;

/**
 * Created by RahulAnsari on 23-09-2018.
 */

public class SelectJobTypeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = SelectJobTypeActivity.class.getSimpleName();
    ListView listView;
    private CustomAdapter adapter;
    private ArrayList<JobTypes> jobTypesArrayList = new ArrayList<>();
    private ArrayList<String> selectedItems = new ArrayList<>();
    private ArrayList<String> selectedIds = new ArrayList<>();
//  Changing

    ArrayList<String> mapping = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job_type);
        listView = (ListView) findViewById(R.id.listViewJobType);

        mapping = getIntent().getStringArrayListExtra("SELECTED_IDS");
        addedItemCount = mapping.size();
        jobTypesArrayList.clear();
        jobTypesArrayList.addAll(JobTypes.getJobTypesList());
        for (JobTypes jobTypes : jobTypesArrayList) {
            if (jobTypes.isChecked()) {
                addedItemCount++;
            }
        }

        for (JobTypes jobTypes : jobTypesArrayList) {
            if (jobTypes.isChecked()) {
                selectedItems.add(jobTypes.getTitle());
                selectedIds.add(jobTypes.getID().toString().trim());
            }
        }

        if (jobTypesArrayList!=null && jobTypesArrayList.size()>0) {
            adapter = new CustomAdapter(jobTypesArrayList, getApplicationContext());
            listView.setAdapter(adapter);
        } else {
            setResult(1050,new Intent()
                    .putStringArrayListExtra("SELECTED_ITEMS", selectedItems)
                    .putStringArrayListExtra("SELECTED_IDS", selectedIds)
            );
            finish();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_bar_done:
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                selectedItems.clear();
                                selectedIds.clear();
                                JobTypes.getJobTypesList().clear();
                                JobTypes.getJobTypesList().addAll(jobTypesArrayList);
                                for (JobTypes jobTypes : jobTypesArrayList) {
                                    if (mapping.contains(jobTypes.getID())) {
                                        selectedItems.add(jobTypes.getTitle());
                                        selectedIds.add(jobTypes.getID());
                                    }
                                }
                                jobTypesArrayList.clear();
                                setResult(1050,new Intent()
                                        .putStringArrayListExtra("SELECTED_ITEMS", selectedItems)
                                        .putStringArrayListExtra("SELECTED_IDS", selectedIds)
                                );
                                finish();
                            }
                        });
                    }
                },200);
                break;
            case R.id.app_bar_cancel:
                jobTypesArrayList.clear();
                finish();
                break;

        }
    }

    private static class ViewHolder {
        TextView txtName;
        TextView headerTitle;
        CheckBox checkBox;
    }

    private class CustomAdapter extends ArrayAdapter {

        private ArrayList<JobTypes> dataSet;
        private static final int TYPE_ITEM = 0;
        private static final int TYPE_HEADER = 1;

        public CustomAdapter(ArrayList data, Context context) {
            super(context, R.layout.job_type_list_item, data);
            this.dataSet = data;

        }

        @Override
        public int getItemViewType(int position) {
            try {
                return (dataSet.get(position).getSortid().equalsIgnoreCase("0")) ? TYPE_HEADER : TYPE_ITEM;
            } catch (Exception e) {
                return  (JobTypes.getJobTypesList().get(position).getSortid().equalsIgnoreCase("0")) ? TYPE_HEADER : TYPE_ITEM;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return dataSet.size();
        }

        @Override
        public JobTypes getItem(int position) {
            try {
                return dataSet.get(position);
            } catch (Exception e) {
                return JobTypes.getJobTypesList().get(position);
            }
        }


        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

            ViewHolder viewHolder = null;
            final View result;
            int rowType = getItemViewType(position);

            if (convertView == null) {

                switch (rowType) {
                    case TYPE_ITEM:
                        viewHolder = new ViewHolder();
                        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_type_list_item, parent, false);
                        viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
                        viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                        break;
                    case TYPE_HEADER:
                        viewHolder = new ViewHolder();
                        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_header_item, parent, false);
                        viewHolder.headerTitle = (TextView) convertView.findViewById(R.id.list_header_title);
                        break;
                }
                convertView.setTag(viewHolder);

                result=convertView;
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result=convertView;
            }

            try {

                if (rowType == TYPE_ITEM ) {
                    JobTypes item = getItem(position);
                    viewHolder.txtName.setText(item.getTitle());

                    if(mapping.contains(dataSet.get(position).getID())) {
                        viewHolder.checkBox.setChecked(true);
                    } else {
                        viewHolder.checkBox.setChecked(false);
                    }


                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                JobTypes dataModel= dataSet.get(position);
                                if (addedItemCount>=5 && !mapping.contains(dataSet.get(position).getID())/*dataModel.isChecked()*/) {
                                    Toast.makeText(SelectJobTypeActivity.this,"Can not add more than five.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (mapping.contains(dataSet.get(position).getID())/*dataModel.isChecked()*/) {
                                    if (addedItemCount>0) {
                                        addedItemCount--;
                                        mapping.remove(dataSet.get(position).getID());
                                    }
                                } else {
                                    mapping.add(dataSet.get(position).getID());
                                    addedItemCount++;
                                }

//                        jobTypesArrayList.get(position).setChecked(!dataModel.isChecked());
                                dataSet.get(position).setChecked(true);
                                dataSet.get(position).setChecked(false);
                                notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    JobTypes item = getItem(position);
                    viewHolder.headerTitle.setText(item.getTitle());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    private int addedItemCount = 0;
}
