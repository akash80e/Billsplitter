package com.example.billsplitter.ui.activity;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.billsplitter.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<String> mPaidBy;
    private ArrayList<String> mItem;
    private ArrayList<String> mAmount;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView expenseDesc;
        public TextView expensePayment;
        public MyViewHolder(View v) {
            super(v);
            expenseDesc = v.findViewById(R.id.item);
            expensePayment = v.findViewById(R.id.paid_by);
        }
    }

    public MyAdapter(ArrayList<String> PaidBy, ArrayList<String> Item, ArrayList<String> Amount) {
       mPaidBy = PaidBy;
       mItem = Item;
       mAmount = Amount;
    }

    //creating my views
    @Override
    public  MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create a new view
        View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    //replace the contents of a view
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        String paid = mPaidBy.get(position);
        String Item = mItem.get(position);
        String Amount = mAmount.get(position);
        Double dAmount = Double.parseDouble(Amount);

        holder.expenseDesc.setText(paid + "added a new expense for " + Item);

        if (dAmount < 0)
        {
            holder.expensePayment.setText("You owe " + -dAmount + " to" + paid);
        }
        else {
            holder.expensePayment.setText(paid + " owes you " + dAmount);
        }

    }

    //returns the size of the dataset
    @Override
    public int getItemCount() {
        return mPaidBy.size();
    }
}