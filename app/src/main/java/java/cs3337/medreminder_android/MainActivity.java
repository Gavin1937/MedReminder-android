package java.cs3337.medreminder_android;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.cs3337.medreminder_android.HttpClient.HttpGetClient;
import java.cs3337.medreminder_android.HttpClient.HttpPutClient;
import java.cs3337.medreminder_android.Util.GlobVariables;
import java.cs3337.medreminder_android.Util.Utilities;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobVariables.IS_LOGGED_IN = launchCheck();
        updateNotiInfo();

        // go to login page
        if (!GlobVariables.IS_LOGGED_IN) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            setContentView(R.layout.activity_login);
        }


        CreateCleanUpThread();
        createNotificationChannel();


        // setting up main activity
        setContentView(R.layout.activity_main);


        // profile button
        Button profile_btn = findViewById(R.id.profile_btn);
        profile_btn.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
        });

        // med info button
        Button medinfo_btn = findViewById(R.id.med_info_btn);
        medinfo_btn.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), MedInfoActivity.class);
            startActivity(i);
        });

        // logout button
        Button logout_btn = findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(view -> {
            getApplicationContext().deleteFile(GlobVariables.CACHE_FILENAME);
            GlobVariables.IS_LOGGED_IN = false;
            GlobVariables.CACHE_DATA = null;
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        });

        Button noti_btn = findViewById(R.id.noti_btn);
        noti_btn.setOnClickListener(view -> {
            try {
                // update cache file
                GlobVariables.CACHE_DATA.put("alreadyNoti", false);
                try (FileOutputStream fos = getApplication()
                    .openFileOutput(GlobVariables.CACHE_FILENAME, Context.MODE_PRIVATE)
                ) {
                    fos.write(
                        GlobVariables.CACHE_DATA.toString().getBytes(StandardCharsets.US_ASCII)
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // update noti prompt
                TextView notiPrompt = findViewById(R.id.noti_prompt);
                notiPrompt.setText(genNotiPrompt());

                // update med history
                HttpPutClient updateMedHistory = new HttpPutClient();
                updateMedHistory.execute(
                    GlobVariables.API_URL+"/api/medication/history"
                );
                while (!updateMedHistory.ready && !updateMedHistory.ok) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

//            // hide button
//            noti_btn.setEnabled(false);
//            noti_btn.setVisibility(View.INVISIBLE);
        });

        // update noti prompt for startup
        TextView notiPrompt = findViewById(R.id.noti_prompt);
        notiPrompt.setText(genNotiPrompt());
        scheduleNotification();


    }

    private boolean launchCheck() {

        // try to open cache file
        String contents;
        try {
            FileInputStream fis = getApplicationContext().openFileInput(GlobVariables.CACHE_FILENAME);
            InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.US_ASCII);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
            } catch (IOException e) {
                // Error occurred when opening raw file for reading.
                return false;
            } finally {
                contents = stringBuilder.toString().trim();
            }
        } catch (FileNotFoundException fnfe) {
            // no cache file
            return false;
        }

        // empty cache file
        if (contents.isEmpty()) {
            return false;
        }

        // load cache data before return
        try {
            GlobVariables.CACHE_DATA = new JSONObject(contents);
        } catch (JSONException e) {
            // cannot parse cache file
            return false;
        }

        return true;
    }

    private void CreateCleanUpThread()
    {
        notificationReloadThread = new Thread(new Runnable() {
        boolean performingCleanup = false;
            public void run() {
                try {
                    while (true) {
                        performingCleanup = true;

                        updateNotiInfo();

                        performingCleanup = false;

//                        Thread.sleep(43200000); // 12 hour wait time
                        Thread.sleep(30000); // 30 sec wait time
                    }
                } catch (Exception ex) {
                    System.out.println("Error in CreateCleanUpThread : " + ex.getMessage());
                }
            }
        });
    }

    private void updateNotiInfo()
    {
        // fetch notification info
        HttpGetClient fetchNotiInfo = new HttpGetClient();
        fetchNotiInfo.execute(GlobVariables.API_URL+"/api/notification");
        while (!fetchNotiInfo.ready && !fetchNotiInfo.ok) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // write fetched noti info to cache file
        // & reload CACHE_DATA
        try {
            if (fetchNotiInfo.ok) {
                GlobVariables.CACHE_DATA.put("noti", fetchNotiInfo.jsonObject());
                try (FileOutputStream fos = getApplication()
                    .openFileOutput(GlobVariables.CACHE_FILENAME, Context.MODE_PRIVATE)
                ) {
                    fos.write(
                        GlobVariables.CACHE_DATA.toString().getBytes(StandardCharsets.US_ASCII)
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String genNotiPrompt() {
        Calendar next = Utilities.nextNotiTime();
        String timeStr;
        if (next == null)
            timeStr = "";
        else
            timeStr = Utilities.calendarToStr(next);
        return "Next Notification Time:\n" + timeStr;
    }

    private void createNotificationChannel()
    {
        String name = "Notif Channel";
        String desc = "A Description of the Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(GlobVariables.CHANNEL_ID, name, importance);
        channel.setDescription(desc);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    private void scheduleNotification()
    {
        Calendar nextTime = Utilities.nextNotiTime();
        String timeStr;
        if (nextTime == null)
            timeStr = "";
        else
            timeStr = Utilities.calendarToStr(nextTime);
        Intent intent = new Intent(this, NotificationBroadcast.class);
        String title = "It's Time to Take Your Medicine.";
        String message = "Please Take Your Medicine At: " + timeStr;
        GlobVariables.notificationMessage = new NotificationMessage(title, message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            this,
            GlobVariables.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long time = nextTime.getTimeInMillis();
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        );

        // update cache file
        try {
            GlobVariables.CACHE_DATA.put("lastNotiTime", Utilities.getUnixTimestamp());
            GlobVariables.CACHE_DATA.put("alreadyNoti", true);
            try (FileOutputStream fos = getApplication()
                    .openFileOutput(GlobVariables.CACHE_FILENAME, Context.MODE_PRIVATE)
            ) {
                fos.write(
                        GlobVariables.CACHE_DATA.toString().getBytes(StandardCharsets.US_ASCII)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static Thread notificationReloadThread;
}