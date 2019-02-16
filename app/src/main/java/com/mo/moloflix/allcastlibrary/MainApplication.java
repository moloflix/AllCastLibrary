package com.mo.moloflix.allcastlibrary;

import android.app.Application;


import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DIALService;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        DIALService.registerApp("Levak");
        DiscoveryManager.init(getApplicationContext());

        super.onCreate();
    }

}