package pandusland;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kexit.kz.pandusland.R;

import java.util.List;


public class Profile extends Fragment {
    public ListView list;
    public PandusAdapter adapter;
    public List<Pandus> objlist;
    boolean check;

    private OnFragmentInteractionListener mListener;

    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ((TextView)v.findViewById(R.id.profileName)).setText(Pandusland.me.getSname());
        ((TextView)v.findViewById(R.id.profileEmail)).setText(Pandusland.me.getEmail());
        if(Pandusland.me.getRating() == 0)
        {
            ((RatingBar)v.findViewById(R.id.ratingBar)).setRating(0);
        }else
        if(Pandusland.me.getRating() < 10)
        {
            ((RatingBar)v.findViewById(R.id.ratingBar)).setRating(1);

        }else
        if(Pandusland.me.getRating() < 20)
        {
            ((RatingBar)v.findViewById(R.id.ratingBar)).setRating(2);

        }else
        if(Pandusland.me.getRating() < 30)
        {
            ((RatingBar)v.findViewById(R.id.ratingBar)).setRating(3);

        }else
        if(Pandusland.me.getRating() < 40)
        {
            ((RatingBar)v.findViewById(R.id.ratingBar)).setRating(4);

        }else
        if(Pandusland.me.getRating() < 50)
        {
            ((RatingBar)v.findViewById(R.id.ratingBar)).setRating(5);

        }

        list = (ListView) v.findViewById(R.id.profileListItem);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent().setClass(getActivity(), Item.class);

                intent.putExtra(Item.ID_INFO, objlist.get(position).id+"");
                intent.putExtra(Item.ID_NAME, objlist.get(position).address);
                intent.putExtra(Item.ID_TYPE, objlist.get(position).type);

                startActivity(intent);
            }
        });

        mListener.loadProfileListView();

        return v;
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

    public void onClick(View v) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



}
