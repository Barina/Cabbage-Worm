package barinadroid.cabbageworm.engine.control;

import java.util.ArrayList;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.TextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import android.graphics.Color;
import android.graphics.Typeface;
import barinadroid.cabbageworm.GameActivity;
import barinadroid.cabbageworm.tools.LogWorm;

public class ResourceManager
{
	private static final ResourceManager INSTANCE = new ResourceManager();
	public static final int ALL_SFX = -1;
	public static final int ALL_SOUNDS = -2;
	public static final int ALL_MUSICS = -3;
	public static final int SOUND_SCORE = 0;
	public static final int MUSIC_START = 1;
	public static final int MUSIC_LOOP = 2;
	public static final int SOUND_GAME_OVER = 3;
	public static final int SOUND_POP_1 = 4;
	public static final int SOUND_POP_2 = 5;
	public static final int SOUND_POP_3 = 6;
	public static final int SOUND_BITE_A = 7;
	public static final int SOUND_BITE_B = 8;
	public static final int SOUND_CABBAGE_GROWTH_A = 9;
	public static final int SOUND_CABBAGE_GROWTH_B = 10;
	public static final int SOUND_CABBAGE_GROWTH_C = 11;
	public static final int MUSIC_BACKGROUND_START = 12;
	public static final int MUSIC_BACKGROUND_LOOP = 13;
	public static final int SOUND_BURP = 15;
	public static final int SOUND_BURP_BIG = 16;
	public static final int SOUND_BURP_EPIC = 17;
	public static final int SOUND_POWERUP_APPEAR = 18;
	public static final int SOUND_POWERUP_DISAPPEAR = 19;
	public static final int SOUND_POWERUP_EATEN = 20;
	public static final int SOUND_POWERUP_GUI_APPEAR = 21;
	public static final int SOUND_POWERUP_GUI_DISAPPEAR = 22;
	public static final int SOUND_ROCK_APPEAR = 23;
	public static final int SOUND_ROCK_DISAPPEAR = 24;
	public static final int SOUND_ROCK_EATEN = 25;

	public GameActivity mActivity;
	private BitmapTextureAtlas mSplashTextureAtlas;
	public ITextureRegion mSplashTextureRegion;
	public Font mSplashFont;

	private ResourceManager()
	{}

	public static ResourceManager getInstance()
	{
		return INSTANCE;
	}

	/**
	 * Prepare {@link ResourceManager} to load and unload resources to\from memory.
	 * 
	 * @param activity
	 *            The {@link GameActivity} that will use the resources.
	 */
	public void prepare(GameActivity activity)
	{
		INSTANCE.mActivity = activity;
	}

	/**
	 * Load all splash screen scene resources to memory.
	 */
	public void loadSplashResources()
	{
		LogWorm.i("Loading splashScene resources.");
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		mSplashTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 1920, 1080, TextureOptions.BILINEAR);
		mSplashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mSplashTextureAtlas, mActivity, "background_menu.png", 0, 0);
		mSplashTextureAtlas.load();
		mSplashFont = FontFactory.createStroke(mActivity.getFontManager(), new BitmapTextureAtlas(mActivity.getTextureManager(), 256, 256),
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 40, true, Color.WHITE, 1, Color.BLACK);
		mSplashFont.load();
		LogWorm.i("Done loading splashScene resources.");
	}

	/**
	 * Unload splash screen resources from memory.
	 */
	public void unloadSplashResources()
	{
		LogWorm.i("Unloading splashScene resources.");
		unloadTextureAtlases(mSplashTextureAtlas);
		mSplashTextureRegion = null;
		unloadFonts(mSplashFont);
		mSplashFont = null;
		LogWorm.i("Done unloading splashScene resources.");
	}

	private BitmapTextureAtlas mBackgroundTextureAtlas;
	public ITextureRegion mBackgroundTextureRegion;

	private BitmapTextureAtlas mMenuBackgroundTextureAtlas;
	public ITextureRegion mMenuBackgroundTextureRegion;

	private BitmapTextureAtlas mBackgroundExtraTextureAtlas;
	public ITextureRegion mMenuGrassBackgroundTextureRegion;
	public ITextureRegion mParticleTextureRegion;

	private BitmapTextureAtlas mWormBitmapTextureAtlas;
	public TiledTextureRegion mWormHeadTextureRegion;
	public ITextureRegion mWormBodyATextureRegion;
	public ITextureRegion mWormBodyBTextureRegion;

	private BitmapTextureAtlas mCabbageAnimBitmapTextureAtlas;
	public ITiledTextureRegion mCabbageAnimTiledTextureRegion;

	private BitmapTextureAtlas mCabbageBitmapTextureAtlas;
	public ITextureRegion mCabbageTextureRegion;
	public ITextureRegion mCabbageLeafATextureRegion;
	public ITextureRegion mCabbageMedium1TextureRegion;
	public ITextureRegion mCabbageMedium2TextureRegion;

	private BitmapTextureAtlas mCabbageBigBitmapTextureAtlas;
	public ITextureRegion mCabbageBigTextureRegion;

	private BitmapTextureAtlas mRockBitmapTextureAtlas;
	public ITextureRegion mRockTextureRegion;

	private BitmapTextureAtlas mHandBitmapTextureAtlas;
	public ITiledTextureRegion mHandTiledTextureRegion;

	private BitmapTextureAtlas mHelpBitmapTextureAtlas;
	public ITextureRegion mWindowTextureRegion;
	public ITextureRegion mHelpWormNaturalTextureRegion;
	public ITextureRegion mHelpWormLeftTextureRegion;

	private BitmapTextureAtlas mBigWormBitmapTextureAtlas;
	public ITextureRegion mBigWormTextureRegion;

	private BitmapTextureAtlas mLogoBitmapTextureAtlas;
	public ITextureRegion mLogoTextureRegion;

	private BitmapTextureAtlas mButtonsBitmapTextureAtlas;
	public ITiledTextureRegion mPlayButtonTiledTextureRegion;
	public ITiledTextureRegion mQuitButtonTiledTextureRegion;
	public ITiledTextureRegion mPlayAgainButtonTiledTextureRegion;

	private BitmapTextureAtlas mDoubleBitmapTextureAtlas;
	public ITiledTextureRegion mDoubleTiledTextureRegion;

	private BitmapTextureAtlas mPowerupBitmapTextureAtlas;
	public ITiledTextureRegion mPowerupTiledTextureRegion;

	public Font mMenuFont;
	public Font mDetailsFont;
	public Font mBigFont;
	public Font mTitleFont;

	private SoundHolder mScoreSound;
	private SoundHolder mGameOverSound;
	private SoundHolder mPop1Sound;
	private SoundHolder mPop2Sound;
	private SoundHolder mPop3Sound;
	private SoundHolder mBiteASound;
	private SoundHolder mBiteBSound;
	private SoundHolder mCabbageGrowthASound;
	private SoundHolder mCabbageGrowthBSound;
	private SoundHolder mCabbageGrowthCSound;
	private SoundHolder mBurpSound;
	private SoundHolder mBurpBigSound;
	private SoundHolder mBurpEpicSound;

	private SoundHolder mPowerupAppearSound;
	private SoundHolder mPowerupDisappearSound;
	private SoundHolder mPowerupEatenSound;
	private SoundHolder mPowerupGuiAppearSound;
	private SoundHolder mPowerupGuiDisappearSound;
	private SoundHolder mRockAppearSound;
	private SoundHolder mRockDisappearSound;
	private SoundHolder mRockEatenSound;
	private ArrayList<AudioHolder> mSounds;

	private MusicHolder mMusicStart;
	private MusicHolder mMusicLoop;
	private MusicHolder mMusicBackgroundStart;
	private MusicHolder mMusicBackgroundLoop;
	private ArrayList<AudioHolder> mMusics;

	/**
	 * Load all game resources to memory.
	 */
	public void loadGameResources()
	{
		LogWorm.i("Loading game resources.");
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		mBackgroundTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 1920, 1080);
		mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBackgroundTextureAtlas, mActivity, "background.png", 0, 0);
		mBackgroundTextureAtlas.load();

		mMenuBackgroundTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 1920, 1080);
		mMenuBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBackgroundTextureAtlas, mActivity,
				"background_menu.png", 0, 0);
		mMenuBackgroundTextureAtlas.load();

		mBackgroundExtraTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 1920, 501);
		mMenuGrassBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBackgroundExtraTextureAtlas, mActivity,
				"background_menu_grass.png", 0, 0);
		mParticleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBackgroundExtraTextureAtlas, mActivity, "particle.png", 0,
				471);
		mBackgroundExtraTextureAtlas.load();

		mWormBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 48, 48);
		mWormHeadTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mWormBitmapTextureAtlas, mActivity, "head.png", 0, 0, 2,
				1);
		mWormBodyATextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mWormBitmapTextureAtlas, mActivity, "body_a.png", 0, 24);
		mWormBodyBTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mWormBitmapTextureAtlas, mActivity, "body_b.png", 24, 24);
		mWormBitmapTextureAtlas.load();

		mCabbageAnimBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 1020, 1020);
		mCabbageAnimTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mCabbageAnimBitmapTextureAtlas, mActivity,
				"cabbage_anim.png", 0, 0, 5, 5);
		mCabbageAnimBitmapTextureAtlas.load();

		mCabbageBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 1024, 512 + 256);
		mCabbageTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mCabbageBitmapTextureAtlas, mActivity, "cabbage.png", 0, 512);
		mCabbageLeafATextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mCabbageBitmapTextureAtlas, mActivity,
				"cabbage_leaf_a.png", 251, 512);
		mCabbageMedium1TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mCabbageBitmapTextureAtlas, mActivity,
				"cabbage_medium_1.png", 0, 0);
		mCabbageMedium2TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mCabbageBitmapTextureAtlas, mActivity,
				"cabbage_medium_2.png", 512, 0);
		mCabbageBitmapTextureAtlas.load();

		mCabbageBigBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 1023, 998);
		mCabbageBigTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mCabbageBigBitmapTextureAtlas, mActivity, "cabbage_big.png",
				0, 0);
		mCabbageBigBitmapTextureAtlas.load();

		mRockBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 256, 225);
		mRockTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mRockBitmapTextureAtlas, mActivity, "rock.png", 0, 0);
		mRockBitmapTextureAtlas.load();

		mHandBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 256, 127);
		mHandTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mHandBitmapTextureAtlas, mActivity, "hand.png", 0, 0, 2,
				1);
		mHandBitmapTextureAtlas.load();

		mHelpBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 1024, 512);
		mWindowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mHelpBitmapTextureAtlas, mActivity, "window_bg.png", 0, 0);
		mHelpWormNaturalTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mHelpBitmapTextureAtlas, mActivity,
				"help_worm_natural.png", 704, 0);
		mHelpWormLeftTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mHelpBitmapTextureAtlas, mActivity, "help_worm_left.png",
				704, 128);
		mHelpBitmapTextureAtlas.load();

		mBigWormBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 368, 290);
		mBigWormTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBigWormBitmapTextureAtlas, mActivity, "worm_big.png", 0, 0);
		mBigWormBitmapTextureAtlas.load();

		mLogoBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 334, 182);
		mLogoTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mLogoBitmapTextureAtlas, mActivity, "mainmenu_title.png", 0, 0);
		mLogoBitmapTextureAtlas.load();

		mButtonsBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 415, 726);
		mPlayButtonTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mButtonsBitmapTextureAtlas, mActivity,
				"play_button.png", 0, 0, 1, 2);
		mQuitButtonTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mButtonsBitmapTextureAtlas, mActivity,
				"quit_button.png", 0, 242, 1, 2);
		mPlayAgainButtonTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mButtonsBitmapTextureAtlas, mActivity,
				"play_again_button.png", 0, 484, 1, 2);
		mButtonsBitmapTextureAtlas.load();

		mDoubleBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 320, 256);
		mDoubleTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mDoubleBitmapTextureAtlas, mActivity,
				"double_anim_gui.png", 0, 0, 5, 4);
		mDoubleBitmapTextureAtlas.load();

		mPowerupBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 1024, 768);
		mPowerupTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mPowerupBitmapTextureAtlas, mActivity,
				"double_point_anim.png", 0, 0, 8, 6);
		mPowerupBitmapTextureAtlas.load();

		FontFactory.setAssetBasePath("font/");
		ITexture fancyFontTexture = new BitmapTextureAtlas(mActivity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		mTitleFont = FontFactory.createStrokeFromAsset(mActivity.getFontManager(), fancyFontTexture, mActivity.getAssets(), "DSChocolade.ttf", 72,
				true, Color.WHITE, 1, Color.DKGRAY);
		mTitleFont.load();

		ITexture bigFontTexture = new BitmapTextureAtlas(mActivity.getTextureManager(), 512, 256);
		mBigFont = FontFactory.createStroke(mActivity.getFontManager(), bigFontTexture, Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD), 72,
				true, Color.WHITE, 2, Color.BLACK);
		mBigFont.load();

		ITexture menuFontTexture = new BitmapTextureAtlas(mActivity.getTextureManager(), 256, 256);
		mMenuFont = FontFactory.createStroke(mActivity.getFontManager(), menuFontTexture, Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD), 64,
				true, Color.WHITE, 2, Color.DKGRAY);
		mMenuFont.load();

		ITexture detailsFontTexture = new BitmapTextureAtlas(mActivity.getTextureManager(), 256, 256);
		mDetailsFont = FontFactory.createStroke(mActivity.getFontManager(), detailsFontTexture, Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL),
				48, true, Color.WHITE, 1, Color.BLACK);
		mDetailsFont.load();

		// ITexture fontTexture5 = new BitmapTextureAtlas(mActivity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		// mFont5 = FontFactory.createStrokeFromAsset(mActivity.getFontManager(), fontTexture5, mActivity.getAssets(), "Font1.ttf", 36, true, Color.WHITE, 2,
		// Color.DKGRAY);
		// mFont5.load();

		SoundFactory.setAssetBasePath("sfx/");
		MusicFactory.setAssetBasePath("sfx/");

		mScoreSound = new SoundHolder(mActivity, "sound_score.ogg");
		mGameOverSound = new SoundHolder(mActivity, "sound_game_over.ogg");
		mPop1Sound = new SoundHolder(mActivity, "sound_pop_1.ogg");
		mPop2Sound = new SoundHolder(mActivity, "sound_pop_2.ogg");
		mPop3Sound = new SoundHolder(mActivity, "sound_pop_3.ogg");

		mBiteASound = new SoundHolder(mActivity, "sound_bite_a.ogg");
		mBiteBSound = new SoundHolder(mActivity, "sound_bite_b.ogg");

		mCabbageGrowthASound = new SoundHolder(mActivity, "sound_cabbage_growth_a.ogg");
		mCabbageGrowthBSound = new SoundHolder(mActivity, "sound_cabbage_growth_b.ogg");
		mCabbageGrowthCSound = new SoundHolder(mActivity, "sound_cabbage_growth_c.ogg");

		mBurpSound = new SoundHolder(mActivity, "burp.ogg");
		mBurpBigSound = new SoundHolder(mActivity, "burp_big.ogg");
		mBurpEpicSound = new SoundHolder(mActivity, "burp_epic.ogg");

		mPowerupAppearSound = new SoundHolder(mActivity, "powerup_appear.ogg");
		mPowerupDisappearSound = new SoundHolder(mActivity, "powerup_disappear.ogg");
		mPowerupEatenSound = new SoundHolder(mActivity, "powerup_eaten.ogg");
		mPowerupGuiAppearSound = new SoundHolder(mActivity, "powerup_gui_appear.ogg");
		mPowerupGuiDisappearSound = new SoundHolder(mActivity, "powerup_gui_disappear.ogg");
		mRockAppearSound = new SoundHolder(mActivity, "rock_appear.ogg");
		mRockDisappearSound = new SoundHolder(mActivity, "rock_disappear.ogg");
		mRockEatenSound = new SoundHolder(mActivity, "rock_eaten.ogg");

		mSounds = new ArrayList<AudioHolder>();
		mSounds.add(mScoreSound);
		mSounds.add(mGameOverSound);
		mSounds.add(mPop1Sound);
		mSounds.add(mPop2Sound);
		mSounds.add(mPop3Sound);
		mSounds.add(mBiteASound);
		mSounds.add(mBiteBSound);
		mSounds.add(mCabbageGrowthASound);
		mSounds.add(mCabbageGrowthBSound);
		mSounds.add(mCabbageGrowthCSound);
		mSounds.add(mBurpSound);
		mSounds.add(mBurpBigSound);
		mSounds.add(mBurpEpicSound);
		mSounds.add(mPowerupAppearSound);
		mSounds.add(mPowerupDisappearSound);
		mSounds.add(mPowerupEatenSound);
		mSounds.add(mPowerupGuiAppearSound);
		mSounds.add(mPowerupGuiDisappearSound);
		mSounds.add(mRockAppearSound);
		mSounds.add(mRockDisappearSound);
		mSounds.add(mRockEatenSound);

		mMusicLoop = new MusicHolder(mActivity, "music_loop.ogg", 1);
		mMusicStart = new MusicHolder(mActivity, "music_start.ogg", 1, mMusicLoop);
		mMusicBackgroundLoop = new MusicHolder(mActivity, "menu_background_music_loop.ogg");
		mMusicBackgroundStart = new MusicHolder(mActivity, "menu_background_music_start.ogg", mMusicBackgroundLoop);

		mMusics = new ArrayList<AudioHolder>();
		mMusics.add(mMusicLoop);
		mMusics.add(mMusicStart);
		mMusics.add(mMusicBackgroundLoop);
		mMusics.add(mMusicBackgroundStart);

		LogWorm.i("Done loading game resources.");
	}

	/**
	 * Unload every game resources.
	 * Including {@link TextureAtlas}, {@link Texture}, {@link Font} and {@link AudioHolder}.
	 */
	public void unloadGameResources()
	{
		LogWorm.i("Unloading game resources.");
		unloadTextureAtlases(mBackgroundTextureAtlas, mMenuBackgroundTextureAtlas, mBackgroundExtraTextureAtlas, mWormBitmapTextureAtlas,
				mCabbageAnimBitmapTextureAtlas, mCabbageBitmapTextureAtlas, mCabbageBigBitmapTextureAtlas, mRockBitmapTextureAtlas,
				mHandBitmapTextureAtlas, mHelpBitmapTextureAtlas, mBigWormBitmapTextureAtlas, mLogoBitmapTextureAtlas, mButtonsBitmapTextureAtlas,
				mDoubleBitmapTextureAtlas, mPowerupBitmapTextureAtlas);

		mBackgroundTextureAtlas = null;
		mMenuBackgroundTextureRegion = null;
		mMenuBackgroundTextureAtlas = null;
		mBackgroundTextureRegion = null;
		mBackgroundExtraTextureAtlas = null;
		mMenuGrassBackgroundTextureRegion = null;
		mParticleTextureRegion = null;

		mWormBitmapTextureAtlas = null;
		mWormHeadTextureRegion = null;
		mWormBodyATextureRegion = null;
		mWormBodyBTextureRegion = null;

		mCabbageAnimBitmapTextureAtlas = null;
		mCabbageAnimTiledTextureRegion = null;

		mCabbageBitmapTextureAtlas = null;
		mCabbageTextureRegion = null;
		mCabbageLeafATextureRegion = null;
		mCabbageMedium1TextureRegion = null;
		mCabbageMedium2TextureRegion = null;

		mCabbageBigBitmapTextureAtlas = null;
		mCabbageBigTextureRegion = null;

		mRockBitmapTextureAtlas = null;
		mRockTextureRegion = null;

		mHandBitmapTextureAtlas = null;
		mHandTiledTextureRegion = null;

		mHelpBitmapTextureAtlas = null;
		mWindowTextureRegion = null;
		mHelpWormNaturalTextureRegion = null;
		mHelpWormLeftTextureRegion = null;

		mBigWormBitmapTextureAtlas = null;
		mBigWormTextureRegion = null;

		mLogoBitmapTextureAtlas = null;
		mLogoTextureRegion = null;

		mButtonsBitmapTextureAtlas = null;
		mPlayButtonTiledTextureRegion = null;
		mQuitButtonTiledTextureRegion = null;
		mPlayAgainButtonTiledTextureRegion = null;

		mDoubleBitmapTextureAtlas = null;
		mDoubleTiledTextureRegion = null;

		mPowerupBitmapTextureAtlas = null;
		mPowerupTiledTextureRegion = null;

		unloadFonts(mBigFont, mMenuFont, mDetailsFont, mTitleFont);
		mBigFont = null;
		mMenuFont = null;
		mDetailsFont = null;
		mTitleFont = null;

		// stopSfx(ALL_SFX);

		unloadAudio(mSounds);
		mScoreSound = null;
		mGameOverSound = null;
		mPop1Sound = null;
		mPop2Sound = null;
		mPop3Sound = null;
		mBiteASound = null;
		mBiteBSound = null;
		mCabbageGrowthASound = null;
		mCabbageGrowthBSound = null;
		mCabbageGrowthCSound = null;
		mBurpSound = null;
		mBurpBigSound = null;
		mBurpEpicSound = null;
		mPowerupAppearSound = null;
		mPowerupDisappearSound = null;
		mPowerupEatenSound = null;
		mPowerupGuiAppearSound = null;
		mPowerupGuiDisappearSound = null;
		mRockAppearSound = null;
		mRockDisappearSound = null;
		mRockEatenSound = null;
		mSounds.clear();
		mSounds = null;

		unloadAudio(mMusics);
		mMusicStart = null;
		mMusicLoop = null;
		mMusicBackgroundStart = null;
		mMusicBackgroundLoop = null;
		mMusics.clear();
		mMusics = null;

		LogWorm.i("Done unloading game resources.");
	}

	/**
	 * Unload a given {@link TextureAtlas} from memory.
	 * 
	 * @param textures
	 *            A list containing all the Textures to unload.
	 */
	private void unloadTextureAtlases(BitmapTextureAtlas... textures)
	{
		for(BitmapTextureAtlas atlas : textures)
			try
			{
				if(atlas != null)
					atlas.unload();
			}
			catch(Exception e)
			{
				LogWorm.w("Can't unload BitmapTextureAtlas for some reason.", e);
			}
	}

	/**
	 * Unload font resources.
	 * 
	 * @param fonts
	 *            A list of all the fonts to unload.
	 */
	private void unloadFonts(Font... fonts)
	{
		for(Font font : fonts)
			try
			{
				if(font != null)
					font.unload();
			}
			catch(Exception e)
			{
				LogWorm.w("Can't unload Font for some reason.", e);
			}
	}

	/**
	 * Will try to unload audio resources.
	 * 
	 * @param holders
	 *            An {@link ArrayList} containing all the {@link AudioHolder}s to unload.
	 */
	private void unloadAudio(ArrayList<AudioHolder> holders)
	{
		for(AudioHolder holder : holders)
			holder.unload();
	}

	/**
	 * Play a sound.
	 * 
	 * @param sfxId
	 *            The ID of the desired sound. Can be retrieved from {@link ResourceManager}.
	 *            Does not support batch calls.
	 * @return If there were a sound file loaded and the play call was initiated.
	 */
	public boolean playSfx(int sfxId)
	{
		AudioHolder sfx = getAudioHolderById(sfxId);
		if(sfx != null)
		{
			sfx.play();
			return true;
		}
		return false;
	}

	/**
	 * Stop sounds.
	 * For some objects it will not actually stop it but rather pause it and seek to 0.
	 * 
	 * @param sfxId
	 *            The ID of the sound effect to stop.
	 */
	public void stopSfx(int sfxId)
	{
		stopSfx(false, sfxId);
	}

	/**
	 * Stop sounds.
	 * For some objects it will not actually stop it but rather pause it and seek to 0.
	 * 
	 * @param sfxId
	 *            The ID of the sound effect to stop.
	 * @param force
	 *            Stop the music immediately. Will only work on Musics now.
	 */
	public void stopSfx(boolean force, int sfxId)
	{
		switch (sfxId)
		{
			case ALL_SOUNDS:
				stopAll(force, mSounds);
				break;

			case ALL_MUSICS:
				stopAll(force, mMusics);
				break;

			case ALL_SFX:
				stopSfx(force, ALL_SOUNDS);
				stopSfx(force, ALL_MUSICS);
				break;

			default:
				stopAll(force, getAudioHolderById(sfxId));
				break;
		}
	}

	/**
	 * Get the {@link AudioHolder} holding the desired sound.
	 * 
	 * @param sfxId
	 *            The holder id.
	 * @return The AudioHolder.
	 */
	private AudioHolder getAudioHolderById(int sfxId)
	{
		switch (sfxId)
		{
			case SOUND_SCORE:
				return mScoreSound;

			case SOUND_POP_1:
				return mPop1Sound;

			case SOUND_POP_2:
				return mPop2Sound;

			case SOUND_POP_3:
				return mPop3Sound;

			case SOUND_BITE_A:
				return mBiteASound;

			case SOUND_BITE_B:
				return mBiteBSound;

			case SOUND_CABBAGE_GROWTH_A:
				return mCabbageGrowthASound;

			case SOUND_CABBAGE_GROWTH_B:
				return mCabbageGrowthBSound;

			case SOUND_CABBAGE_GROWTH_C:
				return mCabbageGrowthCSound;

			case SOUND_BURP:
				return mBurpSound;

			case SOUND_BURP_BIG:
				return mBurpBigSound;

			case SOUND_BURP_EPIC:
				return mBurpEpicSound;

			case SOUND_GAME_OVER:
				return mGameOverSound;

			case SOUND_POWERUP_APPEAR:
				return mPowerupAppearSound;

			case SOUND_POWERUP_DISAPPEAR:
				return mPowerupDisappearSound;

			case SOUND_POWERUP_EATEN:
				return mPowerupEatenSound;

			case SOUND_POWERUP_GUI_APPEAR:
				return mPowerupGuiAppearSound;

			case SOUND_POWERUP_GUI_DISAPPEAR:
				return mPowerupGuiDisappearSound;

			case SOUND_ROCK_APPEAR:
				return mRockAppearSound;

			case SOUND_ROCK_DISAPPEAR:
				return mRockDisappearSound;

			case SOUND_ROCK_EATEN:
				return mRockEatenSound;

			case MUSIC_START:
				return mMusicStart;

			case MUSIC_LOOP:
				return mMusicLoop;

			case MUSIC_BACKGROUND_START:
				return mMusicBackgroundStart;

			case MUSIC_BACKGROUND_LOOP:
				return mMusicBackgroundLoop;

			default:
				return null;
		}
	}

	/**
	 * Will try to pause every {@link AudioHolder} within a list.
	 * 
	 * @param holders
	 *            The list containing all the holders ({@link SoundHolder}s and {@link MusicHolder}s) to pause.
	 */
	private void pauseAll(ArrayList<AudioHolder> holders)
	{
		if(holders != null)
			for(AudioHolder holder : holders)
				if(holder != null)
					holder.pause();
	}

	/**
	 * Will try to resume every {@link AudioHolder} within a list.
	 * 
	 * @param holders
	 *            The list containing all the holders ({@link SoundHolder}s and {@link MusicHolder}s) to resume.
	 */
	private void resumeAll(ArrayList<AudioHolder> holders)
	{
		if(holders != null)
			for(AudioHolder holder : holders)
				if(holder != null)
					holder.resume();
	}

	/**
	 * Will try to stop every {@link AudioHolder} within an array.
	 * 
	 * @param force
	 *            Whether stop it immediately. (Will work only with {@link MusicHolder}s)
	 * @param holders
	 *            The 'params' array containing all the holders ({@link SoundHolder}s and {@link MusicHolder}s) to stop.
	 */
	private void stopAll(boolean force, AudioHolder... holders)
	{
		if(holders != null)
			for(AudioHolder holder : holders)
				if(holder != null)
					holder.stop(force);
	}

	/**
	 * Will try to stop every {@link AudioHolder} within a list.
	 * 
	 * @param force
	 *            Whether stop it immediately. (Will work only with {@link MusicHolder}s)
	 * @param holders
	 *            The list containing all the holders ({@link SoundHolder}s and {@link MusicHolder}s) to stop.
	 */
	private void stopAll(boolean force, ArrayList<AudioHolder> holders)
	{
		if(holders != null)
			for(AudioHolder holder : holders)
				stopAll(force, holder);
	}

	/**
	 * Pause all playing audio.
	 */
	public void pause()
	{
		pauseAll(mSounds);
		pauseAll(mMusics);
	}

	/**
	 * Resume all 'paused' audio.
	 */
	public void resume()
	{
		resumeAll(mSounds);
		resumeAll(mMusics);
	}
}