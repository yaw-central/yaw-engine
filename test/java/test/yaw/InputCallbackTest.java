package test.yaw;

import yaw.engine.World;
import yaw.engine.camera.Camera;
import yaw.engine.items.ItemObject;
import yaw.engine.mesh.MeshBuilder;
import yaw.engine.mesh.Texture;
import yaw.engine.InputCallback;

import static org.lwjgl.glfw.GLFW.*;

public class InputCallbackTest implements InputCallback {
    private int key;
    private int scancode;
    private int action;
    private int mods;
    private Camera camera;



    public InputCallbackTest(Camera camera){
        this.camera = camera;
    }




    public void sendKey(int key, int scancode, int action, int mods) {


        this.key=key;
        this.scancode=scancode;
        this.action=action;
        this.mods=mods;

        if(key == GLFW_KEY_UP){
            camera.translate(0,0,0.1f);
        } else if (key == GLFW_KEY_LEFT) {
            camera.rotateXYZ(1f, 0, 0);
        }



    }




    public static void main(String[] args){
        World world = new World(0, 0, 800, 600);
        InputCallbackTest key = new InputCallbackTest(world.getCamera());
        world.registerInputCallback(key);
        ItemObject cube = world.createItemObject("cube", 0f, 0f, -2f, 1.0f, MeshBuilder.generateBlock(1, 1, 1));
        cube.getMesh().getMaterial().setTexture(new Texture("/resources/diamond.png"));
        world.getCamera().setPosition(0,0,3);

        world.launchSync();
    }


}




