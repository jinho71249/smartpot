package com.example.smartpot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LightActivity extends AppCompatActivity {
    private ImageButton reservButton;
    private ImageButton backButton;
    private EditText lgetTimer;
    private EditText lgetTime;

    private Integer lGetTimer; //전등을 언제 킬건지
    private Integer lGetTime; //얼마나 오래 킬건지



    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lightlayout);
        initContent();
        initListener();
    }

    private void initContent(){
        reservButton=findViewById(R.id.lightReservBtn);
        backButton=findViewById(R.id.lightBackBtn);
        lgetTimer=findViewById(R.id.lgetTimer);
        lgetTime=findViewById(R.id.lgetTime);
    }

    private void initListener(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//뒤로가기 버튼
                Intent out = new Intent();
                setResult(RESULT_CANCELED, out);
                finish();
            }
        });

        reservButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//예약버튼
                lGetTimer=Integer.parseInt(lgetTimer.getText().toString());//전등을 언제 킬건지
                lGetTime=Integer.parseInt(lgetTime.getText().toString());//얼마나 오래 킬건지

                Intent intent = new Intent();
                intent.putExtra("lGetTime",lGetTimer); //메인화면으로 값전달
                intent.putExtra("lGetTimer",lGetTime);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

}


//DatabaseReference myRef1 = database.getReference("LightData/lightcheck"); //파이어베이스로 값 전달
//                myRef1.setValue(CHECK);
//                DatabaseReference myRef2 = database.getReference("LightData/lightHowlong");
//                myRef2.setValue(lGetTime);
//                DatabaseReference myRef3 = database.getReference("LightData/lightWhattime");
//                myRef3.setValue(lGetTimer);