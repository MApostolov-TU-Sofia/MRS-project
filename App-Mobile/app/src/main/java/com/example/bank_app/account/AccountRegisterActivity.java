package com.example.bank_app.account;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AccountRegisterActivity extends AppCompatActivity {

    private AccountRegisterActivity self = this;
    private EditText usernameText, passwordText, confirmPasswordText, pinText, nameText, addressText, phoneText, jobText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_register);

        this.usernameText = findViewById(R.id.inp_r_username);
        this.passwordText = findViewById(R.id.inp_r_password);
        this.confirmPasswordText = findViewById(R.id.inp_r_confirm_password);
        this.pinText = findViewById(R.id.inp_r_pin);
        this.nameText = findViewById(R.id.inp_r_name);
        this.addressText = findViewById(R.id.inp_r_address);
        this.phoneText = findViewById(R.id.inp_r_phone);
        this.jobText = findViewById(R.id.inp_r_job);
        this.submitButton = findViewById(R.id.inp_r_submit);
        this.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                self.usernameText.setError(self.usernameText.getText().toString().length() == 0 ?
                        "Add a value" : null);
                self.passwordText.setError(self.passwordText.getText().toString().length() == 0 ?
                        "Add a value" : null);
                self.confirmPasswordText.setError(self.confirmPasswordText.getText().toString().length() == 0 ?
                        "Add a value" : null);
                self.pinText.setError(self.pinText.getText().toString().length() == 0 ?
                        "Add a value" : null);
                self.nameText.setError(self.nameText.getText().toString().length() == 0 ?
                        "Add a value" : null);
                self.addressText.setError(self.addressText.getText().toString().length() == 0 ?
                        "Add a value" : null);
                self.phoneText.setError(self.phoneText.getText().toString().length() == 0 ?
                        "Add a value" : null);
                self.jobText.setError(self.jobText.getText().toString().length() == 0 ?
                        "Add a value" : null);

                boolean submitCompletion = self.usernameText.getText().toString().length() > 0 &&
                        self.passwordText.getText().toString().length() > 0 &&
                        self.confirmPasswordText.getText().toString().length() > 0 &&
                        self.pinText.getText().toString().length() > 0 &&
                        self.nameText.getText().toString().length() > 0 &&
                        self.addressText.getText().toString().length() > 0 &&
                        self.phoneText.getText().toString().length() > 0 &&
                        self.jobText.getText().toString().length() > 0;

                if (submitCompletion) {
                    if (!self.passwordText.getText().toString().equals(self.confirmPasswordText.getText().toString())) {
                        Toast.makeText(AccountRegisterActivity.this, "Write the same confirm passwords", Toast.LENGTH_SHORT).show();
                    } else {
                        self.submitButton.setEnabled(false);
                        new TransferTask().execute();
                    }
                } else {
                    Toast.makeText(AccountRegisterActivity.this, "Fill all required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class TransferTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String usernameTxt = self.usernameText.getText().toString();
            String passwordTxt = self.passwordText.getText().toString();
            String pinTxt = self.pinText.getText().toString();
            String nameTxt = self.nameText.getText().toString();
            String addressTxt = self.addressText.getText().toString();
            String phoneTxt = self.phoneText.getText().toString();
            String jobTxt = self.jobText.getText().toString();

            try {
                // Create JSON object with username and amount
                JSONObject jsonInput = new JSONObject();

                jsonInput.put("requestor", "NEW");
                jsonInput.put("bank_id", 1);
                jsonInput.put("role_id", 2);
                jsonInput.put("username", usernameTxt);
                jsonInput.put("password", passwordTxt);
                jsonInput.put("pin", pinTxt);
                jsonInput.put("name", nameTxt);
                jsonInput.put("address", addressTxt);
                jsonInput.put("phone_nbr", phoneTxt);
                jsonInput.put("job", jobTxt);

                // Define your Flask server endpoint
                URL url = new URL("http://10.0.2.2:8000/user/register");

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
                if (respJSON.get("status").equals("success")) {
                    MainActivity.appLoggedUser.setUser(respJSON.get("username").toString(), respJSON.get("token").toString());
                    startActivity(new Intent(AccountRegisterActivity.this, AccountViewActivity.class));
                } else {
                    Toast.makeText(AccountRegisterActivity.this, respJSON.get("message").toString(), Toast.LENGTH_SHORT).show();
                    self.submitButton.setEnabled(true);
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                self.submitButton.setEnabled(true);
                Toast.makeText(AccountRegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}