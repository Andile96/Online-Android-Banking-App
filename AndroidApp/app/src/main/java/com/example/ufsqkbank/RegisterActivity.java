package com.example.ufsqkbank;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
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


public class RegisterActivity extends AppCompatActivity {
    AutoCompleteTextView UserTypeDropdown, NationalityTypeDropdown, genderDropdown;
    TextInputEditText dateInputEditText, txtStudNum, txtStaffNum, txtPassport, txtIdentification,
    txtEmail, txtCellPhone, txtPassword, txtConfirmPassword,txtlstName, txtfstName;

    TextInputLayout dateInputLayout, studentNumberLayout, GenderLayout, staffNumberLayout, iDLayout, PassportLayout,
            PasswordLayout,ConfirmPasswordLayout,CellPhoneNumLayout,EmailLayout, lstNameLayout,
            fstNameLayout,UserTypeLayout,NationalityLayout;
    Button btnSignUp;
    TextView txtRedirectLogIn;
    String selectedUserType, selectedNationality, SelectedGender, DateofBith, FirstName, LastName, IDPassportNumber,
    CellPhoneNumber, Email, Password, StudentStaffNumber,StudentNumber,StaffNumber,IDNumber,PassportNumber;
    private MaterialDatePicker<Long> materialDatePicker;


    List<String> allowedDomains = Arrays.asList(
            "ufs.ac.za", "ufs4life.ac.za", "gmail.com", "outlook.com", "yahoo.com"
    );

    public ConnectHelper dbConnection;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        dbConnection = new ConnectHelper(this);



        UserTypeDropdown = findViewById(R.id.spnUserType);
        NationalityTypeDropdown = findViewById(R.id.spnNationality);
        genderDropdown = findViewById(R.id.spnGender);
       dateInputEditText = findViewById(R.id.dateInputEditText);
       //layout
       dateInputLayout = findViewById(R.id.dateInputLayout);
        studentNumberLayout = findViewById(R.id.txtStudentNumber);
        staffNumberLayout = findViewById(R.id.txtStaffNum);
        iDLayout = findViewById(R.id.txtID);
        PassportLayout = findViewById(R.id.txtPassport);
        EmailLayout = findViewById(R.id.EmailLayout);
        CellPhoneNumLayout = findViewById(R.id.cellPhoneNumLayout);
        PasswordLayout = findViewById(R.id.PasswordLayout);
        ConfirmPasswordLayout = findViewById(R.id.ConfirmPasswordLayout);
        lstNameLayout = findViewById(R.id.lastnamelayout);
        fstNameLayout = findViewById(R.id.firstnameLayout);
        UserTypeLayout = findViewById(R.id.UserTypeLayout);
        GenderLayout = findViewById(R.id.GenderLayout);
        NationalityLayout = findViewById(R.id.NationalityLayout);



        //EditText
        txtStudNum = findViewById(R.id.txtStudNum);
        txtStaffNum = findViewById(R.id.txtStafNum);
        txtIdentification = findViewById(R.id.txtIdentification);
        txtPassport = findViewById(R.id.txtPass);
        txtEmail = findViewById(R.id.txtEmailAdress);
        txtCellPhone = findViewById(R.id.txtPhoneNum);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword= findViewById(R.id.ConfirmPassword);
        txtfstName = findViewById(R.id.txtfstNam);
        txtlstName= findViewById(R.id.txtlstNam);

        btnSignUp = findViewById(R.id.btnSign_Up);
        txtRedirectLogIn = findViewById(R.id.txtSignIn);
        btnSignUp.setOnClickListener(view -> RegisterUser());

        RealTimeValidation();
       DisplayUserType();
       DisplayNationality();
       DisplayDropdown();
       DisplayCalender();
       txtRedirectLogIn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               redirectToLogin();
           }
       });


    }
    public void RegisterUser(){
        //for user
        UUID genarateID = UUID.randomUUID();
        String UserId = genarateID.toString();
        //for Roles
      //  SQLiteDatabase identityDb = dbHelper.openDatabase("IdentityUFSQQBankDb_2019728480.db");
       // SQLiteDatabase entityDb = dbHelper.openDatabase("UFSQQBankDb_2019728480.db");


        if(selectedUserType.equals("Student"))
        {
            StudentStaffNumber =StudentNumber;
        }
        else if(selectedUserType.equals("Staff"))
        {
            StudentStaffNumber =StaffNumber;
        }
        if(selectedNationality.equals("South African"))
        {
            IDPassportNumber = IDNumber;
        }
        else if(selectedNationality.equals("Non South African"))
        {
            IDPassportNumber = PassportNumber;
        }
        if (Password.isEmpty()) {
            Toast.makeText(this, "STUDENTSATA", Toast.LENGTH_SHORT).show();
            return;
        }


        String roleId = getRoleId("Client");  // Assuming all users are clients
        if (roleId ==null) {
            Toast.makeText(this, "role not found", Toast.LENGTH_SHORT).show();
            return;
        }
        String accountNumber = generateUniqueAccountNumber();

        String query = "INSERT INTO AspNetUsers (" +
                "Id, FirstName, LastName, Gender, DateOfBirth, " +
                "IDOrPassportNumber, Phone, EmployeeOrStudentNumber, UserType, MobilePassword, " +
                "UserName, NormalizedUserName, Email, NormalizedEmail, EmailConfirmed, PasswordHash, " +
                "SecurityStamp, ConcurrencyStamp, PhoneNumberConfirmed, TwoFactorEnabled, LockoutEnabled, " +
                "AccessFailedCount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // Use a PreparedStatement to safely insert data into the database
            PreparedStatement statement = dbConnection.getConnection().prepareStatement(query);
            statement.setString(1, UserId);
            statement.setString(2, FirstName);
            statement.setString(3, LastName);
            statement.setString(4, SelectedGender);
            statement.setString(5, DateofBith);
            statement.setString(6, IDPassportNumber);
            statement.setString(7, CellPhoneNumber);
            statement.setString(8, StudentStaffNumber);
            statement.setString(9, selectedUserType);
            statement.setString(10, Password);
            statement.setString(11, LastName + FirstName.substring(0, 1));
            statement.setString(12, (LastName + FirstName.substring(0, 1)).toUpperCase());
            statement.setString(13, Email);
            statement.setString(14, Email.toUpperCase());
            statement.setInt(15, 0); // EmailConfirmed
            statement.setString(16, Password); // PasswordHash (should be hashed for security)
            statement.setString(17, UUID.randomUUID().toString()); // SecurityStamp
            statement.setString(18, UUID.randomUUID().toString()); // ConcurrencyStamp
            statement.setInt(19, 0); // PhoneNumberConfirmed
            statement.setInt(20, 0); // TwoFactorEnabled
            statement.setInt(21, 1); // LockoutEnabled
            statement.setInt(22, 0); // AccessFailedCount

            int result = statement.executeUpdate();
            if (result > 0) {
                assignUserRole(UserId, roleId);
                createBankAccount(LastName + FirstName.substring(0, 1), accountNumber);
                Toast.makeText(this, "Account registered successfully!", Toast.LENGTH_SHORT).show();
                redirectToLogin();
            } else {
                Toast.makeText(this, "Account registration failed", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(RegisterActivity.this, "Error during registration", Toast.LENGTH_SHORT).show();
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
                    CellPhoneNumLayout.setError("Cell phone number should not be more than 9 characters!");
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

            }
        });
        //ConfirmPassword
        txtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String sConfirmPassword = charSequence.toString().trim();
                String password = txtPassword.getText().toString().trim();
                if(!sConfirmPassword.equals(password)){
                    ConfirmPasswordLayout.setError("Passwords do not match.");
                }
                else {
                    ConfirmPasswordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Password = editable.toString();

            }
        });
    }

    public void DisplayUserType()
    {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.user_type, android.R.layout.simple_dropdown_item_1line);
        UserTypeDropdown.setAdapter(adapter);

        UserTypeDropdown.setOnClickListener(v -> UserTypeDropdown.showDropDown());

        UserTypeDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedUserType = parent.getItemAtPosition(position).toString();
                handleUserTypeSelection(selectedUserType);
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

    private String generateUniqueAccountNumber()
    {
        Random random = new Random();
        String accountNumber;
        do {
            accountNumber = String.format("%010d", random.nextInt(1000000000));
        } while (accountNumberExists(accountNumber));
        return accountNumber;
    }

    private boolean accountNumberExists(String accountNumber)
    {
        Connection connection = dbConnection.getConnection();
        String query = "SELECT 1 FROM BankAccounts WHERE AccountNumber = '"+accountNumber+"'";
        try
        {
            ResultSet rst = dbConnection.getData(query);

            return rst.next();
        } catch (SQLException e)
        {
            e.printStackTrace();
            return true;
        }
    }

    private void createBankAccount(String accountHolder, String accountNumber) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        String query = "INSERT INTO BankAccounts (AccountHolder, AccountNumber, AccountType, AccountBalance, AccountOpenDate) " +
                "VALUES ('"+accountHolder+"', '"+accountNumber+"', 'Savings', 0.0, '"+format.format(date)+"')";
        try {
           Statement statement = dbConnection.getConnection().createStatement();
           statement.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void redirectToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LogInActivity.class);
        startActivity(intent);
        finish();
    }
    private void assignUserRole(String userId, String roleId) {
        String query = "INSERT INTO AspNetUserRoles (UserId, RoleId) VALUES ('"+userId+"','"+roleId+"')";

        try {
            Statement statement = dbConnection.getConnection().createStatement();
            statement.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }
    private String getRoleId(String roleName) {

        Connection connection = dbConnection.getConnection();
        String query = "SELECT Id FROM AspNetRoles WHERE Name = '"+roleName+"'";
        String roleId = null;

        if (connection == null) {
            System.err.println("Failed to establish a database connection.");
            return null;
        }

        try{
            ResultSet rst = dbConnection.getData(query);

            while (rst.next()) {
                roleId = rst.getString("Id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roleId;
    }



}