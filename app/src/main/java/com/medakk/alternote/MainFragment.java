package com.medakk.alternote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.medakk.alternote.note.SimpleNote;
import com.medakk.alternote.uihelper.NotesAdapter;
import com.medakk.alternote.uihelper.SaveAndLoad;
import com.medakk.alternote.util.Helper;


/*
 * MainActivityFragment
 *  displays the list of entered notes,
 *  permits addition/removal of notes
 */
public class MainFragment extends Fragment {

    private EditText etQuickNote;
    private ImageButton ibtnAddQuickNote;
    private ListView lvNotes;
    private NotesAdapter notesAdapter;

    public MainFragment() {
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

        //start NoteActivity when a list item is pressed
        lvNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent i = new Intent(getActivity(), NoteActivity.class);
                i.putExtra(NoteActivity.EXTRA_NOTE_INDEX, position);
                startActivity(i);
            }
        });

        // create a new note when ibtnAddQuickNote is pressed
        ibtnAddQuickNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteText = etQuickNote.getText().toString();
                String[] splitText = Helper.splitTitleAndContent(noteText);

                SimpleNote n = new SimpleNote(splitText[0], splitText[1]);
                notesAdapter.addNote(n);

                //clear the EditText box
                etQuickNote.setText("");

                //TODO: the following two lines hide the soft key board :p use something else
                etQuickNote.setEnabled(false);
                etQuickNote.setEnabled(true);
            }
        });

        //start an AsyncTask to load the notes from the saved JSON file
        SaveAndLoad.LoadAsyncTask loadAsyncTask = new SaveAndLoad.LoadAsyncTask(getActivity(), notesAdapter);
        loadAsyncTask.execute();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        final SaveAndLoad.SaveNotesAsync saveNotesAsync =
                new SaveAndLoad.SaveNotesAsync(getActivity());
        saveNotesAsync.execute();

    }

    @Override
    public void onResume() {
        super.onResume();

        if(notesAdapter != null) {
            notesAdapter.notifyDataSetChanged();
        }
    }
}