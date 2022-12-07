package java.cs3337.medreminder_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.cs3337.medreminder_android.HttpClient.*;


public class MainActivity extends AppCompatActivity {

    boolean isLoggedIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isLoggedIn) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            setContentView(R.layout.activity_login);
        }
        else {
            setContentView(R.layout.activity_main);
        }

//        String url = "http://44.202.127.191/api/auth";
        String url = "https://httpbin.org/get";

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpGetClient client = new HttpGetClient();
                client.execute(url);
                while (!client.ready && !client.ok) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                System.out.println(client.payload);
            }
        });

    }
}