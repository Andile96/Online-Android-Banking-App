package com.example.ufsqkbank;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ufsqkbank.Database.ConnectHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileFragment extends Fragment {

    private ConnectHelper connectHelper;
    private static final String ARG_USER_ID = "userId";
    private TextView txtlstName, txtEmail, txtfstName, txtUserType, txtIStudentStaff, txtIStudentStaffNum,
            txtGender, txtDateofbirth, txtNationality, txtIDPassport, txtPassportIDNum, txtphone;
    private String userId;
    Button btnEdit;
    Intent intent;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectHelper = new ConnectHelper(getActivity());


        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            Log.d("ProfileFragment", "User ID retrieved from Intent: " + userId);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        txtUserType = view.findViewById(R.id.txtIuserType);
        txtlstName = view.findViewById(R.id.txtIlstName);
        txtfstName = view.findViewById(R.id.txtIfstName);
        txtPassportIDNum = view.findViewById(R.id.txtIDIPassportNum);
        txtIDPassport = view.findViewById(R.id.txtIPassportID);
        txtNationality = view.findViewById(R.id.txtINationality);
        txtGender = view.findViewById(R.id.txtIgender);
        txtEmail = view.findViewById(R.id.txtIEmail);
        txtphone = view.findViewById(R.id.txtIPhone);
        txtIStudentStaff = view.findViewById(R.id.txtIStudentStaff);
        txtIStudentStaffNum = view.findViewById(R.id.txtIStudentStaffNum);
        txtDateofbirth = view.findViewById(R.id.txtIDOB);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {  // Check if the activity is not null
                    Intent intent = new Intent(getActivity(), EditUserActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else {
                    Log.e("ProfileFragment", "Activity is null, cannot start EditUserActivity");
                }
            }
        });

        if (userId != null) {
            new FetchProfileTask(userId).execute();
        } else {
            Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private class FetchProfileTask extends AsyncTask<Void, Void, String[]> {
        private String userId;

        public FetchProfileTask(String userId) {
            this.userId = userId;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            Connection connection = connectHelper.getConnection();
            if (connection == null) {
                Log.e("FetchProfileTask", "Database connection is null");
                return null;
            }

            try {
                String query = "SELECT LastName, FirstName, Gender, DateOfBirth, IDOrPassportNumber, " +
                        "Phone, EmployeeOrStudentNumber, UserType, Email FROM AspNetUsers WHERE Id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, userId);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String lstname = resultSet.getString("LastName");  // Corrected
                    String fstname = resultSet.getString("FirstName");
                    String gender = resultSet.getString("Gender");
                    String dob = resultSet.getString("DateOfBirth");
                    String idpass = resultSet.getString("IDOrPassportNumber");
                    String phone = resultSet.getString("Phone");
                    String staffStudnum = resultSet.getString("EmployeeOrStudentNumber");
                    String usertype = resultSet.getString("UserType");
                    String email = resultSet.getString("Email");

                    return new String[]{lstname, fstname, gender, dob, idpass, phone, staffStudnum, usertype, email};
                } else {
                    Log.e("FetchProfileTask", "No data found for userId: " + userId);
                }
            } catch (SQLException e) {
                Log.e("FetchProfileTask", "SQL Exception: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                txtlstName.setText(result[0]);
                txtfstName.setText(result[1]);
                txtGender.setText(result[2]);
                txtDateofbirth.setText(result[3]);
                txtphone.setText(result[5]);
                txtUserType.setText(result[7]);
                txtEmail.setText(result[8]);

                if ("Student".equals(result[7])) {
                    txtIStudentStaff.setText("Student Number");
                    txtIStudentStaffNum.setText(result[6]);
                    txtIStudentStaff.setVisibility(View.VISIBLE);
                    txtIStudentStaffNum.setVisibility(View.VISIBLE);
                } else if ("Staff".equals(result[7])) {
                    txtIStudentStaff.setText("Staff Number");
                    txtIStudentStaffNum.setText(result[6]);
                    txtIStudentStaff.setVisibility(View.VISIBLE);
                    txtIStudentStaffNum.setVisibility(View.VISIBLE);
                }

                if (result[4].length() > 10) {
                    txtNationality.setText("South African");
                    txtIDPassport.setText("SA Identity Number");
                    txtPassportIDNum.setText(result[4]);
                    txtIDPassport.setVisibility(View.VISIBLE);
                    txtPassportIDNum.setVisibility(View.VISIBLE);
                } else if (result[4].length() < 10) {
                    txtNationality.setText("Non South African");
                    txtIDPassport.setText("Passport Number");
                    txtPassportIDNum.setText(result[4]);
                    txtIDPassport.setVisibility(View.VISIBLE);
                    txtPassportIDNum.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(getContext(), "Failed to fetch profile data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
