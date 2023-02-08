package com.example.smartpot;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;


public class DataGraphActivity extends AppCompatActivity {

    private String data;
    private TextView name;
    private int position;
    protected String[] fdata =new String[5];//파이어베이스에서 가져오는 통짜 데이터
    protected String[] date=new String[5];//오늘 날짜의 데이터

    protected String[] days =new String[5];//
    protected String[][] graphdata=new String[5][4]; //0습도 1온도 2토양습도 3조도
    protected int n;
    protected String oldday;
    protected CombinedChart combinedChart;

    FirebaseDatabase database = FirebaseDatabase.getInstance();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datagraphlayout);
        init();//배열 초기화
        initContent();
        final Db db=(Db)getApplication();
        Intent intent=getIntent();
        position=intent.getIntExtra("position", -1);
        name.setText(db.getNames(position));

        readdata();
    }//oncreate

    private void initContent() {
        name=findViewById(R.id.name);
        combinedChart=findViewById(R.id.combinedChart);
    }

    protected void readdata(){
        SimpleDateFormat format=new SimpleDateFormat("MM-dd");
        Calendar time=Calendar.getInstance();
        days[0]=format.format(time.getTime());
        for(int i=1;i<5;i++) {
            time.add(Calendar.DATE, -1);
            days[i] = format.format(time.getTime());
        }
        time.add(Calendar.DATE, -1);
        oldday = format.format(time.getTime());
        DatabaseReference myRef11 = database.getReference(oldday);
        myRef11.setValue(null);//6일전 데이터는 삭제

        //Toast.makeText(getApplicationContext(), days[4]+"  "+days[3]+"  "+days[2], Toast.LENGTH_SHORT).show(); //테스트용



        final DatabaseReference myRef0 = database.getReference((position+1)+"/"+days[0]);
        myRef0.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                if(data==null)
                    myRef0.setValue("0#0#0#0#");

                fdata[0] = data;
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        final DatabaseReference myRef1 = database.getReference((position+1)+"/"+days[1]);
        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                if(data==null)
                    myRef1.setValue("0#0#0#0#");

                fdata[1] = data;
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        final DatabaseReference myRef2 = database.getReference((position+1)+"/"+days[2]);
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                if(data==null)
                    myRef2.setValue("0#0#0#0#");

                fdata[2] = data;
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        final DatabaseReference myRef3 = database.getReference((position+1)+"/"+days[3]);
        myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                if(data==null)
                    myRef3.setValue("0#0#0#0#");

                fdata[3] = data;
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        final DatabaseReference myRef4 = database.getReference((position+1)+"/"+days[4]);
        myRef4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                if(data==null)
                    myRef4.setValue("0#0#0#0#");

                fdata[4] = data;
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        waitdata();
    }

    protected void waitdata(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fdata[0]==null||fdata[1]==null||fdata[2]==null||fdata[3]==null||fdata[4]==null){
                    waitdata(); //모든 정보를 읽어올때까지 기다린다.
                }
                else if(fdata[0].equals("0")||fdata[1].equals("0")||fdata[2].equals("0")||fdata[3].equals("0")||fdata[4].equals("0")){
                    waitdata(); //모든 정보를 읽어올때까지 기다린다.
                }
                else {
                    StringTokenizer tokens[]=new StringTokenizer[5];
                    for(n=0;n<5;n++) {
                        tokens[n] = new StringTokenizer(fdata[n], "#");
                        int k = 0;
                        while (tokens[n].hasMoreTokens()) {
                            graphdata[n][k++] = tokens[n].nextToken("#");
                        }
                    }

                    setChart();
                }

            }
        }, 1000);
    }

    public void setChart(){
        date= new String[]{
                days[4], days[3], days[2], days[1], days[0]
        };

        combinedChart.setDrawGridBackground(false);
        combinedChart.getDescription().setEnabled(false);
        combinedChart.setDrawBarShadow(false);
        combinedChart.setHighlightFullBarEnabled(false);

        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE
        });


        Legend legend=combinedChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);

        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setMaxSizePercent(10);
        legend.setDrawInside(false);

        YAxis rightAxis=combinedChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setCenterAxisLabels(true);
        rightAxis.setGranularity(300);
        rightAxis.setAxisMinimum(0);
        rightAxis.setAxisMaximum(1200);
        rightAxis.setDrawLabels(true);

        YAxis leftAxis=combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularity(20);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(100);

        XAxis xAxis=combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value){
                return  date[(int)value%date.length];
            }
        });

        CombinedData cdata1=new CombinedData();

        LineData ldata=new LineData();

        ArrayList<Entry> lineEntry1=new ArrayList();//습도
        lineEntry1.add(new Entry(0, Integer.parseInt(graphdata[4][0])));
        lineEntry1.add(new Entry(1, Integer.parseInt(graphdata[3][0])));
        lineEntry1.add(new Entry(2, Integer.parseInt(graphdata[2][0])));
        lineEntry1.add(new Entry(3, Integer.parseInt(graphdata[1][0])));
        lineEntry1.add(new Entry(4, Integer.parseInt(graphdata[0][0])));

        ArrayList<Entry> lineEntry2=new ArrayList(); //data만들고 온도
        lineEntry2.add(new Entry(0, Integer.parseInt(graphdata[4][1])));
        lineEntry2.add(new Entry(1, Integer.parseInt(graphdata[3][1])));
        lineEntry2.add(new Entry(2, Integer.parseInt(graphdata[2][1])));
        lineEntry2.add(new Entry(3, Integer.parseInt(graphdata[1][1])));
        lineEntry2.add(new Entry(4, Integer.parseInt(graphdata[0][1])));

        ArrayList<Entry> lineEntry3=new ArrayList();//토양습도
        lineEntry3.add(new Entry(0, Integer.parseInt(graphdata[4][2])));
        lineEntry3.add(new Entry(1, Integer.parseInt(graphdata[3][2])));
        lineEntry3.add(new Entry(2, Integer.parseInt(graphdata[2][2])));
        lineEntry3.add(new Entry(3, Integer.parseInt(graphdata[1][2])));
        lineEntry3.add(new Entry(4, Integer.parseInt(graphdata[0][2])));

        LineDataSet dataSet1=new LineDataSet(lineEntry1, "습도(%)");//data를 dataset에 넣고
        LineDataSet dataSet2=new LineDataSet(lineEntry2, "온도(℃)");
        LineDataSet dataSet3=new LineDataSet(lineEntry3, "토양습도(%)");

        dataSet1.setLineWidth(2.5f);
        dataSet1.setColor(Color.rgb(234,238,68));
        dataSet1.setCircleColor(Color.rgb(234,238,68));
        dataSet1.setCircleHoleRadius(5f);
        dataSet1.setDrawValues(false);
        dataSet1.setValueTextSize(10f);
        dataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);

        dataSet2.setLineWidth(2.5f);
        dataSet2.setColor(Color.rgb(153,252,54));
        dataSet2.setCircleColor(Color.rgb(153,252,54));
        dataSet2.setCircleHoleRadius(5f);
        dataSet2.setDrawValues(false);
        dataSet2.setValueTextSize(10f);
        dataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);

        dataSet3.setLineWidth(2.5f);
        dataSet3.setColor(Color.rgb(255,204,255));
        dataSet3.setCircleColor(Color.rgb(255,204,255));
        dataSet3.setCircleHoleRadius(5f);
        dataSet3.setDrawValues(false);
        dataSet3.setValueTextSize(10f);
        dataSet3.setAxisDependency(YAxis.AxisDependency.LEFT);

        ldata.addDataSet(dataSet1);//linedata에 넣고
        ldata.addDataSet(dataSet2);
        ldata.addDataSet(dataSet3);

        BarData bdata=new BarData();
        ArrayList<BarEntry> barEntry=new ArrayList<BarEntry>();
        barEntry.add(new BarEntry(0+.1f, Integer.parseInt(graphdata[4][3])));
        barEntry.add(new BarEntry(1+.1f, Integer.parseInt(graphdata[3][3])));
        barEntry.add(new BarEntry(2+.1f, Integer.parseInt(graphdata[2][3])));
        barEntry.add(new BarEntry(3+.1f, Integer.parseInt(graphdata[1][3])));
        barEntry.add(new BarEntry(4-.1f, Integer.parseInt(graphdata[0][3])));

        BarDataSet dataSet5=new BarDataSet(barEntry,"조도(lux)");
        dataSet5.setValueTextSize(10f);
        dataSet5.setDrawValues(false);
        dataSet5.setColor(Color.rgb(102,204,255));
        dataSet5.setAxisDependency(YAxis.AxisDependency.RIGHT);

        bdata.setBarWidth(.2f);
        bdata.addDataSet(dataSet5);

        cdata1.setData(ldata); //combineddata에 넣고
        cdata1.setData(bdata);

        combinedChart.setData(cdata1);//combined차트에 넣는다
        combinedChart.invalidate();//차트 활성화
    }

    public void init(){
        for(int i=0;i<5;i++)
            fdata[i]="0";

        for(int i=0;i<5;i++)
            date[i]="0";

        for(int i=0;i<5;i++)
            days[i]="0";

        for(int i=0;i<5;i++)
            for(int j=0;j<4;j++)
                graphdata[i][j]="0";
    }










}
