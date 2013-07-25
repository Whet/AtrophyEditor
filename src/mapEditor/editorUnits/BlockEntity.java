package mapEditor.editorUnits;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import mapEditor.PreviewPane;

public class BlockEntity extends MapEntity {

	private List<Integer[]> points;
	private Polygon polygon;
	
	public BlockEntity(int x, int y) {
		super(MapEntityType.BLOCK, x, y);
		
		this.polygon = new Polygon();
		this.points = new ArrayList<>();
	}
	
	@Override
	public void move(int x, int y) {
		super.move(x, y);
		for(int i = 0; i < points.size(); i++) {
			Integer[] point = this.points.get(i);
			point[0] += x;
			point[1] += y;
			this.points.set(i, point);
		}
		this.makePolygon();
	}
	
	public void addPoint(int x, int y) {
		this.points.add(new Integer[]{x,y});
		
		makePolygon();
	}
	
	private void makePolygon() {
		this.polygon.reset();
		
		for (Integer[] point : points) {
			this.polygon.addPoint(point[0], point[1]);
		}
	}

	public boolean removePoint(int x, int y) {
		for (Integer[] point : points) {
			if(point[0] == x && point[1] == y) {
				this.points.remove(point);
				makePolygon();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains(Point mousePoint) {
		return this.polygon.contains(mousePoint) || mousePoint.distance(new Point(this.getX(), this.getY())) < 10;
	}

	@Override
	public boolean mouseInteraction(Point mousePoint, int mouseButton) {
		if(!this.selected)
			return false;
		
		switch(mouseButton) {
			case 3:
				if(!this.removePoint(mousePoint.x, mousePoint.y))
					this.addPoint(mousePoint.x, mousePoint.y);
				return true;
		}
		
		return false;
	}

	@Override
	public void draw(Graphics2D g, int panX, int panY) {
		
		if(this.selected) {
			g.setColor(Color.orange);
			for(int i = 0; i < this.points.size(); i++) {
				g.drawOval(this.points.get(i)[0] + panX - 3, this.points.get(i)[1] + panY - 3, 6, 6);
			}
			g.setColor(Color.cyan);
		}
		else
			g.setColor(Color.red);
		
		g.drawOval(this.getX() + panX - 3, this.getY() + panY - 3, 6, 6);
		
		AffineTransform panTransform = new AffineTransform();
		AffineTransform oldTransform = g.getTransform();
		
		// likes to draw polygon 23 units off?!?!?
		panTransform.translate(panX, panY + 23);
		g.setTransform(panTransform);
		g.drawPolygon(polygon);
		g.setTransform(oldTransform);
	}

	@Override
	public String getScript() {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("BLOCK {\n");
		sb.append("\tx: ");
		
		for(int i = 0; i < this.points.size(); i++) {
			sb.append(this.points.get(i)[0]);
			
			if(i + 1 < this.points.size())
				sb.append(", ");
		}
		
		sb.append("\n\ty: ");
		
		for(int i = 0; i < this.points.size(); i++) {
			sb.append(this.points.get(i)[1]);
			
			if(i + 1 < this.points.size())
				sb.append(", ");
		}
		
		sb.append("\n}");
		
		return sb.toString();
	}

	@Override
	public boolean keyboardInteraction(int key) {
//		System.out.println(key);
		
		switch(key) {
			// W
			case 87:
				this.move(0, -PreviewPane.GRID_SIZE);
			break;
			// A
			case 65:
				this.move(-PreviewPane.GRID_SIZE, 0);
			break;
			// S
			case 83:
				this.move(0, PreviewPane.GRID_SIZE);
			break;
			// D
			case 68:
				this.move(PreviewPane.GRID_SIZE, 0);
			break;
		}
		
		return false;
	}
	
}
