package me.arkorwan.srpg.generators;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import me.arkorwan.srpg.generators.constraints.AttributeConstraint;
import me.arkorwan.srpg.generators.constraints.QuotaConstraint;
import me.arkorwan.srpg.generators.constraints.QuotaConstraint.Quota;
import me.arkorwan.srpg.models.BasicAttribute;
import me.arkorwan.srpg.models.Job;
import me.arkorwan.srpg.models.Race;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.utils.NameGenerator;

public class UnitGenerator extends Generator<Unit> {

	double averageAttributeValues = 0.5;
	double deviation = 0.3;

	Set<AttributeConstraint<BasicAttribute.Type>> attributeConstraints;
	QuotaConstraint<Job> jobConstraint;
	QuotaConstraint<Race> raceConstraint;

	transient Quota<Job> qj;
	transient Quota<Race> qr;
	transient boolean peekMode = false;

	static NameGenerator nameGenerator = new NameGenerator(new Random());

	public UnitGenerator(BattleSystem sys, Random r) {
		super(sys, r);
	}

	public void setAttributeConstraints(
			Set<AttributeConstraint<BasicAttribute.Type>> attributeConstraints) {
		this.attributeConstraints = attributeConstraints;
	}

	public void setJobConstraint(QuotaConstraint<Job> job) {
		this.jobConstraint = job;
	}

	public void setRaceConstraint(QuotaConstraint<Race> race) {
		this.raceConstraint = race;
	}

	public void refreshQuotas() {
		qj = QuotaConstraint.createFreshQuota(jobConstraint);
		qr = QuotaConstraint.createFreshQuota(raceConstraint);
	}

	public void setPeekMode(boolean b) {
		this.peekMode = b;
	}

	@Override
	public Unit generate() {

		if(qj == null || qr == null){
			refreshQuotas();
		}
		
		Set<AttributeConstraint<BasicAttribute.Type>> attrConstraints = attributeConstraints == null
				? Collections.emptySet() : attributeConstraints;

		AttributesDistributor<BasicAttribute.Type> distributor = new AttributesDistributor<>();

		Map<BasicAttribute.Type, Double> attributes = distributor.distribute(
				averageAttributeValues, deviation,
				Arrays.asList(BasicAttribute.Type.values()), attrConstraints,
				rand);

		Unit u = new Unit(sys, attributes);
		u.setName(nameGenerator.getName());

		u.setJob(qj.getRandomItem(rand, Job.values(), peekMode));
		u.setRace(qr.getRandomItem(rand, Race.values(), peekMode));

		return u;
	}

	@Override
	protected Generator<?>[] downstream() {
		return new Generator[0];
	}

}
