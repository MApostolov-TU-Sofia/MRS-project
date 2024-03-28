package com.example.bank_app.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bank_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreditCardsListViewAdapter extends ArrayAdapter<String> {

    private CreditCardsListViewAdapter self = this;
    private ArrayList<String> list;
    private JSONArray jsonList;
    private Context context;

    public CreditCardsListViewAdapter(Context context, ArrayList<String> items, JSONArray jsonItems) {
        super(context, R.layout.list_row, items);
        this.context = context;
        this.list = items;
        this.jsonList = jsonItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.credit_cards_list_row, null);
            TextView creditCardNbr = convertView.findViewById(R.id.credit_card_number);
            TextView validTo = convertView.findViewById(R.id.credit_card_valid_to);

            creditCardNbr.setText(list.get(position));

            try {
                JSONObject dObj = (JSONObject) this.jsonList.get(position);
                validTo.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(dObj.get("valid_to").toString())));
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }
        return convertView;
    }

}
