package util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class NumberField extends TextField {
	private final StringProperty defaultProperty;

	public NumberField() {
		this(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public NumberField(final int value) {
		this();
		this.setText(Integer.toString(value));
	}

	public NumberField(final int minValue, final int maxValue) {
		super();
		this.defaultProperty = new SimpleStringProperty("");
		this.setTextFormatter(new TextFormatter<>(new StringConverter<String>() {
			@Override
			public String toString(final String string) {
				return this.formatString(string);
			}

			@Override
			public String fromString(final String string) {
				return this.formatString(string);
			}

			private String formatString(final String string) {
				if ("".equals(string)) {
					return NumberField.this.defaultProperty.get();
				}
				int ret = minValue;
				try {
					ret = Math.max(minValue, Integer.parseInt(string));
				} catch (final NumberFormatException e) {
					return NumberField.this.defaultProperty.get();
				}
				return "" + Math.min(ret, maxValue);
			}
		}));
	}

	public NumberField(
			final int value,
			final int minValue,
			final int maxValue) {
		this(minValue, maxValue);
		this.setText(Integer.toString(value));
	}

	public int getValue() {
		try {
			return this.getText() == null
					? 0
					: Integer.parseInt(this.getText());
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	public void setValue(final int value) {
		this.setText(Integer.toString(value));
	}

	public String getDefaultValue() {
		return this.defaultProperty.get();
	}

	public void setDefaultValue(final String defaultValue) {
		this.defaultProperty.set(defaultValue);
	}

	public StringProperty defaultValueProperty() {
		return this.defaultProperty;
	}
}
