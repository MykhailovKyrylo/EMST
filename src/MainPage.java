import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

public class MainPage{
    private Stage stage;
    private Scene scene;
    private Pane root;

    public MainPage(Stage stage)
    {
        this.stage = stage;

        createRoot();

        scene = new Scene(root, ScreenProperties.getWidth() * 0.75, ScreenProperties.getHeight() * 0.75, Color.WHITE);

        setScene();
    }

    public void setScene()
    {
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    private void createRoot()
    {
        StackPane stackPane = new StackPane();

        VBox vBox = new VBox();
        vBox.getChildren().addAll(createButton1(), createButton2());
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(15);

        stackPane.getChildren().addAll(vBox);
        stackPane.setAlignment(Pos.CENTER);

        root = stackPane;
    }

    private Button createButton1()
    {
        Button btn = new Button("Set points manually");

        btn.setFont(Font.font("Verdana", FontPosture.REGULAR, 25));

        btn.setOnAction(event -> {
            new InteractiveWindow(stage, this);
        });

        return btn;
    }

    private Button createButton2()
    {
        Button btn = new Button("Generate points");

        btn.setFont(Font.font("Verdana", FontPosture.REGULAR, 25));

        btn.setOnAction(event -> {
            new StaticPointsWindow(stage, this);
        });

        return btn;
    }
}
