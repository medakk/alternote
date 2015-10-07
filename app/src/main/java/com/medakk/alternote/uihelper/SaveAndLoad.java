package com.medakk.alternote.uihelper;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.medakk.alternote.R;
import com.medakk.alternote.note.SimpleNote;
import com.medakk.alternote.note.NoteManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;

public class SaveAndLoad {

    private static final String NOTES_FILE = "notes.json";

    /**
     * SaveNotesAsync
     *  -start an asynchronous task that saves the notes in NoteManager
     */
    public static class SaveNotesAsync extends AsyncTask<Void, Void, Void> {

        private final Context context;
        public SaveNotesAsync(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            final NoteManager noteManager = NoteManager.getNoteManager();

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
                Toast.makeText(context, R.string.toast_cant_save, Toast.LENGTH_SHORT).show();
                return null;
            }

            FileOutputStream fos;
            try {
                fos = context.openFileOutput(NOTES_FILE, Context.MODE_PRIVATE);
                fos.write(jsonArray.toString().getBytes());
            } catch(Exception e) {
                Toast.makeText(context, R.string.toast_cant_save, Toast.LENGTH_SHORT).show();
                return null;
            }

            return null;
        }
    }

}

