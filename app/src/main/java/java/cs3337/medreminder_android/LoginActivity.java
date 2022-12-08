package java.cs3337.medreminder_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.cs3337.medreminder_android.HttpClient.HttpPostClient;
import java.cs3337.medreminder_android.Util.GlobVariables;
import java.cs3337.medreminder_android.Util.Utilities;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginbtn = findViewById(R.id.button);
        loginbtn.setOnClickListener(v -> {
            // Collect user input
            TextInputLayout username = findViewById(R.id.login_username_edit);
            TextInputLayout password = findViewById(R.id.login_password_edit);
            String usernameStr = Objects.requireNonNull(
                ((TextInputLayout) username.getChildAt(1)).getEditText()
            ).getText().toString();
            String passwordStr = Objects.requireNonNull(
                ((TextInputLayout) password.getChildAt(1)).getEditText()
            ).getText().toString();

            // generating request parameters
            String authHash = Utilities.md5(usernameStr+passwordStr);
            JSONObject param = new JSONObject();
            try {
                param.put("username", usernameStr);
                param.put("auth_hash", authHash);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // make request
            HttpPostClient client = new HttpPostClient();
            client.execute(GlobVariables.API_URL+"/api/auth", param.toString());
            while (!client.ready && !client.ok) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(param);
            System.out.println("ok = " + client.ok + ", status = " + client.status);
            System.out.println("hasPayload(): " + client.hasPayload());
            System.out.println(client.payload);

            // handle response
            TextView errmsg = findViewById(R.id.errmsg);
            if (!client.ok) {
                errmsg.setVisibility(View.VISIBLE);
            }
            else {
                errmsg.setVisibility(View.INVISIBLE);
                // save login info into local file
                try (FileOutputStream fos = getApplication()
                    .openFileOutput(GlobVariables.CACHE_FILENAME, Context.MODE_PRIVATE)
                ) {
                    fos.write(client.payload.getBytes(StandardCharsets.US_ASCII));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // jump back to MainActivity
                GlobVariables.IS_LOGGED_IN = true;
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }

}