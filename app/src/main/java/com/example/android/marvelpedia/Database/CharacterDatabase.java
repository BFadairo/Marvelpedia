package com.example.android.marvelpedia.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.android.marvelpedia.model.Character;

@Database(entities = {Character.class}, version = 1, exportSchema = false)
public abstract class CharacterDatabase extends RoomDatabase {
    private final static String DATABASE_NAME = "CharacterDatabase.db";
    private static CharacterDatabase INSTANCE;

    public static CharacterDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), CharacterDatabase.class, DATABASE_NAME)
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract CharacterDao characterDao();
}
