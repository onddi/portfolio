package MindBang;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import MindBang.maailma.pelitilat.GameOverState;
import MindBang.maailma.pelitilat.GameplayState;
import MindBang.maailma.pelitilat.HelpMenuState;
import MindBang.maailma.pelitilat.MainMenuState;

/**
 * Pelin rakenne pohjautuu slick kirjaston StateBaseGameen. StateBaseGamen idea
 * on jakaa pelin eri osa-alueet kokonaisuuksikseen, omiksi tiloikseen. On
 * paavalikko -tila, peli -tila, pelin paattymisen -tila seka help -tila. Kaikki
 * pelitilat lisataan pelia ajavaan luokkaan, joka on vastuussa siirtymisesta
 * tilasta toiseen. Tama luokka on pelia ajava paaluokka.
 * **/

public class MindBangMain extends StateBasedGame {

	public static final int MAINMENUSTATE = 0;
	public static final int GAMEPLAYSTATE = 1;
	public static final int GAMEOVERSTATE = 2;
	public static final int HELPMENUSTATE = 3;

	/**
	 * Luokan luontimetodi. Maaritellaan pelin nimi seka luodaan ja lisataan eri
	 * pelitilat.
	 * **/

	public MindBangMain() {
		super("MindBang");
		
		// Peli kaynnistetaan ensimmaisena lisatysta tilasta-> paavalikko
		this.addState(new MainMenuState(MAINMENUSTATE));
		this.addState(new GameplayState(GAMEPLAYSTATE));
		this.addState(new GameOverState(GAMEOVERSTATE));
		this.addState(new HelpMenuState(HELPMENUSTATE));
		
		

	}

	/**
	 * Pelin paaohjelmametodi. Luodaan pelin sailio ja lisataan paaluokka
	 * siihen.
	 * **/

	public static void main(String[] args) throws SlickException {

		AppGameContainer app = new AppGameContainer(new MindBangMain());
		app.setDisplayMode(1280, 800, false); // Asetetaan pelin koko ruudulla
		
		// app.setMinimumLogicUpdateInterval(10);

		app.setTargetFrameRate(70); // Asetetaan pelille tavoite FPS
		app.setShowFPS(false);
		app.start(); // Ajetaan peli
	}

	/**Asetetaan pelisailion otsikko ja pelikuvake. **/
	public void initStatesList(GameContainer container) throws SlickException {
		if (container instanceof AppGameContainer) {
			
			AppGameContainer appContainer = (AppGameContainer) container;
			appContainer.setTitle("MindBang");

			
			if (!appContainer.isFullscreen()) {
				String[] icons = { "res/munkki12.png"};
				container.setIcons(icons);
			}
		}

		this.getState(MAINMENUSTATE).init(container, this);
	}

}
