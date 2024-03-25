package com.example.bank_app.bank;

import static com.example.bank_app.util.UtilAdapter.encodeValue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bank_app.MainActivity;
import com.example.bank_app.R;
import com.example.bank_app.account.AccountViewActivity;
import com.example.bank_app.util.ListViewAdapter;

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
    private Button logoutButton, viewCreditCardsButton;
    private ListView transactionsListView;
    //    private SimpleAdapter bankAccountsListViewAdapter;
    private ListViewAdapter transactionsListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account_view);
        this.logoutButton = findViewById(R.id.btn_bav_logout);
        this.viewCreditCardsButton = findViewById(R.id.btn_bav_view_credit_cards);
        this.transactionsListView = findViewById(R.id.lv_bav_transactions);

        this.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BankAccountViewActivity.this, MainActivity.class));
            }
        });

        this.viewCreditCardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        String urlString = "http://10.0.2.2:8000/transaction/view_by" +
                "?requestor=" + encodeValue(MainActivity.appLoggedUser.getUsername()) +
                "&bank_account_id=" + encodeValue(MainActivity.appLoggedUser.getUsername()) +
                "&token=" + encodeValue(MainActivity.appLoggedUser.getToken());
        this.extractInfo(urlString, "show_transactions");
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
                        self.transactionsListViewAdapter = new ListViewAdapter(self, items, dArray);
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
}