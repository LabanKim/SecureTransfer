package com.fusion.kim.alc30;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private RecyclerView mMessagesRv;
    private TextView mainErrorTv;

    private ProgressBar mLoadingPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser == null){

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                    finish();

                }

            }
        };

        mainErrorTv = findViewById(R.id.tv_main_error);

        mMessagesRv = findViewById(R.id.rv_files_list);
        mMessagesRv.setLayoutManager(new LinearLayoutManager(this));
        mMessagesRv.setHasFixedSize(true);

        mLoadingPb = findViewById(R.id.pb_loading_files);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, SendFileActivity.class));

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseDatabase.getInstance().getReference().child("Messages").child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChildren()){

                            mainErrorTv.setVisibility(View.GONE);

                        } else {

                            mainErrorTv.setVisibility(View.VISIBLE);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        Query query = FirebaseDatabase.getInstance().getReference().child("Messages")
                .child(mAuth.getCurrentUser().getUid()).limitToLast(50).orderByChild("userName");

        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter<Message, MessageViewHolder> adapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MessageViewHolder holder, int position, @NonNull final Message model) {

                FirebaseDatabase.getInstance().getReference().child("Users").child(model.getSender())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                holder.mNameTv.setText(dataSnapshot.child("userName").getValue(String.class));

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                mLoadingPb.setVisibility(View.GONE);

            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return  new MessageViewHolder(LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.item_message, parent, false));

            }
        };

        adapter.startListening();
        mMessagesRv.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {

            mAuth.signOut();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private class  MessageViewHolder extends RecyclerView.ViewHolder{


        private View mView;

        private TextView mNameTv;

        public MessageViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mNameTv = itemView.findViewById(R.id.tv_sender);

        }
    }

}
