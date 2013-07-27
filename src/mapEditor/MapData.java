package mapEditor;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import mapEditor.editorUnits.BlockEntity;
import mapEditor.editorUnits.MapEntity;
import mapEditor.editorUnits.MapEntityType;
import mapEditor.editorUnits.MapInfo;
import mapEditor.editorUnits.PortalEntity;
import mapEditor.editorUnits.TriggerEntity;

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
			case TRIGGER:
				entities.add(new TriggerEntity(mousePoint.x, mousePoint.y));
			break;
			default:
			break;
		}
	}

	public void removeEntity(MapEntity selectedEntity) {
		this.entities.remove(selectedEntity);
	}

	public void loadEntites(File selectedFile) {
		
		ObjectInputStream stream = null;
		
		try {
			stream = new ObjectInputStream(new FileInputStream(selectedFile));
			SaveStub save = (SaveStub) stream.readObject();
			this.entities = save.entities;
			this.mapInfo = save.mapInfo;
			this.scriptPane.setText(save.script);
		}
		catch (IOException | ClassCastException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally{
			try {
				stream.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveEntites(File selectedFile) {
		SaveStub save = new SaveStub(mapInfo, entities, scriptPane.getScript());
		
		ObjectOutputStream stream = null;
		
		try {
			stream = new ObjectOutputStream(new FileOutputStream(selectedFile));   
		
			stream.writeObject(save);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				stream.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	private static class SaveStub implements Serializable {
		public MapInfo mapInfo;
		public List<MapEntity> entities;
		public String script;
		
		public SaveStub(MapInfo mapInfo, List<MapEntity> entities, String script) {
			this.mapInfo = mapInfo;
			this.entities = entities;
			this.script = script;
		}
		
	}
	
}
