package com.example.ufsqkbank;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ufsqkbank.Database.ConnectHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditUserActivity extends AppCompatActivity {
    TextInputLayout editUserTypeLayout, edittxtStudentNumber, edittxteditStaffNum, editlastnamelayout,
            editfirstnameLayout, editNationalityLayout, edittxtID, edittxtPassport, editGenderLayout,
            editdateInputLayout, editEmailLayout, editcellPhoneNumLayout;

    AutoCompleteTextView EditspnUserType, editspnNationality, editspnGender;
    TextInputEditText edittxtStudNum, edittxtStafNum, edittxtlstNam, edittxtfstNam,
            edittxtIdentification, edittxtPass, editdateInputEditText, txtEmailAdress, txteditPhoneNum;

    Button btneditUpdate;
    TextView txtChangePassword;

     ConnectHelper connectHelper;
    String userId; // Replace with actual user ID
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        connectHelper = new ConnectHelper(this);
        intent = getIntent();
        userId = intent.getStringExtra("userId");



        // Initialize views
        editUserTypeLayout = findViewById(R.id.editUserTypeLayout);
        edittxtStudentNumber = findViewById(R.id.edittxtStudentNumber);
        edittxteditStaffNum = findViewById(R.id.edittxteditStaffNum);
        editlastnamelayout = findViewById(R.id.editlastnamelayout);
        editfirstnameLayout = findViewById(R.id.editfirstnameLayout);
        editNationalityLayout = findViewById(R.id.editNationalityLayout);
        edittxtID = findViewById(R.id.edittxtID);
        edittxtPassport = findViewById(R.id.edittxtPassport);
        editGenderLayout = findViewById(R.id.editGenderLayout);
        editdateInputLayout = findViewById(R.id.editdateInputLayout);
        editEmailLayout = findViewById(R.id.editEmailLayout);
        editcellPhoneNumLayout = findViewById(R.id.editcellPhoneNumLayout);

        EditspnUserType = findViewById(R.id.EditspnUserType);
        editspnNationality = findViewById(R.id.editspnNationality);
        editspnGender = findViewById(R.id.editspnGender);

        edittxtStudNum = findViewById(R.id.edittxtStudNum);
        edittxtStafNum = findViewById(R.id.edittxtStafNum);
        edittxtlstNam = findViewById(R.id.edittxtlstNam);
        edittxtfstNam = findViewById(R.id.edittxtfstNam);
        edittxtIdentification = findViewById(R.id.edittxtIdentification);
        edittxtPass = findViewById(R.id.edittxtPass);
        editdateInputEditText = findViewById(R.id.editdateInputEditText);
        txtEmailAdress = findViewById(R.id.txtEmailAdress);
        txteditPhoneNum = findViewById(R.id.txteditPhoneNum);

        btneditUpdate = findViewById(R.id.btneditUpdate);
        txtChangePassword = findViewById(R.id.txtChangePassword);
        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditUserActivity.this, ChangePasswordActivity.class);
                intent.putExtra("Id", userId);
                startActivity(intent);
            }
        });

        // Retrieve and display user information
        displayUserInfo();

        // Set update button listener
        btneditUpdate.setOnClickListener(view -> showUpdateConfirmationDialog());
    }

    private void displayUserInfo() {
        Connection connection = connectHelper.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT LastName, FirstName, Gender, DateOfBirth, IDOrPassportNumber, " +
                        "Phone, EmployeeOrStudentNumber, UserType, Email " +
                        "FROM AspNetUsers WHERE Id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, userId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    edittxtlstNam.setText(resultSet.getString("LastName"));
                    edittxtfstNam.setText(resultSet.getString("FirstName"));
                    editspnGender.setText(resultSet.getString("Gender"));
                    editdateInputEditText.setText(resultSet.getString("DateOfBirth"));
                    edittxtIdentification.setText(resultSet.getString("IDOrPassportNumber"));
                    txteditPhoneNum.setText(resultSet.getString("Phone"));
                    edittxtStudNum.setText(resultSet.getString("EmployeeOrStudentNumber"));
                    EditspnUserType.setText(resultSet.getString("UserType"));
                    txtEmailAdress.setText(resultSet.getString("Email"));
                }

                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Database connection failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUpdateConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Update Profile")
                .setMessage("Are you sure you want to update your profile?")
                .setPositiveButton("Yes", (dialog, which) -> updateUserProfile())
                .setNegativeButton("No", null)
                .show();
    }

    private void updateUserProfile() {
        Connection connection = connectHelper.getConnection();
        if (connection != null) {
            try {

                String updateQuery = "UPDATE AspNetUsers SET LastName = ?, FirstName = ?, Phone = ? WHERE Id = ?";
                PreparedStatement statement = connection.prepareStatement(updateQuery);


                statement.setString(1, edittxtlstNam.getText().toString());
                statement.setString(2, edittxtfstNam.getText().toString());
                statement.setString(3, txteditPhoneNum.getText().toString());


                statement.setString(4, userId);


                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Profile update failed.", Toast.LENGTH_SHORT).show();
                }

                // Close the statement and connection
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Database connection failed", Toast.LENGTH_SHORT).show();
        }
    }

}
