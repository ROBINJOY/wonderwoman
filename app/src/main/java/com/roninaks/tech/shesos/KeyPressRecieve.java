package com.roninaks.tech.shesos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by nihalpradeep on 24/02/18.
 */

public class KeyPressRecieve extends BroadcastReceiver {

    private boolean screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
            Log.e("Screen state", "" + screenOff);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
            Log.e("Screen state", "" + screenOff);
        }
        Intent i = new Intent(context, KeyPressService.class);
        i.putExtra("screen_state", screenOff);
        i.putExtra("start_flag", true);
        i.putExtra("time", System.currentTimeMillis());
        context.startService(i);
    }

}
