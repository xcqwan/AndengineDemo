package com.zombie.gamedemo;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import android.graphics.Point;
import android.view.Display;

public class MainActivity extends SimpleBaseGameActivity {
	
	private static int CAMERA_WIDTH = 720;
	private static int CAMERA_HEIGHT = 480;
	
	private static final float BALL_VELOCITY = 200.0f;
	
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mBallTextureRegion;

	@Override
	public EngineOptions onCreateEngineOptions() {
		Display display = getWindowManager().getDefaultDisplay();
		Point screen = new Point();
		display.getSize(screen);
		
		CAMERA_WIDTH = screen.x;
		CAMERA_HEIGHT = screen.y;
		
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("img/");
		
		mBitmapTextureAtlas = new BitmapTextureAtlas(getTextureManager(), 128, 128, TextureOptions.DEFAULT);
		mBallTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, this, "chromatic_circle.png", 0, 0, 1, 1);
		mBitmapTextureAtlas.load();
	}

	@Override
	protected Scene onCreateScene() {
		mEngine.registerUpdateHandler(new FPSLogger());
		
		final Scene scene = new Scene();
		scene.setBackground(new Background(Color.PINK));
		
		final float centerX = (CAMERA_WIDTH - mBallTextureRegion.getWidth()) / 2;
		final float centerY = (CAMERA_HEIGHT - mBallTextureRegion.getHeight()) / 2;
		final Ball ball = new Ball(centerX, centerY, mBallTextureRegion, getVertexBufferObjectManager());
		
		scene.attachChild(ball);
		
		return scene;
	}
	
	private static class Ball extends AnimatedSprite {
		private final PhysicsHandler mPhysicsHandler;
		
		public Ball(final float pX, final float pY, final TiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
			super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
			
			mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(mPhysicsHandler);
			mPhysicsHandler.setVelocity(BALL_VELOCITY, BALL_VELOCITY);
		}
		
		@Override
		protected void onManagedUpdate(float pSecondsElapsed) {
			if (mX < 0) {
				mPhysicsHandler.setVelocityX(BALL_VELOCITY);
			} else if ((mX + this.getWidth()) > CAMERA_WIDTH) {
				mPhysicsHandler.setVelocityX(-BALL_VELOCITY);
			}
			
			if (mY < 0) {
				mPhysicsHandler.setVelocityY(BALL_VELOCITY);
			} else if ((mY + this.getHeight()) > CAMERA_HEIGHT) {
				mPhysicsHandler.setVelocityY(-BALL_VELOCITY);
			}
			
			super.onManagedUpdate(pSecondsElapsed);
		}
	}
}
