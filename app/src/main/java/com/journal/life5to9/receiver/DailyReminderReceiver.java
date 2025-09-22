package com.journal.life5to9.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.journal.life5to9.service.DailyReminderService;

public class DailyReminderReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        // Show the daily reminder notification
        DailyReminderService.showDailyReminder(context);
    }
}
