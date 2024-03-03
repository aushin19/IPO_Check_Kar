package com.chartianz.ipocheckkar.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chartianz.ipocheckkar.Constants;
import com.chartianz.ipocheckkar.R;
import com.chartianz.ipocheckkar.adapter.onBoardingAdapter;
import com.chartianz.ipocheckkar.databinding.ActivityOnboardingBinding;
import com.chartianz.ipocheckkar.utils.TinyDB;

public class Onboarding extends AppCompatActivity {
    ActivityOnboardingBinding binding;
    int[] images = {R.drawable.illustration_tracking, R.drawable.illustration_updates, R.drawable.illustration_detailed};
    String[] subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        TinyDB tinyDB = new TinyDB(this);

        subtitle = new String[]{"Track MAINLINE and SME IPOs\nOn the GO!",
                "Faster IPO Updates With\nNotifications & Alert",
                "Detailed IPO Information\nWith GMP & LIVE Subscription Status"};

        onBoardingAdapter onBoardingAdapter = new onBoardingAdapter(this, images, subtitle);
        binding.onboardingVP.setAdapter(onBoardingAdapter);

        binding.pageIndicator.setViewPager(binding.onboardingVP);

        binding.getStartedCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.privacyCheckBox.isChecked() && binding.termsCheckBox.isChecked()){
                    tinyDB.putBoolean("isFirstTime", true);
                    startActivity(new Intent(Onboarding.this, ControllerActivity.class));
                    finish();
                }else{
                    Toast.makeText(Onboarding.this, "Make sure, your are agree to everything above!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.termsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_TERMS_LINK));
                startActivity(browserIntent);
            }
        });

        binding.privacyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_PRIVACY_LINK));
                startActivity(browserIntent);
            }
        });
    }

}