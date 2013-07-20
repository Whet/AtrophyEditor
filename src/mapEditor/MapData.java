package mapEditor;

import java.util.ArrayList;
import java.util.List;

import mapEditor.editorUnits.MapEntity;
import mapEditor.editorUnits.MapInfo;

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
		return this.mapInfo.getScript();
	}

	public void generateScript() {
		
		this.mapInfo.readScript(scriptPane.getScript());
		
		scriptPane.setText(this.getScript());
		
		previewPane.setMapSize(this.mapInfo.getSize());
	}
	
}
