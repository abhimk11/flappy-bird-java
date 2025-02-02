import entity.Bird;
import entity.Pipe;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @version 1.0
 * @author: Abhinandan Mallick
 * @Project: The Flappy Bird
 * @Description: This is the main class that handles game logic.
 * @since 2025-02-01
 */
public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image dogoImage;
    Clip gameOverClip;
    Clip gameOnClip;

    //Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    //game logic
    Bird bird;
    int velocityX = -4; //moves pipes to the left speed (simulates bird moving right)
    int velocityY = 0;
    int gravity = 1;


    public ArrayList<Pipe> pipes;

    //Timer (per Frame)
    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true); //make sure that flappyClass takes key event
        addKeyListener(this); // make sure that check the 3 functions added
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    velocityY = -9;
                }
                if (gameOver) {
                    //restart the game by resetting the condition
                    gameOverClip.stop();
                    resetGame();
                }
            }
        });

        //load images and sound
        loadResource();

        bird = new Bird(birdImg);
        setDefaultBird(bird);

        pipes = new ArrayList<>();

        //place pipe timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //game timer
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    private void loadResource() {
        backgroundImg = new ImageIcon(getClass().getClassLoader().getResource("flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getClassLoader().getResource("flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getClassLoader().getResource("toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getClassLoader().getResource("bottompipe.png")).getImage();
        dogoImage = new ImageIcon(getClass().getClassLoader().getResource("huntduck.jpg")).getImage();
        try {
            URL soundURL = getClass().getClassLoader().getResource("gameOver.wav");
            if (soundURL != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                gameOverClip = AudioSystem.getClip();
                gameOverClip.open(audioIn);
            }
            URL soundURL1 = getClass().getClassLoader().getResource("gameOn.wav");
            if (soundURL1 != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL1);
                gameOnClip = AudioSystem.getClip();
                gameOnClip.open(audioIn);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void setDefaultBird(Bird bird) {
        bird.setX(birdX);
        bird.setY(birdY);
        bird.setWidth(birdWidth);
        bird.setHeight(birdHeight);
    }

    private void setDefaultPipe(Pipe pipe) {
        pipe.setX(pipeX);
        pipe.setY(pipeY);
        pipe.setWidth(pipeWidth);
        pipe.setHeight(pipeHeight);
    }

    public void placePipes() {
        //1/4 pipeHeight -> 3/4 pipeHeight
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        setDefaultPipe(topPipe);
        topPipe.setY(randomPipeY);
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        setDefaultPipe(bottomPipe);
        bottomPipe.setY(topPipe.getY() + pipeHeight + openingSpace);
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //bird
        g.drawImage(bird.getImg(), bird.getX(), bird.getY(), bird.getWidth(), bird.getHeight(), null);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.getImg(), pipe.getX(), pipe.getY(), pipe.getWidth(), pipe.getHeight(), null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawImage(dogoImage, 0, 50, boardWidth, boardHeight, null);
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
            if (gameOverClip != null && !gameOverClip.isRunning()) {
                gameOverClip.setFramePosition(0); // Rewind to the beginning of the clip
                gameOnClip.stop();
                gameOverClip.start(); // Start playing the sound
            }
        } else {
            if (gameOnClip != null && !gameOnClip.isRunning()) {
                gameOnClip.setFramePosition(0); // Rewind to the beginning of the clip
                gameOnClip.start(); // Start playing the sound
                gameOnClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        //bird
        velocityY += gravity;
        bird.setY(bird.getY() + velocityY);
        bird.setY(Math.max(bird.getY(), 0));
        //pipe
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);

            pipe.setX(pipe.getX() + velocityX);

            if (!pipe.isPassed() && bird.getX() > pipe.getX() + pipe.getWidth()) {
                pipe.setPassed(true);
                score += 0.5; //0.5 because there are 2 pipes! so 0.5*2 =1, 1 for each set of pipes
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.getY() > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.getX() < b.getX() + b.getWidth() && //a's top left corner doesn't reach b's top right corner
                a.getX() + a.getWidth() > b.getX() && //a's top right corner passes b's top left corner
                a.getY() < b.getY() + b.getHeight() && //a's top left corner doesn't reach b's bottom left corner
                a.getY() + a.getHeight() > b.getY(); //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
        }
        if (gameOver) {
            //restart the game by resetting the condition
            gameOverClip.stop();
            resetGame();
        }
    }

    public void resetGame() {
        //restart the game by resetting the condition
        bird.setY(birdY);
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameOver = false;
        gameLoop.start();
        placePipesTimer.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
