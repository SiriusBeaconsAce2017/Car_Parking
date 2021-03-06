package com.example.lap259.car_parking;

import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private Button park;
    int status;
    private EditText slotNo;
    private ListView slotList;
    private DatabaseReference mDatabase;
    private ArrayList<String> arraySlotList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        park = (Button)findViewById(R.id.park);
        park.setText("BOOK");
        slotNo = (EditText) findViewById(R.id.slotNo);
        slotList = (ListView) findViewById(R.id.listSlot);
        //connecting to root database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference slotDetails = mDatabase;

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arraySlotList);
        slotList.setAdapter(arrayAdapter);


        slotDetails.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                            Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                            while(items.hasNext()){
                                DataSnapshot item = items.next();
                                Iterator<DataSnapshot> iterator_item = item.getChildren().iterator();
                                while(iterator_item.hasNext()) {
                                        DataSnapshot iterator = iterator_item.next();
                                    if(iterator.getValue(Integer.class) == 0) {
                                        Toast.makeText(MainActivity.this, "childor"+ item.getKey(), Toast.LENGTH_SHORT).show();
                                        String slotList = item.getKey();
                                        arraySlotList.add(slotList);
                                        arrayAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
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
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*ref.addListenerForSingleValueEvent(new ValueEventListener) {
  public void onDataChange(DataSnapshot snapshot) {
    if (dataSnapshot.exists()) {
      for (DataSnapshot userSnapshot : snapshot.getChildren()) {
        for (DataSnapshot programSnapshot : userSnapshot.getChildren()) {
          Program program = programSnapshot.getValue(Program.class);
      }
    }
  }

  public void onCancelled(FirebaseError firebaseError) {

  }
});
                */
                String slotNum = slotNo.getText().toString().trim();
                final DatabaseReference slotNumber = mDatabase.child("Slot").child(slotNum).child("status");

                slotNumber.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        status = dataSnapshot.getValue(Integer.class);
                        if(status == 0){
                            park.setText("UNBOOK");
                            slotNumber.setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "slot booked", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MainActivity.this, "error booking", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                               park.setText("BOOK");
                            slotNumber.setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "slot unbooked", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MainActivity.this, "error booking", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        System.out.println(status);
                        Toast.makeText(MainActivity.this, "status assigned or reda", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
