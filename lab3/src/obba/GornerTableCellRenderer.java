package obba;

import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class GornerTableCellRenderer implements TableCellRenderer {

	private JPanel panel = new JPanel();
	private JLabel label = new JLabel();

	private String needle = null;
	private String to = null;
	private String from = null;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	private DecimalFormat formatter = (DecimalFormat) NumberFormat
			.getInstance();

	public GornerTableCellRenderer() {
		formatter.setMaximumFractionDigits(5);
		DecimalFormatSymbols dottedDouble = formatter.getDecimalFormatSymbols();
		dottedDouble.setDecimalSeparator('.');
		formatter.setDecimalFormatSymbols(dottedDouble);
		panel.add(label);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int col) {
		String formattedDouble = formatter.format(value);
		label.setText(formattedDouble);
		if (col == 1 && needle != null && needle.equals(formattedDouble)) {
			panel.setBackground(Color.RED);
		} else if (col == 1 && from != null && to != null
				&& (from.compareTo(formattedDouble) <= 0)
				&& (to.compareTo(formattedDouble) >= 0)) {
			panel.setBackground(Color.RED);
		} else {
			if (((double) row + col) / 2 == (int) (((double) row + col) / 2)) {
				panel.setBackground(Color.BLACK);
				label.setForeground(Color.WHITE);
			} else {
				panel.setBackground(Color.WHITE);
				label.setForeground(Color.BLACK);
			}
		}
		return panel;
	}

	public void setNeedle(String needle) {
		this.needle = needle;
	}
}