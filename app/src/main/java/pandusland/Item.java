package pandusland;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kexit.kz.pandusland.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class Item extends ActionBarActivity {

    public final static String ID_INFO =  "info";
    public final static String ID_NAME =  "name";
    public final static String ID_TYPE =  "good";

    TextView address,author,category,comment, type, commentLine;
    Button addCommentBtn;
    ImageView img;
    EditText txtUrl;
    RatingBar bar ;

    public String responseStr = "";
    public String id = "";
    public String imageUrl = "";
    public String info = "bbbbb";
    public String lat = "0.0";
    public String lon = "0.0";
    public boolean isLogged = false;
    public AlertDialog dialog = null;
    String status = "";

    ListView lvMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        info = getIntent().getStringExtra(ID_NAME);
        id = getIntent().getStringExtra(ID_INFO);
        status = getIntent().getStringExtra(ID_TYPE);
        setContentView(R.layout.activity_item);
        new DataMata().execute();
        txtUrl = new EditText(this);
        bar = new RatingBar(this);
        address = (TextView) findViewById(R.id.addresItem);
        img = (ImageView) findViewById(R.id.imgItem);
        author = (TextView) findViewById(R.id.itemAuthor);
        category = (TextView) findViewById(R.id.itemCategory);
        type = (TextView) findViewById(R.id.itemTypeText);
        comment = (TextView) findViewById(R.id.commentItem);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        //menu_show_on_map
        return super.onCreateOptionsMenu(menu);
    }



    private class DataMata extends AsyncTask<String, Integer, Double>{

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
            super.onPreExecute();
        }

        @Override
        protected Double doInBackground(String... params) {
            GetData();
            return null;
        }

        protected void onPostExecute(Double result){
            try {
                JSONArray array = new JSONArray(responseStr);
                for(int i = 0; i < array.length(); i++){
                    JSONObject explrObject = array.getJSONObject(i);
                    address.setText(explrObject.get("address").toString());
//  				  img.loadUrl("http://kartadostupnosti.org/" + explrObject.get("thumbnail").toString());
                    imageUrl = explrObject.get("imagepath").toString();
                    //name.setText(explrObject.get("username").toString());
                    comment.setText(explrObject.get("comment").toString());
                    category.setText(explrObject.get("category").toString());
                    type.setText(explrObject.get("type").toString());
                    author.setText(explrObject.get("username").toString());
                    lat = explrObject.get("latitude").toString();
                    lon = explrObject.get("longitude").toString();

                    if(category.getText().toString().equals("For Kids")){
                        ImageButton button = (ImageButton) findViewById(R.id.itemCat);
                        button.setImageResource(R.drawable.icon_baby_1);
                    }else if(category.getText().toString().equals("Cafes and Restaurants")){
                        ImageButton button = (ImageButton) findViewById(R.id.itemCat);
                        button.setImageResource(R.drawable.icon_cafe_1);
                    }else if(category.getText().toString().equals("Education")){
                        ImageButton button = (ImageButton) findViewById(R.id.itemCat);
                        button.setImageResource(R.drawable.icon_edu_1);
                    }else if(category.getText().toString().equals("Environment")){
                        ImageButton button = (ImageButton) findViewById(R.id.itemCat);
                        button.setImageResource(R.drawable.icon_flat_1);
                    }else if(category.getText().toString().equals("Medicine")){
                        ImageButton button = (ImageButton) findViewById(R.id.itemCat);
                        button.setImageResource(R.drawable.icon_med_1);
                    }else if(category.getText().toString().equals("Shopping Malls")){
                        ImageButton button = (ImageButton) findViewById(R.id.itemCat);
                        button.setImageResource(R.drawable.icon_shop_1);
                    }


                    if(type.getText().toString().equals("good")){
                        ImageButton button = (ImageButton) findViewById(R.id.itemType);
                        button.setImageResource(R.drawable.ramp_green);
                    }else if(type.getText().toString().equals("requires repair")){
                        ImageButton button = (ImageButton) findViewById(R.id.itemType);
                        button.setImageResource(R.drawable.ramp_yellow);
                    }else if(type.getText().toString().equals("not available")){
                        ImageButton button = (ImageButton) findViewById(R.id.itemType);
                        button.setImageResource(R.drawable.ramp_red);;
                    }

                    new SetImage().execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setProgressBarIndeterminateVisibility(false);
        }

        protected void onProgressUpdate(Integer... progress){}
    }

    class SetImage extends AsyncTask<Void, Void, String> {
        Bitmap imageBitmap = null;

        @Override
        protected String doInBackground(Void... noargs) {
            URL imageURL = null;
            try {
                imageURL = new URL(imageUrl);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                imageBitmap = BitmapFactory.decodeStream(imageURL.openStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            img.setImageBitmap(imageBitmap);
        }
    }

    public void GetData() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet("http://pandusland.byethost16.com/list1.php?id="+id);
        getRequest.setHeader("Accept", "application/json");

        try {
            HttpResponse response = httpclient.execute(getRequest);
            ResponseHandler<String> handler = new BasicResponseHandler();

            responseStr = handler.handleResponse(response);
        } catch (Exception e) {}
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_show_on_map:
                Intent intent = new Intent(getApplicationContext(), ChosePosByMap.class);
                intent.putExtra("action", "show");
                intent.putExtra("title", info);
                intent.putExtra("lon", lon);
                intent.putExtra("lat", lat);
                intent.putExtra("status", status);
                startActivity(intent);
                break;
            case R.id.menu_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }*/
/*
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.addCommentBtn:
                LinearLayout.LayoutParams lpView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout linLayout = new LinearLayout(this);
                linLayout.setOrientation(LinearLayout.VERTICAL);
                final TextView help = new TextView(this);
                help.setText("bbbbbbb bbbbb");

                bar.setNumStars(5);


                txtUrl.setMinLines(2);
                txtUrl.setHint("bbbbbbbb bbb bbbbbbbbbbb");

                lpView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lpView.gravity= Gravity.CENTER;
                linLayout.addView(help, lpView);
                linLayout.addView(bar, lpView);

                lpView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                linLayout.addView(txtUrl, lpView);


                AlertDialog.Builder builder =  new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.add_comment))
                        .setView(linLayout)
                        .setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(getApplicationContext(), txtUrl.getText().toString() + "\r\n " + String.valueOf(bar.getRating()), Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ProgressBar loading = new ProgressBar(getApplicationContext());
                        dialog.setContentView(loading);
                        Boolean wantToCloseDialog = false;
                        new SendComment().execute();
                        if(wantToCloseDialog)
                            dialog.dismiss();
                    }
                });
                break;
            default:   break;
        }
    }
*/

}
