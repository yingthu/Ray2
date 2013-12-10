package ray.accel;

import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

/**
 * A class representing a node in a bounding volume hierarchy.
 * 
 * @author pramook 
 */
public class BvhNode {

	/** The current bounding box for this tree node.
	 *  The bounding box is described by 
	 *  (minPt.x, minPt.y, minPt.z) - (maxBound.x, maxBound.y, maxBound.z).
	 */
	public final Point3 minBound, maxBound;
	
	/**
	 * The array of children.
	 * child[0] is the left child.
	 * child[1] is the right child.
	 */
	public final BvhNode child[];

	/**
	 * The index of the first surface under this node. 
	 */
	public int surfaceIndexStart;
	
	/**
	 * The index of the surface next to the last surface under this node.	 
	 */
	public int surfaceIndexEnd; 
	
	/**
	 * Default constructor
	 */
	public BvhNode()
	{
		minBound = new Point3();
		maxBound = new Point3();
		child = new BvhNode[2];
		child[0] = null;
		child[1] = null;		
		surfaceIndexStart = -1;
		surfaceIndexEnd = -1;
	}
	
	/**
	 * Constructor where the user can specify the fields.
	 * @param minBound
	 * @param maxBound
	 * @param leftChild
	 * @param rightChild
	 * @param start
	 * @param end
	 */
	public BvhNode(Point3 minBound, Point3 maxBound, BvhNode leftChild, BvhNode rightChild, int start, int end) 
	{
		this.minBound = new Point3();
		this.minBound.set(minBound);
		this.maxBound = new Point3();
		this.maxBound.set(maxBound);
		this.child = new BvhNode[2];
		this.child[0] = leftChild;
		this.child[1] = rightChild;		   
		this.surfaceIndexStart = start;
		this.surfaceIndexEnd = end;
	}
	
	/**
	 * @return true if this node is a leaf node
	 */
	public boolean isLeaf()
	{
		return child[0] == null && child[1] == null; 
	}
	
	/** 
	 * Check if the ray intersects the bounding box.
	 * @param ray
	 * @return true if ray intersects the bounding box
	 */
	public boolean intersects(Ray ray) {
		// TODO: fill in this function.
		// Hint: reuse your code from box intersection.
	    
		// Specify the two variables to reuse the code from Box
	    Point3 minPt = new Point3(minBound);
	    Point3 maxPt = new Point3(maxBound);
	    
	    // Copied from Box.java
	    Point3 o = ray.origin;
	    Vector3 d = ray.direction;
	    
	    double ox = o.x;
	    double oy = o.y;
	    double oz = o.z;
	    double dx = d.x;
	    double dy = d.y;
	    double dz = d.z;
	    
	    // a three-slab intersection test. We'll get in and out t values for
	    // all three axes. For instance on the x axis:
	    // o.x + t d.x = 1 => t = (1 - o.x) / d.x
	    // o.x + t d.x = -1 => t = (-1 - o.x) / d.x
	    // This code is straight from Shirley's section 10.9.1

	    double tMin = ray.start, tMax = ray.end;

	    double txMin, txMax;
	    if (dx >= 0) {
	      txMin = (minPt.x - ox) / dx;
	      txMax = (maxPt.x - ox) / dx;
	    }
	    else {
	      txMin = (maxPt.x - ox) / dx;
	      txMax = (minPt.x - ox) / dx;
	    }
	    if (tMin > txMax || txMin > tMax)
	      return false;
	    if (txMin > tMin)
	      tMin = txMin;
	    if (txMax < tMax)
	      tMax = txMax;
	  
	    double tyMin, tyMax;
	    if (dy >= 0) {
	      tyMin = (minPt.y - oy) / dy;
	      tyMax = (maxPt.y - oy) / dy;
	    }
	    else {
	      tyMin = (maxPt.y - oy) / dy;
	      tyMax = (minPt.y - oy) / dy;
	    }
	    if (tMin > tyMax || tyMin > tMax)
	      return false;
	    if (tyMin > tMin)
	      tMin = tyMin;
	    if (tyMax < tMax)
	      tMax = tyMax;
	    
	    double tzMin, tzMax;
	    if (dz >= 0) {
	      tzMin = (minPt.z - oz) / dz;
	      tzMax = (maxPt.z - oz) / dz;
	    }
	    else {
	      tzMin = (maxPt.z - oz) / dz;
	      tzMax = (minPt.z - oz) / dz;
	    }
	    if (tMin > tzMax || tzMin > tMax)
	      return false;
	    if (tzMin > tMin)
	      tMin = tzMin;
	    if (tzMax < tMax)
	      tMax = tzMax;
	    
	    return true;
	}
}
