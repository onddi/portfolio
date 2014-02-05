class CountryManager{
  ArrayList<Country> countries = new ArrayList<Country>();
  float x,y;
  float x0 = -width/2;
  float y0 = -height/2;
  float scale;
  int maxGDP = 0; //The chart axis need to be scaled by the countries involved in the comparison
  int maxPop = 0; //These values represent the biggest values found 
  int valueG = 0; 
  int valueP = 0;
  
  CountryManager(float _x, float _y, float _scale){
    x = _x;
    y = _y;
    scale = _scale;
  }
  
  void addCountry(Country c){countries.add(c);}
   
  void printCountryNames(){ 
    int i = 0;
    for(Country c : countries){
      println("Country " + i + ": " + c.getName() + " ID: " + c.getID() + " GDP: " + c.getGDP() + " Population: " + c.getPop());
      i++;
    } 
  }
  
  int listSize() { return countries.size(); } 
  
  boolean isInList(Country c){
    if(c.getName().equals("Ocean"))
      return true;
    for(Country e : countries) 
       if(e.equals(c)){
         return true;
       }
       return false;
  }
  
  boolean isInListId(String id){
    for(Country e : countries) 
       if(e.getID() == id){
         return true;
       }
       return false;
  }
  
  void deleteAll(){
    countries.clear();
  }
  
  void deleteLast(){
    if(countries.size()>0)
      countries.remove(countries.size()-1);
  }
  
  //Draw function for screen 0, used to draw picked countries 
  void display(){
    pushMatrix();
    scale(scale);
    int i = 0;
    
    for(Country c: countries){
      image(c.getFlag(), (x0+i)/scale, (y0+height-20)/scale, 40/scale, 20/scale);
      i+=55;
      if(i >= width)
        break;
    }
    popMatrix();
  }
  
  //Draw function for screen 1, used to draw chart
  void putToChart(){
    valueG = 0;
    valueP = 0;    
    findMaxValues();
    float _x = 0;
    float _y = 0;
    float maxWidth = width-170;
    float maxHeight = 100;
    float minHeight = 50;
    textSize(15);
    
    //Count flag position for each country. Notice x-values are divided by the max GDP-value,
    //putting the highest GDP valued country to the end of the x-axis. Same for y-axis, but by popoulation.
    for(Country c: countries){
      _x = ((float)c.getGDP()/maxGDP)*maxWidth;
      _y = ((((float)c.getPop()/maxPop)*height)-height)*(-1)-minHeight;
      if(_y < 100) //Limit the y-axis to be under 100
      _y = ((((float)c.getPop()/maxPop)*height)-height)*(-1)+maxHeight;
      
     RShape r = RG.getRect(_x,_y,60.0,30.0); //Shaped is used in flag-cursor collision detection
    
      if(r.contains(cursor)){ //If cursor is on top of a flag, draw information
        text(c.getName(), _x, _y-5);
        text(Float.toString(c.getGDP())+ "$",_x,_y+45);
        if(_x > 200)
        text(Float.toString(c.getPop()),5,_y+20);
        if(_x < 100){
          textAlign(LEFT);
          text(Float.toString(c.getPop()),_x+60,_y+20);
        }else{
          textAlign(RIGHT);
          text(Float.toString(c.getPop()),_x,_y+20);
        }
        textAlign(LEFT);
    }
      image(c.getFlag(), _x, _y, 60/scale, 30/scale); //Draw the flag
      text(Float.toString(c.getGDP()),_x+10,height+20);
      line(_x+30,height+3,_x+30,height-3); //Small axis line for GDP value
      line(-3,_y+15,3,_y+15); //Small axis line for Population value

    }
    
  }
  
  //The axis scales depending on the countries involved. The x-axis is scaled based on 
  //the highest GDP value, y-axis based on the highest population. This function is used
  //to count the highest values
  void findMaxValues(){
    for(Country c : countries){
    if(c.getGDP() > valueG)
        valueG = c.getGDP();
    if(c.getPop() > valueP)
        valueP = c.getPop();
    }
    maxGDP = valueG;
    maxPop = valueP;    
  }
  
    
  //This functionality needs to be implemented
  /*void deleteCountry(Country c){
    for(int i = 0; i < countries.size(); i++){
      if(c.equals(countries[i]))
        countries.remove(i);
    }
  }*/
  
}
