package systemManagers;

import java.util.stream.Stream;

public class VariableManipulator {

	
	public static <T> Object[] concatenate(T[] array1, T[] array2) {
		return Stream.of(array1, array2).flatMap(Stream::of).toArray();		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
