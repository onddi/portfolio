class Arrow {

  ArrayList arrows;
  float velfactor;
  float angle; 
  FPoly arrowBody;

  Arrow(float elapsed_) {

    angle = atan2(arrowY-indian.getPos().y, arrowX-horseBody.getX()); //shot Angle
    velfactor = 1+elapsed_/1000; //how many seconds was the shootingButton pressed, in seconds
    arrowVelocity = new PVector(arrowX-horseBody.getX()+aimShakeX, arrowY-indian.getPos().y+aimShakeY);
    
    setupArrowBody();
    
  }

  void update() {
    pushMatrix();
    arrowBody.resetForces();
    translate(arrowBody.getX(), arrowBody.getY());
    rotate(arrowBody.getRotation());
    
    arrowBody.setRotation(atan2(arrowBody.getVelocityY(), arrowBody.getVelocityX())); //adjust rotation for trajectory
    
    constrains();
    
    popMatrix();
  }
  
  void setupArrowBody(){
    arrowBody = new FPoly();
    arrowBody.vertex(-28f,0);//(-14f, 0);
    arrowBody.vertex(0, -6f);//(0, -3f);
    arrowBody.vertex(12f, 0f);//(6f, 0);
    arrowBody.vertex(0,  6f);//(0, 3f);
    arrowBody.setPosition(indian.getPos().x, indian.getPos().y);
    arrowBody.setVelocity(arrowVelocity.x*velfactor, arrowVelocity.y*velfactor);
    arrowBody.setRestitution(0.5);
    arrowBody.setFriction(0.5);
    arrowBody.setDensity(1);
    arrowBody.setRotation(angle);
    arrowBody.setBullet(true);
    arrowBody.attachImage(arrowimg);
    world.add(arrowBody);
  }

  FPoly getArrowBody() { 
    return arrowBody;
  }
  
  PVector getPos(){ return new PVector(arrowBody.getX(),arrowBody.getY());}
  
  void constrains(){
    if (arrowBody.getY() > height-20 || arrowBody.getX() < -100 || arrowBody.getX() > width+100 || arrowBody.getY() < -500) { //
      arrowContainer.removeArrow(arrowBody);
      points += pointMultiplier*pointMultiplier;
      pointMultiplier = 0;
      //println("REMOVED");
    }
  }
  
}

//ARROWCONTAINER contains/manages all arrows

class ArrowContainer {

  ArrayList<Arrow> arrows;

  ArrowContainer() {
    arrows = new ArrayList<Arrow>();
  }

  void update() {
    for (int i = 0; i < arrows.size(); i++) {
      arrows.get(i).update();
    }
  }

  void addArrow(float elapsed_) {
    arrowsShot += 1;
    arrows.add(new Arrow(elapsed_));
  }
  
  ArrayList<Arrow> getArrows(){ return arrows; }

  void removeArrow(FBody arrowBody_) {

    FPoly arrowBody = (FPoly)arrowBody_;

    for (int i = 0; i < arrows.size(); i++)
      if (arrows.get(i).getArrowBody() == arrowBody)
        arrows.remove(i);

    world.remove(arrowBody);
  }
  
  void removeAllArrows(){
    for (int i = 0; i < arrows.size(); i++){
      FPoly arrowBody = arrows.get(i).getArrowBody();
      world.remove(arrowBody);
    }
    arrows.clear();
    
  }
  
}




