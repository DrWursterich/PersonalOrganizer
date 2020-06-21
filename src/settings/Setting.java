package settings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A abstract Setting.<br/>
 * To create a Setting from this Class, either get <b>T</b>
 * to extend {@link java.io.Serializable Serializable} or do not use Generics.<br/>
 * A Class extending this Setting, using <b>T</b> as a Constant (eg.
 * {@code MyClass extends Setting<String>}) has to override following Methods
 * to ensure the Class to be serializable: <pre>
 * private void writeObject(java.io.ObjectOutputStream out) throws IOException;
 * private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException;
 * </pre>
 * @see java.io.Serializable
 * @author Mario Schäper
 * @param <T> the type of the setting
 */
public abstract class Setting<T extends Object> extends SimpleObjectProperty<T>
		implements Serializable {
	private static final long serialVersionUID = 1105901212689315175L;
	protected transient T defaultValue;

	public Setting(T defaultValue) {
		if (defaultValue == null) {
			throw new IllegalArgumentException("Default Value cannot be null");
		}
		this.defaultValue = defaultValue;
		this.setValue(this.defaultValue);
	}

	@SuppressWarnings("unchecked")
	protected void setDefaultValue(Object value) {
		if (this.getSettingClass().isAssignableFrom(value.getClass())) {
			this.defaultValue = (T)value;
		}
	}

	public T getDefault() {
		return this.defaultValue;
	}

	@SuppressWarnings("unchecked")
	protected void setObject(Object value) {
		this.setValue((T)value);
	}

	public Class<?> getSettingClass() throws NullPointerException {
		return this.defaultValue.getClass();
	}

	/**
	 * Restores the default value.
	 */
	public void restoreDefault() throws NullPointerException {
		if (this.defaultValue != null) {
			this.setValue(this.defaultValue);
		} else {
			throw new NullPointerException();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {}

	@Override
	public int hashCode() {
		return super.hashCode() + this.defaultValue.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return (other == null || !(other instanceof Setting<?>))
				? false
				: this.defaultValue.equals(((Setting<?>)other).defaultValue);
	}

	@Override
	public String toString() {
		return super.toString() + " defaultValue: "
				+ (this.defaultValue == null ? "null" : this.defaultValue.toString());
	}
}
