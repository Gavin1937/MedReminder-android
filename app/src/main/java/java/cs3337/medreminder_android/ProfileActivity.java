package java.cs3337.medreminder_android;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.cs3337.medreminder_android.HttpClient.HttpGetClient;
import java.cs3337.medreminder_android.Util.GlobVariables;

public class ProfileActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fetch current user info
        HttpGetClient fetchMyInfo = new HttpGetClient();
        fetchMyInfo.execute(GlobVariables.API_URL+"/api/user/me");
        while (!fetchMyInfo.ready && !fetchMyInfo.ok) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // fetch doctor user info
        HttpGetClient fetchDocInfo = new HttpGetClient();
        fetchDocInfo.execute(GlobVariables.API_URL+"/api/user/mydoctor");
        while (!fetchDocInfo.ready && !fetchDocInfo.ok) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        setContentView(R.layout.activity_profile);

        // print userinfo on screen
        if (fetchMyInfo.ok) {
            try {
                JSONObject userinfoJson = fetchMyInfo.jsonObject().getJSONObject("pat_info");
                String name =
                    userinfoJson.getString("fname") + " " +
                    userinfoJson.getString("lname")
                ;
                String phone = userinfoJson.getString("phone");
                String email = userinfoJson.getString("email");
                ((TextView)findViewById(R.id.profile_prompt_name_str)).setText(name);
                ((TextView)findViewById(R.id.profile_prompt_phone_str)).setText(phone);
                ((TextView)findViewById(R.id.profile_prompt_email_str)).setText(email);
            } catch (JSONException e) {
                ((TextView)findViewById(R.id.profile_prompt_name_str)).setText(
                    "Fail to fetch User Information. Are you a Patient in the system?"
                );
            }
        }
        else {
            ((TextView)findViewById(R.id.profile_prompt_name_str)).setText(
                "Fail to fetch User Information. Are you a Patient in the system?"
            );
        }


        // print doc userinfo on screen
        if (fetchDocInfo.ok) {
            try {
                JSONObject userinfoJson = fetchDocInfo.jsonObject().getJSONObject("doc_info");
                String name =
                        userinfoJson.getString("fname") + " " +
                                userinfoJson.getString("lname")
                        ;
                String phone = userinfoJson.getString("phone");
                String email = userinfoJson.getString("email");
                ((TextView)findViewById(R.id.profile_prompt_doc_name_str)).setText(name);
                ((TextView)findViewById(R.id.profile_prompt_doc_phone_str)).setText(phone);
                ((TextView)findViewById(R.id.profile_prompt_doc_email_str)).setText(email);
            } catch (JSONException e) {
                ((TextView)findViewById(R.id.profile_prompt_doc_name_str)).setText(
                        "Fail to fetch User Information. Are you a Patient in the system?"
                );
            }
        }
        else {
            ((TextView)findViewById(R.id.profile_prompt_doc_name_str)).setText(
                    "Fail to fetch User Information. Are you a Patient in the system?"
            );
        }

    }
}