package com.example.dictv6;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class WordDao {
    private final SQLiteDatabase db;
    private final Map<String, Function<String, Word>> dictionaryStrategy;

    public WordDao(Context context) {
        // The constructor creates a new DictionaryDbHelper object, which helps interact with the SQLite database.
        // It then populates the 'dictionaryStrategy' Map with two key-value pairs that each correspond to a different dictionary translation function.
        DictionaryDbHelper dbHelper = new DictionaryDbHelper(context);
        this.db = dbHelper.getReadableDatabase();
        dictionaryStrategy = new HashMap<>();
        dictionaryStrategy.put("Từ điển Anh Việt", this::getWordEngToViet);
        dictionaryStrategy.put("Từ điển Việt Anh", this::getWordVietToEng);
    }

    private Word getWordFromTable(String tableName, String wordToFind) {
        Cursor cursor = db.query(tableName, null, "word = ?", new String[]{wordToFind}, null, null, null);

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String word = cursor.getString(cursor.getColumnIndex("word"));
            @SuppressLint("Range") String html = cursor.getString(cursor.getColumnIndex("html"));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));
            @SuppressLint("Range") String pronounce = cursor.getString(cursor.getColumnIndex("pronounce"));

            cursor.close();
            return new Word(id, word, html, description, pronounce);
        }
        cursor.close();
        return null;
    }

    public Word getWordEngToViet(String wordToFind) {
        return getWordFromTable("av", wordToFind);
    }

    public Word getWordVietToEng(String wordToFind) {
        return getWordFromTable("va", wordToFind);
    }

    public Word getWord(String dictionaryName, String wordToFind) {
        // This method accepts the name of a dictionary and a word to find.
        // It uses the 'dictionaryStrategy' Map to retrieve the correct translation function based on the provided dictionary name,
        // then applies the function to the provided word.
        Function<String, Word> strategy = dictionaryStrategy.get(dictionaryName);
        return strategy != null ? strategy.apply(wordToFind) : null;
    }

    public List<Bookmark> getAllBookmarks2() {
        List<Bookmark> bookmarks = new ArrayList<>();
        Cursor cursor = db.query("bookmarks", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int wordId = cursor.getInt(cursor.getColumnIndex("word_id"));
                @SuppressLint("Range") String originTable = cursor.getString(cursor.getColumnIndex("origin_table"));
                Word bookmark = getWordById(wordId, originTable);
                bookmarks.add(new Bookmark(bookmark, originTable));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bookmarks;
    }

    public Word getWordById(int idToFind, String tableName) {
        Cursor cursor = db.query(tableName, null, "id = ?", new String[]{String.valueOf(idToFind)}, null, null, null);

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String word = cursor.getString(cursor.getColumnIndex("word"));
            @SuppressLint("Range") String html = cursor.getString(cursor.getColumnIndex("html"));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));
            @SuppressLint("Range") String pronounce = cursor.getString(cursor.getColumnIndex("pronounce"));

            cursor.close();
            return new Word(id, word, html, description, pronounce);
        }

        cursor.close();
        return null;
    }

    public void bookmarkWord(int id, String originTable) {
        // This method bookmarks a word by its ID and the table it originated from.
        // It creates a new ContentValues object, puts the ID and origin table into it,
        // then inserts the ContentValues object into the 'bookmarks' table of the database.
        ContentValues values = new ContentValues();
        values.put("word_id", id);
        values.put("origin_table", originTable);

        db.insert("bookmarks", null, values);
    }

    public void removeBookmark(int id) {
        db.delete("bookmarks", "word_id = ?", new String[]{String.valueOf(id)});
    }

    public boolean isBookmarked(int id) {
        if (!tableExists("bookmarks")) {
            return false;
        }

        Cursor cursor = db.query("bookmarks", null, "word_id = ?", new String[]{String.valueOf(id)}, null, null, null);
        boolean isBookmarked = cursor.moveToFirst();
        cursor.close();
        return isBookmarked;
    }

    private boolean tableExists(String tableName) {
        Cursor cursor = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name = ?", new String[]{tableName});
        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
        }
        cursor.close();
        return exists;
    }
}

