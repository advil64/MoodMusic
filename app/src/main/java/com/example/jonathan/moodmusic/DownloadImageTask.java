package com.example.jonathan.moodmusic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.InputStream;
import android.util.Log;
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView image;
    public DownloadImageTask(ImageView image){
        this.image = image;
    }
    @Override
    protected Bitmap doInBackground(String... urls){
        String url = urls[0];
        Bitmap bm = null;
        try{
            InputStream in = new java.net.URL(url).openStream();
            bm = BitmapFactory.decodeStream(in);
        }
        catch(Exception e){
            Log.e("Error Message", e.getMessage());
            e.printStackTrace();
        }
        return bm;
    }
    @Override
    protected void onPostExecute(Bitmap result){
        image.setImageBitmap(result);
    }
}