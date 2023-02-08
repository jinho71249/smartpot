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
    private EditText whatTime;
    private EditText howLong;
    private  Switch lightSwitch;
    private String CHECK; //전등 스위치버튼 활성화 여부
    private Integer whattime; //전등을 언제 킬건지
    private Integer timelong; //얼마나 오래 킬건지

    FirebaseDatabase database = FirebaseDatabase.getInstance();//파이어베이스 실시간으로 정보 가져오기


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lightlayout);
        initContent();
        initListener();
    }

    private void initContent(){
        reservButton=findViewById(R.id.lightReservBtn);
        backButton=findViewById(R.id.lightBackBtn);
        whatTime=findViewById(R.id.lightwhatTime);
        howLong=findViewById(R.id.howLong);
        lightSwitch=findViewById(R.id.switch2);
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
                whattime=Integer.parseInt(whatTime.getText().toString());//전등을 언제 킬건지
                timelong=Integer.parseInt(howLong.getText().toString());//얼마나 오래 킬건지

                Intent intent = new Intent();
                intent.putExtra("lightWhatTime",whattime); //메인화면으로 값전달
                intent.putExtra("howLong",timelong);
                intent.putExtra("lightcheck",CHECK); // CHECK >>전등 스위치버튼 활성화 여부

                DatabaseReference myRef1 = database.getReference("LightData/lightcheck"); //파이어베이스로 값 전달
                myRef1.setValue(CHECK);
                DatabaseReference myRef2 = database.getReference("LightData/lightHowlong");
                myRef2.setValue(whattime);
                DatabaseReference myRef3 = database.getReference("LightData/lightWhattime");
                myRef3.setValue(timelong);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){ //스위치버튼 클릭 할때 동작
                if(isChecked)
                    CHECK="1";
                else
                    CHECK="0";
            }
        });

    }

}
