package com.example.ufsqkbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdminConsaltantActivity2 extends AppCompatActivity {

    ImageView imgManageClients, imgManageStaff, imgtransfare;
    LinearLayout txtmanage, txtimgtransfare;
    TextView txtusername,txtemail,txtrole;
    Intent intent;
    String role,username, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        role = intent.getStringExtra("role");
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");
        setContentView(R.layout.activity_admin_consaltant2);

        imgManageClients = findViewById(R.id.imgManageCient);
        imgManageStaff = findViewById(R.id.imgBankStaff);
        imgtransfare = findViewById(R.id.imgPayment);

        txtmanage = findViewById(R.id.txtmanangeStaff);
        txtimgtransfare = findViewById(R.id.txtTransaction);
        txtrole = findViewById(R.id.role);

        txtusername = findViewById(R.id.adConsuserName);
        txtemail = findViewById(R.id.adConsuserEmail);
        txtrole.setText(role);
        txtusername.setText("Welcome, "+username);
        txtemail.setText(email);

        imgManageClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(AdminConsaltantActivity2.this, ManageActivity.class);
                startActivity(intent);

            }
        });

        if(role.equals("Admin"))
        {
            txtmanage.setVisibility(View.VISIBLE);
            imgManageStaff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(AdminConsaltantActivity2.this, ManageStaffActivity.class);
                    startActivity(intent);
                }
            });


        }else
        {
            txtimgtransfare.setVisibility(View.VISIBLE);
            imgtransfare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(AdminConsaltantActivity2.this, MakeTransacActivity.class);
                    startActivity(intent);
                }
            });

        }





    }
}