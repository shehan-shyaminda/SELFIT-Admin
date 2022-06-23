package com.codelabs.selfit_admin.Views.splash;

import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codelabs.selfit_admin.Views.BaseActivity;
import com.codelabs.selfit_admin.Views.authentication.LoginActivity;
import com.codelabs.selfit_admin.Views.authentication.RegisterActivity;
import com.codelabs.selfit_admin.adapters.OnboardSliderAdapter;
import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.helpers.SharedPreferencesManager;

public class OnBoardingActivity extends BaseActivity {


    private SharedPreferencesManager sharedPreferencesManager;

    private ViewPager viewPager;
    private LinearLayout mDotLayout;
    private OnboardSliderAdapter onboardSliderAdapter;
    private Button btnFinish;

    private TextView[] dots;
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        mDotLayout = findViewById(R.id.dotsLayout);
        btnFinish = findViewById(R.id.btn_finish);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        onboardSliderAdapter = new OnboardSliderAdapter(this);
        viewPager.setAdapter(onboardSliderAdapter);
        addDotsIndicators(0);
        viewPager.addOnPageChangeListener(viewListener);

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferencesManager.savePreferences(SharedPreferencesManager.IS_DONE_TUTORIAL, true);
                startActivity(new Intent(OnBoardingActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });

//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mCurrentPage == dots.length - 1) {
//                    if(chb_dontShow.isChecked()){
//                        sharedPreferencesManager.savePreferences(SharedPreferencesManager.IS_DONE_TUTORIAL,true);
//                    }
//
//                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.presidentsoffice.gov.lk/index.php/vaccination-dashboard/"));
//                    startActivity(i);
//                    ActivityCompat.finishAfterTransition(OnBoardingActivity.this);
//                } else {
//                    viewPager.setCurrentItem(mCurrentPage + 1);
//                }
//            }
//        });
//
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewPager.setCurrentItem(mCurrentPage - 1);
//            }
//        });
    }

    public void addDotsIndicators(int position) {
        dots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8211;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorPrimaryVariant));

            mDotLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicators(position);
            mCurrentPage = position;
            if (position == dots.length - 1) {
                btnFinish.setEnabled(true);
                btnFinish.setVisibility(View.VISIBLE);
            } else {
                btnFinish.setEnabled(false);
                btnFinish.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}