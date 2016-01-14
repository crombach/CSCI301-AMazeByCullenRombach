package edu.wm.cs.cs301.cullenrombach.falstad;

import edu.wm.cs.cs301.cullenrombach.falstad.Robot.Direction;
import edu.wm.cs.cs301.cullenrombach.falstad.Robot.Turn;

/**
 * Drives a robot to the exit of that robot's maze using the
 * maze's distance matrix to get the most efficient path.
 * 
 * @author Cullen Rombach (cmromb)
 */

public class Wizard implements RobotDriver {

	private Robot robot; // Store the robot that this driver is driving.
	private Distance dists; // The remaining distance from the robot's position to the maze exit.
	private float initialBatteryLevel; // Store the initial battery level of the robot.
	private int pathLength; // The distance this robot has traveled from its starting position.
	
	/**
	 * Default constructor, sets path length to 0.
	 */
	public Wizard() {
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
	
	/**
	 * Provides the robot driver with information on the dimensions of the 2D maze
	 * measured in the number of cells in each direction.
	 * @param width of the maze
	 * @param height of the maze
	 * @precondition 0 <= width, 0 <= height of the maze.
	 */
	@Override
	public void setDimensions(int width, int height) {
		// Sanity check the dimensions.
		if(width >= 0 && height >= 0) {
			// Do nothing, this is irrelevant to this driver.
		}
	}
	
	/**
	 * Provides the robot driver with information on the distance to the exit.
	 * Only some drivers such as the wizard rely on this information to find the exit.
	 * @param distance gives the length of path from current position to the exit.
	 * @precondition null != distance, a full functional distance object for the current maze.
	 */
	@Override
	public void setDistance(Distance distance) {
		// Sanity check the distance.
		if(distance != null) {
			dists = distance;
		}
	}

	@Override
	public boolean drive2Exit() throws Exception {
		// Set up the variables we need to move through the maze.
		int[] curPos = robot.getCurrentPosition(); //[0] is the column, [1] is the row.
		int[] nextCell;
		Direction moveDirection = Direction.FORWARD;
		int curMinDist = dists.getDistance(curPos[0], curPos[1]);
		int newDist = 0;
		
		
		// Infinite loop.
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

				stepAndUpdate();

				// Return true.
				return true;
			}

			// If the robot has stopped, return false.
			if(robot.hasStopped()) {
				return false;
			}

			// Check to see if the cell in front of the robot can be moved to.
			if(robot.distanceToObstacle(Direction.FORWARD) != 0) {
				// If it can be, get the distance of that cell from the exit.
				nextCell = getCellInDirection(Direction.FORWARD);
				newDist = dists.getDistance(nextCell[0], nextCell[1]);
				// If the distance is smaller, set it to the new curMinDist and store this direction.
				if(newDist < curMinDist) {
					curMinDist = newDist;
					moveDirection = Direction.FORWARD;
				}
			}

			// Check to see if the cell behind the robot can be moved to.
			if(robot.distanceToObstacle(Direction.BACKWARD) != 0) {
				// If it can be, get the distance of that cell from the exit.
				nextCell = getCellInDirection(Direction.BACKWARD);
				newDist = dists.getDistance(nextCell[0], nextCell[1]);
				// If the distance is smaller, set it to the new curMinDist and store this direction.
				if(newDist < curMinDist) {
					curMinDist = newDist;
					moveDirection = Direction.BACKWARD;
				}
			}

			// Check to see if the cell to the left of the robot can be moved to.
			if(robot.distanceToObstacle(Direction.LEFT) != 0) {
				// If it can be, get the distance of that cell from the exit.
				nextCell = getCellInDirection(Direction.LEFT);
				newDist = dists.getDistance(nextCell[0], nextCell[1]);
				// If the distance is smaller, set it to the new curMinDist and store this direction.
				if(newDist < curMinDist) {
					curMinDist = newDist;
					moveDirection = Direction.LEFT;
				}
			}

			// Check to see if the cell to the right of the robot can be moved to.
			if(robot.distanceToObstacle(Direction.RIGHT) != 0) {
				// If it can be, get the distance of that cell from the exit.
				nextCell = getCellInDirection(Direction.RIGHT);
				newDist = dists.getDistance(nextCell[0], nextCell[1]);
				// If the distance is smaller, set it to the new curMinDist and store this direction.
				if(newDist < curMinDist) {
					curMinDist = newDist;
					moveDirection = Direction.RIGHT;
				}
			}
			
			// Move the robot in the moveDirection.
			// If it's forward:
			if(moveDirection == Direction.FORWARD) {
				stepAndUpdate();
			}
			// If it's backward:
			if(moveDirection == Direction.BACKWARD) {
				robot.rotate(Turn.AROUND);
				stepAndUpdate();
			}
			// If it's left:
			if(moveDirection == Direction.LEFT) {
				robot.rotate(Turn.LEFT);
				stepAndUpdate();
			}
			// If it's right:
			if(moveDirection == Direction.RIGHT) {
				robot.rotate(Turn.RIGHT);
				stepAndUpdate();
			}
		}
	}

	/**
	 * Move the robot forward 1 cell and update the path length.
	 * @throws Exception
	 */
	private void stepAndUpdate() throws Exception {
		// Move forward 1.
		robot.move(1);
		// Increment the pathLength.
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
	
	/**
	 * Helper method that returns the position of the cell on step in the
	 * given direction relative to the robot.
	 * @param direction The direction of the cell relative to the robot's direction.
	 * @return The The position of the cell in the given direction relative to the robot's direction.
	 */
	private int[] getCellInDirection(Direction direction) {
		try {
			// Directions for computation.
			int xdir = robot.getCurrentDirection()[0];
			int ydir = robot.getCurrentDirection()[1];
			int xpos = robot.getCurrentPosition()[0];
			int ypos = robot.getCurrentPosition()[1];
			
			// If the direction is forward, return the cell in front of the robot.
			if(direction == Direction.FORWARD) {
				return new int[] {xpos + xdir, ypos + ydir};
			}
			// If the direction is backward, return the cell behind the robot.
			else if(direction == Direction.BACKWARD) {
				return new int[] {xpos - xdir, ypos - ydir};
			}
			// If the direction is left, return the cell to the left of the robot.
			else if(direction == Direction.LEFT) {
				// If the robot is facing north.
				if(xdir == 0 && ydir == 1) {
					return new int[] {xpos - 1, ypos};
				}
				// If the robot is facing east.
				else if(xdir == 1 && ydir == 0) {
					return new int[] {xpos, ypos + 1};
				}
				// If the robot is facing south.
				else if(xdir == 0 && ydir == -1) {
					return new int[] {xpos + 1, ypos};
				}
				// If the robot is facing west.
				else {
					return new int[] {xpos, ypos - 1};
				}
			}
			// If the direction is right, return the cell to the right of the robot.
			else {
				// If the robot is facing north.
				if(xdir == 0 && ydir == 1) {
					return new int[] {xpos + 1, ypos};
				}
				// If the robot is facing east.
				else if(xdir == 1 && ydir == 0) {
					return new int[] {xpos, ypos - 1};
				}
				// If the robot is facing south.
				else if(xdir == 0 && ydir == -1) {
					return new int[] {xpos - 1, ypos};
				}
				// If the robot is facing west.
				else {
					return new int[] {xpos, ypos + 1};
				}
			}		
			
		} catch (Exception e) {
			// Return something to satisfy the compiler.
			return new int[] {0, 0};
		}
	}

}
