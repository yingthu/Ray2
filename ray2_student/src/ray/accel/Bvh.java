
package ray.accel;

import java.util.Arrays;
import java.util.Comparator;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
import ray.surface.Surface;

/**
 * Class for Axis-Aligned-Bounding-Box to speed up the intersection look up time.
 *
 * @author ss932, pramook
 */
public class Bvh implements AccelStruct {   
	/** A shared surfaces array that will be used across every node in the tree. */
	private Surface[] surfaces;

	/** A comparator class that can sort surfaces by x, y, or z coordinate.
	 *  See the subclass declaration below for details.
	 */
	static MyComparator cmp = new MyComparator();
	
	/** The root of the BVH tree. */
	BvhNode root;

	public Bvh() { }

	/**
	 * Set outRecord to the first intersection of ray with the scene. Return true
	 * if there was an intersection and false otherwise. If no intersection was
	 * found outRecord is unchanged.
	 *
	 * @param outRecord the output IntersectionRecord
	 * @param ray the ray to intersect
	 * @param anyIntersection if true, will immediately return when found an intersection
	 * @return true if and intersection is found.
	 */
	public boolean intersect(IntersectionRecord outRecord, Ray rayIn, boolean anyIntersection) {
		return intersectHelper(root, outRecord, rayIn, anyIntersection);
	}
	
	/**
	 * A helper method to the main intersect method. It finds the intersection with
	 * any of the surfaces under the given BVH node.  
	 *   
	 * @param node a BVH node that we would like to find an intersection with surfaces under it
	 * @param outRecord the output InsersectionMethod
	 * @param rayIn the ray to intersect
	 * @param anyIntersection if true, will immediately return when found an intersection
	 * @return true if an intersection is found with any surface under the given node
	 */
	private boolean intersectHelper(BvhNode node, IntersectionRecord outRecord, Ray rayIn, boolean anyIntersection)
	{
		// TODO: fill in this function.
		// Hint: For a leaf node, use a normal linear search. Otherwise, search in the left and right children.
		// Another hint: save time by checking if the ray intersects the node first before checking the children.
		if (node == null)
			return false;
		// Check whether they ray hits the node or not
		if (!node.intersects(rayIn))
			return false;
		
		// If node is a leaf
		if (node.isLeaf())
		{
			boolean retVal = false;
			IntersectionRecord tmpRecord = new IntersectionRecord();
			Ray ray = new Ray(rayIn.origin, rayIn.direction);
			ray.start = rayIn.start;
			ray.end = rayIn.end;
			
			for (int i = node.surfaceIndexStart; i < node.surfaceIndexEnd; i++)
			{
				if (surfaces[i].intersect(tmpRecord, ray))
				{
					if (anyIntersection)
						return true;
					retVal = true;
					ray.end = tmpRecord.t;
					if (outRecord != null)
						outRecord.set(tmpRecord);
				}
			}
			return retVal;
		}
		// Recursive call
		if (anyIntersection)
			return intersectHelper(node.child[0], outRecord, rayIn, anyIntersection) || intersectHelper(node.child[1], outRecord, rayIn, anyIntersection);
		else
		{
			IntersectionRecord leftRecord = new IntersectionRecord();
			IntersectionRecord rightRecord = new IntersectionRecord();
			boolean leftVal = intersectHelper(node.child[0], leftRecord, rayIn, anyIntersection);
			boolean rightVal = intersectHelper(node.child[1], rightRecord, rayIn, anyIntersection);
			if (leftRecord.surface != null && (leftRecord.t < rightRecord.t || rightRecord.surface == null))
				outRecord.set(leftRecord);
			else if (rightRecord.surface != null && (rightRecord.t < leftRecord.t || leftRecord.surface == null))
				outRecord.set(rightRecord);
			return leftVal || rightVal;
		}
		
	}


	@Override
	public void build(Surface[] surfaces) {
		this.surfaces = surfaces;
		root = createTree(0, surfaces.length);
	}
	
	/**
	 * Create a BVH [sub]tree.  This tree node will be responsible for storing
	 * and processing surfaces[start] to surfaces[end-1]. If the range is small enough,
	 * this will create a leaf BvhNode. Otherwise, the surfaces will be sorted according
	 * to the axis of the axis-aligned bounding box that is widest, and split into 2
	 * children.
	 * 
	 * @param start The start index of surfaces
	 * @param end The end index of surfaces
	 */
	private BvhNode createTree(int start, int end) {
		// TODO: fill in this function.

		// ==== Step 1 ====
		// Find out the BIG bounding box enclosing all the surfaces in the range [start, end)
		// and store them in minB and maxB.
		// Hint: To find the bounding box for each surface, use getMinBound() and getMaxBound() */
		// Initialize
		Point3 minBigBound = new Point3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		Point3 maxBigBound = new Point3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		// Loop through surfaces
		for (int i = start; i < end; i++)
		{
			// Current min and max
			Point3 minTmp = surfaces[i].getMinBound();
			Point3 maxTmp = surfaces[i].getMaxBound();
			// Update min and max
			if (minTmp.x < minBigBound.x)
				minBigBound.x = minTmp.x;
			if (minTmp.y < minBigBound.y)
				minBigBound.y = minTmp.y;
			if (minTmp.z < minBigBound.z)
				minBigBound.z = minTmp.z;
			if (maxTmp.x > maxBigBound.x)
				maxBigBound.x = maxTmp.x;
			if (maxTmp.y > maxBigBound.y)
				maxBigBound.y = maxTmp.y;
			if (maxTmp.z > maxBigBound.z)
				maxBigBound.z = maxTmp.z;
		}
		
		// ==== Step 2 ====
		// Check for the base case. 
		// If the range [start, end) is small enough, just return a new leaf node.
		if (end - start <= 10)
			return new BvhNode(minBigBound, maxBigBound, null, null, start, end);
		
		// ==== Step 3 ====
		// Figure out the widest dimension (x or y or z).
		// If x is the widest, set widestDim = 0. If y, set widestDim = 1. If z, set widestDim = 2.
		int widestDim = 0;
		double xWidth = maxBigBound.x - minBigBound.x;
		double yWidth = maxBigBound.y - minBigBound.y;
		double zWidth = maxBigBound.z - minBigBound.z;
		if (yWidth > xWidth)
		{
			if (yWidth > zWidth)
				widestDim = 1;
			else
				widestDim = 2;
		}
		else if (zWidth > xWidth)
			widestDim = 2;
		
		// ==== Step 4 ====
		// Sort surfaces according to the widest dimension.
		// You can also implement O(n) randomized splitting algorithm.
		cmp.setIndex(widestDim);
		Arrays.sort(surfaces, start, end, cmp);
		
		// ==== Step 5 ====
		// Recursively create left and right children.
		BvhNode leftChild = createTree(start, (start+end)/2);
		BvhNode rightChild = createTree((start+end)/2, end);
		return new BvhNode(minBigBound, maxBigBound, leftChild, rightChild, start, end);
		
	}

}

/**
 * A subclass that compares the average position two surfaces by a given
 * axis. Use the setIndex(i) method to select which axis should be considered.
 * i=0 -> x-axis, i=1 -> y-axis, and i=2 -> z-axis.  
 *
 */
class MyComparator implements Comparator<Surface> {
	int index;
	public MyComparator() {  }

	public void setIndex(int index) {
		this.index = index;
	}

	public int compare(Surface o1, Surface o2) {
		double v1 = o1.getAveragePosition().getE(index);
		double v2 = o2.getAveragePosition().getE(index);
		if(v1 < v2) return 1;
		if(v1 > v2) return -1;
		return 0;
	}

}
