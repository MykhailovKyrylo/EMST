import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Created by pasha on 5/8/2017.
 */
public final class ScreenProperties {
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static double screenWidth = screenSize.getWidth();
    private static double screenHeight = screenSize.getHeight();

    private ScreenProperties(){}

    public static double getWidth(){
        return screenWidth;
    }

    public static double getHeight(){
        return screenHeight;
    }
}