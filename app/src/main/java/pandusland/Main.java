package pandusland;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kexit.kz.pandusland.R;


public class Main extends Fragment {

    ImageButton mainButton;
    SharedPreferences sPref;
    public static final String IS_FISRT = "isFirst";
    TextView countStat;
    TextView countStat2;
    private OnFragmentInteractionListener mListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setHasOptionsMenu(true);
        if (getArguments() != null) {
            try {
//				String value = getArguments().getString("key");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main, null);

        LocationManager locationManager = (LocationManager)
                (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        myloc = new Location("MyLoc");
        myloc.setLatitude(latitude);
        myloc.setLongitude(longitude);
        countStat = (TextView) v.findViewById(R.id.redRamp);
        countStat2 = (TextView) v.findViewById(R.id.repRamp);

        try {
            Pandusland.isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch (Exception ex)
        {
            System.out.println("oops");
        }


        ImageButton flashButtonOn = (ImageButton) v.findViewById(R.id.mainButton);
        if (Pandusland.isGPSEnabled) {
            flashButtonOn.setImageResource(R.drawable.gps_main1);

            (v.findViewById(R.id.mainButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent();
                    intent.setClass(getActivity(), CreateItem.class);

                    getActivity().startActivity(intent);
                }
            });

        } else {
            flashButtonOn.setImageResource(R.drawable.gps_main);

            (v.findViewById(R.id.mainButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new  Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(intent);
                }
            });

        }
//		getActivity().setProgressBarIndeterminateVisibility(false);

        //mainButton = (ImageButton) v.findViewById(R.id.mainButton);


        mListener.loadMainStats();


        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



    View.OnClickListener mainBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Intent intent = new Intent();
            intent.setClass(getActivity(), CreateItem.class);

            getActivity().startActivity(intent);
        }
    };

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



}
