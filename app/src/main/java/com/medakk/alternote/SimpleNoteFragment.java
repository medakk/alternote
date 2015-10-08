package com.medakk.alternote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleNoteFragment extends Fragment {

    private TextView tvTitle;
    private TextView tvContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_simple_note, container, false);

        final int noteIndex = getArguments().getInt(NoteActivity.BUNDLE_NOTE_INDEX, -1);
        Toast.makeText(getActivity(), "here : " + noteIndex, Toast.LENGTH_SHORT).show();

        return v;
    }
}
