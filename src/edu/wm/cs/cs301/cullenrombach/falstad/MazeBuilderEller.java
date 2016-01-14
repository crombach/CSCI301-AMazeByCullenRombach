package edu.wm.cs.cs301.cullenrombach.falstad;

/**
 * MazeBuilder that uses Eller's algorithm for efficient perfect maze creation.
 * 
 * @author Cullen Rombach (cmromb)
 */

public class MazeBuilderEller extends MazeBuilder {
	
	// Given input information: 
	protected int width, height ; 	// width and height of maze, 
	Maze maze; 				// reference to the maze that is constructed, results are returned by calling maze.newMaze(..)
	int rooms; // Numbers of rooms in the maze.
	int expectedPartiters; 	// user given limit for partiters
		
	// Produced output information to create the new maze:
	protected int startx, starty ; // starting position inside maze for entity to search for exit
	// conventional encoding of maze as a 2 dimensional integer array encapsulated in the Cells class
	// a single integer entry can hold information on walls, borders/bounds
	protected Cells cells; // the internal representation of a maze as a matrix of cells
	protected Distance dists ; // distance matrix that stores how far each position is away from the exit position

	// Class internal local variables:
	protected SingleRandom random ; // random number stream, used to make randomized decisions, e.g for direction to go

	/**
	 * Default constructor, initializes the random number generator.
	 */
	public MazeBuilderEller() {
		random = SingleRandom.getRandom();
	}
	
	/**
	 * This method generates a maze.
	 * It computes distances, determines a start and exit position that are as far apart as possible.
	 * 
	 * This code is taken from the MazeBuilder class.
	 */
	@Override
	protected void generate() {
		// generate paths in cells such that there is one strongly connected component
		// i.e. between any two cells in the maze there is a path to get from one to the other
		// the search algorithms starts at some random point
		generatePathways(); 

		final int[] remote = dists.computeDistances(cells) ;

		// identify cell with the greatest distance
		final int[] pos = dists.getStartPosition();
		startx = pos[0] ;
		starty = pos[1] ;

		// make exit position at true exit in the cells data structure
		cells.setExitPosition(remote[0], remote[1]);
	}
	
	/**
	 * This method generates the maze's pathways using Eller's algorithm. It works in the same
	 * order that you would read a page of a book -- starts in the top left, ends in the bottom right.
	 */
	@Override
	protected void generatePathways() {
		// Declare the necessary variables.
		boolean debug = false; // If true, the cellSets array will print out after each iteration.
		int[][] cellSets = new int[width][height]; // Holds the set that each maze cell is in.
		int curRow = 0; // Stores which row number we are currently building.
		int curCol = 0; // Stores the current column for vertical wall-building.
		int nextSet = 1; // The next unique set (for grouping cells)
		int randomNum = 0; // Used for storing random numbers.
		
		/** Initialize each cell to 0 to indicate that it has no set. */
		for(int col = 0; col < width; col++) {
			for(int row = 0; row < height; row++) {
				cellSets[col][row] = 0;
			}
		}
		
		/** Loop through all the rows. */
		for(curRow = 0; curRow < height; curRow++) {
		
			/** If we are in the first row: */
			if(curRow == 0) {
				/** Create the first row. Put each cell in its own set and update the set size. */
				for(int col = 0; col < width; col++) {
					cellSets[col][0] = nextSet++;
				}
			}
		
			/** If we aren't in the first row: */
			else {
				/** Copy the cells from the row above. */
				for(int col = 0; col < width; col++) {
					/** If there is a wall above a cell, put it in its own new set. */
					if(cells.hasWallOnTop(col, curRow)) {
						cellSets[col][curRow] = nextSet++;
					}
					/** Otherwise, put it in the same set as the cell above. */
					else {
						cellSets[col][curRow] = cellSets[col][curRow - 1];
					}
				}
			}
			
			/** Set up the right-walls, moving from left to right: */
			for(curCol = 0; curCol < width; curCol++) {
				
				/** If we are in the last row: */
				if(curRow == height - 1) {
					/** Loop through and remove all the right walls between different sets. */
					for(int col = 0; col < width - 1; col++) {
						if(cellSets[col][curRow] != cellSets[col + 1][curRow]) {
							cells.deleteWall(col,  curRow,  1,  0);
							// Join the sets.
							cellSets[col + 1][curRow] = cellSets[col][curRow];
						}
					}
					/** Then, stop building right-walls because we're done. */
					break;
				}
				
				/** If we aren't in the last column, we can set up the right walls. */
				if(curCol != width - 1) {
					/** If this cell and the cell to the right are in different sets and there's not a border: */
					if(cellSets[curCol][curRow] != cellSets[curCol + 1][curRow]  && cells.canGo(curCol, curRow, 1, 0)) {
						
						/** Randomly decide whether or not to delete the right wall. */
						randomNum = random.nextIntWithinInterval(0, 1);
						
						/** Delete the right wall if the random number generator told us to. */
						if(randomNum == 1) {
							cells.deleteWall(curCol, curRow, 1, 0);
							/// Join the two sets the wall was separating.
							cellSets[curCol + 1][curRow] = cellSets[curCol][curRow];
						}
					}
				}
			}
				
			/** Set up bottom walls, moving from left to right: */
			for(curCol = 0; curCol < width; curCol++) {
				
				/** Store the column where this set started. */
				// This will be used later in the loop for wall deletion.
				int setStart = curCol;
				
				/** If we've reached the last row, we don't want to mess with bottom walls so break the loop. */
				if(curRow == height - 1) {
					break;
				}
				
				/**
				 * If we are in the last column, delete the bottom wall and break the loop.
				 * This should only happen if the last cell is in its own set.
				 */
				if(curCol == width - 1) {
					cells.deleteWall(curCol, curRow, 0, 1);
					// Join the two sets.
					cellSets[curCol][curRow + 1] = cellSets[curCol][curRow];
					break;
				}
				
				/** Loop through cells until we get to a different set. */
				while(curCol < width - 1) {
					/** If this cell and the next cell are in the same set: */
					/** Move to the next column and keep looping. */
					if(cellSets[curCol][curRow] == cellSets[curCol+1][curRow]) {
						curCol++;
					}
					/** Otherwise, stop looping. */
					else {
						break;
					}
				}
				
				/** Randomly choose a wall to delete from this set.  Try not to break down barriers.*/
				int tries = 0;
				randomNum = random.nextIntWithinInterval(setStart, curCol);
				while(tries < (curCol - setStart)*5 && !cells.canGo(randomNum, curRow, 0, 1)) {
					randomNum = random.nextIntWithinInterval(setStart, curCol);
					tries++;
				}
				// Delete the wall and join the two sets.
				cells.deleteWall(randomNum, curRow, 0, 1);
				cellSets[randomNum][curRow + 1] = cellSets[randomNum][curRow];
			}
			
			// Print out the cell sets for debugging.
			if(debug) {
				for(int row = height-1; row >= 0; row--) {
					for(int col = 0; col < width; col++) {
						System.out.print(cellSets[col][row] + "\t");
					}
					System.out.print("\n\n");
				}
				System.out.println("");
			}
		}
	}
	
	static final int MAX_TRIES = 250 ;

	/**
	 * Generate all rooms in a given maze where initially all walls are up. Rooms are placed randomly and of random sizes
	 * such that the maze can turn out to be too small to accommodate the requested number of rooms (class attribute rooms). 
	 * In that case less rooms are produced.
	 * 
	 * This code is taken from the MazeBuilder class.
	 * 
	 * @return generated number of rooms
	 */
	private int generateRooms() {
		// Rooms are randomly positioned such that it may be impossible to place the all rooms if the maze is too small
		// to prevent an infinite loop we limit the number of failed to MAX_TRIES == 250
		int tries = 0 ;
		int result = 0 ;
		while (tries < MAX_TRIES && result <= rooms) {
			if (placeRoom())
				result++ ;
			else
				tries++ ;
		}
		return result ;
	}
	
	static final int MIN_ROOM_DIMENSION = 3 ;
	static final int MAX_ROOM_DIMENSION = 8 ;
	/**
	 * Allocates space for a room of random dimensions in the maze.
	 * The position of the room is chosen randomly. The method is not sophisticated 
	 * such that the attempt may fail even if the maze has ample space to accommodate 
	 * a room of the chosen size. 
	 * 
	 * This code is taken from the MazeBuilder class.
	 * 
	 * @return true if room is successfully placed, false otherwise
	 */
	private boolean placeRoom() {
		// get width and height of random size that are not too large
		// if too large return as a failed attempt
		final int rw = random.nextIntWithinInterval(MIN_ROOM_DIMENSION, MAX_ROOM_DIMENSION);
		if (rw >= width-4)
			return false;

		final int rh = random.nextIntWithinInterval(MIN_ROOM_DIMENSION, MAX_ROOM_DIMENSION);
		if (rh >= height-4)
			return false;
		
		// proceed for a given width and height
		// obtain a random position (rx,ry) such that room is located on as a rectangle with (rx,ry) and (rxl,ryl) as corner points
		// upper bound is chosen such that width and height of room fits maze area.
		final int rx = random.nextIntWithinInterval(1, width-rw-1);
		final int ry = random.nextIntWithinInterval(1, height-rh-1);
		final int rxl = rx+rw-1;
		final int ryl = ry+rh-1;
		// check all cells in this area if they already belong to a room
		// if this is the case, return false for a failed attempt
		if (cells.areaOverlapsWithRoom(rx, ry, rxl, ryl))
			return false ;
		// since the area is available, mark it for this room and remove all walls
		// from this on it is clear that we can place the room on the maze
		cells.markAreaAsRoom(rw, rh, rx, ry, rxl, ryl); 
		return true;
	}
	
	/**
	 * Fill the given maze object with a newly computed maze according to parameter settings
	 * @param mz maze to be filled
	 * @param w width of requested maze
	 * @param h height of requested maze
	 * @param roomct number of rooms
	 * @param pc number of expected partiters
	 */
	@Override
	public void build(Maze mz, int w, int h, int roomct, int pc) {
		init(mz, w, h, roomct, pc);
		buildThread = new Thread(this);
		buildThread.start();
	}
	
	/**
	 * Initialize internal attributes, method is called by build() when input parameters are provided
	 * @param mz maze to be filled
	 * @param w width of requested maze
	 * @param h height of requested maze
	 * @param roomct number of rooms
	 * @param pc number of expected partiters
	 */
	private void init(Maze mz, int w, int h, int roomct, int pc) {
		// Store parameters.
		maze = mz;
		width = w;
		height = h;
		rooms = roomct;
		expectedPartiters = pc;
		
		// Initialize data structures.
		cells = new Cells(w,h) ;
		dists = new Distance(w,h) ;
	}
	
	static final long SLEEP_INTERVAL = 100 ; //unit is millisecond
	/**
	 * Main method to run construction of a new maze with a MazeBuilderEller in a thread of its own.
	 * This method is called internally by the build method when it sets up and starts a new thread for this object.
	 */
	public void run() {
		// try-catch block to recognize if thread is interrupted
		try {
			// create an initial invalid maze where all walls and borders are up
			cells.initialize();
			
			// place rooms in maze
			generateRooms();
			
			Thread.sleep(SLEEP_INTERVAL) ; // test if thread has been interrupted, i.e. notified to stop

			// put pathways into the maze, determine its starting and end position and calculate distances
			generate();

			Thread.sleep(SLEEP_INTERVAL) ; // test if thread has been interrupted, i.e. notified to stop

			final int colchange = random.nextIntWithinInterval(0, 255); // used in the constructor for Segments  class Seg
			final BSPBuilder b = new BSPBuilder(maze, dists, cells, width, height, colchange, expectedPartiters) ;
			BSPNode root = b.generateBSPNodes();
			Thread.sleep(SLEEP_INTERVAL) ; // test if thread has been interrupted, i.e. notified to stop

			// dbg("partiters = "+partiters);
			// communicate results back to maze object
			maze.newMaze(root, cells, dists, startx, starty);
		}
		catch (InterruptedException ex) {
			// necessary to catch exception to avoid escalation
			// exception mechanism basically used to exit method in a controlled way
			// no need to clean up internal data structures
			dbg("Catching signal to stop") ;
		}
	}
}
