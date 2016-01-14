package com.lps.lpsapp.helper;

import java.util.UUID;

/**
 * Created by dle on 03.12.2015.
 */
public class ComboBoxItem {
    public ComboBoxItem() {

    }

    public ComboBoxItem(UUID value,String text)
    {
        this.text = text;
        this.value = value;
    }
    public String text;
    public UUID value;
}
