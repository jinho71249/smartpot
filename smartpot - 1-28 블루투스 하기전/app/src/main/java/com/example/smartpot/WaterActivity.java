package com.example.smartpot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WaterActivity extends AppCompatActivity {
    private ImageButton reservButton;
    private ImageButton backButton;
    private EditText howMuch;
    private EditText whatTime;
    private Integer whattime;  //언제 수분공급 할건지
    private Integer howmuch;  //얼마나 물을 공급할건지

    FirebaseDatabase database = FirebaseDatabase.getInstance();


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waterlayout);
        initContent();
        initListener();
    }

    private void initContent(){
        reservButton=findViewById(R.id.waterReservBtn);
        backButton=findViewById(R.id.waterBackBtn);
        howMuch=findViewById(R.id.howMuch);
        whatTime=findViewById(R.id.waterwhatTime);

    }

    private void initListener(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent out = new Intent();
                setResult(RESULT_CANCELED, out);
                finish();
            }
        });

        reservButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whattime=Integer.parseInt(whatTime.getText().toString()); //언제 수분공급 할건지
                howmuch=Integer.parseInt(howMuch.getText().toString()); //얼마나 물을 공급할건지
                Intent intent = new Intent();
                intent.putExtra("waterWhatTime",whattime); //메인화면에 값전달
                intent.putExtra("howMuch",howmuch);

                DatabaseReference myRef1 = database.getReference("WaterData/whatTime"); //파이어베이스로 값전달
                myRef1.setValue(whattime);
                DatabaseReference myRef2 = database.getReference("WaterData/howMuch");
                myRef2.setValue(howmuch);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

}
