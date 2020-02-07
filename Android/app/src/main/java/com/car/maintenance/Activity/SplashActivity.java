package com.car.maintenance.Activity;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.car.maintenance.Application.Constant;
import com.car.maintenance.Application.Helper;
import com.car.maintenance.BuildConfig;
import com.car.maintenance.Database.Blog;
import com.car.maintenance.Database.Company;
import com.car.maintenance.Database.Model;
import com.car.maintenance.Database.Parts;
import com.car.maintenance.Database.User;
import com.car.maintenance.Database.Vehicle;
import com.car.maintenance.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private static final int MY_PERMISSIONS_REQUEST_SMS_PERMISSION = 1001;
    private static final int RC_SIGN_IN = 1002;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private GoogleApiClient mGoogleApiClient;
    private MyPagerAdapter pagerAdapter;
    private boolean isNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        firebaseDatabase = Helper.getDatabase();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = firebaseDatabase.getReference(Constant.FIREBASE_DB);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            requestPermission();
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(SplashActivity.this, "Connection Error", Toast.LENGTH_LONG);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        pagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setBackgroundColor(getResources().getColor(R.color.Grey800));
        viewPager.setClipToPadding(false);
        viewPager.setPadding(100, 100, 100, 100);
        viewPager.setPageMargin(50);


        /*Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String key = mDatabase.child("blogs").push().getKey();
        Blog blog = new Blog(-2,
                "How to get maximum mileage from your petrol car?",
                "The one big drawback owners of petrol cars face is fuel economy. Diesel cars are at least 25% more fuel efficient than equivalent petrol cars. However, many petrol cars give high “claimed mileage” figures, certified by ARAI, which are pretty difficult to achieve in real world conditions.",
                "https://www.cartoq.com/renault-scala-travelogue-limited-edition-launched/",
                formatter.format(calendar.getTime()));
        mDatabase.child("blogs").child(key).setValue(blog);*/


        /*String key = mDatabase.child("company").push().getKey();
        Company company = new Company(key, "Audi",
                "http://www.car-brand-names.com/wp-content/uploads/2015/03/Audi-Logo.png",
                "CAR");
        mDatabase.child("company").child(key).setValue(company);*/


        /*String key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        Model model = new Model(key, "Kodiaq",
                "https://auto.ndtvimg.com/car-images/medium/skoda/kodiaq/skoda-kodiaq.jpg?v=15");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "Octavia",
                "https://auto.ndtvimg.com/car-images/medium/skoda/octavia/skoda-octavia.jpg?v=24");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "Rapid",
                "https://auto.ndtvimg.com/car-images/medium/skoda/rapid/skoda-rapid.jpg?v=39");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "Superb",
                "https://auto.ndtvimg.com/car-images/medium/skoda/superb/skoda-superb.jpg?v=16");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        /*key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "Vento",
                "https://auto.ndtvimg.com/car-images/medium/volkswagen/vento/volkswagen-vento.jpg?v=12");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        /*key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "Figo Aspire",
                "https://auto.ndtvimg.com/car-images/medium/ford/figo-aspire/ford-figo-aspire.jpg?v=7");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        /*key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "Zest",
                "https://auto.ndtvimg.com/car-images/medium/tata/zest/tata-zest.jpg?v=14");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "Nano",
                "https://auto.ndtvimg.com/car-images/medium/tata/nano/tata-nano.jpg?v=7");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "Sumo",
                "https://auto.ndtvimg.com/car-images/medium/tata/sumo/tata-sumo.jpg?v=6");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        /*key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "Eon",
                "https://auto.ndtvimg.com/car-images/medium/hyundai/eon/hyundai-eon.jpg?v=26");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "X3",
                "https://auto.ndtvimg.com/car-images/medium/bmw/x3/bmw-x3.jpg?v=12");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "X5",
                "https://auto.ndtvimg.com/car-images/medium/bmw/x5/bmw-x5.jpg?v=6");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "X6",
                "https://auto.ndtvimg.com/car-images/medium/bmw/x6/bmw-x6.jpg?v=5");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "7 Series",
                "https://auto.ndtvimg.com/car-images/medium/bmw/7-series/bmw-7-series.jpg?v=6");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);
        key = mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").push().getKey();
        model = new Model(key, "X1",
                "https://auto.ndtvimg.com/car-images/medium/bmw/x1/bmw-x1.jpg?v=10");
        mDatabase.child("model").child("-LDLoq5nW9WLehJfYGv9").child(key).setValue(model);*/


        /*String key = mDatabase.child("parts").push().getKey();
        Parts parts = new Parts(key, "Engine Oil", 10000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Oil Filter", 10000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Wheel Balancing/Alignment", 10000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Coolant", 20000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Fuel Filter", 20000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Air Filter", 20000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Wiper Blades", 20000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Transmission/Gear Oil ", 30000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Brake Fluid", 30000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Power Steering Fluid", 30000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Spark Plugs", 30000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Tyres", 60000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Clutch Plate", 60000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Brake Pads", 60000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Timing Belt", 60000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);

        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "AC Service", 60000, "", 0, Helper.currentDateTime());


        key = mDatabase.child("parts").push().getKey();
        parts = new Parts(key, "Battery", 60000, "", 0, Helper.currentDateTime());
        mDatabase.child("parts").child(key).setValue(parts);*/

    }

    @OnClick(R.id.login)
    public void signIn() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Connecting to firebase...");
        dialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            Toast.makeText(SplashActivity.this, "Signin Succes ", Toast.LENGTH_LONG).show();
                            requestPermission();
                        } else {
                            Toast.makeText(SplashActivity.this, "Signin Failed Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SMS_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gotoMainActivity();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Permission Required");
                    alertDialog.setMessage("We need those permission. We will not store your personal data on our servers.");
                    alertDialog.setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermission();
                        }
                    });
                    alertDialog.show();
                }
            }
        }
    }

    private void requestPermission() {
        gotoMainActivity();
        /*if (
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                )
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_SMS_PERMISSION);
        else {
            gotoMainActivity();
        }*/
    }

    private void gotoMainActivity() {

        if (Helper.getStringSharedPreference(Constant.IS_COMPLETED, this).equals("1")) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
            return;
        }

        if (isNewUser) {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), "",
                    firebaseUser.getPhotoUrl().toString(), false, "", "", "",
                    BuildConfig.VERSION_NAME, Helper.currentDateTime(), Helper.currentDateTime());
            mDatabase.child("user").child(mAuth.getCurrentUser().getUid()).setValue(user);
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("app_version", BuildConfig.VERSION_NAME);
            map.put("last_usage", Helper.currentDateTime());
            mDatabase.child("user").child(mAuth.getCurrentUser().getUid()).updateChildren(map);
        }

        mDatabase.child("vehicle").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot == null || !dataSnapshot.hasChildren()) {
                            startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
                        } else {
                            Helper.setStringSharedPreference(Constant.IS_COMPLETED, "1", SplashActivity.this);
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        }
                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    public class MyPagerAdapter extends PagerAdapter {

        String[] titles = new String[]{
                "Live fuel prices",
                "Vehicle Expense",
                "Service & Parts",
                "Must Reads",
                "Safe and Secure"};
        String[] descriptions = new String[]
                {
                        "Get daily live fuel prices in all cities",
                        "Log all your fuel expenses and track your car expenses",
                        "Car Service recommendations and parts health meter",
                        "Must read articles generally you dont know.",
                        "App is fully safe. Dont worry, No sms data is getting stored on servers."
                };

        int[] icons = new int[]
                {
                        R.drawable.gas_station,
                        R.drawable.cash,
                        R.drawable.hammer,
                        R.drawable.book_open_page_variant,
                        R.drawable.lock_outline
                };

        final int[] colors = new int[]{
                getResources().getColor(R.color.Teal500),
                getResources().getColor(R.color.Blue500),
                getResources().getColor(R.color.Pink500),
                getResources().getColor(R.color.Amber500),
                getResources().getColor(R.color.Green500),
        };

        @Override
        public int getCount() {
            return 5;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(SplashActivity.this);
            View view = inflater.inflate(R.layout.list_item_onboarding, null);
            ((TextView) view.findViewById(R.id.title)).setText(titles[position]);
            ((TextView) view.findViewById(R.id.description)).setText(descriptions[position]);
            ((ImageView) view.findViewById(R.id.icon)).setImageResource(icons[position]);
            view.findViewById(R.id.layout).setBackgroundColor(colors[position]);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

}
