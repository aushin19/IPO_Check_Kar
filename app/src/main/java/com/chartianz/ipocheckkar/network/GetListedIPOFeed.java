package com.chartianz.ipocheckkar.network;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.transition.TransitionManager;

import com.chartianz.ipocheckkar.BuildConfig;
import com.chartianz.ipocheckkar.adapter.IPOFeedAdapter;
import com.chartianz.ipocheckkar.modal.IPOInfo;
import com.chartianz.ipocheckkar.ui.activity.ControllerActivity;
import com.chartianz.ipocheckkar.ui.fragments.listed_ipo_fragment;
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

public class GetListedIPOFeed extends AsyncTask<Void, Void, Void> {
    static Context context;
    static ArrayList<IPOInfo> ipoFeedModalArrayList = new ArrayList<>();
    public static IPOFeedAdapter ipoFeedAdapter;
    String content;

    public GetListedIPOFeed(Context context) {
        this.context = context;
        ControllerActivity.ipoInfoArrayList2.clear();
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
                        if(jsonArray.getJSONObject(i).getString("ipo_status").equals("Listed")){
                            addToList(jsonArray, i);
                        }
                    }
                    ControllerActivity.ipoInfoArrayList2 = ipoFeedModalArrayList;
                } catch (JSONException e) {

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

        }
        return null;
    }

    void addToList(JSONArray jsonArray, int i) {
        try {
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
                    jsonArray.getJSONObject(i).getInt("ipo_max_price"),
                    (float) jsonArray.getJSONObject(i).getDouble("ipo_listing_rate"),
                    jsonArray.getJSONObject(i).getString("details_about_company"),
                    jsonArray.getJSONObject(i).getJSONArray("ipo_lot_info"),
                    jsonArray.getJSONObject(i).getJSONObject("ipo_subs_json"),
                    jsonArray.getJSONObject(i).getJSONObject("ipo_fin_info"),
                    jsonArray.getJSONObject(i).getJSONArray("ipo_issue_details"),
                    jsonArray.getJSONObject(i).getJSONObject("ipo_gmp_details")
            ));
        } catch (JSONException e) {

        }
    }


    @Override
    protected void onPostExecute(Void unused) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (ipoFeedModalArrayList.size() != 0) {
                    ipoFeedAdapter = new IPOFeedAdapter(context, ipoFeedModalArrayList);
                    ipoFeedAdapter.setHasStableIds(false);

                    listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setItemViewCacheSize(ipoFeedModalArrayList.size());
                    listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setAdapter(ipoFeedAdapter);

                    playAnimationRCV();

                    if(listed_ipo_fragment.binding.listedIpoRefreshLayout.isRefreshing()){
                        listed_ipo_fragment.binding.listedIpoRefreshLayout.performHapticFeedback(1);
                        listed_ipo_fragment.binding.listedIpoRefreshLayout.setRefreshing(false);
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

        listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setItemViewCacheSize(ipoFeedModalArrayList_temp.size());
        listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setAdapter(ipoFeedAdapter);

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

        listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setItemViewCacheSize(ipoFeedModalArrayList_temp.size());
        listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setAdapter(ipoFeedAdapter);

        playAnimationRCV();
    }

    public static void sortNormalList(){
        ipoFeedAdapter = new IPOFeedAdapter(context, ipoFeedModalArrayList);
        ipoFeedAdapter.setHasStableIds(false);

        listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setItemViewCacheSize(ipoFeedModalArrayList.size());
        listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setAdapter(ipoFeedAdapter);

        playAnimationRCV();
    }

    void showNoDataLayout(){
        TransitionManager.beginDelayedTransition(listed_ipo_fragment.binding.listedIpoMainLayout, new MaterialFadeThrough());
        listed_ipo_fragment.binding.shimmerIpoList.setVisibility(View.INVISIBLE);
        listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setVisibility(View.INVISIBLE);
        listed_ipo_fragment.binding.noListedDataLayout.setVisibility(View.VISIBLE);
        if(listed_ipo_fragment.binding.listedIpoRefreshLayout.isRefreshing()){
            listed_ipo_fragment.binding.listedIpoRefreshLayout.performHapticFeedback(1);
            listed_ipo_fragment.binding.listedIpoRefreshLayout.setRefreshing(false);
        }
    }

    static void playAnimationRCV(){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TransitionManager.beginDelayedTransition(listed_ipo_fragment.binding.listedIpoMainLayout, new MaterialFadeThrough());
                listed_ipo_fragment.binding.shimmerIpoList.setVisibility(View.INVISIBLE);
                listed_ipo_fragment.binding.recyclerViewListedIpoFeed.setVisibility(View.VISIBLE);
            }
        });
    }

}
