package com.example.smartpot;


import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class AddpotActivity extends AppCompatActivity {
    private EditText newName;
    private ImageButton addBtn;
    private ImageButton backBtn;
    private String newname;
    private int potNum;
    private String lastID;


    FirebaseDatabase database = FirebaseDatabase.getInstance();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpotlayout);
        final Db db=(Db)getApplication();

        initListener();
    }

    private void initListener(){
        newName=findViewById(R.id.newname);
        addBtn=findViewById(R.id.addBtn);
        backBtn=findViewById(R.id.backBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Db db = (Db) getApplication();

                newname = newName.getText().toString();
                if (newname.contains("#")) {
                    Toast.makeText(getApplicationContext(), "이름에 # 이 포함될 수 없습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    potNum = db.getPotNum();//화분 갯수를 가져온다.

                    if (potNum > 0) {//화분갯수가 1이상이면
                        lastID = db.getIds(potNum - 1);
                        lastID = Integer.toString(Integer.parseInt(lastID) + 1);
                    } else {//화분갯수가 0이면
                        lastID = "1";
                    }
                    potNum++;                               //화분을 등록하면 갯수가 1더해진다
                    db.setIds(lastID);
                    db.setNames(newname);


                    DatabaseReference myRef7 = database.getReference("admin/potName");
                    myRef7.setValue(db.getPotNames() + newname + "#");//추가된 화분이름 파이어베이스에저장
                    DatabaseReference myRef8 = database.getReference("admin/potNum");
                    myRef8.setValue(Integer.toString(potNum)); //+1된 화분갯수 파이어베이스에 저장
                    DatabaseReference myRef9 = database.getReference("admin/potID");
                    myRef9.setValue(db.getPotIDs() + lastID + "#");//추가된 화분아이디 파이어베이스에저장

                    DatabaseReference myRef10 = database.getReference(lastID + "/WaterData/watercheck");
                    myRef10.setValue("0");   //화분 상태, 관리에 필요한 키값(디렉토리)을 만들고 "1"로 최기화
                    DatabaseReference myRef11 = database.getReference(lastID + "/autoControl/autoWpercent");
                    myRef11.setValue("0");  //이하 생략
                    DatabaseReference myRef12 = database.getReference(lastID + "/autoControl/autoWcontent");
                    myRef12.setValue("0");
                    DatabaseReference myRef13 = database.getReference(lastID + "/autoControl/lightTmp");
                    myRef13.setValue("0");
                    DatabaseReference myRef14 = database.getReference(lastID + "/potState/AHumi");
                    myRef14.setValue("0");
                    DatabaseReference myRef15 = database.getReference(lastID + "/potState/temp");
                    myRef15.setValue("0");
                    DatabaseReference myRef16 = database.getReference(lastID + "/potState/lux");
                    myRef16.setValue("0");
                    DatabaseReference myRef17 = database.getReference(lastID + "/potState/SHumi");
                    myRef17.setValue("0");
                    DatabaseReference myRef18 = database.getReference(lastID + "/potState/Wlevel");
                    myRef18.setValue("0");
                    DatabaseReference myRef20 = database.getReference(lastID + "/manualControl/lampSwitch");
                    myRef20.setValue("0");
                    DatabaseReference myRef21 = database.getReference(lastID + "/ControlSwitch");
                    myRef21.setValue("0");
                    DatabaseReference myRef22 = database.getReference(lastID + "/manualControl/pumpSwitch");
                    myRef22.setValue("0");
                    DatabaseReference myRef23 = database.getReference(lastID + "/manualControl/waterContent");
                    myRef23.setValue("0");
                    DatabaseReference myRef24 = database.getReference(lastID + "/WaterData/watercheck");
                    myRef24.setValue("0");
                    DatabaseReference myRef25 = database.getReference(lastID + "/autoControl/lightLux");
                    myRef25.setValue("0");
                    Intent intent = new Intent();
                    setResult(200, intent);
                    finish();
                }
            }
        });//추가 버튼

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });//back버튼


    }





}
