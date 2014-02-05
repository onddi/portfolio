void contactStarted(FContact c) {
  FBody ball = null;

  //if the arrow hits the ground
  if (c.getBody1() == ground && c.getBody2() instanceof FPoly || c.getBody1() instanceof FBox && c.getBody2() instanceof FPoly) {
    FBody arrow2 = c.getBody2();
    arrowContainer.removeArrow(arrow2);
    //snd[2].trigger();
    //particle.addParticle(new PVector(c.getX(), c.getY()));
  }

  //if arrow hits a bird
  else if (c.getBody2() instanceof FCircle && c.getBody1() instanceof FPoly ) {

    snd[2].trigger();
    particle.addParticle(new PVector(c.getX(), c.getY()));

    FBody arrow1 = c.getBody1();
    arrowContainer.removeArrow(arrow1);
    
    FBody bird = c.getBody2();
    bird.adjustVelocity(0, 600);
    bird.dettachImage();
    bird.attachImage(deadangrybird);
    bird.setSensor(true);
    hits+=1;       //How many birds have been hit + 1
    points += 1;
    pointMultiplier+=1;  //Pointmultiplier +1 (notice, pointmultiplier is reseted if arrow is shot outside the screen)
    
  }

  if (ball == null) {
    return;
  }
  
}

//Raycaster is used to check if the current aiming "line" hits any targets, can be used in debugging
void castRay() {
  FRaycastResult result = new FRaycastResult();
  FBody b = world.raycastOne(horseBody.getX(), indian.getPos().y, arrowX+1, arrowY, result, true);

  stroke(0, 100);
  
  //line(horseBody.getX(), indian.getPos().y, arrowX+aimShakeX, arrowY+aimShakeY);

  if (b != null) {
    b.setFill(120, 90, 120);
    fill(180, 20, 60);
    noStroke();

    float x = result.getX();
    float y = result.getY();
    ellipse(x, y, 10, 10);
  } 
  else {
    obstacle.setFill(0);
  }
}

