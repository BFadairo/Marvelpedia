package com.example.android.marvelpedia.data.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.android.marvelpedia.model.Character;

import java.util.List;

@Dao
public interface CharacterDao {

    @Insert
    void insertTeamMember(Character character);

    @Query("SELECT * FROM team")
    List<Character> getAllMembers();

    @Query("SELECT * FROM team WHERE id = :cId")
    Character fetchCharacterById(int cId);

    @Delete
    void deleteMember(Character character);

    @Delete
    void deleteAllMembers(List<Character> characters);
}
