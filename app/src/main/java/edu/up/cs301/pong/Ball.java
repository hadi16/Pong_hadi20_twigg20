package edu.up.cs301.pong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Class: Ball
 * This class represents a ball in the Pong game.
 *
 * @author Alex Hadi
 * @author Jason Twigg
 * @version March 30, 2018
 */
public class Ball extends PongObject {
    // Generates a random velocity in x and y between -50 and 50.
    private int velX = new Random().nextInt(101)-50;
    private int velY = new Random().nextInt(101)-50;

    // For hit enhancement.
    private int hitCount;
    private Paint hitPaint;

    // Constant for the radius of a ball.
    private int radius = 60;

    /*
     * Int for changing the size of the ball
     * 0 - No Change
     * 1 - Increase
     * 2 - Decrease
     */
    private int changeSize = 1;

    /**
     * Constructor: Ball
     * Creates a ball (isn't created at the bottom of the screen initially).
     *
     * @param c The color of the ball.
     */
    public Ball(int c) {
        super(new Random().nextInt(PongAnimator.width-Wall.getWidth()-
                Paddle.getWidth())+Wall.getWidth(), new Random().nextInt
                (2*PongAnimator.height/3)+Wall.getWidth(), c);

        hitCount = 0;
        hitPaint = new Paint();
        hitPaint.setColor(Color.BLACK);
    }

    /**
     * Method: draw
     * Draws the ball on the screen.
     *
     * @param c The Canvas object.
     */
    @Override
    public void draw(Canvas c) {
        c.drawCircle(posX, posY, radius, paint);
        if( hitCount < 10 ) {
            c.drawText(hitCount + "", posX - radius / 4, posY + radius / 3,
                    hitPaint);
        } else {
            c.drawText(hitCount + "", posX - (int)(radius / 1.75),
                    posY + radius / 3, hitPaint);
        }
    }

    /**
     * Method: isHittingWall
     * Returns which wall is being hit by the ball (if any).
     *
     * @return Which wall the ball is hitting (0: no wall, 1: left wall,
     * 2: top wall, 3: right wall).
     */
    public int isHittingWall(){
        if (posX-radius <= Wall.getWidth() && velX <= 0) {
            return 1;
        }
        if (posY-radius <= Wall.getWidth() && velY <= 0) {
            return 2;
        }
        if (posX+radius >= PongAnimator.width-Wall.getWidth() && velX >= 0) {
            return 3;
        }
        return 0;
    }

    /**
     * Method: isCollidingWithPaddle
     * Returns a boolean for whether the ball is colliding with the paddle.
     *
     * @param paddle The Paddle object.
     * @return True if ball is colliding with paddle, otherwise false.
     */
    public boolean isCollidingWithPaddle(Paddle paddle){
        return posY+radius >= PongAnimator.height-Paddle.getWidth()
                && posX+radius >= paddle.getPosX()
                && posX-radius <= paddle.getPosX()+paddle.getLength()
                && velY >= 0;
    }


    /**
     * Method: changeRadius
     * Radius of the ball is incremented or decremented.
     * Radius oscillates between 10 and 100.
     */
    public void changeRadius() {
        if(changeSize == 1 ){
            radius +=5;
        } else if(changeSize == 2 ){
            radius -=5;
        }
        if(radius <= 10 ){
            changeSize = 1;
        } else if (radius >= 100 ){
            changeSize = 2;
        }
        hitPaint.setTextSize(radius);
    }

    /**
     * Method: setRandomColor
     * Sets the paint and hitPaint to random colors.
     * Paint is set with super call.
     */
    @Override
    public void setRandomColor() {
        super.setRandomColor();
        Random random = new Random();
        hitPaint.setColor(Color.rgb(random.nextInt(256), random.nextInt(256),
                random.nextInt(256)));
    }

    /**
     * Method: reverseVelX
     * Helper method that changes the sign of velX.
     */
    public void reverseVelX() {
        velX *= -1;
    }

    /**
     * Method: reverseVelY
     * Helper method that changes the sign of velY.
     */
    public void reverseVelY() {
        velY *= -1;
    }

    // Getters and setters.
    public int getVelX() {
        return velX;
    }
    public int getVelY() {
        return velY;
    }
    public int getRadius() {
        return radius;
    }
    public int getChangeSize() {
        return changeSize;
    }
    public int getHitCount(){
        return hitCount;
    }
    public void setVelX(int velX) {
        this.velX = velX;
    }
    public void setVelY(int velY) {
        this.velY = velY;
    }
    public void setRadius(int radius) {
        this.radius = radius;
    }
    public void setChangeSize(int changeSize) {
        this.changeSize = changeSize;
    }
    public void setHitCount ( int hitCount ){
        this.hitCount = hitCount;
    }
}
