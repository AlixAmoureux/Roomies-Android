package com.roomies.android;

import android.content.Context;
import android.support.v7.widget.SearchView;


public class MySearchView extends SearchView {
    private boolean expanded;


    public MySearchView(Context context) {
        super(context);
    }

    @Override
    public void onActionViewExpanded() {
        super.onActionViewExpanded();
        expanded = true;
    }

    @Override
    public void onActionViewCollapsed() {
        super.onActionViewCollapsed();
        expanded = false;
    }

    public boolean isExpanded() {
        return expanded;
    }
}
