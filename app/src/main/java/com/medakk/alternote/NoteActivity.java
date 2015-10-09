package com.medakk.alternote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

/**
 * Activity that hosts the fragment to view notes
 */
public class NoteActivity extends FragmentActivity {

    public static final String EXTRA_NOTE_INDEX = "alternote.intent.extra.index";
    public static final String BUNDLE_NOTE_INDEX = "alternote.bundle.index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        final Intent i = getIntent();
        final int noteIndex = i.getIntExtra(EXTRA_NOTE_INDEX, -1);
        if(noteIndex == -1) {
            Toast.makeText(this, R.string.toast_cant_open_note, Toast.LENGTH_SHORT).show();
            finish();
        }

        final Bundle bundleForFragment = new Bundle();
        bundleForFragment.putInt(BUNDLE_NOTE_INDEX, noteIndex);

        /*
         * TODO:
         * this is temporary. we need to identify the type of note,
         * open the appropriate fragment
         */
        final FragmentManager fm = getSupportFragmentManager();
        Fragment f  = new SimpleNoteFragment();
        f.setArguments(bundleForFragment);
        fm.beginTransaction().add(R.id.fragment, f).commit();
    }
}
