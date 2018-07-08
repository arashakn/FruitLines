package com.makersf.andengine.extension.collisions.bindings;

import org.andengine.entity.shape.IShape;
import org.andengine.entity.shape.RectangularShape;

import com.makersf.frameworks.shared.collisioncore.pixelperfect.Transformation;


public class ShapeAdapter implements com.makersf.frameworks.shared.collisioncore.pixelperfect.IShape {

	private final RectangularShape mShape;

	public ShapeAdapter(RectangularShape pShape) {
		mShape = pShape;
	}

	@Override
	public Transformation getLocalToSceneTransformation() {
		return TransformationAdapter.adapt(mShape.getLocalToSceneTransformation());
	}

	@Override
	public Transformation getSceneToLocalTransformation() {
		return TransformationAdapter.adapt(mShape.getSceneToLocalTransformation());
	}

	@Override
	public float getWidth() {
		return mShape.getWidth();
	}

	@Override
	public float getHeight() {
		return mShape.getHeight();
	}

}
