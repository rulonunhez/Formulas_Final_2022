package com.example.formulas_final_2022;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Activity2 extends AppCompatActivity {

    private EditText etCompuesto;
    private RadioGroup rgContinuar;
    private Button btnContinuar, btnComprobar;
    private TextView tvContinuar, tvComprobar;
    private final String databaseName = "DB_Quimica";
    private SQLiteDatabase dbQuimica;
    private RadioButton rbSi, rbNo;
    private int errores, aciertos;
    private final int NOTIFICACION_ID = 1;
    private final int RESULT_CONTINUAR = 2;
    private static final String ID_CANAL = "Identificador canal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        inicializacionView();
        Intent intent = getIntent();
        String nombreCompuesto = intent.getStringExtra("nombreCompuesto");
        errores = intent.getIntExtra("errores", 0);
        aciertos = intent.getIntExtra("aciertos", 0);
        etCompuesto.setHint(nombreCompuesto);
        btnContinuar.setVisibility(View.GONE);
        tvContinuar.setVisibility(View.GONE);
        rgContinuar.setVisibility(View.GONE);

        // Instanciación base de datos
        SQLiteOpenHelper auxiliar = new SQLiteOpenHelper(this, databaseName, null, 1);
        dbQuimica = auxiliar.getWritableDatabase();

        // Crear el canal para la notificación en API 26
        createNotificationChannel();
    }

    public void onClickBtn(View view) {
        if (view.getId() == R.id.btnComprobar){
            String datoIntroducido = etCompuesto.getText().toString();
            String[] columns = {"nombre"};
            if (datoIntroducido.equals("") || datoIntroducido.equals(null)){
                Toast.makeText(this, "¡Introduzca la fórmula primero!", Toast.LENGTH_SHORT).show();
            } else {
                btnContinuar.setVisibility(View.VISIBLE);
                tvContinuar.setVisibility(View.VISIBLE);
                rgContinuar.setVisibility(View.VISIBLE);
                btnComprobar.setVisibility(View.GONE);
                tvComprobar.setVisibility(View.GONE);
                etCompuesto.setVisibility(View.GONE);
                Cursor cursor = dbQuimica.query("compuestos", columns, "formula = '" +  datoIntroducido + "'", null, null, null, null);
                if (cursor.moveToFirst()){
                    Toast.makeText(this, datoIntroducido + ": " + getResources().getString(R.string.acierto), Toast.LENGTH_SHORT).show();
                    aciertos++;
                } else {
                    Toast.makeText(this, datoIntroducido + ": " + getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    errores++;
                    if (errores == 3){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notificacionAPI26();
                        } else {
                            notificacion();
                        }
                    }
                }
            }
        } else {
            if (rbSi.isChecked()){
                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putInt("errores", errores);
                b.putInt("aciertos", aciertos);
                intent.putExtras(b);
                setResult(RESULT_CONTINUAR, intent);
                finish();
            } else {
                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putInt("aciertos", aciertos);
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaciones básicas";
            String description = "Canal para notificaciones sencillas";
            NotificationChannel canal = new NotificationChannel(ID_CANAL, name, NotificationManager.IMPORTANCE_DEFAULT);
            if (canal == null){
                canal.enableVibration(false);
            }
            canal.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(canal);
        }
    }

    private void notificacionAPI26() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ID_CANAL);

        // Generar el contenido
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        builder.setTicker("Aviso");
        builder.setContentTitle("Tiene más de dos errores");
        builder.setContentText("Consulte la web para aprender más sobre química.");

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.flask1);
        builder.setLargeIcon(largeIcon);

        // Eliminar la notificacion al clickarla
        builder.setAutoCancel(true);

        // Preparar la acción para ser activada con el click en la notificación
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.formulacionquimica.com"));
        if (i.resolveActivity(getPackageManager()) != null){
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
            builder.setContentIntent(pendingIntent);
        } else {
            Toast.makeText(this, "No hay activity disponible", Toast.LENGTH_SHORT).show();
        }

        // Lanzar la notificacion
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notificationManager.notify(NOTIFICACION_ID, notification);
    }


    private void notificacion() {
        // Crear la notificación med Notification.Builder
        Notification.Builder builder = new Notification.Builder(this);
        // Generar el contenido
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        builder.setTicker("Aviso");
        builder.setContentTitle("Tiene más de dos errores");
        builder.setContentText("Consulte la web para aprender más sobre química.");

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.flask1);
        builder.setLargeIcon(largeIcon);

        // Eliminar la notificacion al clickarla
        builder.setAutoCancel(true);

        // Preparar la acción para ser activada con el click en la notificación
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.formulacionquimica.com"));
        if (i.resolveActivity(getPackageManager()) != null){
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
            builder.setContentIntent(pendingIntent);
        } else {
            Toast.makeText(this, "No hay activity disponible", Toast.LENGTH_SHORT).show();
        }

        // Lanzar la notificacion
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notificationManager.notify(NOTIFICACION_ID, notification);
    }

    private void inicializacionView() {
        etCompuesto = findViewById(R.id.etCompuesto);
        tvContinuar = findViewById(R.id.tvContinuar);
        tvComprobar = findViewById(R.id.tvComprobar);
        btnComprobar = findViewById(R.id.btnComprobar);
        btnContinuar = findViewById(R.id.btnContinuar);
        rgContinuar = findViewById(R.id.rgContinuar);
        rbSi = findViewById(R.id.rbSi);
        rbNo = findViewById(R.id.rbNo);
    }
}