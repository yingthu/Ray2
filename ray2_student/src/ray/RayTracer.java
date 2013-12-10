package ray;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ray.Image;
import ray.IntersectionRecord;
import ray.Parser;
import ray.Ray;
import ray.Scene;
import ray.TestFolderPath;
import ray.Workspace;
import ray.camera.Camera;
import ray.math.Color;
import ray.shader.Shader;
import ray.surface.Surface;

public class RayTracer {
	
	/**
	 * The maximum number of recursive tracing calls allowed
	 */
	public static final int MAX_DEPTH = 12;
	
	/**
	 * The main method takes all the parameters and assumes they are input files
	 * for the ray tracer. It tries to render each one and write it out to a PNG
	 * file named <input_file>.png.
	 *
	 * @param args
	 */
	public static final void main(String[] args) {
		RayTracer rayTracer = new RayTracer();
		rayTracer.run("data/scenes", args);
	}
	
	/**
	 * If filename is a directory, set testFolderPath = fn.
	 * And return a list of all .xml files inside the directory
	 * @param fn Filename or directory
	 * @return fn itself in case fn is a file, or all .xml files inside fn
	 */
	public ArrayList<String> getFileLists(String fn) {
		if(fn.endsWith("/"))
			fn.substring(0, fn.length()-1);

		File file = new File(fn);
		ArrayList<String> output = new ArrayList<String>();
		if(file.exists()) {
			if(file.isFile()) {
				// Extract the folder part of the name
				int dir_index = fn.lastIndexOf('/');
				if (dir_index > 0 && dir_index < fn.length()) {
					TestFolderPath.set(fn.substring(0, dir_index + 1));
				} else {  
					TestFolderPath.set("");
				}
				output.add(fn);
			} else {
				TestFolderPath.set(fn + "/");				
				for(String fl : file.list()) {
					if(fl.endsWith(".xml")) {
						output.add(TestFolderPath.get() + fl);
					}
				}  
			}
		}
		return output;
	}

	/**
	 * The run method takes all the parameters and assumes they are input files
	 * for the ray tracer. It tries to render each one and write it out to a PNG
	 * file named <input_file>.png.
	 *
	 * @param args
	 */
	public void run(String directory, String[] args) {

		Parser parser = new Parser();
		for (int ctr = 0; ctr < args.length; ctr++) {

			ArrayList<String> fileLists = getFileLists(directory + "/" + args[ctr]);

			for (String inputFilename : fileLists) {
				String outputFilename = inputFilename + ".png";

				// Parse the input file
				Scene scene = (Scene) parser.parse(inputFilename, Scene.class);

				// Propagate transformation matrix through the tree hierarchy
				scene.setTransform();

				// Create the acceleration structure.
				ArrayList<Surface> renderableSurfaces = new ArrayList<Surface>();
				List<Surface> surfaces = scene.getSurfaces();
				for (Iterator<Surface> iter = surfaces.iterator(); iter.hasNext();) {
					iter.next().appendRenderableSurfaces(renderableSurfaces);
				}
				Surface surfaceArray[] = new Surface[renderableSurfaces.size()];
				renderableSurfaces.toArray(surfaceArray);
				scene.getAccelStruct().build(surfaceArray); 

				// Render the scene
				renderImage(scene);

				// Write the image out
				scene.getImage().write(outputFilename);
			}
		}
	}
	
	/**
	 * The renderImage method renders the entire scene.
	 *
	 * @param scene The scene to be rendered
	 */
	public void renderImage(Scene scene) {

		// Get the output image
		Image image = scene.getImage();
		Camera cam = scene.getCamera();

		// Set the camera aspect ratio to match output image
		int width = image.getWidth();
		int height = image.getHeight();

		// Timing counters
		long startTime = System.currentTimeMillis();

		// Do some basic setup
		Workspace work = new Workspace();
		Ray ray = work.eyeRay;
		Color pixelColor = work.pixelColor;
		Color rayColor = work.rayColor;

		int total = height * width;
		int counter = 0;
		int lastShownPercent = 0;
		int samples = scene.getSamples();
		double sInv = 1.0/samples;
		double sInvD2 = sInv / 2;
		double sInvSqr = sInv * sInv;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				pixelColor.set(0, 0, 0);

				// TODO: Implement supersampling for antialiasing.
				// Each pixel should have (samples*samples) subpixels.
				rayColor.set(0, 0, 0);
				for (int i = 0; i < samples; i++)
					for (int j = 0; j < samples; j++)
					{
						rayColor.set(0, 0, 0);
						cam.getRay(ray, (x + (i+0.5)/samples) / width, (y + (j+0.5)/samples) / height);	
						ray.setAbsorption(scene.getAbsorption());
						shadeRay(rayColor, scene, ray, 1);
						pixelColor.add(rayColor);
					}
				pixelColor.scale(sInvSqr);

				

				image.setPixelColor(pixelColor, x, y);

				counter ++;
				if((int)(100.0 * counter / total) != lastShownPercent) {
					lastShownPercent = (int)(100.0*counter / total);
					System.out.println(lastShownPercent + "%");
				}
			}
		}

		// Output time
		long totalTime = (System.currentTimeMillis() - startTime);
		System.out.println("Done.  Total rendering time: "
				+ (totalTime / 1000.0) + " seconds");
	}

	/**
	 * This method returns the color along a single ray in outColor.
	 *
	 * @param outColor output space
	 * @param scene the scene
	 * @param ray the ray to shade
	 */
	public static void shadeRay(Color outColor, Scene scene, Ray ray, int depth) {

		// Reset the output color
		outColor.set(0, 0, 0);
		
		// TODO: Return immediately if depth is greater than MAX_DEPTH.  	   
		if (depth > MAX_DEPTH)
			return;
		
		IntersectionRecord intersectionRecord = new IntersectionRecord();			

		if (!scene.getFirstIntersection(intersectionRecord, ray))
			return ;
		
		Shader shader = intersectionRecord.surface.getShader();
		shader.shade(outColor, scene, ray, intersectionRecord, depth);
		
	}
}
