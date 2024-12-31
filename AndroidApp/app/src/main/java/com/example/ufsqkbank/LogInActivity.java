package com.example.ufsqkbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ufsqkbank.Database.ConnectHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LogInActivity extends AppCompatActivity {

    private ConnectHelper connectHelper;
    private TextInputLayout usernamelayout, passwordlayout;
    private TextInputEditText edtUsername, edtPassword;
    private Button btnSignIn;
    private TextView txtSignUp;

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       connectHelper = new ConnectHelper(this);

        usernamelayout = findViewById(R.id.Usernamelayout);
        passwordlayout = findViewById(R.id.signInPasswordLayout);
        edtUsername = findViewById(R.id.txtSignInUserName);
        edtPassword = findViewById(R.id.txtSignInPassword);
        btnSignIn = findViewById(R.id.btnSign_In);
        txtSignUp = findViewById(R.id.txtRegister);

        executorService = Executors.newSingleThreadExecutor();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogin();
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToRegister();
            }
        });
    }

    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (!username.isEmpty() && !password.isEmpty()) {
            // Execute login in background
            executorService.submit(() -> attemptLogin(username, password));
        } else {
            Toast.makeText(LogInActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
        }
    }

    private void attemptLogin(String username, String password) {

        Connection connection = connectHelper.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT Id, Profile, LastName, Gender, Email, UserName FROM AspNetUsers WHERE Email = ? AND MobilePassword = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String userId = resultSet.getString("Id");
                    byte[] profile = resultSet.getBytes("Profile");
                    String lastName = resultSet.getString("LastName");
                    String gender = resultSet.getString("Gender");
                    String userEmail = resultSet.getString("Email");
                    String userName = resultSet.getString("UserName");
                    String salutation = (gender != null && gender.equals("Male")) ? "Mr" : "Ms";

                    String userRole = fetchUserRole(userId);

                    navigateToNextActivity(userId,profile, lastName, salutation, userName, userEmail, userRole);

                } else {

                    runOnUiThread(() -> showToast("Invalid credentials. Please try again."));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> showToast("Database connection error."));
            }
        } else {
            runOnUiThread(() -> showToast("Unable to connect to the database."));
        }
    }

    private String fetchUserRole(String userId) {
        Connection connection = connectHelper.getConnection();
        String queryRoleId = "SELECT RoleId FROM AspNetUserRoles WHERE UserId = ?";
        String queryRoleName = "SELECT Name FROM AspNetRoles WHERE Id = ?";
        String roleId = null;
        String roleName = null;

        try {
            PreparedStatement roleIdStatement = connection.prepareStatement(queryRoleId);
            roleIdStatement.setString(1, userId);
            ResultSet roleIdResultSet = roleIdStatement.executeQuery();

            if (roleIdResultSet.next()) {
                roleId = roleIdResultSet.getString("RoleId");
            }

            PreparedStatement roleNameStatement = connection.prepareStatement(queryRoleName);
            roleNameStatement.setString(1, roleId);
            ResultSet roleNameResultSet = roleNameStatement.executeQuery();

            if (roleNameResultSet.next()) {
                roleName = roleNameResultSet.getString("Name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roleName;
    }

    private void navigateToNextActivity(String userId,byte[] profile, String lastName, String salutation, String username, String email, String role) {
        runOnUiThread(() -> {
            Intent intent;
            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            bundle.putByteArray("profile", profile);
            bundle.putString("lstname", lastName);
            bundle.putString("mrms", salutation);
            bundle.putString("username", username);
            bundle.putString("email", email);
            bundle.putString("role",role);

            switch (role) {
                case "Client":
                    intent = new Intent(LogInActivity.this, MainActivity.class);
                    break;
                case "Admin":
                    intent = new Intent(LogInActivity.this, AdminConsaltantActivity2.class);
                    break;
                case "Consultant":
                    intent = new Intent(LogInActivity.this, AdminConsaltantActivity2.class);

                    break;
                case "Financial advisor":
                    intent = new Intent(LogInActivity.this, FinancialAdvisorActivity.class);
                    break;
                default:
                    showToast("Invalid User Role!");
                    return;
            }

            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void redirectToRegister() {
        Intent intent = new Intent(LogInActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(LogInActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        executorService.shutdown();
    }
}
