package edu.up.cs301.pong;

import android.graphics.*;
import android.view.MotionEvent;

import edu.up.cs301.animation.Animator;

public class PongAnimator implements Animator {
    // Counts the number of logical clock ticks
    private int count = 0;

    // Whether clock is ticking backwards
    private boolean goBackwards = false;

    private Paint wallPaint = new Paint();

    private int velX = 50;
    private int velY = 20;

    private int width;
    private int height;

    private Ball ball;
    private Paddle paddle;

    public PongAnimator(Ball ball, Paddle paddle) {
        this.ball = ball;
        this.paddle = paddle;
    }

    /**
     * Interval between animation frames: .03 seconds (i.e., about 33 times
     * per second).
     *
     * @return the time interval between frames, in milliseconds.
     */
    public int interval() {
        return 30;
    }

    /**
     * The background color: a light blue.
     *
     * @return the background color onto which we will draw the image.
     */
    public int backgroundColor() {
        // create/return the background color
        return Color.rgb(255, 255, 255);
    }

    /**
     * The wall color: a black color.
     *
     * @return the wall color onto which we will draw the image.
     */
    private int wallColor() {
        //create/return the wall color
        return Color.rgb(100, 100, 100);
    }

    /**
     * Tells the animation whether to go backwards.
     *
     * @param b true iff animation is to go backwards.
     */
    public void goBackwards(boolean b) {
        // set our instance variable
        goBackwards = b;
    }

    /**
     * Action to perform on clock tick
     *
     * @param c the graphics object on which to draw
     */
    public void tick(Canvas c) {
        // bump our count either up or down by one, depending on whether
        // we are in "backwards mode".
        if (goBackwards) {
            count--;
        } else {
            count++;
        }

        width = c.getWidth();
        height = c.getHeight();

        wallPaint.setColor(wallColor());
        c.drawRect(0, 0, 100, c.getHeight(), wallPaint);
        c.drawRect(0, 0, c.getWidth(), 100, wallPaint);
        c.drawRect(c.getWidth()-200, 0, c.getWidth(), c.getHeight(), wallPaint);

        // Determine the pixel position of our ball.  Multiplying by 15
        // has the effect of moving 15 pixel per frame.  Modding by 600
        // (with the appropriate correction if the value was negative)
        // has the effect of "wrapping around" when we get to either end
        // (since our canvas size is 600 in each dimension).
        int num = (count*15)%600;
        if (num < 0) num += 600;

        // Draw the ball in the correct position.
        Paint ballPaint = new Paint();
        ballPaint.setColor(Color.BLACK);

        if (isHittingWall() == 0) {
            //Ball.posX += velX;
            ball.setPosX(ball.getPosX()+velX);
            ball.setPosY(ball.getPosY()+velY);
        }

        //c.drawCircle(posX, posY, 60, ballPaint);
        ball.draw(c);
        ballPaint.setColor(0xff0000ff);
    }

    /**
     * Tells that we never pause.
     *
     * @return indication of whether to pause
     */
    public boolean doPause() {
        return false;
    }

    /**
     * Tells that we never stop the animation.
     *
     * @return indication of whether to quit.
     */
    public boolean doQuit() {
        return false;
    }

    /**
     * reverse the ball's direction when the screen is tapped
     */
    public void onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            goBackwards = !goBackwards;
        }
    }

    private int isHittingWall() {
        if (ball.getPosX() <= 100) {
            return 1;
        } else if (ball.getPosX() <= width) {
            return 3;
        }

        if (ball.getPosY() <= 100) {
            return 2;
        } else if (ball.getPosY() >= height) {
            return 4;
        }

        return 0;
    }
}
