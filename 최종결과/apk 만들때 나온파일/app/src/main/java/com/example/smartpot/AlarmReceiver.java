package com.example.smartpot;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmReceiver {
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in = new Intent(context, NotiService.class);
            context.startForegroundService(in);
        } else {
            Intent in = new Intent(context, NotiService.class);
            context.startService(in);
        }
    }
}
