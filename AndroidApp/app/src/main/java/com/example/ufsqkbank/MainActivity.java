package com.example.ufsqkbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.ufsqkbank.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.ufsqkbank.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    ImageView imageView;
    TextView txtUserName, txtEmail;
    Intent intent;
    Bundle bundle;

    String userId;
    byte[] profile;
    String lastName;
    String gender;
    String userEmail;
    String userName;
    String role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        imageView = findViewById(R.id.profileImage);
        txtUserName = findViewById(R.id.userName);
        txtEmail = findViewById(R.id.userEmail);
        intent = getIntent();
        bundle = intent.getExtras();
        if(bundle!=null)
        {
            userId = bundle.getString("userId");
            profile = bundle.getByteArray("profile");
            lastName = bundle.getString("lstname");
            gender= bundle.getString("mrms");
            userEmail= bundle.getString("email");
            userName = bundle.getString("username");
            role = bundle.getString("role");
        }


        if(role.equals("Client"))
        {
            if (profile != null && profile.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(profile, 0, profile.length);

                imageView.setImageBitmap(bitmap);
                txtUserName.setText("Welcome, "+gender+" "+userName);
                txtEmail.setText(userEmail);
            }
            else {
                txtUserName.setText("Welcome, "+gender+" "+userName);
                txtEmail.setText(userEmail);
            }

        }
        else
        {
            txtUserName.setText("Welcome, "+userName);
            txtEmail.setText(role);
        }


        HomeFragment homeFragment = HomeFragment.newInstance(userId,userName);
        replaceFragment(homeFragment);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.homeIcon) {
                replaceFragment(homeFragment);
            } else if (itemId == R.id.ProfileIcon) {
                ProfileFragment profileFragment = ProfileFragment.newInstance(userId);
                replaceFragment(profileFragment);

            } else if (itemId == R.id.NotificationIcon) {
                NotificationFragment notificationFragment = NotificationFragment.newInstance(userId);
                replaceFragment(notificationFragment);
            } else {
                return false;  // No matching item
            }
            return true;  // Successful item selection
        });

    }
    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.middleFrame,fragment);
        fragmentTransaction.commit();

    }
}