package com.me.breakingwalls;

//import java.awt.Label;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
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
		
		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		heroName = "";
		//TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);
		
		/*sprite = new Sprite(region);
		sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
		*/
		
		// load asset manager and skin
		AssetManager manager = new AssetManager();
		manager.load("test3.pack", TextureAtlas.class);
		manager.finishLoading();
		TextureAtlas atlas = manager.get("test3.pack", TextureAtlas.class);
		skin = new Skin(Gdx.files.internal("data/style.json"), atlas);
		//skin = new Skin();
		
		maleDefault = new TextureRegionDrawable(atlas.findRegion("male"));
		maleArcher = new TextureRegionDrawable(atlas.findRegion("male-archer"));
		maleBarbarian = new TextureRegionDrawable(atlas.findRegion("male-barbarian"));
		maleSorcerer = new TextureRegionDrawable(atlas.findRegion("male-sorcerer"));
		maleWarrior = new TextureRegionDrawable(atlas.findRegion("male-warrior"));
		
		femaleDefault = new TextureRegionDrawable(atlas.findRegion("female"));
		femaleArcher = new TextureRegionDrawable(atlas.findRegion("female-archer"));
		femaleBarbarian = new TextureRegionDrawable(atlas.findRegion("female-barbarian"));
		femaleSorcerer = new TextureRegionDrawable(atlas.findRegion("female-sorcerer"));
		femaleWarrior = new TextureRegionDrawable(atlas.findRegion("female-warrior"));
		//sprite = new Sprite(atlas.findRegion("mainbg"));
		
		// create GUI
		ui = new Stage(w,h, false);
		Gdx.input.setInputProcessor(ui);
		//mainbg = new TextureRegion(new Texture(Gdx.files.internal("data/mainbg.png")));
		//mainbg = atlas.findRegion("mainbg");
		
		root = new Table();
		root.setFillParent(true);
		ui.addActor(root);
		root.debug();
		
		//Image image = new Image(mainbg);
		//image.setScaling(Scaling.fill);
		//root.add(image).width(w).height(h);
		

		
		root.defaults().spaceBottom(10.0f);
		TextButton newGame = new TextButton("New Game", skin);
		root.add(newGame);
		root.row();
		
		TextButton continueGame = new TextButton("Continue", skin);
		root.add(continueGame);
		root.row();
		
		TextButton gameOptions = new TextButton("Options", skin);
		root.add(gameOptions);
		root.row();
		
		TextButton exitGame = new TextButton("Exit", skin);
		root.add(exitGame);
		
		exitGame.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				Gdx.app.exit();
			}
		});
		
		
		final Window optionWindow = new Window("", skin);
		optionWindow.setPosition(100,80);
		//optionWindow.setFillParent(true);
		optionWindow.defaults().spaceBottom(6.0f);
		optionWindow.row().fill().expandX();
		//optionWindow.setModal(true);
		optionWindow.debug();
		
		
		// vibration checkbox option
		Label vibrationLabel = new Label("Vibration", skin);
		CheckBox vibrationFeature = new CheckBox("", skin);
		optionWindow.add(vibrationLabel);
		optionWindow.add(vibrationFeature);
		optionWindow.row();
		
		// Music Volume
		Label musicVolLable = new Label("Music Volume: ",skin);
		Slider musicVolSlider = new Slider(0, 11, 1, false, skin);
		optionWindow.add(musicVolLable);
		optionWindow.add(musicVolSlider);
		optionWindow.row();
		
		
		// SFX Volume
		Label sfxVolLable = new Label("SFX Volume: ",skin);
		Slider sfxVolSlider = new Slider(0, 11, 1, false, skin);
		optionWindow.add(sfxVolLable);
		optionWindow.add(sfxVolSlider);
		optionWindow.row();
		
		// Scroll Speed
		Label scrollSpeedLabel = new Label("Scroll Speed: ", skin);
		SelectBox scrollSpeedBox = new SelectBox(new String[] {"Slow", "Medium", "Fast", "Instant"}, skin);
		optionWindow.add(scrollSpeedLabel);
		optionWindow.add(scrollSpeedBox);
		optionWindow.row();
		
		// back button
		TextButton backButton =  new TextButton("Back", skin);
		backButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				optionWindow.setVisible(false);
				root.setVisible(true);
			}
		});
		optionWindow.add(backButton).center().colspan(2);
		
		optionWindow.pack();
		
		ui.addActor(optionWindow);
		optionWindow.setVisible(false);
		
		// option window switch
		gameOptions.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				root.setVisible(false);
				optionWindow.setVisible(true);
			}
		});
		
		// new game menu
		final Table newGameMenu = new Table();
		newGameMenu.setFillParent(true);
		ui.addActor(newGameMenu);
		newGameMenu.debug();
		
		newGameMenu.setVisible(false);
		
		newGameState = NewGameStates.GENDER;
		
		newGame.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				root.setVisible(false);
				newGameMenu.setVisible(true);
			}
		});
		
		// Form Section
		Table formSection = new Table();
		newGameMenu.add(formSection);
	
		// Form Section: Gender
		final Table genderSelection = new Table();
		genderSelection.defaults().spaceBottom(10);
		formSection.addActor(genderSelection);
		genderSelection.setFillParent(true);
		
		Label genderSelectionLabel = new Label("Gender", skin, "title-text");
		genderSelection.add(genderSelectionLabel);
		genderSelection.row();
		
		TextButton maleSelect = new TextButton("Male", skin, "toggle");
		genderSelection.add(maleSelect);
		genderSelection.row();
		
		TextButton femaleSelect = new TextButton("Female", skin, "toggle");
		genderSelection.add(femaleSelect);
		
		
		// Form Section: Class
		final Table classSelection = new Table();
		classSelection.defaults().spaceBottom(10);
		formSection.addActor(classSelection);
		classSelection.setFillParent(true);
		
		Label classSelectionLabel = new Label("Class", skin, "title-text");
		classSelection.add(classSelectionLabel);
		classSelection.row();
		
		TextButton archerSelect = new TextButton("Archer", skin, "toggle");
		classSelection.add(archerSelect);
		classSelection.row();
		
		TextButton barbarianSelect = new TextButton("Barbarian", skin, "toggle");
		classSelection.add(barbarianSelect);
		classSelection.row();
		
		TextButton sorcererSelect = new TextButton("Sorcerer", skin, "toggle");
		classSelection.add(sorcererSelect);
		classSelection.row();
		
		TextButton warriorSelect = new TextButton("Warrior", skin, "toggle");
		classSelection.add(warriorSelect);
		
		classSelection.setVisible(false);
		
		// Form Section: Name
		final Table nameSelection = new Table();
		nameSelection.defaults().spaceBottom(10);
		formSection.addActor(nameSelection);
		nameSelection.setFillParent(true);
		
		Label nameSelectionLabel = new Label("Name", skin, "title-text");
		nameSelection.add(nameSelectionLabel);
		nameSelection.row();
		
		Label nameLabel =  new Label("Name: ", skin);
		nameSelection.add(nameLabel);
		
		final TextField nameTextField = new TextField("", skin);
		nameTextField.setMessageText("Enter Name Here!");
		nameSelection.add(nameTextField);
		
		
		nameSelection.setVisible(false);
		
		// Form Section: Review
		final Table reviewCharacter = new Table();
		reviewCharacter.defaults().spaceBottom(10);
		formSection.addActor(reviewCharacter);
		reviewCharacter.setFillParent(true);
		
		Label reviewCharacterLabel = new Label("Review", skin, "title-text");
		reviewCharacter.add(reviewCharacterLabel);
		reviewCharacter.row();
		
		final Label reviewNameLabel = new Label("Name: ", skin);
		reviewCharacter.add(reviewNameLabel);
		reviewCharacter.row();
		
		final Label reviewClassLabel = new Label("Class: ", skin);
		reviewCharacter.add(reviewClassLabel);
		reviewCharacter.row();
		
		final Label reviewGenderLabel = new Label("Gender: ", skin);
		reviewCharacter.add(reviewGenderLabel);
		reviewCharacter.row();
		
		reviewCharacter.setVisible(false);
		
		
		// Character Display Function
		Table characterDisplay = new Table();
		characterDisplay.debug();
		newGameMenu.add(characterDisplay);
		
		characterDisplay.defaults().space(20);
		
		final Image charImage = new Image();
		charImage.setDrawable(maleDefault); // default to male image
		characterDisplay.add(charImage);
		
		newGameMenu.row();
		
		//heroGender = HeroGender.MALE;
		
		
		//nameTextField.addListener(new )
		
		// Navigation Section
		Table navSection = new Table();
		navSection.debug();
		newGameMenu.add(navSection).colspan(2).minWidth(w);
		
		Button mainMenuButton = new Button(skin, "to-main");
		navSection.add(mainMenuButton).left();
		
		Table navButtons = new Table();
		//navButtons.debug();
		navSection.add(navButtons).padTop(10).padBottom(10);
		
		final Button backNavButton = new Button(skin, "back-button");
		final Button nextNavButton = new Button(skin, "next-button");
		navButtons.add(backNavButton).padRight(10);
		navButtons.add(nextNavButton);
		
		
		maleSelect.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				charImage.setDrawable(maleDefault);
				heroGender = HeroGender.MALE;
				if(heroGender != null)
					nextNavButton.setDisabled(false);
			}
		});
		
		femaleSelect.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				charImage.setDrawable(femaleDefault);
				heroGender = HeroGender.FEMALE;
				if(heroGender != null)
					nextNavButton.setDisabled(false);
			}
		});
		
		archerSelect.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				switch(heroGender){
				case MALE:
					charImage.setDrawable(maleArcher);
					heroClass = HeroClass.ARCHER;
					if(heroClass != null)
						nextNavButton.setDisabled(false);
					break;
				case FEMALE:
					charImage.setDrawable(femaleArcher);
					heroClass = HeroClass.ARCHER;
					if(heroClass != null)
						nextNavButton.setDisabled(false);
					break;
				}
			}
		});
		
		barbarianSelect.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				switch(heroGender){
				case MALE:
					charImage.setDrawable(maleBarbarian);
					heroClass = HeroClass.BARBARIAN;
					if(heroClass != null)
						nextNavButton.setDisabled(false);
					break;
				case FEMALE:
					charImage.setDrawable(femaleBarbarian);
					heroClass = HeroClass.BARBARIAN;
					if(heroClass != null)
						nextNavButton.setDisabled(false);
					break;
				}
			}
		});
		
		sorcererSelect.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				switch(heroGender){
				case MALE:
					charImage.setDrawable(maleSorcerer);
					heroClass = HeroClass.SORCERER;
					if(heroClass != null)
						nextNavButton.setDisabled(false);
					break;
				case FEMALE:
					charImage.setDrawable(femaleSorcerer);
					heroClass = HeroClass.SORCERER;
					if(heroClass != null)
						nextNavButton.setDisabled(false);
					break;
				}
			}
		});
		
		warriorSelect.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				switch(heroGender){
				case MALE:
					charImage.setDrawable(maleWarrior);
					heroClass = HeroClass.WARRIOR;
					if(heroClass != null)
						nextNavButton.setDisabled(false);
					break;
				case FEMALE:
					charImage.setDrawable(femaleWarrior);
					heroClass = HeroClass.WARRIOR;
					if(heroClass != null)
						nextNavButton.setDisabled(false);
					break;
				}
			}
		});
		
		
		
		backNavButton.setDisabled(true);
		
		mainMenuButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				root.setVisible(true);
				newGameMenu.setVisible(false);
			}
		});
		
		backNavButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				switch(newGameState){
					case GENDER:
						backNavButton.setDisabled(true);
					break;
					case CLASS:
						genderSelection.setVisible(true);
						classSelection.setVisible(false);
						newGameState = NewGameStates.GENDER;
						backNavButton.setDisabled(true);
					break;
					case NAME:
						classSelection.setVisible(true);
						nameSelection.setVisible(false);
						newGameState = NewGameStates.CLASS;
					break;
					case REVIEW:
						nameSelection.setVisible(true);
						reviewCharacter.setVisible(false);
						newGameState = NewGameStates.NAME;
						nextNavButton.setDisabled(false);
					break;
				}
			}
		});
		
		nextNavButton.setDisabled(true);
		
		nextNavButton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				switch(newGameState){
					case GENDER:
						if(heroGender != null){
						genderSelection.setVisible(false);
						classSelection.setVisible(true);
						newGameState = NewGameStates.CLASS;
						backNavButton.setDisabled(false);
						nextNavButton.setDisabled(true);
						}
					break;
					case CLASS:
						if(heroClass != null){
						classSelection.setVisible(false);
						nameSelection.setVisible(true);
						newGameState = NewGameStates.NAME;
						//nextNavButton.setDisabled(true);
						}
					break;
					case NAME:
						
						heroName = nameTextField.getText();
						if(heroName != ""){
						reviewNameLabel.setText("Name: "+ heroName);
						nameSelection.setVisible(false);
						reviewCharacter.setVisible(true);
						newGameState = NewGameStates.REVIEW;
						switch(heroClass){
						case ARCHER:
							reviewClassLabel.setText("Class: Archer");
							break;
						case BARBARIAN:
							reviewClassLabel.setText("Class: Barbarian");
							break;
						case SORCERER:
							reviewClassLabel.setText("Class: Sorcerer");
							break;
						case WARRIOR:
							reviewClassLabel.setText("Class: Warrior");
							break;
						}
						switch(heroGender){
						case MALE:
							reviewGenderLabel.setText("Gender: Male");
							break;
						case FEMALE:
							reviewGenderLabel.setText("Gender: Female");
							break;
						}
						}
						nextNavButton.setDisabled(true);
					break;
					case REVIEW:
						nextNavButton.setDisabled(true);
					break;
				}
			}
		});
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//sprite.draw(batch);
		ui.act(Gdx.graphics.getDeltaTime());
		ui.draw();
		//Table.drawDebug(ui);
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
