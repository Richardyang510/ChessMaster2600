public class Player {
	
	String firstname, lastname;
	int ELO;

	public Player(String whatFirstName, String whatLastName){
		firstname = whatFirstName;
		lastname = whatLastName;
	}
	
	public Player(String whatFirstName, String whatLastName, int whatELO){
		firstname = whatFirstName;
		lastname = whatLastName;
		ELO = whatELO;
	}
	
	public int getELO(){
		return ELO;
	}
	
	public String getName(){
		return firstname + " " + lastname;
	}
	
	public void setELO(int whatELO){
		ELO = whatELO;
	}
	
	public void setFirstName(String whatName){
		firstname = whatName;
	}
	
	public void setLastName(String whatName){
		lastname = whatName;
	}
	
	public void printELO(){
		System.out.println(firstname +" "+ lastname +" has a ELO of "+ELO);
	}
	
	public void newElo(int totELO, int wins, int losses, int games){
		ELO = (totELO+400*(wins-losses))/games;
	}
	
	public void vs(Player b, String result, int k){
	    //equation taken from https://metinmediamath.wordpress.com/2013/11/27/how-to-calculate-the-elo-rating-including-example/
		//transformed rating
		double rA = Math.pow(10, (ELO/400));
		double rB = Math.pow(10, (b.getELO()/400));
		
		//expected score
		double eA = rA/(rA + rB);
		double eB = rB/(rA + rB);
		
		//actual score
		double sA = 0.5, sB = 0.5;
		if(result.equals("WIN")){
			sA = 1;
			sB = 0;
		}
		else if(result.equals("LOSE")){
			sA = 0;
			sB = 1;
		}
		
		//new ELO
		ELO += Math.round(k*(sA-eA));
		b.setELO((int)(b.getELO() + Math.round(k*(sB-eB))));
	}
	
	
}

