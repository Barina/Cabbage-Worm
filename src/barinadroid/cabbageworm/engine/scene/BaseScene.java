package barinadroid.cabbageworm.engine.scene;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import barinadroid.cabbageworm.GameActivity;
import barinadroid.cabbageworm.engine.control.ResourceManager;
import barinadroid.cabbageworm.engine.scene.SceneManager.SceneType;

public abstract class BaseScene extends Scene
{
	protected final int SCREEN_WIDTH = GameActivity.CAMERA_WIDTH;
	protected final int SCREEN_HEIGHT = GameActivity.CAMERA_HEIGHT;

	public final GameActivity mActivity;
	public final Engine mEngine;
	public final Camera mCamera;
	public final VertexBufferObjectManager mVertexBufferObjectManager;
	public final ResourceManager mResourceManager;
	public final SceneManager mSceneManager;

	public BaseScene()
	{
		mResourceManager = ResourceManager.getInstance();
		mActivity = mResourceManager.mActivity;
		mVertexBufferObjectManager = mActivity.getVertexBufferObjectManager();
		mEngine = mActivity.getEngine();
		mCamera = mEngine.getCamera();
		mSceneManager = SceneManager.getInstance();
		createScene();
	}

	public abstract void createScene();

	public abstract void onBackKeyPressed();

	public abstract SceneType getSceneType();

	public abstract void disposeScene();
}