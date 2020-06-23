package com.example.graduation;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class waiting_time extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;
    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_time);

        initDatabase();
        tv1=(TextView)findViewById(R.id.Edong);
        tv2=(TextView)findViewById(R.id.tip);
        tv3=(TextView)findViewById(R.id.Olive);
        tv4=(TextView)findViewById(R.id.sanyong);

        mDatabase=FirebaseDatabase.getInstance();
        mReference=mDatabase.getReference("Waiting_time_DB");

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot myData : dataSnapshot.getChildren()) { ////values에 데이터를 담는 과정

                    String time1= dataSnapshot.child("JongHap").child("Waiting_time").getValue(String.class);
                    String time2= dataSnapshot.child("Olive").child("Waiting_time").getValue(String.class);
                    String time3= dataSnapshot.child("Sanyung").child("Waiting_time").getValue(String.class);
                    String time4= dataSnapshot.child("Tip").child("Waiting_time").getValue(String.class);


                    Log.e(time1,"error");
                    Log.e(time2,"error");
                    Log.e(time3,"error");
                    Log.e(time4,"error");


                    tv1.setText(time1);
                    tv2.setText(time2);
                    tv3.setText(time3);
                    tv4.setText(time4);
                }
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