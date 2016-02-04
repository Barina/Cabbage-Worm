package barinadroid.cabbageworm.engine.scene;

import barinadroid.cabbageworm.engine.control.ResourceManager;

public class SceneManager
{

	private static final SceneManager INSTANCE = new SceneManager();

	public enum SceneType
	{
		SCENE_SPLASH, SCENE_MENU, SCENE_GAME
	}

	private BaseScene mSplashScene;
	private BaseScene mMenuScene;
	private BaseScene mGameScene;

	private SceneType mCurrentSceneType;
	private BaseScene mCurrentScene;

	private SceneManager()
	{}

	public static SceneManager getInstance()
	{
		return INSTANCE;
	}

	public void setScene(SceneType sceneType)
	{
		switch (sceneType)
		{
			case SCENE_MENU:
				setScene(createMenuScene());
				break;
			case SCENE_GAME:
				setScene(createGameScene());
				break;
			case SCENE_SPLASH:
				setScene(createSplashScene());
				break;
		}
	}

	private void setScene(BaseScene scene)
	{
		ResourceManager.getInstance().mActivity.getEngine().setScene(scene);
		mCurrentScene = scene;
		mCurrentSceneType = scene.getSceneType();
	}

	public SceneType getCurrentSceneType()
	{
		return mCurrentSceneType;
	}

	public BaseScene getCurrentScene()
	{
		return mCurrentScene;
	}

	public BaseScene createSplashScene()
	{
		this.mSplashScene = new SplashScene();
		return mSplashScene;
	}

	private BaseScene createMenuScene()
	{
		this.mMenuScene = new MainMenuScene();
		return mMenuScene;
	}

	private BaseScene createGameScene()
	{
		this.mGameScene = new GameScene();
		return mGameScene;
	}
}
