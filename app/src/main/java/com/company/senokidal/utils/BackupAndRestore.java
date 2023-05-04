package com.company.senokidal.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by denny on 16/05/2016.
 * Source: http://stackoverflow.com/questions/18322401/is-it-posible-backup-and-restore-a-database-file-in-android-non-root-devices
 */
public class BackupAndRestore {

    public static void importDB(Context context, File path) {
        try {
            //File sd = new File(path); //Environment.getExternalStorageDirectory();
            if (path.canWrite()) {
                Log.e("x",path.getPath());

                File backupDB = context.getDatabasePath(DatabaseHelper.DATABASE_NAME);
                //File currentDB = new File(path);

                FileChannel src = new FileInputStream(path).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                //Toast.makeText(context, "Import "+path.getPath()+" Successful!",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportDB(Context context, String path) {
        try {
            File sd = new File(path); //Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                Date date_sekarang = new Date();
                SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy-MM-dd h:m:s");

                Log.e("x",sd.getPath());
                String backupDBPath = String.format("%s-"+simpleDateformat.format(date_sekarang)+".bak", DatabaseHelper.DATABASE_NAME);
                File currentDB = context.getDatabasePath(DatabaseHelper.DATABASE_NAME);
                File backupDB = new File(sd, backupDBPath);
                Log.e("x",currentDB.getPath());
                Log.e("x",backupDB.getPath());

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                //Toast.makeText(context, "Backup "+backupDB.getPath()+" Successful!",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}