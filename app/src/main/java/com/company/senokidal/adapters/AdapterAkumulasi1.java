package com.company.senokidal.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.company.senokidal.R;
import com.company.senokidal.model.Akumulasi;
import info.androidhive.fontawesome.FontTextView;

public class AdapterAkumulasi1  extends RecyclerView.Adapter<AdapterAkumulasi1.AdapterViewHolder1> {
    Context context;
    ArrayList<Akumulasi> lists;

    public AdapterAkumulasi1(Context context, ArrayList<Akumulasi> data_) {
        this.context = context;
        this.lists = data_;
    }

    @NonNull
    @Override
    public AdapterViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_akumulasi,parent,false);

        return new AdapterViewHolder1(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder1 holder, int position) {
        Akumulasi akumulasi = lists.get(position);

        holder.tv1.setText(akumulasi.status);
        holder.tv2.setText(akumulasi.tanggal);


        if(!TextUtils.isEmpty(akumulasi.catatan)){
            holder.tv3.setVisibility(View.VISIBLE);
            holder.tv3.setText(akumulasi.catatan);
        }

        if (akumulasi.status.equalsIgnoreCase("Hadir")) {
            holder.iconAkumulasi.setText(R.string.fa_check_circle_solid);
            holder.iconAkumulasi.setTextColor(context.getResources().getColor(R.color.colorMasuk));

        } else if (akumulasi.status.equalsIgnoreCase("Alfa")) {
            holder.iconAkumulasi.setText(R.string.fa_times_circle_solid);
            holder.iconAkumulasi.setTextColor(context.getResources().getColor(R.color.colorAlfa));

        } else if (akumulasi.status.equalsIgnoreCase("Izin")) {
            holder.iconAkumulasi.setText(R.string.fa_envelope);
            holder.iconAkumulasi.setTextColor(context.getResources().getColor(R.color.colorIzin));

        } else if (akumulasi.status.equalsIgnoreCase("Sakit")) {
            holder.iconAkumulasi.setText(R.string.fa_plus_square_solid);
            holder.iconAkumulasi.setTextColor(context.getResources().getColor(R.color.colorSakit));

        } else if (akumulasi.status.equalsIgnoreCase("Terlambat")) {
            holder.iconAkumulasi.setText(R.string.fa_running_solid);
            holder.iconAkumulasi.setTextColor(context.getResources().getColor(R.color.colorTerlambat));

        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }


    public class AdapterViewHolder1 extends RecyclerView.ViewHolder {

        private FontTextView iconAkumulasi;
        private TextView textAkumulasi;
        private TextView tv1;
        private TextView tv2;
        private TextView tv3;
        public AdapterViewHolder1(View itemView) {
            super(itemView);

            iconAkumulasi = itemView.findViewById(R.id.iconAkumulasi);
            textAkumulasi = itemView.findViewById(R.id.textAkumulasi);
            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
            tv3 = itemView.findViewById(R.id.tv3);

        }
    }
}