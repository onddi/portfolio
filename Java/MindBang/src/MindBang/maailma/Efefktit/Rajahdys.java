package MindBang.maailma.Efefktit;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.particles.effects.FireEmitter;

import MindBang.maailma.objektit.Ammus;

/**
 * Rajahdys -luokka vastaa pelin tormaysefekteista. Luokka hyodyntaa slick
 * kirjaston ParticleSystem -luokkaa seka ConfigurableEmitter -luokkaa. Lisaksi
 * Rajahdys -luokka vastaa kaikkien pelitilojen taustalle renderöitävästä
 * "avaruudesta"
 * **/

public class Rajahdys {

	private ParticleSystem system, system1, system2; // Partikkelisysteemit
														// jotka vastaavat
														// efektien
														// piirtamisesta
														// ruudulle

	private ConfigurableEmitter emitter1, emitter2; // Efektin piirtamiseksi
													// tarvitaan efektin
													// synnyttaja eli emitter

	/**
	 * Rajahdys -luokan luontimetodi.
	 * **/

	public Rajahdys() throws SlickException {

		// Luodaan kuvat rajahdysefekin partikkeleille.

		Image image = new Image("res/ammus/ammus1.png", false);
		Image image1 = new Image("res/particles/001.tga", false);
		
		// Luodaan partikkelisysteemit jokat kayttavat yllaluotua kuvaa
		// partikkeleilleen

		system = new ParticleSystem(image, 300);
		system1 = new ParticleSystem(image, 300);
		system2 = new ParticleSystem(image1, 400);

		try {

			// Asetetaan emitteri kayttamaan xml tiedostoa. Partikkelisysteemi
			// piirtaa rajahdyksen emitterin avulla

			emitter1 = ParticleIO.loadEmitter("res/particles/flame.xml");
			emitter2 = ParticleIO.loadEmitter("res/particles/tahdet.xml");

		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}

		
		// Poistetaan partikkelisysteemista valmiit emitterit
		system.setRemoveCompletedEmitters(true);
		system1.setRemoveCompletedEmitters(true);
		system2.setRemoveCompletedEmitters(true);
		// Partikkelisysteemi piirtomuoto
		system.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
		system1.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
		// Lisataan emitteri systeemiin2 joka vastaa avaruuden renderoinnista
		system2.addEmitter(emitter2);
	}

	/**
	 * Metodi tuhoavan ammuksen efektia varten
	 * 
	 * @param x
	 *            = missa x -koordinaatissa rajahdys esitetaan
	 * @param y
	 *            = missa y -koordinaatissa rajahdys esitetaan
	 * @param skaala
	 *            = kuinka suurena efekti esitetään
	 * **/

	public void asetaRajahdys(float x, float y, float skaala)
			throws SlickException {

		// Poistetaan kaikki systeemin emitterit
		system.removeAllEmitters();
		// Kopioidaan alkuperainen emitteri uuteen emitteriin
		ConfigurableEmitter e = emitter1.duplicate();
		// Asetetaan emitteri kayttamaan rajahdyksen kuvaa
		e.setImageName("res/particles/001.tga");
		e.spread.setValue(360);
		e.initialSize.setMax(150 + skaala);
		// Asetetaan emitteri nakyvaksi
		e.setEnabled(true);
		// Asetetaan emitterin sijainti
		system.setPosition(x, y);
		// Lisataan emitteri partikkelisysteemiin
		system.addEmitter(e);

	}

	/**
	 * Metodi maastoa kasvattavan ammuksen efektia varten
	 * 
	 * @param x
	 *            = missa x -koordinaatissa rajahdys esitetaan
	 * @param y
	 *            = missa y -koordinaatissa rajahdys esitetaan
	 * @param skaala
	 *            = kuinka suurena efekti esitetään
	 * **/

	public void asetaKasvatus(float x, float y, float skaala) {
		System.out.println("PALJON SKAALA ON " + skaala);
		// Poistetaan kaikki systeemin emitterit
		system.removeAllEmitters();
		// Kopioidaan alkuperainen emitteri uuteen emitteriin
		ConfigurableEmitter e = emitter1.duplicate();
		// Asetetaan emitteri kayttamaan tankkitormays kuvaa
		e.setImageName("res/particles/lotus.png");
		e.gravityFactor.setValue(-1);
		e.initialSize.setMax(150 + skaala);
		// Asetetaan emitteri nakyvaksi
		e.setEnabled(true);
		// Asetetaan emitterin sijainti
		system.setPosition(x, y);
		// Lisataan emitteri partikkelisysteemiin
		system.addEmitter(e);

	}

	/**
	 * Metodi ajaa efektin mikali osutaan tankkien tormaysympyroihin
	 * 
	 * @param x
	 *            = missa x -koordinaatissa rajahdys esitetaan
	 * @param y
	 *            = missa y -koordinaatissa rajahdys esitetaan
	 * @param skaala
	 *            = kuinka suurena efekti esitetään
	 * @param vari
	 *            = minka varinen osuma on kyseessa
	 * **/

	public void asetaTankkiosuma(float x, float y, float skaala, boolean vari) {
		// Poistetaan kaikki systeemin emitterit
		system.removeAllEmitters();
		// Kopioidaan alkuperainen emitteri uuteen emitteriin
		ConfigurableEmitter e = emitter1.duplicate();
		// Asetetaan emitteri kayttamaan tankkiin liittyvän törmäyksen kuvaa
		if (vari == true)
			e.setImageName("res/particles/buddha.png");
		else
			e.setImageName("res/particles/buddha1.png");
		e.spread.setValue(0);
		e.initialSize.setMax(100 + skaala);
		e.spawnCount.setMax(1);
		// Asetetaan emitteri nakyvaksi
		e.setEnabled(true);
		// Asetetaan emitterin sijainti
		system.setPosition(x, y);
		// Lisataan emitteri partikkelisysteemiin
		system.addEmitter(e);

	}

	/**
	 * Metodi ajaa pelaajan tuhoutumisefektin
	 * 
	 * @param x
	 *            = missa x -koordinaatissa rajahdys esitetaan
	 * @param y
	 *            = missa y -koordinaatissa rajahdys esitetaan
	 * **/

	public void asetaTuhoutumisrajahdys(float x, float y) {
		system.removeAllEmitters();
		system1.removeAllEmitters();
		ConfigurableEmitter e = emitter1.duplicate();
		e.setImageName("res/munkki1voitto.png");
		e.setEnabled(true);
		e.spread.setValue(0);
		e.spawnCount.setMax(20);
		// Asetetaan emitterin sijainti
		system.setPosition(x, y);
		// Lisataan emitteri partikkelisysteemiin
		system.addEmitter(e);
	}

	/**
	 * Ammusta ladatessa tama metodi ajaa riippuen ammuksen tyypista tietyn
	 * latausefektin
	 * 
	 * @param x
	 *            = missa x -koordinaatissa rajahdys esitetaan
	 * @param y
	 *            = missa y -koordinaatissa rajahdys esitetaan
	 * **/

	public void asetaTankinlataus(int x, int y) {
		system1.removeAllEmitters();

		FireEmitter e = new FireEmitter(x + 20, y - 30, 40);
		if (Ammus.getYkkosase() == true)
			system1.setDefaultImageName("res/ammus/ammus1.png");
		else
			system1.setDefaultImageName("res/ammus/ammus2.png");
		// Lisataan emitteri partikkelisysteemiin
		system1.addEmitter(e);

	}

	/**
	 * Poistaa kaikki partikkelisysteemien system ja system1 emitterit.
	 * **/
	public void poistaRajahdykset() {
		system1.removeAllEmitters();
		system.removeAllEmitters();
	}

	/**
	 * Piirretaan partikkelisysteemit ruudulle
	 * **/

	public void render(GameContainer gc, Graphics g) {
		system.render();
		system1.render();
		system2.render();
	}

	/**
	 * Paivitetaan partikkelisysteemin logiikka
	 * **/
	public void update(GameContainer gc, int delta) {
		system.update(delta);
		system1.update(delta);
		system2.update(delta);
	}

}
