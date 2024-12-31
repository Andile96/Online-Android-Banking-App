package com.example.ufsqkbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ufsqkbank.Adapters.BankAcountAdapter;
import com.example.ufsqkbank.Database.ConnectHelper;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FinancialAdvisorActivity extends AppCompatActivity {

    ConnectHelper connectHelper;
    ListView lstBankAccountsView;
    ArrayList<String[]> lstBankAccounts;
    TextView txtusername, txtEmail;

    Intent intent;
    String role,username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_advisor);

        connectHelper = new ConnectHelper(this);
        lstBankAccounts = new ArrayList<String[]>();
        lstBankAccountsView = findViewById(R.id.lstBankAcc);
        txtusername = findViewById(R.id.FAuserName);
        txtEmail = findViewById(R.id.FAuserEmail);

        intent = getIntent();
        role = intent.getStringExtra("role");
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");

        txtusername.setText("Welcome,"+role+ " "+username);
        txtEmail.setText(email);


        new FetchBankAccounts().execute();
    }

    private class FetchBankAccounts extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection connection = connectHelper.getConnection();
            if (connection != null) {
                try {
                    String query = "SELECT AccountHolder, AccountType, AccountNumber, AccountBalance, AccountOpenDate FROM BankAccounts";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    while (resultSet.next()) {
                        String accountHolder = resultSet.getString("AccountHolder");
                        String accountType = resultSet.getString("AccountType");
                        String accountNumber = resultSet.getString("AccountNumber");
                        double accountBalance = resultSet.getDouble("AccountBalance");

                        Timestamp openDateTimestamp = resultSet.getTimestamp("AccountOpenDate");
                        String formattedOpenDate = openDateTimestamp != null ? dateFormat.format(openDateTimestamp) : "N/A";

                        lstBankAccounts.add(new String[]{
                                accountHolder,
                                accountNumber,
                                accountType,
                                "R"+String.valueOf(accountBalance),
                                formattedOpenDate
                        });
                    }

                    resultSet.close();
                    statement.close();
                    connection.close();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {

                BankAcountAdapter adapter = new BankAcountAdapter(
                        FinancialAdvisorActivity.this,
                        lstBankAccounts
                );
                lstBankAccountsView.setAdapter(adapter);
            } else {
                Toast.makeText(FinancialAdvisorActivity.this, "Failed to retrieve data from database", Toast.LENGTH_LONG).show();
            }
        }
    }
}
