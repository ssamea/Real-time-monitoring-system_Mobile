package com.example.graduation;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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
import java.util.List;

public class waiting_time extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;
    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    PieChart pieChart;
    public static Context context_main; // context 변수 선언
    public int var; // 다른 Activity에서 접근할 변수
    public  String s_time1;
    public  String s_time2;
    public  String s_time3;
    public  String s_time4;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_time);
        context_main = this; // onCreate에서 this 할당

        initDatabase();
        tv1=(TextView)findViewById(R.id.Edong);
        tv2=(TextView)findViewById(R.id.tip);
        tv3=(TextView)findViewById(R.id.Olive);
        tv4=(TextView)findViewById(R.id.sanyong);

        pieChart = (PieChart)findViewById(R.id.piechart);

        mDatabase=FirebaseDatabase.getInstance();
        mReference=mDatabase.getReference("Waiting_time_DB");

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PieEntry> yValues = new ArrayList<>();
                Float[] f1 = new Float[1];
                Float[] f2 = new Float[1];
                Float[] f3 = new Float[1];
                Float[] f4 = new Float[1];


                for (DataSnapshot myData : dataSnapshot.getChildren()) { ////values에 데이터를 담는 과정
                    s_time1= dataSnapshot.child("JongHap").child("Waiting_time").getValue(String.class);
                    s_time2= dataSnapshot.child("Olive").child("Waiting_time").getValue(String.class);
                    s_time3= dataSnapshot.child("Sanyung").child("Waiting_time").getValue(String.class);
                    s_time4= dataSnapshot.child("TIP").child("Waiting_time").getValue(String.class);

                    tv1.setText(s_time1);
                    tv2.setText(s_time2);
                    tv3.setText(s_time3);
                    tv4.setText(s_time4);

                }


                String time1= dataSnapshot.child("JongHap").child("Waiting_time").getValue(String.class);
                String time2= dataSnapshot.child("Olive").child("Waiting_time").getValue(String.class);
                String time3= dataSnapshot.child("Sanyung").child("Waiting_time").getValue(String.class);
                String time4= dataSnapshot.child("TIP").child("Waiting_time").getValue(String.class);

                Float SensorValue = Float.valueOf(time1).floatValue();
                Float SensorValue2 = Float.valueOf(time2).floatValue();
                Float SensorValue3 = Float.valueOf(time3).floatValue();
                Float SensorValue4 = Float.valueOf(time4).floatValue();


                f1[0] =SensorValue;
                f2[0] =SensorValue2;
                f3[0] =SensorValue3;
                f4[0] =SensorValue4;

                yValues.add(new PieEntry(f1[0], "종합관"));
                yValues.add(new PieEntry(f2[0], "Olive"));
                yValues.add(new PieEntry(f3[0], "산융"));
                yValues.add(new PieEntry(f4[0], "TIP"));



                Collections.sort(yValues, new EntryXComparator());
                PieDataSet dataSet = new PieDataSet(yValues,"식당");
                dataSet.setSliceSpace(1f);
                dataSet.setSelectionShift(4f);
                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

                PieData data = new PieData(dataSet);
                data.setValueTextSize(15f);
                data.setValueTextColor(Color.BLACK);


                //piechart
                pieChart.setUsePercentValues(false);
                pieChart.getDescription().setEnabled(false);
                pieChart.setExtraOffsets(0,0,0,0);

                pieChart.setDragDecelerationFrictionCoef(1f); //0.95

                pieChart.setDrawHoleEnabled(false);
                pieChart.setHoleColor(Color.WHITE);
                pieChart.setTransparentCircleRadius(61f);


                pieChart.animateY(5000,Easing.EaseInCubic); //애니메이션
                pieChart.setData(data);

                pieChart.notifyDataSetChanged();
                pieChart.invalidate();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


    private void initDatabase() {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("Waiting_time_DB");

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