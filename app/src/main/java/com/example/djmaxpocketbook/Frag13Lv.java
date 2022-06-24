package com.example.djmaxpocketbook;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class Frag13Lv extends Fragment {
    private View view;

    public static Frag13Lv newinstance(){
        Frag13Lv frag13Lv = new Frag13Lv();
        return frag13Lv;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (MainActivity.curBtn == MainActivity.BUTTON.FOUR)
            view = inflater.inflate(R.layout.frag_4b_13, container, false);
        else if (MainActivity.curBtn == MainActivity.BUTTON.FIVE)
            view = inflater.inflate(R.layout.frag_5b_13, container, false);
        else if (MainActivity.curBtn == MainActivity.BUTTON.SIX)
            view = inflater.inflate(R.layout.frag_6b_13, container, false);
        else // if (MainActivity.curBtn == MainActivity.BUTTON.EIGHT)
            view = inflater.inflate(R.layout.frag_8b_13, container, false);
        MainActivity.fetchSong(view);
        return view;
    }

}
