class Indian {

  FBox indianBody;
  FBox gorilla;
  float targetX;
  float dx;

  float angle;
  float drawMax;

  Indian() {
    /*indianBody = new FBox(80, 120);
     indianBody.setPosition(width/2, height-120);
     indianBody.setVelocity(40, 0);
     indianBody.setRestitution(2);
     indianBody.setRotatable(false);
     indianBody.attachImage(gorillaimg);
     //indianBody.setFriction(0.5);
     indianBody.setDensity(1);
     indianBody.setBullet(true);
     indianBody.setSensor(true);
     world.add(indianBody);
     
     gorilla = new FBox(80, 120);
     gorilla.setPosition(width/2+200, height-120);
     gorilla.setVelocity(60, 0);
     gorilla.setRestitution(1);
     gorilla.setRotatable(false);
     gorilla.attachImage(gorillaimg);
     gorilla.setFriction(0.5);
     gorilla.setDensity(1);
     gorilla.setBullet(true);
     gorilla.setSensor(true);
     world.add(gorilla);*/
  }

  void update() {
    castRay();
    rectMode(CENTER);

    /*  //Easing box GORILLA
     targetX = indianX;
     dx = targetX - indianBody.getX();
     if (abs(dx) > 1) {
     indianBody.adjustVelocity(dx*0.005, 0);
     }
     
     
     if (indianBody.getVelocityX() > 0)
     indianBody.attachImage(gorillaimg1);
     else 
     indianBody.attachImage(gorillaimg);
     
     //EASING BOX
     float targetX1 = indianX;
     float dx1 = targetX1 - gorilla.getX();
     if (abs(dx1) > 1) {
     gorilla.adjustVelocity(dx1*0.010, 0);
     }
     
     if (gorilla.getVelocityX() > 0)
     gorilla.attachImage(gorillaimg1);
     else 
     gorilla.attachImage(gorillaimg);
     
     //println("INDIAN VELOCITY " + indianBody.getVelocityX());
     //println("GORILLA VELOCITY " + gorilla.getVelocityX());
     //println("DX " + abs(dx));
     //println("DX1 " + abs(dx1));
     */

    drawIndian();
    

  }

  void drawIndian() {

    pushMatrix();
    translate(horseBody.getX(), indian.getPos().y);//indianX, height);
    angle = atan2(arrowY-indian.getPos().y, arrowX-horseBody.getX())+PI/2;
    rotate(angle);

    imageMode(CENTER);
    if (angle < 0)   //flip indian horizontally if angle < 0   
        scale(-1, 1);
    
    scale(scales,scales);
    image(bowdrawimg, 0, 0); //image of indian
    imageMode(CORNER);
    
    //ellipse(0, 0, 20, 20);

    drawMax = reduceCrosshair; //the bow bends with drawMax

    if (drawMax > 45)  
      drawMax = 45;
    translate(10,40);
    noFill();
    stroke(255);
    strokeWeight(3);
    beginShape();                                //Bow
    vertex(-80, -60+drawMax/2);
    bezierVertex(-40, -120, 40, -120, 80, -60+drawMax/2);
    endShape();

    beginShape();
    strokeWeight(1);
    vertex(-80, -60+drawMax/2);                  //Bow string
    bezierVertex(0, -60+drawMax, 0, -60+drawMax, 80, -60+drawMax/2);
    endShape();

    if (shootPressed) {                 
      stroke(193, 131, 42);
      strokeWeight(9);
      line(5, -20+drawMax, 5, -60+drawMax);          //Bow drawing hand
      ellipse(5, -70+drawMax*1.05, 5, 10);
      ellipse(5, -25+drawMax, 2, 7);
      rotate(-PI/2);
      scale(1.7, 1);
      image(arrowimg, 50-drawMax, 0);
      strokeWeight(1);
      rotate(PI/2);
    }

    popMatrix();
  }

  PVector getPos() {
    return new PVector(horseBody.getX(), horseBody.getY()-100);
  }
  
  float getAngle(){
    return angle;
  }

  
}

class Crosshair {

  Crosshair() {
  }

  void update() {
    fill(255, 0, 0, 100);
    ellipse(arrowX, arrowY, 100-reduceCrosshair, 100-reduceCrosshair);
   
   //CROSSHAIR SIZE
    pushMatrix();
    float r = (100-reduceCrosshair)/2;
    float rSquared = r*r;
    translate(arrowX, arrowY);

    if (shootPressed) { 
      aimShakeX=random(-r, r);              //The bow shakes when drawing so the point towards the arrow is shot varies
      aimShakeY=random(-1, 1)*sqrt(rSquared-aimShakeX*aimShakeX);
      fill(0, 0, 255, 100);
      ellipse(aimShakeX, aimShakeY, 5, 5);
      
    }
    else {
      fill(255, 224, 50, 150);
      ellipse(aimShakeX, aimShakeY, 10, 10);
    }

    popMatrix();
  }
}

