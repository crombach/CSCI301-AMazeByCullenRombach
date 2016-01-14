/**
 * 
 */
package edu.wm.cs.cs301.cullenrombach.falstad;

import edu.wm.cs.cs301.cullenrombach.falstad.Robot.Direction;
import edu.wm.cs.cs301.cullenrombach.falstad.Robot.Turn;

/**
 * Drives a robot to the exit of that robot's maze by
 * randomly choosing a direction to move in, with higher weights
 * given to directions with cells that have not yet been visited.
 * 
 * @author Cullen Rombach (cmromb)
 */

public class CuriousMouse implements RobotDriver {
	
	SingleRandom random; // Random number generator for decision-making.
	private int[][] visitedCells; // Stores whether or not each cell in the maze has been visited.
	private Robot robot; // Store the robot that this driver is driving.
	private int width, height; // Store the dimensions of the maze this driver's robot is in.
	private float initialBatteryLevel; // Store the initial battery level of the robot.
	private int pathLength; // The distance this robot has traveled from its starting position.

	/**
	 * Default constructor, sets path length to 0 and initializes the random
	 * number generator.
	 */
	public CuriousMouse() {
		pathLength = 0;
		random = SingleRandom.getRandom();
	}

	/**
	 * Assigns a robot platform to the driver.
	 * Also sets up this driver's visitedCells array.
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
	 * Also initializes the visitedCells array to be the specified size.
	 * @param width of the maze
	 * @param height of the maze
	 * @precondition 0 <= width, 0 <= height of the maze.
	 */
	@Override
	public void setDimensions(int width, int height) {
		// Sanity check the dimensions.
		if(width >= 0 && height >= 0) {
			this.width = width;
			this.height = height;
		}
		
		// Set up the visitedCells array.
		visitedCells = new int[this.width][this.height];
	}

	/**
	 * Does nothing for this robot driver.
	 * @param distance gives the length of path from current position to the exit.
	 * @precondition null != distance, a full functional distance object for the current maze.
	 */
	@Override
	public void setDistance(Distance distance) {
		// Do nothing.
	}

	@Override
	public boolean drive2Exit() throws Exception {
		// Declare the variables used in this algorithm.
		int[] tempCell;
		int forwardWeight;
		int backwardWeight;
		int leftWeight;
		int rightWeight;
		int range; // Stores the sum total of all combined weights.
		int randomNum; // Used to store random numbers that have been generated.
		
		// Mark the robot's initial position as visited.
		markAsVisited(robot.getCurrentPosition());
		
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
				
				// Move the robot through the exit.
				robot.move(1);
				pathLength++;
				
				// Return true.
				return true;
			}

			// If the robot stopped for some reason, return false.
			if(robot.hasStopped()) {
				return false;
			}
			
			// If the robot is in a room, follow the left wall to the nearest exit.
			while(robot.isInsideRoom()) {
				// If there is a wall to the robot's left:
				if(robot.distanceToObstacle(Direction.LEFT) == 0) {
					// If there isn't a wall in front of the robot, move forward 1.
					if(robot.distanceToObstacle(Direction.FORWARD) != 0) {
						stepMarkUpdate();
					}
					// If there is a wall in front of the robot, turn right.
					else {
						robot.rotate(Turn.RIGHT);
					}
				}
				// If there isn't a wall on the left side of the robot,
				// turn left and move until there is one.
				else {
					// Turn left and move to the next left wall.
					robot.rotate(Turn.LEFT);
					// If there isn't a wall in front of the robot, move forward 1.
					if(robot.distanceToObstacle(Direction.FORWARD) != 0) {
						stepMarkUpdate();
					}
					// If there is a wall in front of the robot, turn right.
					else {
						robot.rotate(Turn.RIGHT);
					}
				}
			}
			
			
			// Reset the weights of each direction.
			forwardWeight = 0;
			backwardWeight = 0;
			leftWeight = 0;
			rightWeight = 0;
						
			// Reset the total range for the RNG.
			range = 0;

			// Randomly choose which direction to go,
			// with heavier weight given to cells that haven't been visited.
			
			// Check to see if the cell in front of the robot can be moved to.
			if(robot.distanceToObstacle(Direction.FORWARD) != 0) {
				tempCell = getCellInDirection(Direction.FORWARD);
				// If the cell has been visited, assign a low weight to it.
				// This weight gets exponentially smaller the more times the cell
				// has been visited in order to prevent the mouse getting stuck.
				if(visited(tempCell)) {
					forwardWeight = 1000 / (timesVisited(tempCell) * timesVisited(tempCell));
				}
				// Otherwise, give it a high weight.
				else {
					forwardWeight = 1000000;
				}
				// Don't let the weight be 0, or there could be an error.
				if(forwardWeight == 0) {
					forwardWeight = 1;
				}
				// Update the total range.
				range += forwardWeight;
			}

			// Check to see if the cell behind the robot can be moved to.
			if(robot.distanceToObstacle(Direction.BACKWARD) != 0) {
				tempCell = getCellInDirection(Direction.BACKWARD);
				// If the cell has been visited, assign a low weight to it.
				// This is lower than other weights because the mouse doesn't
				// want to go backwards if possible.
				if(visited(tempCell)) {
					backwardWeight = 1;
				}
				// Otherwise, give it a high weight.
				else {
					backwardWeight = 1000000;
				}
				// Update the total range.
				range += backwardWeight;
			}

			// Check to see if the cell to the left of the robot can be moved to.
			if(robot.distanceToObstacle(Direction.LEFT) != 0) {
				tempCell = getCellInDirection(Direction.LEFT);
				// If the cell has been visited, assign a low weight to it.
				// This weight gets exponentially smaller the more times the cell
				// has been visited in order to prevent the mouse getting stuck.
				if(visited(tempCell)) {
					leftWeight = 1000 / (timesVisited(tempCell) * timesVisited(tempCell));
				}
				// Otherwise, give it a high weight.
				else {
					leftWeight = 1000000;
				}
				// Don't let the weight be 0, or there could be an error.
				if(leftWeight == 0) {
					leftWeight = 1;
				}
				// Update the total range.
				range += leftWeight;
			}

			// Check to see if the cell to the right of the robot can be moved to.
			if(robot.distanceToObstacle(Direction.RIGHT) != 0) {
				tempCell = getCellInDirection(Direction.RIGHT);
				// If the cell has been visited, assign a low weight to it.
				// This weight gets exponentially smaller the more times the cell
				// has been visited in order to prevent the mouse getting stuck.
				if(visited(tempCell)) {
					rightWeight = 1000 / (timesVisited(tempCell) * timesVisited(tempCell));
				}
				// Otherwise, give it a high weight.
				else {
					rightWeight = 1000000;
				}
				// Don't let the weight be 0, or there could be an error.
				if(rightWeight == 0) {
					rightWeight = 1;
				}
				// Update the total range.
				range += rightWeight;
			}
			
			// Generate a random number in the range.
			randomNum = random.nextIntWithinInterval(1, range);
			
			// Decide which direction to take based on the RNG.
			
			// If the direction is forward:
			if(randomNum <= forwardWeight) {
				stepMarkUpdate();
			}
			// If the direction is backward:
			else if(randomNum <= forwardWeight + backwardWeight) {
				// Turn the robot around and move it forward 1.
				robot.rotate(Turn.AROUND);
				stepMarkUpdate();
			}
			// If the direction is left:
			else if(randomNum <= forwardWeight + backwardWeight + leftWeight) {
				// Turn the robot left and move it forward 1.
				robot.rotate(Turn.LEFT);
				stepMarkUpdate();
			}
			// If the direction is right:
			else {
				// Turn the robot right and move it forward 1.
				robot.rotate(Turn.RIGHT);
				stepMarkUpdate();
			}
		}
	}

	/**
	 * Move the robot forward 1, mark the new position as visited, and update
	 * the driver's path length.
	 * @throws Exception
	 */
	private void stepMarkUpdate() throws Exception {
		// Move the robot forward 1.
		robot.move(1);
		// Mark this cell as visited.
		markAsVisited(robot.getCurrentPosition());
		// Update the robot driver's path length.
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
	 * Helper method that returns whether or not the given cell location has been visited
	 * by the robot.
	 * @param cell The cell for which the number of times visited will be checked.
	 */
	private boolean visited(int[] cell) {
		return visitedCells[cell[0]][cell[1]] != 0;
	}
	
	/**
	 * Helper method that marks the given cell as visited.
	 * @param cell The cell for which the number of times visited will be updated.
	 */
	private void markAsVisited(int[] cell) {
		visitedCells[cell[0]][cell[1]]++;
	}
	
	/**
	 * Helper method that returns the number of times the given cell has been visited.
	 * @param cell The cell for which the number of times visited will be returned.
	 */
	private int timesVisited(int[] cell) {
		return visitedCells[cell[0]][cell[1]];
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
