package team3543.robot;

import java.util.function.BooleanSupplier;

/**
 * The activity interface is at the core of this robot model.
 * 
 * The supports an easy, functional interface for implementing robot behaviors.
 * 
 * An activity need only declare one method, loop(Robot), that is called every time
 * the robot's loop() function is called.  It returns true when it is "done", and false 
 * otherwise.
 * 
 * In general your actuating activities should be organized as follows:
 * <ol>
 * <li> read the sensors
 * <li> compare the sensor values to what the activity is trying to achieve (compute the difference)
 * <li> if the values are "close enough", return true
 * <li> if the values are not "close enough", use the difference to compute some output to the actuators
 * <li> return false
 * </ol>
 * 
 * The interface also supports a Domain-specific language (DSL) for composing more complex
 * activities out of smaller ones, and for creating activities out of zero-arg methods in
 * other classes:
 * <code>
 * Activity a = all(
 * 		wrap(this::arcadeDrive),
 * 	 	when(robot.oi.leftJoystick.triggerPressed(), wrap(robot.claw::open)), wrap(robot.claw::close)),
 * 		wrap(this::updateDashboard)
 * )
 * 
 * </code>
 * 
 * @author mk
 *
 */
public interface Activity {
	
	/**
	 * Code to be performed every cycle.
	 *  
	 * @param robot
	 * 
	 * @return false if the activity is not complete, true if it is
	 */
	boolean loop();
		
	/**
	 * Establishes a last-in-first-out (LIFO) activity stack
	 * 
	 * Unlike the queue() or each(), the stack() of activities
	 * executes the most recently added activity until it is complete,
	 * then removes it and moves onto the next one.
	 * 
	 * @param activities
	 * @return
	 */
	static Stack stack(Activity...activities) {
		Stack stack = new Stack();
		for (Activity a : activities) {
			stack.push(a);
		}
		return stack;
	}
	
	/**
	 * Runs each activity in the order given, stopping at the first that returns false
	 * 
	 * @param activities
	 * @return
	 */
	static Activity untilFirstFalse(Activity...activities) {
		return each(activities).stopOnFirstFalse(true);
	}
	
	/**
	 * Establishes a sequence of activities.  Completed are NOT removed.
	 * 
	 * loop() for this sequence returns true if all activities in the sequence 
	 * return true, false otherwise.
	 * 
	 * @param activities
	 * @return 
	 */
	static Sequence each(Activity...activities) {
		Sequence fifo = new Sequence();
		for (Activity a : activities) {
			fifo.push(a);
		}
		return fifo.returnTrueIfAllTrue(true);
	}
	
	/**
	 * Establishes a sequence of activities.  Completed ARE removed.
	 * 
	 * @param activities
	 * @return
	 */
	static Sequence queue(Activity...activities) {
		return each(activities).removeOnComplete(true);
	}


	/**
	 * Run all activities, returning true only if all activities return true
	 * 
	 * When loop() is called, each activity in the set has its loop()
	 * called as well.  If you set removeWhenDone() to true on the
	 * Set returned by this, an activity will be removed once it is
	 * completed.  loop() returns true if all the activities return true
	 * or if the set is empty.
	 * 
	 * @param activities
	 * @return
	 */
	static Sequence all(Activity...activities) {
		return each(activities).returnTrueIfAllTrue(true);
	}
	
	/**
	 * Convenience method to turn a lambda back to an activity
	 * 
	 * Activity a = Activity.from(robot -> { return .... });
	 * 
	 * @param a some activity (supports lambdas)
	 * @return the same Activity
	 */
	static Activity from(Activity a) {
		return a;
	}
	
	/**
	 * Wraps an activity so it always runs
	 * 
	 * This will re-run an activity's loop(), regardless of what it returns,
	 * because it's outer loop() always returns false.
	 * 
	 * @param activity
	 * @return
	 */
	static Activity always(Activity activity) {
		return new Activity() {
			@Override
			public boolean loop() {
				activity.loop();
				return false;
			}
		};
	};
	
	/**
	 * Wraps an activity so that it only runs once no matter what.
	 * 
	 * This is so even if you've added it to a set that doesn't remove
	 * completed activities, the underlying activity will only ever be called
	 * once.
	 * 
	 * @param subActivity
	 * @return the value returned by the underlying activity loop, the ONE time it was called
	 */
	static Activity once(final Activity subActivity) {
		return new Activity() {
			boolean once = false;
			boolean returnValue = false;
			public boolean loop() {
				if (!once) {
					returnValue = subActivity.loop();
					once = true;
				}
				return returnValue;
			}
		};
	}
	
	/**
	 * Guard an activity in another.
	 * 
	 * If the test activity returns TRUE, the underlying activity WON'T RUN,
	 * otherwise it will run.
	 * 
	 * @param test
	 * @param activity
	 * @return false, unless the underlying activity runs and returns true
	 */
	static Activity unless(Activity testIsTrue, Activity activityWillRun) {
		return from(() -> {
			if (testIsTrue.loop()) {
				return false;
			} else {
				return activityWillRun.loop();
			}						
		});
	}
		
	/**
	 * Enable one or another activity depending on a test activity.
	 * 
	 * If the test activity returns TRUE, the underlying activity WILL RUN,
	 * otherwise the other one will run.
	 * 
	 * @param test
	 * @param activity
	 * 
	 * @return false, unless the underlying activity runs and returns true
	 */
	static Activity when(Activity testIsTrue, Activity thisActivityWillRun, Activity otherwiseThisActivityWillRun) {
		return new Activity() {
			@Override
			public boolean loop() {
				if (testIsTrue.loop()) {
					return thisActivityWillRun.loop();
				} else {
					return otherwiseThisActivityWillRun.loop();
				}
			}
		};
	}	
		
	/**
	 * Enables an activity if another returns true
	 * 
	 * @param testIsTrue
	 * @param thisActivityWillRun
	 * @return
	 */
	static Activity when(Activity testIsTrue, Activity thisActivityWillRun) {
		return when(testIsTrue, thisActivityWillRun, noop());
	}
	
	/**
	 * A noop activity whose loop() always returns false
	 * 
	 * @return
	 */
	static Activity noop() {
		return new Activity() {
			@Override
			public boolean loop() {
				return false;
			}			
		};
	}
	
	/**
	 * Creates an activity that checks check and wraps wrap.
	 * 
	 * The first argument will be called.  if it returns false, the second
	 * argument is called.  The difference between this and when() is that 
	 * the arguments expect to take no parameters instead of the robot.
	 * 
	 * @param check
	 * @param wrap
	 * @return
	 */
	static Activity wrap(BooleanSupplier check, Runnable wrap) {
		return from(() -> {
			if (check.getAsBoolean()) return true;
			wrap.run();
			return false;
		});
	}
	
	static Activity once(Runnable wrap) {
		return once(wrap(wrap));
	}
	
	/**
	 * Equivalent to wrap(() -> false, wrap) where the check always returns false.
	 * @param wrap
	 * @return
	 */
	static Activity wrap(Runnable wrap) {
		return wrap(() -> false, wrap);
	}
	
	/**
	 * Wrap a zero-arg function that returns a boolean as an Activity that returns the boolean
	 * 
	 * @param check
	 * @return whatever the function returns
	 */
	static Activity wrap(BooleanSupplier check) {
		return Activity.from(() -> {
			return check.getAsBoolean();
		});	
	}
	
	/**
	 * Returns an activity that returns true if ANY of its activities return true
	 * 
	 * When loop() is called, each activity in the list is called. If any
	 * return true, the loop() returns true.
	 *  
	 * @param activities
	 * @return
	 */
	static Activity any(Activity...activities) {
		return from(() -> {
			for (Activity a : activities) {
				if (a.loop()) {
					return true;
				}
			}
			return false;
		});
	}
	
	/**
	 * Return false until waitMilliseconds have passed, then run and return the activity.
	 * 
	 * @param waitMilliseconds
	 * @param activity
	 * @return
	 */
	static Activity delay(long waitMilliseconds, Activity activity) {
		return new Activity() {
			final long start = System.currentTimeMillis();
			@Override
			public boolean loop() {
				if (System.currentTimeMillis() - start < waitMilliseconds) {
					return false;
				}
				else {
					return activity.loop();
				}
			}			
		};
	}

	/**
	 * Represents a sequence of activities, executed in a particular order
	 * 
	 * When loop() is called on the sequence, it in turn calls loop() on
	 * each activity in the sequence, in the order they were given.  Using
	 * stopOnFirstFalse() or stopOnFirstTrue() you can exit the sequence early
	 * at the first activity that returns that value.  Using removeOnComplete() 
	 * you can automatically remove items from the sequence that return true.
	 * 
	 * So, you can do things like this, to make a claw that's held open when
	 * button 5 is pressed on the left stick (assuming the robot has a claw that
	 * opens and closes).
	 *  
	 * @author mk
	 */
	public static class Sequence implements Activity {
		java.util.List<Activity> list = new java.util.ArrayList<>();
		boolean removeCompleted = false;
		boolean stopOnFalse = false;
		boolean stopOnTrue = false;
		boolean onListEmpty = true;
		boolean trueIfAllTrue = false;
		
		/**
		 * Add an activity to the sequence. 
		 * 
		 * @param activities one or more Activity objects
		 * @return this sequence, so you can chain method calls
		 */
		public Sequence push(Activity...activities) {
			for (Activity a : activities) list.add(a);
			return this;
		}
		
		/**
		 * Check if the list is empty
		 * 
		 * @return
		 */
		public boolean isEmpty() {
			return list.isEmpty();
		}
		
		/**
		 * Return number of activities in the list
		 * @return
		 */
		public int size() {
			return list.size();
		}
		
		/**
		 * Synonym for push() 
		 * 
		 * @param activities
		 * @return
		 */
		public Sequence add(Activity...activities) {
			return push(activities);
		}
		
		/**
		 * Remove completed activities or not (default FALSE)
		 * 
		 * @param b if true, remove an activity from the list when it is complete
		 * @return this sequence, so you can chain method calls
		 */
		public Sequence removeOnComplete(boolean b) {
			this.removeCompleted = b;
			return this;
		}
		
		/**
		 * Value to return when list is empty.  Default true.
		 * 
		 * @param b
		 * @return this sequence, so you can chain method calls
		 */
		public Sequence returnOnEmpty(boolean b) {
			this.onListEmpty = b;
			return this;
		}

		/**
		 * Return true from loop() if all activities in the sequence return true
		 * 
		 * @param b if true, return true from loop() when all activities return true, otherwise return false
		 * @return this sequence, so you can chain method calls
		 */
		public Sequence returnTrueIfAllTrue(boolean b) {
			this.trueIfAllTrue = b;
			return this;				
		}
		
		/**
		 * Stop after the first false value in the sequence 
		 * 
		 * @param b if true, stop after the first activity that returns false.  Otherwise run the whole sequence in order.
		 * @return this sequence, so you can chain method calls
		 */
		public Sequence stopOnFirstFalse(boolean b) {
			this.stopOnFalse = b;
			return this;
		}
		
		/**
		 * Stop after the first true value in the sequence 
		 * 
		 * @param b if true, stop after the first activity that returns true.  Otherwise run the whole sequence in order.
		 * @return this sequence, so you can chain method calls
		 */
		public Sequence stopOnFirstTrue(boolean b) {
			this.stopOnTrue = b;
			return this;
		}
		
		/**
		 * Loop. 
		 * 
		 * @return onListEmpty when list is empty, otherwise false
		 */
		@Override
		public boolean loop() {
			boolean done = false;
			boolean allDone = true;
			int ctr = 0, len = list.size();
			if (list.isEmpty()) {
				return onListEmpty;
			}
			java.util.List<Activity> toRemove = new java.util.ArrayList<>();
			for (Activity activity : list) {
				done = activity.loop();
				allDone &= done;
				// if the activity is done and removeCompleted is set, remove it
				if (done) {
					if (removeCompleted) toRemove.add(activity);
					if (stopOnTrue) break;
				} 
				else { // if the activity is not done and we're stopping on first false, stop the loop
					if (stopOnFalse) break;
				}
				ctr++;
			}
			for (Activity a : toRemove) list.remove(a);			
			// if we visited each item in the list and trueIfAllTrue is set,
			// return the ANDed value of all the activity responses.  Otherwise return false.
			return (ctr == len && trueIfAllTrue) ? allDone : false;
		}
	} // Sequence
	
	/**
	 * Implements an activity stack
	 * 
	 * Activities are executed most-recent first.  Unlike the Sequence,
	 * only the "current" activity is executed.  When it is completed (returns true),
	 * it is removed from the stack and the next-most-recent activity is executed, 
	 * and so on.
	 * 
	 * @author mk
	 */
	public static class Stack implements Activity {
		java.util.Stack<Activity> stack = new java.util.Stack<>();
		
		public Stack push(Activity activity) {
			stack.push(activity);
			return this;
		}
		
		public Activity pop() {
			return stack.pop();
		}
		
		public Activity peek() {
			return stack.peek();
		}
		
		@Override
		public boolean loop() {
			boolean done = false;
			while (!stack.empty()) {
				done = stack.peek().loop();
				if (done) {
					stack.pop();
				} else {
					// break on first stack item not done
					break;
				}
			}
			return false;
		}
	} // Stack
		
}

