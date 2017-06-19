package com.example.spiros.savetheteacher.Realm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.spiros.savetheteacher.MainActivity;
import com.example.spiros.savetheteacher.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Spiros on 23/5/2017.
 */


/**
 * Σε αυτή τη κλάση δημιουργούμε μία λίστα από JSON αρχείο σε Realm με τις περιοχές αναπληρωτών πρωτοβάθμιας εκπαίδευσης.
 */
public class RegionsListActivity extends AppCompatActivity {
    private static final String TAG = RegionsListActivity.class.getSimpleName();
    private Realm realm;
    private RealmResults<Region> regionRealmList;
    public static Region selectedRegions = new Region();
    private List<Region> regionList;
    private MyAdapter100 myAdapter100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.region_center);
        setContentView(R.layout.regions_view);
        InputStream inputStream = getResources().openRawResource(getResources().getIdentifier("perioxes","raw", getPackageName()));
        String sxml = readTextFile(inputStream);
        System.out.println("----------------------->"+sxml);


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
            realm = Realm.getDefaultInstance() ;

            for (int i=0;i<regionList.size();i++) {

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
        regionRealmList= realm.where(Region.class)
                //                  .equalTo("Id",1)
                .findAll();

       // setContentView(R.layout.regions_view);
        System.out.println("----->"+regionRealmList.size());

        System.out.println("----->"+regionRealmList.get(1).getSubName());

        //   String regionsss =regionList.toString() ;






        myAdapter100 = new MyAdapter100(this, R.layout.view_list_row, regionList);

        ListView listView = (ListView) findViewById(R.id.RegionList200);
        listView.setAdapter(myAdapter100);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selectedRegions= (Region) parent.getItemAtPosition(position);
//                System.out.println("----->" + selectedRegions.toString());
//
//            }
//        });
    }

    public class MyAdapter100 extends ArrayAdapter<Region> {
        public MyAdapter100(Context context, int resource, List<Region> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.view_list_row, parent, false);

                Holder holder = new Holder();
                holder.idTextView = (TextView) rowView.findViewById(R.id.id1);
                holder.nameTextView = (TextView) rowView.findViewById(R.id.name1);
                holder.subnameTextView = (TextView) rowView.findViewById(R.id.subname1);
                holder.officeTextView = (TextView) rowView.findViewById(R.id.office1);
                rowView.setTag(holder);
            }

            Holder holder = (Holder) rowView.getTag();
            holder.idTextView.setText(getItem(position).getId().toString());
            holder.nameTextView.setText(getItem(position).getName());
            holder.subnameTextView.setText(getItem(position).getSubName());
            holder.officeTextView.setText(getItem(position).getOffice());
            return rowView;
        }
        public class Holder {
            public TextView idTextView;
            public TextView nameTextView;
            public TextView subnameTextView;
            public TextView officeTextView;

        }
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



    @Override
    public void onBackPressed() {
        Log.d("Back", "onBackPressed Called");
        Intent ki = new Intent(this, MainActivity.class);
        startActivity(ki);
    }
}