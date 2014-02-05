package MindBang.maailma.hallinta;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.GeomUtil;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import MindBang.maailma.Efefktit.Rajahdys;
import MindBang.maailma.objektit.Ammus;

/**
 * Tormayskasittelija -luokka vastaa pelin tormayksien hallinnasta.
 * Tormayskasittelija pitaa sisallaan listan maastosta (Maasto -luokka),
 * ammuksista (Ammus -luokka) ja tankkien tormaysympyroista (Tormaysympyra
 * -luokka). Kun ammus ammutaan, tallennetaan sen jokainen sijainti kerrallaan
 * tormayskasittelijan paikkavektoreihin. Yksinkertaistaen luokka luo jokaisen
 * sijainnin perusteella ympyran joka on kuin kopion ammuksesta. Tata kopiota
 * verrataan maaston sijaintiin seka tormaysympyroiden sijainteihin. Mikali
 * tormays (muodot kohtaavat/osuvat) havaitaan, toimitaan sen mukaisesti.
 * 
 * **/

public class Tormayskasittelija {

	private List<Shape> maastot; // Lista joka pitaa sisallaan pelin maaston
	private List<Ammus> ammukset; // Lista johon ammutut/lentavat ammukset
	// tallennetaan max 5 viimeisinta
	private Shape uusialue; // Tormaystilanteessa tarvitaan muoto tormaykselle
	private Circle tankkialue1, tankkialue2; // Tankkien tormaysympyrat
	private boolean ykkosase = true; // maastoa tuhoava vai kasvattava ammus
	private static boolean powerup = false; // erikoiskenttaa varten tehty
											// ammuksen ominaisuus
	private GeomUtil leikkaaja; // Tyokalu muotojen yhdistamiseen
	private Vector2f edellinenpaikka, seuraavapaikka, suuntavektori;
	private int tormayspisteet; // Tankin energian kasvattamiseen kytketty
								// muuttuja
	private int tankkipisteet; // Tankkien tormayksissa maariteltavan vahingon
								// muuttuja
	private Line tormaysviiva; // Tormayksen tarkentamiseen luotava viiva
	private Circle ammuscircle; // Ammuksen sijainnin mukaan luotava ympyra
	private Rajahdys rajahdysefekti; // Tormayksen sattuessa kaynnistetaan
	// rajahdysefekti
	private Sound HIT = new Sound("res/sound/HIT.ogg"); // Osuma aani

	/**
	 * Tormayskasittelija -luokan luontimetodi. Luodaan luokan listat seka
	 * muotojen yhdistamiseen kaytettava tyokalu GeomUtil.
	 * 
	 * @param rajahdysefekti
	 *            = Rajahdysefekti joka hyodyntaa slick2d:n ParticleSystemia
	 **/

	public Tormayskasittelija(Rajahdys rajahdysefekti) throws SlickException {

		maastot = new ArrayList<Shape>();
		ammukset = new ArrayList<Ammus>();
		leikkaaja = new GeomUtil();
		this.rajahdysefekti = rajahdysefekti;

	}

	/**
	 * Tormayskasittelijan keskeisin metodi. Ammuksen lentaessa tarkastellaan
	 * jatkuvasti osuuko ammusta kuvaava ympyra edellisessa kohdassaan maastoon
	 * tai tankkien tormaysympyroihin. Tormaystarkastelua
	 * tarkentamista/varmistamista varten metodissa lisaksi piirretaan kokoajan
	 * viiva edellisen paikan seka seuraavan paikan valille. Mikali tama viiva
	 * leikkaa jonkin pinnan rekisteroidaan tormays.
	 * **/

	public void osuuko() throws SlickException {

		// Pisteenlaskujarjestelman pisteet nollataan
		tormayspisteet = 0;

		// Loopataan maasto- ja ammuslistat lapi
		for (int i = 0; i < maastot.size(); i++) {
			for (int j = 0; j < ammukset.size(); j++) {

				// Luodaan maastosta muoto nimeltaan tormaysalue

				Shape tormaysalue = maastot.get(i);

				// Luodaan lentavasta ammuksesta ammus

				Ammus ammus = ammukset.get(j);

				// Tallennetaan kyseisen ammuksen kasvatettu koko muuttujaan w2
				// (eli alkuperainen koko kerrottuna skaalalla)

				float w2 = ammus.getWidth() * ammus.getSkaalaSize();

				// Piirretaan jatkuvasti ammuksen edellisen paikan ja siita
				// seuraavan paikan (seuraavapaikka) valille viiva

				tormaysviiva = new Line(edellinenpaikka, seuraavapaikka);

				// Piirretaan edelliseen paikkaan ympyra jonka sade
				// on ylla tallennettu w2

				ammuscircle = new Circle(edellinenpaikka.x, edellinenpaikka.y,
						w2 / 2);

				// Mikali ammuscircle tormaa jompaan kumpaan tankkialueeseen ja
				// ammuksen lentoaika on yli 2.4 (tama tarkastellaan jotta
				// ammus ei tormaa lahtiessaan tankin omaan tormaysalueeseen)
				// suoritetaan seuraava lohko

				if ((ammuscircle.intersects(tankkialue1) || ammuscircle
						.intersects(tankkialue2))
						&& ammus.getAikalaskuri() > 2.4f) {

					// Soitetaan osumisaani
					HIT.play();
					// Määritetään räjähdysefektin väri tällä muuttujalla
					boolean rajahdyskeltainen = true;

					// Mikali pelaaja omalla vuorollaan onnistuu ampumaan omaa
					// tormaysaluettaan on varmistettava ettei talloin
					// vahingoteta vastustajan tankkia (pelin pistejarjestelma
					// on kytkoksissa vuoroihin). Alla oleva if -lause
					// vuorottaa osuman siten etta omalla vuorollaan itsensa
					// ampuminen ei tee mitaan.

					if ((ammuscircle.intersects(tankkialue1) && ammus
							.getPelaajan1vuoro() == true)
							|| (ammuscircle.intersects(tankkialue2) && ammus
									.getPelaajan1vuoro() == false)) {

					} else {

						// Mikali kyseessa on vastustajan tormaysalue
						// vahennetaan tormaysympyran sateesta ammuksen sade
						// Pisteenlaskija luokka saa tiedon tankkipisteiden
						// kasvamisesta jolloin osutun tankin sade muutetaan.
						if (this.isYkkosase())
							tankkipisteet += w2 / 2;
						else {
							tankkipisteet += -w2 / 2;
							rajahdyskeltainen = false;
						}
					}

					// Asetetaan osumakohtaan ammuksen tyypista riippuva
					// rajahdys
					rajahdysefekti.asetaTankkiosuma(edellinenpaikka.x,
							edellinenpaikka.y, w2, rajahdyskeltainen);

					// Asetetaan ammus nakymattomaksi
					ammus.setNakyvissa(false);

					// Nollataan ammuksen lentoajan laskuri
					ammus.setAikalaskuri(0);

					// Nollataan lentoradan pisteet
					ammus.setEdellinenx(0);
					ammus.setEdellineny(0);

					// Poistetaan kaikki ammukset tormayskasittelijan listasta
					ammukset.clear();

					// Rekisteroidaan osuminen ammukselle
					ammus.osuma();

					// Hypataan metodista ulos
					break;
				}

				// Mikali tormaysviiva tai ammuscircle osuu tormaysalueeseen
				// (eli maastoon) suoritetaan seuraava lohko. Kaksinkertainen
				// tarkistus lisaa tormayksen tarkkuuden laskemista.

				if (tormaysviiva.intersects(tormaysalue)
						|| ammuscircle.intersects(tormaysalue)) {

					// Soitetaan osumisaani

					HIT.play();

					// Mikali tormaysviiva leikkaa tormaysalueen. Tassa
					// tilanteessa siis edellinen paikka on viela maaston
					// ulkopuolella ja seuraava paikka maaston sisalla.

					if (tormaysviiva.intersects(tormaysalue)) {

						// Otetaan ammuksen suuntavektori ja normalisoidaan
						// se.

						suuntavektori = suuntavektori.normalise();

						// Lisataan edelliseen paikkaan normalisoitu
						// suuntavektori niin monta kertaa kunnes
						// edellinenpaikka on myos maaston sisalla.

						while (!tormaysalue.contains(edellinenpaikka.x,
								edellinenpaikka.y)) {

							edellinenpaikka = edellinenpaikka
									.add(suuntavektori);
						}
					}

					// Tallennetaan tama edellisen paikan koordinaatit
					// valiaikaismuuttujiin

					float edex = edellinenpaikka.x;
					float edey = edellinenpaikka.y;

					// Piirretaan kohtaan ympyra. Tata ympyraa kaytetaan
					// maaston muokkausta varten. Ympyra on siis
					// tormayksesta
					// syntyva rajahdys.

					Circle rajahdys = new Circle(edex, edey, w2);

					// Mikali kyseessa on maastoa kasvattava ammus,
					// liitetaan ylla luotu rajahdysympyra maastoon ja
					// tallennetaan uusi maasto muuttujaan uusialue. Tassa
					// kaytetaan apuna slick kirjaston GeomUtil tyokalua
					// (leikkaaja). Ajetaan lisaksi rajahdys -luokan metodi
					// maaston kasvattamisefektin saamiseksi.

					if (!this.isYkkosase()) {
						uusialue = leikkaaja.union(tormaysalue, rajahdys)[0];
						rajahdysefekti.asetaKasvatus(edex, edey, w2);
						tormayspisteet += w2 / 2;
						ammus.setNakyvissa(false);

						// Mikali kyseessa on maastoa tuhoava ammus ja
						// maastoa on jaljella, vahennetaan maastosta
						// rajahdysympyra ja tallennetaan uusi maasto muuttujaan
						// uusialue. Ajetaan lisaksi rajahdysefekti
						// tuhoamiselle.

					} else {
						if (leikkaaja.subtract(tormaysalue, rajahdys).length > 0) {
							uusialue = leikkaaja
									.subtract(tormaysalue, rajahdys)[0];
							rajahdysefekti.asetaRajahdys(edex, edey, w2);
							// Mikali erikoiskenttaa varten tehty muuttuja
							// powerup on true, ei ammus katoakaan nakyvista
							// tormayksen sattuessa.
							if (powerup == false)
								ammus.setNakyvissa(false);

							// Mikali tuhottavaa maastoa ei ole, lopetetaan
							// metodin suoritus

						} else {
							break;
						}

					}

					// Poistetaan vanha maasto maastot listasta ja lisataan
					// rajahdyksen jalkeinen uusialue listaan. Asetetaan
					// nykyiseksi maastoksi uusialue.

					maastot.remove(tormaysalue);
					maastot.add(uusialue);
					

					// Nollataan ammuksen lentoajan laskuri
					ammus.setAikalaskuri(0);
					// Nolla taan paikka koordinaatit
					ammus.setEdellinenx(0);
					ammus.setEdellineny(0);
					// Rekisteroidaan osuma Ammus -luokkaan
					ammus.osuma();
					// Tyhjennetaan tormayskasittelijan ammuslista.
					ammukset.clear();
					// Hypataan metodista ulos
					break;

				}
			}
		}

	}

	/**
	 * Asettaa tehokkaamman/erikoisen ammuksen kayttoon.
	 * 
	 * powerup = true eli tehokkaampi ammus paalla
	 * **/

	public static void setPowerup() {
		Tormayskasittelija.powerup = true;
	}

	/**
	 * Poistaa tehokkaamman/erikoisen ammuksen kaytosta.
	 * 
	 * powerup = false eli tehokkaampi ammus pois paalta
	 * **/

	public static void removePowerup() {
		Tormayskasittelija.powerup = false;
	}

	/**
	 * Asettaa talle Tormayskasittelija luokalle tiedon siita missa ammus on
	 * nykyisella ajanhetkella. Eli asetetaan tormayskasittelijan ammuksen
	 * paikkavektori parametrien mukaisesti.
	 * 
	 * @param edellinen
	 *            = Ammusluokasta saatu paikkavektori
	 * **/

	public void setEdellinenpaikka(Vector2f edellinen) {
		this.edellinenpaikka = edellinen;
	}

	/**
	 * Asetetaan lentoradan mukaisesti laskettu seuraava paikka parametrilla.
	 * Tamakin tieto saadaan Ammus -luokalta.
	 * 
	 * @param seuraava
	 *            = lentoradan seuraavan kohdan paikkavektori
	 * **/

	public void setSeuraavapaikka(Vector2f seuraava) {
		this.seuraavapaikka = seuraava;
	}

	/**
	 * Asetetaan lentoradan suuntavektori kyseisella ajanhetkella.
	 * 
	 * @param suunta
	 *            = Ammus -luokalta saatu suuntavektori
	 * **/

	public void setSuunta(Vector2f suunta) {
		this.suuntavektori = suunta;
	}

	

	/**
	 * Slick kirjaston update -metodi. Tormayskasittelijaluokan metodissa
	 * tarkastellaan ammuslistan kokoa ja nollataan se, mikali alkioita on yli
	 * 5.
	 * **/

	public void update(GameContainer gc, int delta) throws SlickException {
		if (ammukset.size() > 5) {
			ammukset.clear();
		}

	}

	/**
	 * Metodilla lisataan parametrien mukainen ammus ammuslistaan
	 * 
	 * @param ammus
	 *            = mika ammus halutaan lista listaan
	 * **/

	public void addAmmus(Ammus ammus) {
		ammukset.add(ammus);
	}

	/**
	 * Metodilla tyhjennetaan ammuslista
	 * 
	 * 
	 * **/

	public void removeAmmus() {
		ammukset.clear();
	}

	/**
	 * Lisataan muoto maastot listaan.
	 * 
	 * @param shape
	 *            = muoto joka halutaan lisata maastolistaan
	 * **/

	public void add(Shape shape) {
		maastot.add(shape);
	}

	public List<Shape> getMaastot() {
		return maastot;
	}

	/**
	 * Poistetaan maastot listasta parametrina annettu muoto
	 * 
	 * @param shape
	 *            = muoto joka halutaan poistaa
	 * **/

	public void remove(Shape shape) {
		maastot.remove(shape);
	}

	/**
	 * Palauttaa tormayspisteet. Pisteenlaskujarjestelman apumetodi.
	 * Pisteenlaskija -luokka kayttaa tata metodia.
	 * 
	 * @return tormayspisteet = tormayspisteiden lukumaara riippuu tormayksen
	 *         tyypista
	 * **/

	public int getTormayspisteet() {
		return tormayspisteet;
	}

	/**
	 * Asettaa tormayspisteet parametrin mukaiseksi
	 * 
	 * @param tp
	 *            = tormayspisteet
	 * **/

	public void setTormayspisteet(int tp) {
		tormayspisteet = tp;
	}

	/**
	 * Palauttaa tankkipisteet. Pisteenlaskujarjestelman apumetodi.
	 * Pisteenlaskija -luokka kayttaa tata metodia.
	 * 
	 * @return tankkipisteet = tankkipisteiden lukumaara riippuu tormayksen
	 *         tyypista
	 * **/

	public int getTankkipisteet() {
		return tankkipisteet;
	}

	/**
	 * Asettaa tankkipisteet parametrin mukaiseksi
	 * 
	 * @param tp
	 *            = tankkipisteet
	 * **/
	public void setTankkipisteet(int tp) {
		tankkipisteet = tp;
	}

	/**
	 * Palauttaa onko maastoa tuhoava ykkosase aseistettu
	 * 
	 * @return true mikali on, false ei ole
	 * **/
	public boolean isYkkosase() {
		return ykkosase;
	}

	/**
	 * Asetetaan mika asetyyppi on halutaan
	 * 
	 * @param ykkosase
	 *            = true halutaan maastoa tuhoava ase, false halutaan maastoa
	 *            kasvattava ase
	 * **/

	public void setYkkosase(boolean ykkosase) {
		this.ykkosase = ykkosase;
	}

	/**
	 * Palauttaa tankin1 tormaysympyran
	 * 
	 * @return tankkialue1 = tankin1 tormysympyra
	 * **/

	public Circle getTankkialue1() {
		return tankkialue1;
	}

	/**
	 * Asettaa tankin1 tormaysympyran.
	 * 
	 * @param tankkialue1
	 *            = uusi tankin1 tankkiympyra
	 * **/
	public void setTankkialue1(Circle tankkialue1) {
		this.tankkialue1 = tankkialue1;
	}

	/**
	 * Palauttaa tankin2 tormaysympyran
	 * 
	 * @return tankkialue2 = tankin2 tormysympyra
	 * **/

	public Circle getTankkialue2() {
		return tankkialue2;
	}

	/**
	 * Asettaa tankin2 tormaysympyran.
	 * 
	 * @param tankkialue2
	 *            = uusi tankin2 tankkiympyra
	 * **/
	public void setTankkialue2(Circle tankkialue2) {
		this.tankkialue2 = tankkialue2;
	}

}
