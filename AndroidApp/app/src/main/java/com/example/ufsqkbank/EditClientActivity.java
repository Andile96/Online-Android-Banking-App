package com.example.ufsqkbank;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.ufsqkbank.Database.ConnectHelper;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;


import android.os.Bundle;



public class EditClientActivity extends AppCompatActivity {

    AutoCompleteTextView UserTypeDropdown, NationalityTypeDropdown, genderDropdown;
    TextInputEditText dateInputEditText, txtStudNum, txtStaffNum, txtPassport, txtIdentification,
            txtEmail, txtCellPhone, txtPassword, txtConfirmPassword,txtlstName, txtfstName;

    TextInputLayout dateInputLayout, studentNumberLayout, GenderLayout, staffNumberLayout, iDLayout, PassportLayout,
            PasswordLayout,ConfirmPasswordLayout,CellPhoneNumLayout,EmailLayout, lstNameLayout,
            fstNameLayout,UserTypeLayout,NationalityLayout;
    Button btnCreateAccount;
    String selectedUserType, selectedNationality, SelectedGender, DateofBith, FirstName, LastName, IDPassportNumber,
            CellPhoneNumber, Email, Password, StudentStaffNumber,StudentNumber,StaffNumber,IDNumber,PassportNumber;
    private MaterialDatePicker<Long> materialDatePicker;


    List<String> allowedDomains = Arrays.asList(
            "ufs.ac.za", "ufs4life.ac.za", "gmail.com", "outlook.com", "yahoo.com"
    );

    public ConnectHelper dbConnection;
    Intent intent;
    String userId;
    String CurrentPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client);

        intent = getIntent();
        userId = intent.getStringExtra("userId");


        dbConnection = new ConnectHelper(this);



        UserTypeDropdown = findViewById(R.id.spnUserTypeEdit);
        NationalityTypeDropdown = findViewById(R.id.spnNationalityEdit);
        genderDropdown = findViewById(R.id.spnGenderEdit);
        dateInputEditText = findViewById(R.id.dateInputEditTextEdit);
        //layout
        dateInputLayout = findViewById(R.id.dateInputLayoutEdit);
        studentNumberLayout = findViewById(R.id.txtStudentNumberEdit);
        staffNumberLayout = findViewById(R.id.txtStaffNumEdit);
        iDLayout = findViewById(R.id.txtIDEdit);
        PassportLayout = findViewById(R.id.txtPassportEdit);
        EmailLayout = findViewById(R.id.EmailLayoutEdit);
        CellPhoneNumLayout = findViewById(R.id.cellPhoneNumLayoutEdit);
        PasswordLayout = findViewById(R.id.PasswordLayoutEdit);
        lstNameLayout = findViewById(R.id.lastnamelayoutEdit);
        fstNameLayout = findViewById(R.id.firstnameLayoutEdit);
        UserTypeLayout = findViewById(R.id.UserTypeLayoutEdit);
        GenderLayout = findViewById(R.id.GenderLayoutEdit);
        NationalityLayout = findViewById(R.id.NationalityLayoutEdit);



        //EditText
        txtStudNum = findViewById(R.id.txtStudNumEdit);
        txtStaffNum = findViewById(R.id.txtStafNumEdit);
        txtIdentification = findViewById(R.id.txtIdentificationEdit);
        txtPassport = findViewById(R.id.txtPassEdit);
        txtEmail = findViewById(R.id.txtEmailAdressEdit);
        txtCellPhone = findViewById(R.id.txtPhoneNumEdit);
        txtPassword = findViewById(R.id.txtPasswordEdit);
        txtfstName = findViewById(R.id.txtfstNamEdit);
        txtlstName= findViewById(R.id.txtlstNamEdit);

        btnCreateAccount = findViewById(R.id.btnUpdateAcc);



        if (userId!=null)
        {
            displayUserInfo();
        }
        else {
            Toast.makeText(EditClientActivity.this,"ERRORID0",Toast.LENGTH_SHORT).show();
        }
        RealTimeValidation();
        DisplayUserType();
        DisplayNationality();
        DisplayDropdown();
        DisplayCalender();


        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateConfirmationDialog();


            }
        });
    }
    private void showUpdateConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Update Profile")
                .setMessage("Are you sure you want to update clients profile?")
                .setPositiveButton("Yes", (dialog, which) -> {updateUserProfile();
                        Intent intent1 = new Intent(EditClientActivity.this, ManageActivity.class);
                         startActivity(intent1);
                         finish();})
                .setNegativeButton("No", null)
                .show();

    }
    private void updateUserProfile() {
        Connection connection = dbConnection.getConnection();
        if (connection != null) {
            try {
                String updateQuery = "UPDATE AspNetUsers SET LastName = ?, FirstName = ?, Gender= ?, DateOfBirth = ?, IDOrPassportNumber= ?," +
                        "Phone = ?, EmployeeOrStudentNumber = ?, UserType = ?, Email = ?,MobilePassword = ? WHERE Id = ?";
                PreparedStatement statement = connection.prepareStatement(updateQuery);


                statement.setString(1, txtlstName.getText().toString());
                statement.setString(2, txtfstName.getText().toString());
                statement.setString(3, genderDropdown.getText().toString());
                statement.setString(4, dateInputEditText.getText().toString());
                if (!txtIdentification.getText().toString().isEmpty())
                {
                    statement.setString(5, txtIdentification.getText().toString());
                }else if(!txtPassport.getText().toString().isEmpty()){
                    statement.setString(5, txtPassport.getText().toString());
                }

                statement.setString(6, txtCellPhone.getText().toString());
                if(UserTypeDropdown.getText().toString().equals("Student"))
                {
                    statement.setString(7, txtStudNum.getText().toString());
                }else if(UserTypeDropdown.getText().toString().equals("Staff")){
                    statement.setString(7, txtStaffNum.getText().toString());
                }

                statement.setString(8, UserTypeDropdown.getText().toString());
                statement.setString(9, txtEmail.getText().toString());

                if (!txtPassport.getText().toString().isEmpty())
                {
                    statement.setString(10, txtPassport.getText().toString());
                }
                else {
                    statement.setString(10, CurrentPassword);
                }

                statement.setString(11, userId);


                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Profile update failed.", Toast.LENGTH_SHORT).show();
                }

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

    private void displayUserInfo() {
        Connection connection = dbConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT LastName, FirstName, Gender, DateOfBirth, IDOrPassportNumber, " +
                        "Phone, EmployeeOrStudentNumber, UserType, Email,MobilePassword " +
                        "FROM AspNetUsers WHERE Id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, userId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    txtlstName.setText(resultSet.getString("LastName"));
                    txtfstName.setText(resultSet.getString("FirstName"));
                    genderDropdown.setText(resultSet.getString("Gender"));
                    dateInputEditText.setText(resultSet.getString("DateOfBirth"));
                     String idPass = resultSet.getString("IDOrPassportNumber");
                    txtCellPhone.setText(resultSet.getString("Phone"));
                    String StudStaff= resultSet.getString("EmployeeOrStudentNumber");
                    String usertyp = resultSet.getString("UserType");
                    CurrentPassword = resultSet.getString("MobilePassword");
                    UserTypeDropdown.setText(usertyp);
                    txtEmail.setText(resultSet.getString("Email"));
                    if(usertyp.equals("Student"))
                    {
                        studentNumberLayout.setVisibility(View.VISIBLE);
                        txtStudNum.setText(StudStaff);
                    }
                    else if(usertyp.equals("Staff")){
                        staffNumberLayout.setVisibility(View.VISIBLE);
                        txtStaffNum.setText(StudStaff);
                    }

                    if (idPass.length()>10)
                    {
                        NationalityTypeDropdown.setText("South African");
                        iDLayout.setVisibility(View.VISIBLE);
                        txtIdentification.setText(idPass);
                    }
                    else if (idPass.length()<10)
                    {
                        NationalityTypeDropdown.setText("Non South African");
                        PassportLayout.setVisibility(View.VISIBLE);
                        txtPassport.setText(idPass);
                    }



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
    public void RealTimeValidation(){
        //UserType
        UserTypeDropdown.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus && (selectedUserType == null || selectedUserType.isEmpty())) {
                UserTypeLayout.setError("Please select a user type.");
            }
        });
        UserTypeDropdown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().isEmpty())
                {
                    UserTypeLayout.setError(null);

                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //natinality
        NationalityTypeDropdown.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus && (selectedNationality == null || selectedNationality.isEmpty())) {
                NationalityLayout.setError("Please select Nationality.");
            }
        });
        NationalityTypeDropdown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().isEmpty())
                {
                    NationalityLayout.setError(null);

                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //natinality
        genderDropdown.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus && (SelectedGender == null || SelectedGender.isEmpty())) {
                GenderLayout.setError("Please select gender.");
            }
        });
        genderDropdown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().isEmpty())
                {
                    GenderLayout.setError(null);

                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                SelectedGender = editable.toString();
            }
        });
        //lastName
        txtlstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().matches("[a-zA-Z]*"))
                {
                    lstNameLayout.setError("Only text input is allowed!");
                }else if(charSequence.length() > 15)
                {
                    lstNameLayout.setError("Must not be more than 15 characters");
                } else {
                    lstNameLayout.setError(null);  // Clear error if valid

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                LastName = editable.toString();
            }
        });
        //FirstName
        txtfstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().matches("[a-zA-Z]*"))
                {
                    fstNameLayout.setError("Only text input is allowed!");
                }else if(charSequence.length() > 15)
                {
                    fstNameLayout.setError("Must not be more than 15 characters");
                } else {
                    fstNameLayout.setError(null);  // Clear error if valid
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                FirstName = editable.toString();
            }
        });
        //studentNumber
        // txtStudNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        txtStudNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().matches("\\d*"))
                {
                    studentNumberLayout.setError("Only numeric input allowed!");
                }else if(charSequence.length() > 10)
                {
                    studentNumberLayout.setError("Staff number must be exactly 10 digits!");
                } else {
                    studentNumberLayout.setError(null);  // Clear error if valid
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                StudentNumber = editable.toString();

            }
        });

        //StaffNumber
        txtStaffNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().matches("\\d*"))
                {
                    staffNumberLayout.setError("Only numeric input allowed!");
                }else if(charSequence.length() > 8)
                {
                    staffNumberLayout.setError("Staff number must be exactly 8 digits!");
                } else {
                    staffNumberLayout.setError(null);  // Clear error if valid
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                StaffNumber = editable.toString();
            }
        });
        //IDNumber
        txtIdentification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().matches("\\d*"))
                {
                    iDLayout.setError("Only numeric input allowed!");
                }else if(charSequence.length() > 13)
                {
                    iDLayout.setError("ID number must be exactly 13 digits!");
                } else {
                    iDLayout.setError(null);  // Clear error if valid
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                IDNumber = editable.toString();
            }
        });
        //PassPort
        txtPassport.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().matches("[a-zA-Z0-9]*"))
                {
                    PassportLayout.setError("Only numeric input allowed!");
                }else if(charSequence.length() > 9)
                {
                    PassportLayout.setError("Passport number should not be more than 9 characters!");
                } else {
                    PassportLayout.setError(null);  // Clear error if valid
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                PassportNumber = editable.toString();
            }
        });
        //Email
        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String sEmail = charSequence.toString().toLowerCase().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches())
                {
                    EmailLayout.setError("Invalid email format!, should include @");
                }else if(!isValidDomain(sEmail))
                {
                    EmailLayout.setError("Allowed emails: @ufs.ac.za, @ufs4life.ac.za, @gmail.com, @outlook.com, @yahoo.com");
                } else {
                    EmailLayout.setError(null);  // Clear error if valid
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Email = editable.toString();
            }
        });
        //CellPhoneNumber
        txtCellPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().matches("[a-zA-Z0-9]*"))
                {
                    CellPhoneNumLayout.setError("Only numeric input allowed!");
                }else if(charSequence.length() > 10)
                {
                    CellPhoneNumLayout.setError("Cell phone number should not be more than 10 digits!");
                } else {
                    CellPhoneNumLayout.setError(null);  // Clear error if valid
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                CellPhoneNumber = editable.toString();
            }
        });
        //Password
        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String sPassword = charSequence.toString().trim();
                if(!isValidPassword(sPassword))
                {
                    PasswordLayout.setError("Password must be at least 6 characters,contain letters, digits, and special characters {}()@#$%&*!.");
                }
                else {
                    PasswordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Password = editable.toString();
            }
        });
        //ConfirmPassword
    }

    public void DisplayUserType()
    {
        // Create an ArrayAdapter using gender options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.user_type, android.R.layout.simple_dropdown_item_1line);
        UserTypeDropdown.setAdapter(adapter);

        // Show the drop-down when the text field is clicked
        UserTypeDropdown.setOnClickListener(v -> UserTypeDropdown.showDropDown());

        // Handle item selection from the drop-down
        UserTypeDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedUserType = parent.getItemAtPosition(position).toString();  // Extract the selected text
                handleUserTypeSelection(selectedUserType);  // Handle the layout visibility
            }
        });
    }
    private void handleUserTypeSelection(String userType) {
        if (userType.equals("Student")) {
            studentNumberLayout.setVisibility(View.VISIBLE);
            staffNumberLayout.setVisibility(View.GONE);

        } else if (userType.equals("Staff")) {
            studentNumberLayout.setVisibility(View.GONE);
            staffNumberLayout.setVisibility(View.VISIBLE);

        } else {
            studentNumberLayout.setVisibility(View.GONE);
            staffNumberLayout.setVisibility(View.GONE);


        }
    }
    public void  DisplayNationality()
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.nationality, android.R.layout.simple_dropdown_item_1line);
        NationalityTypeDropdown.setAdapter(adapter);

        NationalityTypeDropdown.setOnClickListener(v -> NationalityTypeDropdown.showDropDown());

        NationalityTypeDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedNationality = parent.getItemAtPosition(position).toString();
                handleUserNationalitySelection(selectedNationality);
            }});

    }

    private void handleUserNationalitySelection(String Nationality) {
        if (Nationality.equals("South African")) {
            iDLayout.setVisibility(View.VISIBLE);
            PassportLayout.setVisibility(View.GONE);
        } else if (Nationality.equals("Non South African")) {
            iDLayout.setVisibility(View.GONE);
            PassportLayout.setVisibility(View.VISIBLE);
        } else {
            iDLayout.setVisibility(View.GONE);
            PassportLayout.setVisibility(View.GONE);
        }
    }

    public void DisplayDropdown()
    {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.gender_options, android.R.layout.simple_dropdown_item_1line);
        genderDropdown.setAdapter(adapter);

        genderDropdown.setOnClickListener(v -> genderDropdown.showDropDown());

        genderDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedGender = parent.getItemAtPosition(position).toString();  // Extract the selected text

            }});
    }
    public void DisplayCalender() {

        if (materialDatePicker == null) {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Select a date");
            materialDatePicker = builder.build();

            materialDatePicker.addOnPositiveButtonClickListener(selection -> {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                DateofBith = format.format(new Date(selection));
                dateInputEditText.setText(DateofBith);
            });
        }


        dateInputEditText.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag("DATE_PICKER") == null) {
                materialDatePicker.show(fragmentManager, "DATE_PICKER");
            }
        });
    }

    private boolean isValidDomain(String email)
    {
        String domain = email.substring(email.indexOf('@') + 1);
        return allowedDomains.contains(domain);
    }
    private boolean isValidPassword(String password)
    {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$";
        return password.matches(passwordPattern);
    }
}