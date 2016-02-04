package barinadroid.cabbageworm;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.Toast;
import barinadroid.cabbageworm.engine.control.ResourceManager;
import barinadroid.cabbageworm.engine.scene.GameScene;
import barinadroid.cabbageworm.engine.scene.MainMenuScene;
import barinadroid.cabbageworm.engine.scene.SceneManager;
import barinadroid.cabbageworm.engine.scene.SceneManager.SceneType;
import barinadroid.cabbageworm.tools.CommonUtils;

public class GameActivity extends SimpleBaseGameActivity
{
	public static final int CAMERA_WIDTH = 1920;
	public static final int CAMERA_HEIGHT = 1080;
	private static final String MAX_SCORE_KEY = "maxScore";
	private Camera mCamera;
	private ResourceManager mResourceManager;
	private SceneManager mSceneManager;

	@Override
	public EngineOptions onCreateEngineOptions()
	{
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		engineOptions.getAudioOptions().setNeedsSound(true).setNeedsMusic(true);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);

		if(MultiTouch.isSupported(this))
		{
			if(!MultiTouch.isSupportedDistinct(this))
				Toast.makeText(this,
						"MultiTouch detected, but your device has problems distinguishing between fingers.\n\nControls are placed at different vertical locations.",
						Toast.LENGTH_LONG).show();
		}
		else
			Toast.makeText(this,
					"Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)\n\nControls are placed at different vertical locations.",
					Toast.LENGTH_LONG).show();

		return engineOptions;
	}

	@Override
	protected void onCreateResources()
	{
		mResourceManager = ResourceManager.getInstance();
		mResourceManager.prepare(this);
		mResourceManager.loadSplashResources();
		mSceneManager = SceneManager.getInstance();
	}

	@Override
	protected Scene onCreateScene()
	{
		mEngine.registerUpdateHandler(new TimerHandler(.25f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				mResourceManager.loadGameResources();
			}
		}));
		return mSceneManager.createSplashScene();
	}

	public int getMaxScore()
	{
		return getPreferences(Context.MODE_PRIVATE).getInt(MAX_SCORE_KEY, 0);
	}

	public void setMaxScore(int maxScore)
	{
		getPreferences(Context.MODE_PRIVATE).edit().putInt(MAX_SCORE_KEY, maxScore).commit();
	}

	public String getAppName()
	{
		return CommonUtils.getApplicationName(this);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if(mSceneManager == null)
			return super.onKeyUp(keyCode, event);

		if(mSceneManager.getCurrentSceneType() == SceneType.SCENE_GAME && !mSceneManager.getCurrentScene().hasChildScene())
		{
			GameScene scene = (GameScene)mSceneManager.getCurrentScene();

			switch (keyCode)
			{
				case KeyEvent.KEYCODE_A:
				case KeyEvent.KEYCODE_DPAD_LEFT:
					scene.moveWormLeft(false);
					return true;
				case KeyEvent.KEYCODE_D:
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					scene.moveWormRight(false);
					return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(mSceneManager == null)
			return super.onKeyDown(keyCode, event);

		if(mSceneManager.getCurrentSceneType() == SceneType.SCENE_GAME)
		{
			GameScene scene = (GameScene)mSceneManager.getCurrentScene();
			if(!mSceneManager.getCurrentScene().hasChildScene())
			{
				switch (keyCode)
				{
					case KeyEvent.KEYCODE_A:
					case KeyEvent.KEYCODE_DPAD_LEFT:
						scene.moveWormLeft(true);
						return true;
					case KeyEvent.KEYCODE_D:
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						scene.moveWormRight(true);
						return true;
				}
			}
			else
				if(keyCode == KeyEvent.KEYCODE_ENTER)
				{
					scene.performEnterKey();
					return true;
				}
		}
		else
			if(mSceneManager.getCurrentSceneType() == SceneType.SCENE_MENU && keyCode == KeyEvent.KEYCODE_ENTER)
			{
				MainMenuScene scene = (MainMenuScene)mSceneManager.getCurrentScene();
				scene.startNewGame();
				return true;
			}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed()
	{
		if(mSceneManager != null && mSceneManager.getCurrentScene() != null)
		{
			mSceneManager.getCurrentScene().onBackKeyPressed();
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected synchronized void onResume()
	{
		if(mSceneManager != null && mSceneManager.getCurrentSceneType() == SceneType.SCENE_GAME)
		{
			GameScene scene = (GameScene)mSceneManager.getCurrentScene();
			if(!scene.isPaused())
			{
				if(mResourceManager != null)
					mResourceManager.resume();
			}
		}
		else
			if(mResourceManager != null)
				mResourceManager.resume();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		if(mSceneManager != null && mSceneManager.getCurrentSceneType() == SceneType.SCENE_GAME)
		{
			GameScene scene = (GameScene)mSceneManager.getCurrentScene();
			if(!scene.hasChildScene())
				scene.pauseGame();
		}
		if(mResourceManager != null)
			mResourceManager.pause();
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mResourceManager.unloadSplashResources();
		mResourceManager.unloadGameResources();
		System.exit(0);
	}
}