package me.arkorwan.srpg.ui;

import javax.swing.JLabel;

import me.arkorwan.srpg.models.Attribute;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.srpg.models.equipment.Equipment;

public class UnitInformationPrinter {

	JLabel lblName, lblUnitDetails;

	public UnitInformationPrinter(JLabel lblName, JLabel lblUnitDetails) {
		this.lblName = lblName;
		this.lblUnitDetails = lblUnitDetails;
	}

	public void clearDisplayedUnit() {
		lblName.setText("");
		lblUnitDetails.setText("");
	}

	public void displayUnit(Unit unit) {
		lblName.setText(unit.getName());

		StringBuilder builder = new StringBuilder("<html>");
		builder.append(unit.getRace().name()).append(" ")
				.append(unit.getJob().name()).append("<br>");
		displayPointsAttribute(builder, unit.getHP(), unit.getCurrentHP());
		displayPointsAttribute(builder, unit.getMP(), unit.getCurrentMP());
		displayAttribute(builder, unit.getPhysicalAttack());
		displayAttribute(builder, unit.getPhysicalDefense());
		displayAttribute(builder, unit.getMagicalAttack());
		displayAttribute(builder, unit.getMagicalDefense());
		displayAttribute(builder, unit.getEvasion());
		displayAttribute(builder, unit.getSpeed());
		displayAttribute(builder, unit.getMoveRange());

		builder.append("<br>");
		displayEquipment(builder, "Weapon", unit.getWeapon());
		displayEquipment(builder, "Armour", unit.getArmour());
		builder.append(String.format("Weight: %.1f / %.1f<br>",
				unit.getWeapon().getWeight() + unit.getArmour().getWeight(),
				unit.getWeightThreshold()));
		builder.append("</html>");

		lblUnitDetails.setText(builder.toString());
	}

	private void displayAttribute(StringBuilder b, Attribute attr) {
		b.append(attr.getName()).append(": ").append(attr.getValue())
				.append("<br>");
	}

	private void displayPointsAttribute(StringBuilder b, Attribute attr,
			int current) {
		b.append(attr.getName()).append(": ").append(current).append("/")
				.append(attr.getValue()).append("<br>");
	}

	private void displayEquipment(StringBuilder b, String eqType,
			Equipment eq) {
		b.append(eqType).append(": ").append(eq.getInformation())
				.append("<br>");
	}
}
