
package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

public class Box extends Surface {
  
  /* The corner of the box with the smallest x, y, and z components. */
  protected final Point3 minPt = new Point3();
  public void setMinPt(Point3 minPt) { this.minPt.set(minPt); }
  
  /* The corner of the box with the largest x, y, and z components. */
  protected final Point3 maxPt = new Point3();
  public void setMaxPt(Point3 maxPt) { this.maxPt.set(maxPt); }
  
  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param ray the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO: Process rayIn so that it is in the same coordinates as the object.
	// This should be a single line.
    
    Ray ray = rayIn;
    
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
    
    if (outRecord != null) {
      Point3 sp = new Point3(ray.origin);
      sp.scaleAdd(Ray.EPSILON, ray.direction);
      if(sp.x >= minPt.x && sp.x <= maxPt.x && sp.y >= minPt.y && sp.y <= maxPt.y && sp.z >= minPt.z && sp.z <= maxPt.z) {
        outRecord.t = tMax;
        ray.evaluate(outRecord.location, tMax);
        if (tMax == txMax)
          outRecord.normal.set(1, 0, 0);
        else if (tMax == tyMax)
          outRecord.normal.set(0, 1, 0);
        else
          outRecord.normal.set(0, 0, 1);
        
        if (outRecord.normal.dot(ray.direction) < 0)
          outRecord.normal.scale(-1);
      } else { 
        outRecord.t = tMin;
        ray.evaluate(outRecord.location, tMin);
        if (tMin == txMin)
          outRecord.normal.set(1, 0, 0);
        else if (tMin == tyMin)
          outRecord.normal.set(0, 1, 0);
        else
          outRecord.normal.set(0, 0, 1);
        
        if (outRecord.normal.dot(ray.direction) > 0)
          outRecord.normal.scale(-1);
        
      }  
      outRecord.surface = this;
      
      // TODO: Transform the location and normal back into world coordinates.

      outRecord.normal.normalize();
    }
    
    return true;

  }

  public void computeBoundingBox() {
    // TODO: Compute the bounding box and store the result in
    // averagePosition, minBound, and maxBound.
    // Hint: The bounding box is not the same as just minPt and maxPt, because 
    // this object can be transformed by a transformation matrix.
    
  }
  
  /**
   * @see Object#toString()
   */
  public String toString() {
    return "Box ";
  }
}