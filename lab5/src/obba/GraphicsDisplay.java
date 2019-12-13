package obba;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.*;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {
	private Double[][] graphicsData;

	private boolean showAxis = true;
	private boolean showMarkers = true;
	private boolean showArea = false;
	private boolean isRotated = false;
	private boolean isDraggedRect = false;
	private boolean isDraggedScale = false;
	private boolean isDraggedMark = false;

	private double minX;
	private double maxX;
	private double minY;
	private double maxY;

	private double scaleX;
	private double scaleY;
	
	private Point2D.Double cursor = xyToPoint(0,0);
	private Point2D.Double cursor_end = xyToPoint(0,0);
	private Point2D.Double marker = xyToPoint(0,0);
	
	AffineTransform prev_at= new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
	AffineTransform at= new AffineTransform(1.0,0.0,0.0,1.0, 0, 0);
	Rectangle2D.Double rect=new Rectangle2D.Double();
	Rectangle2D.Double new_rect=new Rectangle2D.Double();
	Rectangle2D.Double prev_rect=new Rectangle2D.Double();

	private BasicStroke graphicsStroke;
	private BasicStroke axisStroke;
	private BasicStroke markerStroke;

	private Font axisFont;
	private Font areaFont;
	private Font coordFont;

	public GraphicsDisplay() {
		setBackground(Color.WHITE);
		addMouseListener(new MyMouseAdapter());
		addMouseMotionListener(new MyMouseMotionListener());
		graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 10.0f, new float[] { 40, 10, 10, 10,
						10, 10, 20, 10, 20, 10 }, 0.0f);
		axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
		markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
		axisFont = new Font("Serif", Font.BOLD, 36);
		areaFont = new Font("Serif", Font.BOLD, 36);
		coordFont= new Font("SansSerif",Font.PLAIN,15);
		
	}

	public void showGraphics(Double[][] graphicsData) {
		this.graphicsData = graphicsData;
		prev_rect.setFrame(000.0,000.0,getWidth(),getHeight());
		rect.setFrame(000.0,000.0,getWidth(),getHeight());
		prev_at = new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
		at = new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
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
	public void setCursor(double x,double y){
		cursor.x=x;
		cursor.y=y;
		repaint();
	}
	public void setCursor_e(double x,double y){
		cursor_end.x=x;
		cursor_end.y=y;
		repaint();
	}
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		if (graphicsData == null || graphicsData.length == 0)
			return;
		
		double w = getSize().getWidth();
		double h = getSize().getHeight();
		
		scaleX = w / (maxX - minX);
		scaleY = h / (maxY - minY);
		
		Graphics2D canvas = (Graphics2D) g;
		Stroke oldStroke = canvas.getStroke();
		Color oldColor = canvas.getColor();
		Paint oldPaint = canvas.getPaint();
		Font oldFont = canvas.getFont();
		
		canvas.setTransform(at);
		Paint oldP=canvas.getPaint();
		canvas.setPaint(Color.green);
		canvas.draw(rect);
		canvas.setPaint(Color.red);
		canvas.draw(prev_rect);
		canvas.setPaint(oldP);
        
		if(isRotated)
			paintRotated(canvas);
		if(isDraggedScale)
			paintScaled(canvas);
		if (showAxis)
			paintAxis(canvas);
		paintGraphics(canvas);
		if (showMarkers)
			paintMarkers(canvas);
		if(isDraggedRect)
			paintRect(canvas);
		if(showArea)
			paintArea(canvas);
		
		canvas.setFont(oldFont);
		canvas.setPaint(oldPaint);
		canvas.setColor(oldColor);
		canvas.setStroke(oldStroke);
		repaint();
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
        AffineTransform at1 = new AffineTransform(1.0,0.0,0.0,1.0,0.0,0.0);
        at1.rotate(-Math.PI/2, 0,0);
        at1.translate(-h,0);
		at1.concatenate(new AffineTransform(h/w,0,0,w/h,0,0));
		at1.concatenate(at);
		canvas.setTransform(at1);
		//isRotated=false;
	}
	
	protected void paintMarkers(Graphics2D canvas) {

		canvas.setStroke(markerStroke);

		canvas.setColor(Color.RED);
		
		FontRenderContext context = canvas.getFontRenderContext();
		
		double w = getSize().getWidth();
		double h = getSize().getHeight();
		Double a1=cursor.getX()*rect.width/w + rect.x;
		Double a2=cursor.getY()*rect.height/h + rect.y; 

		for (Double[] point : graphicsData) {
			canvas.setPaint(Color.BLUE);
			Point2D.Double center = xyToPoint(point[0], point[1]);
			Double lengthX = center.getX() - a1;
			Double lengthY = center.getY() - a2;
			if(Math.abs(lengthX)<5 && Math.abs(lengthY)<5)
			{
				
				 if(isDraggedRect || isDraggedMark){
					setCursor(center.getX(),center.getY());
					isDraggedRect=false;
					isDraggedMark=true;
				    point[1]=maxY-cursor_end.getY()/scaleY;
				    marker=xyToPoint(point[0],point[1]);
				}
				Rectangle2D.Double coordinates = new Rectangle2D.Double();
				Rectangle2D bounds = coordFont.getStringBounds((center.getX()/scaleX+minX)+","+ (maxY-center.getY()/scaleY), context);
				Double Width=bounds.getWidth();
				Double Height=bounds.getHeight();
				canvas.setColor(Color.LIGHT_GRAY);
				Point2D.Double labelPos = new Point2D.Double(a1,a2);
				Font old=canvas.getFont();
				canvas.setFont(coordFont);
				if(a1<Width && a2<Height)
				{
					coordinates.setFrame(a1+10,a2+10, Width , Height );
					canvas.draw(coordinates);
					canvas.fill(coordinates);
					canvas.setPaint(Color.BLACK);
					canvas.drawString((center.getX()/scaleX+minX)+","+ (maxY-center.getY()/scaleY), (float) labelPos.getX()+10,
						(float) (labelPos.getY()+bounds.getHeight()+10));
				}
				else if(a1<Width)
				{
					coordinates.setFrame(a1,a2-Height , Width , Height );
					canvas.draw(coordinates);
					canvas.fill(coordinates);
					canvas.setPaint(Color.BLACK);
					canvas.drawString((center.getX()/scaleX+minX)+","+ (maxY-center.getY()/scaleY), (float) labelPos.getX(),
						(float) (labelPos.getY()));
				}
				else if(w-a1<Width && a2<Height)
				{
					coordinates.setFrame(a1-Width,a2, Width , Height );
					canvas.draw(coordinates);
					canvas.fill(coordinates);
					canvas.setPaint(Color.BLACK);
					canvas.drawString((center.getX()/scaleX+minX)+","+ (maxY-center.getY()/scaleY), (float) (labelPos.getX()-Width),
							(float) (labelPos.getY()+bounds.getHeight()));
				}
				else if (w-a1<Width)
				{
					coordinates.setFrame(a1-Width,a2-Height, Width , Height );
					canvas.draw(coordinates);
					canvas.fill(coordinates);
					canvas.setPaint(Color.BLACK);
					canvas.drawString((center.getX()/scaleX+minX)+","+ (maxY-center.getY()/scaleY), (float) (labelPos.getX()-Width),
							(float) (labelPos.getY()));
				}
				else if(a2<Height){
					coordinates.setFrame(a1-Width,a2, Width , Height );
				canvas.draw(coordinates);
				canvas.fill(coordinates);
				canvas.setPaint(Color.BLACK);
				canvas.drawString((center.getX()/scaleX+minX)+","+ (maxY-center.getY()/scaleY), (float) (labelPos.getX()-Width),
						(float) (labelPos.getY()+bounds.getHeight()));
				}
				else
				{
					coordinates.setFrame(a1-Width,a2-Height, Width , Height );
					canvas.draw(coordinates);
					canvas.fill(coordinates);
					canvas.setPaint(Color.BLACK);
					canvas.drawString((center.getX()/scaleX+minX)+","+ (maxY-center.getY()/scaleY), (float) (labelPos.getX()-Width),
							(float) (labelPos.getY()));
				}
				canvas.setFont(old);
				canvas.setPaint(Color.BLUE);
			}
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
				int h1 = ((Double) (f * 10.0)).intValue();
				if (h1 % 10 < a) {
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
					f-=h1/10;
					f *= 10;
					a = h1 % 10;
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
	
	protected void paintRect(Graphics2D canvas)
	{
		double w = getSize().getWidth();
		double h = getSize().getHeight();
		if (cursor.getX()-cursor_end.getX()==0 || cursor.getY()-cursor_end.getY()==0)
		{
			Paint oldP=canvas.getPaint();
			canvas.setPaint(Color.green);
			canvas.draw(rect);
			canvas.setPaint(Color.red);
			canvas.draw(prev_rect);
			canvas.setPaint(oldP);
			return;
		}
		Stroke old=canvas.getStroke();
		
		Double a1=cursor.getX()*rect.width/w + rect.x;
		Double a2=cursor.getY()*rect.height/h + rect.y; 
		Double a3=cursor_end.getX()*rect.width/w + rect.x; 
		Double a4=cursor_end.getY()*rect.height/h + rect.y; 
		if(a3>a1 && a4>a2)
		new_rect.setFrame(a1,a2,a3-a1,a4-a2);
		else if(a1>a3 && a4>a2)//
			new_rect.setFrame(a3,a2,a1-a3,a4-a2);
		else if(a3>a1 && a2>a4)
			new_rect.setFrame(a1,a4,a3-a1,a2-a4);
		else if(a1>a3 && a2>a4)
			new_rect.setFrame(a3,a4,a1-a3,a2-a4);
		canvas.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 10.0f, new float[] { 40, 10 }, 0.0f));
		canvas.draw(new_rect);
		canvas.setStroke(old);
	}
	
	protected void paintScaled(Graphics2D canvas)
	{
		double per_x=rect.width/new_rect.width;
		double per_y=rect.height/new_rect.height;
		Double a1=new_rect.x-rect.x/per_x; 
		Double a2=new_rect.y-rect.y/per_y;
		System.out.println(at.getScaleX());

		at.concatenate(new AffineTransform(per_x,0.0,0.0,per_y,0,0));
		at.translate(-a1,-a2);
		
		
		prev_at=canvas.getTransform();
		prev_rect.setRect(rect);
		rect.setRect(new_rect);
		canvas.setTransform(at);
		canvas.draw(prev_rect);
		canvas.draw(rect);
		canvas.draw(new_rect);
		isDraggedScale=false;
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
	
	private class MyMouseAdapter extends MouseAdapter  {
		
		public void mouseClicked(MouseEvent e) {
			//rect.setFrame(0.0,0.0,getWidth(),getHeight());
			at=new AffineTransform(prev_at);
			rect.setRect(prev_rect);
		}
		public void mousePressed(MouseEvent e) {
			
			setCursor(e.getX(),e.getY());
			
			//prev_rect=rect;
		}
		public void mouseReleased(MouseEvent e) {
			if(SwingUtilities.isLeftMouseButton(e)){
			
			setCursor_e(e.getX(),e.getY());
			if(isDraggedRect)
				isDraggedScale=true;
			isDraggedRect=false;
			isDraggedMark=false;
			}
		}

	}
	private class MyMouseMotionListener implements MouseMotionListener{
		
		public void mouseMoved(MouseEvent e){
			setCursor(e.getX(),e.getY());
			setCursor_e(e.getX(),e.getY());
			//System.out.println(e.getY());
		};
		public void mouseDragged(MouseEvent e){
			//System.out.println(SwingUtilities.isLeftMouseButton(e));
			//if(e.getButton()==MouseEvent.BUTTON3){
			if(SwingUtilities.isLeftMouseButton(e)){
			//if(e.isPopupTrigger()){
				if(!isDraggedMark)
			isDraggedRect=true;
			setCursor_e(e.getX(),e.getY());
				if(isDraggedMark)
					setCursor(marker.getX(),marker.getY());
			}
		};
	}
	protected void saveToGraphicsFile(File selectedFile) {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
			for (int i = 0; i < graphicsData.length; i++) {
				out.writeDouble((Double) graphicsData[i][0]);
				out.writeDouble((Double) graphicsData[i][1]);
			}
			out.close();
		} catch (Exception e) {
		}
	}
}