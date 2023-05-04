package com.company.senokidal.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Fungsi {


    static int BUFFER_SIZE = 1024;
    public static DatabaseHelper db;

    /**
    public static String getDate(long timeStamp){

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "0000-00-00";
        }
    }


    public static String getFormatDate(Date time){
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z");
        Date newDate = null;
        try {
            newDate = format.parse(time.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(newDate);

        return date;
    }


    public static Date getDate(Timestamp time){
        return time.toDate();
    }

    public static Timestamp getTimestamp(String time){
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = null;
        try{
            date = s.parse(time);
        }catch (Exception e){
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return new Timestamp(date);
    }
*/


    public String to(String time, String format) {

        if(format == null){
            format = "yyyy-MM-dd";
        }

        Date date_sekarang = new Date();

        SimpleDateFormat s = new SimpleDateFormat(format);
        Date date = null;
        try{
            date = s.parse(time);
        }catch (Exception e){
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);

        //String sekarang = now.get(Calendar.YEAR)+"-"+now.get(Calendar.MONTH)+"-"+now.get(Calendar.DAY_OF_MONTH);

        //SimpleDateFormat formatter = new SimpleDateFormat("E");
        //String days = formatter.format(date);
        //int dayofweek = Integer.valueOf(days);

        //now.set(tahun,bulan,hari);


        String[] yyyymd = time.split("-");

        int tahun = 0;
        int bulan = 0;
        int hari = 0;
        String timeago = "";
        String hasil = null;


        SimpleDateFormat f1 = new SimpleDateFormat(format);
        if(time.equals(f1.format(date_sekarang))){
            timeago = "Hari ini ";
        }

        if(yyyymd.length == 3) {
            tahun = Integer.parseInt(yyyymd[0]);
            bulan = Integer.parseInt(yyyymd[1]);
            hari = Integer.parseInt(yyyymd[2]);
        }

        //String[] timeago = new String[]{"Hari ini","Kemarin","Minggu lalu","Bulan lalu","Tahun lalu"};
        String[] nama_hari = new String[]{"Minggu","Senin","Selasa","Rabu","Kamis","Jumat","Sabtu"};
        String[] nama_bulan = new String[]{
                "Januari",
                "Februari",
                "Maret",
                "April",
                "Mei",
                "Juni",
                "Juli",
                "Agustus",
                "September",
                "Oktober",
                "November",
                "Desember"
        };

        /**Format: Hari ini Rabu, 28 Oktober 1988
        Format: Kemarin Rabu, 28 Oktober 1988
        Format: Minggu lalu Rabu, 28 Oktober 1988*/
        //hasil = hari+" "+nama_bulan[bulan]+" "+tahun;

        //int day = now.get(Calendar.DAY_OF_WEEK);

        hasil = timeago + nama_hari[day_of_week-1]+", " +hari+ " " +nama_bulan[bulan-1]+" "+tahun;

        return hasil;
    }

    public String to2(String tanggal){
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try{
            date = s.parse(tanggal);
        }catch (Exception e){
            e.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        String format = formatter.format(date);

        return format;

    }

    public String toConvert(String tanggal){
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-d");
        Date date = null;
        try{
            date = s.parse(tanggal);
        }catch (Exception e){
            e.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String format = formatter.format(date);

        return String.valueOf(format);

    }

    public String now(){
        Date date = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy-MM-dd");

        return to( simpleDateformat.format(date), null );
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }



    public void openFile(Context c,File filepath){
        //Log.e("a",filepath);

        String str = "com.microsoft.office.excel";
        //Uri pathe = Uri.fromFile(new File(filepath+".xls"));


        //Uri pathe = FileProvider.getUriForFile(c, c.getApplicationContext().getPackageName() + ".provider", new File(filepath));
        Uri pathe = Uri.fromFile(filepath);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(pathe, "application/vnd.ms-excel");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PackageManager packageManager = c.getPackageManager();
        if (intent.resolveActivity(packageManager) != null) {
            c.startActivity(intent.createChooser(intent, "Choose app to open document"));
        }
        else
        {
            Toast.makeText(c, "Aplikasi Excel belum tersedia harap download dahulu dari Play Store", Toast.LENGTH_SHORT).show();
            //Launch PlayStore
            try {
                c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+str)));

            } catch (android.content.ActivityNotFoundException n) {
                c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+str)));
            }
        }

    }

    public static void zip(String[] files, String zipFile) throws IOException {
        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte data[] = new byte[BUFFER_SIZE];

            for (int i = 0; i < files.length; i++) {
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                try {
                    ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                        out.write(data, 0, count);
                    }
                } finally {
                    origin.close();
                }
            }
        } finally {
            out.close();
        }
    }


    static void saveImageToSDCard(String Image, int photo_id, String nama, String kelas, String path)
    {
        try{
            byte[] imageBytes=Base64.decode(Image, Base64.DEFAULT);
            InputStream is = new ByteArrayInputStream(imageBytes);
            Bitmap image=BitmapFactory.decodeStream(is);

            if (!new File(path).exists()) {
                new File(path).mkdir();
            }


            if (new File( path ).exists()) {

                path = path + File.separator + kelas;
                if (!new File(path).exists()) {
                    new File(path).mkdir();
                }


            }

            String mFilePath = path + "/" + nama.toLowerCase().replaceAll(" ","_") + ".jpg";

            File file = new File(mFilePath);

            FileOutputStream stream = new FileOutputStream(file);

            if (!file.exists()){
                file.createNewFile();
            }

            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            is.close();
            image.recycle();

            stream.flush();
            stream.close();
        }
        catch(Exception e)
        {
            Log.v("SaveFile",""+e);
        }
    }


    public String filterText(String txt){
        return txt.replaceAll("[^a-zA-Z0-9]", " ");
    }


    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }
}
