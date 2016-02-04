package barinadroid.cabbageworm.engine.model;

import java.util.Random;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseElasticOut;
import org.andengine.util.modifier.ease.EaseStrongIn;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import barinadroid.cabbageworm.GameActivity;
import barinadroid.cabbageworm.engine.control.ResourceManager;
import barinadroid.cabbageworm.engine.scene.GameScene;

public class PowerUpX2 implements IConsumable
{
	private static final FixtureDef FIXTURE_DEF;
	private static final float ANIMATION_DURATION = .7f;
	private static final Random RANDOM = new Random(23423);
	public static final long FPS = 45l;

	static
	{
		FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0, 0);
	}

	private static float randomFloat(float max)
	{
		return max * RANDOM.nextFloat();
	}

	private final AnimatedSprite powerSprite;
	private final Body powerBody;
	private boolean isAvailable;
	private GameScene gameScene;

	public PowerUpX2(GameScene gameScene)
	{
		this.gameScene = gameScene;

		this.powerSprite = new AnimatedSprite(0, 0, gameScene.mResourceManager.mPowerupTiledTextureRegion, gameScene.mVertexBufferObjectManager);
		this.powerSprite.setScaleCenter(powerSprite.getWidth() * .5f, powerSprite.getHeight() * .5f);

		this.powerBody = PhysicsFactory.createCircleBody(gameScene.getPhysicsWorld(), powerSprite, BodyType.KinematicBody, FIXTURE_DEF);
		this.powerBody.setUserData("powerupX2");
		this.powerBody.setActive(false);
		this.powerSprite.setUserData(powerBody);
		this.powerSprite.setZIndex(0);
		gameScene.getPhysicsWorld().registerPhysicsConnector(new PhysicsConnector(powerSprite, powerBody, true, true));
	}

	@Override
	public boolean isAvailable()
	{
		return isAvailable;
	}

	@Override
	public Vector2 getCurrentLocation()
	{

		float x = powerSprite.getX() + powerSprite.getScaleCenterX();
		float y = powerSprite.getY() + powerSprite.getScaleCenterY();
		return new Vector2(x, y);
	}

	@Override
	public void respawn(float scale)
	{
		float toAngle = 0;
		float fromAngle = -60 + randomFloat(120);

		float width = GameActivity.CAMERA_WIDTH - GameScene.SIDES_GAP * 2 - GameScene.SAFE_GAP * 2;
		float height = GameActivity.CAMERA_HEIGHT - powerSprite.getHeight() - GameScene.SAFE_GAP * 2;
		float x = GameScene.SAFE_GAP + GameScene.SIDES_GAP + (float)(width * Math.random());
		x /= PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		float y = GameScene.SAFE_GAP + powerSprite.getHeight() * .5f + (float)(height * Math.random());
		y /= PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		powerBody.setTransform(x, y, (float)Math.toRadians(toAngle));
		if(powerSprite.hasParent())
			powerSprite.detachSelf();
		powerSprite.setScale(1);
		powerSprite.animate(FPS);
		ResourceManager.getInstance().playSfx(ResourceManager.SOUND_POWERUP_APPEAR);
		isAvailable = true;

		gameScene.mActivity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				gameScene.attachChild(powerSprite);
				powerBody.setActive(isAvailable);
			}
		});

		ParallelEntityModifier modifier = new ParallelEntityModifier(new ScaleModifier(ANIMATION_DURATION, 0f, 1, EaseElasticOut.getInstance()),
				new RotationModifier(ANIMATION_DURATION, fromAngle, toAngle, EaseElasticOut.getInstance()));
		powerSprite.registerEntityModifier(modifier);
		ResourceManager.getInstance().playSfx((int)(ResourceManager.SOUND_CABBAGE_GROWTH_A
				+ (ResourceManager.SOUND_CABBAGE_GROWTH_C - ResourceManager.SOUND_CABBAGE_GROWTH_A + .99f) * Math.random()));
	}

	@Override
	public float despawn(boolean eaten)
	{
		if(eaten)
		{
			forceDespawn();
		}
		else
			powerSprite.registerEntityModifier(new ScaleModifier(ANIMATION_DURATION, 1, 0, new IEntityModifierListener()
			{
				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
				{
					ResourceManager.getInstance().playSfx(ResourceManager.SOUND_POWERUP_DISAPPEAR);
				}

				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
				{
					ResourceManager.getInstance().playSfx(ResourceManager.SOUND_POWERUP_EATEN);
					forceDespawn();
				}
			}, EaseStrongIn.getInstance()));
		return 1;
	}

	public void forceDespawn()
	{
		gameScene.mActivity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				gameScene.detachChild(powerSprite);
				powerSprite.stopAnimation();
				powerSprite.clearEntityModifiers();
				powerBody.setActive(isAvailable = false);
			}
		});
	}
}