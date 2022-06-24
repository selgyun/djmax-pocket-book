package com.example.djmaxpocketbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.DecimalFormat;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    static Context context;

    // 현재 버튼 모드 설정
    public enum BUTTON { FOUR, FIVE , SIX, EIGHT};
    public static BUTTON curBtn;
    // 전체 곡 수
    // 4B 13LV : 75개
    public static int SONG_NUM = 217;

    public boolean isEditMode = false;
    public boolean isMaxCombo = false;

    // 전체 버튼 리스트
    static Button[] song = new Button[SONG_NUM];

    // frag 어댑터
    private FragmentStateAdapter fragmentStateAdapter;

    //dbHelper
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;

    static DecimalFormat accFormat = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //상태 창 지우기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        context = this;

        // 처음 구동했을 때, 버튼은 4버튼
        ImageButton button_4b = (ImageButton) findViewById(R.id.imageButton_4b);
        button_4b.setSelected(true);
        curBtn = BUTTON.FOUR;
        refreshPage();

        // edit switch 리스너 등록
        Switch editSwitch = findViewById(R.id.editModeSwitch);
        editSwitch.setOnCheckedChangeListener(new editSwitchListener());
        Switch maxComboSwitch = findViewById(R.id.maxComboSwitch);
        maxComboSwitch.setOnCheckedChangeListener(new maxComboSwitchListener());

        //db
        dbHelper = new DBHelper(this, "songDB.db", null, 1);
        db = dbHelper.getWritableDatabase();

        dbHelper.onCreate(db);
        // 엑셀 csv로 해결할 예정
        //dbHelper.insertSong(db, "Rolling On The Duck", 13, "HD", 9, "4B", "PORTABLE 2", 0.00f, 0);
        //dbHelper.insertSong(db, "BLACK GOLD", 13, "MX", 9, "4B", "V EXTENSION", 0.00f, 0);
    }

    // 뷰페이저 Refresh
    public void refreshPage(){
        // 뷰페이저와 탭바 연동 부분
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        fragmentStateAdapter = new ViewPagerAdapter(this);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager.setAdapter(fragmentStateAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText("LV. " + (position+13))
        ).attach();
    }
    // 송리스트 db 에서 정확도 값 불러오기
    public static void fetchSong(View view){

        for(int i = 1;i <= SONG_NUM;i++){
            int btnId = view.getResources().getIdentifier("song_"+i, "id", context.getPackageName());
            song[i-1] = (Button)view.findViewById(btnId);
            float acc = dbHelper.searchSong(db, i);
            if(song[i-1] != null){
                if(acc == 200.0f){
                    acc = 100.00f;
                    song[i-1].setTextColor(Color.RED);
                }
                else if(acc >= 100.0f){
                    acc -= 100.0f;
                    song[i-1].setTextColor(Color.BLUE);
                }else{
                    song[i-1].setTextColor(Color.BLACK);
                }
                String accText = accFormat.format(acc) + "%";
                song[i-1].setText(accText);
            }else{
                //Toast.makeText(context, "song data can not be found", Toast.LENGTH_SHORT).show();
            }
        }

    }

    // 버튼 배열 툴바 onClick
    public void buttonClicked(View view){

        // 이미지 버튼 가져오기
        ImageButton button_4b = (ImageButton) findViewById(R.id.imageButton_4b);
        ImageButton button_5b = (ImageButton) findViewById(R.id.imageButton_5b);
        ImageButton button_6b = (ImageButton) findViewById(R.id.imageButton_6b);
        ImageButton button_8b = (ImageButton) findViewById(R.id.imageButton_8b);
        button_4b.setSelected(false);
        button_5b.setSelected(false);
        button_6b.setSelected(false);
        button_8b.setSelected(false);

        if (view.getId() == findViewById(R.id.imageButton_4b).getId()) {
            curBtn = BUTTON.FOUR;
            view.setSelected(true);
            // Toast.makeText(this, "4B", Toast.LENGTH_SHORT).show();
        }
        else if (view.getId() == findViewById(R.id.imageButton_5b).getId()){
            curBtn = BUTTON.FIVE;
            view.setSelected(true);
            // Toast.makeText(this, "5B", Toast.LENGTH_SHORT).show();
        }
        else if (view.getId() == findViewById(R.id.imageButton_6b).getId()){
            curBtn = BUTTON.SIX;
            view.setSelected(true);
            // Toast.makeText(this, "6B", Toast.LENGTH_SHORT).show();
        }
        else if (view.getId() == findViewById(R.id.imageButton_8b).getId()){
            curBtn = BUTTON.EIGHT;
            view.setSelected(true);
            // Toast.makeText(this, "8B", Toast.LENGTH_SHORT).show();
        }
        refreshPage();
    }
    // 각 노래별 버튼 클릭
    public void songButtonClicked(View view){
        // id 받아오기
        int song_idx = Integer.parseInt(view.getResources().getResourceEntryName(view.getId()).split("_")[1]);
        // 수정모드일때
        // 곡 버튼 아이디 형식 song_idx
        if(isEditMode){
            float acc = 0.0f;
            try {
                acc = Float.parseFloat(((EditText) findViewById(R.id.getAcc)).getText().toString());
            }catch(Exception e){
                e.printStackTrace();
            }
            dbHelper.updateSong(db, song_idx, acc, isMaxCombo);
            refreshPage();
        }
        // 수정모드가 아닐 때
        else{
            // 정보 검색
            Cursor cursor = null;
            try{
                cursor = db.rawQuery("SELECT * FROM "
                                + DBHelper.TABLE_NAME + " WHERE _id="
                                + song_idx
                        ,null);
                cursor.moveToFirst();
            }catch(Exception e){
                e.printStackTrace();
            }
            if(cursor.getCount() == 0)
                Toast.makeText(context, "song db is empty on idx "+song_idx, Toast.LENGTH_SHORT).show();
            else{
                Toast.makeText(context,
                        cursor.getString(1) +" \ndifficulty: "
                        + cursor.getString(3) + " \ndlc: "
                        + cursor.getString(6), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Switch 리스너 설정
    class editSwitchListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b)
                isEditMode = true;
            else
                isEditMode = false;
        }
    }
    class maxComboSwitchListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b)
                isMaxCombo = true;
            else
                isMaxCombo = false;
        }
    }

}