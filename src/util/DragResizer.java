package util;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;

/**
 * BORROWED FROM: <a href="https://gist.github.com/hitchcock9307/b8d40576f11794c08cae783610771ea8">
 * github/hitchcock9307/DragResizer</a>
 * Restructured and Modified to Support Minimal, Maximal and Direction Settings.<br/><br/>
 * This Class can be used to add Mouse-Listeners to a {@link Region}
 * and make it Resizable by the user by Clicking and Dragging the Bborder in the
 * same Way as a Window.
 * @author atill, hitchcock9307, Mario Schäper
 */
public class DragResizer {
	/**
	 * The margin around the control that a user can click in to start resizing
	 * the region.
	 */
	private static final byte RESIZE_MARGIN = 5;
	private static final byte NOTDRAGGING = 0;
	private static final byte NORTH = 1;
	private static final byte SOUTH = 2;
	private static final byte EAST = 3;
	private static final byte WEST = 4;
	private boolean draggableNorth = true;
	private boolean draggableSouth = true;
	private boolean draggableEast = true;
	private boolean draggableWest = true;
	private ObservableValue<? extends Number> minHeight;
	private ObservableValue<? extends Number> minWidth;
	private ObservableValue<? extends Number> maxHeight;
	private ObservableValue<? extends Number> maxWidth;
	private short dragging = NOTDRAGGING;
	private boolean initMinHeight;
	private Region region;

	private DragResizer(Region region) {
		this.setRegion(region);
		this.setSizes(new SimpleDoubleProperty(), new SimpleDoubleProperty(),
				new SimpleDoubleProperty(), new SimpleDoubleProperty());
	}

	private DragResizer(Region region, boolean north, boolean east, boolean south, boolean west) {
		this(region);
		this.setResizeDirections(north, east, south, west);
	}

	private DragResizer(Region region, boolean north, boolean east, boolean south, boolean west,
			ObservableValue<? extends Number> minHeight, ObservableValue<? extends Number> minWidth,
			ObservableValue<? extends Number> maxHeight, ObservableValue<? extends Number> maxWidth) {
		this.setRegion(region);
		this.setResizeDirections(north, east, south, west);
		this.setResizeDirections(north, east, south, west);
		this.setSizes(minHeight, minWidth, maxHeight, maxWidth);
	}

	public static DragResizer makeResizable(Region region) {
		return new DragResizer(region);
	}

	public static DragResizer makeResizable(Region region, boolean north, boolean east, boolean south, boolean west) {
		return new DragResizer(region, north, east, south, west);
	}

	public static DragResizer makeResizable(Region region, boolean north, boolean east, boolean south, boolean west,
			ObservableValue<? extends Number> minHeight, ObservableValue<? extends Number> minWidth,
			ObservableValue<? extends Number> maxHeight, ObservableValue<? extends Number> maxWidth) {
		return new DragResizer(region, north, east, south, west, minHeight, minWidth, maxHeight, maxWidth);
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

	/**
	 * 
	 * @param minHeight
	 * @param minWidth
	 * @param maxHeight
	 * @param maxWidth
	 */
	public void setSizes(ObservableValue<? extends Number> minHeight, ObservableValue<? extends Number> minWidth,
			ObservableValue<? extends Number> maxHeight, ObservableValue<? extends Number> maxWidth) {
		this.minHeight = minHeight;
		this.minWidth = minWidth;
		this.maxHeight = maxHeight;
		this.maxWidth = maxWidth;
	}

	private void mouseOver(MouseEvent e) {
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
				this.region.setPrefHeight(e.getY());
				if (this.region.getPrefHeight() > this.maxHeight.getValue().doubleValue()) {
					this.region.setPrefHeight(this.maxHeight.getValue().doubleValue());
				}
				if (this.region.getPrefHeight() < this.minHeight.getValue().doubleValue()) {
					this.region.setPrefHeight(this.minHeight.getValue().doubleValue());
					return;
				}
				this.region.setPrefHeight(this.region.getPrefHeight());
				break;
			case (DragResizer.EAST):
				this.region.setPrefWidth(e.getX());
				if (this.region.getPrefWidth() > this.maxWidth.getValue().doubleValue()) {
					this.region.setPrefWidth(this.maxWidth.getValue().doubleValue());
				}
				if (this.region.getPrefWidth() < this.minWidth.getValue().doubleValue()) {
					this.region.setPrefWidth(this.minWidth.getValue().doubleValue());
					return;
				}
				this.region.setPrefWidth(this.region.getPrefWidth());
				break;
			case (DragResizer.NORTH):
				double prevMinHeight = this.region.getPrefHeight();
				this.region.setPrefHeight(this.region.getPrefHeight() - e.getY());
				if (this.region.getPrefHeight() < this.minHeight.getValue().doubleValue()) {
					this.region.setPrefHeight(this.minHeight.getValue().doubleValue());
					this.region.setTranslateY(this.region.getTranslateY()
							-(this.minHeight.getValue().doubleValue()-prevMinHeight));
					return;
				}
				if (this.region.getPrefHeight() > this.region.getPrefHeight() || e.getY() < 0) {
					this.region.setTranslateY(this.region.getTranslateY() + e.getY());
				}
				break;
			case (DragResizer.WEST):
				double prevMinWidth = this.region.getPrefWidth();
				this.region.setPrefWidth(this.region.getWidth() - e.getX());
				if (this.region.getPrefWidth() < this.minWidth.getValue().doubleValue()) {
					this.region.setPrefWidth(this.minWidth.getValue().doubleValue());
					this.region.setTranslateX(this.region.getTranslateX()
							-(this.minWidth.getValue().doubleValue()-prevMinWidth));
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
			this.region.setPrefHeight(this.region.getHeight());
			this.region.setPrefWidth(this.region.getWidth());
			this.initMinHeight = true;
		}
	}
}
