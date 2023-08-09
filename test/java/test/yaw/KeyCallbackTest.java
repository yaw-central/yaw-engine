package test.yaw;

import yaw.engine.SceneRenderer;
import yaw.engine.World;
import yaw.engine.camera.Camera;
import yaw.engine.items.ItemObject;
import yaw.engine.light.LightModel;
import yaw.engine.mesh.Texture;
import yaw.engine.InputCallback;
import yaw.engine.mesh.builder.Cuboid;

import static org.lwjgl.glfw.GLFW.*;

public class KeyCallbackTest implements InputCallback {
    private int key;
    private int scancode;
    private int action;
    private int mods;
    private Camera camera;



    public KeyCallbackTest(Camera camera){
        this.camera = camera;
    }


    public void sendKey(int key, int scancode, int action, int mods) {
        this.key=key;
        this.scancode=scancode;
        this.action=action;
        this.mods=mods;

        switch(key) {
            case GLFW_KEY_UP:
                camera.translate(0, -0.1f, 0);
                break;
            case GLFW_KEY_DOWN:
                camera.translate(0, 0.1f, 0);
                break;

            case GLFW_KEY_LEFT:
                camera.translate(0.1f, 0, 0);
                break;

            case GLFW_KEY_RIGHT:
                camera.translate(-0.1f, 0, 0);
                break;
        }




    }




    public static void main(String[] args){
        World world = new World(0, 0, 800, 600);
        world.installScene(new SceneRenderer(new LightModel()));
        KeyCallbackTest key = new KeyCallbackTest(world.getCamera());
        world.registerInputCallback(key);
        ItemObject cube = world.createItemObject("cube", 0f, 0f, -2f, 1.0f, new Cuboid(1).generate());
        cube.getMesh().getMaterial().setTexture(new Texture("/resources/diamond.png"));
        world.getCamera().setPosition(0,0,3);

        world.launchSync();
    }


}




