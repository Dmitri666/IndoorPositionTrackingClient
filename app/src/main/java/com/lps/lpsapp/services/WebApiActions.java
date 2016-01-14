package com.lps.lpsapp.services;

import com.lps.lpsapp.LpsApplication;
import com.lps.lpsapp.R;

/**
 * Created by user on 22.08.2015.
 */
public class WebApiActions {
    public static String getToken()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.getToken);
    }

    public static String PostBeaconData()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.PostBeaconData);
    }
    public static String GetImage()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.images);
    }
    public static String GetRegions()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetRegions);
    }

    public static String RegisterUserCurrentDevice()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.RegisterUserCurrentDevice);
    }

    public static String RegisterDevice()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.RegisterDevice);
    }

    public static String PostMeasurement()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.PostMeasurement);
    }
    public static String GetRooms()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetLocaleMap);
    }

    public static String GetLocaleNames()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetLocaleNames);
    }

    public static String GetLocaleTypes()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetLocaleTypes);
    }

    public static String GetLocaleSpecializings()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetLocaleSpecializing);
    }

    public static String GetCities()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetCities);
    }

    public static String GetActorsInLocale()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetActorsInLocale);
    }

    public static String SetPosition()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.SetPosition);
    }
    public static String RemovePosition()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.RemovePosition);
    }
    public static String GetActorByDevice()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetActorByDevice);
    }

    public static String GetBookingModel()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetBookings);
    }

    public static String GetBookingState()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetTableReservation);
    }

    public static String GetBookingHistory()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetBookingHistory);
    }
    public static String GetBeaconsInLocale()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetBeaconsInLocale);
    }
    public static String SendBookingRequest()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.SendBookingRequest);
    }
    public static String SendBookingResponse()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.SendBookingResponse);
    }

    public static String Subscribe()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl);
    }

    public static String PostChatMessage()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.PostChatMessage);
    }

    public static String CreateConversation()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.CreateConversation);
    }

    public static String GetConversation()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetConversation);
    }

    public static String GetRoomConversations()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.GetRoomConversations);
    }

    public static String InsertFavorite()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.InsertFavorit);
    }
    public static String DeleteFavorite()
    {
        return LpsApplication.getContext().getResources().getString(R.string.serverUrl) + LpsApplication.getContext().getResources().getString(R.string.DeleteFavorit);
    }
}
