package com.car.maintenance.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.TwoStatePreference;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.car.maintenance.Application.Constant;
import com.car.maintenance.Application.Helper;
import com.car.maintenance.BuildConfig;
import com.car.maintenance.Database.Company;
import com.car.maintenance.Database.Model;
import com.car.maintenance.Database.User;
import com.car.maintenance.Database.Vehicle;
import com.car.maintenance.Fragment.BlogFragment;
import com.car.maintenance.Fragment.DashboardFragment;
import com.car.maintenance.Fragment.ExpenseFragment;
import com.car.maintenance.Fragment.PartsFragment;
import com.car.maintenance.Fragment.ProfileFragment;
import com.car.maintenance.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    private FragmentManager fragmentManager;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @BindView(R.id.logo)
    ImageView ivLogo;

    @BindView(R.id.company)
    TextView tvCompany;

    @BindView(R.id.odometer)
    TextView tvOdometer;

    @BindView(R.id.model)
    TextView tvModel;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    public BottomNavigationView navigation;
    private Vehicle vehicle;
    private InterstitialAd interstitialAd;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private MenuItem prevMenuItem;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        firebaseDatabase = Helper.getDatabase();
        mDatabase = firebaseDatabase.getReference(Constant.FIREBASE_DB);
        mAuth = FirebaseAuth.getInstance();

        setActionBar();
        sendRegistrationIdToServer();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
            itemView.setShiftingMode(false);
            itemView.setChecked(false);
        }
        loadFacebookAd();
        checkUpdate();
        setViewPager();
    }

    private void setViewPager() {
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    navigation.getMenu().getItem(0).setChecked(false);

                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setActionBar() {
        mDatabase.child("vehicle").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    vehicle = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        vehicle = snapshot.getValue(Vehicle.class);
                    }
                    tvOdometer.setText("" + vehicle.getOdometer() + " KM");
                    mDatabase.child("company").child(vehicle.getCompany()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Company company = dataSnapshot.getValue(Company.class);
                            Glide.with(MainActivity.this).load(company.getLogo()).into(ivLogo);
                            tvCompany.setText(company.getName());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    final Vehicle finalVehicle = vehicle;
                    mDatabase.child("model").child(vehicle.getCompany()).child(vehicle.getModel()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Model model = dataSnapshot.getValue(Model.class);
                            tvModel.setText(model.getName() + " " + finalVehicle.getYear());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Helper.setStringSharedPreference(Constant.VEHICLE_ID, finalVehicle.get_id(), MainActivity.this);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.dashboard:
                    viewPager.setCurrentItem(0, true);
                    return true;
                case R.id.expenses:
                    viewPager.setCurrentItem(1, true);

                    return true;
                case R.id.vehicle_parts:
                    viewPager.setCurrentItem(2, true);

                    return true;
                case R.id.blog:
                    viewPager.setCurrentItem(3, true);

                    return true;
            }
            return false;
        }
    };

    private void sendRegistrationIdToServer() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map = new HashMap<>();
                map.put("fcm_id", FirebaseInstanceId.getInstance().getToken());
                map.put("app_version", BuildConfig.VERSION_NAME);
                map.put("last_usage", Helper.currentDateTime());
                mDatabase.child("user").child(mAuth.getCurrentUser().getUid()).updateChildren(map);

            }
        }).start();
    }

    public void loadFacebookAd() {
        interstitialAd = new InterstitialAd(this, "261287334429127_261287424429118");
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                finish();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();

    }

    private void checkUpdate() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.fetch(0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mFirebaseRemoteConfig.activateFetched();
                if (BuildConfig.VERSION_CODE < mFirebaseRemoteConfig.getLong(Constant.CONFIG_UPDATE)) {
                    Helper.showAlertDialog(MainActivity.this, "Outdated App", "Please update your app", "Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
                        }
                    }, "CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new DashboardFragment();
                case 1:
                    return new ExpenseFragment();
                case 2:
                    Bundle bundle = new Bundle();
                    bundle.putInt("odometer", vehicle.getOdometer());
                    bundle.putString("vehicle_id", vehicle.get_id());
                    PartsFragment partsFragment = new PartsFragment();
                    partsFragment.setArguments(bundle);
                    return partsFragment;
                case 3:
                    return new BlogFragment();
                default:
                    return new DashboardFragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @Override
    public void onBackPressed() {
        if (interstitialAd.isAdLoaded())
            interstitialAd.show();
        else finish();
    }
}
