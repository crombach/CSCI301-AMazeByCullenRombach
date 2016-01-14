/**
 * 
 */
package edu.wm.cs.cs301.cullenrombach.falstad;
import edu.wm.cs.cs301.cullenrombach.falstad.Robot.Direction;
import edu.wm.cs.cs301.cullenrombach.falstad.Robot.Turn;

/**
 * Drives a robot to the exit of that robot's maze by following
 * the wall directly to the left of that robot at all times.
 * 
 * @author Cullen Rombach (cmromb)
 */

public class WallFollower implements RobotDriver {

	private Robot robot; // Store the robot that this driver is driving.
	private float initialBatteryLevel; // Store the initial battery level of the robot.
	private int pathLength; // The distance this robot has traveled from its starting position.

	/**
	 * Default constructor sets path length to 0.
	 */
	public WallFollower() {
		pathLength = 0;
	}
	
	/**
	 * Assigns a robot platform to the driver.
	 * @param r The robot to operate
	 */
	@Override
	public void setRobot(Robot r) {
		// Set this driver's robot.
		robot = r;
		
		// Store the robot's initial battery level for use in getEnergyConsumption().
		initialBatteryLevel = robot.getBatteryLevel();
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
		// Do nothing since this robot doesn't need to know dimensions.
	}
	
	/**
	 * Provides the robot driver with information on the distance to the exit.
	 * Not used in this driver.
	 * @param distance gives the length of path from current position to the exit.
	 * @precondition null != distance, a fully functional distance object for the current maze.
	 */
	@Override
	public void setDistance(Distance distance) {
		// Do nothing.
	}

	/**
	 * This method doesn't do much since the robot is driven manually.
	 * @return true if the robot reaches the exit, false otherwise
	 * @throws exception if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive2Exit() throws Exception {		
		// Keep looping while the robot is not at the goal.
		while(true) {
			
			// If the robot is at the goal go through the exit and return true.
			if(robot.isAtGoal()) {
				// Go through the exit.
				if(robot.canSeeGoal(Direction.LEFT)) {
					robot.rotate(Turn.LEFT);
				}
				else if(robot.canSeeGoal(Direction.RIGHT)) {
					robot.rotate(Turn.RIGHT);
				}
				else if(robot.canSeeGoal(Direction.BACKWARD)) {
					robot.rotate(Turn.AROUND);
				}

				// Move the robot through the exit.
				stepAndUpdate();

				// Return true.
				return true;
			}
			
			// Return false if the robot has stopped and is not at the goal.
			if(robot.hasStopped()) {
				return false;
			}
			
			// If there is a wall to the robot's left:
			if(robot.distanceToObstacle(Direction.LEFT) == 0) {
				// If there isn't a wall in front of the robot, move forward 1.
				if(robot.distanceToObstacle(Direction.FORWARD) > 0) {
					stepAndUpdate();
				}
				// If there is a wall in front of the robot, turn right.
				else {
					robot.rotate(Turn.RIGHT);
				}
			}
			
			// If there isn't a wall to the robot's left:
			else {
				// Turn left and move to the next left wall.
				robot.rotate(Turn.LEFT);
				// If there isn't a wall in front of the robot, move forward 1.
				if(robot.distanceToObstacle(Direction.FORWARD) != 0) {
					stepAndUpdate();
				}
				// If there is a wall in front of the robot, turn right.
				else {
					robot.rotate(Turn.RIGHT);
				}
			}
		}
	}

	/**
	 * Move the Robot forward 1 space and then update the path length.
	 * @throws Exception
	 */
	private void stepAndUpdate() throws Exception {
		// Move forward 1.
		robot.move(1);
		// Update the path length.
		pathLength++;
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

}
