package com.example.bank_app.transaction;

import static com.example.bank_app.util.UtilAdapter.encodeValue;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bank_app.MainActivity;
import com.example.bank_app.R;
import com.example.bank_app.account.AccountLoginActivity;
import com.example.bank_app.account.AccountViewActivity;
import com.example.bank_app.account.MyProfileActivity;
import com.example.bank_app.bank.AboutActivity;
import com.example.bank_app.model.BankAccount;
import com.google.android.material.internal.NavigationMenuItemView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class PaymentActivity extends AppCompatActivity {

    private PaymentActivity self = this;
    private AutoCompleteTextView bankAccountsTextView;
    private ArrayAdapter<String> bankAccountsArrayAdapter;
    private ImageView menuButton;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private EditText inpBankAccountIban,
            inpToBankAccountName,
            inpToBankAccountIban,
            inpToBankAccountNote,
            inpToBankAccountSecondaryNote,
            inpToBankAccountCash;
    private Button inpPaymentCommitButton;
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
        setContentView(R.layout.activity_payment);
        this.inpBankAccountIban = findViewById(R.id.inp_bank_account_iban);
        this.inpToBankAccountName = findViewById(R.id.inp_to_bank_account_name);
        this.inpToBankAccountIban = findViewById(R.id.inp_to_bank_account_iban);
        this.inpToBankAccountNote = findViewById(R.id.inp_to_bank_account_note);
        this.inpToBankAccountSecondaryNote = findViewById(R.id.inp_to_bank_account_secondary_note);
        this.inpToBankAccountCash = findViewById(R.id.inp_to_bank_account_cash);
        this.inpPaymentCommitButton = findViewById(R.id.inp_payment_commit);

        this.bankAccountsTextView = findViewById(R.id.inp_auto_complete_bank_account);
        this.bankAccountsArrayAdapter = new ArrayAdapter<String>(this, R.layout.select_ba_list_items, AccountViewActivity.bankAccountItems);
        this.bankAccountsTextView.setAdapter(this.bankAccountsArrayAdapter);
        this.bankAccountsTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String item = adapterView.getItemAtPosition(position).toString();
                for (int i = 0; i < AccountViewActivity.activeBankAccounts.size(); i++) {
                    BankAccount currentBankAccount = AccountViewActivity.activeBankAccounts.get(i);
                    if (currentBankAccount.getAccountNbr().equals(item)) {
                        MainActivity.appBankAccount = new BankAccount(currentBankAccount.getId(),
                                currentBankAccount.getBankId(),
                                currentBankAccount.getUserId(),
                                currentBankAccount.getAccountNbr(),
                                currentBankAccount.getStatus(),
                                currentBankAccount.getCash());
                        break;
                    }
                }
                self.inpBankAccountIban.setText(item);
            }
        });

        this.inpPaymentCommitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                self.inpBankAccountIban.setError(self.inpBankAccountIban.getText().toString().length() == 0 ?
                            "Add a value" : null);
                self.inpToBankAccountName.setError(self.inpToBankAccountName.getText().toString().length() == 0 ?
                        "Add a value" : null);
                self.inpToBankAccountIban.setError(self.inpToBankAccountIban.getText().toString().length() == 0 ?
                        "Add a value" : null);
                self.inpToBankAccountNote.setError(self.inpToBankAccountNote.getText().toString().length() == 0 ?
                        "Add a value" : null);
                self.inpToBankAccountCash.setError(self.inpToBankAccountCash.getText().toString().length() == 0 ?
                        "Add a value" : null);
                boolean toPaymentCompletion = self.inpBankAccountIban.getText().toString().length() > 0 &&
                        self.inpToBankAccountName.getText().toString().length() > 0 &&
                        self.inpToBankAccountIban.getText().toString().length() > 0 &&
                        self.inpToBankAccountNote.getText().toString().length() > 0 &&
                        self.inpToBankAccountCash.getText().toString().length() > 0;
                if (toPaymentCompletion) {
                    self.inpPaymentCommitButton.setEnabled(false);
                    new TransferTask().execute();
                } else {
                    Toast.makeText(PaymentActivity.this, "Fill all required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.drawerLayout = findViewById(R.id.layout_aap);
        this.actionBarDrawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, R.string.app_open_nav, R.string.app_close_nav);
        this.drawerLayout.addDrawerListener(this.actionBarDrawerToggle);
        this.actionBarDrawerToggle.syncState();

        // Open menu button
        this.menuButton = findViewById(R.id.btn_amp_menu);
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
                        startActivity(new Intent(PaymentActivity.this, AccountViewActivity.class));
                    }
                });
                self.navBankAccountsMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(PaymentActivity.this, AccountViewActivity.class));
                    }
                });
                self.navMakeMyPaymentMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(PaymentActivity.this, MyPaymentActivity.class));
                    }
                });
                self.navMakeExternalPaymentMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(PaymentActivity.this, PaymentActivity.class));
                    }
                });
                self.navMyProfileMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(PaymentActivity.this, MyProfileActivity.class));
                    }
                });
                self.navAboutMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(PaymentActivity.this, AboutActivity.class));
                    }
                });
                self.navLogOutMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(PaymentActivity.this, MainActivity.class));
                    }
                });
            }
        });
    }

    private class TransferTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String inpBankAccountIbanTxt = self.inpBankAccountIban.getText().toString();
            String inpToBankAccountNameTxt = self.inpToBankAccountName.getText().toString();
            String inpToBankAccountIbanTxt = self.inpToBankAccountIban.getText().toString();
            String inpToBankAccountNoteTxt = self.inpToBankAccountNote.getText().toString();
            String inpToBankAccountSecondaryNoteTxt = self.inpToBankAccountSecondaryNote.getText().toString();
            Double inpToBankAccountCashDbl = Double.parseDouble(self.inpToBankAccountCash.getText().toString());

            try {
                // Create JSON object with username and amount
                JSONObject jsonInput = new JSONObject();

                jsonInput.put("requestor", MainActivity.appLoggedUser.getUsername());
                jsonInput.put("token", MainActivity.appLoggedUser.getToken());
                jsonInput.put("bank_account_id", MainActivity.appBankAccount.getId());
                jsonInput.put("user_id", MainActivity.appBankAccount.getUserId());
                jsonInput.put("bank_account_iban", inpBankAccountIbanTxt);
                jsonInput.put("to_bank_account_name", inpToBankAccountNameTxt);
                jsonInput.put("to_bank_account_iban", inpToBankAccountIbanTxt);
                jsonInput.put("to_bank_account_note", inpToBankAccountNoteTxt);
                jsonInput.put("to_bank_account_secondary_note", inpToBankAccountSecondaryNoteTxt);
                jsonInput.put("to_bank_account_cash", inpToBankAccountCashDbl);
                jsonInput.put("process", "TRANSFER");

                // Define your Flask server endpoint
                URL url = new URL("http://10.0.2.2:8000/transaction/create");

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
                    startActivity(new Intent(PaymentActivity.this, AccountViewActivity.class));
                } else {
                    Toast.makeText(PaymentActivity.this, respJSON.get("message").toString(), Toast.LENGTH_SHORT).show();
                }
                self.inpPaymentCommitButton.setEnabled(true);
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                self.inpPaymentCommitButton.setEnabled(true);
                Toast.makeText(PaymentActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}