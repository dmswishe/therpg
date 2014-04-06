package com.me.breakingwalls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.Vector;

public class MapLayer{
	private Image mapBackground;
	private ObjectMap<String, MapObject> mapObjects;
	private Stage layerStage;
	private OrthographicCamera layerCamera;
	
	// Camera Management
	private int cameraX;
	private int cameraY;
	private int viewPortWidth;
	private int viewPortHeight;
	
	// Map size information
	private int mapWidth;
	private int mapHeight;
	
	public MapLayer(int width, int height, int viewWidth, int viewHeight){
		// set up map size
		mapWidth = width;
		mapHeight = height;
		
		// center camera to middle of map by default...not good for dungeons
		cameraX = mapWidth / 2;
		cameraY = mapHeight / 2;
		
		viewPortWidth = viewWidth;
		viewPortHeight = viewHeight;
		
		layerCamera =  new OrthographicCamera((float) viewPortWidth, (float) viewPortHeight);
		layerCamera.setToOrtho(false, viewPortWidth, viewPortHeight); // possibly remove?
		layerCamera.position.set(cameraX, cameraY, 0);
		layerStage = new Stage((float) viewPortWidth, (float) viewPortHeight, true);
		layerStage.setCamera(layerCamera);
		
		mapObjects = new ObjectMap<String, MapObject>();

	}
	
	public void SetMapBackground(Image background){
		layerStage.clear(); // if we're setting the background, other objects should be cleared off too
		mapBackground = background;
		layerStage.addActor(mapBackground);
	}
	
	public void AddMapObject(MapObject objectToAdd){
		mapObjects.put(objectToAdd.GetName(), objectToAdd);
		layerStage.addActor(objectToAdd.GetImage());
	}
	
	public void RemoveMapObject(String objectName){
		MapObject objectToRemove = mapObjects.get(objectName);
		objectToRemove.GetImage().remove();
		mapObjects.remove(objectName);
	}
	
	public Stage GetLayerStage(){
		return layerStage;
	}
	
	public OrthographicCamera GetCurrentCamera(){
		return layerCamera;
	}
	
	public MapObject GetMapObject(String objectName){
		return mapObjects.get(objectName);
	}
	
	public void DrawMapLayer(float delta){
		layerStage.act(delta);
		layerStage.draw();
	}
	

}
