package edu.up.cs301.pong;

import android.graphics.*;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import edu.up.cs301.animation.Animator;

/**
 * Class: PongAnimator
 * This class is for the animator.
 *
 * @author Alex Hadi
 * @author Jason Twigg
 * @version March 30, 2018
 */
public class PongAnimator implements Animator {
    // Static variables for width and height of the canvas.
    public static int width = 2048;
    public static int height = 1200;

    private Random random = new Random();

    // For score enhancement
    private Paint scorePaint;
    private int scoreCount;

    // To allow game to be over.
    private boolean gameOver;

    // Balls: ArrayList for multiple balls enhancement.
    private ArrayList<Ball> balls;

    // Paddle and Wall objects.
    private Paddle paddle;
    private Wall wall = new Wall();

    // To allow for pause of the game
    private boolean pauseMode = false;

    // Speed for slowing down and speeding up the balls
    private double speed = 0.5;

    // Blocks
    private Block[] blocks;

    /**
     * Constructor: PongAnimator
     * Initializes the animator with one ball and the paddle.
     *
     * @param ball The Ball object.
     * @param paddle The Paddle object.
     */
    public PongAnimator(Ball ball, Paddle paddle) {
        if (balls == null) {
            balls = new ArrayList<>();
            balls.add(ball);
        }

        this.paddle = paddle;

        // For score enhancement.
        scoreCount = 0;
        scorePaint = new Paint();
        scorePaint.setTextSize(150f);

        // For blocks enhancement. Blocks are created.
        blocks = new Block[20];
        Paint blockPaint = new Paint();
        blockPaint.setColor(Color.BLACK);
        for (int i = 0; i < 20; i++) {
            blocks[i] = new Block(((i%5))*width/6+ width/12,
                    ((i/5)+2)*(height/20), width/7, height/25, Color.BLACK);
        }

        // Game initially isn't over.
        gameOver = false;
    }

    /**
     * Method: interval
     * Required for Animator implementation (overridden).
     * Interval between animation frames: .03 seconds (about 33 times per sec)
     *
     * @return the time interval between frames in milliseconds.
     */
    @Override
    public int interval() {
        return 60;
    }

    /**
     * Method: backgroundColor
     * Required for Animator implementation (overridden).
     * The background color, which is set to white.
     *
     * @return The background color onto which the animation is drawn.
     */
    @Override
    public int backgroundColor() {
        return Color.rgb(255, 255, 255);
    }

    /**
     * Method: tick
     * Action to perform on clock tick.
     * Required for Animator implementation (overridden).
     *
     * @param c The Canvas object onto which animation is drawn.
     */
    @Override
    public void tick(Canvas c) {
        // Game is over once the score is negative.
        if (scoreCount < 0) gameOver = true;
        if (gameOver) {
            c.drawText("GAME OVER :(", c.getWidth()/2 - 500, c.getHeight()/2,
                    scorePaint);
            return;
        }

        // Draw wall and paddle.
        wall.draw(c);
        paddle.draw(c);

        // Draw the score text.
        c.drawText("Score: " + scoreCount, c.getWidth()/2 - 300,
                c.getHeight()/2, scorePaint);

        // Draw the balls and blocks.
        for (Ball ball : balls) ball.draw(c);
        for (Block bl : blocks) if (bl != null) bl.draw(c);

        // Stop and return if the game is paused.
        if (pauseMode) return;

        // Change color of the score text.
        scorePaint.setColor(Color.rgb(random.nextInt(256), random.nextInt(256),
                random.nextInt(256)));

        /*
         External Citation
         Date: 17 March 2018
         Problem: Could not remove an item from list while iterating over it.
         Resource:
         https://stackoverflow.com/questions/1196586/
         calling-remove-in-foreach-loop-in-java
         Solution: Used iterator directly instead of foreach loop.
         */
        // Increment values of the balls with each tick.
        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            // Get the next ball.
            Ball ball = iterator.next();

            // Set the velocity increments.
            int incrementVelX = (10-(int)(Math.random()*21));
            int incrementVelY = (10-(int)(Math.random()*21));

            // Make sure ball bounces off the walls.
            switch (ball.isHittingWall()) {
                case 1:
                    ball.reverseVelX();
                    ball.setVelX(ball.getVelX()+incrementVelX);
                    ball.setVelY(ball.getVelY()+incrementVelY);
                    ball.setHitCount(ball.getHitCount()+1);
                    scoreCount+=ball.getHitCount();
                    break;
                case 2:
                    ball.reverseVelY();
                    ball.setVelY(ball.getVelY()+incrementVelX);
                    ball.setVelY(ball.getVelY()+incrementVelY);
                    ball.setHitCount(ball.getHitCount()+1);
                    scoreCount+=ball.getHitCount();
                    break;
                case 3:
                    ball.reverseVelX();
                    ball.setVelX(ball.getVelX()+incrementVelX);
                    ball.setVelY(ball.getVelY()+incrementVelY);
                    ball.setHitCount(ball.getHitCount()+1);
                    scoreCount+=ball.getHitCount();
                    break;
            }

            // Make ball bounce off paddle.
            if (ball.isCollidingWithPaddle(paddle)){
                ball.reverseVelY();
                ball.setVelX(ball.getVelX()+incrementVelX);
                ball.setVelY(ball.getVelY()+incrementVelY);
                ball.setHitCount(ball.getHitCount()+1);
                scoreCount+=ball.getHitCount();
            }

            // Increment the ball position by the velocity also
            // multiplies the velocity by the speed variable.
            ball.setPosX((int)(ball.getPosX() + ball.getVelX()*speed));
            ball.setPosY((int)(ball.getPosY() + ball.getVelY()*speed));

            // Radius is incremented or decremented with helper method.
            ball.changeRadius();

            // Remove the ball if it goes off the screen.
            if (ball.getPosY() >= height+10) {
                scoreCount-=ball.getHitCount()*5;
                iterator.remove();
            }

            // The blocks are destroyed if they collide with a ball.
            for (int i = 0; i<blocks.length; i++) {
                Block bl = blocks[i];

                if (bl == null) continue;
                if (bl.isCollidingWith(ball) == 1 ||
                        bl.isCollidingWith(ball) == 0) {
                    ball.reverseVelY();
                    blocks[i] = null;
                    scoreCount += 10;
                }
            }
        }

        // Color of all PongObjects are changed.
        for (Ball ball : balls) ball.setRandomColor();
        wall.setRandomColor();
        paddle.setRandomColor();
    }

    /**
     * Method: doPause
     * Required by the animator implementation (overridden).
     * Always returns false.
     *
     * @return Indication of whether to pause as a boolean.
     */
    @Override
    public boolean doPause() {
        return false;
    }

    /**
     * Method: doQuit
     * Required by the animator implementation (overridden).
     * Always returns false (never quit).
     *
     * @return Indication of whether to quit as a boolean.
     */
    @Override
    public boolean doQuit() {
        return false;
    }

    /**
     * Method: onTouch
     * Required by the animator implementation (overridden).
     * Reverse each ball's direction when the screen is tapped by the user.
     */
    @Override
    public void onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Restart game with a tap of the screen.
            if (gameOver) {
                gameOver = false;
                scoreCount = 0;
            }
            // To allow user to select the paddle.
            else if (paddle.contains(event.getX(),event.getY())) {
                paddle.setSelected(true,(int)event.getX());
            }
            // Otherwise, reverse direction of all the balls.
            else {
                if( event.getY() > 100) {
                    for (Ball b : balls) {
                        b.reverseVelX();
                        b.reverseVelY();
                    }
                }
            }
        }
        // To deselect the paddle.
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            paddle.setSelected(false, 0);
        }
        // To drag the paddle across the screen.
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            paddle.dragX((int)event.getX());
        }
    }

    /**
     * Method: addBall
     * Add a Ball object to the balls ArrayList.
     *
     * @param ball The Ball object.
     */
    public void addBall(Ball ball) {
        balls.add(ball);
    }

    /**
     * Method: togglePause
     * Helper method to reverse the value of the pauseMode boolean.
     */
    public void togglePause() {
        pauseMode = !pauseMode;
    }

    /* Getters and setters below: */

    //Setter for the speed variable (sets to percent to make calculation easy)
    public void setSpeed(int speed){
        this.speed = (double)(speed)/100.0;
    }
    // Getter for speed (just returns its raw value)
    public double getSpeed() {
        return speed;
    }

    // Getter and setter for scoreCount.
    public int getScoreCount() {
        return scoreCount;
    }
    public void setScoreCount(int scoreCount) {
        this.scoreCount = scoreCount;
    }

    // Getter and setter for pauseMode
    public boolean isPauseMode() {
        return pauseMode;
    }
    public void setPauseMode(boolean pauseMode) {
        this.pauseMode = pauseMode;
    }

    // Getter and setter for balls.
    public ArrayList<Ball> getBalls() {
        return balls;
    }
    public void setBalls(ArrayList<Ball> balls) {
        this.balls = balls;
    }

    // Getter and setter for blocks
    public Block[] getBlocks() {
        return blocks;
    }
    public void setBlocks(Block[] blocks) {
        this.blocks = blocks;
    }
}
