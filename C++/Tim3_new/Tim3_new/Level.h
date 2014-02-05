#ifndef tim3_levelh
#define tim3_levelh

#include "objects.h"
#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>
#include <SFML/Window.hpp>
#include <Box2D/Box2D.h>
#include <string>
#include <list>


class Level
{
public:
	//bool editorMode = false;
	sf::Sprite background;
	sf::Texture backgroundtexture;
	sf::Texture buttontexture;
	static sf::Texture firetexture;
	std::list<Baseobject*>* boxes;
	std::vector<sf::Texture*>* texvector;
	std::vector<sf::Sound*> soundvector;
	Level(b2World* world, std::vector<sf::Texture*>* texvector, std::vector<sf::Sound*>& soundvector);// , b2Vec2 goalpos = b2Vec2(0.0, 0.0), b2Vec2 goalsize = b2Vec2(0.0, 0.0), float angle = 0);
	~Level();
	void render(sf::RenderWindow* window, const bool running);
	void update(bool running, b2World* world, sf::RenderWindow* window);

	struct InventoryItem {
		std::string name;
		size_t count = 0;
	};

	int activeInventoryItem = -1;
	std::vector<InventoryItem> getInventory() { return inventory;  }
	InventoryItem getInventoryItem(size_t itemI);
	void setInventoryItemCount(size_t itemI, size_t i);
	void incInventoryItemCount(size_t itemI, int i = 1);
	void addInventoryItem(InventoryItem);

	std::string save;
	void saveObjects(b2World* world);
	void loadLevel(b2World* world, std::vector<sf::Sound*>& sounds, std::string levelname = "");
	std::string currentlevel;
	float sizex = 0;
	float sizey = 0;
	float posx = 0;
	float posy = 0;
	float ang = 0;
	bool flipped;
	bool showeditor = true;

	sf::RectangleShape editorblock;

	void showEditorblock();
	void setEditorblock(float sizex, float sizey, float posx, float posy, float ang, bool flipped);
	void deleteObj(Baseobject* o);

	bool levelcleared = false;
	int pointnumber = 0;
	int requiredPoints = 0;
	//b2Vec2 GoalAreaPos;
	//b2Vec2 GoalAreaSize;
	//float GoalAngle;

private:
	std::vector<InventoryItem> inventory;
	std::string openFileDialog(std::string title);
	void render_brick(sf::RenderWindow* window);
};

#endif