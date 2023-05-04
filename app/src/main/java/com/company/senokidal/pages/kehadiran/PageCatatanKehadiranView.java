package com.company.senokidal.pages.kehadiran;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdSize;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import com.company.senokidal.R;
import com.company.senokidal.Splash;
import com.company.senokidal.model.Status;
import com.company.senokidal.model.Murid;
import com.company.senokidal.utils.Fungsi;
import com.company.senokidal.utils.Iklan;
import info.androidhive.fontawesome.FontTextView;

public class PageCatatanKehadiranView extends AppCompatActivity {

    String TAG = "MainActivity";

    RecyclerView recycle_view;
    private String getUserID;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;


    private BottomSheetDialog mBottomSheetDialog;
    private View bottomSheetLayout;


    AppCompatEditText editKey;
    TextInputEditText edit1tanggal,edit1jam;
    AutoCompleteTextView edit2, edit3;

    Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String dateToday, dateTime;

    String key;

    Map<String, Object> data = new HashMap<>();
    ArrayList<Status> body = new ArrayList<>();

    Context context;
    Iklan iklan;

    static SharedPreferences sharedpreferences;
    String semester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_catatankehadiranview);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView title = toolbar.findViewById(R.id.toolbar_title);
        TextView titlesub = toolbar.findViewById(R.id.toolbar_titlesub);

        context = PageCatatanKehadiranView.this;


        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        semester = sharedpreferences.getString("semester","ganjil");

        iklan = new Iklan(context);
        LinearLayout linearLayout = findViewById(R.id.addView);
        linearLayout.addView(iklan.getAd1View(AdSize.BANNER, R.string.banner_ad_unit_id));


        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        String tanggal = intent.getStringExtra("tanggal");
        String jam = intent.getStringExtra("jam");
        String kelas = intent.getStringExtra("kelas");
        String pelajaran = intent.getStringExtra("pelajaran");

        body = (ArrayList<Status>) intent.getSerializableExtra("body");


        long _kehadiran_jam  = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date mDate1 = sdf.parse(jam);

            _kehadiran_jam = mDate1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        data.put("kehadiran_tanggal", tanggal);
        data.put("kehadiran_jam", _kehadiran_jam);
        data.put("kelas_key", kelas);
        data.put("pelajaran_key", pelajaran);


        String f = new Fungsi().to(tanggal,null);

        title.setText(kelas);
        titlesub.setText(f+" "+jam);

        init();
        getList(kelas);

    }


    private void init(){

        db = FirebaseFirestore.getInstance();
        getUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        recycle_view = findViewById(R.id.recycle_view);
        recycle_view.setNestedScrollingEnabled(false);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recycle_view.setLayoutManager(linearLayoutManager);






        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(PageCatatanKehadiranView.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_catatankehadiranview, null);
        mBottomSheetDialog = new BottomSheetDialog(PageCatatanKehadiranView.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });
        (bottomSheetLayout.findViewById(R.id.fab_close)).setOnClickListener(view -> mBottomSheetDialog.dismiss());


    }

    private void getList(String kelas){


        Query query = db
                .collection("users/"+getUserID+"/siswa")
                .whereEqualTo("kelas_key",kelas)
                .orderBy("siswa_nama", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Murid> response = new FirestoreRecyclerOptions.Builder<Murid>()
                .setQuery(query, Murid.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Murid, AdapterViewHolder>(response) {

            @Override
            public void onBindViewHolder(AdapterViewHolder holder, int position, Murid model) {
                String docId = getSnapshots().getSnapshot(position).getId();


                holder.tv1.setText(model.siswa_nama);
                holder.tv2.setText(model.siswa_nis);

                for(Status itemBody:body){

                    if(itemBody.k.equalsIgnoreCase(docId)){

                        if (itemBody.v.equalsIgnoreCase("Hadir")) {
                            holder.imageView1.setText(R.string.fa_check_circle_solid);
                            holder.imageView1.setTextColor(context.getResources().getColor(R.color.colorMasuk));

                        } else if (itemBody.v.equalsIgnoreCase("Alfa")) {
                            holder.imageView1.setText(R.string.fa_times_circle_solid);
                            holder.imageView1.setTextColor(context.getResources().getColor(R.color.colorAlfa));

                        } else if (itemBody.v.equalsIgnoreCase("Izin")) {
                            holder.imageView1.setText(R.string.fa_envelope);
                            holder.imageView1.setTextColor(context.getResources().getColor(R.color.colorIzin));

                        } else if (itemBody.v.equalsIgnoreCase("Sakit")) {
                            holder.imageView1.setText(R.string.fa_plus_square_solid);
                            holder.imageView1.setTextColor(context.getResources().getColor(R.color.colorSakit));

                        } else if (itemBody.v.equalsIgnoreCase("Terlambat")) {
                            holder.imageView1.setText(R.string.fa_running_solid);
                            holder.imageView1.setTextColor(context.getResources().getColor(R.color.colorTerlambat));

                        }

                    }
                }

                holder.sex.setImageDrawable(null);

                if(model.siswa_jk.equalsIgnoreCase("Laki-laki") || model.siswa_jk.equalsIgnoreCase("l")){
                    holder.sex.setImageDrawable(getResources().getDrawable(R.drawable.ic_male));

                }else if(model.siswa_jk.equalsIgnoreCase("Perempuan") || model.siswa_jk.equalsIgnoreCase("p")){
                    holder.sex.setImageDrawable(getResources().getDrawable(R.drawable.ic_female));

                }

                if(!TextUtils.isEmpty(model.siswa_foto)){
                    Picasso.with(context)
                            .load(model.siswa_foto)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(holder.foto);
                }

                holder.itemView.setOnClickListener(v->{
                    mBottomSheetDialog.show();


                    FontTextView action_hadir_icon = bottomSheetLayout.findViewById(R.id.action_hadir_icon);
                    FontTextView action_alfa_icon = bottomSheetLayout.findViewById(R.id.action_alfa_icon);
                    FontTextView action_izin_icon = bottomSheetLayout.findViewById(R.id.action_izin_icon);
                    FontTextView action_sakit_icon = bottomSheetLayout.findViewById(R.id.action_sakit_icon);
                    FontTextView action_terlambat_icon = bottomSheetLayout.findViewById(R.id.action_terlambat_icon);

                    action_hadir_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));
                    action_alfa_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));
                    action_izin_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));
                    action_sakit_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));
                    action_terlambat_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));



                    for(Status itemBody:body){

                        if(itemBody.k.equalsIgnoreCase(docId)){

                            if (itemBody.v.equalsIgnoreCase("Hadir")) {
                                action_hadir_icon.setTextColor(context.getResources().getColor(R.color.colorMasuk));

                            } else if (itemBody.v.equalsIgnoreCase("Alfa")) {
                                action_alfa_icon.setTextColor(context.getResources().getColor(R.color.colorAlfa));

                            } else if (itemBody.v.equalsIgnoreCase("Izin")) {
                                action_izin_icon.setTextColor(context.getResources().getColor(R.color.colorIzin));

                            } else if (itemBody.v.equalsIgnoreCase("Sakit")) {
                                action_sakit_icon.setTextColor(context.getResources().getColor(R.color.colorSakit));

                            } else if (itemBody.v.equalsIgnoreCase("Terlambat")) {
                                action_terlambat_icon.setTextColor(context.getResources().getColor(R.color.colorTerlambat));

                            }

                        }
                    }



                    bottomSheetLayout.findViewById(R.id.action_hadir).setOnClickListener(y->{
                        mBottomSheetDialog.dismiss();

                        setDataItem(docId,"Hadir",holder,R.string.fa_check_circle_solid,R.color.colorMasuk);

                    });
                    bottomSheetLayout.findViewById(R.id.action_alfa).setOnClickListener(y->{
                        mBottomSheetDialog.dismiss();

                        setDataItem(docId,"Alfa",holder,R.string.fa_times_circle_solid,R.color.colorAlfa);

                    });
                    bottomSheetLayout.findViewById(R.id.action_izin).setOnClickListener(y->{
                        mBottomSheetDialog.dismiss();

                        setDataItem(docId,"Izin",holder,R.string.fa_envelope,R.color.colorIzin);

                    });
                    bottomSheetLayout.findViewById(R.id.action_sakit).setOnClickListener(y->{
                        mBottomSheetDialog.dismiss();

                        setDataItem(docId,"Sakit",holder,R.string.fa_plus_square_solid,R.color.colorSakit);

                    });
                    bottomSheetLayout.findViewById(R.id.action_terlambat).setOnClickListener(y->{
                        mBottomSheetDialog.dismiss();

                        setDataItem(docId,"Terlambat",holder,R.string.fa_running_solid,R.color.colorTerlambat);

                    });

                });

            }

            @Override
            public AdapterViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_daftarsiswaview_kehadiran, group, false);

                return new AdapterViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }

        };

        adapter.notifyDataSetChanged();
        recycle_view.setAdapter(adapter);
    }

    public void setDataItem(String docId, String status, AdapterViewHolder holder, int icon, int color) {

        int _id = 0;
        for(Status itemBody:body) {

            Log.e("docId",itemBody.k+"<==>"+docId);

            if (itemBody.k.equalsIgnoreCase(docId)) {
                body.set(_id,new Status(docId,status) );
            }

            _id++;
        }

        data.put("kehadiran_body", body);
        data.put("kehadiran_semester", semester);
        db.collection("users/" + getUserID + "/kehadiran")
                .document(key)
                .set(data)
                .addOnSuccessListener(documentReference -> {
                    holder.imageView1.setText(icon);
                    holder.imageView1.setTextColor(context.getResources().getColor(color));
                });


    }


    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        FontTextView imageView1;
        private TextView tv1, tv2, tv3;
        private ImageView actEdit, actDel;
        private ImageView sex;
        private CircleImageView foto;
        public AdapterViewHolder(View itemView) {
            super(itemView);

            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
            sex = itemView.findViewById(R.id.sex);
            foto = itemView.findViewById(R.id.foto);
            imageView1 = itemView.findViewById(R.id.imageView1);


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
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