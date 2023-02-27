package test.yaw;

import org.joml.Vector3f;
import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.geom.Geometry;
import yaw.engine.items.ItemObject;
import yaw.engine.light.AmbientLight;
import yaw.engine.light.DirectionalLight;
import yaw.engine.mesh.Material;
import yaw.engine.mesh.Mesh;
import yaw.engine.mesh.strategy.DefaultDrawingStrategy;
import yaw.engine.resources.ObjLoader;

import java.io.IOException;

/**
 * Basic example of a cube rotating on y axis
 */
public class RotatingObj implements UpdateCallback {
	private int nbUpdates = 0;
	private double totalDeltaTime = 0.0;
	private static long deltaRefreshMillis = 1000;
	private long prevDeltaRefreshMillis = 0;
	private ItemObject cube ;
	private float speed = 0.1f;

	public RotatingObj(ItemObject cube) {
		this.cube = cube;
	}
	
	public ItemObject getItem() {
		return cube;
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

		float angle = 2.0f * 3.1415925f * (float) deltaTime * speed;
		//System.out.println(deltaTime);
		cube.rotateY(angle);
		//cube.rotateXYZAround(0f, 3.1415925f * speed * (float) deltaTime, 0f, new Vector3f(0f, 0f, -10f));
		//cube.rotateX(0.0f);


	}
	
	public static void main(String[] args) {

		World world = new World(0, 0, 800, 600);
		world.getSceneLight().setSun(new DirectionalLight(new Vector3f(1,1,1), 0.7f, new Vector3f(-1,-1,-1)));
		//world.getSceneLight().getSun().setDirection(-1f, 3f, 5f);
		world.getSceneLight().setAmbient(new AmbientLight(1.0f, 1.0f, 1.0f, 0.4f));

		Mesh axeX = MeshReferentiel.makeReferentiel();
		ItemObject ref = world.createItemObject("axeX", -1f, -1f, -1f, 10.0f, axeX);

		RotatingObj rRef = new RotatingObj(ref);
		world.registerUpdateCallback(rRef);

		ObjLoader objLoader = new ObjLoader();
		try {
			objLoader.parseFromResource("/resources/models/icosphere.obj");
		} catch (IOException e) {
			System.out.println("Errror : " + e.getMessage());
			System.exit(1);
		}
		Geometry geom = objLoader.getScene().getGeometryByIndex(0);
		Mesh objm = geom.buildMesh();

		objm.setDrawingStrategy(new DefaultDrawingStrategy());
		Material mat = new Material();
		mat.setColor(new Vector3f(0.1f , 0.7f, 0.9f));
		objm.setMaterial(mat);
		ItemObject obji = world.createItemObject("obj", 0f, 0f, 0f, 1.0f, objm);
		//obji.translate(2f,0f, -5f);

		world.getCamera().translate(0, 0,4);


		RotatingObj rObj = new RotatingObj(obji);

		world.registerUpdateCallback(rObj);

		world.launchSync();
	}

}
