void mousePressed(){
  dragMap = true;
    
  if(mouseButton == RIGHT){
    strokeWeight(1/scale);
        
    if(showBox == false)
      showBox = true;
     else
      showBox = false;
     
  }
  
  if(mouseButton == LEFT){
    Country country = new Country(currentCountryName,flag,currentCountryId,currentCountryGDP,currentCountryPop);
    if(!countryManager.isInList(country))
      countryManager.addCountry(country);
    else
      country = null;
    
  }
}

void mouseDragged(){
  if(dragMap){
    cursor(MOVE);
    origoX += (mouseX-pmouseX); 
    origoY += (mouseY-pmouseY);
   }
}

void mouseReleased(){
  dragMap = false;
  cursor(ARROW);
}

void keyPressed() {
  if (key == 'h') {
    if(helpScreen == false)
      currentScreen = 2;
    else
      currentScreen = 0;
  }
  
  if (keyCode == DELETE) {
    countryManager.deleteAll();
  }
  
  if (keyCode == BACKSPACE){
    countryManager.deleteLast();
  }
  
  if(keyCode == ENTER){
      timer = millis();
    
      if(currentScreen == 0){
        currentScreen = 1;
      }else{
        currentScreen = 0;
      }
    
  }
}

void mouseWheel(MouseEvent event){
  float e = event.getAmount();
  
   if(e < 0){
     scale *= 1.1;
      }
   else{
     scale /= 1.1;
    }
   
   
 
}
