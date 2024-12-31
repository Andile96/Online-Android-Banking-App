package com.example.ufsqkbank.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ufsqkbank.R;

import java.util.ArrayList;

public class StaffAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<String[]> employees;

    public StaffAdapter(Context context, ArrayList<String[]> employees) {
        this.context = context;
        this.employees = employees;
    }

    @Override
    public int getCount() {
        return employees.size();
    }

    @Override
    public Object getItem(int i) {
        return employees.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        if(view ==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.lst_staff,viewGroup,false);
        }

        String[] staff = employees.get(i);
        TextView txtstaffusername = view.findViewById(R.id.staffUsername);
        TextView txtstaffrole = view.findViewById(R.id.staffRole);
        TextView txtstaffId = view.findViewById(R.id.staffID);

        txtstaffusername.setText(staff[0]);
        txtstaffrole.setText(staff[1]);
        txtstaffId.setText(staff[2]);


        return view;

    }
}
