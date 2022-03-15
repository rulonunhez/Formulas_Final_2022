package com.example.formulas_final_2022;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.formulas_final_2022.adaptador.AdaptadorLista;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String databaseName = "DB_Quimica";
    private final int LLAMADA_TELEFONO = 1;
    private final int CODIGO_LLAMADA_2 = 2;
    private final int CODIGO_LLAMADA_3 = 3;
    private String nombreCompuesto;
    private SQLiteDatabase dbQuimica;
    private ImageView ivGrande;
    private TextView tvTitulo, tvElige;
    private ListView lvCompuestos;
    private AlertDialog.Builder ventana;
    private int errores = 0;
    private int aciertos = 0;
    private final int RESULT_CONTINUAR = 2;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Crear un objeto SharedPreferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int aciertosUltimaEjecucion = preferences.getInt("aciertos", -1);
        if (aciertosUltimaEjecucion != -1){
            Toast.makeText(this, "Número de aciertos en la última ejecución: " + aciertosUltimaEjecucion, Toast.LENGTH_SHORT).show();
        }

        editor = preferences.edit();
        editor.putInt("aciertos", aciertos);
        editor.apply();

        // Instanciación base de datos
        SQLiteOpenHelper auxiliar = new SQLiteOpenHelper(this, databaseName, null, 1);
        dbQuimica = auxiliar.getWritableDatabase();

        // Inicialización datos y configuración primera pantalla
        inicializacionView();
        tvTitulo.setText(tvTitulo.getText().toString().toUpperCase());
        lvCompuestos.setVisibility(View.GONE);
        tvElige.setVisibility(View.GONE);

        // Consulta compuestos
        Cursor cursor = dbQuimica.rawQuery("SELECT nombre FROM compuestos", null);
        ArrayList<String> compuestos = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                compuestos.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // Adaptador para la lista
        AdaptadorLista adaptadorLista = new AdaptadorLista(MainActivity.this, R.layout.fila_compuestos, compuestos);
        lvCompuestos.setAdapter(adaptadorLista);

        ivGrande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                lvCompuestos.setVisibility(View.VISIBLE);
                tvElige.setVisibility(View.VISIBLE);
                ivGrande.setVisibility(View.GONE);
                tvTitulo.setVisibility(View.GONE);
            }
        });

        lvCompuestos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                nombreCompuesto = adapterView.getItemAtPosition(i).toString();
                Intent intent = new Intent(MainActivity.this, Activity2.class);
                Bundle b = new Bundle();
                b.putString("nombreCompuesto", nombreCompuesto);
                b.putInt("errores", errores);
                b.putInt("aciertos", aciertos);
                intent.putExtras(b);
                startActivityForResult(intent, CODIGO_LLAMADA_2);
            }
        });
    }

    private void inicializacionView() {
        ivGrande = findViewById(R.id.ivGrande);
        tvTitulo = findViewById(R.id.tvTitulo);
        lvCompuestos = findViewById(R.id.lvCompuestos);
        tvElige = findViewById(R.id.tvElige);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobar si la ejecución se realizó correctamente
        if (requestCode == CODIGO_LLAMADA_2){
            if (resultCode == RESULT_OK) {
                aciertos = data.getIntExtra("aciertos", 0);
                editor.putInt("aciertos", aciertos);
                editor.apply();
                finish();
            } else if (resultCode == RESULT_CONTINUAR){
                errores = data.getIntExtra("errores", 0);
                aciertos = data.getIntExtra("aciertos", 0);
                editor.putInt("aciertos", aciertos);
                editor.apply();
            }
        } else if (requestCode == CODIGO_LLAMADA_3){
            if (resultCode == RESULT_OK) {
                Cursor cursor = dbQuimica.rawQuery("SELECT nombre FROM compuestos", null);
                ArrayList<String> compuestos = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        compuestos.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }
                // Adaptador para la lista
                AdaptadorLista adaptadorLista = new AdaptadorLista(MainActivity.this, R.layout.fila_compuestos, compuestos);
                lvCompuestos.setAdapter(adaptadorLista);
            }
        }
    }

    // Creación de menú de opciones
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_opciones, menu);
        return true;
    }

    // Listener del menú de opciones
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            // Insertar
            case R.id.insertar:
                intent = new Intent(MainActivity.this, Activity3.class);
                startActivityForResult(intent, CODIGO_LLAMADA_3);
                return true;
            case R.id.contacto:
                dialogo_2boton();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LLAMADA_TELEFONO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:(+34)999112233"));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Permiso denegado por el usuario", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dialogo_2boton() {
        ventana = new AlertDialog.Builder(this);
        ventana.setTitle("Contacto");
        ventana.setMessage("IES de Teis" +
                "\n¿Desea contactar con nosotros?");
        ventana.setIcon(R.drawable.flask1);
        ventana.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(Build.VERSION.SDK_INT >= 23){
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                        // existe el permiso
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:(+34)999112233"));
                        startActivity(intent);
                    }
                    else {
                        // no hay permiso (solicitamos el dialogo de petición de permiso)
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE},LLAMADA_TELEFONO);
                    }
                } else {
                    // operaciones para version < 23
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:(+34)999112233"));
                    startActivity(intent);
                }
            }
        });
        ventana.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        ventana.setCancelable(false);
        ventana.show();
    }
}