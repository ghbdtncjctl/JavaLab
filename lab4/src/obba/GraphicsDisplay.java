package obba;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.*;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {
	private Double[][] graphicsData;

	private boolean showAxis = true;
	private boolean showMarkers = true;
	private boolean showArea = false;
	private boolean isRotated = false;

	private double minX;
	private double maxX;
	private double minY;
	private double maxY;

	private double scaleX;
	private double scaleY;

	private BasicStroke graphicsStroke;
	private BasicStroke axisStroke;
	private BasicStroke markerStroke;

	private Font axisFont;
	private Font areaFont;

	public GraphicsDisplay() {
		setBackground(Color.WHITE);
		graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 10.0f, new float[] { 40, 10, 10, 10,
						10, 10, 20, 10, 20, 10 }, 0.0f);
		axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
		markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
		axisFont = new Font("Serif", Font.BOLD, 36);
		areaFont = new Font("Serif", Font.BOLD, 36);
	}

	public void showGraphics(Double[][] graphicsData) {
		this.graphicsData = graphicsData;
		repaint();
	}

	public void setShowAxis(boolean showAxis) {
		this.showAxis = showAxis;
		repaint();
	}

	public void setShowMarkers(boolean showMarkers) {
		this.showMarkers = showMarkers;
		repaint();
	}
	public void setRotated(boolean isRotated) {
		this.isRotated = isRotated;
		repaint();
	}

	public void setShowArea(boolean showArea) {
		this.showArea = showArea;
		repaint();
	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		if (graphicsData == null || graphicsData.length == 0)
			return;
		minX = graphicsData[0][0];
		maxX = graphicsData[graphicsData.length - 1][0];
		minY = graphicsData[0][1];
		maxY = minY;
		for (int i = 1; i < graphicsData.length; i++) {
			if (graphicsData[i][1] < minY) {
				minY = graphicsData[i][1];
			}
			if (graphicsData[i][1] > maxY) {
				maxY = graphicsData[i][1];
			}
		}
		double w = getSize().getWidth();
		double h = getSize().getHeight();
		
		scaleX = w / (maxX - minX);
		scaleY = h / (maxY - minY);
		
		Graphics2D canvas = (Graphics2D) g;
		Stroke oldStroke = canvas.getStroke();
		Color oldColor = canvas.getColor();
		Paint oldPaint = canvas.getPaint();
		Font oldFont = canvas.getFont();
		
		AffineTransform at = new AffineTransform(1,0,0,1,0,0);
        canvas.setTransform(at);
        
		if(isRotated)
			paintRotated(canvas);
		if (showAxis)
			paintAxis(canvas);
		paintGraphics(canvas);
		if (showMarkers)
			paintMarkers(canvas);
		if(showArea)
			paintArea(canvas);
		
		canvas.setFont(oldFont);
		canvas.setPaint(oldPaint);
		canvas.setColor(oldColor);
		canvas.setStroke(oldStroke);
		updateUI();
	}

	protected void paintGraphics(Graphics2D canvas) {

		canvas.setStroke(graphicsStroke);
		canvas.setColor(Color.RED);
		GeneralPath graphics = new GeneralPath();
		for (int i = 0; i < graphicsData.length; i++) {
			Point2D.Double point = xyToPoint(graphicsData[i][0],
					graphicsData[i][1]);
			if (i > 0) {
				graphics.lineTo(point.getX(), point.getY());
			} else {

				graphics.moveTo(point.getX(), point.getY());
			}
		}
		canvas.draw(graphics);
	}
	
	protected void paintRotated(Graphics2D canvas)
	{
		double w = getSize().getWidth();
		double h = getSize().getHeight();
        AffineTransform at = canvas.getTransform();
        at.rotate(-Math.PI/2, 0,0);
        at.translate(-w,0);
		at.concatenate(new AffineTransform(h/w,0,0,w/h,0,0));
		at.translate((w-h)*w/h,0);
		canvas.setTransform(at);
	}
	
	protected void paintMarkers(Graphics2D canvas) {

		canvas.setStroke(markerStroke);

		canvas.setColor(Color.RED);

		

		for (Double[] point : graphicsData) {
			canvas.setPaint(Color.BLUE);
			Point2D.Double center = xyToPoint(point[0], point[1]);
			Line2D.Double markerLD = new Line2D.Double(shiftPoint(center, -5,
					-5), shiftPoint(center, 5, 5));
			Line2D.Double markerRD = new Line2D.Double(
					shiftPoint(center, 5, -5), shiftPoint(center, -5, 5));
			Line2D.Double markerU = new Line2D.Double(
					shiftPoint(center, 0, -5), shiftPoint(center, 0, 5));
			Line2D.Double markerD = new Line2D.Double(
					shiftPoint(center, -5, 0), shiftPoint(center, 5, 0));
			Double f = Math.abs(point[1]);
			while (f > 10)
				f /= 10;
			while (f < 1 && !(f.equals(0.0)))
				f *= 10;
			int a = f.intValue();
			for (int i = 0; i < 13; i++) {
				int h = ((Double) (f * 10.0)).intValue();
				if (h % 10 < a) {
					boolean usl = false;
					Double f1=f;
					for (int k = i; k < 13; k++) {
						int p = ((Double) (f1 * 10.0)).intValue();
						if (p % 10 != 0) {
							usl = true;
							break;
						}
						f1-=p/10;
						f1*=10;
					}
					if (usl) {
						canvas.setPaint(Color.BLACK);
						break;
					}
					else
						i=15;
				} else {
					f-=h/10;
					f *= 10;
					a = h % 10;
				}
			}
			canvas.draw(markerLD);
			canvas.draw(markerRD);
			canvas.draw(markerU);
			canvas.draw(markerD);

		}
	}

	protected void paintAxis(Graphics2D canvas) {


		canvas.setStroke(axisStroke);

		canvas.setColor(Color.BLACK);

		canvas.setPaint(Color.BLACK);

		canvas.setFont(axisFont);

		FontRenderContext context = canvas.getFontRenderContext();

		if (minX <= 0.0 && maxX >= 0.0) {

			canvas.draw(new Line2D.Double(xyToPoint(0, maxY),
					xyToPoint(0, minY)));

			GeneralPath arrow = new GeneralPath();

			Point2D.Double lineEnd = xyToPoint(0, maxY);
			arrow.moveTo(lineEnd.getX(), lineEnd.getY());

			arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow
					.getCurrentPoint().getY() + 20);

			arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow
					.getCurrentPoint().getY());

			arrow.closePath();
			canvas.draw(arrow);
			canvas.fill(arrow);
			Rectangle2D bounds = axisFont.getStringBounds("y", context);
			Point2D.Double labelPos = xyToPoint(0, maxY);
			canvas.drawString("y", (float) labelPos.getX() + 10,
					(float) (labelPos.getY() - bounds.getY()));
		}
		if (minY <= 0.0 && maxY >= 0.0) {

			canvas.draw(new Line2D.Double(xyToPoint(minX, 0),
					xyToPoint(maxX, 0)));

			GeneralPath arrow = new GeneralPath();

			Point2D.Double lineEnd = xyToPoint(maxX, 0);
			arrow.moveTo(lineEnd.getX(), lineEnd.getY());

			arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow
					.getCurrentPoint().getY() - 5);

			arrow.lineTo(arrow.getCurrentPoint().getX(), arrow
					.getCurrentPoint().getY() + 10);

			arrow.closePath();
			canvas.draw(arrow);
			canvas.fill(arrow);
			Rectangle2D bounds = axisFont.getStringBounds("x", context);
			Point2D.Double labelPos = xyToPoint(maxX, 0);
			canvas.drawString("x",
					(float) (labelPos.getX() - bounds.getWidth() - 10),
					(float) (labelPos.getY() + bounds.getY()));
		}
	}
	
	
	protected void paintArea(Graphics2D canvas)
	{
		canvas.setPaint(Color.YELLOW);
		FontRenderContext context = canvas.getFontRenderContext();
		Double sizeArea=0.0;
		int firstPoint=XIntersection(graphicsData,0);
		if(firstPoint==-1)
			return;
		else
		{
			int point=XIntersection(graphicsData,firstPoint+1);
			while(point!=-1)
			{
				Double dx1=graphicsData[firstPoint][0]-graphicsData[firstPoint+1][0];
				Double dy1=graphicsData[firstPoint][1]-graphicsData[firstPoint+1][1];
				Double x1 = graphicsData[firstPoint][0]+(dx1/dy1)*graphicsData[firstPoint][1];
				sizeArea+=(x1-graphicsData[firstPoint][0])*graphicsData[firstPoint][1]/2;
				Double dx2=graphicsData[firstPoint][0]-graphicsData[point+1][0];
				Double dy2=graphicsData[point][1]-graphicsData[point+1][1];
				Double x2 = graphicsData[point][0]+(dx2/dy2)*graphicsData[point][1];
				GeneralPath path= new GeneralPath();
				Point2D startPoint= xyToPoint(x1,0);
				path.moveTo(startPoint.getX(),startPoint.getY());
				for (int i = firstPoint+1; i < point+1; i++) {
					Point2D.Double graphPoint = xyToPoint(graphicsData[i][0],
							graphicsData[i][1]);
					sizeArea+=(graphicsData[i+1][1]+graphicsData[i][1])*(graphicsData[i][0]-graphicsData[i+1][0])/2;
						path.lineTo(graphPoint.getX(), graphPoint.getY());
				}
				Point2D endPoint= xyToPoint(x2,0);
				sizeArea+=(x2-graphicsData[point][0])*graphicsData[point][1]/2;
				path.lineTo(endPoint.getX(),endPoint.getY());
				path.closePath();
				canvas.draw(path);
				canvas.fill(path);
				Rectangle2D bounds = areaFont.getStringBounds(sizeArea.toString(), context);
				Point2D.Double labelPos = xyToPoint((x1+x2)/2, 0);
				canvas.setPaint(Color.BLACK);
				canvas.drawString(sizeArea.toString(), (float) labelPos.getX(),
						(float) (labelPos.getY() - bounds.getY()));
				firstPoint=point+1;
				point=XIntersection(graphicsData,point+1);
			}
		}
	}
	
	protected Point2D.Double xyToPoint(double x, double y) {

		double deltaX = x - minX;

		double deltaY = maxY - y;
		return new Point2D.Double(deltaX * scaleX, deltaY * scaleY);
	}

	protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX,
			double deltaY) {

		Point2D.Double dest = new Point2D.Double();
		dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
		return dest;
	}
	

	protected int XIntersection(Double[][] points, int k)

	{
		for (int i = k; i < points.length - 1; i++) {
			if (points[i][1] < 0 && points[i + 1][1] > 0 || 
					points[i][1] > 0 && points[i + 1][1] < 0)
				return i;
		}
		return -1;
	};
}