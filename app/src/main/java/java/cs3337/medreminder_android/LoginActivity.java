package java.cs3337.medreminder_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginbtn = findViewById(R.id.login_btn);
        loginbtn.setOnClickListener(v -> {
            TextInputLayout username = findViewById(R.id.login_username_edit);
            TextInputLayout password = findViewById(R.id.login_password_edit);
            TextView msg = findViewById(R.id.msg);
            String output =
                    Objects.requireNonNull(((TextInputLayout) username.getChildAt(1))
                            .getEditText()).getText().toString() +
                            "\n" +
                    Objects.requireNonNull(((TextInputLayout) password.getChildAt(1))
                            .getEditText()).getText().toString()
                    ;
            System.out.println(output);
            msg.setText(output);
        });
    }
}