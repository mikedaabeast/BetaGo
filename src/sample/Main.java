package sample;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import sample.Model.Game;
import sample.Model.Utility.Pair;
import sample.Model.Stone;

public class Main extends Application {

    private static final int WIDTH  = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;
    private static final int HEIGHT = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;

    private Game game;
    private GamePlayView gamePlayView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        game = new Game();

        StackPane root = new StackPane();
        root.setPrefSize(WIDTH, HEIGHT);

        gamePlayView = new GamePlayView();
        root.getChildren().add(gamePlayView);

        primaryStage.setTitle("Go");
        primaryStage.setScene(new Scene(root, WIDTH * (1 / 0.70), HEIGHT ));
        primaryStage.show();
    }

    class GamePlayView extends GridPane {

        private BoardView boardView;
        private ControlPanel controlPanel;

        GamePlayView() {
            boardView = new BoardView(WIDTH, HEIGHT);
            controlPanel = new ControlPanel();

            ColumnConstraints col1 = new ColumnConstraints(70);
            col1.setPercentWidth(70);
            ColumnConstraints col2 = new ColumnConstraints(30);
            col2.setPercentWidth(30);
            getColumnConstraints().addAll(col1, col2);

            add(boardView, 0, 0);
            add(controlPanel, 1, 0);

            setStyle("-fx-background-color: azure;");
        }

        public void updateEverything() {
            boardView.drawBoardState();
            controlPanel.updateLabel();
        }

    }

    class BoardView extends Canvas {
        private GraphicsContext gc;

        BoardView(int width, int height) {
            super(width, height);
            gc = getGraphicsContext2D();

            setOnMouseClicked(event -> {
                double x = event.getX(), y = event.getY();
                Pair<Double, Double> position = boardClickedAt(x, y);
                attemptToPlaceStone(position.getKey(), position.getValue());
            });
            setOnMouseMoved(event -> {
                Pair<Double, Double> position = boardClickedAt(event.getX(), event.getY());
                double x = position.getKey(), y = position.getValue();
                int row = (int)y, col = (int)x;
                drawBoardState();
                if(game.isValidMove(row, col)) {
                    drawCircle(col, row, game.getCurrentPlayer().getColor());
                }
            });

            drawBackground();
        }

        private void attemptToPlaceStone(double x, double y) {
            int row = (int)y;
            int col = (int)x;

            if(game.isValidMove(row, col)) {
                game.playerMove(row, col);
                game.nextTurn();

                gamePlayView.updateEverything();
            }
        }

        private void drawBoardState() {
            drawBackground();

            Stone[][] stones = game.getBoard().getBoard();
            for (int i = 0; i < stones.length; i++)
                for (int j = 0; j < stones.length; j++)
                    if(stones[i][j] != null)
                        drawCircle(j, i, stones[i][j].getColor());
        }

        // final Image blackStone = new Image(Main.class.getResourceAsStream("blackStone.png"), 100, 100, true, false);
        // final Image whiteStone = new Image(Main.class.getResourceAsStream("whiteStone.png"), 100, 100, true, false);
        private void drawCircle(double x, double y, Paint p) {
            int xOffset = (int)getWidth() / game.getBoardSize();
            int yOffset = (int)getHeight() / game.getBoardSize();

            gc.setFill(p);
            gc.fillOval(x * xOffset, y * yOffset, xOffset, yOffset);
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(1);
            gc.strokeOval(x * xOffset, y * yOffset, xOffset, yOffset);
        }

        private Pair<Double, Double> boardClickedAt(double x , double y) {
            x = (int)(x / (getWidth() / game.getBoardSize()));
            y = (int)(y / (getHeight() / game.getBoardSize()));
            return new Pair<>(x, y);
        }

        private void drawGridLines(int size) {
            int xOffset = (int)(getWidth() / 1.0 / size);
            int yOffset = (int)(getHeight() / 1.0 / size);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.5);

            for (int i = 0; i < size; i++) {
                gc.strokeLine(i * xOffset + xOffset / 2,  yOffset / 2, i * xOffset + xOffset / 2, getHeight() - yOffset / 2 - 1);
                gc.strokeLine(xOffset / 2, i * yOffset + yOffset / 2, getWidth() - xOffset / 2 - 1, i * yOffset + yOffset / 2);
            }
        }

        private void drawBackground() {
            gc.setFill(new Color((double)186/255, (double)174/255, (double)125/255, (double)255/255));
            gc.fillRect(0,0, getWidth(), getHeight());
            drawGridLines(game.getBoardSize());
        }
    }

    class ControlPanel extends VBox {
        private Label label;

         ControlPanel() {
            Button passTurnBtn = new Button("Pass turn");
            passTurnBtn.setOnAction(e -> System.out.println("Keks"));

            Button newGameBtn = new Button("New Game");
            newGameBtn.setOnAction(e -> {
                game.restartGame();
                gamePlayView.updateEverything();
            });

            Button exitBtn = new Button("Exit");
            exitBtn.setOnAction(e -> System.exit(0));

            label = new Label("");
            updateLabel();
            label.setStyle("-fx-background-color: azure;");

            getChildren().addAll(label, passTurnBtn, newGameBtn, exitBtn);
            setSpacing(15);
            setAlignment(Pos.CENTER);
        }

        private void updateLabel() {
            label.setFont(Font.font ("Verdana", 14));
            label.setTextFill(Color.BLACK);
            label.setText(game.getCurrentPlayer().getName() + "'s turn\nP1: " + game.getPlayers()[0].numStonesCaptured() + " P2: " + game.getPlayers()[1].numStonesCaptured());
        }
    }

}