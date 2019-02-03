package Marlang.AbilityWar.Utils;

/**
 * Math Util
 * @author _Marlang ����
 */
public class NumberUtil {
	
	public static boolean isInt(String s) {
		boolean isInt = true;
		try {
			Integer.parseInt(s);
		} catch(Exception e) {
			isInt = false;
		}
		return isInt;
	}
	
	public static String parseTimeString(Integer Second) {
		Integer Hour = Second / 3600;
		Second -= Hour * 3600;
		Integer Minute = Second / 60;
		Second -= Minute * 60;
		
		return (Hour != 0 ? Hour + "�ð� " : "") + (Minute != 0 ? Minute + "�� " : "") + (Second >= 0 ? Second + "��" : "");
	}
	
	public static NumberStatus getNumberStatus(Number Number) {
		if(Number.intValue() > 0) {
			return NumberStatus.Plus;
		} else if(Number.intValue() < 0) {
			return NumberStatus.Minus;
		} else {
			return NumberStatus.Zero;
		}
	}
	
	public static enum NumberStatus {
		
		Minus,
		Zero,
		Plus;

		public boolean isMinus() {
			return this.equals(NumberStatus.Minus);
		}

		public boolean isZero() {
			return this.equals(NumberStatus.Zero);
		}

		public boolean isPlus() {
			return this.equals(NumberStatus.Plus);
		}
		
	}
	
}
