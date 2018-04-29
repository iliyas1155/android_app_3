package pandusland;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kexit.kz.pandusland.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.map.GeoCode;
import ru.yandex.yandexmapkit.map.GeoCodeListener;


public class CreateItem extends ActionBarActivity implements GeoCodeListener, OnClickListener, LocationListener{
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int MAP_PLACE_CHOOSE_REQUEST_CODE = 555;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public String responseStr = "", status = "";
    public ToggleButton switchAnon;
    public EditText inDetailField;
    public EditText addressField;
    private ImageView imgPreview;
    private Button sendBtn;
    File imageFile;
    MapController mMapController;
    AlertDialog.Builder builder;
    private String rampType = "";
    private String category = "";

    private String urlYandex = "http://geocode-maps.yandex.ru/1.x/?geocode=";
    private XMLParser xmlp;

    private String USER_SERVICE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_create_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgPreview = (ImageView) findViewById(R.id.imgItem);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        addressField = (EditText) findViewById(R.id.addressField);

        inDetailField = (EditText) findViewById(R.id.inDetailField);



        imgPreview.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

        builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.updateImageTitle))
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.updateImageContent))
                .setNegativeButton(getResources().getString(R.string.updateImageNo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(getResources().getString(R.string.updateImageYes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                captureImage();
                            }
                        });
        builder.create();


         if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.camerisnotexist),
                    Toast.LENGTH_LONG).show();
        }else{
            captureImage();
        }


        //location
        LocationManager locationManager = (LocationManager)
                getSystemService(this.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        myloc = new Location("MyLoc");
        myloc.setLatitude(latitude);
        myloc.setLongitude(longitude);


        //ramp type
        (findViewById(R.id.greenButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rampType = "good";
                defaultButton();
                ImageButton button = (ImageButton) findViewById(R.id.greenButton);
                button.setImageResource(R.drawable.ramp_green_i);
            }
        });
        (findViewById(R.id.yellowButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rampType = "requires repair";
                defaultButton();
                ImageButton button = (ImageButton) findViewById(R.id.yellowButton);
                button.setImageResource(R.drawable.ramp_yellow_i);
            }
        });

        (findViewById(R.id.redButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rampType = "not available";
                defaultButton();
                ImageButton button = (ImageButton) findViewById(R.id.redButton);
                button.setImageResource(R.drawable.ramp_red_i);
            }
        });

        //category

        (findViewById(R.id.icon_cat_1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "For Kids";
                defaultButtonCat();
                ImageButton button = (ImageButton) findViewById(R.id.icon_cat_1);
                button.setImageResource(R.drawable.icon_baby);
            }
        });
        (findViewById(R.id.icon_cat_2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Cafes and Restaurants";
                defaultButtonCat();
                ImageButton button = (ImageButton) findViewById(R.id.icon_cat_2);
                button.setImageResource(R.drawable.icon_cafe);
            }
        });
        (findViewById(R.id.icon_cat_3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Education";
                defaultButtonCat();
                ImageButton button = (ImageButton) findViewById(R.id.icon_cat_3);
                button.setImageResource(R.drawable.icon_edu);
            }
        });
        (findViewById(R.id.icon_cat_4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Environment";
                defaultButtonCat();
                ImageButton button = (ImageButton) findViewById(R.id.icon_cat_4);
                button.setImageResource(R.drawable.icon_flat);
            }
        });
        (findViewById(R.id.icon_cat_5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Medicine";
                defaultButtonCat();
                ImageButton button = (ImageButton) findViewById(R.id.icon_cat_5);
                button.setImageResource(R.drawable.icon_med);
            }
        });
        (findViewById(R.id.icon_cat_6)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Shopping Malls";
                defaultButtonCat();
                ImageButton button = (ImageButton) findViewById(R.id.icon_cat_6);
                button.setImageResource(R.drawable.icon_shop);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.imgItem:{
                builder.show();
                break;
            }
            case R.id.sendBtn:
                if(  addressField.getText().toString().equals("") || addressField.getText().toString().equals(" "))
                {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.emptyField), Toast.LENGTH_SHORT).show();
                }else{
                    new SendDataTask().execute();
                }
                break;

            default: break;
        }



    }


    //location
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }





    private class SendDataTask extends AsyncTask<String, Integer, Double> {

        @Override
        protected void onPreExecute() {
            sendBtn.setEnabled(false);
            //sendBtn.setBackground(getResources().getDrawable(R.drawable.button_inverse_selector));
            setProgressBarIndeterminateVisibility(true);
            super.onPreExecute();
        }

        @Override
        protected Double doInBackground(String... params) {
            postData();
            return null;
        }

        protected void onPostExecute(Double result){
            setProgressBarIndeterminateVisibility(false);
            Toast.makeText(getApplicationContext(), responseStr, Toast.LENGTH_LONG).show();
            sendBtn.setEnabled(true);
            //sendBtn.setBackground(getResources().getDrawable(R.drawable.button_success_selector));
            finish();
        }

        protected void onProgressUpdate(Integer... progress){
        }
    }

    public void postData() {

        
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://pandusland.byethost16.com/addramps.php");

            try {
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null, Charset.forName("UTF-8"));


                if (Pandusland.me != null)
                {
                    entity.addPart("user_id", new StringBody(Pandusland.me.getId()));
                }
                else {
                    entity.addPart("user_id", new StringBody("0"));
                }
                Switch s = (Switch) findViewById(R.id.switchAnon);

                if (s.getText().toString().equals("Yes")) {
                    entity.addPart("anon", new StringBody("1"));
                    entity.addPart("username", new StringBody("Anonymous"));
                }else{
                    if (Pandusland.me != null)
                    {
                        entity.addPart("username", new StringBody(Pandusland.me.getSname().toString()));
                    }
                    else {
                        entity.addPart("username", new StringBody("Anonymous"));
                    }
                    entity.addPart("anon", new StringBody("0"));
                }

                // entity.addPart("lat", new StringBody(String.valueOf(latitude)));
                // entity.addPart("lon", new StringBody(String.valueOf(longitude)));
                entity.addPart("address", new StringBody(String.valueOf(addressField.getText().toString())));
                entity.addPart("category", new StringBody(category));
                entity.addPart("type", new StringBody(rampType));
                entity.addPart("comment", new StringBody(String.valueOf(inDetailField.getText().toString())));
                //entity.addPart("anon", new StringBody(String.valueOf(switchAnon.getText().toString())));
                entity.addPart("Image", new FileBody(imageFile));
                entity.addPart("Longitude", new StringBody(String.valueOf(longitude)));
                entity.addPart("Latitude", new StringBody(String.valueOf(latitude)));

                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                ResponseHandler<String> handler = new BasicResponseHandler();
                responseStr = handler.handleResponse(response);
                /*
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();

                pairs.add(new BasicNameValuePair("address", addressField.getText().toString()));
                pairs.add(new BasicNameValuePair("category", String.valueOf(category)));
                pairs.add(new BasicNameValuePair("type", String.valueOf(types)));
                pairs.add(new BasicNameValuePair("comment", String.valueOf(inDetailField.getText().toString())));
                pairs.add(new BasicNameValuePair("anon", String.valueOf(switchAnon.getText().toString())));
                //pairs.add(new BasicNameValuePair("Image", (imageFile));
                pairs.add(new BasicNameValuePair("Longitude", String.valueOf(longitude)));
                pairs.add(new BasicNameValuePair("Latitude", String.valueOf(latitude)));

                httppost.setEntity(new UrlEncodedFormEntity(pairs));*/
            }
            catch (Exception ex)
            {
                System.out.println("ooops aidigidai");
            }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cancel_add) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            try {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imgPreview.setImageBitmap(imageBitmap);
                File filesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                imageFile = new File(filesDir, imageFileName + ".jpg");
                OutputStream os;

                try {
                    os = new FileOutputStream(imageFile);
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);}
            } catch (NullPointerException e) {e.printStackTrace();}
        }
        else if(requestCode == MAP_PLACE_CHOOSE_REQUEST_CODE)
        {
            if (data == null) {Log.d(USER_SERVICE, "RETURN");return;}
            Bundle extras = data.getExtras();
            //longitude = extras.getDouble("lon");
           // latitude = extras.getDouble("lat");
            //mMapController.getDownloader().getGeoCode(this, new GeoPoint(latitude, longitude));
        }

    }

    @Override
    public boolean onFinishGeoCode(final GeoCode geoCode) {
        if (geoCode != null){
            mMapController.getMapView().post(new Runnable() {
                @Override
                public void run() {
                    addressField.setText(geoCode.getDisplayName());
                }
            });
        }
        return false;
    }
    private double longitude;
    private double latitude;
    private Location myloc;


    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            longitude = loc.getLongitude();
            latitude = loc.getLatitude();
            String s = longitude + "\n" + latitude;
            System.out.println("MYLOC" + loc.toString());
            myloc = loc;

            //address
            String finalUrl = urlYandex + longitude +","+ latitude + "&lang=en-US";
            xmlp = new XMLParser(finalUrl);
            xmlp.fetchXML();
            while(xmlp.parsingComplete);
            System.out.println("yahoo");
            System.out.println(longitude);
            System.out.println(latitude);
            try {
                //for city ---->  xmlp.getyandexCityName().toString() + " " +
                addressField.setText( xmlp.getyandexStreetName().toString() + " " + xmlp.getyandexNumberName().toString());
            }
            catch (Exception ex)
            {
                System.out.println("error");

            }

            System.out.println(xmlp.getyandexCityName().toString());
            System.out.println(xmlp.getyandexStreetName().toString());
            System.out.println(xmlp.getyandexNumberName().toString());

        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }


    public void defaultButton(){
        ImageButton button1 = (ImageButton) findViewById(R.id.greenButton);
        button1.setImageResource(R.drawable.ramp_green);

        ImageButton button2 = (ImageButton) findViewById(R.id.yellowButton);
        button2.setImageResource(R.drawable.ramp_yellow);

        ImageButton button3 = (ImageButton) findViewById(R.id.redButton);
        button3.setImageResource(R.drawable.ramp_red);

    }


    public void defaultButtonCat(){
        ImageButton button1 = (ImageButton) findViewById(R.id.icon_cat_1);
        button1.setImageResource(R.drawable.icon_baby_1);
        ImageButton button2 = (ImageButton) findViewById(R.id.icon_cat_2);
        button2.setImageResource(R.drawable.icon_cafe_1);
        ImageButton button3 = (ImageButton) findViewById(R.id.icon_cat_3);
        button3.setImageResource(R.drawable.icon_edu_1);
        ImageButton button4 = (ImageButton) findViewById(R.id.icon_cat_4);
        button4.setImageResource(R.drawable.icon_flat_1);
        ImageButton button5 = (ImageButton) findViewById(R.id.icon_cat_5);
        button5.setImageResource(R.drawable.icon_med_1);
        ImageButton button6 = (ImageButton) findViewById(R.id.icon_cat_6);
        button6.setImageResource(R.drawable.icon_shop_1);

    }
}
