package com.example.smartpot;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class WaterActivity extends AppCompatActivity {
    protected PieChart lpieChart;
    private TextView selectedName;
    private EditText waterContent;
    private ImageButton supplyBtn;
    private ImageView backBtn;
    protected String selected;
    protected String selectedID;
    private String watercontent;

    private String data;
    protected int ahumi;
    protected int selectedNum;
    static int PERCENT=100;

    FirebaseDatabase database = FirebaseDatabase.getInstance();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waterlayout);

        Intent intent=getIntent();
        ahumi=intent.getIntExtra("ahumi", 0);
        if(ahumi==-1){
            ahumi=1;
        }
        selectedNum=intent.getIntExtra("selectedNum", -1);

        //Toast.makeText(getApplicationContext(), Integer.toString(ahumi), Toast.LENGTH_SHORT).show();//테스트

        initContent();
        initListener();
        setPieChart();

    }

    private void initContent(){
        lpieChart=findViewById(R.id.piechart);
        waterContent=findViewById(R.id.waterContent);
        selectedName=findViewById(R.id.selectedName);
        supplyBtn=findViewById(R.id.wsupplyBtn);
        backBtn=findViewById(R.id.backBtn);
    }

    private void initListener(){
        final Db db=(Db)getApplication();
        selected =db.getNames(selectedNum);
        selectedID=db.getIds(selectedNum);
        selectedName.setText(selected);

        supplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watercontent= waterContent.getText().toString();

                DatabaseReference myRef1 = database.getReference(selectedID +"/manualControl/waterContent");
                myRef1.setValue(watercontent);
                DatabaseReference myRef2 = database.getReference(selectedID +"/manualControl/pumpSwitch");
                myRef2.setValue("1");

                Toast.makeText(getApplicationContext(), "수분공급량 전송.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });//수분공급 버튼



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });//back버튼
    }

    private void setPieChart(){

        ArrayList chartContent=new ArrayList();
        chartContent.add(new PieEntry(ahumi, "토양\n습도"));
        chartContent.add(new PieEntry(PERCENT-ahumi, ""));

        PieDataSet dataSet=new PieDataSet(chartContent, null);
        PieData data=new PieData(dataSet);

        lpieChart.setData(data);
        lpieChart.setDrawCenterText(true);
        lpieChart.setCenterText("토양습도");
        lpieChart.setDescription(null);
        lpieChart.setEntryLabelTextSize(10);
        lpieChart.setTransparentCircleRadius(0);//그래프 안쪽 입체감 0
        lpieChart.setCenterTextSize(30);

        Legend legend=lpieChart.getLegend();
        legend.setEnabled(false);

        dataSet.setColors(Color.rgb(77,145,227), Color.GRAY);

        //lpieChart.notifyDataSetChanged();
        //lpieChart.invalidate();//refresh
    }




}
