package com.example.ufsqkbank;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {

    ConnectHelper connectHelper;
    Intent intent;
    String userId;

    TextInputEditText txtCurrentPassword, txtNewPassword, txtConfirmPassword;
    Button btnUpdatePass;
    ImageButton imgbtnSuccessful, imgbtnNotSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize UI elements
        connectHelper = new ConnectHelper(this);
        intent = getIntent();
        userId = intent.getStringExtra("Id");

        txtCurrentPassword = findViewById(R.id.txtCrrent4Password);
        txtNewPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.ConfirmPassword);
        btnUpdatePass = findViewById(R.id.btnUpdatePass);
        imgbtnSuccessful = findViewById(R.id.imgbtnSuccessful);
        imgbtnNotSend = findViewById(R.id.imgbtnnotSend);

        btnUpdatePass.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPassword = txtCurrentPassword.getText().toString().trim();
        String newPassword = txtNewPassword.getText().toString().trim();
        String confirmPassword = txtConfirmPassword.getText().toString().trim();

        if (validateInput(currentPassword, newPassword, confirmPassword)) {
            // Verify current password
            try (Connection connection = connectHelper.getConnection()) {
                if (connection == null || connection.isClosed()) {
                    showErrorMessage("Unable to connect to the database.");
                    return;
                }

                if (isCurrentPasswordCorrect(connection, currentPassword)) {
                    // Show confirmation dialog
                    new AlertDialog.Builder(this)
                            .setTitle("Confirm Password Change")
                            .setMessage("Are you sure you want to change your password?")
                            .setPositiveButton("Yes", (dialog, which) -> updatePassword(newPassword))
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    showErrorMessage("Current password is incorrect.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorMessage("An error occurred while verifying the password: " + e.getMessage());
            }
        }
    }


    private boolean validateInput(String currentPassword, String newPassword, String confirmPassword) {
        // Check if all fields are filled
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showErrorMessage("All fields are required");
            return false;
        }

        // Check if new password matches confirmation
        if (!newPassword.equals(confirmPassword)) {
            showErrorMessage("New password and confirmation do not match");
            return false;
        }

        // Check if new password meets the criteria
        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])[^\\s]{6,}$");
        if (!passwordPattern.matcher(newPassword).matches()) {
            showErrorMessage("Password must be at least 6 characters, and include letters, numbers, and special characters");
            return false;
        }

        return true;
    }

    private boolean isCurrentPasswordCorrect(Connection connection, String currentPassword) throws SQLException {
        String query = "SELECT MobilePassword FROM AspNetUsers WHERE Id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("MobilePassword");
                return storedPassword.equals(currentPassword);
            }
        }
        return false;
    }
    private void updatePassword(String newPassword) {
        Connection connection1 = connectHelper.getConnection();
        if (userId == null || userId.isEmpty()) {
            showErrorMessage("User ID is missing.");
            return;
        }



        try{
            String query = "UPDATE AspNetUsers SET MobilePassword = ? WHERE Id = ?";
            PreparedStatement statement = connection1.prepareStatement(query);
            statement.setString(1, newPassword);
            statement.setString(2, userId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                imgbtnSuccessful.setVisibility(View.VISIBLE);
                showSuccessDialog();
            } else {
                showErrorMessage("Password change failed. No rows were affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("An error occurred while updating the password : "+ e.getMessage());
        }
    }


    private void showErrorMessage(String message) {
        imgbtnNotSend.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Password Changed")
                .setMessage("You will be logged out now.")
                .setPositiveButton("OK", (dialog, which) -> {

                    Intent loginIntent = new Intent(ChangePasswordActivity.this, LogInActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                })
                .show();
    }
}
