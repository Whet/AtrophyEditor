package mapEditor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import mapEditor.editorUnits.BlockEntity;
import mapEditor.editorUnits.MapEntity;
import mapEditor.editorUnits.MapEntityType;
import mapEditor.editorUnits.MapInfo;
import mapEditor.editorUnits.PortalEntity;

public class MapData {

	private MapInfo mapInfo;
	private List<MapEntity> entities;
	
	private PreviewPane previewPane;
	private ScriptPane scriptPane;
	
	public MapData(PreviewPane previewPane, ScriptPane scriptPane) {
		this.mapInfo = new MapInfo();
		this.entities = new ArrayList<>();
		
		this.previewPane = previewPane;
		this.scriptPane = scriptPane;
	}
	
	private String getScript() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.mapInfo.getScript());
		
		sb.append("\n\n");
		
		for(MapEntity entity: entities) {
			sb.append(entity.getScript() + "\n\n");
		}
		
		return sb.toString();
	}

	public void generateScript() {
		
		this.mapInfo.readScript(scriptPane.getScript());
		
		scriptPane.setText(this.getScript());
		
		previewPane.setMapSize(this.mapInfo.getSize());
	}

	public List<MapEntity> getEntities() {
		return entities;
	}

	public void createEntity(MapEntityType selectedType, Point mousePoint) {
		switch(selectedType) {
			case BLOCK:
				entities.add(new BlockEntity(mousePoint.x, mousePoint.y));
			break;
			case PORTAL:
				entities.add(new PortalEntity(mousePoint.x, mousePoint.y));
			break;
			default:
			break;
		}
	}

	public void removeEntity(MapEntity selectedEntity) {
		this.entities.remove(selectedEntity);
	}
	
}
