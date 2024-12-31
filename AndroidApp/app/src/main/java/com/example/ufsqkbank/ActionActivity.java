package com.example.ufsqkbank;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ufsqkbank.Database.ConnectHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActionActivity extends AppCompatActivity {


    ConnectHelper connectHelper;
    String[] clientData;
    String id, lastName, firstName, gender, dateOfBirth,
            idOrPassportNumber, phone, employeeOrStudentNumber, userType,
            userName, email, bankAccount;
    TextView txtlastName, txtfirstName, txtgender, txtdateOfBirth,txtidOrPassportNumber,
              txtphone, txtemployeeOrStudentNumber, txtuserType,txtuserName, txtemail,
               txtlabelStudStaff,txtnationality, txtlabelIDPass;
    ImageView imgEditimgViewbank, imgDelete , imgDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        connectHelper = new ConnectHelper(this);
        clientData = getIntent().getStringArrayExtra("clientData");

        id= clientData[0];lastName= clientData[1];
        firstName= clientData[2]; gender= clientData[3]; dateOfBirth= clientData[4];
        idOrPassportNumber= clientData[5]; phone= clientData[6];
        employeeOrStudentNumber= clientData[7]; userType= clientData[8];
        userName= clientData[9];email= clientData[10];bankAccount= clientData[11];

        txtlastName = findViewById(R.id.actionttxtIlstName);
        txtfirstName =findViewById(R.id.txtIfstName);
        txtgender= findViewById(R.id.actionttxtIgender);
        txtdateOfBirth =findViewById(R.id.actionttxtIDOB);
        txtidOrPassportNumber =findViewById(R.id.actionttxtIDIPassportNum);
        txtphone=findViewById(R.id.actiontxtIPhone);
        txtemployeeOrStudentNumber=findViewById(R.id.actionttxtIStudentStaffNum);
        txtuserType= findViewById(R.id.actionttxtIuserType);
        txtemail=findViewById(R.id.actionttxtIEmail);

        txtlabelStudStaff =findViewById(R.id.actionttxtIStudentStaff);
        txtnationality =findViewById(R.id.actionttxtINationality);
        txtlabelIDPass = findViewById(R.id.actionttxtIPassportID);

        imgEditimgViewbank = findViewById(R.id.imgAction);
        imgDetails= findViewById(R.id.imgActionAccountDetails);
        imgDelete = findViewById(R.id.imgActDelete);

        txtuserType.setText(userType);
        if (userType.equals("Student"))
        {
            txtlabelStudStaff.setText("Student");
            txtlabelStudStaff.setVisibility(View.VISIBLE);
            txtemployeeOrStudentNumber.setVisibility(View.VISIBLE);

        }else if(userType.equals("Staff"))
        {
            txtlabelStudStaff.setText("Student");
            txtlabelStudStaff.setVisibility(View.VISIBLE);
            txtemployeeOrStudentNumber.setVisibility(View.VISIBLE);
        }
        txtemployeeOrStudentNumber.setText(employeeOrStudentNumber);
        txtlastName.setText(lastName);
        txtfirstName.setText(firstName);



        if (idOrPassportNumber.length() >10)
        {
            txtnationality.setText("South African");
            txtlabelIDPass.setText("RSA ID Number");

            txtnationality.setVisibility(View.VISIBLE);
            txtlabelIDPass.setVisibility(View.VISIBLE);
            txtidOrPassportNumber.setVisibility(View.VISIBLE);

        }
        else if(idOrPassportNumber.length()<10){
            txtnationality.setText("Non South African");
            txtlabelIDPass.setText("Passport Number");
            txtnationality.setVisibility(View.VISIBLE);
            txtidOrPassportNumber.setVisibility(View.VISIBLE);

        }

        txtidOrPassportNumber.setText(idOrPassportNumber);
        txtgender.setText(gender);
        txtdateOfBirth.setText(dateOfBirth);
        txtphone.setText(phone);
        txtemail.setText(email);

        imgEditimgViewbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActionActivity.this, EditClientActivity.class );
                 intent.putExtra("userId",id);
                startActivity(intent);
            }
        });
        imgDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActionActivity.this, AccountIforActivity.class );
                intent.putExtra("username",userName);
                startActivity(intent);

            }
        });

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDeleteConfirmationDialog();


               // Intent intent = new Intent(ActionActivity.this, ManageActivity.class);
               // startActivity(intent);

            }
        });



    }
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this user's information?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isDeleted = performDeleteOperations();
                        showResultDialog(isDeleted);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private boolean performDeleteOperations() {
        Connection connection = connectHelper.getConnection();
        if (connection == null) {
            Toast.makeText(this, "Database connection failed", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {

            connection.setAutoCommit(false);


            DeleteUser(id, connection);
            DeleteFromRole(id, connection);
            DeleteFromNotification(userName, connection);
            String accountID = GetAccountID(userName, connection);

            if (accountID != null) {
                DeleteFromTransactionHist(accountID, connection);
                DeleteFromBankAccount(bankAccount, connection);
            }
            DeleteFromReviews(id, connection);


            connection.commit();
            return true;
        } catch (SQLException e) {
            try {

                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }

    private void DeleteUser(String Id, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM AspNetUsers WHERE Id = ?")) {
            statement.setString(1, Id);
            statement.executeUpdate();
        }
    }


    private void DeleteFromRole(String Id, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM AspNetUserRoles WHERE UserId = ?")) {
            statement.setString(1, Id);
            statement.executeUpdate();
        }
    }

    private void DeleteFromNotification(String userName, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Notifications WHERE UserID = ?")) {
            statement.setString(1, userName);
            statement.executeUpdate();
        }
    }

    private String GetAccountID(String userName, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT AccountID FROM BankAccounts WHERE AccountNumber = ?")) {
            statement.setString(1, bankAccount);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("AccountID") : null;
            }
        }
    }

    private void DeleteFromTransactionHist(String accountID, Connection connection) throws SQLException {
        if (accountID != null) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Transactions WHERE BankAccountAccountID = ?")) {
                statement.setString(1, accountID);
                statement.executeUpdate();
            }
        }
    }

    private void DeleteFromBankAccount(String bankAccount, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM BankAccounts WHERE AccountNumber = ?")) {
            statement.setString(1, bankAccount);
            statement.executeUpdate();
        }
    }

    private void DeleteFromReviews(String id, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Reviews WHERE UserId = ?")) {
            statement.setString(1, id);
            statement.executeUpdate();
        }
    }

    private void showResultDialog(boolean isDeleted) {
        String message = isDeleted ? "User and associated records deleted successfully." : "Deletion failed. Please try again.";
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isDeleted) {
                            Intent intent = new Intent(ActionActivity.this, ManageActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .show();
    }


}