package com.company.senokidal.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import com.company.senokidal.R;
import com.company.senokidal.model.RuangBelajar;

public class AdapterRuangBelajarDialog extends ArrayAdapter<RuangBelajar> {
    private final Context context;
    private final ArrayList<RuangBelajar> lists;

    public AdapterRuangBelajarDialog(@NonNull Context context, int resource, @NonNull ArrayList<RuangBelajar> objects) {
        super(context, resource, objects);
        lists = objects;
        this.context = context;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_ruangbelajar_dialog, parent, false);

        RuangBelajar item = lists.get(position);

        TextView tv1 = rowView.findViewById(R.id.tv1);
        TextView tv2 = rowView.findViewById(R.id.tv2);
        tv1.setText(item.kelas_desc);
        tv2.setText(item.kelas_nama);

        return rowView;
    }
}