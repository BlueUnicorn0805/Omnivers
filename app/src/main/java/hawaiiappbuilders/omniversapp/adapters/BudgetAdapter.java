package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.Budget;

import java.util.ArrayList;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ItemViewHolder> {

    Context context;
    ArrayList<Budget> mBudgets;

    public BudgetAdapter(Context context, ArrayList<Budget> mBudgets) {
        this.context = context;
        this.mBudgets = mBudgets;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImage;
        public TextView mSpendingAmount,mSpendingType,mBudget,mUsedPercent;

        public ItemViewHolder(View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.image);
            mSpendingAmount = itemView.findViewById(R.id.spending_amount);
            mSpendingType = itemView.findViewById(R.id.spending_type);
            mBudget = itemView.findViewById(R.id.budget);
            mUsedPercent = itemView.findViewById(R.id.used_percent);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_item_row,parent,false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position)
    {
           Budget budget = mBudgets.get(position);

           switch (budget.getImageId())
           {
               case "1":
                  setImage(holder,R.drawable.ic_income);
                   break;
               case "2":
                   setImage(holder,R.drawable.ic_automobile);
                   break;
               case "3":
                   setImage(holder,R.drawable.ic_autorepairs);
                   break;
               case "4":
                   setImage(holder,R.drawable.ic_bankcharges);
                   break;
               case "5":
                   setImage(holder,R.drawable.ic_childecare);
                   break;
               case "6":
                   setImage(holder,R.drawable.ic_education);
                   break;
               case "7":
                   setImage(holder,R.drawable.ic_events);
                   break;
               case "8":
                   setImage(holder,R.drawable.ic_food);
                   break;
               case "9":
                   setImage(holder,R.drawable.ic_gifts);
                   break;
               case "10":
                   setImage(holder,R.drawable.ic_charity);
                   break;
               case "11":
                   setImage(holder,R.drawable.ic_healthcare);
                   break;
               case "12":
                   setImage(holder,R.drawable.ic_medicine);
                   break;
               case "13":
                   setImage(holder,R.drawable.ic_household);
                   break;
               case "14":
                   setImage(holder,R.drawable.ic_insurance);
                   break;
               case "15":
                   setImage(holder,R.drawable.ic_jobexpenses);
                   break;
               case "16":
                   setImage(holder,R.drawable.ic_leisure);
                   break;
               case "17":
                   setImage(holder,R.drawable.ic_hobbies);
                   break;
               case "18":
                   setImage(holder,R.drawable.ic_loan);
                   break;
               case "19":
                   setImage(holder,R.drawable.ic_petcare);
                   break;
               case "20":
                   setImage(holder,R.drawable.ic_childerncloth);
                   break;
               case "21":
                   setImage(holder,R.drawable.ic_adultcloth);
                   break;
               case "22":
                   setImage(holder,R.drawable.ic_savings);
                   break;
               case "23":
                   setImage(holder,R.drawable.ic_tax);
                   break;
               case "24":
                   setImage(holder,R.drawable.ic_utilities);
                   break;
               case "25":
                   setImage(holder,R.drawable.ic_vacation);
                   break;
               case "26":
                   setImage(holder,R.drawable.ic_mobile);
                   break;
               case "27":
                   setImage(holder,R.drawable.ic_internet);
                   break;
           }
           holder.mSpendingAmount.setText("$"+budget.getSumOfSpent());
           holder.mBudget.setText("$"+budget.getBudget());
           holder.mSpendingType.setText(budget.getTitle());
           double result = Double.parseDouble(budget.getSumOfSpent()) / Integer.parseInt(budget.getBudget());
           holder.mUsedPercent.setText(String.format("%.2f", result)+"%");
    }

    @Override
    public int getItemCount() {
        return mBudgets.size();
    }


    private void setImage(ItemViewHolder holder,int resId)
    {
        holder.mImage.setImageResource(resId);
    }
}
