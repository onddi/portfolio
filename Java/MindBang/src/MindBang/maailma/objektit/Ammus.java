package MindBang.maailma.objektit;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import MindBang.maailma.hallinta.Tormayskasittelija;

/**
 * Ammus -luokka sisältää nimensä mukaisesti pelin ammuksen ja sen olemassaoloon
 * liittyviä metodeja ja attribuutteja. Perusperiaatteena ammus laskee kokoajan
 * itselleen seuraavan koordinaatin (joka lasketaan alkukulman, -nopeuden ja
 * kuluneen ajan perusteella) ja ilmoittaa sen törmäyskäsittelijälle. Luokka
 * vastaa ammuksen piirtamisestä.
 * **/

public class Ammus {

	private float xd, yd; // xd,yd = akseleiden mukaiset nopeudet;

	private float alkux, alkuy, alkux1, alkuy1; // alkux,alkuy = tankin1
												// ammuksen alkupaikka;
												// alkux1,alkux2 = tankin2
												// ammuksen alkupaikka;
	private float edellinenx, edellineny;// edellinenx,edellineny = ammuksen
											// edellisen sijainnin koordinaatit;
	private float kulma; // Ammuksen lahtokulma
	private float aikalaskuri = 0; // Ammuksen lentoajan muuttuja
	private boolean nakyvissa = false; // Ammuksen nakyvyys muuttuja
	private boolean kasvattaakokoaan = true; // Kasvattaako ammus kokoaan
	private boolean osuma = false; // Osuuko ammus johonkin
	private boolean pelaajan1vuoro = false; // Onko pelaajan 1 vuoro
	private static boolean ykkosase = true; // Ammustyypin muuttuja
	private Vector2f paikkavektori; // Ammuksen paikkavektori
	private Vector2f edellinenpaikka; // Ammuksen edellisen paikan paikkavektori
	private Vector2f suuntavektori; // Ammuksen lentoradan suuntavektori
	private float koko; // Ammuksen koko
	private Tormayskasittelija tk; // Tormayskasittelijan instassi
	private Image ammus; // Ammuksen kuva
	private Image ammus1 = new Image("res/ammus/ammus1.png")
			.getScaledCopy(0.1f); // Tuhoavan ammuksen kuva
	private Image ammus2 = new Image("res/ammus/ammus2.png")
			.getScaledCopy(0.1f); // Lotus (kasvattavan) ammuksen kuva

	/**
	 * Ammus -luokan luontimetodi.
	 * 
	 * @param tankki1
	 *            = tankki1 (pelaaja1)
	 * @param tankki2
	 *            = tankki2 (pelaaja2)
	 * @param tk
	 *            = tormayskasittelija luokan instanssi
	 * 
	 * **/

	public Ammus(Tankki tankki1, Tankki tankki2, Tormayskasittelija tk)
			throws SlickException {

		// Koska peli etenee vuoroittain ja pelissa kaytetaan vain yhta ja samaa
		// ammusta, on ammukselle annettava koordinaatit mista lahtea liikkeelle
		// kullakin vuorolla. alkux,alkuy ovat tankin1 ammuksen
		// lahtokoordinaatit ja alkux1,alkuy1 ovat tankin2 ammuksen
		// lahtokoordinaatit

		this.alkux = tankki1.getX() + 10;
		this.alkuy = tankki1.getY() - 20;
		this.alkux1 = tankki2.getX() + 10;
		this.alkuy1 = tankki2.getY() - 20;

		// Ammuksen paikkaa kuvaava vektori luodaan. Aluksi se on kohdassa
		// alkux,alkuy. Luodaan lisaksi vektorit suunnalle ja edelliselle
		// paikalle.

		this.paikkavektori = new Vector2f(alkux, alkuy);
		this.suuntavektori = new Vector2f();
		this.edellinenpaikka = new Vector2f();

		// Asetetaan ammuksen kuvaksi
		this.ammus = setKuva(1);

		this.tk = tk;

	}

	/**
	 * Metodi maarittelee mika kuva piirretaan ruudulle. On kaksi kuvaa: toinen
	 * maastoa tuhoavalle ammukselle, toinen maastoa kasvattavalle ammukselle.
	 * 
	 * @param kumpi
	 *            = kumpi ammus halutaan piirtaa
	 * **/
	public Image setKuva(int kumpi) {
		if (kumpi == 1) {
			ammus = ammus1;
		} else if (kumpi == 2) {
			ammus = ammus2;
		}
		return ammus;
	}

	/**
	 * Palauttaa ammuksen kuvan.
	 * 
	 * @return ammuksen nykyinen kuva
	 * **/
	public Image getKuva() {
		return ammus;
	}

	public void render(GameContainer gc, Graphics g) {

		// Mikali ammusta kasvatetaan piirretaan ruudulle kasvava ammus.
		// Ammuksen piirtokoordinaatit muuttuvat koon muuttuessa. Jotta ammus
		// nayttaisi kasvavan paikoillaan taytyy paikkakoordinaatteja muokata
		// suhteessa kokoon.

		if (kasvattaakokoaan) {
			g.drawImage(getKuva().getScaledCopy(koko), paikkavektori.x - koko
					* 10, paikkavektori.y - koko * 30);

		}

		// Mikali ammus on ammuttu ja se on nakyvissa piirretaan se
		// paikkakoordinaattien mukaan. Piirtokohtaa muutetaan suhteessa
		// ammuksen kokoon.
		if (nakyvissa) {

			g.drawImage(getKuva().getScaledCopy(koko), paikkavektori.x - koko,
					paikkavektori.y - koko);

		}

	}

	// Update -metodin sisalla tarkastellaan ammukseen lentamiseen liittyvia
	// asioita.

	public void update(GameContainer gc, int delta) throws SlickException {

		// Mikali ammus on nakyvissa (eli se on ammuttu ja se ei ole tormannyt
		// mihinkaan) tehdaan seuraava:

		if (nakyvissa) {

			// Kutsutaan tormayskasittelijan update metodia. Delta on pelin
			// logiikan paivitystiheys.

			tk.update(gc, delta);

			// Mikali ammus ammutaan peliruudulta ulos(vasen,oikea tai
			// alareuna), lopetetaan vuoro.

			if (paikkavektori.x > gc.getWidth()
					|| paikkavektori.y > gc.getHeight() + koko * 50
					|| paikkavektori.x + koko * 10 < 0) {

				// Aikalaskuri nollataan, ammus asetetaan nakymattomaksi, ammus
				// ei osu mihinkaan ja se poistetaan tormayskasittelijan
				// ammukset listasta.

				aikalaskuri = 0;
				nakyvissa = false;
				osuma = false;
				tk.removeAmmus();

				// Alla lasketaan ammuksen lentorataa.

			} else {

				// Ammuksen edellisen paikan koordinaatit tallennetaan ja
				// kerrotaan tormayskasittelijalle tiedot naista koordinaateista

				edellinenpaikka.set(paikkavektori);
				tk.setEdellinenpaikka(edellinenpaikka);

				// Mikali ammus on juuri ammuttu asetetaan aikalaskuri 1
				// suuruiseksi. Asettamalla aikalaskuri 1:seksi saadaan ammuksen
				// lentoradasta "aidomman" nakoinen, koska aikalaskurin arvo
				// vaikuttaa lentorataan.

				if (aikalaskuri == 0) {
					aikalaskuri = 1;
				}

				// Lisataan aikalaskuriin joka paivityskerralla 0.035.

				aikalaskuri += 0.035;

				// Suuntavektorin avulla saadaan ammukselle laskettua seuraavan
				// paikan koordinaatit.

				// Suuntavektori lasketaan vaakasuuntaisen nopeuden ja
				// pystysuuntaisen nopeuden avulla. Laskentakaavat tulevat
				// fysiikasta. Vaakakoordinaatit tietylla ajanhetkella saadaan
				// laskemalla vaakasuuntainen nopeus ja kerrotaan se
				// aikalaskurin arvolla. Pystykoordinaatit puolestaan saadaan
				// kertomalla vaakasuuntainen nopeus ja lisataan siihen
				// painovoiman vaikutus (0.3*9.81*aikalaskuri^2). Delta * 0.1f
				// antaa lentoradalle hieman aidomman tunnun.

				suuntavektori = suuntavektori.set(xd * aikalaskuri * delta
						* 0.1f, (float) (yd * aikalaskuri + 0.3 * 9.81
						* aikalaskuri * aikalaskuri)
						* delta * 0.1f);

				// Lisataan tamanhetkiseen paikkaan ylla laskettu suuntavektori.

				paikkavektori = paikkavektori.add(suuntavektori);

				// Lisataan uudessa paikassa oleva ammus tormayskasittelijan
				// ammuslistaan. Kerrotaan lisaksi tormayskasittelijalle
				// nykyinen sijainti, suuntavektori.

				tk.addAmmus(this);
				tk.setSuunta(suuntavektori);
				tk.setSeuraavapaikka(paikkavektori);

				// Kysytaan tormayskasittelijalta tormaako ammus nyt mihinkaan

				tk.osuuko();

			}
		}

	}

	/**
	 * Palauttaa suuntavektorin y -komponentin.
	 * 
	 * @return suuntavektorin y -komponentti
	 * **/

	public float getSuuntavektoriy() {
		return suuntavektori.y;
	}

	/**
	 * Asettaa aikalaskurin ajaksi parametrin ajan
	 * 
	 * @param aika
	 *            = haluttu aikalaskurin aika
	 * **/

	public void setAikalaskuri(float aika) {
		this.aikalaskuri = aika;
	}

	/**
	 * Palauttaa aikalaskurin ajan
	 * 
	 * @return aikalaskurin aika
	 * **/

	public float getAikalaskuri() {
		return this.aikalaskuri;
	}

	/**
	 * Palauttaa tankin1 ammuksen x -lahtokoordinaatin
	 * 
	 * @return tankin1 ammuksen x -lahtokoordinaatin
	 * **/
	public float getAlkux() {
		return alkux;
	}

	/**
	 * Asettaa tankin1 ammuksen x -lahtokoordinaatin
	 * 
	 * @param alkux
	 *            = haluttu x -lahtokooridnaatti
	 * **/
	public void setAlkux(float alkux) {
		this.alkux = alkux;
	}

	/**
	 * Palauttaa tankin1 ammuksen y -lahtokoordinaatin
	 * 
	 * @return tankin1 ammuksen y -lahtokoordinaatin
	 * **/

	public float getAlkuy() {
		return alkuy;
	}

	/**
	 * Asettaa tankin1 ammuksen y -lahtokoordinaatin
	 * 
	 * @param alkuy
	 *            = haluttu y -lahtokooridnaatti
	 * **/

	public void setAlkuy(float alkuy) {
		this.alkuy = alkuy;
	}

	/**
	 * Palauttaa tankin2 ammuksen x -lahtokoordinaatin
	 * 
	 * @return tankin2 ammuksen x -lahtokoordinaatin
	 * **/

	public float getAlkux1() {
		return alkux1;
	}

	/**
	 * Asettaa tankin2 ammuksen x -lahtokoordinaatin
	 * 
	 * @param alkux1
	 *            = haluttu x -lahtokooridnaatti
	 * **/

	public void setAlkux1(float alkux1) {
		this.alkux1 = alkux1;
	}

	/**
	 * Palauttaa tankin2 ammuksen y -lahtokoordinaatin
	 * 
	 * @return tankin2 ammuksen y -lahtokoordinaatin
	 * **/
	public float getAlkuy1() {
		return alkuy1;
	}

	/**
	 * Asettaa tankin2 ammuksen y -lahtokoordinaatin
	 * 
	 * @param alkuy1
	 *            = haluttu y -lahtokooridnaatti
	 * **/
	public void setAlkuy1(float alkuy1) {
		this.alkuy1 = alkuy1;
	}

	/**
	 * Palauttaa ammuksen lahtokulman
	 * 
	 * @return ammuksen lahtokulma radiaaneissa
	 **/

	public float getKulma() {
		return kulma;
	}

	/**
	 * Asettaa ammuksen lahtokulmaksi parametrin kulma arvon
	 * 
	 * @param kulma
	 *            = ammuksen haluttu lahtokulma
	 * **/

	public void setKulma(float kulma) {
		this.kulma = kulma;
	}

	/**
	 * Asetetaan onko ammus nakyvissa vai ei
	 * 
	 * @param nakyvissa
	 *            = true nakyy, false ei nay
	 * **/

	public void setNakyvissa(boolean nakyvissa) {
		this.nakyvissa = nakyvissa;
	}

	/**
	 * Onko ammus nakyvissa vai ei
	 * 
	 * @return true nakyy, false ei nay
	 * **/
	public boolean onkoNakyvissa() {
		return this.nakyvissa;
	}

	/**
	 * Asettaa vaakasuuntaiselle nopeudelle xd arvon
	 * 
	 * @param xd
	 *            = haluttu vaakasuuntainen nopeus
	 * **/

	public void setXd(float xd) {
		this.xd = xd;
	}

	/**
	 * Asettaa pystysuuntaiselle nopeudelle yd arvon
	 * 
	 * @param yd
	 *            = haluttu pystysuuntainen nopeus
	 * **/

	public void setYd(float yd) {
		this.yd = yd;
	}

	/**
	 * Palauttaa edellisen sijainnin x -koordinaatin
	 * 
	 * @return edellinenx = edellisen sijainnin x -koordinaatti
	 * **/

	public float getEdellinenx() {
		return edellinenx;
	}

	/**
	 * Asettaa edellisen sijainnin x -koordinaatin
	 * 
	 * @param edellinenx
	 *            = halutun sijainnin x -koordinaatti
	 * **/
	public void setEdellinenx(float edellinenx) {
		this.edellinenx = edellinenx;
	}

	/**
	 * Palauttaa edellisen sijainnin y -koordinaatin
	 * 
	 * @return edellineny = edellisen sijainnin y -koordinaatti
	 * **/
	public float getEdellineny() {
		return edellineny;
	}

	/**
	 * Asettaa edellisen sijainnin y -koordinaatin
	 * 
	 * @param edellineny
	 *            = halutun sijainnin y -koordinaatti
	 * **/
	public void setEdellineny(float edellineny) {
		this.edellineny = edellineny;
	}

	/**
	 * Palauttaa ammuksen leveyden
	 * 
	 * @return ammuksen leveys
	 **/

	public int getWidth() {
		return ammus.getWidth();

	}

	/**
	 * Metodi jolla kerrotaan etta ammus osuu johonkin. Asettaa osuman arvoksi
	 * true
	 * **/

	public void osuma() {
		this.osuma = true;

	}

	/**
	 * Metodi palauttaa tiedon onko osuttu vai ei
	 * 
	 * @return osuma = true on osuttu, false ei ole osuttu
	 * **/

	public boolean onkoOsuma() {
		return this.osuma;
	}

	/**
	 * Asetetaan onko pelaajan 1 vuoro vai ei
	 * 
	 * @param vuoro
	 *            = true pelaajan1 vuoro, false pelaajan2 vuoro
	 * **/

	public void setpelaajan1vuoro(boolean vuoro) {
		this.pelaajan1vuoro = vuoro;

	}

	/**
	 * Palauttaa onko pelaaja1 vuorossa vai ei
	 * 
	 * @return true pelaaja1 vuorossa, false pelaaja2 vuorossa
	 * **/

	public boolean getPelaajan1vuoro() {
		return this.pelaajan1vuoro;
	}

	/**
	 * Asettaa paikkavektorille x ja y -koordinaatit
	 * 
	 * @param x
	 *            = haluttu x -koordinaatti
	 * @param y
	 *            = haluttu y -koordinaatti
	 * **/

	public void setPaikkavektori(float x, float y) {
		this.paikkavektori.set(x, y);
	}

	/**
	 * Palauttaa paikkavektorin x -koordinaatin
	 * 
	 * @return paikkavektorin x -koordinatti
	 * **/
	public float getPaikkavektoriX() {
		return this.paikkavektori.x;
	}

	/**
	 * Palauttaa paikkavektorin y -koordinaatin
	 * 
	 * @return paikkavektorin y -koordinatti
	 * **/
	public float getPaikkavektoriY() {
		return this.paikkavektori.y;
	}

	/**
	 * Asettaa suuntavektorille x ja y -koordinaatit
	 * 
	 * @param x
	 *            = haluttu x -koordinaatti
	 * @param y
	 *            = haluttu y -koordinaatti
	 * **/
	public void setSuuntavektori(float x, float y) {
		this.suuntavektori.set(x, y);
	}

	/**
	 * Metodin avulla asetetaan ammuksen koko. Koko maaritellaan siita kuinka
	 * kauan hiiren vasenta painiketta on pidetty pohjassa
	 * 
	 * @param aikalaskuri
	 *            = kuinka kauan hiiren painiketta on pidetty pohjassa
	 * **/

	public void setSize(float aikalaskuri) {
		koko = aikalaskuri;

	}

	/**
	 * Asetetaan ammukselle tieto kasvatetaanko ammuksen kokoa viela, ts.
	 * pidetaanko hiiren vasenta painiketta viela pohjassa
	 * 
	 * @param kasvattaako
	 *            = true kasvaa, false ei kasva
	 * **/

	public void setKasvattaakokoaan(boolean kasvattaako) {
		this.kasvattaakokoaan = kasvattaako;
	}

	public boolean getKasvattaakokoaan() {
		return kasvattaakokoaan;
	}

	/**
	 * Palauttaa ammuksen koon
	 * 
	 * @return ammuksen koko
	 * **/

	public float getSkaalaSize() {

		return koko;
	}

	/**
	 * Asetetaan onko maastoa tuhoava ammus. Lisaksi tieto ammuksen tyypista
	 * kerrotaan tormayskasittelijalle.
	 * 
	 * @param onko
	 *            = true kyseessa tuhoava ammus, false kyseessa maastoa
	 *            kasvattava ammus
	 * **/
	public void setYkkosase(boolean onko) {
		tk.setYkkosase(onko);
		Ammus.ykkosase = onko;
	}

	/**
	 * Palauttaa tiedon onko kyseessa ykkosase eli maastoa tuhoava ammus
	 * 
	 * @return true jos on, false jos kyseessa on maastoa kasvattava ammus
	 * **/

	public static boolean getYkkosase() {
		return Ammus.ykkosase;
	}

}
