package com.chartianz.ipocheckkar.modal;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class IPOInfo implements Parcelable {
    // Existing fields
    public String ipo_full_name, ipo_company_url, ipo_company_logo,
            ipo_open_date, ipo_close_date, ipo_allotment_date,
            ipo_listing_date, ipo_size, ipo_subs, ipo_status, ipo_last_update, ipo_allotment_status_link, ipo_registrar;
    public String details_about_company;
    public int ipo_gmp, ipo_max_price;
    public float ipo_listing_rate;
    public JSONArray ipo_lot_info, ipo_issue_details;
    public JSONObject ipo_subs_json;
    public JSONObject ipo_fin_info;
    public JSONObject ipo_gmp_details;

    public IPOInfo(String ipo_full_name, String ipo_company_url, String ipo_company_logo,
                   String ipo_open_date, String ipo_close_date, String ipo_allotment_date, String ipo_listing_date,
                   String ipo_size, String ipo_subs, String ipo_status, String ipo_last_update, String ipo_allotment_status, String ipo_registrar,
               int ipo_gmp, int ipo_max_price, float ipo_listing_rate, String details_about_company,
                   JSONArray ipo_lot_info, JSONObject ipo_subs_json, JSONObject ipo_fin_info, JSONArray ipo_issue_details, JSONObject ipo_gmp_details) {
        this.ipo_full_name = ipo_full_name;
        this.ipo_company_url = ipo_company_url;
        this.ipo_company_logo = ipo_company_logo;
        this.ipo_open_date = ipo_open_date;
        this.ipo_close_date = ipo_close_date;
        this.ipo_allotment_date = ipo_allotment_date;
        this.ipo_listing_date = ipo_listing_date;
        this.ipo_size = ipo_size;
        this.ipo_subs = ipo_subs;
        this.ipo_status = ipo_status;
        this.ipo_last_update = ipo_last_update;
        this.ipo_allotment_status_link = ipo_allotment_status;
        this.ipo_registrar = ipo_registrar;
        this.ipo_gmp = ipo_gmp;
        this.ipo_max_price = ipo_max_price;
        this.ipo_listing_rate = ipo_listing_rate;
        this.details_about_company = details_about_company;

        this.ipo_issue_details = ipo_issue_details;
        this.ipo_lot_info = ipo_lot_info;
        this.ipo_subs_json = ipo_subs_json;
        this.ipo_fin_info = ipo_fin_info;
        this.ipo_gmp_details = ipo_gmp_details;
    }

    protected IPOInfo(Parcel in) {
        ipo_full_name = in.readString();
        ipo_company_url = in.readString();
        ipo_company_logo = in.readString();
        ipo_open_date = in.readString();
        ipo_close_date = in.readString();
        ipo_allotment_date = in.readString();
        ipo_listing_date = in.readString();
        ipo_size = in.readString();
        ipo_subs = in.readString();
        ipo_status = in.readString();
        ipo_last_update = in.readString();
        ipo_allotment_status_link = in.readString();
        ipo_registrar = in.readString();
        ipo_gmp = in.readInt();
        ipo_max_price = in.readInt();
        ipo_listing_rate = in.readFloat();
        details_about_company = in.readString();

        try {
            ipo_issue_details = new JSONArray(in.readString());
            ipo_lot_info = new JSONArray(in.readString());
            ipo_subs_json = new JSONObject(Objects.requireNonNull(in.readString()));
            ipo_fin_info = new JSONObject(Objects.requireNonNull(in.readString()));
            ipo_gmp_details = new JSONObject(Objects.requireNonNull(in.readString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<IPOInfo> CREATOR = new Creator<IPOInfo>() {
        @Override
        public IPOInfo createFromParcel(Parcel in) {
            return new IPOInfo(in);
        }

        @Override
        public IPOInfo[] newArray(int size) {
            return new IPOInfo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ipo_full_name);
        dest.writeString(ipo_company_url);
        dest.writeString(ipo_company_logo);
        dest.writeString(ipo_open_date);
        dest.writeString(ipo_close_date);
        dest.writeString(ipo_allotment_date);
        dest.writeString(ipo_listing_date);
        dest.writeString(ipo_size);
        dest.writeString(ipo_subs);
        dest.writeString(ipo_status);
        dest.writeString(ipo_last_update);
        dest.writeString(ipo_allotment_status_link);
        dest.writeString(ipo_registrar);
        dest.writeInt(ipo_gmp);
        dest.writeInt(ipo_max_price);
        dest.writeFloat(ipo_listing_rate);
        dest.writeString(details_about_company);

        dest.writeString(ipo_issue_details.toString());
        dest.writeString(ipo_lot_info.toString());
        dest.writeString(ipo_subs_json.toString());
        dest.writeString(ipo_fin_info.toString());
        dest.writeString(ipo_gmp_details.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

