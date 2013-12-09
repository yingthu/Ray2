package ray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ray.accel.AccelStruct;
import ray.accel.Bvh;
import ray.accel.NaiveAccelStruct;
import ray.camera.Camera;
import ray.light.Light;
import ray.math.Color;
import ray.math.Matrix4;
import ray.shader.Shader;
import ray.surface.Surface;

/**
 * The scene is just a collection of objects that compose a scene. The camera,
 * the list of lights, and the list of surfaces.
 *
 * @author ags, pramook
 */
public class Scene {
	
	/** The camera for this scene. */
	protected Camera camera;
	public void setCamera(Camera camera) { this.camera = camera; }
	public Camera getCamera() { return this.camera; }
	
	/** The background color for this scene. Any rays that don't hit a surface
	 *  return this color.
	 */
	protected Color backColor = new Color(0, 0, 0);
	public void setBackColor(Color color) { this.backColor = color; }
	public Color getBackColor() { return this.backColor; }
	
	/** The list of lights for the scene. */
	protected ArrayList<Light> lights = new ArrayList<Light>();
	public void addLight(Light toAdd) { lights.add(toAdd); }
	public List<Light> getLights() { return this.lights; }
	
	/** The list of surfaces for the scene. */
	protected ArrayList<Surface> surfaces = new ArrayList<Surface>();
	public void addSurface(Surface toAdd) { surfaces.add(toAdd); }
	public List<Surface> getSurfaces() { return this.surfaces; }
	
	/** The list of shaders in the scene. */
	protected ArrayList<Shader> shaders = new ArrayList<Shader>();
	public void addShader(Shader toAdd) { shaders.add(toAdd); }
	
	/** Image to be produced by the renderer **/
	protected Image outputImage;
	public Image getImage() { return this.outputImage; }
	public void setImage(Image outputImage) { this.outputImage = outputImage; }

	/** samples^2 is the number of samples per pixel **/
	protected int samples;
	public int getSamples() { return this.samples==0 ? 1 : this.samples; }
	public void setSamples(int n) {	samples = (int)Math.round(Math.sqrt(n)); }
	
	/** The absorption coefficient of the scene */
	protected final Color absorption = new Color(0.0, 0.0, 0.0);
	public void setAbsorption(Color value) { absorption.set(value); }
	public Color getAbsorption() { return absorption; }

	/** The acceleration structure **/
	// TODO: Change this to:
	
	// protected AccelStruct accelStruct = new Bvh();
	
	// ...when you are finished with Bvh and BvhNode.
	protected AccelStruct accelStruct = new NaiveAccelStruct();
	public void setAccelStruct(AccelStruct accelStruct) { this.accelStruct = accelStruct; }
	public AccelStruct getAccelStruct() { return accelStruct; }
	
	/**
	 * Initialize transformation matrices for entire tree hierarchy
	 */
	public void setTransform() {
		Matrix4 id = new Matrix4();
		id.setIdentity();
		for (Iterator<Surface> iter = surfaces.iterator(); iter.hasNext();) {
			Surface currSurface = iter.next();
			currSurface.setTransformation(id, id, id);
		}
	}
	
	/**
	 * Set outRecord to the first intersection of ray with the scene. Return true
	 * if there was an intersection and false otherwise. If no intersection was
	 * found outRecord is unchanged.
	 *
	 * @param outRecord the output IntersectionRecord
	 * @param ray the ray to intesect
	 * @return true if and intersection is found.
	 */
	public boolean getFirstIntersection(IntersectionRecord outRecord, Ray ray) {
		return accelStruct.intersect(outRecord, ray, false);
	}
	
	/**
	 * Shadow ray calculations can be considerably accelerated by not bothering to find the
	 * first intersection.  This record returns any intersection of the ray and the surfaces
	 * and returns true if one is found.
	 * @param ray the ray to intersect
	 * @return true if any intersection is found
	 */
	public boolean getAnyIntersection(Ray ray) {
		return accelStruct.intersect(new IntersectionRecord(), ray, true);	
	}
}