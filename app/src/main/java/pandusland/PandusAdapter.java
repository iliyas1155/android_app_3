package pandusland;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kexit.kz.pandusland.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.List;

public class PandusAdapter  extends ArrayAdapter {
    private List<Pandus> obj;
    private Context context;
    private int resource;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public PandusAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.obj = (List<Pandus>) objects;

        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "PandusCache");
        imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .discCache(new UnlimitedDiscCache(cacheDir))
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        imageLoader.init(config);
        options = new DisplayImageOptions.Builder()
                //.showStubImage(R.drawable.theater)
                .cacheInMemory()
                .cacheOnDisc()
                .displayer(new RoundedBitmapDisplayer(0))
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(resource, parent, false);
        ((TextView) view.findViewById(R.id.title)).setText(obj.get(position).address);
        int dist = (int)obj.get(position).distance / 100000;
        ((TextView) view.findViewById(R.id.subtitle)).setText(obj.get(position).category + "\n Distance: " + dist + " kilometers");
        imageLoader.displayImage(obj.get(position).imagepath,  ((ImageView) view.findViewById(R.id.image)),options);
        return view;
    }
}