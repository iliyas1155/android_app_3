package pandusland;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kexit.kz.pandusland.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends ActionBarActivity implements OnFragmentInteractionListener {
    private CloseList closeList;
    private FragmentTransaction transaction;

    private Map map;
    private About about;
    private Main main;
    private Profile profile;


    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private MenuAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private FrameLayout frame;
    private float lastTranslate = 0.0f;
    Typeface typeface;
    View header, footer;
    List<Menu> list;
    private int currpage;

    public String responseStr = "";
    Integer stats = 0;
    Integer stats2 = 0;

    private MyDBHandler db = new MyDBHandler(this);

    public File offileTemp;
    public Pandus offPandus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /////
        if(!hasConnection()){
            final Intent intent = new Intent();
            intent.setClass(getActivity(), CreateItemOffline.class);

            startActivity(intent);
        }
        else {

            setContentView(R.layout.activity_main);
            List<Pandus> offlist = db.getRecords();
            if(!offlist.isEmpty()){
                //Vilozhit

                for(Pandus item : offlist){
                    try {
                        File filesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        File imageFile = new File(filesDir, item.file);
                        //ADHERERERE
                        offileTemp = imageFile;
                        offPandus = item;
                        new SendDataTask().execute();
                        db.deleteRecord(item.id);
                        return;
                    } catch (NullPointerException e) {e.printStackTrace();}
                }

            }

            prefs = this.getSharedPreferences("PANDUSLAND", Context.MODE_PRIVATE);
            editor = prefs.edit();

            typeface = Typeface.createFromAsset(getAssets(), "fonts/Oswald-Regular.ttf");
            frame = (FrameLayout) findViewById(R.id.container);
            mDrawerList = (ListView) findViewById(R.id.navList);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mActivityTitle = getTitle().toString();

            list = new ArrayList<>();
            addDrawerItems();
            setupDrawer();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            setTitleToBar("PandusLand");
            //SET FRAGMENTS
            closeList = new CloseList();
            map = new Map();
            about = new About();
            main = new Main();
            profile = new Profile();


            transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, main);
            transaction.addToBackStack(null);
            transaction.commit();


            LocationManager locationManager = (LocationManager)
                    getSystemService(this.LOCATION_SERVICE);
            LocationListener locationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            myloc = new Location("MyLoc");
            myloc.setLatitude(latitude);
            myloc.setLongitude(longitude);


            try {
                Pandusland.isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                System.out.println("oops");
            }


        }
    }

    public void setTitleToBar(String titleToBar) {
        SpannableString s = new SpannableString(titleToBar);
        s.setSpan(new TypefaceSpan(this, "Oswald-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
    }

    private class SendDataTask extends AsyncTask<String, Integer, Double> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Double doInBackground(String... params) {
            postData();
            return null;
        }

        protected void onPostExecute(Double result){
        }

        protected void onProgressUpdate(Integer... progress){
        }
    }

    public void postData() {


        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://pandusland.byethost16.com/addramps.php");

        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null, Charset.forName("UTF-8"));

            entity.addPart("address", new StringBody(offPandus.address));
            entity.addPart("category", new StringBody(offPandus.category));
            entity.addPart("type", new StringBody(offPandus.type));
            entity.addPart("comment", new StringBody(offPandus.comment));
            entity.addPart("anon", new StringBody(offPandus.anon));
            entity.addPart("Image", new FileBody(offileTemp));
            entity.addPart("Longitude", new StringBody(offPandus.longitude+""));
            entity.addPart("Latitude", new StringBody(offPandus.latitude+""));
            entity.addPart("username", new StringBody(offPandus.uname));
            entity.addPart("user_id", new StringBody(offPandus.user_id + ""));

            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            ResponseHandler<String> handler = new BasicResponseHandler();
            responseStr = handler.handleResponse(response);

        }
        catch (Exception ex)
        {
            System.out.println("ooops aidigidai");
        }


    }

    private void addDrawerItems() {
        header = getHeader();
        mDrawerList.addHeaderView(header);

        list.add(new Menu("Add a ramp", "Main"));
        list.add(new Menu("Nearest", "Near"));
        list.add(new Menu("Map", "Map"));
        list.add(new Menu("Profile", "Profile"));
        list.add(new Menu("About the project", "About"));
        adapter = new MenuAdapter(getActivity(), R.layout.menu_list_item, list, typeface);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    //profileOpen();
                    return;
                } else if (position == 1) {
                    openMain();
                } else if (position == 2) {
                    openNear();
                } else if (position == 3) {
                    openMap();
                } else if (position == 4) {
                    profileOpen();
                } else if (position == 5) {
                    openAbout();
                }
                System.out.println(list.get(position - 1).name);
            }
        });
    }

    public View getHeader() {
        View v = getActivity().getLayoutInflater().inflate(R.layout.menu_list_header, null);
        if (Pandusland.me == null){
            ((TextView) v.findViewById(R.id.headerText)).setText("");
        }else{
            ((TextView) v.findViewById(R.id.headerText)).setText(Pandusland.me.getSname());
        }

        ((TextView) v.findViewById(R.id.headerText)).setTypeface(typeface);
        return v;
    }

    int currentPage = 0;

    public void profileOpen() {
        if (Pandusland.me == null) {
            showLogin();
        } else {
            mDrawerLayout.closeDrawer(mDrawerList);
            transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, profile);
            transaction.commit();
        }
    }


    public void openMain() {
        mDrawerLayout.closeDrawer(mDrawerList);
        if (currentPage == 1) {
            return;
        }
        currentPage = 1;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, main);
        transaction.commit();


    }

    public void openNear() {
        mDrawerLayout.closeDrawer(mDrawerList);
        if (currentPage == 2) {
            return;
        }
        currentPage = 2;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, closeList);
        transaction.commit();

        System.out.println("yahoo" + myloc.toString());

        System.out.println();
        new GetList().execute();
    }

    public void openMap() {
        mDrawerLayout.closeDrawer(mDrawerList);
        if (currentPage == 3) {
            return;
        }
        currentPage = 3;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, map);
        transaction.commit();
    }

    public void openAbout() {
        mDrawerLayout.closeDrawer(mDrawerList);
        if (currentPage == 4) {
            return;
        }
        currentPage = 4;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, about);
        transaction.commit();
    }

    public String dlLogin_, dlPassword_;
    public View dlView;

    public void showLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dlView = inflater.inflate(R.layout.dialog_layout_login, null);
        builder.setView(dlView)
                .setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dlLogin_ = ((EditText) dlView.findViewById(R.id.username)).getText().toString();
                        dlPassword_ = ((EditText) dlView.findViewById(R.id.password)).getText().toString();
                        System.out.println(dlLogin_ + " " + dlPassword_);
                        new Login().execute();
                    }
                })
                .setNegativeButton("Registration", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showRegistration();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    boolean checkLogin;

    @Override
    public void loadProfileListView() {
        new GetUserList().execute();
    }

    @Override
    public void loadMainStats() {
        new getStat().execute();
    }

    class Login extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", dlLogin_));
            params.add(new BasicNameValuePair("password", dlPassword_));
            JSONObject json = jsonParser.makeHttpRequest(Pandusland.URL, "POST", params);
            System.out.println(json.toString());
            try {
                checkLogin = json.getBoolean("CHECK");
                if (checkLogin) {
                    JSONArray user = json.getJSONArray("ROWS");
                    for (int i = 0; i < user.length(); i++) {
                        JSONObject c = user.getJSONObject(i);
                        String id = c.getString("id");
                        String sname = c.getString("sname");
                        int raiting = c.getInt("rating");
                        String email = c.getString("email");
                        dlPassword_ = c.getString("password");
                        Pandusland.userid = id;
                        Pandusland.me = new Member(id, sname, email, raiting);
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            if (checkLogin) {
                editor.putString("EMAIL", Pandusland.me.getEmail());
                editor.putString("PASSWORD", dlPassword_);
                editor.commit();
                System.out.println("LOGGED IN");
            } else {
                System.out.println("ERROR");
            }
        }
    }

    public String dRLogin_, dRPassword_, dRSname_;
    public View dRView;

    public void showRegistration() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dRView = inflater.inflate(R.layout.dialog_layout_regi, null);
        builder.setView(dRView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dRLogin_ = ((EditText) dRView.findViewById(R.id.username)).getText().toString();
                        dRPassword_ = ((EditText) dRView.findViewById(R.id.password)).getText().toString();
                        dRSname_ = ((EditText) dRView.findViewById(R.id.sname)).getText().toString();
                        System.out.println(dRLogin_ + " " + dRPassword_ + " " + dRSname_);
                        new Registr().execute();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    boolean checkRegistr;

    class Registr extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", dRLogin_));
            params.add(new BasicNameValuePair("password", dRPassword_));
            params.add(new BasicNameValuePair("name", dRSname_));
            JSONObject json = jsonParser.makeHttpRequest(Pandusland.URL + "registration.php", "POST", params);
            System.out.println(json.toString());
            try {
                checkRegistr = json.getBoolean("CHECK");
                if (checkRegistr) {
                    JSONArray user = json.getJSONArray("ROWS");
                    for (int i = 0; i < user.length(); i++) {
                        JSONObject c = user.getJSONObject(i);
                        String id = c.getString("id");
                        String sname = c.getString("sname");
                        int raiting = c.getInt("rating");
                        String email = c.getString("email");
                        dlPassword_ = c.getString("password");
                        Pandusland.userid = id;
                        Pandusland.me = new Member(id, sname, email, raiting);
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            if (checkRegistr) {
                editor.putString("EMAIL", Pandusland.me.getEmail());
                editor.putString("PASSWORD", dRPassword_);
                editor.commit();
                System.out.println("LOGGED IN");
            } else {
                System.out.println("ERROR");
            }
        }
    }

    public Activity getActivity() {
        return this;
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle("Navigation!");
                setTitleToBar("Navigation");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setTitleToBar("Pandus Land");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /*public void onDrawerSlide(View drawerView, float slideOffset)
            {
                float moveFactor = (mDrawerList.getWidth() * slideOffset);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                {
                    frame.setTranslationX(moveFactor);
                }
                else
                {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    frame.startAnimation(anim);

                    lastTranslate = moveFactor;
                }
            }*/
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getHeader();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if(hasConnection())
        {
            mDrawerToggle.syncState();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private double longitude;
    private double latitude;
    private Location myloc;
    JSONParser jsonParser = new JSONParser();

    /*---------- Listener class to get coordinates ------------- */
    protected class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            longitude = loc.getLongitude();
            latitude = loc.getLatitude();
            String s = longitude + "\n" + latitude;
            System.out.println(loc.toString());
            myloc = loc;
            try {
                for (Pandus item : closeList.objlist) {
                    Location t = new Location("Location of " + item.address);

                    t.setLongitude(item.longitude);
                    t.setLatitude(item.latitude);
                    System.out.println(t.toString());
                    item.distance = (myloc.distanceTo(t) / 1);
                    System.out.println(t.toString());

                }

                Collections.sort(closeList.objlist);

                if (currpage == 1) {

                }
            } catch (Exception ex) {
                System.out.println("error");
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    /*INIT CLOSESTS*/
    class GetList extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            closeList.objlist = new ArrayList<Pandus>();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = jsonParser.makeHttpRequest(Pandusland.URL + "list.php", "POST", params);
            try {
                closeList.check = json.getBoolean("CHECK");
                if (closeList.check) {
                    JSONArray rows = json.getJSONArray("ROWS");
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject c = rows.getJSONObject(i);
                        int id = c.getInt("id");
                        double longitude = c.getDouble("longitude");
                        double latitude = c.getDouble("latitude");
                        String address = c.getString("address");
                        String type = c.getString("type");
                        String comment = c.getString("comment");
                        String imagepath = c.getString("imagepath");
                        int user_id = c.getInt("user_id");
                        String category = c.getString("category");
                        closeList.objlist.add(new Pandus(id, longitude, latitude, address, type, comment, imagepath, user_id, category));
                        Location t = new Location("Temp location");
                        t.setLongitude(closeList.objlist.get(closeList.objlist.size() - 1).longitude);
                        t.setLatitude(closeList.objlist.get(closeList.objlist.size() - 1).latitude);
                        System.out.println(t.toString());
                        closeList.objlist.get(closeList.objlist.size() - 1).distance = myloc.distanceTo(t) / 1;
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            if (closeList.check) {
                closeList.adapter = new PandusAdapter(getActivity(), R.layout.item_pandus, closeList.objlist);
                closeList.list.setAdapter(closeList.adapter);
            }
        }
    }


    /*USer list*/
    class GetUserList extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            profile.objlist = new ArrayList<Pandus>();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = jsonParser.makeHttpRequest(Pandusland.URL + "list.php?"+Pandusland.me.getId(), "POST", params);
            try {
                profile.check = json.getBoolean("CHECK");
                if (profile.check) {
                    JSONArray rows = json.getJSONArray("ROWS");
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject c = rows.getJSONObject(i);
                        int id = c.getInt("id");
                        double longitude = c.getDouble("longitude");
                        double latitude = c.getDouble("latitude");
                        String address = c.getString("address");
                        String type = c.getString("type");
                        String comment = c.getString("comment");
                        String imagepath = c.getString("imagepath");
                        int user_id = c.getInt("user_id");
                        String category = c.getString("category");
                        profile.objlist.add(new Pandus(id, longitude, latitude, address, type, comment, imagepath, user_id, category));
                        Location t = new Location("Temp location");
                        t.setLongitude(profile.objlist.get(profile.objlist.size() - 1).longitude);
                        t.setLatitude(profile.objlist.get(profile.objlist.size() - 1).latitude);
                        System.out.println(t.toString());
                        profile.objlist.get(profile.objlist.size() - 1).distance = myloc.distanceTo(t) / 1;
                        System.out.println("yahoo motherfather");
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            if (profile.check) {
                profile.adapter = new PandusAdapter(getActivity(), R.layout.item_pandus, closeList.objlist);
                profile.list.setAdapter(profile.adapter);
            }
        }
    }



    /*status*/
    class getStat extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            closeList.objlist = new ArrayList<Pandus>();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = jsonParser.makeHttpRequest(Pandusland.URL + "stat.php", "POST", params);
            try {
                closeList.check = json.getBoolean("CHECK");
                if (closeList.check) {
                    JSONArray rows = json.getJSONArray("ROWS");
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject c = rows.getJSONObject(i);

                        stats = c.getInt("count");
                        stats2 = stats/3;

                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            main.countStat.setText(stats.toString());
            main.countStat2.setText(stats2.toString());
        }


    }



    private boolean hasConnection()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

}
