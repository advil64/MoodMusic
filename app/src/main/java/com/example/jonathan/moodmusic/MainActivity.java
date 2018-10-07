package com.example.jonathan.moodmusic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.Random;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Random rand = new Random();
    private Location lastlocation;
    double latitude;
    double longitude;
    private FusedLocationProviderClient mfc; //included in the newest update of android studio
    String iframe;
    String lol = "02d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView temp_tv = findViewById(R.id.tv_temperature);
        TextViewCompat.setAutoSizeTextTypeWithDefaults(temp_tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        mfc = LocationServices.getFusedLocationProviderClient(this);

    }

    //runs when the app becomes visible and checks for permission
    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //34 is the code for location permission
        if (requestCode == 34) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.

            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied. Make sure to let the user know that permission is required and that
                //they can click on the settings link to open the app settings where they can approve permission

                showSnackbar(R.string.warn, R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 34);
    }



    private void getLastLocation() {
        try {
            mfc.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                lastlocation = task.getResult();

                                latitude = lastlocation.getLatitude();
                                longitude = lastlocation.getLongitude();
                            }

                        }
                    });
        }
        catch(SecurityException e){
            //Log.e("going","going1");
        }
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            //Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.warn, R.string.settings,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {

            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    public void onClick(View v){
        if(v.getId()==R.id.go_button){
            final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
            MyBounceInterpolator myBounceInterpolator = new MyBounceInterpolator(0.2,20);
            myAnim.setInterpolator(myBounceInterpolator);
            v.startAnimation(myAnim);

            String baseURL = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=imperial&APPID=588a47c2ff108cc1e4dd31dc1a033a32";

            try{

                Data x = new Data();
                String[] info = x.execute(baseURL).get().split("-");
                String temp=info[0];
                TextView tv=findViewById(R.id.tv_temperature);
                tv.setText(temp.substring(0,temp.indexOf('.')));

                //Toast.makeText(this,"Going",Toast.LENGTH_SHORT).show();
                new DownloadImageTask((ImageView) findViewById(R.id.iv_tempearture)).execute("https://openweathermap.org/img/w/" + info[2] + ".png");
                //Toast.makeText(this,"Going2",Toast.LENGTH_SHORT).show()
                TextView loc=findViewById(R.id.location_view);
                loc.setText(info[3]);
                TextView weather=findViewById(R.id.weather_view);
                weather.setText(info[1]);
                initWebView(info[2]);
            }
            catch(Exception e){

                Toast.makeText(this, "Error is "+e.getMessage(),Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
            }



        }
    }
    private void initWebView(String weather) {

        WebView mWebView = findViewById(R.id.webview);


        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT > 7) {
            settings.setPluginState(WebSettings.PluginState.ON);
        }

        else {
            //settings.setPluginsEnabled(true);
        }

        songfinder(weather);

        String html = "";
        html += "<html><body>";
        html += iframe;
        html += "</body></html>";

        mWebView.loadData(html, "text/html", null);
    }

    private void songfinder(String we){

        ArrayList<ArrayList<String>> master_list=new ArrayList<>();

        ArrayList<String> sunny_morn=new ArrayList<>();
        sunny_morn.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/233701420&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        sunny_morn.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/67283900&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        sunny_morn.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/326576278&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        sunny_morn.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/102367070&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        master_list.add(sunny_morn);

        ArrayList<String> clear_night=new ArrayList<>();
        clear_night.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/156125563&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        clear_night.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/313627932&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        clear_night.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/381451454&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        master_list.add(clear_night);

        ArrayList<String> cloudy=new ArrayList<>();
        cloudy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/139526999&color=%23ff5500&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        cloudy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/147743247&color=%23ff5500&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        cloudy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/90465819&color=%23ff5500&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        cloudy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/45108181&color=%23ff5500&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        cloudy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/123525703&color=%23ff5500&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        cloudy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/231447788&color=%23ff5500&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        cloudy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/222688263&color=%23ff5500&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        cloudy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/116769568&color=%23ff5500&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        cloudy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/79879805&color=%23ff5500&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        master_list.add(cloudy);

        ArrayList<String> rain=new ArrayList<>();
        rain.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/255976825&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        rain.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/144189921&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        rain.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/252016766&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        rain.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/55492432&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        rain.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/232915336&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        rain.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/145214852&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        rain.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/255737731&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        master_list.add(rain);

        ArrayList<String> thunderstorm=new ArrayList<>();
        thunderstorm.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/151129490&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        thunderstorm.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/105085838&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        thunderstorm.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/341062161&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        master_list.add(thunderstorm);

        ArrayList<String> snowy=new ArrayList<>();
        snowy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/71071977&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        snowy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/238049485&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        snowy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/6736771&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        snowy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/28949088&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        snowy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/28949088&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        snowy.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/373055171&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        master_list.add(snowy);

        ArrayList<String> mist = new ArrayList<>();
        mist.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/62770474&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        mist.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/271223479&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        mist.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/369205784&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"></iframe>");
        mist.add("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" allow=\"autoplay\" src=\"https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/170391727&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true\"</iframe>");
        master_list.add(mist);

        switch(we) {
            case "01d":

                iframe = master_list.get(0).get((int)(Math.random() * master_list.get(0).size()));
                break;

            case "01n":

                iframe = master_list.get(1).get((int)(Math.random() * master_list.get(1).size()));
                break;
            case "02d":

                iframe = master_list.get(2).get((int)(Math.random() * master_list.get(2).size()));
                break;

            case "02n":

                iframe = master_list.get(2).get((int)(Math.random() * master_list.get(2).size()));
                break;

            case "03d":

                iframe = master_list.get(2).get((int)(Math.random() * master_list.get(2).size()));
                break;

            case "03n":

                iframe = master_list.get(2).get((int)(Math.random() * master_list.get(2).size()));
                break;

            case "04d":

                iframe = master_list.get(2).get((int)(Math.random() * master_list.get(2).size()));
                break;

            case "04n":

                iframe = master_list.get(2).get((int)(Math.random() * master_list.get(2).size()));
                break;

            case "09d":

                iframe = master_list.get(3).get((int)(Math.random() * master_list.get(3).size()));
                break;

            case "09n":

                iframe = master_list.get(3).get((int)(Math.random() * master_list.get(3).size()));
                break;

            case "10d":

                iframe = master_list.get(3).get((int)(Math.random() * master_list.get(3).size()));
                break;

            case "10n":

                iframe = master_list.get(3).get((int)(Math.random() * master_list.get(3).size()));
                break;

            case "11d":

                iframe = master_list.get(4).get((int)(Math.random() * master_list.get(4).size()));
                break;

            case "11n":

                iframe = master_list.get(4).get((int)(Math.random() * master_list.get(4).size()));
                break;

            case "13d":

                iframe = master_list.get(5).get((int)(Math.random() * master_list.get(5).size()));
                break;

            case "13n":

                iframe = master_list.get(5).get((int)(Math.random() * master_list.get(5).size()));
                break;

            case "50d":

                iframe = master_list.get(6).get((int)(Math.random() * master_list.get(6).size()));
                break;

            case "50n":

                iframe = master_list.get(6).get((int)(Math.random() * master_list.get(6).size()));
                break;

            default :
                iframe = "There's something terribly wrong with this app";
        }
    }
}


