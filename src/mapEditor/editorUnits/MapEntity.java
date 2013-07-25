package mapEditor.editorUnits;

import java.awt.Graphics2D;
import java.awt.Point;

public abstract class MapEntity {

	private MapEntityType type;
	private int x,y;
	protected boolean selected;
	protected boolean typeSelected;
	
	public MapEntity(MapEntityType type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.selected = false;
		this.typeSelected = true;
	}
	
	public void move(int x, int y) {
		this.x += x;
		this.y += y;
	}

	public MapEntityType getType() {
		return type;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public abstract boolean contains(Point mousePoint);

	public abstract boolean mouseInteraction(Point mousePoint, int mouseButton);
	
	public abstract boolean keyboardInteraction(int key);

	public abstract void draw(Graphics2D g, int panX, int panY);
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public void setTypeSelected(MapEntityType type) {
		this.typeSelected = type == this.type;
	}

	public abstract String getScript();
	
}
