package com.example.bank_app.bank;

import static com.example.bank_app.util.UtilAdapter.encodeValue;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bank_app.MainActivity;
import com.example.bank_app.R;
import com.example.bank_app.account.AccountRegisterActivity;
import com.example.bank_app.account.AccountViewActivity;
import com.example.bank_app.account.MyProfileActivity;
import com.example.bank_app.model.BankAccount;
import com.example.bank_app.transaction.MyPaymentActivity;
import com.example.bank_app.transaction.PaymentActivity;
import com.example.bank_app.util.CreditCardsListViewAdapter;
import com.example.bank_app.util.ListViewAdapter;
import com.google.android.material.internal.NavigationMenuItemView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class CreditCardsActivity extends AppCompatActivity {

    private CreditCardsActivity self = this;
    private Button logoutButton, createCreditCardButton, makePaymentButton;
    private ImageView menuButton;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ArrayList<String> creditCardsList;
    private CreditCardsListViewAdapter creditCardsListViewAdapter;
    private ListView creditCardsListView;
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
        setContentView(R.layout.activity_credit_cards);
        this.logoutButton = findViewById(R.id.btn_cca_logout);
        this.createCreditCardButton = findViewById(R.id.btn_cca_create_credit_card);
        this.makePaymentButton = findViewById(R.id.btn_cca_pay);
        this.menuButton = findViewById(R.id.btn_cca_menu);
        this.creditCardsListView = findViewById(R.id.lv_cca_credit_cards);

        this.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreditCardsActivity.this, MainActivity.class));
            }
        });

        this.createCreditCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                self.extractInfo(null, "create_new_card");
            }
        });

        this.makePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        this.drawerLayout = findViewById(R.id.layout_cca);
        this.actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_open_nav, R.string.app_close_nav);
        this.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        this.actionBarDrawerToggle.syncState();

        // Open menu button
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
                        startActivity(new Intent(CreditCardsActivity.this, AccountViewActivity.class));
                    }
                });
                self.navBankAccountsMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(CreditCardsActivity.this, AccountViewActivity.class));
                    }
                });
                self.navMakeMyPaymentMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(CreditCardsActivity.this, MyPaymentActivity.class));
                    }
                });
                self.navMakeExternalPaymentMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(CreditCardsActivity.this, PaymentActivity.class));
                    }
                });
                self.navMyProfileMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(CreditCardsActivity.this, MyProfileActivity.class));
                    }
                });
                self.navAboutMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        TODO:
//                        startActivity(new Intent(CreditCardsActivity.this, MyProfileActivity.class));
                    }
                });
                self.navLogOutMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(CreditCardsActivity.this, MainActivity.class));
                    }
                });
            }
        });

        // Construct the URL with parameters
        String urlString = "http://10.0.2.2:8000/credit_card/view_by" +
                "?requestor=" + encodeValue(MainActivity.appLoggedUser.getUsername()) +
                "&bank_account_id=" + encodeValue("" + MainActivity.appBankAccount.getId()) +
                "&token=" + encodeValue(MainActivity.appLoggedUser.getToken());
        this.extractInfo(urlString, "view_by");
    }

    private void extractInfo(String urlString, String action) {
        try {
            // Define your Flask server endpoint
            if (action == "view_by") {
                new HttpRequestViewByTask().execute(urlString);
            } else if (action == "create_new_card") {
                new HttpRequestCreateCreditCardTask().execute(urlString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
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
                startActivity(new Intent(CreditCardsActivity.this, MyPaymentActivity.class));
            }
        });

        paymentReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(CreditCardsActivity.this, PaymentActivity.class));
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    private class HttpRequestViewByTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }

                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject resJSON = new JSONObject(result);
                    if (resJSON.get("status").equals("success")) {
                        JSONArray dArray = (JSONArray) resJSON.get("data");
                        self.creditCardsList = new ArrayList<>();
                        for (int i = 0; i < dArray.length(); i++) {
                            JSONObject dObj = (JSONObject) dArray.get(i);
                            self.creditCardsList.add(dObj.get("credit_card_nbr").toString());
                        }
                        self.creditCardsListViewAdapter = new CreditCardsListViewAdapter(self, self.creditCardsList, dArray);
                        self.creditCardsListView.setAdapter(self.creditCardsListViewAdapter);
                    }
                } catch (JSONException e) {
                    System.out.println(e);
                }
            } else {
                System.out.println("Error in the GET request");
            }
        }
    }


    private class HttpRequestCreateCreditCardTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                // Create JSON object with username and amount
                JSONObject jsonInput = new JSONObject();

                jsonInput.put("requestor", MainActivity.appLoggedUser.getUsername());
                jsonInput.put("token", MainActivity.appLoggedUser.getToken());
                jsonInput.put("bank_account_id", MainActivity.appBankAccount.getId());
                jsonInput.put("limit", 1000);

                // Define your Flask server endpoint
                URL url = new URL("http://10.0.2.2:8000/credit_card/create");

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
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject resJSON = new JSONObject(result);
                    if (resJSON.get("status").equals("success")) {
                        String urlString = "http://10.0.2.2:8000/credit_card/view_by" +
                                "?requestor=" + encodeValue(MainActivity.appLoggedUser.getUsername()) +
                                "&bank_account_id=" + encodeValue("" + MainActivity.appBankAccount.getId()) +
                                "&token=" + encodeValue(MainActivity.appLoggedUser.getToken());
                        self.extractInfo(urlString, "view_by");
                        Toast.makeText(CreditCardsActivity.this, resJSON.get("message").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    System.out.println(e);
                }
            } else {
                System.out.println("Error in the GET request");
            }
        }
    }

}