import android.graphics.drawable.Drawable;
import android.media.Image;
import java.util.jar.Attributes;

public class AppObject {
    private  String name,
                    packageName;
    private Drawable image;
    public AppObject(String packageName, String name, Drawable image)
    {this.name = name;
    this.packageName = packageName;
    this.image = image;
    }

    public String getPackageName(){return packageName;}
    public String getName(){return name;}
    public Drawable getImage(){return image;}

}
