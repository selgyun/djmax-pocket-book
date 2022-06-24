package com.example.djmaxpocketbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Frag15Lv extends Fragment {
    private View view;

    public static Frag15Lv newinstance(){
        Frag15Lv frag15Lv = new Frag15Lv();
        return frag15Lv;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (MainActivity.curBtn == MainActivity.BUTTON.FOUR)
            view = inflater.inflate(R.layout.frag_4b_15, container, false);
        else if (MainActivity.curBtn == MainActivity.BUTTON.FIVE)
            view = inflater.inflate(R.layout.frag_5b_15, container, false);
        else if (MainActivity.curBtn == MainActivity.BUTTON.SIX)
            view = inflater.inflate(R.layout.frag_6b_15, container, false);
        else // if (MainActivity.curBtn == MainActivity.BUTTON.EIGHT)
            view = inflater.inflate(R.layout.frag_8b_15, container, false);
        MainActivity.fetchSong(view);
        return view;
    }

}
