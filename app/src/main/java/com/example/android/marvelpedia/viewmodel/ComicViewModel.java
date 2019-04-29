package com.example.android.marvelpedia.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.marvelpedia.model.Comic;

import java.util.List;

public class ComicViewModel extends AndroidViewModel {

    private final String LOG_TAG = ComicViewModel.class.getSimpleName();
    private LiveData<List<Comic>> comicList;

    public ComicViewModel(@NonNull Application application) {
        super(application);
    }
}
