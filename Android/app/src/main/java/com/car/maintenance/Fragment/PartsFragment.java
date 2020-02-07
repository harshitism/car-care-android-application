package com.car.maintenance.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.car.maintenance.Activity.AddExpenseActivity;
import com.car.maintenance.Application.Constant;
import com.car.maintenance.Application.Helper;
import com.car.maintenance.Database.Parts;
import com.car.maintenance.Database.Vehicle;
import com.car.maintenance.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PartsFragment extends Fragment {

    public static String TAG = "parts";

    @BindView(R.id.list)
    ListView listView;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Bundle bundle;
    private HashMap<String, Integer> partReplaced = new HashMap<>();
    private ProgressDialog dialog;
    private Long odo;

    public PartsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseDatabase = Helper.getDatabase();
        mDatabase = firebaseDatabase.getReference(Constant.FIREBASE_DB);
        mAuth = FirebaseAuth.getInstance();
        //fetchParts();
        bundle = getArguments();
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.show();
        mDatabase.child("parts_changed").child(bundle.getString("vehicle_id"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            partReplaced.put(snapshot.getKey(), Integer.parseInt("" + snapshot.getValue()));
                        }

                        mDatabase.child("vehicle").child(mAuth.getCurrentUser().getUid()).child(bundle.getString("vehicle_id")).child("odometer")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        odo = (Long) dataSnapshot.getValue();
                                        fetchParts();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    private void fetchParts() {
        Query mRef = mDatabase.child("parts");
        mRef.keepSynced(true);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DataSnapshot> snapshots = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshots.add(snapshot);
                }
                dialog.dismiss();
                listView.setAdapter(new CustomAdapter(snapshots));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });
        /*FirebaseListOptions<Parts> options = new FirebaseListOptions.Builder<Parts>()
                .setQuery(mRef, Parts.class)
                .setLayout(R.layout.list_item_parts)
                .build();
        FirebaseListAdapter<Parts> adapter = new FirebaseListAdapter<Parts>(options) {
            @Override
            protected void populateView(View v, final Parts model, int position) {

            }
        };
        listView.setAdapter(adapter);*/
    }

    private class CustomAdapter extends BaseAdapter {


        private final List<DataSnapshot> documentSnapshots;

        public CustomAdapter(List<DataSnapshot> documentSnapshots) {
            this.documentSnapshots = documentSnapshots;
        }

        @Override
        public int getCount() {
            return documentSnapshots.size();
        }

        @Override
        public Parts getItem(int position) {
            return documentSnapshots.get(position).getValue(Parts.class);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null)
                v = getLayoutInflater().inflate(R.layout.list_item_parts, null);

            final Parts model = getItem(position);

            ((TextView) v.findViewById(R.id.part_name)).setText(model.getName());
            Integer partDue = 0;
            long progress = 0;
            Integer lastDone = 0;

            if (partReplaced.containsKey(model.getId())) {
                lastDone = partReplaced.get(model.getId());
            }
            long rem = 0;

            if (lastDone == 0) {
                int i = 0;
                while (i < odo)
                    i += model.getKms();
                partDue = i;
            } else {
                partDue = lastDone + model.getKms();
            }
            rem = partDue - odo;

            if (rem < 0) {
                ((ProgressBar) v.findViewById(R.id.part_progress)).setProgress(100);
            } else {
                progress = (model.getKms() - rem) * 100 / model.getKms();
                ((ProgressBar) v.findViewById(R.id.part_progress)).setProgress((int) progress);
            }
            ((TextView) v.findViewById(R.id.part_kms)).setText("At " + partDue + " KM ( Rem : " + rem + " KM)");
            v.findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("" + model.getName() + " has been replaced.Please choose option to record your expense : ");
                    builder.setPositiveButton("DONE WITH LAST SERVICE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mDatabase.child("vehicle").child(mAuth.getCurrentUser().getUid()).child(bundle.getString("vehicle_id"))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                                            mDatabase.child("parts_changed").child(vehicle.get_id()).child(model.getId()).setValue(vehicle.getLast_service_odometer());
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                        }
                    });
                    builder.setNegativeButton("CHANGED SEPARATELY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
                            intent.putExtra("expense_type", "PART");
                            intent.putExtra("part_id", model.getId());
                            intent.putExtra("part_name", model.getName());
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                }
            });
            return v;
        }
    }


}
