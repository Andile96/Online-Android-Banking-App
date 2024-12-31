package com.example.ufsqkbank;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.example.ufsqkbank.Adapters.StaffAdapter;
import com.example.ufsqkbank.Database.ConnectHelper;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ManageStaffActivity extends AppCompatActivity {

    private ConnectHelper connectHelper;
    private ListView lstStaffView;
    private ArrayList<String[]> lstStaff;
    private Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_staff);

        connectHelper = new ConnectHelper(this);
        lstStaffView = findViewById(R.id.lstEmployees);
        lstStaff = new ArrayList<>();
        create = findViewById(R.id.btnAddStaff);


        create.setOnClickListener(view -> {

        });

        new FetchStaff().execute();
    }

    private class FetchStaff extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection connection = connectHelper.getConnection();
            if (connection == null) {
                System.err.println("Database connection is null.");
                return false;
            }

            String query = "SELECT UserName, EmployeeOrStudentNumber, UserType " +
                    "FROM AspNetUsers WHERE UserType IN ('Consultant', 'Financial advisor')";
            ResultSet resultSet = connectHelper.getData(query);

            if (resultSet == null) {
                System.err.println("Failed to execute query or received null ResultSet.");
                return false;
            }

            try {
                while (resultSet.next()) {
                    String username = resultSet.getString("UserName");
                    String employeeOrStudentNumber = resultSet.getString("EmployeeOrStudentNumber");
                    String userType = resultSet.getString("UserType");

                    lstStaff.add(new String[]{username, employeeOrStudentNumber, userType});
                }
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    resultSet.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                StaffAdapter adapter = new StaffAdapter(
                        ManageStaffActivity.this,
                        lstStaff
                );
                lstStaffView.setAdapter(adapter);

                lstStaffView.setOnItemClickListener((adapterView, view, position, id) -> {

                    String[] staffData = lstStaff.get(position);
                    Toast.makeText(ManageStaffActivity.this, "Selected: " + staffData[0], Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(ManageStaffActivity.this, "Failed to retrieve data from database", Toast.LENGTH_LONG).show();
            }
        }
    }
}
