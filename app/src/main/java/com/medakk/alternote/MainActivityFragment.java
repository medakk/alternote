package com.medakk.alternote;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.medakk.alternote.note.Note;
import com.medakk.alternote.uihelper.NotesAdapter;
import com.medakk.alternote.util.Helper;

import java.util.Scanner;


/*
 * MainActivityFragment
 *  displays the list of entered notes,
 *  permits addition/removal of notes
 */
public class MainActivityFragment extends Fragment {

    private EditText etQuickNote;
    private ImageButton ibtnAddQuickNote;
    private ListView lvNotes;
    private NotesAdapter notesAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        etQuickNote = (EditText) v.findViewById(R.id.main_et_quicknote);
        ibtnAddQuickNote = (ImageButton) v.findViewById(R.id.main_ibtn_addquicknote);
        lvNotes = (ListView) v.findViewById(R.id.main_lv_notes);

        // Create a TextView to display when there are no notes to display
        final TextView tvEmptyListMsg = new TextView(getActivity());
        tvEmptyListMsg.setGravity(Gravity.CENTER);
        tvEmptyListMsg.setText(R.string.empty_list);
        ((ViewGroup) lvNotes.getParent()).addView(tvEmptyListMsg);
        lvNotes.setEmptyView(tvEmptyListMsg);

        // set the adapter for lvNotes
        notesAdapter = new NotesAdapter(getActivity());
        lvNotes.setAdapter(notesAdapter);

        // create a new note when ibtnAddQuickNote is pressed
        ibtnAddQuickNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteText = etQuickNote.getText().toString();
                String[] splitText = Helper.splitTitleAndContent(noteText);

                Note n = new Note(splitText[0], splitText[1]);
                notesAdapter.addNote(n);
            }
        });

        return v;
    }
}