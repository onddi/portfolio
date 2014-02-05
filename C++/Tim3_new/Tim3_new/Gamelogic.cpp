
#include "Gamelogic.h"
#include "Menu.h"
#include "Level.h"
#include "objects.h"
#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>
#include <SFML/window.hpp>
#include <Box2D/Box2D.h>
#include <iostream>
#include <sstream>
#include <list>
#include "QueryCallback.h"

Menu::GameState Gamelogic::run()
{
	if (running)
	{
		level->update(running, world, window);
	}
	camx += camxvel * 0.1f * zoom;
	camxvel *= 0.8f;

	camy += camyvel * 0.1f * zoom;
	camyvel *= 0.8f;

	zoom += camzvel;
	camzvel *= 0.8f;

	if (zoom < 10.0f)
	{
		zoom = 10.0f;
		camzvel = 0.0f;
	}
	if (zoom > 400.0f)
	{
		zoom = 400.0f;
		camzvel = 0.0f;
	}

	sf::Vector2f size = sf::Vector2f(float(window->getSize().x), float(window->getSize().y));

	view1.setCenter(sf::Vector2f(camx, camy));
	view1.setSize(sf::Vector2f(zoom * size.x / size.y, zoom));
	window->setView(view1);

	level->background.setOrigin(size.x * 0.5f, size.y * 0.5f);
	level->background.setPosition(camx, camy);
	level->background.setScale(zoom / size.y, zoom / size.y);

	float X = (float(sf::Mouse::getPosition(*window).x) / size.x - 0.5f) * size.x / size.y * zoom + camx;
	float Y = (float(sf::Mouse::getPosition(*window).y) / size.y - 0.5f) * zoom + camy;

	sf::Event event;
	while (window->pollEvent(event))
	{
		if (event.type == sf::Event::Closed)
			window->close();
		if (event.type == sf::Event::KeyPressed)
		{
			size_t mouseX = sf::Mouse::getPosition(*window).x;
			size_t mouseY = sf::Mouse::getPosition(*window).y;

			if (sf::Keyboard::isKeyPressed(sf::Keyboard::Escape))
			{
				return Menu::GameState::MENU;
			}
			if (sf::Keyboard::isKeyPressed(sf::Keyboard::Tab))
			{
				return Menu::GameState::GAME;
				//std::cout << level->currentlevel << std::endl;
			}
			// Only for editor mode
			if (editorModeOn) {
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::Space))
				{
					running = !running;
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::LControl))
				{
					level->boxes->push_back(new Box(12, world, b2Vec2(sizex / 2.0f, sizey / 2.0f), b2Vec2(X, Y), 0, 3.1415f + ang, -44.0f, (soundvector)[1], (soundvector)[2], false));
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::LShift))
				{
					level->boxes->push_back(new Box(1, 4, world, b2Vec2(sizex / 2.0f, sizey / 2.0f), b2Vec2(X, Y), ang, (soundvector)[1]));
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::A))
				{
					level->boxes->push_back(new Box(11, world, b2Vec2(4.0f, 1.0f), b2Vec2(X, Y), 20.0f, ang, 25.0f, (soundvector)[1], (soundvector)[2], true));
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::S))
				{
					level->boxes->push_back(new Box(1, world, b2Vec2(sizex / 2.0f, sizey / 2.0f), b2Vec2(X, Y), 20.0f, ang, (soundvector)[1]));
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::L))
				{
					level->boxes->push_back(new Laser(1, world, b2Vec2(sizex / 2.0f, sizey / 2.0f), b2Vec2(X, Y), 0.0f, ang, (soundvector)[0], (soundvector)[0]));
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::D))
				{
					level->boxes->push_back(new Box(11, world, b2Vec2(4.0f, 1.0f), b2Vec2(X, Y), 20.0f, ang, -25.0f, (soundvector)[1], (soundvector)[2], true));
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::F))
				{
					level->boxes->push_back(new Box(1, world, b2Vec2(sizex / 2.0f, sizey / 2.0f), b2Vec2(X, Y), 0.0f, ang, (soundvector)[1]));
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::W))
				{
					level->boxes->push_back(new Ball(2, world, 2.0f, b2Vec2(X, Y), (soundvector)[1]));
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::Q))
				{
					level->boxes->push_back(new BouncingBox(26, world, b2Vec2(sizex / 2.0f, sizey / 2.0f), b2Vec2(X, Y), 0.0f, ang, 1.0f, (soundvector)[1]));
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::E))
				{
					level->boxes->push_back(new BouncingBox(26, world, b2Vec2(sizex / 2.0f, sizey / 2.0f), b2Vec2(X, Y), 1.0f, ang, 1.0f, (soundvector)[1]));
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::T)){
					level->boxes->push_back(new Rocket(3, 4, world, b2Vec2(4.0f, 2.0f), b2Vec2(X, Y), 600.0f, 1.0f, ang , (soundvector)[3], (soundvector)[2]));
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::Y)){
					level->boxes->push_back(new Rocket(3, 4, world, b2Vec2(2.0f, 1.0f), b2Vec2(X, Y), 1600.0f, 1.0f, ang , (soundvector)[3], (soundvector)[2]));
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::H))
				{
					showEditorHelp = !showEditorHelp;
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::I))
				{
					showInventoryList = !showInventoryList;
				}
				if (sf::Keyboard::isKeyPressed(sf::Keyboard::BackSpace) && mouseleftheld)
				{
					if (dragbody){
						Baseobject* obj = static_cast<Baseobject*>(dragbody->GetUserData());
						level->deleteObj(obj);
					}
				}
			}

			if (sf::Keyboard::isKeyPressed(sf::Keyboard::X))
			{
				flipped = !flipped;
			}
			if (sf::Keyboard::isKeyPressed(sf::Keyboard::Subtract))
			{
				if (editorModeOn == false || showInventoryList == false || !guiInventory->click(*window, mouseX, mouseY, GuiInventory::CLICKTYPE::CLICKTYPE_SUBSTRACT))
				{
					ang -= 0.06f;
					std::cout << "Ang: " << ang << std::endl;
				}
			}
			if (sf::Keyboard::isKeyPressed(sf::Keyboard::Add))
			{
				if (editorModeOn == false || showInventoryList == false || !guiInventory->click(*window, mouseX, mouseY, GuiInventory::CLICKTYPE::CLICKTYPE_ADD))
				{
					ang += 0.06f;
					std::cout << "Ang: " << ang << std::endl;
				}
			}
			if (sf::Keyboard::isKeyPressed(sf::Keyboard::Right))
			{
				sizex /= 0.5f;
				std::cout << "Sizex: " << sizex << std::endl;

			}
			if (sf::Keyboard::isKeyPressed(sf::Keyboard::Left))
			{
				sizex *= 0.5f;
				std::cout << "Sizex: " << sizex << std::endl;
			}
			if (sf::Keyboard::isKeyPressed(sf::Keyboard::Up))
			{
				sizey += 0.5f;
				std::cout << "Sizey: " << sizey << std::endl;

			}
			if (sf::Keyboard::isKeyPressed(sf::Keyboard::Down))
			{
				sizey -= 0.5f;
				std::cout << "Sizey: " << sizey << std::endl;
			}
			if (sf::Keyboard::isKeyPressed(sf::Keyboard::Return) && mouseleftheld)
			{

				if (dragbody && !running){
					Baseobject* obj = static_cast<Baseobject*>(dragbody->GetUserData());
					float a, b, c, d;

					a = obj->getPos().x;
					b = obj->getPos().y;
					c = sizex;
					d = sizey;
					std::cout << "x: " << a << " y: " << b << std::endl;
					//std::cin >> a >> b;
					std::cout << obj->getGoalAreaPos().x << std::endl;

					obj->setGoalArea(b2Vec2(a, b), b2Vec2(c, d), ang);
					//obj->goalHitNum = 0;
					std::cout << obj->getName() << " goalareasizex   " << obj->getGoalAreaSize().x << std::endl;

				}
			}
		}

		if (event.type == sf::Event::MouseMoved && mouserightheld)
		{
			camxvel = -(sf::Mouse::getPosition().x - lastmousex) / 100.0f;
			camyvel = -(sf::Mouse::getPosition().y - lastmousey) / 100.0f;
		}

		if (event.type == sf::Event::MouseWheelMoved)
		{
			if (mouseleftheld)
				ang += -event.mouseWheel.delta / 20.0f;
			else
				camzvel = -event.mouseWheel.delta * zoom / 20.0f;
		}
		if (event.type == sf::Event::MouseButtonPressed)
		{
			size_t mouseX = sf::Mouse::getPosition(*window).x;
			size_t mouseY = sf::Mouse::getPosition(*window).y;
			float X = (float(mouseX) / size.x - 0.5f) * size.x / size.y * zoom + camx;
			float Y = (float(mouseY) / size.y - 0.5f) * zoom + camy;


			if (sf::Mouse::isButtonPressed(sf::Mouse::Left))
			{

				if (editorModeOn){
					//Collision check for drag&drop
					b2Vec2 pos = b2Vec2(X, Y);
					// Make a small box.
					b2AABB aabb;
					b2Vec2 d;
					d.Set(1.0f, 1.0f);// 0.001f, 0.001f);
					aabb.lowerBound = pos - d;
					aabb.upperBound = pos + d;

					// Query the world for overlapping shapes.
					QueryCallback callback(pos);
					world->QueryAABB(&callback, aabb);

					dragbody = callback.m_object; //If a body is found ==> dragbody != 0

						if (dragbody != 0)
						{
							ang = dragbody->GetAngle();
							Baseobject* obj = static_cast<Baseobject*>(dragbody->GetUserData());
							sizex = obj->getSize().x * 2.0f;
							sizey = obj->getSize().y * 2.0f;
							dragbody->SetLinearVelocity(b2Vec2(0.0, 0.0));
							dragbody->SetAngularVelocity(0);
						}
				}
				bool eventHappened = false;
				if (!eventHappened && editorModeOn == true && showInventoryList == true) {
					eventHappened = guiInventory->click(*window, mouseX, mouseY, GuiInventory::CLICKTYPE::CLICKTYPE_ADD);
				}
				if (!eventHappened && editorModeOn == false)
				{
					eventHappened = guiInventory->click(*window, mouseX, mouseY);
				}
				if (!eventHappened)
				{
					if (level->activeInventoryItem >= 0)
					{
						auto item = level->getInventoryItem(level->activeInventoryItem);
						if (item.count > 0)// || level->editorMode == true)
						{
							//if (level->editorMode == false) {
							level->incInventoryItemCount(level->activeInventoryItem, -1);
							//}
							std::cout << item.count << std::endl;
							if (item.name == "Ball") {
								std::cout << "Balls!" << std::endl;
								level->boxes->push_back(new Ball(2, world, 2.0f, b2Vec2(X, Y), (soundvector)[1]));
							}
							else if (item.name == "Rocket") {
								std::cout << "Rockets!" << std::endl;
								level->boxes->push_back(new Rocket(3, 4, world, b2Vec2(4.0f, 2.0f), b2Vec2(X, Y), 600.0f, 1.0f, ang, (soundvector)[3], (soundvector)[2]));
							}
							else if (item.name == "Brick") {
								std::cout << "Rockets!" << std::endl;
								level->boxes->push_back(new BouncingBox(1, world, b2Vec2(sizex / 2.0f, sizey / 2.0f), b2Vec2(X, Y), 0.0f, ang, 0.3f, (soundvector)[1]));
							}
							else if (item.name == "Tank") {
								std::cout << "Tank!" << std::endl;
								level->boxes->push_back(new Box(11, world, b2Vec2(4.0f, 1.0f), b2Vec2(X, Y), 20.0f, ang, (flipped ? -20.0f : 20.0f), (soundvector)[1], (soundvector)[2], true));
							}
							else if (item.name == "Convey") {
								level->boxes->push_back(new Box(12, world, b2Vec2(sizex / 2.0f, sizey / 2.0f), b2Vec2(X, Y), 0, 3.1415f + ang, -44.0f, (soundvector)[1], (soundvector)[2], false));
							}
							else if (item.name == "Laser") {
								level->boxes->push_back(new Laser(1, world, b2Vec2(sizex / 2.0f, sizey / 2.0f), b2Vec2(X, Y), 0.0f, ang, (soundvector)[0], (soundvector)[0]));
							}
							else
							{
								std::cout << "ERROR!" << std::endl;
								std::cout << "Item " << item.name << " not found!" << std::endl;
							}
						}
						else
						{
							std::cout << "No more items left!" << std::endl;
						}
					}
				}
				if (!eventHappened) {
					mouseleftheld = true;
				}
			}
			if (sf::Mouse::isButtonPressed(sf::Mouse::Right))
			{
				bool eventHappened = false;
				if (!eventHappened && editorModeOn == true && showInventoryList == true) {
					eventHappened = guiInventory->click(*window, mouseX, mouseY, GuiInventory::CLICKTYPE::CLICKTYPE_SUBSTRACT);
				}
				if (!eventHappened) {
					mouserightheld = true;
				}
			}
		}
		if (event.type == sf::Event::MouseButtonReleased)
		{
			if (event.mouseButton.button == sf::Mouse::Right){
				mouserightheld = false;

				//Collision check for drag&drop
				b2Vec2 pos = b2Vec2(X, Y);
				// Make a small box.
				b2AABB aabb;
				b2Vec2 d;
				d.Set(0.01f, 0.01f);// 0.001f, 0.001f);
				aabb.lowerBound = pos - d;
				aabb.upperBound = pos + d;

				// Query the world for overlapping shapes.
				QueryCallback callback(pos);
				world->QueryAABB(&callback, aabb);

				dragbody = callback.m_object; //If a body is found ==> dragbody != 0

				if (dragbody != 0){
					
					Baseobject* obj = static_cast<Baseobject*>(dragbody->GetUserData());
					level->deleteObj(obj);
				}
			}

			if (event.mouseButton.button == sf::Mouse::Left){
				mouseleftheld = false;
			}
		}
	}

	if (mouseleftheld){  //If a body is pressed and left mousedragged
		if (dragbody)
		{
			dragbody->SetTransform(b2Vec2(X, Y), ang);

			if (dragbody->GetType() == 2)
				dragbody->SetAwake(true);
		}
	}

	level->render(window, running);

	if (level->levelcleared)
	{
		// Declare and load a font
		std::stringstream type;
		type << level->pointnumber;
		std::string pnum = type.str();
		sf::String counter = pnum;
		sf::Text text("Level cleared!", *font);
		text.setCharacterSize(30);
		text.setStyle(sf::Text::Bold);
		text.setColor(sf::Color::Red);
		//center text
		sf::FloatRect textRect = text.getLocalBounds();
		text.setOrigin(textRect.left + textRect.width / 2.0f,
			textRect.top + textRect.height / 2.0f);
		text.setPosition(sf::Vector2f(window->getView().getCenter()));
		window->draw(text);
	}

	if (editorModeOn && showEditorHelp)
	{
		editorhelp.setScale(0.1f, 0.1f);
		editorhelp.setPosition(X + 10, Y + 10);
		window->draw(editorhelp);
	}		

	//Block/box building helper 
	level->setEditorblock(sizex, sizey, X, Y, ang*(180.0f / 3.141592654f), flipped);

	lastmousex = sf::Mouse::getPosition().x;
	lastmousey = sf::Mouse::getPosition().y;

	view1.reset(sf::FloatRect(0.0f, 0.0f, size.x, size.y)); //vaihdetaan kamera takaisin näyttökoordinaatistoon guin piirtoa varten
	window->setView(view1);

	if (showInventoryList == true)
		guiInventory->render(*window);
	return Menu::GameState::CONTINUE_GAME;
}

Gamelogic::Gamelogic(sf::RenderWindow* win, std::vector<sf::Texture*>* tex, std::vector<sf::Sound*>& sounds, sf::Font* font_, bool editorMode)
{
	texvector = tex;
	soundvector = sounds;
	running = true;
	window = win;
	font = font_;
	helptexture = *(*texvector)[25];
	editorhelp.setTexture(helptexture, true);
	world = new b2World(b2Vec2(0.0f, 9.8f));
	collist = new CollisionListener();
	world->SetContactListener(collist);
	level = new Level(world, tex, sounds);
	if (!editorMode)
	guiInventory = new GuiInventory(level, tex, font_);

	//It's an example, you have to keep the sf::Texture and sf::Sprite as members of your class
	

	//Then, in PlayState::render()
}

void Gamelogic::reset()
{
	delete level;
	level = new Level(world, texvector, soundvector);
}

Gamelogic::~Gamelogic()
{
	delete level;
	delete world;
	delete guiInventory;
	delete collist;
}