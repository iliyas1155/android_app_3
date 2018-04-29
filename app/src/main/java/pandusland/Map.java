package pandusland;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.map.GeoCode;
import ru.yandex.yandexmapkit.map.GeoCodeListener;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.overlay.balloon.OnBalloonListener;
import ru.yandex.yandexmapkit.overlay.location.MyLocationItem;
import ru.yandex.yandexmapkit.overlay.location.OnMyLocationListener;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class Map extends Fragment implements OnMyLocationListener, GeoCodeListener, OnBalloonListener{
    MapController mMapController;
    OverlayManager mOverlayManager;
    public String responseStr = "";

    public Map() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_map,
                null);
//		getActivity().setProgressBarIndeterminateVisibility(true);

        final MapView mapView = (MapView) v.findViewById(R.id.map);
        mapView.showJamsButton(false);
        mMapController = mapView.getMapController();
        mOverlayManager = mMapController.getOverlayManager();
        mOverlayManager.getMyLocation().setEnabled(true);
        mOverlayManager.getMyLocation().addMyLocationListener(this);
        mMapController.setPositionAnimationTo(new GeoPoint(43.235002,
                76.909874));
        mMapController.setZoomCurrent(30);
        if (getArguments() != null) {
            //
            try {
//				String value = getArguments().getString("key");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new GatherData().execute();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//	   inflater.inflate(R.menu.demo, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            mMapController.getDownloader().getGeoCode(this, new GeoPoint(
                    mOverlayManager.getMyLocation().getMyLocationItem().getGeoPoint().getLat(),
                    mOverlayManager.getMyLocation().getMyLocationItem().getGeoPoint().getLon()));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onMyLocationChange(MyLocationItem myLocationItem) {

    }

    @Override
    public boolean onFinishGeoCode(final GeoCode geoCode) {
        if (geoCode != null){
            mMapController.getMapView().post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder dialog = new AlertDialog.Builder( mMapController.getContext());
                    dialog.setTitle(geoCode.getDisplayName());
                    dialog.show();
                }
            });
        }
        return true;
    }

    private class GatherData extends AsyncTask<String, Integer, Double> {
        @Override
        protected void onPreExecute() {
            getActivity().setProgressBarIndeterminateVisibility(true);
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

                    Double lat = Double.parseDouble(explrObject.get("latitude").toString());
                    Double lon = Double.parseDouble(explrObject.get("longitude").toString());
                    String txt = explrObject.get("address").toString();
                    String status = explrObject.get("type").toString();
                    String id= explrObject.get("id").toString();
                    AddItem(lat, lon, txt, status, id);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getActivity().setProgressBarIndeterminateVisibility(false);
        }

        protected void onProgressUpdate(Integer... progress){
        }
    }

    public void AddItem(double latitude, double longitude, String text, String status, String id){
        Resources res = getResources();
        Overlay overlay = new Overlay(mMapController);
        Drawable icon = res.getDrawable(R.drawable.point_marker_green);
        if(status.equals("not available")){
            icon = res.getDrawable(R.drawable.point_marker_red);
        }else if(status.equals("good")){
            icon = res.getDrawable(R.drawable.point_marker_green);
        }else if(status.equals("requires repair")){
            icon = res.getDrawable(R.drawable.point_marker_yellow);
        }
        OverlayItem yandex = new OverlayItem(new GeoPoint(latitude , longitude), icon);
        BalloonItem balloonYandex = new MyBalloonItem(getActivity(), yandex.getGeoPoint());
        balloonYandex.setText(text);
        balloonYandex.setOnBalloonListener(this);
        ((MyBalloonItem) balloonYandex).setMyValue(id);
        ((MyBalloonItem) balloonYandex).setType(status);


        yandex.setBalloonItem(balloonYandex);
        overlay.addOverlayItem(yandex);
        mOverlayManager.addOverlay(overlay);
    }

    public void GetData() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet("http://pandusland.byethost16.com/list1.php");
        getRequest.setHeader("Accept", "application/json");

        try {
            HttpResponse response = httpclient.execute(getRequest);
            ResponseHandler<String> handler = new BasicResponseHandler();

            responseStr = handler.handleResponse(response);
//    		getActivity().setProgressBarIndeterminateVisibility(false);

        } catch (Exception e) {}
    }


    @Override
    public void onBalloonViewClick(BalloonItem balloonItem, View view)  {
        Intent intent = new Intent().setClass(getActivity(), Item.class);
        if (balloonItem.getText() != null){
            intent.putExtra(Item.ID_INFO, ((MyBalloonItem) balloonItem).getMyValue());
            intent.putExtra(Item.ID_NAME, ((MyBalloonItem) balloonItem).getText());
            intent.putExtra(Item.ID_TYPE, ((MyBalloonItem) balloonItem).getType());

        }else{
            intent.putExtra(Item.ID_INFO, "qq qqqqqqq");
        }
        startActivity(intent);
    }

    @Override
    public void onBalloonShow(BalloonItem balloonItem) {}

    @Override
    public void onBalloonHide(BalloonItem balloonItem) {}

    @Override
    public void onBalloonAnimationStart(BalloonItem balloonItem) {}

    @Override
    public void onBalloonAnimationEnd(BalloonItem balloonItem) {}
}
