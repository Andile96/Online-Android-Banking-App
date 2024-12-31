package com.example.ufsqkbank;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ufsqkbank.Database.ConnectHelper;
import com.google.android.material.textfield.TextInputEditText;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransfareFunds extends AppCompatActivity {
    private ConnectHelper connectHelper;
    private String username;
    private TextInputEditText txtAccountNum, txtAmount, txtReference;
    private ImageButton imgbtnSuccessful, imgbtnNotSend;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfare_funds);

        connectHelper = new ConnectHelper(this);
        username = getIntent().getStringExtra("username");

        txtAccountNum = findViewById(R.id.txtAccountNum);
        txtAmount = findViewById(R.id.txtAmount);
        txtReference = findViewById(R.id.txtReference);
        imgbtnSuccessful = findViewById(R.id.imgbtnSuccessful);
        imgbtnNotSend = findViewById(R.id.imgbtnnotSend);
        btnSend = findViewById(R.id.btnSent);

        imgbtnSuccessful.setVisibility(View.INVISIBLE);
        imgbtnNotSend.setVisibility(View.INVISIBLE);

        btnSend.setOnClickListener(v -> handleSendClick());
        imgbtnSuccessful.setOnClickListener(v -> imgbtnSuccessful.setVisibility(View.INVISIBLE));
        imgbtnNotSend.setOnClickListener(v -> imgbtnNotSend.setVisibility(View.INVISIBLE));
    }

    private void handleSendClick() {
        String accountNumber = txtAccountNum.getText().toString();
        String amountStr = txtAmount.getText().toString();
        String reference = txtReference.getText().toString();

        if (validateInput(accountNumber, amountStr, reference)) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Transaction")
                    .setMessage("Are you sure you want to send " + amountStr + " to account " + accountNumber + "?")
                    .setPositiveButton("Yes", (dialog, which) -> processTransaction(accountNumber, amountStr, reference))
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private boolean validateInput(String accountNumber, String amountStr, String reference) {
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
    private void processTransaction(String receiverAccountNumber, String amountStr, String reference) {
        Connection connection = null;
        double originalSenderBalance = 0;
        try {
            connection = connectHelper.getConnection();
            double amount = Double.parseDouble(amountStr);

            originalSenderBalance = getBalance(connection, username);
            if (originalSenderBalance < amount) {
                Toast.makeText(this, "Insufficient funds", Toast.LENGTH_SHORT).show();
                imgbtnNotSend.setVisibility(View.VISIBLE);
                return;
            }

            if (!accountExists(connection, receiverAccountNumber)) {
                Toast.makeText(this, "Receiver account does not exist", Toast.LENGTH_SHORT).show();
                imgbtnNotSend.setVisibility(View.VISIBLE);
                return;
            }

            updateBalance(connection, username, -amount);
            updateBalance(connection, receiverAccountNumber, amount);

            int senderAccountID = getAccountID(connection, username);
            int receiverAccountID = getAccountID(connection, receiverAccountNumber);

            insertTransaction(connection, senderAccountID, "Transfer", -amount, reference, senderAccountID);
            insertTransaction(connection, receiverAccountID, "Payment", amount, reference, receiverAccountID);

            imgbtnSuccessful.setVisibility(View.VISIBLE);
            clearFields();
        } catch (SQLException e) {

            try {
                if (connection != null) {
                    updateBalance(connection, username, originalSenderBalance - getBalance(connection, username));
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            Toast.makeText(this, "Transaction failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            imgbtnNotSend.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            try {
                if (connection != null) {
                    updateBalance(connection, username, originalSenderBalance - getBalance(connection, username));
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            Toast.makeText(this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            imgbtnNotSend.setVisibility(View.VISIBLE);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
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

    private double getBalance(Connection connection, String username) throws SQLException {
        String query = "SELECT AccountBalance FROM BankAccounts WHERE AccountHolder = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("AccountBalance");
            } else {
                throw new SQLException("Account not found for username: " + username);
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


    private void updateBalance(Connection connection, String identifier, double amount) throws SQLException {
        String query = "UPDATE BankAccounts SET AccountBalance = AccountBalance + ? WHERE AccountHolder = ? OR AccountNumber = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setString(2, identifier);
            statement.setString(3, identifier);
            statement.executeUpdate();
        }
    }

    private int getAccountID(Connection connection, String identifier) throws SQLException {
        String query = "SELECT AccountID FROM BankAccounts WHERE AccountHolder = ? OR AccountNumber = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, identifier);
            statement.setString(2, identifier);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("AccountID");
            } else {
                throw new SQLException("Account not found for identifier: " + identifier);
            }
        }
    }

    private void clearFields() {
        txtAccountNum.setText("");
        txtAmount.setText("");
        txtReference.setText("");
    }

}
