package com.example.jonathan.moodmusic;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.AsyncTask;
import java.lang.String;
import android.os.NetworkOnMainThreadException;
public class Data extends AsyncTask<String, String, String>{
    String info = "";
    URL url;
    HttpURLConnection con = null;
    @Override
    protected String doInBackground(String... urls){
        try {
            url = new URL(urls[0]);
            con = (HttpURLConnection) url.openConnection();
            InputStream in = con.getInputStream();
            InputStreamReader ir = new InputStreamReader(in);
            int data = ir.read();
            while(data != -1){
                char current = (char) data;
                info += current;
                data = ir.read();
            }
            int pos = info.indexOf("temp");
            int endPos = info.indexOf(",", pos);
            String temp = info.substring(pos+6, endPos);
            pos = info.indexOf("main");
            endPos = info.indexOf("\"", pos + 8);
            String weather = info.substring(pos + 7, endPos);
            pos = info.indexOf("icon");
            endPos = info.indexOf("\"", pos + 9);
            String icon = info.substring(pos+7, endPos);
            pos = info.indexOf("name");
            endPos = info.indexOf("\"", pos + 9);
            String name = info.substring(pos + 7, endPos);
            return temp + "-" + weather + "-" + icon + "-" + name;
        }
        catch(NetworkOnMainThreadException e){
            return "NMTE";
        }
        catch(MalformedURLException a){
            return "MUE";
        }
        catch(IOException i){
            return "IO";
        }
    }
    @Override
    protected void onPostExecute(String result){
    }
}