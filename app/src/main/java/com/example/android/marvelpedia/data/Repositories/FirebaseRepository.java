package com.example.android.marvelpedia.data.Repositories;

import com.example.android.marvelpedia.data.Database.CharacterDao;
import com.example.android.marvelpedia.model.Character;

import java.util.List;

import androidx.lifecycle.LiveData;

public class FirebaseRepository {

    private final String LOG_TAG = FirebaseRepository.class.getSimpleName();
    private CharacterDao characterDao;
    private LiveData<List<Character>> characterList;
}
