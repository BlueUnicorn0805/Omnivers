package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.autocomplete.Prediction;

public class PredictionsAdapter extends ArrayAdapter<Prediction> {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Prediction> predictions;

    public PredictionsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Prediction> predictions) {
        super(context, resource, predictions);
        this.context = context;
        this.predictions = predictions;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_list_item, parent, false);
        Prediction prediction = predictions.get(position);
        TextView item = row.findViewById(R.id.name);
        item.setText(prediction.description);
        return row;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
         View row = inflater.inflate(R.layout.spinner_list_item, parent, false);
        Prediction prediction = predictions.get(position);
        TextView item = row.findViewById(R.id.name);
        item.setText(prediction.description);
        return row;
    }
}
