package com.example.ufsqkbank;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.ufsqkbank.Adapters.TransactionAdapter;
import com.example.ufsqkbank.Database.ConnectHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class TransactionHistoryFragment extends Fragment {

    private static final String ARG_AccountId = "AccountId";
    private static final String ARG_PARAM2 = "param2";


    private ConnectHelper connectHelper;
    private ArrayList<String[]> transactionHst;
    private ListView listView;
    private String accountId;

    public TransactionHistoryFragment() {

    }

    public static TransactionHistoryFragment newInstance(String AccountId) {
        TransactionHistoryFragment fragment = new TransactionHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_AccountId, AccountId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectHelper = new ConnectHelper(getActivity());
        transactionHst = new ArrayList<>();
        if (getArguments() != null) {
            accountId = getArguments().getString(ARG_AccountId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);

        listView = view.findViewById(R.id.lstTransacHst);
        if(accountId !=null)
        {
            new FetchTransaction(accountId).execute();
        }
        else {
            Toast.makeText(getActivity(),"No transaction matches the id",Toast.LENGTH_LONG).show();
        }

        return view;
    }
    private class FetchTransaction extends AsyncTask<Void, Void, Boolean>{
        private String accountId;

        public FetchTransaction(String accountId) {
            this.accountId = accountId;
        }



        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection connection = connectHelper.getConnection();
            if (connection == null) {
                System.err.println("Database connection is null.");
                return false;
            }

            String query = "SELECT TransactionType, Amount, TransactionDate FROM Transactions WHERE BankAccountAccountID = '" + accountId + "'";
            System.out.println("Executing query: " + query);

            ResultSet resultSet = connectHelper.getData(query);

            if (resultSet == null) {
                System.err.println("Failed to execute query or received null ResultSet.");
                return false;
            }
            try {
                transactionHst.clear();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                boolean hasData = false;  // Flag to check if we found any data

                while (resultSet.next()) {
                    hasData = true;
                    String transType = resultSet.getString("TransactionType");
                    String amount = resultSet.getString("Amount");


                    Date transDate = resultSet.getTimestamp("TransactionDate");
                    String formattedDate = dateFormatter.format(transDate);

                    String[] transaction = {"R"+amount ,transType, formattedDate};
                    transactionHst.add(transaction);
                }

                if (!hasData) {
                    System.err.println("No transactions found for AccountID: " + accountId);
                }

                return hasData;

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
            {
                TransactionAdapter adapter = new TransactionAdapter(
                        getContext(),
                        transactionHst
                );
                listView.setAdapter(adapter);
            }
            else {
                Toast.makeText(getActivity(), "Failed to retrieve data from database", Toast.LENGTH_LONG).show();
            }
        }
    }
}