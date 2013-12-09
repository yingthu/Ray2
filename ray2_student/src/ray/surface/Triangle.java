
package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector2;
import ray.math.Vector3;
import ray.shader.Shader;

/**
 * Represents a single triangle, part of a triangle mesh
 *
 * @author ags
 */
public class Triangle extends Surface {
  /** The normal vector of this triangle */
  Vector3 norm;
  
  /** The mesh that contains this triangle */
  Mesh owner;
  
  /** 3 indices to the vertices of this triangle. */
  int []index;
  
  double a, b, c, d, e, f;
  public Triangle(Mesh owner, int index0, int index1, int index2, Shader shader) {
    this.owner = owner;
    index = new int[3];
    index[0] = index0;
    index[1] = index1;
    index[2] = index2;
    
    Point3 v0 = owner.getVertex(index0);
    Point3 v1 = owner.getVertex(index1);
    Point3 v2 = owner.getVertex(index2);
    
    if(!owner.existsNormals()) {
      Vector3 e0 = new Vector3(), e1 = new Vector3();
      e0.sub(v1, v0);
      e1.sub(v2, v0);
      norm = new Vector3();
      norm.cross(e0, e1);
    }
    a = v0.x-v1.x;
    b = v0.y-v1.y;
    c = v0.z-v1.z;
    
    d = v0.x-v2.x;
    e = v0.y-v2.y;
    f = v0.z-v2.z;
    
    this.setShader(shader);
  }

  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param rayIn the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO: fill in this function.
    // Hint: This object can be transformed by a transformation matrix,
    // so the rayIn needs to be processed so that it is in the same coordinate as the object.
	rayIn = untransformRay(rayIn);
	Ray ray = rayIn;
	
	// Coordinates of triangle vertex a
	double xa = owner.getVertex(index[0]).x;
	double ya = owner.getVertex(index[0]).y;
	double za = owner.getVertex(index[0]).z;
	// Coordinates of ray origin e
	double xe = ray.origin.x;
	double ye = ray.origin.y;
	double ze = ray.origin.z;
	// Coordinates of ray direction d
	double xd = ray.direction.x;
	double yd = ray.direction.y;
	double zd = ray.direction.z;
	
	// Compute beta, gamma and t according to Shirley and Marschner 4.4.2
	double ei_hf = e * zd - yd * f;
	double gf_di = xd * f - d * zd;
	double dh_eg = d * yd - e * xd;
	double ak_jb = a * (ya - ye) - (xa - xe) * b;
	double jc_al = (xa - xe) * c - a * (za - ze);
	double bl_kc = b * (za - ze) - (ya - ye) * c;
	double M = a * ei_hf + b * gf_di + c * dh_eg;
	// Compute t first
	double t = - (f * ak_jb + e * jc_al + d * bl_kc) / M;
	if (t < ray.start || t > ray.end)
		return false;
	// Compute gamma
	double gamma = (zd * ak_jb + yd * jc_al + xd * bl_kc) / M;
	if (gamma < 0 || gamma > 1)
		return false;
	// Compute beta
	double beta = ((xa-xe) * ei_hf + (ya-ye) * gf_di + (za-ze) * dh_eg) / M;
	if (beta < 0 || beta > 1 - gamma)
		return false;
	
	// Set the values
	if (outRecord != null)
	{
		outRecord.t = t;
	    ray.evaluate(outRecord.location, t);
	    outRecord.surface = this;
	    // Interpolate the normal using barycentric coordinates
	    if (norm == null)
	    {
	    	double alpha = 1.0 - beta - gamma;
	    	Vector3 an = owner.getNormal(index[0]);
	    	Vector3 bn = owner.getNormal(index[1]);
	    	Vector3 cn = owner.getNormal(index[2]);
	    	an.scale(alpha);
	    	bn.scale(beta);
	    	cn.scale(gamma);
	    	Vector3 n = new Vector3(an.x+bn.x+cn.x, an.y+bn.y+cn.y, an.z+bn.z+cn.z);
	    	outRecord.normal.set(n);
	    }
	    else
	    	outRecord.normal.set(norm);
        // Transform the location by tMat
	    tMat.rightMultiply(outRecord.location);
	    // Transform the normal by tMatTInv
	    tMatTInv.rightMultiply(outRecord.normal);
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
    return "Triangle ";
  }
}