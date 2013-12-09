package ray.shader;

import ray.IntersectionRecord;
import ray.Ray;
import ray.Scene;
import ray.math.Color;
import ray.math.Vector3;
import ray.RayTracer;

/**
 * A Glass Shader. 
 *
 */
public class Glass extends Shader {

	/**
	 * The index of refraction of this material. Used when calculating Snell's Law.
	 */
	protected double refractiveIndex;
	public void setRefractiveIndex(double refractiveIndex) { this.refractiveIndex = refractiveIndex; }


	public Glass() { 
		refractiveIndex = 1.0;
	}

	/**
	 * Determine a color given information about the intersection of a ray and a surface. Glass does
	 * not directly interact with light, and so outIntensity is set to some weighted combination of
	 * internal and external reflections. Because of this, the ray may be coming from inside the surface,
	 * and this must be accounted for. Reflection angles are determined by Snell's law, and total internal
	 * reflection effects are considered.
	 * 
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Color outIntensity, Scene scene, Ray ray, IntersectionRecord record, int depth) {
		// TODO: fill in this function.
		// You may find it helpful to create helper methods if the code here gets too long.
		
	}
	
}