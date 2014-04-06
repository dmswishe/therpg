package com.me.breakingwalls;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonValue;
import java.util.Map;

public class UISpawner {
	private Skin skin;
	private TextureAtlas atlas;
	public Stage ui;
	public ObjectMap<String, Actor> uiWidgets;
	public ObjectMap<String, EventListener> uiCallBacks;
	public ObjectMap<String, TextureRegionDrawable> uiDrawables;
	private float width, height;
	
	public UISpawner(String texturePack, String style, String layout, float width, float height, boolean keepAspect) {
		AssetManager manager = new AssetManager();
		manager.load(texturePack, TextureAtlas.class);
		manager.finishLoading();
		atlas = manager.get(texturePack, TextureAtlas.class);
		skin = new Skin(Gdx.files.internal(style), atlas);
		ui = new Stage(width, height, keepAspect);
		this.width = width;
		this.height = height;
		
		uiWidgets = new ObjectMap<String, Actor>();
		uiCallBacks = new ObjectMap<String, EventListener>();
		uiDrawables = new ObjectMap<String, TextureRegionDrawable>();
		
		// Load Widgets
		JsonValue root = new JsonReader().parse(Gdx.files.internal(layout));
		ProcessChild(root, root.child);
		//int temp = uiWidgets.size;
	}
	
	public void RegisterCallback(String widget, String callBackName, EventListener callBack){
		Actor uiWidget = uiWidgets.get(widget);
		
		uiCallBacks.put(callBackName, callBack);
		uiWidget.addListener(callBack);
	}
	
	
	// Creates a UI Widget according to ui layout file
	// Assumptions made: 1) only WidgetGroups have child actors, otherwise it's a table of some sort
	// 2) ???
	private void ProcessChild(JsonValue parent, JsonValue child){
		// A sub-widget, add to parent appropriately
		for(JsonValue entry = child.child; entry != null; entry = entry.next){
			if(entry.name.equals("type")){
				if(entry.asString().equals("Table")){
					Table widget = new Table();
					widget.debug();
					uiWidgets.put(child.name, widget);
				}else if(entry.asString().equals("Button")){
					if(child.get("style") != null){
						uiWidgets.put(child.name, new Button(skin, child.get("style").asString()));
					}else{
						uiWidgets.put(child.name, new Button(skin));
					}
				}else if(entry.asString().equals("TextButton")){
					if(child.get("style") != null){
						uiWidgets.put(child.name, new TextButton("", skin, child.get("style").asString()));
					}else{
						uiWidgets.put(child.name, new TextButton("", skin));
					}
				}else if(entry.asString().equals("Label")){
					if(child.get("style") != null){
						uiWidgets.put(child.name, new Label("", skin, child.get("style").asString()));
					}else{
						uiWidgets.put(child.name, new Label("", skin));
					}
				}else if(entry.asString().equals("CheckBox")){
					if(child.get("style") != null){
						uiWidgets.put(child.name, new CheckBox("", skin, child.get("style").asString()));
					}else{
						uiWidgets.put(child.name, new CheckBox("", skin));
					}
				}else if(entry.asString().equals("Slider")){
					if(child.get("style") != null){
						uiWidgets.put(child.name, new Slider(0,0,1,false, skin, child.get("style").asString()));
					}else{
						uiWidgets.put(child.name, new Slider(0,0,1,false, skin));
					}
				}else if(entry.asString().equals("SelectBox")){
					if(child.get("style") != null){
						uiWidgets.put(child.name, new SelectBox(child.get("items").asString().split(","), skin, child.get("style").asString()));
					}else{
						uiWidgets.put(child.name, new SelectBox(child.get("items").asString().split(","), skin));
					}
				}else if(entry.asString().equals("TextField")){
					if(child.get("style") != null){
						uiWidgets.put(child.name, new TextField("", skin, child.get("style").asString()));
					}else{
						uiWidgets.put(child.name, new TextField("", skin));
					}
				}else if(entry.asString().equals("Image")){
					uiWidgets.put(child.name, new Image());
				}
			}else if(entry.name.equals("fullscreen")){
				if(entry.asBoolean() == true){
					((WidgetGroup)uiWidgets.get(child.name)).setFillParent(true);
				}
			} else if(entry.name.equals("isActor")){
				if(entry.asBoolean() == true){
					// check if root widget
					if(parent.name != null){
						((WidgetGroup)uiWidgets.get(parent.name)).addActor(uiWidgets.get(child.name));
					}else{
						ui.addActor(uiWidgets.get(child.name));
					}
				}else{
					((Table)uiWidgets.get(parent.name)).add(uiWidgets.get(child.name));
				}
				
			} else if(entry.name.equals("space")){
				if(child.get("type") != null){
					if(child.get("type").asString().equals("Table")){
						((Table)uiWidgets.get(child.name)).defaults().space(entry.asFloat());
					}else{
						((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).space(entry.asFloat());
					}
				}
			} else if(entry.name.equals("spaceTop")){
				if(child.get("type") != null){
					if(child.get("type").asString().equals("Table")){
						((Table)uiWidgets.get(child.name)).defaults().spaceTop(entry.asFloat());
					}else{
						((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).spaceTop(entry.asFloat());
					}
				}
			} else if(entry.name.equals("spaceBottom")){
				if(child.get("type") != null){
					if(child.get("type").asString().equals("Table")){
						((Table)uiWidgets.get(child.name)).defaults().spaceBottom(entry.asFloat());
					}else{
						((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).spaceBottom(entry.asFloat());
					}
				}
			} else if(entry.name.equals("spaceLeft")){
				if(child.get("type") != null){
					if(child.get("type").asString().equals("Table")){
						((Table)uiWidgets.get(child.name)).defaults().spaceLeft(entry.asFloat());
					}else{
						((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).spaceLeft(entry.asFloat());
					}
				}
			} else if(entry.name.equals("spaceRight")){
				if(child.get("type") != null){
					if(child.get("type").asString().equals("Table")){
						((Table)uiWidgets.get(child.name)).defaults().spaceRight(entry.asFloat());
					}else{
						((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).spaceRight(entry.asFloat());
					}
				}
			} else if(entry.name.equals("pad")){
				((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).pad(entry.asFloat());
			} else if(entry.name.equals("padLeft")){
				((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).padLeft(entry.asFloat());
			} else if(entry.name.equals("padRight")){
				((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).padRight(entry.asFloat());
			} else if(entry.name.equals("padTop")){
				((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).padTop(entry.asFloat());
			} else if(entry.name.equals("padBottom")){
				((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).padBottom(entry.asFloat());
			} else if(entry.name.equals("align")){
				if(entry.asString().equals("center")){
					((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).center();
				} else if(entry.asString().equals("top")){
					((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).top();
				} else if(entry.asString().equals("bottom")){
					((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).bottom();
				} else if(entry.asString().equals("left")){
					((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).left();
				} else if(entry.asString().equals("right")){
					((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).right();
				}
			} else if(entry.name.equals("width")){
				
			} else if(entry.name.equals("height")){
				
			} else if(entry.name.equals("lastcell")){
				((Table)uiWidgets.get(parent.name)).row();
			} else if(entry.name.equals("text")){
				if(child.get("type") != null){
					if(child.get("type").asString().equals("Label")){
						((Label)uiWidgets.get(child.name)).setText(entry.asString());
					} else if(child.get("type").asString().equals("TextButton")){
						((TextButton)uiWidgets.get(child.name)).setText(entry.asString());
					} else if(child.get("type").asString().equals("TextField")){
						((TextField)uiWidgets.get(child.name)).setText(entry.asString());
					} else if(child.get("type").asString().equals("CheckBox")){
						((CheckBox)uiWidgets.get(child.name)).setText(entry.asString());
					}
				}
			} else if(entry.name.equals("message")){
				((TextField)uiWidgets.get(child.name)).setMessageText(entry.asString());
			} else if(entry.name.equals("min")){
				((Slider)uiWidgets.get(child.name)).setRange(entry.asFloat(), ((Slider)uiWidgets.get(child.name)).getMaxValue());
			} else if(entry.name.equals("max")){
				((Slider)uiWidgets.get(child.name)).setRange(((Slider)uiWidgets.get(child.name)).getMinValue(), entry.asFloat());
			} else if(entry.name.equals("step")){
				((Slider)uiWidgets.get(child.name)).setStepSize(entry.asFloat());
			} else if(entry.name.equals("vertical")){
				if(entry.asBoolean() == true){
					float min = ((Slider)uiWidgets.get(child.name)).getMinValue();
					float max = ((Slider)uiWidgets.get(child.name)).getMaxValue();
					float step =  ((Slider)uiWidgets.get(child.name)).getStepSize();
					if(child.get("style") != null){
						uiWidgets.put(child.name, new Slider(min, max, step, true, skin, child.get("style").asString()));
					}else{
						uiWidgets.put(child.name, new Slider(min, max, step, true, skin));
					}
				}
			} else if(entry.name.equals("disabled")){
				((Button)uiWidgets.get(child.name)).setDisabled(entry.asBoolean());
			} else if(entry.name.equals("isVisible")){
				uiWidgets.get(child.name).setVisible(entry.asBoolean());
			} else if(entry.name.equals("drawable")){
				((Image)uiWidgets.get(child.name)).setDrawable(skin, entry.asString());
			} else if(entry.name.equals("fillparent")){
				((Table)uiWidgets.get(child.name)).setFillParent(entry.asBoolean());
			} else if(entry.name.equals("colspan")){
				((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).colspan(entry.asInt());
			} else if(entry.name.equals("minWidth")){
				if(entry.isString()){
					((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).minWidth(width);
				}else{
					((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).minWidth(entry.asFloat());
				}
			} else if(entry.name.equals("maxWidth")){
				if(entry.isString()){
					((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).maxWidth(width);
				}else{
					((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).maxWidth(entry.asFloat());
				}
			} else if(entry.name.equals("minHeight")){
				if(entry.isString()){
					((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).minHeight(height);
				}else{
					((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).minHeight(entry.asFloat());
				}
			} else if(entry.name.equals("maxHeight")){
				if(entry.isString()){
					((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).maxHeight(height);
				}else{
					((Table)uiWidgets.get(parent.name)).getCell(uiWidgets.get(child.name)).maxHeight(entry.asFloat());
				}
			} else if(entry.name.equals("background")){
				((Table)uiWidgets.get(child.name)).setBackground(new TextureRegionDrawable(atlas.findRegion(entry.asString())));
			} else if(entry.name.equals("children")){
				for(JsonValue subWidget = entry.child; subWidget != null; subWidget = subWidget.next){
					ProcessChild(child,subWidget);
				}
			}

		}
	}
	
	public Skin getSkin(){
		return skin;
	}
	
	public Stage getUI(){
		return ui;
	}
	
	public void RenderWidgets(float delta){
		ui.act(delta);
		ui.draw();
		//Table.drawDebug(ui);
	}
}
