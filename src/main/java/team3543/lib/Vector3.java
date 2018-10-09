package team3543.lib;

/////////////////////////////
// Geometry classes
public class Vector3 {
	public static final Vector3 ZERO = new Vector3(0,0,0);
	public static final Vector3 UNIT = new Vector3(1,1,1);
	public static final Vector3 X_AXIS = new Vector3(1,0,0);
	public static final Vector3 Y_AXIS = new Vector3(0,1,0);
	public static final Vector3 Z_AXIS = new Vector3(0,0,1);		
	
	public double x;
	public double y;
	public double z;
		
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3 copy() {
		return new Vector3(this.x, this.y, this.z);
	}
	
	public Vector3 scale(double by) {
		this.x *= by;
		this.y *= by;			
		this.z *= by;
		return this;
	}
	
	public Vector3 add(Vector3 v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}
	
	public double dot(Vector3 v) {
		return (this.x * v.x + this.y * v.y + this.z * v.z);
	}
	
	public double dotSelf() {
		return this.dot(this);
	}
	
	public double mag() {
		return Math.sqrt(dotSelf());
	}
	
	public double rotate(Vector3 axis, double radians) {
		// TODO
		return 0;
	}
}