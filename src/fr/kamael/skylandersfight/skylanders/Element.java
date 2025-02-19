package fr.kamael.skylandersfight.skylanders;

public enum Element {
	MAGIE ("§5Magie§f", "§5", "§5Magie§f : vous gagnez §510%§f de §5Force§f supplémentaires."), 
	TECH ("§eTech§f", "§e", "§eTech§f : vous gagnez §e5%§f de §eForce§f et §eRésistance§f supplémentaires."), 
	VIE ("§2Vie§f", "§2", "§2Vie§f : vous avez un moyen de récupérer des §2Points de Vie§f."), 
	MORT ("§7Mort§f", "§7", "§7Mort§f : vous avez §75%§f de chance d'esquiver une attaque."), 
	FEU ("§4Feu§f", "§4", "§4Feu§f : vous avez l'effet §cRésistance au Feu§f et vos armes sont §cenflammées§f."), 
	EAU ("§9Eau§f", "§9", "§9Eau§f : après avoir passé 3 secondes dans l'eau, vous gagnez 30 secondes de §bVitesse I§f."), 
	TERRE ("§6Terre§f", "§6", "§6Terre§f : vous avez gagnez §610%§f de §6Résistance§f supplémentaires."), 
	AIR ("§3Air§f", "§3", "§3Air§f : vous êtes §3insensible§f aux §3dégats de chute§f."), 
	BOGDA ("§dBogda§f", "§d", "§dBogda§f : ça, c'est la §dBogda§f, aléatoirement vous gagnez un effet positif ou négatif."), 
	AUCUN ("§8Aucun§f", "§8", "§8Aucun§f : vous n'avez aucun element."),
	;
	
	private String name;
	private String color;
	private String desc;
	
	Element(String name, String color, String desc) {
		this.name = name;
		this.color = color;
		this.desc = desc;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getColor() {
		return this.color;
	}
	
	public String getDesc() {
		return this.desc;
	}
}
