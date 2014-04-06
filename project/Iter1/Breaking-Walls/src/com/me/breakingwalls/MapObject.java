package com.me.breakingwalls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.Vector;

public class MapObject{
	private Image objectImage;
	private String objectName;
	
	public MapObject(String name, Image image){
		objectName = name;
		objectImage = image;
	}
	
	public Image GetImage(){
		return objectImage;
	}
	
	public String GetName(){
		return objectName;
	}
	
	public void RegisterCallback(EventListener callback){
		objectImage.addListener(callback);
	}
}
