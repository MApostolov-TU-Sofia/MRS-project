package com.example.bank_app.account;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bank_app.MainActivity;
import com.example.bank_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AccountLoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private ImageButton homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);

        this.usernameEditText = findViewById(R.id.inp_username);
        this.passwordEditText = findViewById(R.id.inp_password);
        this.loginButton = findViewById(R.id.loginButton);
        this.homeButton = findViewById(R.id.btn_home);

        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Execute AsyncTask to perform transfer operation
                new TransferTask().execute(username, password);
            }
        });

        this.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountLoginActivity.this, MainActivity.class));
            }
        });
    }

    private class TransferTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            try {
                // Create JSON object with username and amount
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("username", username);
                jsonInput.put("password", password);

                // Define your Flask server endpoint
                URL url = new URL("http://10.0.2.2:8000/user/login");

                // Open connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Write JSON data to the server
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInput.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Read the response from the server
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    StringBuilder response = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        response.append(scanner.nextLine());
                    }
                    return response.toString();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject respJSON = null;
            try {
                respJSON = new JSONObject(result.toString());
                if (respJSON.get("status").equals("success") && respJSON.get("message").equals("Successful login")) {
                    MainActivity.appLoggedUser.setUser(respJSON.get("username").toString(), respJSON.get("token").toString());

                    startActivity(new Intent(AccountLoginActivity.this, AccountViewActivity.class));
                } else {
                    Toast.makeText(AccountLoginActivity.this, "Invalid login", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

