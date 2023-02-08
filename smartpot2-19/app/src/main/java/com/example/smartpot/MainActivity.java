package com.example.smartpot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private ImageButton manageButton;
    private ImageButton searchButton;
    private ImageButton waterButton;
    private ImageButton lightButton;
    //private TextView test;
    private String data;
    private String mngCheck="1";
    private Button bluetoothbtn;
    private String tmp2;
    private String tmp3;


    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initContent();
        initListener();
       // readdata();



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==101){
            if(resultCode==RESULT_OK){
                String tmp1=data.getStringExtra("plantname");

                mngCheck=data.getStringExtra("managecheck");
            }
        }
        if(requestCode==104){
            if(resultCode==RESULT_OK){
                int lGetTime=data.getIntExtra("lGetTime",1);
                tmp2=Integer.toString(lGetTime);
                int lGetTimer=data.getIntExtra("lGetTimer",1);
                tmp3=Integer.toString(lGetTimer);
                Toast.makeText(MainActivity.this, Integer.toString(lGetTime), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseReference myRef1 = database.getReference("LightData/lGetTime");
                        myRef1.setValue(tmp2);

                    }
                },lGetTime*1000);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseReference myRef2 = database.getReference("LightData/lGetTimer");
                        myRef2.setValue(tmp3);

                    }
                },(lGetTimer+lGetTime)*1000);
            }

        }
   }

    private void initCatcher() {
        // 오류 발생시 프로그램을 강제로 종료시킵니다.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
                System.exit(0);
            }
        });
    }

    private void initContent() {//xml하고 변수 연동
        manageButton=findViewById(R.id.manageButton);
        searchButton=findViewById(R.id.searchButton);
        waterButton=findViewById(R.id.waterButton);
        lightButton=findViewById(R.id.lightButton);
        bluetoothbtn=findViewById(R.id.blueboothbtn);
        //test=findViewById(R.id.test);
    }

    private void initListener(){//버튼 설정
        manageButton.setOnClickListener(new View.OnClickListener(){//관리모드버튼
            public void onClick(View v){
                Intent intent=new Intent(getApplicationContext(), ManageActivity.class);
                startActivityForResult(intent, 101);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener(){//식물인식버튼
            public void onClick(View v){
                Intent intent=new Intent(getApplicationContext(), SearchActivity.class);
                startActivityForResult(intent, 102);
            }
        });

        waterButton.setOnClickListener(new View.OnClickListener(){//수분공급버튼
            public void onClick(View v){
                if(mngCheck.equals("1")) {
                    Intent intent=new Intent(getApplicationContext(), WaterActivity.class);
                    startActivityForResult(intent, 103);
                }
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage("관리버튼이 off되어있습니다.");
                    alert.show();
                }

            }
        });

        lightButton.setOnClickListener(new View.OnClickListener(){//전등설정버튼
            public void onClick(View v){
                if(mngCheck.equals("1")) {
                    Intent intent = new Intent(getApplicationContext(), LightActivity.class);
                    startActivityForResult(intent, 104);
                }
                else
                    Log.e("관리버튼","관리버튼이 off로 되어있습니다.");
            }
        });

        bluetoothbtn.setOnClickListener(new View.OnClickListener(){//블루투스
            public void onClick(View v){
                Intent intent=new Intent(getApplicationContext(), BluetoothActivity.class);
                startActivityForResult(intent, 105);
            }
        });
    }

   // public void readdata(){//
    //    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //    DatabaseReference myRef = database.getReference("data/Date");
//
    //    myRef.addValueEventListener(new ValueEventListener() {

    //        @Override
     //       public void onDataChange(DataSnapshot dataSnapshot) {
     //           data = dataSnapshot.getValue(String.class);
      //          //test.setText(null);
                //test.setText(data);
      //      }
      //      @Override
      //      public void onCancelled(DatabaseError error) {
      //      }
     //   });
   // }


}


//<TextView
//            android:id="@+id/test"
//            android:layout_width="wrap_content"
//            android:layout_height="wrap_content"
//            android:layout_weight="1"/>