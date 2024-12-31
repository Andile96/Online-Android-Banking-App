package com.example.ufsqkbank.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class RatingsAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<String[]> Ratings;

    public RatingsAdapter(Context context, ArrayList<String[]> ratings) {
        this.context = context;
        Ratings = ratings;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
