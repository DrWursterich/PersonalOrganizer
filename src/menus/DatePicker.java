package menus;

import java.time.LocalDate;

public class DatePicker extends javafx.scene.control.DatePicker {
	public int getDayOfMonth() {
		return this.getDayOfMonth(LocalDate.now().getDayOfMonth());
	}

	public int getDayOfMonth(int standard) {
		return this.getValue() != null ? this.getValue().getDayOfMonth() : standard;
	}

	public int getMonth() {
		return this.getMonth(LocalDate.now().getMonthValue());
	}

	public int getMonth(int standard) {
		return this.getValue() != null ? this.getValue().getMonthValue() - 1 : standard;
	}

	public int getYear() {
		return this.getYear(LocalDate.now().getYear());
	}

	public int getYear(int standard) {
		return this.getValue() != null ? this.getValue().getYear() : standard;
	}
}

