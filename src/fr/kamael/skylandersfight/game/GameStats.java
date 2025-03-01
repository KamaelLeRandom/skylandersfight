package fr.kamael.skylandersfight.game;

public class GameStats {
	public Integer nbKill = 0;
	public Integer nbDeath = 0;
	public Integer nbAssist = 0;
	public Double  nbDamage = 0.;
	public Integer nbItem = 0;
	public Integer nbHeal = 0;
	
	public void reset() {
		this.nbKill = 0;
		this.nbDeath = 0;
		this.nbAssist = 0;
		this.nbDamage = 0.;
		this.nbItem = 0;
		this.nbHeal = 0;
	}
}
