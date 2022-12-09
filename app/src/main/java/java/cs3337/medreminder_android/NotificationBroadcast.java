package java.cs3337.medreminder_android;


import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.cs3337.medreminder_android.Util.GlobVariables;

public class NotificationBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String locTitle;
        String locMessage;
        if (
            GlobVariables.notificationMessage == null ||
            GlobVariables.notificationMessage.isVisited() ||
            GlobVariables.notificationMessage.getTitle() == null ||
            GlobVariables.notificationMessage.getMessage() == null
        )
        {
            locTitle = "This is title";
            locMessage = "This is the content of the notification";
        }
        else {
            locTitle = GlobVariables.notificationMessage.getTitle();
            locMessage = GlobVariables.notificationMessage.getMessage();
            GlobVariables.notificationMessage.setVisited();
        }
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
        stackBuilder.getPendingIntent(0,
        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder =
        new NotificationCompat.Builder(context, GlobVariables.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(locTitle)
            .setContentText(locMessage)
            .setContentIntent(resultPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(GlobVariables.NOTIFICATION_ID, builder.build());
        GlobVariables.notificationMessage = null;
    }

}