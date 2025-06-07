package fr.kamael.skylandersfight.skylanders;

import org.bukkit.Color;

public enum Element {
	MAGIE ("§5Magie§f", "§5", "§5Magie§f : vous gagnez §510%§f de §5Force§f supplémentaires.", Color.PURPLE), 
	TECH ("§eTech§f", "§e", "§eTech§f : vous gagnez §e5%§f de §eForce§f et §eRésistance§f supplémentaires.", Color.YELLOW), 
	VIE ("§2Vie§f", "§2", "§2Vie§f : vous avez un moyen de récupérer des §2Points de Vie§f.", Color.GREEN), 
	MORT ("§7Mort§f", "§7", "§7Mort§f : .", Color.BLACK), 
	FEU ("§4Feu§f", "§4", "§4Feu§f : vous avez l'effet §cRésistance au Feu§f et vos armes sont §cenflammées§f.", Color.RED), 
	EAU ("§9Eau§f", "§9", "§9Eau§f : vous obtenez un léger effet de Vitesse bonus§f.", Color.BLUE), 
	TERRE ("§6Terre§f", "§6", "§6Terre§f : vous avez gagnez §610%§f de §6Résistance§f supplémentaires.", Color.MAROON), 
	AIR ("§3Air§f", "§3", "§3Air§f : vous êtes §3insensible§f aux §3dégats de chute§f.", Color.WHITE), 
	BOGDA ("§dBogda§f", "§d", "§dBogda§f : ça, c'est la §dBogda§f, aléatoirement vous gagnez un effet positif ou négatif.", Color.FUCHSIA), 
	AUCUN ("§8Aucun§f", "§8", "§8Aucun§f : vous n'avez aucun element.", Color.GRAY),
	;
	
	private String name;
	private String color;
	private String desc;
	private Color colorArmor;
	
	Element(String name, String color, String desc, Color armorColor) {
		this.name = name;
		this.color = color;
		this.desc = desc;
		this.colorArmor = armorColor;
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
	
	public Color getColorArmor() {
		return this.colorArmor;
	}
}
