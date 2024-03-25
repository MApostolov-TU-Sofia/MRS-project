package com.example.bank_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bank_app.account.AccountLoginActivity;
import com.example.bank_app.account.AccountRegisterActivity;
import com.example.bank_app.model.LoggedUser;
import com.example.bank_app.model.User;

public class MainActivity extends AppCompatActivity {

    public static LoggedUser appLoggedUser;
    public static User appUser;
    private Button loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.appLoggedUser = new LoggedUser();
        this.appUser = new User();
        this.loginButton = findViewById(R.id.btn_login);
        this.registerButton = findViewById(R.id.btn_register);

        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AccountLoginActivity.class));
            }
        });

        this.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AccountRegisterActivity.class));
            }
        });
    }

}