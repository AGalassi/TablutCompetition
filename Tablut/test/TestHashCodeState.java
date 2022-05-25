import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

class TestHashCodeState {

	@Test
	void testBrokenHashCodeState() { //success if broken
		StateTablut a = new StateTablut();
		StateTablut b = new StateTablut();
		
		assertEquals(a.getBoard()[0][0].hashCode(), a.getBoard()[0][0].hashCode());
		assertEquals(a.getBoard().hashCode(), a.getBoard().hashCode());
		assertEquals(a.hashCode(), a.hashCode());
		
		assertEquals(a, b);
		assertEquals(a.getBoard()[0][0].hashCode(), b.getBoard()[0][0].hashCode());
		assertNotEquals(a.getBoard().hashCode(), b.getBoard().hashCode());  //<--- !!!!
		assertNotEquals(a.hashCode(), b.hashCode());  // <-- broken!
		
		assertEquals(deepHashCode(a.getBoard()), deepHashCode(b.getBoard()));      //this works
	}
	
	@Test
	void testExpectedBehaviourHashCodeState() { //success if correct
		StateTablut a = new StateTablut();
		StateTablut b = new StateTablut();
		
		assertEquals(a.hashCode(), b.hashCode());
	}
	
	@Test
	void testHashCodeMatrix() {
		Integer[][] a = new Integer[1][1];
		a[0] = new Integer[] {1};
		Integer[][] b = new Integer[1][1];
		b[0] = new Integer[] {1};
		
		assertEquals(Arrays.hashCode(a[0]), Arrays.hashCode(b[0]));
		assertEquals(deepHashCode(a), deepHashCode(b));     //this works
		
		assertNotEquals(a[0].hashCode(), b[0].hashCode());  //<--- !!!!
		assertNotEquals(a.hashCode(), b.hashCode());     //<--- !!!!
	}
	
	public static <T> int deepHashCode(T[][] matrix) {
		int tmp[] = new int[matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			tmp[i] = Arrays.hashCode(matrix[i]);
		}
		return Arrays.hashCode(tmp);
	}

}
