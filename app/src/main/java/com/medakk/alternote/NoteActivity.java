package com.medakk.alternote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Activity that hosts the fragment to view notes
 */
public class NoteActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        final FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.fragment);
        if(f == null) {
            f = new SimpleNoteFragment();
            fm.beginTransaction().add(R.id.fragment, f).commit();
        }
    }
}
