#ifndef tim3_menu
#define tim3_menu

#include <iostream>
#include <string>
#include <vector>
#include <utility>

#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>
#include <SFML/Window.hpp>
#include <SFML/System.hpp>

#include "Guielement.h"
//#include "Gamelogic.h"

class Menu
{
public:

	enum GameState {
		MENU,
		GAME,
		EDITOR,
		LOAD_LEVEL,
		SAVE_LEVEL,
		CONTINUE_GAME
	};

	Menu(sf::RenderWindow* window, std::vector<sf::Texture*>* texvector, std::vector<sf::Sound*>* soundvector, std::vector<sf::Music*>* musicvector);

	GameState run();
	void rendermenu();

	bool running;
	bool optionsOn;
	void changeOptions(std::string optionstype);
	void updateElementPositions();
	void toggleMusics();


private:
	
	sf::RenderWindow* window;
	std::vector<sf::Texture*>* texvector;
	std::vector<sf::Sound*>* soundvector;
	std::vector<sf::Music*>* musicvector;
	std::vector<Guielement*> elementvector;
	std::vector<Guielement*> optionsvector;

	sf::Sprite background;
	sf::Texture backgroundtexture;

	sf::Font font;	
	int element_y_spacing = 100;
	int element_x_spacing = 100;
	std::vector<std::pair<int, int>> allowedResolutions;
	int resolutionSelect;


};

#endif