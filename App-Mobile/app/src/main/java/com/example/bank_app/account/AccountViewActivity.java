package com.example.bank_app.account;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bank_app.MainActivity;
import com.example.bank_app.R;
import com.example.bank_app.util.ListViewAdapter;

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
    private Button logoutButton, createBankAccountButton;

    private ListView bankAccountsListView;

//    private SimpleAdapter bankAccountsListViewAdapter;
    private ListViewAdapter bankAccountListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);
        this.logoutButton = findViewById(R.id.btn_logout);
        this.createBankAccountButton = findViewById(R.id.btn_create_bank_account);
        this.bankAccountsListView = findViewById(R.id.lv_bank_accounts);

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

        // Construct the URL with parameters
        String urlString = "http://10.0.2.2:8000/user/show_info" +
                "?requestor=" + encodeValue(MainActivity.appLoggedUser.getUsername()) +
                "&username=" + encodeValue(MainActivity.appLoggedUser.getUsername()) +
                "&token=" + encodeValue(MainActivity.appLoggedUser.getToken());
        this.extractInfo(urlString, "show_info");
    }

    private void extractInfo(String urlString, String action) {
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

    // Encode parameter values to be URL-safe
    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return value;
        }
    }
}