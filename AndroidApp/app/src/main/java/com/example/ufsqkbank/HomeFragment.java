package com.example.ufsqkbank;

import android.content.ClipData;
import android.content.Intent;
import android.media.RouteListingPreference;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_userId = "userId";
    private static final String ARG_username = "userName";

    // TODO: Rename and change types of parameters
    private String userId;
    private String mUserName;
    private ImageView imgViewBal, imgAccDetails, imgPay, imgPernalInfor, imgfeedback;


    public HomeFragment() {
        // Required empty public constructor

    }


    public static HomeFragment newInstance(String userId, String username) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_userId, userId);
        args.putString(ARG_username, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
             userId= getArguments().getString(ARG_userId);
            mUserName = getArguments().getString(ARG_username);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imgViewBal = view.findViewById(R.id.imgViewBal);
        imgAccDetails = view.findViewById(R.id.imgAccountDetails);
        imgPay = view.findViewById(R.id.imgPayment);
        imgPernalInfor = view.findViewById(R.id.imgPersonal);
        imgfeedback = view.findViewById(R.id.imgFeeback);
        if(userId !=null && mUserName!= null)
        {
            imgPernalInfor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileFragment profileFragment = ProfileFragment.newInstance(userId);
                    replaceFragment(profileFragment);
                }
            });


            imgViewBal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ViewBalanceActivity.class);
                    intent.putExtra("userId",userId);
                    intent.putExtra("username",mUserName);
                    startActivity(intent);
                }
            });
            imgAccDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), AccountIforActivity.class);
                    intent.putExtra("userId",userId);
                    intent.putExtra("username",mUserName);
                    startActivity(intent);
                }
            });
            imgPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), TransfareFunds.class);
                    intent.putExtra("userId",userId);
                    intent.putExtra("username",mUserName);
                    startActivity(intent);

                }
            });
            imgfeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), FeedBackActivity.class);
                    intent.putExtra("userId",userId);
                    intent.putExtra("username",mUserName);
                    startActivity(intent);
                }
            });

        }
        else {
            Toast.makeText(getContext(), "Id and Username are empty", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.middleFrame,fragment);
        fragmentTransaction.commit();

    }
}