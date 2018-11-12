package com.example.android.marvelpedia.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new WidgetViewsFactory(this.getApplicationContext(), intent));
    }
}
