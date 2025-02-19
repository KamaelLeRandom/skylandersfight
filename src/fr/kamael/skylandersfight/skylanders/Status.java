package fr.kamael.skylandersfight.skylanders;

public enum Status {
	NOTAKEDAMAGE,  // Ne peut pu prendre de dégats
	NOMAKEDAMAGE,  // Ne peut pu infliger de dégats
	NOSPELL,       // Ne peut pu lancer de sort
	NOMOVE,        // Ne peut plus bouger
	NOHEAL,        // Ne peut plus se soigner
	NOTELEPORT,    // Ne peut pas se téléporter
	NOFLY,         // Ne peut plus voler
	NOFALL,        // Ne prend pas de dégats de chute
	ONEFALL,       // Ne prend pas de dégats de chute pour une fois
	INVISIBLE,     // Est invisible
	
	CONCENTRATION, // Entrain de faire quelque
	RECHERCHE,     // Quand un joueur est recherché par Zem'Zem
}
