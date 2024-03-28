package com.example.bank_app.account;

import static com.example.bank_app.util.UtilAdapter.encodeValue;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RouteListingPreference;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.bank_app.MainActivity;
import com.example.bank_app.R;
import com.example.bank_app.bank.AboutActivity;
import com.example.bank_app.bank.BankAccountViewActivity;
import com.example.bank_app.model.BankAccount;
import com.example.bank_app.transaction.MyPaymentActivity;
import com.example.bank_app.transaction.PaymentActivity;
import com.example.bank_app.util.ListViewAdapter;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

public class AccountViewActivity extends AppCompatActivity {

    private AccountViewActivity self = this;
    private Button logoutButton, createBankAccountButton, makePaymentButton;
    private MenuItem homeButton;
    private ImageView menuButton;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView bankAccountsListView;

//    private SimpleAdapter bankAccountsListViewAdapter;
    private ListViewAdapter bankAccountListViewAdapter;
    public static ArrayList<String> bankAccountItems;
    public static ArrayList<BankAccount> activeBankAccounts;
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
        setContentView(R.layout.activity_account_view);
        this.logoutButton = findViewById(R.id.btn_logout);
        this.createBankAccountButton = findViewById(R.id.btn_create_bank_account);
        this.bankAccountsListView = findViewById(R.id.lv_bank_accounts);
        this.makePaymentButton = findViewById(R.id.btn_aav_pay);

        this.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountViewActivity.this, MainActivity.class));
            }
        });

        this.createBankAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String requestor = MainActivity.appLoggedUser.getUsername();
                String bank_id = "" + MainActivity.appUser.getBank_id();
                String user_id = "" + MainActivity.appUser.getId();

                // Execute AsyncTask to perform transfer operation
                new CreateBankAccountTask().execute(requestor, bank_id, user_id);
            }
        });

        this.makePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });


        this.drawerLayout = findViewById(R.id.layout_aav);
        this.actionBarDrawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, R.string.app_open_nav, R.string.app_close_nav);
        this.drawerLayout.addDrawerListener(this.actionBarDrawerToggle);
        this.actionBarDrawerToggle.syncState();

        // Open menu button
        this.menuButton = findViewById(R.id.btn_aav_menu);
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
                        startActivity(new Intent(AccountViewActivity.this, AccountViewActivity.class));
                    }
                });
                self.navBankAccountsMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(AccountViewActivity.this, AccountViewActivity.class));
                    }
                });
                self.navMakeMyPaymentMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(AccountViewActivity.this, MyPaymentActivity.class));
                    }
                });
                self.navMakeExternalPaymentMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(AccountViewActivity.this, PaymentActivity.class));
                    }
                });
                self.navMyProfileMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(AccountViewActivity.this, MyProfileActivity.class));
                    }
                });
                self.navAboutMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(AccountViewActivity.this, AboutActivity.class));
                    }
                });
                self.navLogOutMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(AccountViewActivity.this, MainActivity.class));
                    }
                });
            }
        });

        // Construct the URL with parameters
        String urlString = "http://10.0.2.2:8000/user/show_info" +
                "?requestor=" + encodeValue(MainActivity.appLoggedUser.getUsername()) +
                "&username=" + encodeValue(MainActivity.appLoggedUser.getUsername()) +
                "&token=" + encodeValue(MainActivity.appLoggedUser.getToken());
        this.extractInfo(urlString, "show_info");
    }

    public void extractInfo(String urlString, String action) {
        try {
            // Define your Flask server endpoint
            if (action == "show_info") {
                new HttpRequestShowInfoTask().execute(urlString);
            } else if (action == "view_by") {
                new HttpRequestViewByTask().execute(urlString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }


    private class HttpRequestShowInfoTask extends AsyncTask<String, Void, String> {

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
                    JSONObject resInfoJSON = new JSONObject(resJSON.get("data").toString());
                    Integer iId = (Integer) resInfoJSON.get("id");
                    if (iId != null) {
                        MainActivity.appUser.setUser(iId,
                                resInfoJSON.get("username").toString(),
                                null,
                                resInfoJSON.get("salt").toString(),
                                (Integer) resInfoJSON.get("pin"),
                                (Integer) resInfoJSON.get("bank_id"),
                                (Integer) resInfoJSON.get("role_id"),
                                resInfoJSON.get("name").toString(),
                                resInfoJSON.get("address").toString(),
                                resInfoJSON.get("phone_nbr").toString(),
                                resInfoJSON.get("job").toString()
                        );

                        String urlString = "http://10.0.2.2:8000/bank_account/view_by" +
                                "?requestor=" + encodeValue(MainActivity.appLoggedUser.getUsername()) +
                                "&user_id=" + iId +
                                "&token=" + encodeValue(resInfoJSON.get("salt").toString());
                        self.extractInfo(urlString, "view_by");
                    }
                } catch (JSONException e) {
                    System.out.println(e);
                }
            } else {
                System.out.println("Error in the GET request");
            }
        }
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
                        self.bankAccountItems = new ArrayList<>();
                        self.activeBankAccounts = new ArrayList<>();
                        for (int i = 0; i < dArray.length(); i++) {
                            JSONObject dObj = (JSONObject) dArray.get(i);
                            self.activeBankAccounts.add(new BankAccount(Integer.parseInt(dObj.get("id").toString()),
                                    Integer.parseInt(dObj.get("bank_id").toString()),
                                    Integer.parseInt(dObj.get("user_id").toString()),
                                    dObj.get("account_nbr").toString(),
                                    Integer.parseInt(dObj.get("status").toString()),
                                    Double.parseDouble(dObj.get("cash").toString())));
                            self.bankAccountItems.add(dObj.get("account_nbr").toString());
                        }
                        self.bankAccountListViewAdapter = new ListViewAdapter(self, self.bankAccountItems, dArray);
                        self.bankAccountsListView.setAdapter(self.bankAccountListViewAdapter);
                        self.bankAccountsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                String selectedItem = (String) adapterView.getItemAtPosition(position);
//                                Toast.makeText(AccountViewActivity.this, "" + selectedItem, Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < self.activeBankAccounts.size(); i++) {
                                    BankAccount currentBankAccount = self.activeBankAccounts.get(i);
                                    if (currentBankAccount.getAccountNbr().equals(selectedItem)) {
                                        MainActivity.appBankAccount = new BankAccount(currentBankAccount.getId(),
                                                currentBankAccount.getBankId(),
                                                currentBankAccount.getUserId(),
                                                currentBankAccount.getAccountNbr(),
                                                currentBankAccount.getStatus(),
                                                currentBankAccount.getCash());
                                        break;
                                    }
                                }
                                startActivity(new Intent(AccountViewActivity.this, BankAccountViewActivity.class));
                            }
                        });
                    }
                } catch (JSONException e) {
                    System.out.println(e);
                }
            } else {
                System.out.println("Error in the GET request");
            }
        }
    }

    private class CreateBankAccountTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String requestor = params[0];
            int bankId = Integer.parseInt(params[1]);
            int userId = Integer.parseInt(params[2]);

            try {
                // Create JSON object with username and amount
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("requestor", requestor);
                jsonInput.put("bank_id", bankId);
                jsonInput.put("user_id", userId);

                // Define your Flask server endpoint
                URL url = new URL("http://10.0.2.2:8000/bank_account/create");

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
                        JSONArray dArray = (JSONArray) resJSON.get("data");
                        ArrayList<String> items = new ArrayList<>();
                        for (int i = 0; i < dArray.length(); i++) {
                            JSONObject dObj = (JSONObject) dArray.get(i);
                            items.add(dObj.get("account_nbr").toString());
                        }
                        self.bankAccountListViewAdapter = new ListViewAdapter(self, items, dArray);
                        self.bankAccountsListView.setAdapter(self.bankAccountListViewAdapter);
                    }
                } catch (JSONException e) {
                    System.out.println(e);
                }
            } else {
                System.out.println("Error in the GET request");
            }
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
                startActivity(new Intent(AccountViewActivity.this, MyPaymentActivity.class));
            }
        });

        paymentReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(AccountViewActivity.this, PaymentActivity.class));
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}