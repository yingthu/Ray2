package ray;

import ray.math.Color;
import ray.math.Point3;
import ray.math.Vector3;

/**
 * A ray is simply an origin point and a direction vector.
 *
 * @author ags
 */
public class Ray {

  /**
   * This quantity represents a "small amount" to handle comparisons in
   * floating point computations.  It is often useful to have one global
   * value across the ray tracer that stands for a "small amount".
   */
  public static final double EPSILON = 1e-6;

  /** The starting point of the ray. */
  public final Point3 origin = new Point3();

  /** The normalized direction in which the ray travels. */
  public final Vector3 direction = new Vector3();
  
  /** The absorption color of the medium through which this ray is traveling. */
  public final Color absorption = new Color();

  /**
  * It is convenient to have a ray have a start and end t values.
  * The start value lets the ray be offset slightly from surfaces
  * avoiding self intersection through numerical inaccuracies, and
  * the end value lets rays be cut off at certain points (such
  * as for shadow rays).
  */

  /** Starting t value of the ray **/
  public double start;

  /** Ending t value of the ray **/
  public double end;

  /**
   * Default constructor generates a trivial ray.
   */
  public Ray() {}

  /**
   * The explicit constructor. Values should be set here, and any
   * variables that need to be calculated should be done here.
   * @param newOrigin The origin of the new ray.
   * @param newDirection The direction of the new ray.
   */
  public Ray(Point3 newOrigin, Vector3 newDirection) {

    origin.set(newOrigin);
    direction.set(newDirection);
  }
  
  /**
   * The explicit constructor. Values should be set here, and any
   * variables that need to be calculated should be done here.
   * @param newOrigin The origin of the new ray.
   * @param newDirection The direction of the new ray.
   * @param newColor The absorption color of the new ray.
   */
  public Ray(Point3 newOrigin, Vector3 newDirection, Color newAbsorption) {

	  origin.set(newOrigin);
	  direction.set(newDirection);
	  absorption.set(newAbsorption);
  }

  /**
   * Sets this ray with the given direction and origin.
   * @param newOrigin the new origin point
   * @param newDirection the new direction vector
   */
  public void set(Point3 newOrigin, Vector3 newDirection) {

	  origin.set(newOrigin);
	  direction.set(newDirection);
  }
  
  public void setAbsorption(Color newAbsorption) {
	  
	  absorption.set(newAbsorption);
  }

  /**
   * Sets outPoint to the point on this ray t units from the origin.  Note that t can
   * be considered as distance along this ray only if the ray direction is normalized.
   * @param outPoint the output point
   * @param t The distance along the ray.
   */
  public void evaluate(Point3 outPoint, double t) {

    outPoint.set(origin);
    outPoint.scaleAdd(t, direction);

  }
  
  /**
   * Takes a color and compute the distance that the ray travels and attenuates
   * the output color by the ray's absorption according to Beer's law.
   * @param inColor The color to be attenuated.
   * @param endPoint The location of the end of the ray. This point is assumed to be
   * on the ray.
   */
  public void attenuate(Color inColor, Point3 endPoint) {
	  // TODO: fill in this function.
	  // Distance that ray traveled through
	  double l = endPoint.distance(origin);
	  // Scale down
	  inColor.r *= Math.exp(-absorption.r*l);
	  inColor.g *= Math.exp(-absorption.g*l);
	  inColor.b *= Math.exp(-absorption.b*l);
  }	
  
  /**
   * Moves the origin of the ray EPISILON units along ray.  Avoids self intersection
   * when casting rays from surfaces.
   */
  public void makeOffsetRay() {

    start = EPSILON;
    end = Double.POSITIVE_INFINITY;

  }

  /**
   * Offsets the ray origin by EPSILON and sets the end point of the ray to t.
   * @param newEnd the endpoint of the ray.
   */
  public void makeOffsetSegment(double newEnd) {

    start = EPSILON;
    end = newEnd;


  }
}