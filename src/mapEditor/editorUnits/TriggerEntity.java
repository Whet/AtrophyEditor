package mapEditor.editorUnits;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import mapEditor.PreviewPane;

public class TriggerEntity extends MapEntity {

	public TriggerEntity(int x, int y) {
		super(MapEntityType.TRIGGER, x, y);
	}

	@Override
	public boolean contains(Point mousePoint) {
		return mousePoint.distance(new Point(this.getX(), this.getY())) < 10;
	}

	@Override
	public boolean mouseInteraction(Point mousePoint, int mouseButton) {
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
				g.setColor(Color.cyan);
			}
			else
				g.setColor(Color.blue);
		
			g.drawOval(this.getX() + panX - 3, this.getY() + panY - 3, 6, 6);
		}
		else
			g.setColor(Color.gray);
		
		g.drawLine(this.getX() + panX, this.getY() + panY, this.getX() + panX, this.getY() + panY - 14);
		g.fillRect(this.getX() + panX, this.getY() + panY - 14, 10, 5);
	}

	@Override
	public String getScript() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("TRIGGER::\"" + this.varName + "\" {\n");
		sb.append(this.additionalCode + "\n}");
		
		return sb.toString();
	}

}
