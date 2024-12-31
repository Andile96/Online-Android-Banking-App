package com.example.ufsqkbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ufsqkbank.Database.ConnectHelper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class ViewBalanceActivity extends AppCompatActivity {
    ConnectHelper connectHelper;
    Intent intent;
    String userId, userName;
    TextView txtbalance;
    ImageButton imgback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_balance);
        connectHelper = new ConnectHelper(this);
        intent = getIntent();
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("username");
        txtbalance = findViewById(R.id.txtBalanceAmount);
        imgback = findViewById(R.id.btnImgBack);

        String AccountId = DisplayBalance(userName);

        if(savedInstanceState ==null)
        {
            TransactionHistoryFragment fragment = TransactionHistoryFragment.newInstance(AccountId);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.TransactionHstLayout,fragment);
            fragmentTransaction.commit();
        }
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.middleFrame);

                if (!(currentFragment instanceof HomeFragment)) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.middleFrame, new HomeFragment());
                    fragmentTransaction.addToBackStack(null); // Optional: allows back navigation
                    fragmentTransaction.commit();
                }
            }
        });



    }
    private String DisplayBalance(String username) {
        String accountId = null;
        String balance = null;
        java.util.Currency currency = Currency.getInstance("ZAR");
        Locale locale = new Locale("hi", "ZA");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(currency);

        Connection connection = connectHelper.getConnection();
        if (connection == null) {
            System.err.println("Failed to establish a database connection.");
            return null;
        }

        String query = "SELECT AccountID, AccountBalance FROM BankAccounts WHERE AccountHolder = '" + username + "'";
        try {
            ResultSet resultSet = connectHelper.getData(query);
            while (resultSet.next()) {
                accountId = resultSet.getString("AccountID");
                balance = resultSet.getString("AccountBalance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (balance != null) {
            BigDecimal amt = new BigDecimal(balance);
            txtbalance.setText(numberFormat.format(amt).substring(2)); // Ensure txtbalance is defined
        } else {
            System.err.println("No balance data found for the user.");
        }

        return accountId;
    }


}