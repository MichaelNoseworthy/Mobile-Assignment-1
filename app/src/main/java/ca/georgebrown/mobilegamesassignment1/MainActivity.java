package ca.georgebrown.mobilegamesassignment1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Random;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    Canvas canvas;

    SlotMachineView SlotMachineView;

    /*
    //Sound
    //initialize sound variables
    private SoundPool soundPool;
    int sample1 = -1;
    int sample2 = -1;
    int sample3 = -1;
    int sample4 = -1;

    */

    //Used for getting display details like the number of pixels
    Display display;
    Point size;
    int screenWidth;
    int screenHeight;

    //!!Candidates for class!!!
    //Game objects
    int racketWidth;
    int racketHeight;
    Point racketPosition;

    Point ballPosition;
    int ballWidth;

    //for ball movement
    boolean ballIsMovingLeft;
    boolean ballIsMovingRight;
    boolean ballIsMovingUp;
    boolean ballIsMovingDown;

    //for racket movement
    boolean racketIsMovingLeft;
    boolean racketIsMovingRight;

    // Slot Machine Variables:
    int playerMoney = 1000;
    int winnings = 0;
    int jackpot = 5000;
    int turn = 0;
    int playerBet = 0;
    int winNumber = 0;
    int lossNumber = 0;
    int spinResult;
    //char fruits = ""; //String?
    int winRatio = 0;
    int grapes = 0;  //Change to Star Wars Stuff
    int bananas = 0;  //Change to Star Wars Stuff
    int oranges = 0;  //Change to Star Wars Stuff
    int cherries = 0;  //Change to Star Wars Stuff
    int bars = 0;  //Change to Star Wars Stuff
    int bells = 0;  //Change to Star Wars Stuff
    int sevens = 0;  //Change to Star Wars Stuff
    int blanks = 0;  //Change to Star Wars Stuff





    //stats
    long lastFrameTime;
    int fps;
    int score;
    int lives;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SlotMachineView = new SlotMachineView(this);
        setContentView(SlotMachineView);

        //Sound code
        /*soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            //Create objects of the 2 required classes
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            //create our three fx in memory ready for use
            descriptor = assetManager.openFd("sample1.ogg");
            sample1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sample2.ogg");
            sample2 = soundPool.load(descriptor, 0);


            descriptor = assetManager.openFd("sample3.ogg");
            sample3 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sample4.ogg");
            sample4 = soundPool.load(descriptor, 0);


        } catch (IOException e) {
            //catch exceptions here
        }

        */

        //Get the screen size in pixels
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;


        //The game objects
        racketPosition = new Point();
        racketPosition.x = screenWidth / 2;
        racketPosition.y = screenHeight - 20;
        racketWidth = screenWidth / 8;
        racketHeight = 10;

        ballWidth = screenWidth / 35;
        ballPosition = new Point();
        ballPosition.x = screenWidth / 2;
        ballPosition.y = 1 + ballWidth;

        lives = 3;
    }

    class SlotMachineView extends SurfaceView implements Runnable {
        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingSlots;
        Paint paint;

        public SlotMachineView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            ballIsMovingDown = true;

            Random randomNumber = new Random();
            int ballDirection = randomNumber.nextInt(3);
            switch (ballDirection) {
                case 0:
                    ballIsMovingLeft = true;
                    ballIsMovingRight = false;
                    break;

                case 1:
                    ballIsMovingRight = true;
                    ballIsMovingLeft = false;
                    break;

                case 2:
                    ballIsMovingLeft = false;
                    ballIsMovingRight = false;
                    break;
            }


        }

        @Override
        public void run() {
            while (playingSlots) {
                updateCourt();
                drawCourt();
                controlFPS();

            }

        }

        //Slot class:

        public class PlaySlotGame{

            int value = 0; //assuming these are ints during int
            int lowerBounds = 0; //assuming these are ints during int
            int upperBounds = 0; //assuming these are ints during int


            // Utility function to show Player Stats

            public void showPlayerStats(){
                winRatio = winNumber / turn;
                /*
                $("#jackpot").text("Jackpot: " + jackpot);
                $("#playerMoney").text("Player Money: " + playerMoney);
                $("#playerTurn").text("Turn: " + turn);
                $("#playerWins").text("Wins: " + winNumber);
                $("#playerLosses").text("Losses: " + lossNumber);
                $("#playerWinRatio").text("Win Ratio: " + (winRatio * 100).toFixed(2) + "%");
                */
            }


// Utility function to reset all fruit tallies

            void resetFruitTally() {
                grapes = 0;
                bananas = 0;
                oranges = 0;
                cherries = 0;
                bars = 0;
                bells = 0;
                sevens = 0;
                blanks = 0;
            }

// Utility function to reset the player stats
            void resetAll() {
                playerMoney = 1000;
                winnings = 0;
                jackpot = 5000;
                turn = 0;
                playerBet = 0;
                winNumber = 0;
                lossNumber = 0;
                winRatio = 0;
            }


// Check to see if the player won the jackpot
            void checkJackPot() {
    // compare two random values
                double jackPotTry = Math.floor(Math.random() * 51 + 1);
                double jackPotWin = Math.floor(Math.random() * 51 + 1);
                if (jackPotTry == jackPotWin) {
                    //alert("You Won the $" + jackpot + " Jackpot!!"); //Call to paint!
                    playerMoney += jackpot;
                    jackpot = 1000;
                }
            }


// Utility function to show a win message and increase player money
            void showWinMessage() {
                playerMoney += winnings;
                //$("div#winOrLose>p").text("You Won: $" + winnings); //Call to paint!
                resetFruitTally();
                checkJackPot();
            }


// Utility function to show a loss message and reduce player money
            void showLossMessage() {
                playerMoney -= playerBet;
                //$("div#winOrLose>p").text("You Lost!"); //Call to paint!
                resetFruitTally();
            }


// Utility function to check if a value falls within a range of bounds
            int checkRange(int value, int lowerBounds, int upperBounds) { //assuming these are ints during init
                if (value >= lowerBounds && value <= upperBounds)
                {
                    return value;
                }
                else {
                    //return !value;  // Converted from javascript...might be wrong.
                    return 0;
                }
            }


// When this function is called it determines the betLine results.
//e.g. Bar - Orange - Banana
            void Reels() { //Really need to rework this code.  Leaving out for now.
                //string betLine = [" ", " ", " "]; //something to figure out
                /*
                int outCome[] = [0, 0, 0];

                for (int spin = 0; spin < 3; spin++) {
                    outCome[spin] = Math.floor((Math.random() * 65) + 1);
                    switch (outCome[spin]) {
                        case checkRange(outCome[spin], 1, 27):  // 41.5% probability
                            betLine[spin] = "blank";
                            blanks++;
                            break;
                        case checkRange(outCome[spin], 28, 37): // 15.4% probability
                            betLine[spin] = "Grapes";
                            grapes++;
                            break;
                        case checkRange(outCome[spin], 38, 46): // 13.8% probability
                            betLine[spin] = "Banana";
                            bananas++;
                            break;
                        case checkRange(outCome[spin], 47, 54): // 12.3% probability
                            betLine[spin] = "Orange";
                            oranges++;
                            break;
                        case checkRange(outCome[spin], 55, 59): //  7.7% probability
                            betLine[spin] = "Cherry";
                            cherries++;
                            break;
                        case checkRange(outCome[spin], 60, 62): //  4.6% probability
                            betLine[spin] = "Bar";
                            bars++;
                            break;
                        case checkRange(outCome[spin], 63, 64): //  3.1% probability
                            betLine[spin] = "Bell";
                            bells++;
                            break;
                        case checkRange(outCome[spin], 65, 65): //  1.5% probability
                            betLine[spin] = "Seven";
                            sevens++;
                            break;
                    }
                }
                return betLine;
                */
            }

// This function calculates the player's winnings, if any
            void determineWinnings()
            {
                if (blanks == 0)
                {
                    if (grapes == 3) {
                        winnings = playerBet * 10;
                    }
                    else if(bananas == 3) {
                        winnings = playerBet * 20;
                    }
                    else if (oranges == 3) {
                        winnings = playerBet * 30;
                    }
                    else if (cherries == 3) {
                        winnings = playerBet * 40;
                    }
                    else if (bars == 3) {
                        winnings = playerBet * 50;
                    }
                    else if (bells == 3) {
                        winnings = playerBet * 75;
                    }
                    else if (sevens == 3) {
                        winnings = playerBet * 100;
                    }
                    else if (grapes == 2) {
                        winnings = playerBet * 2;
                    }
                    else if (bananas == 2) {
                        winnings = playerBet * 2;
                    }
                    else if (oranges == 2) {
                        winnings = playerBet * 3;
                    }
                    else if (cherries == 2) {
                        winnings = playerBet * 4;
                    }
                    else if (bars == 2) {
                        winnings = playerBet * 5;
                    }
                    else if (bells == 2) {
                        winnings = playerBet * 10;
                    }
                    else if (sevens == 2) {
                        winnings = playerBet * 20;
                    }
                    else if (sevens == 1) {
                        winnings = playerBet * 5;
                    }
                    else {
                        winnings = playerBet * 1;
                    }
                    winNumber++;
                    showWinMessage();
                }
                else
                {
                    lossNumber++;
                    showLossMessage();
                }

            }
            /*
// When the player clicks the spin button the game kicks off
            $("#spinButton").click(function () {
                playerBet = $("div#betEntry>input").val();

                if (playerMoney == 0)
                {
                    if (confirm("You ran out of Money! \nDo you want to play again?")) {
                        resetAll();
                        showPlayerStats();
                    }
                }
                else if (playerBet > playerMoney) {
                    alert("You don't have enough Money to place that bet.");
                }
                else if (playerBet < 0) {
                    alert("All bets must be a positive $ amount.");
                }
                else if (playerBet <= playerMoney) {
                    spinResult = Reels();
                    fruits = spinResult[0] + " - " + spinResult[1] + " - " + spinResult[2];
                    $("div#result>p").text(fruits);
                    determineWinnings();
                    turn++;
                    showPlayerStats();
                }
                else {
                    alert("Please enter a valid bet amount");
                }

            });
             */
        }



        public void updateCourt() {




            /*
            if (racketIsMovingRight) {
                racketPosition.x = racketPosition.x + 10;
            }

            if (racketIsMovingLeft) {
                racketPosition.x = racketPosition.x - 10;
            }


            //detect collisions

            //hit right of screen
            if (ballPosition.x + ballWidth > screenWidth) {
                ballIsMovingLeft = true;
                ballIsMovingRight = false;
                //soundPool.play(sample1, 1, 1, 0, 0, 1);
            }

            //hit left of screen
            if (ballPosition.x < 0) {
                ballIsMovingLeft = false;
                ballIsMovingRight = true;
                //soundPool.play(sample1, 1, 1, 0, 0, 1);
            }

            //Edge of ball has hit bottom of screen
            if (ballPosition.y > screenHeight - ballWidth) {
                lives = lives - 1;
                if (lives == 0) {
                    lives = 3;
                    score = 0;
                    //soundPool.play(sample4, 1, 1, 0, 0, 1);
                }
                ballPosition.y = 1 + ballWidth;//back to top of screen

                //what horizontal direction should we use
                //for the next falling ball
                Random randomNumber = new Random();
                int startX = randomNumber.nextInt(screenWidth - ballWidth) + 1;
                ballPosition.x = startX + ballWidth;

                int ballDirection = randomNumber.nextInt(3);
                switch (ballDirection) {
                    case 0:
                        ballIsMovingLeft = true;
                        ballIsMovingRight = false;
                        break;

                    case 1:
                        ballIsMovingRight = true;
                        ballIsMovingLeft = false;
                        break;

                    case 2:
                        ballIsMovingLeft = false;
                        ballIsMovingRight = false;
                        break;
                }
            }

            //we hit the top of the screen
            if (ballPosition.y <= 0) {
                ballIsMovingDown = true;
                ballIsMovingUp = false;
                ballPosition.y = 1;
                //soundPool.play(sample2, 1, 1, 0, 0, 1);
            }

            //depending upon the two directions we should be
            //moving in adjust our x any positions
            if (ballIsMovingDown) {
                ballPosition.y += 6;
            }

            if (ballIsMovingUp) {
                ballPosition.y -= 10;
            }

            if (ballIsMovingLeft) {
                ballPosition.x -= 12;
            }

            if (ballIsMovingRight) {
                ballPosition.x += 12;
            }

            //Has ball hit racket
            if (ballPosition.y + ballWidth >= (racketPosition.y - racketHeight / 2)) {
                int halfRacket = racketWidth / 2;
                if (ballPosition.x + ballWidth > (racketPosition.x - halfRacket)
                        && ballPosition.x - ballWidth < (racketPosition.x + halfRacket)) {
                    //rebound the ball and play a sound
                    //soundPool.play(sample3, 1, 1, 0, 0, 1);
                    score++;
                    ballIsMovingUp = true;
                    ballIsMovingDown = false;
                    //now decide how to rebound the ball
                    if (ballPosition.x > racketPosition.x) {
                        ballIsMovingRight = true;
                        ballIsMovingLeft = false;

                    } else {
                        ballIsMovingRight = false;
                        ballIsMovingLeft = true;
                    }

                }
            }

            */
        }

        public void drawCourt() {

            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                //Paint paint = new Paint();
                canvas.drawColor(Color.BLACK);//the background
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(45);
                canvas.drawText("Score:" + score + " Lives:" + lives + " fps:" + fps, 20, 40, paint);


                //Draw the squash racket
                canvas.drawRect(racketPosition.x - (racketWidth / 2),
                        racketPosition.y - (racketHeight / 2), racketPosition.x + (racketWidth / 2),
                        racketPosition.y + racketHeight, paint);

                //Draw the ball
                canvas.drawRect(ballPosition.x, ballPosition.y,
                        ballPosition.x + ballWidth, ballPosition.y + ballWidth, paint);


                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        public void controlFPS() {
            long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
            long timeToSleep = 15 - timeThisFrame;
            if (timeThisFrame > 0) {
                fps = (int) (1000 / timeThisFrame);
            }
            if (timeToSleep > 0) {

                try {
                    ourThread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                }

            }

            lastFrameTime = System.currentTimeMillis();
        }


        public void pause() {
            playingSlots = false;
            try {
                ourThread.join();
            } catch (InterruptedException e) {
            }

        }

        public void resume() {
            playingSlots = true;
            ourThread = new Thread(this);
            ourThread.start();
        }


        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:

                    if (motionEvent.getX() >= screenWidth / 2) {
                        racketIsMovingRight = true;
                        racketIsMovingLeft = false;
                    } else {
                        racketIsMovingLeft = true;
                        racketIsMovingRight = false;
                    }

                    break;


                case MotionEvent.ACTION_UP:
                    racketIsMovingRight = false;
                    racketIsMovingLeft = false;
                    break;
            }
            return true;
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        while (true) {
            SlotMachineView.pause();
            break;
        }

        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        SlotMachineView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SlotMachineView.pause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SlotMachineView.pause();
            finish();
            return true;
        }
        return false;
    }

}
