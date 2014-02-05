/**
 * This sketch demonstrates how to use the BeatDetect object song SOUND_ENERGY mode.<br />
 * You must call <code>detect</code> every frame and then you can use <code>isOnset</code>
 * to track the beat of the music.
 * <p>
 * This sketch plays an entire song, so it may be a little slow to load.
 */

import peasy.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import com.phidgets.*;
import com.phidgets.event.*;
import fisica.*;
import org.jbox2d.common.*;

//AUDIO
Minim minim;
AudioSource mic;
FFT fft;
AudioPlayer aplayer;

AudioSample snd[]; //AudioSamples are "triggered" sounds
int currSnd; //simple counter

SoundAnalyzer soundanalyzer;

//BACKGROUND & EFFECTS
Background scrollingbackground;
ParticleSystem particle;

//PHYSICS BODIES
FWorld world;
FBox obstacle;
FBox ground;
FPoly poly;
FBox horseBody;

//PLAYER
Indian indian;
Crosshair crosshair;
ArrowContainer arrowContainer;
Horse horse;

//BIRD
Bird bird;
BirdContainer birdcontainer;

//PHIDGET INTERFACE
InterfaceKitPhidget ik;

ArrayList arrows;

PVector birdvelocity;
PVector arrowVelocity;

float aimShakeX = 0;   //The hand shakes when drawing bow
float aimShakeY = 0;   
float angle;           //angle between crosshair and player
float arrowX, arrowY;
float indianX = 600;
float reduceCrosshair = 0;
float pressedX, pressedY;
float startCounter = 255;

int elapsed;
int startTimer;
int pointMultiplier = 0;
int points;
int hits;
int arrowsShot = 0;

//POINT TEXTS
String si;
String mi;
String mipm;
String po;
String aw;
PFont font;

boolean aiming = true;         //truth value if the player is drawing bow
boolean shootPressed = false;  //truth value if shoot button is pressed
boolean moveLeft = true;       //player moving left
boolean gameover = false;
boolean showMicInput = false;

//IMAGES
PImage angrybird, deadangrybird;
PImage arrowimg;
PImage mountains;
PImage bowdrawimg;

PeasyCam cam;

//DEBUG
boolean showJungle = true;
boolean disablesound = false;
float scales = 0.7;
int WIDTH, HEIGHT;

void setup()
{
  size(1600,800, P3D);//displayWidth(), displayHeight(), P3D);
  noCursor();
  font = loadFont("Chiller-Regular-48.vlw");
  textFont(font, 32);
  frameRate(60);

  //cam = new PeasyCam(this, width/2, height/2 , 0 , 700);

  Fisica.init(this);   //Physics library

  setupSound();      
  loadImages();
  createWorld();       //Create physics world
 
  
  //Load Phdigets
  //setupIKP();

  particle = new ParticleSystem(new PVector(0, 0));
  scrollingbackground = new Background();
  crosshair = new Crosshair();
  birdcontainer = new BirdContainer();
  indian = new Indian();
  horse = new Horse();
  arrowContainer = new ArrowContainer();
}

void draw()
{
  background(200, 243, 255);
  
  if (arrowsShot >= 30 && arrowContainer.getArrows().size() == 0)
    gameover=true;

  if (gameover) {
     gameoverScreen();
   }
  else {
    //float a = map(indianX, 0, width, 0, mountains.width-width);
    image(mountains, 0, 0);

    world.step();

    birdcontainer.update();
    crosshair.update();
    world.draw();
    //scale(scales,scales);
    //scale(1/scales,1/scales); 
    if (showJungle)
      scrollingbackground.display();

    particle.addParticle();
    particle.run();
    arrowContainer.update();

    horse.update();
    indian.update();

    if (!disablesound)
      soundanalyzer.update();

    drawingBow();
    drawPoints();
    //setShotAngle();

    arrowX = mouseX;
    arrowY = mouseY;
    
    ellipse(indianX,height-5,5,5);
    
    startScreen();
    
  }
}

void shoot(float elapsed_) {
  if(arrowsShot < 30){
  arrowContainer.addArrow(elapsed_);

  snd[4].stop();
  snd[0].trigger(); //trigger the sound
  }
}

void drawingBow() {

  if (shootPressed) {
    reduceCrosshair+=1;

    if (reduceCrosshair > 100)
      reduceCrosshair = 100;
  }
}

void setShotAngle() {
  angle = atan2(arrowY-indian.getPos().y, arrowX-horseBody.getX()); //shot Angle
}

void createWorld() {
  world = new FWorld();
  //world.setGravity(0,9.89);

  obstacle = new FBox(150, 150);
  obstacle.setRotation(PI/4);
  obstacle.setPosition(0, -500);
  obstacle.setStatic(true);
  obstacle.setFill(0);
  obstacle.setRestitution(0);
  obstacle.setGrabbable(true);

  ground = new FBox(width*10, 5);
  ground.setPosition(width/2, height);
  ground.setStatic(true);
  ground.setFill(0);
  ground.setRestitution(1);
  ground.setDensity(2);

  world.add(obstacle);
  world.add(ground);
}

void setupSound() {
  //SETUP AUDIO SAMPLER
  soundanalyzer = new SoundAnalyzer();
  minim = new Minim(this);
  mic = minim.getLineIn(Minim.STEREO, 1024);
  fft = new FFT(1024, 44000);
  fft.logAverages(20, 60);

  //LOAD SOUND EFFECTS
  snd = new AudioSample[6]; //setup sample array
  for (int i=0; i < 6; i++) snd[i] = minim.loadSample(i+".wav"); //load few samples for variety

  aplayer = minim.loadFile("bgmusic.wav"); //Music from /data folder (CREDIT music from a game called Advent Rising - song Muse)
  aplayer.play(); // play music
  aplayer.setGain(-10);
  aplayer.loop(); // loop song
  
  snd[5].trigger(); //indian scream
}

void loadImages() {
  angrybird = loadImage("angrybird.png");
  deadangrybird = loadImage("deadangrybird.png");
  arrowimg = loadImage("arrow.png");
  arrowimg.resize(0, 6);
  bowdrawimg = loadImage("bowdraw1.png");
  mountains = loadImage("westlands.jpg");
}

void startScreen(){
  if(startCounter > 0){
    fill(0,startCounter);
    pushMatrix();
    translate(width/2,height/2);
    rect(0,0,width+10,height+10);
    fill(255,startCounter);
    textAlign(CENTER);
    textSize(80);
    text("Bird Hunter", 0, -160);
    textSize(40);
    text("You have 30 arrows",0,-80);
    text("Hunt as many birds as possible!",0,-40);
    text("Shoot by holding down joystick & move with rotation sensor",0,0);
    text("Lure birds out by whistling & freeze birds by blowing to the mic",0,40);
    text("Subsequent hits will multiply points",0,80);
    text("Press M to display mic input levels",0,120);
    text("Press Y to view this screen", 0, 160);
    text("Keyboard: move A/D, Mouse: shoot", 0, 200);
    startCounter-=0.3;
    textAlign(BASELINE);
    //println(startCounter);
    popMatrix();
    }
}

void gameoverScreen(){
  
    image(mountains, 0, 0);
    scrollingbackground.display();
    pushMatrix();
    textSize(48);
    textAlign(CENTER);
    fill(255,0,0,200);
    translate(width/2, height/2);
    text("GAMEOVER", 0, -50);
    text("You hunted: " + str(hits) + " birds", 0, 0);
    text("Points earned: " + str(points), 0, 50);
    text("Press any key to retry", 0, 100);
    popMatrix();
    horse.update();
    indian.update();
    textAlign(BASELINE);
}

void stop()
{
  // always close Minim audio classes when you are finished with them
  mic.close();
  // always stop Minim before exiting
  minim.stop();
  super.stop();
}

/*public int displayWidth() {
  return displayWidth-100;
}

public int displayHeight() {
  return displayHeight-100;
}
public String displayRenderer() {
  return P3D;
}*/

