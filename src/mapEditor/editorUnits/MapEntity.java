package mapEditor.editorUnits;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.List;

public abstract class MapEntity implements Serializable {

	protected MapEntityType type;
	private int x,y;
	protected boolean selected;
	protected boolean typeSelected;
	
	protected String varName;
	protected String additionalCode;
	
	public MapEntity(MapEntityType type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.selected = false;
		this.typeSelected = true;
		
		varName = "";
		additionalCode = "";
	}
	
	public void setVarName(String varName) {
		this.varName = varName;
	}

	public void setAdditionalCode(String additionalCode) {
		this.additionalCode = additionalCode;
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

	public String getVarName() {
		return varName;
	}

	public String getAdditionalCode() {
		return additionalCode;
	}

	public void createScopedEntity(MapEntityType scopedType, Point mousePoint) {
	}
	
	public void removeScopedEntity(MapEntity entity) {
	}

	public List<MapEntity> getEntities() {
		return null;
	}
	
}
