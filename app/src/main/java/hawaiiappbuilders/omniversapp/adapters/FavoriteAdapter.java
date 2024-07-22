package hawaiiappbuilders.omniversapp.adapters;

import static hawaiiappbuilders.omniversapp.global.BaseActivity.FITSERVER_CURBSIDE;
import static hawaiiappbuilders.omniversapp.global.BaseActivity.FITSERVER_Cater;
import static hawaiiappbuilders.omniversapp.global.BaseActivity.FITSERVER_DELIVERY;
import static hawaiiappbuilders.omniversapp.global.BaseActivity.FITSERVER_DIRS;
import static hawaiiappbuilders.omniversapp.global.BaseActivity.FITSERVER_DONATE;
import static hawaiiappbuilders.omniversapp.global.BaseActivity.FITSERVER_Favs;
import static hawaiiappbuilders.omniversapp.global.BaseActivity.FITSERVER_Menus;
import static hawaiiappbuilders.omniversapp.global.BaseActivity.FITSERVER_Party;
import static hawaiiappbuilders.omniversapp.global.BaseActivity.FITSERVER_ResTabs;
import static hawaiiappbuilders.omniversapp.global.BaseActivity.FITSERVER_Vids;
import static hawaiiappbuilders.omniversapp.utils.WebViewUtil.YOUTUBE_URL;

import android.content.ActivityNotFoundException;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.MapFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.ActivityAddAppointment;
import hawaiiappbuilders.omniversapp.ActivityCatering;
import hawaiiappbuilders.omniversapp.ActivityFavorite;
import hawaiiappbuilders.omniversapp.FoodCheckoutActivity;
import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.MenuListActivity;
import hawaiiappbuilders.omniversapp.QRCodeScanResultActivity;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.RestaurantListActivity;
import hawaiiappbuilders.omniversapp.ServiceListActivity;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.IndustryInfo;
import hawaiiappbuilders.omniversapp.model.MenuHeader;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.WebViewUtil;

public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = FavoriteAdapter.class.getSimpleName();
    BaseFunctions baseFunctions;
    private LayoutInflater inflater;
    public ArrayList<Restaurant> restList;
    private Context _ctx;
    private BaseActivity _activity;
    private IndustryInfo _industryItem;

    int colorRed = 0xFFFF0000;
    int colorGrey = 0xFF676767;

    ImageLoader _imageLoader;
    DisplayImageOptions _imageOptions;

    public FavoriteAdapter(Context ctx, ArrayList<Restaurant> restList, IndustryInfo industryItem, ImageLoader imageLoader, DisplayImageOptions imageOptions) {

        inflater = LayoutInflater.from(ctx);
        this.restList = restList;
        this._ctx = ctx;
        this._activity = (BaseActivity) ctx;
        this._industryItem = industryItem;

        this._imageLoader = imageLoader;
        this._imageOptions = imageOptions;
        this.baseFunctions = new BaseFunctions(this._ctx, TAG);
    }

    @Override
    public int getItemViewType(int position) {
        return restList.get(position).get_industryID();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (viewType == 123) {
            view = inflater.inflate(R.layout.item_restaurant, parent, false);
            RestMenuViewHolder holder = new RestMenuViewHolder(view);

            return holder;
        } else {
            view = inflater.inflate(R.layout.item_favorite, parent, false);
            NormalServiceViewHolder holder = new NormalServiceViewHolder(view);

            return holder;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == 123) {
            ((RestMenuViewHolder) holder).showDetails(position);
        } else {
            ((NormalServiceViewHolder) holder).showDetails(position);
        }
    }

    View.OnClickListener btnBanneImageListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Restaurant resInfo = restList.get(position);

            // Hit Server
            _activity.hitServer(FITSERVER_Vids, String.valueOf(resInfo.get_industryID()), resInfo.get_id());

            WebView wv = (WebView) ((AppCompatActivity) _ctx).findViewById(R.id.wvYouTube);
            MapFragment mapFragment = (MapFragment) ((AppCompatActivity) _ctx).getFragmentManager().findFragmentById(R.id.restMap);

            if (Restaurant.hasEmptyValue(resInfo.getLink())) {
                _activity.showAlert("Sorry, not available at this time");

                wv.setVisibility(View.GONE);
                mapFragment.getView().setVisibility(View.VISIBLE);
            } else {

                wv.setVisibility(View.VISIBLE);
                mapFragment.getView().setVisibility(View.GONE);

                WebViewUtil.initialize(_ctx, wv).loadUrl(YOUTUBE_URL + resInfo.getLink());

                Toast.makeText(_ctx, "Play video", Toast.LENGTH_LONG).show();
            }
        }
    };

    View.OnClickListener btnAddressClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            try {
                //Uri gmmIntentUri = Uri.parse(String.format("geo:%f,%f", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%f,%f&mode=d", restList.get(position).get_lattiude(), restList.get(position).get_longitude()));

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                //if (mapIntent.resolveActivity(_ctx.getPackageManager()) != null) {
                _ctx.startActivity(mapIntent);
                //}

            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }

            _activity.hit1Server(FITSERVER_DIRS, restList.get(position).get_id());
        }
    };

    View.OnClickListener btnFavoriteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Restaurant resInfo = restList.get(position);

            Toast.makeText(_ctx, "Added to Favorites", Toast.LENGTH_LONG).show();

            _activity.hit1Server(FITSERVER_Favs, restList.get(position).get_id());
        }
    };

    View.OnClickListener btnMapClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Restaurant resInfo = restList.get(position);
            _activity.hitServer(FITSERVER_DIRS, String.valueOf(resInfo.get_industryID()), resInfo.get_id());

            if (_ctx instanceof ServiceListActivity) {
                ((ServiceListActivity) _ctx).zoomMapPin(position);
            } else {
                ((ActivityFavorite) _ctx).zoomMapPin(position);
            }
        }
    };

    View.OnClickListener btnDonateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Restaurant resInfo = restList.get(position);

            View dialogView = _activity.getLayoutInflater().inflate(R.layout.dialog_donate, null);

            final android.app.AlertDialog inputDlg = new android.app.AlertDialog.Builder(_activity, R.style.AlertDialogTheme)
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

            final CheckBox chkAnonymous = dialogView.findViewById(R.id.chkAnonymous);
            final EditText edtTips = (EditText) dialogView.findViewById(R.id.edtTips);

            // Button Actions
            dialogView.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    _activity.showToastMessage("Thank you");
                    inputDlg.dismiss();
                }
            });

            // Button Actions
            dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    inputDlg.dismiss();
                }
            });

            inputDlg.show();
            inputDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            _activity.hit1Server(FITSERVER_DONATE, restList.get(position).get_id());
        }
    };

    View.OnClickListener btnStoreMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final int position = (int) v.getTag();
            final Restaurant resInfo = restList.get(position);

            /*if (Restaurant.hasEmptyValue(resInfo.getOrders())) {
                _activity.showAlert("Sorry, not available at this time");
            } else {*/
            //TODO: will need to send the restaruant ID to the fragment to get the correct menu items

            if (_activity.getLocation()) {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(_activity,
                        "ProdsBySellerID",
                        BaseFunctions.MAIN_FOLDER,
                        _activity.getUserLat(),
                        _activity.getUserLon(),
                        ((KTXApplication) _activity.getApplication()).getAndroidId());
                //params.put("sellerID", String.valueOf(resInfo.get_id()));
                String extraParams =
                        "&storeid=" + String.valueOf(resInfo.get_id());
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                _activity.showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(_activity);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(_activity);

                String finalBaseUrl = baseUrl;
                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("getMenus", response);

                        _activity.hideProgressDialog();

                        ArrayList<MenuHeader> menuHeaderList = new ArrayList<>();
                        ArrayList<MenuItem> menuList = new ArrayList<>();

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);

                                if ("null".equalsIgnoreCase(jsonArray.getString(0))) {
                                    //_activity.showToastMessage("No products for booking");

                                    showInviteDialog(resInfo);
                                    return;
                                }

                                JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    _activity.showToastMessage(jsonObject.getString("msg"));
                                } else {
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        if ("null".equalsIgnoreCase(jsonArray.getString(i))) {
                                            break;
                                        }

                                        JSONObject dataObj = jsonArray.getJSONObject(i);

                                        MenuHeader newMenuHeader = new MenuHeader(dataObj.getString("catname"), "group");
                                        menuHeaderList.add(newMenuHeader);

                                        JSONArray menuItemArray = dataObj.getJSONArray("items");
                                        for (int j = 0; j < menuItemArray.length(); j++) {
                                            // Check NULL
                                            if ("null".equalsIgnoreCase(menuItemArray.getString(j))) {
                                                break;
                                            }

                                            JSONObject itemObj = menuItemArray.getJSONObject(j);
                                            MenuItem newMenuItem = new MenuItem();
                                            newMenuItem.set_name(itemObj.getString("name"));
                                            newMenuItem.set_description(itemObj.getString("des"));
                                            newMenuItem.set_id(itemObj.getInt("prodid"));
                                            newMenuItem.set_category(newMenuHeader.get_headerTitle());
                                            newMenuItem.set_hasOptions(false);

                                            if (i == 0) {
                                                if (j == 0) {
                                                    newMenuItem.set_imgResId(R.drawable.aptizer1);
                                                } else {
                                                    newMenuItem.set_imgResId(R.drawable.aptizer2);
                                                }
                                            } else {
                                                if (j == 0) {
                                                    newMenuItem.set_imgResId(R.drawable.fillet1);
                                                } else {
                                                    newMenuItem.set_imgResId(R.drawable.fillet2);
                                                }
                                            }

                                            // Random Price
                                            //newMenuItem.set_price(String.format("$%.2f", new Random().nextDouble() % 20));
                                            newMenuItem.set_price(String.format("$%.2f", itemObj.getDouble("price")));
                                            newMenuItem.set_size(itemObj.getString("size"));
                                            newMenuItem.set_taxable(itemObj.optInt("taxable"));

                                            menuList.add(newMenuItem);
                                        }
                                    }

                                    //_activity.startActivity(new Intent(_ctx, ActivityAddAppointment.class));

                                    Intent restmenuintent = new Intent(_ctx, ActivityAddAppointment.class);
                                    restmenuintent.putExtra("restaurant", resInfo);
                                    restmenuintent.putExtra("headermenus", menuHeaderList);
                                    restmenuintent.putExtra("itemmenus", menuList);
                                    _ctx.startActivity(restmenuintent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                _activity.showAlert(e.getMessage());
                            }
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _activity.hideProgressDialog();

                        baseFunctions.handleVolleyError(_ctx, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                        //showMessage(error.getMessage());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return params;
                    }
                };

                sr.setShouldCache(false);
                queue.add(sr);
            }

            //}
            _activity.hitServer(FITSERVER_Menus, "", restList.get(position).get_id());

        }
    };

    @Override
    public int getItemCount() {
        return restList.size();
    }

    class NormalServiceViewHolder extends RecyclerView.ViewHolder {

        protected int restID;
        protected TextView restName;
        protected TextView restWelcomeMsg;
        protected TextView restHours;
        protected TextView restDistance;
        protected RatingBar ratingBar;

        protected TextView restDescription;
        protected TextView restAddress;

        protected ImageButton ibrestFavorite;
        protected ImageButton ibrestMap;
        protected ImageButton ibrestDonate;

        protected Button ibrestBook;

        protected TextView ivTitleText;
        protected ImageView imgBanner;

        public NormalServiceViewHolder(View itemView) {
            super(itemView);

            restName = (TextView) itemView.findViewById(R.id.restName);
            restWelcomeMsg = (TextView) itemView.findViewById(R.id.restWelcomeMsg);

            restHours = (TextView) itemView.findViewById(R.id.restHours);
            restDistance = (TextView) itemView.findViewById(R.id.restDistance);

            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);

            restDescription = (TextView) itemView.findViewById(R.id.restDescription);

            restAddress = (TextView) itemView.findViewById(R.id.restAddress);

            ibrestFavorite = (ImageButton) itemView.findViewById(R.id.ibrestFavorite);
            ibrestMap = (ImageButton) itemView.findViewById(R.id.ibrestMap);
            ibrestDonate = (ImageButton) itemView.findViewById(R.id.ibrestDonate);

            ibrestBook = (Button) itemView.findViewById(R.id.ibrestBook);

            ivTitleText = (TextView) itemView.findViewById(R.id.ivTitleText);
            imgBanner = (ImageView) itemView.findViewById(R.id.ivBanner);
        }

        public void showDetails(int position) {
            Restaurant resInfo = restList.get(position);

            // Restaurant Name
            restName.setText(resInfo.get_name());

            // Welcome Message
            if (Restaurant.hasEmptyValue(resInfo.getWelcomeMsg())) {
                restWelcomeMsg.setText("");
                restWelcomeMsg.setVisibility(View.GONE);
            } else {
                restWelcomeMsg.setText(resInfo.getWelcomeMsg());
            }

            // Show Time
            boolean closed = resInfo.getClosed() > 0;
            if (closed) {
                restHours.setText("Closed");
                restHours.setTextColor(colorRed);
            } else {
                String timeValue = "";
                Calendar calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SUNDAY) {
                    timeValue = String.format("%s - %s", resInfo.getSunB(), resInfo.getSunE());
                } else if (dayOfWeek == Calendar.MONDAY) {
                    timeValue = String.format("%s - %s", resInfo.getMonB(), resInfo.getMonE());
                } else if (dayOfWeek == Calendar.TUESDAY) {
                    timeValue = String.format("%s - %s", resInfo.getTueB(), resInfo.getTueE());
                } else if (dayOfWeek == Calendar.WEDNESDAY) {
                    timeValue = String.format("%s - %s", resInfo.getWedB(), resInfo.getWedE());
                } else if (dayOfWeek == Calendar.THURSDAY) {
                    timeValue = String.format("%s - %s", resInfo.getThuB(), resInfo.getThuE());
                } else if (dayOfWeek == Calendar.FRIDAY) {
                    timeValue = String.format("%s - %s", resInfo.getFriB(), resInfo.getFriE());
                } else if (dayOfWeek == Calendar.SATURDAY) {
                    timeValue = String.format("%s - %s", resInfo.getSatB(), resInfo.getSatE());
                }

                if ("null - null".equalsIgnoreCase(timeValue)) {
                    restHours.setText("11:00 AM - 11:00 AM");
                    restHours.setTextColor(colorGrey);
                } else {
                    restHours.setText(timeValue);
                    restHours.setTextColor(colorGrey);
                }
            }

            // Distance
            String distanceString = resInfo.get_dist();
            try {
                double distanceMiles = Double.parseDouble(distanceString);
                distanceMiles = ((int) (distanceMiles * 10)) / 10.f;
                distanceString = String.format("%.1f mi", distanceMiles);
            } catch (NumberFormatException e) {
            }
            restDistance.setText(distanceString);

            // Description
            if (Restaurant.hasEmptyValue(resInfo.get_description())) {
                restDescription.setText("");
                restDescription.setVisibility(View.GONE);
            } else {
                restDescription.setText(resInfo.getWelcomeMsg());
            }

            // Restaurant ID
            restID = restList.get(position).get_id();

            String videoID = restList.get(position).getLink();

            // Restaurant Image
            if (TextUtils.isEmpty(videoID) || "null".equals(videoID)) {
//                imgBanner.setImageResource(R.mipmap.ic_launcher1_foreground);
                ivTitleText.setVisibility(View.VISIBLE);
                ivTitleText.setText(resInfo.get_name());
                imgBanner.setVisibility(View.GONE);
            } else {
                imgBanner.setVisibility(View.VISIBLE);
                String videoUrlFormat = "https://img.youtube.com/vi/" + videoID + "/0.jpg";
                _imageLoader.displayImage(videoUrlFormat, imgBanner, _imageOptions);
                imgBanner.setTag(position);
                imgBanner.setOnClickListener(btnBanneImageListener);
                ivTitleText.setVisibility(View.GONE);
            }

            // Restaurant Address String
            String addrString = resInfo.get_address();
            String cszString = resInfo.getStZipCity();
            String addressLines = String.format("%s\n%s", addrString, cszString);
            restAddress.setText(addressLines.trim());
            restAddress.setTag(position);
            restAddress.setOnClickListener(btnAddressClickListener);

            ratingBar.setRating(resInfo.get_rating());

            // Add to Favorite
            ibrestFavorite.setTag(position);
            ibrestFavorite.setOnClickListener(btnFavoriteClickListener);

            // Map
            ibrestMap.setTag(position);
            ibrestMap.setOnClickListener(btnMapClickListener);

            // Donate
            ibrestDonate.setTag(position);
            ibrestDonate.setOnClickListener(btnDonateClickListener);

            // Restaurant Menu
            ibrestBook.setTag(position);
            ibrestBook.setOnClickListener(btnStoreMenuClickListener);
        }
    }

    class RestMenuViewHolder extends RecyclerView.ViewHolder {

        protected int restID;

        protected View panelItem;
        protected TextView restName;
        protected TextView restWelcomeMsg;

        protected TextView restHours;
        protected TextView restDistance;
        protected TextView restAddress;
        protected RatingBar ratingBar;

        protected View ibrestFavorite;
        protected View ibrestMenu;
        protected View ibrestDirections;
        protected View ibrestVideo;

        protected View ibReserveTable;
        protected View ibCatering;
        protected View ibDelivery;
        protected View ibParty;

        protected View ibDonate;
        protected View ibCubside;

        protected TextView ivTitleText;
        protected ImageView imgBanner;

        public RestMenuViewHolder(View itemView) {
            super(itemView);

            panelItem = itemView.findViewById(R.id.panelItem);

            restName = (TextView) itemView.findViewById(R.id.restName);
            restWelcomeMsg = (TextView) itemView.findViewById(R.id.restWelcomeMsg);

            restHours = (TextView) itemView.findViewById(R.id.restHours);
            restDistance = (TextView) itemView.findViewById(R.id.restDistance);
            restAddress = (TextView) itemView.findViewById(R.id.restAddress);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);

            ibrestFavorite = itemView.findViewById(R.id.ibrestFavorite);
            ibrestMenu = itemView.findViewById(R.id.ibrestMenu);
            ibrestDirections = itemView.findViewById(R.id.ibrestDirections);
            ibrestVideo = itemView.findViewById(R.id.ibrestVideo);

            ibReserveTable = itemView.findViewById(R.id.ibReserveTable);
            ibCatering = itemView.findViewById(R.id.ibCatering);
            ibDelivery = itemView.findViewById(R.id.ibDelivery);
            ibParty = itemView.findViewById(R.id.ibParty);

            ibDonate = itemView.findViewById(R.id.ibDonate);
            ibCubside = itemView.findViewById(R.id.ibCurbside);

            ivTitleText = (TextView) itemView.findViewById(R.id.ivTitleText);
            imgBanner = (ImageView) itemView.findViewById(R.id.imgBanner);
        }

        public void showDetails(int position) {
            Restaurant resInfo = restList.get(position);

            restName.setText(resInfo.get_name());

            // Welcome Message
            if (Restaurant.hasEmptyValue(resInfo.getWelcomeMsg())) {
                restWelcomeMsg.setText("");
                restWelcomeMsg.setVisibility(View.GONE);
            } else {
                restWelcomeMsg.setText(resInfo.getWelcomeMsg());
                restWelcomeMsg.setVisibility(View.VISIBLE);
            }

            // Show Time
            boolean closed = resInfo.getClosed() > 0;
            if (closed) {
                restHours.setText("Closed Today");
                restHours.setTextColor(colorRed);
            } else {
                String timeValue = "";
                Calendar calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SUNDAY) {
                    timeValue = String.format("%s - %s", resInfo.getSunB(), resInfo.getSunE());
                } else if (dayOfWeek == Calendar.MONDAY) {
                    timeValue = String.format("%s - %s", resInfo.getMonB(), resInfo.getMonE());
                } else if (dayOfWeek == Calendar.TUESDAY) {
                    timeValue = String.format("%s - %s", resInfo.getTueB(), resInfo.getTueE());
                } else if (dayOfWeek == Calendar.WEDNESDAY) {
                    timeValue = String.format("%s - %s", resInfo.getWedB(), resInfo.getWedE());
                } else if (dayOfWeek == Calendar.THURSDAY) {
                    timeValue = String.format("%s - %s", resInfo.getThuB(), resInfo.getThuE());
                } else if (dayOfWeek == Calendar.FRIDAY) {
                    timeValue = String.format("%s - %s", resInfo.getFriB(), resInfo.getFriE());
                } else if (dayOfWeek == Calendar.SATURDAY) {
                    timeValue = String.format("%s - %s", resInfo.getSatB(), resInfo.getSatE());
                }

                if ("null - null".equalsIgnoreCase(timeValue)) {
                    restHours.setText("Closed Today");
                    restHours.setTextColor(colorGrey);
                } else {
                    restHours.setText(timeValue + " Today");
                    restHours.setTextColor(colorGrey);
                }
            }

            // Distance
            String distanceString = resInfo.get_dist();
            try {
                double distanceMiles = Double.parseDouble(distanceString);
                distanceMiles = ((int) (distanceMiles * 10)) / 10.f;
                distanceString = String.format("%.1f mi", distanceMiles);
            } catch (NumberFormatException e) {
            }
            restDistance.setText(distanceString);
            restDistance.setVisibility(View.GONE);

            // Restaurant ID
            restID = restList.get(position).get_id();
            String videoID = restList.get(position).getLink();

            // Restaurant Image
            if (TextUtils.isEmpty(videoID) || "null".equals(videoID)) {
                ivTitleText.setVisibility(View.VISIBLE);
                ivTitleText.setText(resInfo.get_name());
                imgBanner.setVisibility(View.GONE);
            } else {
                imgBanner.setVisibility(View.VISIBLE);
                String videoUrlFormat = "https://img.youtube.com/vi/" + videoID + "/0.jpg";
                _imageLoader.displayImage(videoUrlFormat, imgBanner, _imageOptions);
                imgBanner.setTag(position);
                imgBanner.setOnClickListener(btnBanneImageListener);
                ivTitleText.setVisibility(View.GONE);
            }

            // Restaurant Address String
            String addrString = resInfo.get_address();
            String cszString = resInfo.getStZipCity();
            String addressLines = String.format("%s\n%s, %s", addrString, cszString, distanceString);
            restAddress.setText(addressLines.trim());
            restAddress.setTag(position);
            restAddress.setOnClickListener(btnAddressClickListener);

            ratingBar.setRating(resInfo.get_rating());

            // Panel
            panelItem.setTag(position);
            panelItem.setOnClickListener(btnRestVideoClickListener);

            // Add to Favorite
            ibrestFavorite.setTag(position);
            ibrestFavorite.setOnClickListener(btnFavoriteClickListener);

            ibrestDirections.setTag(position);
            ibrestDirections.setOnClickListener(btnRestDirectionClickListener/*btnRestDirectionClickListener*/);

            // Restaurant Menu
            ibrestMenu.setTag(position);
            ibrestMenu.setOnClickListener(btnRestMenuClickListener);

            // This item is not visible now
            ibrestVideo.setTag(position);
            ibrestVideo.setOnClickListener(btnRestVideoClickListener);

            ibReserveTable.setTag(position);
            ibReserveTable.setOnClickListener(btnRestReserveTableClickListener);

            ibCatering.setTag(position);
            ibCatering.setOnClickListener(btnRestCateringClickListener);

            ibDelivery.setTag(position);
            ibDelivery.setOnClickListener(btnRestDeliveryClickListener);

            ibParty.setTag(position);
            ibParty.setOnClickListener(btnRestPartyClickListener);

            ibDonate.setTag(position);
            ibDonate.setOnClickListener(btnDonateClickListener);

            ibCubside.setTag(position);
            ibCubside.setOnClickListener(btnCurbsideClickListener);
        }
    }

    View.OnClickListener btnRestVideoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int position = (int) v.getTag();
            Restaurant resInfo = restList.get(position);

            WebView wv = (WebView) ((AppCompatActivity) _ctx).findViewById(R.id.wvYouTube);
            MapFragment mapFragment = (MapFragment) ((AppCompatActivity) _ctx).getFragmentManager().findFragmentById(R.id.restMap);

            if (Restaurant.hasEmptyValue(resInfo.getLink())) {
                _activity.showAlert("This Restaurant does not offer this service at this time.");

                wv.setVisibility(View.GONE);
                mapFragment.getView().setVisibility(View.VISIBLE);
            } else {

                wv.setVisibility(View.VISIBLE);
                mapFragment.getView().setVisibility(View.GONE);

                //wv.loadUrl(videoUrl);

                WebViewUtil.initialize(_ctx, wv).loadUrl(YOUTUBE_URL + resInfo.getLink());

                Toast.makeText(_ctx, "Play video", Toast.LENGTH_LONG).show();
            }

            //_activity.hitServer(FITSERVER_Vids, restList.get(position).get_id());
            _activity.hit1Server(FITSERVER_Vids, restList.get(position).get_id());

        }
    };

    View.OnClickListener btnRestMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final int position = (int) v.getTag();
            final Restaurant resInfo = restList.get(position);

            if (resInfo.getCurrTakingOrders() == 0) {
                //_activity.showAlert("Not available at this time");
                showInviteDialog(resInfo);
            } else {
                resInfo.setOption(0);
                gotoOrderPage(resInfo);
            }

            _activity.hit1Server(FITSERVER_Menus, restList.get(position).get_id());
        }
    };

    private void showInviteDialog(Restaurant resInfo) {
        LayoutInflater inflater = _activity.getLayoutInflater();
        View pinLayout = inflater.inflate(R.layout.dialog_invite_friend, null);
        final TextView tvEmail = pinLayout.findViewById(R.id.tvEmail);
        final CheckBox chkPersonalize = pinLayout.findViewById(R.id.chkPersonalize);
        tvEmail.requestFocus();
        final Button submit = pinLayout.findViewById(R.id.pin_submit);
        final Button cancel = pinLayout.findViewById(R.id.pin_cancel);
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(_ctx);
        alert.setView(pinLayout);
        alert.setCancelable(true);
        final android.app.AlertDialog dialog = alert.create();
        dialog.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tvEmail.getText().toString().trim();
                boolean isPersonalize = chkPersonalize.isChecked();
                if (_activity.isEmailValid(email)) {
                    dialog.dismiss();

                    if (_activity.getLocation()) {
                        HashMap<String, String> params = new HashMap<>();
                        String baseUrl = BaseFunctions.getBaseUrl(_activity,
                                "sendStoreInvite",
                                BaseFunctions.MAIN_FOLDER,
                                _activity.getUserLat(),
                                _activity.getUserLon(),
                                ((KTXApplication) _activity.getApplication()).getAndroidId());
                        String extraParams = "&sellerID=" + String.valueOf(resInfo.get_id()) +
                                "&toSellerCo=" + String.valueOf(resInfo.get_name()) +
                                "&fromFN=" + _activity.getAppSettings().getFN() +
                                "&fromLN=" + _activity.getAppSettings().getLN() +
                                "&toEmail=" + email;
                        baseUrl += extraParams;
                        Log.e("Request", baseUrl);

                        _activity.showProgressDialog();
                        RequestQueue queue = Volley.newRequestQueue(_activity);

                        //HttpsTrustManager.allowAllSSL();
                        GoogleCertProvider.install(_activity);

                        String finalBaseUrl = baseUrl;
                        StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                // Menus
                                Log.e("sendStoreInvite", response);

                                _activity.hideProgressDialog();
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    _activity.msg(jsonObject.getString("msg"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    _activity.showAlert(e.getMessage());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                _activity.hideProgressDialog();
                                baseFunctions.handleVolleyError(_ctx, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                return params;
                            }
                        };

                        sr.setShouldCache(false);
                        queue.add(sr);
                    }
                } else {
                    tvEmail.setError("Invalid Email!");
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void gotoOrderPage(final Restaurant resInfo) {
        if (_activity.getLocation()) {
            String userLat = _activity.getUserLat();
            String userLon = _activity.getUserLon();
            KTXApplication mMyApp = (KTXApplication) _activity.getApplication();
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(_activity,
                    "ProdsBySellerID",
                    BaseFunctions.MAIN_FOLDER,
                    _activity.getUserLat(),
                    _activity.getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&storeid=" + String.valueOf(resInfo.get_id());
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            _activity.showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(_activity);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(_activity);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    // Menus
                    Log.e("menus", response);

                    _activity.hideProgressDialog();

                    ArrayList<MenuHeader> menuHeaderList = new ArrayList<>();

                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        if (jsonArray.length() == 0 || "null".equals(jsonArray.getString(0))) {
                            // Still not ready for the menus
                            _activity.showToastMessage("Menu is not completed yet.");
                            return;
                        }

                        JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                        if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                            _activity.showToastMessage(jsonObject.getString("msg"));
                        } else {
                            for (int i = 0; i < jsonArray.length(); i++) {

                                if ("null".equalsIgnoreCase(jsonArray.getString(i))) {
                                    break;
                                }

                                JSONObject dataObj = jsonArray.getJSONObject(i);

                                MenuHeader newMenuHeader = new MenuHeader(dataObj.getString("catname"), "group");
                                menuHeaderList.add(newMenuHeader);

                                ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
                                JSONArray menuItemArray = dataObj.getJSONArray("items");
                                for (int j = 0; j < menuItemArray.length(); j++) {
                                    // Check NULL
                                    if ("null".equalsIgnoreCase(menuItemArray.getString(j))) {
                                        break;
                                    }

                                    JSONObject itemObj = menuItemArray.getJSONObject(j);
                                    MenuItem newMenuItem = new MenuItem();
                                    newMenuItem.set_name(itemObj.getString("name"));
                                    newMenuItem.set_description(itemObj.getString("des"));
                                    newMenuItem.set_id(itemObj.getInt("prodid"));
                                    newMenuItem.set_category(newMenuHeader.get_headerTitle());
                                    newMenuItem.set_hasOptions(false);

                                    if (i == 0) {
                                        if (j == 0) {
                                            newMenuItem.set_imgResId(R.drawable.aptizer1);
                                        } else {
                                            newMenuItem.set_imgResId(R.drawable.aptizer2);
                                        }
                                    } else {
                                        if (j == 0) {
                                            newMenuItem.set_imgResId(R.drawable.fillet1);
                                        } else {
                                            newMenuItem.set_imgResId(R.drawable.fillet2);
                                        }
                                    }

                                    // Random Price
                                    //newMenuItem.set_price(String.format("$%.2f", new Random().nextDouble() % 20));
                                    newMenuItem.set_price(String.format("$%.2f", itemObj.getDouble("price")));
                                    newMenuItem.set_size(itemObj.getString("size"));
                                    newMenuItem.set_taxable(itemObj.optInt("taxable"));

                                    menuItems.add(newMenuItem);
                                }

                                newMenuHeader.setMenuList(menuItems);
                            }

                            Intent restmenuintent = new Intent(_ctx, MenuListActivity.class);
                            restmenuintent.putExtra("restaurant", resInfo);
                            restmenuintent.putExtra("headermenus", menuHeaderList);

                            _ctx.startActivity(restmenuintent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        _activity.showAlert(e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    _activity.hideProgressDialog();
                    baseFunctions.handleVolleyError(_ctx, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                    //showMessage(error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            sr.setShouldCache(false);
            queue.add(sr);
        }
    }

    View.OnClickListener btnRestDirectionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = (int) v.getTag();

            if (_activity instanceof RestaurantListActivity) {
                ((RestaurantListActivity) _activity).zoomMapPin(position);
            } else if (_activity instanceof QRCodeScanResultActivity) {
                ((QRCodeScanResultActivity) _activity).zoomMapPin(position);
            } else if (_activity instanceof ActivityFavorite) {
                ((ActivityFavorite) _activity).zoomMapPin(position);
            }

            _activity.hit1Server(FITSERVER_DIRS, restList.get(position).get_id());
        }
    };

    View.OnClickListener btnRestReserveTableClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            final Restaurant resInfo = restList.get(position);

            if (resInfo.getRes() < 2) {
                //_activity.showAlert("Not available at this time");
                showInviteDialog(resInfo);
            } else {
                //TODO: will need to send the restaruant ID
                /*Intent reserveTableIntent = new Intent(_ctx, ReserveTableActivity.class);
                reserveTableIntent.putExtra("restaurant", restList.get(position));
                _ctx.startActivity(reserveTableIntent);*/

                new AlertDialog.Builder(_ctx)
                        .setTitle("Order")
                        .setMessage("Would you like to reserve table at this time?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                resInfo.setOption(5);
                                gotoOrderPage(resInfo);
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Intent reserveTableIntent = new Intent(_ctx, ReserveTableActivity.class);
                                //reserveTableIntent.putExtra("restaurant", restList.get(position));
                                //_ctx.startActivity(reserveTableIntent);

                                resInfo.setOption(5);

                                Intent checkoutintent = new Intent(_activity, FoodCheckoutActivity.class);
                                checkoutintent.putExtra("restaurant", resInfo);
                                checkoutintent.putExtra("menus", new ArrayList<>());
                                checkoutintent.putExtra("tot_items", 0);
                                checkoutintent.putExtra("tot_food_price", (float) resInfo.getResFee());
                                checkoutintent.putExtra("tot_tax", 0);

                                checkoutintent.putExtra("table_id", 0);

                                _ctx.startActivity(checkoutintent);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }

            _activity.hit1Server(FITSERVER_ResTabs, restList.get(position).get_id());
        }
    };

    View.OnClickListener btnRestCateringClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = (int) v.getTag();
            final Restaurant resInfo = restList.get(position);

            if (resInfo.getCater() < 2) {
                //_activity.showAlert("Not available at this time");
                showInviteDialog(resInfo);
            } else {

                new AlertDialog.Builder(_ctx)
                        .setTitle("Order")
                        .setMessage("Would you like to order food at this time?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                resInfo.setOption(3);
                                gotoOrderPage(resInfo);
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent cateringIntent = new Intent(_ctx, ActivityCatering.class);
                                cateringIntent.putExtra("restaurant", restList.get(position));
                                _ctx.startActivity(cateringIntent);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            _activity.hit1Server(FITSERVER_Cater, restList.get(position).get_id());
        }
    };

    View.OnClickListener btnRestDeliveryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Restaurant resInfo = restList.get(position);

            if (resInfo.getDel() < 2) {
                //_activity.showAlert("Not available at this time");
                showInviteDialog(resInfo);
            } else {
                resInfo.setOption(2);
                gotoOrderPage(resInfo);
            }

            _activity.hit1Server(FITSERVER_DELIVERY, restList.get(position).get_id());
        }
    };

    View.OnClickListener btnRestPartyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Restaurant resInfo = restList.get(position);

            if (resInfo.getParty() < 2) {
                //_activity.showAlert("Not available at this time");
                showInviteDialog(resInfo);
            } else {
                //TODO: will need to send the restaruant ID
                /*Intent partyIntent = new Intent(_ctx, PartyActivity.class);
                partyIntent.putExtra("restaurant", restList.get(position));
                _ctx.startActivity(partyIntent);*/

                resInfo.setOption(4);
                gotoOrderPage(resInfo);
            }

            _activity.hit1Server(FITSERVER_Party, restList.get(position).get_id());
        }
    };

    View.OnClickListener btnCurbsideClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Restaurant resInfo = restList.get(position);

            if (resInfo.getCurb() < 2) {
                //_activity.showAlert("Not available at this time");
                showInviteDialog(resInfo);
            } else {
                resInfo.setOption(1);
                gotoOrderPage(resInfo);
            }

            _activity.hit1Server(FITSERVER_CURBSIDE, restList.get(position).get_id());
        }
    };
}
