package com.me.breakingwalls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Array;

public class MapManager implements GestureListener {
	private ObjectMap<String, MapLayer> mapLayers;
	private String currentMapLayer;
	private int viewWidth;
	private int viewHeight;
	private String mapPack;
	
	private boolean isVisible;
	private AssetManager manager;
	private TextureAtlas objectAtlas;
	
	private InputMultiplexer gameInputs;
	
	private float originalZoomDistance;
	private float originalZoom;

	public MapManager(int width, int height){
		// load first layer by default
		viewWidth = width;
		viewHeight = height;
		currentMapLayer = "";
		manager = new AssetManager();
		mapLayers = new ObjectMap<String, MapLayer>();
		
		originalZoomDistance = 0f;
		originalZoom = 0f;
	}
	
	
	public void  LoadMap(String mapName){
		JsonValue root = new JsonReader().parse(Gdx.files.internal(mapName));
		
		
		
		for(JsonValue entry = root.child.child; entry != null; entry = entry.next ){
			if(entry.name.equals("mapPack")){
				mapPack = entry.asString();
				manager.load(mapPack, TextureAtlas.class);
				manager.finishLoading();
				objectAtlas = manager.get(mapPack, TextureAtlas.class);
			} else if(entry.name.equals("layer")){
				ProcessLayer(entry);
			}
		}
	}
	
	public void ProcessLayer(JsonValue layer){
		MapLayer newLayer = new MapLayer(layer.getInt("width"), layer.getInt("height"), viewWidth, viewHeight);
		
		for(JsonValue entry = layer.child; entry != null; entry = entry.next){
			if(entry.name.equals("background")){
				newLayer.SetMapBackground(new Image(objectAtlas.findRegion(entry.asString())));
			} else if(entry.name.equals("object")){
				ProcessObject(entry, newLayer);
			}
		}
		
		mapLayers.put(layer.getString("name"),newLayer);
	}
	
	public void ProcessObject(final JsonValue mapObject, MapLayer newLayer){
		MapObject newObject = new MapObject(mapObject.getString("name"), new Image(objectAtlas.findRegion(mapObject.getString("image"))));
		
		newObject.GetImage().setX(mapObject.getInt("pos-x"));
		newObject.GetImage().setY(mapObject.getInt("pos-y"));
		
		for(JsonValue entry = mapObject.child; entry != null; entry = entry.next){
			if(entry.name.equals("type")){
				if(entry.asString().equals("layer-transition")){
					newObject.RegisterCallback(new ClickListener(){
						public void clicked(InputEvent event, float x, float y){
							SwitchCurrentLayer(mapObject.getString("location"));
						}
					});
				} else if (entry.asString().equals("dialog")){
					// add callback to create a dialog box
				}
			}
		}
		
		newLayer.AddMapObject(newObject);
	}
	
	public void RegisterInputMultiplexer(InputMultiplexer gameMultiplexer){
		gameInputs = gameMultiplexer;
		
	}
	
	public Stage GetCurrentStage(){
		return mapLayers.get(currentMapLayer).GetLayerStage();
	}
	
	public void SetCurrentLayer(String layerName){
		//gameInputs.removeProcessor(GetCurrentStage());
		currentMapLayer = layerName;
		//gameInputs.addProcessor(GetCurrentStage());
	}
	
	public void SwitchCurrentLayer(String layerName){
		gameInputs.removeProcessor(GetCurrentStage());
		currentMapLayer = layerName;
		gameInputs.addProcessor(GetCurrentStage());
	}
	
	public MapLayer GetCurrentLayer(){
		return mapLayers.get(currentMapLayer);
	}
	
	
	public void ShowMap(){
		isVisible = true;
	}
	
	public void HideMap(){
		isVisible = false;
	}
	
	public void DrawMap(float delta){
		if(isVisible){
			mapLayers.get(currentMapLayer).DrawMapLayer(delta);
		}
	}
	
	
	
	

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		GetCurrentLayer().GetCurrentCamera().translate(-deltaX, deltaY);
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		//GetCurrentLayer().GetCurrentCamera().zoom += initialDistance - distance;
		//float ratio = initialDistance / distance;
		
		//GetCurrentLayer().GetCurrentCamera().zoom *= ratio;
		
		
		if(initialDistance != originalZoomDistance){
			originalZoomDistance = initialDistance;
			originalZoom = GetCurrentLayer().GetCurrentCamera().zoom;
		}
		
		GetCurrentLayer().GetCurrentCamera().zoom = originalZoom * initialDistance/distance;
		
		
		if(GetCurrentLayer().GetCurrentCamera().zoom < 0.5f){
			GetCurrentLayer().GetCurrentCamera().zoom = 0.5f;
		}
		if(GetCurrentLayer().GetCurrentCamera().zoom > 1.5f){
			GetCurrentLayer().GetCurrentCamera().zoom = 1.5f;
		}
		
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		
		
		return false;
	}
	
}
