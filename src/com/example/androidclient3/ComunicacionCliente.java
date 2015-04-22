package com.example.androidclient3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import android.util.Log;

public class ComunicacionCliente extends Thread {

	private static ComunicacionCliente instance;
	private Socket s;
	private boolean conectado;

	private ComunicacionCliente() {
		conectado = false;
		start();
	}

	public static synchronized ComunicacionCliente getInstance() {
		if (instance == null) {
			instance = new ComunicacionCliente();
		}

		return instance;
	}

	private void conectar() {
		try {
			s = new Socket("10.0.2.2", 5000);
			conectado = true;
			Log.d(getName(), "Conectado al servidor");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enviar(int pantalla) {
		if (conectado) {
			Log.d(getName(), "Enviando numero de pantalla");
			try {
				DataOutputStream salida = new DataOutputStream(
						s.getOutputStream());
				// int valor = (int) (Math.random() * 255);
				salida.writeInt(pantalla);
				Log.d(getName(), "se envio: " + pantalla);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		if (!conectado) {
			conectar();
		}
		while (conectado) {
			recibir();
			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private String recibir() {
		DataInputStream entrada = null;
		String mensaje = "";
		try {
			entrada = new DataInputStream(s.getInputStream());
			mensaje = entrada.readUTF();
			Log.d(getName(), "Se recibio: " + mensaje);
		} catch (SocketException e) {			
			System.err.println("Se perdió la conexión con servidor");
			try {
				entrada.close();
				s.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
			s = null;
			conectado = false;			
		}catch (IOException e) {
			e.printStackTrace();
		}
		return mensaje;
	}
}
