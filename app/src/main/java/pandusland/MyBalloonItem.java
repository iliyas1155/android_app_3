package pandusland;
import android.content.Context;

import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class MyBalloonItem extends BalloonItem {
	public String myValue = null ;
	public String type = "";
	public MyBalloonItem(Context arg0, GeoPoint arg1) {
		super(arg0, arg1);
	}

	public void setMyValue(String str){
		this.myValue = str;
	}
	
	public String getMyValue(){
		return this.myValue;
	}

	public void setType(String str){
		this.type = str;
	}
	
	public String getType(){
		return this.type;
	}


    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
