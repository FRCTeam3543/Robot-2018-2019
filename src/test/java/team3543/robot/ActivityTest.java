package team3543.robot;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static team3543.robot.Activity.*;

class ActivityTest {
	
	@BeforeEach
	void setup() {
		MockActivity.idCtr = 0;
	}
		
	@Test
	void testQueue() {
		
		MockActivity mock1 = new MockActivity();
		MockActivity mock2 = new MockActivity();
		
		Activity.Sequence seq = queue(mock1, mock2);
		
		assertFalse(seq.stopOnFalse);
		assertFalse(seq.stopOnTrue);		
		assertTrue(seq.removeCompleted);
		assertTrue(seq.onListEmpty);
		assertTrue(seq.trueIfAllTrue);
		
		seq.loop();
		assertEquals(1, mock1.timesLooped);
		assertEquals(1, mock2.timesLooped);
		// should run again
		seq.loop();
		assertEquals(2, mock1.timesLooped);
		assertEquals(2, mock2.timesLooped);
		mock1.complete(); 
		mock2.complete();
		seq.loop();
		assertEquals(3, mock1.timesLooped);
		assertEquals(3, mock2.timesLooped);
		assertTrue(seq.isEmpty());
		boolean done = seq.loop();
		assertEquals(3, mock1.timesLooped);
		assertEquals(3, mock2.timesLooped);
		// should be done
		assertTrue(done);
	}
	
	@Test
	void testEach() {
		
		MockActivity mock1 = new MockActivity();
		MockActivity mock2 = new MockActivity();
		
		Activity.Sequence seq = each(mock1, mock2);
		
		assertFalse(seq.stopOnFalse);
		assertFalse(seq.stopOnTrue);		
		assertTrue(seq.trueIfAllTrue);
		assertFalse(seq.removeCompleted);
		assertTrue(seq.onListEmpty);
		boolean done;
		done = seq.loop();
		assertEquals(1, mock1.timesLooped);
		assertEquals(1, mock2.timesLooped);
		assertFalse(done);
		// should run again
		done = seq.loop();
		assertEquals(2, mock1.timesLooped);
		assertEquals(2, mock2.timesLooped);
		assertFalse(done);
		mock1.complete(); 		
		mock2.complete();
		done = seq.loop();
		assertEquals(3, mock1.timesLooped);
		assertEquals(3, mock2.timesLooped);
		assertEquals(2, seq.size());
		assertTrue(done);
	}
	
	@Test
	void testWhen() {
		
		MockActivity mock1 = new MockActivity();
		MockActivity mock2 = new MockActivity();
		
		Activity a = when(() -> true, mock1, mock2);
		assertFalse(a.loop());
		assertEquals(1, mock1.timesLooped);
		assertEquals(0, mock2.timesLooped);
		
		a = when(() -> false, mock1, mock2);
		assertFalse(a.loop());
		assertEquals(1, mock1.timesLooped);
		assertEquals(1, mock2.timesLooped);
		
		// should return true if the activity that runs returns true
		assertTrue(when(() -> true, () -> true, () -> false).loop());
		assertTrue(when(() -> false, () -> false, () -> true).loop());
		
	}
	
	@Test
	void testUnless() {
		
		MockActivity mock = new MockActivity();
		
		Activity a = unless(() -> false, mock);
		assertFalse(a.loop());		
		assertEquals(1, mock.timesLooped);
		a = unless(() -> true, mock);
		assertFalse(a.loop());
		assertEquals(1, mock.timesLooped);
		
		// if check is false, should return true if the activity that runs returns true
		assertTrue(unless(() -> false, () -> true).loop());
		assertFalse(unless(() -> false, () -> false).loop());		
	}
	
	@Test
	void testNoop() {
		// noop always returns false
		assertFalse(noop().loop());
	}
	
	@Test
	void testUntilFirstFalse() {
		MockActivity mock1 = new MockActivity();
		MockActivity mock2 = new MockActivity();
		MockActivity mock3 = new MockActivity();

		mock1.complete();
		
		Activity seq = untilFirstFalse(mock1, mock2, mock3);
		
		// mock1 is complete, mocks 2 and 3 are not.  So, mock1 and mock2 should run,
		// and mock3 should not
		assertFalse(seq.loop());
		assertEquals(1, mock1.timesLooped);
		assertEquals(1, mock2.timesLooped);
		assertEquals(0, mock3.timesLooped);
		
		// now let's complete #2.  Now mocks 1 2 and 3 should run, but overall we should
		// be false because mock3 returns false
		mock2.complete();
		assertFalse(seq.loop());
		assertEquals(2, mock1.timesLooped);
		assertEquals(2, mock2.timesLooped);
		assertEquals(1, mock3.timesLooped);
		// now let's complete #3.  Shoudl return true because all complete
		mock3.complete();
		assertTrue(seq.loop());
		assertEquals(3, mock1.timesLooped);
		assertEquals(3, mock2.timesLooped);
		assertEquals(2, mock3.timesLooped);
	}
	
	@Test
	void testAlways() {
		// always wraps an activity so it returns false, no matter what the underlying activity returns
		assertFalse(always(() -> true).loop());
		assertFalse(always(() -> false).loop());		
	}
	
	@Test
	void testStack() {
		MockActivity mock1 = new MockActivity();
		MockActivity mock2 = new MockActivity();
		
		Activity.Stack stack = stack();
		// stacks always return false
		assertFalse(stack.loop());
		// add to the stack
		assertEquals(0, mock1.timesLooped);
		assertEquals(0, mock2.timesLooped);
		
		stack.push(mock1);
		stack.push(mock2);
		assertFalse(stack.loop());		
		// since mock2 was added more recently, only it should have run
		assertEquals(0, mock1.timesLooped);
		assertEquals(1, mock2.timesLooped);
		// complete mock2, to ensure stack still returns false
		mock2.complete();
		assertFalse(stack.loop());
		// since 2 was completed and removed, mock1 would have also run this loop
		assertEquals(1, mock1.timesLooped);
		assertEquals(2, mock2.timesLooped);		
		// mock2 should have been removed from the stack once completed
		assertFalse(stack.loop());
		assertEquals(2, mock1.timesLooped);
		assertEquals(2, mock2.timesLooped);			
	}
	
	@Test
	void testOnce() {
		MockActivity mock1 = new MockActivity();
		// once means the underlying activity will only run once no matter how many times loop is called
		mock1.complete();
		Activity one = once(mock1);
		assertTrue(one.loop());
		assertTrue(one.loop());
		assertTrue(one.loop());
		assertTrue(one.loop());
		assertEquals(1, mock1.timesLooped);
		
		// now make sure it works with false
		mock1 = new MockActivity();
		one = once(mock1);
		assertFalse(one.loop());
		assertFalse(one.loop());
		assertFalse(one.loop());
		assertEquals(1, mock1.timesLooped);
	}
	
	@Test
	void testWrap() {
		MockActivity mock = new MockActivity();

		// wrap() takes a Runnable (something that implements a single no-arg, void-return function)
		// wrapper should NOT loop if checker is true
		assertTrue(wrap(() -> true, () -> { mock.loop(); }).loop());
		assertEquals(0, mock.timesLooped);
		
		// wrapper should loop if checker is false, and return false
		assertFalse(wrap(() -> false, () -> { mock.loop(); }).loop());
		assertEquals(1, mock.timesLooped);		
	}
	
	@Test
	void testAny() {
		// any runs the activities provided in order, returning after the first one
		// that returns true.  (contrast this with untilFirstFalse)
		MockActivity mock1 = new MockActivity();
		MockActivity mock2 = new MockActivity();
		MockActivity mock3 = new MockActivity();

		// an any that is empty returns false
		assertFalse(any().loop());

		Activity a = any(mock1, mock2, mock3);
		// none are true, and all ran
		assertFalse(a.loop());
		assertEquals(1, mock1.timesLooped);
		assertEquals(1, mock2.timesLooped);
		assertEquals(1, mock3.timesLooped);		
		// now complete 1
		mock1.complete();
		assertTrue(a.loop());
		assertEquals(2, mock1.timesLooped);
		assertEquals(1, mock2.timesLooped);
		assertEquals(1, mock3.timesLooped);		
		
		mock1.completed = false;
		mock2.complete();
		assertTrue(a.loop());
		assertEquals(3, mock1.timesLooped);
		assertEquals(2, mock2.timesLooped);
		assertEquals(1, mock3.timesLooped);		

		mock2.completed = false;
		mock3.complete();
		assertTrue(a.loop());
		assertEquals(4, mock1.timesLooped);
		assertEquals(3, mock2.timesLooped);
		assertEquals(2, mock3.timesLooped);		
		
	}
	
	void testFrom() {
		// from makes an Activity from a lambda
		assertTrue(from(() -> true) instanceof Activity);
	}
	
	void testAll() {
		// all is like each, except it does not remove completed, and returns true if all are true
		MockActivity mock1 = new MockActivity();
		MockActivity mock2 = new MockActivity();

		// empty is false
		assertFalse(all().loop());
		Activity seq = all(mock1, mock2);
		assertFalse(seq.loop());
		
		mock1.complete();
		assertFalse(seq.loop());

		mock2.complete();
		assertTrue(seq.loop());
		
		mock1.completed = false;
		assertFalse(seq.loop());
	}
	
//	@Test 
	void testDelay() throws InterruptedException {
		MockActivity mock1 = new MockActivity();
		mock1.complete();
		Activity delayed = delay(1000, mock1);
		assertFalse(delayed.loop());
		assertEquals(0, mock1.timesLooped);
		Thread.currentThread().sleep(1001);
		assertTrue(delayed.loop());
		assertEquals(1, mock1.timesLooped);
	}
}
