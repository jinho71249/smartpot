package com.example.smartpot;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;



public class NotiService extends Service {


    private String data;
    protected String potnames="";
    protected String potids="";
    protected String[] selectedID;
    protected String[] wlevels;
    protected String[] selectedname;
    protected String msg;
    protected int i;
    protected int n;
    protected int num;
    protected int wlv;
    protected String alarmcheck;
    protected Timer timer;
    protected TimerTask tt;

    protected boolean[] checks;

    public static Intent serviceIntent = null;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;
        //initializeNotification();

        DatabaseReference myRef1 = database.getReference("admin/potID");//ID 가져와서 저장
        myRef1.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                potids=data;
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        DatabaseReference myRef2 = database.getReference("admin/potNum");//화분갯수 가져와서 저장
        myRef2.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                num=Integer.parseInt(data);
                selectedID =new String[num];
                checks=new boolean[num];
                wlevels=new String[num];
                selectedname=new String[num];

            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        DatabaseReference myRef3 = database.getReference("admin/potAlarm");
        myRef3.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                alarmcheck=data;
                if(alarmcheck.equals("1")) {//파이어베이스에 알림체크가 되있으면
                    initializeNotification(); //푸시알림 활성화
                    startAlarm();             //물부족 알림 시작
                }
                else if(alarmcheck.equals("0"))//체크오프 되있으면
                    stopAlarm();            //물부족 알림 중지

            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        DatabaseReference myRef4 = database.getReference("admin/potName");//ID 가져와서 저장
        myRef4.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
                potnames=data;
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        waitdata();

        return START_STICKY;
    }

    public void waitdata(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(potnames.equals("")||potids.equals("")){
                    waitdata();
                }else {

                    StringTokenizer token1 = new StringTokenizer(potids, "#");

                    int j = 0;
                    while (token1.hasMoreTokens()) {
                        selectedID[j++] = token1.nextToken("#");
                    }

                    StringTokenizer token2 = new StringTokenizer(potnames, "#");

                    int k = 0;
                    while (token2.hasMoreTokens()) {
                        selectedname[k++] = token2.nextToken("#");
                    }
                    //Toast.makeText(getApplicationContext(), Integer.toString(i), Toast.LENGTH_SHORT).show(); //테스트용
                }

            }
        }, 2000);
    }

    public void startAlarm(){
        timer=new Timer();
        tt=new TimerTask() {
            public void run() {
                final ServData sb=new ServData();

                msg="";
                DatabaseReference[] waterRef = new DatabaseReference[num];
                for (i = 0; i < num; i++) {
                    waterRef[i] = database.getReference(selectedID[i] + "/potState/Wlevel");
                    waterRef[i].addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            data = dataSnapshot.getValue(String.class);
                            if(data!=null) {
                                wlv=Integer.parseInt(data);
                                if (wlv <= 30) {
                                    sb.setChecks(true);
                                    String tmp=msg;
                                    tmp = tmp.concat("(" + data + "%)" + "#");
                                    msg=tmp;
                                } else
                                    sb.setChecks(false);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "통신 불량", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < num; j++) {
                    if (sb.getChecks(j)) {//체크가 하나라도 되있으면 알림
                        n=0;
                        StringTokenizer tokens=new StringTokenizer(msg, "#");
                        while(tokens.hasMoreTokens()){
                            wlevels[n++]=tokens.nextToken("#");
                        }
                        n=0;
                        msg="";
                        for(int m=0;m<num;m++){
                            if(sb.getChecks(m)) {
                                msg = msg.concat(selectedname[m]+wlevels[n++]+" ");
                            }
                        }
                        notificationCannel();
                        break;//한번 알리면 바로 같은 알림은 필요으니 for문에서 나가기
                    }
                }


            }
        };
        timer.schedule(tt, 2000, 1000*60*60);//딜레이 1.5초 반복주기 6초 반복주기 1시간으로 늘릴계획
    }

    public void stopAlarm(){
       tt.cancel();
    }


    public void initializeNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setSmallIcon(R.mipmap.ic_launcher);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("설정을 보려면 누르세요.");
        style.setBigContentTitle(null);
        style.setSummaryText("서비스 동작중"); //포그라운드에서 계속 서비스를 실행시켜줄 푸시알림


        builder.setContentText(null);
        builder.setContentTitle(null);
        builder.setOngoing(true);


        builder.setStyle(style);
        builder.setWhen(0);
        builder.setShowWhen(false);


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);


        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("1",
                    "undead_service", NotificationManager.IMPORTANCE_NONE));
        }

        Notification notification = builder.build();
        startForeground(1, notification);

    }


    @Override
    public void onDestroy() { //서비스가 종료되면
        super.onDestroy();

        if(alarmcheck.equals("1")) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());//현재 시간을 가져와
            calendar.add(Calendar.SECOND, 3);//현재시간에 3초를 더해서
            Intent intent = new Intent(this, AlarmReceiver.class);//알림받는 곳에 그 시간을 준다
            PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            //broadcast로 알림을 받은 클라스는 다시 서비스를 실행시켜준다.
        }
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {//task제거 시에도 똑같은 일을 해준다.
        super.onTaskRemoved(rootIntent);

        if(alarmcheck.equals("1")) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 3);
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }


    public void notificationCannel(){


        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "notiID")
                .setContentText(msg+"의 물탱크에 물이 부족합니다.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("notiID", channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(1234, builder.build());


    }

    class ServData extends Application{
        public boolean getChecks(int i) {
            return checks[i];
        }

        public void setChecks(boolean checks) {
            this.checks[checknum++] = checks;
        }

        public int getChecknum() {
            return checknum;
        }

        public void setChecknum(int checknum) {
            this.checknum = checknum;
        }

        private int checknum =0;
        private boolean checks[]=new boolean[100];
    }

}