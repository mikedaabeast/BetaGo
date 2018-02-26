package sample;
import javafx.application.Application;
import javafx.geometry.Pos;
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

    private static int HEIGHT = (int) (java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 1.20);
    private static int WIDTH = HEIGHT;

    private Game game;
    private GameView gameView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        game = new Game();

        gameView = new GameView();
        gameView.getStylesheets().add("sample/stylesheet.css");
        gameView.setPrefSize(WIDTH, HEIGHT);
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

        public void displayScreen(Node screen) {
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
            label.getStyleClass().add("sidePanelLabel");
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
            label.setText(game.getCurrentPlayer().getName() + "'s turn\nP1: " + game.getPlayers()[0].numStonesCaptured() + "\nP2: " + game.getPlayers()[1].numStonesCaptured());
         }
    }

    class HomeScreen extends VBox {

        private Button newGameBtn;
        private Button settingsBtn;
        private Button exitBtn;
        private VBox newGameOptions;
        private Label label;

        HumanVsHumanScreen humanVsHumanScreen;

        HomeScreen() {
            getStyleClass().add("homeScreen");

            label = new Label("BetaGo");
            label.getStyleClass().add("homeScreenLabel");

            newGameBtn = new Button("New Game");
            newGameBtn.setOnAction(e -> gameView.displayGamePlayScreen());

            settingsBtn = new Button("Settings");
            settingsBtn.setOnAction(e -> {});

            exitBtn = new Button("Quit");
            exitBtn.setOnAction(e -> System.exit(0));

            humanVsHumanScreen = new HumanVsHumanScreen();

            Button vsHumanBtn = new Button("Human vs Human");
            vsHumanBtn.setOnAction(e -> gameView.displayScreen(humanVsHumanScreen));
            vsHumanBtn.setMinWidth(WIDTH * .5);

            Button vsComputerBtn = new Button("Human vs Computer");
            vsComputerBtn.setOnAction(e -> gameView.displayGamePlayScreen());
            vsComputerBtn.setMinWidth(WIDTH * .5);

            vsHumanBtn.setStyle("-fx-border-color: black;");            // TODO: use .css file
            vsComputerBtn.setStyle("-fx-border-color: transparent black black black;");

            for(Button button : new Button[]{newGameBtn, settingsBtn, exitBtn}) {
                button.getStyleClass().add("homeScreenButton");
                button.setMinWidth(WIDTH * .5);
            }

            newGameOptions = new VBox();
            newGameOptions.setMaxWidth(WIDTH * .5);
            newGameOptions.getStyleClass().add("newGameOptions");
            newGameOptions.getChildren().addAll(vsHumanBtn, vsComputerBtn);

            newGameBtn.setOnMouseEntered(e -> {
                this.getChildren().removeAll(getChildren());
                this.getChildren().add(label);
                this.getChildren().add(newGameOptions);
//              this.getChildren().add(settingsBtn);
                this.getChildren().add(exitBtn);
            });

            newGameOptions.setOnMouseExited(e -> {
                this.getChildren().removeAll(getChildren());
                this.getChildren().add(label);
                this.getChildren().add(newGameBtn);
//              this.getChildren().add(settingsBtn);
                this.getChildren().add(exitBtn);
            });

            getChildren().addAll(label, newGameBtn, exitBtn);
        }
    }

    class HumanVsHumanScreen extends VBox {

        private Button playBtn;
        private Button backBtn;
        private Label label;

        HumanVsHumanScreen() {
            getStyleClass().add("humanVsHumanScreen");

            playBtn = new Button("Play");
            playBtn.setOnAction(e -> gameView.displayGamePlayScreen());

            backBtn = new Button("Back");
            backBtn.setOnAction(e -> gameView.displayHomeScreen());

            for(Button button : new Button[]{playBtn, backBtn}) {
                button.setStyle("-fx-font-size: 28pt;");
                button.setMinWidth(WIDTH * .5);
            }

            HBox hBox = new HBox();
            hBox.getChildren().addAll(playBtn, backBtn);
            hBox.setStyle("-fx-alignment: center;");

            // ------------------------------------

            HBox btnHbox = new HBox();
            btnHbox.setStyle("-fx-alignment: center;");
            btnHbox.setSpacing(20.0);

            for (Integer i :  new int[]{9, 13, 19}) {
                Button button = new Button(i + "");
                button.getStyleClass().add("boardSizeButtons");
                button.setPrefWidth(WIDTH / 5);
                button.setMinWidth(WIDTH / 5);

                final int boardSize = i;
                button.setOnAction(e -> {
                    game.setBoardSize( boardSize );
                    gameView.updateGamePlayScreen();
                });

                btnHbox.getChildren().add(button);
            }

            // ------------------------------

            GridPane grid = new GridPane();
            grid.add( new Label("Players"), 0, 0, 2, 1);

            int imageWidth = WIDTH / 5;
            ImageView whiteImageView = new ImageView( new Image(Main.class.getResourceAsStream("../images/white.png"), imageWidth, imageWidth, true, true) );
            ImageView blackImageView = new ImageView( new Image(Main.class.getResourceAsStream("../images/black.png"), imageWidth, imageWidth, true, true) );

            TextField playerOneName = new TextField("Player 1");
            TextField playerTwoName = new TextField("Player 2");

            grid.add(blackImageView, 0, 1);
            grid.add(playerOneName,  1, 1);
            grid.add(whiteImageView, 0, 2);
            grid.add(playerTwoName,  1, 2);

            grid.setPrefWidth(WIDTH / 5);
            grid.setMinWidth(WIDTH  / 5);
            grid.setStyle("-fx-alignment: center;");

            // ------------------------------

            label = new Label("Board size");
            getChildren().addAll(label, btnHbox, grid, hBox);
        }
    }

}