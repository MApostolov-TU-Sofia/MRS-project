package com.example.bank_app.util;

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
            TextView number = convertView.findViewById(R.id.number);
            TextView name = convertView.findViewById(R.id.name);
            TextView cash = convertView.findViewById(R.id.cash);

            number.setText((position + 1) + ".");
            name.setText(list.get(position));
            cash.setText("0.00");

            try {
                JSONObject dObj = (JSONObject) this.jsonList.get(position);
                cash.setText(dObj.get("cash").toString());

                if (Integer.parseInt(dObj.get("status").toString()) == 1) {
                    ImageView remove = convertView.findViewById(R.id.remove);
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                new AlertDialog.Builder(self.context)
                                        .setTitle("Confirmation")
                                        .setMessage("Do you want to disable the bank account?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Toast.makeText(self.context, "Yes, will be disabled", Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null).show();
                            } catch (Error err) {
                                System.out.println(err.getMessage());
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }
        return convertView;
    }

}