package mapEditor.editorUnits;


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
			sb.append("cover {\n");
		else
			sb.append("cover::\"" + this.varName + "\" {\n");
		
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
		
		sb.append("\n" + this.additionalCode + "\n}");
		
		return sb.toString();
	}

}
