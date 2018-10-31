package com.fusion.kim.alc30;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

public class PickPersonActivity extends AppCompatActivity {

    private DatabaseReference mUsersRef;

    private RecyclerView mUsersRv;
    private ProgressBar mLoadingPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_person);

        getSupportActionBar().setTitle("Pick Receiver");

        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersRef.keepSynced(true);

        mUsersRv = findViewById(R.id.rv_pick_person);
        mUsersRv.setLayoutManager(new LinearLayoutManager(this));
        mUsersRv.setHasFixedSize(true);

        mLoadingPb = findViewById(R.id.pb_loading_people);
        mLoadingPb.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mLoadingPb.setVisibility(View.VISIBLE);

        Query query = mUsersRef.limitToLast(50).orderByChild("userName");

        FirebaseRecyclerOptions<People> options =
                new FirebaseRecyclerOptions.Builder<People>()
                        .setQuery(query, People.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter<People, PeopleViewHolder>  adapter = new FirebaseRecyclerAdapter<People, PeopleViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PeopleViewHolder holder, final int position, @NonNull People model) {

                holder.mUserNameTv.setText(model.getUserName());
                mLoadingPb.setVisibility(View.GONE);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final ProgressDialog progressDialog = new ProgressDialog(PickPersonActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Sending...");
                        progressDialog.show();

                        Map messageMap = new HashMap();
                        messageMap.put("message", getIntent().getStringExtra("message"));
                        messageMap.put("secretKey", "default");
                        messageMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        FirebaseDatabase.getInstance().getReference().child("Messages")
                                .child(getRef(position).getKey()).push()
                                .setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    progressDialog.dismiss();

                                    startActivity(new Intent(PickPersonActivity.this, MainActivity.class));
                                    finish();

                                    Toast.makeText(PickPersonActivity.this, "Sent", Toast.LENGTH_LONG).show();

                                } else {

                                    progressDialog.dismiss();

                                    Toast.makeText(PickPersonActivity.this, "Failed to send try again", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                    }
                });

            }

            @NonNull
            @Override
            public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return  new PeopleViewHolder(LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.item_people_list, parent, false));
            }
        };

        adapter.startListening();
        mUsersRv.setAdapter(adapter);

    }


    private class PeopleViewHolder extends RecyclerView.ViewHolder{

        View mView;

        TextView mUserNameTv;

        public PeopleViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mUserNameTv = itemView.findViewById(R.id.tv_username);

        }
    }
}
