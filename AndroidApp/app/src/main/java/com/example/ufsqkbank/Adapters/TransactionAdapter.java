package com.example.ufsqkbank.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ufsqkbank.R;

import java.util.ArrayList;

public class TransactionAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<String[]> transaction;

    public TransactionAdapter(Context context, ArrayList<String[]> transaction) {
        this.context = context;
        this.transaction = transaction;
    }

    @Override
    public int getCount() {
        return transaction.size();
    }

    @Override
    public Object getItem(int i) {
        return transaction.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.lst_items,viewGroup,false);
        }
        String[] trans = transaction.get(i);
        TextView txtAmount = view.findViewById(R.id.txtTransAmount);
        TextView txtTransactionType = view.findViewById(R.id.txtTransType);
        TextView txtTransactionDate = view.findViewById(R.id.txtTransDate);

        txtAmount.setText(trans[0]);
        txtTransactionType.setText(trans[1]);
        txtTransactionDate.setText(trans[2]);
        return view;
    }
}
