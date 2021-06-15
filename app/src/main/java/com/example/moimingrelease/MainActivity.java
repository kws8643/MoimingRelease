package com.example.moimingrelease;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moimingrelease.fragments_main.MoimingHomeFragment;
import com.example.moimingrelease.fragments_main.MoimingReserveFragment;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public final static String MAIN_TAG = "MAIN_LOG";
    public final static String MOIMING_USER_DATA_KEY = "current_moiming_user";

    public MoimingUserVO curMoimingUser;

    private ExtendedFloatingActionButton btnDutchpay, btnDutchpayOld, btnDutchpayNew;
    private View transparent_bg;
    //    private Animation main_fab_open, main_fab_close;

    // FAB Animations
//    private TranslateAnimation mainFabOpen, mainFabClose;
    private boolean isFabOpen = false;
//    private final int openYDelta = 100;
//    private final int closeYDelta = -100;


    private BottomNavigationView mainNavigation;
    private ImageView btnSetting, btnNotification, btnAdd, btnSearch;


    private FragmentManager fragmentManager;
    MoimingHomeFragment homeFragment;
    MoimingReserveFragment reserveFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mainNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int id = item.getItemId();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (id) {

                case R.id.btn_main_home:

                    fragmentTransaction.replace(R.id.frame_main, homeFragment).commit();

                    btnAdd.setVisibility(View.VISIBLE);
                    btnSearch.setVisibility(View.VISIBLE);

                    break;

                case R.id.btn_main_reserve:

                    fragmentTransaction.replace(R.id.frame_main, reserveFragment).commit();

                    btnAdd.setVisibility(View.GONE);
                    btnSearch.setVisibility(View.GONE);

                    break;

            }
            return true;
        }
    };

    private View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            int fabId = view.getId();

            switch (fabId) {

                case R.id.btn_dutchpay:
                    fabAction();
                    break;

                case R.id.btn_dutchpay_old:
                    Toast.makeText(getApplicationContext(), "기존 그룹원들과 더치페이를 합니다.", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.btn_dutchpay_new:
                    Toast.makeText(getApplicationContext(), "새 그룹원들과 더치페이를 합니다.", Toast.LENGTH_SHORT).show();
                    break;

            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initParams();

        //fab listeners
        btnDutchpay.setOnClickListener(fabListener);
        btnDutchpayNew.setOnClickListener(fabListener);
        btnDutchpayOld.setOnClickListener(fabListener);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toGroupCreation = new Intent(MainActivity.this, GroupCreationActivity.class);

                toGroupCreation.putExtra(MOIMING_USER_DATA_KEY, curMoimingUser);

                startActivity(toGroupCreation);
            }
        });


        mainNavigation.getMenu().getItem(1).setEnabled(false);

        mainNavigation.setOnNavigationItemSelectedListener(mainNavListener);
        mainNavigation.setSelectedItemId(R.id.btn_main_home);

    }

    private void initParams() {

//        /*// User Check. (통신 확인) // 나중에 삭제.
        Intent dataReceived = getIntent();
        curMoimingUser = (MoimingUserVO) dataReceived.getExtras().getSerializable("moiming_user");

/*
        String userName = "";

        if (activatedUser != null) {
            userName = activatedUser.getUserName();
        }
        Toast.makeText(getApplicationContext(), userName + ", " + activatedUser.getCreatedAt().toString(), Toast.LENGTH_LONG).show();
*/

        // 변수 확인
        fragmentManager = getSupportFragmentManager();

        // FG 정의
        homeFragment = new MoimingHomeFragment();
        reserveFragment = new MoimingReserveFragment();

        /*//  FAB 에니메이션 정의
        mainFabOpen = new TranslateAnimation(0, 0, 0, 0);
        mainFabClose = new TranslateAnimation(0, 0, 0, 0);
        mainFabOpen.setDuration(300);
        mainFabOpen.setFillAfter(true);
        mainFabClose.setDuration(300);
        mainFabClose.setFillAfter(true);


        mainFabOpen.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                CoordinatorLayout.LayoutParams newBtn = (CoordinatorLayout.LayoutParams) btnDutchpayNew.getLayoutParams();
                CoordinatorLayout.LayoutParams oldBtn = (CoordinatorLayout.LayoutParams) btnDutchpayOld.getLayoutParams();

                newBtn.bottomMargin += openYDelta;
                oldBtn.bottomMargin += openYDelta;

                btnDutchpayNew.setLayoutParams(newBtn);
                btnDutchpayOld.setLayoutParams(oldBtn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });


        mainFabClose.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                CoordinatorLayout.LayoutParams newBtn = (CoordinatorLayout.LayoutParams) btnDutchpayNew.getLayoutParams();
                CoordinatorLayout.LayoutParams oldBtn = (CoordinatorLayout.LayoutParams) btnDutchpayOld.getLayoutParams();

                newBtn.bottomMargin += closeYDelta;
                oldBtn.bottomMargin += closeYDelta;

                btnDutchpayNew.setLayoutParams(newBtn);
                btnDutchpayOld.setLayoutParams(oldBtn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });*/

    }



    private void initView() {

        mainNavigation = findViewById(R.id.main_navigation);

        // FAB Settings
        btnDutchpay = findViewById(R.id.btn_dutchpay);
        btnDutchpayNew = findViewById(R.id.btn_dutchpay_new);
        btnDutchpayNew.setClickable(false);
        btnDutchpayOld = findViewById(R.id.btn_dutchpay_old);
        btnDutchpayOld.setClickable(false);

        transparent_bg = findViewById(R.id.transparent_bg);
        transparent_bg.bringToFront(); // Fade 효과를 주기 위함.

        // Upper Btns
        btnSetting = findViewById(R.id.btn_setting);
        btnNotification = findViewById(R.id.btn_notification);
        btnAdd = findViewById(R.id.btn_add);
        btnSearch = findViewById(R.id.btn_search);

    }

    private void fabAction() {

        if (isFabOpen) {

//            btnDutchpayOld.startAnimation(mainFabOpen);
//            btnDutchpayNew.startAnimation(mainFabOpen);

            transparent_bg.setVisibility(View.GONE);

            btnDutchpayOld.setClickable(false);
            btnDutchpayOld.setVisibility(View.GONE);
            btnDutchpayNew.setClickable(false);
            btnDutchpayNew.setVisibility(View.GONE);


            isFabOpen = false;

        } else {

//            btnDutchpayOld.startAnimation(mainFabOpen);
//            btnDutchpayNew.startAnimation(mainFabClose);

            transparent_bg.setVisibility(View.VISIBLE);

            btnDutchpayOld.setClickable(true);
            btnDutchpayOld.setVisibility(View.VISIBLE);
            btnDutchpayNew.setClickable(true);
            btnDutchpayNew.setVisibility(View.VISIBLE);

            isFabOpen = true;
        }
    }
}