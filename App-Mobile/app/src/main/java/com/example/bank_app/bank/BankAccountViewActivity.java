package com.example.bank_app.bank;

import static com.example.bank_app.util.UtilAdapter.encodeValue;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
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

import com.example.bank_app.MainActivity;
import com.example.bank_app.R;
import com.example.bank_app.account.AccountViewActivity;
import com.example.bank_app.transaction.MyPaymentActivity;
import com.example.bank_app.transaction.PaymentActivity;
import com.example.bank_app.util.ListViewAdapter;
import com.example.bank_app.util.TransactionsListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BankAccountViewActivity extends AppCompatActivity {

    private BankAccountViewActivity self = this;
    private Button logoutButton, viewCreditCardsButton, makePaymentButton;
    private ImageView menuButton;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView transactionsListView;
    //    private SimpleAdapter bankAccountsListViewAdapter;
    private TransactionsListViewAdapter transactionsListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account_view);
        this.logoutButton = findViewById(R.id.btn_bav_logout);
        this.viewCreditCardsButton = findViewById(R.id.btn_bav_view_credit_cards);
        this.transactionsListView = findViewById(R.id.lv_bav_transactions);
        this.makePaymentButton = findViewById(R.id.btn_aba_pay);

        this.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BankAccountViewActivity.this, MainActivity.class));
            }
        });

        this.viewCreditCardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BankAccountViewActivity.this, CreditCardsActivity.class));
            }
        });

        drawerLayout = findViewById(R.id.layout_bav);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_open_nav, R.string.app_close_nav);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Open menu button
        this.menuButton = findViewById(R.id.btn_bav_menu);
        this.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                self.drawerLayout.open();
            }
        });

        this.makePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        String urlString = "http://10.0.2.2:8000/transaction/view_by" +
                "?requestor=" + encodeValue(MainActivity.appLoggedUser.getUsername()) +
                "&bank_account_id=" + encodeValue("" + MainActivity.appBankAccount.getId()) +
                "&token=" + encodeValue(MainActivity.appLoggedUser.getToken());
        this.extractInfo(urlString, "show_transactions");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Switching on the item id of the menu item
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home || itemId == R.id.nav_bank_accounts) {
            startActivity(new Intent(BankAccountViewActivity.this, AccountViewActivity.class));
            return true;
        } else if (itemId == R.id.nav_make_my_payment) {
            return true;
        } else if (itemId == R.id.nav_make_ext_payment) {
            return true;
        } else if (itemId == R.id.nav_profile) {
            return true;
        } else if (itemId == R.id.nav_about) {
            return true;
        } else if (itemId == R.id.nav_logout) {
            startActivity(new Intent(BankAccountViewActivity.this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void extractInfo(String urlString, String action) {
        try {
            // Define your Flask server endpoint
            if (action == "show_transactions") {
                new BankAccountViewActivity.HttpRequestShowTransactionsInfoTask().execute(urlString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private class HttpRequestShowTransactionsInfoTask extends AsyncTask<String, Void, String> {

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
                        ArrayList<String> items = new ArrayList<>();
                        for (int i = 0; i < dArray.length(); i++) {
                            JSONObject dObj = (JSONObject) dArray.get(i);
                            items.add(dObj.get("id").toString());
                        }
                        self.transactionsListViewAdapter = new TransactionsListViewAdapter(self, items, dArray);
                        self.transactionsListView.setAdapter(self.transactionsListViewAdapter);
                        self.transactionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                String selectedItem = (String) adapterView.getItemAtPosition(position);
                                Toast.makeText(BankAccountViewActivity.this, "" + selectedItem, Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(BankAccountViewActivity.this, TransactionViewActivity.class));
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
                startActivity(new Intent(BankAccountViewActivity.this, MyPaymentActivity.class));
            }
        });

        paymentReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(BankAccountViewActivity.this, PaymentActivity.class));
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}