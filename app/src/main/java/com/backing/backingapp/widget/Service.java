package com.backing.backingapp.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class Service extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.e("Service", "onGetViewFactory");
        return new Factory(getApplicationContext(), intent);
    }
}
