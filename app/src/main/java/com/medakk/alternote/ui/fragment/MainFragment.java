package com.medakk.alternote.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.medakk.alternote.R;
import com.medakk.alternote.note.SimpleNote;
import com.medakk.alternote.ui.activity.NoteActivity;
import com.medakk.alternote.uihelper.NotesAdapter;
import com.medakk.alternote.uihelper.SaveAndLoad;
import com.medakk.alternote.uihelper.SwipeToDismissListViewListener;
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
    private LinearLayout llUndoMessage;
    private Button btnUndo;

    private int animationDuration;

    private Handler handler = new Handler();

    private SimpleNote undoNote = null;
    private int undoPosition = -1;

    private final long WAIT_HIDE_UNDO_MSG = 4500;
    private Runnable runnableHideUndoMsg = new Runnable() {
        @Override
        public void run() {
            llUndoMessage.animate()
                    .translationY(llUndoMessage.getHeight())
                    .alpha(0)
                    .setDuration(animationDuration)
                    .setListener(null);
        }
    };

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        etQuickNote = (EditText) v.findViewById(R.id.main_et_quicknote);
        ibtnAddQuickNote = (ImageButton) v.findViewById(R.id.main_ibtn_addquicknote);
        lvNotes = (ListView) v.findViewById(R.id.main_lv_notes);
        llUndoMessage = (LinearLayout) v.findViewById(R.id.main_ll_undo_message);
        btnUndo = (Button) v.findViewById(R.id.main_btn_undo);

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

        //make the views swipeable
        final SwipeToDismissListViewListener swipeTouchListener = new SwipeToDismissListViewListener(lvNotes, new SwipeToDismissListViewListener.DismissCallback() {
            @Override
            public boolean canDismiss(int position) {
                return true;
            }

            @Override
            public void onDismiss(ListView lv, int[] reverseSortedPositions) {
                for(int position: reverseSortedPositions) {
                    undoNote = (SimpleNote) notesAdapter.getItem(position);
                    undoPosition = position;

                    notesAdapter.removeNote(position);
                }

                llUndoMessage.setTranslationY(llUndoMessage.getMeasuredHeight());
                llUndoMessage.animate()
                        .translationY(0)
                        .alpha(1)
                        .setDuration(animationDuration)
                        .setListener(null);
                handler.postDelayed(runnableHideUndoMsg, WAIT_HIDE_UNDO_MSG);
            }
        });
        lvNotes.setOnTouchListener(swipeTouchListener);
        lvNotes.setOnScrollListener(swipeTouchListener.makeOnScrollListener());

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

        //for the undo button
        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(undoNote != null) {
                    notesAdapter.addNote(undoPosition, undoNote);
                    undoNote = null;
                    undoPosition = -1;

                    handler.removeCallbacks(runnableHideUndoMsg);
                    runnableHideUndoMsg.run();
                }
            }
        });

        //start an AsyncTask to load the notes from the saved JSON file
        SaveAndLoad.LoadAsyncTask loadAsyncTask = new SaveAndLoad.LoadAsyncTask(getActivity(), notesAdapter);
        loadAsyncTask.execute();

        //get the animation time for the undo message bar
        animationDuration = getActivity().getResources().
                getInteger(android.R.integer.config_longAnimTime);

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