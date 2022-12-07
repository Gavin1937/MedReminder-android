package java.cs3337.medreminder_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    boolean isLoggedIn = false;

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

//        Button btn = (Button) findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!isLoggedIn) {
//                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//                    startActivity(i);
//                }
//            }
//        });

    }
}