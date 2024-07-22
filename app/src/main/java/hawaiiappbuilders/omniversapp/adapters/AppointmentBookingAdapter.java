package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.IndustryInfo;

import java.util.ArrayList;
import java.util.List;

public class AppointmentBookingAdapter extends RecyclerView.Adapter<AppointmentBookingAdapter.ItemViewHolder> implements Filterable {

    private Context context;
    private AppointmentBookingAdapter.RecyclerViewClickListener recyclerViewClickListener;
    private List<IndustryInfo> searchableStringList;
    private List<IndustryInfo> searchableStringListFiltered = new ArrayList<>();

    public AppointmentBookingAdapter(Context _context, List<IndustryInfo> searchableStringList, AppointmentBookingAdapter.RecyclerViewClickListener recyclerViewClickListener) {
        this.context = _context;
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.searchableStringList = searchableStringList;
        this.searchableStringListFiltered = searchableStringList;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AppointmentBookingAdapter.RecyclerViewClickListener mListener;
        private TextView abHead;
        private TextView abTextView;

        public ItemViewHolder(View itemView, AppointmentBookingAdapter.RecyclerViewClickListener recyclerViewClickListener) {
            super(itemView);
            abHead = (TextView) itemView.findViewById(R.id.ab_head);
            abTextView = (TextView) itemView.findViewById(R.id.ab_textview);
            mListener = recyclerViewClickListener;
            abTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            mListener.onClick(v, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_booking_list_item, parent, false);
        return new ItemViewHolder(v, recyclerViewClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        IndustryInfo item = searchableStringListFiltered.get(position);
        if (item.isHead()) {
            holder.abHead.setVisibility(View.VISIBLE);
            holder.abTextView.setVisibility(View.GONE);

            holder.abHead.setText("" + item.getTypeDesc());
        } else {
            holder.abHead.setVisibility(View.GONE);
            holder.abTextView.setVisibility(View.VISIBLE);

            holder.abTextView.setText("" + item.getTypeDesc());
        }
    }

    @Override
    public int getItemCount() {
        return searchableStringListFiltered.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                Log.e("XXXXXXXXXXXXX", "performFiltering: " + charSequence.toString());
                if (charString.isEmpty()) {
                    searchableStringListFiltered = searchableStringList;//new ArrayList<>();
                } else {
                    List<IndustryInfo> filteredList = new ArrayList<>();
                    for (IndustryInfo row : searchableStringList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTypeDesc().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    searchableStringListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = searchableStringListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                Log.e("XXXXXXXXXXXXX", "publishResults: " + charSequence.toString());
                searchableStringListFiltered = (ArrayList<IndustryInfo>) filterResults.values;
                // refresh the list with filtered data
                notifyDataSetChanged();
                if (searchableStringListFiltered.size() > 0) {
                    recyclerViewClickListener.onSearchResultFound(true);
                } else {
                    recyclerViewClickListener.onSearchResultFound(false);
                }
            }
        };
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);

        void onSearchResultFound(boolean hasResult);
    }
}
