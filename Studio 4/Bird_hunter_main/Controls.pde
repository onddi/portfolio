void mouseMoved() {
  //arrowX = mouseX;
  //arrowY = mouseY;
}

void mousePressed()
{  
  if (mouseButton == LEFT) {
    snd[1].trigger(); //HEARTBEAT
    shootPressed = true;
    startTimer = millis();
    elapsed = 0;
  }
  else if (mouseButton == RIGHT) {
  }
  else if (mouseButton == CENTER) {
    aiming=!aiming;
  }
}

void mouseReleased() {
  if (mouseButton == LEFT) {
    elapsed = millis() - startTimer;
    shoot(elapsed);
    reduceCrosshair = 0;
    shootPressed = false;
    snd[1].stop();
  }
}

void keyPressed() {
  if (key == 'y')
    startCounter = 255; //SHOW HELP MENU
  if (key == 'a')
    if (indianX > 5) 
      indianX-=20;
  if (key == 'd')
    if (indianX < width-5)
      indianX+=20;
  if (key == 'e')
    birdcontainer.addBird();
  if (key == 'w')
    birdcontainer.moveBirds(1);
  if (key == 'r')
    birdcontainer.moveBirds(0);
  if (key == 'm')
    showMicInput = !showMicInput;

  //DEBUG
  if (key == 'q')
    showJungle = !showJungle;
  if (key == 'x')
    disablesound = !disablesound;
  if (key == 't')
    arrowContainer.removeAllArrows();

  //KEYBOARD CROSSBOX DRAW 
  if (key == 'f') {
    shootPressed = true;
    startTimer = millis();
    elapsed = 0;
  } 
  if (key == 'g') {
    elapsed = millis() - startTimer;
    shoot(elapsed);
    reduceCrosshair = 0;
    shootPressed = false;
  }
  if (gameover) {
    arrowsShot = 0;
    points = 0;
    hits = 0;
    pointMultiplier = 0;
    arrowContainer.removeAllArrows();
    birdcontainer.removeAllBirds();
    textSize(20);
    startCounter = 255;
    gameover = false;
  }
}

