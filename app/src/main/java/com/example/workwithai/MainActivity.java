package com.example.workwithai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.workwithai.Activities.DashboardActivity;
import com.example.workwithai.Activities.MenuActivity;
import com.example.workwithai.Activities.SignUpActivity;
import com.example.workwithai.Models.UserModel;
import com.example.workwithai.R;
import com.example.workwithai.Repositories.UserRepository;

public class MainActivity extends AppCompatActivity {
    UserRepository userRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_relative_layout);
        userRepository = new UserRepository(MainActivity.this);

        //find element in layout by id
        EditText edtUsername = findViewById(R.id.username);
        EditText edtPassword = findViewById(R.id.password);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnCancel = findViewById(R.id.btn_cancel);
        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignup = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intentSignup);
            }
        });
        //event for element
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data from username and password
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username)){
                    edtUsername.setError("Please enter username");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("Please enter password");
                    return;
                }
                UserModel user = userRepository.loginUser(username, password);
                assert user != null;
                if (user.getId() > 0 && !TextUtils.isEmpty(user.getUsername())) {
                    //luu thong tin user vao Shared Preference
                    SharedPreferences sharedPf = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPf.edit();
                    editor.putInt("ID_USER", user.getId());
                    editor.putString("USERNAME_USER", user.getUsername());
                    editor.putString("EMAIL_USER", user.getEmail());
                    editor.putInt("ROLE_USER", user.getRole());
                    editor.apply();



                    //show notification
                    Toast.makeText(MainActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    // forward to another screen - DashboardActivity
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID_ACCOUNT", user.getId());
                    bundle.putString("USERNAME_ACCOUNT", user.getUsername());
                    bundle.putString("EMAIL_ACCOUNT", user.getEmail());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Account Invalid", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}