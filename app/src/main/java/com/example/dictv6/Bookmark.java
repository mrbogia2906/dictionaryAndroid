package com.example.dictv6;

public class Bookmark {
    private Word word;
    private String originTable;

    public Bookmark(Word bookmark, String originTable) {
        this.word = bookmark;
        this.originTable = originTable;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public String getOriginTable() {
        return originTable;
    }

    public void setOriginTable(String originTable) {
        this.originTable = originTable;
    }
}
