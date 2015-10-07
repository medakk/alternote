package com.medakk.alternote.note;

import android.util.Log;

import com.medakk.alternote.util.Helper;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Maintains list of Notes
 * Singleton class
 */
public class NoteManager implements Iterable<SimpleNote> {
    private ArrayList<SimpleNote> alNotes;

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

    public void addNote(SimpleNote n) {
        if(n == null) {
            Log.d("NoteManager", "Attempting to add null note");
            return;
        }

        //make sure there is some content in the note's title, ie: it isn't just whitespace
        if(Helper.onlyContainsWhitespace(n.getTitle())) {
            return;
        }

        alNotes.add(n);
    }

    public void removeNote(int index) {
        alNotes.remove(index);
    }

    public void removeNote(SimpleNote n) {
        alNotes.remove(n);
    }

    public SimpleNote getNote(int n) {
        return alNotes.get(n);
    }

    public void clear() {
        alNotes.clear();
    }

    @Override
    public Iterator<SimpleNote> iterator() {
        return alNotes.iterator();
    }
}
