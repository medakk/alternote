package com.medakk.alternote.uihelper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.medakk.alternote.R;
import com.medakk.alternote.note.NoteManager;
import com.medakk.alternote.note.SimpleNote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

public class SaveAndLoad {

    private static final String NOTES_FILE = "notes.json";

    /**
     * SaveNotesAsync
     *  -start an asynchronous task that saves the notes in NoteManager
     */
    public static class SaveNotesAsync extends AsyncTask<Void, Void, Void> {

        private final Context context;

        private boolean errorOccurred = false;

        public SaveNotesAsync(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            final NoteManager noteManager = NoteManager.getNoteManager();

            //check if NoteManager is dirty
            if(!noteManager.isDirty()) {
                Log.d("SaveAsyncTask", "NoteManager isn't dirty. Skipping save.");
                return null;
            }

            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray();
                jsonArray.put(noteManager.getSize()); //add the number of notes

                //get all the notes
                for (SimpleNote n : noteManager) {
                    JSONObject jsonNote = n.toJSON();
                    jsonArray.put(jsonNote);
                }
            } catch (JSONException e) {
                errorOccurred = true;
                return null;
            }

            FileOutputStream fos;
            try {
                fos = context.openFileOutput(NOTES_FILE, Context.MODE_PRIVATE);
                fos.write(jsonArray.toString().getBytes());
                fos.close();
            } catch(Exception e) {
                errorOccurred = true;
                return null;
            }

            noteManager.clearDirtyFlag();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(errorOccurred) {
                Toast.makeText(context, R.string.toast_cant_save, Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public static class LoadAsyncTask extends AsyncTask<Void, Void, Void> {

        private final Context context;
        private final WeakReference<NotesAdapter> wrNotesAdapter;
        private final byte[] byteBuffer = new byte[1024];

        private boolean errorOccurred = false;

        public LoadAsyncTask(Context context, NotesAdapter notesAdapter) {
            this.context = context;
            wrNotesAdapter = new WeakReference<>(notesAdapter);
        }

        @Override
        protected Void doInBackground(Void... params) {
            final NoteManager noteManager = NoteManager.getNoteManager();

            noteManager.clear();

            FileInputStream fis;
            try {
                fis = context.openFileInput(NOTES_FILE);
            } catch (FileNotFoundException e) {
                //no saved JSON file. no worries
                return null;
            }

            StringBuilder sb = new StringBuilder();

            try {
                int bytesRead;
                while ((bytesRead = fis.read(byteBuffer)) != -1) {
                    //TODO: is this really the best way to read a text file? o.O
                    final String s = new String(byteBuffer, 0, bytesRead);
                    sb.append(s);
                }

                fis.close();
            } catch(java.io.IOException e) {
                errorOccurred = true;
                return null;
            }

            Log.d("LoadAsyncTask", sb.toString());

            try {
                JSONArray jsonArray = (JSONArray) (new JSONTokener(sb.toString())).nextValue();
                int size = jsonArray.getInt(0); //TODO: this assumes that the first object is the size.it shoudln't

                for(int i = 1; i<size+1; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SimpleNote note = SimpleNote.fromJSON(jsonObject);

                    noteManager.addNote(note);
                }

            } catch(JSONException e) {
                errorOccurred = true;
                return null;
            }
            noteManager.clearDirtyFlag();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(errorOccurred) {
                Toast.makeText(context, R.string.toast_cant_load, Toast.LENGTH_SHORT).show();
                return;
            }

            // notify the adapter provided that it still exists
            // ie: the activity hasn't been destroyed
            final NotesAdapter notesAdapter = wrNotesAdapter.get();
            if(notesAdapter != null) {
                notesAdapter.notifyDataSetChanged();
            }
        }
    }

}

