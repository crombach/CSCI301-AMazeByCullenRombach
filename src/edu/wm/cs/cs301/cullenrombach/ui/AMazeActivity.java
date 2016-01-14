package edu.wm.cs.cs301.cullenrombach.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.util.Log;
import android.content.Intent;
import edu.wm.cs.cs301.cullenrombach.R;

public class AMazeActivity extends Activity {
	
	private static final String TAG = "AMazeActivity";
	private SeekBar sizeBar;
	private Spinner generatorSpinner;
	private String generatorType;
	private int mazeSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_amaze);
		
		// Store the UI elements we need to interact with.
		generatorSpinner = (Spinner)findViewById(R.id.generatorSpinner);
		sizeBar = (SeekBar)findViewById(R.id.sizeBar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.amaze, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Called when the user clicks the play button, sends them to the generating
	 * activity.
	 * @param view The view that was clicked.
	 */
	public void moveToGenerating(View view) {
		// Log what we're doing.
		Log.v(TAG, "Moving to GeneratingActivity");
		
		// Get the maze generator and size values.
		generatorType = generatorSpinner.getSelectedItem().toString();
		mazeSize = sizeBar.getProgress();

		// Output the maze generator and difficulty to LogCat.
		Log.v(TAG, "Generator: " + generatorType + ", Maze Size: " + mazeSize);
		
		// Move to the next screen and send it the collected data.
		Intent intent = new Intent(this, GeneratingActivity.class);
		intent.putExtra("generatorType", generatorType);
		intent.putExtra("mazeSize", mazeSize);
		startActivity(intent);
	}
}
