package com.medakk.alternote.note;

import android.util.Log;

import java.util.ArrayList;

/**
 * Maintains list of Notes
 * Singleton class
 */
public class NoteManager {
    private ArrayList<Note> alNotes;

    // Singleton methods
    private static NoteManager me;
    public static NoteManager getNoteManager() {
        if(me == null) {
            me = new NoteManager();
        }

        return me;
    }

    private NoteManager() {
        alNotes = new ArrayList<>();
    }

    public int getSize() {
        return alNotes.size();
    }

    public void addNote(Note n) {
        if(n == null) {
            Log.d("NoteManager", "Attempting to add null note");
            return;
        }

        alNotes.add(n);
    }

    public Note getNote(int n) {
        return alNotes.get(n);
    }
}
