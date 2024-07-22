package hawaiiappbuilders.omniversapp;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityDepositCheck extends BaseActivity {

    /*private static final int REQUEST_CODE = 99;
    // private Button scanButton;
    private Button cameraButton;
    private Button mediaButton;
    private ImageView scannedImageView;

    private TextView textAmountLabel;
    private TextView textAmount;
    private TextView textBankName;
    private TextView textName;
    private TextView textAccountNumber;
    private TextView textCheckNumber;

    private TextView textRoutingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_check);

        init();
    }

    private void init() {
        *//*scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new ScanButtonClickListener());*//*
        cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_CAMERA));
        mediaButton = (Button) findViewById(R.id.mediaButton);
        mediaButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_MEDIA));
        scannedImageView = (ImageView) findViewById(R.id.scannedImage);

        // check details
        textAmountLabel = findViewById(R.id.textAmountLabel);
        textAmount = findViewById(R.id.textAmount);

        textBankName = findViewById(R.id.textBankName);
        textName = findViewById(R.id.textName);
        textAccountNumber = findViewById(R.id.textAccountNumber);
        textCheckNumber = findViewById(R.id.textCheckNumber);
        textRoutingNumber = findViewById(R.id.textRoutingNumber);
    }

    private void showCheckDetails(boolean show) {
        int isShown = show ? View.VISIBLE : View.GONE;
        textBankName.setVisibility(isShown);
        textName.setVisibility(isShown);
        textAccountNumber.setVisibility(isShown);
        textCheckNumber.setVisibility(isShown);
        textRoutingNumber.setVisibility(isShown);

        textAmount.setText("$ 0.00");
    }

    private class ScanButtonClickListener implements View.OnClickListener {

        private int preference;

        public ScanButtonClickListener(int preference) {
            this.preference = preference;
        }

        public ScanButtonClickListener() {
        }

        @Override
        public void onClick(View v) {
            startScan(preference);
        }
    }

    protected void startScan(int preference) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                scannedImageView.setImageBitmap(bitmap);

                showCheckDetails(true);

                // todo: extract check details via OCR dependency

                // todo: set dummy data for now

                displayAmount(4578320323.23);
                textBankName.setText("Bank:  Your Financial Institution");
                textName.setText("Name:  " + appSettings.getFN() + " " + appSettings.getLN());
                textAccountNumber.setText("Account#:  003810-2931-35");
                textCheckNumber.setText("Check No.:  014202");
                textRoutingNumber.setText("RT No.:  42236 2423");

                sendEmail(uri);
            } catch (IOException e) {
                e.printStackTrace();
                displayAmount(0.00);
                showCheckDetails(false);
            }
        }
    }

    public void sendEmail(Uri uri) {
        try {
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{appSettings.getEmail()});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Deposit Check");
            if (uri != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            }

            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Review Deposit Check");
            this.startActivity(Intent.createChooser(emailIntent, "Sending email..."));
        } catch (Throwable t) {
            Toast.makeText(this, "Request failed try again: "+ t.toString(), Toast.LENGTH_LONG).show();
        }
    }
    // checks@halopay.app
    // api returns transaction#
    //

    private void displayAmount(double amt) {
        DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
        textAmount.setText("$ " + formatter.format(amt));
    }

    private Bitmap convertByteArrayToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        *//*if (id == R.id.action_settings) {
            return true;
        }*//*

        return super.onOptionsItemSelected(item);
    }*/
}
