package com.chartianz.ipocheckkar.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;

import com.chartianz.ipocheckkar.Constants;
import com.chartianz.ipocheckkar.R;
import com.chartianz.ipocheckkar.adapter.IPOFeedAdapter;
import com.chartianz.ipocheckkar.modal.IPOInfo;
import com.chartianz.ipocheckkar.network.GetListedIPOFeed;
import com.chartianz.ipocheckkar.network.GetLiveIPOFeed;
import com.chartianz.ipocheckkar.ui.bottomsheets.NoInternetBottomsheet;
import com.chartianz.ipocheckkar.ui.bottomsheets.SortListBottomsheet;
import com.chartianz.ipocheckkar.ui.fragments.listed_ipo_fragment;
import com.chartianz.ipocheckkar.ui.fragments.live_ipo_fragment;
import com.chartianz.ipocheckkar.utils.CheckInternet;
import com.chartianz.ipocheckkar.utils.KeyboardUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.transition.MaterialElevationScale;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewException;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.model.ReviewErrorCode;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;

import java.util.ArrayList;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class ControllerActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
    public static com.chartianz.ipocheckkar.databinding.ActivityControllerBinding binding;
    static Context context;
    Activity activity;
    public static ArrayList<IPOInfo> ipoInfoArrayList = new ArrayList<>();
    public static ArrayList<IPOInfo> ipoInfoArrayList2 = new ArrayList<>();
    private AppUpdateManager mAppUpdateManager;
    public static FirebaseAnalytics mFirebaseAnalytics;
    private ReviewManager reviewManager;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.chartianz.ipocheckkar.databinding.ActivityControllerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        checkInAppUpdate();
        initFirebaseAnalytics();
        initOneSignal();
        setNavigation();
        setSearchIPO();
        if(!new CheckInternet().isInternet(context)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new NoInternetBottomsheet().Show(ControllerActivity.this);
                }
            });
        }
    }

    private void init() {
        context = this;
        activity = this;

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        reviewManager = ReviewManagerFactory.create(this);
        requestAndShowReview();

        binding.moreOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                SortListBottomsheet sortListBottomsheet = new SortListBottomsheet();
                sortListBottomsheet.Show(activity);
            }
        });
    }

    private void requestAndShowReview() {
        Task <ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                Task <Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {

                });
            }
        });
    }

    private void checkInAppUpdate() {
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(result,AppUpdateType.IMMEDIATE, ControllerActivity.this,100);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initOneSignal() {
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        OneSignal.initWithContext(this, Constants.ONESIGNAL_ID);

        OneSignal.getNotifications().requestPermission(true, Continue.with(r -> {
            if (r.isSuccess()) {
                if (r.getData()) {
                    // `requestPermission` completed successfully and the user has accepted permission
                }
                else {
                    // `requestPermission` completed successfully but the user has rejected permission
                }
            }
            else {
                // `requestPermission` completed unsuccessfully, check `r.getThrowable()` for more info on the failure reason
            }
        }));
    }

    private static void initFirebaseAnalytics() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    private void setNavigation() {
        binding.mainPageView.setAdapter(new MainPageViewAdapter(getSupportFragmentManager()));
        binding.mainPageView.setOffscreenPageLimit(1);

        binding.bottomNav.setOnNavigationItemSelectedListener(this);
        binding.mainPageView.addOnPageChangeListener(this);
    }

    private void setSearchIPO() {
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(binding.searchBtn.getVisibility() == View.VISIBLE){
                            TransitionManager.beginDelayedTransition(binding.mainLayout, new MaterialElevationScale(true));
                            binding.searchBtn.setVisibility(View.INVISIBLE);
                            binding.appbarMainText.setVisibility(View.INVISIBLE);
                            binding.searchIpoET.setVisibility(View.VISIBLE);
                            binding.imageView.setVisibility(View.VISIBLE);
                            binding.searchIpoET.requestFocus();
                            KeyboardUtils.showKeyboard(ControllerActivity.this, binding.searchIpoET);
                        }else {
                            TransitionManager.beginDelayedTransition(binding.mainLayout, new MaterialElevationScale(false));
                            binding.searchBtn.setVisibility(View.VISIBLE);
                            binding.appbarMainText.setVisibility(View.VISIBLE);
                            binding.searchIpoET.setVisibility(View.GONE);
                            binding.imageView.setVisibility(View.GONE);
                        }

                    }
                });

                binding.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(binding.searchBtn.getVisibility() == View.VISIBLE){
                                    TransitionManager.beginDelayedTransition(binding.mainLayout, new MaterialElevationScale(true));
                                    binding.searchBtn.setVisibility(View.INVISIBLE);
                                    binding.appbarMainText.setVisibility(View.INVISIBLE);
                                    binding.searchIpoET.setVisibility(View.VISIBLE);
                                    binding.imageView.setVisibility(View.VISIBLE);
                                }else {
                                    TransitionManager.beginDelayedTransition(binding.mainLayout, new MaterialElevationScale(false));
                                    binding.searchBtn.setVisibility(View.VISIBLE);
                                    binding.appbarMainText.setVisibility(View.VISIBLE);
                                    binding.searchIpoET.setVisibility(View.GONE);
                                    binding.imageView.setVisibility(View.GONE);
                                    KeyboardUtils.hideKeyboard(ControllerActivity.this, binding.searchIpoET);
                                    binding.searchIpoET.setText("");
                                }
                            }
                        });

                    }
                });

                binding.searchIpoET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });
    }

    private void filter(String text) {
        ArrayList<IPOInfo> filteredList = new ArrayList<>();

        if(binding.bottomNav.getSelectedItemId() == R.id.upcoming_ipo_fragment){
            if(text.isEmpty()){
                GetLiveIPOFeed.ipoFeedAdapter = new IPOFeedAdapter(context, ipoInfoArrayList);
                GetLiveIPOFeed.ipoFeedAdapter.setHasStableIds(false);

                live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setItemViewCacheSize(ipoInfoArrayList.size());
                live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setAdapter(GetLiveIPOFeed.ipoFeedAdapter);
                return;
            }

            for (IPOInfo item : ipoInfoArrayList) {
                if (item.ipo_full_name.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }

            for (IPOInfo item : ipoInfoArrayList2) {
                if (item.ipo_full_name.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }

            GetLiveIPOFeed.ipoFeedAdapter = new IPOFeedAdapter(context, filteredList);
            GetLiveIPOFeed.ipoFeedAdapter.setHasStableIds(false);

            live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setItemViewCacheSize(filteredList.size());
            live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setAdapter(GetLiveIPOFeed.ipoFeedAdapter);
        }else if(binding.bottomNav.getSelectedItemId() == R.id.listed_ipo_fragment){
            if(text.isEmpty()){
                GetListedIPOFeed.ipoFeedAdapter = new IPOFeedAdapter(context, ipoInfoArrayList2);
                GetListedIPOFeed.ipoFeedAdapter.setHasStableIds(false);

                listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setItemViewCacheSize(ipoInfoArrayList2.size());
                listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setAdapter(GetListedIPOFeed.ipoFeedAdapter);
                return;
            }

            for (IPOInfo item : ipoInfoArrayList) {
                if (item.ipo_full_name.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }

            for (IPOInfo item : ipoInfoArrayList2) {
                if (item.ipo_full_name.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }

            GetListedIPOFeed.ipoFeedAdapter = new IPOFeedAdapter(context, filteredList);
            GetListedIPOFeed.ipoFeedAdapter.setHasStableIds(false);

            listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setItemViewCacheSize(filteredList.size());
            listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setAdapter(GetListedIPOFeed.ipoFeedAdapter);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.upcoming_ipo_fragment) {
            binding.mainPageView.setCurrentItem(0);
        } else if (item.getItemId() == R.id.listed_ipo_fragment) {
            binding.mainPageView.setCurrentItem(1);
        }

        return true; // Suggestion 1: Return true when a navigation item is selected
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        switch (position) {
            case 0:
                //binding.appbarMainText.setText("Main Board IPO");
                break;
            case 1:
                //binding.appbarMainText.setText("SME IPO");
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                binding.bottomNav.getMenu().findItem(R.id.upcoming_ipo_fragment).setChecked(true);
                break;
            case 1:
                binding.bottomNav.getMenu().findItem(R.id.listed_ipo_fragment).setChecked(true);
                break;
        }

        binding.mainPageView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); // Suggestion 2: Use HapticFeedbackConstants.VIRTUAL_KEY for better readability
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class MainPageViewAdapter extends FragmentStatePagerAdapter { // Suggestion 4: Use FragmentStatePagerAdapter instead of FragmentPagerAdapter
        public MainPageViewAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return new listed_ipo_fragment();
                    //return new subscription();
                default:
                    return new live_ipo_fragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public void onBackPressed() {

        if(binding.searchBtn.getVisibility() == View.INVISIBLE){
            TransitionManager.beginDelayedTransition(binding.mainLayout, new MaterialElevationScale(false));
            binding.searchBtn.setVisibility(View.VISIBLE);
            binding.appbarMainText.setVisibility(View.VISIBLE);
            binding.searchIpoET.setVisibility(View.GONE);
            binding.imageView.setVisibility(View.GONE);
            binding.searchIpoET.setText("");
        }else{
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this)
                    .setTitle("Exit?")
                    .setMessage("Are you sure, you want to Exit?")
                    .setCancelable(false)
                    .setPositiveButton("Exit", R.drawable.ic_ok, new BottomSheetMaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                            overridePendingTransition(R.anim.scale_xy_enter, R.anim.scale_xy_exit);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", R.drawable.ic_close, new BottomSheetMaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    })
                    .build();
            mBottomSheetDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(result,AppUpdateType.IMMEDIATE, ControllerActivity.this,100);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}