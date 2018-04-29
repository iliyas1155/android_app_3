package pandusland;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.kexit.kz.pandusland.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class OpeningActivity extends Activity {
    Typeface typeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/Oswald-Regular.ttf");
        ((TextView)findViewById(R.id.title)).setTypeface(typeface);
        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(5*1000);
                    Intent i=new Intent(getBaseContext(),MainActivity.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();
        /*if (isReachableByPing("pandusland.byethost16.com")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Сервер не доступен!");
            AlertDialog dialog = builder.create();
            dialog.show();
        }*/
    }

    public static boolean isReachableByPing(String host) {
        try{
            String cmd = "";
            if(System.getProperty("os.name").startsWith("Windows"))
                cmd = "ping -n 1 " + host; // For Windows
            else
                cmd = "ping -c 1 " + host; // For Linux and OSX
            Process myProcess = Runtime.getRuntime().exec(cmd);
            myProcess.waitFor();
            return myProcess.exitValue() == 0;
        } catch( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean isURLReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL("http://pandusland.byethost16.com");   // Change to "http://google.com" for www  test.
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(10 * 1000);          // 10 s.
                urlc.connect();
                if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).

                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_opening, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
