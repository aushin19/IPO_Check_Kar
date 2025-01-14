package com.chartianz.ipocheckkar.network;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.transition.TransitionManager;

import com.chartianz.ipocheckkar.BuildConfig;
import com.chartianz.ipocheckkar.adapter.IPOFeedAdapter;
import com.chartianz.ipocheckkar.modal.IPOInfo;
import com.chartianz.ipocheckkar.ui.activity.ControllerActivity;
import com.chartianz.ipocheckkar.ui.fragments.live_ipo_fragment;
import com.chartianz.ipocheckkar.ui.snackbar.CustomSnackbar;
import com.google.android.material.transition.MaterialFadeThrough;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GetLiveIPOFeed extends AsyncTask<Void, Void, Void> {
    static Context context;
    static ArrayList<IPOInfo> ipoFeedModalArrayList = new ArrayList<>();
    public static IPOFeedAdapter ipoFeedAdapter;
    String content;

    public GetLiveIPOFeed(Context context) {
        this.context = context;
        ControllerActivity.ipoInfoArrayList.clear();
        ipoFeedModalArrayList.clear();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Document document;
            document = Jsoup.connect(BuildConfig.IPO_API_KEY).get();

            Elements element = document.getElementsByClass("ipo_details");

            content = Jsoup.parse(String.valueOf(element)).text();

            if (content.length() != 0) {
                try {
                    JSONArray jsonArray = new JSONArray(content);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if(jsonArray.getJSONObject(i).getString("ipo_status").equals("Upcoming") || jsonArray.getJSONObject(i).getString("ipo_status").equals("Open")
                                || jsonArray.getJSONObject(i).getString("ipo_status").equals("Closed")){
                            addToList(jsonArray, i);
                        }
                    }

                    ControllerActivity.ipoInfoArrayList = ipoFeedModalArrayList;
                } catch (JSONException e) {
                    Log.d("GetLiveIPOFeed", e.getMessage());
                }
            }else{
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showNoDataLayout();
                    }
                });
            }

        } catch (IOException e) {
            Log.d("GetLiveIPOFeed", e.getMessage());
        }
        return null;
    }

    void addToList(JSONArray jsonArray, int i) {
        try {
            int max_price;
            if(jsonArray.getJSONObject(i).getString("ipo_max_price").equals("NA"))
                max_price = 0;
            else
                max_price = jsonArray.getJSONObject(i).getInt("ipo_max_price");
            ipoFeedModalArrayList.add(new IPOInfo(
                    jsonArray.getJSONObject(i).getString("ipo_full_name"),
                    jsonArray.getJSONObject(i).getString("ipo_company_url"),
                    jsonArray.getJSONObject(i).getString("ipo_company_logo"),
                    jsonArray.getJSONObject(i).getString("ipo_open_date"),
                    jsonArray.getJSONObject(i).getString("ipo_close_date"),
                    jsonArray.getJSONObject(i).getString("ipo_allotment_date"),
                    jsonArray.getJSONObject(i).getString("ipo_listing_date"),
                    jsonArray.getJSONObject(i).getString("ipo_size"),
                    jsonArray.getJSONObject(i).getString("ipo_subs"),
                    jsonArray.getJSONObject(i).getString("ipo_status"),
                    jsonArray.getJSONObject(i).getString("ipo_last_update"),
                    jsonArray.getJSONObject(i).getString("ipo_allotment_status"),
                    jsonArray.getJSONObject(i).getString("ipo_registrar"),
                    jsonArray.getJSONObject(i).getInt("ipo_gmp"),
                    max_price,
                    (float) jsonArray.getJSONObject(i).getDouble("ipo_listing_rate"),
                    jsonArray.getJSONObject(i).getString("details_about_company"),
                    jsonArray.getJSONObject(i).getJSONArray("ipo_lot_info"),
                    jsonArray.getJSONObject(i).getJSONObject("ipo_subs_json"),
                    jsonArray.getJSONObject(i).getJSONObject("ipo_fin_info"),
                    jsonArray.getJSONObject(i).getJSONArray("ipo_issue_details"),
                    jsonArray.getJSONObject(i).getJSONObject("ipo_gmp_details")
            ));
        } catch (JSONException e) {
            Log.d("GetLiveIPOFeed", e.getMessage());
        }
    }


    @Override
    protected void onPostExecute(Void unused) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (ipoFeedModalArrayList.size() != 0) {
                    ipoFeedAdapter = new IPOFeedAdapter(context, ipoFeedModalArrayList);
                    ipoFeedAdapter.setHasStableIds(false);

                    live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setItemViewCacheSize(ipoFeedModalArrayList.size());
                    live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setAdapter(ipoFeedAdapter);

                    playAnimationRCV();

                    if(live_ipo_fragment.binding.liveIpoRefreshLayout.isRefreshing()){
                        live_ipo_fragment.binding.liveIpoRefreshLayout.performHapticFeedback(1);
                        live_ipo_fragment.binding.liveIpoRefreshLayout.setRefreshing(false);
                        new CustomSnackbar().showSnackbar("Last updated on "+ipoFeedModalArrayList.get(0).ipo_last_update,
                                live_ipo_fragment.binding.getRoot());
                    }
                } else {
                    showNoDataLayout();
                }
            }
        });
    }

    public static void sortMainIPOFirst(){
        ArrayList<IPOInfo> ipoFeedModalArrayList_temp = new ArrayList<>(ipoFeedModalArrayList);

        Collections.sort(ipoFeedModalArrayList_temp, new Comparator<IPOInfo>() {
            @Override
            public int compare(IPOInfo ipoInfo1, IPOInfo ipoInfo2) {
                if (ipoInfo1.ipo_full_name.contains("SME") && !ipoInfo2.ipo_full_name.contains("SME")) {
                    return 1;
                } else if (!ipoInfo1.ipo_full_name.contains("SME") && ipoInfo2.ipo_full_name.contains("SME")) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        ipoFeedAdapter = new IPOFeedAdapter(context, ipoFeedModalArrayList_temp);
        ipoFeedAdapter.setHasStableIds(false);

        live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setItemViewCacheSize(ipoFeedModalArrayList_temp.size());
        live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setAdapter(ipoFeedAdapter);

        playAnimationRCV();
    }

    public static void sortSMEIPOFirst(){
        ArrayList<IPOInfo> ipoFeedModalArrayList_temp = new ArrayList<>(ipoFeedModalArrayList);

        Collections.sort(ipoFeedModalArrayList_temp, new Comparator<IPOInfo>() {
            @Override
            public int compare(IPOInfo ipoInfo1, IPOInfo ipoInfo2) {
                if (ipoInfo1.ipo_full_name.contains("SME") && !ipoInfo2.ipo_full_name.contains("SME")) {
                    return -1;
                } else if (!ipoInfo1.ipo_full_name.contains("SME") && ipoInfo2.ipo_full_name.contains("SME")) {
                    return 1;
                } else {
                    return ipoInfo1.ipo_full_name.compareToIgnoreCase(ipoInfo2.ipo_full_name);
                }
            }
        });

        ipoFeedAdapter = new IPOFeedAdapter(context, ipoFeedModalArrayList_temp);
        ipoFeedAdapter.setHasStableIds(false);

        live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setItemViewCacheSize(ipoFeedModalArrayList_temp.size());
        live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setAdapter(ipoFeedAdapter);

        playAnimationRCV();
    }

    public static void sortNormalList(){
        ipoFeedAdapter = new IPOFeedAdapter(context, ipoFeedModalArrayList);
        ipoFeedAdapter.setHasStableIds(false);

        live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setItemViewCacheSize(ipoFeedModalArrayList.size());
        live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setAdapter(ipoFeedAdapter);

        playAnimationRCV();
    }

    void showNoDataLayout(){
        TransitionManager.beginDelayedTransition(live_ipo_fragment.binding.liveIpoMainLayout, new MaterialFadeThrough());
        live_ipo_fragment.binding.shimmerIpoList.setVisibility(View.INVISIBLE);
        live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setVisibility(View.INVISIBLE);
        live_ipo_fragment.binding.noLiveDataLayout.setVisibility(View.VISIBLE);
        if(live_ipo_fragment.binding.liveIpoRefreshLayout.isRefreshing()){
            live_ipo_fragment.binding.liveIpoRefreshLayout.performHapticFeedback(1);
            live_ipo_fragment.binding.liveIpoRefreshLayout.setRefreshing(false);
        }
    }

    static void playAnimationRCV(){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TransitionManager.beginDelayedTransition(live_ipo_fragment.binding.liveIpoMainLayout, new MaterialFadeThrough());
                live_ipo_fragment.binding.shimmerIpoList.setVisibility(View.INVISIBLE);
                live_ipo_fragment.binding.recyclerViewLiveIpoFeed.setVisibility(View.VISIBLE);
            }
        });
    }

}
