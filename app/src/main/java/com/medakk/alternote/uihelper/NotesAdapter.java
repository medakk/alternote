package com.medakk.alternote.uihelper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.medakk.alternote.R;
import com.medakk.alternote.note.SimpleNote;
import com.medakk.alternote.note.NoteManager;

import java.util.UUID;

/**
 * NotesAdapter
 *  -used to populate lvNotes in MainActivityFragment
 */
public class NotesAdapter extends BaseAdapter{

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

    public void addNote(int position, SimpleNote note) {
        noteManager.addNote(position, note);
        super.notifyDataSetChanged();
    }

    public void removeNote(int position) {
        noteManager.removeNote(position);
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
        // because of a bug with the SwipeToDismissListViewListener,
        // we aren't trying to reuse the views
        // TODO: reuse views after the SwipeToDismiss listener is fixed
        convertView = View.inflate(context, R.layout.lvitem_note, null);

        final TextView tvTitle = (TextView) convertView.findViewById(R.id.lvitem_tv_note_title);
        final TextView tvContent = (TextView) convertView.findViewById(R.id.lvitem_tv_note_content);

        SimpleNote n = (SimpleNote) getItem(position);
        tvTitle.setText(n.getTitle());
        tvContent.setText(n.getContent());

        return convertView;
    }
}
