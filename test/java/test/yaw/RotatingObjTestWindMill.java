package test.yaw;

import org.joml.Vector3f;
import yaw.engine.SceneRenderer;
import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.items.Item;
import yaw.engine.items.ItemGroup;
import yaw.engine.light.AmbientLight;
import yaw.engine.light.DirectionalLight;
import yaw.engine.light.LightModel;
import yaw.engine.mesh.Mesh;
import yaw.engine.resources.ObjLoader;

import java.io.IOException;

/**
 * Basic example of a cube rotating on y axis
 */
public class RotatingObjTestWindMill implements UpdateCallback {
	private int nbUpdates = 0;
	private double totalDeltaTime = 0.0;
	private static long deltaRefreshMillis = 1000;
	private long prevDeltaRefreshMillis = 0;
	private Item obj;
	private float speed = 0.1f;

	public RotatingObjTestWindMill(Item obj) {
		this.obj = obj;
	}
	
	public Item getItem() {
		return obj;
	}

	
	@Override
	public void update(double deltaTime) {
		nbUpdates++;
		totalDeltaTime += deltaTime;
		
		long currentMillis = System.currentTimeMillis();
		if (currentMillis - prevDeltaRefreshMillis > deltaRefreshMillis) {
			double avgDeltaTime = totalDeltaTime / (double) nbUpdates;
			//System.out.println("Average deltaTime = " + Double.toString(avgDeltaTime) +" s ("+nbUpdates+")");
			nbUpdates = 0;
			totalDeltaTime = 0.0;
			prevDeltaRefreshMillis = currentMillis;
		}

		//cube.rotateXYZ(0f, 3.1415925f * speed * (float) deltaTime, 0f);
		//cube.rotateZAround(1f, new Vector3f(0f, 0f, -3f));

		float angle = 3.0f * 3.1415925f * (float) deltaTime * speed;
		//System.out.println(deltaTime);
		obj.rotateY(angle);
		//cube.rotateXYZAround(0f, 3.1415925f * speed * (float) deltaTime, 0f, new Vector3f(0f, 0f, -10f));
		//cube.rotateX(0.0f);


	}
	
	public static void main(String[] args) {

		World world = new World(0, 0, 800, 600);
		world.installScene(new SceneRenderer(new LightModel()));
		world.getSceneLight().setDirectionalLight(new DirectionalLight(new Vector3f(1,1,1), 0.7f, new Vector3f(-1,-1,-1)));
		world.getSceneLight().setAmbientLight(new AmbientLight(0.3f));
		ObjLoader objLoader = new ObjLoader();
		try {
			objLoader.parseFromResource("/resources/models/windmill.obj");

		} catch (IOException e) {
			System.out.println("Errror : " + e.getMessage());
			System.exit(1);
		}

		/* DEBUG
		Geometry geom = objLoader.getScene().getGeometryByIndex(10).build();
		Mesh objm = new Mesh(geom);

		objm.setDrawingStrategy(new DefaultDrawingStrategy());
		Material mat = new Material();
		mat.setColor(new Vector3f(0.1f , 0.7f, 0.9f));
		objm.setMaterial(mat);
		ItemObject obji = world.createItemObject("obj", 0f, 0f, 0f, 1.0f, objm);
		obji.translate(0f,0f, -5f);
		*/

		Mesh[] meshes = objLoader.getScene().buildMeshes(false);

		int i = 1;
		ItemGroup grp = world.createGroup("obj");
		for(Mesh mesh : meshes) {
			Item obj = world.createItemObject("obj_" + i, 0, 0, 0, 1.0f, mesh);
			grp.add(obj.getId(), obj);
			i += 1;
		}

		grp.translate(0f,0f, -5f);

		world.getCamera().translate(0, 3,5.5f);

		RotatingObjTestWindMill rObj = new RotatingObjTestWindMill(grp);

		world.registerUpdateCallback(rObj);

		world.setBackgroundColor(0.5f, 0.5f, 0.5f);





		world.launchSync();
	}

}
