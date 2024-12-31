package com.example.ufsqkbank.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ufsqkbank.R;

import java.util.ArrayList;

public class ClientAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<String[]> Clients;

    public ClientAdapter(Context context, ArrayList<String[]> clients) {
        this.context = context;
        Clients = clients;
    }

    @Override
    public int getCount() {
        return Clients.size();
    }

    @Override
    public Object getItem(int i) {
        return Clients.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view ==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.lst_clients,viewGroup,false);
        }
        String[] client = Clients.get(i);
        TextView clsusername = view.findViewById(R.id.ClientUsername);
        TextView clsusertype = view.findViewById(R.id.ClientUserType);
        TextView clientId = view.findViewById(R.id.ClientUserNum);

        clsusername.setText(client[0]);
         clsusertype.setText(client[1]);
        clientId.setText(client[2]);

        return view;

    }

}
