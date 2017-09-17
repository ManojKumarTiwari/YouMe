package com.theinfinitecoder.youme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private Button btSend;
    private EditText etInputMessage;
    private TextView tvMessage;

    private String mCurrentUserUID;
    private String tempChatRoom;

    private String chatMessage;

    private DatabaseReference mRefChatRoom;
    private String tempMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btSend = (Button) findViewById(R.id.bt_send);
        etInputMessage = (EditText) findViewById(R.id.et_input_message);
        tvMessage = (TextView) findViewById(R.id.tv_message);

        mCurrentUserUID = getIntent().getExtras().getString("mCurrentUserUID").toString();
        tempChatRoom = getIntent().getExtras().getString("tempChatRoom").toString();


        mRefChatRoom = FirebaseDatabase.getInstance().getReference().child(tempChatRoom);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> messageMap = new HashMap<>();
                tempMessage = mRefChatRoom.push().getKey();
                messageMap.put(tempMessage, "");
                mRefChatRoom.updateChildren(messageMap);

                DatabaseReference mRefMessageRoot = mRefChatRoom.child(tempMessage);

                Map<String, Object> messageDetailsMap = new HashMap<>();
                messageDetailsMap.put("mCurrentUserUID", mCurrentUserUID);
                messageDetailsMap.put("message", etInputMessage.getText().toString());

                mRefMessageRoot.updateChildren(messageDetailsMap);

            }
        });

        mRefChatRoom.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendChatRoom(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendChatRoom(dataSnapshot);
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
        });

    }

    private void appendChatRoom(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){

            chatMessage = (String)((DataSnapshot)i.next()).getValue();
            tvMessage.append(chatMessage + "\n");
        }
    }
}
