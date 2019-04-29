package com.example.android.marvelpedia.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.android.marvelpedia.data.Repositories.ComicRepository;
import com.example.android.marvelpedia.data.Repositories.EventRepository;
import com.example.android.marvelpedia.data.Repositories.HeroRepository;
import com.example.android.marvelpedia.data.Repositories.SeriesRepository;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Series;

import java.util.ArrayList;
import java.util.List;

public class DetailExtrasViewModel extends AndroidViewModel {

    private final String LOG_TAG = HeroViewModel.class.getSimpleName();
    private MutableLiveData<Character> currentCharacter = new MutableLiveData<>();
    private MutableLiveData<Comic> currentComic = new MutableLiveData<>();

    //Comic Related Variables
    private MediatorLiveData<List<Comic>> comicList = new MediatorLiveData<>();
    private ComicRepository comicRepository;

    //Event Related Variables
    private MediatorLiveData<List<Event>> eventList = new MediatorLiveData<>();
    private EventRepository eventRepository;

    //Series related Variables
    private MediatorLiveData<List<Series>> seriesList = new MediatorLiveData<>();
    private SeriesRepository seriesRepository;

    //Character related variables
    private MediatorLiveData<List<Character>> characterList = new MediatorLiveData<>();
    private HeroRepository characterRepository;

    public DetailExtrasViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Comic>> getCharacterComics() {
        if (currentCharacter != null) {
            comicRepository = new ComicRepository();
            comicList.addSource(comicRepository.getCharacterComics(currentCharacter.getValue()),
                    new Observer<List<Comic>>() {
                        @Override
                        public void onChanged(List<Comic> comics) {
                            if (comics != null) {
                                comicList.setValue(comics);
                            }
                        }
                    });
        }
        return comicList;
    }

    public LiveData<List<Event>> getCharacterEvents() {
        if (currentCharacter != null) {
            eventRepository = new EventRepository();
            eventList.addSource(eventRepository.getCharacterEvents(currentCharacter.getValue()),
                    new Observer<List<Event>>() {
                        @Override
                        public void onChanged(List<Event> events) {
                            if (events != null) {
                                eventList.setValue(events);
                            }
                        }
                    });
        }
        return eventList;
    }

    public LiveData<List<Series>> getCharacterSeries() {
        if (currentCharacter != null) {
            seriesRepository = new SeriesRepository();
            seriesList.addSource(seriesRepository.getCharacterSeries(currentCharacter.getValue()),
                    new Observer<List<Series>>() {
                        @Override
                        public void onChanged(List<Series> series) {
                            if (series != null) {
                                seriesList.setValue(series);
                            }
                        }
                    });
        }
        return seriesList;
    }

    public LiveData<List<Character>> getComicCharacters() {
        if (currentCharacter != null) {
            characterRepository = new HeroRepository();
            characterList.addSource(characterRepository.getComicCharacters(currentComic.getValue()),
                    new Observer<List<Character>>() {
                        @Override
                        public void onChanged(List<Character> characters) {
                            if (characters != null) {
                                characterList.setValue(characters);
                            }
                        }
                    });
        }
        return characterList;
    }

    public LiveData<List<Event>> getComicEvents() {
        if (currentCharacter != null) {
            eventRepository = new EventRepository();
            eventList.addSource(eventRepository.getComicEvents(currentComic.getValue()),
                    new Observer<List<Event>>() {
                        @Override
                        public void onChanged(List<Event> events) {
                            if (events != null) {
                                eventList.setValue(events);
                            }
                        }
                    });
        }
        return eventList;
    }


    public void setCurrentCharacter(Character character) {
        currentCharacter.setValue(character);
    }

    public void setCurrentComic(Comic comic) {
        currentComic.setValue(comic);
    }

}
