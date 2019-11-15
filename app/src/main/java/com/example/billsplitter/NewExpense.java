package com.example.billsplitter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class NewExpense extends AppCompatActivity {
    private Button paid;
    private Button split;
    private TextView res;
    private String[] listItems;
    private boolean[] checkedItems;
    private ArrayList<String> userItems = new ArrayList<>();
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_expense);
        database = FirebaseDatabase.getInstance().getReference("");
        paid = findViewById(R.id.paidByButton);
        res = findViewById(R.id.temp_res);
        listItems = getResources().getStringArray(R.array.person);
        checkedItems = new boolean[listItems.length];
        paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewExpense.this);
                builder.setTitle("Select the other persons");
                builder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos, boolean check) {
                        if(check){
                            if(! userItems.contains(listItems[pos])){
                                userItems.add(listItems[pos]);
                            }
                        }else if(userItems.contains(listItems[pos])){
                            userItems.remove(listItems[pos]);
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String people = "";
                        for (int j = 0; j < userItems.size(); j++){
                            people = people + userItems.get(j);
                        }
                        res.setText(people);
                    }
                });
                AlertDialog mDialog = builder.create();
                mDialog.show();
            }
        });
    }
}
