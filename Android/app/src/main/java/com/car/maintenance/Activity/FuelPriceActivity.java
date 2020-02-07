package com.car.maintenance.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.car.maintenance.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FuelPriceActivity extends AppCompatActivity {

    @BindView(R.id.list)
    ListView lvPrices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_price);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Live Fuel Prices");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            JSONObject response = new JSONObject(getIntent().getStringExtra("data"));
            lvPrices.setAdapter(new CustomAdapter(response.getJSONArray("cities")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public class CustomAdapter extends BaseAdapter {


        private JSONArray data;

        public CustomAdapter(JSONArray data) {

            this.data = data;
        }

        @Override
        public int getCount() {
            return data.length();
        }

        @Override
        public JSONObject getItem(int position) {
            try {
                return data.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.list_item_fuel_price, null);


            JSONObject object = getItem(position);
            try {
                ((TextView) view.findViewById(R.id.petrol)).setText("Rs " + object.getString("petrol"));
                ((TextView) view.findViewById(R.id.diesel)).setText("Rs " + object.getString("diesel"));
                ((TextView) view.findViewById(R.id.city)).setText(object.getString("city"));
                ((TextView) view.findViewById(R.id.state)).setText(object.getString("state"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return view;
        }
    }
}
