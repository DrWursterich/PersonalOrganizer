package menus;

import org.schaeper.fxiterator.Iterator;
import org.schaeper.fxiterator.VIterator;
import org.schaeper.fxiterator.indexer.Indexer;
import org.schaeper.fxiterator.indexer.ItemPositionIndexer;
import org.schaeper.fxiterator.item.wrapper.controls.AddItemButton;
import org.schaeper.fxiterator.item.wrapper.controls.RemoveItemButton;

import javafx.geometry.Insets;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

public class AlarmIterator extends VIterator<AlarmIteratorItem> {
	private static final Indexer BEFORE_INDEXER = new ItemPositionIndexer();
	private static final Indexer AFTER_INDEXER = new ItemPositionIndexer(1);
	private static final Insets GRID_PADDING = new Insets(-10, 10, -10, 0);
	private static final Border GRID_BORDER = new Border(
			new BorderStroke(
				Color.DARKSLATEGREY,
				new BorderStrokeStyle(
					StrokeType.OUTSIDE,
					StrokeLineJoin.ROUND,
					StrokeLineCap.ROUND,
					10,
					0,
					null),
				new CornerRadii(6),
				new BorderWidths(2)));

	public AlarmIterator() {
		super(AlarmIteratorItem::new, AlarmIterator::createWrapper);
		this.setSpacing(10);
	}

	private static Pane createWrapper(
			final Iterator<AlarmIteratorItem> iterator,
			final AlarmIteratorItem item) {
		final RemoveItemButton remove = new RemoveItemButton(
				iterator,
				item,
				AlarmIterator.BEFORE_INDEXER);
		final AddItemButton addBefore = new AddItemButton(
				iterator,
				item,
				AlarmIterator.BEFORE_INDEXER);
		final AddItemButton addAfter = new AddItemButton(
				iterator,
				item,
				AlarmIterator.AFTER_INDEXER);
		final GridPane grid = AlarmIterator.createPane();
		grid.add(addBefore, 1, 0, 1, 2);
		grid.add(item, 0, 1, 1, 3);
		grid.add(remove, 1, 2);
		grid.add(addAfter, 1, 3, 1, 2);
		return grid;
	}

	private static GridPane createPane() {
		final GridPane grid = new GridPane();
		final ColumnConstraints rightColumn = new ColumnConstraints(
				USE_COMPUTED_SIZE);
		final ColumnConstraints leftColumn = new ColumnConstraints(
				120,
				USE_COMPUTED_SIZE,
				USE_COMPUTED_SIZE);
		rightColumn.setHgrow(Priority.NEVER);
		leftColumn.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(leftColumn, rightColumn);
		grid.getRowConstraints().addAll(
				new RowConstraints(10, 30, USE_COMPUTED_SIZE),
				new RowConstraints(10, 30, USE_COMPUTED_SIZE),
				new RowConstraints(10, 30, USE_COMPUTED_SIZE),
				new RowConstraints(10, 30, USE_COMPUTED_SIZE),
				new RowConstraints(10, 30, USE_COMPUTED_SIZE));
		grid.setPadding(AlarmIterator.GRID_PADDING);
		grid.setBorder(AlarmIterator.GRID_BORDER);
		return grid;
	}

	public void insertElement(
			final int index,
			final int months,
			final int days,
			final int hours,
			final int minutes) {
		this.insertElement(index);
		final AlarmIteratorItem item = this.itemsProperty().get().get(index);
		item.setMonths(months);
		item.setDays(days);
		item.setHours(hours);
		item.setMinutes(minutes);
	}
}

