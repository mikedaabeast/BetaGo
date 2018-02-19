package sample;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int WIDTH  = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;
    private static final int HEIGHT = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;

    private Game game;

    private Label label;
    private Canvas canvas;
    private GraphicsContext gc;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        game = new Game();

        StackPane root = new StackPane();
        root.setPrefSize(WIDTH, HEIGHT);
        GridPane gridPane = createContentPane();
        root.getChildren().add(gridPane);
        primaryStage.setTitle("Go");
        primaryStage.setScene(new Scene(root, WIDTH * 0.90, HEIGHT ));
        primaryStage.show();
    }

    private void attemptToPlaceStone(double x, double y) {
        int row = (int)y;
        int col = (int)x;

        if(game.isValidMove(row, col)) {
            game.playerMove(row, col);
            game.nextTurn();
            drawBoardState();
        }
    }

    private void drawBoardState() {
        drawBackground();

        Stone[][] stones = game.getBoard().getBoard();
        for (int i = 0; i < stones.length; i++)
            for (int j = 0; j < stones.length; j++)
                if(stones[i][j] != null)
                    drawCircle(j, i, stones[i][j].getColor());

        updateLabel();
    }

    private void updateLabel() {
        label.setFont(Font.font ("Verdana", 14));
        label.setTextFill(Color.BLACK);
        label.setText(game.getCurrentPlayer().getName() + "'s turn\nP1: " + game.getPlayers()[0].numStonesCaptured() + " P2: " + game.getPlayers()[1].numStonesCaptured());
    }

    private void drawCircle(double x, double y, Paint p) {
        int xOffset = (int)canvas.getWidth() / game.getBoardSize();
        int yOffset = (int)canvas.getHeight() / game.getBoardSize();

        gc.setFill(p);
        gc.fillOval(x * xOffset, y * yOffset, xOffset, yOffset);
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);
        gc.strokeOval(x * xOffset, y * yOffset, xOffset, yOffset);
    }

    private Pair<Double, Double> boardClickedAt(double x , double y) {
        x = (int)(x / (canvas.getWidth() / game.getBoardSize()));
        y = (int)(y / (canvas.getHeight() / game.getBoardSize()));
        return new Pair<>(x, y);
    }

    private void drawGridLines(int size) {
        int xOffset = (int)(canvas.getWidth() / 1.0 / size);
        int yOffset = (int)(canvas.getHeight() / 1.0 / size);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);

        for (int i = 0; i < size; i++) {
            gc.strokeLine(i * xOffset + xOffset / 2,  yOffset / 2, i * xOffset + xOffset / 2, canvas.getHeight() - yOffset / 2 - 1);
            gc.strokeLine(xOffset / 2, i * yOffset + yOffset / 2, canvas.getWidth() - xOffset / 2 - 1, i * yOffset + yOffset / 2);
        }
    }

    private void drawBackground() {
        gc.setFill(new Color((double)186/255, (double)174/255, (double)125/255, (double)255/255));
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        drawGridLines(game.getBoardSize());
    }

    // GUI COMPONENTS AND EVENT LISTENERS
    private GridPane createContentPane() {
        canvas = new Canvas(WIDTH * 0.90, HEIGHT * 0.90);
        gc = canvas.getGraphicsContext2D();

        canvas.setOnMouseClicked(event -> {
            double x = event.getX(), y = event.getY();
            Pair<Double, Double> position = boardClickedAt(x, y);
            attemptToPlaceStone(position.getKey(), position.getValue());
        });

        canvas.setOnMouseMoved(event -> {
            Pair<Double, Double> position = boardClickedAt(event.getX(), event.getY());
            double x = position.getKey(), y = position.getValue();
            int row = (int)y, col = (int)x;
            drawBoardState();
            if(game.isValidMove(row, col))
                drawCircle(col, row,game.getCurrentPlayer().getColor());
        });

        drawBackground();

        Button passTurnBtn = new Button("Pass turn");
        passTurnBtn.setOnAction(e -> System.out.println("Keks"));

        Button newGameBtn = new Button("New Game");
        newGameBtn.setOnAction(e -> {
            game.restartGame();
            drawBoardState();
        });

        Button exitBtn = new Button("Exit");
        exitBtn.setOnAction(e -> System.exit(0));

        label = new Label("");
        updateLabel();

        GridPane topPane = new GridPane();
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100 / 4);
            topPane.getColumnConstraints().add(col);
        }
        topPane.add(label, 0, 0);
        topPane.add(passTurnBtn, 1, 0);
        topPane.add(newGameBtn, 2, 0);
        topPane.add(exitBtn, 3, 0);

        GridPane gridpane = new GridPane();
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(10);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(90);
        gridpane.getRowConstraints().addAll(row1, row2);

        gridpane.add(topPane, 0, 0);
        gridpane.add(canvas, 0, 1);

        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setHalignment(passTurnBtn, HPos.CENTER);
        GridPane.setHalignment(newGameBtn, HPos.CENTER);
        GridPane.setHalignment(exitBtn, HPos.CENTER);
        GridPane.setHalignment(canvas, HPos.CENTER);
        GridPane.setHalignment(topPane, HPos.CENTER);

        return gridpane;
    }

}