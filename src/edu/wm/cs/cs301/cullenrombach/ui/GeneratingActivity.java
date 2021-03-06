package edu.wm.cs.cs301.cullenrombach.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ProgressBar;
import android.support.v4.app.NavUtils;
import edu.wm.cs.cs301.cullenrombach.R;
import edu.wm.cs.cs301.cullenrombach.falstad.Distance;
import edu.wm.cs.cs301.cullenrombach.falstad.Maze;
import edu.wm.cs.cs301.cullenrombach.falstad.MazeFileReader;
import edu.wm.cs.cs301.cullenrombach.falstad.MazeFileWriter;

public class GeneratingActivity extends Activity {
	
	private static final String TAG = "GeneratingActivity";
	private ProgressBar progressBar;
	private int progressBarStatus = 0;
	private Button startButton;
	private Thread mazeThread;
	private boolean showMap = false;
	private boolean showWalls = false;
	private boolean showSolution = false;
	private boolean loadFailed; // Flag set to true if the MazeFileReader failed to find a file.
	private Spinner driverSpinner;
	private String driverType;
	private String generatorType;
	private TextView generatingLabel;
	private int mazeSize;
	public static Maze maze; // The Maze object for this application.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generating);
		
		// Get the UI elements we need to interact with.
		progressBar = (ProgressBar)findViewById(R.id.generatingProgressBar);
		startButton = (Button)findViewById(R.id.playGeneratingButton);
		driverSpinner = (Spinner)findViewById(R.id.driverSpinner);
		
		// Make the start button invisible.
		startButton.setVisibility(View.GONE);
		
		// Get the information passed from AMazeActivity.
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    generatorType = extras.getString("generatorType");
		    mazeSize = extras.getInt("mazeSize");
		}
		
		// Set up the thread that handles the maze generation and progress bar.
		if(generatorType.equals("Saved File")) {
			mazeThread = newMazeLoadThread();
		}
		else {
			mazeThread = newMazeGenerationThread();
		}
		
		// Set the loadFailed flag to false;
		loadFailed = false;
		
		// Start generating or loading the Maze.
		mazeThread.start();
		Log.v(TAG, "mazeThread started");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.generating, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		// If the up button was pressed:
		if (id == android.R.id.home) {
			// Go to AMaze.
			NavUtils.navigateUpFromSameTask(this);
			// Interrupt maze generation.
			maze.getMazeBuilder().buildThread.interrupt();
			mazeThread.interrupt();
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Go back to the home screen and stop maze generation if the back button was pressed.
	 */
	@Override
    public void onBackPressed()
    {
		// Interrupt maze generation.
		mazeThread.interrupt();
		
		// Log what we're doing.
		Log.v(TAG, "Moving to AMazeActivity");
		
		// Go back to the main screen.
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
        
        // Finish this activity.
     	this.finish();
    }
	
	/**
	 * Called when the user clicks the back button, sends them to the Play
	 * activity.
	 * @param view The view that was clicked.
	 */
	public void moveToPlay(View view) {
		// Wait for the maze generator to finish.
		try {
			mazeThread.join();
		} catch(InterruptedException e) {
			Log.e(TAG, "Maze loading thread interrupted.");
		}
		
		// Save the robot type.
		driverType = driverSpinner.getSelectedItem().toString();
		
		// Log what we're doing.
		Log.v(TAG, "Moving to PlayActivity");
		Log.v("GeneratorActivity", "Driver Type: " + driverType);
		Log.v("GeneratorActivity", "Show Map: " + showMap + "; Walls: " + showWalls + "; Solution: " + showSolution);
		
		// Move to the next screen.
		Intent intent = new Intent(this, PlayActivity.class);
		intent.putExtra("showMap", showMap);
		intent.putExtra("showWalls", showWalls);
		intent.putExtra("showSolution", showSolution);
		intent.putExtra("driverType", driverType);
		intent.putExtra("mazeSize", mazeSize);
		startActivity(intent);
		
		// Finish this activity.
		this.finish();
	}
	
	/**
	 * Handle clicks on the map visibility Switch.
	 */
	public void updateShowMap(View v) {
		// Store the view as a Switch.
		Switch mapSwitch = (Switch)v;
		
		// Change the map visibility.
		showMap = mapSwitch.isChecked();
		
		// Log what we're doing.
		Log.v(TAG, "Show Map: " + showMap);
	}
	
	/**
	 * Handle clicks on the wall visibility Switch.
	 */
	public void updateShowWalls(View v) {
		// Store the view as a Switch.
		Switch wallSwitch = (Switch)v;

		// Change the wall visibility.
		showWalls = wallSwitch.isChecked();

		// Log what we're doing.
		Log.v(TAG, "Show Walls: " + showWalls);
	}

	/**
	 * Handle clicks on the solution visibility Switch.
	 */
	public void updateShowSolution(View v) {
		// Store the view as a Switch.
		Switch solutionSwitch = (Switch)v;

		// Change the solution visibility.
		showSolution = solutionSwitch.isChecked();

		// Log what we're doing.
		Log.v(TAG, "Show Solution: " + showSolution);
	}

	/**
	 * Helper method that loads a maze from a file and, if there isn't a
	 * saved maze, creates a new one.
	 */
	private Thread newMazeLoadThread() {
		return new Thread(new Runnable() {
			public void run() {
				// Reset the progress bar.
				progressBarStatus = 0;

				// Load the maze.
				maze = new Maze();
				// Try to load the saved Maze.
				try {
					Log.v(TAG, "Loading saved maze...");
					updateProgress(20);
					MazeFileReader reader = new MazeFileReader(GeneratingActivity.this, "maze_of_size_" + mazeSize + ".xml");
					Log.v(TAG, "Maze loaded from file");
					updateProgress(50);
					maze.setWidth(reader.getWidth());
					updateProgress(60);
					maze.setHeight(reader.getHeight());
					updateProgress(70);
					Distance dists = new Distance(reader.getDistances());
					maze.setDistances(dists);
					updateProgress(80);
					maze.newMaze(reader.getRootNode(), reader.getCells(), dists, reader.getStartX(), reader.getStartY()) ;
					updateProgress(90);
				// If there isn't a saved Maze for this difficulty, save a new one.
				} catch (Exception e) {
					Log.v(TAG, "Maze file does not exist for difficulty level " + mazeSize);
					Log.v(TAG, "Generating and saving a new Maze file...");
					// Mark that the load failed.
					loadFailed = true;
					// Pick a random Maze generator.
					int method = randomWithinRange(0, 2);
					switch(method) {
					case(0): generatorType = "Backtracker"; break;
					case(1): generatorType = "Prim"; break;
					case(2): generatorType = "Eller"; break;
					}
					// Start a new Maze generation thread.
					mazeThread = newMazeGenerationThread();
					mazeThread.start();
					// Wait for the thread to finish.
					try {
						mazeThread.join();
					} catch (Exception ex) {
						Log.e(TAG, "Maze generation thread interrupted");
					}
					// Save the generated Maze.
					saveMaze();
					// Set the start button to be visible once the loading is done.
					startButton.getHandler().post(new Runnable() {
						public void run() {
							startButton.setVisibility(View.VISIBLE);
						}
					});
				}
				
				// Only execute this if the Maze loaded properly.
				if(!loadFailed) {
					// Initialize the Maze.
					maze.init();
					updateProgress(100);
					
					// Log that the maze generation is done.
					Log.v(TAG, "mazeLoadThread finished");
					
					// Set the label to indicate that the Maze is done loading.
					generatingLabel = (TextView)findViewById(R.id.generatingLabel);
					generatingLabel.getHandler().post(new Runnable() {
						public void run() {
							generatingLabel.setText(getString(R.string.generating_maze_complete));
						}
					});

					// Set the button to be visible once the loading is done.
					startButton.getHandler().post(new Runnable() {
						public void run() {
							startButton.setVisibility(View.VISIBLE);
						}
					});
				}
			}
		});
	}

	/**
	 * Helper method that sets up the maze generator thread and defines what it does.
	 */
	private Thread newMazeGenerationThread() {
		return new Thread(new Runnable() {
			public void run() {
				// Reset the progress bar.
				progressBarStatus = 0;

				// Make a new Maze depending upon the generator type.
				if(generatorType.equals("Prim")) {
					maze = new Maze(1);
				}
				else if(generatorType.equals("Eller")) {
					maze = new Maze(2);
				}
				else if(generatorType.equals("Backtracker")){
					maze = new Maze(0);
				}

				// Build the Maze.
				maze.build(mazeSize);

				// Keep looping while the progress bar isn't full.
				while (maze.getMazeBuilder().buildThread.isAlive()) {

					// Update the progress bar status.
					progressBarStatus = maze.getPercentDone();

					// Update the actual progress bar in the GUI.
					updateProgress(progressBarStatus);

					// Check for a thread interruption.
					if(Thread.interrupted()) {
						return;
					}
				}
				
				// Update the progress bar.
				updateProgress(100);

				// Initialize the Maze.
				maze.init();

				// Log that the maze generation is done.
				Log.v(TAG, "mazeGenerationThread finished");
				
				// Set the label to indicate that the Maze is done loading.
				generatingLabel = (TextView)findViewById(R.id.generatingLabel);
				generatingLabel.getHandler().post(new Runnable() {
					public void run() {
						generatingLabel.setText(getString(R.string.generating_maze_complete));
					}
				});
				
				if(!loadFailed) {
					// Set the button to be visible once the loading is done.
					startButton.getHandler().post(new Runnable() {
						public void run() {
							startButton.setVisibility(View.VISIBLE);
						}
					});
				}
			}
		});
	}

	// Helper method that updates the progress bar to the given value.
	private void updateProgress(int progress) {
		final int num = progress;
		// Set the progress bar to 100.
		// Update the actual progress bar in the GUI.
		progressBar.post(new Runnable() {
			public void run() {
				progressBar.setProgress(num);
			}
		});
	}
	
	/**
	 *  Helper method for newMazeLoadThread that generates a random int
	 *  within the given range, inclusive. Taken from StackOverflow.
	 * @param min The bottom of the range
	 * @param max The top of the range
	 * @return A random int from min to max, inclusive
	 */
	private int randomWithinRange(int min, int max)
	{
	   int range = (max - min) + 1;     
	   return (int)(Math.random() * range) + min;
	}
	
	/**
	 * Called by newMazeLoadThread, saves a generated Maze.
	 */
	public void saveMaze() {
		// Log what we're doing.
		Log.v(TAG, "Saving new maze...");
		
		// Store some local variables used for writing.
		String filename = "maze_of_size_" + mazeSize + ".xml";
		final int[] pos = maze.getDistances().getStartPosition();
		int startx = pos[0] ;
		int starty = pos[1] ;
		
		MazeFileWriter.store(this, filename, maze.getWidth(), maze.getHeight(), 0 /*number of rooms doesn't matter*/,
				0 /* expectedPartiters doesn't matter*/, maze.getRootnode(), maze.getCells(), maze.getDistances().dists,
				startx, starty);
		
		// Log what we're doing.
		Log.v(TAG, "New maze saved");
	}
}
