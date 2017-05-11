package com.example.spiros.savetheteacher;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Spiros on 10/5/2017.
 */

public class RegionsRealm extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Config Realm for the application
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("Regions.realm")
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);
    }



}