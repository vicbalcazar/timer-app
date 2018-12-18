package com.cpsc411.campususer.finalmet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by perez on 11/14/2017.
 */

public class SetAlarmActivity extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent){
        Toast.makeText(context, "SET ALARM", Toast.LENGTH_LONG).show();

        String state = intent.getExtras().getString("extra");

    }
}
