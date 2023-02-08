package com.example.smartpot;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.StringTokenizer;

public class MainActivity extends Activity {
    private String data;
    protected int num=-1;
    protected String potnames="";
    protected String potids="";

    FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readdata();
    }


    public void readdata() {//모든화분이름, 화분 갯수 읽어오기
        final Db db=(Db)getApplicationContext();

        DatabaseReference myRef0 = database.getReference("admin/potID");
        myRef0.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                potids=data;
                db.setPotIDs(data);
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        DatabaseReference myRef1 = database.getReference("admin/potName");
        myRef1.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                potnames=data;
                db.setPotNames(data);
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        DatabaseReference myRef2 = database.getReference("admin/potNum");
        myRef2.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                num = Integer.parseInt(data);
                db.setPotNum(num);
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        waitdata();

    }

    protected void waitdata(){
        final Db db=(Db)getApplicationContext();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(potids.equals("")||num==-1){
                    waitdata();
                }
                else {
                    StringTokenizer tokens1 = new StringTokenizer(potnames, "#");

                    while (tokens1.hasMoreTokens()) {
                        db.setNames(tokens1.nextToken("#"));
                    }
                    //db.setSelectedPot(db.getNames(0));
                    //Toast.makeText(getApplicationContext(), Integer.toString(i), Toast.LENGTH_SHORT).show(); //테스트용

                    StringTokenizer tokens2 = new StringTokenizer(potids, "#");
                    while (tokens2.hasMoreTokens()) {
                        db.setIds(tokens2.nextToken("#"));
                    }
                    db.setSelectedPot(db.getIds(0));
                    if(num==0){
                        Intent intent = new Intent(getBaseContext(), WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(getBaseContext(), StateActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }
        }, 1000);
    }




}