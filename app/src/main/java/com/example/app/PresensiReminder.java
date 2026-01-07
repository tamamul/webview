package com.example.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import java.util.Calendar;

public class PresensiReminder extends BroadcastReceiver {
    
    private static final String CHANNEL_ID = "presensi_channel";
    private static final int NOTIFICATION_ID_MASUK = 1001;
    private static final int NOTIFICATION_ID_SIANG = 1002;
    private static final int NOTIFICATION_ID_PULANG = 1003;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        int notificationId = intent.getIntExtra("notification_id", NOTIFICATION_ID_MASUK);
        
        showNotification(context, title, message, notificationId);
    }
    
    private void showNotification(Context context, String title, String message, int notificationId) {
        createNotificationChannel(context);
        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        
        NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        }
    }
    
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Pengingat Presensi";
            String description = "Notifikasi pengingat untuk presensi harian";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            
            NotificationManager notificationManager = 
                    context.getSystemService(NotificationManager.class);
            
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    
    // Static method untuk setup alarm dari MainActivity
    public static void setupPresensiReminder(Context context) {
        scheduleNotification(context, 7, 0, 
                "Waktunya Presensi Masuk!", 
                "Jangan lupa melakukan presensi masuk hari ini di aplikasi SMK MAARIF 9",
                NOTIFICATION_ID_MASUK);
        
        scheduleNotification(context, 12, 0, 
                "Pengingat Presensi", 
                "Apakah Anda sudah melakukan presensi hari ini?",
                NOTIFICATION_ID_SIANG);
        
        scheduleNotification(context, 15, 0, 
                "Waktunya Presensi Pulang!", 
                "Jangan lupa melakukan presensi pulang sebelum pulang sekolah",
                NOTIFICATION_ID_PULANG);
    }
    
    private static void scheduleNotification(Context context, int hour, int minute, 
                                            String title, String message, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        Intent intent = new Intent(context, PresensiReminder.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("notification_id", notificationId);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        
        // Cek jika waktu sudah lewat, set untuk besok
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        if (alarmManager != null) {
            // Gunakan setExactAndAllowWhileIdle untuk Android 6.0+ agar lebih reliable
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 
                        calendar.getTimeInMillis(), pendingIntent);
            } 
            // Gunakan setExact untuk Android 4.4+
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, 
                        calendar.getTimeInMillis(), pendingIntent);
            }
            // Untuk Android lama
            else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, 
                        calendar.getTimeInMillis(), pendingIntent);
            }
        }
        
        // Set repeating alarm untuk setiap hari
        Calendar repeatCalendar = Calendar.getInstance();
        repeatCalendar.setTimeInMillis(System.currentTimeMillis());
        repeatCalendar.set(Calendar.HOUR_OF_DAY, hour);
        repeatCalendar.set(Calendar.MINUTE, minute);
        repeatCalendar.set(Calendar.SECOND, 0);
        
        // Jika mau repeating setiap hari, uncomment kode di bawah
        /*
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
                    repeatCalendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        */
    }
}