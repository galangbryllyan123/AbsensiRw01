package com.company.senokidal.pages.sikapprilaku;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import com.company.senokidal.R;
import com.company.senokidal.Splash;
import com.company.senokidal.model.Murid;
import com.company.senokidal.model.RuangBelajar;
import com.company.senokidal.model.SikapPrilaku;
import com.company.senokidal.model.Status;
import com.company.senokidal.utils.Fungsi;
import com.company.senokidal.utils.Iklan;

public class PageCatatanSikapPrilakuView extends AppCompatActivity {

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
        setContentView(R.layout.page_catatansikapprilakuview);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView title = toolbar.findViewById(R.id.toolbar_title);
        TextView titlesub = toolbar.findViewById(R.id.toolbar_titlesub);


        context = PageCatatanSikapPrilakuView.this;


        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        semester = sharedpreferences.getString("semester","ganjil");

        iklan = new Iklan(context);
        LinearLayout linearLayout = findViewById(R.id.addView);
        linearLayout.addView(iklan.getAd1View(AdSize.BANNER, R.string.banner_ad_unit_id));

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        String tanggal = intent.getStringExtra("tanggal");
        String ket = intent.getStringExtra("ket");
        String kelas = intent.getStringExtra("kelas");
        body = (ArrayList<Status>) intent.getSerializableExtra("body");


        String f = new Fungsi().to(tanggal,null);

        title.setText(kelas);
        titlesub.setText(f);

        data.put("sikapprilaku_tanggal", tanggal);
        data.put("sikapprilaku_ket", ket);
        data.put("kelas_key", kelas);

        init();

        getList(kelas);

    }


    private void init(){

        db = FirebaseFirestore.getInstance();
        getUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users/"+getUserID+"/ruangbelajar")
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<RuangBelajar> items = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()){
                            RuangBelajar item = document.toObject(RuangBelajar.class);
                            items.add(item);

                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });




        db.collection("users/"+getUserID+"/sikapprilaku")
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<SikapPrilaku> items = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()){
                            SikapPrilaku p = document.toObject(SikapPrilaku.class);
                            p.setKey( document.getId() );
                            items.add(p);
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        recycle_view = findViewById(R.id.recycle_view);
        recycle_view.setNestedScrollingEnabled(false);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recycle_view.setLayoutManager(linearLayoutManager);






        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(PageCatatanSikapPrilakuView.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_catatansikapprilakuview, null);
        mBottomSheetDialog = new BottomSheetDialog(PageCatatanSikapPrilakuView.this);
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

                for(Status itemBody:body) {

                    if (itemBody.k.equalsIgnoreCase(docId)) {
                        holder.tv0.setText(itemBody.v);

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


                    TextView action_a_icon = bottomSheetLayout.findViewById(R.id.action_a_icon);
                    TextView action_b_icon = bottomSheetLayout.findViewById(R.id.action_b_icon);
                    TextView action_c_icon = bottomSheetLayout.findViewById(R.id.action_c_icon);
                    TextView action_d_icon = bottomSheetLayout.findViewById(R.id.action_d_icon);
                    TextView action_e_icon = bottomSheetLayout.findViewById(R.id.action_e_icon);

                    action_a_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));
                    action_b_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));
                    action_c_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));
                    action_d_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));
                    action_e_icon.setTextColor(context.getResources().getColor(R.color.colorTextThird));


                    for(Status itemBody:body){

                        if(itemBody.k.equalsIgnoreCase(docId)){

                            if (itemBody.v.equalsIgnoreCase("A")) {
                                action_a_icon.setTextColor(context.getResources().getColor(R.color.colorAccent));

                            } else if (itemBody.v.equalsIgnoreCase("B")) {
                                action_b_icon.setTextColor(context.getResources().getColor(R.color.colorAccent));

                            } else if (itemBody.v.equalsIgnoreCase("C")) {
                                action_c_icon.setTextColor(context.getResources().getColor(R.color.colorAccent));

                            } else if (itemBody.v.equalsIgnoreCase("D")) {
                                action_d_icon.setTextColor(context.getResources().getColor(R.color.colorAccent));

                            } else if (itemBody.v.equalsIgnoreCase("E")) {
                                action_e_icon.setTextColor(context.getResources().getColor(R.color.colorAccent));

                            }

                        }
                    }

                    bottomSheetLayout.findViewById(R.id.action_a).setOnClickListener(view->{
                        mBottomSheetDialog.dismiss();

                        setDataItem(docId,holder,"A",view);
                    });

                    bottomSheetLayout.findViewById(R.id.action_b).setOnClickListener(view->{
                        mBottomSheetDialog.dismiss();

                        setDataItem(docId,holder,"B",view);
                    });

                    bottomSheetLayout.findViewById(R.id.action_c).setOnClickListener(view->{
                        mBottomSheetDialog.dismiss();

                        setDataItem(docId,holder,"C",view);
                    });

                    bottomSheetLayout.findViewById(R.id.action_d).setOnClickListener(view->{
                        mBottomSheetDialog.dismiss();

                        setDataItem(docId,holder,"D",view);
                    });

                    bottomSheetLayout.findViewById(R.id.action_e).setOnClickListener(view->{
                        mBottomSheetDialog.dismiss();

                        setDataItem(docId,holder,"E",view);
                    });
                });

            }

            @Override
            public AdapterViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_daftarsiswaview_sikapprilaku, group, false);

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

    public void setDataItem(String docId, AdapterViewHolder holder, String huruf, View view) {

        int _id = 0;
        for(Status itemBody:body) {

            if (itemBody.k.equalsIgnoreCase(docId)) {
                body.set(_id,new Status(docId,huruf) );
            }

            _id++;
        }

        data.put("sikapprilaku_body", body);
        data.put("sikapprilaku_semester", semester);
        db.collection("users/" + getUserID + "/sikapprilaku")
                .document(key)
                .set(data)
                .addOnSuccessListener(documentReference -> {
                    holder.tv0.setText(huruf);
                    mBottomSheetDialog.dismiss();
                    view.setEnabled(true);
                });
    }


    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView tv1, tv2, tv0;
        private ImageView actEdit, actDel;
        private ImageView sex;
        private CircleImageView foto;
        public AdapterViewHolder(View itemView) {
            super(itemView);

            tv0 = itemView.findViewById(R.id.tv0);
            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
            sex = itemView.findViewById(R.id.sex);
            foto = itemView.findViewById(R.id.foto);


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