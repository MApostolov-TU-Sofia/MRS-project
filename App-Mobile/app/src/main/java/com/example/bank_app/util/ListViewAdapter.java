package com.example.bank_app.util;

import static com.example.bank_app.util.UtilAdapter.addSpaces;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bank_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.example.bank_app.account.AccountViewActivity;

public class ListViewAdapter extends ArrayAdapter<String> {
    private ListViewAdapter self = this;
    private ArrayList<String> list;
    private JSONArray jsonList;
    private Context context;

    // The ListViewAdapter Constructor
    // @param context: the Context from the MainActivity
    // @param items: The list of items in our Grocery List
    public ListViewAdapter(Context context, ArrayList<String> items, JSONArray jsonItems) {
        super(context, R.layout.list_row, items);
        this.context = context;
        this.list = items;
        this.jsonList = jsonItems;
    }

    // The method we override to provide our own layout for each View (row) in the ListView
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_row, null);
            TextView name = convertView.findViewById(R.id.name);
            TextView cash = convertView.findViewById(R.id.cash);

            name.setText(addSpaces(list.get(position)));
            cash.setText("0.00");

            try {
                JSONObject dObj = (JSONObject) this.jsonList.get(position);
                cash.setText(dObj.get("cash").toString());
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }
        return convertView;
    }

}