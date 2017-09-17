package com.theinfinitecoder.youme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mRoot;

    private Button btLetsGo;
    private Button btStartChat;

    private String mCurrentUserUID;
    private String tempChatRoom;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        btLetsGo = (Button) findViewById(R.id.bt_lets_go);
        btStartChat = (Button) findViewById(R.id.bt_start_chat);

        mAuth = FirebaseAuth.getInstance();
        mRoot = FirebaseDatabase.getInstance().getReference().getRoot();

        btLetsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<AuthResult> resultTask = mAuth.signInAnonymously();
                resultTask.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                    }
                });
            }
        });

       /* btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });*/

       /* btnPermanent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = "password";
                String email = "malo@malo.com";

                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                mAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        btnPermanent.setVisibility(View.GONE);
                        Toast.makeText(StartActivity.this, "done!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });*/

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    // Toast.makeText(ActivityAnonymous.this, "SignedIn", Toast.LENGTH_SHORT).show();
                    btLetsGo.setVisibility(View.INVISIBLE);
                    btLetsGo.setEnabled(false);
                    btStartChat.setVisibility(View.VISIBLE);
                    btStartChat.setEnabled(true);

                    mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Log.d(TAG, "onComplete: uid=" + mCurrentUser.getUid());
                    mCurrentUserUID = mCurrentUser.getUid();

                    btStartChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Map<String, Object> chatRoomMap = new HashMap<>();
                            tempChatRoom = mRoot.push().getKey();
                            chatRoomMap.put(tempChatRoom,"");
                            mRoot.updateChildren(chatRoomMap);

                            Intent chatIntent = new Intent(StartActivity.this, ChatActivity.class);
                            chatIntent.putExtra("mCurrentUserUID", mCurrentUserUID);
                            chatIntent.putExtra("tempChatRoom", tempChatRoom);
                            startActivity(chatIntent);
                            finish();
                        }
                    });


                    //Toast.makeText(ActivityAnonymous.this, mAuth.getCurrentUser().getProviderId(), Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(ActivityAnonymous.this, "SignedOut", Toast.LENGTH_SHORT).show();
                    btStartChat.setVisibility(View.INVISIBLE);
                    btStartChat.setEnabled(false);
                    btLetsGo.setVisibility(View.VISIBLE);
                    btLetsGo.setEnabled(true);
                }
            }
        };
    }

}
