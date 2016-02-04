package barinadroid.cabbageworm.engine.model;

import java.util.ArrayList;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleParticleEmitter;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.BlendFunctionParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.ScaleParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.util.modifier.ease.EaseCircularOut;
import org.andengine.util.modifier.ease.EaseElasticOut;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import android.opengl.GLES20;
import barinadroid.cabbageworm.GameActivity;
import barinadroid.cabbageworm.engine.control.ResourceManager;
import barinadroid.cabbageworm.engine.scene.GameScene;

public class Worm implements IUpdateHandler
{
	// private final static short CATEGORY_HEAD = 0x0001;
	// private final static short CATEGORY_NECK = 0x0002;
	// private final static short CATEGORY_BODY = 0x0004;

	// private static final short MASK_BODY = CATEGORY_HEAD | CATEGORY_NECK;
	// private static final short MASK_HEAD = CATEGORY_BODY | CATEGORY_NECK;
	// private static final short MASK_NECK = -1;

	private static final FixtureDef HEAD_FIXTURE_DEF;
	private static final FixtureDef NECK_FIXTURE_DEF;
	private static final FixtureDef BODY_FIXTURE_DEF;

	static
	{
		HEAD_FIXTURE_DEF = PhysicsFactory.createFixtureDef(100, 0, 10);
		NECK_FIXTURE_DEF = PhysicsFactory.createFixtureDef(100, 0, 10);
		BODY_FIXTURE_DEF = PhysicsFactory.createFixtureDef(100, 0, 10);

		// HEAD_FIXTURE_DEF.filter.categoryBits = CATEGORY_HEAD;
		// NECK_FIXTURE_DEF.filter.categoryBits = CATEGORY_NECK;
		// BODY_FIXTURE_DEF.filter.categoryBits = CATEGORY_BODY;
		//
		// HEAD_FIXTURE_DEF.filter.maskBits = MASK_HEAD;
		// NECK_FIXTURE_DEF.filter.maskBits = MASK_NECK;
		// BODY_FIXTURE_DEF.filter.maskBits = MASK_BODY;

		// HEAD_FIXTURE_DEF.filter.groupIndex = -3;
		// NECK_FIXTURE_DEF.filter.groupIndex = -2;
		// BODY_FIXTURE_DEF.filter.groupIndex = -1;
	}

	private static final float STEP = .010f;
	private static final float SPEED = 15f;
	private static final float MAX_SQUIRM = 25;
	private static final float GROWTH_PACE = .250f;
	private static final float TURN_FACTOR = 400f;
	public static final float WORM_SCALE = 2;

	private float stepInterval;
	private float growthInterval;
	private float rotation;
	private int size;
	private final AnimatedSprite head;
	private final Body headBody;
	private final WormLink headLink;
	private final GameScene gameScene;
	private final ArrayList<WormLink> links;
	private boolean wormBodyDelta;
	private boolean moveLeft, moveRight;
	private boolean pause;

	private float squirmRotation;
	private boolean squirmDelta;

	public Worm(GameScene gameScene, int x, int y)
	{
		this.gameScene = gameScene;
		this.gameScene.registerUpdateHandler(this);
		this.size = 10;

		this.head = new AnimatedSprite(x, y, gameScene.mResourceManager.mWormHeadTextureRegion, gameScene.mVertexBufferObjectManager);
		this.gameScene.attachChild(head);
		this.head.setScale(WORM_SCALE);
		this.head.animate(new long[]
		{5600, 155}, true);

		// headBody = PhysicsFactory.createPolygonBody(gameScene.getPhysicsWorld(), head, new Vector2[]
		// {new Vector2(-0.03125f * head.getWidth(), -0.41146f * head.getHeight()), new Vector2(+0.24479f * head.getWidth(), -0.39583f * head.getHeight()),
		// new Vector2(+0.38021f * head.getWidth(), -0.26042f * head.getHeight()), new Vector2(+0.41146f * head.getWidth(), +0.26042f * head.getHeight()),
		// new Vector2(+0.25521f * head.getWidth(), +0.41146f * head.getHeight()), new Vector2(-0.03646f * head.getWidth(), +0.41146f * head.getHeight())},
		// BodyType.DynamicBody,
		// FIXTURE_DEF);

		Rectangle bodyArea = new Rectangle(x, y, head.getWidth(), head.getHeight(), gameScene.mVertexBufferObjectManager);
		bodyArea.setScaleCenter(head.getWidth() * .5f, head.getHeight() * .5f);
		bodyArea.setScale(.5f);

		headBody = PhysicsFactory.createCircleBody(gameScene.getPhysicsWorld(), bodyArea, BodyType.DynamicBody, HEAD_FIXTURE_DEF);
		bodyArea.dispose();
		headBody.setUserData("wormHead");
		this.head.setUserData(headBody);
		head.setZIndex(size);
		PhysicsConnector connector = new PhysicsConnector(head, headBody, true, true);
		gameScene.getPhysicsWorld().registerPhysicsConnector(connector);

		headLink = new WormLink(null, head, headBody, connector, gameScene.getPhysicsWorld());

		this.links = new ArrayList<WormLink>();
		for(int i = 0 ; i < 4 ; i++)
			growWormBody("neck", 0, 0, NECK_FIXTURE_DEF, false);
		setRotation(90);
		pause();
		particleEmitter = new CircleParticleEmitter(GameActivity.CAMERA_WIDTH, GameActivity.CAMERA_HEIGHT, 15);
		gameScene.attachChild(getParticleSystem());
	}

	public void setRotation(float rotation)
	{
		this.rotation = rotation;
		setSquirmedRotation();
	}

	private void setSquirmedRotation()
	{
		float rotation = getSquirmedRotation();

		Vector2 direction = new Vector2();
		direction.x = (float)Math.cos(Math.toRadians(rotation));
		direction.y = (float)Math.sin(Math.toRadians(rotation));

		Vector2 velocity = new Vector2();
		velocity.x = direction.x * SPEED;
		velocity.y = direction.y * SPEED;
		headBody.setLinearVelocity(velocity.x, velocity.y);
		headBody.setTransform(headBody.getPosition(), (float)Math.toRadians(rotation));
		headBody.setActive(!pause);
	}

	private float getSquirmedRotation()
	{
		return this.rotation + this.squirmRotation;
	}

	public void growWorm(int bySize)
	{
		size += bySize;
		head.setZIndex(size);
	}

	private void growWormBody()
	{
		growWormBody("wormBody", 0, 0, BODY_FIXTURE_DEF, true);
	}

	private void growWormBody(String userData, int gapX, int gapY, FixtureDef fixtureDef, boolean playSound)
	{
		Vector2 dstPos = new Vector2(head.getX(), head.getY());
		float x = dstPos.x + gapX;
		float y = dstPos.y + gapY;
		WormLink lastLink = headLink;
		if(links.size() > 0)
		{
			lastLink = links.get(links.size() - 1);
			x = lastLink.getSprite().getX() + gapX;
			y = lastLink.getSprite().getY() + gapY;
		}

		Sprite link = null;
		if(wormBodyDelta = !wormBodyDelta)
			link = new Sprite(x, y, gameScene.mResourceManager.mWormBodyATextureRegion, gameScene.mVertexBufferObjectManager);
		else
			link = new Sprite(x, y, gameScene.mResourceManager.mWormBodyBTextureRegion, gameScene.mVertexBufferObjectManager);
		Rectangle bodyArea = new Rectangle(x, y, link.getWidth(), link.getHeight(), gameScene.mVertexBufferObjectManager);
		bodyArea.setScaleCenter(link.getWidth() * .5f, link.getHeight() * .5f);
		bodyArea.setScale(.5f);
		final Body linkBody = PhysicsFactory.createCircleBody(gameScene.getPhysicsWorld(), bodyArea, BodyType.DynamicBody, fixtureDef);
		bodyArea.dispose();
		// final Body linkBody = PhysicsFactory.createCircleBody(gameScene.getPhysicsWorld(), link, BodyType.DynamicBody, FIXTURE_DEF);
		linkBody.setUserData(userData);
		link.setUserData(linkBody);
		link.setScale(WORM_SCALE);
		PhysicsConnector connector = new PhysicsConnector(link, linkBody, true, false);
		gameScene.getPhysicsWorld().registerPhysicsConnector(connector);

		final WormLink wormLink = new WormLink(lastLink, link, linkBody, connector, gameScene.getPhysicsWorld());
		link.registerEntityModifier(
				new ParallelEntityModifier(new ScaleModifier(.75f, 0, 2, EaseElasticOut.getInstance())/* , new AlphaModifier(.350f, 0, 1) */));
		if(playSound)
		{
			for(int i = 0 ; i < 5 ; i++)
			{
				int soundId = ResourceManager.SOUND_POP_1 + (int)(3 * Math.random());
				if(ResourceManager.getInstance().playSfx(soundId))
					break;
			}
		}
		// gameScene.registerUpdateHandler(chase);
		gameScene.registerUpdateHandler(wormLink);

		links.add(wormLink);
		gameScene.attachChild(link);
		// gameScene.attachChild(getNewParticleSystem(link));//taking too much graphics memory

		for(int i = 0 ; i < links.size() ; i++)
			links.get(links.size() - 1 - i).getSprite().setZIndex(i);

		if(links.size() / 50 > 0)
		{
			if(links.size() % 25 == 0)
				if(links.size() == 50)
					ResourceManager.getInstance().playSfx(ResourceManager.SOUND_BURP_EPIC);
				else
				{
					double chance = Math.random();
					if(chance < .33d)
						ResourceManager.getInstance().playSfx(ResourceManager.SOUND_BURP);
					else
						if(chance < .66d)
							ResourceManager.getInstance().playSfx(ResourceManager.SOUND_BURP_BIG);
						else
							ResourceManager.getInstance().playSfx(ResourceManager.SOUND_BURP_EPIC);
				}
		}
	}

	private void destroyFirstLink()
	{
		WormLink link = links.get(0);
		final Sprite firstLinkSprite = link.getSprite();
		// gameScene.unregisterUpdateHandler(link.getChase());
		gameScene.mActivity.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				gameScene.detachChild(firstLinkSprite);
			}
		});
		// gameScene.unregisterUpdateHandler(link);
		link.destroyJoint(gameScene.getPhysicsWorld());
		gameScene.getPhysicsWorld().unregisterPhysicsConnector(link.getPhysicsConnector());
		gameScene.getPhysicsWorld().destroyBody(link.getLinkBody());
		firstLinkSprite.dispose();
		links.remove(link);
	}

	@Override
	public void onUpdate(float pSecondsElapsed)
	{
		if(pause)
			return;

		stepInterval += pSecondsElapsed;
		if(links.size() < size)
			growthInterval += pSecondsElapsed;

		if(stepInterval > STEP)
		// do
		{
			stepInterval -= STEP;
			if(links.size() < size)
			{
				if(growthInterval > GROWTH_PACE)
				{
					growthInterval -= GROWTH_PACE;
					growWormBody();
				}
			}
			else
				while(links.size() > size)
					destroyFirstLink();

			if(moveLeft || moveRight)
				squirmRotation = 0;
			else
				if(squirmDelta)
				{
					squirmRotation -= TURN_FACTOR * .5f * pSecondsElapsed;
					squirmDelta = !(squirmRotation < -MAX_SQUIRM);
				}
				else
				{
					squirmRotation += TURN_FACTOR * .5f * pSecondsElapsed;
					squirmDelta = squirmRotation > MAX_SQUIRM;
				}

			if(!moveLeft && !moveRight)
				setSquirmedRotation();

			if(moveLeft)
				setRotation(rotation - TURN_FACTOR * pSecondsElapsed);
			if(moveRight)
				setRotation(rotation + TURN_FACTOR * pSecondsElapsed);
		} // while(stepInterval > STEP); // the do while loop causing the worm to turn too quickly and too much when the device hangs somtimes..

		Sprite link = getBodyLinks().get(getBodyLinks().size() - 1).getSprite();
		particleEmitter.setCenter(link.getX(), link.getY());
	}

	@Override
	public void reset()
	{}

	public ArrayList<WormLink> getBodyLinks()
	{
		return this.links;
	}

	public void destroyAll()
	{
		while(links.size() > 0)
			destroyFirstLink();
	}

	public Sprite getHead()
	{
		return this.head;
	}

	public Vector2 getCenter()
	{
		Vector2 center = new Vector2();
		center.x = head.getX() + head.getWidth() * .5f;
		center.y = head.getY() + head.getHeight() * .5f;
		return center;
	}

	public void setMoveLeft(boolean moveLeft)
	{
		this.moveLeft = moveLeft;
	}

	public void setMoveRight(boolean moveRight)
	{
		this.moveRight = moveRight;
	}

	public void setPause(boolean pause)
	{
		this.pause = pause;
		headBody.setActive(!pause);
		for(WormLink wormLink : links)
			wormLink.getLinkBody().setActive(!pause);
	}

	public void pause()
	{
		setPause(true);
	}

	public void resume()
	{
		setPause(false);
	}

	public void eat()
	{
		ResourceManager.getInstance().playSfx((int)(ResourceManager.SOUND_BITE_A + ((Math.random() <= 0.5) ? 0 : 1)));
		float toScale = getHead().getScaleX();
		for(int i = 0 ; i < links.size() ; i++)
		{
			float fromScale = 4 - .1f * i;
			if(fromScale <= toScale)
				break;
			Sprite link = links.get(i).getSprite();
			link.clearEntityModifiers();
			// link.setAlpha(1);
			link.setScale(WORM_SCALE);
			link.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(.10f * i),
					new ScaleModifier(.5f, fromScale, toScale, EaseCircularOut.getInstance())));
		}
	}

	private SpriteParticleSystem particleSystem;
	private final CircleParticleEmitter particleEmitter;

	private SpriteParticleSystem getParticleSystem()
	{
		if(particleSystem == null)
		{
			particleSystem = new SpriteParticleSystem(particleEmitter, 20, 25, 150, ResourceManager.getInstance().mParticleTextureRegion,
					gameScene.mVertexBufferObjectManager);
			particleSystem.addParticleInitializer(new BlendFunctionParticleInitializer<Sprite>(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA));
			particleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(2));
			particleSystem.addParticleInitializer(new RotationParticleInitializer<Sprite>(-360, 360));
			particleSystem.addParticleInitializer(new AlphaParticleInitializer<Sprite>(.35f));
			particleSystem.addParticleInitializer(new ScaleParticleInitializer<Sprite>(.4f, 2));
			particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(1.5f, 2, .35f, 0));
		}
		return particleSystem;
	}
}