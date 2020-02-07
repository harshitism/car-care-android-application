package com.car.maintenance.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.car.maintenance.Activity.MainActivity;
import com.car.maintenance.Application.Constant;
import com.car.maintenance.Application.Helper;
import com.car.maintenance.Database.Company;
import com.car.maintenance.Database.Model;
import com.car.maintenance.Database.Vehicle;
import com.car.maintenance.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectYearFragment extends Fragment {


    private Bundle mainBundle;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @BindView(R.id.logo1)
    ImageView imageView1;
    @BindView(R.id.logo2)
    ImageView imageView2;
    @BindView(R.id.name1)
    TextView textView1;
    @BindView(R.id.name2)
    TextView textView2;
    @BindView(R.id.name3)
    TextView textView3;
    @BindView(R.id.grid)
    GridView gridView;

    public SelectYearFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_year, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainBundle = getArguments();

        firebaseDatabase = Helper.getDatabase();
        mDatabase = firebaseDatabase.getReference(Constant.FIREBASE_DB);
        mAuth = FirebaseAuth.getInstance();

        mDatabase.child("company").child(mainBundle.getString("company"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Company company = dataSnapshot.getValue(Company.class);
                        Glide.with(getActivity()).load(company.getLogo()).into(imageView1);
                        textView1.setText(company.getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        mDatabase.child("model").child(mainBundle.getString("company")).child(mainBundle.getString("model"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Model model = dataSnapshot.getValue(Model.class);
                        Glide.with(getActivity()).load(model.getImage()).into(imageView2);
                        textView2.setText(model.getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        textView3.setText(mainBundle.getString("fuel"));
        final List<String> list = new ArrayList<>();
        for (int i = 2018; i > 2000; i--)
            list.add("" + i);
        gridView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_item_year, list) {
            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view1 = super.getView(position, convertView, parent);
                view1.findViewById(R.id.TextView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectYear(list.get(position));
                    }
                });
                return view1;
            }
        });
    }

    public void selectYear(String year) {
        mainBundle.putString("year", year);

        Vehicle vehicle = new Vehicle();
        String key = mDatabase.child("vehicle").child(mAuth.getCurrentUser().getUid()).push().getKey();
        vehicle.set_id(key);
        vehicle.setType(mainBundle.getString("type"));
        vehicle.setCompany(mainBundle.getString("company"));
        vehicle.setModel(mainBundle.getString("model"));
        vehicle.setFuel(mainBundle.getString("fuel"));
        vehicle.setYear(Integer.parseInt(year));
        vehicle.setOdometer(mainBundle.getInt("odometer"));

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Saving details ... ");
        dialog.setCancelable(false);
        dialog.show();

        mDatabase.child("vehicle").child(mAuth.getCurrentUser().getUid()).child(key).setValue(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Try Again,Some error occured", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
