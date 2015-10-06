package com.medakk.alternote.note;

/**
 * Note
 *  -a simple note with a title and some text
 */
public class Note {
    private String title;
    private String content;

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
