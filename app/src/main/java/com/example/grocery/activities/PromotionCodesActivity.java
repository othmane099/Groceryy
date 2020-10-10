package com.example.grocery.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.grocery.R;
import com.example.grocery.adapters.AdapterPromotionShop;
import com.example.grocery.models.ModelPromotion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

public class PromotionCodesActivity extends AppCompatActivity {

    private ImageButton backBtn, addPromoBtn, filterBtn;
    private TextView filteredTv;
    private RecyclerView promoRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelPromotion> modelPromotionList;
    private AdapterPromotionShop adapterPromotionShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_codes);

        backBtn = findViewById(R.id.backBtn);
        addPromoBtn = findViewById(R.id.addPromoBtn);
        filterBtn = findViewById(R.id.filterBtn);
        filteredTv = findViewById(R.id.filteredTv);
        promoRv = findViewById(R.id.promoRv);

        firebaseAuth = FirebaseAuth.getInstance();
        loadAllPromoCode();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle click, open add promo code activity
        addPromoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PromotionCodesActivity.this, AddPromotionCodeActivity.class));
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
            }
        });
    }

    private void filterDialog() {

        String[] options = {"All", "Expired", "Not Expired"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Promotion Code")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            filteredTv.setText("All Promotion Codes");
                            loadAllPromoCode();
                        }else if (which==1){
                            filteredTv.setText("Expired Promotion Codes");
                            loadExpiredPromoCodes();
                        }else if (which==2){
                            filteredTv.setText("Not Expired Promotion Codes");
                            loadNotExpiredPromoCodes();
                        }
                    }
                })
                .show();
    }

    private void loadAllPromoCode(){

        modelPromotionList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelPromotionList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()){
                            ModelPromotion promotion = ds.getValue(ModelPromotion.class);

                            modelPromotionList.add(promotion);
                        }

                        adapterPromotionShop = new AdapterPromotionShop(PromotionCodesActivity.this, modelPromotionList);
                        promoRv.setAdapter(adapterPromotionShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadExpiredPromoCodes(){
        DecimalFormat mFormat = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        final String todayDate = day + "/" + month + "/" + year ;

        modelPromotionList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelPromotionList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ModelPromotion promotion = ds.getValue(ModelPromotion.class);

                            String expDate = promotion.getExpireDate();

                            /*  Check for expired  */
                            try {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date currentDate = simpleDateFormat.parse(todayDate);
                                Date expireDate = simpleDateFormat.parse(expDate);
                                if (expireDate.compareTo(currentDate) > 0){
                                    //date 1 occurs after date 2
                                }else if (expireDate.compareTo(currentDate) < 0){
                                    //date 1 occurs before date 2 (i.e. Expired)
                                    modelPromotionList.add(promotion);

                                }else if (expireDate.compareTo(currentDate) == 0){
                                    //both are equals
                                }

                            }catch (Exception e){

                            }
                        }

                        adapterPromotionShop = new AdapterPromotionShop(PromotionCodesActivity.this, modelPromotionList);
                        promoRv.setAdapter(adapterPromotionShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadNotExpiredPromoCodes(){
        DecimalFormat mFormat = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        final String todayDate = day + "/" + month + "/" + year ;
        Log.d("ss",""+month);
        Log.d("ss",""+year);
        Log.d("ss",""+day);

        modelPromotionList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelPromotionList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ModelPromotion promotion = ds.getValue(ModelPromotion.class);

                            String expDate = promotion.getExpireDate();

                            /*  Check for expired  */
                            try {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date currentDate = simpleDateFormat.parse(todayDate);
                                Date expireDate = simpleDateFormat.parse(expDate);
                                if (expireDate.compareTo(currentDate) > 0){
                                    //date 1 occurs after date 2
                                    modelPromotionList.add(promotion);
                                }else if (expireDate.compareTo(currentDate) < 0){
                                    //date 1 occurs before date 2 (i.e. Expired)


                                }else if (expireDate.compareTo(currentDate) == 0){
                                    //both are equals
                                    modelPromotionList.add(promotion);
                                }

                            }catch (Exception e){

                            }
                        }

                        adapterPromotionShop = new AdapterPromotionShop(PromotionCodesActivity.this, modelPromotionList);
                        promoRv.setAdapter(adapterPromotionShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
