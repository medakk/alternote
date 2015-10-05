package com.medakk.alternote.uihelper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * NotesAdapter
 *  -used to populate lvNotes in MainActivityFragment
 */
public class NotesAdapter extends BaseAdapter {

    private Context context;

    public NotesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = new TextView(context);
        }
        ((TextView)convertView).setText("afsf" + position);
        return convertView;
    }
}
