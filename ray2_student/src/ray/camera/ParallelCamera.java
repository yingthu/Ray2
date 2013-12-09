package ray.camera;

import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

/**
 * Represents a camera with a parallel view.
 * For this camera, all rays should start at the viewing window and have a direction
 * parallel to viewDir. The viewing window's center is defined by viewPoint; all rays
 * will have their origins offset from this point. Note that the viewing window's normal
 * is defined by projNormal, which may be different than the viewing direction.
 *
 */
public class ParallelCamera extends Camera {
	
	/*
	 * Derived values that are computed before ray generation.
	 * basisU, basisV, and basisW form an orthonormal basis.
	 * 
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
	    
	    centerDir.set(viewDir);
	    centerDir.normalize();
	    
	    initialized = true;
    }


    /**
     * Set outRay to be a ray originating from a point in the image.
     *
     * @param outRay The output ray (not normalized)
     * @param inU The u coord of the image point (range [0,1])
     * @param inV The v coord of the image point (range [0,1])
     */
	@Override
	public void getRay(Ray outRay, double inU, double inV) {
		if (!initialized) initView();

	    double u = inU * 2 - 1;
	    double v = inV * 2 - 1;
	    
	    // Set the output ray
	    outRay.origin.set(viewPoint);
	    outRay.origin.scaleAdd(u * viewWidth / 2, basisU);
	    outRay.origin.scaleAdd(v * viewHeight /2, basisV);
	    outRay.direction.set(centerDir);
	    
	    outRay.makeOffsetRay();
	}
	
}