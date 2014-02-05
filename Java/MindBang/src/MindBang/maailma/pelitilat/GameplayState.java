package MindBang.maailma.pelitilat;

import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import MindBang.maailma.Efefktit.Rajahdys;
import MindBang.maailma.hallinta.MaastoFactory;
import MindBang.maailma.hallinta.Ohjain;
import MindBang.maailma.hallinta.Pisteenlaskija;
import MindBang.maailma.hallinta.Tormayskasittelija;
import MindBang.maailma.objektit.Ammus;
import MindBang.maailma.objektit.Maasto;
import MindBang.maailma.objektit.Tahtain;
import MindBang.maailma.objektit.Tankki;
import MindBang.maailma.objektit.Tormaysympyra;

/**
 * Pelin pelitila -tila. Tama luokka on vastuussa paaosin pelin olioiden
 * piirtamisen hallinnoinnista ja pelin logiikan paivittamisesta. Toisinsanoen
 * taman luokan kautta kutsutaan olioiden omia piirtometodeja ja
 * logiikanpaivitys -metodeja. Taman tyyppinen lahestymistapa on osa slick2d
 * -kirjaston rakennetta.
 * **/

public class GameplayState extends BasicGameState {

	private Tankki tankki1; // Pelaaja1
	private Tankki tankki2; // Pelaaja2
	private Tahtain tahtain; // Tahtain
	private Ammus ammus; // Ammus
	private Tormayskasittelija tk; // Tormayskasittelija
	private Maasto palikka; // Maasto
	private MaastoFactory palikkafactory; // Maaston luonti
	private List<Shape> polygoni; // Luodun maaston muoto
	private Pisteenlaskija pisteenlaskija; // Pelin pistelaskujarjestelma
	private Tormaysympyra tormaysympyra; // Pelaajien tormaysympyrat
	private Ohjain ohjain; // Pelin ohjain
	private Rajahdys rajahdys; // Rajahdysefekti
	private static Sound taustaaani;
	private static int level = 0; // Kentan numero

	int stateID = 0;

	/**
	 * Luokan luontimetodissa kerrotaan pelitilalle sen tunnus numero
	 * 
	 * @param stateID
	 *            = pelitilan tunnusnumero
	 * **/

	public GameplayState(int stateID) {
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
	 * Metodi joka ajetaan kun pelitilaan tullaan. Taman luokan enter -metodissa
	 * luodaan instanssit kaikista pelissa olevista asioista.
	 * **/

	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {

		rajahdys = new Rajahdys();
		tk = new Tormayskasittelija(rajahdys);
		palikkafactory = new MaastoFactory(level);
		// Luodaan pelin maasto ja asetetaan se muuttujaan
		polygoni = palikkafactory.luoMaasto();
		palikka = new Maasto(tk, polygoni);
		// Asetetaan pelaajat omiin koordinaatteihinsa
		tankki1 = new Tankki("Player 1", 90, 700, rajahdys);
		tankki2 = new Tankki("Player 2", 1150, 700, rajahdys);
		tormaysympyra = new Tormaysympyra(tankki1, tankki2, tk);
		ammus = new Ammus(tankki1, tankki2, tk);
		tahtain = new Tahtain(tankki1, tankki2);
		ohjain = new Ohjain(ammus, tankki1, tankki2, tormaysympyra, 1);
		pisteenlaskija = new Pisteenlaskija(ohjain, tk, tormaysympyra);

		taustaaani.play(1, 0.2f);
		// otetaan hiiri pois nakyvista pelitilassa
		container.setMouseGrabbed(true);
	}

	/**
	 * Metodi ajetaan kun pelitilasta poistutaan
	 * **/
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		// stopataan taustamusiikki
		taustaaani.stop();
	}

	/**
	 * Ladataan pelintilan taustamusiikki.
	 * **/
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {

		taustaaani = new Sound("res/sound/avaruusmusac.ogg");
	}

	/**
	 * Pelitilan piirtometodi. Tassa kutsutaan pelitila -luokan alla olevien
	 * luokkien piirtometodeja.
	 * **/

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {

		palikka.render(gc, g);
		tormaysympyra.render(gc, g);
		ohjain.render(gc, g);
		tankki1.render(gc, g);
		tankki2.render(gc, g);
		tahtain.render(gc, g);
		ammus.render(gc, g);
		rajahdys.render(gc, g);

	}

	/**
	 * Taman luokan update -metodi kutsuu pelitilan alla olevien luokkien update
	 * -metodeja. Update on vastuussa pelin logiikan paivittamisesta.
	 * **/

	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {

		// Mikali tankki on elossa, paivitetaan sen logiikka. Jos taas ei
		// asetetaan elossa oleva tankki voittajaksi.
		if (tankki1.getOnElossa()) {
			tankki1.update(gc, sbg, delta);
		} else {
			GameOverState.asetaVoittaja(2);

		}

		if (tankki2.getOnElossa()) {
			tankki2.update(gc, sbg, delta);
		} else {
			GameOverState.asetaVoittaja(1);

		}

		tahtain.update(gc);
		ammus.update(gc, delta);
		pisteenlaskija.update(gc, delta);
		tormaysympyra.update(gc, delta);
		ohjain.update(gc, sbg, delta);
		rajahdys.update(gc, delta);

	}

	/**
	 * Metodilla voidaan kaynnistaa tai stopata taustalla soiva musiikki
	 * **/
	public static void setTaustamusa() {
		if (!taustaaani.playing()) {
			taustaaani.play(1, 0.2f);
		} else {
			taustaaani.stop();
		}

	}

	/**
	 * Metodilla voidaan asettaa se kentta mita halutaan pelata. MainMenuState
	 * -luokasta valitaan kentta, jolloin tieto siirretaan taman metodin kautta.
	 * Tietoa kaytetaan maaston luomisessa.
	 * 
	 * @param level
	 *            = mika kenttaa halutaan pelata
	 * **/
	public static void setLevel(int level) {
		GameplayState.level = level;

	}

}
