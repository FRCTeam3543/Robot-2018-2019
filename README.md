# Robot-2018-2019
## C4 Robotics, Arnprior District High School

** !!! DRAFT DOCUMENT !!! **

This repo contains much (but not all, yet) of the code from the 2017-2018 robot, but with the source layout following the 2019 Visual Studio Code project format described here: https://wpilib.screenstepslive.com/s/currentCS/m/79833 .  That page also has the latest download links and installation instructions for VS Code and the WPILib plugin.  The plugin has all the java libraries for writing robot programs for the RoboRIO, the tools for deploying your code to the robot.  It _doesn't_ contain the code for the CTRE Talon SRX motor controllers, which you can access here: http://www.ctr-electronics.com/talon-srx.html .

A few key items:

- our codebase this year is reorganized to be simpler to learn for the new coders on the team.  The robot is defined in a few key files, almost all `team3543.robot` (folder `src/main/java/team3453/robot/`).
- a few of the more rigorous Java idioms and constructs are avoided, to make things feel more like the Arduino sketch programming you're learning in the robotics course.

## Rules for coding

* **Add comments to your code to explain!!** - If your job was just to tell the computer what to do, we'd all be writing low-level machine language.  You job is to explain _why you did it that way_ to the person who inherits your code.  Don't over-comment though--assume the reader knows the language and pick variable names that make sense.
* **Keep your code DRY** **D**on't **R**epeat **Y**ourself** - if you find yourself repeating the exact same snippet of code everywhere (or the same but for a parameter), make a function that can be re-used.
* **Neat and Tidy** - try to stick to the same layout, indents and naming conventions everyone else is using.  Makes things easy to share/discuss/debug and helps make it so you "know where to look" when there's a problem.

## How Java programs are organized

### Filenames and Classes

Java programs are organized into _classes_, each one in its own `.java` file with the same name
as the class.  For example, the `DriveLine` class is in a "class file" called `DriveLine.java`.
A class is like a self-contained mini-program within the overall program that a) does a single job of some sort;
b) has all the variables and functions it needs to do its job; and c) can be used by other classes. A class defines functions called _methods_, and variables called _properties_.  In Java, you cannot define a function or variable outside a class.

	int c = 0; // OK in C, Not OK in Java

	void loop() { // OK in C, NOT OK in Java
		// ...
	}

	// OK in Java
	class MyProgram {
		int c = 0;
		void loop() {

		}
	}

In Java, you use a class by making an _instance_ of it using the `new` operator: `MyProgram p = new MyProgram;`.

#### package

At the top of a `.java` class file, you see a _package_ statement that identifies what package ("group") the class file belongs to.  Even though the `Robot.java` file is in the `team3543/robot` folder, you still need to put `package team3543.robot;` at the top of the file.

#### import

At the top of a `.java` class file, you may also see a set of `import` statements.  These refer to class files in another _package_.  In order to use a class file from a different package, you need to add an `import` statement for it.  The equivalent in an Arduino program is when you use `#include "somelibrary.h"`.   An `import some.package.*;` statement (enging in `.*`) means "import all the classes in that package".  So, in classes for Subsystems (e.g. `DriveLine`), you'll see `import` statements for classes from the WPI toolkit for sensors and actuators, like `AnalogGyro`.  A class file can't `import` a class from another package that has the exact same name as a class in its own package.  Instead, you need to need to refer to the class in the other package by its "full name":

	// I've called my class "Joystick", so if I want to refer to the Joystick class in the WPI library I have to be specific
	class Joystick extends edu.wpi.first.wpilibj.Joystick {
		// ...
	}

#### extends

When a class is declared with `extends` and some other class name, it means the first class should inherit _all the
properties and methods from the other ("parent") class.  We won't be using this construct much, but you can
see it being used in `DriveLine.java` which extends the `Subsystem` class from the WPI toolkit, and in `Robot.java` that extends the `TimedRobot` class in the WPI toolkit.

	// This is an example, not real robot code!!
	class RobotPet {
		Speaker speaker = new Speaker();
		void speak() {
			speaker.say("I am a RobotPet");
		}
	}

	class RoboDog extends RoboPet {
		void speak() {
			speaker.say("WOOF"); // Can use "speaker" because it inherits it from RobotPet.
		}
	}

### Constructors

To use a class in a program, you have to create an _instance_ of it using the `new` operator.
Under the hood this allocates the memory and sets everything up.  Every class has a _constructor_,
which is a special method that is called when the instance is being created.  It has the same name as the class.
Inside this method is where you initialize (give a starting value to) any of your instances properties.  If your class
needs to use an instance of another class to do its job, the constructor method should require an instance
of that other class as a `parameter`.  For example, the `DriveLine` needs to
access the `Robot`'s wiring and calibration during setup, so a `Robot` instance is a required parameter in
the `DriveLine` constructor.

	// This is an example, not real robot code!!
	class Terminator extends ActionHero {
		Speaker speaker;
		// this is the constructor
		Terminator() {
			speaker = new Speaker();
		}
		void leave() {
			speaker.say("I'll be back");
		}
	}

	// somewhere else in the program, an instance is made ...
	Terminator arnold = new Terminator();
	arnold.leave();  // says "I'll be back"

### Properties

Properties are variables that belong to the class.  If you have an instance of the class, you use the dot syntax to access a property.

	// This is an example, not real robot code!!
	class Point {
		int x = 0;
		int y = 0;

		Point(int xval, int yval) {
			x = xval; // "set my x property to the value of the xarg argument"
			y = yval;
		}
	}

	// somewhere else in the program ...
	Point p = new Point(1,1); // Have I made a Point? :D
	System.out.println(p.x); // prints "1"

### Methods

Inside a class, the methods (functions) can only access (see) properties of the same instance of the class. Instances of other classes cannot see another instance's properties or methods unless they have a reference to that instance.  For example, the `Robot` class cannot "see" the motor controllers in the `DriveLine`.  This is a Good Thing(TM).  It's the `DriveLine`'s job to manage its own motor controllers, and the `Robot`'s job to manage the `DriveLine`.

	// DriveLine.java
	class DriveLine extends Subsystem {
		...
		AnalogGyro gyro;

		DriveLine(Robot robot) {
			gyro = new AnalogGyro(robot.wiring.DRIVELINE_GYRO_PORT);
			gyro.setSensitivity(robot.calibration.DRIVELINE_GYRO_SENSITIVITY);
			...
		}
	}

	// Robot.java
	class Robot extends TimedRobot {
		...
		DriveLine driveLine;

		Robot() {
			super(); // this calls TimedRobot's constructor
			driveLine = new DriveLine(this); // constructs a driveline - the instance of DriveLine is a property of the Robot
			// note: you could also write this.driveLine = new DriveLine(this); however, the meaning
			// is implied if there's no variable of the same name in the method body.
			driveLine.calibrate();	// use the "dot" syntax to access properties and methods
		}
	}

## How the Robot works

To try to make things easier, the robot code is organized to look and feel a bit more like Arduino sketches.
However the robot is likely more complicated.  So, the code is organized into a set of class files that work together:

* `Robot.java` - the "main" robot class as defined as `ROBOT_CLASS` in `build.gradle`.  The robot instance holds references to all the subsystems, wiring, calibration and geometry.  The runtime environment makes an instance of this class when it starts up the robot.  This class should take care of configuring the robot and instantiating and storing its subsystems.
* `Wiring.java` - contains wiring port values used by subsystems.  PUT ALL THE WIRING VALUES HERE DON'T SCATTER THEM THROUGOUT THE CODE.  It's instance is accessed as `robot.wiring`.
* `Calibration.java` - contains calibration values used by subsystems. PUT ALL CALIBRATION VALUES HERE DON'T SCATTER THEM THROUGOUT THE CODE.  Access as `robot.calibration`.
* `Geometry.java` - contains the field and robot geometry.  PUT ALL BOT AND FIELD GEOMETRY HERE DON'T SCATTER IT THROUGHOUT THE CODE.  **Also put conversion functions and geometry calculation functions here as public static methods**.  Access as `robot.geometry` for robot geometry. Access constants using Geometry.FIELD_CONSTANT_NAME.
* `Constants.java` - contains constants used elsewhere in the application BUT THAT ARE NOT WIRING OR CALIBRATION or GEOMETRY.
* `DriveLine.java` - subsystem for the drive base used in 2016-2018 seasons (motors, encoders and gyro)
* `Claw.java` - subsystem for the pneumatic claw used in the 2018 season, adapted for this year's code.
* `OI.java` - describes the operator interface.  Add your custom code (dashboard buttons, etc.) in the `configure()` method.
* `Teleop.java` - contains the `setup()` and 'loop()` code for teleop mode.
* `Autonomous.java` - contains the `setup()` and 'loop()` code for autonomous mode.
* `Utils.java` - contains various utility functions.

## Subsystems

Components on the robot - for example, the drive line (motors, wheels & gyro) should be organized
into _subsystems_.  This keeps all the parts, sensors and
actuators together that should go together.  Put methods (functions) in the subsystem class that manage
what the subsystem _does_, so code in other classes that use the subsystem is more obvious:

	// what is more obvious, this:
	robot.claw.doubleSolenoid.set(DoubleSolenoid.Value.kForward);
	// or this:
	robot.claw.open();

This also makes it so you can swap out the doubleSolenoid for some other actuator, and all the classes that use `Claw` don't have to care, they just call `claw.open()`.

### How to add a new Subsystem

1. Create a class file for the Subsystem, e.g. `MySubsystem.java`
2. Declare the class:

  package team3543.robot;

  // ... import statements

  class MySubsystem extends Subsystem {
	  Sensor mySensor; // whatevs...
	  // constructor
	  MySubsystem(Robot robot) {
		  super();
		  mySensor = new Sensor(robot.wiring.MY_SENSOR_PORT); // also whatevs...
	  }
	  // ... and implement
  }

3. Add an instance of the subsystem to the `Robot.java` properties:

		class Robot extends TimedRobot {
			// ...
			DriveLine driveLine
			Claw claw;
			MySubsystem mySubsystem;  // see the naming convention - Classes start with uppercase, properties with lowercase
		}

4. Create an instance of the subsystem, in the `Robot.java` constructor

		class Robot extends TimedRobot {
			// ...
			DriveLine driveLine
			Claw claw;
			MySubsystem mySubsystem;  // see the naming convention - Classes start with uppercase, properties with lowercase
			// Constructor
			public Robot() {
				super();
				// ...
				mySubsystem = new MySubsystem(this);
			}
		}

5. Create activities that use the subsystem in the `Autonomous` or `Teleop` classes (more on this below).

## Wiring and calibrating the Robot

All the robot wiring (port numbers) should go into `Wiring.java`.  When you create a new subsystem,
you should add values for all its ports here, as `int` properties.  This puts all the ports and such in one place, instead
of having them scattered throughout the class files.  Then, if we wire things to different ports,
we only have to fix them in one place.  The wiring is assigned to the `wiring` property of your
`Robot` instance, so you can access it when setting up a subsystem.

The same goes for calibration constants, which should all go into `Calibration.java` and is access as the `calibration` property of the
`Robot` instance.  This class stores `double` type properties for things like distance-per-pulse for encoders or sensitivity for gyros.
This is a useful idiom because the person performing calibration can do it all in one place.

	// Wiring.java
	class Wiring {
		...
		final int LIFT_MOTOR_PORT = 2;	// lift motor is in port #2
		...
	}

	// Lift.java
	class Lift extends Subsystem {
		Victor motor;

		// Constructor
		Lift(Robot robot) {
			super("Lift"); // call superclass constructor
			motor = new Victor(robot.wiring.LIFT_MOTOR_PORT); // define this in Wiring.java
			motor.setDistancePerPulse(robot.calibration.LIFT_MOTOR_DISTANCE_PER_PULSE); // define this in Calibration.java
			... etc
		}
	}

## How the Robot starts up

In the project, there is a file called `build.properties`.  It contains `package` and `robot.class` parameters
that indicate which package (`team3543.robot`) and class to run (`Robot`) when the robot is started.

When the robot is started, an instance of `Robot` is created so the Robot() constructor is called to
make an instance of that class.  Then, `robot.robotInit()` is called to initialize the robot.  At this point
we initialize the OI (Operator interface) to hook up buttons, etc.

When the competition starts, `robot.startCompetition()` is called.  You probably don't need to add
anything here.

When autonomous mode starts, `robot.autonomousInit()` is called _once_.
While it is running, `robot.autonomousPeriodic()` is called _every cycle_.

When teleop mode starts, `robot.teleopInit()` is called _once_,
and while it is running, `robot.teleopPeriodic()` is called _every cycle_.

If the robot is disabled, `robot.disabledinit()` is called _once_.

## How to program the Robot

### Making things Arduino-like.

To make programming the Robot closer to Arduino programming from class (even though one is C and the
other is Java!) we've made two _Run mode_ classes, `Teleop.java` and `Autonomous.java`.  They look sort-of
like arduino sketches, wrapped in Java class syntax.  When constructed, the `Robot` creates an instance of `Autonomous` assigned to
the `robot.autononomous`, and an instance of `Teleop` assigned to `robot.teleop`. The `robot` is stored as a
property of each when they are constructed, so both classes can easily access subsystems of the robot.

Each run mode class has a `setup()` method, that is called
_once_ by the robot when that mode is started, and each has a `loop()` method that is called _every cycle_
when the robot is running in that mode.

That is:

`robot.teleopInit()` calls `robot.teleop.setup()` just like an Arduino sketch would.
`robot.teleopPeriodic()` calls `robot.teleop.loop()` just like an Arduino sketch would.

`robot.autonomousInit()` calls `robot.autonomous.setup()` just like an Arduino sketch would.
`robot.autonomousPeriodic()` calls `robot.autonomous.loop()` just like an Arduino sketch would.

So, to program autonomous mode, add your setup-once code to the `setup()` method of `Autonomous.java`,
and your every-time code to the `loop()` method.  Do the same for teleop mode, only in `Teleop.java`.

### Adding a subsystem

If you need a new Subsystem (call it Foo):

1. Create a `Foo.java` class file for it, following `DriveLine.java` or `Claw.java` as an example of how to code the class.
2. Add it as a property to `Robot.java` (use the driveLine as an example `Foo foo;`)
3. Initialize the property in the `Robot()` constructor in `Robot.java` by calling its constructor (`foo = new Foo(this);`) (**!!** inside a method in a class, `this` means "me")
4. Use your new subsystem in the `loop()` method of `Teleop` or `Autonomous`, or in an `Activity` (more on this next)

### Activity programming

Designing a robot to perform a complex task is like solving any complex task: break down the task into a series of
steps.  Break those down further into a series of steps.  Keep doing this until the individual steps are simple and easy to solve.

In the activity programming model, steps are called _activities_.  In the java programming model, they use a special kind
of construct called an _interface_.  An _interface_ can be _implemented_ by a class file, and when
it does it *must* have methods specified with the same signature as the interface.

The `Activity` interface is very simple to use, and has only one method, `boolean loop();`.  This makes the interface "functional" and really easy to implement.  This method is intended to be called by the `loop()` method of the `Teleop` or `Autonomous` classes. When it is called, it receives the `Robot` instance as a parameter, so your code can make use of subsystems, operator interface, and so on.  It returns a `boolean` value (`true` or `false`), which has the following (general) meaning: `true` means the activity should be considered 'done`, and false means the activity should be considered 'not done'.

	// Example of raising a lift
	// RaiseLiftActivity.java
	class RaiseLiftActivity implements Activity {
		Robot robot;
		// ...
		RaiseLiftActivity(Robot robot) {
			this.robot = robot;
		}
		// this of course requires you to have implemented the Lift subsystem accordingly
		public boolean loop() {
			if (!robot.lift.isAtTop()) { // this would, say, check a digital limit switch
				robot.lift.goUp(robot.calibration.LIFT_MAX_SPEED);	// assume this tells the motor to go up
			}
			return robot.lift.isAtTop();
		}
	}

	// Teleop.java
	class Teleop extends Robot.RunMode {
		Robot robot;
		// ...

		RaiseLiftActivity raiseLift;

		void setup() {
			raiseLift = new RaiseLiftActivity(robot);
		}

		void loop() {
			// ...

			// if the left joystick button 7 is pressed, raise the lift
			if (robot.oi.leftJoystick.getButtonPressed(7)) {
				raiseLift.loop(robot);
			}
		}

		// ...
	}

In Java version 8 or higher, in many cases you don't even need to
create a separate `.java` file, you can instead use what's called a "_lambda_" anywhere you require an
`Activity`.  A lambda is a function, defined inline as a method argument, that the compiler can coerce into implementing the same interface as required by the method argument. So, for quick-n-dirty activities, you might be able to declare them in-line like this (reworking the same example as above).

Under the hood, Java 8+ automatically creates an implementation class for you, keeping things way simpler, especially when the alternative is to create an entire `Command` class just to run `robot.claw.open()` when a button is pressed. Factor out a `lambda` function into its own `.java` class file if you find yourself using it over and over again:

	// Teleop.java
	class Teleop {
		Robot robot;
		// ...

		void loop() {
			// if the left joystick button 7 is pressed, raise the lift
			if (robot.oi.leftJoystick.getButtonPressed(7)) {
				// here we use an inline lambda rather than creating a separate class
				// This syntax, explained more below, "makes" an Activity implementation
				// from the lambda function passed, because the lambda function satisifies
				// the Activity interfacem which needs a function that takes no args and returns
				// a boolean.
				// Note:() -> is how you define a lambda.  It means 'a lambda taking no arguments'
				Activity.from(() -> {
					// if the robot is at the top already, return "done"
					if (robot.lift.isAtTop()) return true;
					// otherwise, make the lift go up
					robot.lift.goUp(robot.calibration.LIFT_MAX_SPEED);
					// and return "not done"
					return false;
				}).loop();
			}
		}

		// ...
	}

For activities that are repeated--or repeated but-for-a-small-change, create a _parameterized_ method for generating an Activity.  For an example, see the `driveStraight(double distance)` method of the `DriveLine` class.  It accepts the distance to go and returns an Activity instance that will drive the robot forward until the wheel encoders have registered that change in distance-traveled.

#### Programming the Activity loop method

Your block of code in your activity's `boolean loop()` method should go like this:

1. Read a sensor of some sort (or some internal state).
2. Compare the sensor value to the value you want to get the `error`
3. If you're "there", return true, otherwise:
4. Optionally, if you're "not applicable", return false (this guards against an active activity attempting to actuate when too far away, etc.). Otherwise:
5. Compute some action to take, based on the `error`.
6. Call the relevant subsystem actions.
7. Return false (not-done, next loop we'll start again at step #1).

#### Convenience Activity DSL

We made a small _Domain-Specific Language_ (DSL) of functions to make activities easier to compose into
more complex activities.  To use these functions, import the DSL into your `.java` file like this:

	import static team3543.robot.Activity.*; // this imports all the DSL functions into your `.java` class file

DSL Functions:

* `from(activity)` - a simple convenience wrapper for lambdas `from(() -> { return robot.lift.isAtTop() })`
* `each(activity1, activity2, ... activityN)` - will run each activity in the list until it is done, then move on to the next one.  e.g. `Activity dropItem = each(robot.wrist.openActivity, robot.claw.openActivity)`
* `all(activity1, activity2, ... activityN)` - will run each activity in the list _every loop_.  e.g. `all(robot.oi.openClawButtonPressed, robot.oi.closeClawButtonPressed)`.  Optionally remove completed activities: `all(activity1, activity2).removeWhenComplete()`
* `unless(activityIsTrue, doThisActivity)` - will run the second activity, but only if the first returns `false`.  e.g. `unless(robot.oi.pauseButtonPressed, all(something, somethingElse, somethingElse))`
* `when(activityIsTrue, doThisActivity, otherwiseDoThisActivity)` - will run the second activity if the first returns `true`, otherwise will run the third.  The third can be omitted, in which case `noop` will be performed.
* `noop()` - does nothing (noop means "no operation") and _always returns `false`_
* `stack()` - creates an Activity stack that you can 'push()` to.  The "last-in" activity is executed until it returns true, then it is removed from the stack and the next one is executed.  Always returns `false`;
* `untilFirstFalse(activity1, activity2, ... activityN)` - will try to run each activity in the list _every loop_ in the order they are given, _stopping_ at the first activity that returns `false`.   Returns `true` if _all_ the activities returned true.
* `any(activity1, activity2, ... activityN)` - runs activities in the list in order until one returns `true`.  Returns `false` if _none_ of the activities returns true.
* `once(activity)` - runs an activity _once_ only, no matter what it's `loop()` returns.
* `wrap(sensorCheck, actuator)` - creates an activity out of two arguments, designed to be lambdas.  The first is a "sensor" function that takes no arguments and returns a boolean, and the second is an "actuator" function that takes no arguments and returns `void`.  When the activity loops, if the sensor check is true the `loop()` returns `true`, and if it is false then the actuator function is called and the loop returns `false`.  Useful with the following Java 8+ idiom: `openClaw = wrap(robot.claw::isOpen, robot.claw::open);`.  Here the `::` is a special operator used to denote a method within robot.claw.
* `wrap(wrapped)` - turns a `void` lambda/method taking no arguments into an `Activity` that calls the method and always returns `false`.  `wrap(robot.lift::goUp)`
* `delay(milliseconds, activity)` - creates an activity that returns `false` until at least milliseconds have passed, then returns whatever the underlying activity returns after it runs.  E.g. `delay(2000, wrap(robot.claw::open))`.

For more help understanding what the Activity DSL functions do, check out the unit tests in `src/test/java/team3543/robot/ActivityTest.java`.  The classes in the `test/` folder are used to test the "real" robot classes to make sure they work as expected.

#### Adding activities to subsystems

For sub-teams creating subsystems, it's a good idea to create activity properties for commonly-used
activities that only employ that subsystem, and activity methods for activities that only use that
subsystem and need to be parameterized:

	class Lift extends Subsystem {
		// ...
		void goUp(speed) {
			this.liftMotor.set(speed);
		}

		boolean isAtTop() {
			return this.topLimitSwitch.get();
		}

		Activity goUpActivity(double speed) {
			return Activity.from(() -> {
				if (isAtTop()) return true;
				goUp(speed);
				return false;
			});
		}

		// OR better still, use wrap().  Note we can't just
		// pass this::goUp as the second argument, because the method requires
		// a speed argument.
		Activity betterGoUpActivity(double speed) {
			return Activity.wrap(this::isAtTop, () -> { goUp(speed); });
		}

		// ...

	}

	// Teleop.java
	class Teleop extends Robot.RunMode {
		// ...

		void setup() {
			// add an activity that makes the lift go up when the button 7 is pressed
			activities.add(
				when(robot.oi.leftJoystick.buttonPressed(7), robot.lift.goUpActivity(0.5));
			);
		}

		void loop() {
			activities.loop();
		}
		// ...
	}

### Hooking up Manual Controls

The Operator Interface (`OI.java`) is created as a property of the `Robot` (in the robot instance you
can get at it through `robot.oi`.  It contains, at a minimum, the left and right joysticks.

#### Hooking up to the GUI (Smart Dashboard or ShuffleBoard)

Because we're wrapping the sometimes-wonky _Command_ pattern in the WPI library with our simpler
_Activity_ pattern, the standard "SmartDashboard" interface won't do.  To get around this, we've
added tools to OI to set up Dashboard buttons.  You can use these in the `configure()` method of
the OI class, which is called during `robotInit()` in the robot class.

* `robot.oi.addDashboardActivity(label, activity)` - launch an activity from a Dashboard button.
* `robot.oi.addDashboardTextInput(label, defaultValue)
* `robot.oi.addDashboardNumberInput(label, defaultValue)

#### Hooking up to Joystick buttons

We've augmented the Joystick class in the WPI library to include easy methods to get an `Activity` for detecting a button press or release:


	when(robot.oi.leftJoystick.buttonPressed(7), wrap(robot.voicebox::scream)); // not sure this is allowed in FIRST :)

## FAQ

### Robot Questions

TBD -- please A some Qs F, and I'll add them here!

### General Java Questions

**What does 'void' mean, like in '`void loop()`'?**

With the exception of the constructor method in a class (the method with the same name as the class itself), all
other methods need to return a value of some kind.  "`void`" means "nothing", which implies the method is expected to
return no value, and so doesn't need a `return` statement inside it.

Let's practice reading method declarations:

* `void loop() { ... }` - "A method called 'loop' that takes no parameters and returns nothing"
* `int max(int x, int y) { ... }` - "A method called 'max' that requires two integers as arguments (called x and y) and returns an integer result"
* `boolean foo(Robot robot) { ... }` - "A method called 'foo' that requires an instance of `Robot` as an argument, and returns a boolean (`true` or `false`) as a result
* `void save(File file, byte[] data)` - "a method called 'save' that requires a File argument and an array of bytes argument, and returns nothing."

**Why do some class properties start with `final`?**

For classes the `final` keyword means the class cannot be extended (subclassed):

	final class A { ... }
	class B extends A { ... } // NOPE!

For properties and variables, `final` means the value cannot be overwritten.

	class Wiring {
		final int MOTOR_PORT = 2;
	}
	// ...
	wiring.MOTOR_PORT = 3; // NOPE!

For methods, it means the method cannot be overridden in a subclass.

	class A {
		final void doSomething() { ... }
	}

	class B extends A {
		@Override
		void doSomething() { ... } // NOPE!
	}

**What is a `static` property or method?**

Normally, every instance of a class gets its own copy of properties, and methods in the instance refer to those copies.
When a method or property is declared `static`, it means all instances of the class share the same value.

Static methods are good for methods that don't operate on any data managed by the class.  For example `Math.max(x,y)` in the `java.lang.Math`
class.  Our `Utils.java` class contains some of these, as does our `Activity.java` interface (in Java 8+, interfaces
can have `static` properties and methods).


	class A {
		int x = 0;
		static int y = 0;
		static int xPlusOne() {
			return x+1; // NOPE!
		}
		static int yPlusOne() {
			return y+1; // OK
		}
	}

	A a1 = new A();
	A a2 = new A();
	System.out.println(a2.y);
	> 0
	a1.y = a1.yPlusOne();
	System.out.println(a2.y);
	> 1


	class Constants {
		public static final int ZERO = 0;  // Constants.ZERO can be used anywhere an int can be used and cannot be changed.
	}

**Why do some class properties & methods start with `public`, `private` and `protected`?**

These are called _modifiers_ and they specify how a property or method can be used outside the class, or outside the package the class is declared in.
We have not been judicious about these in the codebase because for new coders they can be confusing, and we've tried to keep the codebase as
simple and Arduino-like as possible so students coming from Arduino work in the robotics course can be productive more
quickly.

* without any such modifier, a property or method is package-private, meaning only instances of classes in the same _package_ (i.e. `team3543.robot`) can access it.
* `public` means any instance of any class can access it
* `private` means it can only be accessed inside the class that declared it.  Use this to make sure classes outside yours don't change your properties unexpectedly?
* `protected` means instances of classes in the same package _and_ instances of classes in other packages that _extend_ this class can access it.

So:

	package foo;
	class A {
		private int x = 0;
		public int y = 1;
		int z = 2;
		protected int w = 3;
	}

	package foo;
	class B {
		void hello() {
			A a = new A();
			a.x; // nope!
			a.y; // OK
			a.z; // OK
			a.w; // OK
		}
	}

	package bar;
	class C {
		void hello() {
			A a = new A();
			a.x; // nope!
			a.y; // OK
			a.z; // nope
			a.w; // nope
		}
	}

	package bar;
	class D extends A {
		void hello() {
			this.x; // nope!
			this.y; // OK
			this.z; // nope!
			this.w; // OK
		}
	}
