package com.medakk.alternote.uihelper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.medakk.alternote.R;
import com.medakk.alternote.note.SimpleNote;
import com.medakk.alternote.note.NoteManager;

/**
 * NotesAdapter
 *  -used to populate lvNotes in MainActivityFragment
 */
public class NotesAdapter extends BaseAdapter {

    private Context context;
    private NoteManager noteManager;

    public NotesAdapter(Context context) {
        this.context = context;
        noteManager = NoteManager.getNoteManager();
    }

    public void addNote(SimpleNote n) {
        noteManager.addNote(n);
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return noteManager.getSize();
    }

    @Override
    public Object getItem(int position) {
        return noteManager.getNote(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.lvitem_note, null);
        }

        final TextView tvTitle = (TextView) convertView.findViewById(R.id.lvitem_note_title);
        final TextView tvContent = (TextView) convertView.findViewById(R.id.lvitem_note_content);

        SimpleNote n = (SimpleNote) getItem(position);
        tvTitle.setText(n.getTitle());
        tvContent.setText(n.getContent());

        return convertView;
    }
}
