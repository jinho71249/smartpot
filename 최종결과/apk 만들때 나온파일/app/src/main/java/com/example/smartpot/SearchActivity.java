package com.example.smartpot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;



public class SearchActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private Button refreshBtn;
    private WebView webView;
    private WebSettings webSettings;
    ValueCallback mFilePathCallback;
    String[] permission_list = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

    };

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlayout);
        checkPermission();
        initListener();

        webView=findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webSettings=webView.getSettings();
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    request.grant(request.getResources());
                }
            }
            public boolean onShowFileChooser(WebView webView, ValueCallback filePathCallback, FileChooserParams fileChooserParams){
                mFilePathCallback = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                startActivity(intent);
                return  true;
            }
        });
        webView.loadUrl("https://www.bing.com/camera?ptn=Homepage&rtpu=%2F");
//https://www.bing.com/visualsearch
//https://www.bing.com/camera?ptn=Homepage&rtpu=%2F

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission() {
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for (String permission : permission_list) {
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if (chk == PackageManager.PERMISSION_DENIED) {
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list, 0);
            }
        }
    }

    public void onActivityResult(int requestCode,int resultCode,@Nullable Intent data){
        Log.e("resultCode :: ",String.valueOf(requestCode));
        if(requestCode == 0 && requestCode == RESULT_OK){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode,data));
            }else{
                mFilePathCallback.onReceiveValue(new Uri[]{data.getData()});
            }
            mFilePathCallback = null;
        }else {
            mFilePathCallback.onReceiveValue(null);
        }
    }

    private void initListener(){
        backBtn=findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent out = new Intent();
                setResult(RESULT_CANCELED, out);
                finish();
            }
        });

        refreshBtn=findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("https://www.bing.com/camera?ptn=Homepage&rtpu=%2F");
            }
        });

    }

}
//try {
//}
//catch (IOException e){
//    e.printStackTrace();
//}