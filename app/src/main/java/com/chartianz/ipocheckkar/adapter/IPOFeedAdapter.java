package com.chartianz.ipocheckkar.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chartianz.ipocheckkar.Constants;
import com.chartianz.ipocheckkar.R;
import com.chartianz.ipocheckkar.databinding.IpoFeedListBinding;
import com.chartianz.ipocheckkar.modal.IPOInfo;
import com.chartianz.ipocheckkar.ui.activity.ControllerActivity;
import com.chartianz.ipocheckkar.ui.activity.DetailsActivity;
import com.chartianz.ipocheckkar.utils.WhatsAppShare;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class IPOFeedAdapter extends RecyclerView.Adapter<IPOFeedAdapter.IPOFeedViewHolder> {
    private final Context context;
    private ArrayList<IPOInfo> ipoInfoArrayList;
    IPOFeedViewHolder holder;

    public IPOFeedAdapter(final Context context, final ArrayList<IPOInfo> ipoInfoArrayList) {
        this.context = context;
        this.ipoInfoArrayList = ipoInfoArrayList;
    }

    @NonNull
    @Override
    public IPOFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        IpoFeedListBinding binding = IpoFeedListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new IPOFeedViewHolder(binding);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(@NonNull IPOFeedViewHolder holder, int position) {
        IPOInfo ipoInfo = ipoInfoArrayList.get(position);
        this.holder = holder;

        Glide.with(context)
                .load(ipoInfo.ipo_company_logo)
                .thumbnail(0.25F)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.binding.ipoCompanyLogo);

        holder.binding.ipoFullName.setText(ipoInfo.ipo_full_name.replace("NSE", "")
                .replace("BSE", "").replace("IPO", "")
                .replace("SME", "").replace("And", "").replace("and", "").trim());
        holder.binding.ipoOfferDate.setText(ipoInfo.ipo_open_date + " - " + ipoInfo.ipo_close_date);

        if(ipoInfo.ipo_max_price == 0)
            holder.binding.ipoMaxPrice.setText("N/A");
        else
            holder.binding.ipoMaxPrice.setText("₹" + ipoInfo.ipo_max_price + ".00");
        holder.binding.ipoSize.setText(ipoInfo.ipo_size.replace("Shares", "").replace("shares", "").trim());

        if(ipoInfo.ipo_full_name.contains("SME")){
            holder.binding.mainboardIpoLabel.setVisibility(View.GONE);
            holder.binding.smeIpoLabel.setVisibility(View.VISIBLE);
        }

        if(ipoInfo.ipo_status.equals("Closed") && !ipoInfo.ipo_allotment_status_link.equals("false"))
            holder.binding.ipoStatus.setText("ALLOTMENT OUT");
        else if(ipoInfo.ipo_status.equals("Upcoming") || ipoInfo.ipo_status.equals("Open")){
            holder.binding.ipoStatus.setText(ipoInfo.ipo_status.toUpperCase());
            holder.binding.ipoStatus.setTextColor(context.getColor(R.color.font));
            holder.binding.cardView2.setBackgroundTintList(context.getColorStateList(R.color.background));
            holder.binding.linearLayout6.setBackgroundColor(context.getColor(R.color.white));
        }
        else
            holder.binding.ipoStatus.setText(ipoInfo.ipo_status.toUpperCase());

        new DateParsingTask(holder.binding, ipoInfo.ipo_close_date, ipoInfo.ipo_allotment_date).execute();

        if(ipoInfo.ipo_subs.equals("0"))
            holder.binding.ipoSubs.setText("To be announced");
        else
            holder.binding.ipoSubs.setText(ipoInfo.ipo_subs + "x");

        if(ipoInfo.ipo_status.equals("Listed"))
            setListingInfo(ipoInfo);
        else
            getGMP(ipoInfo);

        holder.binding.ipoFeedMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putString("ipo_name", ipoInfo.ipo_full_name);
                ControllerActivity.mFirebaseAnalytics.logEvent("ipo_post", params);

                Intent intent = new Intent(context, DetailsActivity.class);
                //Intent intent = new Intent(context, test.class);

                intent.putParcelableArrayListExtra("ipoInfoArrayList", ipoInfoArrayList);
                intent.putExtra("position", holder.getAdapterPosition());
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.scale_xy_enter, R.anim.scale_xy_exit);

                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        });

        holder.binding.ipoShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putString("ipo_name", ipoInfo.ipo_full_name);
                ControllerActivity.mFirebaseAnalytics.logEvent("ipo_share", params);

                String shareBody = "*IPO Basic Details*\n\n" + "\uD83D\uDCE2 Company Name : " + ipoInfo.ipo_full_name.replace("NSE", "")
                        .replace("And", "").replace("and", "").trim() + "\n" +
                        "✨Offer Date : " + ipoInfo.ipo_open_date + " - " + ipoInfo.ipo_listing_date + "\n" +
                        "✨Issue Price : ₹" + ipoInfo.ipo_max_price + ".00\n" +
                        "✨Issue Size : " + ipoInfo.ipo_size + "\n" +
                        "✨Subscription : " + holder.binding.ipoSubs.getText().toString() + "\n" +
                        "✨GMP : " + holder.binding.ipoGmp.getText().toString() + "\n" +
                        "✨IPO Status : " + holder.binding.ipoStatus.getText().toString();

                String shareBody2 = context.getString(R.string.ipo_share_body) + "\n\n" +
                        Constants.IPO_CHECK_KAR_APP_LINK;

                WhatsAppShare.shareText(context, shareBody, shareBody2);

                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        });

    }

    private void setListingInfo(IPOInfo ipoInfo) {
        holder.binding.gmpListingTextview.setText("Listed on " + ipoInfo.ipo_listing_date + " at");
        String gmpText;
        int textColor;

        float ipoListingRate = ipoInfo.ipo_listing_rate;
        float percentGain = ipoListingRate * 100.0f;
        int actualRate = Math.round((ipoInfo.ipo_max_price * ipoListingRate) + ipoInfo.ipo_max_price);

        if (percentGain < 0) {
            gmpText = String.format("₹%d (%.2f%%) ▼", actualRate, percentGain);
            textColor = R.color.gmp_red;
            holder.binding.materialCardView4.setCardBackgroundColor(ContextCompat.getColor(context, R.color.listing_back_red));
        } else if (percentGain == 0) {
            gmpText = String.format("₹%d (%.2f%%) --", actualRate, percentGain);
            textColor = R.color.font;
        } else {
            gmpText = String.format("₹%d (+%.2f%%) ▲", actualRate, percentGain);
            textColor = R.color.gmp_green;
            holder.binding.materialCardView4.setCardBackgroundColor(ContextCompat.getColor(context, R.color.listing_back_green));
        }

        holder.binding.ipoGmp.setText(gmpText);
        holder.binding.ipoGmp.setTextColor(ContextCompat.getColor(context, textColor));
    }

    private void getGMP(IPOInfo ipoInfo) {
        if(ipoInfo.ipo_max_price == 0){
            holder.binding.ipoGmp.setText("₹0 (0.00%) --");
            holder.binding.ipoGmp.setTextColor(ContextCompat.getColor(context, R.color.font));
            return;
        }
        float percentGain = (ipoInfo.ipo_gmp * 100.0f) / ipoInfo.ipo_max_price;

        String gmpText;
        int textColor;

        if (percentGain < 0) {
            gmpText = String.format("₹%d (%.2f%%) ▼", ipoInfo.ipo_gmp, percentGain);
            textColor = R.color.gmp_red;
        } else if (percentGain == 0) {
            gmpText = String.format("₹%d (%.2f%%) --", ipoInfo.ipo_gmp, percentGain);
            textColor = R.color.font;
        } else {
            gmpText = String.format("₹%d (%.2f%%) ▲", ipoInfo.ipo_gmp, percentGain);
            textColor = R.color.gmp_green;
        }

        holder.binding.ipoGmp.setText(gmpText);
        holder.binding.ipoGmp.setTextColor(ContextCompat.getColor(context, textColor));
    }

    @Override
    public int getItemCount() {
        return ipoInfoArrayList.size();
    }

    public static class IPOFeedViewHolder extends RecyclerView.ViewHolder {
        private IpoFeedListBinding binding;
        public IPOFeedViewHolder(@NonNull IpoFeedListBinding binding) {
            super(binding.getRoot());
            this.binding= binding;
        }
    }

    private static class DateParsingTask extends AsyncTask<Void, Void, Boolean> {
        private final IpoFeedListBinding binding;
        private final String closeDate;
        private final String allotmentDate;

        public DateParsingTask(IpoFeedListBinding binding, String closeDate, String allotmentDate) {
            this.binding = binding;
            this.closeDate = closeDate;
            this.allotmentDate = allotmentDate;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault());

                LocalDate currentDate = LocalDate.now();
                LocalDate closeDateTime = LocalDate.parse(closeDate, formatter);
                LocalDate allotmentDateTime = LocalDate.parse(allotmentDate, formatter);

                return !currentDate.isBefore(closeDateTime) && currentDate.isBefore(allotmentDateTime);

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isAllotmentWaited) {
            if (isAllotmentWaited) {
                binding.ipoStatus.setText("ALLOTMENT AWAITED");
            }
        }
    }

}
