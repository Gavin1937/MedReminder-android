package java.cs3337.medreminder_android;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.cs3337.medreminder_android.HttpClient.HttpGetClient;
import java.cs3337.medreminder_android.Util.GlobVariables;
import java.cs3337.medreminder_android.Util.Utilities;

public class MedInfoActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fetch med info
        HttpGetClient fetchMedInfo = new HttpGetClient();
        fetchMedInfo.execute(GlobVariables.API_URL+"/api/medication/mymed");
        while (!fetchMedInfo.ready && !fetchMedInfo.ok) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        setContentView(R.layout.activity_med_info);

        // print doc userinfo on screen
        if (fetchMedInfo.ok) {
            try {
                JSONObject medInfoJson = fetchMedInfo.jsonObject();
                ((TextView)findViewById(R.id.medinfo_id_str)).setText(
                    Integer.toString(medInfoJson.getInt("id"))
                );
                ((TextView)findViewById(R.id.medinfo_name_str)).setText(
                    medInfoJson.getString("name")
                );
                ((TextView)findViewById(R.id.medinfo_desc_str)).setText(
                    medInfoJson.getString("description")
                );
                ((TextView)findViewById(R.id.medinfo_freq_str)).setText(
                    medInfoJson.getInt("frequency") +
                    " Day"
                );
                ((TextView)findViewById(R.id.medinfo_early_str)).setText(
                    Utilities.militaryTimeToStr(medInfoJson.getInt("early_time"))
                );
                ((TextView)findViewById(R.id.medinfo_late_str)).setText(
                    Utilities.militaryTimeToStr(medInfoJson.getInt("late_time"))
                );
            } catch (JSONException e) {
                ((TextView)findViewById(R.id.medinfo_id_str)).setText(
                    "Fail to fetch Medication Information."
                );
                ((TextView)findViewById(R.id.medinfo_name_str)).setText(
                    "Fail to fetch Medication Information."
                );
                ((TextView)findViewById(R.id.medinfo_desc_str)).setText(
                    "Fail to fetch Medication Information."
                );
                ((TextView)findViewById(R.id.medinfo_freq_str)).setText(
                    "Fail to fetch Medication Information."
                );
                ((TextView)findViewById(R.id.medinfo_early_str)).setText(
                    "Fail to fetch Medication Information."
                );
                ((TextView)findViewById(R.id.medinfo_late_str)).setText(
                    "Fail to fetch Medication Information."
                );
            }
        }
        else {
            ((TextView)findViewById(R.id.medinfo_id_str)).setText(
                "Fail to fetch Medication Information."
            );
            ((TextView)findViewById(R.id.medinfo_name_str)).setText(
                "Fail to fetch Medication Information."
            );
            ((TextView)findViewById(R.id.medinfo_desc_str)).setText(
                "Fail to fetch Medication Information."
            );
            ((TextView)findViewById(R.id.medinfo_freq_str)).setText(
                "Fail to fetch Medication Information."
            );
            ((TextView)findViewById(R.id.medinfo_early_str)).setText(
                "Fail to fetch Medication Information."
            );
            ((TextView)findViewById(R.id.medinfo_late_str)).setText(
                "Fail to fetch Medication Information."
            );
        }

    }



}