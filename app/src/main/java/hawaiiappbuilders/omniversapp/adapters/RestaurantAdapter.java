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
import android.graphics.Bitmap;
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
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.ActivityAddTabs;
import hawaiiappbuilders.omniversapp.ActivityCatering;
import hawaiiappbuilders.omniversapp.CartListActivity;
import hawaiiappbuilders.omniversapp.FoodCheckoutActivity;
import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.MenuListActivity;
import hawaiiappbuilders.omniversapp.QRCodeScanResultActivity;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.RestaurantListActivity;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.MenuHeader;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.WebViewUtil;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.MyViewHolder> {

    private static final String TAG = RestaurantAdapter.class.getSimpleName();
    BaseFunctions baseFunctions;
    private LayoutInflater inflater;
    public ArrayList<Restaurant> restList;
    private Context _ctx;
    private BaseActivity _activity;

    ImageLoader imageLoader;
    DisplayImageOptions imageOptions;

    int colorRed = 0xFFFF0000;
    int colorGrey = 0xFF676767;

    private int _tableID = 0;

    public RestaurantAdapter(Context ctx, ArrayList<Restaurant> restList) {

        inflater = LayoutInflater.from(ctx);
        this.restList = restList;
        this._ctx = ctx;
        this._activity = (BaseActivity) ctx;
        this.baseFunctions = new BaseFunctions(this._ctx, TAG);

        // Initialize the ImageLoader
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(ctx));
        imageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                /* .showImageOnLoading(R.drawable.logo_halopay_notg_white)
                 .showImageOnFail(R.drawable.logo_halopay_notg_white)
                 .showImageForEmptyUri(R.drawable.logo_halopay_notg_white)*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public RestaurantAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_restaurant, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final RestaurantAdapter.MyViewHolder holder, int position) {

        Restaurant resInfo = restList.get(position);

        holder.restName.setText(resInfo.get_name());

        // Welcome Message
        if (Restaurant.hasEmptyValue(resInfo.getWelcomeMsg())) {
            holder.restWelcomeMsg.setText("");
            holder.restWelcomeMsg.setVisibility(View.GONE);
        } else {
            holder.restWelcomeMsg.setText(resInfo.getWelcomeMsg());
            holder.restWelcomeMsg.setVisibility(View.VISIBLE);
        }

        // Show Time
        boolean closed = resInfo.getClosed() > 0;
        if (closed) {
            holder.restHours.setText("Closed Today");
            holder.restHours.setTextColor(colorRed);
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
                holder.restHours.setText("Closed Today");
                holder.restHours.setTextColor(colorGrey);
            } else {
                holder.restHours.setText(timeValue + " Today");
                holder.restHours.setTextColor(colorGrey);
            }
        }

        // Video Vote
        if (resInfo.getUTID() > 0) {
            holder.ivVVote.setVisibility(View.VISIBLE);
        } else {
            holder.ivVVote.setVisibility(View.GONE);
        }
        holder.ivVVote.setTag(position);
        holder.ivVVote.setOnClickListener(btnVoteClickListener);

        // Heap Map Action
        holder.tvHeatMap.setTag(position);
        holder.tvHeatMap.setText(String.valueOf(resInfo.getSeekIT()));
        holder.tvHeatMap.setOnClickListener(btnHeapMapClickListener);

        // Distance
        String distanceString = resInfo.get_dist();
        try {
            double distanceMiles = Double.parseDouble(distanceString);
            distanceMiles = ((int) (distanceMiles * 10)) / 10.f;
            distanceString = String.format("%.1f mi", distanceMiles);
        } catch (NumberFormatException e) {
        }
        holder.restDistance.setText(distanceString);
        holder.restDistance.setVisibility(View.GONE);

        // Restaurant ID
        holder.restID = restList.get(position).get_id();
        String videoID = restList.get(position).getLink();

        // Restaurant Image
        if (TextUtils.isEmpty(videoID) || "null".equals(videoID)) {
            holder.ivTitleText.setVisibility(View.VISIBLE);
            holder.ivTitleText.setText(resInfo.get_name());
            holder.imgBanner.setVisibility(View.GONE);
        } else {
            holder.imgBanner.setVisibility(View.VISIBLE);
            //http://img.youtube.com/vi/bHRuy6cNPO0/0.jpg
            String videoUrlFormat = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
            imageLoader.displayImage(videoUrlFormat, holder.imgBanner, imageOptions);
            holder.ivTitleText.setVisibility(View.GONE);
        }

        // Restaurant Address String
        String addrString = resInfo.get_address();
        String cszString = resInfo.getStZipCity();
        String addressLines = String.format("%s\n%s, %s", addrString, cszString, distanceString);
        holder.restAddress.setText(addressLines.trim());
        holder.restAddress.setTag(position);
        holder.restAddress.setOnClickListener(btnAddressClickListener);

        holder.ratingBar.setRating(resInfo.get_rating());

        // Panel
        holder.panelItem.setTag(position);
        holder.panelItem.setOnClickListener(btnRestVideoClickListener);

        // Add to Favorite
        holder.ibrestFavorite.setTag(position);
        holder.ibrestFavorite.setOnClickListener(btnFavoriteClickListener);

        holder.ibrestDirections.setTag(position);
        holder.ibrestDirections.setOnClickListener(btnRestDirectionClickListener/*btnRestDirectionClickListener*/);

        // Restaurant Menu
        holder.ibrestMenu.setTag(position);
        holder.ibrestMenu.setOnClickListener(btnRestMenuClickListener);

        // This item is not visible now
        holder.ibrestVideo.setTag(position);
        holder.ibrestVideo.setOnClickListener(btnRestVideoClickListener);

        holder.ibReserveTable.setTag(position);
        holder.ibReserveTable.setOnClickListener(btnRestReserveTableClickListener);

        holder.ibCatering.setTag(position);
        holder.ibCatering.setOnClickListener(btnRestCateringClickListener);

        holder.ibDelivery.setTag(position);
        holder.ibDelivery.setOnClickListener(btnRestDeliveryClickListener);

        holder.ibParty.setTag(position);
        holder.ibParty.setOnClickListener(btnRestPartyClickListener);

        holder.ibDonate.setTag(position);
        holder.ibDonate.setOnClickListener(btnDonateClickListener);

        holder.ibGiftCard.setTag(position);
        holder.ibGiftCard.setOnClickListener(btnGiftListener);

        holder.ibCubside.setTag(position);
        holder.ibCubside.setOnClickListener(btnCurbsideClickListener);

        holder.ibAddTab.setTag(position);
        holder.ibAddTab.setOnClickListener(btnTabsListener);
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

                WebViewUtil.initialize(_ctx, wv).loadUrl(YOUTUBE_URL + resInfo.getLink());

                //Toast.makeText(_ctx, "Play video", Toast.LENGTH_LONG).show();
            }

            //_activity.hitServer(FITSERVER_Vids, restList.get(position).get_id());
            _activity.hit1Server(FITSERVER_Vids, restList.get(position).get_id());
        }
    };

    View.OnClickListener btnVoteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Restaurant resInfo = restList.get(position);

            View dialogView = _activity.getLayoutInflater().inflate(R.layout.dialog_vvote, null);

            final android.app.AlertDialog inputDlg = new android.app.AlertDialog.Builder(_activity, R.style.AlertDialogTheme)
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

            // UpVote
            dialogView.findViewById(R.id.btnUpVote).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    inputDlg.dismiss();
                    voteVideo(resInfo, true);
                }
            });

            // DownVote
            dialogView.findViewById(R.id.btnDownVote).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    inputDlg.dismiss();
                    voteVideo(resInfo, false);
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
        }
    };

    private void voteVideo(Restaurant resInfo, boolean updown) {
        if (_activity.getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(_activity,
                    "videoVote",
                    BaseFunctions.MAIN_FOLDER,
                    _activity.getUserLat(),
                    _activity.getUserLon(),
                    ((KTXApplication) _activity.getApplication()).getAndroidId());
            String upDown = updown ? "1" : "0";
            String extraParams =
                    "&storeid=" + String.valueOf(resInfo.get_id()) +
                            "&vidID=" + String.valueOf(resInfo.getUTID()) +
                            "&upDown=" + upDown;
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
                    Log.e("videoVote", response);

                    _activity.hideProgressDialog();

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                        if (jsonObject.has("msg")) {
                            _activity.showToastMessage(jsonObject.getString("msg"));
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

    View.OnClickListener btnHeapMapClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            Restaurant resInfo = restList.get(position);

            if (_activity instanceof RestaurantListActivity) {
                ((RestaurantListActivity) _activity).heatMap(position);
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

    View.OnClickListener btnRestMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final int position = (int) v.getTag();
            final Restaurant resInfo = restList.get(position);

            if (resInfo.getOrders() == 0 && resInfo.getPu() == 0) {
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
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(_activity,
                    "ProdsBySellerID",
                    BaseFunctions.MAIN_FOLDER,
                    _activity.getUserLat(),
                    _activity.getUserLon(),
                    ((KTXApplication) _activity.getApplication()).getAndroidId());
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
                    ArrayList<MenuItem> menuList = new ArrayList<>();

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

                                checkoutintent.putExtra("table_id", _tableID);

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

    View.OnClickListener btnDonateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = (int) v.getTag();
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

    View.OnClickListener btnGiftListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int position = (int) v.getTag();
            Restaurant resInfo = restList.get(position);

            ArrayList<MenuItem> menuItems = new ArrayList<>();
            MenuItem menuItem = new MenuItem();
            menuItem.set_id(15);
            menuItem.set_name("Gift Card");
            menuItem.set_quantity(0);
            menuItem.set_taxfees("0");
            menuItem.set_price("1");
            menuItem.set_taxable(0);
            menuItems.add(menuItem);

            Intent cartIntent = new Intent(_activity, CartListActivity.class);
            cartIntent.putExtra("menus", menuItems);
            cartIntent.putExtra("restaurant", resInfo);
            _activity.startActivity(cartIntent);
        }
    };

    View.OnClickListener btnTabsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int position = (int) v.getTag();
            Restaurant resInfo = restList.get(position);

            Intent cartIntent = new Intent(_activity, ActivityAddTabs.class);
            cartIntent.putExtra("restaurant", resInfo);
            _activity.startActivity(cartIntent);
        }
    };

    @Override
    public int getItemCount() {
        return restList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected int restID;

        protected View panelItem;
        protected TextView restName;
        protected TextView restWelcomeMsg;

        protected ImageView ivVVote;
        protected TextView tvHeatMap;

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

        protected View ibGiftCard;

        protected View ibDonate;
        protected View ibCubside;
        protected View ibAddTab;

        protected TextView ivTitleText;
        protected ImageView imgBanner;

        public MyViewHolder(View itemView) {
            super(itemView);

            panelItem = itemView.findViewById(R.id.panelItem);

            restName = (TextView) itemView.findViewById(R.id.restName);
            restWelcomeMsg = (TextView) itemView.findViewById(R.id.restWelcomeMsg);

            ivVVote = (ImageView) itemView.findViewById(R.id.ivVVote);
            tvHeatMap = (TextView) itemView.findViewById(R.id.tvHeatMap);

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

            ibGiftCard = itemView.findViewById(R.id.ibGiftCard);

            ibDonate = itemView.findViewById(R.id.ibDonate);
            ibCubside = itemView.findViewById(R.id.ibCurbside);
            ibAddTab = itemView.findViewById(R.id.ibAddTab);

            imgBanner = (ImageView) itemView.findViewById(R.id.imgBanner);
            ivTitleText = (TextView) itemView.findViewById(R.id.ivTitleText);
        }
    }
}
