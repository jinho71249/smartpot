package com.example.smartpot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {
    private ImageButton searchButton;
    private ImageButton imageOpenButton;
    private ImageView imageView;
    private final int GET_GALLERY_IMAGE = 200;



    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlayout);
        initContent();
        initListener();
    }

    private void initContent(){
        searchButton=findViewById(R.id.searchButton);
        imageOpenButton=findViewById(R.id.imageOpenButton);
        imageView=findViewById(R.id.imageView);
    }

    private void initListener(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent out = new Intent();
                setResult(RESULT_OK, out);
                finish();
            }
        });

        imageOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);


        }

    }

}
