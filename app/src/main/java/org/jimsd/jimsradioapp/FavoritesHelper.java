package org.jimsd.jimsradioapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Chetan & Rahul on 10-Mar-18.
 */

public class FavoritesHelper extends SQLiteOpenHelper{

    public FavoritesHelper(Context context) {
        super(context,"fav.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table fav(prog_id text unique)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists fav");
        onCreate(db);
    }

    public Cursor selectData(){
        Cursor cursor;
        SQLiteDatabase db=getReadableDatabase();
        cursor=db.query("fav",new String[]{"prog_id"},null,null,null,null,"prog_id ASC");
        return cursor;
    }

    public int addFavorite(String prog_id){
        int status=-1;
        ContentValues contentValues=new ContentValues();
        contentValues.put("prog_id",prog_id);
        SQLiteDatabase db=getWritableDatabase();
        long res=db.insert("fav",null,contentValues);
        if(res>0)
            status=1;
        return status;
    }
    public int removeFavorite(String prog_id){
        SQLiteDatabase db=getWritableDatabase();
        //db.execSQL("delete from fav where prog_id="+prog_id);
        db.delete("fav","prog_id=?",new String[]{prog_id});
        return checkFavorite(prog_id);
    }

    public int checkFavorite(String prog_id){
        int response=1;
        Cursor cursor;
        SQLiteDatabase db=getReadableDatabase();
        if(prog_id==null){
            return -1;
        }
        cursor=db.query("fav",new String[]{"prog_id"},"prog_id=?",new String[]{prog_id},null,null,"prog_id ASC");

        if(cursor.getCount()>0)
            response =1;
        else if(cursor.getCount()==0)
            response=0;

        return response;
    }
}
