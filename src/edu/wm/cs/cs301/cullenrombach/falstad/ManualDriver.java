package edu.wm.cs.cs301.cullenrombach.falstad;

/**
 * Responsibilities: This class enables the manual operation of a Robot in order to escape from a given maze.
 * 
 * Collaborators: A Robot class which the driver controls, and a Maze class from which the driver receives
 * information about keyboard inputs.
 * 
 * @author Cullen Rombach (cmromb)
 */

public class ManualDriver implements RobotDriver {
	
	private Robot robot; // Store the robot that this driver is driving.
	private float initialBatteryLevel; // Store the initial battery level of the robot.
	private int pathLength; // The distance this robot has traveled from its starting position.
	
	/**
	 * The default constructor for a manual driver, sets the path length to 0.
	 */
	public ManualDriver() {
		pathLength = 0;
	}

	/**
	 * Assigns a robot platform to the driver. 
	 * The driver uses a robot to perform, this method provides it with this necessary information.
	 * @param r robot to operate
	 */
	@Override
	public void setRobot(Robot r) {
		robot = r;
		// Store the robot's initial battery level for use in getEnergyConsumption().
		initialBatteryLevel = robot.getBatteryLevel();
	}
	
	public Robot getRobot() {
		return robot;
	}

	/**
	 * Provides the robot driver with information on the dimensions of the 2D maze
	 * measured in the number of cells in each direction.
	 * @param width of the maze
	 * @param height of the maze
	 * @precondition 0 <= width, 0 <= height of the maze.
	 */
	@Override
	public void setDimensions(int width, int height) {
		// Do nothing, not necessary for the manual driver.
	}
	
	/**
	 * Provides the robot driver with information on the distance to the exit.
	 * Only some drivers such as the wizard rely on this information to find the exit.
	 * @param distance gives the length of path from current position to the exit.
	 * @precondition null != distance, a full functional distance object for the current maze.
	 */
	@Override
	public void setDistance(Distance distance) {
		// Do nothing, not relevant to this driver.
	}
	
	/**
	 * This method doesn't do much since the robot is driven manually.
	 * @return true if the robot is at the exit, false otherwise
	 * @throws exception if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		// Throw an exception if the robot has stopped moving.
		if(getRobot().hasStopped()) {
			throw new Exception();
		}
		
		// If the robot is at the exit, return true.
		if(getRobot().isAtGoal()) {
			return true;
		}
		
		// Otherwise, return false.
		return false;
	}
	
	/**
	 * Returns the total energy consumption of the journey, i.e.,
	 * the difference between the robot's initial energy level at
	 * the starting position and its energy level at the exit position. 
	 * This is used as a measure of efficiency for a robot driver.
	 */
	@Override
	public float getEnergyConsumption() {
		return initialBatteryLevel - robot.getBatteryLevel();
	}
	
	/**
	 * Returns the total length of the journey in number of cells traversed. 
	 * Being at the initial position counts as 0. 
	 * This is used as a measure of efficiency for a robot driver.
	 */
	@Override
	public int getPathLength() {
		return pathLength;
	}
	
	/* These methods move the robot manually. */
	
	/**
	 * Moves the robot forward by one cell.
	 * @throw an exception if the robot hits a wall.
	 */
	public void moveForward() throws Exception {
		// Move forward by 1 space.
		getRobot().move(1);
		// Update the path length.
		pathLength++;
	}
	
	/**
	 * Moves the robot backward by one cell by turning it around
	 * and then moving forward by one cell.
	 * @throw an exception if the robot hits a wall.
	 */
	public void moveBackward() throws Exception {
		// Turn the robot around.
		getRobot().rotate(Robot.Turn.AROUND);
		// Move forward by 1 space.
		getRobot().move(1);
		// Update the path length.
		pathLength++;
	}
	
	/**
	 * Turns the robot 90 degrees to the right.
	 * @throw an exception if the robot hits a wall.
	 */
	public void turnRight() throws Exception {
		// Turn the robot right.
		getRobot().rotate(Robot.Turn.RIGHT);
	}
	
	/**
	 * Turns the robot 90 degrees to the left.
	 * @throw an exception if the robot hits a wall.
	 */
	public void turnLeft() throws Exception {
		// Turn the robot left.
		getRobot().rotate(Robot.Turn.LEFT);
	}
}
