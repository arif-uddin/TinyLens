package com.lazydevs.tinylens.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lazydevs.tinylens.adapter.CommentsAdapter;
import com.lazydevs.tinylens.Model.ModelComment;
import com.lazydevs.tinylens.Model.ModelUser;
import com.lazydevs.tinylens.R;

import java.util.ArrayList;

public class PostDetailViewActivity extends AppCompatActivity {


     TextView title,user_name,description,category;
     ImageView imageView;

     //for comment
     RecyclerView recyclerView_comment;
     ArrayList<ModelComment> comments;
     ArrayList<ModelUser> users;
     ImageButton postCommennt;
     EditText commentString;
     CommentsAdapter commentsAdapter;

     TextView commenterName;
     ImageView commenterPhoto;


    private DatabaseReference mDatabaseRef;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail_view);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        title= findViewById(R.id.title);
        description= findViewById(R.id.description);
        imageView= findViewById(R.id.im_images);
        user_name= findViewById(R.id.tv_name);
        category= findViewById(R.id.category);




        mDatabaseRef = FirebaseDatabase.getInstance().getReference("comments");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        users = new ArrayList<>();
        comments = new ArrayList<>();

        //for comment
        commentString=(EditText) findViewById(R.id.et_new_comment);
        postCommennt=(ImageButton)findViewById(R.id.btn_post_comment);
        recyclerView_comment = findViewById(R.id.recyclerView_comment);

        commenterName = findViewById(R.id.tv_commenter_username);
        commenterPhoto = findViewById(R.id.iv_commenter_photo);


        LinearLayoutManager linearVertical = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView_comment.setLayoutManager(linearVertical);
        commentsAdapter = new CommentsAdapter(getApplicationContext(),comments,users);
        recyclerView_comment.setAdapter(commentsAdapter);


        //details view of post
        Glide
                .with(getApplicationContext())
                .load(getIntent().getExtras().getString("image"))
                .into(imageView);

        user_name.setText("By"+" "+getIntent().getExtras().getString("user_name"));
        title.setText(getIntent().getExtras().getString("title"));
        description.setText(getIntent().getExtras().getString("description"));
        category.setText("Category: "+getIntent().getExtras().getString("category"));

        final String mKey = getIntent().getExtras().getString("image_key");

        Query query = FirebaseDatabase.getInstance().getReference().child("comments");
        query.orderByChild("imageKey").equalTo(mKey).addChildEventListener(new QueryForComments());


        postCommennt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                if(!commentString.getText().toString().equals(""))
                {
                    ModelComment modelComment = new ModelComment(firebaseUser.getUid(),mKey,commentString.getText().toString());
                    String key = databaseReference.child("comments").push().getKey();
                    databaseReference.child("comments").child(key).setValue(modelComment);
                    commentString.setText("");
                    Toast.makeText(PostDetailViewActivity.this, "commented", Toast.LENGTH_SHORT).show();
                    commentString.setHint("Comment here");
                }

            }
        });

//        commenterName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });



    }

    public void btn_back_post_detail(View view) {

        Intent intent=new Intent(PostDetailViewActivity.this,MainActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    public void btn_cart_post_detail(View view) {

        Intent intent=new Intent(PostDetailViewActivity.this,OrderActivity.class);
        startActivity(intent);
    }


    class QueryForComments implements ChildEventListener
    {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            final ModelComment modelComment = dataSnapshot.getValue(ModelComment.class);
            Log.d("Comment",""+modelComment.getComment());
            Log.d("Key",""+modelComment.getComment());


            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
            databaseReference.child(modelComment.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ModelUser  modelUser = dataSnapshot.getValue(ModelUser.class);
                    try
                    {
                        Log.d("User",""+modelUser.getFirstName());
                    }
                    catch(NullPointerException E)
                    {

                    }
                    commentsAdapter.setValues(modelComment,modelUser);
                    commentsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            commentsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }


}
