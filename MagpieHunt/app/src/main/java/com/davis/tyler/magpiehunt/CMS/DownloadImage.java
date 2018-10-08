package com.davis.tyler.magpiehunt.CMS;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.davis.tyler.magpiehunt.FileSystemManager;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

public class DownloadImage extends AsyncTask<URL, Void, Bitmap> {

    private static final String LOG_E_TAG = "DownloadImageTask";
    private Context context;
    private FileSystemManager fm;
    private String filename;
    public DownloadImage(Context context, String filename) {
        this.context = context;
        fm = new FileSystemManager();
        this.filename = filename;
    }

    @Override
    protected Bitmap doInBackground(URL... params) {
        URL imageURL = params[0];
        Bitmap downloadedBitmap = null;
        try {
            InputStream inputStream = imageURL.openStream();
            downloadedBitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e(LOG_E_TAG, e.getMessage());
            e.printStackTrace();
        }
        return downloadedBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {

        try {

            Thread t = new Thread(new DownloadImageThread(result));
            t.start();


        }catch(Exception e){
            e.printStackTrace();
        }

    }
    private class DownloadImageThread implements Runnable{
        private Bitmap bitmap;
        DownloadImageThread(Bitmap bitmap){
            this.bitmap = bitmap;
        }
        @Override
        public void run() {
            try {
                fm.saveImageToInternalStorage(context, bitmap, filename);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}