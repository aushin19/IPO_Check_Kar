package com.chartianz.ipocheckkar.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.chartianz.ipocheckkar.Constants;
import com.chartianz.ipocheckkar.R;
import com.chartianz.ipocheckkar.adapter.IPODetailsPageAdapter;
import com.chartianz.ipocheckkar.ads.InterstitialAds;
import com.chartianz.ipocheckkar.databinding.ActivityDetailsBinding;
import com.chartianz.ipocheckkar.modal.IPOInfo;
import com.chartianz.ipocheckkar.ui.fragments.detailed_fragments.subscription;
import com.chartianz.ipocheckkar.utils.WhatsAppShare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {
    private Context context;
    private ActivityDetailsBinding binding;
    private IPOInfo ipoInfo;
    private LiveData<String> fullNameLiveData;
    private LiveData<String> offerDateLiveData;
    String DRHP_LINK, RHP_LINK;
    InterstitialAds interstitialAds;
    private Handler handler = new Handler();
    private Runnable runnable;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setTabLayout();
        setData();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setIssueTableDetails();
                setLotsTableDetails();
                setSubscriptionTableDetails();
                setFinancialTableDetails();
                setGMPTableDetails();
            }
        });
    }

    private void init() {
        context = this;

        interstitialAds = new InterstitialAds(context);
        interstitialAds.loadAd();

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        runnable = new Runnable() {
            @Override
            public void run() {
                if (interstitialAds.isAdLoaded()) {
                    interstitialAds.showAd();
                    handler.removeCallbacks(runnable);
                }
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnable);

        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<IPOInfo> ipoInfoArrayList = intent.getParcelableArrayListExtra("ipoInfoArrayList");
            if (ipoInfoArrayList != null) {
                ipoInfo = ipoInfoArrayList.get(intent.getIntExtra("position", 0));
            }
        }
    }

    private void setTabLayout(){
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new subscription());

        IPODetailsPageAdapter adapter = new IPODetailsPageAdapter(getSupportFragmentManager(), fragments, ipoInfo);
        binding.include.viewPager.setAdapter(adapter);
        binding.include.tabLayout.setupWithViewPager(binding.include.viewPager);
        binding.include.tabLayout.getTabAt(0).setText("Subscription");
    }

    private void setData() {
        fullNameLiveData = new MutableLiveData<>();
        fullNameLiveData.observe(this, fullName -> binding.detailsIpoName.setText(fullName));

        offerDateLiveData = new MutableLiveData<>();
        offerDateLiveData.observe(this, offerDate -> binding.detailsIpoOfferDate.setText(offerDate));

        ((MutableLiveData<String>) fullNameLiveData).setValue(ipoInfo.ipo_full_name);
        ((MutableLiveData<String>) offerDateLiveData).setValue(ipoInfo.ipo_open_date + " - " + ipoInfo.ipo_close_date);

        Glide.with(context).load(ipoInfo.ipo_company_logo).into(binding.detailsIpoCompanyLogo);
        binding.include.detailsAboutCompany.setText(ipoInfo.details_about_company);

        if(ipoInfo.ipo_max_price == 0)
            binding.detailsIpoMaxPrice.setText("N/A");
        else
            binding.detailsIpoMaxPrice.setText("₹" + ipoInfo.ipo_max_price + ".00");

        binding.detailsIpoSize.setText(ipoInfo.ipo_size);

        if (ipoInfo.ipo_subs.equals("0"))
            binding.detailsIpoSubs.setText("To be announced");
        else
            binding.detailsIpoSubs.setText(ipoInfo.ipo_subs + "x");

        if (!ipoInfo.ipo_status.equals("Open"))
            binding.lottieAnimationView.setAnimation(R.raw.ipo_closed);

        if (ipoInfo.ipo_status.equals("Closed") && !ipoInfo.ipo_allotment_status_link.equals("false")) {
            binding.include.checkAllotmentButton.setVisibility(View.VISIBLE);
            binding.detailsIpoStatus.setText("Allotment Out".toUpperCase());
        } else {
            binding.include.checkAllotmentButton.setVisibility(View.GONE);
            binding.detailsIpoStatus.setText(ipoInfo.ipo_status.toUpperCase());
        }

        binding.include.checkAllotmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putString("ipo_name", ipoInfo.ipo_full_name);
                ControllerActivity.mFirebaseAnalytics.logEvent("ipo_check_allotment", params);

                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ipoInfo.ipo_allotment_status_link));
                context.startActivity(browserIntent);
            }
        });


        binding.include.detailsIpoIssueDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putString("ipo_name", ipoInfo.ipo_full_name);
                ControllerActivity.mFirebaseAnalytics.logEvent("ipo_issue_details", params);
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                toggleVisibility(binding.include.detailsIpoIssueDetailsCardview, binding.include.detailsIpoIssueDetails);
            }
        });

        binding.include.detailsIpoLotsDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putString("ipo_name", ipoInfo.ipo_full_name);
                ControllerActivity.mFirebaseAnalytics.logEvent("ipo_lots_details", params);
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                toggleVisibility(binding.include.detailsIpoLotsDetailsCardview, binding.include.detailsIpoLotsDetails);
            }
        });

        binding.include.detailsIpoSubscriptionDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putString("ipo_name", ipoInfo.ipo_full_name);
                ControllerActivity.mFirebaseAnalytics.logEvent("ipo_subs_details", params);
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                if (binding.include.detailsIpoSubsDetailsCardview.getVisibility() == View.VISIBLE) {
                    binding.include.detailsIpoSubsDetailsCardview.setVisibility(View.GONE);
                    binding.include.detailsIpoSubsOfferDetailsCardview.setVisibility(View.GONE);
                    binding.include.detailsIpoSubscriptionDetails.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.small_arrow_right, 0);
                } else {
                    binding.include.detailsIpoSubsDetailsCardview.setVisibility(View.VISIBLE);
                    binding.include.detailsIpoSubsOfferDetailsCardview.setVisibility(View.VISIBLE);
                    binding.include.detailsIpoSubscriptionDetails.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.small_arrow_down, 0);
                }
            }
        });

        binding.include.detailsIpoFinancialDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putString("ipo_name", ipoInfo.ipo_full_name);
                ControllerActivity.mFirebaseAnalytics.logEvent("ipo_fin_details", params);
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                toggleVisibility(binding.include.detailsIpoFinancialDetailsCardview, binding.include.detailsIpoFinancialDetails);

                if (binding.include.detailsIpoFinancialDetailsCardview.getVisibility() == View.VISIBLE)
                    binding.include.amountUnit.setVisibility(View.VISIBLE);
                else
                    binding.include.amountUnit.setVisibility(View.GONE);
            }
        });

        binding.include.detailsIpoGmpDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putString("ipo_name", ipoInfo.ipo_full_name);
                ControllerActivity.mFirebaseAnalytics.logEvent("ipo_gmp_details", params);
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                toggleVisibility(binding.include.detailsIpoGmpDetailsCardview, binding.include.detailsIpoGmpDetails);
            }
        });

        binding.include.drhpDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                if (DRHP_LINK.contains("://")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(DRHP_LINK));
                    context.startActivity(browserIntent);
                } else {
                    Toast.makeText(context, "DRHP not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.include.rhpDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                if (RHP_LINK.contains("://")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(RHP_LINK));
                    context.startActivity(browserIntent);
                } else {
                    Toast.makeText(context, "RHP not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.ipoShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareBody = "*IPO Basic Details*\n\n" + "✨Company Name : " + ipoInfo.ipo_full_name.replace("NSE", "")
                        .replace("And", "").replace("and", "").trim() + "\n" +
                        "✨Offer Date : " + ipoInfo.ipo_open_date + " - " + ipoInfo.ipo_listing_date + "\n" +
                        "✨Issue Price : ₹" + ipoInfo.ipo_max_price + ".00\n" +
                        "✨Issue Size : " + ipoInfo.ipo_size + "\n" +
                        "✨Subscription : " + binding.detailsIpoSubs.getText().toString() + "\n" +
                        "✨IPO Status : " + binding.detailsIpoStatus.getText().toString();

                String shareBody2 = context.getString(R.string.ipo_share_body) + "\n\n" +
                        Constants.IPO_CHECK_KAR_APP_LINK;
                WhatsAppShare.shareText(context, shareBody, shareBody2);

                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        });

    }

    private void setIssueTableDetails() {
        binding.include.tableIpoOpenDate.setText(ipoInfo.ipo_open_date);
        binding.include.tableIpoCloseDate.setText(ipoInfo.ipo_close_date);
        binding.include.tableIpoAllotmentDate.setText(ipoInfo.ipo_allotment_date);
        binding.include.tableIpoListingDate.setText(ipoInfo.ipo_listing_date);
        binding.include.tableIpoRegistrar.setText(ipoInfo.ipo_registrar);

        try {
            JSONArray jsonArray = ipoInfo.ipo_issue_details;
            if (jsonArray.length() > 0) {
                JSONObject ipoDetails = jsonArray.getJSONObject(0);

                DRHP_LINK = ipoDetails.optString("DRHP", "N/A");
                RHP_LINK = ipoDetails.optString("RHP", "N/A");

                String issuePrice = ipoDetails.optString("IPO Issue Price", ipoDetails.optString("SME IPO Issue Price", "N/A"));
                String issueSize = ipoDetails.optString("IPO Issue Size", ipoDetails.optString("SME IPO Issue Size", "N/A"));
                String offerForSale = ipoDetails.optString("Offer for Sale", "N/A");
                String freshIssue = ipoDetails.optString("Fresh Issue", "N/A");
                String faceValue = ipoDetails.optString("Face Value", "N/A");
                String discount = ipoDetails.optString("IPO Discount", ipoDetails.optString("SME IPO Discount", "N/A"));
                String listingGroup = ipoDetails.optString("IPO Listing At", "N/A");
                String promoterHoldingPreIpo = ipoDetails.optString("Promoter Holding Pre IPO", "N/A");
                String promoterHoldingPostIpo = ipoDetails.optString("Promoter Holding Post IPO", "N/A");

                binding.include.tableIpoPriceRange.setText(issuePrice);
                binding.include.tableIpoIssueSize.setText(issueSize);
                binding.include.tableIpoOfferSale.setText(offerForSale);
                binding.include.tableIpoFreshIssue.setText(freshIssue);
                binding.include.tableIpoFaceValue.setText(faceValue);
                binding.include.tableIpoDiscount.setText(discount);
                binding.include.tableIpoListingGroup.setText(listingGroup);
                binding.include.tableIpoPromoterHoldingPreIpo.setText(promoterHoldingPreIpo);
                binding.include.tableIpoPromoterHoldingPostIpo.setText(promoterHoldingPostIpo);
            }
        } catch (JSONException e) {

        }

    }

    private void setLotsTableDetails() {
        try {
            JSONArray jsonArray = ipoInfo.ipo_lot_info;
            if (jsonArray.length() > 0) {
                binding.include.tableIpoIssuePrice.setText(jsonArray.getJSONObject(0).optString("Date", "N/A"));
                binding.include.tableIpoMarketLot.setText(currencyFormat(jsonArray.getJSONObject(1).optString("Date", "N/A")));
                binding.include.tableIpoLotAmount.setText(currencyFormat(jsonArray.getJSONObject(2).optString("Date", "N/A")));
                binding.include.tableIpoSmallHni.setText(jsonArray.getJSONObject(3).optString("Date", "N/A"));
                binding.include.tableIpoBigHni.setText(jsonArray.getJSONObject(4).optString("Date", "N/A"));
            } else {

            }
        } catch (JSONException e) {

        }
    }

    private void toggleVisibility(View view, TextView textView) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.small_arrow_right, 0);
        } else {
            view.setVisibility(View.VISIBLE);
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.small_arrow_down, 0);
        }
    }

    private void setSubscriptionTableDetails() {
        JSONObject ipoSubsJson = ipoInfo.ipo_subs_json;
        if (!ipoSubsJson.optString("TBA").equals("TBA")) {
            try {
                JSONObject jsonObject = ipoInfo.ipo_subs_json;
                JSONArray dataArray = jsonObject.getJSONArray("data");

                TableLayout sub_tableLayout = findViewById(R.id.subscription_details_table);
                TableLayout offer_tableLayout = findViewById(R.id.subscription_offer_details_table);

                TableRow headerRow = new TableRow(this);
                headerRow.setPadding(30, 30, 30, 30);

                addHeaderTextView(headerRow, "Day");
                addHeaderTextView(headerRow, "QIB");
                addHeaderTextView(headerRow, "NII");
                addHeaderTextView(headerRow, "RII");
                addHeaderTextView(headerRow, "Total");

                sub_tableLayout.addView(headerRow);

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject dataObject = dataArray.getJSONObject(i);

                    TableRow dataRow = new TableRow(this);
                    dataRow.setPadding(30, 30, 30, 30);

                    addInfoDataTextView(dataRow, convertDateFormat(dataObject.optString("Bid Date-Day" + (i + 1)), "dd MMM yyyy HH:mm", "MMM dd, yyyy"));
                    addInfoDataTextView(dataRow, dataObject.optString("QIB-Day" + (i + 1), "N/A"));
                    addInfoDataTextView(dataRow, dataObject.optString("NII-Day" + (i + 1), "N/A"));
                    addInfoDataTextView(dataRow, dataObject.optString("RII-Day" + (i + 1), "N/A"));
                    addInfoDataTextView(dataRow, dataObject.optString("Total-Day" + (i + 1), "N/A"));

                    sub_tableLayout.addView(dataRow);
                }

                TableRow shareRow = new TableRow(this);
                shareRow.setPadding(30, 30, 30, 30);

                addHeaderTextView(shareRow, "");
                addHeaderTextView(shareRow, "QIB");
                addHeaderTextView(shareRow, "NII");
                addHeaderTextView(shareRow, "RII");
                addHeaderTextView(shareRow, "Total");

                offer_tableLayout.addView(shareRow);

                TableRow shareRow2 = new TableRow(this);
                shareRow2.setPadding(30, 30, 30, 30);
                JSONObject headers = jsonObject.getJSONObject("headers");

                addHeaderTextView(shareRow2, "No. of Shares\nOffered");
                addInfoDataTextView(shareRow2, formatNumber(headers.optString("QIB Offered", "N/A")));
                addInfoDataTextView(shareRow2, formatNumber(headers.optString("NII Offered", "N/A")));
                addInfoDataTextView(shareRow2, formatNumber(headers.optString("RII Offered", "N/A")));
                addInfoDataTextView(shareRow2, formatNumberTotal(headers.optString("Total Offered", "N/A")));

                offer_tableLayout.addView(shareRow2);
            } catch (JSONException e) {

            }
        } else {
            binding.include.subsDetailSection.setVisibility(View.GONE);
        }
    }

    private void setFinancialTableDetails() {
        JSONObject finJSON = ipoInfo.ipo_fin_info;
        if (!finJSON.optString("TBA").equals("TBA")) {
            try {
                JSONObject jsonObject = ipoInfo.ipo_fin_info;
                JSONArray headers = jsonObject.getJSONArray("headers");
                JSONArray data = jsonObject.getJSONArray("data");
                String unit = jsonObject.optString("unit", "N/A");

                binding.include.amountUnit.setText("*" + unit.replace("₹", "").trim());
                TableLayout tableLayout = binding.include.financialDetailsTable;

                TableRow headerRow = new TableRow(this);
                headerRow.setPadding(30, 30, 30, 30);
                for (int i = 0; i < headers.length(); i++) {
                    if (headers.optString(i).equals("Period Ended")) {
                        addHeaderTextView(headerRow, headers.optString(i));
                        continue;
                    }
                    addInfoDataTextView(headerRow, convertDateFormat(headers.optString(i), "dd MMM yyyy", "dd MMM, yy"));
                }
                tableLayout.addView(headerRow);

                for (int i = 0; i < data.length(); i++) {
                    JSONArray rowData = data.getJSONArray(i);

                    TableRow dataRow = new TableRow(this);
                    dataRow.setPadding(30, 30, 30, 30);
                    for (int j = 0; j < rowData.length(); j++) {
                        if (rowData.optString(j).equals("Profit After Tax")) {
                            addHeaderTextView(dataRow, "Profit After\nTax");
                            continue;
                        } else if (rowData.optString(j).equals("Reserves and Surplus")) {
                            addHeaderTextView(dataRow, "Reserves &\nSurplus");
                            continue;
                        } else if (rowData.optString(j).equals("Total Borrowing")) {
                            addHeaderTextView(dataRow, "Total\nBorrowing");
                            continue;
                        }else if (rowData.optString(j).equals("Assets")) {
                            addHeaderTextView(dataRow, "Assets");
                            continue;
                        }else if (rowData.optString(j).equals("Revenue")) {
                            addHeaderTextView(dataRow, "Revenue");
                            continue;
                        }else if (rowData.optString(j).equals("Net Worth")) {
                            addHeaderTextView(dataRow, "Net Worth");
                            continue;
                        }
                        addInfoDataTextView(dataRow, rowData.optString(j, "N/A"));
                    }
                    tableLayout.addView(dataRow);
                }

            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            binding.include.financialDetailSection.setVisibility(View.GONE);
        }
    }

    private void setGMPTableDetails() {
        JSONObject gmpJSON = ipoInfo.ipo_gmp_details;
        if (!gmpJSON.optString("TBA").equals("TBA")) {
            try {
                JSONObject jsonObject = ipoInfo.ipo_gmp_details;
                JSONArray headers = jsonObject.getJSONArray("header");
                JSONArray data = jsonObject.getJSONArray("data");

                TableLayout tableLayout = binding.include.gmpDetailsTable;

                TableRow headerRow = new TableRow(this);
                headerRow.setPadding(30, 30, 30, 30);
                for (int i = 0; i < headers.length(); i++) {
                    if (headers.optString(i).equals("Sub2 Sauda Rate") || headers.optString(i).equals("Last Updated"))
                        continue;
                    addHeaderTextView(headerRow, headers.optString(i));
                }

                tableLayout.addView(headerRow);

                for (int i = 0; i < data.length(); i++) {
                    JSONObject dataObject = data.getJSONObject(i);

                    TableRow dataRow = new TableRow(this);
                    dataRow.setPadding(30, 30, 30, 30);

                    addInfoDataTextView(dataRow, slitDates(dataObject.optString("GMP Date", "N/A")));
                    addInfoDataTextView(dataRow, dataObject.optString("IPO Price", "N/A"));
                    addInfoDataTextView(dataRow, dataObject.optString("GMP", "N/A"));
                    addInfoDataTextView(dataRow, dataObject.optString("Estimated Listing Price", "N/A"));

                    tableLayout.addView(dataRow);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            binding.include.gmpDetailsTable.setVisibility(View.GONE);
        }
    }

    private void addHeaderTextView(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                6f));
        textView.setTextAppearance(this, R.style.Table_Title);
        textView.setTypeface(null, Typeface.BOLD);
        row.addView(textView);
    }

    private void addInfoDataTextView(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                6f));
        textView.setTextAppearance(this, R.style.Table_SubTitle);
        row.addView(textView);
    }

    private String convertDateFormat(String inputDate, String inputFormat, String outputFormat) {
        inputDate = inputDate.replace("st", "")
                .replace("th", "")
                .replace("nd", "")
                .replace("rd", "");
        SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat, Locale.getDefault());

        try {
            Date date = inputFormatter.parse(inputDate);
            return outputFormatter.format(date);
        } catch (ParseException e) {
            return inputDate;
        }
    }

    private String slitDates(String input) {
        try {
            String[] parts = input.split(" ");
            if (parts.length == 2) {
                return parts[0];
            } else {
                return input;
            }
        } catch (Exception e) {
            return input;
        }
    }

    private static String formatNumber(String input) {
        try {
            String[] parts = input.split(" ");

            if (parts.length == 2) {
                String numberPart = parts[0].replace(",", "");
                String percentagePart = parts[1];

                long number = Long.parseLong(numberPart);

                if (number >= 10000000) {
                    return String.format("%.2f Cr. ", (number / 10000000.0)) + "\n" + percentagePart;
                } else if (number >= 100000) {
                    return String.format("%.2f Lakh ", (number / 100000.0)) + "\n" + percentagePart;
                } else {
                    return input;
                }
            } else {
                return input;
            }
        } catch (Exception e) {
            return input;
        }
    }

    private static String formatNumberTotal(String numberStr) {
        try {
            long number = Long.parseLong(numberStr.replace(",", ""));
            if (number >= 10000000) {
                double croreValue = number / 10000000.0;
                return String.format("%.2f Cr.", croreValue);
            } else if (number >= 100000) {
                double lakhValue = number / 100000.0;
                return String.format("%.2f Lakh", lakhValue);
            } else {
                return numberStr;
            }
        }catch (Exception e){
            return numberStr;
        }
    }

    private String currencyFormat(String inputAmount) {
        try {
            String numericValue = inputAmount.replaceAll("[^\\d]", "");
            double amount = Double.parseDouble(numericValue);
            NumberFormat indianFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            return indianFormat.format(amount);
        } catch (Exception e) {
            return inputAmount;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}