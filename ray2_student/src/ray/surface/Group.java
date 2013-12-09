
package ray.surface;

import java.util.ArrayList;
import java.util.Iterator;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Matrix4;
import ray.math.Point3;
import ray.math.Vector3;

public class Group extends Surface {

  /** List of objects under this group. */
  ArrayList<Surface> objs = new ArrayList<Surface>();
  
  /** The transformation matrix associated with this group. */
  private Matrix4 transformMat;
  
  /** A shared temporary matrix */
  static Matrix4 tmp = new Matrix4();
  
  public Group() {
    transformMat = new Matrix4();
    transformMat.setIdentity();
  }
  
  /**
   * Compute tMat, tMatInv, tMatTInv for this group and propagate values to the children under it.
   * @param cMat The transformation matrix of the parent for this node.
   * @param cMatInv The inverse of cMat.
   * @param cMatTInv The inverse of the transpose of cMat.
   */
  public void setTransformation(Matrix4 cMat, Matrix4 cMatInv, Matrix4 cMatTInv) {
    // TODO: Compute tMat, tMatInv, tMatTInv using transformMat.
    // Hint: We apply the transformation from bottom up the tree. 
    // i.e. The child's transformation will be applied to objects before its parent's.
	
	// tMat = cMat * transformMat
    tMat = new Matrix4(transformMat);
    tMat.leftCompose(cMat);
    // inverse of tMat
    tMatInv = new Matrix4(tMat);
    tMatInv.invert();
    // inverse transpose of tMat
    tMatTInv = new Matrix4(tMat);
    tMatTInv.transpose();
    tMatTInv.invert();
    
    // TODO: Call setTransformation(tMat, tMatInv, tMatTInv) on each of the children.
    for (Iterator<Surface> iter = objs.iterator(); iter.hasNext();)
    {
    	Surface s = iter.next();
    	s.setTransformation(tMat, tMatInv, tMatTInv);
    	
    }

    computeBoundingBox();
  }
  
  
  public void setTranslate(Vector3 T) { 
    tmp.setTranslate(T);
    transformMat.rightCompose(tmp);
  }
  
  public void setRotate(Point3 R) {
    // TODO: add rotation to transformMat
	
	// transformMat_new = transformMat * Rz * Ry * Rx
	tmp.setRotate(R.z, new Vector3(0.0, 0.0, 1.0));
	transformMat.rightCompose(tmp);
	tmp.setRotate(R.y, new Vector3(0.0, 1.0, 0.0));
	transformMat.rightCompose(tmp);
	tmp.setRotate(R.z, new Vector3(1.0, 0.0, 0.0));
	transformMat.rightCompose(tmp);
  }
  
  public void setScale(Vector3 S) { 
    // TODO: add scale to transformMat
    tmp.setScale(S);
    transformMat.rightCompose(tmp);
  }
  
  public void addSurface(Surface a) {
    objs.add(a);
  }
  
  public boolean intersect(IntersectionRecord outRecord, Ray ray) { return false; }
  public void computeBoundingBox() {  }

  public void appendRenderableSurfaces (ArrayList<Surface> in) {
    for (Iterator<Surface> iter = objs.iterator(); iter.hasNext();)
      iter.next().appendRenderableSurfaces(in);
  }
}