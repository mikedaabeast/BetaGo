package sample;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import sample.Model.Game;
import sample.Model.Utility.Pair;
import sample.Model.Stone;

public class Main extends Application {

    private static final int WIDTH  = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;
    private static final int HEIGHT = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;

    private Game game;
    private GamePlayScreen gamePlayScreen;
    private HomeScreen homeScreen;
    private StackPane root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        game = new Game();
        gamePlayScreen = new GamePlayScreen();
        homeScreen = new HomeScreen();

        root = new StackPane();
        root.setPrefSize(WIDTH, HEIGHT);
        root.getStylesheets().add("sample/stylesheet.css");
        displayScreen(gamePlayScreen);

        primaryStage.setTitle("Go");
        primaryStage.setScene(new Scene(root, WIDTH * (1 / 0.80), HEIGHT ));
        primaryStage.show();

    }

    private void displayScreen(Node screen) {
        root.getChildren().removeAll(root.getChildren());
        root.getChildren().add(screen);
    }

    class GamePlayScreen extends GridPane {

        private BoardView boardView;
        private SidePanel controlPanel;

        GamePlayScreen() {
            boardView = new BoardView(WIDTH, HEIGHT);
            controlPanel = new SidePanel();

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(80);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(20);
            getColumnConstraints().addAll(col1, col2);

            add(boardView, 0, 0);
            add(controlPanel, 1, 0);
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
            getStyleClass().add("boardView");
            gc = getGraphicsContext2D();

            setOnMouseClicked(event -> {
                double x = event.getX(), y = event.getY();
                Pair<Double, Double> position = boardClickedAt(x, y);
                attemptToPlaceStone(position.getKey(), position.getValue());
            });

            setOnMouseExited(event -> drawBoardState());

            setOnMouseMoved(event -> {
                Pair<Double, Double> position = boardClickedAt(event.getX(), event.getY());
                double x = position.getKey(), y = position.getValue();
                int row = (int)y, col = (int)x;
                drawBoardState();
                if(game.isValidMove(row, col))
                    drawCircle(col, row, game.getCurrentPlayer().getColor());
            });

            drawBackground();
        }

        private void attemptToPlaceStone(double x, double y) {
            int row = (int)y, col = (int)x;

            if(game.isValidMove(row, col)) {
                game.playerMove(row, col);
                game.nextTurn();

                gamePlayScreen.updateEverything();
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

        private void drawCircle(double x, double y, Paint p) {
            int xOffset = (int)getWidth() / game.getBoardSize();
            int yOffset = (int)getHeight() / game.getBoardSize();

            gc.setFill(p);
            gc.fillOval(x * xOffset, y * yOffset, xOffset, yOffset);
            gc.setLineWidth(1);
            gc.setStroke(Color.GRAY);
            gc.strokeOval(x * xOffset, y * yOffset, xOffset, yOffset);
        }

        private Pair<Double, Double> boardClickedAt(double x , double y) {
            x = (int)(x / (getWidth()  / game.getBoardSize()));
            y = (int)(y / (getHeight() / game.getBoardSize()));
            return new Pair<>(x, y);
        }

        private void drawGridLines(int size) {
            int xOffset = (int)(getWidth()  / 1.0 / size);
            int yOffset = (int)(getHeight() / 1.0 / size);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.5);

            for (int i = 0; i < size; i++) {
                gc.strokeLine(i * xOffset + xOffset / 2,  yOffset / 2, i * xOffset + xOffset / 2, getHeight() - yOffset / 2 - 1);
                gc.strokeLine(xOffset / 2, i * yOffset + yOffset / 2, getWidth() - xOffset / 2 - 1, i * yOffset + yOffset / 2);
            }
        }

        Image image = new Image(Main.class.getResourceAsStream("../images/background.jpg"));
        private void drawBackground() {
            gc.drawImage(image, 0, 0, WIDTH, HEIGHT);
            drawGridLines(game.getBoardSize());
        }
    }

    class SidePanel extends VBox {
        private Label label;

         SidePanel() {
            getStyleClass().add("sidePanel");

            Button homeScreenBtn = new Button("Home Screen");
            homeScreenBtn.setOnAction(e -> displayScreen(homeScreen));

            Button exitBtn = new Button("Quit");
            exitBtn.setOnAction(e -> System.exit(0));

            Button passTurnBtn = new Button("Pass turn");
            passTurnBtn.setOnAction(e -> {
                game.passTurn();
                gamePlayScreen.updateEverything();
            });

            Button newGameBtn = new Button("New Game");
            newGameBtn.setOnAction(e -> {
                game.restartGame();
                gamePlayScreen.updateEverything();
            });

            label = new Label("");
            label.getStyleClass().add("label");
            label.setPrefWidth(WIDTH * 0.20);
            updateLabel();

            passTurnBtn.setMinWidth(WIDTH * 0.20);
            newGameBtn.setMinWidth(WIDTH * 0.20);
            homeScreenBtn.setMinWidth(WIDTH * 0.20);
            exitBtn.setMinWidth(WIDTH * 0.20);

            passTurnBtn.setMinHeight(HEIGHT * 0.06);
            newGameBtn.setMinHeight(HEIGHT * 0.06);
            homeScreenBtn.setMinHeight(HEIGHT * 0.06);
            exitBtn.setMinHeight(HEIGHT * 0.06);

            getChildren().addAll(label, passTurnBtn, newGameBtn, homeScreenBtn, exitBtn);
            setSpacing(5);
            setAlignment(Pos.CENTER);
        }

        private void updateLabel() {
            label.setText(game.getCurrentPlayer().getName() + "'s turn\nP1: " + game.getPlayers()[0].numStonesCaptured() + " P2: " + game.getPlayers()[1].numStonesCaptured());
         }
    }

    class HomeScreen extends VBox {
        HomeScreen() {
            getStyleClass().add("homeScreen");

            Button newGameBtn = new Button("Play Game");
            newGameBtn.setOnAction(e -> displayScreen(gamePlayScreen));
            newGameBtn.setMinWidth(WIDTH * .5);
            newGameBtn.getStyleClass().add("homeScreenButton");


            Button exitBtn = new Button("Quit");
            exitBtn.setOnAction(e -> System.exit(0));
            exitBtn.setMinWidth(WIDTH * .5);
            exitBtn.getStyleClass().add("homeScreenButton");

            getChildren().addAll(newGameBtn, exitBtn);
            setSpacing(5);
            setAlignment(Pos.CENTER);
        }
    }

}