package hawaiiappbuilders.omniversapp;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.K;

public class ActivityHotelExperience extends BaseActivity {
    public static final String TAG = ActivityHotelExperience.class.getSimpleName();
    Context mContext;
    Button btnCallHotel;
    Button btnTextHotel;


    ImageView btnBellService;
    ImageView btnTaxi;
    ImageView btnRoomService;
    ImageView btnConcierge;
    ImageView btnBar;
    ImageView btnDirections;
    ImageView btnHotelKey;
    ImageView btnSpa;

    double lat;
    double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_experience);

        mContext = this;
        initViews();
    }

    private void initViews() {
        btnCallHotel = findViewById(R.id.btnCallHotel);
        btnCallHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions(mContext, PERMISSION_REQUEST_PHONE_STRING, false, 109)) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    phoneIntent.setData(Uri.parse(String.format("tel:%s", "8084507683")));
                    startActivity(phoneIntent);
                }
            }
        });

        btnTextHotel = findViewById(R.id.btnTextHotel);
        btnTextHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ConnectionActivity.class));
            }
        });

        btnBellService = findViewById(R.id.ivHighlight1);
        btnBellService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSomething(1);
            }
        });

        btnTaxi = findViewById(R.id.ivHighlight2);
        btnTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSomething(2);
            }
        });

        btnRoomService = findViewById(R.id.ivHighlight3);
        btnRoomService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSomething(3);
            }
        });

        btnConcierge = findViewById(R.id.ivHighlight4);
        btnConcierge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSomething(4);
            }
        });

        btnBar = findViewById(R.id.ivHighlight5);
        btnBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSomething(5);
            }
        });

        btnDirections = findViewById(R.id.ivHighlight6);
        btnDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSomething(6);
            }
        });

        btnHotelKey = findViewById(R.id.ivHighlight7);
        btnHotelKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSomething(7);
            }
        });

        btnSpa = findViewById(R.id.ivHighlight8);
        btnSpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSomething(8);
            }
        });
    }

    private String getFullName() {
        AppSettings appSettings = new AppSettings(mContext);
        return appSettings.getFN() + " " + appSettings.getLN();
    }

    private void doSomething(int requestCode) {
        String sampleBar = "{\"_dist\":\"4\",\"WP\":\"(515) 313-0700\",\"ownerID\":1434350,\"advertised\":1,\"Rating\":5,\"UTID\":3,\"vid\":2,\"IndustryID\":123,\"StoreID\":1434349,\"Co\":\"Smash Park\",\"address\":\"6625 Coachlight Dr\",\"STE\":\"\",\"City\":\"WDM\",\"St\":\"IA\",\"Zip\":\"50266\",\"Lat\":41.5658340000,\"Lon\":-93.7979970000,\"TaxRate\":0.07,\"MenuStyle\":0,\"WelcomeMsg\":\"\",\"CurrFoodWait\":2,\"CurrWaitTable\":2,\"CurrTakingOrders\":0,\"DoingUPX\":1,\"Doing3rdParty\":1,\"CurrTakingOrders1\":0,\"DoingTableRes\":1,\"DoingOnTable\":1,\"DoingEatIn\":1,\"DoingPU\":1,\"DoingPUin\":1,\"DoingCurb\":1,\"DoingCater\":1,\"DoingParty\":1,\"DoingDel\":1,\"DoingSelf\":1,\"DoingAppts\":1,\"DoingBOGO\":2,\"DoingLoyalty\":2,\"DoingBAM\":2,\"DoingPayroll\":1,\"onTableFee\":5.00,\"PartyFee\":1.00,\"ResFee\":4.00,\"CatFee\":2.00,\"ApptFee\":3.00,\"DisplayBrdFee\":0.00,\"UPXFee\":8.00,\"NonUPXFee\":10.00,\"SelfFee\":9.00,\"CurbFee\":0.00,\"MonB\":\"11:00:00\",\"MonE\":\"2:00 am\",\"TueB\":\"11:00:00\",\"TueE\":\"2:00 am\",\"WedB\":\"11:00:00\",\"WedE\":\"2:00 am\",\"ThuB\":\"11:00:00\",\"ThuE\":\"2:00 am\",\"FriB\":\"11:00:00\",\"FriE\":\"2:00 am\",\"SatB\":\"11:00:00\",\"SatE\":\"2:00 am\",\"SunB\":\"11:00\",\"SunE\":\"2:00 am\",\"Title\":\"\",\"Link\":\"irm1yAe8j2I\",\"closed\":0,\"seekIT\":0}";
        Restaurant bar = convert(sampleBar);

        String sampleRestaurant = "{\"_dist\":\"4\",\"WP\":\"(555) 499-2804\",\"ownerID\":186373,\"advertised\":1,\"Rating\":3,\"UTID\":2,\"vid\":2,\"IndustryID\":123,\"StoreID\":186305,\"Co\":\"Mamas Pizza\",\"address\":\"909 E 1st\",\"STE\":\"\",\"City\":\"Ankeny\",\"St\":\"IA\",\"Zip\":\"50021\",\"Lat\":41.7344678000,\"Lon\":-93.5831880400,\"TaxRate\":0.07,\"MenuStyle\":0,\"WelcomeMsg\":\"Stop by today\",\"CurrFoodWait\":0,\"CurrWaitTable\":0,\"CurrTakingOrders\":1,\"DoingUPX\":1,\"Doing3rdParty\":1,\"CurrTakingOrders1\":1,\"DoingTableRes\":1,\"DoingOnTable\":0,\"DoingEatIn\":1,\"DoingPU\":1,\"DoingPUin\":1,\"DoingCurb\":1,\"DoingCater\":1,\"DoingParty\":1,\"DoingDel\":1,\"DoingSelf\":1,\"DoingAppts\":0,\"DoingBOGO\":1,\"DoingLoyalty\":0,\"DoingBAM\":0,\"DoingPayroll\":1,\"onTableFee\":200.00,\"PartyFee\":30.00,\"ResFee\":111.11,\"CatFee\":35.00,\"ApptFee\":44.00,\"DisplayBrdFee\":5.00,\"UPXFee\":0.00,\"NonUPXFee\":8.99,\"SelfFee\":7.99,\"CurbFee\":2.00,\"MonB\":\"11 am\",\"MonE\":\"2:00 am\",\"TueB\":\"11 am\",\"TueE\":\"2:00 am\",\"WedB\":\"11 am\",\"WedE\":\"2:00 am\",\"ThuB\":\"11 am\",\"ThuE\":\"2:00 am\",\"FriB\":\"11 am\",\"FriE\":\"2:00 am\",\"SatB\":\"11 am\",\"SatE\":\"2:00 am\",\"SunB\":\"11 am\",\"SunE\":\"2:00 am\",\"Title\":\"\",\"Link\":\"irm1yAe8j2I\",\"closed\":0,\"seekIT\":3}";
        Restaurant restaurant = convert(sampleRestaurant);
        switch (requestCode) {
            case 1: // Bell Service
                AlertDialog.Builder question1 = askQuestion("Hello " + getFullName(), "How many bags?", "");
                AlertDialog.Builder question2 = askQuestion("We'll be right there", "", "");
                question1.setNegativeButton("Less than 5", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        question2.show();
                    }
                }).setNeutralButton("More than 5", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        question2.show();
                    }
                }).show();
                question2.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
                break;
            case 2: // Taxi
                AlertDialog.Builder question3 = askQuestion("Hello " + getFullName(), "How many in the party?", "");
                question3.setNegativeButton("Less than 5", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                }).setNeutralButton("More than 5", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                }).show();
                break;
            case 3: // Room Service
                restaurant.set_industryID(123);
                restaurant.setLink("yewlM0i1b1k");
                restaurant.set_name("Mama's Pizza");
                restaurant.set_address("300 E Locust St Suite 313, Des Moines, IA 50309, United States");
                restaurant.set_city("Des Moines");
                restaurant.set_st("IA");
                restaurant.set_zip("50309");
                restaurant.set_lattiude(41.594951829912304);
                restaurant.set_longitude(-93.61559684799768);
                getRestaurants("Restaurants", restaurant);
                break;
            case 4: // Concierge
                AlertDialog.Builder question8 = askQuestion("Due to Covid", "We are closed", "");
                question8.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                }).show();
                break;
            case 5: // Bar
                bar.set_industryID(123);
                bar.setLink("jkdMi5gKLcs");
                bar.set_name("Smash Park");
                bar.set_address("300 E Locust St Suite 313, Des Moines, IA 50309, United States");
                bar.set_city("Des Moines");
                bar.set_st("IA");
                bar.set_zip("50309");
                bar.set_lattiude(41.594951829912304);
                bar.set_longitude(-93.61559684799768);
                getRestaurants("Bars", bar);
                break;
            case 6: // Directions
                getCoordinates();
                break;
            case 7: // Access
                startActivity(new Intent(mContext, ActivityAccessibleDoors.class));
                break;
            case 8: // Spa
                restaurant.setLink("56cuF0PMmBM");
                restaurant.set_name("East Village Spa");
                restaurant.set_address("300 E Locust St Suite 313, Des Moines, IA 50309, United States");
                restaurant.set_city("Des Moines");
                restaurant.set_st("IA");
                restaurant.set_zip("50309");
                restaurant.set_lattiude(41.594951829912304);
                restaurant.set_longitude(-93.61559684799768);
                getRestaurants("Spa", restaurant);
                break;
        }
    }

    private Restaurant convert(String jsonString) {
        return new Gson().fromJson(jsonString, Restaurant.class);
    }

    private AlertDialog.Builder askQuestion(String title, String message, String positiveText) {
        return new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.mipmap.ic_launcher1_foreground);
    }

    /**
     * Room Service, Bar
     * <p>
     * Order food from Hotel's restaurant
     * Show list of other restaurant's if Hotel doesn't have a restaurant
     */
    private void getRestaurants(String title, Restaurant restaurant) {
        Intent intent = new Intent(mContext, ActivityFavorite.class);
        intent.putExtra("parent", "favorites");
        ArrayList<Restaurant> restaurantArrayList = new ArrayList<>();
        restaurantArrayList.add(restaurant);
        intent.putExtra("restaurants", restaurantArrayList);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    /**
     * Taxi, Spa
     */
    private void setAppointment() {
        new AlertDialog.Builder(mContext)
                .setTitle("Set Appointment")
                .setMessage("You will receive confirmation for your appointment")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showSuccess();
                    }
                })
                .setIcon(R.mipmap.ic_launcher1_foreground)
                .show();
        // startActivity(new Intent(mContext, ConnectionActivity.class));
    }

    private void showSuccess() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);

        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.findViewById(R.id.viewSuccess).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    /**
     * Directions
     */
    public void getCoordinates() {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        GoogleCertProvider.install(mContext);
        String address = getString(R.string.hotel_address);
        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s|country:US&region=us&key=%s", address, K.gKy(BuildConfig.G));
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();
                try {
                    JSONObject returnJSON = new JSONObject(response);
                    if (returnJSON.has("status") && "OK".equals(returnJSON.getString("status"))) {
                        // Save current zipCode
                        JSONObject jsonZipLocationInfo = returnJSON;
                        if (jsonZipLocationInfo.has("results")) {
                            JSONArray jsonArrayResult = jsonZipLocationInfo.getJSONArray("results");
                            if (jsonArrayResult.length() > 0) {
                                JSONObject jsonAddrObj = jsonArrayResult.getJSONObject(0);

                                JSONObject geometry = jsonAddrObj.getJSONObject("geometry");
                                JSONObject locationObject = geometry.getJSONObject("location");
                                lat = locationObject.getDouble("lat");
                                lon = locationObject.getDouble("lng");

                                // Get City and State
                                /*JSONArray jsonAddressComponentArray = jsonAddrObj.getJSONArray("address_components");
                                for (int i = 0; i < jsonAddressComponentArray.length(); i++) {
                                    JSONObject jsonAddrComponent = jsonAddressComponentArray.getJSONObject(i);
                                    String longName = jsonAddrComponent.getString("long_name");
                                    String shortName = jsonAddrComponent.getString("short_name");
                                    String types = jsonAddrComponent.getString("types");

                                    if (types.contains("administrative_area_level_1")) {
                                        // This means state info
                                        edtState.setText(shortName);
                                    } else if (types.contains("locality")) {
                                        edtCity.setText(longName);
                                    }
                                }

                                // Get Location
                                JSONObject jsonGeometryObj = jsonAddrObj.getJSONObject("geometry");
                                JSONObject jsonLocationObj = jsonGeometryObj.getJSONObject("location");*/

                                // open external maps
                                Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%f,%f&mode=d", lat, lon));

                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                mContext.startActivity(mapIntent);
                            }
                        }
                    } else {
                        showToastMessage("Couldn't get address information");
                    }
                } catch (JSONException e) {
                    // Error
                    showToastMessage("Couldn't get address information");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                baseFunctions.handleVolleyError(mContext, error, TAG, "geocode");
            }
        });

        sr.setRetryPolicy(new DefaultRetryPolicy(
                25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        sr.setShouldCache(false);
        queue.add(sr);
    }

}
