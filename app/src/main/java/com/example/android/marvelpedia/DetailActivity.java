package com.example.android.marvelpedia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.example.android.marvelpedia.model.Character;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private Character passedCharacter;
    private int passedCharacterId;
    private String characterImage;
    private ImageView characterImageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        retrieveCharacterDetails();

        Log.v("Tag", passedCharacter.getName());
    }

    public void retrieveCharacterDetails() {
        passedCharacter = getIntent().getParcelableExtra("character_extras");
        passedCharacterId = passedCharacter.getId();
        setCharacterImage();
    }

    public void setCharacterImage() {
        characterImageLayout = findViewById(R.id.character_image_detail);
        characterImage = passedCharacter.getImageUrl();
        Log.v("Tag", characterImage + "");
        Picasso.get().load(characterImage).into(characterImageLayout);
    }
}
