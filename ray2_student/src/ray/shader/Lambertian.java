
package ray.shader;

import ray.IntersectionRecord;
import ray.Ray;
import ray.Scene;
import ray.light.Light;
import ray.math.Color;
import ray.math.Vector3;

/**
 * A Lambertian material scatters light equally in all directions. BRDF value is
 * a constant
 *
 * @author ags
 */
public class Lambertian extends Shader {

	/** The color of the surface. */
	protected final Color diffuseColor = new Color(1, 1, 1);
	public void setDiffuseColor(Color inDiffuseColor) { diffuseColor.set(inDiffuseColor); }

	public Lambertian() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "lambertian: " + diffuseColor;
	}

	/**
	 * Evaluate the intensity for a given intersection using the Lambert shading model.
	 * 
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Color outIntensity, Scene scene, Ray ray, IntersectionRecord record, int depth) {
		Vector3 incoming = new Vector3();
		Color color = new Color();
		Ray shadowRay = new Ray();
		shadowRay.setAbsorption(scene.getAbsorption()); // Assume we are not inside a surface
		
		outIntensity.set(0);
		for(Light light : scene.getLights()) {
			if(!isShadowed(scene, light, record, shadowRay)) {
				incoming.sub(light.position, record.location);
				incoming.normalize();
				double dotProd = record.normal.dot(incoming);
				if (dotProd <= 0)
					continue;
				else {
					color.set(diffuseColor);
					color.scale(dotProd);
					color.scale(light.intensity);
					shadowRay.attenuate(color, light.position);
					outIntensity.add(color);
				}
			}
		}
		
		ray.attenuate(outIntensity, record.location);
	}

}