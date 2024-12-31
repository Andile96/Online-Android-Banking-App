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
import com.example.ufsqkbank.Adapters.ClientAdapter;
import com.example.ufsqkbank.Database.ConnectHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ManageActivity extends AppCompatActivity {

    ConnectHelper connectHelper;
    ListView lstClientView;
    ArrayList<String[]> lstclients;
    ArrayList<String[]> lstAllClientData;

    Button btnCreateClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        connectHelper = new ConnectHelper(this);

        lstClientView = findViewById(R.id.lstClients);
        lstclients = new ArrayList<>();
        lstAllClientData = new ArrayList<>();
        btnCreateClient = findViewById(R.id.btnAddClient);
        btnCreateClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageActivity.this, CreateClientActivity.class);
                startActivity(intent);
            }
        });

        new FetchClients().execute();


        lstClientView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String[] fullClientData = lstAllClientData.get(position);


                Intent intent = new Intent(ManageActivity.this, ActionActivity.class);
                intent.putExtra("clientData", fullClientData);
                startActivity(intent);
            }
        });
    }

    private class FetchClients extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection connection = connectHelper.getConnection();
            if (connection == null) {
                System.err.println("Database connection is null.");
                return false;
            }

            String query = "SELECT Id, LastName, FirstName, Gender, DateOfBirth, IDOrPassportNumber, " +
                    "Phone, EmployeeOrStudentNumber, UserType, UserName, Email " +
                    "FROM AspNetUsers WHERE UserType IN ('Student', 'Staff', 'External user')";
            ResultSet resultSet = connectHelper.getData(query);

            if (resultSet == null) {
                System.err.println("Failed to execute query or received null ResultSet.");
                return false;
            }

            try {
                lstclients.clear();
                lstAllClientData.clear();

                while (resultSet.next()) {
                    String id = resultSet.getString("Id");
                    String lastName = resultSet.getString("LastName");
                    String firstName = resultSet.getString("FirstName");
                    String gender = resultSet.getString("Gender");
                    String dateOfBirth = resultSet.getString("DateOfBirth");
                    String idOrPassportNumber = resultSet.getString("IDOrPassportNumber");
                    String phone = resultSet.getString("Phone");
                    String employeeOrStudentNumber = resultSet.getString("EmployeeOrStudentNumber");
                    String userType = resultSet.getString("UserType");
                    String userName = resultSet.getString("UserName");
                    String email = resultSet.getString("Email");

                    String bankQuery = "SELECT AccountNumber FROM BankAccounts WHERE AccountHolder = ?";
                    try (PreparedStatement bankStatement = connection.prepareStatement(bankQuery)) {
                        bankStatement.setString(1, userName);
                        ResultSet resultSet1 = bankStatement.executeQuery();

                        while (resultSet1.next()) {
                            String bankAccount = resultSet1.getString("AccountNumber");
                            String idgender="";
                            if(gender.equals("Male"))
                            {
                                idgender= "Mr";
                            }
                            else if (gender.equals("Female")){
                                idgender="Ms";
                            }
                            else {
                                idgender ="";
                            }

                            lstclients.add(new String[]{idgender+"."+userName, userType, bankAccount});

                            lstAllClientData.add(new String[]{id, lastName, firstName, gender, dateOfBirth,
                                    idOrPassportNumber, phone, employeeOrStudentNumber, userType,
                                    userName, email, bankAccount});
                        }
                        resultSet1.close();
                    }
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
                ClientAdapter adapter = new ClientAdapter(
                        ManageActivity.this,
                        lstclients
                );
                lstClientView.setAdapter(adapter);
            } else {
                Toast.makeText(ManageActivity.this, "Failed to retrieve data from database", Toast.LENGTH_LONG).show();
            }
        }
    }
}

