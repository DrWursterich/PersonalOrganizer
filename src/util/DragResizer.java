package util;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;

/**
 * BORROWED FROM: https://gist.github.com/hitchcock9307/b8d40576f11794c08cae783610771ea8
 * Restructured and Modified to support all side resizing (no edges)<br/>
 * {@link DragResizer} can be used to add mouse listeners to a {@link Region}
 * and make it resizable by the user by clicking and dragging the border in the
 * same way as a window.
 * <p>
 * Only height resizing is currently implemented. Usage: <pre>DragResizer.makeResizable(myAnchorPane);</pre>
 *
 * @author atill, hitchcock9307, Mario Schäper
 */
public class DragResizer {
	/**
	 * The margin around the control that a user can click in to start resizing
	 * the region.
	 */
	private static final int RESIZE_MARGIN = 5;
	private static final short NOTDRAGGING = 0;
	private static final short NORTH = 1;
	private static final short SOUTH = 2;
	private static final short EAST = 3;
	private static final short WEST = 4;
	private Region region;
	private boolean initMinHeight;
	private short dragging = NOTDRAGGING;
	private boolean draggableNorth = true;
	private boolean draggableSouth = true;
	private boolean draggableEast = true;
	private boolean draggableWest = true;
	private DoubleProperty minHeight = new SimpleDoubleProperty();
	private DoubleProperty minWidth = new SimpleDoubleProperty();
	private DoubleProperty maxHeight = new SimpleDoubleProperty();
	private DoubleProperty maxWidth = new SimpleDoubleProperty();

	private DragResizer(Region region) {
		this.setRegion(region);
	}

	public static DragResizer makeResizable(Region region) {
		return new DragResizer(region);
	}

	public static DragResizer makeResizable(Region region, boolean north, boolean east, boolean south, boolean west) {
		DragResizer ret = new DragResizer(region);
		ret.setResizeDirections(north, east, south, west);
		return ret;
	}

	public static DragResizer makeResizable(Region region, boolean north, boolean east, boolean south, boolean west,
			ObservableValue<? extends Number> minHeight, ObservableValue<? extends Number> minWidth,
			ObservableValue<? extends Number> maxHeight, ObservableValue<? extends Number> maxWidth) {
		DragResizer ret = new DragResizer(region);
		ret.setResizeDirections(north, east, south, west);
		ret.setSizes(minHeight, minWidth, maxHeight, maxWidth);
		return ret;
	}

	protected void mouseReleased(MouseEvent event) {
		this.initMinHeight = false; //Reset each time
		this.dragging = NOTDRAGGING;
		this.region.setCursor(Cursor.DEFAULT);
	}

	public void setRegion(Region region) {
		this.region = region;
		this.setSizes(this.region.minHeightProperty(), this.region.minWidthProperty(),
				this.region.maxHeightProperty(), this.region.maxWidthProperty());
		this.region.setOnMousePressed(e -> {
			this.mousePressed(e);
		});
		this.region.setOnMouseDragged(e -> {
			this.mouseDragged(e);
		});
		this.region.setOnMouseMoved(e -> {
			this.mouseOver(e);
		});
		this.region.setOnMouseReleased(e -> {
			this.mouseReleased(e);
		});
	}

	public void setResizeDirections(boolean north, boolean east, boolean south, boolean west) {
		this.draggableNorth = north;
		this.draggableEast = east;
		this.draggableSouth = south;
		this.draggableWest = west;
	}

	public void setSizes(ObservableValue<? extends Number> minHeight, ObservableValue<? extends Number> minWidth,
			ObservableValue<? extends Number> maxHeight, ObservableValue<? extends Number> maxWidth) {
		this.minHeight.bind(minHeight);
		this.minWidth.bind(minWidth);
		this.maxHeight.bind(maxHeight);
		this.maxWidth.bind(maxWidth);
	}

	protected void mouseOver(MouseEvent e) {
		if (this.draggableSouth && (isInDraggableZoneS(e) || this.dragging == SOUTH)) {
			this.region.setCursor(Cursor.S_RESIZE);
		} else if (this.draggableEast && (isInDraggableZoneE(e) || this.dragging == EAST)) {
			this.region.setCursor(Cursor.E_RESIZE);
		} else if (this.draggableNorth && (isInDraggableZoneN(e) || this.dragging == NORTH)) {
			this.region.setCursor(Cursor.N_RESIZE);
		} else if (this.draggableWest && (isInDraggableZoneW(e) || this.dragging == WEST)) {
			this.region.setCursor(Cursor.W_RESIZE);
		} else {
			this.region.setCursor(Cursor.DEFAULT);
		}
	}

	private boolean isInDraggableZoneN(MouseEvent e) {
		return e.getY() < DragResizer.RESIZE_MARGIN;
	}

	private boolean isInDraggableZoneW(MouseEvent e) {
		return e.getX() < DragResizer.RESIZE_MARGIN;
	}

	private boolean isInDraggableZoneS(MouseEvent e) {
		return e.getY() > (this.region.getHeight()-DragResizer.RESIZE_MARGIN);
	}

	private boolean isInDraggableZoneE(MouseEvent e) {
		return e.getX() > (this.region.getWidth()-DragResizer.RESIZE_MARGIN);
	}

	private void mouseDragged(MouseEvent e) {
		switch (this.dragging) {
			case (DragResizer.SOUTH):
				this.region.setMinHeight(e.getY());
				if (this.region.getMinWidth() > this.maxHeight.getValue()) {
					System.out.println("denied since " + (this.region.getMinWidth()) + " > " + this.maxWidth.getValue());
					this.region.setMinWidth(this.maxHeight.getValue());
				}
				break;
			case (DragResizer.EAST):
				this.region.setMinWidth(e.getX());
				if (this.region.getMinWidth() > this.maxWidth.getValue()) {
					this.region.setMinWidth(this.maxWidth.getValue());
				}
				break;
			case (DragResizer.NORTH):
				double prevMinHeight = this.region.getMinHeight();
				this.region.setMinHeight(this.region.getMinHeight() - e.getY());
				if (this.region.getMinHeight() < this.minHeight.getValue()) {
					this.region.setMinHeight(this.minHeight.getValue());
					this.region.setTranslateY(this.region.getTranslateY()-(this.minHeight.getValue()-prevMinHeight));
					return;
				}
				if (this.region.getMinHeight() > this.region.getPrefHeight() || e.getY() < 0) {
					this.region.setTranslateY(this.region.getTranslateY() + e.getY());
				}
				break;
			case (DragResizer.WEST):
				double prevMinWidth = this.region.getMinWidth();
				this.region.setMinWidth(this.region.getMinWidth() - e.getX());
				if (this.region.getMinWidth() < this.minWidth.getValue()) {
					this.region.setMinWidth(this.minWidth.getValue());
					this.region.setTranslateX(this.region.getTranslateX()-(this.minWidth.getValue()-prevMinWidth));
					return;
				}
				if (e.getX() < 0) {
					this.region.setTranslateX(this.region.getTranslateX()+e.getX());
				}
				break;
		}
	}

	private void mousePressed(MouseEvent e) {
		// ignore clicks outside of the draggable margin
		if (this.draggableEast && isInDraggableZoneE(e)) {
			this.dragging = DragResizer.EAST;
		} else if (this.draggableSouth && this.isInDraggableZoneS(e)) {
			this.dragging = DragResizer.SOUTH;
		} else if (this.draggableNorth && this.isInDraggableZoneN(e)) {
			this.dragging = DragResizer.NORTH;
		} else if (this.draggableWest && this.isInDraggableZoneW(e)) {
			this.dragging = DragResizer.WEST;
		} else {
			return;
		}

		// make sure that the minimum height is set to the current height once,
		// setting a min height that is smaller than the current height will
		// have no effect
		if (!this.initMinHeight) {
			this.region.setMinHeight(this.region.getHeight());
			this.region.setMinWidth(this.region.getWidth());
			this.initMinHeight = true;
		}
	}
}
