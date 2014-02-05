#include <Windows.h>

#include "Level.h"
#include "Gamelogic.h"
#include "objects.h"
#include "utils.h"
#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>
#include <SFML/window.hpp>
#include <Box2D/Box2D.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <list>
#include <Windows.h>


void Level::update(bool running, b2World* world, sf::RenderWindow* window)
{
	
	if (running)
		world->Step(1.0f / 60, 8, 3);
	for (std::list<Baseobject*>::iterator it = boxes->begin(); it != boxes->end(); it++)
	{
		if ((*it)->timetodie && (*it)->explodes)
		{
			soundvector[3]->play();
			(*it)->explode(boxes, world);
		}
		if ((*it)->conveyorstrain == 0)
			(*it)->conveyorstrain = 1;
		if ((*it)->conveyorstr != 0 && (*it)->isconveyor)
		{
			if ((*it)->conveyorspeed < 0)
			{
				(*it)->conveyorfriction = -(*it)->conveyorfriction;
				(*it)->conveyorstrain = -(*it)->conveyorstrain;
			}
			(*it)->conveyorspeed += ((*it)->conveyorstr + (*it)->conveyorstrain + (*it)->conveyorfriction)*(0.02f);
		}
		if ((*it)->conveyorspeed>20.0f)
			(*it)->conveyorspeed = 20.0f;
		if ((*it)->conveyorspeed<-20.0f)
			(*it)->conveyorspeed = -20.0f;
		//if (abs((*it)->conveyorspeed)>0)
			//std::cout << (*it)->conveyorspeed << std::endl;
		(*it)->conveyorfriction = -abs(1.5f*(*it)->conveyorspeed);
		(*it)->conveyorstrain = 0;

		if ((*it)->isGoalAreaHit()){
			sf::Sound* goalhit = soundvector[4];

			std::cout << "GoalHitAmount " << (*it)->goalHitAmount() << std::endl;
			pointnumber++;
			std::cout << "Points so far :" << pointnumber << std::endl;
			std::cout << "Required : " << requiredPoints << std::endl;
			if (pointnumber >= requiredPoints){
				levelcleared = true;
				goalhit = soundvector[5];
			}

			goalhit->setVolume(100);
			goalhit->play();
		}
	}
	for (auto it : *boxes)
	{
		it->update(boxes, window, texvector);
	}
	boxes->remove_if([](const Baseobject* b)->bool{if (b->timetodie) { delete b; return true; }
	else return false; });
}

enum EDIT_MODES {
	EDIT_MODES_BRICK,
	EDIT_MODES_IMAGE
};

void Level::render_brick(sf::RenderWindow* window) {
	editorblock.setFillColor(sf::Color(100, 250, 50));
	editorblock.setOrigin(sf::Vector2f(sizex / 2, sizey / 2));
	editorblock.setSize(sf::Vector2f(sizex, sizey));
	editorblock.setPosition(sf::Vector2f(posx, posy));
	editorblock.setRotation(ang);
	window->draw(editorblock);
}



void Level::render(sf::RenderWindow* window, const bool running)
{
	window->draw(background);

	if (activeInventoryItem >= 0)
	{
		auto item = getInventoryItem(activeInventoryItem);
		if (item.count > 0)// || editorMode == true)
		{
			if (item.name == "Ball") {
				float scale = 4 / float((*(*texvector)[2]).getSize().x);
				
				render_image(*window, *(*texvector)[2], int(posx), int(posy), scale, 0, true);
				//gamelogic->level->boxes->push_back(new Ball(2, gamelogic->world, 2, b2Vec2(X, Y), (*soundvector)[1]));
			}
			else if (item.name == "Rocket") {
				float scale = 4 / float((*(*texvector)[3]).getSize().x);
				render_image(*window, *(*texvector)[3], int(posx), int(posy), scale, ang, true);
				//gamelogic->level->boxes->push_back(new Rocket(3, 4, gamelogic->world, b2Vec2(4, 2), b2Vec2(X, Y), 600, 1, ang + b2_pi / 2, (*soundvector)[3], (*soundvector)[2]));
			}
			else if (item.name == "Brick") {
				Level::render_brick(window);
			}
			else if (item.name == "Tank") {
				float scale = 12 / float((*(*texvector)[22]).getSize().x);
				render_image(*window, *(*texvector)[22], int(posx), int(posy), scale, ang, true, flipped);
				//gamelogic->level->boxes->push_back(new Box(11, gamelogic->world, b2Vec2(4, 1), b2Vec2(X, Y), 20, ang, 88, (*soundvector)[1], (*soundvector)[2]));
			}
			else if (item.name == "Convey") {
				Level::render_brick(window);
			}
			else if (item.name == "Laser") {
				Level::render_brick(window);
				//gamelogic->level->boxes->push_back(new Box(11, gamelogic->world, b2Vec2(4, 1), b2Vec2(X, Y), 20, ang, 88, (*soundvector)[1], (*soundvector)[2]));
			}
			else
			{
				std::cout << "ERROR!" << std::endl;
				std::cout << "Item " << item.name << " not found!" << std::endl;
			}
		}
	}

	if (showeditor)
	{
		editorblock.setFillColor(sf::Color(100, 250, 50, 100));
		editorblock.setOrigin(sf::Vector2f(sizex / 2, sizey / 2));
		editorblock.setSize(sf::Vector2f(sizex, sizey));
		editorblock.setPosition(sf::Vector2f(posx, posy));
		editorblock.setRotation(ang);
		window->draw(editorblock);
	}
	editorblock.setFillColor(sf::Color(0, 0, 0));

	for (auto it : *boxes)
	{
		window->draw(it->GoalAreaRect);

		if (it->isdrawn)
			it->render(window, texvector, running);
		it->update(boxes, window, texvector);
	}

}

Level::Level(b2World* world, std::vector<sf::Texture*>* tex, std::vector<sf::Sound*>& sounds)
{
	
	texvector = tex;
	soundvector = sounds;

	background.setTexture(*(*texvector)[15], true);

	boxes = new std::list<Baseobject*>();
	boxes->push_back(new Box(1, world, b2Vec2(100, 1), b2Vec2(50, 60), 0, 0, sounds[1]));
	//boxes->push_back(new BouncingBox(1, world, b2Vec2(100, 1), b2Vec2(50, 60), 0, 0, 0.5, sounds[1]));
	//new BouncingBox(1, gamelogic->world, b2Vec2(sizex / 2, sizey / 2), b2Vec2(X, Y), 0, ang, 0.3, (*soundvector)[1]));

	Level::InventoryItem item;
	item.name = "Ball";
	item.count = 0;
	addInventoryItem(item);

	item.name = "Rocket";
	item.count = 0;
	addInventoryItem(item);

	item.name = "Brick";
	item.count = 0;
	addInventoryItem(item);

	item.name = "Tank";
	item.count = 0;
	addInventoryItem(item);

	item.name = "Convey";
	item.count = 0;
	addInventoryItem(item);

	item.name = "Laser";
	item.count = 0;
	addInventoryItem(item);

}

Level::~Level()
{
	for (auto it : *boxes)
	{
		delete it;
	}
	delete boxes;
}

Level::InventoryItem Level::getInventoryItem(size_t itemI) {
	return inventory[itemI];
}

void Level::setInventoryItemCount(size_t itemI, size_t i) {
	inventory[itemI].count = i;
}

void Level::incInventoryItemCount(size_t itemI, int i) {
	int val = int(getInventoryItem(itemI).count) + i;
	if (val < 0) {
		val = 0;
	}
	setInventoryItemCount(itemI, val);
}

void Level::addInventoryItem(InventoryItem item) {
	inventory.push_back(item);
}

void Level::deleteObj(Baseobject* o){

	for (std::list<Baseobject*>::iterator itr = boxes->begin(); itr != boxes->end();)
	{
		if (*itr == o){
			itr = boxes->erase(itr);
			delete o; 
			break;
		}
		else
			++itr;
	}

}

void Level::showEditorblock(){
	showeditor = !showeditor;
}

void Level::setEditorblock(float sizex_, float sizey_, float posx_, float posy_, float angle, bool flipped_){
	sizex = sizex_;
	sizey = sizey_;
	posx = posx_;
	posy = posy_;
	ang = angle;
	flipped = flipped_;
}

void Level::saveObjects(b2World* world){

	std::string name = openFileDialog("savefile") + ".txt";
	std::cout << name << " has been created \n";

	std::ofstream outFile(name);

	for (auto &item : inventory)
	{
		outFile << "Inventory: ";
		outFile << item.name << " ";
		outFile << item.count << " ";
		outFile << std::endl;
	}

	for (std::list<Baseobject*>::iterator it = boxes->begin(); it != boxes->end(); it++){

		for (auto it : *boxes)
		{
			if (it->getName() != "P"){
				outFile << it->getName() << ' ';
				outFile << it->getPos().x << ' ';
				outFile << it->getPos().y << ' ';
				outFile << it->getAngle() << ' ';
				outFile << it->getSize().x << ' ';
				outFile << it->getSize().y << ' ';
				outFile << it->getDensity() << ' ';
				if (it->conveyorstr != 0)
					outFile << it->conveyorstr << ' ';
				if (it->thrust != 0)
					outFile << it->thrust << ' ';
				if (it->GoalAreasize.x > 0 && it->GoalAreasize.y > 0){
					outFile << it->GoalAreapos.x << ' ';
					outFile << it->GoalAreapos.y << ' ';
					outFile << it->GoalAreasize.x << ' ';
					outFile << it->GoalAreasize.y << ' ';
					outFile << it->GoalAngle << ' ';
				}

				outFile << std::endl;
			}
		}

	
		outFile.close();
	}

}


void Level::loadLevel(b2World* world, std::vector<sf::Sound*>& sounds, std::string levelname){

	std::string name;

	if (levelname.length() > 0){
		std::string lname = levelname;
		std::stringstream sstm;
		sstm << lname << ".txt";
		name = sstm.str();
	}
	else
		name = openFileDialog("openfile");

	std::cout << "opening file: " << name << std::endl;

	std::ifstream inFile(name);
	std::string line;
	std::string word;


	if (inFile.is_open())
	{
		inventory.clear();
		while (std::getline(inFile, line))
		{
			//std::cout << line << std::endl;

			std::istringstream iss(line);

			std::string result;
			float x, y, angle, sizex, sizey, density;
			b2Vec2 GoalAreaPos = b2Vec2(0.0,0.0);
			b2Vec2 GoalAreaSize = b2Vec2(0.0,0.0);
			float GoalAngle;

			if (std::getline(iss, result, ':'))
			{
				if (result == "Ball")
				{
					std::string token;

					while (std::getline(iss, token, ' '))
					{
						iss >> x >> y >> angle >> sizex >> sizey >> density >> GoalAreaPos.x >> GoalAreaPos.y >> GoalAreaSize.x >> GoalAreaSize.y >> GoalAngle;
					}
					GoalAngle*=(180.0f / 3.141592654f);
					boxes->push_back(new Ball(2, world, 2, b2Vec2(x, y), sounds[1], GoalAreaPos, GoalAreaSize, GoalAngle));
				}

				if (result == "Tankki")
				{
					std::string token;
					float convspeed;

					while (std::getline(iss, token, ' '))
					{
						iss >> x >> y >> angle >> sizex >> sizey >> density >> convspeed >> GoalAreaPos.x >> GoalAreaPos.y >> GoalAreaSize.x >> GoalAreaSize.y >> GoalAngle;
					}
					GoalAngle /= (180.0f / 3.141592654f);
					if (density > 0)
					boxes->push_back(new Box(11, world, b2Vec2(sizex, sizey), b2Vec2(x, y), density, angle, convspeed, sounds[1], sounds[2], true, GoalAreaPos, GoalAreaSize, GoalAngle));
					else
						boxes->push_back(new Box(12, world, b2Vec2(sizex, sizey), b2Vec2(x, y), density, angle, convspeed, sounds[1], sounds[2], false, GoalAreaPos, GoalAreaSize, GoalAngle));
				}

				if (result == "Box1")
				{
					std::string token;
					float x, y, angle;
					while (std::getline(iss, token, ' '))
					{
						iss >> x >> y >> angle >> sizex >> sizey >> density;
					}

					boxes->push_back(new Box(1, 4, world, b2Vec2(sizex, sizey), b2Vec2(x, y), angle, sounds[1]));
				}

				if (result == "Box2")
				{
					std::string token;
					float x, y, angle;
					while (std::getline(iss, token, ' '))
					{
						iss >> x >> y >> angle >> sizex >> sizey >> density;
					}

					boxes->push_back(new Box(1, world, b2Vec2(sizex, sizey), b2Vec2(x, y), density, angle, sounds[1]));
				}
				if (result == "BouncingBox")
				{
					std::string token;
					float x, y, angle;
					while (std::getline(iss, token, ' '))
					{
						iss >> x >> y >> angle >> sizex >> sizey >> density >> GoalAreaPos.x >> GoalAreaPos.y >> GoalAreaSize.x >> GoalAreaSize.y >> GoalAngle;;
					}
					GoalAngle /= (180.0f / 3.141592654f);
					boxes->push_back(new BouncingBox(26, world, b2Vec2(sizex, sizey), b2Vec2(x, y), density, angle, 1, sounds[1], GoalAreaPos, GoalAreaSize, GoalAngle));
				}
				if (result == "Rocket")
				{
					std::string token;
					float thrust;

					while (std::getline(iss, token, ' '))
					{
						iss >> x >> y >> angle >> sizex >> sizey >> density >> thrust;
					}
					boxes->push_back(new Rocket(3, 4, world, b2Vec2(sizex, sizey), b2Vec2(x, y), thrust, 1, angle, sounds[3], sounds[2]));
				}
				if (result == "Laser")
				{
					std::string token;

					while (std::getline(iss, token, ' '))
					{
						iss >> x >> y >> angle >> sizex >> sizey >> density;
					}
					boxes->push_back(new Laser(1, world, b2Vec2(sizex / 2.0f, sizey / 2.0f), b2Vec2(x, y), density, angle, (soundvector)[0], (soundvector)[0]));
				}
				if (result == "Inventory")
				{
					std::string token;
					Level::InventoryItem item;

					while (std::getline(iss, token, ' '))
					{
						iss >> item.name >> item.count;
					}
					addInventoryItem(item);
				}

				if (GoalAreaSize.x > 0 && GoalAreaSize.y > 0)
					requiredPoints++;
				
			}
			

		}
		inFile.close();
		std::cout << "Required points: " << requiredPoints << std::endl;
		
		char drive[_MAX_DRIVE];
		char dir[_MAX_DIR];
		char fname[_MAX_FNAME];
		char ext[_MAX_EXT];
		_splitpath_s(name.c_str(), drive, dir, fname, ext);
		//std::cout << fname << std::endl;
		currentlevel = fname;
		std::cout << currentlevel << std::endl;
	}
}

std::string Level::openFileDialog(std::string title)
{
	const int BUFSIZE = 1024;
	char buffer[BUFSIZE] = { 0 };
	OPENFILENAME ofns = { 0 };
	ofns.lStructSize = sizeof(ofns);
	ofns.lpstrFile = buffer;
	//ofns.lpstrInitialDir = "levels";
	ofns.lpstrFilter = "Text\0*.TXT\0";
	ofns.nMaxFile = BUFSIZE;
	if (title == "openfile")
	{
		ofns.lpstrTitle = "Open file";
		GetOpenFileName(&ofns);
	}
	else
	{
		ofns.lpstrTitle = "Save file";
		GetSaveFileName(&ofns);
	}

	return buffer;
}