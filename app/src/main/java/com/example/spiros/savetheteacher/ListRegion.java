package com.example.spiros.savetheteacher;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spiros.savetheteacher.Realm.Region;
import com.example.spiros.savetheteacher.Realm.RegionService;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Spiros on 9/5/2017.
 */

public class ListRegion extends AppCompatActivity {

    private static final String TAG = ListRegion.class.getSimpleName();

    private RealmResults<Region> regions;
    private MyAdapter1 adapter;
    private Realm realm;

    private EditText regionNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        regionNameEditText = (EditText) findViewById(R.id.txt_list_region_name);

        realm = Realm.getDefaultInstance();

        try {
            regions = realm.where(Region.class).findAll();
        } catch (Exception e) {
            //Do something
        }


        adapter = new MyAdapter1 (this, android.R.layout.simple_list_item_1, regions);

        ListView listView = (ListView) findViewById(R.id.listview_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListRegion.this, "Clicked " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

//    public void addItem(View view) {
//        Region r = new Region(UUID.randomUUID().toString(), regionNameEditText.getText().toString());
//
//        realm.beginTransaction();
//        realm.copyToRealm(r);
//        realm.commitTransaction();
//
//        adapter.notifyDataSetChanged();
//    }

    public void loadRegions(View view) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://83.212.102.247:8083/perioxes.json")
                .addConverterFactory(GsonConverterFactory.create()) //Εάν θέλουμε GSON
                .build();
        RegionService regionService = retrofit.create(RegionService.class);
        regionService.getRegion("2").enqueue(new Callback<Region>() {
            @Override
            public void onResponse(Call<Region> call, Response<Region> response) {
                if (response.isSuccessful()) {
                    Region region = response.body();
                    Log.i(TAG, "New Region: " + region.getName() + " (" + region.getId() + ")");
                } else {
                    Log.e(TAG, "Failed. Status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Region> call, Throwable t) {
                Log.e(TAG, "Failed. Error: " + t.getMessage());
            }
        });
        }


    private class MyAdapter1 extends ArrayAdapter<Region> {
        public MyAdapter1(Context context, int resource, List<Region> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.sample_list_row, parent, false);

                Holder holder = new Holder();
                holder.idTextView = (TextView) rowView.findViewById(R.id.txt_list_row_id);
                holder.nameTextView = (TextView) rowView.findViewById(R.id.txt_list_row_name);
                rowView.setTag(holder);
            }

            Holder holder = (Holder) rowView.getTag();
            holder.idTextView.setText(getItem(position).getId());
            holder.nameTextView.setText(getItem(position).getName());
            return rowView;
        }

        class Holder {
            TextView idTextView;
            TextView nameTextView;
        }
    }


}
