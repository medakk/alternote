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
public class NotesAdapter extends BaseAdapter implements View.OnClickListener{

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

            final ImageButton ibtnDelete = (ImageButton) convertView.findViewById(R.id.lvitem_ibtn_delete);
            ibtnDelete.setOnClickListener(this);
        }

        final TextView tvTitle = (TextView) convertView.findViewById(R.id.lvitem_tv_note_title);
        final TextView tvContent = (TextView) convertView.findViewById(R.id.lvitem_tv_note_content);
        final ImageButton ibtnDelete = (ImageButton) convertView.findViewById(R.id.lvitem_ibtn_delete);

        SimpleNote n = (SimpleNote) getItem(position);
        tvTitle.setText(n.getTitle());
        tvContent.setText(n.getContent());
        ibtnDelete.setTag(n.getUuid());

        return convertView;
    }

    //for the delete button
    @Override
    public void onClick(View v) {
        UUID uuid = (UUID) v.getTag();

        int foundNoteIndex = noteManager.findNoteByUuid(uuid);

        if(foundNoteIndex != -1) {
            noteManager.removeNote(foundNoteIndex);
            this.notifyDataSetChanged();
        }
    }
}
