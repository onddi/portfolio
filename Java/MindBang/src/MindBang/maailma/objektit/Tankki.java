package MindBang.maailma.objektit;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.StateBasedGame;
import MindBang.maailma.Efefktit.Rajahdys;

/**
 * Tankki -luokka on pelaajien hahmoja kuvaava luokka.
 * **/

public class Tankki {

	private int x, y, l, k; // Tankin sijainti seka leveys ja korkeus
	private int energia; // Tankin energia
	private boolean onElossa; // Onko tankki elossa
	private Image pelaaja; // Tankin kuva
	private Rajahdys rajahdys;
	private int ammustaladataan;
	private Sound destroysound = new Sound("res/sound/HIT.ogg"); // tuhoutumisaani
	private boolean vuoro = false;

	/**
	 * Tankki -luokan luontimetodi.
	 * 
	 * @param nimi
	 *            = tankille annettava nimi
	 * @param x
	 *            = tankin sijainnin x -koordinaatti
	 * @param y
	 *            = tankin sijainnin y -koordinaatti
	 * **/

	public Tankki(String nimi, int x, int y, Rajahdys rajahdys)
			throws SlickException {

		this.x = x;
		this.y = y;
		this.onElossa = true;
		this.energia = 110;
		this.rajahdys = rajahdys;

		// Nimen perusteella tankeille annetaan eri kuvat.

		if (nimi.equals("Player 1")) {
			pelaaja = new Image("res/munkki1.png").getScaledCopy(0.4f);
		} else {
			pelaaja = new Image("res/munkki2.png").getScaledCopy(0.4f);
		}

		// Tallennetaan tankin leveys ja korkeus muuttujiin l ja k

		l = pelaaja.getWidth();
		k = pelaaja.getHeight();

	}

	public void render(GameContainer gc, Graphics g) {

		// Mikali tankki on elossa, se piirretaan naytolle.

		if (onElossa) {
			g.drawImage(pelaaja, x, y);
		}

	}

	/**
	 * Luokan update metodi tarkastelee onko pelaaja elossa. Ja kutsuu ammusta
	 * ladattaessa Rajahdys -luokalta pelaajan ylle ammuksen latausefektin
	 * **/

	public void update(GameContainer gc, StateBasedGame sb, int delta) {

		// Kun ammuksen lataus on 1, halutaan ammuksen latausefekti ajaa
		if (getAmmuksenlataus() == 1) {
			rajahdys.asetaTankinlataus(this.x, this.y);
			setAmmuksenlataus(0);
			// Kun ammuksen lataus on 2, halutaan ammuksen latausefekti lopettaa
		} else if (getAmmuksenlataus() == 2) {
			rajahdys.poistaRajahdykset();
			setAmmuksenlataus(0);
		}

		// Mikali energia on 10 tai pienempi, tankki ei ole elossa ja ajetaan
		// tuhoutumisaani seka efekti

		if (this.energia <= 10) {
			destroysound.play(1f, 0.5f);
			rajahdys.asetaTuhoutumisrajahdys(x, y);
			setOnElossa(false);
		}

	}

	/**
	 * Metodi tankin energian muuttamiseksi
	 * 
	 * @param uusienergia
	 *            = energian uusi arvo
	 * **/

	public void asetaEnergia(int uusienergia) {
		this.energia = uusienergia;
	}

	/**
	 * Palauttaa tankin energian.
	 * 
	 * @return tankin energian arvon
	 * **/

	public int getEnergia() {
		return this.energia;
	}

	/**
	 * Asetetaan onElossa muuttujalle arvo
	 * 
	 * @param onkoElossa
	 *            = true niin tankki on elossa, false ei ole
	 * **/

	public void setOnElossa(boolean onkoElossa) {
		this.onElossa = onkoElossa;
	}

	/**
	 * Palauttaa onElossa arvon
	 * 
	 * @return true = tankki elossa, false = ei elossa
	 * **/
	public boolean getOnElossa() {
		return this.onElossa;
	}

	/**
	 * Palauttaa tankin x -koordinaatin
	 * 
	 * @return x = tankin x-koordinaatti
	 * **/

	public float getX() {
		return x;
	}

	/**
	 * Asettaa tankin x -koordinaatin
	 * 
	 * @param x
	 *            = haluttu x -koordinaatti
	 * **/
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Palauttaa tankin y -koordinaatin
	 * 
	 * @return y = tankin y-koordinaatti
	 * **/

	public float getY() {
		return y;
	}

	/**
	 * Asettaa tankin y -koordinaatin
	 * 
	 * @param y
	 *            = haluttu y -koordinaatti
	 * **/

	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Palauttaa tankin leveyden arvon.
	 * 
	 * @return l = tankin leveys
	 * **/

	public int getL() {
		return l;
	}

	/**
	 * Asettaa tankin leveyden arvon.
	 * 
	 * @param l
	 *            = haluttu tankin leveys
	 * **/
	public void setL(int l) {
		this.l = l;
	}

	/**
	 * Palauttaa tankin korkeuden arvon.
	 * 
	 * @return k = tankin korkeus
	 * **/

	public int getK() {
		return k;
	}

	/**
	 * Asettaa tankin korkeuden arvon.
	 * 
	 * @param k
	 *            = tankin korkeus
	 * **/

	public void setK(int k) {
		this.k = k;
	}

	/**
	 * Metodin avulla annetaan tankille tieto ammuksen lataamisesta seka
	 * ampumisesta. Tama metodi kaynnistaa/lopettaa update -metodin kautta
	 * Rajahdys -luokan efektin ammuksen lataukselle.
	 * 
	 * @param lataus
	 *            = 1 mikali ammusta ladataan, 2 mikali ammus ammuttiin ja
	 *            latausefekti halutaan lopettaa
	 * **/

	public void setAmmuksenlataus(int lataus) {
		this.ammustaladataan = lataus;
	}

	/**
	 * Palauttaa ammuksen ammustaladataan muuttujan
	 * 
	 * @return ammustaladataan arvo
	 * **/

	public int getAmmuksenlataus() {
		return this.ammustaladataan;
	}

	/**
	 * Asettaa tankille tiedon onko tama vuorossa
	 * 
	 * @param onkovuoro
	 *            = true, on vuoro
	 * **/

	public void setVuoro(boolean onkovuoro) {
		this.vuoro = onkovuoro;
	}

	/**
	 * Palauttaa tiedon onko tankki vuorossa
	 * 
	 * @return vuoro = true on vuoro, false ei ole vuoro
	 * **/

	public boolean onkoVuoro() {
		return this.vuoro;
	}

}
