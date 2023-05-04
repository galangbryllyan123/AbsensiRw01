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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import com.company.senokidal.R;
import com.company.senokidal.Splash;
import com.company.senokidal.model.Profile;


public class PageProfile extends AppCompatActivity {
    String TAG = "MainActivity";

    private String getUserID;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    SweetAlertDialog sweetAlertDialog;
    SweetAlertDialog sweetAlertDialog2;

    private static int REQUEST_PERMISSION_GALLERY = 11101;
    private static int REQUEST_PERMISSION_CAMERA = 11102;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    CircleImageView foto;
    AppCompatEditText editKeyFoto;
    static SharedPreferences sharedpreferences;

    TextInputEditText editEmail;
    TextInputEditText editFullName;
    TextInputEditText editAsalSekolah;
    AutoCompleteTextView editSemester;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_profile);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();


        db = FirebaseFirestore.getInstance();
        getUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);


        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        editKeyFoto = findViewById(R.id.editKeyFoto);
        foto = findViewById(R.id.foto);
        editEmail = findViewById(R.id.editEmail);
        editFullName = findViewById(R.id.editFullName);
        editAsalSekolah = findViewById(R.id.editAsalSekolah);
        editSemester = findViewById(R.id.editSemester);

        editEmail.setText(email);
        editEmail.setEnabled(false);
        editFullName.setText(name);



        ArrayList<String> items = new ArrayList<>();
        items.add("GANJIL");
        items.add("GENAP");
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        editSemester.setAdapter(itemsAdapter);

        findViewById(R.id.actSimpan).setOnClickListener(v->{
            String fullname = editFullName.getText().toString();
            final String keyFoto = editKeyFoto.getText().toString();
            final String keyAsalSekolah = editAsalSekolah.getText().toString();
            final String keySemester = editSemester.getText().toString().toLowerCase();
            if(TextUtils.isEmpty(fullname)){
                Toast.makeText(v.getContext(),"Nama belum diisi!",Toast.LENGTH_SHORT).show();
            }else{
                sweetAlertDialog.show();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(fullname).build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                                Toast.makeText(v.getContext(),"Data Profile telah tersimpan!",Toast.LENGTH_SHORT).show();
                                sweetAlertDialog.dismiss();

                            }
                        });



                db.collection("users")
                        .document(getUserID)
                        .set(new Profile(keyFoto,keyAsalSekolah,keySemester))
                        .addOnSuccessListener(documentReference -> {

                            //editKeyFoto.setText("");
                            Toast.makeText(PageProfile.this, "Data Setting telah tersimpan", Toast.LENGTH_SHORT).show();
                        });

            }
        });



        findViewById(R.id.actReset).setOnClickListener(v->{

            sweetAlertDialog2 = new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Anda yakin?")
                    .setContentText("Yakin mau hapus data? Semua data akan dihapus secara permanent");
            sweetAlertDialog2.setConfirmText("Yakin!");
            sweetAlertDialog2.setConfirmClickListener(sDialog -> {

                sDialog.dismissWithAnimation();
                //sweetAlertDialog.show();

                Log.d(TAG, "id: "+getUserID);



                db.collection("users/"+getUserID+"/Ketua")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            WriteBatch batch = db.batch();
                            List<DocumentSnapshot> snapshotList1 = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot s1:snapshotList1){
                                batch.delete(s1.getReference());
                            }
                            batch.commit()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess delete data: Ketua");

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailure: "+e);
                                        }
                                    });
                        });


                db.collection("users/"+getUserID+"/warga")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            WriteBatch batch = db.batch();
                            List<DocumentSnapshot> snapshotList1 = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot s1:snapshotList1){
                                batch.delete(s1.getReference());
                            }
                            batch.commit()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess delete data: warga");

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailure: "+e);
                                        }
                                    });
                        });


                db.collection("users/"+getUserID+"/Shift")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            WriteBatch batch = db.batch();
                            List<DocumentSnapshot> snapshotList1 = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot s1:snapshotList1){
                                batch.delete(s1.getReference());
                            }
                            batch.commit()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess delete data: shift");

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailure: "+e);
                                        }
                                    });
                        });


                db.collection("users/"+getUserID+"/kehadiran")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            WriteBatch batch = db.batch();
                            List<DocumentSnapshot> snapshotList1 = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot s1:snapshotList1){
                                batch.delete(s1.getReference());
                            }
                            batch.commit()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess delete data: kehadiran");

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailure: "+e);
                                        }
                                    });
                        });


                db.collection("users/"+getUserID+"/penilaian")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            WriteBatch batch = db.batch();
                            List<DocumentSnapshot> snapshotList1 = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot s1:snapshotList1){
                                batch.delete(s1.getReference());
                            }
                            batch.commit()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess delete data: penilaian");

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailure: "+e);
                                        }
                                    });
                        });


                db.collection("users/"+getUserID+"/sikapprilaku")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            WriteBatch batch = db.batch();
                            List<DocumentSnapshot> snapshotList1 = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot s1:snapshotList1){
                                batch.delete(s1.getReference());
                            }
                            batch.commit()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess delete data: sikapprilaku");

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailure: "+e);
                                        }
                                    });
                        });



                Toast.makeText(v.getContext(),"Semua data berhasil di hapus!",Toast.LENGTH_SHORT).show();

            });

            sweetAlertDialog2.setCancelText("Batal");
            sweetAlertDialog2.showCancelButton(true);
            sweetAlertDialog2.setCancelClickListener(sDialog -> sDialog.dismissWithAnimation());
            sweetAlertDialog2.setCancelable(false);
            sweetAlertDialog2.show();

        });


        findViewById(R.id.edit).setOnClickListener(view -> {

            showPictureDialog(view.getContext());
        });

    }


    private void setDataProfile(){

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
                                    Picasso.with(PageProfile.this)
                                            .load(p.foto)
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .into(foto);
                                    editKeyFoto.setText(p.foto);
                                }

                                if(!TextUtils.isEmpty(p.asal_sekolah)){
                                    //editAsalSekolah.setText(p.asal_sekolah);

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("WargaRW01",p.asal_sekolah);
                                    editor.apply();
                                    editAsalSekolah.setText(p.asal_sekolah);

                                }

                                if(!TextUtils.isEmpty(p.semester)){
                                    //editAsalSekolah.setText(p.asal_sekolah);

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("shift",p.semester);
                                    editor.apply();
                                    editSemester.setText(p.semester, false);

                                }
                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                    }
                });
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

            StorageReference ref = storageReference.child(getUserID+"/images/profile.jpeg" );
            ref.putBytes(bytes)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(PageProfile.this, "Uploaded", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(PageProfile.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        setDataProfile();
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