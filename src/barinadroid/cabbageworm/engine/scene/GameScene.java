package barinadroid.cabbageworm.engine.scene;

import java.util.ArrayList;
import java.util.Random;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ColorModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier.ILoopEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.LoopModifier;
import org.andengine.util.modifier.ease.EaseCircularInOut;
import org.andengine.util.modifier.ease.EaseCircularOut;
import org.andengine.util.modifier.ease.EaseCubicOut;
import org.andengine.util.modifier.ease.EaseElasticOut;
import org.andengine.util.modifier.ease.EaseExponentialOut;
import org.andengine.util.modifier.ease.EaseQuadInOut;
import org.andengine.util.modifier.ease.EaseStrongIn;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import android.widget.Toast;
import barinadroid.cabbageworm.GameActivity;
import barinadroid.cabbageworm.engine.control.ResourceManager;
import barinadroid.cabbageworm.engine.model.Cabbage;
import barinadroid.cabbageworm.engine.model.PowerUpX2;
import barinadroid.cabbageworm.engine.model.Rock;
import barinadroid.cabbageworm.engine.model.Worm;
import barinadroid.cabbageworm.engine.scene.SceneManager.SceneType;

public class GameScene extends BaseScene implements IOnSceneTouchListener
{
	private final Random random = new Random((long)(31 + 4631 * Math.random()));

	public static final int SIDES_GAP = 325;
	public static final int SAFE_GAP = 50;
	private SpriteBackground mBackground;
	private HUD gameHUD;
	private AnimatedSprite doublePointsSprite;
	private Text mHudText;
	private Text mPointScoreText;
	private ArrayList<Integer> mScore;
	private int mScoreOverall;
	private int mMost;
	private boolean mIsGamePaused;
	private boolean mGameOver;
	private boolean mCheckBounds;
	private boolean isDoublePoints;
	private CameraScene mGameReadyScene;
	private CameraScene mGameOverScene;
	private CameraScene mGamePauseScene;
	private PhysicsWorld mPhysicsWorld;
	private Worm mWorm;
	private Cabbage mCabbage;
	private Rock mRock;
	private PowerUpX2 mPowerUp;
	private int leftHalfScreenPointerId, rightHalfScreenPointerId;
	private TimerHandler mPowerupDespawner;

	@Override
	public void createScene()
	{
		mGameOver = true;
		mEngine.registerUpdateHandler(new FPSLogger());

		setOnSceneTouchListener(this);
		mPowerupDespawner = new TimerHandler(3, new ITimerCallback()
		{
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				unregisterUpdateHandler(pTimerHandler);
				mPowerUp.despawn(false);
			}
		});
		mPowerupDespawner.setAutoReset(true);

		// TODO create entities
		mBackground = new SpriteBackground(new Sprite(0, SCREEN_HEIGHT - mResourceManager.mBackgroundTextureRegion.getHeight(),
				mResourceManager.mBackgroundTextureRegion, mVertexBufferObjectManager));
		setBackground(mBackground);

		mMost = mActivity.getMaxScore();

		// create HUD for score
		gameHUD = new HUD();
		mScore = new ArrayList<Integer>();
		// CREATE SCORE TEXT
		mHudText = new Text(SCREEN_WIDTH * .5f, 50, mResourceManager.mBigFont, "0123456789", new TextOptions(HorizontalAlign.LEFT),
				mVertexBufferObjectManager);
		mHudText.setText("0");
		mHudText.setX((SCREEN_WIDTH - mHudText.getWidth()) / 2);
		// mHudText.setVisible(false);

		mPointScoreText = new Text(0, 0, mResourceManager.mBigFont, "0123", mVertexBufferObjectManager);
		mPointScoreText.setText("0");
		mPointScoreText.setVisible(false);
		mPointScoreText.setScale(.5f);

		doublePointsSprite = new AnimatedSprite(0, mHudText.getY(), mResourceManager.mDoubleTiledTextureRegion, mVertexBufferObjectManager);
		doublePointsSprite.setX(-doublePointsSprite.getWidth() + GameActivity.CAMERA_WIDTH - SIDES_GAP - SAFE_GAP);
		doublePointsSprite.setVisible(false);

		gameHUD.attachChild(mHudText);
		gameHUD.attachChild(mPointScoreText);
		gameHUD.attachChild(doublePointsSprite);
		// mCamera.setHUD(gameHUD);

		// TODO create PhysicsWorld
		// mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		mPhysicsWorld.setContactListener(createContactListener());
		// attachChild(new DebugRenderer(mPhysicsWorld, mVertexBufferObjectManager));// TODO for debug

		// TODO create body and fixture
		mWorm = new Worm(this, (int)(GameActivity.CAMERA_WIDTH * .5f), GameActivity.CAMERA_HEIGHT + 50);
		mWorm.setRotation(-90);

		mCabbage = new Cabbage(this);
		mRock = new Rock(this);
		mPowerUp = new PowerUpX2(this);

		/* The actual collision-checking. */
		registerUpdateHandler(new IUpdateHandler()
		{
			@Override
			public void reset()
			{}

			@Override
			public void onUpdate(float pSecondsElapsed)
			{
				if(mCheckBounds && !mGameOver)
				{
					Vector2 center = mWorm.getCenter();
					if(center.x < SIDES_GAP || center.y < 0 || center.x > GameActivity.CAMERA_WIDTH - SIDES_GAP
							|| center.y > GameActivity.CAMERA_HEIGHT)
					{
						gameOver();
						return;
					}
				}

				mPhysicsWorld.onUpdate(pSecondsElapsed);

				sortChildren();
			}
		});

		registerEntityModifier(new LoopEntityModifier(new DelayModifier(1.0f, new IEntityModifierListener()
		{
			// boolean spawnCabbage = true;
			private boolean lastSpawnWasNotCabbage = true;

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				if(mCheckBounds && !mGameOver && !mCabbage.isAvailable() && !mRock.isAvailable() && !mPowerUp.isAvailable())
				{
					if(lastSpawnWasNotCabbage)
					{
						// spawn cabbage immediately.
						mCabbage.respawn(.5f + random.nextFloat());
						lastSpawnWasNotCabbage = false;
					}
					else
					{
						float chance = random.nextFloat();
						lastSpawnWasNotCabbage = true;
						if(chance <= .7)
						{
							// 70% spawn cabbage
							lastSpawnWasNotCabbage = false;
							mCabbage.respawn(.5f + random.nextFloat());
						}
						else
							if(isDoublePoints || chance <= .9)
							{
								// 20% spawn rock
								mRock.respawn(1);
								registerUpdateHandler(new TimerHandler(4, new ITimerCallback()
								{
									@Override
									public void onTimePassed(TimerHandler pTimerHandler)
									{
										unregisterUpdateHandler(pTimerHandler);
										mRock.despawn(false);
									}
								}));
							}
							else
							{
								// 10% spawn dp
								mPowerUp.respawn(1);
								mPowerupDespawner.reset();
								registerUpdateHandler(mPowerupDespawner);
							}
					}

					// if(spawnCabbage || random.nextFloat() > .4f)
					// {
					// spawnCabbage = false;
					// mCabbage.respawn(.5f + random.nextFloat());
					// }
					// else
					// {
					// if(isDoublePoints || random.nextFloat() > .4f)
					// {
					// mRock.respawn(1);
					// registerUpdateHandler(new TimerHandler(4, new ITimerCallback()
					// {
					// @Override
					// public void onTimePassed(TimerHandler pTimerHandler)
					// {
					// unregisterUpdateHandler(pTimerHandler);
					// mRock.despawn(false);
					// }
					// }));
					// }
					// else
					// {
					// mPowerUp.respawn(1);
					// mPowerupDespawner.reset();
					// registerUpdateHandler(mPowerupDespawner);
					// }
					// }
				}
			}
		})));

		createGetReadyScene();
		setChildScene(mGameReadyScene, false, true, true);

		createGameOverScene();
		createGamePauseScene();

		createPlayCounter();
		// ResourceManager.getInstance().stopSfx(ResourceManager.ALL_SOUNDS);
	}

	private void createPlayCounter()
	{
		final Text countDownText = new Text(0, 0, ResourceManager.getInstance().mBigFont, "GO!", mVertexBufferObjectManager);
		countDownText.setAlpha(0);
		attachChild(countDownText);
		EaseCircularOut ease = EaseCircularOut.getInstance();
		LoopEntityModifier mod = new LoopEntityModifier(new ScaleModifier(1, 1, 2, new IEntityModifierListener()
		{
			int countDown = 3;

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{
				if(countDown == 0)
					countDownText.setText("GO!");
				else
					countDownText.setText("" + countDown);
				float x = (GameActivity.CAMERA_WIDTH - countDownText.getWidth()) * .5f;
				float y = (GameActivity.CAMERA_HEIGHT - countDownText.getHeight()) * .5f - 250;
				countDownText.setPosition(x, y);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				--countDown;
			}
		}, ease), 4, new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{
				countDownText.setAlpha(1);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				mActivity.runOnUpdateThread(new Runnable()
				{
					@Override
					public void run()
					{
						detachChild(countDownText);
						countDownText.dispose();
					}
				});
				startGame();
			}
		});
		countDownText.registerEntityModifier(mod);
	}

	private void createGetReadyScene()
	{
		// TODO GameReadyScene
		// TODO building top help window
		final float safeGap = 100;
		final float windowX = (SCREEN_WIDTH - mResourceManager.mWindowTextureRegion.getWidth()) / 2;
		final float windowY = 150;
		final float windowCenterX = windowX + mResourceManager.mWindowTextureRegion.getWidth() * .5f;
		final float windowCenterY = windowY + mResourceManager.mWindowTextureRegion.getHeight() * .5f;

		final float handRightX = windowCenterX + 150;
		final float handRightY = windowCenterY;

		final float handLeftX = windowX + safeGap;
		final float handLeftY = handRightY;

		final float wormNatX = windowCenterX - mResourceManager.mHelpWormNaturalTextureRegion.getWidth() * .5f;
		final float wormNatY = windowY + mResourceManager.mWindowTextureRegion.getHeight()
				- mResourceManager.mHelpWormNaturalTextureRegion.getHeight() - safeGap;

		final float wormLeftX = wormNatX - mResourceManager.mHelpWormLeftTextureRegion.getWidth()
				+ mResourceManager.mHelpWormNaturalTextureRegion.getWidth();
		final float wormLeftY = wormNatY + mResourceManager.mHelpWormNaturalTextureRegion.getHeight()
				- mResourceManager.mHelpWormLeftTextureRegion.getHeight();
		final float wormLeftWidth = mResourceManager.mHelpWormLeftTextureRegion.getWidth();

		final float wormRightX = wormLeftX + wormLeftWidth * 2 - mResourceManager.mHelpWormNaturalTextureRegion.getWidth();
		final float wormRightWidth = -wormLeftWidth;

		Text descText = new Text(0, windowY - safeGap, mResourceManager.mBigFont, "Tap on screen sides to move the worm", mVertexBufferObjectManager);
		float descX = windowCenterX - descText.getWidth() * .5f;
		descText.setX(descX);

		final Sprite windowsSprite = new Sprite(windowX, windowY, mResourceManager.mWindowTextureRegion, mVertexBufferObjectManager);
		windowsSprite.setZIndex(0);
		final Sprite helpWormNatSprite = new Sprite(wormNatX, wormNatY, mResourceManager.mHelpWormNaturalTextureRegion, mVertexBufferObjectManager);
		helpWormNatSprite.setZIndex(1);
		final Sprite helpWormSprite = new Sprite(wormLeftX, wormLeftY, mResourceManager.mHelpWormLeftTextureRegion, mVertexBufferObjectManager);
		helpWormSprite.setZIndex(1);
		final TiledSprite handSprite = new TiledSprite(handRightX, handRightY, mResourceManager.mHandTiledTextureRegion, mVertexBufferObjectManager);
		handSprite.setCurrentTileIndex(1);
		handSprite.setZIndex(2);

		EaseCircularOut ease = EaseCircularOut.getInstance();

		MoveModifier moveLeft = new MoveModifier(.750f, handRightX, handLeftX, handRightY, handLeftY, new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{
				handSprite.setCurrentTileIndex(1);
				helpWormNatSprite.setVisible(true);
				helpWormSprite.setVisible(false);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				helpWormSprite.setWidth(wormLeftWidth);
				helpWormSprite.setX(wormLeftX);
			}
		}, ease);

		MoveModifier moveRight = new MoveModifier(.750f, handLeftX, handRightX, handLeftY, handRightY, new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{
				handSprite.setCurrentTileIndex(1);
				helpWormNatSprite.setVisible(true);
				helpWormSprite.setVisible(false);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				helpWormSprite.setWidth(wormRightWidth);
				helpWormSprite.setX(wormRightX);
			}
		}, ease);

		final SequenceEntityModifier clickOnLeft = getClickSequenceEntityModifier(helpWormNatSprite, helpWormSprite, handSprite);
		final SequenceEntityModifier clickOnRight = getClickSequenceEntityModifier(helpWormNatSprite, helpWormSprite, handSprite);

		handSprite.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(moveLeft, clickOnLeft, moveRight, clickOnRight)));

		// TODO building bottom help window
		final float helpWindowX = windowX - safeGap;
		final float helpWindowY = windowY + windowsSprite.getHeight();
		final float helpWindowW = safeGap + windowsSprite.getWidth() + safeGap;
		final float helpWindowH = GameActivity.CAMERA_HEIGHT - windowsSprite.getHeight() - safeGap * 2;

		Rectangle helpWindow = new Rectangle(helpWindowX, helpWindowY, helpWindowW, helpWindowH, mVertexBufferObjectManager);
		helpWindow.setColor(0, 0, 0, .25f);

		final AnimatedSprite cabbage = new AnimatedSprite(safeGap, 0, mResourceManager.mCabbageAnimTiledTextureRegion, mVertexBufferObjectManager);
		cabbage.setScale(.9f);
		cabbage.setY((helpWindowH - cabbage.getHeightScaled()) * .5f);
		long[] frames = new long[cabbage.getTileCount()];
		for(int i = 0 ; i < frames.length ; i++)
			frames[i] = Cabbage.FPS;
		frames[cabbage.getTileCount() - 1] = Cabbage.FPS + 1000;
		cabbage.animate(frames);

		final Sprite rock = new Sprite(0, 0, mResourceManager.mRockTextureRegion, mVertexBufferObjectManager);
		rock.setScale(.75f);
		rock.setX((helpWindowW - rock.getWidthScaled()) * .5f);
		rock.setY((helpWindowH - rock.getHeightScaled()) * .5f);
		final AnimatedSprite x2Points = new AnimatedSprite(0, 0, mResourceManager.mPowerupTiledTextureRegion, mVertexBufferObjectManager);
		x2Points.setX(-safeGap + helpWindowW - x2Points.getWidth());
		x2Points.setY((helpWindowH - x2Points.getHeightScaled()) * .5f);
		x2Points.animate(PowerUpX2.FPS);

		final Text helpTitle = new Text(0, 0, mResourceManager.mMenuFont, "Stay Within Bounds, And:", mVertexBufferObjectManager);
		helpTitle.setX((helpWindowW - helpTitle.getWidth()) * .5f);
		helpTitle.setY(helpTitle.getHeight() * .5f);
		float textY = helpWindowH - safeGap;
		final Text eatText = new Text(0, textY, mResourceManager.mDetailsFont, "EAT", mVertexBufferObjectManager);
		eatText.setX(cabbage.getX() + cabbage.getScaleCenterX() - eatText.getWidth() * .5f);
		final Text avoidText = new Text(0, textY, mResourceManager.mDetailsFont, "AVOID", mVertexBufferObjectManager);
		avoidText.setX(rock.getX() + rock.getScaleCenterX() - avoidText.getWidth() * .5f);
		final Text takeText = new Text(0, textY, mResourceManager.mDetailsFont, "TAKE", mVertexBufferObjectManager);
		takeText.setX(x2Points.getX() + x2Points.getScaleCenterX() - takeText.getWidth() * .5f);

		helpWindow.attachChild(cabbage);
		helpWindow.attachChild(rock);
		helpWindow.attachChild(x2Points);
		helpWindow.attachChild(helpTitle);
		helpWindow.attachChild(eatText);
		helpWindow.attachChild(avoidText);
		helpWindow.attachChild(takeText);

		// building the scene itself
		mGameReadyScene = new CameraScene(mCamera);
		mGameReadyScene.attachChild(windowsSprite);
		mGameReadyScene.attachChild(helpWormNatSprite);
		mGameReadyScene.attachChild(helpWormSprite);
		mGameReadyScene.attachChild(handSprite);
		mGameReadyScene.attachChild(descText);

		mGameReadyScene.attachChild(helpWindow);

		mGameReadyScene.setBackgroundEnabled(false);
		mGameReadyScene.registerEntityModifier(new DelayModifier(.25f, new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				mGameReadyScene.setOnSceneTouchListener(new IOnSceneTouchListener()
				{
					@Override
					public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
					{
						if(pSceneTouchEvent.isActionUp())
						{
							clearChildScene();
							ResourceManager.getInstance().stopSfx(ResourceManager.ALL_MUSICS);
						}
						return true;
					}
				});
			}
		}));
	}

	private void createGameOverScene()
	{
		// TODO GameOverScene
		mGameOverScene = new CameraScene(mCamera);
		mGameOverScene.setBackgroundEnabled(false);

		final Text gameOverText = new Text(0, 0, mResourceManager.mBigFont, "GAME OVER!", mVertexBufferObjectManager);
		final float goTextX = (GameActivity.CAMERA_WIDTH - gameOverText.getWidth()) * .5f;
		final float goTextY = gameOverText.getHeight() + 20;
		gameOverText.setPosition(goTextX, (GameActivity.CAMERA_HEIGHT - gameOverText.getHeight()) * .5f);
		gameOverText.setZIndex(50);
		gameOverText.setScale(0);

		// general values
		final float safeGap = 50;
		final float yGap = 10;

		// window
		final float windowW = 1024;
		final float windowH = 445;
		final float windowX = (GameActivity.CAMERA_WIDTH - windowW) * .5f;
		final float windowY = goTextY + gameOverText.getHeight() + safeGap;

		final Rectangle window = new Rectangle(windowX, windowY, windowW, windowH, mVertexBufferObjectManager);
		window.setColor(Color.BLACK);
		window.setAlpha(0);
		window.setZIndex(0);

		// cabbageEatenTitle
		final float eatenX = safeGap;
		final float eatenY = safeGap;

		final Text eatenTitleText = new Text(eatenX, eatenY, mResourceManager.mDetailsFont, "Cabbages Eaten:", new TextOptions(HorizontalAlign.LEFT),
				mVertexBufferObjectManager);
		eatenTitleText.setAlpha(0);

		// cabbageScoreTitle
		final float cabbageScoreX = safeGap;
		final float cabbageScoreY = eatenY + yGap + eatenTitleText.getHeight();

		final Text cabbageScoreTitleText = new Text(cabbageScoreX, cabbageScoreY, mResourceManager.mDetailsFont, "Cabbages Score:",
				new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
		cabbageScoreTitleText.setAlpha(0);

		// wormLengthTitle
		final float lengthX = safeGap;
		final float lengthY = cabbageScoreY + yGap + cabbageScoreTitleText.getHeight();

		final Text lengthTitleText = new Text(lengthX, lengthY, mResourceManager.mDetailsFont, "Worm Length:", new TextOptions(HorizontalAlign.LEFT),
				mVertexBufferObjectManager);
		lengthTitleText.setAlpha(0);

		// scoreTitle
		final float scoreX = safeGap;
		final float scoreY = lengthY + yGap * 2 + lengthTitleText.getHeight();

		final Text scoreTitleText = new Text(scoreX, scoreY, mResourceManager.mDetailsFont, "Final Score:", new TextOptions(HorizontalAlign.LEFT),
				mVertexBufferObjectManager);
		scoreTitleText.setAlpha(0);

		// final TiledSprite labelSprite = new TiledSprite(labelX, labelY, mResourceManager.mStateTextureRegion, mVertexBufferObjectManager);
		// labelSprite.setCurrentTileIndex(1);
		// mGameOverScene.attachChild(labelSprite);

		// final Sprite pauseSprite = new Sprite(overX, overY, mResourceManager.mPausedTextureRegion, mVertexBufferObjectManager);
		// pauseSprite.setScale(0.75f);
		// mGameOverScene.attachChild(pauseSprite);

		// mostTitle
		final float mostX = scoreX;
		final float mostY = scoreY + yGap + scoreTitleText.getHeight();

		final Text mostTitleText = new Text(mostX, mostY, mResourceManager.mDetailsFont, "Highest:", new TextOptions(HorizontalAlign.LEFT),
				mVertexBufferObjectManager);
		mostTitleText.setAlpha(0);

		// cabbageEaten
		final Text eatenText = new Text(GameActivity.CAMERA_WIDTH, eatenTitleText.getY(), mResourceManager.mDetailsFont, "1234567890",
				mVertexBufferObjectManager);
		eatenText.setTextOptions(new TextOptions(HorizontalAlign.RIGHT));

		// cabbageScore
		final Text cabbageScoreText = new Text(GameActivity.CAMERA_WIDTH, cabbageScoreTitleText.getY(), mResourceManager.mDetailsFont, "1234567890",
				mVertexBufferObjectManager);
		cabbageScoreText.setTextOptions(new TextOptions(HorizontalAlign.RIGHT));

		// wormLength
		final Text lengthText = new Text(GameActivity.CAMERA_WIDTH, lengthTitleText.getY(), mResourceManager.mDetailsFont, "1234567890",
				mVertexBufferObjectManager);
		lengthText.setTextOptions(new TextOptions(HorizontalAlign.RIGHT));

		// score
		final Text scoreText = new Text(GameActivity.CAMERA_WIDTH, scoreTitleText.getY(), mResourceManager.mDetailsFont, "1234567890",
				mVertexBufferObjectManager);
		scoreText.setTextOptions(new TextOptions(HorizontalAlign.RIGHT));

		// most (highest score)
		final Text mostText = new Text(GameActivity.CAMERA_WIDTH, mostTitleText.getY(), mResourceManager.mDetailsFont, "1234567890",
				mVertexBufferObjectManager);
		mostText.setTextOptions(new TextOptions(HorizontalAlign.RIGHT));

		// playAgain button
		final float playButtonX = safeGap;
		final float playButtonY = windowH + safeGap;

		final ButtonSprite playAgainButton = new ButtonSprite(playButtonX, playButtonY, mResourceManager.mPlayAgainButtonTiledTextureRegion,
				mVertexBufferObjectManager);
		playAgainButton.setX((windowW - playAgainButton.getWidth()) * .5f);
		playAgainButton.setVisible(false);
		playAgainButton.setAlpha(0);
		playAgainButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				clearChildScene();
				mSceneManager.setScene(SceneType.SCENE_GAME);
				ResourceManager.getInstance().playSfx(ResourceManager.MUSIC_BACKGROUND_START);
			}
		});

		mGameOverScene.setOnAreaTouchListener(new IOnAreaTouchListener()
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if(pTouchArea == window)
				{
					mGameOverScene.unregisterTouchArea(window);
					ResourceManager.getInstance().stopSfx(ResourceManager.SOUND_SCORE);
					ResourceManager.getInstance().playSfx(ResourceManager.SOUND_SCORE);

					eatenText.clearEntityModifiers();
					cabbageScoreText.clearEntityModifiers();
					lengthText.clearEntityModifiers();
					scoreText.clearEntityModifiers();
					mostText.clearEntityModifiers();

					int eaten = mScore.size();
					int score = mScoreOverall;
					// for(Integer points : mScore)
					// score += points;
					int length = mWorm.getBodyLinks().size() * 5;
					int finalScore = eaten + score + length;
					int highest = mMost;
					if(finalScore > mMost)
					{
						highest = finalScore;
						mGameOverScene.registerUpdateHandler(new TimerHandler(45, new ITimerCallback()
						{
							@Override
							public void onTimePassed(TimerHandler pTimerHandler)
							{
								onBackKeyPressed();
							}
						}));

						showNewHighestScoreText(safeGap, windowW, windowH, window, mostText);
					}

					eatenText.setText(String.valueOf(eaten));
					cabbageScoreText.setText(String.valueOf(score));
					lengthText.setText(String.valueOf(length));
					scoreText.setText(String.valueOf(finalScore));
					mostText.setText(String.valueOf(highest));

					eatenText.setColor(Color.WHITE);
					cabbageScoreText.setColor(Color.WHITE);
					lengthText.setColor(Color.WHITE);
					scoreText.setColor(Color.WHITE);
					mostText.setColor(Color.WHITE);

					eatenText.setX(windowW - safeGap - eatenText.getWidth());
					cabbageScoreText.setX(windowW - safeGap - cabbageScoreText.getWidth());
					lengthText.setX(windowW - safeGap - lengthText.getWidth());
					scoreText.setX(windowW - safeGap - scoreText.getWidth());
					mostText.setX(windowW - safeGap - mostText.getWidth());
					return true;
				}
				return false;
			}
		});

		// final TiledSprite playSprite = new TiledSprite(playX, playY, mResourceManager.mButtonTextureRegion, mVertexBufferObjectManager){
		// @Override
		// public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY){
		// if(pSceneTouchEvent.isActionUp()){
		// clearChildScene();
		// mSceneManager.setScene(SceneType.SCENE_GAME);}
		// return true;}};
		// playSprite.setCurrentTileIndex(0);
		// playSprite.setScale(0.75f);
		// mGameOverScene.registerTouchArea(playSprite);
		// mGameOverScene.attachChild(playSprite);
		// final TiledSprite posSprite = new TiledSprite(posX, posY, mResourceManager.mButtonTextureRegion, mVertexBufferObjectManager);
		// posSprite.setCurrentTileIndex(1);
		// posSprite.setScale(0.75f);
		// mGameOverScene.registerTouchArea(posSprite);
		// mGameOverScene.attachChild(posSprite);

		mGameOverScene.attachChild(gameOverText);
		gameOverText.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(1, 0, 1, EaseElasticOut.getInstance()), new MoveModifier(1,
				goTextX, goTextX, (GameActivity.CAMERA_HEIGHT - gameOverText.getHeight()) * .5f, goTextY, new IEntityModifierListener()
				{
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
					{
						int sum = mScore.size() + mScoreOverall;
						// for(Integer num : mScore)
						// sum += num;
						final int baseScore = sum;

						eatenText.setText(String.valueOf(0));
						cabbageScoreText.setText(String.valueOf(0));
						lengthText.setText(String.valueOf(0));
						scoreText.setText(String.valueOf(0));
						mostText.setText(String.valueOf(mMost));

						mGameOverScene.attachChild(window);
						window.registerEntityModifier(new ParallelEntityModifier(new AlphaModifier(1, 0, .45f),
								new MoveModifier(1, windowX, windowX, windowY + 50, windowY, EaseCircularOut.getInstance())));
						window.attachChild(eatenTitleText);
						window.attachChild(cabbageScoreTitleText);
						window.attachChild(lengthTitleText);
						window.attachChild(scoreTitleText);
						window.attachChild(mostTitleText);
						window.attachChild(eatenText);
						window.attachChild(cabbageScoreText);
						window.attachChild(lengthText);
						window.attachChild(scoreText);
						window.attachChild(mostText);
						window.attachChild(playAgainButton);

						ParallelEntityModifier modifier = new ParallelEntityModifier(new AlphaModifier(1, 0, 1),
								new MoveXModifier(1, 0, safeGap, EaseCircularOut.getInstance()));
						eatenTitleText.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(.25f), modifier));
						cabbageScoreTitleText.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(.5f), modifier.deepCopy()));
						lengthTitleText.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(.75f), modifier.deepCopy()));
						scoreTitleText.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(1.0f), modifier.deepCopy()));
						mostTitleText.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(1.25f), modifier.deepCopy()));

						final LoopEntityModifier scoreCounterLoop = new LoopEntityModifier(new IEntityModifierListener()
						{
							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
							{}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
							{
								final int highest = baseScore + (mWorm.getBodyLinks().size() * 5);
								if(highest > mMost)
								{
									int steps = (highest - mMost) / 5;
									final int reminder = (highest - mMost) % 5;
									mostText.registerEntityModifier(new LoopEntityModifier(new ColorModifier(.05f, Color.GREEN, Color.WHITE), steps,
											new ILoopEntityModifierListener()
									{
										int extra = reminder;

										@Override
										public void onLoopStarted(LoopModifier<IEntity> pLoopModifier, int pLoop, int pLoopCount)
										{
											extra += 5;
											mostText.setText(String.valueOf(mMost + extra));
											mostText.setX(windowW - safeGap - mostText.getWidth());
											ResourceManager.getInstance().stopSfx(ResourceManager.SOUND_SCORE);
											ResourceManager.getInstance().playSfx(ResourceManager.SOUND_SCORE);
										}

										@Override
										public void onLoopFinished(LoopModifier<IEntity> pLoopModifier, int pLoop, int pLoopCount)
										{
											if(pLoop >= pLoopCount - 1)
											{
												mGameOverScene.unregisterTouchArea(window);
												mGameOverScene.registerUpdateHandler(new TimerHandler(45, new ITimerCallback()
												{
													@Override
													public void onTimePassed(TimerHandler pTimerHandler)
													{
														onBackKeyPressed();
													}
												}));

												showNewHighestScoreText(safeGap, windowW, windowH, window, mostText);
											}
										}
									}));
								}
								else
								{
									mGameOverScene.unregisterTouchArea(window);
									mGameOverScene.registerUpdateHandler(new TimerHandler(45, new ITimerCallback()
									{
										@Override
										public void onTimePassed(TimerHandler pTimerHandler)
										{
											onBackKeyPressed();
										}
									}));
								}
							}
						}, mWorm.getBodyLinks().size(), new ILoopEntityModifierListener()
						{
							private int extra;// = steps % 5; // doesn't need the reminder now as the score of the worm length is multiplied by 5. therefore there
												// won't be any reminder ever.

							@Override
							public void onLoopStarted(LoopModifier<IEntity> pLoopModifier, int pLoop, int pLoopCount)
							{
								extra += 5;
								scoreText.setText(String.valueOf(baseScore + extra));
								scoreText.setX(windowW - safeGap - scoreText.getWidth());
								ResourceManager.getInstance().stopSfx(ResourceManager.SOUND_SCORE);
								ResourceManager.getInstance().playSfx(ResourceManager.SOUND_SCORE);
							}

							@Override
							public void onLoopFinished(LoopModifier<IEntity> pLoopModifier, int pLoop, int pLoopCount)
							{}
						}, new ColorModifier(.05f, Color.GREEN, Color.WHITE));

						final LoopEntityModifier lengthCounterLoop = new LoopEntityModifier(new IEntityModifierListener()
						{

							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
							{}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
							{
								scoreText.registerEntityModifier(scoreCounterLoop);
							}
						}, mWorm.getBodyLinks().size(), new ILoopEntityModifierListener()
						{
							private int bonus;

							@Override
							public void onLoopStarted(LoopModifier<IEntity> pLoopModifier, int pLoop, int pLoopCount)
							{
								bonus += 5;
								lengthText.setText(String.valueOf(bonus));
								lengthText.setX(windowW - safeGap - lengthText.getWidth());
								ResourceManager.getInstance().stopSfx(ResourceManager.SOUND_SCORE);
								ResourceManager.getInstance().playSfx(ResourceManager.SOUND_SCORE);
							}

							@Override
							public void onLoopFinished(LoopModifier<IEntity> pLoopModifier, int pLoop, int pLoopCount)
							{}
						}, new ColorModifier(.05f, Color.GREEN, Color.WHITE));

						final LoopEntityModifier cabbageScoreCounterLoop = new LoopEntityModifier(new IEntityModifierListener()
						{

							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
							{}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
							{
								lengthText.registerEntityModifier(lengthCounterLoop);
							}
						}, mScore.size(), new ILoopEntityModifierListener()
						{
							private int score;

							@Override
							public void onLoopStarted(LoopModifier<IEntity> pLoopModifier, int pLoop, int pLoopCount)
							{
								if(mScore.size() > 0)
									score += mScore.get(pLoop);
								cabbageScoreText.setText(String.valueOf(score));
								cabbageScoreText.setX(windowW - safeGap - cabbageScoreText.getWidth());
								ResourceManager.getInstance().stopSfx(ResourceManager.SOUND_SCORE);
								ResourceManager.getInstance().playSfx(ResourceManager.SOUND_SCORE);
							}

							@Override
							public void onLoopFinished(LoopModifier<IEntity> pLoopModifier, int pLoop, int pLoopCount)
							{}
						}, new ColorModifier(.05f, Color.GREEN, Color.WHITE));

						final LoopEntityModifier eatenCounterLoop = new LoopEntityModifier(new IEntityModifierListener()
						{
							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
							{}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
							{
								cabbageScoreText.registerEntityModifier(cabbageScoreCounterLoop);
							}
						}, mScore.size(), new ILoopEntityModifierListener()
						{
							private int score;

							@Override
							public void onLoopStarted(LoopModifier<IEntity> pLoopModifier, int pLoop, int pLoopCount)
							{
								score++;
								eatenText.setText(String.valueOf(score));
								eatenText.setX(windowW - safeGap - eatenText.getWidth());
								ResourceManager.getInstance().stopSfx(ResourceManager.SOUND_SCORE);
								ResourceManager.getInstance().playSfx(ResourceManager.SOUND_SCORE);
							}

							@Override
							public void onLoopFinished(LoopModifier<IEntity> pLoopModifier, int pLoop, int pLoopCount)
							{}
						}, new ColorModifier(.05f, Color.GREEN, Color.WHITE));

						eatenText.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(.25f),
								new ParallelEntityModifier(new AlphaModifier(1, 0, 1), new MoveXModifier(1, windowW + safeGap,
										windowW - safeGap - cabbageScoreText.getWidth(), EaseCircularOut.getInstance()))));

						cabbageScoreText.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(.5f),
								new ParallelEntityModifier(new AlphaModifier(1, 0, 1), new MoveXModifier(1, windowW + safeGap,
										windowW - safeGap - cabbageScoreText.getWidth(), EaseCircularOut.getInstance()))));

						lengthText.registerEntityModifier(new SequenceEntityModifier(new IEntityModifierListener()
						{
							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
							{}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
							{}
						}, new DelayModifier(.75f), new ParallelEntityModifier(new AlphaModifier(1, 0, 1),
								new MoveXModifier(1, windowW + safeGap, windowW - safeGap - lengthText.getWidth(), EaseCircularOut.getInstance()))));

						scoreText.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(1.0f), new ParallelEntityModifier(
								new AlphaModifier(1, 0, 1),
								new MoveXModifier(1, windowW + safeGap, windowW - safeGap - scoreText.getWidth(), EaseCircularOut.getInstance()))));

						mostText.registerEntityModifier(new SequenceEntityModifier(new IEntityModifierListener()
						{
							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
							{}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
							{
								if(mScore.size() > 0)
									eatenText.registerEntityModifier(eatenCounterLoop);
								else
									cabbageScoreText.registerEntityModifier(cabbageScoreCounterLoop);
								mGameOverScene.registerTouchArea(window);
							}
						}, new DelayModifier(1.25f), new ParallelEntityModifier(new AlphaModifier(1, 0, 1),
								new MoveXModifier(1, windowW + safeGap, windowW - safeGap - mostText.getWidth(), EaseCircularOut.getInstance()))));
					}

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
					{
						playAgainButton.setVisible(true);
						mGameOverScene.registerTouchArea(playAgainButton);
						playAgainButton.registerEntityModifier(new AlphaModifier(1, 0, 1));
					}
				}, EaseCircularOut.getInstance())));
	}

	private SequenceEntityModifier getClickSequenceEntityModifier(final Sprite helpWormNatSprite, final Sprite helpWormSprite,
			final TiledSprite handSprite)
	{
		DelayModifier clickModifier = new DelayModifier(.5f, new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{
				handSprite.setCurrentTileIndex(0);
				helpWormNatSprite.setVisible(false);
				helpWormSprite.setVisible(true);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{}
		});

		DelayModifier clickUpModifier = new DelayModifier(.5f, new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{
				handSprite.setCurrentTileIndex(1);
				helpWormNatSprite.setVisible(true);
				helpWormSprite.setVisible(false);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{}
		});
		return new SequenceEntityModifier(clickModifier, clickUpModifier);
	}

	private void createGamePauseScene()
	{
		Text pausedText = new Text(0, 0, mResourceManager.mBigFont, "PAUSED", mVertexBufferObjectManager);
		pausedText.setPosition((GameActivity.CAMERA_WIDTH - pausedText.getWidth()) * .5f,
				(GameActivity.CAMERA_HEIGHT - pausedText.getHeight()) * .5f);
		mGamePauseScene = new CameraScene(mCamera);
		mGamePauseScene.attachChild(pausedText);
		mGamePauseScene.setBackgroundEnabled(false);
		mGamePauseScene.setOnSceneTouchListener(new IOnSceneTouchListener()
		{
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
			{
				resumeGame();
				return false;
			}
		});
	}

	public PhysicsWorld getPhysicsWorld()
	{
		if(this.mPhysicsWorld == null)
			throw new NullPointerException(
					"mPhysicsWorld is null. probably because calling getPhysicsWorld() too early. perhaps before GameScene.createScene(). Fix that!");
		return this.mPhysicsWorld;
	}

	private void startGame()
	{
		ResourceManager.getInstance().stopSfx(ResourceManager.MUSIC_LOOP);
		ResourceManager.getInstance().playSfx(ResourceManager.MUSIC_START);
		mCamera.setHUD(gameHUD);
		// mHudText.setVisible(true);
		mHudText.registerEntityModifier(new ScaleModifier(1, 0, 1, EaseElasticOut.getInstance()));
		mGameOver = false;
		mWorm.resume();
		mWorm.setRotation(-90);
		registerEntityModifier(new DelayModifier(.25f, new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				mCheckBounds = true;
			}
		}));
	}

	public void pauseGame()
	{
		mIsGamePaused = true;
		setChildScene(mGamePauseScene, false, true, true);
	}

	public boolean isPaused()
	{
		return mIsGamePaused;
	}

	public void resumeGame()
	{
		mIsGamePaused = false;
		clearChildScene();
		mResourceManager.resume();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{
		if(mCheckBounds && mPhysicsWorld != null)
		{
			if(!mGameOver)
			{
				float x = pSceneTouchEvent.getX();
				if(pSceneTouchEvent.isActionDown())
				{
					if(x < GameActivity.CAMERA_WIDTH * .5f)
					{
						mWorm.setMoveLeft(true);
						leftHalfScreenPointerId = pSceneTouchEvent.getPointerID();
					}
					else
					{
						mWorm.setMoveRight(true);
						rightHalfScreenPointerId = pSceneTouchEvent.getPointerID();
					}
					return true;
				}

				if(pSceneTouchEvent.isActionUp() || pSceneTouchEvent.isActionOutside() || pSceneTouchEvent.isActionCancel())
				{
					if(pSceneTouchEvent.getPointerID() == leftHalfScreenPointerId)
						mWorm.setMoveLeft(false);
					if(pSceneTouchEvent.getPointerID() == rightHalfScreenPointerId)
						mWorm.setMoveRight(false);
					return true;
				}
			}
		}
		return false;
	}

	public void moveWormLeft(boolean move)
	{
		if(mWorm != null && mCheckBounds)
			mWorm.setMoveLeft(move);
	}

	public void moveWormRight(boolean move)
	{
		if(mWorm != null && mCheckBounds)
			mWorm.setMoveRight(move);
	}

	public void performEnterKey()
	{
		if(hasChildScene())
		{
			if(getChildScene() == mGameReadyScene)
			{
				clearChildScene();
				ResourceManager.getInstance().stopSfx(ResourceManager.ALL_MUSICS);
			}
			else
				if(getChildScene() == mGameOverScene)
				{
					clearChildScene();
					mSceneManager.setScene(SceneType.SCENE_GAME);
					ResourceManager.getInstance().playSfx(ResourceManager.MUSIC_BACKGROUND_START);
				}
		}
	}

	private ContactListener createContactListener()
	{
		ContactListener contactListener = new ContactListener()
		{
			@Override
			public void beginContact(Contact pContact)
			{
				final Fixture fixtureA = pContact.getFixtureA();
				final Body bodyA = fixtureA.getBody();
				final String userDataA = (String)bodyA.getUserData();

				final Fixture fixtureB = pContact.getFixtureB();
				final Body bodyB = fixtureB.getBody();
				final String userDataB = (String)bodyB.getUserData();

				if(("wormHead".equals(userDataA) && "wormBody".equals(userDataB)) || ("wormBody".equals(userDataA) && "wormHead".equals(userDataB)))
				{
					mActivity.runOnUpdateThread(new Runnable()
					{
						@Override
						public void run()
						{
							gameOver();
						}
					});
				}
				else
					if(("wormHead".equals(userDataA) && "cabbage".equals(userDataB)) || ("cabbage".equals(userDataA) && "wormHead".equals(userDataB)))
					{
						if(mCabbage.isAvailable())
						{
							ResourceManager.getInstance().playSfx(ResourceManager.SOUND_SCORE);

							Vector2 scorePoint = mCabbage.getCurrentLocation();
							float cabbageScale = mCabbage.despawn(true);
							mWorm.growWorm(5);
							int score = (int)((6 + 3 * cabbageScale) * cabbageScale);
							if(isDoublePoints)
								score *= 2;

							mPointScoreText.setText(String.valueOf(score));
							mPointScoreText.setX(scorePoint.x - mPointScoreText.getWidth() * .5f);
							scorePoint.y = scorePoint.y - mPointScoreText.getHeight() * .5f;
							mPointScoreText.setVisible(true);
							mPointScoreText
									.registerEntityModifier(new MoveYModifier(.75f, scorePoint.y, scorePoint.y - 25, new IEntityModifierListener()
							{
								@Override
								public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
								{}

								@Override
								public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
								{
									mPointScoreText.setVisible(false);
								}
							}, EaseCubicOut.getInstance()));

							mScore.add(score);
							mScoreOverall += score;
							// for(int i = 0 ; i < mScore.size() - 1 ; i++)
							// score += mScore.get(i);

							mHudText.setText(String.valueOf(mScoreOverall));
							mHudText.setX((SCREEN_WIDTH - mHudText.getWidth()) * .5f);
							mHudText.clearEntityModifiers();
							EaseExponentialOut ease = EaseExponentialOut.getInstance();
							mHudText.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(.15f, 1, 2, ease),
									new LoopEntityModifier(
											new SequenceEntityModifier(new RotationModifier(.07f, -30, 30), new RotationModifier(.07f, 30, -30)), 2),
									new RotationModifier(.05f, -30, 0), new ScaleModifier(.15f, 2, 1, ease)));
							mWorm.eat();
						}
					}
					else
						if(("wormHead".equals(userDataA) && "rock".equals(userDataB)) || ("rock".equals(userDataA) && "wormHead".equals(userDataB)))
						{
							ResourceManager.getInstance().stopSfx(true, ResourceManager.ALL_MUSICS);
							ResourceManager.getInstance().playSfx(ResourceManager.SOUND_ROCK_EATEN);
							// mRock.forceDespawnRock();
							setIgnoreUpdate(true);
							mActivity.getEngine().registerUpdateHandler(new TimerHandler(.75f, new ITimerCallback()
							{
								@Override
								public void onTimePassed(TimerHandler pTimerHandler)
								{
									mActivity.getEngine().unregisterUpdateHandler(pTimerHandler);
									mActivity.runOnUpdateThread(new Runnable()
									{
										@Override
										public void run()
										{
											setIgnoreUpdate(false);
											gameOver();
										}
									});
								}
							}));
						}
						else
							if(("wormHead".equals(userDataA) && "powerupX2".equals(userDataB))
									|| ("powerupX2".equals(userDataA) && "wormHead".equals(userDataB)))
							{
								mPowerUp.forceDespawn();
								unregisterUpdateHandler(mPowerupDespawner);
								mPowerupDespawner.reset();
								doublePointsSprite.setVisible(isDoublePoints = true);
								doublePointsSprite.animate(50);
								mResourceManager.playSfx(ResourceManager.SOUND_POWERUP_GUI_APPEAR);
								doublePointsSprite.registerEntityModifier(new ScaleModifier(.75f, 0, 1, EaseElasticOut.getInstance()));
								registerUpdateHandler(new TimerHandler(10, new ITimerCallback()
								{
									@Override
									public void onTimePassed(TimerHandler pTimerHandler)
									{
										unregisterUpdateHandler(pTimerHandler);
										doublePointsSprite.registerEntityModifier(new ScaleModifier(.75f, 1, 0, new IEntityModifierListener()
										{
											@Override
											public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
											{}

											@Override
											public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
											{
												doublePointsSprite.setVisible(isDoublePoints = false);
												doublePointsSprite.stopAnimation();
												mResourceManager.playSfx(ResourceManager.SOUND_POWERUP_GUI_DISAPPEAR);
											}
										}, EaseStrongIn.getInstance()));
									}
								}));
							}
			}

			@Override
			public void endContact(Contact contact)
			{}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold)
			{}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse)
			{}
		};
		return contactListener;
	}

	private void gameOver()
	{
		mGameOver = true;
		mWorm.pause();

		int sum = mScore.size() + mScoreOverall;
		// for(Integer num : mScore)
		// sum += num;

		int score = sum + (mWorm.getBodyLinks().size() * 5);
		// mScore += mWorm.getBodyLinks().size();

		if(score > mMost)
		{
			// mMost = mScore;
			mActivity.setMaxScore(score);
		}

		// mHudText.setVisible(false);
		mCamera.setHUD(null);

		// TODO display game over with score
		// mScoreText.setText(String.valueOf("Score: " + mScore));
		// mMostText.setText(String.valueOf("Highest: " + mMost));
		setChildScene(mGameOverScene, false, true, true);

		mResourceManager.stopSfx(true, ResourceManager.ALL_SFX);
		mResourceManager.playSfx(ResourceManager.SOUND_GAME_OVER);
	}

	private boolean doExit;

	@Override
	public void onBackKeyPressed()
	{
		if(doExit)
		{
			ResourceManager.getInstance().stopSfx(ResourceManager.ALL_SFX);
			mCamera.setHUD(null);
			mSceneManager.setScene(SceneType.SCENE_MENU);
		}
		else
			if(getChildScene() == null)
			{
				doExit = true;
				Toast.makeText(mActivity, "Press again to quit to main menu.", Toast.LENGTH_SHORT).show();
				registerUpdateHandler(new TimerHandler(2, new ITimerCallback()
				{
					@Override
					public void onTimePassed(TimerHandler pTimerHandler)
					{
						doExit = false;
					}
				}));
			}
			else
			{
				ResourceManager.getInstance().stopSfx(ResourceManager.ALL_SFX);
				mCamera.setHUD(null);
				mSceneManager.setScene(SceneType.SCENE_MENU);
			}
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene()
	{
		mWorm.destroyAll();
	}

	private void showNewHighestScoreText(final float safeGap, final float windowW, final float windowH, final Rectangle window, final Text mostText)
	{
		LoopEntityModifier loopA = new LoopEntityModifier(
				new SequenceEntityModifier(new ScaleModifier(.35f, 1, 1.25f, EaseCircularInOut.getInstance()),
						new ScaleModifier(.75f, 1.25f, 1, EaseCircularInOut.getInstance())));
		LoopEntityModifier loopB = new LoopEntityModifier(
				new SequenceEntityModifier(new ColorModifier(.25f, Color.WHITE, Color.GREEN), new ColorModifier(.25f, Color.GREEN, Color.WHITE)));

		mostText.registerEntityModifier(new ParallelEntityModifier(loopA, loopB));

		Text high = new Text(0, 0, mResourceManager.mTitleFont, "NEW HIGH SCORE!", mVertexBufferObjectManager);
		final float highY = windowH - safeGap;
		high.setPosition((windowW - high.getWidth()) * .5f, highY);
		high.setRotationCenter(high.getWidth() * .5f, high.getHeight() * .5f);
		high.setScaleCenter(high.getRotationCenterX(), high.getRotationCenterY());
		window.attachChild(high);
		EaseQuadInOut ease = EaseQuadInOut.getInstance();
		high.registerEntityModifier(
				new SequenceEntityModifier(new ScaleModifier(.5f, 0, 1, EaseCircularOut.getInstance()), new RotationModifier(.5f, 0, -5, ease),
						new ParallelEntityModifier(
								new LoopEntityModifier(new SequenceEntityModifier(new MoveYModifier(1.25f, highY, highY - 25, ease),
										new MoveYModifier(1.39f, highY - 25, highY, ease))),
						new LoopEntityModifier(
								new SequenceEntityModifier(new RotationModifier(1, -5, 5, ease), new RotationModifier(1, 5, -5, ease))))));
	}
}