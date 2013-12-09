
package ray.shader;

import ray.IntersectionRecord;
import ray.Ray;
import ray.Scene;
import ray.math.Color;
import ray.math.Vector3;
import ray.RayTracer;

public class Glazed extends Shader {

	/**
	 * The index of refraction of this material. Used to calculate the Fresnel coefficient.
	 */
	protected double refractiveIndex;
	public void setRefractiveIndex(double refractiveIndex) { this.refractiveIndex = refractiveIndex; }

	/**
	 * The underlying material beneath the glaze.
	 */
	protected Shader substrate;
	public void setSubstrate(Shader substrate) {
		this.substrate = substrate; 
	}

	public Glazed() { }

	/**
	 * Determine a color given information about the intersection of a ray and a surface. A
	 * glazed material has two components: the first is determined by the surface's substrate,
	 * and the second is a perfectly specular component in the perfect-mirror reflection 
	 * direction. The factor of the specular component is the Fresnel coefficient value,
	 * computed using the refractive index.
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