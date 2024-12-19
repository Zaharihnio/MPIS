package com.example.lab_5;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {

    private DataBase db;
    private EditText editTextAdd;
    private Button buttonAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        db = new DataBase(getActivity());
        editTextAdd = view.findViewById(R.id.editTextAdd);
        buttonAdd = view.findViewById(R.id.buttonAdd);

        buttonAdd.setOnClickListener(v -> {
            String description = editTextAdd.getText().toString();
            db.addNote(description);
            Toast.makeText(getActivity(), "Note added", Toast.LENGTH_SHORT).show();
        });
        return view;
    }
}