package com.fusion.kim.alc30;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class SecureEncryption  extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference().keepSynced(true);

    }
}
