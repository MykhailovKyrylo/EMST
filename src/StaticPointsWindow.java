import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Vector;

public class StaticPointsWindow {
    private Stage stage;
    private MainPage mainPage;
    private Scene scene;
    private Pane root;
    private Group group;
    private int pointsNumber;
    private static double screenWidth = 1000;
    private static double screenHeight = 700;
    private Vector<Circle> points = new Vector<>();
    private Vector<Line> lines = new Vector<>();

    public StaticPointsWindow(Stage stage, MainPage mainPage)
    {
        this.stage = stage;
        this.mainPage = mainPage;

        createRoot();

        scene = new Scene(root, screenWidth, screenHeight, Color.WHITE);

        setScene();
    }

    private void setScene()
    {
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    private void createRoot()
    {
        VBox vBox = new VBox();

        HBox hBox = new HBox();
        hBox.getChildren().addAll(createButtonBack(), createButtonRun(), createButtonClear(),
                createButtonGenerate(), new Separator(), createTextField());

        vBox.getChildren().addAll(hBox, createLayout());

        root = vBox;
    }

    private Group createLayout()
    {
        group = new Group();

        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(screenWidth);
        rectangle.setHeight(screenHeight);
        rectangle.setFill(Paint.valueOf("White"));

        group.getChildren().addAll(rectangle);

        rectangle.setOnMouseClicked(event -> {
            double x = event.getSceneX();
            double y = event.getSceneY();

            Circle circle = new Circle(x, y - 50, 1.0);
            group.getChildren().addAll(circle);
            points.add(circle);
        });

        return group;
    }

    private Button createButtonBack()
    {
        Button btn = new javafx.scene.control.Button("← Back");

        btn.setFont(Font.font("Verdana", FontPosture.REGULAR, 25));

        btn.setOnAction(event -> {
            mainPage.setScene();
        });

        return btn;
    }

    private Button createButtonRun()
    {
        Button btn = new javafx.scene.control.Button("Run");

        btn.setFont(Font.font("Verdana", FontPosture.REGULAR, 25));

        btn.setOnAction(event -> {

            Solver solver = new Solver();

            solver.solve(points);

            for (Line line: lines)
                group.getChildren().remove(line);

            for (Line line: solver.getEMST()) {
                line.setStroke(Color.RED);
                line.setStrokeWidth(4);
                group.getChildren().add(line);
            }

            for (Line line: solver.getTriangulationLines()) {
                group.getChildren().add(line);
            }

            System.out.println(solver.getEMST().size());

        });

        return btn;
    }

    private void clear()
    {
        Node rectangle = group.getChildren().get(0);
        group.getChildren().clear();
        group.getChildren().add(rectangle);

        points.clear();
        lines.clear();
    }

    private Button createButtonClear()
    {
        Button btn = new javafx.scene.control.Button("Clear");

        btn.setFont(Font.font("Verdana", FontPosture.REGULAR, 25));

        btn.setOnAction(event -> {
            clear();
        });

        return btn;
    }

    private Button createButtonGenerate()
    {
        Button btn = new javafx.scene.control.Button("Generate");

        btn.setFont(Font.font("Verdana", FontPosture.REGULAR, 25));

        btn.setOnAction(event -> {
            clear();

            for (int i = 0; i < pointsNumber; ++i){
                double x = Math.round( Math.random() * screenWidth );
                double y =  Math.round( Math.random() * ( screenHeight - 50) );
                Circle circle = new Circle(x, y, 1);

                System.out.println(x + " " + y);

                points.add(circle);
                group.getChildren().add(circle);
            }
        });

        return btn;
    }

    private HBox createTextField()
    {
        HBox hBox = new HBox();
        hBox.setSpacing(15);
        hBox.setAlignment(Pos.CENTER);

        Text text = new Text("Number of points: ");
        text.setFont(Font.font("Verdana", FontPosture.REGULAR, 25));
        hBox.getChildren().add(text);

        TextField textField = new TextField();
        textField.setOnKeyReleased(event -> {
            String value = textField.getText();

            try {
                pointsNumber = Integer.parseInt(value);
            }
            catch (Exception exp)
            {
                pointsNumber = 0;
            }
        });

        hBox.getChildren().add(textField);

        return hBox;
    }
}
