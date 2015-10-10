package com.medakk.alternote.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.medakk.alternote.R;
import com.medakk.alternote.note.NoteManager;
import com.medakk.alternote.note.SimpleNote;
import com.medakk.alternote.ui.activity.NoteActivity;
import com.medakk.alternote.uihelper.SaveAndLoad;
import com.medakk.alternote.util.Helper;

public class SimpleNoteFragment extends Fragment {

    private EditText tvTitle;
    private EditText tvContent;

    private SimpleNote note;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_simple_note, container, false);
        final int noteIndex = getArguments().getInt(NoteActivity.BUNDLE_NOTE_INDEX, -1);

        final NoteManager noteManager = NoteManager.getNoteManager();
        note = noteManager.getNote(noteIndex);

        tvTitle = (EditText) v.findViewById(R.id.snote_et_title);
        tvContent = (EditText) v.findViewById(R.id.snote_et_content);

        tvTitle.setText(note.getTitle());
        tvContent.setText(note.getContent());

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        final String title = tvTitle.getText().toString();
        final String content = tvContent.getText().toString();

        if(Helper.onlyContainsWhitespace(title)) {
            Toast.makeText(getActivity(), "Title is blank. Discarding changes", Toast.LENGTH_SHORT).show();
            return;
        }

        note.set(title,content);

        SaveAndLoad.SaveNotesAsync saveNotesAsync = new SaveAndLoad.SaveNotesAsync(getActivity());
        saveNotesAsync.execute();
    }
}
