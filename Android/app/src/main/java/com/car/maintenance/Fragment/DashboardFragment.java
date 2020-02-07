package com.car.maintenance.Fragment;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatSpinner;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.car.maintenance.Activity.AddExpenseActivity;
import com.car.maintenance.Activity.FuelPriceActivity;
import com.car.maintenance.Activity.MainActivity;
import com.car.maintenance.Activity.SplashActivity;
import com.car.maintenance.Activity.WebActivity;
import com.car.maintenance.Application.Constant;
import com.car.maintenance.Application.Helper;
import com.car.maintenance.Application.SmsReceiver;
import com.car.maintenance.Application.URL;
import com.car.maintenance.BuildConfig;
import com.car.maintenance.Database.Transaction;
import com.car.maintenance.Database.Vehicle;
import com.car.maintenance.R;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_SMS_PERMISSION = 2001;
    @BindView(R.id.city_spinner)
    AppCompatSpinner citySpinner;

    @BindView(R.id.shimmer1)
    ShimmerFrameLayout shimmer1;

    @BindView(R.id.shimmer2)
    ShimmerFrameLayout shimmer2;

    @BindView(R.id.petrol_layout)
    LinearLayout llPetrol;

    @BindView(R.id.diesel_layout)
    LinearLayout llDiesel;

    @BindView(R.id.petrol)
    TextView tvPetrol;

    @BindView(R.id.diesel)
    TextView tvDiesel;

    @BindView(R.id.all_cities)
    Button btnCities;

    @BindView(R.id.service_kms)
    TextView tvServiceKms;

    @BindView(R.id.service_progress)
    ProgressBar progressService;


    @BindView(R.id.this_month)
    TextView tvThisMonth;

    @BindView(R.id.last_month)
    TextView tvLastMonth;

    @BindView(R.id.last_3month)
    TextView tvLast3Month;

    @BindView(R.id.this_month_o)
    TextView tvThisMonthO;

    @BindView(R.id.last_month_o)
    TextView tvLastMonthO;

    @BindView(R.id.last_3month_o)
    TextView tvLast3MonthO;

    @BindView(R.id.rating)
    AppCompatRatingBar ratingBar;


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;
    private AdView adView;
    private ProgressDialog dialog;
    private String vehicleNumber = "";
    private FirebaseAnalytics mFirebaseAnalytics;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static String TAG = "dashboard";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseDatabase = Helper.getDatabase();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mDatabase = firebaseDatabase.getReference(Constant.FIREBASE_DB);
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();

        showFuelPrice();
        setServiceIndicator();
        setFuelExpenditure();
        loadFacebookAd();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Helper.hideKeyboard(getActivity());

            }
        }, 200);

    }

    private void loadFacebookAd() {
        adView = new AdView(getActivity(), "261287334429127_261290481095479", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = (LinearLayout) getView().findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();
    }

    private void setFuelExpenditure() {
        mFireStore.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("transaction")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        List<DocumentSnapshot> snapshots = documentSnapshots.getDocuments();

                        Integer thisMonthAmount = 0;
                        Integer lastMonthAmount = 0;
                        Integer last3MonthAmount = 0;

                        Integer thisMonthAmountO = 0;
                        Integer lastMonthAmountO = 0;
                        Integer last3MonthAmountO = 0;

                        for (int i = 0; i < snapshots.size(); i++) {
                            Transaction transaction = snapshots.get(i).toObject(Transaction.class);

                            if (transaction.getType().equals("REFUEL")) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/dd");
                                try {
                                    Date tdate = dateFormat.parse(transaction.getDate());
                                    tdate.setHours(1);
                                    Calendar calendar = Calendar.getInstance();

                                    Date today = calendar.getTime();

                                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0);
                                    Date thisMonth = calendar.getTime();

                                    calendar.add(Calendar.MONTH, -1);
                                    Date lastMonth = calendar.getTime();

                                    calendar.add(Calendar.MONTH, -2);
                                    Date last3Month = calendar.getTime();

                                    if (tdate.after(thisMonth) && tdate.before(today)) {
                                        thisMonthAmount += transaction.getAmount();
                                    } else {
                                        if (tdate.after(lastMonth) && tdate.before(thisMonth)) {
                                            lastMonthAmount += transaction.getAmount();
                                        }
                                        if (tdate.after(last3Month) && tdate.before(thisMonth)) {
                                            last3MonthAmount += transaction.getAmount();
                                        }
                                    }

                                    tvThisMonth.setText("Rs " + thisMonthAmount);
                                    tvLastMonth.setText("Rs " + lastMonthAmount);
                                    tvLast3Month.setText("Rs " + last3MonthAmount);


                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/dd");
                                try {
                                    Date tdate = dateFormat.parse(transaction.getDate());
                                    tdate.setHours(1);
                                    Calendar calendar = Calendar.getInstance();

                                    Date today = calendar.getTime();

                                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0);
                                    Date thisMonth = calendar.getTime();

                                    calendar.add(Calendar.MONTH, -1);
                                    Date lastMonth = calendar.getTime();

                                    calendar.add(Calendar.MONTH, -2);
                                    Date last3Month = calendar.getTime();

                                    if (tdate.after(thisMonth) && tdate.before(today)) {
                                        thisMonthAmountO += transaction.getAmount();
                                    } else {
                                        if (tdate.after(lastMonth) && tdate.before(thisMonth)) {
                                            lastMonthAmountO += transaction.getAmount();
                                        }
                                        if (tdate.after(last3Month) && tdate.before(thisMonth)) {
                                            last3MonthAmountO += transaction.getAmount();
                                        }
                                    }

                                    tvThisMonthO.setText("Rs " + thisMonthAmountO);
                                    tvLastMonthO.setText("Rs " + lastMonthAmountO);
                                    tvLast3MonthO.setText("Rs " + last3MonthAmountO);


                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }


                        }


                    }
                });
    }


    private void setServiceIndicator() {
        String vehicle_id = Helper.getStringSharedPreference(Constant.VEHICLE_ID, getActivity());
        mDatabase.child("vehicle").child(mAuth.getCurrentUser().getUid()).child(vehicle_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                if (vehicle != null && vehicle.getOdometer() != null) {
                    Integer serviceDue = 0;
                    Integer progress = 0;
                    Integer lastDone = 0;
                    Integer odo = vehicle.getOdometer();
                    Integer rem = 0;
                    if (vehicle.getLast_service_odometer() != null)
                        lastDone = vehicle.getLast_service_odometer();

                    if (lastDone == 0) {
                        int i = 0;
                        while (i < odo)
                            i += 10000;
                        serviceDue = i;
                    } else {
                        serviceDue = lastDone + 10000;
                    }

                    rem = serviceDue - odo;

                    if (rem < 0) {
                        progressService.setProgress(100);
                    } else {
                        progress = (10000 - rem) * 100 / 10000;
                        progressService.setProgress(progress);
                    }
                    tvServiceKms.setText("At " + serviceDue + " KM ( Rem : " + rem + " KM)");
                    progressService.setProgress(progress);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @OnClick(R.id.service_button)
    public void openAddActivity() {
        Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
        intent.putExtra("expense_type", "SERVICE");
        startActivity(intent);
    }

    @OnClick(R.id.service_done_button)
    public void openAddActivityService() {
        Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
        intent.putExtra("expense_type", "SERVICE");
        startActivity(intent);
    }

    @OnClick(R.id.refuel)
    public void openAddActivityRefuel() {
        Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
        intent.putExtra("expense_type", "REFUEL");
        startActivity(intent);
    }

    @OnClick(R.id.expense)
    public void openAddActivityExpense() {
        Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
        intent.putExtra("expense_type", "EXPENSE");
        startActivity(intent);
    }



    @OnClick(R.id.vehicle_rel_service)
    public void VehicleRelServices() {
        Bundle bundle = new Bundle();
        bundle.putString("title", "Vehicle Related Service");
        mFirebaseAnalytics.logEvent("useful_services", bundle);
        {
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra("url", "https://parivahan.gov.in/vahanservice/vahan/ui/statevalidation/homepage.xhtml");
            intent.putExtra("title","Vehicle Related Service" );
            startActivity(intent);
        }
    }

    @OnClick(R.id.licence_rel_service)
    public void LicenceRelServices() {
        Bundle bundle = new Bundle();
        bundle.putString("title", "Licence Related Service");
        mFirebaseAnalytics.logEvent("useful_services", bundle);
        {
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra("url", "https://parivahan.gov.in/sarathiservice/");
            intent.putExtra("title","Licence Related Service" );
            startActivity(intent);
        }
    }

    @OnClick(R.id.fancy_number_booking)
    public void FancyNumber() {
        Bundle bundle = new Bundle();
        bundle.putString("title", "Fancy Number Booking");
        mFirebaseAnalytics.logEvent("useful_services", bundle);
        {
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra("url", "https://parivahan.gov.in/fancy/");
            intent.putExtra("title","Fancy Number Booking" );
            startActivity(intent);
        }
    }

    @OnClick(R.id.know_licence_details)
    public void LicenceDetails() {
        Bundle bundle = new Bundle();
        bundle.putString("title", "Licence Details");
        mFirebaseAnalytics.logEvent("useful_services", bundle);
        {
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra("url", "https://parivahan.gov.in/rcdlstatus/?pur_cd=101");
            intent.putExtra("title","Licence Details");
            startActivity(intent);
        }
    }

    @OnClick(R.id.authorization_card)
    public void authCard() {
        Bundle bundle = new Bundle();
        bundle.putString("title", "Authorization Card");
        mFirebaseAnalytics.logEvent("useful_services", bundle);
        {
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra("url", "https://parivahan.gov.in/authorization/");
            intent.putExtra("title","E-Authorization Card");
            startActivity(intent);
        }
    }



    private void showFuelPrice() {

        shimmer1.startShimmerAnimation();
        shimmer2.startShimmerAnimation();
        llPetrol.setVisibility(View.GONE);
        llDiesel.setVisibility(View.GONE);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL.GET_FUEL_PRICES, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                shimmer1.setVisibility(View.GONE);
                shimmer1.stopShimmerAnimation();
                shimmer2.setVisibility(View.GONE);
                shimmer2.stopShimmerAnimation();
                llPetrol.setVisibility(View.VISIBLE);
                llDiesel.setVisibility(View.VISIBLE);
                if (getActivity() == null) return;
                try {
                    final JSONArray cities = response.getJSONArray("cities");
                    List<String> cityList = new ArrayList<>();
                    for (int i = 0; i < cities.length(); i++)
                        cityList.add(cities.getJSONObject(i).getString("city"));
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, cityList);
                    citySpinner.setAdapter(adapter);
                    tvPetrol.setText("Rs " + cities.getJSONObject(0).getString("petrol"));
                    tvDiesel.setText("Rs " + cities.getJSONObject(0).getString("diesel"));

                    citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            try {
                                tvPetrol.setText("Rs " + cities.getJSONObject(position).getString("petrol"));
                                tvDiesel.setText("Rs " + cities.getJSONObject(position).getString("diesel"));
                                Helper.setIntegerSharedPreference(Constant.FUEL_CITY, position, getActivity());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    if (Helper.getIntegerSharedPreference(Constant.FUEL_CITY, getActivity()) != 0) {
                        citySpinner.setSelection(Helper.getIntegerSharedPreference(Constant.FUEL_CITY, getActivity()));
                    }
                    btnCities.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), FuelPriceActivity.class);
                            intent.putExtra("data", response.toString());
                            startActivity(intent);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("X-Mashape-Key", "PA5I4SnAvXmshOCJIJ7ZsR0WBHv7p1IeenxjsnrUVELMn4rx72");
                return map;
            }
        };
        request.setShouldCache(true);
        Volley.newRequestQueue(getActivity()).add(request);
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}
