package com.example.lab_5;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentShow extends Fragment {

    private ListView listView;
    private DataBase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        listView = view.findViewById(R.id.listView);
        db = new DataBase(getActivity());

        loadNotes();
        return view;
    }

    private void loadNotes() {
        Cursor cursor = db.getAllNotes();
        Adapter adapter = new Adapter(getActivity(), cursor);
        listView.setAdapter(adapter);
    }
}