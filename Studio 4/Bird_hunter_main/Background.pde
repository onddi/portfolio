class Background {

  PImage img, img2, img3;
  int x_, y_;

  Background()
  {

    img = loadImage("level2.png"); // image is 1600 x 600
    img2 = loadImage("level2.png");
    img3 = loadImage("level2.png");
  }

  void display()
  {
    
    // not needed as image is bigger than size 
    // and thus overwrites all areas
    //x_ = constrain(x_, 0, img.width - width);    
    // ensures that "scrolling" stops at right end of image
    // y = constrain(y, 0, img.height - height); 
    // Not needed here, as scolling only in x

    if (horseBody.getVelocityX() >= 0)
      x_ += 2 + (int) horseBody.getVelocityX()/100;
    else if (horseBody.getVelocityX() < 0)
      x_ += -2 + (int) horseBody.getVelocityX()/100;

    image(img, -x_-400, 50);//-x_, 0);
    //image(img3,-x_-img3.width,50);

    if (x_ > img.width-width-400) {
      pushMatrix();
      translate(img.width, 0);
      image(img2, -x_-400, 50);
      popMatrix();
    }else if(x_ < width){
      image(img3,-x_-img3.width,50);
    }
    
    if(x_ < -3000)
    x_ = 0;
    if(x_ > 7000)
    x_ = 7000;
    
    //println(x_);
    
  }

  void setX(int x) {
    x_ = x;
  }
}

