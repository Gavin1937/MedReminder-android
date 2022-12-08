package java.cs3337.medreminder_android.Util;

import android.app.ActionBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.cs3337.medreminder_android.NotificationMessage;

public class GlobVariables {

    public static boolean IS_LOGGED_IN = false;
    public static String API_URL = "http://44.202.127.191";
    public static String CACHE_FILENAME = "cache.json";
    public static JSONObject CACHE_DATA = null;

    public static String CHANNEL_ID = "MedReminder Notification";
    public static int NOTIFICATION_ID = 1;
    public static NotificationMessage notificationMessage = null;
}
