
package ray.shader;

import ray.IntersectionRecord;
import ray.Ray;
import ray.Scene;
import ray.light.Light;
import ray.math.Color;
import ray.math.Vector3;

/**
 * A Phong material.
 *
 * @author ags, pramook
 */
public class Phong extends Shader {

	/** The color of the diffuse reflection. */
	protected final Color diffuseColor = new Color(1, 1, 1);
	public void setDiffuseColor(Color diffuseColor) { this.diffuseColor.set(diffuseColor); }

	/** The color of the specular reflection. */
	protected final Color specularColor = new Color(1, 1, 1);
	public void setSpecularColor(Color specularColor) { this.specularColor.set(specularColor); }

	/** The exponent controlling the sharpness of the specular reflection. */
	protected double exponent = 1.0;
	public void setExponent(double exponent) { this.exponent = exponent; }

	public Phong() { }

	/**
	 * @see Object#toString()
	 */
	public String toString() {    
		return "phong " + diffuseColor + " " + specularColor + " " + exponent + " end";
	}

	/**
	 * Evaluate the intensity for a given intersection using the Phong shading model.
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
		Vector3 outgoing = new Vector3();
		outgoing.sub(ray.origin, record.location);
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
					Vector3 halfVec = new Vector3();
					halfVec.add(incoming, outgoing);
					halfVec.normalize();
					double halfDotNormal = Math.max(0.0 ,halfVec.dot(record.normal));
					double factor = Math.pow(halfDotNormal, exponent);
					
					color.set(specularColor);
					color.scale(factor);
					color.scaleAdd(dotProd, diffuseColor);
					color.scale(light.intensity);
					shadowRay.attenuate(color, light.position);
					outIntensity.add(color);
				}
			}
		}
		
		ray.attenuate(outIntensity, record.location);
	}

}