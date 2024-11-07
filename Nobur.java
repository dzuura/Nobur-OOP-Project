package tes;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Nobur extends JPanel implements ActionListener, KeyListener {
    final int boardWidth = 360;
    final int boardHeight = 640;
    private static int highScore = 0;

    // images
    Image nagaImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // naga class
    private int nagaX = boardWidth / 8;
    private int nagaY = boardWidth / 2;
    int nagaWidth = 70;
    int nagaHeight = 50;

    public int getNagaX() {
        return nagaX;
    }
    public int getNagaY() {
        return nagaY;
    }
    class Naga {
        int x = getNagaX();
        int y = getNagaY();
        int width = nagaWidth;
        int height = nagaHeight;
        Image img;

        Naga(Image img) {
            this.img = img;
        }
    }

    // pipe class
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // game logic
    Naga naga;
    int velocityX = -4; // move pipes to the left speed
    int velocityY = 0; // move naga up/down speed.
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    final Timer gameLoop;
    final Timer placePipeTimer;

    private boolean gameOver = false;
    private double score = 0;

    // UI state
    boolean showStartScreen = true;
    boolean showMapSelectionScreen = false;
    JButton playButton;

    final JButton[] mapButtons = new JButton[3];

    final JButton backToMapButton;
    final JButton restartButton;

    // Maps
    final Image[] mapImages = new Image[3];
    final Image[] nagaImages = new Image[3];
    final Image[] topPipeImages = new Image[3];
    final Image[] bottomPipeImages = new Image[3];
    int selectedMapIndex = -1;

    // Background movement
    int backgroundX1 = 0;
    int backgroundX2 = boardWidth;

    Nobur() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // load images
        mapImages[0] = new ImageIcon(getClass().getResource("/tes/maps/lava.jpg")).getImage();
        mapImages[1] = new ImageIcon(getClass().getResource("/tes/maps/hutan.png")).getImage();
        mapImages[2] = new ImageIcon(getClass().getResource("/tes/maps/es.jpg")).getImage();

        // Load naga images for each map
        nagaImages[0] = new ImageIcon(getClass().getResource("/tes/char/nagaMerah.png")).getImage();
        nagaImages[1] = new ImageIcon(getClass().getResource("/tes/char/nagaHijau.png")).getImage();
        nagaImages[2] = new ImageIcon(getClass().getResource("/tes/char/nagaBiru.png")).getImage();

        // Load top pipe images for each map
        topPipeImages[0] = new ImageIcon(getClass().getResource("/tes/toppipe/pillarKota.png")).getImage();
        topPipeImages[1] = new ImageIcon(getClass().getResource("/tes/toppipe/pillarHutan.png")).getImage();
        topPipeImages[2] = new ImageIcon(getClass().getResource("/tes/toppipe/pillarEs.png")).getImage();

        // Load bottom pipe images for each map
        bottomPipeImages[0] = new ImageIcon(getClass().getResource("/tes/bottompipe/pillarKota.png")).getImage();
        bottomPipeImages[1] = new ImageIcon(getClass().getResource("/tes/bottompipe/pillarHutan.png")).getImage();
        bottomPipeImages[2] = new ImageIcon(getClass().getResource("/tes/bottompipe/pillarEs.png")).getImage();

        // naga
        naga = new Naga(nagaImg);
        pipes = new ArrayList<>();

        // place pipes timer
        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        // game timer
        gameLoop = new Timer(1000 / 60, this);

        // setup UI components
        setLayout(null);

        playButton = new JButton("Play");
        playButton.setBounds(130, 280, 100, 50);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStartScreen = false;
                showMapSelectionScreen = true;
                playButton.setVisible(false);
                for (JButton button : mapButtons) {
                    button.setVisible(true);
                }
                repaint();
            }
        });
        add(playButton);

        for (int i = 0; i < 3; i++) {
            int mapIndex = i;
            mapButtons[i] = new JButton("Map " + (i + 1));
            mapButtons[i].setBounds(80, 150 + i * 60, 200, 50);
            mapButtons[i].setVisible(false);
            mapButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedMapIndex = mapIndex;
                    showMapSelectionScreen = false;
                    for (JButton button : mapButtons) {
                        button.setVisible(false);
                    }
                    startGame();
                }
            });
            add(mapButtons[i]);
        }

        backToMapButton = new JButton("Back to Map Selection");
        backToMapButton.setBounds(80, 300, 200, 50);
        backToMapButton.setVisible(false);
        backToMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
                showMapSelectionScreen = true;
                backToMapButton.setVisible(false);
                restartButton.setVisible(false);
                for (JButton button : mapButtons) {
                    button.setVisible(true);
                }
                repaint();
            }
        });
        add(backToMapButton);

        restartButton = new JButton("Restart");
        restartButton.setBounds(80, 360, 200, 50);
        restartButton.setVisible(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
                startGame();
                backToMapButton.setVisible(false);
                restartButton.setVisible(false);
            }
        });
        add(restartButton);
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (showStartScreen) {
            drawStartScreen(g);
        } else if (showMapSelectionScreen) {
            drawMapSelectionScreen(g);
        } else {
            if (selectedMapIndex >= 0 && selectedMapIndex < mapImages.length) {
                draw(g, mapImages[selectedMapIndex]);
            }
        }
    }

    public void drawStartScreen(Graphics g) {
        Image backgroundImage = new ImageIcon(getClass().getResource("/tes/bg/map.jpg")).getImage();
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, this);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString("Nobur", 130, 250);
    }

    public void drawMapSelectionScreen(Graphics g) {
        Image backgroundImage = new ImageIcon(getClass().getResource("/tes/bg/map.jpg")).getImage();
        draw(g, backgroundImage);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString("Select Map", 110, 100);
    }

    public void draw(Graphics g, Image backgroundImage) {
        backgroundX1 += velocityX;
        backgroundX2 += velocityX;

        if (backgroundX1 <= -boardWidth) {
            backgroundX1 = boardWidth;
        }
        if (backgroundX2 <= -boardWidth) {
            backgroundX2 = boardWidth;
        }

        // Draw the background images
        g.drawImage(backgroundImage, backgroundX1, 0, boardWidth, boardHeight, this);
        g.drawImage(backgroundImage, backgroundX2, 0, boardWidth, boardHeight, this);

        // Draw other elements (naga, pipes, score, etc.)
        g.drawImage(naga.img, naga.x, naga.y, naga.width, naga.height, null);
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Draw score and other UI elements
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
            backToMapButton.setVisible(true);
            restartButton.setVisible(true);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("High Score: " + highScore, 10, 60);
    }


    public void draw(Graphics g, Color backgroundColor) {
        // Draw solid background color
        g.setColor(backgroundColor);
        g.fillRect(0, 0, boardWidth, boardHeight);

        // Draw naga, pipes, score, etc.
        g.drawImage(naga.img, naga.x, naga.y, naga.width, naga.height, null);
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
            backToMapButton.setVisible(true);
            restartButton.setVisible(true);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("High Score: " + highScore, 10, 60);
    }


    public void move() {
        // naga
        velocityY += gravity;
        naga.y += velocityY;
        naga.y = Math.max(naga.y, 0); // apply gravity to current naga.y, limit the naga.y to top of the canvas

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && naga.x > pipe.x + pipe.width) {
                score += 0.5; // 0.5 because there are 2 pipes! so 0.5*2 = 1, 1 for each set of pipes
                pipe.passed = true;
            }

            if (collision(naga, pipe)) {
                gameOver = true;
            }
        }

        if (naga.y > boardHeight) {
            gameOver = true;
        }

        // move background
        backgroundX1 += velocityX;
        backgroundX2 += velocityX;
        if (backgroundX1 + boardWidth <= 0) {
            backgroundX1 = backgroundX2 + boardWidth;
        }
        if (backgroundX2 + boardWidth <= 0) {
            backgroundX2 = backgroundX1 + boardWidth;
        }
    }

    boolean collision(Naga a, Pipe b) {
        return a.x < b.x + b.width &&   // a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x &&   // a's top right corner passes b's top left corner
                a.y < b.y + b.height &&  // a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y;    // a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) { // called every x milliseconds by gameLoop timer
        if (!showStartScreen && !showMapSelectionScreen) {
            move();
        }
        repaint();
        if (gameOver) {
            if (score > highScore) {
                highScore = (int) score; // update high score if current score is higher
            }
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (showStartScreen) {
                showStartScreen = false;
                showMapSelectionScreen = true;
                playButton.setVisible(false);
                for (JButton button : mapButtons) {
                    button.setVisible(true);
                }
            } else if (!gameOver) {
                velocityY = -9;
            } else {
                // restart game by resetting conditions
                naga.y = nagaY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placePipeTimer.start();
                backToMapButton.setVisible(false);
                restartButton.setVisible(false);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    void startGame() {
        showMapSelectionScreen = false;
        playButton.setVisible(false);
        for (JButton button : mapButtons) {
            button.setVisible(false);
        }

        // Set images based on selected map
        naga.img = nagaImages[selectedMapIndex];
        topPipeImg = topPipeImages[selectedMapIndex];
        bottomPipeImg = bottomPipeImages[selectedMapIndex];

        gameLoop.start();
        placePipeTimer.start();
    }

    void resetGame() {
        naga.y = nagaY;
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        score = 0;
        backgroundX1 = 0;
        backgroundX2 = boardWidth;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Nobur");
        Nobur game = new Nobur();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
