package me.arkorwan.srpg.models;

import me.arkorwan.srpg.models.equipment.AttackType;
import me.arkorwan.srpg.models.equipment.ConcreteArmour.ArmourType;

public enum Job {

	//@formatter:off
	Warrior  (AttackType.Melee, ArmourType.Plate), 
	Mage     (AttackType.Magic, ArmourType.Robe), 
	Cleric   (AttackType.Magic, ArmourType.Robe), 
	Ranger   (AttackType.Range, ArmourType.Hide);
	//@formatter:on
	
	AttackType atkType;
	ArmourType armourType;
	
	Job(AttackType atk, ArmourType arm){
		this.atkType = atk;
		this.armourType = arm;
	}
	
	public AttackType getWeaponAttackType(){
		return atkType;
	}
	
	public ArmourType getArmourType(){
		return armourType;
	}
}
