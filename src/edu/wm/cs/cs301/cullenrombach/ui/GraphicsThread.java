package edu.wm.cs.cs301.cullenrombach.ui;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import edu.wm.cs.cs301.cullenrombach.falstad.GraphicsWrapper;

/**
 * This class creates a Thread that manages the GraphicsWrapper SurfaceView.
 * This is based on a tutorial from http://www.edu4java.com/en/androidgame/androidgame3.html
 * @author Cullen Rombach (cmromb)
 */
@SuppressLint("WrongCall")
public class GraphicsThread extends Thread {

	static final long FPS = 60;
	private GraphicsWrapper gw;
	private boolean running = false;

	/**
	 * Constructor that takes a GraphicsWrapper.
	 * @param gw the GraphicsWrapper that will be used.
	 */
	public GraphicsThread(GraphicsWrapper gw) {
		this.gw = gw;
	}

	/**
	 * Sets a flag for whether or not this thread is currently running.
	 * @param run True is running, False otherwise.
	 */
	public void setRunning(boolean run) {
		running = run;
	}

	/**
	 * Main method that runs the GraphicsWrapper SurfaceView at
	 * the specified number of frames per second (FPS).
	 */
	@Override
	public void run() {
		long ticksPS = 1000 / FPS;
		long startTime;
		long sleepTime;

		while (running) {
			Canvas c = null;
			startTime = System.currentTimeMillis();
			try {
				c = gw.getHolder().lockCanvas();
				synchronized (gw.getHolder()) {
					gw.onDraw(c);
				}
			} finally {
				if (c != null) {
					gw.getHolder().unlockCanvasAndPost(c);
				}
			}
			sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
			try {
				if (sleepTime > 0)
					sleep(sleepTime);
				else
					sleep(10);
			} catch (Exception e) {}
		}
	}
}
