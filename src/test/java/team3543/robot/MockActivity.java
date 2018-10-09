package team3543.robot;

public class MockActivity implements Activity {
	public int timesLooped = 0;
	boolean completed = false;	
	int completeAfter = -1;
	int id = 0;
	public boolean log = false;
	
	static int idCtr = 0;
	
	public MockActivity() {
		id = ++idCtr;
	}

	@Override
	public boolean loop() {		
		timesLooped++;
		if (completeAfter >= 0 && timesLooped >= completeAfter) {
			completed = true;
		}
		if (log) System.out.println(String.format("MockActivity #%d: times=%d completed=%s", id, timesLooped, completed));		
		return completed;
	}
	
	public MockActivity complete() {
		this.completed = true;
		return this;
	}
	
	public MockActivity completeAfter(int numCalls) {
		completeAfter = numCalls;
		return this;
	}	
	
}
