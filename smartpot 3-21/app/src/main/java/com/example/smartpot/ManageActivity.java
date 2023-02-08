package com.example.smartpot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ManageActivity extends AppCompatActivity {
    private ImageButton backButton;
    private ImageButton saveButton;
    private EditText plantName;
    private Switch switchButton;
    private String CHECK;//관리모드 온오프 여부
    private String name;//식물이름
    private String data;//파이어베이스에서 값 가져올때 임시변수
    private TextView AhumText;//습도
    private TextView tempText;//온도
    private TextView ShumText;//토양습도
    private TextView waterText;//수위

    FirebaseDatabase database = FirebaseDatabase.getInstance();





    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.managelayout);


        initContent();
        initListener();
        readData();

    }

    private void initContent(){ //xml과 동기화
        backButton=findViewById(R.id.back);
        saveButton=findViewById(R.id.save);
        switchButton=findViewById(R.id.switch1);
        plantName=findViewById(R.id.plantname);
        AhumText=findViewById((R.id.ahumText));
        ShumText=findViewById((R.id.shumText));
        tempText=findViewById((R.id.tempText));
        waterText=findViewById((R.id.waterText));

    }

    private void initListener(){//버튼이나 textview동작
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//뒤로가기 버튼
                Intent intent = new Intent();
                setResult(RESULT_FIRST_USER, intent);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//저장버튼
                name=plantName.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("managecheck", CHECK);
                intent.putExtra("plantname", name);
                FirebaseDatabase database = FirebaseDatabase.getInstance();

                setResult(RESULT_OK, intent);
                DatabaseReference myRef1 = database.getReference("MngData/plantName");//식물이름 파이어베이스로보내기
                myRef1.setValue(name);
                DatabaseReference myRef2 = database.getReference("MngData/switch"); //관리모드 온인지 오프인지 보내기
                myRef2.setValue(CHECK);

                finish();
            }
        });

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){//스위치 버튼 동작
                if(isChecked)
                    CHECK="1";
                else
                    CHECK="0";
            }
        });


        Intent intent=getIntent(); //스위치버튼 파이어베이스에서 불러와서 설정
        CHECK=intent.getStringExtra("switch");
        //Toast.makeText(ManageActivity.this, CHECK, Toast.LENGTH_SHORT).show(); 테스트용
        DatabaseReference myRef2 = database.getReference("MngData/switch");
        myRef2.addValueEventListener(new ValueEventListener() {//습도
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//파이어베이스 값이 변하면 동작하는 메소드
                String data = dataSnapshot.getValue(String.class);//값 가져오기
                CHECK=data;//관리스위치 정보저장
                if(CHECK.equals("1"))
                    switchButton.setChecked(true);
                else
                    switchButton.setChecked(false);

            }
            @Override
            public void onCancelled(DatabaseError error) {//에러났을때 동작하는 메소드
            }
        });



    }

    public void readData(){ //상태값 파이어베이스에서 읽어오기

        DatabaseReference myRef3 = database.getReference("data/AHumi");//습도
        DatabaseReference myRef4 = database.getReference("data/Temp");//온도
        DatabaseReference myRef5 = database.getReference("data/SHumi");//토양습도
        DatabaseReference myRef6 = database.getReference("data/Water_content");//수위

        myRef3.addValueEventListener(new ValueEventListener() {//습도

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//파이어베이스 값이 변하면 동작하는 메소드
                data = dataSnapshot.getValue(String.class);//값 가져오기

                AhumText.setText(data);//textview에 값 설정하기
            }
            @Override
            public void onCancelled(DatabaseError error) {//에러났을때 동작하는 메소드
            }
        });



        myRef4.addValueEventListener(new ValueEventListener() {//온도

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);

                tempText.setText(data);
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });



        myRef5.addValueEventListener(new ValueEventListener() {//토양습도

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);

                ShumText.setText(data);
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });


        myRef6.addValueEventListener(new ValueEventListener() {//수위

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);

                waterText.setText(data);
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }




}
