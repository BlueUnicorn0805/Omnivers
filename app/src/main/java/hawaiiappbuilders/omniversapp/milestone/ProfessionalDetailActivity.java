package hawaiiappbuilders.omniversapp.milestone;

import static hawaiiappbuilders.omniversapp.milestone.JobDescAdapter.formatAmount;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ProfessionalDetailActivity extends BaseActivity {

    Professional professional;

    TextView textStatus;
    TextView textNameJobTitle;
    TextView textReviews;
    TextView textServicesOffered;
    TextView textFAQ;

    EditText etRequirements;
    Button btnSubmitRequirements;
    Button btnMessageProfessional;

    ArrayList<FAQ> faqs;

    public static class FAQ {
        String question;
        String answer;

        public FAQ() {
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_detail);

        faqs = new ArrayList<>();
        professional = getIntent().getParcelableExtra("professional");
        etRequirements = findViewById(R.id.edtRequirements);
        etRequirements.setHint(HtmlCompat.fromHtml("Send requirements so <b>" + professional.getName() + "</b> can start the project", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textStatus = findViewById(R.id.textStatus);
        textNameJobTitle = findViewById(R.id.textNameJobTitle);
        textReviews = findViewById(R.id.textReviews);
        textServicesOffered = findViewById(R.id.textServicesOffered);
        textFAQ = findViewById(R.id.textFAQItems);
        btnSubmitRequirements = findViewById(R.id.btnSubmitRequirements);
        btnSubmitRequirements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etRequirements.getText() != null && !etRequirements.getText().toString().isEmpty()) {
                    reviewRequirements(etRequirements.getText().toString().trim());
                } else {
                    showToastMessage("Requirements should not be empty");
                }
            }
        });
        btnSubmitRequirements.setText(HtmlCompat.fromHtml("Submit Requirements <b>(" + formatAmount(professional.getStartingPayment()) + ")</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));

        btnMessageProfessional = findViewById(R.id.btnMessage);
        btnMessageProfessional.setText(HtmlCompat.fromHtml("Message <b>" + professional.getName() + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
        btnMessageProfessional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, professional.getEmail());
                intent.putExtra(Intent.EXTRA_SUBJECT, "Milestone Contracts - " + appSettings.getFN() + " " + appSettings.getLN());
                intent.putExtra(Intent.EXTRA_TEXT, "Regarding the project...\n\n");
                startActivity(intent);
            }
        });

        textStatus.setText(professional.getStatus());
        textNameJobTitle.setText(HtmlCompat.fromHtml("<b>" + professional.getName() + "</b><br />" + professional.getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        textReviews.setText(professional.getReviews() + " Reviews");

        textServicesOffered.setText(professional.getServicesOffered());

        FAQ faq1 = new FAQ();
        faq1.setQuestion("What if I don't approve your work?");
        faq1.setAnswer("You can request a refund");
        faqs.add(faq1);

        FAQ faq2 = new FAQ();
        faq2.setQuestion("Do you accept long term projects?");
        faq2.setAnswer("Yes.  Depending on the scope of the project.  That will be subject for additional payment charges as it will require longer delivery time");
        faqs.add(faq2);

        StringBuilder stringBuilder = new StringBuilder();
        for (FAQ faq : faqs) {
            String faqItem = "<b>" + faq.getQuestion() + "</b><br /><small>" + faq.getAnswer() + "</small><br /><br />";
            stringBuilder.append(faqItem);
        }
        textFAQ.setText(HtmlCompat.fromHtml(stringBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY));

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
    }

    private void reviewRequirements(String requirements) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_review_requirements, null);
        AlertDialog askDialog = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                .setView(dialogView)
                .setCancelable(true)
                .create();


        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        Button btnSubmit = (Button) dialogView.findViewById(R.id.btnOk);
        TextView textRequirementsToSubmit = (TextView) dialogView.findViewById(R.id.textRequirementsToSubmit);
        textRequirementsToSubmit.setText(requirements);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askDialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastMessage("Your requirements have been submitted.  Please wait for feedback through your email");
                askDialog.dismiss();
            }
        });

        askDialog.show();
    }
}
