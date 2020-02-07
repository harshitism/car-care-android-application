package com.car.maintenance.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.car.maintenance.Application.Constant;
import com.car.maintenance.Application.Helper;
import com.car.maintenance.Database.Company;
import com.car.maintenance.Database.Model;
import com.car.maintenance.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectFuelFragment extends Fragment {


    private FirebaseDatabase firebaseDatabase;
    private Bundle mainBundle;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @BindView(R.id.iv1)
    ImageView imageView1;
    @BindView(R.id.iv2)
    ImageView imageView2;
    @BindView(R.id.iv3)
    ImageView imageView3;
    @BindView(R.id.tv1)
    TextView textView1;
    @BindView(R.id.tv2)
    TextView textView2;
    @BindView(R.id.tv3)
    TextView textView3;
    @BindView(R.id.odometer)
    EditText etOdometer;

    public SelectFuelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_fuel, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainBundle = getArguments();

        firebaseDatabase = Helper.getDatabase();
        mDatabase = firebaseDatabase.getReference(Constant.FIREBASE_DB);
        mAuth = FirebaseAuth.getInstance();

    }

    @OnClick(R.id.petrol)
    public void selectPetrol() {
        selectFuel("PETROL");
        imageView1.setColorFilter(getActivity().getResources().getColor(R.color.Blue300));
        imageView2.setColorFilter(getActivity().getResources().getColor(R.color.Grey300));
        imageView3.setColorFilter(getActivity().getResources().getColor(R.color.Grey300));

        textView1.setTextColor(getActivity().getResources().getColor(R.color.Blue700));
        textView2.setTextColor(getActivity().getResources().getColor(R.color.Grey700));
        textView3.setTextColor(getActivity().getResources().getColor(R.color.Grey700));
    }

    @OnClick(R.id.cng)
    public void selectcng() {
        selectFuel("CNG");

        imageView1.setColorFilter(getActivity().getResources().getColor(R.color.Grey300));
        imageView2.setColorFilter(getActivity().getResources().getColor(R.color.Grey300));
        imageView3.setColorFilter(getActivity().getResources().getColor(R.color.Green300));

        textView1.setTextColor(getActivity().getResources().getColor(R.color.Grey700));
        textView2.setTextColor(getActivity().getResources().getColor(R.color.Grey700));
        textView3.setTextColor(getActivity().getResources().getColor(R.color.Green700));
    }

    @OnClick(R.id.diesel)
    public void selectDiesel() {
        selectFuel("DIESEL");
        imageView1.setColorFilter(getActivity().getResources().getColor(R.color.Grey300));
        imageView2.setColorFilter(getActivity().getResources().getColor(R.color.Orange300));
        imageView3.setColorFilter(getActivity().getResources().getColor(R.color.Grey300));
        textView1.setTextColor(getActivity().getResources().getColor(R.color.Grey700));
        textView2.setTextColor(getActivity().getResources().getColor(R.color.Orange700));
        textView3.setTextColor(getActivity().getResources().getColor(R.color.Grey700));
    }

    @OnClick(R.id.next)
    public void next() {

        if (etOdometer.getText().toString().length() > 0) {
            etOdometer.setError(null);
            mainBundle.putInt("odometer", Integer.parseInt(etOdometer.getText().toString()));

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            SelectYearFragment fragment = new SelectYearFragment();
            fragment.setArguments(mainBundle);
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            etOdometer.setError("Enter valid number");
        }


    }

    public void selectFuel(String fuel) {
        mainBundle.putString("fuel", fuel);
        etOdometer.requestFocus();
    }

}
