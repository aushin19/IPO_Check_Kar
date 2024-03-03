package com.chartianz.ipocheckkar.ui.bottomsheets;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.transition.TransitionManager;

import com.chartianz.ipocheckkar.R;
import com.chartianz.ipocheckkar.databinding.BottomsheetMoreOptionBinding;
import com.chartianz.ipocheckkar.network.GetListedIPOFeed;
import com.chartianz.ipocheckkar.network.GetLiveIPOFeed;
import com.chartianz.ipocheckkar.ui.activity.ControllerActivity;
import com.chartianz.ipocheckkar.ui.fragments.listed_ipo_fragment;
import com.chartianz.ipocheckkar.ui.fragments.live_ipo_fragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.transition.MaterialFadeThrough;

public class SortListBottomsheet {

    private BottomSheetDialog dialog;
    private BottomsheetMoreOptionBinding binding;
    private Activity activity;

    private static final String PREF_NAME = "SortListBottomsheetPrefs";
    private static final String MAIN_FIRST_STATE = "mainFirstState";
    private static final String SME_FIRST_STATE = "smeFirstState";

    public void Show(Activity activity) {
        this.activity = activity;
        dialog = new BottomSheetDialog(activity, R.style.BottomSheetTheme);
        dialog.setCancelable(true);
        dialog.setDismissWithAnimation(true);

        binding = BottomsheetMoreOptionBinding.inflate(LayoutInflater.from(activity));
        dialog.setContentView(binding.getRoot());

        loadSavedStates();

        binding.mainFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ControllerActivity.binding.bottomNav.getSelectedItemId() == R.id.upcoming_ipo_fragment){
                    playAnimationLiveRCV();
                    GetLiveIPOFeed.sortMainIPOFirst();
                } else if(ControllerActivity.binding.bottomNav.getSelectedItemId() == R.id.listed_ipo_fragment){
                    playAnimationListedRCV();
                    GetListedIPOFeed.sortMainIPOFirst();
                }

                handleStateChange(binding.mainFirst, binding.smeFirst);
                saveStates();
            }
        });

        binding.smeFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ControllerActivity.binding.bottomNav.getSelectedItemId() == R.id.upcoming_ipo_fragment){
                    playAnimationLiveRCV();
                    GetLiveIPOFeed.sortSMEIPOFirst();
                } else if(ControllerActivity.binding.bottomNav.getSelectedItemId() == R.id.listed_ipo_fragment){
                    playAnimationListedRCV();
                    GetListedIPOFeed.sortSMEIPOFirst();
                }

                handleStateChange(binding.smeFirst, binding.mainFirst);
                saveStates();
            }
        });

        if (!activity.isFinishing()) {
            dialog.show();
        }
    }

    void playAnimationLiveRCV(){
        (activity).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TransitionManager.beginDelayedTransition(live_ipo_fragment.binding.liveIpoMainLayout, new MaterialFadeThrough());
                live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setVisibility(View.INVISIBLE);
                live_ipo_fragment.binding.shimmerIpoList.setVisibility(View.VISIBLE);
            }
        });
    }
    
    void playAnimationListedRCV(){
        (activity).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TransitionManager.beginDelayedTransition(listed_ipo_fragment.binding.listedIpoMainLayout, new MaterialFadeThrough());
                listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setVisibility(View.INVISIBLE);
                listed_ipo_fragment.binding.shimmerIpoList.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleStateChange(TextView selectedView, TextView otherView) {
        Drawable checkDrawable = activity.getResources().getDrawable(R.drawable.ic_ok);
        Drawable emptyDrawable = null;

        if (selectedView.getCompoundDrawablesRelative()[2] == null) {
            selectedView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, checkDrawable, null);
            otherView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, emptyDrawable, null);
        } else {
            selectedView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, emptyDrawable, null);
            if(ControllerActivity.binding.bottomNav.getSelectedItemId() == R.id.upcoming_ipo_fragment){
                playAnimationListedRCV();
                GetLiveIPOFeed.sortNormalList();
            } else if(ControllerActivity.binding.bottomNav.getSelectedItemId() == R.id.listed_ipo_fragment){
                playAnimationListedRCV();
                GetListedIPOFeed.sortNormalList();
            }
        }
    }

    private void saveStates() {
        SharedPreferences prefs = dialog.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(MAIN_FIRST_STATE, binding.mainFirst.getCompoundDrawables()[2] != null);
        editor.putBoolean(SME_FIRST_STATE, binding.smeFirst.getCompoundDrawables()[2] != null);
        editor.apply();
    }

    private void loadSavedStates() {
        SharedPreferences prefs = dialog.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean mainFirstState = prefs.getBoolean(MAIN_FIRST_STATE, false);
        boolean smeFirstState = prefs.getBoolean(SME_FIRST_STATE, false);

        if (mainFirstState) {
            binding.mainFirst.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ok, 0);
            binding.smeFirst.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else if (smeFirstState) {
            binding.smeFirst.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ok, 0);
            binding.mainFirst.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    public void dismiss(Context context) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }
}

