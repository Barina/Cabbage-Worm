package barinadroid.cabbageworm.engine.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;
import org.andengine.util.HorizontalAlign;
import barinadroid.cabbageworm.GameActivity;
import barinadroid.cabbageworm.R;
import barinadroid.cabbageworm.engine.scene.SceneManager.SceneType;

public class SplashScene extends BaseScene implements IOnSceneTouchListener
{
	TimerHandler timerHandler;

	@Override
	public void createScene()
	{
		Sprite splash = new Sprite(0, 0, mResourceManager.mSplashTextureRegion, mVertexBufferObjectManager)
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		attachChild(splash);

		float bgX = 100;
		float bgY = 100;
		float bgW = GameActivity.CAMERA_WIDTH - bgX * 2;
		float bgH = GameActivity.CAMERA_HEIGHT - bgY * 2;

		Rectangle background = new Rectangle(bgX, bgY, bgW, bgH, mVertexBufferObjectManager);
		background.setColor(0, 0, 0, .5f);
		background.setZIndex(0);
		attachChild(background);

		Text privacyPolicyText = new Text(0, 150, mResourceManager.mSplashFont, mActivity.getText(R.string.privacy_policy),
				mVertexBufferObjectManager);
		privacyPolicyText.setTextOptions(new TextOptions(HorizontalAlign.CENTER));
		privacyPolicyText.setX((GameActivity.CAMERA_WIDTH - privacyPolicyText.getWidth()) * .5f);
		attachChild(privacyPolicyText);

		Text copyrightText = new Text(0, 0, mResourceManager.mSplashFont, "Cabbage Worm v" + mActivity.getAppName() + " Barina (c) 2011-2015",
				new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
		copyrightText.setPosition(SCREEN_WIDTH - copyrightText.getWidth() - 5, SCREEN_HEIGHT - copyrightText.getHeight() - 5);
		attachChild(copyrightText);

		timerHandler = new TimerHandler(4f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				closeSceneAndStartMenuScene();
			}
		});

		mEngine.registerUpdateHandler(timerHandler);
		mEngine.registerUpdateHandler(new TimerHandler(.25f, new ITimerCallback()
		{
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				setOnSceneTouchListener(SplashScene.this);
			}
		}));
	}

	private void closeSceneAndStartMenuScene()
	{
		mEngine.unregisterUpdateHandler(timerHandler);
		mSceneManager.setScene(SceneType.SCENE_MENU);
		mResourceManager.unloadSplashResources();
	}

	@Override
	public void onBackKeyPressed()
	{
		mActivity.finish();
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene()
	{}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{
		closeSceneAndStartMenuScene();
		return true;
	}
}