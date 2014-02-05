package MindBang.maailma.pelitilat;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.particles.effects.FireEmitter;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import MindBang.MindBangMain;
import MindBang.maailma.Efefktit.Rajahdys;

/**
 * Pelin paattymisen pelitila.
 * **/

public class GameOverState extends BasicGameState {

	private Image mainMenuOption = null; // Valikkokuva mainmenulle
	private Image exitOption = null; // Valikkokuva poistumiselle
	private Image helpOption = null; // Valikkokuva helppiin siirtymiseen
	private Sound alkusoundi;
	private static int menuX = 100; // Valikkokuvien kiintopiste x
	private static int menuY = 690; // Valikkokuvien kiintopiste y
	private float mainMenuScale = 1; // Alla kuvien alkuskaalat. Kuvat
										// skaalautuvat suuremmiksi kun niiden
										// paalle pitaa hiirta
	private float exitScale = 1;
	private float helpScale = 1;
	private float scaleStep = 0.0001f;
	private int stateID = 0;
	private static int voittaja; // Tieto voittajasta numeroin
	private Image voitto1, voitto2; // Voittokuvat
	private ParticleSystem system, system1; // Voitto efektit
	private Rajahdys rajahdys;

	/**
	 * Luokan luontimetodissa kerrotaan pelitilalle sen tunnus numero
	 * 
	 * @param stateID
	 *            = pelitilan tunnusnumero
	 * **/

	public GameOverState(int stateID) {
		this.stateID = stateID;

	}

	/**
	 * Palauttaa pelitilan tunnusnumeron
	 * 
	 * @return pelitilan tunnusnumero
	 * **/

	public int getID() {
		return stateID;
	}

	/**
	 * Metodi joka ajetaan kun pelitilaan tullaan. Tassa asetetaan hiiren kuvake
	 * nakyvaksi. Ja kaynnistetaan tausta avaruus -efekti luomalla Rajahdys
	 * -luokka.
	 * **/

	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		container.setMouseGrabbed(false);
		alkusoundi.play(0.5f, 0.3f);
		rajahdys = new Rajahdys();
	}

	/**
	 * Init metodi lataa pelin kuvat resursseista kun peli kaynnistetaan. Seka
	 * luo voittoefektin partikkelisysteemit ja emitterit
	 * **/

	public void init(GameContainer gc, StateBasedGame arg1)
			throws SlickException {

		alkusoundi = new Sound("res/sound/holyc.ogg");
		mainMenuOption = new Image("res/valikot/MainMenu.png");
		exitOption = new Image("res/valikot/exit.png");
		helpOption = new Image("res/valikot/help.png");
		voitto1 = new Image("res/valikot/voitto1.png");
		voitto2 = new Image("res/valikot/voitto2.png");
		system = new ParticleSystem("res/munkki2voitto.png", 200);
		system1 = new ParticleSystem("res/ammus/ammus1.png", 200);
		system.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
		system1.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
		FireEmitter tuli = new FireEmitter(610, 500, 400);
		FireEmitter tuli1 = new FireEmitter(600, 150, 200);
		system1.addEmitter(tuli1);
		system.addEmitter(tuli);

	}

	/**
	 * Metodissa piirretaan pelin valikkokuvat seka voittajan hahmo seka ammus
	 * -efektit
	 * **/

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {

		// Piirretaan ammuskuvaan perustuva efekti ruudulle
		system1.render();

		// Mikali pelaaja1 voittaa
		if (voittaja == 1) {
			// Piirretaan voiton teksti
			g.drawImage(voitto1, 450, 10);
			// Asetetaan particle emitterin systeemi kayttamaan pelaaja1 hahmoa
			system.setDefaultImageName("res/munkki1voitto.png");
			// Renderoidaan pelaaja1 hahmon efekti ruudulle
			system.render();
			// Mikali pelaaja2 voittaa toistetaan tehdaan ylla olevat vaiheet
		} else if (voittaja == 2) {
			g.drawImage(voitto2, 450, 10);
			system.setDefaultImageName("res/munkki2voitto.png");
			system.render();
		}
		rajahdys.render(gc, g);

		// Piirretaan valikkokuvat omille paikoilleen
		mainMenuOption.draw(menuX, menuY, mainMenuScale);

		exitOption.draw(menuX + 900, menuY, exitScale);

		helpOption.draw(menuX + 430, menuY, helpScale);

	}

	/**
	 * Update metodi paivittaa pelin logiikkaa automaattisesti. Taman luokan
	 * updatessa kuunnellaan hiiren liikkeita jotta pelin valikkoja voi kayttaa.
	 * Lisaksi paivitetaan efekteista vastaavien partikkelisysteemien logiikka.
	 * **/

	public void update(GameContainer gc, StateBasedGame sb, int delta)
			throws SlickException {
		// paivitetaan rajahdysluokka jotta avaruustausta paivittyy
		rajahdys.update(gc, delta);
		// paivitetaan lopetusruudun efektit
		system.update(delta);
		system1.update(delta);
		// Tallennetaan hiiren kuuntelija muuttujaan
		Input input = gc.getInput();

		// Tallennetaan hiiren sijainnin x- ja y -koordinaatit
		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();

		// Totuusmuuttujat onko hiiri jonkun valikkokuvan ylla
		boolean insideMainMenu = false;
		boolean insideExit = false;
		boolean insideHelp = false;

		// Alla tarkastellaan onko hiiri jonkun valikkokuvan koordinaattien
		// ylla.
		if ((mouseX >= menuX && mouseX <= menuX + mainMenuOption.getWidth())
				&& (mouseY >= menuY && mouseY <= menuY
						+ mainMenuOption.getHeight())) {
			insideMainMenu = true;
		} else if ((mouseX >= menuX + 900 && mouseX <= menuX + 900
				+ exitOption.getWidth())
				&& (mouseY >= menuY && mouseY <= menuY + exitOption.getHeight())) {
			insideExit = true;
		} else if ((mouseX >= menuX + 430 && mouseX <= menuX + 430
				+ helpOption.getWidth())
				&& (mouseY >= menuY && mouseY <= menuY + helpOption.getHeight())) {
			insideHelp = true;
		}

		// Alla on vaihtoehdot mita tehdaan mikali ollaan jonkun valikkokuvan
		// ylla. Jokainen kuva skaalataan suuremmaksi mikali hiiri on kuvan
		// ylla. Tama helpottaa kayttajaa hahmottaan valikon kayttoa.

		if (insideMainMenu) {

			if (mainMenuScale < 1.05f)
				mainMenuScale += scaleStep * delta;
			// Mikali ollaan pelin mainkuvan paalla ja painetaan hiiren vasenta
			// painiketta, siirrytaan pelitilaan.
			if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
				sb.enterState(MindBangMain.MAINMENUSTATE);
			}
		} else if (insideExit) {
			if (exitScale < 1.05f)
				exitScale += scaleStep * delta;
			// Mikali ollaan poistumiskuvan ylla ja painetaan hiiren vasenta
			// painiketta peli lopetetaan.
			if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
				gc.exit();

		} else if (insideHelp) {
			if (helpScale < 1.05f)
				helpScale += scaleStep * delta;
			// Mikali ollaan helpkuvan ylla ja painetaan hiiren vasenta
			// painikketa, peli siirtyy help -tilaan.
			if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
				sb.enterState(MindBangMain.HELPMENUSTATE);
		}

		// Mikali siirrytaan jonkun kuvan paalta pois, skaalataan kuva taas
		// pienemmaksi.
		else {

			if (mainMenuScale > 1.0f)
				mainMenuScale -= scaleStep * delta;

			if (exitScale > 1.0f)
				exitScale -= scaleStep * delta;

			if (helpScale > 1.0f)
				helpScale -= scaleStep * delta;
		}
	}

	/**
	 * Metodilla voi luokalle antaa tiedon voittajasta jo ennen kuin luokkaan on
	 * siirrytty. Talloin voittaja efekti on jo ladattuna valmiina.
	 * 
	 * @param kumpivoitti
	 *            = kumpi pelaajista voitti
	 * 
	 * **/

	public static void asetaVoittaja(int kumpivoitti) {
		voittaja = kumpivoitti;
	}

}
