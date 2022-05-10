/**	
Copyright (c) 2018 David Phung

Building on work by Mathew A. Nelson and Robocode contributors.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package se.lth.cs.etsa02.basicmeleebot;

import java.awt.geom.Point2D;

import robocode.util.Utils;
import se.lth.cs.etsa02.basicmeleebot.PositioningSystem.Wall;

/**
 * Handles the computation for a type of movement called Anti-gravity and is based on
 * the following tutorial: <a href="http://robowiki.net/wiki/Anti-Gravity_Tutorial">http://robowiki.net/wiki/Anti-Gravity_Tutorial</a>
 * <br/>
 * Each enemy is assigned a repulsive force that pushes the robot away. The sum of all forces will
 * determine the direction of the robot.
 */
public class MovementSystem {
	// ETSA02 Lab2: Add attributes according to the provided UML class diagram.
	private EnemyTracker enemyTracker;
	private BasicMeleeBot robot;
	private PositioningSystem positioningSystem;
	private Point2D.Double repulsivePoint;
	private double xForce;
	private double yForce;
	
	/** 
	 * Construct an object to handle a type of movement called anti-gravity.
	 * @param robot the robot we are working on.
	 * @param enemyHelper the object managing enemies.
	 * @param battleField the object handling battle field related calculations.
	 */
	public MovementSystem(BasicMeleeBot robot, EnemyTracker enemyTracker, PositioningSystem positioningSystem) {
		// ETSA02 Lab2: Implement this constructor to initiate the attributes.
		this.robot = robot;
		this.enemyTracker = enemyTracker;
		this.positioningSystem = positioningSystem;
	}

	/**
	 * To be called every turn. Compute the forces and set the movement for the robot.
	 */
	public void update() {
		// ETSA02 Lab2: Copy movement parts from BasicMeleeBot_AntiPattern here
		// When this is done, you can start Robocode and try the robot. It should get stuck in a wall.
		// This variable is used by both movement and targeting code so make sure to copy it when refactoring in Lab 2.
		xForce = 0;
		yForce = 0;
		Point2D.Double robotPosition = new Point2D.Double(robot.getX(), robot.getY());
		
		// Loop through all enemies and compute their forces.
		for (int i = 0; i < enemyTracker.getEnemyCount(); i++) {
			Point2D.Double enemyPosition = enemyTracker.getEnemyPositions()[i];
			double absBearing=Utils.normalAbsoluteAngle(MathUtils.calcAngle(robotPosition, enemyPosition));
			double distance = enemyPosition.distance(robotPosition);
			xForce -= Math.sin(absBearing) / (distance * distance);
			yForce -= Math.cos(absBearing) / (distance * distance);
		}
		
		// ETSA02 Lab2: Final task if time permits. Uncomment the code below and move it to just before the
		// comment "// Set the movement in a smart way so that the robot would never turn more than 90 degrees."
		
		// To handle wall-avoidance, we check to see if our robot is close to a wall.
		// If it is, we spawn a repulsive point on that wall to push our robot away.
		
		Wall closestWall = positioningSystem.checkCloseToWall(robotPosition, 40);
		if (closestWall != null) {
			repulsivePoint = positioningSystem.getProjectionOnWall(robotPosition, closestWall);
		}
		
		if (repulsivePoint != null) {
			computeForce(robotPosition, repulsivePoint);
		}
		
		// Set the movement in a smart way so that the robot would never turn more than 90 degrees.
		double angle = Math.atan2(xForce, yForce);
		if (xForce == 0 && yForce == 0) {
			// If no force, do nothing.
		} else if (Math.abs(angle - Utils.normalRelativeAngle(robot.getHeadingRadians())) < Math.PI / 2) {
			robot.setTurnRightRadians(Utils.normalRelativeAngle(angle - robot.getHeadingRadians()));
			robot.setAhead(Double.POSITIVE_INFINITY);
		} else {
			robot.setTurnRightRadians(Utils.normalRelativeAngle(angle + Math.PI - robot.getHeadingRadians()));
			robot.setAhead(Double.NEGATIVE_INFINITY);
		}

	}
	
	private void computeForce(Point2D.Double robotPosition, Point2D.Double repulsivePoint) {
		// ETSA02 Lab2: Break out the code that sets the xForce and yForce to this method
		// throw new UnsupportedOperationException("To be implemented in Lab2");
		double absBearing=Utils.normalAbsoluteAngle(MathUtils.calcAngle(robotPosition, repulsivePoint));
		double distance = repulsivePoint.distance(robotPosition);
		xForce -= Math.sin(absBearing) / (distance * distance);
		yForce -= Math.cos(absBearing) / (distance * distance);
	}
}
