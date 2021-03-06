package com.car.maintenance.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.car.maintenance.Application.Constant;
import com.car.maintenance.Application.Helper;
import com.car.maintenance.Database.Blog;
import com.car.maintenance.Database.Company;
import com.car.maintenance.Database.Model;
import com.car.maintenance.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectModelFragment extends Fragment {

    @BindView(R.id.list)
    ListView listView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Bundle mainBundle;

    public SelectModelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_model, container, false);
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
        fetchModel(mainBundle.getString("company"));
    }

    private void fetchModel(String company) {
        Query mRef = mDatabase.child("model").child(company).orderByChild("name");
        mRef.keepSynced(true);
        FirebaseListAdapter<Model> adapter = new FirebaseListAdapter<Model>(getActivity(),
                Model.class,
                R.layout.list_item_model,
                mRef
                ) {
            @Override
            protected void populateView(View v, final Model model, int position) {
                ((TextView) v.findViewById(R.id.name)).setText(model.getName());
                Glide.with(getActivity()).load(model.getImage()).into((ImageView) v.findViewById(R.id.logo));
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectModel(model.get_id());
                    }
                });
            }
        };
        listView.setAdapter(adapter);
    }

    public void selectModel(String id) {

        mainBundle.putString("model", id);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SelectFuelFragment fragment = new SelectFuelFragment();
        fragment.setArguments(mainBundle);
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}
