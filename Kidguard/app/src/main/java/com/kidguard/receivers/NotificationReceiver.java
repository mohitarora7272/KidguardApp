package com.kidguard.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kidguard.interfaces.Constant;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.services.LocationService;

public class NotificationReceiver extends BroadcastReceiver implements Constant{

    @Override
    public void onReceive(Context context, Intent intent) {
        String pack = intent.getStringExtra("package");
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        Log.d("package", "package??" + pack);
        Log.d("title", "title??" + title);
        Log.d("text", "text??" + text);


//        String tag = intent.getStringExtra("tag");
//        if(tag.equals(TAG_IMAGES)){
//            Intent myIntent = new Intent(context, BackgroundDataService.class);
//            context.startService(myIntent);
//            return;
//        }
//
//        if(tag.equals(TAG_FILES)){
//            Intent myIntent = new Intent(context, BackgroundDataService.class);
//            context.startService(myIntent);
//            return;
//        }
//
//        if(tag.equals(TAG_CALLS)){
//            Intent myIntent = new Intent(context, BackgroundDataService.class);
//            context.startService(myIntent);
//            return;
//        }
//
//        if(tag.equals(TAG_CONTACTS)){
//            Intent myIntent = new Intent(context, BackgroundDataService.class);
//            context.startService(myIntent);
//            return;
//        }
//
//        if(tag.equals(TAG_LIST_APPS)){
//            Intent myIntent = new Intent(context, BackgroundDataService.class);
//            context.startService(myIntent);
//            return;
//        }
//
//        if(tag.equals(TAG_SMS)){
//            Intent myIntent = new Intent(context, BackgroundDataService.class);
//            context.startService(myIntent);
//            return;
//        }
//
//        if(tag.equals("LOCATION")){
//            Intent myIntent = new Intent(context, LocationService.class);
//            context.startService(myIntent);
//            return;
//        }

    }
}

