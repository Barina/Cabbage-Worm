package barinadroid.cabbageworm.engine.model;

import java.util.Random;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseElasticOut;
import org.andengine.util.modifier.ease.EaseStrongIn;
import org.andengine.util.modifier.ease.EaseStrongOut;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import barinadroid.cabbageworm.GameActivity;
import barinadroid.cabbageworm.engine.control.ResourceManager;
import barinadroid.cabbageworm.engine.scene.GameScene;

public class Rock implements IConsumable
{
	private static final FixtureDef FIXTURE_DEF;
	private static final float ANIMATION_DURATION = .7f;
	private static final Random RANDOM = new Random(23423);

	static
	{
		FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0, 0);
	}

	private static float randomFloat(float max)
	{
		return max * RANDOM.nextFloat();
	}

	private final Sprite rockSprite;
	private final Body rockBody;
	private boolean isAvailable;
	private GameScene gameScene;
	private float lastScale;

	public Rock(GameScene gameScene)
	{
		this.gameScene = gameScene;

		this.rockSprite = new Sprite(0, 0, gameScene.mResourceManager.mRockTextureRegion, gameScene.mVertexBufferObjectManager);
		float width = rockSprite.getWidth() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		float height = rockSprite.getHeight() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		this.rockSprite.setScaleCenter(rockSprite.getWidth() * .5f, rockSprite.getHeight() * .5f);

		// this.cabbageSprite = new Sprite(0, 0, gameScene.mResourceManager.mCabbageTextureRegion, gameScene.mVertexBufferObjectManager);
		// float width = cabbageSprite.getWidth() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		// float height = cabbageSprite.getHeight() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		// this.cabbageSprite.setScaleCenter(122, 108);

		this.rockBody = PhysicsFactory.createPolygonBody(gameScene.getPhysicsWorld(), rockSprite, new Vector2[]
		{new Vector2(+0.07812f * width, -0.49778f * height), new Vector2(+0.31641f * width, -0.44000f * height),
				new Vector2(+0.47656f * width, -0.04889f * height), new Vector2(+0.12500f * width, +0.35556f * height),
				new Vector2(-0.41406f * width, +0.28444f * height), new Vector2(-0.49609f * width, +0.09333f * height)}, BodyType.KinematicBody,
				FIXTURE_DEF);
		this.rockBody.setUserData("rock");
		this.rockBody.setActive(false);
		this.rockSprite.setUserData(rockBody);
		this.rockSprite.setZIndex(0);
		gameScene.getPhysicsWorld().registerPhysicsConnector(new PhysicsConnector(rockSprite, rockBody, true, true));
	}

	@Override
	public boolean isAvailable()
	{
		return isAvailable;
	}

	@Override
	public Vector2 getCurrentLocation()
	{

		float x = rockSprite.getX() + rockSprite.getScaleCenterX();
		float y = rockSprite.getY() + rockSprite.getScaleCenterY();
		return new Vector2(x, y);
	}

	@Override
	public void respawn(float scale)
	{
		float toAngle = 0;
		float fromAngle = -60 + randomFloat(120);

		lastScale = scale = 1;

		float width = GameActivity.CAMERA_WIDTH - GameScene.SIDES_GAP * 2 - GameScene.SAFE_GAP * 2;
		float height = GameActivity.CAMERA_HEIGHT - rockSprite.getHeight() - GameScene.SAFE_GAP * 2;
		float x = GameScene.SAFE_GAP + GameScene.SIDES_GAP + (float)(width * Math.random());
		x /= PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		float y = GameScene.SAFE_GAP + rockSprite.getHeight() * .5f + (float)(height * Math.random());
		y /= PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		rockBody.setTransform(x, y, (float)Math.toRadians(toAngle));
		if(rockSprite.hasParent())
			rockSprite.detachSelf();
		rockSprite.setScale(scale);
		ResourceManager.getInstance().playSfx(ResourceManager.SOUND_ROCK_APPEAR);
		isAvailable = true;

		gameScene.mActivity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				gameScene.attachChild(rockSprite);
			}
		});

		ParallelEntityModifier modifier = new ParallelEntityModifier(new ScaleModifier(ANIMATION_DURATION, 0f, scale, new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				gameScene.mActivity.runOnUpdateThread(new Runnable()
				{
					@Override
					public void run()
					{
						rockBody.setActive(isAvailable);
					}
				});
			}
		}, EaseStrongOut.getInstance()), new RotationModifier(ANIMATION_DURATION, fromAngle, toAngle, EaseElasticOut.getInstance()));
		rockSprite.registerEntityModifier(modifier);
		ResourceManager.getInstance().playSfx((int)(ResourceManager.SOUND_CABBAGE_GROWTH_A
				+ (ResourceManager.SOUND_CABBAGE_GROWTH_C - ResourceManager.SOUND_CABBAGE_GROWTH_A + .99f) * Math.random()));
	}

	@Override
	public float despawn(boolean eaten)
	{
		if(!eaten)
			ResourceManager.getInstance().playSfx(ResourceManager.SOUND_ROCK_DISAPPEAR);
		rockSprite.registerEntityModifier(new ScaleModifier(ANIMATION_DURATION, 1, 0, new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{
				gameScene.mActivity.runOnUpdateThread(new Runnable()
				{
					@Override
					public void run()
					{
						rockBody.setActive(false);
					}
				});
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				forceDespawn();
			}
		}, EaseStrongIn.getInstance()));

		return lastScale;
	}

	public void forceDespawn()
	{
		gameScene.mActivity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				gameScene.detachChild(rockSprite);
				rockSprite.clearEntityModifiers();
				rockBody.setActive(isAvailable = false);
			}
		});
	}
}