package com.car.maintenance.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.car.maintenance.Application.Constant;
import com.car.maintenance.Application.Helper;
import com.car.maintenance.Database.Transaction;
import com.car.maintenance.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ExpenseFragment extends Fragment {

    public static String TAG = "expense";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;

    @BindView(R.id.listview)
    ListView listView;

    public ExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseDatabase = Helper.getDatabase();
        mDatabase = firebaseDatabase.getReference(Constant.FIREBASE_DB);
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();
        getTransactions();
    }

    public void getTransactions() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.show();
        mFireStore.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("transaction")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
                        listView.setAdapter(new CustomAdapter(documents));
                        dialog.dismiss();
                    }
                });
    }

    private class CustomAdapter extends BaseAdapter {


        private List<DocumentSnapshot> documentSnapshots;

        public CustomAdapter(List<DocumentSnapshot> documentSnapshots) {
            this.documentSnapshots = documentSnapshots;
        }

        @Override
        public int getCount() {
            return documentSnapshots.size();
        }

        @Override
        public Transaction getItem(int position) {
            return documentSnapshots.get(position).toObject(Transaction.class);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.list_item_expense, null);
            Transaction transaction = getItem(position);
            if (transaction.getType().equals("REFUEL")) {
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.gas_station);
                ((ImageView) view.findViewById(R.id.icon)).setColorFilter(getResources().getColor(R.color.White));
                ((ImageView) view.findViewById(R.id.icon)).setBackgroundResource(R.drawable.custom_button_blue);
            } else if (transaction.getType().equals("SERVICE")) {
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.hammer);
                ((ImageView) view.findViewById(R.id.icon)).setColorFilter(getResources().getColor(R.color.White));
                ((ImageView) view.findViewById(R.id.icon)).setBackgroundResource(R.drawable.custom_button_orange);
            } else if (transaction.getType().equals("PART")) {
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.wrench);
                ((ImageView) view.findViewById(R.id.icon)).setColorFilter(getResources().getColor(R.color.White));
                ((ImageView) view.findViewById(R.id.icon)).setBackgroundResource(R.drawable.custom_button_red);
            } else if (transaction.getType().equals("EXPENSE")) {
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.cash);
                ((ImageView) view.findViewById(R.id.icon)).setColorFilter(getResources().getColor(R.color.White));
                ((ImageView) view.findViewById(R.id.icon)).setBackgroundResource(R.drawable.custom_button_teal);
            }

            if (position == 0)
                view.findViewById(R.id.item_line).setBackgroundResource(R.drawable.custom_button_light_blue_top);
            else if (position == getCount() - 1)
                view.findViewById(R.id.item_line).setBackgroundResource(R.drawable.custom_button_light_blue_bottom);
            else
                view.findViewById(R.id.item_line).setBackgroundResource(R.drawable.custom_button_light_blue);


            ((TextView) view.findViewById(R.id.type)).setText(transaction.getType());
            ((TextView) view.findViewById(R.id.info)).setText(transaction.getNote());
            ((TextView) view.findViewById(R.id.amount)).setText("" + transaction.getAmount());
            ((TextView) view.findViewById(R.id.odometer)).setText(transaction.getOdometer() + " KM");
            ((TextView) view.findViewById(R.id.date)).setText(transaction.getDate());
            return view;
        }
    }

}
