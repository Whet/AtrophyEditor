package mapEditor.editorUnits;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;


public class CoverEntity extends BlockEntity {

	public CoverEntity(int x, int y) {
		super(x, y);
		this.type = MapEntityType.BLOCK_REGION;
	}

	@Override
	public String getScript() {
		if(this.points.size() < 3)
			return "";
	
		StringBuffer sb = new StringBuffer();
		
		if(this.varName.isEmpty())
			sb.append("\tcover {\n");
		else
			sb.append("\tcover::\"" + this.varName + "\" {\n");
		
		sb.append("\t\tx: ");
		
		for(int i = 0; i < this.points.size(); i++) {
			sb.append(this.points.get(i)[0]);
			
			if(i + 1 < this.points.size())
				sb.append(", ");
		}
		
		sb.append("\n\t\ty: ");
		
		for(int i = 0; i < this.points.size(); i++) {
			sb.append(this.points.get(i)[1]);
			
			if(i + 1 < this.points.size())
				sb.append(", ");
		}
		
		sb.append("\n\t" + this.additionalCode + "\n\t}");
		
		return sb.toString();
	}
	
	public void draw(Graphics2D g, int panX, int panY) {
		
		if(this.typeSelected) {
			
			if(this.selected) {
				g.setColor(Color.orange);
				for(int i = 0; i < this.points.size(); i++) {
					g.drawOval(this.points.get(i)[0] + panX - 3, this.points.get(i)[1] + panY - 3, 6, 6);
				}
				g.setColor(Color.cyan);
			}
			else
				g.setColor(Color.green);
		
			g.drawOval(this.getX() + panX - 3, this.getY() + panY - 3, 6, 6);
		}
		else
			g.setColor(Color.green);
		
		AffineTransform panTransform = new AffineTransform();
		AffineTransform oldTransform = g.getTransform();
		
		// likes to draw polygon 23 units off?!?!?
		panTransform.translate(panX, panY + 23);
		g.setTransform(panTransform);
		g.drawPolygon(polygon);
		g.setTransform(oldTransform);
		
	}

}
