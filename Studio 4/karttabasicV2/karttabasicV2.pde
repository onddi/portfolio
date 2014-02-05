// The next line is needed if running in JavaScript Mode with Processing.js
//@pjs preload="https://upload.wikimedia.org/wikipedia/commons/0/03/BlankMap-World6.svg";

import geomerative.*;
import de.bezier.data.*;




RShape worldblueprint; //Wolrdblueprint is a .SVG map that has every country as a child - only childs are visual if highlighted
Worldmap worldmap; //Worldmap is an object for a map which is drawn to the screen
RPoint cursor; //The cursor needs a shape for collisions
CountryManager countryManager; //Country manager is a multifunctional object for holding, dislaying selected Country objects and drawing them to the graph
flagBox flagbox; //A simple box to display countries flags
XlsReader reader; //A .XLS reader is needed for data input

PImage worldmapimg; //Worldmap object needs a .PNG file to draw the map
PImage flag; //A variable to hold country flags

String currentCountryId,currentCountryName; //Id is used to find the corresponding country's name from .XLS file

int currentCountryPop; //Variables for holding data from the excel
int currentCountryGDP;
int countryNumber;
int currentScreen = 0; //The screen-variable is used to switch between different screens
int timer = -3000; //Timer used for fading effect

float alpha = 0; //Fading effect variable
float origoX,origoY; 
float scale = 1; 

boolean showBox = false;
boolean dragMap = false;
boolean helpScreen = false;
boolean programStart = true;

void setup() {
  size(displayWidth-200, displayHeight-200);
  origoX = width/2;
  origoY = height/2;
  smooth();
  RG.init(this);
  RG.ignoreStyles(true);
  worldmapimg = loadImage("data/worldmap.png");
  worldmapimg.resize(width,height);
  
  worldblueprint = RG.loadShape("data/BlankMap-World6.svg");
  worldblueprint.centerIn(g); 
  countryNumber = worldblueprint.countChildren();
  currentCountryId = "ocean";
  currentCountryName = "ocean";
  
  worldmap = new Worldmap(0,0,1);
  reader = new XlsReader( this, "Country List.xls" ); 
  cursor = new RPoint((mouseX-origoX)/scale, (mouseY-origoY)/scale); 
  countryManager = new CountryManager(0,0,1);

}

void draw() {
  
  //Control the volume of the fade effect
  if(alpha > 4)
    alpha = 4;
  if(alpha < 0)
    alpha = 0;
  
  //Switching between different screens
  switch(currentScreen){
    case 0: drawScreenOne();break;
    case 1: drawScreenTwo();break;
    case 2: drawScreenHelp();break;
    default: drawScreenOne();break;
  }
  
}

//DRAW THE WORLDMAP SCREEN-----------------------------------------------------
void drawScreenOne(){
  helpScreen = false;
  background(0);
  stroke(0,50);
  noFill();
  pushMatrix();
  //ORIGIN is translated from the upper-left to the center of the screen
  //This is done because the .SVG file is drawn from the center.
  //The origo -values change when the map is dragged so the translate moves 
  //the picture accordingly.
  translate(origoX,origoY);
  scale(scale,scale);

  //A draw function for the Worldmap object
  worldmap.display();
  //The translate and scale has to be added to the cursor as well
  cursor.x = (mouseX-origoX)/scale;
  cursor.y = (mouseY-origoY)/scale;
  
  //Identify and hightlight the current country where the cursor is
  highlightCountry();
  //Add a small cirle to the cursor point
  noFill();
  stroke(0,50);
  ellipse((mouseX-origoX)/scale, (mouseY-origoY)/scale,10,10);
  fill(255);
  
  //The currentCountry is used to get the flag .png file
  flag = loadImage(getFlag(), "png");
  
  //The flag is displayed if the showbox is true 
  if(showBox){
    flagbox = new flagBox(flag);
    flagbox.display();
  }
  
  //If a country is picked for the graph, it is displayed in the bottom-left corner
  countryManager.display();
  popMatrix();
  if(programStart)
    currentScreen = 2;
  
  //Console prints for debugging
  /*println("MouseX: " + mouseX + " MouseY " + mouseY);
  println("Screen: " + screenX(0,0) + " " + screenY(0,0));
  println("Width: " + width + " Height: " + height);
  //println("HalfscreenX: " + halfscreenX + " HalfscreenY: " + halfscreenY);
  println("Origo X: " + origoX + " Y: " + origoY);
  println("Current country ID: " + currentCountryId);
  println("Current country name: " + currentCountryName);
  println("Scale: " + scale);
  countryManager.printCountryNames();
  println("GDP: " + currentCountryGDP + " Population: " + currentCountryPop);
  println(frameRate);*/

}

//DRAW THE GRAPH SCREEN---------------------------------------------------------------
void drawScreenTwo(){
  
  fill(255,alpha);
    
  if (millis() - timer < 3000) {
    alpha+=1;
  }
  //Translate is added to set the graphs origin away from screen bottom
  translate(50,-50);
  
  //The translate above has to be added to the Cursor object for collision checking
  cursor.x = mouseX-50;
  cursor.y = mouseY+50;
  //White background of the graph -screen  
  rect(-50,50,width,height);
  fill(0);
  textSize(30);
  
  //Axis texts
  String gdp = "GDP per capita ($)";
  String pop = "Population";
  String zero = "0";
  text(zero,-25,height+25);
  text(gdp, width*(40.0/100), height+40);
  
  //Rotation of "population" text
  pushMatrix();
  rotate(-HALF_PI);
  text(pop, -width*(35.0/100), -10);
  popMatrix();
  
  //Both axis
  line(0,height,width*(19.0/20),height);
  line(0,height,0,100);
  
  //The CountryManager takes care of the calculations and drawing needed to draw the graph
  countryManager.putToChart();
  
}

//DRAW THE HELP SCREEN-----------------------------------------------------------------------------
void drawScreenHelp(){
  programStart = false;
  helpScreen = true;
    
  if (millis() - timer < 3000) {
    alpha+=1;
  }
  fill(255,5);
  rect(width/3,height/4,width/3,height/2,50);
  fill(0);
  textSize(40);
  text("Help", width/4+width/7,height*(30.0/100));
  textSize(20);
  text("Mouse LEFT = pick a country",width/4+width/7,height*(35.0/100));
  text("Mouse RIGHT = display flag",width/4+width/7,height*(40.0/100));
  text("Mouse DRAGGED = drag map",width/4+width/7,height*(45.0/100));
  text("Mousewheel = zooming",width/4+width/7,height*(50.0/100));
  text("ENTER = compare countries",width/4+width/7,height*(55.0/100));
  text("BACKSPACE = delete last country",width/4+width/7,height*(60.0/100));
  text("DELETE = delete all countries",width/4+width/7,height*(65.0/100));
  text("H = toggle help",width/4+width/7,height*(70.0/100));
  
}

//THE OUTSIDE FUNCTIONS----------------------------------------------------------------------------

//The method for checking if the Cursor object is contained in one of the countries
void highlightCountry(){
  for(int i = 0; i < countryNumber; i++){
     if(worldblueprint.children[i].contains(cursor)){
        currentCountryId = worldblueprint.children[i].name;  //Set the current countryId to the highlighted country for other functions/objects to use
        RG.ignoreStyles(true);
        noStroke();
        worldblueprint.children[i].draw(g);
        fill(150,255,150);
     }
     //If a country is picked it needs to stay highlighted
     if(countryManager.isInListId(worldblueprint.children[i].name)){
       worldblueprint.children[i].draw(g);
     }
  }
    getCountryInfo(); //Get the current countries info from the .xls file
}

//Function for reading the .xls file. Here the currentCountryId is used to 
//get country's name, GDP and population 
void getCountryInfo(){
      reader.firstRow();
      reader.firstCell();
            
      while(reader.hasMoreRows()){
        reader.firstCell();
        reader.nextRow();
      
        String loopcountry = reader.getString();
      
        if(loopcountry.equals(currentCountryId) == true){
          //READ ALL THE INFO AND SAVE TO VARIABLES
          reader.nextCell();
          currentCountryName = reader.getString();
          reader.nextCell();
          currentCountryGDP =  reader.getInt();
          reader.nextCell();
          currentCountryPop = reader.getInt();
      }
    }
}

//A string parser for matching together country's name and the .png files name
String getFlag(){
    String[] parseSplit = split(currentCountryName, " ");
    String parsedCountry = join(parseSplit, "_");
    String flag = ("data/flags/" + parsedCountry + ".png");
    return flag;
}


