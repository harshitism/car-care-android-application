package com.car.maintenance.Application;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class NextFocus implements TextWatcher {

    private int length;
    private TextView textView;
    private int id;

    public NextFocus(int length, TextView textView) {

        this.length = length;
        this.textView = textView;
        this.id = id;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == length) textView.requestFocus();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}