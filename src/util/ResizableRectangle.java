package util;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Offers a resizeble {@link javafx.scene.shape.Rectangle Rectangle}.<br/>
 * Initaly it is resizable in width and height.
 * @author Mario Schäper
 */
public class ResizableRectangle extends Rectangle {
	private boolean resizeWidth = true;
	private boolean resizeHeight = true;

	public ResizableRectangle() {
		super();
	}
	
	public ResizableRectangle(boolean resizeWidth, boolean resizeHeight) {
		super();
		this.resizeWidth = resizeWidth;
		this.resizeHeight = resizeHeight;
	}
	
	public ResizableRectangle(double width, double height) {
		super(width, height);
	}
	
	public ResizableRectangle(double width, double height,
			boolean resizeWidth, boolean resizeHeight) {
		super(width, height);
		this.resizeWidth = resizeWidth;
		this.resizeHeight = resizeHeight;
	}

	public ResizableRectangle(double width, double height, Paint fill) {
		super(width, height, fill);
	}

	public ResizableRectangle(double width, double height, Paint fill,
			boolean resizeWidth, boolean resizeHeight) {
		super(width, height, fill);
		this.resizeWidth = resizeWidth;
		this.resizeHeight = resizeHeight;
	}
	
	public ResizableRectangle(double x, double y, double width, double height) {
		super(x, y, width, height);
	}
	
	public ResizableRectangle(double x, double y, double width, double height,
			boolean resizeWidth, boolean resizeHeight) {
		super(x, y, width, height);
		this.resizeWidth = resizeWidth;
		this.resizeHeight = resizeHeight;
	}
	
	/**
	 * <b>Default:</b> <em>true</em>
	 * @return whether the instance should be resizable in its width
	 */
	public boolean getResizeWidth() {
		return resizeWidth;
	}

	/**
	 * <b>Default:</b> <em>true</em>
	 * @return whether the instance should be resizable in its height
	 */
	public boolean getResizeHeight() {
		return resizeHeight;
	}

	/**
	 * <b>Default:</b> <em>true</em>
	 * @param resizeWidth whether the instance should be resizable in its width
	 */
	public void setResizeWidth(boolean resizeWidth) {
		this.resizeWidth = resizeWidth;
	}

	/**
	 * <b>Default:</b> <em>true</em>
	 * @param resizeHeight whether the instance should be resizable in its height
	 */
	public void setResizeHeight(boolean resizeHeight) {
		this.resizeHeight = resizeHeight;
	}
	
	@Override
	public double minWidth(double height) {
		return this.resizeWidth ? 0 : super.minWidth(height);
	}

	@Override
	public double minHeight(double width) {
		return this.resizeHeight ? 0 : super.minHeight(width);
	}

	@Override
	public boolean isResizable() {
		return true;
	}
}
