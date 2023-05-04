package com.company.senokidal.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.company.senokidal.R;
import com.company.senokidal.model.Recent;
import com.company.senokidal.utils.DatabaseHelper;
import com.company.senokidal.utils.Fungsi;

public class PageRekapDataHistory extends AppCompatActivity {
    Context context;
    public static DatabaseHelper dbase;
    static LinearLayout empty_view;
    public static RecyclerView recyclerView;
    public RecyclerViewAdapter adapter;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_rekapdatahistory);

        context = PageRekapDataHistory.this;
        dbase = new DatabaseHelper(context);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        empty_view = findViewById(R.id.empty_view);
        recyclerView = findViewById(R.id.recycle_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        List<Recent> recents = dbase.RekapRecentAll();

        empty_view.setVisibility(View.VISIBLE);
        if(recents.size() > 0){
            empty_view.setVisibility(View.GONE);
        }

        adapter = new RecyclerViewAdapter(recents);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder> {
        private List<Recent> recentList;

        public RecyclerViewAdapter(List<Recent> recentList) {
            this.recentList = recentList;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recent, parent, false);

            return new ItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, int position) {
            final Recent recent = recentList.get(position);

            String d = DateFormat.format("EE, dd MMMM yyyy hh:mm:ss", Long.parseLong(recent.recent_tanggal)).toString();
            holder.tv1.setText(d);
            holder.tv2.setText(recent.recent_tipe);
            holder.tv3.setText(recent.recent_opsi1);
            holder.tv4.setText(recent.recent_opsi2);

            if(!TextUtils.isEmpty(recent.recent_opsi1)) holder.tv3.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(recent.recent_opsi2)) holder.tv4.setVisibility(View.VISIBLE);

            holder.itemView.setOnClickListener(v->{

                File file = new File(recent.recent_files);

                if(!file.exists()){
                    Toast.makeText(v.getContext(),"Maaf file "+file.getPath()+" tidak tersedia",Toast.LENGTH_LONG).show();
                }else{


                    sweetAlertDialog = new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Buka file?")
                            .setContentText("Anda akan diarahkan ke pembuka dokument excel?");
                    sweetAlertDialog.setConfirmText("Yakin!");
                    sweetAlertDialog.setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();

                        new Fungsi().openFile(context,file);
                    });
                    sweetAlertDialog.setCancelText("Batal");
                    sweetAlertDialog.showCancelButton(true);
                    sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismissWithAnimation());
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.show();

                }
            });

            holder.action_share.setOnClickListener(v->{

                File file = new File(recent.recent_files);

                if(!file.exists()){
                    Toast.makeText(v.getContext(),"Maaf file "+file.getPath()+" tidak tersedia",Toast.LENGTH_LONG).show();
                }else {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("application/vnd.ms-excel");
                    share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(v.getContext(), v.getContext().getPackageName() + ".provider", file));
                    //share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cachePath));
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(share, "Bagikan ke:"));
                }

            });

            holder.action_del.setOnClickListener(v->{

                sweetAlertDialog = new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Anda yakin?")
                        .setContentText("Yakin mau hapus data?");
                sweetAlertDialog.setConfirmText("Ok!");
                sweetAlertDialog.setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                    File file = new File(recent.recent_files);

                    if(!file.exists()){
                        Toast.makeText(v.getContext(),"Maaf file "+file.getPath()+" tidak tersedia",Toast.LENGTH_LONG).show();
                    }else{
                        if(!file.delete()){
                            Toast.makeText(v.getContext(),"File tidak dapat dihapus",Toast.LENGTH_LONG).show();

                        }else{

                            dbase.RecentDel(recent.recent_id);


                            List<Recent> recents = dbase.RekapRecentAll();
                            empty_view.setVisibility(View.VISIBLE);
                            if(recents.size() > 0){
                                empty_view.setVisibility(View.GONE);
                            }

                            adapter = new RecyclerViewAdapter(recents);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            Toast.makeText(v.getContext(),"File telah terhapus!",Toast.LENGTH_LONG).show();
                        }
                    }


                });
                sweetAlertDialog.setCancelText("Batal");
                sweetAlertDialog.showCancelButton(true);
                sweetAlertDialog.setCancelClickListener(sDialog -> sDialog.dismissWithAnimation());
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
            });
        }


        @Override
        public int getItemCount() {
            return recentList.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView tv1,tv2,tv3,tv4;
            ImageButton action_share, action_del;
            public ItemViewHolder(View itemView) {
                super(itemView);

                tv1 = itemView.findViewById(R.id.tv1);
                tv2 = itemView.findViewById(R.id.tv2);
                tv3 = itemView.findViewById(R.id.tv3);
                tv4 = itemView.findViewById(R.id.tv4);

                action_share = itemView.findViewById(R.id.action_share);
                action_del = itemView.findViewById(R.id.action_del);

            }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
