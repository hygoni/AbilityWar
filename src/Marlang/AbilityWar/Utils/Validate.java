package Marlang.AbilityWar.Utils;

/**
 * Validate
 * @author _Marlang 말랑
 */
public class Validate {

	/**
	 * null 체크
	 * @param objects						null 체크를 할 객체들
	 * @throws IllegalArgumentException		객체중 하나라도 null일 경우
	 */
	public static void NotNull(Object... objects) throws IllegalArgumentException {
		if(objects != null) {
			for(Object o : objects) {
				if(o == null) {
					throw new IllegalArgumentException("Null이 되어서는 안됩니다.");
				}
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public static void MinimumConstant(Class<?> enumClass, int count) throws IllegalArgumentException {
		NotNull(enumClass);
		if(enumClass.isEnum()) {
			if(enumClass.getEnumConstants().length < count) {
				throw new IllegalArgumentException(enumClass.getName() + "에 최소 " + count + "개의 상수가 있어야 합니다.");
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
	
}
