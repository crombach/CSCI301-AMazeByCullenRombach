package edu.wm.cs.cs301.cullenrombach.falstad;

/**
 * Responsibilities: This class contains the methods necessary to operates a robot that is
 * inside a maze at a particular location and looking in a particular direction.
 * 
 * Collaborators: a Maze class to be explored, a RobotDriver class that operates the BasicRobot, and a Cells class
 * for checking characteristics about the maze cell the robot is in.
 * 
 * @author Cullen Rombach (cmromb)
 */

public class BasicRobot implements Robot {
	
	// The fields used by this robot.
	private Maze myMaze; // The maze that this robot is operating on.
	private float myBattery; // The amount of charge left in the robot's battery.
	private int[] myPosition; // The current position of this robot in the maze. [0] is x, [1] is y.
	private int[] myDirection; // The x and y direction of the current robot. [0] is x, [1] is y.
	
	// This robot's sensors.
	private boolean forwardSensor;
	private boolean backwardSensor;
	private boolean rightSensor;
	private boolean leftSensor;
	private boolean roomSensor;
	private boolean isMoving;
	
	// The costs of all the robot's actions.
	private static final float costToSenseDist = 1;
	private static final float costToRotate = 3;
	private static final float costToMove = 5;
	
	/**
	 * The default constructor for a new BasicRobot object.
	 * Sets up the robot's sensors and fields.
	 */
	public BasicRobot() {
		// Set the battery to the initial value of 2500.
		setBatteryLevel(2500);
		
		// Set all the sensors to true.
		forwardSensor = true;
		backwardSensor = true;
		leftSensor = true;
		rightSensor = true;
		roomSensor = true;
		
		// The robot is initially moving (has battery > 0).
		isMoving = true;
	}

	/**
	 * Constructs a new BasicRobot object and sets up its sensors and fields.
	 * This constructor also sets the Maze the robot will operate on.
	 * @param maze is the Maze this robot will operate on.
	 */
	public BasicRobot(Maze maze) {
		// Set the battery to the initial value of 2500.
		setBatteryLevel(2500);
		
		// Set all the sensors to true.
		forwardSensor = true;
		backwardSensor = true;
		leftSensor = true;
		rightSensor = true;
		roomSensor = true;
		
		// The robot is initially moving (has battery > 0).
		isMoving = true;
		
		// Set this robot's maze to the given maze.
		setMaze(maze);
	}

	/**
	 * Rotates the robot, LEFT, RIGHT, or AROUND.
	 * @param turn is the direction the robot will rotate.
	 * @throws Exception if the robot stops for lack of energy. 
	 */
	public void rotate(Turn turn) throws Exception {
		// Stop the robot's motion if it doesn't have enough battery.
		if(myBattery < costToRotate) {
			isMoving = false;
			throw new Exception();
		}
		
		// Turn the robot in the requested direction.
		// Left
		if(turn == Turn.LEFT) {
			myMaze.rotate(1);
			// Update this robot's direction.
			myDirection = myMaze.getCurrentDirection();
			// Update the battery level.
			setBatteryLevel(myBattery - getEnergyForFullRotation() / 4);
		}
		// Right
		else if(turn == Turn.RIGHT) {
			myMaze.rotate(-1);
			// Update this robot's direction.
			myDirection = myMaze.getCurrentDirection();
			// Update the battery level.
			setBatteryLevel(myBattery - getEnergyForFullRotation() / 4);
		}
		// Around
		else {
			// Make two right turns to turn around.
			myMaze.rotate(-1);
			myMaze.rotate(-1);
			// Update this robot's direction.
			myDirection = myMaze.getCurrentDirection();
			// Update the battery level.
			setBatteryLevel(myBattery - getEnergyForFullRotation() / 2);
		}
	}

	/**
	 * Moves robot forward a given number of steps.
	 * If the robot runs out of energy somewhere on its way, it stops.
	 * If the robot hits an obstacle like a wall, it remains at the position in front 
	 * of the obstacle but hasStopped() == false.
	 * @param distance is the number of cells to move in the robot's current forward direction
	 * @throws Exception if robot hits an obstacle like a wall or border, which indicates that
	 * current position is not as expected. Also thrown if robot runs out of energy. 
	 * @precondition distance >= 0
	 */
	public void move(int distance) throws Exception {
		// Store the distance the robot still needs to traverse.
		int distanceLeft = distance;
		
		// Update the robot's current position and direction.
		myPosition = myMaze.getCurrentPosition();
		myDirection = myMaze.getCurrentDirection();
		
		// Move until the distance has been traversed.
		while(distanceLeft > 0) {
			// If the battery is too low, the robot stops.
			if(myBattery < getEnergyForStepForward() || hasStopped()) {
				isMoving = false;
				throw new Exception();
			}
			
			// If there isn't a wall:
			if(canMove(getBearing(), myPosition)) {
				// Move forward.
				myMaze.walk(1);
				// Update this robot's position.
				myPosition = myMaze.getCurrentPosition();
				// Deplete the battery.
				setBatteryLevel(myBattery - getEnergyForStepForward());
			}
			// If there is a wall:
			else {
				// Stop the robot.
				isMoving = false;
				// Throw an exception.
				throw new Exception();
			}
			
			// Decrement the distanceLeft.
			distanceLeft--;
		}
	}

	/**
	 * Provides the current position as (x,y) coordinates for the maze cell as an array of length 2 with [x,y].
	 * @return array of length 2, x = array[0], y = array[1]
	 * @throws Exception if position is outside of the maze
	 * @postcondition 0 <= x < width, 0 <= y < height of the maze. 
	 */
	public int[] getCurrentPosition() throws Exception {
		// Sanity check the position.
		if(myPosition[0] < 0 || myPosition[1] < 0 || myPosition[0] >= myMaze.getWidth() || myPosition[1] >= myMaze.getHeight()) {
			throw new Exception();
		}
		
		// Return it if it's fine.
		return myPosition;
	}

	/**
	 * Stores the given maze as the maze this robot will operate on.
	 * The maze serves as the main source of information about the current location,
	 * the presence of walls, the reaching of an exit.
	 * @param maze is the current maze
	 * @precondition maze != null, maze refers to a fully operational, configured maze object
	 */
	public void setMaze(Maze maze) {
		// Check that this is a valid maze.
		if(maze != null) {
			// Store the Maze this robot will operate on.
			myMaze = maze;
			
			// Store the position of this robot.
			myPosition = new int[2];
			myPosition = myMaze.getCurrentPosition();
			
			// Set the directions of this robot.
			myDirection = new int[2];
			myDirection = myMaze.getCurrentDirection();
		}
	}

	/**
	 * Tells if current position is at the goal (the exit). Used to recognize termination of a search.
	 * @return true if robot is at the goal, false otherwise
	 */
	public boolean isAtGoal() {
		return myMaze.getCells().isExitPosition(myPosition[0], myPosition[1]);
	}

	/**
	 * Tells if a sensor can identify the goal in given direction relative to 
	 * the robot's current forward direction from the current position.
	 * @return true if the goal (here: exit of the maze) is visible in a straight line of sight, false otherwise.
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 */
	public boolean canSeeGoal(Direction direction) throws UnsupportedOperationException {
		// The robot can see the goal (the opening to the outside of the maze)
		// only if the distance to the next object is Integer.MAX_VALUE.
		if(hasDistanceSensor(direction)) {
			return distanceToObstacle(direction) == Integer.MAX_VALUE;
		}
		// If the robot doesn't have a sensor in the given direction, return false.
		return false;
	}

	/**
	 * Tells if current position is inside a room. 
	 * @return true if robot is inside a room, false otherwise
	 * @throws UnsupportedOperationException if not supported by robot
	 */	
	public boolean isInsideRoom() throws UnsupportedOperationException {
		// If there isn't a room sensor, throw an exception.
		if(!roomSensor) {
			throw new UnsupportedOperationException();
		}
		
		// If there is a room sensor, find and return the answer.
		return myMaze.getCells().isInRoom(myPosition[0], myPosition[1]);
	}

	/**
	 * Tells if the robot has a room sensor (true if yes, false if no).
	 */
	public boolean hasRoomSensor() {
		return roomSensor;
	}

	/**
	 * Provides the current direction as (dx,dy) values for the robot as an array of length 2 with [dx,dy].
	 * Note that dx,dy are elements of {-1,0,1} and as in bitmasks masks in Cells.java and dirsx,dirsy in MazeBuilder.java.
	 * @return array of length 2, dx = array[0], dy=array[1]
	 */	
	public int[] getCurrentDirection() {
		return myDirection;
	}

	/**
	 * Returns the current battery level.
	 * If battery level <= 0 then robot stops to function and hasStopped() is true.
	 * @return current battery level, level is > 0 if operational.
	 */
	public float getBatteryLevel() {
		return myBattery;
	}

	/**
	 * Sets the current battery level.
	 * If battery level <= 0 then robot stops to function and hasStopped() is true.
	 * @param level is the current battery level
	 * @precondition level >= 0
	 */
	public void setBatteryLevel(float level) {
		myBattery = level;
	}

	/**
	 * Gives the energy consumption for a full 360 degree rotation.
	 * Scaling by other degrees approximates the corresponding consumption. 
	 * @return energy for a full rotation
	 */
	public float getEnergyForFullRotation() {
		return costToRotate * 4;
	}

	/**
	 * Gives the energy consumption for moving forward for a distance of 1 step.
	 * For simplicity, we assume that this equals the energy necessary 
	 * to move 1 step backwards and that scaling by a larger number of moves is 
	 * approximately the corresponding multiple.
	 * @return energy for a single step forward
	 */
	public float getEnergyForStepForward() {
		return costToMove;
	}

	/**
	 * Tells if the robot has stopped for reasons like lack of energy, hitting an obstacle, etc.
	 * @return true if the robot has stopped, false otherwise
	 */
	public boolean hasStopped() {
		return !isMoving;
	}

	/**
	 * Tells the distance to an obstacle (a wall or border) 
	 * in a direction as given and relative to the robot's current forward direction.
	 * Distance is measured in the number of cells towards that obstacle, 
	 * e.g. 0 if current cell has a wall in this direction
	 * @return number of steps towards obstacle if obstacle is visible 
	 * in a straight line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedOperationException if not supported by robot
	 */
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		// Store the position of this robot as the current position.
		int[] curPosition = {myPosition[0], myPosition[1]};
		
		// Store the distance to the next obstacle.
		int distanceToObstacle = 0;
		
		// Throw an exception if this robot doesn't have a sensor in the given direction.
		if(hasDistanceSensor(direction) == false) {
			throw new UnsupportedOperationException();
		}
		
		// Check if there is enough battery to do this.
		if(myBattery < costToSenseDist) {
			isMoving = false;
			return 0;
		}
		
		// Get the compass bearing of the given direction relative to the robot.
		int relBearing = getBearingRelativeToRobot(direction);
		
		// Keep adding to the distance as long as there isn't a wall in the
		// given direction.
		while(canMove(relBearing, curPosition)) {
			
			// Increment the distance to the obstacle.
			distanceToObstacle++;
			
			// If we are looking North.
			if(relBearing == 0) {
				curPosition[1]++;
			}
			// If we are looking East.
			else if(relBearing == 90) {
				curPosition[0]++;
			}
			// If we are looking South.
			else if(relBearing == 180) {
				curPosition[1]--;
			}
			// If we are looking West.
			else if(relBearing == 270) {
				curPosition[0]--;
			}
			
			// Check for infinite loop (no obstacle in line of sight)
			// by checking the current position against myMaze's width and height.
			if(curPosition[0] < 0 || curPosition[1] < 0 || curPosition[0] >= myMaze.getWidth() || curPosition[1] >= myMaze.getHeight()) {
				return Integer.MAX_VALUE;
			}
		}
		
		// Update the battery.
		setBatteryLevel(myBattery - costToSenseDist);
		
		// Return the distance to the next obstacle.
		return distanceToObstacle;
	}

	/**
	 * Tells if the robot has a distance sensor for the given direction.
	 * @return true if yes, false otherwise.
	 */
	public boolean hasDistanceSensor(Direction direction) {
		// Check the given direction for a sensor.
		if(direction == Direction.FORWARD) {
			return forwardSensor;
		}
		else if(direction == Direction.BACKWARD) {
			return backwardSensor;
		}
		else if(direction == Direction.LEFT) {
			return leftSensor;
		}
		// If the direction was right.
		else {
			return rightSensor;
		}
	}
	
	/**
	 * Tells whether or not there is a wall in front of the robot.
	 * Used as a helper method in distanceToObstacle() and move().
	 * @return true if there is not a wall in front of the robot, false otherwise.
	 */
	private boolean canMove(int bearing, int[] position) {
		// Store a boolean to return.
		boolean canMove = false;
		
		// If the robot is facing north.
		if(bearing == 0) {
			if(myMaze.getCells().hasNoWallOnBottom(position[0], position[1])) {
				canMove = true;
			}
		}
		// If the robot is facing east.
		else if(bearing == 90) {
			if(myMaze.getCells().hasNoWallOnRight(position[0], position[1])) {
				canMove = true;
			}
		}
		// If the robot is facing south.
		else if(bearing == 180) {
			if(myMaze.getCells().hasNoWallOnTop(position[0], position[1])) {
				canMove = true;
			}
		}
		// If the robot is facing west.
		else if(bearing == 270) {
			if(myMaze.getCells().hasNoWallOnLeft(position[0], position[1])) {
				canMove = true;
			}
		}
		
		// Return the correct boolean value.
		return canMove;
	}
	
	/**
	 * Helper method used throughout the code, gives the robot's current compass bearing.
	 * The top of the maze in the GUI is considered to be North.
	 * 0 is North, 90 is East, 180 is South, 270 is West.
	 * @return the robot's current compass bearing.
	 */
	private int getBearing() {
		// If the robot is facing north.
		if(myDirection[0] == 0 && myDirection[1] == 1) {
			return 0;
		}
		// If the robot is facing east.
		if(myDirection[0] == 1 && myDirection[1] == 0) {
			return 90;
		}
		// If the robot is facing south.
		if(myDirection[0] == 0 && myDirection[1] == -1) {
			return 180;
		}
		// If the robot is facing west.
		else {
			return 270;
		}
	}
	
	/**
	 * Helper method used in distanceToObstacle, gives the compass bearing
	 * of the given direction relative to the robot.
	 * The top of the maze in the GUI is considered to be North.
	 * 0 is North, 90 is East, 180 is South, 270 is West.
	 * @return the robot's current compass bearing.
	 */
	private int getBearingRelativeToRobot(Direction direction) {
		// If the robot is facing north.
		if(myDirection[0] == 0 && myDirection[1] == 1) {
			if(direction == Direction.FORWARD) {
				return 0;
			}
			if(direction == Direction.BACKWARD) {
				return 180;
			}
			if(direction == Direction.LEFT) {
				return 270;
			}
			// If the direction was right.
			else {
				return 90;
			}
		}
		// If the robot is facing east.
		if(myDirection[0] == 1 && myDirection[1] == 0) {
			if(direction == Direction.FORWARD) {
				return 90;
			}
			if(direction == Direction.BACKWARD) {
				return 270;
			}
			if(direction == Direction.LEFT) {
				return 0;
			}
			// If the direction was right.
			else {
				return 180;
			}
		}
		// If the robot is facing south.
		if(myDirection[0] == 0 && myDirection[1] == -1) {
			if(direction == Direction.FORWARD) {
				return 180;
			}
			if(direction == Direction.BACKWARD) {
				return 0;
			}
			if(direction == Direction.LEFT) {
				return 90;
			}
			// If the direction was right.
			else {
				return 270;
			}
		}
		// If the robot is facing west.
		else {
			if(direction == Direction.FORWARD) {
				return 270;
			}
			if(direction == Direction.BACKWARD) {
				return 90;
			}
			if(direction == Direction.LEFT) {
				return 180;
			}
			// If the direction was right.
			else {
				return 0;
			}
		}
	}
}
