package barinadroid.cabbageworm.engine.model;

import java.util.Random;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleParticleEmitter;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.ScaleParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.RotationParticleModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.util.modifier.ease.EaseElasticOut;
import org.andengine.util.modifier.ease.EaseSineOut;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import barinadroid.cabbageworm.GameActivity;
import barinadroid.cabbageworm.engine.control.ResourceManager;
import barinadroid.cabbageworm.engine.scene.GameScene;

public class Cabbage implements IConsumable
{
	public static final long FPS = 75l;
	private static final FixtureDef CABBAGE_FIXTURE_DEF;
	private static final float ANIMATION_DURATION = 2f;
	private static final Random random = new Random(23423);

	static
	{
		CABBAGE_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0, 0);
		// CABBAGE_FIXTURE_DEF.filter.groupIndex = 2;
	}

	private static float randomFloat(float max)
	{
		return max * random.nextFloat();
	}

	// private final Sprite cabbageSprite;
	private final AnimatedSprite cabbageAnimSprite;
	private final Body cabbageBody;
	private boolean cabbageAvailable;
	private GameScene gameScene;
	private float lastScale;
	private SpriteParticleSystem particleSystem;
	private CircleParticleEmitter particleEmitter;

	public Cabbage(GameScene gameScene)
	{
		this.gameScene = gameScene;

		this.cabbageAnimSprite = new AnimatedSprite(0, 0, gameScene.mResourceManager.mCabbageAnimTiledTextureRegion,
				gameScene.mVertexBufferObjectManager);
		float width = cabbageAnimSprite.getWidth() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		float height = cabbageAnimSprite.getHeight() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		this.cabbageAnimSprite.setScaleCenter(cabbageAnimSprite.getWidth() * .5f, cabbageAnimSprite.getHeight() * .5f);

		// this.cabbageSprite = new Sprite(0, 0, gameScene.mResourceManager.mCabbageTextureRegion, gameScene.mVertexBufferObjectManager);
		// float width = cabbageSprite.getWidth() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		// float height = cabbageSprite.getHeight() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		// this.cabbageSprite.setScaleCenter(122, 108);

		this.cabbageBody = PhysicsFactory.createPolygonBody(gameScene.getPhysicsWorld(), cabbageAnimSprite, new Vector2[]
		{new Vector2(-0.22800f * width, -0.14516f * height), new Vector2(-0.15600f * width, -0.23387f * height),
				new Vector2(-0.00800f * width, -0.27419f * height), new Vector2(+0.14800f * width, -0.19758f * height),
				new Vector2(+0.18400f * width, +0.01210f * height), new Vector2(+0.10400f * width, +0.13710f * height),
				new Vector2(-0.08000f * width, +0.15323f * height), new Vector2(-0.22400f * width, +0.06452f * height)}, BodyType.KinematicBody,
				CABBAGE_FIXTURE_DEF);
		this.cabbageBody.setUserData("cabbage");
		this.cabbageBody.setActive(false);
		this.cabbageAnimSprite.setUserData(cabbageBody);
		this.cabbageAnimSprite.setZIndex(0);
		gameScene.getPhysicsWorld().registerPhysicsConnector(new PhysicsConnector(cabbageAnimSprite, cabbageBody, true, true));
		createParticleSystem();
		gameScene.attachChild(particleSystem);
	}

	private void createParticleSystem()
	{
		particleEmitter = new CircleParticleEmitter(0, 0, 50);
		particleSystem = new SpriteParticleSystem(particleEmitter, 3, 15, 50, gameScene.mResourceManager.mCabbageLeafATextureRegion,
				gameScene.mVertexBufferObjectManager);
		particleSystem.addParticleInitializer(new VelocityParticleInitializer<Sprite>(-100, 100, -100, 100));
		particleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(.5f));
		particleSystem.addParticleInitializer(new AlphaParticleInitializer<Sprite>(1));
		particleSystem.addParticleInitializer(new ScaleParticleInitializer<Sprite>(.7f));
		particleSystem.addParticleModifier(new RotationParticleModifier<Sprite>(0, .5f, 10, -75, EaseSineOut.getInstance()));
		// particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(.75f, 0.9f, 1, 0));//stupid bug.. can't use this
		particleSystem.setParticlesSpawnEnabled(false);
		particleSystem.setZIndex(1000);
	}

	/**
	 * 
	 * @return The scale the cabbage was.
	 */
	@Override
	public float despawn(boolean eaten)
	{
		if(eaten)
		{
			particleEmitter.setCenter(cabbageAnimSprite.getX() + cabbageAnimSprite.getScaleCenterX(),
					cabbageAnimSprite.getY() + cabbageAnimSprite.getScaleCenterY());
			particleSystem.setParticlesSpawnEnabled(true);
			gameScene.registerUpdateHandler(new TimerHandler(.5f, new ITimerCallback()
			{
				@Override
				public void onTimePassed(TimerHandler pTimerHandler)
				{
					particleSystem.setParticlesSpawnEnabled(false);
				}
			}));
		}
		gameScene.mActivity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				gameScene.detachChild(cabbageAnimSprite);
				cabbageAnimSprite.clearEntityModifiers();
				cabbageBody.setActive(cabbageAvailable = false);
			}
		});
		return lastScale;
	}

	@Override
	public void respawn(float scale)
	{
		float toAngle = -15 + randomFloat(30);
		float fromAngle = -60 + randomFloat(120);

		lastScale = scale;
		// float width = GameActivity.CAMERA_WIDTH - GameScene.SIDES_GAP * 2 - GameScene.SAFE_GAP * 2;
		// float height = GameActivity.CAMERA_HEIGHT - cabbageSprite.getHeight() - GameScene.SAFE_GAP * 2;
		// float x = GameScene.SAFE_GAP + GameScene.SIDES_GAP + (float)(width * Math.random());
		// x /= PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		// float y = GameScene.SAFE_GAP + cabbageSprite.getHeight() * .5f + (float)(height * Math.random());
		// y /= PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		// cabbageBody.setTransform(x, y, (float)Math.toRadians(toAngle));
		// cabbageBody.getFixtureList().get(0).getShape().setRadius(scale);
		// if(cabbageSprite.hasParent())
		// cabbageSprite.detachSelf();
		// gameScene.attachChild(cabbageSprite);
		// cabbageSprite.setScale(scale);

		float width = GameActivity.CAMERA_WIDTH - GameScene.SIDES_GAP * 2 - GameScene.SAFE_GAP * 2;
		float height = GameActivity.CAMERA_HEIGHT - cabbageAnimSprite.getHeight() - GameScene.SAFE_GAP * 2;
		float x = GameScene.SAFE_GAP + GameScene.SIDES_GAP + (float)(width * Math.random());
		x /= PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		float y = GameScene.SAFE_GAP + cabbageAnimSprite.getHeight() * .5f + (float)(height * Math.random());
		y /= PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		cabbageBody.setTransform(x, y, (float)Math.toRadians(toAngle));
		cabbageBody.getFixtureList().get(0).getShape().setRadius(scale);
		if(cabbageAnimSprite.hasParent())
			cabbageAnimSprite.detachSelf();
		gameScene.attachChild(cabbageAnimSprite);
		cabbageAnimSprite.setScale(scale);
		cabbageAnimSprite.animate(FPS, 0);

		// ParallelEntityModifier modifier = new ParallelEntityModifier(new ScaleModifier(ANIMATION_DURATION, 0f, scale, EaseElasticOut.getInstance()),
		// new RotationModifier(ANIMATION_DURATION, fromAngle, toAngle, EaseElasticOut.getInstance()));
		// cabbageSprite.registerEntityModifier(modifier);
		cabbageAnimSprite.registerEntityModifier(new RotationModifier(ANIMATION_DURATION, fromAngle, toAngle, EaseElasticOut.getInstance()));
		gameScene.mActivity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				cabbageBody.setActive(cabbageAvailable = true);
			}
		});
		ResourceManager.getInstance().playSfx((int)(ResourceManager.SOUND_CABBAGE_GROWTH_A
				+ (ResourceManager.SOUND_CABBAGE_GROWTH_C - ResourceManager.SOUND_CABBAGE_GROWTH_A + .99f) * Math.random()));
	}

	@Override
	public Vector2 getCurrentLocation()
	{
		float x = cabbageAnimSprite.getX() + cabbageAnimSprite.getScaleCenterX();
		float y = cabbageAnimSprite.getY() + cabbageAnimSprite.getScaleCenterY();
		return new Vector2(x, y);
	}

	@Override
	public boolean isAvailable()
	{
		return cabbageAvailable;
	}
}