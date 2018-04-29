package pandusland;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.kexit.kz.pandusland.R;

import java.util.List;

public class CloseList extends Fragment {
    private OnFragmentInteractionListener mListener;
    public ListView list;
    public PandusAdapter adapter;
    public List<Pandus> objlist;
    boolean check;
    Typeface typeface;


    //public MembersAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_listview, container, false);
        list = (ListView) v.findViewById(R.id.listView);
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
}
