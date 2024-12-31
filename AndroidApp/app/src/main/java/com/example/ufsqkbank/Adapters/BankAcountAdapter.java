package com.example.ufsqkbank.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ufsqkbank.R;

import java.util.ArrayList;

public class BankAcountAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<String[]> Accounts;

    public BankAcountAdapter(Context context, ArrayList<String[]> accounts) {
        this.context = context;
        Accounts = accounts;
    }

    @Override
    public int getCount() {
        return Accounts.size();
    }

    @Override
    public Object getItem(int i) {
        return Accounts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view ==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.lst_bankaccounts,viewGroup,false);
        }
        String[] accounts = Accounts.get(i);
        TextView txtAccHolder = view.findViewById(R.id.txtAcchold);
        TextView txtAccountNum = view.findViewById(R.id.txtAccNum);
        TextView txtAccType = view.findViewById(R.id.txtAccTyp);
        TextView txtBabkbal = view.findViewById(R.id.txtAccBKbal);
        TextView txtAccOpnDate = view.findViewById(R.id.txtAccOpDate);

        txtAccHolder.setText(accounts[0]);
        txtAccountNum.setText(accounts[1]);
        txtAccType.setText(accounts[2]);
        txtBabkbal.setText(accounts[3]);
        txtAccOpnDate.setText(accounts[4]);
        return view;
    }
}
