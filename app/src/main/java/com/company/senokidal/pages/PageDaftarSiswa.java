package com.company.senokidal.pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import biz.laenger.android.vpbs.BottomSheetUtils;
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import com.company.senokidal.LoginActivity;
import com.company.senokidal.R;
import com.company.senokidal.Splash;
import com.company.senokidal.adapters.AdapterAkumulasi1;
import com.company.senokidal.adapters.AdapterAkumulasi2;
import com.company.senokidal.adapters.AdapterAkumulasi3;
import com.company.senokidal.model.Akumulasi;
import com.company.senokidal.model.Kehadiran;
import com.company.senokidal.model.Murid;
import com.company.senokidal.model.Penilaian;
import com.company.senokidal.model.SikapPrilaku;
import com.company.senokidal.model.Status;
import com.company.senokidal.utils.Iklan;
import com.company.senokidal.utils.NonSwipeableViewPager;

public class PageDaftarSiswa extends AppCompatActivity {

    static String TAG = "MainActivity";


    LinearLayout empty;
    RecyclerView recyclerView;
    private static String getUserID;
    private static FirebaseFirestore db;
    LinearLayoutManager linearLayoutManager;
    private AdapterDaftarSiswa adapter;

    private BottomSheetDialog mBottomSheetDialog, mBottomSheetDialog2;
    private View bottomSheetLayout, bottomSheetLayout2;
    private BottomSheetBehavior mBehavior, mBehavior2;


    AppCompatEditText editKey,editKeyFoto;
    TextInputEditText edit1, edit2;
    AutoCompleteTextView edit3;

    ImageView sex;
    TextView tv1, tv2, tv3;

    SweetAlertDialog sweetAlertDialog;
    ProgressDialog dialog;

    static SharedPreferences sharedpreferences;
    String kelas;
    Context context;

    private static int REQUEST_PERMISSION_GALLERY = 11101;
    private static int REQUEST_PERMISSION_CAMERA = 11102;

    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    CircleImageView foto, foto2;

    private static String semester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_daftarsiswa);

        Intent intent = getIntent();
        kelas = intent.getStringExtra("kelas");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = PageDaftarSiswa.this;

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        semester = sharedpreferences.getString("semester","ganjil");

        LinearLayout linearLayout = findViewById(R.id.addView);
        linearLayout.addView(new Iklan(context).getAd1View(AdSize.BANNER, R.string.banner_ad_unit_id));

        init();
        getList();


    }


    private void init(){

        db = FirebaseFirestore.getInstance();
        getUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();



        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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


        /**
         * BOTTOM SHEET ADD
         */
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(PageDaftarSiswa.this);
        bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_daftarsiswa, null);
        mBottomSheetDialog = new BottomSheetDialog(PageDaftarSiswa.this);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());
        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });

        bottomSheetLayout.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog.dismiss());

        findViewById(R.id.fab_add).setOnClickListener(v->{
            mBottomSheetDialog.show();
        });



        editKey = bottomSheetLayout.findViewById(R.id.editKey);
        editKeyFoto = bottomSheetLayout.findViewById(R.id.editKeyFoto);
        edit1 = bottomSheetLayout.findViewById(R.id.tf_nama);
        edit2 = bottomSheetLayout.findViewById(R.id.tf_nisn);
        edit3 = bottomSheetLayout.findViewById(R.id.tf_jk);
        foto = bottomSheetLayout.findViewById(R.id.foto);



        /**
         * BOTTOM SHEET VIEW
         */
        LayoutInflater layoutInflaterAndroid2 = LayoutInflater.from(PageDaftarSiswa.this);
        bottomSheetLayout2 = layoutInflaterAndroid2.inflate(R.layout.bottomsheet_catatankehadiran_siswa, null);
        mBottomSheetDialog2 = new BottomSheetDialog(PageDaftarSiswa.this);
        mBottomSheetDialog2.setCancelable(false);
        mBottomSheetDialog2.setContentView(bottomSheetLayout2);


        mBehavior2 = BottomSheetBehavior.from((View) bottomSheetLayout2.getParent());
        mBottomSheetDialog2.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout2.getHeight());//get the height dynamically
        });

        bottomSheetLayout2.findViewById(R.id.fab_close).setOnClickListener(view -> mBottomSheetDialog2.dismiss());


        tv1 = bottomSheetLayout2.findViewById(R.id.tf_nama);
        tv2 = bottomSheetLayout2.findViewById(R.id.tf_nisn);
        tv3 = bottomSheetLayout2.findViewById(R.id.tf_jk);
        sex = bottomSheetLayout2.findViewById(R.id.sex);
        foto2 = bottomSheetLayout2.findViewById(R.id.foto2);








        ArrayList<String> items = new ArrayList<>();
        items.add("Laki-laki");
        items.add("Perempuan");
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        edit3.setAdapter(itemsAdapter);


        bottomSheetLayout.findViewById(R.id.action_simpan).setOnClickListener(v -> {
            v.setEnabled(false);

            final String key = editKey.getText().toString();
            final String keyFoto = editKeyFoto.getText().toString();
            String nama = edit1.getText().toString();
            String nisn = edit2.getText().toString();
            String jk = edit3.getText().toString();

            if(!TextUtils.isEmpty(key)){
                //update
                db.collection("users/" + getUserID + "/siswa")
                        .document(key)
                        .set(new Murid(kelas,nisn,nama,jk,keyFoto))
                        .addOnSuccessListener(documentReference -> {

                            editKey.setText("");
                            editKeyFoto.setText("");
                            edit1.setText("");
                            edit2.setText("");
                            edit3.setText("");
                            foto.setImageResource(R.drawable.family_avatar);
                            v.setEnabled(true);
                            mBottomSheetDialog.dismiss();

                            Toast.makeText(PageDaftarSiswa.this, "Data Berhasil diubah", Toast.LENGTH_SHORT).show();
                });

            }else{

                //add
                db.collection("users/" + getUserID + "/siswa")
                        .add(new Murid(kelas,nisn,nama,jk,keyFoto))
                        .addOnSuccessListener(documentReference -> {

                            editKey.setText("");
                            editKeyFoto.setText("");
                            edit1.setText("");
                            edit2.setText("");
                            edit3.setText("");
                            foto.setImageResource(R.drawable.family_avatar);
                            v.setEnabled(true);

                            mBottomSheetDialog.dismiss();

                            Toast.makeText(PageDaftarSiswa.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
                });

            }

        });


    }


    private void getList(){
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

                        if(p.kelas_key != null && p.kelas_key.equalsIgnoreCase(kelas)){
                            items.add( p );
                        }
                    }

                    Collections.sort(items);


                    if(items.size() > 0){
                        empty.setVisibility(View.GONE);


                        adapter = new AdapterDaftarSiswa(items);
                        recyclerView.setAdapter(adapter);
                    }

                    sweetAlertDialog.dismiss();

                    Log.d(TAG, "Current cites in CA: ");
                });
    }



    public class AdapterDaftarSiswa extends RecyclerView.Adapter<AdapterViewHolder> {
        private final ArrayList<Murid> lists;

        public AdapterDaftarSiswa(@NonNull ArrayList<Murid> objects) {
            lists = objects;
        }

        @NonNull
        @Override
        public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daftarsiswa,parent,false);

            return new AdapterViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
            Murid model = lists.get(position);


            String docId = model.key;
            holder.tv1.setText(model.siswa_nama);
            holder.tv2.setText(model.siswa_nis);

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



            holder.actDel.setOnClickListener(v -> {

                sweetAlertDialog = new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Anda yakin?")
                        .setContentText("Yakin mau hapus data?");
                sweetAlertDialog.setConfirmText("Yakin!");
                sweetAlertDialog.setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                    db.collection("users/" + getUserID + "/siswa").document(docId).delete();
                });
                sweetAlertDialog.setCancelText("Batal");
                sweetAlertDialog.showCancelButton(true);
                sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismissWithAnimation());
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();


            });


            holder.actEdit.setOnClickListener(v -> {

                editKey.setText(docId);
                edit1.setText(model.siswa_nama);
                edit2.setText(model.siswa_nis);
                edit3.setText(model.siswa_jk);

                if(!TextUtils.isEmpty(model.siswa_foto)){
                    Picasso.with(context)
                            .load(model.siswa_foto)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(foto);
                }

                mBottomSheetDialog.show();
            });


            tv1.setText("Unknown");
            tv2.setText("0");
            tv3.setText("Unknown");
            holder.itemView.setOnClickListener(view -> {
                mBottomSheetDialog2.show();
                tv1.setText(model.siswa_nama);
                tv2.setText(model.siswa_nis);
                tv3.setText(model.siswa_jk);


                if(model.siswa_jk.equalsIgnoreCase("Laki-laki") || model.siswa_jk.equalsIgnoreCase("l")){
                    sex.setImageDrawable(getResources().getDrawable(R.drawable.ic_male));

                }else if(model.siswa_jk.equalsIgnoreCase("Perempuan") || model.siswa_jk.equalsIgnoreCase("p")){
                    sex.setImageDrawable(getResources().getDrawable(R.drawable.ic_female));

                }


                if(!TextUtils.isEmpty(model.siswa_foto)){
                    Picasso.with(context)
                            .load(model.siswa_foto)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(foto2);
                }

                /**
                 * BOTTOMSHEET VIEWPAGER
                 */

                TabLayout bottomSheetTabLayout = bottomSheetLayout2.findViewById(R.id.tabLayoutX);
                NonSwipeableViewPager bottomSheetViewPager = bottomSheetLayout2.findViewById(R.id.viewPagerX);
                DialogFragment.SimplePagerAdapter dialogFragment = new DialogFragment.SimplePagerAdapter(view.getContext(),model.kelas_key,docId);

                //bottomSheetViewPager.setOffscreenPageLimit(1);
                bottomSheetViewPager.setAdapter(dialogFragment);
                bottomSheetTabLayout.setupWithViewPager(bottomSheetViewPager);
                BottomSheetUtils.setupViewPager(bottomSheetViewPager);


            });



            /**
             * EDIT FOTO
             */
            bottomSheetLayout.findViewById(R.id.edit).setOnClickListener(view -> {

                showPictureDialog(view.getContext());
            });

        }

        @Override
        public int getItemCount() {
            return lists.size();
        }
    }

    public static class DialogFragment extends ViewPagerBottomSheetDialogFragment {


        public static class SimplePagerAdapter extends PagerAdapter {

            private String docId;
            private String kelas;
            private Context context;
            SimplePagerAdapter(Context context, String kelas, String docId){
                this.context=context;
                this.kelas=kelas;
                this.docId=docId;
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return object == view;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String title = null;
                if (position == 0){
                    title = "Kehadiran";
                }else if (position == 1){
                    title = "Penilaian";
                }else if (position == 2){
                    title = "Sikap dan Prilaku";
                }
                return title;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position){
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View ly1 = layoutInflater.inflate(R.layout.akumulasi_kehadiran,container,false);
                View ly2 = layoutInflater.inflate(R.layout.akumulasi_penilaian,container,false);
                View ly3 = layoutInflater.inflate(R.layout.akumulasi_sikapprilaku,container,false);

                /**
                 * KEHADIRAN
                 */

                TextView tv1_kehadiran = ly1.findViewById(R.id.tv1);
                TextView tv2_kehadiran = ly1.findViewById(R.id.tv2);
                TextView tv3_kehadiran = ly1.findViewById(R.id.tv3);
                TextView tv4_kehadiran = ly1.findViewById(R.id.tv4);
                TextView tv5_kehadiran = ly1.findViewById(R.id.tv5);
                LinearLayout empty_view_kehadiran = ly1.findViewById(R.id.empty_view_down);
                RecyclerView recycle_view_kehadiran = ly1.findViewById(R.id.recycle_view_recent);
                recycle_view_kehadiran.setNestedScrollingEnabled(false);

                LinearLayoutManager linearLayoutManager_kehadiran = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                recycle_view_kehadiran.setLayoutManager(linearLayoutManager_kehadiran);

                db.collection("users/"+getUserID+"/kehadiran")
                        .whereEqualTo("kelas_key",kelas)
                        .whereEqualTo("kehadiran_semester",semester)
                        .addSnapshotListener((value4, e4) -> {

                            if (e4 != null) {
                                Log.w(TAG, "Listen failed.", e4);
                                return;
                            }

                            int hadir = 0;
                            int alfa = 0;
                            int izin = 0;
                            int sakit = 0;
                            int terlambat = 0;


                            ArrayList<Akumulasi> data_ = new ArrayList<>();
                            for (QueryDocumentSnapshot document4 : value4) {
                                Kehadiran item = document4.toObject(Kehadiran.class);

                                int _id = 0;
                                for (Status itemBody : item.kehadiran_body) {

                                    if(itemBody.k.equalsIgnoreCase(docId)){
                                        long tanggal_miliseconds = 0;

                                        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-d");
                                        Date date1 = null;
                                        try{
                                            date1 = s.parse(item.kehadiran_tanggal);
                                        }catch (Exception e1){
                                            e1.printStackTrace();
                                        }
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.setTime(date1);

                                        tanggal_miliseconds = calendar.getTimeInMillis();

                                        data_.add(new Akumulasi(item.kehadiran_tanggal,tanggal_miliseconds,itemBody.v,""));

                                        if (itemBody.v.equalsIgnoreCase("Hadir")) {
                                            hadir++;

                                        } else if (itemBody.v.equalsIgnoreCase("Alfa")) {
                                            alfa++;

                                        } else if (itemBody.v.equalsIgnoreCase("Izin")) {
                                            izin++;

                                        } else if (itemBody.v.equalsIgnoreCase("Sakit")) {
                                            sakit++;

                                        } else if (itemBody.v.equalsIgnoreCase("Terlambat")) {
                                            terlambat++;

                                        }

                                        _id++;
                                    }
                                }

                            }

                            tv1_kehadiran.setText(String.valueOf(hadir));
                            tv2_kehadiran.setText(String.valueOf(terlambat));
                            tv3_kehadiran.setText(String.valueOf(izin));
                            tv4_kehadiran.setText(String.valueOf(sakit));
                            tv5_kehadiran.setText(String.valueOf(alfa));



                            if(data_.size() > 0){
                                empty_view_kehadiran.setVisibility(View.GONE);

                                Collections.sort(data_);

                                AdapterAkumulasi1 adapter_ = new AdapterAkumulasi1(context,data_);
                                recycle_view_kehadiran.setAdapter(adapter_);
                            }

                        });


                /**
                 * PENILAIAN
                 */


                TextView tv1_penilaian = ly2.findViewById(R.id.tv1);
                TextView tv2_penilaian = ly2.findViewById(R.id.tv2);
                TextView tv3_penilaian = ly2.findViewById(R.id.tv3);
                TextView tv4_penilaian = ly2.findViewById(R.id.tv4);
                LinearLayout empty_view_penilaian = ly2.findViewById(R.id.empty_view_down);
                RecyclerView recycle_view_penilaian = ly2.findViewById(R.id.recycle_view_recent);
                recycle_view_penilaian.setNestedScrollingEnabled(false);

                LinearLayoutManager linearLayoutManager_penilaian = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                recycle_view_penilaian.setLayoutManager(linearLayoutManager_penilaian);


                db.collection("users/"+getUserID+"/penilaian")
                        .whereEqualTo("kelas_key",kelas)
                        .whereEqualTo("penilaian_semester",semester)
                        .addSnapshotListener((value4, e4) -> {

                            if (e4 != null) {
                                Log.w(TAG, "Listen failed.", e4);
                                return;
                            }


                            Integer total = 0;
                            Integer jumlah = 0;
                            int rata2x = 0;

                            List<Integer> n = new ArrayList<>();

                            int count_nilai = 0;
                            int nilai_total = 0;
                            int nilai_max = 0;
                            int nilai_min = 0;

                            List<Integer> list_nilai = new ArrayList<>();

                            ArrayList<Akumulasi> data_ = new ArrayList<>();
                            for (QueryDocumentSnapshot document4 : value4) {
                                Penilaian item = document4.toObject(Penilaian.class);


                                int _id = 0;
                                for (Status itemBody : item.penilaian_body) {

                                    if(itemBody.k.equalsIgnoreCase(docId)){
                                        long tanggal_miliseconds = 0;

                                        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-d");
                                        Date date1 = null;
                                        try{
                                            date1 = s.parse(item.penilaian_tanggal);
                                        }catch (Exception e2){
                                            e2.printStackTrace();
                                        }
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.setTime(date1);

                                        tanggal_miliseconds = calendar.getTimeInMillis();

                                        data_.add(new Akumulasi(item.penilaian_tanggal,tanggal_miliseconds,itemBody.v,item.penilaian_ket));

                                        count_nilai++;
                                        nilai_total = nilai_total + Integer.parseInt(nilai_total+itemBody.v);
                                        list_nilai.add(Integer.valueOf(itemBody.v));

                                        n.add( Integer.valueOf(itemBody.v) );
                                        total = total + Integer.valueOf(itemBody.v);

                                        _id++;
                                    }
                                }

                            }

                            if( count_nilai > 0 ){
                                nilai_max = Collections.max(list_nilai);
                                nilai_min = Collections.min(list_nilai);

                                jumlah = n.size();
                                rata2x = total/jumlah;
                            }

                            tv1_penilaian.setText(total+"/"+count_nilai);
                            tv2_penilaian.setText(String.valueOf(nilai_max));
                            tv3_penilaian.setText(String.valueOf(nilai_min));
                            tv4_penilaian.setText(String.valueOf(rata2x));


                            if(data_.size() > 0){
                                empty_view_penilaian.setVisibility(View.GONE);

                                Collections.sort(data_);

                                AdapterAkumulasi2 adapter_ = new AdapterAkumulasi2(context,data_);
                                recycle_view_penilaian.setAdapter(adapter_);
                            }

                        });

                /**
                 * SIKAP DAN PRILAKU
                 */



                TextView tv1_sikapprilaku = ly3.findViewById(R.id.tv1);
                TextView tv2_sikapprilaku = ly3.findViewById(R.id.tv2);
                TextView tv3_sikapprilaku = ly3.findViewById(R.id.tv3);
                TextView tv4_sikapprilaku = ly3.findViewById(R.id.tv4);
                TextView tv5_sikapprilaku = ly3.findViewById(R.id.tv5);

                LinearLayout empty_view_sikapprilaku = ly3.findViewById(R.id.empty_view_down);
                RecyclerView recycle_view_sikapprilaku = ly3.findViewById(R.id.recycle_view_recent);
                recycle_view_penilaian.setNestedScrollingEnabled(false);

                LinearLayoutManager linearLayoutManager_sikapprilaku = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                recycle_view_sikapprilaku.setLayoutManager(linearLayoutManager_sikapprilaku);

                db.collection("users/"+getUserID+"/sikapprilaku")
                        .whereEqualTo("kelas_key",kelas)
                        .whereEqualTo("sikapprilaku_semester",semester)
                        .addSnapshotListener((value4, e4) -> {

                            if (e4 != null) {
                                Log.w(TAG, "Listen failed.", e4);
                                return;
                            }

                            int a = 0;
                            int b = 0;
                            int c = 0;
                            int d = 0;
                            int e = 0;

                            ArrayList<Akumulasi> data_ = new ArrayList<>();
                            for (QueryDocumentSnapshot document4 : value4) {
                                SikapPrilaku item = document4.toObject(SikapPrilaku.class);

                                int _id = 0;
                                for (Status itemBody : item.sikapprilaku_body) {

                                    if(itemBody.k.equalsIgnoreCase(docId)){
                                        long tanggal_miliseconds = 0;

                                        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-d");
                                        Date date1 = null;
                                        try{
                                            date1 = s.parse(item.sikapprilaku_tanggal);
                                        }catch (Exception e3){
                                            e3.printStackTrace();
                                        }
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.setTime(date1);

                                        tanggal_miliseconds = calendar.getTimeInMillis();

                                        data_.add(new Akumulasi(item.sikapprilaku_tanggal,tanggal_miliseconds,itemBody.v,item.sikapprilaku_ket));

                                        if (itemBody.v.equalsIgnoreCase("A")) {
                                            a++;

                                        } else if  (itemBody.v.equalsIgnoreCase("B")) {
                                            b++;

                                        } else if  (itemBody.v.equalsIgnoreCase("C")) {
                                            c++;

                                        } else if  (itemBody.v.equalsIgnoreCase("D")) {
                                            d++;

                                        } else if  (itemBody.v.equalsIgnoreCase("E")) {
                                            e++;

                                        }

                                        _id++;
                                    }
                                }

                            }

                            tv1_sikapprilaku.setText(a+" A");
                            tv2_sikapprilaku.setText(b+" B");
                            tv3_sikapprilaku.setText(c+" C");
                            tv4_sikapprilaku.setText(d+" D");
                            tv5_sikapprilaku.setText(e+" E");

                            if(data_.size() > 0){
                                empty_view_sikapprilaku.setVisibility(View.GONE);

                                Collections.sort(data_);

                                AdapterAkumulasi3 adapter_ = new AdapterAkumulasi3(context,data_);
                                recycle_view_sikapprilaku.setAdapter(adapter_);
                            }

                        });





                View viewarr[]={ly1,ly2,ly3};
                container.addView(viewarr[position]);
                return viewarr[position];
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        }

    }





    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView tv1, tv2, tv3;
        private ImageButton actEdit, actDel;
        private ImageButton actMore;
        private ImageView sex;
        private CircleImageView foto;
        public AdapterViewHolder(View itemView) {
            super(itemView);

            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);

            sex = itemView.findViewById(R.id.sex);
            foto = itemView.findViewById(R.id.foto);
            //actMore = itemView.findViewById(R.id.action_more);
            actEdit = itemView.findViewById(R.id.action_edit);
            actDel = itemView.findViewById(R.id.action_del);

        }
    }



    private void showPictureDialog(Context c){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(c);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });

        pictureDialog.show();//.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


    }



    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, REQUEST_PERMISSION_GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_PERMISSION_CAMERA);
    }

    private Bitmap scaleImageBitmap(Bitmap bitmap, int boundBoxInDp) {

        // Get current dimensions
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) boundBoxInDp) / width;
        float yScale = ((float) boundBoxInDp) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();

        return scaledBitmap;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;
        if (requestCode == REQUEST_PERMISSION_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri picUri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(picUri);
                Drawable drawing = Drawable.createFromStream(inputStream, picUri.toString() );
                bitmap = ((BitmapDrawable)drawing).getBitmap();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            bitmap = scaleImageBitmap(bitmap,650);

        } else if (requestCode == REQUEST_PERMISSION_CAMERA && resultCode == RESULT_OK && data != null) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            bitmap = scaleImageBitmap(imageBitmap,650);

        }

        if(bitmap != null) {

            BitmapDrawable result = new BitmapDrawable(bitmap);
            foto.setImageDrawable(result);
            foto.setScaleType(ImageView.ScaleType.CENTER_CROP);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] bytes = baos.toByteArray();


            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(getUserID+"/images/"+ UUID.randomUUID().toString() );
            ref.putBytes(bytes)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(PageDaftarSiswa.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "onSuccess: uri= " + uri.toString());
                                    editKeyFoto.setText(uri.toString());
                                }
                            });

                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(PageDaftarSiswa.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }

    /**

     Intent intent = new Intent();
     intent.setType("image/*");
     intent.setAction(Intent.ACTION_GET_CONTENT);
     startActivityForResult(
     Intent.createChooser(
     intent,
     "Select Image from here..."),
     PICK_IMAGE_REQUEST);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                foto.setImageBitmap(bitmap);

                if(filePath != null)
                {

                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    StorageReference ref = storageReference.child(getUserID);
                    ref.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(PageDaftarSiswa.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(TAG, "onSuccess: uri= "+ uri.toString());
                                            editKeyFoto.setText(uri.toString());
                                        }
                                    });

                                }
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(PageDaftarSiswa.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            })
                            .addOnProgressListener(taskSnapshot -> {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded "+(int)progress+"%");
                            });
                }

            }catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }*/


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