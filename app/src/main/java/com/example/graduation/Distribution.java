package com.example.graduation;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.github.mikephil.charting.data.LineData;

public class Distribution extends AppCompatActivity{
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;


    LineChart lineChart; //Temp_linechart
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribution);

        initDatabase();

        lineChart=(LineChart)findViewById(R.id.mpChart);
      //  tv=(TextView)findViewById(R.id.cntid);

        mDatabase=FirebaseDatabase.getInstance();
        mReference=mDatabase.getReference("Distribution_DB");

       // mReference.child("Kpu").addValueEventListener(new ValueEventListener() {
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Entry> dataVals  = new ArrayList<>(); //차트 데이터 셋에 담겨질 데이터

                ArrayList<String> labels = new ArrayList<String>();
                labels.add("TIP");
                labels.add("TIP");
                labels.add("종합관");
                labels.add("Olive");
                labels.add("산융");

                //float i=0;

                    for (DataSnapshot myData : dataSnapshot.getChildren()) { ////values에 데이터를 담는 과정
                        //i = i + 1;
                        Integer cnt= dataSnapshot.child("Tip").child("people_number").getValue(Integer.class);
                        Integer cnt2= dataSnapshot.child("JongHap").child("people_number").getValue(Integer.class);
                        Integer cnt3= dataSnapshot.child("Olive").child("people_number").getValue(Integer.class);
                        Integer cnt4= dataSnapshot.child("Sanyung").child("people_number").getValue(Integer.class);

                        Float SensorValue = Float.valueOf(cnt).floatValue();
                        Float SensorValue2 = Float.valueOf(cnt2).floatValue();
                        Float SensorValue3 = Float.valueOf(cnt3).floatValue();
                        Float SensorValue4 = Float.valueOf(cnt4).floatValue();

                        dataVals.add(new Entry(1f,SensorValue));
                        dataVals.add(new Entry(2f,SensorValue2));
                        dataVals.add(new Entry(3f,SensorValue3));
                        dataVals.add(new Entry(4f,SensorValue4));

                       // tv.setText("현재 인원:"+cnt);
                    }

                Collections.sort(dataVals, new EntryXComparator());
                final LineDataSet lineDataSet = new LineDataSet(dataVals, "인원 수");    //LineDataSet 선언

                LineData data = new LineData(lineDataSet); //LineDataSet을 담는 그릇 여러개의 라인 데이터 삽입
                lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                //x축 string으로 변환
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new MyXAxisValueFormatter(labels)); //MyXAxisValueFormatter 커스텀 디자인 클래스 호출

                //출력
                lineChart.setData(data);
                lineChart.animateY(5000);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Distribution.this, "Fail to load post", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void initDatabase() {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("Distribution_DB");

        mChild = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mReference.addChildEventListener(mChild);
    }




}
