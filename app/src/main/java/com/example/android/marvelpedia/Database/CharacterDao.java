package com.example.android.marvelpedia.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

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
