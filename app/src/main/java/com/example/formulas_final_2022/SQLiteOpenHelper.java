package com.example.formulas_final_2022;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {

    private final String onCreateTable = "CREATE TABLE compuestos (nombre TEXT, formula TEXT PRIMARY KEY)";
    private final String insertCompuestos = "INSERT INTO compuestos (nombre, formula) VALUES ('Ácido sulfúrico', 'SO4H2')";
    private final String insertCompuestos1 = "INSERT INTO compuestos (nombre, formula) VALUES ('Água', 'H2O')";
    private final String insertCompuestos2 = "INSERT INTO compuestos (nombre, formula) VALUES ('Carbonato cálcico', 'CO3CA')";
    private final String insertCompuestos3 = "INSERT INTO compuestos (nombre, formula) VALUES ('Anhídrido carbónico', 'CO2')";
    private final String insertCompuestos4 = "INSERT INTO compuestos (nombre, formula) VALUES ('Monóxido de carbono', 'CO')";

    public SQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(onCreateTable);
        sqLiteDatabase.execSQL(insertCompuestos);
        sqLiteDatabase.execSQL(insertCompuestos1);
        sqLiteDatabase.execSQL(insertCompuestos2);
        sqLiteDatabase.execSQL(insertCompuestos3);
        sqLiteDatabase.execSQL(insertCompuestos4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
