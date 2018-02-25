package sample;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import sample.Model.*;
import sample.Model.Utility.*;

public class Main extends Application {

    private static final int WIDTH  = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;
    private static final int HEIGHT = WIDTH;

    private Game game;
    private GameView gameView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        game = new Game();

        gameView = new GameView();
        gameView.setPrefSize(WIDTH, HEIGHT);
        gameView.getStylesheets().add("sample/stylesheet.css");
        gameView.displayGamePlayScreen();

        primaryStage.setTitle("Go");
        primaryStage.setScene(new Scene(gameView, WIDTH * (1 / 0.80), HEIGHT ));
        primaryStage.show();
    }

    class GameView extends StackPane {

        private GamePlayScreen gamePlayScreen;
        private HomeScreen homeScreen;

        GameView() {
            gamePlayScreen = new GamePlayScreen();
            homeScreen = new HomeScreen();
        }

        public void displayHomeScreen() {
            displayScreen(homeScreen);
        }

        public void displayGamePlayScreen() {
            displayScreen(gamePlayScreen);
        }

        public void updateGamePlayScreen() {
            gamePlayScreen.update();
        }

        private void displayScreen(Node screen) {
            this.getChildren().removeAll(this.getChildren());
            this.getChildren().add(screen);
        }
    }

    class GamePlayScreen extends GridPane {

        private BoardView boardView;
        private SidePanel sidePanel;

        GamePlayScreen() {
            boardView = new BoardView(WIDTH, HEIGHT);
            sidePanel = new SidePanel();

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(80);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(20);
            getColumnConstraints().addAll(col1, col2);

            add(boardView, 0, 0);
            add(sidePanel, 1, 0);
        }

        public void update() {
            boardView.drawBoardState();
            sidePanel.updateLabel();
        }

    }

    class BoardView extends Canvas {
        private GraphicsContext gc;

        BoardView(int width, int height) {
            super(width, height);
            getStyleClass().add("boardView");
            gc = getGraphicsContext2D();

            setOnMouseClicked(event -> {
                Pair<Integer, Integer> position = boardClickedAt(event.getX(), event.getY());
                int row = position.getValue(), col = position.getKey();

                attemptToPlaceStone(row, col);
            });

            setOnMouseMoved(event -> {
                Pair<Integer, Integer> position = boardClickedAt(event.getX(), event.getY());
                int row = position.getValue(), col = position.getKey();

                if(game.isValidMove(row, col)) {
                    drawBoardState();                                           // draw board on top of previously drawn valid move
                                                                                // draw valid move on top of board
                    if(game.getCurrentPlayer().getColor() == Color.WHITE)
                        drawCircle(row, col, new Color(1, 1, 1, 0.5));
                    else
                        drawCircle(row, col, new Color(0, 0, 0, 0.5));
                }
            });

            setOnMouseExited(event -> drawBoardState());

            drawBackground();
        }

        private void attemptToPlaceStone(int row, int col) {
            if(game.isValidMove(row, col)) {
                game.playerMove(row, col);
                game.nextTurn();

                gameView.updateGamePlayScreen();
            }
        }

        private void drawBoardState() {
            drawBackground();

            Stone[][] stones = game.getBoard().getBoard();
            for (int i = 0; i < stones.length; i++)
                for (int j = 0; j < stones.length; j++)
                    if(stones[i][j] != null)
                        drawCircle(i, j, stones[i][j].getColor());
        }

        private void drawCircle(double row, double col, Paint p) {
            int xOffset = (int)getWidth()  / game.getBoardSize();
            int yOffset = (int)getHeight() / game.getBoardSize();

            gc.setFill(p);
            gc.fillOval(col * xOffset, row * yOffset, xOffset, yOffset);
        }

        private Pair<Integer, Integer> boardClickedAt(double x , double y) {
            x = (x / (getWidth()  / game.getBoardSize()));
            y = (y / (getHeight() / game.getBoardSize()));
            return new Pair<>((int)x, (int)y);
        }

        private void drawGridLines(int size) {
            int xOffset = (int)(getWidth()  / 1.0 / size);
            int yOffset = (int)(getHeight() / 1.0 / size);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.5);

            for (int i = 0; i < size; i++) {
                gc.strokeLine(i * xOffset + xOffset / 2,  yOffset / 2, i * xOffset + xOffset / 2, game.getBoardSize() * yOffset - yOffset / 2 - 1);
                gc.strokeLine(xOffset / 2, i * yOffset + yOffset / 2, game.getBoardSize() * xOffset - xOffset / 2 - 1, i * yOffset + yOffset / 2);
            }
        }

        Image image = new Image(Main.class.getResourceAsStream("../images/wood1.jpg"));
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
            homeScreenBtn.setOnAction(e -> gameView.displayHomeScreen());

            Button exitBtn = new Button("Quit");
            exitBtn.setOnAction(e -> System.exit(0));

            Button passTurnBtn = new Button("Pass turn");
            passTurnBtn.setOnAction(e -> {
                game.passTurn();
                gameView.updateGamePlayScreen();
            });

            Button newGameBtn = new Button("Restart");
            newGameBtn.setOnAction(e -> {
                game.restartGame();
                gameView.updateGamePlayScreen();
            });

            label = new Label("");
            label.getStyleClass().add("label");
            label.setPrefWidth(WIDTH * 0.20);
            updateLabel();

            for(Button button : new Button[]{passTurnBtn, newGameBtn, homeScreenBtn, exitBtn}) {
                button.setMinWidth(WIDTH * 0.20);
                button.setMinHeight(HEIGHT * 0.06);
                button.getStyleClass().add("sidePanelButton");
            }

            getChildren().addAll(label, passTurnBtn, newGameBtn, homeScreenBtn, exitBtn);
        }

        private void updateLabel() {
            label.setText(game.getCurrentPlayer().getName() + "'s turn\nP1: " + game.getPlayers()[0].numStonesCaptured() + " P2: " + game.getPlayers()[1].numStonesCaptured());
         }
    }

    class HomeScreen extends VBox {

        private VBox newGameOptions;
        private Button newGameBtn;
        private Button exitBtn;

        HomeScreen() {
            getStyleClass().add("homeScreen");

            newGameBtn = new Button("New Game");
            newGameBtn.setOnAction(e -> gameView.displayGamePlayScreen());

            exitBtn = new Button("Quit");
            exitBtn.setOnAction(e -> System.exit(0));

            Button vsHumanBtn = new Button("Human vs Human");
            vsHumanBtn.setOnAction(e -> gameView.displayGamePlayScreen());
            vsHumanBtn.setMinWidth(WIDTH * .5);

            Button vsComputerBtn = new Button("Human vs Computer");
            vsComputerBtn.setOnAction(e -> gameView.displayGamePlayScreen());
            vsComputerBtn.setMinWidth(WIDTH * .5);

            newGameOptions = new VBox();
            newGameOptions.getStyleClass().add("newGameOptions");
            newGameOptions.getChildren().addAll(vsHumanBtn, vsComputerBtn);

            newGameBtn.setOnMouseEntered(e -> {
                this.getChildren().removeAll(getChildren());
                this.getChildren().add(newGameOptions);
                this.getChildren().add(exitBtn);
            });

            newGameOptions.setOnMouseExited(e -> {
                this.getChildren().removeAll(getChildren());
                this.getChildren().add(newGameBtn);
                this.getChildren().add(exitBtn);
            });

            for(Button button : new Button[]{newGameBtn, exitBtn}) {
                button.getStyleClass().add("homeScreenButton");
                button.setMinWidth(WIDTH * .5);
            }

            getChildren().addAll(newGameBtn, exitBtn);
        }
    }

}