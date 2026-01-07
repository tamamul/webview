package com.example.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Bundle;
import java.util.Calendar;

public class NotificationScheduler {
    
    private static final String CHANNEL_ID = "presensi_channel";
    private static final String CHANNEL_NAME = "Pengingat Presensi";
    private static final int NOTIFICATION_ID_MASUK = 1001;
    private static final int NOTIFICATION_ID_PULANG = 1002;
    
    // Setup daily notifications
    public static void setupDailyNotifications(Context context) {
        createNotificationChannel(context);
        
        // Schedule notification for 07:00
        scheduleNotification(context, 7, 0, 
                context.getString(R.string.notification_masuk_title),
                context.getString(R.string.notification_masuk_body),
                NOTIFICATION_ID_MASUK);
        
        // Schedule notification for 14:30
        scheduleNotification(context, 14, 30,
                context.getString(R.string.notification_pulang_title),
                context.getString(R.string.notification_pulang_body),
                NOTIFICATION_ID_PULANG);
        
        // Show immediate notification for testing (comment in production)
        // showTestNotification(context);
    }
    
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            
            channel.setDescription(context.getString(R.string.notification_channel_description));
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 100, 200});
            
            NotificationManager notificationManager = 
                    context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    
    private static void scheduleNotification(Context context, int hour, int minute, 
                                           String title, String message, int notificationId) {
        
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        
        // Pass data to receiver
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("notification_id", notificationId);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        // Set calendar to specified time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        
        // If time has passed, schedule for next day
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        if (alarmManager != null) {
            // Use exact alarm for reliability
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), pendingIntent);
            }
            
            // Set repeating for daily
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
    }
    
    // For testing - shows notification immediately
    public static void showTestNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Test Notifikasi")
                .setContentText("Ini adalah notifikasi test dari Absen-MARSA")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(999, builder.build());
    }
}