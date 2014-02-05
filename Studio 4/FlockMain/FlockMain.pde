import peasy.*;
import processing.opengl.*;
import ddf.minim.*;

//3D CAMERA
PeasyCam cam;

//GRAPHICS
PGraphics pg;

//MOVING OBJECTS
Flock flock;
Flock flock2;
Player player;

//PARTICLE SYSTEMS
ParticleSystem ps; //The Sun
CloudGenerator cs; //Cloud generator manages multiple cloudparticlesystems
FireParticleSystem fs; //Fire

//BACKGROUND STARS
int numStars;
Star[] circles; // define the array

//MUSIC
Minim minim;
AudioPlayer aplayer;

//IMAGES
PImage ocean;
PImage cloudimg;

//PREDICATES
boolean avoidWalls = false;
boolean followCursor = false;
boolean breeding = true;

//TIME INDICATED WITH COLORS
float red = 255;
float green = 510;
float blue = 765;
float colorchanger;

boolean day = true;
boolean runTime = true; //TIME CAN BE PAUSED WITH V

PFont font;

void setup() {
  size(1366, 768, P3D); //size(1400,1000,P3D); 

  pg = createGraphics(width, height, P3D);
  minim = new Minim(this);
  font = loadFont("ScriptMTBold-80.vlw");

  flock = new Flock();
  flock2 = new Flock();
  player = new Player();

  ps = new ParticleSystem(new PVector(0, 400));
  fs = new FireParticleSystem(new PVector(-1000, 0));
  cs = new CloudGenerator(1); //Add only one cloud 

  // Add an initial set of boids into the system, start with two different boids
  for (int i = 0; i < 100; i++) {
    if (i < 50)
      flock.addBoid(new Boid(width/2, height/4));
    else
      flock2.addBoid(new Boid(width/2, height/(4.0/3)));
  }

  aplayer = minim.loadFile("muse.mp3"); //Music from /data folder (CREDIT music from a game called Advent Rising - song Muse)
  aplayer.play(); // play music
  aplayer.loop(); // loop song
  aplayer.setGain(100); // more volume

  ocean = loadImage("deepsea1.jpg"); //deepsea1440x1000.jpg
  //loadPixels();
  smooth();
  cloudimg = loadImage("cloud.png");

  //Stars to the sky and underwater
  numStars = 200;
  circles = new Star[numStars];
  for (int i=0; i<numStars; i++) {
    circles[i] = new Star(random(width), random(height)); // fill the array with circles at random positions
  }

  //Camera
  cam = new PeasyCam(this, width/2, height/2, 0, 800);
  cam.setMinimumDistance(90);
  cam.setRightDragHandler(cam.getRotateDragHandler()); //Rotation with right mouse
  cam.setLeftDragHandler(null); //Disable left mouse rotation
  noCursor();
}

void draw() {

  background(20, 20, 20);
  
  println("red: " + red + "green: " + green + "blue: " + blue + " MouseX " + (float)(width/2-mouseX)/1000 + " blue " + 100*(float)(1/blue) + " FrameRate: " + frameRate);
  cam.setDistance(600+blue);

  colorchanger = (float)(width/2-mouseY)/1000; //The background color changes according to mouseY offset from the middle
  if (colorchanger < 0)
    colorchanger*=-1;

  tint(255*colorchanger, green, blue, 255);

  image(ocean, 0, 0);

  if (avoidWalls) //Inverted colors filter if 
    //filter(INVERT);

    noStroke();

  //STARS DISPLAY
  for (int i=0; i<numStars; i++) {
    circles[i].display(); // display all the circles
  }

  ps.addParticle(); //Add new particle to the current location of the ParticleSystem
  ps.run(); //update function for ParticleSystem

  for (CloudParticleSystem cs1 : cs.cloudsystem) { //Multiple clouds can be generated
    cs1.addParticle();
    cs1.run();
  }

  flock.run(); //update method for flocks
  flock2.run();

  player.display(); //draw method for player 

  if (runTime) //time can be paused with v
    timeofday();

  lights(); //ligthing for the 3D frame
  drawFrame(); //draw the painting frame
  drawInstructions(); //Title and instructions
  
  noFill();

  fs.run(); //update method for fire particles
}

//The background color changes depending on the time elapsed

void timeofday()
{
  if (day) {
    red-=0.2;
    green-=0.2;
    blue-=0.2;
    if (blue < -200)     
      day = false; //day turns to night
  }

  if (!day) {
    red+=0.5;
    green+=0.5;
    blue+=0.5;
    if (blue > 760)
      day = true; //night turns to day
  }
}

//The 3D frame of the painting
void drawFrame() {

  //CANVAS
  fill(255);
  pushMatrix();
  translate(width/2, height/2, -26);
  box(width+80, height+80, 50);
  popMatrix();
  
  //LEFT SIDE
  fill(200, 145, 12);
  pushMatrix();
  translate(-25, height/2, 0);
  box(50, height+80, 20);
  popMatrix();
  
  //RIGHT SIDE
  pushMatrix();
  translate(width+25, height/2, 0);
  box(50, height+80, 20);
  popMatrix();
  
  //TOP
  pushMatrix();
  translate(width/2, -25, 0);
  box(width+80, 50, 20);
  popMatrix();
  
  //BOTTOM
  pushMatrix();
  translate(width/2, height+25, 0);
  box(width+80, 50, 20);
  popMatrix();

  //NAMETAG
  fill(255, 215, 0);
  pushMatrix();
  translate(width/2, height+20, 0);
  box(150, 30, 30);
  
  textSize(15);
  textAlign(CENTER);
  fill(0);
  text("The Godfather", 0, 5, 15);
  popMatrix();
}

void drawInstructions(){
  fill(0, red+100); //Fade in from black
  rect(0, 0, width, height);
  fill(255, red-100);
  pushMatrix();
  translate(width/2, height/2-100);
  //scale((red+100)/255);
  textFont(font);
  textSize(120);
  text("The Creator", 0, 0);
  textSize(40);
  text("Mouse", 0, 100);
  textSize(40);
  text("Left - destroy or breed...", 0, 150);
  text("Right - move sun...", 0, 200);
  text("Center - no barriers...", 0, 250);
  textSize(30);
  text("Hold down right - camera rotation", 0, 300);
  text("Double click - center camera", 0, 350);
  popMatrix();
}

//A helpfun method for checking if something is on screen
boolean onScreen(float x, float y) {
  if (x > 0 && x < width && y > 0 && y < height)
    return true;
  else 
    return false;
}






