package com.lazydevs.tinylens.Activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lazydevs.tinylens.Model.ModelOrder;
import com.lazydevs.tinylens.R;
import com.lazydevs.tinylens.adapter.OrderListAdapter;

import java.util.ArrayList;

public class OrderListActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    RecyclerView recyclerVieworderListView;
    OrderListAdapter orderListAdapter;

    ArrayList<ModelOrder> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        orders = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("orders");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerVieworderListView=(RecyclerView)findViewById(R.id.rv_order_list);

        LinearLayoutManager linearVertical = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerVieworderListView.setLayoutManager(linearVertical);
        orderListAdapter = new OrderListAdapter(getApplicationContext(),orders);
        recyclerVieworderListView.setAdapter(orderListAdapter);

        Query query = FirebaseDatabase.getInstance().getReference().child("orders");
        query.orderByChild("buyerId").equalTo(firebaseAuth.getCurrentUser().getUid()).addChildEventListener(new QueryForOrders());
    }

    public void btn_back_order_list(View view) {
        onBackPressed();
    }

    private class QueryForOrders implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            final ModelOrder modelOrder = dataSnapshot.getValue(ModelOrder.class);
            orderListAdapter.setValues(modelOrder);
            orderListAdapter.notifyDataSetChanged();

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
