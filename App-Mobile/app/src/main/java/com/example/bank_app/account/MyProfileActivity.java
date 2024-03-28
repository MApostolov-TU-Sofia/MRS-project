package com.example.bank_app.account;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bank_app.MainActivity;
import com.example.bank_app.R;
import com.example.bank_app.bank.AboutActivity;
import com.example.bank_app.transaction.MyPaymentActivity;
import com.example.bank_app.transaction.PaymentActivity;
import com.google.android.material.internal.NavigationMenuItemView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MyProfileActivity extends AppCompatActivity {

    private MyProfileActivity self = this;
    private Button logoutButton, paymentButton, submitNewPasswordButton;
    private EditText oldPasswordText, newPasswordText, confirmNewPasswordText;
    private ImageView menuButton;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationMenuItemView navHomeMenuItem,
            navBankAccountsMenuItem,
            navMakeMyPaymentMenuItem,
            navMakeExternalPaymentMenuItem,
            navMyProfileMenuItem,
            navAboutMenuItem,
            navLogOutMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        this.logoutButton = findViewById(R.id.btn_ampv_logout);
        this.menuButton = findViewById(R.id.btn_ampv_menu);
        this.paymentButton = findViewById(R.id.btn_ampv_pay);

        this.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyProfileActivity.this, MainActivity.class));
            }
        });

        this.paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        this.drawerLayout = findViewById(R.id.layout_ampv);
        this.actionBarDrawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, R.string.app_open_nav, R.string.app_close_nav);
        this.drawerLayout.addDrawerListener(this.actionBarDrawerToggle);
        this.actionBarDrawerToggle.syncState();

        // Open menu button
        this.menuButton = findViewById(R.id.btn_ampv_menu);
        this.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                self.drawerLayout.open();
                self.navHomeMenuItem = self.drawerLayout.findViewById(R.id.nav_home);
                self.navBankAccountsMenuItem = self.drawerLayout.findViewById(R.id.nav_bank_accounts);
                self.navMakeMyPaymentMenuItem = self.drawerLayout.findViewById(R.id.nav_make_my_payment);
                self.navMakeExternalPaymentMenuItem = self.drawerLayout.findViewById(R.id.nav_make_ext_payment);
                self.navMyProfileMenuItem = self.drawerLayout.findViewById(R.id.nav_profile);
                self.navAboutMenuItem = self.drawerLayout.findViewById(R.id.nav_about);
                self.navLogOutMenuItem = self.drawerLayout.findViewById(R.id.nav_logout);

                self.navHomeMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MyProfileActivity.this, AccountViewActivity.class));
                    }
                });
                self.navBankAccountsMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MyProfileActivity.this, AccountViewActivity.class));
                    }
                });
                self.navMakeMyPaymentMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MyProfileActivity.this, MyPaymentActivity.class));
                    }
                });
                self.navMakeExternalPaymentMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MyProfileActivity.this, PaymentActivity.class));
                    }
                });
                self.navMyProfileMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MyProfileActivity.this, MyProfileActivity.class));
                    }
                });
                self.navAboutMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MyProfileActivity.this, AboutActivity.class));
                    }
                });
                self.navLogOutMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MyProfileActivity.this, MainActivity.class));
                    }
                });
            }
        });

        this.oldPasswordText = findViewById(R.id.inp_prev_password);
        this.newPasswordText = findViewById(R.id.inp_new_password);
        this.confirmNewPasswordText = findViewById(R.id.inp_confirm_new_password);
        this.submitNewPasswordButton = findViewById(R.id.btn_ampv_submit_password);

        this.submitNewPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                self.oldPasswordText.setError(self.oldPasswordText.getText().toString().length() == 0 ?
                        "Add a value" : null);
                self.newPasswordText.setError(self.newPasswordText.getText().toString().length() == 0 ?
                        "Add a value" : null);
                self.confirmNewPasswordText.setError(self.confirmNewPasswordText.getText().toString().length() == 0 ?
                        "Add a value" : null);
                boolean toPaymentCompletion = self.oldPasswordText.getText().toString().length() > 0 &&
                        self.newPasswordText.getText().toString().length() > 0 &&
                        self.confirmNewPasswordText.getText().toString().length() > 0;
                if (toPaymentCompletion) {
                    if (!self.newPasswordText.getText().toString().equals(self.confirmNewPasswordText.getText().toString())) {
                        Toast.makeText(MyProfileActivity.this, "Write the same new and confirm passwords", Toast.LENGTH_SHORT).show();
                    } else {
                        if (self.oldPasswordText.getText().toString().equals(self.newPasswordText.getText().toString())) {
                            Toast.makeText(MyProfileActivity.this, "New password can not equal the old password", Toast.LENGTH_SHORT).show();
                        } else {
                            self.submitNewPasswordButton.setEnabled(false);
                            new TransferTask().execute();
                        }
                    }
                } else {
                    Toast.makeText(MyProfileActivity.this, "Fill all required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_payment_layout);

        LinearLayout myPaymentReq = dialog.findViewById(R.id.my_payment_req);
        LinearLayout paymentReq = dialog.findViewById(R.id.payment_req);

        myPaymentReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(MyProfileActivity.this, MyPaymentActivity.class));
            }
        });

        paymentReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(MyProfileActivity.this, PaymentActivity.class));
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    private class TransferTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String prevPasswordTxt = self.oldPasswordText.getText().toString();
            String newPasswordTxt = self.newPasswordText.getText().toString();

            try {
                // Create JSON object with username and amount
                JSONObject jsonInput = new JSONObject();

                jsonInput.put("requestor", MainActivity.appLoggedUser.getUsername());
                jsonInput.put("token", MainActivity.appLoggedUser.getToken());
                jsonInput.put("previous_password", prevPasswordTxt);
                jsonInput.put("new_password", newPasswordTxt);

                // Define your Flask server endpoint
                URL url = new URL("http://10.0.2.2:8000/user/change_password");

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
                Toast.makeText(MyProfileActivity.this, respJSON.get("message").toString(), Toast.LENGTH_SHORT).show();
                self.submitNewPasswordButton.setEnabled(true);
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                self.submitNewPasswordButton.setEnabled(true);
                Toast.makeText(MyProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}