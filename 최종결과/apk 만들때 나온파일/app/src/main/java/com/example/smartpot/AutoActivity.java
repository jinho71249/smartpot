package com.example.smartpot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.StringTokenizer;

public class AutoActivity extends AppCompatActivity {
    private Spinner auto_spinner;
    private Spinner lux_spinner;
    private TextView selectedName;
    private ImageButton saveBtn;
    private ImageButton backBtn;
    private EditText autoWpercent;
    private EditText autoWcontent;
    private EditText lightTmp;
    protected String autoWPercent;
    protected String autoWContent;
    protected String lighttmp;
    protected String lightlux;
    private String data;
    protected String name;
    protected String ID;

    private String[] plantData={"사용자 설정", "개운죽","금전수", "꽃베고니아", "나한송", "레티지아",
            "무늬산호수", "뮤렌베키아","사랑초","산호수", "시클라멘", "율마", "클레마티스", "포인세티아", "협죽도"};
    private String[][] autoData={{},{"30","100","13","0"},{"30","100","13","1"},{"50","100","13","2"},{"30","100","5","1"},
            {"10","100","5","1"},{"30","100","0","1"}, {"30","100","10","0"}, {"30","100","10","1"}, {"30","100","5","0"},
            {"30","100","7","1"},{"30","100","10","2"},{"30","100","0","2"},{"30","100","13","2"},{"30","100","7","2"}};

    private String[] luxValue={"아주 약함 상태일때", "약함 상태일때", "보통 상태일때" };
    private String[] lux={"150", "300", "650"};

    FirebaseDatabase database = FirebaseDatabase.getInstance();


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autolayout);


        initContent();
        initListener();
    }

    private void initContent(){
        auto_spinner=findViewById(R.id.auto_spinner);
        lux_spinner=findViewById(R.id.lux_spinner);
        saveBtn=findViewById(R.id.saveBtn);
        backBtn=findViewById(R.id.backBtn);
        autoWpercent=findViewById(R.id.autoWpercent);
        autoWcontent=findViewById(R.id.autoWcontent);
        lightTmp=findViewById(R.id.lightTmp);
        selectedName=findViewById(R.id.selectedName);

    }

    private void initListener(){
        Intent intent=getIntent();

        name=intent.getStringExtra("name");
        ID=intent.getStringExtra("ID");
        //Toast.makeText(getApplicationContext(), name+"&"+ID, Toast.LENGTH_SHORT).show();

        selectedName.setText(name);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent out = new Intent();
                setResult(RESULT_CANCELED, out);
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoWPercent=autoWpercent.getText().toString();
                autoWContent=autoWcontent.getText().toString();
                lighttmp=lightTmp.getText().toString();


                DatabaseReference myRef1 = database.getReference(ID+"/autoControl/autoWpercent"); //파이어베이스로 값전달
                myRef1.setValue(autoWPercent);
                DatabaseReference myRef2 = database.getReference(ID+"/autoControl/autoWcontent"); //파이어베이스로 값전달
                myRef2.setValue(autoWContent);
                DatabaseReference myRef3 = database.getReference(ID+"/autoControl/lightTmp"); //파이어베이스로 값전달
                myRef3.setValue(lighttmp);
                DatabaseReference myRef4 = database.getReference(ID+"/autoControl/lightLux"); //파이어베이스로 값전달
                myRef4.setValue(lightlux);

                finish();
            }
        });//저장버튼


        auto_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    autoWpercent.setText(autoData[position][0]);
                    autoWcontent.setText(autoData[position][1]);
                    lightTmp.setText(autoData[position][2]);
                    lux_spinner.setSelection(Integer.parseInt(autoData[position][3]));
                    lightlux=lux[Integer.parseInt(autoData[position][3])];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //이런 경우 없음
            }
        });


        ArrayAdapter<String> adapter1=new ArrayAdapter<String>(AutoActivity.this, android.R.layout.simple_spinner_item, plantData);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//스피너 적용
        auto_spinner.setAdapter(adapter1);


        lux_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lightlux=lux[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //이런 경우 없음
            }
        });


        ArrayAdapter<String> adapter2=new ArrayAdapter<String>(AutoActivity.this, android.R.layout.simple_spinner_item, luxValue);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//스피너 적용
        lux_spinner.setAdapter(adapter2);


        DatabaseReference myRef1 = database.getReference(ID+"/autoControl/autoWpercent");//습도
        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {//습도

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//파이어베이스 값이 변하면 동작하는 메소드
                data = dataSnapshot.getValue(String.class);//값 가져오기

                autoWpercent.setText(data);//textview에 값 설정하기
            }
            @Override
            public void onCancelled(DatabaseError error) {//에러났을때 동작하는 메소드
            }
        });

        DatabaseReference myRef2 = database.getReference(ID+"/autoControl/autoWcontent");//수분공급량
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {//습도

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//파이어베이스 값이 변하면 동작하는 메소드
                data = dataSnapshot.getValue(String.class);//값 가져오기

                autoWcontent.setText(data);//textview에 값 설정하기
            }
            @Override
            public void onCancelled(DatabaseError error) {//에러났을때 동작하는 메소드
            }
        });

        DatabaseReference myRef3 = database.getReference(ID+"/autoControl/lightTmp");//온도
        myRef3.addListenerForSingleValueEvent(new ValueEventListener() {//습도

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//파이어베이스 값이 변하면 동작하는 메소드
                data = dataSnapshot.getValue(String.class);//값 가져오기

                lightTmp.setText(data);//textview에 값 설정하기
            }
            @Override
            public void onCancelled(DatabaseError error) {//에러났을때 동작하는 메소드
            }
        });

        DatabaseReference myRef4 = database.getReference(ID+"/autoControl/lightLux");//조도
        myRef4.addListenerForSingleValueEvent(new ValueEventListener() {//습도

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//파이어베이스 값이 변하면 동작하는 메소드
                data = dataSnapshot.getValue(String.class);//값 가져오기
                if(data!=null){
                    if(data.equals("150"))
                        lux_spinner.setSelection(0);
                    else if(data.equals("300"))
                        lux_spinner.setSelection(1);
                    else
                        lux_spinner.setSelection(2);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {//에러났을때 동작하는 메소드
            }
        });



    }

}
