package com.example.bank_app.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bank_app.MainActivity;
import com.example.bank_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionsListViewAdapter extends ArrayAdapter<String> {
    private TransactionsListViewAdapter self = this;
    private ArrayList<String> list;
    private JSONArray jsonList;
    private Context context;

    // The ListViewAdapter Constructor
    // @param context: the Context from the MainActivity
    // @param items: The list of items in our Grocery List
    public TransactionsListViewAdapter(Context context, ArrayList<String> items, JSONArray jsonItems) {
        super(context, R.layout.transactions_list_row, items);
        this.context = context;
        this.list = items;
        this.jsonList = jsonItems;
    }

    // The method we override to provide our own layout for each View (row) in the ListView
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.transactions_list_row, null);
                ImageView transactionIn = convertView.findViewById(R.id.tlr_in);
                ImageView transactionOut = convertView.findViewById(R.id.tlr_out);
                TextView to_ba_date = convertView.findViewById(R.id.to_ba_date);
                TextView to_ba_name = convertView.findViewById(R.id.to_ba_name);
                TextView to_ba_iban = convertView.findViewById(R.id.to_ba_iban);
                TextView to_ba_note = convertView.findViewById(R.id.to_ba_note);
                TextView to_ba_cash = convertView.findViewById(R.id.to_ba_cash);

                try {
                    JSONObject dObj = (JSONObject) this.jsonList.get(position);
                    if (dObj.get("to_bank_account_iban").equals(MainActivity.appBankAccount.getAccountNbr())) {
                        transactionIn.setVisibility(View.VISIBLE);
                        transactionOut.setVisibility(View.INVISIBLE);
                    } else {
                        transactionIn.setVisibility(View.INVISIBLE);
                        transactionOut.setVisibility(View.VISIBLE);
                    }
                    to_ba_date.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(dObj.get("date").toString())));
                    to_ba_name.setText(dObj.get("to_bank_account_name").toString());
                    to_ba_iban.setText(dObj.get("to_bank_account_iban").toString());
                    to_ba_note.setText(dObj.get("to_bank_account_note").toString());
                    to_ba_cash.setText(dObj.get("to_bank_account_cash").toString());
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception err) {
            System.out.println(err);
        }
        return convertView;
    }
}
