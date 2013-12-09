
package ray.camera;

import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

/**
 * Represents a camera with perspective view.
 * For this camera, the view window corresponds to a rectangle on a
 * plane perpendicular to projNormal but at distance projDistance from
 * viewPoint in the direction of viewDir. A ray with its origin at viewPoint
 * going in the direction of viewDir should intersect the center
 * of the image plane. Given u and v, you should compute a point on the
 * rectangle corresponding to (u,v), and create a ray from viewPoint that
 * passes through the computed point.
 */
public class PerspectiveCamera extends Camera {
  
  protected double projDistance = 1.0;
  public void setprojDistance(double projDistance) { this.projDistance = projDistance; }
  
  /*
   * Derived values that are computed before ray generation.
   * basisU, basisV, and basisW form an orthonormal basis.
   * basisW is parallel to projNormal.
   */
  protected final Vector3 basisU = new Vector3();
  protected final Vector3 basisV = new Vector3();
  protected final Vector3 basisW = new Vector3();
  protected final Vector3 centerDir = new Vector3();
  
  // Has the view been initialized?
  protected boolean initialized = false;
  
  /**
   * Initialize the derived view variables to prepare for using the camera.
   */
  public void initView() {
    
	if (projNormal.length() == 0) {
		projNormal.set(viewDir);
	}
	basisW.set(projNormal);
	if (basisW.dot(viewDir) > 0)
		basisW.scale(-1);
    basisW.normalize();
    
    basisU.cross(viewUp, basisW);
    basisU.normalize();
    basisV.cross(basisW, basisU);
    basisV.normalize();
    
    centerDir.set(viewDir);
    centerDir.normalize();
    centerDir.scale(projDistance);
    
    initialized = true;
  }
  
  /**
   * Set outRay to be a ray from the camera through a point in the image.
   *
   * @param outRay The output ray (not normalized)
   * @param inU The u coord of the image point (range [0,1])
   * @param inV The v coord of the image point (range [0,1])
   */
  public void getRay(Ray outRay, double inU, double inV) {
    if (!initialized) initView();

    double u = inU * 2 - 1;
    double v = inV * 2 - 1;
    
    // Set the output ray
    outRay.origin.set(viewPoint);
    outRay.direction.set(centerDir);
    outRay.direction.scaleAdd(u * viewWidth / 2, basisU);
    outRay.direction.scaleAdd(v * viewHeight / 2, basisV);
    outRay.direction.normalize();
    
    outRay.makeOffsetRay();
  }
}