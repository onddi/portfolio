class flagBox{
  PImage flag;
  String name;
  float boxWidth = 252;
  float boxHeight = 170;
  float boxX = (mouseX-origoX+40);
  float boxY = (mouseY-origoY+70);
  float yMove = 0;
  float xMove = 0;
  
  flagBox(PImage _flag){
    flag = _flag;
  }
  
  void display(){
     
    fill(220);
    if(mouseY > height-boxHeight){
      yMove =(boxHeight)/scale;
    }else{
      yMove = 0;
    }
    
    if(mouseX > width-boxWidth){
      xMove =(boxWidth)/scale;
    }else{
      xMove = 0;
    }
    
    rect((boxX-xMove)/scale, (boxY-yMove)/scale, boxWidth/scale, boxHeight/scale, 10/scale);
    
    fill(0);
    textSize(32/scale);
    strokeWeight(1/scale);
    stroke(5);
    text(currentCountryName, (boxX+2-xMove)/scale, (boxY-8-yMove)/scale);
    fill(255);
    text(currentCountryName, (boxX-xMove)/scale, (boxY-10-yMove)/scale);
      
    image(flag, (boxX+10-xMove)/scale, (boxY+10-yMove)/scale, 234/scale, 150/scale);
    
  }
}