package com.example.android.marvelpedia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.marvelpedia.Fragments.ComicFragment;
import com.example.android.marvelpedia.Fragments.DetailFragment;
import com.example.android.marvelpedia.model.Comic;

public class DetailActivity extends AppCompatActivity implements ComicFragment.SendComicData {

    private ComicFragment comicFragment;
    private DetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle argsToPass = getIntent().getExtras();

        //Create a new Detail Fragment
        detailFragment = new DetailFragment();
        //Set the arguments for the detail fragment
        detailFragment.setArguments(argsToPass);

        //Create a new Comic Fragment
        comicFragment = new ComicFragment();
        //Set the arguments for the comic fragment
        comicFragment.setArguments(argsToPass);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.comic_container, comicFragment)
                .add(R.id.detail_information_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void sendComic(Comic comic) {
        Bundle bundle = new Bundle();
        //TODO Add string for this
        bundle.putParcelable("comic_extras", comic);
        detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .remove(comicFragment)
                .replace(R.id.detail_information_container, detailFragment)
                .commit();
    }
}
