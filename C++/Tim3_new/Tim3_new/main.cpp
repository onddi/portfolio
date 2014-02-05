#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>
#include <SFML/Window.hpp>
#include <iostream>
#include <fstream>
#include <Box2D\Box2D.h>
#include "objects.h"
#include "Gamelogic.h"
#include <list>
#include "Guielement.h"
#include "Menu.h"

#include <stdlib.h>     /* srand, rand */
#include <time.h>       /* time */

int main()
{
	Menu::GameState state = Menu::GameState::MENU;

	std::fstream config("config.lol");
	int screenwidth, screenheight;
	float musicvolume, soundvolume, gravity;

	if (!config)
	{
		std::cout << "no configuration file found, using default values" << std::endl;
		screenwidth = 1280, screenheight = 800, musicvolume = 60, soundvolume = 80, gravity = 9.8f;
	}
	else
		config >> screenwidth >> screenheight >> musicvolume >> soundvolume >> gravity;

	sf::ContextSettings settings;
	settings.antialiasingLevel = 8;
	sf::RenderWindow window(sf::VideoMode(screenwidth, screenheight), "TIM3", sf::Style::Default, settings); // ikkuna
	window.setFramerateLimit(60);
	window.setKeyRepeatEnabled(true);//false);
	float camx = 0;//kameran keskipiste
	float camy = 0;
	float zoom = 200;//montako pikseli‰ per metri

	sf::View view1;
	view1.setCenter(sf::Vector2f(camx, camy));
	view1.setSize(sf::Vector2f(zoom * window.getSize().x / window.getSize().y, zoom));
	window.setView(view1);

	sf::SoundBuffer buffer;													//‰‰nibufferi
	if (!buffer.loadFromFile("sounds/click.wav"))							//ladataan ‰‰ni bufferiin
		std::cout << "error reading sound from file" << std::endl;
	sf::Sound click;														//Sound olio soittaa ‰‰nen bufferista
	click.setBuffer(buffer);

	sf::SoundBuffer buffer1;													
	if (!buffer1.loadFromFile("sounds/drop.wav"))							
		std::cout << "error reading sound from file" << std::endl;
	sf::Sound drop;														
	drop.setBuffer(buffer1);

	sf::SoundBuffer buffer2;
	if (!buffer2.loadFromFile("sounds/rocket.wav"))
		std::cout << "error reading sound from file" << std::endl;
	sf::Sound missile;
	missile.setBuffer(buffer2);

	sf::SoundBuffer buffer3;
	if (!buffer3.loadFromFile("sounds/bomb.wav"))
		std::cout << "error reading sound from file" << std::endl;
	sf::Sound bomb;
	bomb.setBuffer(buffer3);

	sf::SoundBuffer buffer4;
	if (!buffer4.loadFromFile("sounds/blop.wav"))
		std::cout << "error reading sound from file" << std::endl;
	sf::Sound blop;
	blop.setBuffer(buffer4);

	sf::SoundBuffer buffer5;
	if (!buffer5.loadFromFile("sounds/win.wav"))
		std::cout << "error reading sound from file" << std::endl;
	sf::Sound winsound;
	winsound.setBuffer(buffer5);

	std::vector<std::string> texnames;
	texnames.push_back("textures/button1.png"); //0
	texnames.push_back("textures/brick.png");
	texnames.push_back("textures/ball.png");
	texnames.push_back("textures/rocket.png");
	texnames.push_back("textures/fire.png");
	texnames.push_back("textures/button3.gif");
	texnames.push_back("textures/button3pressed.gif");
	texnames.push_back("textures/minus.png");
	texnames.push_back("textures/minuspressed.png");
	texnames.push_back("textures/plus.png");
	texnames.push_back("textures/pluspressed.png");
	texnames.push_back("textures/tank.png");//11
	texnames.push_back("textures/convey.png");
	texnames.push_back("textures/button1pressed.png"); //13
	texnames.push_back("textures/menubackground.gif"); 
	texnames.push_back("textures/background.png");
	texnames.push_back("textures/inventoryBg.png");
	texnames.push_back("textures/inventoryText.png");
	texnames.push_back("textures/missing_item.png");
	texnames.push_back("textures/active_item.png");
	texnames.push_back("textures/laser.png");
	texnames.push_back("textures/inventoryBrick.png"); // 21
	texnames.push_back("textures/inventoryTank.png");
	texnames.push_back("textures/inventoryConvey.png");
	texnames.push_back("textures/inventoryLaser.png");
	texnames.push_back("textures/levelegui.png"); //25
	texnames.push_back("textures/bouncy.png");


	std::vector<sf::Texture*>* texvector = new std::vector<sf::Texture*>(texnames.size());
	std::vector<sf::Sound*>* soundvector = new std::vector<sf::Sound*>();
	soundvector->push_back(new sf::Sound(click));
	soundvector->push_back(new sf::Sound(drop));
	soundvector->push_back(new sf::Sound(missile));
	soundvector->push_back(new sf::Sound(bomb));
	soundvector->push_back(new sf::Sound(blop));
	soundvector->push_back(new sf::Sound(winsound));

	std::vector<sf::Music*>* musicvector = new std::vector<sf::Music*>();

	sf::Music *menumusic = new sf::Music();
	if (!(*menumusic).openFromFile("sounds/music/Club Diver.ogg"))
		std::cout << "error reading music from file" << std::endl;

	sf::Music *music1 = new sf::Music();
	if (!(*music1).openFromFile("sounds/music/Harmful or Fatal.ogg"))
		std::cout << "error reading music from file" << std::endl;

	sf::Music *music2 = new sf::Music();
	if (!(*music2).openFromFile("sounds/music/Junkyard Tribe.ogg"))
		std::cout << "error reading music from file" << std::endl;

	sf::Music *music3 = new sf::Music();
	if (!(*music3).openFromFile("sounds/music/Rising.ogg"))
		std::cout << "error reading music from file" << std::endl;

	sf::Music *music4 = new sf::Music();
	if (!(*music4).openFromFile("sounds/music/All This.ogg"))
		std::cout << "error reading music from file" << std::endl;

	sf::Music *music5 = new sf::Music();
	if (!(*music5).openFromFile("sounds/music/The Complex.ogg"))
		std::cout << "error reading music from file" << std::endl;

	sf::Font *font = new sf::Font();
	if (!font->loadFromFile("fonts/OpenSans-Regular.ttf"))
		std::cout << "error reading font from file ubuntu" << std::endl;

	musicvector->push_back(menumusic);
	musicvector->push_back(music1);
	musicvector->push_back(music2);
	musicvector->push_back(music3);
	musicvector->push_back(music4);
	musicvector->push_back(music5);

	for (auto it : *musicvector)
	{
		it->setLoop(true);
		it->setVolume(musicvolume);
	}

	for (auto it : *soundvector)
		it->setVolume(soundvolume);

	int i = 0;
	for (auto it : texnames)
	{
		sf::Texture* texture = new sf::Texture;
		if (!texture->loadFromFile(it))
		{
			std::cout << "error reading texture from file " << it << std::endl;
		}
		else
		{
			texture->setSmooth(true);
			(*texvector)[i] = texture;
		}
		i++;
	}
	Gamelogic* gamelogic = new Gamelogic(&window, texvector, *soundvector, font);
	Menu menu(&window, texvector, soundvector, musicvector);

	while (window.isOpen())
	{
		sf::Vector2f size = sf::Vector2f(float(window.getSize().x), float(window.getSize().y));

		if (state == Menu::GameState::CONTINUE_GAME)
		{
			state = gamelogic->run();
		}
		else if (state == Menu::GameState::MENU)
		{
			for (auto it : *musicvector) //pausetetaan mahollisesti soivat musat, kaynnistetaan menumusa
			{
				if (it->getStatus() == sf::SoundSource::Playing)
					it->pause();
			}

			(*musicvector)[0]->play();

			view1.reset(sf::FloatRect(0, 0, size.x, size.y)); //vaihdetaan kamera takaisin n‰yttˆkoordinaatistoon guin piirtoa varten
			window.setView(view1);
			state = menu.run();
		}
		else if (state == Menu::GameState::GAME)
		{
			std::string level = gamelogic->level->currentlevel;
			delete gamelogic;
			gamelogic = new Gamelogic(&window, texvector, *soundvector, font);
			gamelogic->level->loadLevel(gamelogic->world, *soundvector, level);
			state = Menu::GameState::CONTINUE_GAME;

			for (auto it : *musicvector)
				it->stop();
			menu.toggleMusics();
		}
		else if (state == Menu::GameState::SAVE_LEVEL){
			gamelogic->level->saveObjects(gamelogic->world);
			state = Menu::GameState::MENU;
	
		}
		else if (state == Menu::GameState::LOAD_LEVEL){

			delete gamelogic;
			gamelogic = new Gamelogic(&window, texvector, *soundvector, font);
			gamelogic->level->loadLevel(gamelogic->world, *soundvector);
			state = Menu::GameState::CONTINUE_GAME;
			//gamelogic->level->editorMode = false;

			for (auto it : *musicvector)
				it->stop();
			menu.toggleMusics();
		}
		else if (state == Menu::GameState::EDITOR){

			delete gamelogic;
			std::cout << "jou, editori!" << std::endl;
			gamelogic = new Gamelogic(&window, texvector, *soundvector, font);
			gamelogic->level->loadLevel(gamelogic->world, *soundvector);
			gamelogic->editorModeOn = true;
			gamelogic->showInventoryList = false;
			state = Menu::GameState::CONTINUE_GAME;

			for (auto it : *musicvector)
				it->stop();
			menu.toggleMusics();
		}

		else
			window.close();

		window.display();
		window.clear();
	}
	delete gamelogic;
	for (auto it : *texvector)
	{
		delete it;
	}
	delete texvector;
	return 0;
}
