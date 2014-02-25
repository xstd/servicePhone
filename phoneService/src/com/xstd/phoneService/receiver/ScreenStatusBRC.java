package com.xstd.phoneService.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.xstd.phoneService.secondeService.SMSFilterService;

/**
 * Created by michael on 14-2-1.
 */
public class ScreenStatusBRC extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        //check Google Service if runging for SMS
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(context, SMSFilterService.class);
        context.startService(serviceIntent);

    }

}
