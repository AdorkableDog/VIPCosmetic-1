package com.yj.cosmetics.model;

public class StoreClassEntity {
	public boolean Default = true;
	public boolean Highest = false;
	public boolean Lately = false;

	public boolean isDefault() {
		return Default;
	}

	public void setDefault(boolean aDefault) {
		Default = aDefault;
	}

	public boolean isHighest() {
		return Highest;
	}

	public void setHighest(boolean highest) {
		Highest = highest;
	}

	public boolean isLately() {
		return Lately;
	}

	public void setLately(boolean lately) {
		Lately = lately;
	}
}
