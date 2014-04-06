package com.me.breakingwalls;

//import java.awt.Label;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.Actor;




public class BreakingWalls implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture;
	private Sprite sprite;
	private InputMultiplexer gameInputProcessors;
	
	UISpawner uiSpawner;
	MapManager mapManager;
	Skin skin;
	Stage ui;
	Table root;
	TextureRegion mainbg;
	
	TextureRegionDrawable maleDefault, maleArcher, maleBarbarian, maleSorcerer, maleWarrior;
	TextureRegionDrawable femaleDefault, femaleArcher, femaleBarbarian, femaleSorcerer, femaleWarrior;
	NewGameStates newGameState;
	HeroGender heroGender;
	HeroClass heroClass;
	String heroName;
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		
		heroName = "";
		newGameState = NewGameStates.GENDER;
		
		gameInputProcessors =  new InputMultiplexer();
		
		
		// testing UI loading
		uiSpawner = new UISpawner("test3.pack", "data/style.json", "data/UI/ui_layout.json", w, h, false);
		
		mapManager = new MapManager((int) w, (int) h);
		mapManager.LoadMap("data/Maps/town_map.json");
		
		mapManager.SetCurrentLayer("Main");
		
		gameInputProcessors.addProcessor(uiSpawner.getUI());
		gameInputProcessors.addProcessor(new GestureDetector(mapManager));
		gameInputProcessors.addProcessor(mapManager.GetCurrentStage());
		
		mapManager.RegisterInputMultiplexer(gameInputProcessors);
		
		
		mapManager.HideMap();
		
		//Gdx.input.setInputProcessor(uiSpawner.getUI());
		Gdx.input.setInputProcessor(gameInputProcessors);
		
		// add callbacks
		
		// new game
		uiSpawner.RegisterCallback("newgamebutton", "newgame", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				uiSpawner.uiWidgets.get("mainmenu").setVisible(false);
				uiSpawner.uiWidgets.get("newgame").setVisible(true);
			}
		});
		
		// continue
		uiSpawner.RegisterCallback("continuebutton", "continue", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				
			}
		});
		
		// options
		uiSpawner.RegisterCallback("optionbutton", "options", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				uiSpawner.uiWidgets.get("mainmenu").setVisible(false);
				uiSpawner.uiWidgets.get("options").setVisible(true);
			}
		});
		
		// exit
		uiSpawner.RegisterCallback("exitbutton", "exit", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				Gdx.app.exit();
			}
		});
		
		//male
		uiSpawner.RegisterCallback("maleButton", "selectMale", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Image)uiSpawner.uiWidgets.get("characterPane")).setDrawable(uiSpawner.getSkin(), "male");
				heroGender = HeroGender.MALE;
				if(heroGender != null)
					((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(false);
			}
		});
		
		//female
		uiSpawner.RegisterCallback("femaleButton", "selectFemale", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				((Image)uiSpawner.uiWidgets.get("characterPane")).setDrawable(uiSpawner.getSkin(), "female");
				heroGender = HeroGender.FEMALE;
				if(heroGender != null)
					((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(false);
			}
		});
		
		//archer
		uiSpawner.RegisterCallback("archer", "archer", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				switch(heroGender){
				case MALE:
					((Image)uiSpawner.uiWidgets.get("characterPane")).setDrawable(uiSpawner.getSkin(), "male-archer");
					heroClass = HeroClass.ARCHER;
					if(heroClass != null)
						((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(false);
					break;
				case FEMALE:
					((Image)uiSpawner.uiWidgets.get("characterPane")).setDrawable(uiSpawner.getSkin(), "female-archer");
					heroClass = HeroClass.ARCHER;
					if(heroClass != null)
						((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(false);
					break;
				}
			}
		});
		
		// barbarian
		uiSpawner.RegisterCallback("barbarian", "barbarian", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				switch(heroGender){
				case MALE:
					((Image)uiSpawner.uiWidgets.get("characterPane")).setDrawable(uiSpawner.getSkin(), "male-barbarian");
					heroClass = HeroClass.BARBARIAN;
					if(heroClass != null)
						((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(false);
					break;
				case FEMALE:
					((Image)uiSpawner.uiWidgets.get("characterPane")).setDrawable(uiSpawner.getSkin(), "female-barbarian");
					heroClass = HeroClass.BARBARIAN;
					if(heroClass != null)
						((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(false);
					break;
				}
			}
		});
		
		//sorcerer
		uiSpawner.RegisterCallback("sorcerer", "sorcerer", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				switch(heroGender){
				case MALE:
					((Image)uiSpawner.uiWidgets.get("characterPane")).setDrawable(uiSpawner.getSkin(), "male-sorcerer");
					heroClass = HeroClass.SORCERER;
					if(heroClass != null)
						((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(false);
					break;
				case FEMALE:
					((Image)uiSpawner.uiWidgets.get("characterPane")).setDrawable(uiSpawner.getSkin(), "female-sorcerer");
					heroClass = HeroClass.SORCERER;
					if(heroClass != null)
						((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(false);
					break;
				}
			}
		});
		
		//warrior
		uiSpawner.RegisterCallback("warrior", "warrior", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				switch(heroGender){
				case MALE:
					((Image)uiSpawner.uiWidgets.get("characterPane")).setDrawable(uiSpawner.getSkin(), "male-warrior");
					heroClass = HeroClass.WARRIOR;
					if(heroClass != null)
						((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(false);
					break;
				case FEMALE:
					((Image)uiSpawner.uiWidgets.get("characterPane")).setDrawable(uiSpawner.getSkin(), "female-warrior");
					heroClass = HeroClass.WARRIOR;
					if(heroClass != null)
						((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(false);
					break;
				}
			}
		});
		
		//next
		uiSpawner.RegisterCallback("navNext", "navNext", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				switch(newGameState){
				case GENDER:
					if(heroGender != null){
						uiSpawner.uiWidgets.get("class").setVisible(true);
						uiSpawner.uiWidgets.get("gender").setVisible(false);
						newGameState = NewGameStates.CLASS;
						((Button)uiSpawner.uiWidgets.get("navBack")).setDisabled(false);
						((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(true);
					}
					break;
				case CLASS:
					if(heroClass != null){
						uiSpawner.uiWidgets.get("name").setVisible(true);
						uiSpawner.uiWidgets.get("class").setVisible(false);
						newGameState = NewGameStates.NAME;
						//nextNavButton.setDisabled(true);
					}
					break;
				case NAME:
					heroName = ((TextField)uiSpawner.uiWidgets.get("nameText")).getText();
					if(heroName != ""){
						((Label)uiSpawner.uiWidgets.get("reviewName")).setText("Name: "+ heroName);
						uiSpawner.uiWidgets.get("review").setVisible(true);
						uiSpawner.uiWidgets.get("name").setVisible(false);
						newGameState = NewGameStates.REVIEW;
						switch(heroClass){
						case ARCHER:
							((Label)uiSpawner.uiWidgets.get("reviewClass")).setText("Class: Archer");
							break;
						case BARBARIAN:
							((Label)uiSpawner.uiWidgets.get("reviewClass")).setText("Class: Barbarian");
							break;
						case SORCERER:
							((Label)uiSpawner.uiWidgets.get("reviewClass")).setText("Class: Sorcerer");
							break;
						case WARRIOR:
							((Label)uiSpawner.uiWidgets.get("reviewClass")).setText("Class: Warrior");
							break;
						}
						switch(heroGender){
						case MALE:
							((Label)uiSpawner.uiWidgets.get("reviewGender")).setText("Gender: Male");
							break;
						case FEMALE:
							((Label)uiSpawner.uiWidgets.get("reviewGender")).setText("Gender: Female");
							break;
						}
					}
					//((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(true);
					break;
				case REVIEW:
					//((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(true);
					
					uiSpawner.uiWidgets.get("newgame").setVisible(false);
					mapManager.ShowMap();
					
					break;
				}
			}
		});
		
		//back
		uiSpawner.RegisterCallback("navBack", "navBack", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				switch(newGameState){
				case GENDER:
					((Button)uiSpawner.uiWidgets.get("navBack")).setDisabled(true);
					break;
				case CLASS:
					uiSpawner.uiWidgets.get("gender").setVisible(true);
					uiSpawner.uiWidgets.get("class").setVisible(false);
					newGameState = NewGameStates.GENDER;
					((Button)uiSpawner.uiWidgets.get("navBack")).setDisabled(true);
					break;
				case NAME:
					uiSpawner.uiWidgets.get("class").setVisible(true);
					uiSpawner.uiWidgets.get("name").setVisible(false);
					newGameState = NewGameStates.CLASS;
					break;
				case REVIEW:
					uiSpawner.uiWidgets.get("name").setVisible(true);
					uiSpawner.uiWidgets.get("review").setVisible(false);
					newGameState = NewGameStates.NAME;
					((Button)uiSpawner.uiWidgets.get("navNext")).setDisabled(false);
					break;
				}
			}
		});
	
		//option back
		uiSpawner.RegisterCallback("optionBack", "optionBack", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				uiSpawner.uiWidgets.get("mainmenu").setVisible(true);
				uiSpawner.uiWidgets.get("options").setVisible(false);
			}
		});
		
		//to menu
		uiSpawner.RegisterCallback("newgameBack", "newgameBack", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				uiSpawner.uiWidgets.get("mainmenu").setVisible(true);
				uiSpawner.uiWidgets.get("newgame").setVisible(false);
			}
		});
		
		//mute
		uiSpawner.RegisterCallback("mutebutton", "mute", new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				
			}
		});
	}

	@Override
	public void dispose() {
		batch.dispose();
		//texture.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//sprite.draw(batch);
		//ui.act(Gdx.graphics.getDeltaTime());
		//ui.draw();
		//Table.drawDebug(ui);
		uiSpawner.RenderWidgets(Gdx.graphics.getDeltaTime());
		mapManager.DrawMap(Gdx.graphics.getDeltaTime());
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
