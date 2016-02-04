package barinadroid.cabbageworm.engine.scene;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.animator.AlphaMenuAnimator;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseElasticOut;
import org.andengine.util.modifier.ease.EaseSineInOut;
import org.andengine.util.modifier.ease.EaseStrongOut;
import android.opengl.GLES20;
import barinadroid.cabbageworm.GameActivity;
import barinadroid.cabbageworm.engine.control.ResourceManager;
import barinadroid.cabbageworm.engine.control.TiledSpriteMenuItem;
import barinadroid.cabbageworm.engine.scene.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener
{
	protected MenuScene mMenuScene;
	protected static final int MENU_PLAY = 0;
	protected static final int MENU_RATE = 1;
	protected static final int MENU_QUIT = 2;

	@Override
	public void createScene()
	{
		SpriteBackground background = new SpriteBackground(new Sprite(0, SCREEN_HEIGHT - mResourceManager.mMenuBackgroundTextureRegion.getHeight(),
				mResourceManager.mMenuBackgroundTextureRegion, mVertexBufferObjectManager));
		setBackground(background);

		// Text nameText = new Text(0, 0, mResourceManager.mTitleFont, mActivity.getString(R.string.app_name), new TextOptions(HorizontalAlign.LEFT),
		// mVertexBufferObjectManager);
		// nameText.setPosition((SCREEN_WIDTH - nameText.getWidth()) * .5f, 75);
		// attachChild(nameText);

		mMenuScene = createMenuScene();

		/* Attach the menu. */
		this.setChildScene(mMenuScene, false, true, true);

		// if(!mResourceManager.mMusic.isPlaying())
		// {
		// mResourceManager.mMusic.play();
		// }

		ResourceManager.getInstance().playSfx(ResourceManager.MUSIC_BACKGROUND_START);
	}

	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY)
	{
		mResourceManager.playSfx(ResourceManager.SOUND_POP_2);
		switch (pMenuItem.getID())
		{
			case MENU_PLAY:
				startNewGame();
				return true;

			case MENU_RATE:
				// TODO implement
				return true;

			case MENU_QUIT:
				/* End Activity. */
				mActivity.finish();
				return true;

			default:
				return false;
		}
	}

	public void startNewGame()
	{
		if(mMenuScene != null)
			mMenuScene.closeMenuScene();
		mSceneManager.setScene(SceneType.SCENE_GAME);
	}

	protected MenuScene createMenuScene()
	{
		final MenuScene menuScene = new MenuScene(mCamera);
		menuScene.setMenuAnimator(new AlphaMenuAnimator());

		float logoX = (SCREEN_WIDTH - mResourceManager.mLogoTextureRegion.getWidth()) * .5f;
		float logoY = 75;// + mResourceManager.mLogoTextureRegion.getWidth() * .5f;
		Sprite logo = new Sprite(logoX, -mResourceManager.mLogoTextureRegion.getHeight(), mResourceManager.mLogoTextureRegion,
				mVertexBufferObjectManager);
		menuScene.attachChild(logo);
		EaseSineInOut sineEase = EaseSineInOut.getInstance();

		logo.registerEntityModifier(new SequenceEntityModifier(
				new ParallelEntityModifier(new MoveModifier(2, logoX, logoX, logo.getY(), logoY, sineEase), new RotationModifier(2, 0, -5, sineEase)),
				new ParallelEntityModifier(
						new LoopEntityModifier(new SequenceEntityModifier(new MoveYModifier(3.25f, logoY, logoY - 25, sineEase),
								new MoveYModifier(2.79f, logoY - 25, logoY, sineEase))),
						new LoopEntityModifier(new SequenceEntityModifier(new RotationModifier(2.5f, -5, 5, sineEase),
								new RotationModifier(2.95f, 5, -5, sineEase))))));

		TiledSpriteMenuItem playItem = new TiledSpriteMenuItem(MENU_PLAY, 0, 0, mResourceManager.mPlayButtonTiledTextureRegion,
				mVertexBufferObjectManager);

		final ScaleMenuItemDecorator playMenuItem = new ScaleMenuItemDecorator(playItem, 0.9f, 1);
		playMenuItem.setZIndex(10);
		playMenuItem.setX(GameActivity.CAMERA_WIDTH * .5f);
		playMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(playMenuItem);
		float playX = playMenuItem.getX();
		playMenuItem.registerEntityModifier(
				new LoopEntityModifier(new SequenceEntityModifier(new MoveXModifier(2, playX, playX + 20, EaseSineInOut.getInstance()),
						new MoveXModifier(1.9f, playX + 20, playX, EaseSineInOut.getInstance()))));

		final TiledSpriteMenuItem quitItem = new TiledSpriteMenuItem(MENU_QUIT, 0, 0, mResourceManager.mQuitButtonTiledTextureRegion,
				mVertexBufferObjectManager);

		final ScaleMenuItemDecorator quitMenuItem = new ScaleMenuItemDecorator(quitItem, 0.9f, 1);
		quitMenuItem.setZIndex(10);
		quitMenuItem.setX(GameActivity.CAMERA_WIDTH * .5f + 50);
		quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(quitMenuItem);
		float quitX = quitMenuItem.getX();
		quitMenuItem.registerEntityModifier(
				new LoopEntityModifier(new SequenceEntityModifier(new MoveXModifier(2.1f, quitX - 20, quitX, EaseSineInOut.getInstance()),
						new MoveXModifier(2.8f, quitX, quitX - 20, EaseSineInOut.getInstance()))));

		EaseElasticOut ease = EaseElasticOut.getInstance();

		Sprite grassSprite = new Sprite(0, 1080, mResourceManager.mMenuGrassBackgroundTextureRegion, mVertexBufferObjectManager);
		float toY = 1080 - mResourceManager.mMenuGrassBackgroundTextureRegion.getHeight() * .8f;
		menuScene.attachChild(grassSprite);
		grassSprite.registerEntityModifier(
				new SequenceEntityModifier(new DelayModifier(.5f), new MoveModifier(1.5f, 0, 0, 1080, toY, EaseStrongOut.getInstance())));

		final Sprite cabBig = new Sprite(-(mResourceManager.mCabbageBigTextureRegion.getWidth() * .25f), 1080 - 768,
				mResourceManager.mCabbageBigTextureRegion, mVertexBufferObjectManager);
		cabBig.setScaleCenter(cabBig.getWidth() * .5f, cabBig.getHeight() * .5f);
		cabBig.setScale(0);
		cabBig.setZIndex(1);

		final Sprite cabMed1 = new Sprite(260, 1080 - 400, mResourceManager.mCabbageMedium1TextureRegion, mVertexBufferObjectManager);
		cabMed1.setScaleCenter(cabMed1.getWidth() * .5f, cabMed1.getHeight() * .5f);
		cabMed1.setScale(0);
		cabMed1.setZIndex(2);

		final Sprite cabMed2 = new Sprite(mResourceManager.mCabbageMedium2TextureRegion.getWidth(), 1080 - 256,
				mResourceManager.mCabbageMedium2TextureRegion, mVertexBufferObjectManager);
		cabMed2.setScaleCenter(cabMed2.getWidth() * .5f, cabMed2.getHeight() * .5f);
		cabMed2.setScale(0);
		cabMed2.setZIndex(3);

		cabBig.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(.75f, new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				ResourceManager.getInstance().playSfx(ResourceManager.SOUND_CABBAGE_GROWTH_A);
			}
		}), new ParallelEntityModifier(new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				EaseSineInOut ease = EaseSineInOut.getInstance();
				cabBig.registerEntityModifier(new LoopEntityModifier(
						new SequenceEntityModifier(new RotationModifier(75, 12, 45, ease), new RotationModifier(75, 45, 12, ease))));
			}
		}, new ScaleModifier(1.5f, 0, 1, ease), new RotationModifier(1.5f, -50, 12, ease))));
		cabMed1.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(1.0f, new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				ResourceManager.getInstance().playSfx(ResourceManager.SOUND_CABBAGE_GROWTH_B);
			}
		}), new ParallelEntityModifier(new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				EaseSineInOut ease = EaseSineInOut.getInstance();
				cabMed1.registerEntityModifier(new LoopEntityModifier(
						new SequenceEntityModifier(new RotationModifier(82, 30, -20, ease), new RotationModifier(82, -20, 30, ease))));
			}
		}, new ScaleModifier(1.5f, 0, 1, ease), new RotationModifier(1.5f, -45, 30, ease))));
		cabMed2.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(1.2f, new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				ResourceManager.getInstance().playSfx(ResourceManager.SOUND_CABBAGE_GROWTH_C);
			}
		}), new ParallelEntityModifier(new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				EaseSineInOut ease = EaseSineInOut.getInstance();
				cabMed2.registerEntityModifier(new LoopEntityModifier(
						new SequenceEntityModifier(new RotationModifier(60, 0, 36, ease), new RotationModifier(60, 36, 0, ease))));
			}
		}, new ScaleModifier(1.5f, 0, 1, ease), new RotationModifier(1.5f, 50, 0, ease))));

		menuScene.attachChild(cabBig);
		menuScene.attachChild(cabMed1);
		menuScene.attachChild(cabMed2);

		Sprite worm = new Sprite(GameActivity.CAMERA_WIDTH, 512, mResourceManager.mBigWormTextureRegion, mVertexBufferObjectManager);
		worm.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(1.7f),
				new MoveModifier(1, GameActivity.CAMERA_WIDTH, GameActivity.CAMERA_WIDTH - worm.getWidth() + 20,
						GameActivity.CAMERA_HEIGHT - worm.getHeight(), GameActivity.CAMERA_HEIGHT - worm.getHeight() - 50,
						EaseStrongOut.getInstance())));
		menuScene.attachChild(worm);

		menuScene.buildAnimations();
		menuScene.setBackgroundEnabled(false);
		mActivity.getEngine().registerUpdateHandler(new TimerHandler(.25f, new ITimerCallback()
		{
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				mActivity.getEngine().unregisterUpdateHandler(pTimerHandler);
				menuScene.setOnMenuItemClickListener(MainMenuScene.this);
			}
		}));
		return menuScene;
	}

	protected MenuScene createSubMenuScene()
	{
		// TODO implement
		return null;
	}

	@Override
	public void onBackKeyPressed()
	{
		ResourceManager.getInstance().stopSfx(ResourceManager.ALL_SFX);
		mActivity.finish();
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene()
	{
		// TODO
	}
}