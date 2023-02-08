package com.example.smartpot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;


public class ReceiveDaily extends BroadcastReceiver {
    private String data;
    protected String ahumi="";
    protected String temp="";
    protected String lux="";
    protected String shumi="";
    protected String date;
    protected String concatdata="finish";
    protected String potnum;
    protected int num;
    protected int i;
    protected String oldday;

    FirebaseDatabase database = FirebaseDatabase.getInstance();



    public void onReceive(Context context, Intent intent) {
        deleteold();//전주 데이터는 삭제됨

        DatabaseReference myRef0 = database.getReference("admin/potNum");//등록된 화분 갯수
        myRef0.addListenerForSingleValueEvent(new ValueEventListener() {//습도
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                potnum=data;
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        i=1;
        readdata();
    }

    protected void readdata(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(potnum==null){
                    readdata();
                }
                else {
                    num = Integer.parseInt(potnum);
                    if(concatdata.equals("finish")) {//
                        concatdata="";

                        DatabaseReference myRef1 = database.getReference(i+"/potState/AHumi");//습도
                        DatabaseReference myRef2 = database.getReference(i+"/potState/temp");//온도
                        DatabaseReference myRef3 = database.getReference(i+"/potState/SHumi");//토양습도
                        DatabaseReference myRef4 = database.getReference(i+"/potState/lux");//조도


                        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {//습도
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                data = dataSnapshot.getValue(String.class);
                                ahumi = data;
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        });

                        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {//온도
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                data = dataSnapshot.getValue(String.class);
                                temp = data;
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        });
                        myRef3.addListenerForSingleValueEvent(new ValueEventListener() {//토양습도
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                data = dataSnapshot.getValue(String.class);
                                shumi = data;
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        });
                        myRef4.addListenerForSingleValueEvent(new ValueEventListener() {//조도
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                data = dataSnapshot.getValue(String.class);
                                lux = data;
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        });
                        waitdata();
                    }//if문

                }//else potnum이 정상적으로 읽혔으면 실행

            }//run

        }, 1000);//handler

    }//readdata

    protected void waitdata(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(ahumi.equals("")||temp.equals("")||shumi.equals("")||lux.equals("")) {
                    waitdata();
                }
                else {

                    SimpleDateFormat format = new SimpleDateFormat("MM-dd");
                    Calendar time = Calendar.getInstance();
                    date = format.format(time.getTime());

                    concatdata = concatdata.concat(ahumi + "#");
                    concatdata = concatdata.concat(temp + "#");
                    concatdata = concatdata.concat(shumi + "#");
                    concatdata = concatdata.concat(lux + "#");

                    DatabaseReference myRef = database.getReference(i + "/" + date);
                    myRef.setValue(concatdata);

                    concatdata="finish";//데이터 저장 완료string 저장. 다음 화분 데이터 수집 시작
                    i++;//다음 화분
                    if(i<=num)
                        readdata();
                }

            }
        }, 1000);
    }

    private void deleteold(){
        SimpleDateFormat format=new SimpleDateFormat("MM-dd");
        Calendar time=Calendar.getInstance();
        time.add(Calendar.DATE, -6);
        for(int i=0;i<7;i++) {//이전 데이터는 삭제
            time.add(Calendar.DATE, -1);
            oldday = format.format(time.getTime());
            DatabaseReference myRef11 = database.getReference(oldday);
            myRef11.setValue(null);
        }
    }






}
