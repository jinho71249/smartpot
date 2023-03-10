package com.example.smartpot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MngActivity extends AppCompatActivity {
    private TextView selectedName;
    private LinearLayout autolayout;
    private LinearLayout manuallayout;
    private ImageView modeImg;
    private ImageButton glassImg;
    private ImageButton rightBtn;
    private ImageButton leftBtn;
    private ImageButton autoBtn;
    private ImageButton manualBtn;
    private ImageButton waterBtn;
    private ImageButton lightBtn;
    private ImageButton settingBtn;
    private Switch alarmSwitch;
    protected String modecheck;
    protected String lightcheck;
    protected String item[];
    protected String selected;
    private String alarmcheck;
    protected int num;
    protected int selectedNum;
    private String data;
    protected int ahumi;
    private Intent foregroundServiceIntent;




    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mnglayout);
        initContent();
        initListener();
    }

    private void initContent(){
        glassImg=findViewById(R.id.glassImg);
        alarmSwitch=findViewById(R.id.alarmSwitch);
        selectedName=findViewById(R.id.selectedName);
        modeImg=findViewById(R.id.modeImg);
        rightBtn=findViewById(R.id.rightBtn);
        leftBtn=findViewById(R.id.leftBtn);
        autoBtn=findViewById(R.id.autoBtn);
        manualBtn=findViewById(R.id.manualBtn);
        autolayout=findViewById(R.id.autolayout);
        manuallayout=findViewById(R.id.manuallayout);
        waterBtn=findViewById(R.id.waterBtn);
        lightBtn=findViewById(R.id.lightBtn);
        settingBtn=findViewById(R.id.settingBtn);

    }


    private void initListener(){
        final Db db=(Db)getApplication();
        Intent intent=getIntent();

        selected =db.getSelectedPot();

        num=db.getPotNum();
        item=new String[num];
        for(int i=0;i<num;i++){
            item[i]=db.getIds(i);
        }
        for(int i=0;i<num;i++){
            if(selected.equals(item[i]))
                selectedNum=i;
        }
        selectedName.setText(db.getNames(selectedNum));
        //Toast.makeText(getApplicationContext(), db.getNames(selectedNum), Toast.LENGTH_SHORT).show(); //????????????

        DatabaseReference myRef1 = database.getReference(selected + "/ControlSwitch");//???,????????????
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                modecheck=data;
                if(modecheck!=null) {//modecheck??? null??? ?????????
                    if(modecheck.equals("0")){//??????manual
                        modeImg.setBackgroundResource(R.mipmap.mode_manual);
                        manualBtn.setBackgroundResource(R.mipmap.manual2);
                        autoBtn.setBackgroundResource(R.mipmap.auto1);
                        manuallayout.setVisibility(View.VISIBLE);
                        autolayout.setVisibility(View.INVISIBLE);

                    }
                    else {//??????auto
                        modeImg.setBackgroundResource(R.mipmap.mode_auto);
                        manualBtn.setBackgroundResource(R.mipmap.manual1);
                        autoBtn.setBackgroundResource(R.mipmap.auto2);
                        manuallayout.setVisibility(View.INVISIBLE);
                        autolayout.setVisibility(View.VISIBLE);

                        DatabaseReference myRef10 = database.getReference(selected + "/manualControl/lampSwitch");//?????? ?????????
                        myRef10.setValue("0");
                        DatabaseReference myRef11 = database.getReference(selected + "/manualControl/pumpSwitch");//?????? ?????????
                        myRef11.setValue("0");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {//??????????????? ???????????? ?????????
            }
        });


        DatabaseReference myRef2 = database.getReference(selected + "/manualControl/lampSwitch");//?????? ?????????
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                if(data!=null) {
                    lightcheck=data;
                    if (lightcheck.equals("0"))
                        lightBtn.setBackgroundResource(R.mipmap.lightbtn_off);
                    else
                        lightBtn.setBackgroundResource(R.mipmap.lightbtn_on);
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {//??????????????? ???????????? ?????????
            }
        });

        DatabaseReference myRef3 = database.getReference(selected + "/potState/AHumi");//????????????
        myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                if(data!=null)
                    ahumi=Integer.parseInt(data);
                else
                    ahumi=0;

            }
            @Override
            public void onCancelled(DatabaseError error) {//??????????????? ???????????? ?????????
            }
        });



        glassImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), StateActivity.class);
                startActivity(intent);
            }
        });//????????????????????? ????????? ??????

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNum++;
                if(selectedNum==num)
                    selectedNum=0;
                selected =item[selectedNum];
                db.setSelectedPot(selected);
                selectedName.setText(db.getNames(selectedNum));
                initListener();

            }
        });//?????? ?????? ??????

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedNum==0)
                    selectedNum=num-1;
                else
                    selectedNum--;
                selected =item[selectedNum];
                db.setSelectedPot(selected);
                selectedName.setText(db.getNames(selectedNum));
                initListener();


            }
        });//?????? ?????? ??????

        autoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference myRef1 = database.getReference(selected + "/ControlSwitch");//???,????????????
                if(modecheck!=null) {
                    if (modecheck.equals("0")) {//????????????
                        modecheck = "1";//????????????
                        myRef1.setValue("1");//????????????????????????
                    }
                }
            }
        });//????????????

        manualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference myRef1 = database.getReference(selected + "/ControlSwitch");//???,????????????
                if(modecheck!=null)
                    if(modecheck.equals("1")){//????????????
                        modecheck="0";//????????????
                        myRef1.setValue("0");
                    }
            }
        });//????????????





        DatabaseReference myRef10 = database.getReference("admin/potAlarm");
        myRef10.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//?????????????????? ?????? ????????? ???????????? ?????????
                String data = dataSnapshot.getValue(String.class);//??? ????????????
                alarmcheck=data;
                if (alarmcheck.equals("1"))
                    alarmSwitch.setChecked(true);
                else
                    alarmSwitch.setChecked(false);
            }
            @Override
            public void onCancelled(DatabaseError error) {//??????????????? ???????????? ?????????
            }
        });

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){//????????? ?????? ??????
                DatabaseReference myRef1 = database.getReference("admin/potAlarm");
                if(isChecked) {
                    alarmcheck="1";
                    if (null == NotiService.serviceIntent) {
                        foregroundServiceIntent = new Intent(getApplicationContext(), NotiService.class);
                        startService(foregroundServiceIntent);
                        Toast.makeText(getApplicationContext(), "????????? ??????????????????", Toast.LENGTH_LONG).show();
                    } else {
                        foregroundServiceIntent = NotiService.serviceIntent;
                        Toast.makeText(getApplicationContext(), "?????? ?????????????????????", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    alarmcheck="0";
                    stopService(foregroundServiceIntent);
                    foregroundServiceIntent = null;
                    NotiService.serviceIntent=null;
                    Toast.makeText(getApplicationContext(), "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                }

                myRef1.setValue(alarmcheck);
            }
        });

        waterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), WaterActivity.class);//???????????? ???????????? ??????
                //Toast.makeText(getApplicationContext(), Integer.toString(ahumi), Toast.LENGTH_SHORT).show();
                intent.putExtra("ahumi", ahumi);
                intent.putExtra("selectedNum", selectedNum);
                startActivity(intent);
            }
        });//???????????? ??????

        lightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference myRef1 = database.getReference(selected + "/manualControl/lampSwitch");//?????? ?????????
                if(lightcheck.equals("1")) {
                    myRef1.setValue("0");
                }
                else {
                    myRef1.setValue("1");
                }
            }
        });//????????????

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), AutoActivity.class);//???????????? ???????????? ??????
                intent.putExtra("name", db.getNames(selectedNum));
                intent.putExtra("ID", db.getIds(selectedNum));
                startActivity(intent);
            }
        });//????????????



    }//initListener??????



}

