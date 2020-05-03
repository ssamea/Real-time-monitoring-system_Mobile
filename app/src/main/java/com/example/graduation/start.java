package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class start extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;

    private ListView listView;
  private ArrayAdapter<String> adapter;
  //private ArrayAdapter<Client> adapter;


    List<Object> Array = new ArrayList<Object>();
   //List<Client> Array = new ArrayList<Client>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        listView = (ListView) findViewById(R.id.list_View);

        initDatabase();

       adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
      //  adapter = new ArrayAdapter<Client>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<Client>());

        listView.setAdapter(adapter);

        mReference=mDatabase.getReference("Client_DB");


        mReference.child("Client").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();

                for (DataSnapshot Data : dataSnapshot.getChildren()) {
                    // child 내에 있는 데이터만큼 반복합니다.

                    String str1= Data.getValue(Client.class).getID();
                    String str2= Data.getValue(Client.class).getPASSWORD();
                    String str3= Data.getValue(Client.class).getName();


                    /*
                    Client client= new Client(str1,str2,str3);
                    Array.add(client);
                    adapter.add(client);

                    Client client= Data.getValue(str1,str2,str3);
                    Array.add(client);
                  adapter.add(client);
                     */



                    Array.add(str1);
                    adapter.add(str1);

                    Array.add(str2);
                    adapter.add(str2);

                    Array.add(str3);
                    adapter.add(str3);



                }

                adapter.notifyDataSetChanged();
                listView.setSelection(adapter.getCount()-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initDatabase() {
        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference("Client_DB");
       // mReference.child("Client").setValue("Check");

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReference.removeEventListener(mChild);
    }
}

