package com.example.graduation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class MapKpu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_kpu);
        ImageView imageView1=findViewById(R.id.imageView1);
        ImageView imageView2=findViewById(R.id.imageView2);
    }
}
