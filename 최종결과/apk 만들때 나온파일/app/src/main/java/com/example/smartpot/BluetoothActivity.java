package com.example.smartpot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class BluetoothActivity extends AppCompatActivity {

    private BluetoothSPP bt;
    private Button btnConnect;
    private Button btnSend;
    private Button callid;

    private EditText internetID;
    private EditText internetPW;
    private String id;
    private String pw;
    private String ID;
    private String data;
    private int position;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);
        initContent();


        bt = new BluetoothSPP(this); //Initializing

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            //finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(BluetoothActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });


        btnConnect.setOnClickListener(new View.OnClickListener() {//연결시도
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }



    private void initContent(){
        btnConnect = findViewById(R.id.btnConnect);
        btnSend = findViewById(R.id.btnSend);
        internetID=findViewById(R.id.internetID);
        internetPW=findViewById(R.id.internetPW);
        callid=findViewById(R.id.callid);
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                initListener();
                getwifi();
            }
        }
    }




    public void initListener() {

        btnSend.setOnClickListener(new View.OnClickListener() {//데이터 전송
            public void onClick(View v) {
                final Db db=(Db)getApplication();
                Intent intent=getIntent();
                position=intent.getIntExtra("position", -1);
                ID =db.getIds(position);

                id=internetID.getText().toString();
                pw=internetPW.getText().toString();
                DatabaseReference myRef5 = database.getReference("admin/wifiID");
                myRef5.setValue(id); //wifi 아이디 비밀번호 저장
                DatabaseReference myRef6 = database.getReference("admin/wifiPW");
                myRef6.setValue(pw); //wifi 아이디 비밀번호 저장

                bt.send(id +"/"+pw+"#"+ ID +"$", true);//블루투스를 통해 아두이노로 정보전달
                Toast.makeText(getApplicationContext(), "블루투스 통신 성공.", Toast.LENGTH_SHORT).show(); //테스트용
                finish();
            }
        });

        callid.setOnClickListener(new View.OnClickListener() {//데이터 전송
            public void onClick(View v) {
                getwifi();
            }
        });


    }

    public void getwifi(){
        DatabaseReference myRef1 = database.getReference("admin/wifiID");
        myRef1.addValueEventListener(new ValueEventListener() {//습도

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//파이어베이스 값이 변하면 동작하는 메소드
                data = dataSnapshot.getValue(String.class);//값 가져오기
                internetID.setText(data);//textview에 값 설정하기
            }
            @Override
            public void onCancelled(DatabaseError error) {//에러났을때 동작하는 메소드
                Toast.makeText(getApplicationContext(), "통신 불량", Toast.LENGTH_SHORT).show();
            }
        });
        DatabaseReference myRef2 = database.getReference("admin/wifiPW");
        myRef2.addValueEventListener(new ValueEventListener() {//습도

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//파이어베이스 값이 변하면 동작하는 메소드
                data = dataSnapshot.getValue(String.class);//값 가져오기
                internetPW.setText(data);//textview에 값 설정하기
            }
            @Override
            public void onCancelled(DatabaseError error) {//에러났을때 동작하는 메소드
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                initListener();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}


