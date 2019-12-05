package obba;

import javax.swing.table.*;

@SuppressWarnings("serial")
public class GornerTableModel extends AbstractTableModel {
	private Double[] coefficients;
	private Double from;
	private Double to;
	private Double step;

	public GornerTableModel(Double from, Double to, Double step,
			Double[] coefficients) {
		this.from = from;
		this.to = to;
		this.step = step;
		this.coefficients = coefficients;
	}

	public Double getFrom() {
		return from;
	}

	public Double getTo() {
		return to;
	}

	public Double getStep() {
		return step;
	}

	public int getColumnCount() {
		return 4;
	}

	public int getRowCount() {
		return new Double(Math.ceil((to - from) / step)).intValue() + 1;
	}

	public Object getValueAt(int row, int col) {
		double x = from + step * row;
		if (col == 0) {
			return x;
		} else if(col==1){
			Double result = 0.0;
			for (int i = 0; i < coefficients.length - 1; i++) {
				result += coefficients[i];
				result *= x;
			}
			result += coefficients[coefficients.length - 1];
			return result;
		}
		else if(col==2){
			Double result = 0.0;
			for (int i = coefficients.length - 1; i > 0; i--) {
				result += coefficients[i];
				result *= x;
			}
			result += coefficients[0];
			return result;
		}
		else {
			Double result1 = 0.0;
			for (int i = 0; i < coefficients.length - 1; i++) {
				result1 += coefficients[i];
				result1 *= x;
			}
			result1 += coefficients[coefficients.length - 1];
			Double result2 = 0.0;
			for (int i = coefficients.length - 1; i > 0; i--) {
				result2 += coefficients[i];
				result2 *= x;
			}
			result2 += coefficients[0];
			return result1-result2;
		}
	}

	public String getColumnName(int col) {

		switch (col) {
		case 0:
			return "Значение X";
		case 1:
			return "Значение многочлена";
		case 2:
			return "Polynom2";
		default:
			return "dif 1-2";
		}
	}

	public Class<?> getColumnClass(int col) {
		return Double.class;
	}
}
