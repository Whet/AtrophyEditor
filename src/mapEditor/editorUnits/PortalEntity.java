package mapEditor.editorUnits;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import mapEditor.PreviewPane;

public class PortalEntity extends MapEntity {

	private Point point1, point2;
	
	public PortalEntity(int x, int y) {
		super(MapEntityType.PORTAL, x, y);
		point1 = null;
		point2 = null;
	}

	@Override
	public void move(int x, int y) {
		super.move(x, y);
		
		if(point1 != null)
			this.point1.translate(x, y);
		
		if(point2 != null)
			this.point2.translate(x, y);
	}
	
	@Override
	public boolean contains(Point mousePoint) {
		return mousePoint.distance(new Point(this.getX(), this.getY())) < 10;
	}

	@Override
	public boolean mouseInteraction(Point mousePoint, int mouseButton) {
		switch(mouseButton) {
			case 3:
				if(!this.removePoint(mousePoint))
					this.addPoint(mousePoint);
				return true;
		}
		return false;
	}

	private void addPoint(Point mousePoint) {
		if(this.point1 == null)
			this.point1 = mousePoint;
		else if(this.point2 == null)
			this.point2 = mousePoint;
	}

	private boolean removePoint(Point mousePoint) {
		if(point1 != null && point1.distance(mousePoint) < 10) {
			point1 = null;
			return true;
		}
		else if(point2 != null && point2.distance(mousePoint) < 10) {
			point2 = null;
			return true;
		}
		
		return false;
	}

	@Override
	public boolean keyboardInteraction(int key) {
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

	@Override
	public void draw(Graphics2D g, int panX, int panY) {
		if(this.typeSelected) {
			if(this.selected) {
				g.setColor(Color.orange);
				
				if(point1 != null)
					g.drawOval(this.point1.x + panX - 3, this.point1.y + panY - 3, 6, 6);
				
				if(point2 != null)
					g.drawOval(this.point2.x + panX - 3, this.point2.y + panY - 3, 6, 6);
				
				g.setColor(Color.cyan);
			}
			else
				g.setColor(Color.red);
			
			g.drawOval(this.getX() + panX - 3, this.getY() + panY - 3, 6, 6);
		}
		else {
			g.setColor(Color.orange);
			
			g.fillOval(this.getX() + panX - 3, this.getY() + panY - 3, 6, 6);
		}
		
	}

	@Override
	public String getScript() {
		if(this.point1 == null || this.point2 == null)
			return "";
		
		StringBuffer sb = new StringBuffer();
		
		if(this.varName.isEmpty())
			sb.append("PORTAL {\n");
		else
			sb.append("PORTAL::\"" + this.varName + "\"{\n");
		
		sb.append("\tx: " + this.getX() + ", " + this.point1.x + ", " + this.point2.x + "\n");
		sb.append("\ty: " + this.getY() + ", " + this.point1.y + ", " + this.point2.y + "\n");
		
		sb.append(this.additionalCode + "\n}");
		
		return sb.toString();
	}

}
