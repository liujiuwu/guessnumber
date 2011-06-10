package com.pure.guessnumber.util;

import java.util.Random;

public abstract class Utils {
	public static long getRandomByRange(int min, int max) {
		return min + new Random().nextInt(max - min + 1);
	}
}
