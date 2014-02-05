#include "Menu.h"
#include "Guielement.h"
#include <iostream>
#include <fstream>

Menu::Menu(sf::RenderWindow* win, std::vector<sf::Texture*>* tex, std::vector<sf::Sound*>* sounds, std::vector<sf::Music*>* musics)
{

	if (!font.loadFromFile("fonts/OpenSans-Regular.ttf"))
		std::cout << "error reading font from file ubuntu" << std::endl;


	running = true;
	optionsOn = false;

	soundvector = sounds;
	musicvector = musics;

	(*musicvector)[0]->play();

	texvector = tex;
	window = win;

	sf::Texture;

	background.setTexture(*(*texvector)[14], true);

	allowedResolutions.push_back(std::make_pair(640, 480));
	allowedResolutions.push_back(std::make_pair(800, 600));
	allowedResolutions.push_back(std::make_pair(1024, 768));
	allowedResolutions.push_back(std::make_pair(1280, 720));
	allowedResolutions.push_back(std::make_pair(1280, 800));
	allowedResolutions.push_back(std::make_pair(1280, 1024));
	allowedResolutions.push_back(std::make_pair(1360, 768));
	allowedResolutions.push_back(std::make_pair(1400, 1050));
	allowedResolutions.push_back(std::make_pair(1440, 900));
	allowedResolutions.push_back(std::make_pair(1600, 1200));
	allowedResolutions.push_back(std::make_pair(1680, 1050));
	allowedResolutions.push_back(std::make_pair(1920, 1080));

	resolutionSelect = -1;
	for (auto it : allowedResolutions)
	{
		resolutionSelect++;
		if (window->getSize().x == it.first && window->getSize().y == it.second)
			break;	
	}

	if (window->getSize().x <= 800)
		element_x_spacing = 50;

	if (window->getSize().y <= 750)
		element_y_spacing = 50;

	elementvector.push_back(new Guielement(176, 46, element_x_spacing, element_y_spacing, (*texvector)[5], (*texvector)[6], (*soundvector)[0], sf::Text("New game", font, 18), "newgame"));
	elementvector.push_back(new Guielement(176, 46, element_x_spacing, 2 * element_y_spacing, (*texvector)[5], (*texvector)[6], (*soundvector)[0], sf::Text("Continue game", font, 18), "continuegame"));
	elementvector.push_back(new Guielement(176, 46, element_x_spacing, 3 * element_y_spacing, (*texvector)[5], (*texvector)[6], (*soundvector)[0], sf::Text("Load game", font, 18), "loadgame"));
	elementvector.push_back(new Guielement(176, 46, element_x_spacing, 4 * element_y_spacing, (*texvector)[5], (*texvector)[6], (*soundvector)[0], sf::Text("Save game", font, 18), "savegame"));
	elementvector.push_back(new Guielement(176, 46, element_x_spacing, 5 * element_y_spacing, (*texvector)[5], (*texvector)[6], (*soundvector)[0], sf::Text("Options", font, 18), "options"));
	elementvector.push_back(new Guielement(176, 46, element_x_spacing, 6 * element_y_spacing, (*texvector)[5], (*texvector)[6], (*soundvector)[0], sf::Text("Editor", font, 18), "editor"));
	elementvector.push_back(new Guielement(176, 46, element_x_spacing, 7 * element_y_spacing, (*texvector)[5], (*texvector)[6], (*soundvector)[0], sf::Text("Exit game", font, 18), "exitgame"));

	optionsvector.push_back(new Guielement(220, 40, 4 * element_x_spacing + 100, element_y_spacing, (*texvector)[0], sf::Text("Resolution: " + std::to_string(allowedResolutions[resolutionSelect].first) + "x" + std::to_string(allowedResolutions[resolutionSelect].second), font, 18), "resolution"));
	optionsvector.push_back(new Guielement(40, 40, 4 * element_x_spacing + 40, element_y_spacing, (*texvector)[7], (*texvector)[8], (*soundvector)[0], "resominus"));
	optionsvector.push_back(new Guielement(40, 40, 4 * element_x_spacing + 340, element_y_spacing, (*texvector)[9], (*texvector)[10], (*soundvector)[0], "resoplus"));
	optionsvector.push_back(new Guielement(220, 40, 4 * element_x_spacing + 100, 2 * element_y_spacing, (*texvector)[0], sf::Text("Music volume: " + std::to_string(int(round((*musicvector)[0]->getVolume()))), font, 18), "mvolume"));
	optionsvector.push_back(new Guielement(40, 40, 4 * element_x_spacing + 40, 2 * element_y_spacing, (*texvector)[7], (*texvector)[8], (*soundvector)[0], "mvolumeminus"));
	optionsvector.push_back(new Guielement(40, 40, 4 * element_x_spacing + 340, 2 * element_y_spacing, (*texvector)[9], (*texvector)[10], (*soundvector)[0], "mvolumeplus"));
	optionsvector.push_back(new Guielement(220, 40, 4 * element_x_spacing + 100, 3 * element_y_spacing, (*texvector)[0], sf::Text("Sound volume: " + std::to_string(int(round((*soundvector)[0]->getVolume()))), font, 18), "svolume"));
	optionsvector.push_back(new Guielement(40, 40, 4 * element_x_spacing + 40, 3 * element_y_spacing, (*texvector)[7], (*texvector)[8], (*soundvector)[0], "svolumeminus"));
	optionsvector.push_back(new Guielement(40, 40, 4 * element_x_spacing + 340, 3 * element_y_spacing, (*texvector)[9], (*texvector)[10], (*soundvector)[0], "svolumeplus"));
	optionsvector.push_back(new Guielement(340, 80, 4 * element_x_spacing + 40, 4 * element_y_spacing, (*texvector)[0], (*texvector)[13], (*soundvector)[0], sf::Text("To save & apply settings,\nclick here", font, 18), "applysettings"));

}


Menu::GameState Menu::run()
{
	Menu::GameState returnvalue = Menu::GameState::MENU;

	sf::Event event;
	while (window->pollEvent(event))
	{

		if (event.type == sf::Event::Closed)
			window->close();

		if (event.type == sf::Event::MouseButtonPressed)
		{
			if (sf::Mouse::isButtonPressed(sf::Mouse::Left) && !sf::Mouse::isButtonPressed(sf::Mouse::Right))
			{
				for (auto it : elementvector)
				{
					if (it->contains(sf::Mouse::getPosition(*window)))
						it->setClicked();		
				}

				if (optionsOn)
				for (auto it : optionsvector)
				{
					if (it->contains(sf::Mouse::getPosition(*window)))
						it->setClicked();
				}

			}
		}
	}
	for (auto it : elementvector)
	{
		if (it->isClicked && !sf::Mouse::isButtonPressed(sf::Mouse::Left))
		{
			it->set_not_Clicked();
			if (it->objecttype == "newgame")
			{
				returnvalue = Menu::GameState::GAME;
				toggleMusics();
			}
			else if (it->objecttype == "options")
			{
				optionsOn = !optionsOn;
				break;
			}
			else if (it->objecttype == "continuegame")
			{
				returnvalue = Menu::GameState::CONTINUE_GAME;
				toggleMusics();
			}
			else if (it->objecttype == "loadgame")
			{
				returnvalue = Menu::GameState::LOAD_LEVEL;
			}
			else if (it->objecttype == "savegame")
				returnvalue = Menu::GameState::SAVE_LEVEL;
			else if (it->objecttype == "editor")
			{
				returnvalue = Menu::GameState::EDITOR;
				toggleMusics();
			}
			else if (it->objecttype == "exitgame")
				window->close();
			if (optionsOn)
				optionsOn = !optionsOn;
		}
	}
	if (optionsOn)
	{
		for (auto it : optionsvector)
		{
			if (it->isClicked && !sf::Mouse::isButtonPressed(sf::Mouse::Left))
			{
				it->set_not_Clicked();
				changeOptions(it->objecttype);
			}
		}
	}
	rendermenu();
	return returnvalue;

}

void Menu::rendermenu()
{
	window->draw(background);

	for (auto it : elementvector)
	{
		it->render(window);
	}

	if (optionsOn)
	{
		for (auto it : optionsvector)
		{
			it->render(window);
		}
	}
}

void Menu::changeOptions(std::string optionstype)
{
	
	if (optionstype == "mvolumeminus" && round((*musicvector)[0]->getVolume()) > 0)
	{
		for (auto it : (*musicvector))
			it->setVolume(round(it->getVolume() - 10));
	}
	else if (optionstype == "mvolumeplus" && round((*musicvector)[0]->getVolume() < 100))
	{
		for (auto it : (*musicvector))
			it->setVolume(round(it->getVolume() + 10));
	}
	else if (optionstype == "svolumeminus" && round((*soundvector)[0]->getVolume()) > 0)
	{
		for (auto it : (*soundvector))
			it->setVolume(round(it->getVolume() - 10));
	}
	else if (optionstype == "svolumeplus" && round((*soundvector)[0]->getVolume()) < 100)
	{
		for (auto it : (*soundvector))
			it->setVolume(round(it->getVolume() + 10));
	}
	else if (optionstype == "resoplus")
	{
		resolutionSelect += 1;
		if (resolutionSelect > int(allowedResolutions.size())-1)
			resolutionSelect = int(allowedResolutions.size())-1;
	}
	else if (optionstype == "resominus")
	{
		resolutionSelect -= 1;
		if (resolutionSelect < 0)
			resolutionSelect = 0;
	}

	else if (optionstype == "applysettings")
	{
		std::ofstream config("config.lol");
		config << allowedResolutions[resolutionSelect].first << std::endl << allowedResolutions[resolutionSelect].second << std::endl << int(round((*musicvector)[0]->getVolume())) <<
			std::endl << (*soundvector)[0]->getVolume() << std::endl << "9.8" << std::endl;

		window->setSize(sf::Vector2u(allowedResolutions[resolutionSelect].first, allowedResolutions[resolutionSelect].second));
		updateElementPositions();
	}

	optionsvector[0]->changeText(sf::Text("Resolution: " + std::to_string(allowedResolutions[resolutionSelect].first) + "x" + std::to_string(allowedResolutions[resolutionSelect].second), font, 18));
	optionsvector[3]->changeText(sf::Text("Music volume: " + std::to_string(int(round((*musicvector)[0]->getVolume()))), font, 18));
	optionsvector[6]->changeText(sf::Text("Sound volume: " + std::to_string(int(round((*soundvector)[0]->getVolume()))), font, 18));
}

void Menu::updateElementPositions()
{
	if (allowedResolutions[resolutionSelect].first <= 800)
		element_x_spacing = 50;
	else
		element_x_spacing = 100;

	if (allowedResolutions[resolutionSelect].second <= 750)
		element_y_spacing = 50;
	else
		element_y_spacing = 100;

	if (!(window->getSize().x == allowedResolutions[resolutionSelect].first && window->getSize().y == allowedResolutions[resolutionSelect].second))
	{
		int a = 1;
		for (auto it : elementvector)
		{
			it->changePosition(element_x_spacing, a*element_y_spacing);
			a++;
		}
		optionsvector[0]->changePosition(4 * element_x_spacing + 100, element_y_spacing);
		optionsvector[1]->changePosition(4 * element_x_spacing + 40, element_y_spacing);
		optionsvector[2]->changePosition(4 * element_x_spacing + 340, element_y_spacing);
		optionsvector[3]->changePosition(4 * element_x_spacing + 100, 2 * element_y_spacing);
		optionsvector[4]->changePosition(4 * element_x_spacing + 40, 2 * element_y_spacing);
		optionsvector[5]->changePosition(4 * element_x_spacing + 340, 2 * element_y_spacing);
		optionsvector[6]->changePosition(4 * element_x_spacing + 100, 3 * element_y_spacing);
		optionsvector[7]->changePosition(4 * element_x_spacing + 40, 3 * element_y_spacing);
		optionsvector[8]->changePosition(4 * element_x_spacing + 340, 3 * element_y_spacing);
		optionsvector[9]->changePosition(4 * element_x_spacing + 40, 4 * element_y_spacing);
	}
}

void Menu::toggleMusics(){

(*musicvector)[0]->pause(); //pausetaan menumusa, jonka jalkeen kaydaan lapi oliko jollain pelilla jo musa pausetettuna

size_t i = 1;

while(i < 7){

	if (i == 6) // jos ei ollut musaa valmiiksi soimassa, arvotaan randomilla uus musa pelille
	{
		srand(unsigned int(time(NULL)));
		int index = rand() % 5 + 1;
		std::cout << index << std::endl;
		(*musicvector)[index]->play();
		break;
	}

	if ((*musicvector)[i]->getStatus() == sf::SoundSource::Paused)//jos oli, jatketaan
	{
		(*musicvector)[i]->play();
		break;
	}
	i++;
	}
}




