package com.example.ufsqkbank;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ufsqkbank.Database.ConnectHelper;
import com.google.android.material.textfield.TextInputEditText;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MakeTransacActivity extends AppCompatActivity {
    private ConnectHelper connectHelper;
    private String username;
    private TextInputEditText txtAccountNum, txtAmount, txtReference;
    private AutoCompleteTextView tracTransacTypeSpn;
    private ImageButton imgbtnSuccessful, imgbtnNotSend;
    private Button btnSend;
    private String transactionType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_transac);

        // Initialize database helper and UI elements
        connectHelper = new ConnectHelper(this);
        username = getIntent().getStringExtra("username");

        txtAccountNum = findViewById(R.id.tractxtAccountNum);
        txtAmount = findViewById(R.id.tractxtAmount);
        txtReference = findViewById(R.id.tractractxtReference);
        tracTransacTypeSpn = findViewById(R.id.tracTransacTypeSpn);
        imgbtnSuccessful = findViewById(R.id.traimgbtnSuccessful);
        imgbtnNotSend = findViewById(R.id.traimgbtnnotSend);
        btnSend = findViewById(R.id.tracbtnSent);

        imgbtnSuccessful.setVisibility(View.INVISIBLE);
        imgbtnNotSend.setVisibility(View.INVISIBLE);

        // Set up dropdown optionsArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        //                this, R.array.gender_options, android.R.layout.simple_dropdown_item_1line);
        //        genderDropdown.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.transaction,
                android.R.layout.simple_dropdown_item_1line);
        tracTransacTypeSpn.setAdapter(adapter);
        tracTransacTypeSpn.setOnClickListener(view -> tracTransacTypeSpn.showDropDown());

        tracTransacTypeSpn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                transactionType = adapterView.getItemAtPosition(i).toString();

            }
        });

        // Set button click listeners
        btnSend.setOnClickListener(v -> handleSendClick());
        imgbtnSuccessful.setOnClickListener(v -> imgbtnSuccessful.setVisibility(View.INVISIBLE));
        imgbtnNotSend.setOnClickListener(v -> imgbtnNotSend.setVisibility(View.INVISIBLE));
    }

    private void handleSendClick() {


        String accountNumber = txtAccountNum.getText().toString();
        String amountStr = txtAmount.getText().toString();
        String reference = txtReference.getText().toString();

        if (validateInput(accountNumber, amountStr, reference,transactionType)) {
            new AlertDialog.Builder(MakeTransacActivity.this)
                    .setTitle("Confirm Transaction")
                    .setMessage("Are you sure you want to " + transactionType + " " + amountStr + " to/from account " + accountNumber + "?")
                    .setPositiveButton("Yes", (dialog, which) -> processTransaction(transactionType, accountNumber, amountStr, reference))
                    .setNegativeButton("No", null)
                    .show();
        }


    }

    private boolean validateInput(String accountNumber, String amountStr, String reference, String transactionType) {
        if (transactionType.isEmpty()) {
            Toast.makeText(this, "Please select a transaction type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (accountNumber.length() != 10) {
            Toast.makeText(this, "Account number must be 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (reference.isEmpty()) {
            Toast.makeText(this, "Reference is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be positive", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void processTransaction(String transactionType, String accountNumber, String amountStr, String reference) {
        Connection connection = null;
        double originalBalance = 0;
        try {
            connection = connectHelper.getConnection();
            double amount = Double.parseDouble(amountStr);


            if (!accountExists(connection, accountNumber)) {
                Toast.makeText(this, "Account does not exist", Toast.LENGTH_SHORT).show();
                imgbtnNotSend.setVisibility(View.VISIBLE);
                return;
            }

            originalBalance = getBalance(connection, accountNumber);

            if (transactionType.equals("Withdraw") && originalBalance < amount) {
                Toast.makeText(this, "Insufficient funds for withdrawal", Toast.LENGTH_SHORT).show();
                imgbtnNotSend.setVisibility(View.VISIBLE);
                return;
            }


            double balanceChange = transactionType.equals("Deposit") ? amount : -amount;
            updateBalance(connection, accountNumber, balanceChange);


            int accountID = getAccountID(connection, accountNumber);
            insertTransaction(connection, accountID, transactionType, balanceChange, reference, accountID);


            imgbtnSuccessful.setVisibility(View.VISIBLE);
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Transaction failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            imgbtnNotSend.setVisibility(View.VISIBLE);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    private boolean accountExists(Connection connection, String accountNumber) throws SQLException {
        String query = "SELECT 1 FROM BankAccounts WHERE AccountNumber = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accountNumber);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    private double getBalance(Connection connection, String accountNumber) throws SQLException {
        String query = "SELECT AccountBalance FROM BankAccounts WHERE AccountNumber = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accountNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("AccountBalance");
            } else {
                throw new SQLException("Account not found for account number: " + accountNumber);
            }
        }
    }

    private void updateBalance(Connection connection, String accountNumber, double amount) throws SQLException {
        String query = "UPDATE BankAccounts SET AccountBalance = AccountBalance + ? WHERE AccountNumber = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setString(2, accountNumber);
            statement.executeUpdate();
        }
    }

    private int getAccountID(Connection connection, String accountNumber) throws SQLException {
        String query = "SELECT AccountID FROM BankAccounts WHERE AccountNumber = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accountNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("AccountID");
            } else {
                throw new SQLException("Account not found for account number: " + accountNumber);
            }
        }
    }

    private void insertTransaction(Connection connection, int accountID, String transactionType, double amount, String reference, int bankAccountAccountID) throws SQLException {
        String query = "INSERT INTO Transactions (AccountID, TransactionType, Amount, Reference, TransactionDate, BankAccountAccountID) " +
                "VALUES (?, ?, ?, ?, GETDATE(), ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, accountID);
            statement.setString(2, transactionType);
            statement.setDouble(3, amount);
            statement.setString(4, reference);
            statement.setInt(5, bankAccountAccountID);
            statement.executeUpdate();
        }
    }

    private void clearFields() {
        txtAccountNum.setText("");
        txtAmount.setText("");
        txtReference.setText("");
        tracTransacTypeSpn.setText("");
    }
}
