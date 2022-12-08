package java.cs3337.medreminder_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.cs3337.medreminder_android.HttpClient.HttpGetClient;
import java.cs3337.medreminder_android.Util.GlobVariables;
import java.cs3337.medreminder_android.Util.Utilities;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobVariables.IS_LOGGED_IN = loginCheck();

        if (GlobVariables.IS_LOGGED_IN) {
            setContentView(R.layout.activity_main);
        }
        else { // go to login page
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            setContentView(R.layout.activity_login);
        }

        String url = "https://httpbin.org/post";

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> {
            HttpGetClient client = new HttpGetClient();
            client.execute(url, Utilities.md5("hello"));
            while (!client.ready && !client.ok) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("ok = " + client.ok + ", status = " + client.status);
            System.out.println("hasPayload(): " + client.hasPayload());
            System.out.println(client.payload);

        });

        // logout button
        Button logout_btn = findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(view -> {
            getApplicationContext().deleteFile(GlobVariables.CACHE_FILENAME);
            GlobVariables.IS_LOGGED_IN = false;
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