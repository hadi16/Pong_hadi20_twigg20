package edu.up.cs301.pong;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.up.cs301.animation.AnimationSurface;

/**
 * Class: PongMainActivity
 * This class contains the code to interact with the XML.
 *
 * ADDED ENHANCEMENTS:
 * - Beginner and expert mode for the paddle
 * - Multiple balls on the screen at once (with button to add balls)
 * - Pause Button (Button that toggles the pausing of the game)
 * - Speed SeekBar (SeekBar that changes the speeds of all of the balls)
 * - Color for the all of the Game objects change with every tick
 * - Sizes of the ball oscillate from 10 to 100, increasing by 5 every tick
 * - File IO (game saved when app exited and restarted)
 * - Balls keep track on how many walls they have hit & displays this on them in the game
 * - Score added that increments by the hit amount of each ball when it hits a wall or paddle
 * - Game over is incorporated into the game. User can start it again w/ a tap on the screen.
 * - Balls incorporate randomness when bouncing. The bounce-back is different depending on hit.
 * - Added breakout blocks that increase the score and reverse the direction of the ball when hit
 *
 * @author Alex Hadi
 * @author Jason Twigg
 * @version March 30, 2018
 */
public class PongMainActivity extends Activity {
    // Instance variables
    private PongAnimator pongAnimator;
    private Paddle paddle;
    private Button buttonTogglePause;
    private TextView textViewSpeed;
    private SeekBar speedSeekBar;
    private RadioGroup radioGroupDifficulty;

    /**
     * Method: onCreate
     * Creates an AnimationSurface containing a PongAnimator.
     *
     * @param savedInstanceState The Bundle object for the current instance.
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        // Required method calls.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pong_main);

        // Instantiate the Ball, Paddle, PongAnimator, and Listener objects.
        Ball ball = new Ball(Color.rgb(0,0,0));
        paddle = new Paddle(Color.RED);
        pongAnimator = new PongAnimator(ball, paddle);
        Listener listeners = new Listener();

        // Connect the animation surface with the animator.
        AnimationSurface mySurface =
                (AnimationSurface)findViewById(R.id.animationSurface);
        mySurface.setAnimator(pongAnimator);

        // Set RadioGroup properties for beginner/expert mode enhancement.
        radioGroupDifficulty =
                (RadioGroup)findViewById(R.id.radioGroupDifficulty);
        radioGroupDifficulty.setOnCheckedChangeListener(listeners);
        radioGroupDifficulty.check(R.id.radioButtonBeginner);

        // Get the button for adding balls (enhancement) and set its listener.
        Button buttonAddBall =
                (Button)findViewById(R.id.buttonAddBall);
        buttonAddBall.setOnClickListener(listeners);

        // Get the button for toggling collisions and set its listener.
        buttonTogglePause =
                (Button)findViewById(R.id.buttonPause);
        buttonTogglePause.setOnClickListener(listeners);

        //Setup the SeekBar and the TextView for the changing speed.
        speedSeekBar = (SeekBar)findViewById(R.id.seekBarSpeed);
        speedSeekBar.setOnSeekBarChangeListener(listeners);

        // Get the TextView for speed.
        textViewSpeed = (TextView)findViewById(R.id.textViewSpeed);

        // Start the speed at half speed
        speedSeekBar.setProgress(50);
        pongAnimator.setSpeed(50);
	}

	/*
     External Citation
     Date: 28 March 2018
     Problem: Did not know how to properly implement File IO.
     Resource:
     Nux emailed me an example of using onResume and onPause.
     Solution: Mocked my code off of the code Nux sent me.
     */

    /**
     * Method: onResume
     * Called when the application is reopened.
     * Used to read the restart the app with the saved state.
     */
	@Override
    public void onResume() {
        super.onResume();

        // Get the SharedPreferences file.
        SharedPreferences pref = getSharedPreferences("PONG_INFO",
                Context.MODE_PRIVATE);

        // Set general game values.
        pongAnimator.setScoreCount(pref.getInt("scoreCount", 0));
        pongAnimator.setSpeed(pref.getInt("speed", 50));
        paddle.setPosX(pref.getInt("paddlePosX",
                (PongAnimator.width-Paddle.getBeginnerLength())/2));
        paddle.setPosY(PongAnimator.height-Paddle.getWidth());
        paddle.setLength(pref.getInt("paddleLength",
                Paddle.getBeginnerLength()));
        paddle.setExpertMode(pref.getBoolean("expertMode", false));
        pongAnimator.setPauseMode(pref.getBoolean("paused", false));

        // Set all the ball values
        int ballCount = pref.getInt("ballCount", 0);
        ArrayList<Ball> balls = new ArrayList<>();
        pongAnimator.setBalls(balls);
        for (int i = 0; i<ballCount; i++) {
            Ball b = new Ball(Color.rgb(0, 0, 0));
            b.setPosX(pref.getInt("posX"+i, 200));
            b.setPosY(pref.getInt("posY"+i, 200));
            b.setVelX(pref.getInt("velX"+i, 20));
            b.setVelY(pref.getInt("velY"+i, 20));
            b.setChangeSize(pref.getInt("changeSize"+i, 1));
            b.setRadius(pref.getInt("radius"+i, 60));
            b.setHitCount(pref.getInt("hitCount"+i, 0));
            b.setRandomColor();
            pongAnimator.addBall(b);
        }

        // Set up all the blocks.
        Set<String> blockSet = pref.getStringSet("blockSet", null);
        if (blockSet != null) {
            Block[] blocks = new Block[20];
            for (String s : blockSet) {
                int i = Integer.parseInt(s);
                blocks[i] = new Block(((i%5))*PongAnimator.width/6+
                        PongAnimator.width/12, ((i/5)+2)*
                        (PongAnimator.height/20), PongAnimator.width/7,
                        PongAnimator.height/25, Color.BLACK);
            }
            pongAnimator.setBlocks(blocks);
        }

        // Set the SeekBar and RadioButton.
        speedSeekBar.setProgress((int)(pongAnimator.getSpeed()*100));
        radioGroupDifficulty.check(paddle.isExpertMode() ?
                R.id.radioButtonExpert : R.id.radioButtonBeginner);
        buttonTogglePause.setText("Pause: " +
                (pongAnimator.isPauseMode() ? "ON" : "OFF"));
    }

    /**
     * Method: onPause
     * Called when the application is terminated.
     * Used to write the state of the app to a SharedPreferences file.
     */
    @Override
    protected void onPause() {
        super.onPause();

        // Create a new SharedPreferences file.
        SharedPreferences pref =
                getSharedPreferences("PONG_INFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();

        // Save the general game values.
        prefEditor.putInt("scoreCount", pongAnimator.getScoreCount());
        prefEditor.putInt("speed", (int)(pongAnimator.getSpeed()*100));
        prefEditor.putInt("paddlePosX", paddle.getPosX());
        prefEditor.putInt("paddleLength", paddle.getLength());
        prefEditor.putBoolean("expertMode", paddle.isExpertMode());
        prefEditor.putBoolean("paused", pongAnimator.isPauseMode());

        // Save the values for the balls.
        ArrayList<Ball> ballList = pongAnimator.getBalls();
        prefEditor.putInt("ballCount", ballList.size());
        for (int i = 0; i<ballList.size(); i++) {
            Ball b = ballList.get(i);
            prefEditor.putInt("posX"+i, b.getPosX());
            prefEditor.putInt("posY"+i, b.getPosY());
            prefEditor.putInt("velX"+i, b.getVelX());
            prefEditor.putInt("velY"+i, b.getVelY());
            prefEditor.putInt("changeSize"+i, b.getChangeSize());
            prefEditor.putInt("radius"+i, b.getRadius());
            prefEditor.putInt("hitCount"+i, b.getHitCount());
        }

        /*
         External Citation
         Date: 28 March 2018
         Problem: Did not know how to put a string set into SharedPreferences.
         Resource:
         https://stackoverflow.com/questions/29195164/
         android-setting-and-fetching-a-stringset-from-sharedpreferences
         Solution: Used a HashSet with a Set<String> as was mentioned online.
         */
        // Save the values for the blocks.
        Set<String> blockSet = new HashSet<>();
        Block[] blocks = pongAnimator.getBlocks();
        for (int i = 0; i<blocks.length; i++) {
            if (blocks[i] != null) blockSet.add(Integer.toString(i));
        }
        prefEditor.putStringSet("blockSet", blockSet);

        // commit() isn't used because apply() works in the background.
        prefEditor.apply();
    }

    /**
     * Inner Class: Listener
     * Contains code for all listeners.
     */
	private class Listener implements RadioGroup.OnCheckedChangeListener,
            View.OnClickListener, SeekBar.OnSeekBarChangeListener {
        /**
         * Method: onCheckedChanged
         * Listener for the RadioGroup and RadioButtons.
         * Executed when user checks different RadioButton.
         *
         * @param group The RadioGroup object.
         * @param checkedId The ID of the checked RadioButton in the group.
         */
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            // Must be the RadioGroup for difficulty.
            if (group.getId() != R.id.radioGroupDifficulty) return;

            // Beginner Mode
            if (checkedId == R.id.radioButtonBeginner) {
                paddle.setExpertMode(false);
            }
            // Expert Mode
            else if (checkedId == R.id.radioButtonExpert) {
                paddle.setExpertMode(true);
            }
        }

        /**
         * Method: onClick
         * Executed when something is clicked.
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            // Add a new ball to the game.
            if (v.getId() == R.id.buttonAddBall) {
                pongAnimator.addBall(new Ball(Color.rgb(
                        (int) (Math.random() * 256),
                        (int) (Math.random() * 256),
                        (int) (Math.random() * 256))));
            }
            // Toggle the collisions.
            else if (v.getId() == R.id.buttonPause) {
                pongAnimator.togglePause();
                if (pongAnimator.isPauseMode()) {
                    buttonTogglePause.setText("Pause: ON");
                }
                else {
                    buttonTogglePause.setText("Pause: OFF");
                }
            }
        }

        /**
         * Method: onProgressChanged
         * Executed when the progress of the SeekBar changes.
         * @param seekBar The SeekBar.
         * @param progress The current progress.
         * @param fromUser Whether it came from the user.
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // check if the seekBar is the speed SeekBar and then set the text
            // to resemble the SeekBar & then sets the new speed in animator
            if( R.id.seekBarSpeed == seekBar.getId() ) {
                textViewSpeed.setText("Speed: " + progress);
                pongAnimator.setSpeed(progress);
            }
        }

        /**
         * Required for the SeekBar listener. Not used.
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        /**
         * Required for the SeekBar listener. Not used.
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}
