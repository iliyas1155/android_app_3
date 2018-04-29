package pandusland;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kexit.kz.pandusland.R;

import java.util.List;

public class MenuAdapter extends ArrayAdapter {
    private List<Menu> obj;
    private Context context;
    private int resource;
    Typeface typeface;

    public MenuAdapter(Context context, int resource, List objects, Typeface typeface) {
        super(context, resource, objects);
        this.context = context;
        this.typeface = typeface;
        this.resource = resource;
        this.obj = (List<Menu>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(resource, parent, false);
        ((TextView) view.findViewById(R.id.title)).setText(obj.get(position).name);
        //((TextView) view.findViewById(R.id.title)).setTypeface(typeface);
        //IF IMAGE HERE
        if(obj.get(position).image != null){
            //((ImageView) view.findViewById(R.id.image)).setImageBitmap(obj.get(position).getImage());
            if(obj.get(position).image == "Main")
            {
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.menu_main1);
            }else
            if(obj.get(position).image == "Near")
            {
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.menu_near1);
            }else
            if(obj.get(position).image == "Map")
            {
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.menu_map1);
            }else
            if(obj.get(position).image == "Profile")
            {
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.menu_profile1);
            }else
            if(obj.get(position).image == "About")
            {
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.menu_about1);
            }

        }
        return view;
    }
}