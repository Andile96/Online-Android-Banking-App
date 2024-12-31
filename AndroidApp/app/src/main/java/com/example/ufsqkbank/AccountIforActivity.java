package com.example.ufsqkbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ufsqkbank.Database.ConnectHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccountIforActivity extends AppCompatActivity {

    TextView txtaccHolder, txtaccNumber, txtaccBalance, txtaccType, txtaccOpenDate;


    ConnectHelper connectHelper;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_ifor);

        txtaccHolder = findViewById(R.id.txtAccountholder);
        txtaccNumber = findViewById(R.id.txtAccountNumber);
        txtaccType = findViewById(R.id.txtAccType);
        txtaccBalance = findViewById(R.id.txtBankAccountBal);
        txtaccOpenDate = findViewById(R.id.AccOpenDate);


        Intent intent = getIntent();
        username = intent.getStringExtra("username");


        connectHelper = new ConnectHelper(this);
        new FetchAccountInfoTask().execute(username);
    }


    private class FetchAccountInfoTask extends AsyncTask<String, Void, ResultSet> {

        @Override
        protected ResultSet doInBackground(String... params) {
            ResultSet resultSet = null;
            String username = params[0];

            try {
                Connection connection = connectHelper.getConnection();
                if (connection != null) {
                    String query = "SELECT AccountHolder, AccountNumber, AccountBalance, AccountType, AccountOpenDate FROM BankAccounts WHERE AccountHolder = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, username);
                    resultSet = preparedStatement.executeQuery();
                }
            } catch (SQLException e) {
                Log.e("DB_ERROR", "Error fetching account info", e);
            }
            return resultSet;
        }

        @Override
        protected void onPostExecute(ResultSet resultSet) {
            try {
                if (resultSet != null && resultSet.next()) {

                    txtaccHolder.setText(resultSet.getString("AccountHolder"));
                    txtaccNumber.setText(resultSet.getString("AccountNumber"));
                    txtaccBalance.setText("R"+resultSet.getString("AccountBalance"));
                    txtaccType.setText(resultSet.getString("AccountType"));
                    Date myDate =resultSet.getTimestamp("AccountOpenDate");
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");


                    txtaccOpenDate.setText(dateFormatter.format(myDate));
                }
            } catch (SQLException e) {
                Log.e("DB_ERROR", "Error setting account info", e);
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                } catch (SQLException e) {
                    Log.e("DB_ERROR", "Error closing ResultSet", e);
                }
            }
        }
    }
}
