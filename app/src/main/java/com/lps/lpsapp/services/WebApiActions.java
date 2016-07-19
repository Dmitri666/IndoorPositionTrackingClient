package com.lps.lpsapp.services;

import com.lps.lpsapp.LpsApplication;
import com.lps.lpsapp.R;
import com.lps.lpsapp.activities.SettingsActivity;

/**
 * Created by user on 22.08.2015.
 */
public class WebApiActions {
    public static String getToken() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.getToken);
    }

    public static String PostBeaconData() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.PostBeaconData);
    }

    public static String GetImage() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.images);
    }

    public static String GetRegions() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.GetRegions);
    }

    public static String SaveBackgroundBeacons() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.SaveBackgroundBeacons);
    }

    public static String PostMeasurement() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.PostMeasurement);
    }

    public static String GetRooms() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.GetLocaleMap);
    }

    public static String GetTableModel() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.GetTableModel);
    }

    public static String GetLocaleNames() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.GetLocaleNames);
    }

    public static String GetLocaleTypes() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.GetLocaleTypes);
    }

    public static String GetCities() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.GetCities);
    }

    public static String GetActorsInLocale() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.GetActorsInLocale);
    }

    public static String SetPosition() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.SetPosition);
    }

    public static String RemovePosition() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.RemovePosition);
    }


    public static String GetBookingState() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.GetTableReservation);
    }

    public static String GetBookingHistory() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.GetBookingHistory);
    }

    public static String GetBeaconsInLocale() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.GetBeaconsInLocale);
    }

    public static String SendBookingRequest() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.SendBookingRequest);
    }

    public static String SendBookingResponse() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.SendBookingResponse);
    }

    public static String Subscribe() {
        return SettingsActivity.WebApiUrl;
    }

    public static String PostChatMessage() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.PostChatMessage);
    }

    public static String GetConversation() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.GetConversation);
    }

    public static String GetRoomConversations() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.GetRoomConversations);
    }

    public static String InsertFavorite() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.InsertFavorit);
    }

    public static String DeleteFavorite() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.DeleteFavorit);
    }

    public static String SavePositionLog() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.SavePositionLog);
    }

    public static String IsAuthenticated() {
        return SettingsActivity.WebApiUrl + LpsApplication.getContext().getResources().getString(R.string.IsAuthenticated);
    }
}
