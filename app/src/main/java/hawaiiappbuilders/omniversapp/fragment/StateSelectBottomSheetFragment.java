package hawaiiappbuilders.omniversapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.adapters.StateAdapter;
import hawaiiappbuilders.omniversapp.model.StateDataProvider;

public class StateSelectBottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener, StateAdapter.RecyclerViewClickListener {

    Context context;
    String currState;

    SelectStateListener listener;

    RecyclerView recyclerStates;
    StateAdapter stateAdapter;

    public StateSelectBottomSheetFragment(String info, SelectStateListener listener) {
        this.currState = info;
        this.listener = listener;
    }

    @Override
    public void onClick(View view, int position) {
        if (listener!= null) {
            listener.onStateSelected(StateDataProvider.getInstance().getStateList().get(position).abbr);
        }
        dismiss();
    }

    public interface SelectStateListener {
        void onStateSelected(String statePrefix);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_state_sheet, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerStates = view.findViewById(R.id.recyclerStates);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerStates.setLayoutManager(layoutManager);
        stateAdapter = new StateAdapter(context, StateDataProvider.getInstance().getStateList(), currState, this);
        recyclerStates.setAdapter(stateAdapter);

        view.findViewById(R.id.btnCancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnCancel) {
            dismiss();
        }
    }
}
