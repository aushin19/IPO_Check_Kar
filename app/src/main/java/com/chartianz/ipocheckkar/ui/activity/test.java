package com.chartianz.ipocheckkar.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.chartianz.ipocheckkar.adapter.IPODetailsPageAdapter;
import com.chartianz.ipocheckkar.databinding.ActivityTestBinding;
import com.chartianz.ipocheckkar.modal.IPOInfo;
import com.chartianz.ipocheckkar.ui.fragments.detailed_fragments.subscription;

import java.util.ArrayList;
import java.util.List;

public class test extends AppCompatActivity {
    private ActivityTestBinding binding;
    IPOInfo ipoInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<IPOInfo> ipoInfoArrayList = intent.getParcelableArrayListExtra("ipoInfoArrayList");
            if (ipoInfoArrayList != null) {
                ipoInfo = ipoInfoArrayList.get(intent.getIntExtra("position", 0));
            }
        }

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new subscription());

        IPODetailsPageAdapter adapter = new IPODetailsPageAdapter(getSupportFragmentManager(), fragments, ipoInfo);
        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.tabLayout.getTabAt(0).setText("Subscription");
    }
}