package MindBang.maailma.pelitilat;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import MindBang.MindBangMain;
import MindBang.maailma.Efefktit.Rajahdys;

/**
 * Pelin paavalikko -tila. Pelin paavalikosta voidaan siirtya peliin, help
 * -tilaan tai peli voidaan lopettaa.
 **/
public class MainMenuState extends BasicGameState {

	private Image startGameOption = null; // Start -kuvake
	private Image exitOption = null; // Exit -kuvake
	private Image helpOption = null; // Help -kuvake
	private Image title = null; // Pelin otsikko -kuvake
	private Sound alkusoundi; // Aaniefekti
	private Sound fx = null; // Aaniefekti
	private static int menuX = 130; // Kuvakkeiden kiintopisteet
	private static int menuY = 640;
	private float startGameScale = 1; // Alla on kuvakkeiden alkuskaalat. Mikali
										// hiiri tulee kuvakkeiden ylle,
										// skaalataan kuvakkeita suuremmiksi.
										// Tama parantaa valikoiden
										// kaytettavyytta.
	private float exitScale = 1;
	private float helpScale = 1;
	private float scaleStep = 0.0001f; // Kuinka paljon kuvaketta suurennetaan
										// kerrallaan

	private Image maasto1, maasto2, maasto3, maasto0, crazy; // Kenttien
																// valikkokuvat
	private boolean levelselector; // Alavalikon totuusmuuttuja
	private boolean level0, level1, level2, level3, levelc; // Kenttien valinta
															// totuusmuuttujat
	private Color l0, l1, l2, l3, l4; // Kenttien valinnan varit muuttuvat jos
										// hiiri on valinnan paalla
	private int stateID = 0;
	private Animation[] animaatio; // Valikon taustakuvan animaatio
	private Rajahdys rajahdys;

	/**
	 * Luokan luontimetodissa kerrotaan pelitilalle sen tunnus numero
	 * 
	 * @param stateID
	 *            = pelitilan tunnusnumero
	 * **/
	public MainMenuState(int stateID) {
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
	 * nakyvaksi. Kaynnistetaan lisaksi tausta avaruus -efekti luomalla Rajahdys
	 * -luokka.
	 * **/

	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		container.setMouseGrabbed(false);
		alkusoundi.play(0.2f, 0.5f);
		rajahdys = new Rajahdys();
	}

	/**
	 * Init metodi lataa valikon kuvat ja aanet resursseista kun peli
	 * kaynnistetaan. Tassa ladataan lisaksi paavalikon animaation kuvat.
	 * **/

	public void init(GameContainer gc, StateBasedGame arg1)
			throws SlickException {
		alkusoundi = new Sound("res/sound/alkusoundi.ogg");
		startGameOption = new Image("res/valikot/start.png");
		exitOption = new Image("res/valikot/exit.png");
		helpOption = new Image("res/valikot/help.png");
		title = new Image("res/valikot/title.png");
		maasto0 = new Image("res/valikot/maasto0.png").getScaledCopy(0.5f);
		maasto1 = new Image("res/valikot/maasto1.png").getScaledCopy(0.5f);
		maasto2 = new Image("res/valikot/maasto2.png").getScaledCopy(0.5f);
		maasto3 = new Image("res/valikot/maasto3.png").getScaledCopy(0.5f);
		crazy = new Image("res/valikot/crazy.png").getScaledCopy(0.5f);
		fx = new Sound("res/sound/sparkle.ogg");

		setAnimation(new Image[] {
				new Image("res/buddha/buddha1.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha2.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha3.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha4.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha5.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha6.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha7.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha8.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha9.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha10.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha11.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha12.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha11.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha10.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha9.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha8.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha7.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha6.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha5.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha4.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha3.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha2.png").getScaledCopy(1.2f),
				new Image("res/buddha/buddha4.png").getScaledCopy(1.2f) });

	}

	/**
	 * Paavalikon piirtometodi. Piirretaan kuvakkeet omille paikoilleen.
	 * **/

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {

		// Piirretaan animaatio
		g.drawAnimation(animaatio[0], -140, -30);

		// Piirretaan kuvakkeet omille paikoilleen
		title.draw(310, 10, 2f);
		startGameOption.draw(menuX, menuY, startGameScale);
		exitOption.draw(menuX + 900, menuY, exitScale);
		helpOption.draw(menuX + 430, menuY, helpScale);

		// Mikali ollaan alavalikossa, piirretaan alavalikon valikkokuvat
		if (levelselector == true) {
			crazy.draw(menuX + 30, menuY - 250, l4);
			maasto3.draw(menuX + 30, menuY - 200, l3);
			maasto2.draw(menuX + 30, menuY - 150, l2);
			maasto1.draw(menuX + 30, menuY - 100, l1);
			maasto0.draw(menuX + 30, menuY - 50, l0);
		}
		// Piirretaan tausta avaruus -efekti ruudulle
		rajahdys.render(gc, g);
	}

	/**
	 * Update metodi paivittaa pelin logiikkaa automaattisesti. Taman luokan
	 * updatessa kuunnellaan hiiren liikkeita jotta pelin valikkoja voi kayttaa.
	 * **/

	public void update(GameContainer gc, StateBasedGame sb, int delta)
			throws SlickException {
		rajahdys.update(gc, delta);

		// Tallennetaan hiiren kuuntelija muuttujaan
		Input input = gc.getInput();

		// Tallennetaan hiiren sijainnin x- ja y -koordinaatit
		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();

		// Totuusmuuttujat onko hiiri jonkun valikkokuvan ylla
		boolean insideStartGame = false;
		boolean insideExit = false;
		boolean insideHelp = false;
		// Ollaanko kentanvalitsemis alavalikossa
		boolean insideLevelselector = false;

		// Alla tarkastellaan onko hiiri jonkun valikkokuvan koordinaattien
		// ylla.
		if ((mouseX >= menuX && mouseX <= menuX + startGameOption.getWidth())
				&& (mouseY >= menuY && mouseY <= menuY
						+ startGameOption.getHeight())) {
			insideStartGame = true;
			// Tuodaan alavalikko esille
			this.levelselector = true;
		} else if ((mouseX >= menuX + 900 && mouseX <= menuX + 900
				+ exitOption.getWidth())
				&& (mouseY >= menuY && mouseY <= menuY + exitOption.getHeight())) {
			insideExit = true;
		} else if ((mouseX >= menuX + 430 && mouseX <= menuX + 430
				+ helpOption.getWidth())
				&& (mouseY >= menuY && mouseY <= menuY + helpOption.getHeight())) {
			insideHelp = true;
			// Kun hiiri on start menun ylla tulee alavalikko nakyviin
		} else if (levelselector == true) {
			// Alavalikon tulee pysya nakyvissa eri vaihtoehtojen valitsemista
			// varten
			if ((mouseX >= menuX + 30 && mouseX <= menuX + 30
					+ maasto2.getWidth())
					&& (mouseY >= menuY - 250 && mouseY <= menuY
							+ startGameOption.getHeight())) {
				insideStartGame = true;
				insideLevelselector = true;
			} else
				insideStartGame = false;

		}

		// Alla on vaihtoehdot mita tehdaan mikali ollaan jonkun valikkokuvan
		// ylla. Jokainen kuva skaalataan suuremmaksi mikali hiiri on kuvan
		// ylla. Tama helpottaa kayttajaa hahmottaan valikon kayttoa.

		if (insideStartGame) {
			// Start valikko avaa alavalikon. Alla alavalikon vaihtoehtojen
			// totuusmuuttujat ja varit
			level0 = false;
			level1 = false;
			level2 = false;
			level3 = false;
			levelc = false;
			l0 = Color.lightGray;
			l1 = Color.lightGray;
			l2 = Color.lightGray;
			l3 = Color.lightGray;
			l4 = Color.lightGray;

			// Jos ollaan alavalikon kentan kuvakkeen ylla muuttuu
			// totuusmuuttuja true ja vari valkoiseksi
			if (insideLevelselector) {
				if ((mouseX >= menuX + 30 && mouseX <= menuX + 30
						+ maasto0.getWidth())
						&& (mouseY >= menuY - 50 && mouseY <= menuY - 50
								+ maasto0.getHeight())) {
					level0 = true;
					l0 = Color.white;
				} else if ((mouseX >= menuX + 30 && mouseX <= menuX + 30
						+ maasto1.getWidth())
						&& (mouseY >= menuY - 100 && mouseY <= menuY - 100
								+ maasto1.getHeight())) {
					level1 = true;
					l1 = Color.white;
				} else if ((mouseX >= menuX + 30 && mouseX <= menuX + 30
						+ maasto2.getWidth())
						&& (mouseY >= menuY - 150 && mouseY <= menuY - 150
								+ maasto2.getHeight())) {
					level2 = true;
					l2 = Color.white;
				} else if ((mouseX >= menuX + 30 && mouseX <= menuX + 30
						+ maasto3.getWidth())
						&& (mouseY >= menuY - 200 && mouseY <= menuY - 200
								+ maasto3.getHeight())) {
					level3 = true;
					l3 = Color.white;
				} else if ((mouseX >= menuX + 30 && mouseX <= menuX + 30
						+ crazy.getWidth())
						&& (mouseY >= menuY - 250 && mouseY <= menuY - 250
								+ crazy.getHeight())) {
					levelc = true;
					l4 = Color.white;
				}
			}

			if (startGameScale < 1.05f)
				startGameScale += scaleStep * delta;
			// Mikali ollaan pelin startkuvan tai jonkin levelkuvan paalla ja
			// painetaan hiiren vasenta painiketta, siirrytaan pelitilaan.
			if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
				fx.play(0.8f, 1);
				// Mikali oltiin alavalikossa jonkin vaihtoehdon paalla kun
				// hiiren vasenta painiketta painettiin. Valitetaan tieto
				// valitusta kentasta/maastosta pelitila -luokalle.
				int level = 0;
				if (level0 == true)
					level = 0;
				if (level1 == true)
					level = 1;
				if (level2 == true)
					level = 2;
				if (level3 == true)
					level = 3;
				if (levelc == true)
					level = 4;
				GameplayState.setLevel(level);
				//siirrytaan pelitilaan
				sb.enterState(MindBangMain.GAMEPLAYSTATE);
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

			this.levelselector = false;

			if (startGameScale > 1.0f)
				startGameScale -= scaleStep * delta;

			if (exitScale > 1.0f)
				exitScale -= scaleStep * delta;

			if (helpScale > 1.0f)
				helpScale -= scaleStep * delta;
		}
	}

	/**
	 * Luodaan animaatio kuva -listan kuvista.
	 * 
	 * @param buddha
	 *            = lista kuvista jotka muodostavat animaation
	 * **/
	public void setAnimation(Image[] buddha) {
		// Animaatio jossa on 24 kuvaa
		animaatio = new Animation[24];
		// Tallennetaan ensimmaiseen alkioon animaatio
		animaatio[0] = new Animation();
		// Loopataan kuvalista lapi ja lisataan jokainen kuva omaksi animaation
		// ruuduksi. Maaritellaan lisaksi kuinka kauan yhta ruutua naytetaan.
		for (Image i : buddha) {
			animaatio[0].addFrame(i, 100);
		}

	}

}