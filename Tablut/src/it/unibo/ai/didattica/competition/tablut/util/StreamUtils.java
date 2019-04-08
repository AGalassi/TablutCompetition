package it.unibo.ai.didattica.competition.tablut.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StreamUtils {
	public static void writeString(DataOutputStream out, String s) throws IOException {
		// Converti la stringa in un array di byte codificati con UTF-8
		byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
		
		// Invio la lunghezza dell'array di byte come intero
		out.writeInt(bytes.length);
		
		// Invio l'array di byte
		out.write(bytes, 0, bytes.length);
	}
	
	public static String readString(DataInputStream in) throws IOException {
		// Leggo la lunghezza dei byte in ingresso
		int len = in.readInt();
		
		// Creo un array di bytes che conterra' i dati in ingresso
		byte[] bytes = new byte[len];
		
		// Leggo TUTTI i bytes
		in.readFully(bytes, 0, len);
		
		// Converto i bytes in tringa
		return new String(bytes, StandardCharsets.UTF_8);
	}
}
