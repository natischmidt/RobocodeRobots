# Robocode - Robots

Welcome to the Robocode project featuring two advanced robots: Calculon and Powerranger. These robots showcase distinct strategies and movements to excel in battles. Below, you'll find information on each robot's structure, implemented methods, and overall tactics.

## Calculon

### Overview
Calculon is an advanced robot that adapts its movement pattern based on the number of enemies in the match. It employs SmartFire for efficient shooting and utilizes linear aiming for accurate targeting.

### Structure
#### `Run`
- Width lock radar is used for continuous scanning.
- Radar adjusts to a fixed distance on both sides of the enemy.
- WallMovement, CircleMovement, or ClosingIn is chosen based on the number of enemies:
  - WallMovement: More than 3 enemies.
  - CircleMovement: More than 1 but less than or equal to 3 enemies.
  - ClosingIn: Only 1 enemy remaining.

#### `initialize`
- Radar, gun, and body can move independently.
  
#### `CircleMovement`
- Circulates around the target using currentTarget information.
- Effective for dodging shots when facing 2 enemies.

#### `ClosingIn`
- Strafing method to approach the last remaining enemy slowly.

#### `WallMovement`
- Determines battlefield dimensions to navigate along the walls.

#### `onScannedRobot`
- Efficiently scans enemies by adjusting radar direction.
- Adjusts radar turn for extended scanning.

#### `trackenemy`
- Finds a new target if the current one is dead, too far, or already scanned.

#### `smartFire`
- Calculates bullet power based on gun heat, enemy distance, and energy.
- Shoots with more power if the enemy is stationary.

#### `onBattleEnded`
- Displays statistics at the end of each match for analysis.

## Powerranger

### Overview
Powerranger is an advanced robot with wall movement and dodging capabilities. It calculates the enemy's direction and velocity to enhance shooting accuracy using SmartFire. The robot employs a strategy of strafing to be less predictable.

### Structure
#### `Run`
- Essential methods are called, and the robot turns towards the wall.
- Radar is set to Infinity lock for continuous scanning.

#### `WallMovement`
- Determines if the robot is almost straight north or south.
- Moves forward to stay close to the wall.
- Turns 90 degrees either towards the x or y axis.

#### `Dodgemovement`
- Provides less predictable movements to avoid being an easy target.
- Slows down or speeds up randomly.

#### `energyBuddies`
- Custom event triggered when an enemy has the same energy as Powerranger.

#### `onScannedRobot`
- `trackenemy`: Determines a target based on scanned enemy information.
- `strafeEnemy`: Moves in a triangular pattern relative to the enemy.
- `smartFire`: Adjusts bullet power based on gun heat, enemy distance, and energy.

#### `onRobotDeath`
- Looks for a new target if the current one is dead.

#### `onBattleEnded`
- Prints out statistics at the end of each battle for analysis.

Feel free to explore and modify these robots to enhance their performance in Robocode battles. Happy coding!
