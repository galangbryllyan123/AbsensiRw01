package com.company.senokidal;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import com.company.senokidal.adapters.AdapterRuangBelajarDialog;
import com.company.senokidal.model.Kehadiran;
import com.company.senokidal.model.Profile;
import com.company.senokidal.model.RuangBelajar;
import com.company.senokidal.model.Status;
import com.company.senokidal.model.Versi;
import com.company.senokidal.pages.PageImportData;
import com.company.senokidal.pages.PageLog;
import com.company.senokidal.pages.PageProfile;
import com.company.senokidal.pages.PageRekapData;
import com.company.senokidal.pages.PageUpdate;
import com.company.senokidal.pages.kehadiran.PageCatatanKehadiran;
import com.company.senokidal.pages.penilaian.PageCatatanPenilaian;
import com.company.senokidal.pages.sikapprilaku.PageCatatanSikapPrilaku;
import com.company.senokidal.pages.PageDaftarSiswa;
import com.company.senokidal.pages.PageMataPelajaran;
import com.company.senokidal.pages.PageRuangBelajar;
import com.company.senokidal.utils.DatabaseHelper;
import com.company.senokidal.utils.Iklan;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";

    private InterstitialAd mInterstitialAd;

    ArrayList<RuangBelajar> items;
    RecyclerView recyclerView;


    SweetAlertDialog sweetAlertDialog;

    public static DatabaseHelper dbase;
    static SharedPreferences sharedpreferences;
    String semester;

    private String getUserID;



    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        if (firebaseAuth.getCurrentUser() == null){
            //Do anything here which needs to be done after signout is complete
            signOutComplete();
        }
    };

    private FirebaseFirestore db;

    Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String dateToday, dateTime;

    Context context;
    File folder;
    CircleImageView btn_action_profile;
    TextView tv_sekolah, tv_semester;
    ImageButton btn_action_update;
    TextView btn_action_update_text;

    int verCode = 0;

    private static final String SHOWCASE_ID = "sequence-1";
    static MaterialShowcaseSequence sequence;

    /**
    private void loadRewardedVideoAd() {
        //prepareAd();

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }


    private void prepareAd() {
        //Menginisialisasi Rewarded Video Ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.inter_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        sequence = new MaterialShowcaseSequence(MainActivity.this, SHOWCASE_ID);

        sequence.setConfig(config);

        final String path = Environment.getExternalStorageDirectory().getPath() + File.separator + getString(R.string.app_name);

        folder = new File(path);
        if(!folder.exists())folder.mkdir();

        // Initialize the Mobile Ads SDK.
        //MobileAds.initialize(this, getString(R.string.admob_app_id));

        //loadRewardedVideoAd();

        dbase = new DatabaseHelper(context);

        db = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);

        FirebaseUser user = firebaseAuth.getCurrentUser();


        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        semester = sharedpreferences.getString("semester","ganjil");


        dateToday = sdf.format(new Date());

        getUserID = user.getUid();
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if (getUserID == null) {

            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (TextUtils.isEmpty(name)) {

            Intent intent = new Intent(context, PageProfile.class);
            startActivity(intent);
        }



        btn_action_profile = findViewById(R.id.btn_action_profile);
        btn_action_profile.setOnClickListener(view -> {
            Intent intent = new Intent(context, PageProfile.class);
            startActivity(intent);
        });

        /**

         Picasso.with(context)
         .load(photoUrl)
         .networkPolicy(NetworkPolicy.NO_CACHE)
         .into(btn_action_profile);
         
         */

        btn_action_update = findViewById(R.id.btn_action_update);
        btn_action_update_text = findViewById(R.id.btn_action_update_text);
        btn_action_update_text.setVisibility(View.GONE);


        TextView tv1hadir = findViewById(R.id.tv1);
        TextView tv2izin = findViewById(R.id.tv2);
        TextView tv3terlambat = findViewById(R.id.tv3);
        TextView tv4sakit = findViewById(R.id.tv4);
        TextView tv5alfa = findViewById(R.id.tv5);
        TextView tv6ruang = findViewById(R.id.tv6);
        TextView tv7diampu = findViewById(R.id.tv7);
        TextView tv8pelajaran = findViewById(R.id.tv8);

        tv1hadir.setText("0 Warga"); //hadir
        tv2izin.setText("0 Warga"); //izin
        tv3terlambat.setText("0 Warga"); //terlambat
        tv4sakit.setText("0 Warga"); //sakit
        tv5alfa.setText("0 Warga"); //alfa
        tv6ruang.setText("0 Ketua"); //ruang
        tv7diampu.setText("0 Warga"); //diampu
        tv8pelajaran.setText("0 Shift"); //pelajaran



        TextView tv_name = findViewById(R.id.nav_header_textView);
        TextView tv_email = findViewById(R.id.nav_header_textView2);

        tv_name.setText(name);
        tv_email.setText(email);

        findViewById(R.id.actionProfile).setOnClickListener(v->{

            Intent intent = new Intent(context, PageProfile.class);
            startActivity(intent);
        });

        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();


        /**
         * Dialog
         */

        final Dialog dialog1 = new Dialog(context);
        dialog1.setContentView(R.layout.dialog_ruangkelas);

        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // this is optional
        }

        ListView listView = dialog1.findViewById(R.id.lv_assignment_users);


        /**
         * BottomSheet
         */

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);

        View bottomSheetLayout = layoutInflaterAndroid.inflate(R.layout.bottomsheet_home, null);

        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setContentView(bottomSheetLayout);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) bottomSheetLayout.getParent());

        mBottomSheetDialog.setOnShowListener(dialogInterface -> {
            mBehavior.setPeekHeight(bottomSheetLayout.getHeight());//get the height dynamically
        });

        (bottomSheetLayout.findViewById(R.id.fab_close)).setOnClickListener(view -> mBottomSheetDialog.dismiss());



        LinearLayout linearLayout = bottomSheetLayout.findViewById(R.id.addView);


        findViewById(R.id.btn_action_logout).setOnClickListener(v -> {
            firebaseAuth.signOut();
        });



        db.collection("users/"+getUserID+"/ruangbelajar")
                .addSnapshotListener((value1, e1) -> {
                    if (e1 != null) {
                        Log.w(TAG, "Listen failed.", e1);
                        return;
                    }

                    items = new ArrayList<>();



                    int jumlah1 = 0;
                    for (QueryDocumentSnapshot document1 : value1) {
                        RuangBelajar item = document1.toObject(RuangBelajar.class);
                        items.add(item);
                        jumlah1++;
                    }

                    Comparator<RuangBelajar> compareById1 = (o1, o2) -> o1.kelas_desc.compareTo(o2.kelas_desc);
                    Collections.sort(items, compareById1);

                    tv6ruang.setText(jumlah1 +" Ketua");


                    db.collection("users/"+getUserID+"/siswa")
                            .addSnapshotListener((value2, e2) -> {
                                if (e2 != null) {
                                    Log.w(TAG, "Listen failed.", e2);
                                    return;
                                }

                                int jumlah2 = 0;
                                for (QueryDocumentSnapshot document2 : value2) {
                                    jumlah2++;
                                }
                                tv7diampu.setText(jumlah2 +" Warga");

                                db.collection("users/"+getUserID+"/Ketua")
                                        .addSnapshotListener((value3, e3) -> {
                                            if (e3 != null) {
                                                Log.w(TAG, "Listen failed.", e3);
                                                return;
                                            }

                                            int jumlah3 = 0;
                                            for (QueryDocumentSnapshot document3 : value3) {
                                                jumlah3++;
                                            }
                                            tv8pelajaran.setText(jumlah3 +" Shift");

                                            db.collection("users/"+getUserID+"/kehadiran")
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
                                                        for (QueryDocumentSnapshot document4 : value4) {
                                                            Kehadiran item = document4.toObject(Kehadiran.class);


                                                            if(semester == "semua" || (item.kehadiran_semester != null && item.kehadiran_semester.equals(semester))) {
                                                                int _id = 0;

                                                                for (Status itemBody : item.kehadiran_body) {

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


                                                        tv1hadir.setText(hadir+" Warga");
                                                        tv5alfa.setText(alfa+" Warga");
                                                        tv2izin.setText(izin+" Warga");
                                                        tv4sakit.setText(sakit+" Warga");
                                                        tv3terlambat.setText(terlambat+" Warga");

                                                        sweetAlertDialog.dismiss();
                                                    });
                                        });

                            });
                });





        FloatingActionButton fab_up = findViewById(R.id.fab_up);
        fab_up.setOnClickListener(v->{


            linearLayout.addView(new Iklan(context).getAd1View(AdSize.BANNER, R.string.banner_ad_unit_id));

            mBottomSheetDialog.show();


            /**
             * Daftar Menu Bottom Sheet
             */

            (bottomSheetLayout.findViewById(R.id.action_nav1)).setOnClickListener(view -> {
                startActivity(new Intent(context, PageRuangBelajar.class) );
                mBottomSheetDialog.dismiss();
            });

            (bottomSheetLayout.findViewById(R.id.action_nav2)).setOnClickListener(view -> {
                startActivity(new Intent(context, PageMataPelajaran.class) );
                mBottomSheetDialog.dismiss();
            });

            (bottomSheetLayout.findViewById(R.id.action_nav3)).setOnClickListener(view -> {


                if(items.size() > 0){


                    ArrayAdapter arrayAdapter = new AdapterRuangBelajarDialog(context,R.layout.item_ruangbelajar_dialog, items);
                    listView.setAdapter(arrayAdapter);
                    listView.setOnItemClickListener((adapterView, view1, which, l) -> {


                        if(TextUtils.isEmpty(items.get(which).kelas_desc)){
                            Toast.makeText(view.getContext(),"Ketua tidak ditemukan!",Toast.LENGTH_SHORT).show();
                        }else{

                            Intent intent = new Intent(context, PageDaftarSiswa.class);
                            intent.putExtra("ketua", items.get(which).kelas_desc);
                            startActivity(intent);

                            dialog1.dismiss();
                            mBottomSheetDialog.dismiss();

                        }
                    });

                    dialog1.show();
                }else{
                    Toast.makeText(view.getContext(),"Harap tambahkan ketua dahulu!",Toast.LENGTH_SHORT).show();
                }

            });

            (bottomSheetLayout.findViewById(R.id.action_nav4)).setOnClickListener(view -> {
                if(items.size() > 0) {
                    startActivity(new Intent(context, PageCatatanKehadiran.class));
                    mBottomSheetDialog.dismiss();
                }else{
                    Toast.makeText(view.getContext(),"Harap tambahkan ketua dahulu!",Toast.LENGTH_SHORT).show();
                }
            });

            (bottomSheetLayout.findViewById(R.id.action_nav5)).setOnClickListener(view -> {
                if(items.size() > 0) {
                startActivity(new Intent(context, PageCatatanPenilaian.class) );
                mBottomSheetDialog.dismiss();
                }else{
                    Toast.makeText(view.getContext(),"Harap tambahkan ketua dahulu!",Toast.LENGTH_SHORT).show();
                }
            });

            (bottomSheetLayout.findViewById(R.id.action_nav6)).setOnClickListener(view -> {
                if(items.size() > 0) {
                    startActivity(new Intent(context, PageCatatanSikapPrilaku.class) );
                    mBottomSheetDialog.dismiss();
                }else{
                    Toast.makeText(view.getContext(),"Harap tambahkan ketua dahulu!",Toast.LENGTH_SHORT).show();
                }
            });

            (bottomSheetLayout.findViewById(R.id.action_nav7)).setOnClickListener(view -> {
                if(items.size() > 0) {

                    //Toast.makeText(context,"Maaf fitur belum tersedia",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, PageRekapData.class) );
                    mBottomSheetDialog.dismiss();

                }else{
                    Toast.makeText(view.getContext(),"Maaf beberapa data yang akan di export tidak tersedia!",Toast.LENGTH_SHORT).show();
                }

            });

            (bottomSheetLayout.findViewById(R.id.action_nav8)).setOnClickListener(view -> {
                //Toast.makeText(context,"Maaf fitur belum tersedia",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, PageImportData.class) );
                mBottomSheetDialog.dismiss();

            });
        });




        String asalsekolah = dbase.getAsalSekolah();
        String semester = dbase.getSemester();

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;//Version Name
        verCode = pInfo.versionCode;//Version Code
        String versionName = version;




        tv_sekolah = findViewById(R.id.tv_pantau_sekolah);
        tv_semester = findViewById(R.id.tv_pantau_count_semester);
        tv_sekolah.setText(asalsekolah);
        tv_semester.setText(semester);

        /**
        findViewById(R.id.actSekolah).setOnClickListener(v->{

            LinearLayout l = new LinearLayout(v.getContext());
            l.setOrientation(LinearLayout.VERTICAL);
            l.setPadding(32,24,32,24);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT
            );

            final EditText edittext1 = new EditText(v.getContext());

            edittext1.setLayoutParams(params);

            edittext1.setText( dbase.getAsalSekolah() );
            l.addView(edittext1);

            AlertDialog.Builder builder2 = new AlertDialog.Builder(v.getContext())
                    .setTitle("Nama Sekolah")
                    .setView(l)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            String as = edittext1.getText().toString();
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("asalsekolah",as);
                            editor.apply();
                            tv_sekolah.setText(as);

                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog alertdialog2 = builder2.create();
            //alertdialog2.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            alertdialog2.show();
        });



        findViewById(R.id.actSemester).setOnClickListener(view -> {
            final CharSequence[] items = {"Ganjil", "Genap"};
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext())
                    .setTitle("Pilih Semester")
                    .setItems(items, (dialog, which) -> {
                        String pilihan = "ganjil";
                        switch (which){
                            case 0: pilihan = "ganjil"; break;
                            case 1: pilihan = "genap"; break;
                        }

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("semester",pilihan);
                        editor.apply();

                        tv_semester.setText(pilihan);

                    });

            AlertDialog alertdialog = builder.create();
            alertdialog.show();
        });*/

        TextView info = findViewById(R.id.tv_pantau_count_info);
        info.setText(versionName);

        CardView actVersion = findViewById(R.id.actVersion);
        actVersion.setOnClickListener(v->{
            startActivity(new Intent(context, PageLog.class) );

        });



        btn_action_update.setOnClickListener(v->{
            startActivity(new Intent(context, PageUpdate.class) );

        });

        //sequence.addSequenceItem(fab_up, "Fitur Detail Kehadiran, Penilaian, Sikap dan Prilaku telah tersedia. Silahkan klik pada bagian siswa akan muncul akumulasi disana, Trimakasih", "Ok Mengeri");
        //sequence.start();


        Log.e("on","Create");
    }

    public void setDataProfile(){

        db.collection("users")
                .document(getUserID)
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {

                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                        try{

                            Profile p = documentSnapshot.toObject(Profile.class);
                            if( p != null ){

                                p.setKey( documentSnapshot.getId() );

                                if (!TextUtils.isEmpty(p.foto)) {
                                    Picasso.with(MainActivity.this)
                                            .load(p.foto)
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .into(btn_action_profile);
                                }

                                if(!TextUtils.isEmpty(p.asal_sekolah)){
                                    //editAsalSekolah.setText(p.asal_sekolah);

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("asalWargaRW01",p.asal_sekolah);
                                    editor.apply();
                                    tv_sekolah.setText(p.asal_sekolah);

                                }

                                if(!TextUtils.isEmpty(p.semester)){
                                    //editAsalSekolah.setText(p.asal_sekolah);

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("shift",p.semester);
                                    editor.apply();
                                    tv_semester.setText(p.semester);

                                }
                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                    }
                });


    }

    public void seeViewUpdate(){
        db.collection("log")
                .orderBy("versi_code", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean up = false;
                        String ver = "0";
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Versi versi = document.toObject(Versi.class);

                            if(versi.versi_code > verCode ){
                                up = true;
                                ver = versi.versi_name;
                            }

                        }

                        if( up ){
                            btn_action_update_text.setVisibility(View.VISIBLE);
                            btn_action_update_text.setText(ver);

                            Log.e("on","Update");
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });


    }

    /**
     *

     private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;

    private void popupSnackbarForCompleteUpdate() {
        final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "New app is ready!", Snackbar.LENGTH_INDEFINITE);

        snackBar.setAction("Install", view -> {
            if (mAppUpdateManager != null){
                mAppUpdateManager.completeUpdate();
            }
        });

        snackBar.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                Log.e(TAG, "onActivityResult: app download failed");
            }
        }
    }

    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED){
                        //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                        popupSnackbarForCompleteUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED){
                        if (mAppUpdateManager != null){
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }

                    } else {
                        Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());
                    }
                }
            };

    public void seeNewUpdate(){


        mAppUpdateManager = AppUpdateManagerFactory.create(this);

        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){

                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.FLEXIBLE, MainActivity.this, RC_APP_UPDATE);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED){
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                popupSnackbarForCompleteUpdate();
            } else {
                Log.e(TAG, "checkForAppUpdateAvailability: something else");
            }
        });
    }

    */
    @Override
    protected void onStart() {
        super.onStart();

        setDataProfile();
        seeViewUpdate();

        Log.e("on","Start");
        /**
        try {
            seeNewUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        /**if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }*/
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Ketuk sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    /**
    @Override
    public void onBackPressed() {

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin?")
                .setContentText("Sesi akan kami akhiri segera!");
        sweetAlertDialog.setConfirmText("Keluar!");
        sweetAlertDialog.setConfirmClickListener(sDialog -> {
            sDialog.dismissWithAnimation();

            finish();
            System.exit(0);
        });
        sweetAlertDialog.setCancelText("Batal");
        sweetAlertDialog.showCancelButton(true);
        sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismissWithAnimation());
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

    }*/


    private void signOutComplete(){

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin?")
                .setContentText("Sesi akan kami akhiri segera!");
        sweetAlertDialog.setConfirmText("Keluar!");
        sweetAlertDialog.setConfirmClickListener(sDialog -> {
            sDialog.dismissWithAnimation();

            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        sweetAlertDialog.setCancelText("Batal");
        sweetAlertDialog.showCancelButton(true);
        sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismissWithAnimation());
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

    }

}