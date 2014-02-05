class Worldmap {
  float x,y,Width,Height,scale;
  float addX,addY;
  float x0 = -width/2;
  float y0 = -height/2;
  
  Worldmap(float _x, float _y, float _scale) {
    x = _x;
    y = _y;
    scale = _scale;
    
  }

  void setScale(float Scale){
    
    scale *= Scale;
    
  }

  float getScale(){ return scale;}

  void setPosition(float px, float py){
    x = px;
    y = py;
  }

  float getX(){
  return x;
  }

  float getY(){
  return y;
  }
  
  void display(){
    pushMatrix();
    translate(x,y);
    scale(scale);
    image(worldmapimg, x0/scale, y0/scale);
    popMatrix();
  }
}
