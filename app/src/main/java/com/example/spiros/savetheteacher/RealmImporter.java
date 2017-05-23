package com.example.spiros.savetheteacher;

/**
 * Created by Spiros on 10/5/2017.
 */

import android.content.res.Resources;
import android.util.Log;

import com.example.spiros.savetheteacher.Realm.Region;

import java.io.InputStream;

import io.realm.Realm;

/**
 * From json to realm database
 */
public class RealmImporter {

    Resources resources;


    public RealmImporter(Resources resources) {
        this.resources = resources;
    }

    public void importFromJson(){

        // Αρχικοποίηση του Realm
        Realm realm = Realm.getDefaultInstance();


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                InputStream inputStream = resources.openRawResource(R.raw.perioxes);
                try {
                    realm.createAllFromJson(Region.class, inputStream);

                } catch (Exception e){
                    realm.cancelTransaction();
                } finally {
                    if(realm != null) {
                        realm.close();
                    }
                }

            }
        });

        Log.d( "Realm","createAllFromJson Task completed !" );
    }

}