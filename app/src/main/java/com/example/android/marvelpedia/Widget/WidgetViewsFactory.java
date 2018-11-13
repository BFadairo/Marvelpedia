package com.example.android.marvelpedia.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.marvelpedia.Database.CharacterDatabase;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.model.Character;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class WidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final String LOG_TAG = WidgetViewsFactory.class.getSimpleName();

    private final Context mContext;
    private List<Character> mMembers = new ArrayList<>();

    public WidgetViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        Log.v(LOG_TAG, "RemoteViews on create called");
    }

    @Override
    public void onDataSetChanged() {
        mMembers = CharacterDatabase.getAppDatabase(mContext).characterDao().getAllMembers();
        for (int i = 0; i < mMembers.size(); i++) {
            Log.v(LOG_TAG, mMembers.get(i).getName());
        }
    }

    @Override
    public void onDestroy() {
        mMembers.clear();
        CharacterDatabase.destroyInstance();
    }

    @Override
    public int getCount() {
        return mMembers.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_list);

        if (position <= getCount()) {
            Character character = mMembers.get(position);
            remoteViews.setTextViewText(R.id.stack_view_widget_name, character.getName());
            Log.v(LOG_TAG, character.getName());

            if (character.getImageUrl() != null) {
                try {
                    String completedPath = character.getImageUrl();
                    Log.v(LOG_TAG, completedPath);
                    Bitmap bitmap = Picasso.get().load(completedPath).get();
                    remoteViews.setImageViewBitmap(R.id.stack_view_widget_image, bitmap);
                } catch (Exception e) {
                    Log.v(LOG_TAG, "Crash");
                    e.printStackTrace();
                }
            }
        }

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
