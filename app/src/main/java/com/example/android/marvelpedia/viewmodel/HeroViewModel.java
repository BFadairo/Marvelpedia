package com.example.android.marvelpedia.viewmodel;

import android.app.Application;

import com.example.android.marvelpedia.data.Repositories.HeroRepository;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class HeroViewModel extends AndroidViewModel {

    private final String LOG_TAG = HeroViewModel.class.getSimpleName();
    private MutableLiveData<String> heroSearchTerm = new MutableLiveData<>();
    private MutableLiveData<Character> currentCharacter = new MutableLiveData<>();

    private List<Character> charactersList = new ArrayList<>();
    private MediatorLiveData<List<Character>> characterList = new MediatorLiveData<>();
    private MediatorLiveData<List<Comic>> comicList = new MediatorLiveData<>();
    private LiveData<List<Character>> heroList;
    private HeroRepository heroRepository;

    public HeroViewModel(@NonNull Application application) {
        super(application);
        characterList.setValue(charactersList);
        heroList = characterList;
    }

    public LiveData<List<Character>> getHerosFromSearchTerm() {
        if (!(heroSearchTerm.getValue().isEmpty())) {
            heroRepository = new HeroRepository();
            characterList.addSource(heroRepository.getSearchTermHeroes(heroSearchTerm.getValue().toString()),
                    new Observer<List<Character>>() {
                        @Override
                        public void onChanged(List<Character> characters) {
                            characterList.setValue(characters);
                        }
                    });
        }
        return heroList;
    }


    public void setHeroSearchTerm(String heroSearchTerm) {
        this.heroSearchTerm.setValue(heroSearchTerm);
    }

    public LiveData<List<Character>> getHeroList() {
        return this.characterList;
    }
}
