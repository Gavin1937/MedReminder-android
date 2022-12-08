package java.cs3337.medreminder_android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.cs3337.medreminder_android.Util.GlobVariables;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobVariables.IS_LOGGED_IN = loginCheck();

        // go to login page
        if (!GlobVariables.IS_LOGGED_IN) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            setContentView(R.layout.activity_login);
        }


        // setting up main activity
        setContentView(R.layout.activity_main);


        // profile button
        Button profile_btn = findViewById(R.id.profile_btn);
        profile_btn.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
        });

        // logout button
        Button logout_btn = findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(view -> {
            getApplicationContext().deleteFile(GlobVariables.CACHE_FILENAME);
            GlobVariables.IS_LOGGED_IN = false;
            GlobVariables.LOGIN_INFO = null;
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        });

    }

    private boolean loginCheck() {
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

        // load login info before return
        try {
            GlobVariables.LOGIN_INFO = new JSONObject(contents);
        } catch (JSONException e) {
            // cannot parse cache file
            return false;
        }

        return true;
    }

}