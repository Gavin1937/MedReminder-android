package java.cs3337.medreminder_android.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class Utilities {

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Long getUnixTimestamp() {
        return Long.valueOf(Calendar.getInstance().getTimeInMillis()/1000);
    }


    public static String getUsername() {
        try {
            if (
                GlobVariables.CACHE_DATA != null &&
                GlobVariables.CACHE_DATA.has("login") &&
                GlobVariables.CACHE_DATA.getJSONObject("login") != null
            )
            {
                try {
                    return GlobVariables.CACHE_DATA.getJSONObject("login").getString("username");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            return "";
        }
        return "";
    }

    public static String getSecret() {
        try {
            if (
                GlobVariables.CACHE_DATA != null &&
                GlobVariables.CACHE_DATA.has("login") &&
                GlobVariables.CACHE_DATA.getJSONObject("login") != null
            )
            {
                try {
                    return GlobVariables.CACHE_DATA.getJSONObject("login").getString("secret");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            return "";
        }
        return "";
    }

    public static JSONObject getNotiInfo() {
        try {
            if (
                GlobVariables.CACHE_DATA != null &&
                GlobVariables.CACHE_DATA.has("noti") &&
                GlobVariables.CACHE_DATA.getJSONObject("noti") != null
            )
            {
                try {
                    return GlobVariables.CACHE_DATA.getJSONObject("noti");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    public static Calendar nextNotiTime()
    {
        Calendar now = Calendar.getInstance();
        Calendar out = Calendar.getInstance();

        try {
            boolean alreadyNoti =
                (
                    GlobVariables.CACHE_DATA != null &&
                    GlobVariables.CACHE_DATA.has("alreadyNoti")
                ) ?
                GlobVariables.CACHE_DATA.getBoolean("alreadyNoti") :
                false
            ;
            int lastNotiTime =
                (
                    GlobVariables.CACHE_DATA != null &&
                    GlobVariables.CACHE_DATA.has("lastNotiTime")
                ) ?
                GlobVariables.CACHE_DATA.getInt("lastNotiTime") :
                -1
            ;
            // already sent current notification, wait for next time
            if (lastNotiTime > 0 && alreadyNoti) {
                out.setTimeInMillis(lastNotiTime*1000L);
                out.set(Calendar.SECOND, 0);
                out.set(Calendar.MILLISECOND, 0);
            }
            // don't have last noti time, init
            else {
                JSONObject notiInfo = getNotiInfo();
                if (notiInfo == null)
                    throw new JSONException("No notification information.");
                Calendar early = militaryTimeToCal(notiInfo.getInt("early_time"));
                Calendar late = militaryTimeToCal(notiInfo.getInt("late_time"));
                boolean missEarly = (now.getTimeInMillis() > early.getTimeInMillis());
                boolean missLate = (now.getTimeInMillis() > late.getTimeInMillis());

                if (!missEarly && !missLate) {
                    // set to early
                    return early;
                }
                else if (missEarly && !missLate) {
                    // set to late
                    return late;
                }
                else if (missEarly && missLate) {
                    // set to next day early
                    early.set(Calendar.DATE, early.get(Calendar.DATE)+1);
                    return early;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return out;
    }

    public static String militaryTimeToStr(Integer militaryTime)
    {
        int hr = militaryTime / 100;
        int mi = militaryTime % 100;
        return (
            String.format("%1$" + 2 + "s", hr).replace(' ', '0') +
            ":" +
            String.format("%1$" + 2 + "s", mi).replace(' ', '0')
        );
    }

    public static Calendar militaryTimeToCal(Integer militaryTime)
    {
        int hr = militaryTime / 100;
        int mi = militaryTime % 100;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hr);
        cal.set(Calendar.MINUTE, mi);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static String calendarToStr(Calendar cal)
    {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        int hr = cal.get(Calendar.HOUR_OF_DAY);
        int mi = cal.get(Calendar.MINUTE);
        return (
            String.format("%1$" + 4 + "s", year).replace(' ', '0') + "/" +
            String.format("%1$" + 2 + "s", month).replace(' ', '0') + "/" +
            String.format("%1$" + 2 + "s", day).replace(' ', '0') +
            " " +
            String.format("%1$" + 2 + "s", hr).replace(' ', '0') + ":" +
            String.format("%1$" + 2 + "s", mi).replace(' ', '0')
        );
    }
}
