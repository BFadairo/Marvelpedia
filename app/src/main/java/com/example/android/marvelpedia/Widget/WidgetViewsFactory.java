package com.example.android.marvelpedia.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.marvelpedia.Database.CharacterDatabase;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.model.Character;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final String LOG_TAG = WidgetViewsFactory.class.getSimpleName();

    private Context mContext;
    private FirebaseDatabase database;
    private List<Character> teamMembers;
    private List<Character> mMembers = new ArrayList<>();
    private DatabaseReference teamMember;
    private CharacterDatabase roomDatabase;
    private Intent mIntent;
    private int appWidgetId;

    public WidgetViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        this.mIntent = intent;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        Log.v(LOG_TAG, "RemoteViews on create called");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mMembers = roomDatabase.characterDao().getAllMembers();
                return null;
            }
        };
    }

    @Override
    public void onDataSetChanged() {
        //mMembers = mIntent.getBundleExtra("team_members").getParcelableArrayList("team_list");
    }

    @Override
    public void onDestroy() {
        mMembers.clear();
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
            Log.v(LOG_TAG, character.getName());

            if (character.getThumbnail().getPath() != null) {
                try {
                    String completedPath = character.getThumbnail().getPath() + character.getThumbnail().getExtension();
                    Bitmap bitmap = Picasso.get().load(completedPath).resize(100, 100).get();
                    remoteViews.setImageViewBitmap(R.id.stack_view_widget_image, bitmap);
                } catch (Exception e) {
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
