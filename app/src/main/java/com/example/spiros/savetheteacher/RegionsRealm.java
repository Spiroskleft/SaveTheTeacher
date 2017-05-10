package com.example.spiros.savetheteacher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.spiros.savetheteacher.Realm.Region;

import io.realm.Realm;

/**
 * Created by Spiros on 10/5/2017.
 */

public class RegionsRealm extends AppCompatActivity {

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        Button importbtn = (Button) findViewById(R.id.importbtn);
        importbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RealmImporter realmImporter = new RealmImporter(getResources());
                realmImporter.importFromJson();
            }
        });

//        Button countbtn = (Button) findViewById(R.id.count);
//        countbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                realm = Realm.getDefaultInstance();
//
//                int people = realm.where(People.class).findAll().size();
//                if (people>0) {
//                    Snackbar.make(view, "Found: " + people + " people in the database", Snackbar.LENGTH_LONG).show();
//                }else {
//                    Snackbar.make(view, "Found no people in the database!", Snackbar.LENGTH_LONG).show();
//                }
//
//            }
//        });

        Button viewNamebtn = (Button) findViewById(R.id.viewname);
        viewNamebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Region first = realm.where(Region.class).findFirst();
                Log.d("onclick","*****************************clicked");
                Toast.makeText(RegionsRealm.this, "your message"+ first.getName(), Toast.LENGTH_SHORT)
                        .show();

                //Snackbar.make(view, "First person's name is: "+ first.getName() + " and his age is: " + first.getSubName(), Snackbar.LENGTH_LONG).show();
            }
        });
//
//        Button changeNamebtn = (Button) findViewById(R.id.changename);
//        changeNamebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                realm.executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                        People michael = realm.where(People.class).findFirst();
//                        michael.setName("John");
//                    }
//                });
//            }
//        });

    }

    @Override
    protected void onDestroy() {
        if (!realm.isClosed()) realm.close();
        super.onDestroy();
    }


}