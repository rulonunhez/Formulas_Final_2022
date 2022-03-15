package com.example.formulas_final_2022;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Activity3 extends AppCompatActivity {

    private EditText etNombre, etFormula;
    private final String databaseName = "DB_Quimica";
    private SQLiteDatabase dbQuimica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        // Instanciación base de datos
        SQLiteOpenHelper auxiliar = new SQLiteOpenHelper(this, databaseName, null, 1);
        dbQuimica = auxiliar.getWritableDatabase();

        etNombre = findViewById(R.id.etNombre);
        etFormula = findViewById(R.id.etFormula);
    }

    public void onClickBtn(View view) {
        String nombre = etNombre.getText().toString();
        String formula = etFormula.getText().toString();
        if (nombre.equals("") || formula.equals("") || nombre.equals(null) || nombre.equals(null)){
            Toast.makeText(this, "¡Faltan datos por introducir!", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues nuevaTupla = new ContentValues();
            nuevaTupla.put("formula", formula);
            nuevaTupla.put("nombre", nombre);
            long l = dbQuimica.insert("compuestos", null, nuevaTupla);
            if (l != 0){
                Toast.makeText(this, "¡Compuesto añadido correctamenta!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se ha podido añadir el compuesto.", Toast.LENGTH_SHORT).show();
            }
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}