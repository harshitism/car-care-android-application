package com.car.maintenance.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.car.maintenance.Application.Constant;
import com.car.maintenance.Application.Helper;
import com.car.maintenance.Application.NextFocus;
import com.car.maintenance.Database.Transaction;
import com.car.maintenance.Database.Vehicle;
import com.car.maintenance.R;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddExpenseActivity extends AppCompatActivity {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2001;
    @BindView(R.id.expense_type)
    EditText etExpenseType;

    @BindView(R.id.part)
    EditText etPart;
    @BindView(R.id.odometer)
    EditText etOdometer;

    @BindView(R.id.amount)
    EditText etAmount;

    @BindView(R.id.date)
    EditText etDate;

    /*@BindView(R.id.month)
    EditText etMonth;

    @BindView(R.id.year)
    EditText etYear;*/

    @BindView(R.id.note)
    EditText etNote;

    @BindView(R.id.location)
    EditText etLocation;

    @BindView(R.id.previous_odo)
    TextView tvPreviousOdomenter;


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String expenseType;
    private LatLng latLng;
    private Vehicle vehicle;
    private FirebaseFirestore mFireStore;
    private String partId;
    private String partName;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Add your expense");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseDatabase = Helper.getDatabase();
        mDatabase = firebaseDatabase.getReference(Constant.FIREBASE_DB);
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();

        expenseType = getIntent().getStringExtra("expense_type");
        etExpenseType.setText(expenseType);
        if (expenseType.equals("PART")) {
            findViewById(R.id.part_layout).setVisibility(View.VISIBLE);
            partId = getIntent().getStringExtra("part_id");
            partName = getIntent().getStringExtra("part_name");
            etPart.setText(partName);
        } else {
            partId = "";
        }


        String vehicle_id = Helper.getStringSharedPreference(Constant.VEHICLE_ID, this);
        mDatabase.child("vehicle").child(mAuth.getCurrentUser().getUid()).child(vehicle_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vehicle = dataSnapshot.getValue(Vehicle.class);
                tvPreviousOdomenter.setText("Previous Odometer Reading - " + vehicle.getOdometer() + " KM");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        loadFacebookAd();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.date)
    public void openPicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String monthStr = "" + (month + 1);
                String dateStr = "" + dayOfMonth;
                if (month + 1 < 10) {
                    monthStr = "0" + monthStr;
                }
                if (dayOfMonth < 10) {
                    dateStr = "0" + dayOfMonth;
                }
                etDate.setText("" + year + "/" + monthStr + "/" + dateStr);

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        dialog.show();
    }

    @OnClick(R.id.save)
    public void saveClick() {

        if (etOdometer.getText().length() == 0) {
            Toast.makeText(AddExpenseActivity.this, "Odometer reading is mandatory", Toast.LENGTH_LONG).show();
            return;
        }

        /*if (vehicle.getOdometer() > Integer.parseInt(etOdometer.getText().toString())) {
            Toast.makeText(AddExpenseActivity.this, "Odometer reading cant be less than previous current vehicle odometer reading.", Toast.LENGTH_LONG).show();
            return;
        }*/

        if (etAmount.getText().length() == 0) {
            Toast.makeText(AddExpenseActivity.this, "Amount is mandatory", Toast.LENGTH_LONG).show();
            return;
        }

        if (etDate.getText().length() == 0) {
            Toast.makeText(AddExpenseActivity.this, "Date is mandatory", Toast.LENGTH_LONG).show();
            return;
        }


        if (expenseType.equals("PART")) {
            etNote.setText(partName);
        }

        Transaction transaction = new Transaction();
        transaction.setType(expenseType);
        transaction.setOdometer(Integer.parseInt(etOdometer.getText().toString()));
        transaction.setPart_id(partId);
        transaction.setCreated_on(Helper.currentDateTime());
        transaction.setDate(etDate.getText().toString());
        transaction.setAmount(Integer.parseInt(etAmount.getText().toString()));
        transaction.setLatitude(0D);
        transaction.setLongitude(0D);
        transaction.setNote(etNote.getText().toString());
        final ProgressDialog dialog = new ProgressDialog(AddExpenseActivity.this);
        dialog.setMessage("Saving Data ... ");
        dialog.show();
        mFireStore.collection("users").document(mAuth.getCurrentUser().getUid()).collection("transaction").add(transaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                if (Integer.parseInt(etOdometer.getText().toString()) > vehicle.getOdometer()) {
                    mDatabase.child("vehicle").child(mAuth.getCurrentUser().getUid()).child(vehicle.get_id()).child("odometer").setValue(Integer.parseInt(etOdometer.getText().toString()));

                }
                if (expenseType.equals("PART")) {
                    mDatabase.child("parts_changed").child(vehicle.get_id()).child(partId).setValue(Integer.parseInt(etOdometer.getText().toString()));
                }
                if (expenseType.equals("SERVICE")) {
                    if (vehicle.getLast_service_odometer() != null) {
                        if (Integer.parseInt(etOdometer.getText().toString()) > vehicle.getLast_service_odometer()) {
                            mDatabase.child("vehicle").child(mAuth.getCurrentUser().getUid()).child(vehicle.get_id()).child("last_service_odometer").setValue(Integer.parseInt(etOdometer.getText().toString()));
                            mDatabase.child("vehicle").child(mAuth.getCurrentUser().getUid()).child(vehicle.get_id()).child("last_service_time").setValue(etDate.getText().toString());
                        }
                    } else {
                        mDatabase.child("vehicle").child(mAuth.getCurrentUser().getUid()).child(vehicle.get_id()).child("last_service_odometer").setValue(Integer.parseInt(etOdometer.getText().toString()));
                        mDatabase.child("vehicle").child(mAuth.getCurrentUser().getUid()).child(vehicle.get_id()).child("last_service_time").setValue(etDate.getText().toString());
                    }

                }
                dialog.dismiss();
                finish();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                etLocation.setText(place.getName());
                latLng = place.getLatLng();
            }
        }
    }

    private void loadFacebookAd() {
        adView = new AdView(this, "261287334429127_261291811095346", AdSize.RECTANGLE_HEIGHT_250);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }


}
