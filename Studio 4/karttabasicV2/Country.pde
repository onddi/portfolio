class Country {
  String name;
  String ID;
  PImage flag;
  int population;
  int GDP;
  
  Country(String _name, PImage _flag, String _ID, int _GDP, int _population){
    name = _name;
    flag = _flag;
    ID = _ID;
    GDP = _GDP;
    population = _population;
  }
  
  String getName(){ return name; }
  
  PImage getFlag(){ return flag; }
  
  String getID(){ return ID; }
  
  int getGDP(){ return GDP; }
  
  int getPop(){ return population; }
  
   @Override 
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof Country)
        {
            sameSame = this.name == ((Country) object).name;
        }

        return sameSame;
    }
   
}
