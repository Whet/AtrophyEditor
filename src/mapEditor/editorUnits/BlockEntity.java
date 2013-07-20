package mapEditor.editorUnits;

import java.awt.Polygon;
import java.util.List;

public class BlockEntity extends MapEntity {

	private List<Integer[]> points;
	private Polygon polygon;
	
	public BlockEntity(int x, int y) {
		super(MapEntityType.BLOCK, x, y);
		
		this.polygon = new Polygon();
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

	public void removePoint(int x, int y) {
		for (Integer[] point : points) {
			if(point[0] == x && point[1] == y) {
				this.points.remove(point);
				return;
			}
		}
	}
	
}
