import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
    public static void main(String[] args) { launch(args); }

    public void start(Stage stage) throws Exception{
        stage.setTitle("Maximal circle in the Convex Hull");

        new MainPage(stage);
    }

}
