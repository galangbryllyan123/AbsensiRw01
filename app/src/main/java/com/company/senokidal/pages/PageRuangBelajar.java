package com.company.senokidal.pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdSize;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.company.senokidal.LoginActivity;
import com.company.senokidal.R;
import com.company.senokidal.model.Murid;
import com.company.senokidal.model.RuangBelajar;
import com.company.senokidal.utils.Iklan;

public class PageRuangBelajar extends AppCompatActivity {
    String TAG = "MainActivity";


    LinearLayout empty;
    RecyclerView recyclerView;
    private String getUserID;
    private FirebaseFirestore db;
    private AdapterRuangBelajar adapter;
    LinearLayoutManager linearLayoutManager;

    private BottomSheetDialog mBottomSheetDialog;
    private View bottomSheetLayout;


    AppCompatEditText editKey;
    TextInputEditText editNama, editNamaDesc;

    SweetAlertDialog sweetAlertDialog;
    ProgressDialog dialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_ruangbelajar);

        context = PageRuangBelajar.this;


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout linearLayout = findViewById(R.id.addView);
        linearLayout.addView(new Iklan(context).getAd1View(AdSize.BANNER,R.string.banner_ad_unit_id));

        init();
        getList();

    }


    private void init(){

        db = FirebaseFirestore.getInstance();
        getUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();



        if (getUserID == null) {

            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        empty = findViewById(R.id.empty_view);
        recyclerView = findViewById(R.id.recycle_view);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);





        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(PageRuangBelajar.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_ruangkelas, null);
        mBottomSheetDialog = new BottomSheetDialog(PageRuangBelajar.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });

        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());

        findViewById(R.id.fab_add).setOnClickListener(v->{
            mBottomSheetDialog.show();
        });



        editKey = bottomSheetLayout.findViewById(R.id.editKey);
        editNama = bottomSheetLayout.findViewById(R.id.tf_title);
        editNamaDesc = bottomSheetLayout.findViewById(R.id.tf_desc);




        bottomSheetLayout.findViewById(R.id.action_simpan).setOnClickListener(v -> {
            v.setEnabled(false);

            final String key = editKey.getText().toString();
            String nama = editNama.getText().toString();
            String desc = editNamaDesc.getText().toString();

            if(!TextUtils.isEmpty(key)){
                //update
                db.collection("users/" + getUserID + "/ruangbelajar").document(key).set(
                        new RuangBelajar(nama,desc)
                ).addOnSuccessListener(documentReference -> {

                    editKey.setText("");
                    editNama.setText("");
                    editNamaDesc.setText("");
                    v.setEnabled(true);

                    Toast.makeText(PageRuangBelajar.this, "Data Berhasil diubah", Toast.LENGTH_SHORT).show();
                });

            }else{

                //add
                db.collection("users/" + getUserID + "/ruangbelajar").add(
                        new RuangBelajar(nama,desc)
                ).addOnSuccessListener(documentReference -> {

                    editKey.setText("");
                    editNama.setText("");
                    editNamaDesc.setText("");
                    v.setEnabled(true);

                    Toast.makeText(PageRuangBelajar.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
                });

            }
        });


    }


    private void getList(){
        db.collection("users/"+getUserID+"/ruangbelajar")
                .orderBy("kelas_desc", Query.Direction.ASCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    ArrayList<RuangBelajar> items = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : value) {
                        RuangBelajar p = doc.toObject(RuangBelajar.class);
                        p.setKey( doc.getId() );

                        items.add( p );
                    }

                    Collections.sort(items);


                    if(items.size() > 0){
                        empty.setVisibility(View.GONE);


                        adapter = new AdapterRuangBelajar(items);
                        recyclerView.setAdapter(adapter);
                    }

                    sweetAlertDialog.dismiss();

                    Log.d(TAG, "Current cites in CA: ");
                });
    }


    public class AdapterRuangBelajar extends RecyclerView.Adapter<AdapterViewHolder> {
        private final ArrayList<RuangBelajar> lists;

        public AdapterRuangBelajar(@NonNull ArrayList<RuangBelajar> objects) {
            lists = objects;
        }

        @NonNull
        @Override
        public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ruangbelajar,parent,false);

            return new AdapterViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
            RuangBelajar model = lists.get(position);

            String docId = model.key;
            holder.tv2.setText(model.kelas_nama);
            holder.tv1.setText(model.kelas_desc);
            holder.tv3.setText("0 Anak");

            db.collection("users/"+getUserID+"/siswa")
                    .orderBy("siswa_nama", Query.Direction.ASCENDING)
                    .addSnapshotListener((value, e) -> {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        ArrayList<Murid> items = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : value) {
                            Murid p = doc.toObject(Murid.class);
                            p.setKey( doc.getId() );

                            if(p.kelas_key != null && p.kelas_key.equalsIgnoreCase(model.kelas_desc)){
                                items.add( p );
                            }
                        }

                        holder.tv3.setText( items.size() + " Anak");

                        Log.d(TAG, "Current cites in CA: ");
                    });


            holder.actDel.setOnClickListener(v -> {

                sweetAlertDialog = new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Anda yakin?")
                        .setContentText("Yakin mau hapus data?");
                sweetAlertDialog.setConfirmText("Yakin!");
                sweetAlertDialog.setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                    db.collection("users/" + getUserID + "/ruangbelajar").document(docId).delete();
                });
                sweetAlertDialog.setCancelText("Batal");
                sweetAlertDialog.showCancelButton(true);
                sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismissWithAnimation());
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();


            });


            holder.actEdit.setOnClickListener(v -> {

                editKey.setText(docId);
                editNama.setText(model.kelas_nama);
                editNamaDesc.setText(model.kelas_desc);

                mBottomSheetDialog.show();
            });


            holder.itemView.setOnClickListener(view -> {

                Intent intent = new Intent(view.getContext(), PageDaftarSiswa.class);
                intent.putExtra("kelas", model.kelas_desc);
                startActivity(intent);

            });

        }

        @Override
        public int getItemCount() {
            return lists.size();
        }
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView tv1, tv2, tv3;
        private ImageButton actEdit, actDel;
        private ImageButton actMore;
        public AdapterViewHolder(View itemView) {
            super(itemView);

            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
            tv3 = itemView.findViewById(R.id.tv3);

            //actMore = itemView.findViewById(R.id.action_more);
            actEdit = itemView.findViewById(R.id.action_edit);
            actDel = itemView.findViewById(R.id.action_del);

        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }





    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}