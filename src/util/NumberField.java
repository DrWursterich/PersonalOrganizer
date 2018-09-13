package util;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class NumberField extends TextField {
	public NumberField() {
		this(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public NumberField(int value) {
		this();
		this.setText(Integer.toString(value));
	}

	public NumberField(int minValue, int maxValue) {
		super();
		this.setTextFormatter(new TextFormatter<>(new StringConverter<String>() {
			@Override
			public String toString(String string) {
				return this.formatString(string);
			}

			@Override
			public String fromString(String string) {
				return this.formatString(string);
			}

			private String formatString(String string) {
				if ("".equals(string)) {
					return "";
				}
				int ret = minValue;
				try {
					ret = Math.max(minValue, Integer.parseInt(string));
				} catch (NumberFormatException e) {
					return "";
				}
				return "" + Math.min(ret, maxValue);
			}
		}));
	}

	public NumberField(int value, int minValue, int maxValue) {
		this(minValue, maxValue);
		this.setText(Integer.toString(value));
	}

	public int getValue() {
		try {
			return this.getText() == null ? 0 : Integer.parseInt(this.getText());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public void setValue(int value) {
		this.setText(Integer.toString(value));
	}
}
