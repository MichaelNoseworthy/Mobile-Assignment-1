package ca.georgebrown.mobilegamesassignment1;

/**
 * Created by Owner on 3/13/2018.
 */

public class Wheel extends Thread {

    interface WheelListener {
        void newImage(int img);
    }

    private static int[] imgs = {R.drawable.at_at, R.drawable.b2a0da322a77f7f51849105c19aff5d9, R.drawable.da2f2077d408c0746c26c6a75721b6ee, R.drawable.darth_mauls_light_sabers_icon, R.drawable.dce112d61502d251eccce08df928c8d5,
            R.drawable.death_star_1st, R.drawable.mc3po_icon, R.drawable.n_all_painting_256x256_darthvader_cdt, R.drawable.pistol_00010};
    public int currentIndex;
    private WheelListener wheelListener;
    private long frameDuration;
    private long startIn;
    private boolean isStarted;

    public Wheel(WheelListener wheelListener, long frameDuration, long startIn) {
        this.wheelListener = wheelListener;
    this.frameDuration = frameDuration;
    this.startIn = startIn;
    currentIndex = 0;
    isStarted = true;
}

    public void nextImg() {
        currentIndex++;
        if (currentIndex == imgs.length) {
            currentIndex = 0;
        }

    }

    @Override
    public void run() {
        try {
            Thread.sleep(startIn);
        } catch (InterruptedException e) {

        }
        while (isStarted) {
            try {
                Thread.sleep(frameDuration);
            } catch (InterruptedException e) {
            }

            nextImg();
            if (wheelListener != null) {
                wheelListener.newImage(imgs[currentIndex]);
            }
        }
    }

    public void stopWheel() {
        isStarted = false;


    }

}
