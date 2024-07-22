package hawaiiappbuilders.omniversapp.milestone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ProfessionalAdapter extends RecyclerView.Adapter<ProfessionalAdapter.ItemViewHolder> {
    Context context;
    BaseActivity activity;
    ArrayList<Professional> mProfessionals;

    public interface OnClickProfessionalListener {
        void onClickProfessional(Professional professional);
    }

    private ProfessionalAdapter.OnClickProfessionalListener listener;

    public ProfessionalAdapter(Context context, ArrayList<Professional> professionals, ProfessionalAdapter.OnClickProfessionalListener listener) {
        this.context = context;
        this.activity = (BaseActivity) context;
        this.mProfessionals = professionals;
        this.listener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public View layoutView;
        public TextView textStatus;
        public TextView textName;
        public TextView textTitle;
        public TextView textReviews;

        public ItemViewHolder(View itemView) {
            super(itemView);
            layoutView = itemView.findViewById(R.id.layoutView);
            textStatus = itemView.findViewById(R.id.textStatus);
            textName = itemView.findViewById(R.id.textName);
            textTitle = itemView.findViewById(R.id.textTitle);
            textReviews = itemView.findViewById(R.id.textReviews);
        }
    }

    @NonNull
    @Override
    public ProfessionalAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_professional, parent, false);
        return new ProfessionalAdapter.ItemViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProfessionalAdapter.ItemViewHolder holder, int position) {
        Professional professional = mProfessionals.get(position);
        holder.layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickProfessional(professional);
                }
            }
        });
        holder.textStatus.setText(professional.getStatus());
        holder.textName.setText(professional.getName());
        holder.textTitle.setText(professional.getTitle());
        holder.textReviews.setText(professional.getReviews() + " Reviews");

    }


    @Override
    public int getItemCount() {
        return mProfessionals.size();
    }


}

