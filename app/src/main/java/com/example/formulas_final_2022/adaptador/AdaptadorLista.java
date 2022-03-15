package com.example.formulas_final_2022.adaptador;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.formulas_final_2022.R;

import java.util.ArrayList;

public class AdaptadorLista extends ArrayAdapter {

    private Activity context;
    private ArrayList<String> arrayCompuestos;
    private TextView tvCompuesto;

    public AdaptadorLista(@NonNull Activity context, int layoutPersonalizado, ArrayList<String> arrayCompuestos) {
        super(context, layoutPersonalizado, arrayCompuestos);
        this.context = context;
        this.arrayCompuestos = arrayCompuestos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Código para la optimización del adaptador
        View fila = convertView;
        LayoutInflater layoutInflater = context.getLayoutInflater();
        fila = layoutInflater.inflate(R.layout.fila_compuestos, null);
        tvCompuesto = fila.findViewById(R.id.tvCompuesto);
        tvCompuesto.setText(arrayCompuestos.get(position));
        return fila;
    }


}
