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
			n_1 = refractiveIndex;
			n_2 = 1.0;
			normalNorm.scale(-1.0); //make normal on the same side as outgoingNorm
			normalNorm.normalize();
			projection = outgoingNorm.dot(normalNorm);
		}
		
		else { //this should never happen
			//if the outgoing and the normal are perp., then the
			//outgoing is implied not to intersect the surface, contradiction!
			System.out.println("This happened!");
			return;
		}
		
		double r_0 = Math.pow(((n_2 - n_1)/(n_2 + n_1)), 2.0);
		//Fresnel Coefficient
		//r_0 + (1 - r_0)(1 - cos(theta))^5
		//cos(theta) = (normalNorm dot outgoingNorm) / (||normalNorm|| ||outgoingNorm||)
		
		double fresnel = r_0 + (1.0 - r_0) * Math.pow(1.0 - projection, 5.0);
		
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
		
		
		projection = outgoing.dot(normalNorm);
		double determinant = 1 - (Math.pow(n_1, 2.0) * (1-Math.pow(projection, 2.0)))/(Math.pow(n_2, 2.0));
		
		if (determinant < 0.0) {
			reflectedColor.scale(1.0);
			outIntensity.set(reflectedColor);
		} else {
			reflectedColor.scale(fresnel);
			
			Vector3 transmittedVector = new Vector3(outgoing);
			Vector3 subtractArgOne = new Vector3(normalNorm);
			Vector3 subtractArgTwo = new Vector3(normalNorm);
			
			subtractArgOne.scale(projection);
			transmittedVector.sub(subtractArgOne);
			transmittedVector.scale(n_1/n_2);
			
			subtractArgTwo.scale(Math.sqrt(determinant));
			transmittedVector.sub(subtractArgTwo);
			
			Ray refractedRay = new Ray();
			refractedRay.makeOffsetRay();
			reflectedRay.set(record.location, transmittedVector);
			RayTracer.shadeRay(outIntensity, scene, refractedRay, depth);
			outIntensity.scale(1-fresnel);
			outIntensity.add(reflectedColor);
		}
		
	}
	
}