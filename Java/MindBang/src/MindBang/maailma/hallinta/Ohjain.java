package MindBang.maailma.hallinta;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import MindBang.MindBangMain;
import MindBang.maailma.objektit.Ammus;
import MindBang.maailma.objektit.Tankki;
import MindBang.maailma.objektit.Tormaysympyra;
import MindBang.maailma.pelitilat.GameplayState;

/**
 * Ohjain -luokka vastaa pelin ohjaamisesta ja vuorojarjestelman yllapidosta.
 * Luokka vaikuttaa ja tekee muutoksia moneen muuhun luokkaan joten se on
 * keskeinen osa koko pelin logiikkaa.
 * **/

public class Ohjain {

	private Ammus ammus; // Ammus muuttuja
	private Tormaysympyra tormayslaatikko; // Tormaysympyra muuttuja
	private float tahtaysalkupisteX, tahtaysalkupisteY; // Tahtaysviivan
														// alkukoordinaatit
	private float mouseX2, mouseY2; // Hiiren paikkakoordinaatit
	private float alkux, alkuy, alkux1, alkuy1; // Ammusten alkukoordinaatit
												// tankille1 ja tankille2
	private boolean laukaus = false; // Ampumisen hallintaan liittyva
										// totuusmuuttuja
	private float kulma; // Ammuksen lahtokulma
	private int vuoronumero; // Pitaa kirjaa monesko vuoro kyseessa(parilliset
								// tankin1 vuoroja parittomat tankin2)
	private Tankki tankki1, tankki2; // Tankkien eli pelaajien muuttujat
	private Sound omm = new Sound("res/sound/omm.ogg"); // Ammuksen latausaani
	private Sound impact = new Sound("res/sound/impact.ogg"); // Ammuksen
																// ampumisaani
	private boolean vuoronaloitus = false; // Vuoron aloituksen totuusmuuttuja
	private float aikalaskuri; // Ammuksen lataamiseen kaytettava aikalaskuri
	private TrueTypeFont ttf; // Kaytettava fontti
	private Vector2f piste1, piste2; // Ampumisen apuviivan alku ja
										// loppupisteiden vektorit

	/**
	 * Ohjain -luokan luontimetodi.
	 * 
	 * @param ammus
	 *            = lisataan Ammusluokan instanssi
	 * @param t1
	 *            = lisataan pelaaja1
	 * @param t2
	 *            = lisataan pelaaja2
	 * @param tl
	 *            = lisataan tormayslaatikot
	 * @param vuoronumero
	 *            = aloitusvuoron numero
	 * **/

	public Ohjain(Ammus ammus, Tankki t1, Tankki t2, Tormaysympyra tl,
			int vuoronumero) throws SlickException {

		this.ammus = ammus;
		this.tormayslaatikko = tl;
		// Kerrotaan ohjaimelle mista kummankin tankin ammukset ammutaan
		alkux = this.ammus.getAlkux();
		alkuy = this.ammus.getAlkuy();
		alkux1 = this.ammus.getAlkux1();
		alkuy1 = this.ammus.getAlkuy1();
		this.tankki1 = t1;
		this.tankki2 = t2;
		this.vuoronumero = vuoronumero;
		piste1 = new Vector2f();
		piste2 = new Vector2f();
		Font aw = new Font("buddha", Font.BOLD, 60); // Luodaan buddha fontti
		ttf = new TrueTypeFont(aw, false); // Laitetaan truetype font kayttamaan
											// buddha fonttia

	}

	/**
	 * Ohjaimen update -metodi vastaa komentojen vastaanottamisesta. Tama metodi
	 * on vastuussa ammuksen lentoradan laskemiseen tarvittavien lahtonopeuden
	 * ja lahtokulman laskemisesta. Lisaksi pelin vuoropohjainen logiikka
	 * paivittyy taman metodin kautta. Update -metodi on slickiin
	 * sisaanrakennettu pelin logiikan paivittamisen metodi. Katso metodin
	 * sisaiset kommentit nahdaksesi tarkemmin metodin toimintaperiaatteen.
	 * **/

	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {

		// Tallennetaan muuttujaan hiiren ja nappaimiston kuuntelija

		Input i = gc.getInput();

		// Mikali jompi kumpi pelaajista on tuhoutunut, siirtyy peli hiirten
		// painalluksesta pelin loppumisen pelitilaan.

		if (!tankki1.getOnElossa() || !tankki2.getOnElossa()) {
			if (i.isMousePressed(Input.MOUSE_LEFT_BUTTON)
					|| i.isMousePressed(Input.MOUSE_RIGHT_BUTTON))
				sbg.enterState(MindBangMain.GAMEOVERSTATE);
		}

		// Mikali painetaan ESC niin mennaan paavalikkoon

		if (i.isKeyPressed(Input.KEY_ESCAPE)) {
			sbg.enterState(MindBangMain.MAINMENUSTATE);

		}

		// Mikali painetaan S niin pelin aanet menevat paalle tai pois paalta

		if (i.isKeyPressed(Input.KEY_S)) {
			if (gc.isSoundOn()) {
				gc.setSoundOn(false);
				GameplayState.setTaustamusa();

			} else {
				gc.setSoundOn(true);
				GameplayState.setTaustamusa();
			}
		}

		// SPACE -painiketta tulee painaa aina ennen vuoron aloitusta.
		// Tama vahentaa mahdollisten vahinkoampumisten tapahtumista toisen
		// vuorolla.

		if (i.isKeyPressed(Input.KEY_SPACE)) {
			vuoronaloitus = true;
			ammus.setKuva(1);
			ammus.setYkkosase(true);

		}

		// Hiiren oikealla painikkeella muutetaan ammuksen tyyppia tuhoavaksi
		// tai maastoa kasvattavaksi

		if (i.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {

			if (Ammus.getYkkosase() == true) {
				ammus.setKuva(2);
				ammus.setYkkosase(false);
			} else {
				ammus.setKuva(1);
				ammus.setYkkosase(true);
			}
		}

		// TÄSSÄ LOHKOSSA KÄSITELLÄÄN AMPUMINEN
		// Mikali ammus ei ole (enaa) nakyvissa ajetaan seuraava lohko

		if (!ammus.onkoNakyvissa()) {

			// Tarkistetaan onko vuoro aloitettu SPACE -painikkeella

			if (vuoronaloitus == true) {

				// Kun hiiren vasentapainiketta painetaan ajetaan seuraava
				// lohko. Ammuksen lahtokulma ja nopeus lasketaan vuorossa
				// olevan tankin keskikohdan ja hiiren vasemman painikkeen
				// irtipaastokohdan valisen viivan avulla. Tama lohko tallentaa
				// hiiren painamiskohdan. Lohko pitaa kirjaa vuoroista ja
				// alustaa ammuksen vuoron mukaan oikealle kohdalleen.

				if (i.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {

					// Soitetaan lataamisessa kaytettava aani
					omm.play();
					ammus.setXd(0); // Asetetaan ammuksen x -nopeus 0
					ammus.setYd(0); // Asetetaan ammuksen y -nopeus 0

					vuoronumero++; // Kasvatetaan vuoronumeroa yhdella

					// Mikali kyseessa parillinen vuoro, on takin1 vuoro
					if (tankki1Vuoro()) {
						// Tallennetaan vuorossa olevan tankin koordinaatit
						// muuttujaan
						tahtaysalkupisteX = alkux + 10;
						tahtaysalkupisteY = alkuy;
						// Kerrotaan tankille1 etta ammusta alettiin ladata
						tankki1.setAmmuksenlataus(1);
						// Alustetaan ammus tankin1 ammuksen lahtopisteisiin
						ammus.setPaikkavektori(alkux, alkuy);
						// Kerrotaan ammukselle etta on pelaajan 1 vuoro
						ammus.setpelaajan1vuoro(true);
						// Mikali kyseessa pariton vuoro, on takin2 vuoro. Samat
						// asiat toistetaan kuin ylla, mutta pelaajalle 2.
					} else {
						tahtaysalkupisteX = alkux1 + 10;
						tahtaysalkupisteY = alkuy1;
						tankki2.setAmmuksenlataus(1);
						ammus.setPaikkavektori(alkux1, alkuy1);
						ammus.setpelaajan1vuoro(false);

					}
					// Asetetaan ampumista helpottavan tahtaysviivan
					// alkupiste
					piste1.set(tahtaysalkupisteX, tahtaysalkupisteY);
					// Jotta seuraavaa lohkoa ei aleta suorittaa ennen kuin
					// hiirta on painettu on kaytettava totuusmuuttujaa.
					laukaus = true;
				}

				// Niin kauan kunnes hiiren 1 painike on pohjassa ajetaan
				// seuraavaa lohkoa. Lohkossa kasvatetaan ammuksen kokoa ja
				// pienennetaan vuorossa olevan tankin tormaysympyraa eli
				// elinvoimaa.

				if (i.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {

					// Tallennetaan tamanhetkiset koordinaatit
					// valiaikaismuuttujiin

					float mouseX = i.getMouseX();
					float mouseY = i.getMouseY();

					// Asetetaan piste2 (tahtaysta helpottavan viivan
					// loppupiste) ylla oleviin koordinaatteihin eli hiiren sen
					// hetkiseen kohtaan
					piste2.set(mouseX, mouseY);

					// Valiaikaismuuttuja joka kertoo kumman vuoro on kyseessa
					int vuoro;

					if (tankki1Vuoro() == true) {
						vuoro = 1;
					} else {
						vuoro = 2;
					}

					// Aikalaskuri jonka avulla kasvatetaan ammuksen kokoa.
					// Delta on Slick kirjaston oma paivitysnopeus. Aikalaskurin
					// arvo kasvaa siis mita kauemmin hiirta pidetaan pohjassa.

					aikalaskuri += delta / 100f;

					// Ammuksen lataaminen pienentaa pelaajan omaa
					// tormaysympyraa eli elinvoimaa.

					tormayslaatikko.setTormaysympyrasize(vuoro,
							(aikalaskuri / 100));

					// Ammuksen lataaminen kasvattaa sen kokoa
					ammus.setSize(aikalaskuri / 10);
					// Kerrotaan ammukselle etta kokoa kasvatetaan viela
					ammus.setKasvattaakokoaan(true);

					// Mikali pelaaja tuhoutuu ladatessaan ammusta taytyy ammus
					// poistaa nakyvista ja latausaani lopettaa
					if (!tankki1.getOnElossa() || !tankki2.getOnElossa()) {
						ammus.setKasvattaakokoaan(false);
						ammus.setNakyvissa(false);
						omm.stop();

					}
				}

				// Kun hiiren ykkospainikkeesta päästetään irti, laukaus
				// tapahtuu ja lasketaan x- ja y -nopeudet. Irtipaastokohdan ja
				// pelaajan keskikohdan avulla saadaan lahtokulma ja
				// nopeudet laskettua.

				if (laukaus == true && ammus.getKasvattaakokoaan() == true) {

					if (!i.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {

						omm.stop(); // Stopataan ammuksen lataamisaani
						impact.play(1f, 1f); // Soitetaan ammuksen ampumisaani

						ammus.setKasvattaakokoaan(false); // Ammus ei enaa kasva
						aikalaskuri = 0; // Aika nollataan

						mouseX2 = i.getMouseX(); // Tallennetaan x
													// -irtipaastokohta
													// muuttujaan
						mouseY2 = i.getMouseY(); // Tallennetaan y
													// -irtipaastokohta
													// muuttujaan

						// Lasketaan painamiskohdan ja irtipaastokohdan valisten
						// x -koordinaattien erotuksen itseisarvo
						float xEtaisyys1 = Math
								.abs(mouseX2 - tahtaysalkupisteX);
						// Lasketaan painamiskohdan ja irtipaastokohdan valisten
						// y -koordinaattien erotuksen itseisarvo
						float yEtaisyys1 = Math
								.abs(mouseY2 - tahtaysalkupisteY);

						// Laukaisunopeus maaritellaan pythagoraan lauseen
						// mukaan. Voidaan ajatella etta ylla lasketut xEtaisyys
						// ja yEtaisyys muodostavat suorakulman. Taman
						// suorakulman hypotenuusa lasketaan alla, saadaan:
						// laukaisunopeus.
						float laukaisunopeus = (float) Math.sqrt(Math.pow(
								xEtaisyys1, 2) + Math.pow(yEtaisyys1, 2));

						// Minimilaukaisunopeus
						if (laukaisunopeus < 200) {
							laukaisunopeus = 200;
						}
						// Maksimilaukaisunopeus
						if (laukaisunopeus > 400) {
							laukaisunopeus = 400;
						}

						// Valiaikaismuuttujat joiden avulla lasketaan tankin
						// keskipisteen ja hiiren irtipaastokohdan valinen kulma
						float xEtaisyys;
						float yEtaisyys;

						// Vuorosta riippuen lasketaan jommankumman tankin ja
						// hiiren irtipaaston valiset x- ja y-etaisyydet
						if (tankki1Vuoro()) {
							tankki1.setAmmuksenlataus(2);
							xEtaisyys = Math.abs(mouseX2
									- (tankki1.getX() + tankki1.getL() / 2));

							yEtaisyys = Math.abs(mouseY2 - 725);

						} else {
							tankki2.setAmmuksenlataus(2);
							xEtaisyys = Math.abs(tankki2.getX()
									+ tankki2.getL() / 2 - mouseX2);
							yEtaisyys = Math.abs(mouseY2 - 725);

						}

						// Voidaan ajatella etta yllalasketut etaisyydet
						// muodostavat suorakulmaisen kolmion vastaisen ja
						// viereisen sivun. Naiden avulla saadaan laskettua
						// tangentti, joka niin ikaan on laukaisukulma.

						kulma = (float) Math.atan(yEtaisyys / xEtaisyys);
						System.out.println("LAUKAISUNOPEUS " + laukaisunopeus);
						System.out.println("KULMA " + Math.toDegrees(kulma));

						// Erotellaan laukaisunopeudesta x -komponentti. Kaava
						// tulee geometriasta. Jaetaan nopeus hitaammaksi.

						float xNopeus = (float) (laukaisunopeus * Math
								.cos(kulma)) / 35;

						// Erotellaan laukaisunopeudesta y -komponentti. Kaava
						// tulee geometriasta. Jaetaan nopeus hitaammaksi.
						float yNopeus = (float) (laukaisunopeus * Math
								.sin(kulma)) / 35;

						System.out.println("XNOPEUS " + xNopeus);
						System.out.println("YNOPEUS " + yNopeus);

						// Vuorosta riippuen ammuksen x -suuntainen nopeus on
						// joko oikealle (tankki1) tai vasemmalle (tankki2)

						if (tankki1Vuoro()) {
							ammus.setXd(xNopeus);
						} else {
							ammus.setXd(-(xNopeus));
						}

						// Asetetaan ammuksen y -suuntainen nopeus

						ammus.setYd(-(yNopeus));

						// Asetetaan laukaus jalleen falseksi, jotta laskuja ei
						// tehda kuin vasta seuraavalla kerralla kun ammus
						// ammutaan.

						laukaus = false;

						// Kerrotaan ammukselle etta lento voidaan aloittaa
						ammus.setNakyvissa(true);
						// Estetaan seuraavan vuoron aloitus ennenaikojaan
						vuoronaloitus = false;
					}
				}
			}
		}
	}

	/**
	 * Palauttaa vuoronumeron, jonka avulla lasketaan kumman vuoro on kyseessa.
	 * 
	 * @return vuoronumero = parillinen tai pariton luku
	 * **/

	public int getVuoronumero() {
		return this.vuoronumero;
	}

	/**
	 * Slickin piirtometodi. Tassa piirretaan kehoitus pelaajalle vuoron
	 * aloituksesta.
	 * **/

	public void render(GameContainer gc, Graphics g) {

		// Mikali vuoroa ei ole aloitettu ja ammus ei ole nakyvissa, piirretaan
		// ruudulle kehoitus vuorossa olevalla pelaajalle vuoron aloittamisesta.
		g.setFont(ttf);
		if (vuoronaloitus == false && ammus.onkoNakyvissa() == false) {

			// Alustetaan tahtaysviivan pisteet nolliksi koska vuoro vaihtuu
			piste1.set(0, 0);
			piste2.set(0, 0);

			String p;
			// Vuorossa olevalle pelaajalle kerrotaan vuoron aloittamisesta
			// kyseista pelaajaa kuvaavalla varilla.
			if (vuoronumero % 2 != 0) {
				p = "Player 1";
				g.setColor(Color.yellow);
				tankki1.setVuoro(true);
				tankki2.setVuoro(false);
			} else {
				p = "Player 2";
				g.setColor(Color.cyan);
				tankki2.setVuoro(true);
				tankki1.setVuoro(false);

			}

			g.drawString(p + " press SPACE", 350, 350);
			g.setColor(Color.white);

		}

		// Ammuksen tahtaamista helpottava viiva piirretaan vuorossa olevan
		// pelaajan varin mukaan. Mikali pelaaja tuhoutuu ladatessaan, ei viivaa
		// piirreta.
		if (laukaus == true) {
			if (tankki1.onkoVuoro())
				g.setColor(Color.yellow);
			else if (tankki2.onkoVuoro())
				g.setColor(Color.cyan);

			if (tankki2.getOnElossa() && tankki1.getOnElossa()) {

				g.drawLine(piste1.x, piste1.y, piste2.x, piste2.y);
				g.setColor(Color.white);
			}

		}
	}

	/**
	 * Metodi palauttaa totuusarvon onko kyseessa tankin1 vuoro vai ei
	 * 
	 * @return true on tankin1 vuoro, false on tankin2 vuoro
	 * **/

	public boolean tankki1Vuoro() {
		if (vuoronumero % 2 != 0) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * Palauttaa vuoronaloituksen totuusarvon
	 * 
	 * @return true vuoro aloitettu, false vuoroa ei aloitettu
	 * **/

	public boolean getVuoronaloitus() {
		return this.vuoronaloitus;
	}

}
