package com.company.senokidal;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    String email, password, fullname;

    private FirebaseFirestore db;

    SweetAlertDialog sweetAlertDialog;
    MaterialCheckBox tv_restore;
    int restore = 0;

    static SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        restore = sharedpreferences.getInt("restore", 0);

        db = FirebaseFirestore.getInstance();


        mAuth = FirebaseAuth.getInstance();


        requestStoragePermission();

        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);



        tv_restore = findViewById(R.id.restore);

        final TextInputEditText txtEmail = findViewById(R.id.editEmail);
        final TextInputEditText editPassword = findViewById(R.id.editPassword);
        final TextInputEditText editFullName = findViewById(R.id.editFullName);
        final TextInputLayout editFullNameLayout = findViewById(R.id.editFullNameLayout);

        final MaterialButton actSignIn = findViewById(R.id.actSignIn);
        final MaterialButton actSignUp = findViewById(R.id.actSignUp);
        final MaterialButton actSignInConfirm = findViewById(R.id.actSignInConfirm);
        final MaterialButton actSignUpConfirm = findViewById(R.id.actSignUpConfirm);


        if(restore == 1){
            tv_restore.setEnabled(true);
            tv_restore.setText("Data akan dimigrasikan!");
        }else if(restore == 2){
            tv_restore.setChecked(true);
            tv_restore.setEnabled(false);
            tv_restore.setText("Data telah dimigrasi ke versi terbaru");
        }else{
            tv_restore.setEnabled(true);
        }

        tv_restore.setOnClickListener(view -> {
            if (((MaterialCheckBox) view).isChecked()) {
                Toast.makeText(view.getContext(),
                        "Data akan di Restore", Toast.LENGTH_LONG).show();

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("restore",1);
                editor.apply();
            }
        });

        actSignIn.setOnClickListener(v -> {
            email = txtEmail.getText().toString();
            password = editPassword.getText().toString();

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(v.getContext(),"Email atau Sandi belum diisi!",Toast.LENGTH_LONG).show();
            }else{
                sweetAlertDialog.show();

                getSignIn();
            }
        });

        actSignUp.setOnClickListener(v -> {
            email = txtEmail.getText().toString();
            password = editPassword.getText().toString();
            fullname = editFullName.getText().toString();

            if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(fullname)){
                Toast.makeText(v.getContext(),"Email atau Sandi belum diisi!",Toast.LENGTH_LONG).show();
            }else{
                sweetAlertDialog.show();

                getSignUp();
            }
        });

        actSignUpConfirm.setOnClickListener(v -> {

            actSignIn.setVisibility(View.GONE);
            actSignUp.setVisibility(View.VISIBLE);
            actSignInConfirm.setVisibility(View.VISIBLE);
            actSignUpConfirm.setVisibility(View.GONE);
            editFullNameLayout.setVisibility(View.VISIBLE);

        });

        actSignInConfirm.setOnClickListener(v -> {

            actSignIn.setVisibility(View.VISIBLE);
            actSignUp.setVisibility(View.GONE);
            actSignInConfirm.setVisibility(View.GONE);
            actSignUpConfirm.setVisibility(View.VISIBLE);
            editFullNameLayout.setVisibility(View.GONE);

        });

    }

    private void getSignIn(){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        sweetAlertDialog.dismiss();
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Gagal masuk, periksa kembali akun.",
                                Toast.LENGTH_LONG).show();

                        sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.setTitleText("Oops");
                        sweetAlertDialog.setContentText("Gagal masuk, periksa kembali akun.");
                        sweetAlertDialog.showCancelButton(true);
                        sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.cancel());
                        sweetAlertDialog.show();

                        updateUI(null);
                    }

                    // ...
                });

    }

    private void getSignUp(){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullname)
                                .build();

                        user.updateProfile(profileUpdates);


                        Toast.makeText(LoginActivity.this, "Sign up success, now you can login.",
                                Toast.LENGTH_SHORT).show();

                        sweetAlertDialog.dismiss();

                        updateUI(null);

                    } else {
                        sweetAlertDialog.dismiss();
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                        sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.setTitleText("Oops");
                        sweetAlertDialog.setContentText("Gagal daftar, periksa kembali field dan coba lagi.");
                        sweetAlertDialog.showCancelButton(true);
                        sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.cancel());
                        sweetAlertDialog.show();

                        updateUI(null);
                    }

                    // ...
                });

    }

    @Override
    protected void onPause() {
        super.onPause();

        sweetAlertDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sweetAlertDialog.dismiss();
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();

            if (!TextUtils.isEmpty(uid)) {

                Intent intent = null;
                intent = new Intent(LoginActivity.this, SkipActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        }
    }



    private void requestStoragePermission() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                Log.i("izin", "Semua izin telah disetujui!");
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // permission denied, block the feature.
                showSettingsDialog();
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Membutuhkan Izin");
        builder.setMessage("Beberapa fitur diperlukan untuk aplikasi ini. Kamu Setujui di pengaturan.");
        builder.setPositiveButton("KE PENGATURAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    @Override
    public void onBackPressed() {

        sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin?")
                .setContentText("Ingin keluar dari aplikasi!");
        sweetAlertDialog.setConfirmText("Ok!");
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

    }
}