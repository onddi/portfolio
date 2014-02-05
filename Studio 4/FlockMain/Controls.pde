
void mousePressed() {

  if (mouseButton == CENTER) {
    avoidWalls = !avoidWalls; //toggle if the boids avoid borders
  }
  else if (mouseButton == LEFT) {
    breeding = !breeding;  //toggle destroy/breed
  }
  else if (mouseButton == RIGHT) {
    moveParticle(); //move the sun
  }
}

void mouseDragged() 
{
  if (mouseButton == LEFT) {
    if(breeding)                    //Add boids depending on the y position
      for (int i = 0; i < 2; i++)
        if(mouseY < height/2)
         flock.addBoid(new Boid(mouseX, mouseY));
        else
         flock2.addBoid(new Boid(mouseX,mouseY));
     else
      for(int i = 0; i < 10; i++)
       fs.addParticle();            //Fire
   } 
}

void keyPressed() {
  
  if (key == 'b') {
    cam.reset();
  }
  if (key == 'v') { //Pause time
    if (runTime == true)
      runTime=false;
    else 
      runTime=true;
  }
}

