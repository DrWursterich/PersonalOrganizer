package menus;

import java.util.ArrayList;
import java.util.List;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import util.Translator;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyCode.SPACE;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

public class FontPicker extends ComboBoxBase<Font> {
	protected static final String OPEN_ACTION = "Open";
	protected static final String CLOSE_ACTION = "Close";
	protected static final List<KeyBinding> FONT_PICKER_BINDINGS = new ArrayList<>();
	private FontPicker me = this;
	private Tooltip tooltip = new Tooltip();
	private GridPane content = new GridPane();
	private CornerRadii contentRadii = new CornerRadii(5);
	private ComboBox<String> fontBox = new ComboBox<>(FXCollections.observableList(Font.getFamilies()));
	private Label previewLabel = new Label();
	private Label preview = new Label();
	private Insets previewInsets = new Insets(0, 1, 0, 1);
	private Label fontLabel = new Label();
	private Label sizeLabel = new Label();
	private Spinner<Integer> sizeSpinner;
	private Label styleLabel = new Label();
	private CheckBox boldCheckBox = new CheckBox();
	private CheckBox italicCheckBox = new CheckBox();
	private HBox styleBox = new HBox(10);
	private Button okButton = new Button();
	private Button cancelButton = new Button();
	private HBox buttonBox = new HBox(5);
	StringConverter<Integer> spinnerFormatter = new StringConverter<Integer>() {
		@Override
		public Integer fromString(String val) {
			String oldVal = String.valueOf(me.sizeSpinner.getValue());
			try {
				int result = Integer.parseInt(val);
				if (result >= 1 && result <= 100) {
					return result;
				}
				throw new Exception("Size out of Range.");
			} catch (Exception e) {
				me.sizeSpinner.getEditor().setText(oldVal);
				return me.sizeSpinner.getValue();
			}
		}

		@Override
		public String toString(Integer val) {
			return val.toString();
		}
	};

	private class FontPickerBehavior extends ComboBoxBaseBehavior<Font> {
		public FontPickerBehavior(FontPicker fontPicker) {
			this(fontPicker, FontPicker.FONT_PICKER_BINDINGS);
		}

		public FontPickerBehavior(FontPicker fontPicker, List<KeyBinding> keyBindings) {
			super(fontPicker, keyBindings);
		}

		@Override
		protected void callAction(String name) {
			if (FontPicker.OPEN_ACTION.equals(name)) {
				this.show();
			} else if (FontPicker.CLOSE_ACTION.equals(name)) {
				this.hide();
			} else {
				super.callAction(name);
			}
		}

		@Override
		public void onAutoHide() {
			((FontPickerPopupControl)this.getControl().getSkin()).syncWithAutoUpdate();
			if (!me.isShowing()) {
				super.onAutoHide();
			}
		}
	}

	private class FontPickerPopupControl extends ComboBoxPopupControl<Font> {
		private final Label displayNode = new Label("");

		public FontPickerPopupControl(FontPicker fontPicker) {
			this(fontPicker, new FontPickerBehavior(fontPicker));
		}

		public FontPickerPopupControl(ComboBoxBase<Font> arg0, ComboBoxBaseBehavior<Font> arg1) {
			super(arg0, arg1);
			this.registerChangeListener(me.valueProperty(), "VALUE");

			this.displayNode.setManaged(false);
			this.displayNode.setPadding(new Insets(4, 4, 4, 8));
			this.displayNode.setFont(Font.font(this.displayNode.getFont().getFamily(), 12));

			me.tooltip.textProperty().bind(this.displayNode.textProperty());

			this.updateFont();
		}

		@Override
		protected StringConverter<Font> getConverter() {
			return null;
		}

		@Override
		protected TextField getEditor() {
			return null;
		}

		@Override
		protected Node getPopupContent() {
			return me.content;
		}

		@Override
		public Node getDisplayNode() {
			return this.displayNode;
		}

		@Override
		public void handleControlPropertyChanged(String str) {
			super.handleControlPropertyChanged(str);
			if ("VALUE".equals(str)) {
				this.updateFont();
			}
		}

		private void updateFont() {
			Font font = this.getSkinnable().getValue();
			this.displayNode.setText(String.format("%s, %s, %d",
					font.getFamily(), font.getStyle(), (int)font.getSize()));
		}
		
		public void syncWithAutoUpdate() {
			if (!this.getPopup().isShowing() && this.getSkinnable().isShowing()) {
				this.getSkinnable().hide();
			}
		}
	}

	static {
		FONT_PICKER_BINDINGS.add(new KeyBinding(ESCAPE, KEY_PRESSED, CLOSE_ACTION));
		FONT_PICKER_BINDINGS.add(new KeyBinding(SPACE, KEY_PRESSED, OPEN_ACTION));
		FONT_PICKER_BINDINGS.add(new KeyBinding(ENTER, KEY_PRESSED, OPEN_ACTION));
	}

	public FontPicker(Font font) {
		this.setValue(font);
		this.setPrefWidth(133);
		Tooltip.install(this, this.tooltip);

		this.content.setPrefSize(200, 230);
		this.content.getColumnConstraints().addAll(new ColumnConstraints(40), new ColumnConstraints(160));
		this.content.getRowConstraints().addAll(new RowConstraints(20), new RowConstraints(35), new RowConstraints(35),
				new RowConstraints(35), new RowConstraints(30), new RowConstraints(30), new RowConstraints(30));
		this.content.setAlignment(Pos.CENTER);
		this.content.setVgap(10);
		this.content.setHgap(10);
		this.content.setPadding(new Insets(10));
		this.content.setBackground(new Background(new BackgroundFill(
				Color.WHITE.deriveColor(0, 1, 0.935, 1), this.contentRadii, this.previewInsets)));
		this.content.setBorder(new Border(new BorderStroke(
				Color.DARKGRAY, BorderStrokeStyle.SOLID, this.contentRadii, new BorderWidths(1))));
		this.content.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.color(0, 0, 0, 0.2), 12.0, 0.0, 0.0, 8.0));

		this.previewLabel.textProperty().bind(Translator.translationProperty("general", "fontPicker", "previewLabel"));
		this.preview.textProperty().bind(Translator.translationProperty("general", "fontPicker", "previewText"));
		this.preview.setMaxWidth(Double.MAX_VALUE);
		this.preview.setMaxHeight(Double.MAX_VALUE);
		this.preview.setAlignment(Pos.CENTER);
		this.preview.setBorder(new Border(new BorderStroke(
				Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
		this.preview.setBackground(new Background(new BackgroundFill(
				Color.WHITE, CornerRadii.EMPTY, this.previewInsets)));

		this.fontLabel.textProperty().bind(Translator.translationProperty("general", "fontPicker", "fontLabel"));
		this.fontBox.setMaxWidth(Double.MAX_VALUE);
		this.fontBox.getSelectionModel().select(font == null ? Font.getDefault().getFamily() : font.getFamily());
		this.fontBox.valueProperty().addListener(o -> this.changeFont());
		this.fontBox.setCellFactory((ListView<String> listView) -> {
			final ListCell<String> cell = new ListCell<String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (item != null) {
						this.setText(item);
						this.setFont(new Font(item, 12));;
					}
				}
			};
			cell.setPrefWidth(120);
			return cell;
		});

		this.sizeLabel.textProperty().bind(Translator.translationProperty("general", "fontPicker", "sizeLabel"));
		this.sizeSpinner = new Spinner<>(1, 100, (int)(font == null ? 12 : font.getSize()));
		this.sizeSpinner.setEditable(true);
		this.sizeSpinner.setPrefWidth(90);
		this.sizeSpinner.getValueFactory().setConverter(this.spinnerFormatter);
		this.sizeSpinner.valueProperty().addListener(o -> this.changeFont());

		this.styleLabel.textProperty().bind(Translator.translationProperty("general", "fontPicker", "styleLabel"));
		this.boldCheckBox.textProperty().bind(Translator.translationProperty("general", "fontPicker", "boldLabel"));
		this.boldCheckBox.setSelected(font == null ? false : font.getStyle().contains("Bold"));
		this.boldCheckBox.selectedProperty().addListener(o -> this.changeFont());
		this.italicCheckBox.textProperty().bind(Translator.translationProperty("general", "fontPicker", "italicLabel"));
		this.italicCheckBox.setSelected(font == null ? false : font.getStyle().contains("Italic"));
		this.italicCheckBox.selectedProperty().addListener(o -> this.changeFont());
		this.styleBox.setAlignment(Pos.CENTER_LEFT);
		this.styleBox.getChildren().addAll(this.boldCheckBox, this.italicCheckBox);

		this.okButton.textProperty().bind(Translator.translationProperty("general", "ok"));
		this.okButton.setPrefWidth(90);
		this.okButton.setOnAction(e -> {
			this.setValue(this.preview.getFont());
			this.hide();
		});
		this.cancelButton.textProperty().bind(Translator.translationProperty("general", "cancel"));
		this.cancelButton.setPrefWidth(90);
		this.cancelButton.setOnAction(e -> this.hide());
		this.buttonBox.setAlignment(Pos.CENTER_RIGHT);
		this.buttonBox.getChildren().addAll(this.okButton, this.cancelButton);

		this.content.add(this.previewLabel, 0, 0, 2, 1);
		this.content.add(this.preview, 		0, 1, 2, 2);
		this.content.add(this.fontLabel, 	0, 3, 1, 1);
		this.content.add(this.fontBox, 		1, 3, 1, 1);
		this.content.add(this.sizeLabel, 	0, 4, 1, 1);
		this.content.add(this.sizeSpinner, 	1, 4, 1, 1);
		this.content.add(this.styleLabel, 	0, 5, 1, 1);
		this.content.add(this.styleBox, 	1, 5, 1, 1);
		this.content.add(this.buttonBox, 	1, 6, 1, 1);

		this.changeFont();
	}

	private void changeFont() {
		this.preview.setFont(Font.font(this.fontBox.getValue(),
				this.boldCheckBox.isSelected() ? FontWeight.BOLD : FontWeight.NORMAL,
				this.italicCheckBox.isSelected() ? FontPosture.ITALIC : FontPosture.REGULAR,
				this.sizeSpinner.getValue()));
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new FontPickerPopupControl(this);
	}
}
