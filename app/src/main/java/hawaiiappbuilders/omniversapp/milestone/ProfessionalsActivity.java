package hawaiiappbuilders.omniversapp.milestone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ProfessionalsActivity extends BaseActivity {
    Context mContext;
    RecyclerView rvProfessional;
    ArrayList<Professional> mProfessionals;

    ProfessionalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_professionals);
        mContext = this;
        mProfessionals = new ArrayList<>();
        rvProfessional = findViewById(R.id.rv_professionals);
        Professional professional1 = new Professional();
        professional1.setStatus("Available");
        professional1.setName("John Doe");
        professional1.setTitle("Researcher/Writer");
        professional1.setReviews(239);
        professional1.setEmail("johndoe@z99.io");
        professional1.setServicesOffered("SERVICES I OFFER\n1 RESEARCH WRITER\n2 Journalistic Writing\n\n3 times a week availability");
        professional1.setStartingPayment(90.50);
        mProfessionals.add(professional1);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnToolbarHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        rvProfessional.setHasFixedSize(true);
        rvProfessional.setLayoutManager(new LinearLayoutManager(mContext));
        rvProfessional.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new ProfessionalAdapter(mContext, mProfessionals, new ProfessionalAdapter.OnClickProfessionalListener() {
            @Override
            public void onClickProfessional(Professional professional) {
                Intent intent = new Intent(mContext, ProfessionalDetailActivity.class);
                intent.putExtra("professional", professional);
                startActivity(intent);
                //todo:
            }
        });
        rvProfessional.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
