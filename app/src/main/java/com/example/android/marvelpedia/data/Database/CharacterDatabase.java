package com.example.android.marvelpedia.data.Database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.android.marvelpedia.model.Character;

@Database(entities = {Character.class}, version = 2, exportSchema = false)
public abstract class CharacterDatabase extends RoomDatabase {
    private final static String DATABASE_NAME = "CharacterDatabase.db";
    private static CharacterDatabase INSTANCE;

    public static CharacterDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), CharacterDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract CharacterDao characterDao();
}
