package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import hawaiiappbuilders.omniversapp.ActivityInvoice;
import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.VolleySingleton;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.model.Transaction;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ItemViewHolder> {
private static final String TAG = TransactionAdapter.class.getSimpleName();
    Context context;
    BaseActivity activity;
    ArrayList<Transaction> mTransactions;

    BaseFunctions baseFunctions;
    public TransactionAdapter(Context context, ArrayList<Transaction> mTransactions) {
        this.context = context;
        this.baseFunctions = new BaseFunctions(this.context, TAG);
        this.activity = (BaseActivity) context;
        this.mTransactions = mTransactions;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public View itemTransaction;
        public TextView mTransactionEmail, mTransactionAmount, mTransactionDate, mTransactionId, mTransactionStatus;
        public TextView mTransactionMemo;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemTransaction = itemView.findViewById(R.id.itemTransaction);
            mTransactionEmail = itemView.findViewById(R.id.transaction_email);
            mTransactionAmount = itemView.findViewById(R.id.transaction_amount);
            mTransactionDate = itemView.findViewById(R.id.transaction_date);
            mTransactionId = itemView.findViewById(R.id.transaction_id);
            mTransactionStatus = itemView.findViewById(R.id.transaction_status);
            mTransactionMemo = itemView.findViewById(R.id.tvMemo);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item_row, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Transaction transaction = mTransactions.get(position);
        String transactionInfo = transaction.getName();
        transactionInfo = transactionInfo.replace("Fr:", "Fr: ");
        transactionInfo = transactionInfo.replace(",To:", "\nTo: ");
        holder.mTransactionEmail.setText(transactionInfo);

        double amt = 0;
        try {
            amt = Double.parseDouble(transaction.getAmt());
        } catch (Exception e) {
            e.printStackTrace();
        }
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        String currency = format.format(amt);
        holder.mTransactionAmount.setText(currency/*"$".concat(String.format("%.02f", amt))*/);

        int orderID = 0;
        try {
            orderID = Integer.parseInt(transaction.getTxID());
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.mTransactionDate.setText(convertDate(transaction.getItemDate()));
        holder.mTransactionId.setText("Tracking#: ".concat(String.format("%03d", orderID)));
        holder.mTransactionStatus.setText(transaction.getStatus());
        holder.itemTransaction.setTag(position);
        holder.mTransactionMemo.setText("Memo: " + transaction.getNote());
        holder.itemTransaction.setOnClickListener(itemClickListener);
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();

            Transaction transaction = mTransactions.get(position);

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(activity,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    activity.getUserLat(),
                    activity.getUserLon(),
                    ((KTXApplication) activity.getApplication()).getAndroidId());
            String extraParams =
                    "&mode=" + "OrderDetailsByTx" +
                            "&misc=" + transaction.getTxID();
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            activity.showProgressDialog();
            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(context);

            String finalBaseUrl = baseUrl;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.hideProgressDialog();
                    Log.e("OrderDetails", response);
                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;

                            Restaurant resInfo = null;
                            ArrayList<MenuItem> menuList = new ArrayList<>();
                            String orderID = "";
                            String orderDueAt = "";
                            int statusID = 0;

                            orderID = transaction.getTxID();
                            orderDueAt = transaction.getItemDate();

                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                activity.showToastMessage(jsonObject.getString("msg"));
                                return;

                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject dataObj = jsonArray.getJSONObject(i);

                                    // Get Restaurant information from First Order Info
                                    if (resInfo == null) {
                                        resInfo = new Restaurant();
                                        resInfo.set_address("");
                                        resInfo.set_name("");
                                        resInfo.set_city("");

                                        if (dataObj.has("address")) {
                                            resInfo.set_address(dataObj.getString("address"));
                                        } else {
                                            resInfo.set_address("No Address");
                                        }

                                        if (dataObj.has("Co")) {
                                            resInfo.set_name(dataObj.getString("Co"));
                                        } else {
                                            resInfo.set_name("No Name");
                                        }

                                        if (dataObj.has("CSZ")) {
                                            resInfo.set_csz(dataObj.getString("CSZ"));
                                        } else {
                                            resInfo.set_csz("No CSZ");
                                        }

                                        if (dataObj.has("OrderID")) {
                                            resInfo.set_id(dataObj.getInt("OrderID"));
                                        } else {
                                            resInfo.set_id(Integer.parseInt(orderID));
                                        }

                                        if (dataObj.has("TimePlaced")) {
                                            orderDueAt = dataObj.getString("TimePlaced");
                                        } else {
                                            orderDueAt = "No time";
                                        }

                                        statusID = dataObj.optInt("StatusID");
                                    }

                                    MenuItem newMenuItem = new MenuItem();
                                    newMenuItem.set_name(dataObj.getString("Name"));
                                    newMenuItem.set_description(dataObj.getString("Des"));
                                    newMenuItem.set_price(String.format("$%.2f", dataObj.getDouble("Price")));
                                    newMenuItem.set_size(dataObj.getString("Size"));
                                    newMenuItem.set_quantity(dataObj.getInt("Qty"));
                                    newMenuItem.set_taxfees(dataObj.getString("TaxFees"));
                                    newMenuItem.set_lineTot(dataObj.getString("LineTot"));
                                    newMenuItem.set_totPrice(dataObj.getString("TotPrice"));
                                    newMenuItem.set_subTotal(dataObj.getString("SubTotal"));

                                    menuList.add(newMenuItem);
                                }
                            }

                            Intent invoiceIntent = new Intent(activity, ActivityInvoice.class);
                            invoiceIntent.putExtra("restaurant", resInfo);
                            invoiceIntent.putExtra("orderid", orderID);
                            invoiceIntent.putExtra("datetime", orderDueAt);
                            invoiceIntent.putExtra("menus", menuList);
                            invoiceIntent.putExtra("statusID", statusID);
                            activity.startActivity(invoiceIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            activity.showAlert(e.getMessage());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activity.hideProgressDialog();
                    baseFunctions.handleVolleyError(context, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                    activity.showToastMessage(error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            stringRequest.setShouldCache(false);
            VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
        }
    };

//    "05-27-24 12:05"
    private String convertDate(String dateString) {
        try {
            // 2023-02-03 05:33:06.983
            //create SimpleDateFormat object with source string date format
            SimpleDateFormat sdfSource = new SimpleDateFormat("MM-dd-yy HH:mm");

            //parse the string into Date object
            Date date = sdfSource.parse(dateString);

            //create SimpleDateFormat object with desired date format
            SimpleDateFormat sdfDestination = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm a");

            //parse the date into another format
            dateString = sdfDestination.format(date);
        } catch (ParseException pe) {
            System.out.println("Parse Exception : " + pe);
        }
        return dateString;
    }
}
