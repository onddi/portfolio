class Horse {

  PImage image;
  PImage legs;
  PVector position;
  int frameRow;
  int frameColumn;
  float frameTimer;
  PVector velocity;
  boolean left, right;
  float targetX;            //Used for easing movement
  float dx;                //Speed towards easing movement

  Horse() {
    image = loadImage("horse_sheet.png");
    legs = loadImage("legs1.png");
    position = new PVector(0, height-150); // lähtö positio
    frameRow = 0; // mikä rivi lähdekuvasta
    frameColumn = 0; // mikä kolumni lähdekuvasta
    frameTimer = 0; // mikä kolumni animoidaan
    left = false;
    //right = 0;
    setupHorseBody();
  }

  void update() {

    //Easing box
    targetX = indianX;
    dx = targetX - horseBody.getX();
    if (abs(dx) >= 1) {
      horseBody.adjustVelocity(dx*0.005, 0);
    }

    position.x = horseBody.getX()-150;

    frameTimer += 0.3; // animaation framerate
    if (frameTimer >= 15) { // 15 rivii
      frameTimer = 0;
    }
    frameColumn = (int)frameTimer; // heitetään timerista kokonaisluku

    if (horseBody.getVelocityX() > 0)
      moveLeft = false;
    else 
      moveLeft = true;

    if (moveLeft) {
      frameRow = 1; // rivi 1 vasemmalle
    }
    if (!moveLeft) {
      frameRow = 0; // rivi 0 oikealle päin
    }

    pushMatrix(); 
    translate(position.x, position.y);

    // Käytetään getSubImage-funktioo, joka tehtiin alempana.
    // sisään syötetään image ja takas saadaan sopiva frame lähdekuvasta.
    // Framen koko, ja rivi + kolumni määritetään.
    PImage frameImage = getSubImage(image, frameRow, frameColumn, 300, 200);

    // Piirretään tämä kuva image:n sijaan
    image(frameImage, 0, 0);

    popMatrix();

    pushMatrix();
    if (moveLeft) {
      scale(-1, 1);
      if (indian.getAngle() > 0)
        image(legs, -indian.getPos().x-10, indian.getPos().y+10);
      else
        image(legs, -indian.getPos().x-20, indian.getPos().y+10);
    }
    else
      if (indian.getAngle() < 0)
        image(legs, indian.getPos().x-10, indian.getPos().y+10);
      else
        image(legs, indian.getPos().x-20, indian.getPos().y+10);
    popMatrix();
  }

  void setupHorseBody() {
    //BODY
    horseBody = new FBox(20, 50);
    horseBody.setPosition(position.x, position.y+100);
    horseBody.setVelocity(40, 0);
    horseBody.setRestitution(1);
    horseBody.setRotatable(false);
    //horseBody.attachImage(gorillaimg);
    //horseBody.setFriction(0.5);
    horseBody.setDensity(1);
    horseBody.setBullet(true);
    //horseBody.setSensor(true);
    horseBody.setDrawable(false);
    world.add(horseBody);
  }

  // Funktio, joka palauttaa pienemmän croppauksen alkuperäisestä kuvasta, eli framen
  PImage getSubImage(PImage image, int row, int column, int frameWidth, int frameHeight) {
    return image.get(column*frameWidth, row*frameHeight, frameWidth, frameHeight);
  }
}

