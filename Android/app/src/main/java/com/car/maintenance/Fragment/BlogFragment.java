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
import com.car.maintenance.Activity.WebActivity;
import com.car.maintenance.Application.Constant;
import com.car.maintenance.Application.Helper;
import com.car.maintenance.Database.Blog;
import com.car.maintenance.Database.Parts;
import com.car.maintenance.Database.Vehicle;
import com.car.maintenance.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
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
public class BlogFragment extends Fragment {

    public static final String TAG = "BlogFragment";
    @BindView(R.id.listview)
    ListView listView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private InterstitialAd interstitialAd;
    private String redirect_link;
    private String redirect_title;

    public BlogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseDatabase = Helper.getDatabase();
        mDatabase = firebaseDatabase.getReference(Constant.FIREBASE_DB);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        updateData();
        loadFacebookAd();
    }

    public void loadFacebookAd() {
        interstitialAd = new InterstitialAd(getActivity(), "261287334429127_261292277761966");
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Intent intent = new Intent(getActivity(), WebActivity.class);
                if (redirect_link != null) {
                    intent.putExtra("url", redirect_link);
                    intent.putExtra("title", redirect_title);
                    startActivity(intent);
                }
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

    public void updateData() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.show();
        firebaseDatabase = Helper.getDatabase();
        mDatabase = firebaseDatabase.getReference(Constant.FIREBASE_DB);
        mDatabase.child("blogs").orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
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
        public Blog getItem(int position) {
            return documentSnapshots.get(position).getValue(Blog.class);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null)
                v = getLayoutInflater().inflate(R.layout.list_item_blog, null);

            final Blog model = getItem(position);

            ((TextView) v.findViewById(R.id.title)).setText(model.getTitle());
            ((TextView) v.findViewById(R.id.description)).setText(model.getDescription());
            v.findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("title", model.getTitle());
                    mFirebaseAnalytics.logEvent("blogs", bundle);
                    if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                        interstitialAd.show();
                        redirect_link = model.getLink();
                        redirect_title = model.getTitle();
                    } else {
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        intent.putExtra("url", model.getLink());
                        intent.putExtra("title", model.getTitle());
                        startActivity(intent);
                    }
                }
            });
            return v;
        }
    }

}
