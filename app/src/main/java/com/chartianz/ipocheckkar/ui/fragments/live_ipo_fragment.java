package com.chartianz.ipocheckkar.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.TransitionManager;

import com.chartianz.ipocheckkar.R;
import com.chartianz.ipocheckkar.databinding.FragmentLiveIpoBinding;
import com.chartianz.ipocheckkar.utils.SwipeDistance;
import com.chartianz.ipocheckkar.network.GetLiveIPOFeed;
import com.google.android.material.transition.MaterialFadeThrough;

public class live_ipo_fragment extends Fragment {

    public static FragmentLiveIpoBinding binding;
    Context context;

    public live_ipo_fragment() {
        // Required empty public constructor
    }

    public static live_ipo_fragment newInstance(String param1, String param2) {
        live_ipo_fragment fragment = new live_ipo_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLiveIpoBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        getFileFeeds(context);
    }

    private void init(){
        context = getContext();

        //Swipe Refresh Layout Distance
        int distanceInDp = 400;
        int distanceInPixels = SwipeDistance.dpToPixels(context, distanceInDp);
        binding.liveIpoRefreshLayout.setDistanceToTriggerSync(distanceInPixels);

        //Recycler View
        binding.liveIpoRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.background));
        binding.liveIpoRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary));

        binding.liveIpoRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFileFeeds(context);

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TransitionManager.beginDelayedTransition(binding.liveIpoMainLayout, new MaterialFadeThrough());
                        binding.recyclerViewLiveIpoFeed.setVisibility(View.INVISIBLE);
                        live_ipo_fragment.binding.noLiveDataLayout.setVisibility(View.INVISIBLE);
                        binding.shimmerIpoList.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    void getFileFeeds(Context context) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.recyclerViewLiveIpoFeed.setLayoutManager(linearLayoutManager);

        new Thread(new Runnable() {
            @Override
            public void run() {
                new GetLiveIPOFeed(context).execute();
            }
        }).start();
    }
}