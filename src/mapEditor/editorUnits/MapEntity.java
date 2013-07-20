package mapEditor.editorUnits;

public abstract class MapEntity {

	private MapEntityType type;
	private int x,y;
	
	public MapEntity(MapEntityType type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
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
	
}
