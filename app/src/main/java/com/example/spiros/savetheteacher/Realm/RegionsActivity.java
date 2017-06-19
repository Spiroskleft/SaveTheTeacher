package com.example.spiros.savetheteacher.Realm;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.spiros.savetheteacher.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Spiros on 19/6/2017.
 */

public class RegionsActivity extends AppCompatActivity {

    private Realm realm;
    private RealmResults<Region> regionRealmList;
    public static Region selectedRegions = new Region();
    private List<Region> regionList;

    ArrayList<Region> PerioxesList = new ArrayList<Region>();
    private MyCustomAdapter myadapterforRegions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.region_center);
        //Ορίζουμε τον adapter για τα news
        PerioxesList.add(new Region());
        myadapterforRegions = new MyCustomAdapter(this, PerioxesList);
//        ListView lsNews = (ListView) findViewById(R.id.RegionList200);
//        lsNews.setAdapter(myadapterforRegions);// with data


        setContentView(R.layout.regions_view);
        InputStream inputStream = getResources().openRawResource(getResources().getIdentifier("perioxes", "raw", getPackageName()));
        String sxml = readTextFile(inputStream);
        System.out.println("----------------------->" + sxml);


        //Αρχικοποιούμε τον json αντικείμενο
        Gson gson = new Gson();

        //Κάνουμε τα κατάλληλα Cast για να μας επιστρέψει την List που θέλουμε
        Type type = new TypeToken<List<Region>>() {
        }.getType();
//                System.out.println(output);
        //Μετατρέπουμε το Json σε List<Applicationn>
        try {
            regionList = gson.fromJson(sxml, type);
            //Αρχικοποίηση του Realm
            realm = Realm.getDefaultInstance();

            for (int i = 0; i < regionList.size(); i++) {

                Region region = new Region();
                region = regionList.get(i);

                realm.beginTransaction();
                realm.copyToRealmOrUpdate(region);
                realm.commitTransaction();
            }

        } catch (IllegalStateException | JsonSyntaxException e) {
            e.printStackTrace();
        }
        //Κάνουμε ερώτημα στη Βάση για να φέρουμε όλα τις περιοχές και τις βάζουμε σε μία RealmList
        regionRealmList = realm.where(Region.class)
                //                  .equalTo("Id",1)
                .findAll();

        // setContentView(R.layout.regions_view);
        System.out.println("----->" + regionRealmList.size());

        System.out.println("----->" + regionRealmList.get(1).getSubName());
        ListView lsNews = (ListView) findViewById(R.id.RegionList200);
        lsNews.setAdapter(myadapterforRegions);// with data

    }

    public String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }

    // O adapter για την list των news
    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<Region> PerioxesListAdapter;
        Context context;

        public MyCustomAdapter(Context context, ArrayList<Region> listnewsDataAdpater) {
            this.PerioxesListAdapter = listnewsDataAdpater;
            this.context = context;
        }


        @Override
        public int getCount() {
            return PerioxesListAdapter.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Region s = PerioxesListAdapter.get(position);

            LayoutInflater mInflater = getLayoutInflater();
            View myView = mInflater.inflate(R.layout.view_list_row, null);

            TextView txtId = (TextView) myView.findViewById(R.id.id1);

            TextView txtName = (TextView) myView.findViewById(R.id.name1);
            txtName.setText(s.getName());

            TextView txtSubName = (TextView) myView.findViewById(R.id.subname1);
            txtSubName.setText(s.getSubName());

            TextView txtOffice = (TextView) myView.findViewById(R.id.subname1);
            txtOffice.setText(s.getOffice());

            return myView;

        }


    }
}
