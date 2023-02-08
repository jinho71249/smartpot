package com.example.smartpot;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.StringTokenizer;

public class StateActivity extends AppCompatActivity {
    private Spinner spinner;
    private ImageButton setting;
    private ImageButton menu;
    private ImageButton rightBtn;
    private ImageButton leftBtn;
    protected String selected;//식물이름
    private String data;//파이어베이스에서 값 가져올때 임시변수
    private TextView mode;
    private TextView ahumi;//습도
    private TextView temp;//온도
    private TextView Lux;//조도
    private TextView shumi;//토양습도
    private TextView wLevel;//수위
    protected int wlv;
    protected int lux;
    private String item[]; //식물 아이디 배열
    private String id[];  //식물 이름 배열
    protected int num; //화분갯수
    protected int selectedNum;
    protected int position;
    private Intent foregroundServiceIntent;


    FirebaseDatabase database = FirebaseDatabase.getInstance();


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statelayout);
        final Db db=(Db)getApplication();
        //Toast.makeText(getApplicationContext(), db.getSelectedPot(), Toast.LENGTH_SHORT).show(); //테스트용

        initContent();
        initListener();

        selected =db.getSelectedPot();

        readData();
        if(num!=0)
            settimemethod();


        //Toast.makeText(getApplicationContext(), "만드는중", Toast.LENGTH_SHORT).show(); //테스트용
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Db db=(Db)getApplication();
        if(num>1) {//등록된 화분이 2개 이상일때만 작동
            for (int i = 0; i < num; i++) {
                if (db.getSelectedPot().equals(id[i]))
                    selectedNum = i;
            }
            spinner.setSelection(selectedNum);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==201) {
            if (resultCode == 200) {
                Toast.makeText(getApplicationContext(), "화분이 추가됐습니다.", Toast.LENGTH_SHORT).show(); //테스트용
                readData();

                final DatabaseReference myRef0 = database.getReference("admin/potAlarm");
                myRef0.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String tmpcheck = dataSnapshot.getValue(String.class);
                        if(tmpcheck.equals("1")) {
                            myRef0.setValue("0");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

                Intent intent=new Intent(getApplicationContext(), BluetoothActivity.class);
                intent.putExtra("position", num-1);
                startActivity(intent);

            }
        }
    }


    private void initContent(){ //xml과 동기화
        spinner=findViewById((R.id.spinner));
        mode=findViewById(R.id.mode);
        menu=findViewById(R.id.menu);
        rightBtn=findViewById(R.id.rightBtn);
        leftBtn=findViewById(R.id.leftBtn);
        ahumi=findViewById((R.id.ahumi));
        temp=findViewById((R.id.temp));
        Lux =findViewById((R.id.lux));
        shumi=findViewById((R.id.shumi));
        wLevel=findViewById((R.id.wLevel));
        setting=findViewById(R.id.setting);
    }

    private void initListener(){//버튼이나 textview동작
        final Db db=(Db)getApplication();

        setting.setOnClickListener(new View.OnClickListener(){//화분 제어모드로 이동
            public void onClick(View v){
                Intent intent=new Intent(getApplicationContext(), MngActivity.class);
                intent.putExtra("wlv", wlv);
                startActivity(intent);
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNum++;
                if(selectedNum==num)
                    selectedNum=0;
                selected =item[selectedNum];
                db.setSelectedPot(selected);
                //selectedName.setText(db.getNames(selectedNum));
                onResume();

            }
        });//다음 화분 버튼

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedNum==0)
                    selectedNum=num-1;
                else
                    selectedNum--;
                selected =item[selectedNum];
                db.setSelectedPot(selected);
                //selectedName.setText(db.getNames(selectedNum));
                onResume();


            }
        });//이전 화분 버튼

        menu.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final CharSequence[] menuitem={"화분 추가","화분 삭제", "와이파이 연동", "식물 검색", "데이터 일지" };
                AlertDialog.Builder menubox=new AlertDialog.Builder(StateActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                menubox.setItems(menuitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){//화분추가
                            Intent intent = new Intent(getApplicationContext(), AddpotActivity.class);
                            startActivityForResult(intent, 201);
                        }

                        else if(which==2){//와이파이 등록
                            final Db item=(Db)getApplication();
                            if(item.getPotNum()<1){//등록된 화분이 1개도 없으면
                                Toast.makeText(getApplicationContext(), "화분을 먼저 추가해 주세요.", Toast.LENGTH_SHORT).show(); //테스트용
                            }
                            else {//등록된 화분이 1개 이상이면
                                final CharSequence[] setitem = new CharSequence[item.getPotNum()];

                                for (int i = 0; i < item.getPotNum(); i++)
                                    setitem[i] = item.getNames(i); //화분 목록을 불러옵니다.

                                AlertDialog.Builder setbox = new AlertDialog.Builder(StateActivity.this,
                                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                                setbox.setTitle("와이파이를 등록시킬 화분을 선택하세요.");
                                setbox.setItems(setitem, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent=new Intent(getApplicationContext(), BluetoothActivity.class);
                                        intent.putExtra("position", which);
                                        startActivity(intent);
                                    }
                                }).show();
                            }
                        }

                        else if(which==3){//식물 인식
                            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                            startActivity(intent);
                        }

                        else if(which==4){//그래프
                            final Db item=(Db)getApplication();
                            if(item.getPotNum()>=1) {
                                final CharSequence[] items = new CharSequence[item.getPotNum()];

                                for (int i = 0; i < item.getPotNum(); i++)
                                    items[i] = item.getNames(i);
                                AlertDialog.Builder selectbox = new AlertDialog.Builder(StateActivity.this,
                                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                                selectbox.setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        position = which; //지울 이름 넘버 저장

                                        Intent intent = new Intent(getApplicationContext(), DataGraphActivity.class);
                                        intent.putExtra("position", which);
                                        startActivity(intent);

                                    }//목록 선택시 작동
                                }).show();//선택 목록 박스


                            }else{
                                Toast.makeText(getApplicationContext(), "등록된 화분이 0개 입니다.", Toast.LENGTH_SHORT).show();
                            }

                        }

                        else if(which==1){//등록된 화분 삭제
                            final Db item=(Db)getApplication();
                            if(item.getPotNum()>=1) {//등록된 화분이 1개 이상이면
                                final CharSequence[] deleteitem = new CharSequence[item.getPotNum()];

                                for (int i = 0; i < item.getPotNum(); i++)
                                    deleteitem[i] = item.getNames(i); //선택된 이름을 지울이름 변수에 넣습니다.
                                AlertDialog.Builder deletebox = new AlertDialog.Builder(StateActivity.this,
                                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                                deletebox.setItems(deleteitem, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        position=which; //지울 이름 넘버 저장
                                        AlertDialog.Builder confirmbox=new AlertDialog.Builder(StateActivity.this);
                                        confirmbox.setTitle("정말 삭제하시겠습니까?");
                                        confirmbox.setPositiveButton("네", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                String deleteid = item.getIds(position);//1번이 id 관한 변수
                                                //String deletename=item.getNames(which);//2번이 name관한 변수
                                                int i;//반복을 위한 변수
                                                String result1 = "#";//지운 이름을 뺀 나머지 이름들이 들어갈 변수
                                                String result2 = "#";
                                                String tmp = Integer.toString(item.getPotNum() - 1);//임시 화분갯수 변수, 갯수를 -1해줍니다.

                                                DatabaseReference myRef10 = database.getReference(deleteid);
                                                myRef10.setValue(null);//파이어베이스에서 지울화분의 데이터를 모조리 삭제합니다.

                                                DatabaseReference myRef11 = database.getReference("admin/potNum");
                                                myRef11.setValue(tmp);//-1한 화분 갯수를 파이어베이스에 저장

                                                for (i = 0; i < item.getPotNum(); i++) {
                                                    if (i != position)//그 위치가 아닌 나머지 이름을 차래대로 붙여줍니다.
                                                        result1 = result1.concat(item.getIds(i) + "#");
                                                    item.setPotIDs(result1);
                                                }
                                                DatabaseReference myRef12 = database.getReference("admin/potID");
                                                myRef12.setValue(item.getPotIDs());//다 붙인 모든 화분아이디을 파이어베이스에 저장합니다.

                                                for (i = 0; i < item.getPotNum(); i++) {
                                                    if (i != position)//그 위치가 아닌 나머지 이름을 차래대로 붙여줍니다.
                                                        result2 = result2.concat(item.getNames(i) + "#");
                                                    item.setPotNames(result2);
                                                }
                                                DatabaseReference myRef13 = database.getReference("admin/potName");
                                                myRef13.setValue(item.getPotNames());//다 붙인 모든 화분이름을 파이어베이스에 저장합니다.

                                                item.setPotNum(Integer.parseInt(tmp)); //화분갯수 클라스 속성을 저장합니다
                                                item.setIdnum(0);   //등록된 화분갯수 배열을 다시 처음부터
                                                item.setNamesnum(0);

                                                StringTokenizer tokens1 = new StringTokenizer(item.getPotIDs(), "#");
                                                while (tokens1.hasMoreTokens()) {
                                                    item.setIds(tokens1.nextToken("#"));//토큰으로 분리해줍니다.
                                                }
                                                item.setSelectedPot(item.getIds(0));//등록된것중 첫번째것을 선택된화분으로
                                                selectedNum = 0;

                                                StringTokenizer tokens2 = new StringTokenizer(item.getPotNames(), "#");
                                                while (tokens2.hasMoreTokens()) {
                                                    item.setNames(tokens2.nextToken("#"));//토큰으로 분리해줍니다.
                                                }


                                                readData(); // 마지막으로 다시 데이터를 읽어 옵니다.
                                                //selectedName.setText(db.getNames(selectedNum));
                                                final DatabaseReference myRef0 = database.getReference("admin/potAlarm");
                                                myRef0.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String tmpcheck = dataSnapshot.getValue(String.class);
                                                        if (tmpcheck.equals("1")) {
                                                            myRef0.setValue("0");
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });

                                        confirmbox.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        confirmbox.show();//다이얼 활성화

                                    }//목록 선택시 작동
                                }).show();//삭제 목록 박스

                            }
                            else {
                                Toast.makeText(getApplicationContext(), "등록된 화분이 0개미만으로 될수 없습니다.", Toast.LENGTH_SHORT).show();
                                //화분개수가 0인데 삭제하려는 경우
                            }
                        }
                    }
                }).show();
            }
        });


    }

    public void readData() { //상태값 파이어베이스에서 읽어오기
        final Db db=(Db)getApplication();
        selected =db.getSelectedPot();
        num=db.getPotNum();
        item=new String[num];
        for(int i=0;i<num;i++){
            item[i]=db.getNames(i);
        }
        id=new String[num];
        for(int i=0;i<num;i++){
            id[i]=db.getIds(i);
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                db.setSelectedPot(db.getIds(position));//선택된 이름 전역변수에 저장
                selected=db.getSelectedPot();

                DatabaseReference myRef3 = database.getReference(selected + "/potState/AHumi");//습도
                DatabaseReference myRef4 = database.getReference(selected + "/potState/temp");//온도
                DatabaseReference myRef5 = database.getReference(selected + "/potState/lux");//조도
                DatabaseReference myRef6 = database.getReference(selected + "/potState/SHumi");//토양습도
                DatabaseReference myRef7 = database.getReference(selected + "/potState/Wlevel");//수위
                DatabaseReference myRef8 = database.getReference(selected + "/ControlSwitch");// 자,수동 모드

                myRef3.addValueEventListener(new ValueEventListener() {//습도

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {//파이어베이스 값이 변하면 동작하는 메소드
                        data = dataSnapshot.getValue(String.class);//값 가져오기

                        ahumi.setText(data);//textview에 값 설정하기
                        ahumi.setTextColor(Color.BLACK);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {//에러났을때 동작하는 메소드
                    }
                });


                myRef4.addValueEventListener(new ValueEventListener() {//온도

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        data = dataSnapshot.getValue(String.class);

                        temp.setText(data);
                        temp.setTextColor(Color.BLACK);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });

                myRef5.addValueEventListener(new ValueEventListener() {//조도

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        data = dataSnapshot.getValue(String.class);
                        if(data!=null) {
                            lux = Integer.parseInt(data);

                            if (0 < lux && lux < 150)
                                Lux.setText("매우 약함");
                            else if (150 <= lux && lux < 300)
                                Lux.setText("약함");
                            else if (300 <= lux && lux < 650)
                                Lux.setText("보통");
                            else if (650 <= lux && lux < 1000)
                                Lux.setText("강함");
                            else if (lux >= 1000)
                                Lux.setText("매우 강함");

                            Lux.setTextColor(Color.BLACK);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });


                myRef6.addValueEventListener(new ValueEventListener() {//토양습도

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        data = dataSnapshot.getValue(String.class);

                        shumi.setText(data);
                        shumi.setTextColor(Color.BLACK);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });


                myRef7.addValueEventListener(new ValueEventListener() {//수위

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        data = dataSnapshot.getValue(String.class);
                        if(data!=null) {
                            wLevel.setText(data);
                            db.setWlv(data);
                            wlv = Integer.parseInt(db.getWlv());
                            if (wlv <= 30)
                                wLevel.setTextColor(Color.RED);
                            else
                                wLevel.setTextColor(Color.BLACK);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });

                myRef8.addValueEventListener(new ValueEventListener() {// 수,자동 모드

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        data = dataSnapshot.getValue(String.class);
                        if(data!=null) {
                            if (data.equals("0"))
                                mode.setText("수동모드");
                            else if (data.equals("1"))
                                mode.setText("자동모드");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {//화분이 등록안돼 있을 경우에만 작동

            }
        });

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(StateActivity.this, android.R.layout.simple_spinner_item, item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//스피너 적용
        spinner.setAdapter(adapter);


        if(num==0) {
            Intent intent = new Intent(getBaseContext(), AddpotActivity.class);
            startActivityForResult(intent, 201);
        }
    }//readdata종료


    public void settimemethod(){
        AlarmManager manager=(AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent=new Intent(getApplicationContext(), ReceiveDaily.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(), 9, intent,PendingIntent.FLAG_NO_CREATE);

        if(pendingIntent==null) {//설정된알림이 없으면 설정한다.  intent,PendingIntent.FLAG_NO_CREATE
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 06);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            calendar.set(Calendar.MILLISECOND, 00);

            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

    }




}
