package com.company.senokidal.pages.kehadiran;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.company.senokidal.LoginActivity;
import com.company.senokidal.R;
import com.company.senokidal.Splash;
import com.company.senokidal.adapters.AdapterRuangBelajarDialog;
import com.company.senokidal.model.Kehadiran;
import com.company.senokidal.model.MataPelajaran;
import com.company.senokidal.model.RuangBelajar;
import com.company.senokidal.model.Status;
import com.company.senokidal.utils.Fungsi;
import com.company.senokidal.utils.Iklan;

public class PageCatatanKehadiran extends AppCompatActivity {

    String TAG = "MainActivity";

    SweetAlertDialog sweetAlertDialog;

    LinearLayout empty_up, empty_down, linierLayout1, linierLayout2;
    RecyclerView recycle_view_today,recycle_view_recent;
    private String getUserID;
    private FirebaseFirestore db;
    private AdapterKehadiran adapter_today, adapter_recent;
    LinearLayoutManager linearLayoutManager_today,linearLayoutManager_recent;


    private BottomSheetDialog mBottomSheetDialog;
    private View bottomSheetLayout;


    AppCompatEditText editKey;
    TextInputEditText edit1tanggal,edit1jam;
    AutoCompleteTextView edit2, edit3;

    Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String dateToday, dateTime;

    ArrayList<Status> status = new ArrayList<>();
    Context context;
    Iklan iklan;

    static SharedPreferences sharedpreferences;
    String semester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_catatankehadiran);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        semester = sharedpreferences.getString("semester","ganjil");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = PageCatatanKehadiran.this;
        iklan = new Iklan(context);

        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);

        dateToday = sdf.format(new Date());
        dateTime = hour+":"+minute;

        String f = new Fungsi().to(dateToday,null);

        //TextView toolbar_timetoday = findViewById(R.id.toolbar_time_today);
        //toolbar_timetoday.setText(f);

        LinearLayout linearLayout = findViewById(R.id.addView);
        linearLayout.addView(iklan.getAd1View(AdSize.BANNER, R.string.banner_ad_unit_id));

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

        linierLayout1 = findViewById(R.id.linierLayout1);
        linierLayout2 = findViewById(R.id.linierLayout2);

        empty_up = findViewById(R.id.empty_view_up);
        empty_down = findViewById(R.id.empty_view_down);


        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        db.collection("users/"+getUserID+"/ruangbelajar")
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<RuangBelajar> items = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()){
                            RuangBelajar item = document.toObject(RuangBelajar.class);
                            items.add(item);

                        }


                        Comparator<RuangBelajar> compareById = (o1, o2) -> o1.kelas_desc.compareTo(o2.kelas_desc);
                        Collections.sort(items, compareById);

                        ArrayAdapter arrayAdapter = new AdapterRuangBelajarDialog(PageCatatanKehadiran.this,R.layout.item_ruangbelajar_dialog, items);
                        edit2.setAdapter(arrayAdapter);
                        edit2.setOnItemClickListener((parent, view, position, id) -> {
                            String selected = items.get(position).kelas_desc;
                            edit2.setText(selected,false);
                        });
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        db.collection("users/"+getUserID+"/matapelajaran")
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<String> items = new ArrayList<>();

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()){
                            MataPelajaran item = document.toObject(MataPelajaran.class);
                            items.add(item.pelajaran_desc);
                        }

                        ArrayAdapter<String> itemsAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
                        edit3.setAdapter(itemsAdapter2);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });



        recycle_view_today = findViewById(R.id.recycle_view_today);
        recycle_view_today.setNestedScrollingEnabled(false);

        recycle_view_recent = findViewById(R.id.recycle_view_recent);
        recycle_view_recent.setNestedScrollingEnabled(false);

        linearLayoutManager_today = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recycle_view_today.setLayoutManager(linearLayoutManager_today);

        linearLayoutManager_recent = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recycle_view_recent.setLayoutManager(linearLayoutManager_recent);





        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(PageCatatanKehadiran.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_catatankehadiran, null);
        mBottomSheetDialog = new BottomSheetDialog(PageCatatanKehadiran.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });

        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());

        findViewById(R.id.fab_add).setOnClickListener(v->{

            editKey.setText("");
            edit1tanggal.setText(dateToday);
            edit1jam.setText(dateTime);
            edit2.setText("",false);
            edit2.setEnabled(true);
            edit3.setText("",false);
            edit3.setEnabled(true);


            mBottomSheetDialog.show();
        });



        editKey = bottomSheetLayout.findViewById(R.id.editKey);
        edit1tanggal = bottomSheetLayout.findViewById(R.id.tf_tanggal);
        edit1jam = bottomSheetLayout.findViewById(R.id.tf_jam);
        edit2 = bottomSheetLayout.findViewById(R.id.tf_kelas);
        edit3 = bottomSheetLayout.findViewById(R.id.tf_pelajaran);


        edit1tanggal.setText(sdf.format(new Date()));
        edit1tanggal.setOnClickListener(v->{

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                edit1tanggal.setText(sdf.format(myCalendar.getTime()));
            };

            new DatePickerDialog(
                    PageCatatanKehadiran.this,
                    date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();

        });

        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        edit1jam.setText(hour + ":" + minute);
        edit1jam.setOnClickListener(v->{
            TimePickerDialog mTimePicker = new TimePickerDialog(PageCatatanKehadiran.this, (timePicker, selectedHour, selectedMinute) -> {
                edit1jam.setText(selectedHour + ":" + selectedMinute);
            }, hour, minute, true);//Yes 24 hour time

            mTimePicker.show();
        });




        bottomSheetLayout.findViewById(R.id.action_simpan).setOnClickListener(v -> {
            String key = editKey.getText().toString();
            String tanggal = edit1tanggal.getText().toString();
            String jam = edit1jam.getText().toString();
            String kelas = edit2.getText().toString();
            String pelajaran = edit3.getText().toString();

            if(TextUtils.isEmpty(tanggal) ||TextUtils.isEmpty(jam) || TextUtils.isEmpty(kelas) || TextUtils.isEmpty(pelajaran) ){
                Toast.makeText(v.getContext(),"Periksa kembali field!",Toast.LENGTH_SHORT).show();
            }else{

                v.setEnabled(false);
                sweetAlertDialog.show();


                long _kehadiran_jam  = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                try {
                    Date mDate1 = sdf.parse(jam);

                    _kehadiran_jam = mDate1.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Timestamp timestamp = Fungsi.getTimestamp(tanggal+" "+jam);

                Map<String, Object> data = new HashMap<>();
                data.put("kehadiran_tanggal", tanggal );
                data.put("kehadiran_jam", _kehadiran_jam);
                data.put("kelas_key", kelas);
                data.put("pelajaran_key", pelajaran);

                if(!TextUtils.isEmpty(key)){

                    //update
                    db.collection("users/" + getUserID + "/kehadiran")
                            .document(key)
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener(documentReference -> {
                                editKey.setText("");
                                edit1tanggal.setText(dateToday);
                                edit1jam.setText(dateTime);
                                edit2.setText("",false);
                                edit2.setEnabled(true);
                                edit3.setText("",false);
                                edit3.setEnabled(true);

                                mBottomSheetDialog.dismiss();
                                v.setEnabled(true);

                                sweetAlertDialog.dismiss();

                                Toast.makeText(PageCatatanKehadiran.this, "Data Berhasil diubah", Toast.LENGTH_SHORT).show();

                            });

                }else{

                    //add



                    status = new ArrayList<>();

                    db.collection("users/"+getUserID+"/siswa")
                            .whereEqualTo("kelas_key",kelas)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        status.add(new Status(document.getId(),"Hadir"));
                                    }

                                    Log.d(TAG, "Success getting documents ");


                                    data.put("kehadiran_body", status);
                                    data.put("kehadiran_semester", semester);

                                    db.collection("users/" + getUserID + "/kehadiran")
                                            .add(data)
                                            .addOnSuccessListener(documentReference -> {

                                                String docId = documentReference.getId();

                                                editKey.setText("");
                                                edit1tanggal.setText(dateToday);
                                                edit1jam.setText(dateTime);
                                                edit2.setText("",false);
                                                edit2.setEnabled(true);
                                                edit3.setText("",false);
                                                edit3.setEnabled(true);

                                                sweetAlertDialog.dismiss();
                                                mBottomSheetDialog.dismiss();
                                                v.setEnabled(true);

                                                Toast.makeText(PageCatatanKehadiran.this, "Data Berhasil Dibuat", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(PageCatatanKehadiran.this, PageCatatanKehadiranView.class);
                                                intent.putExtra("key",docId);
                                                intent.putExtra("tanggal",tanggal);
                                                intent.putExtra("jam",jam);
                                                intent.putExtra("kelas",kelas);
                                                intent.putExtra("pelajaran",pelajaran);
                                                intent.putExtra("body", (ArrayList<Status>) status);
                                                startActivity(intent);


                                            });

                                } else {
                                    Log.e(TAG, "Error getting documents: ", task.getException());
                                }
                            });

                }


            }
        });


    }


    private void getList(){
        db.collection("users/"+getUserID+"/kehadiran")
                .orderBy("kehadiran_tanggal", Query.Direction.DESCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    int jum_a = 0;
                    int jum_b = 0;

                    ArrayList<Kehadiran> itemsToday = new ArrayList<>();
                    ArrayList<Kehadiran> itemsRecent = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : value) {
                        Kehadiran p = doc.toObject(Kehadiran.class);
                        p.setKey( doc.getId() );

                        Log.e("semester:",p.kehadiran_semester+"="+semester);

                        if(p.kehadiran_semester != null && p.kehadiran_semester.equals(semester)){

                            if(dateToday.equals(p.kehadiran_tanggal)){
                                itemsToday.add( p );
                                jum_a++;
                            }else{
                                itemsRecent.add( p );
                                jum_b++;
                            }

                        }
                    }

                    Collections.sort(itemsToday);
                    Collections.sort(itemsRecent);


                    adapter_today = new AdapterKehadiran(itemsToday);
                    recycle_view_today.setAdapter(adapter_today);


                    linierLayout1.setVisibility(View.GONE);
                    if(jum_a > 0){
                        linierLayout1.setVisibility(View.VISIBLE);
                        empty_up.setVisibility(View.GONE);
                    }


                    adapter_recent = new AdapterKehadiran(itemsRecent);
                    recycle_view_recent.setAdapter(adapter_recent);

                    linierLayout2.setVisibility(View.GONE);
                    empty_down.setVisibility(View.VISIBLE);
                    if(jum_b > 0){
                        linierLayout2.setVisibility(View.VISIBLE);
                        empty_down.setVisibility(View.GONE);
                    }

                    sweetAlertDialog.dismiss();

                    Log.d(TAG, "Current cites in CA: ");
                });
    }


    public class AdapterKehadiran extends RecyclerView.Adapter<AdapterViewHolder> {
        private final ArrayList<Kehadiran> lists;

        public AdapterKehadiran(@NonNull ArrayList<Kehadiran> objects) {
            lists = objects;
        }

        @NonNull
        @Override
        public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catatankehadiran,parent,false);

            return new AdapterViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
            Kehadiran model = lists.get(position);

            String docId = model.key;
            String kehadiran_jam = String.format("%1$tH:%1$tM", model.kehadiran_jam);

            String f = new Fungsi().to(model.kehadiran_tanggal,null);
            //String f = Fungsi.getFormatDate(kehadiran_tanggal);

            holder.tv1.setText(f);
            holder.tv2.setText(model.kelas_key);
            holder.tv3.setText(kehadiran_jam);
            holder.tv4.setText(model.pelajaran_key);
            ArrayList<Status> body_x = (ArrayList<Status>) model.kehadiran_body;

            int hadir = 0;
            int alfa = 0;
            int izin = 0;
            int sakit = 0;
            int terlambat = 0;

            for(Status bx:body_x){
                Log.e("xxx", docId+":"+bx.v );
                if (bx.v.equalsIgnoreCase("Hadir")) {
                    hadir++;

                } else if (bx.v.equalsIgnoreCase("Alfa")) {
                    alfa++;

                } else if (bx.v.equalsIgnoreCase("Izin")) {
                    izin++;

                } else if (bx.v.equalsIgnoreCase("Sakit")) {
                    sakit++;

                } else if (bx.v.equalsIgnoreCase("Terlambat")) {
                    terlambat++;

                }
            }

            holder.tvh.setText(String.valueOf(hadir));
            holder.tva.setText(String.valueOf(alfa));
            holder.tvi.setText(String.valueOf(izin));
            holder.tvs.setText(String.valueOf(sakit));
            holder.tvt.setText(String.valueOf(terlambat));


            holder.itemView.setOnClickListener(v -> {

                //ArrayList<KehadiranStatus> aa = model.kehadiran_body;
                //for(KehadiranStatus ab:aa){
                    //Log.e("xxx", ab.v );

                //}

                Intent intent = new Intent(PageCatatanKehadiran.this, PageCatatanKehadiranView.class);
                intent.putExtra("key",docId);
                intent.putExtra("tanggal",model.kehadiran_tanggal);
                intent.putExtra("jam",kehadiran_jam);
                intent.putExtra("kelas",model.kelas_key);
                intent.putExtra("pelajaran",model.pelajaran_key);
                intent.putExtra("body", body_x);

                startActivity(intent);

            });
            holder.actDel.setOnClickListener(v -> {

                sweetAlertDialog = new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Anda yakin?")
                        .setContentText("Yakin mau hapus data?");
                sweetAlertDialog.setConfirmText("Yakin!");
                sweetAlertDialog.setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                    db.collection("users/" + getUserID + "/kehadiran").document(docId).delete();
                });
                sweetAlertDialog.setCancelText("Batal");
                sweetAlertDialog.showCancelButton(true);
                sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismissWithAnimation());
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();


            });


            holder.actMore.setOnClickListener(v -> {
                editKey.setText(docId);
                //edit1tanggal.setText(model.kehadiran_tanggal);
                edit1jam.setText(kehadiran_jam);
                edit2.setText(model.kelas_key,false);
                edit2.setEnabled(false);
                edit3.setText(model.pelajaran_key,false);
                edit3.setEnabled(false);

                mBottomSheetDialog.show();
            });

        }

        @Override
        public int getItemCount() {
            return lists.size();
        }
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView tv1, tv2, tv3, tv4;
        private ImageView actEdit, actDel;
        private ImageButton actMore;
        private TextView tvh, tva, tvi, tvs, tvt;
        public AdapterViewHolder(View itemView) {
            super(itemView);

            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
            tv3 = itemView.findViewById(R.id.tv3);
            tv4 = itemView.findViewById(R.id.tv4);

            actMore = itemView.findViewById(R.id.action_icon);
            actDel = itemView.findViewById(R.id.action_del);
            //actMore = itemView.findViewById(R.id.action_more);
            //actEdit = itemView.findViewById(R.id.action_edit);
            //actDel = itemView.findViewById(R.id.action_del);

            tvh = itemView.findViewById(R.id.tvh);
            tva = itemView.findViewById(R.id.tva);
            tvi = itemView.findViewById(R.id.tvi);
            tvs = itemView.findViewById(R.id.tvs);
            tvt = itemView.findViewById(R.id.tvt);



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