package org.andengine.opengl.texture.atlas.bitmap.source;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.opengl.texture.atlas.source.BaseTextureAtlasSource;
import org.andengine.util.StreamUtils;
import org.andengine.util.debug.Debug;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

import com.lib.util.Base64;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 12:07:52 - 09.03.2010
 */
public class AssetBitmapTextureAtlasSource extends BaseTextureAtlasSource implements IBitmapTextureAtlasSource {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final AssetManager mAssetManager;
	private final String mAssetPath;
	private static final String encode = ".enc";
 	// ===========================================================
	// Constructors
	// ===========================================================

	public static AssetBitmapTextureAtlasSource create(final AssetManager pAssetManager, final String pAssetPath) {
		
		return AssetBitmapTextureAtlasSource.create(pAssetManager, pAssetPath, 0, 0);
	}


	public static AssetBitmapTextureAtlasSource create(final AssetManager pAssetManager, final String pAssetPath, final int pTextureX, final int pTextureY) {
		final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		decodeOptions.inJustDecodeBounds = true;
		InputStream in = null;
		if(pAssetPath.endsWith(encode)){
			try {
				in = pAssetManager.open(pAssetPath);
				byte[] buffer = new byte[in.available()];
				in.read(buffer);
				buffer = Base64.decode(buffer);
				BitmapFactory.decodeByteArray(buffer, 0, buffer.length, decodeOptions);
			} catch (final IOException e) {
				Debug.e("Failed loading Bitmap in AssetBitmapTextureAtlasSource. AssetPath: " + pAssetPath, e);
			} finally {
				StreamUtils.close(in);
			}
		}
		else{
			try {
				in = pAssetManager.open(pAssetPath);
				BitmapFactory.decodeStream(in, null, decodeOptions);
			} catch (final IOException e) {
				Debug.e("Failed loading Bitmap in AssetBitmapTextureAtlasSource. AssetPath: " + pAssetPath, e);
			} finally {
				StreamUtils.close(in);
			}
		}

		

		return new AssetBitmapTextureAtlasSource(pAssetManager, pAssetPath, pTextureX, pTextureY, decodeOptions.outWidth, decodeOptions.outHeight);
	}

	

	
	AssetBitmapTextureAtlasSource(final AssetManager pAssetManager, final String pAssetPath, final int pTextureX, final int pTextureY, final int pTextureWidth, final int pTextureHeight) {
		super(pTextureX, pTextureY, pTextureWidth, pTextureHeight);

		this.mAssetManager = pAssetManager;
		this.mAssetPath = pAssetPath;
	}

	@Override
	public AssetBitmapTextureAtlasSource deepCopy() {
		return new AssetBitmapTextureAtlasSource(this.mAssetManager, this.mAssetPath, this.mTextureX, this.mTextureY, this.mTextureWidth, this.mTextureHeight);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Bitmap onLoadBitmap(final Config pBitmapConfig) {
		InputStream in = null;
		Bitmap bitmap = null;
		try {
			final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
			if(mAssetPath.endsWith(encode)){				
				in = mAssetManager.open(mAssetPath);
				byte[] buffer = new byte[in.available()];
				in.read(buffer);
				buffer = Base64.decode(buffer);
				decodeOptions.inPreferredConfig = pBitmapConfig;
				bitmap =  BitmapFactory.decodeByteArray(buffer, 0, buffer.length, decodeOptions);
			}
			else{
				in = this.mAssetManager.open(this.mAssetPath);
				bitmap = BitmapFactory.decodeStream(in, null, decodeOptions);
			}
			return bitmap;
			
		} catch (final Exception e) {
			Debug.e("Failed loading Bitmap in " + this.getClass().getSimpleName() + ". AssetPath: " + this.mAssetPath, e);
			return null;
		} finally {
			StreamUtils.close(in);
		}
		

	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + this.mAssetPath + ")";
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}