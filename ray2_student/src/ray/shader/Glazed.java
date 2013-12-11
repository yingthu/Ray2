
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
		
		/* Generate Reflected Ray */
		Vector3 outgoing = new Vector3();
		outgoing.sub(ray.origin, record.location);
		Vector3 outgoingNorm = new Vector3(outgoing);
		outgoingNorm.normalize();
		
		Vector3 normalNorm = record.normal;
		normalNorm.normalize();
		
		//Project outgoing ray to normal
		double projection = outgoingNorm.dot(normalNorm);
		
		//Fresnel Coefficient using Schlick's Approx
		double n_1, n_2;
		if (projection > 0) { //outgoing is outside the material
			n_1 = 1.0;
			n_2 = refractiveIndex;
		}
		else if (projection < 0) { //outgoing is inside the material
			System.out.println("Outgoing is inside the material");
			return ;
		}
		else { //this should never happen
			//if the outgoing and the normal are perp., then the
			//outgoing is implied not to intersect the surface, contradiction!
			System.out.println("Outgoing is on the material!");
			return ;
		}
		
		double r_0 = Math.pow(((n_2 - n_1)/(n_2 + n_1)), 2.0);
		//Fresnel Coefficient
		//r_0 + (1 - r_0)(1 - cos(theta))^5
		//cos(theta) = (normalNorm dot outgoingNorm) / (||normalNorm|| ||outgoingNorm||)
		double fresnel = r_0 + (1.0 - r_0) * Math.pow(1.0 - projection, 5.0);
		
		//Reflected Ray = 2 * normalNorm * (normalNorm dot outgoingNorm) - outgoingNorm
		Vector3 reflectedRayDir = new Vector3(normalNorm);
		reflectedRayDir.scale(2.0);
		reflectedRayDir.scale(projection);
		reflectedRayDir.sub(outgoingNorm);
		reflectedRayDir.normalize();
		
		Ray reflectedRay = new Ray();
		reflectedRay.set(record.location, reflectedRayDir);
		reflectedRay.makeOffsetRay();
		Color reflectedColor = new Color();
		RayTracer.shadeRay(reflectedColor, scene, reflectedRay, depth);
		reflectedColor.scale(fresnel);

		/* substrate material */
		substrate.shade(outIntensity, scene, ray, record, depth);
		
		outIntensity.scale(1.0-fresnel);
		outIntensity.add(reflectedColor);
	}
}