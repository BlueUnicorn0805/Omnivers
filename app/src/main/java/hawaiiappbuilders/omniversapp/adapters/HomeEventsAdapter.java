package hawaiiappbuilders.omniversapp.adapters;

import static hawaiiappbuilders.omniversapp.appointment.ApptUtil.NEW_APPOINTMENT;
import static hawaiiappbuilders.omniversapp.utils.WebViewUtil.YOUTUBE_URL;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.ActivityHomeEvents;
import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.VolleySingleton;
import hawaiiappbuilders.omniversapp.model.Videos;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.WebViewUtil;

public class HomeEventsAdapter extends RecyclerView.Adapter<HomeEventsAdapter.ItemViewHolder> implements View.OnClickListener {

    BaseFunctions baseFunctions;
    private Context context;
    HomeEventsAdapter.RecyclerViewClickListener listener;

    private ArrayList<Videos> mEventDataList;
    private static final String TAG = HomeEventsAdapter.class.getSimpleName();

    private int selectedItemID = -1;

    private YoutubeVideoListener _videoListener;

    DataUtil dataUtil;

    public interface YoutubeVideoListener {
        void onVideoPlay(String videoID);
    }

    // Image Loader
//    ImageLoader mImageLoader;
//    DisplayImageOptions mImageOptions;

    public HomeEventsAdapter(Context context, ArrayList<Videos> eventList, YoutubeVideoListener videoListener, HomeEventsAdapter.RecyclerViewClickListener listener) {
        this.context = context;
        this.mEventDataList = eventList;
        this._videoListener = videoListener;
        this.listener = listener;
        dataUtil = new DataUtil(context, HomeEventsAdapter.class.getSimpleName());

//        mImageLoader = ImageLoader.getInstance();
//        mImageLoader.init(ImageLoaderConfiguration.createDefault(context.getApplicationContext()));
//        mImageOptions = new DisplayImageOptions.Builder()
//                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//                .cacheInMemory(false)
//                .cacheOnDisk(false)
//                .considerExifParams(true)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .build();
        this.baseFunctions = new BaseFunctions(context, TAG);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        int position = (int) view.getTag();
        if (listener != null) {
            if (viewId == R.id.btnPurchase) {
                listener.onClick(view, position);
            } else if (viewId == R.id.btnLearnMore) {
                listener.onDetails(view, position);
            } else if (viewId == R.id.btnTrash) {
                listener.onRemoveItem(view, position);
            } else if (viewId == R.id.btnDonate) {
                listener.onClickDonate(view, position);
            }
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        /*public ImageView imgMarker;*/
        public ImageView imgBanner;
        public WebView webView;
        public View btnPlayV;
        /* public View btnViewImg;*/

        public TextView tvEventTitle, tvHeadLine, tvDateTime, tvMessages;

        public View btnPurchase;
        public Button btnDonate;
        public ImageView btnLearnMore;
        public ImageView btnTrash;
        public View btnEventShare;
        public TextView tvCategory;
        public View btnEventSchedule;
        public ImageView btnDirections;

        public ItemViewHolder(View itemView) {
            super(itemView);

            /* imgMarker = itemView.findViewById(R.id.imgMarker);*/

            imgBanner = itemView.findViewById(R.id.imgBanner);
            webView = itemView.findViewById(R.id.webView);
            btnPlayV = itemView.findViewById(R.id.btnPlayV);
            btnDirections = itemView.findViewById(R.id.btnDirections);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvHeadLine = itemView.findViewById(R.id.tvHeadLine);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvMessages = itemView.findViewById(R.id.tvMessages);

            btnPurchase = itemView.findViewById(R.id.btnPurchase);
            btnDonate = itemView.findViewById(R.id.btnDonate);
            btnLearnMore = itemView.findViewById(R.id.btnLearnMore);
            btnEventShare = itemView.findViewById(R.id.btnEventShare);
            btnEventSchedule = itemView.findViewById(R.id.btnEventSchedule);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnTrash = itemView.findViewById(R.id.btnTrash);

        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_event, parent, false);
        return new ItemViewHolder(v);
    }

    private String getCategory(int categoryId) {
        String[] categories = context.getResources().getStringArray(R.array.array_categories);
        return categories[categoryId];
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String imgLink = getVideImageLink(position);

        holder.tvEventTitle.setText(mEventDataList.get(position).getTitle());
        holder.tvHeadLine.setText(mEventDataList.get(position).getHeadLine());
        Date parseDate = DateUtil.parseDataFromFormat28(mEventDataList.get(position).getStartDate());
        Date parseStopDate = DateUtil.parseDataFromFormat28(mEventDataList.get(position).getStopDate());
        holder.tvDateTime.setText(DateUtil.toStringFormat_7(parseDate) + " - " + DateUtil.toStringFormat_7(parseStopDate));

        holder.tvMessages.setText(mEventDataList.get(position).getDescript());
        holder.tvCategory.setText(getCategory(mEventDataList.get(position).getCatID()));
        // Display Image
        if (getVideImageLink(position) != null) {
//            mImageLoader.displayImage(getVideImageLink(position), holder.imgBanner, mImageOptions);
            Glide.with(context)
                    .load(String.format(getVideImageLink(position)))
                    .centerCrop()
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // You can adjust the target size as per your requirement
                    .fitCenter() // Adjust the scaling as needed
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk caching
                    .skipMemoryCache(true) // Disable memory caching
                    .into(holder.imgBanner);
        }

      /*  holder.imgMarker.setVisibility(View.GONE);
        holder.btnViewImg.setVisibility(View.GONE);*/

        holder.imgBanner.setVisibility(View.VISIBLE);
        holder.btnPlayV.setVisibility(View.VISIBLE);
        holder.webView.setVisibility(View.INVISIBLE);

        holder.btnLearnMore.setTag(position);
        holder.btnLearnMore.setOnClickListener(this);
        holder.btnTrash.setTag(position);
        holder.btnTrash.setOnClickListener(this);

        holder.btnEventShare.setTag(position);
        holder.btnEventShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShare(v, position);
            }
        });

        holder.btnEventSchedule.setTag(position);
        holder.btnEventSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Set a Reminder")
                        .setMessage("Add this event to your Calendar.")
                        .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addAppointment(position);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

        // Click Action
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);

        WebViewUtil.initialize(context, holder.webView);

        holder.btnPlayV.setTag(position);
        holder.btnPlayV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Videos bodyVideo = mEventDataList.get(position);

                holder.btnPlayV.setVisibility(View.INVISIBLE);
                holder.imgBanner.setVisibility(View.INVISIBLE);
                holder.webView.setVisibility(View.VISIBLE);

                holder.webView.setTag(position);
                WebViewUtil.initialize(context, holder.webView).loadUrl(YOUTUBE_URL + mEventDataList.get(position).getLink());
                holder.webView.loadUrl(getVideLink(position));

                /*if (_videoListener != null) {
                    _videoListener.onVideoPlay(bodyVideo.getLink());
                }*/
            }
        });


        if (mEventDataList.get(position).getTkReq() == 0) {
            holder.btnPurchase.setVisibility(View.GONE);
        } else {
            holder.btnPurchase.setVisibility(View.VISIBLE);
        }
        holder.btnPurchase.setTag(position);
        holder.btnPurchase.setOnClickListener(this);

        holder.btnDirections.setTag(position);
        holder.btnDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof ActivityHomeEvents) {
                    ((ActivityHomeEvents) context).zoomMapPin(position);
                }

                String addressCoordinates = String.format("%f, %f", mEventDataList.get(position).getLat(), mEventDataList.get(position).getLon());
                Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%s&mode=d", addressCoordinates));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });

        holder.btnDonate.setTag(position);
        holder.btnDonate.setOnClickListener(this);
    }

    public void addAppointment(int position) {
        BaseActivity activity = ((BaseActivity) context);
        if (activity.getLocation()) {
            Date eventStartDate = DateUtil.parseDataFromFormat20(mEventDataList.get(position).getStartDate());
            Date eventStopDate = DateUtil.parseDataFromFormat20(mEventDataList.get(position).getStopDate());

            Calendar cStart = Calendar.getInstance();
            Calendar cStop = Calendar.getInstance();

            // Add 8 hrs to start date
            cStart.setTime(eventStartDate);
            cStart.add(Calendar.HOUR_OF_DAY, 8);

            // Get hours of start date and add 30 mins
            cStop.setTime(eventStopDate);
            cStop.add(Calendar.HOUR_OF_DAY, cStart.get(Calendar.HOUR_OF_DAY));
            cStop.add(Calendar.MINUTE, 30);

            Date newStartDate = new Date(cStart.getTimeInMillis());
            Date newStopDate = new Date(cStop.getTimeInMillis());

            long timeDiff = eventStopDate.getTime() - eventStartDate.getTime();
            long minuteDiff = (timeDiff / (1000 * 60)) % 60;
            AppSettings appSettings = new AppSettings(context);
            HashMap<String, String> params = new HashMap<>();
            /*String baseUrl = BaseFunctions.getBaseUrl(context,
                    "setAppt",
                    BaseFunctions.MAIN_FOLDER,
                    activity.getUserLat(),
                    activity.getUserLon(),
                    ((KTXApplication)activity.getApplication()).getAndroidId());

            String extraParams =
                    "&mode=" + "0" +
                    "&SetByID=" + appSettings.getUserId() +
                    "&tolat=" + mEventDataList.get(position).getLat() +
                    "&tolon=" + mEventDataList.get(position).getLon() +
                    "&industryID=" + appSettings.getIndustryid() +
                    "&ApptWithMLID=" + "0" +
                    "&meetingID=" + "0" +
                    "&CP=" + "" +
                    "&Email=" + mEventDataList.get(position).getEmail() +
                    "&EventTitle=" + mEventDataList.get(position).getTitle() +
                    "&promoid=" + "420" +
                    "&Amt=" + mEventDataList.get(position).getGenAdmission() +
                    "&OrderID=" + "0" +
                    "&qty=" + "0" +
                    "&apptTime=" + mEventDataList.get(position).getStartDate() +
                    "&mins=" + minuteDiff +
                    "&buyerID=" + "0" +
                    "&NoteID=" + "0" +
                    "&FN=" + "" +
                    "&LN=" + "";*/

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("promoid", 420);
                jsonObject.put("industryID", 0);
                jsonObject.put("attendeeMLID", 0);
                jsonObject.put("LDBID", 0);
                jsonObject.put("meetingID", 0);
                jsonObject.put("sellerID", 0);
                jsonObject.put("orderID", 0);
                jsonObject.put("mode", 0);
                jsonObject.put("amt", mEventDataList.get(position).getGenAdmission());
                jsonObject.put("TZ", appSettings.getUTC()); // PTZ, MTZ, CTZ, ETZ
                jsonObject.put("apptTime", DateUtil.toStringFormat_12(newStartDate));
                jsonObject.put("apptTimeEnd", DateUtil.toStringFormat_12(newStopDate));
                jsonObject.put("eventTitle", mEventDataList.get(position).getTitle());
                jsonObject.put("address", mEventDataList.get(position).getLocation());
                jsonObject.put("apptLon", mEventDataList.get(position).getLon());
                jsonObject.put("apptLat", mEventDataList.get(position).getLat());
                jsonObject.put("cp", "");
                jsonObject.put("email", mEventDataList.get(position).getEmail());
                jsonObject.put("mins", 0);
                jsonObject.put("videoMeetingURL", "");
                jsonObject.put("videoMeetingID", "");
                jsonObject.put("videoPasscode", "");
                jsonObject.put("videoAutoPhoneDial", "");
                jsonObject.put("miscUN", "");
                jsonObject.put("miscPW", "");
                jsonObject.put("qty", 0);
                jsonObject.put("editApptID", 0);
                jsonObject.put("share", "");
                jsonObject.put("newStatusID", NEW_APPOINTMENT);
                String company;
                if (appSettings.getCompany().isEmpty() && appSettings.getCompany() == null) {
                    company = "-";
                } else {
                    company = appSettings.getCompany();
                }

                String name = appSettings.getFN() + " " + appSettings.getLN();
                String senderName;
                if (company.contentEquals("-")) {
                    senderName = name;
                } else {
                    senderName = company + "\n" + name;
                }

                jsonObject.put("senderName", senderName);

                String baseUrl = BaseFunctions.getBaseData(jsonObject, activity.getApplicationContext(),
                        "setAppt", BaseFunctions.MAIN_FOLDER, activity.getUserLat(), activity.getUserLon(), ((KTXApplication) activity.getApplication()).getAndroidId());

                Log.e("Request", baseUrl);

                GoogleCertProvider.install(context);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("setAppt", response);
                        try {
                            JSONArray responseArray = new JSONArray(response);
                            JSONObject responseObject = responseArray.getJSONObject(0);
                            if (responseObject.has("status") && !responseObject.getBoolean("status")) {
                                activity.showToastMessage("An error occurred");
                            } else {
                                activity.showToastMessage("Event added to your calendar");
                            }
                        } catch (JSONException e) {
                            // e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        baseFunctions.handleVolleyError(context, error, TAG, BaseFunctions.getApiName(baseUrl));
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        25000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                stringRequest.setShouldCache(false);
                VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
            } catch (JSONException e) {
                dataUtil.setActivityName(HomeEventsAdapter.class.getSimpleName());
                dataUtil.zzzLogIt(e, "setAppt");
                e.printStackTrace();
            }
        }

    }

    public String getVideImageLink(int position) {

        String youtubeID = mEventDataList.get(position).getLink();
        if (TextUtils.isEmpty(youtubeID)) {
            return "";
        } else {
            return String.format("http://img.youtube.com/vi/%s/0.jpg", youtubeID);
        }
    }

    public String getVideLink(int position) {
        String youtubeID = mEventDataList.get(position).getLink();
        if (TextUtils.isEmpty(youtubeID)) {
            return "";
        } else {
            return "https://www.youtube.com/embed/" + youtubeID;
        }
    }

    @Override
    public int getItemCount() {
        if (mEventDataList == null)
            return 0;

        return mEventDataList.size();
    }

    public interface RecyclerViewClickListener {
        void onShare(View view, int position);

        void onClick(View view, int position);

        void onDetails(View view, int position);

        void onRemoveItem(View view, int position); // remove from memory only

        void onClickDonate(View view, int position);
    }
}