
package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
  
  /** The center of the sphere. */
  protected final Point3 center = new Point3();
  public void setCenter(Point3 center) { this.center.set(center); }
  
  /** The radius of the sphere. */
  protected double radius = 1.0;
  public void setRadius(double radius) { this.radius = radius; }
  
  public Sphere() { }
  
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
    
    // Rename the common vectors so I don't have to type so much
    Vector3 d = ray.direction;
    Point3 c = center;
    Point3 o = ray.origin;
    
    // Compute some factors used in computation
    double qx = o.x - c.x;
    double qy = o.y - c.y;
    double qz = o.z - c.z;
    double dd = d.lengthsquared();
    double qd = qx * d.x + qy * d.y + qz * d.z;
    double qq = qx * qx + qy * qy + qz * qz;
    
    // solving the quadratic equation for t at the pts of intersection
    // dd*t^2 + (2*qd)*t + (qq-r^2) = 0
    double discriminantsqr = (qd * qd - dd * (qq - radius * radius));
    
    // If the discriminant is less than zero, there is no intersection
    if (discriminantsqr < 0) {
      return false;
    }
    
    // Otherwise check and make sure that the intersections occur on the ray (t
    // > 0) and return the closer one
    double discriminant = Math.sqrt(discriminantsqr);
    double t1 = (-qd - discriminant) / dd;
    double t2 = (-qd + discriminant) / dd;
    double t = 0;
    if (t1 > ray.start && t1 < ray.end) {
      t = t1;
    }
    else if (t2 > ray.start && t2 < ray.end) {
      t = t2;
    }
    else {
      return false; // Neither intersection was in the ray's half line.
    }
    
    // There was an intersection, fill out the intersection record
    if (outRecord != null) {
      outRecord.t = t;
      ray.evaluate(outRecord.location, t);
      outRecord.surface = this;
      
      outRecord.normal.sub(outRecord.location, center);
      
      // TODO: Transform the location and normal back into world coordinates.

      outRecord.normal.normalize();

    }
    
    return true;

  }
  
  public void computeBoundingBox() {
    // TODO: Compute the bounding box and store the result in
    // averagePosition, minBound, and maxBound.

  }
  
  /**
   * @see Object#toString()
   */
  public String toString() {
    return "sphere " + center + " " + radius + " " + shader + " end";
  }

}