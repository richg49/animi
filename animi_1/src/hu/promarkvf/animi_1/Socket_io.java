package hu.promarkvf.animi_1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.os.NetworkOnMainThreadException;

public class Socket_io {
	private Socket socket = null;
	private DataOutputStream dataOutputStream = null;
	private DataInputStream dataInputStream = null;
	private boolean isConnect = false;
	private String log ="";

	public Socket_io(String dest_ip, int dest_port, int timeout) {
		try {
			SocketAddress sockaddr = new InetSocketAddress(dest_ip, dest_port);
			socket = new Socket();
			socket.connect(sockaddr, timeout);
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataInputStream = new DataInputStream(socket.getInputStream());
			isConnect = true;
			log = "";
		}
		catch ( UnknownHostException e ) {
			isConnect = false;
			e.printStackTrace();
			log = e.toString();
		}
		catch ( IOException e ) {
			isConnect = false;
			e.printStackTrace();
			log = e.toString();
		}
		catch ( IllegalArgumentException e ) {
			isConnect = false;
			e.printStackTrace();
			log = e.toString();
		}
		catch ( NetworkOnMainThreadException e ) {
			isConnect = false;
			e.printStackTrace();
			log = e.toString();
		}
	}

	public void socket_write(byte[] data) {
		try {
			dataOutputStream.write(data);
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	public String socket_write(float fenyero, int r, int g, int b, String pixeltomb) {
		int i = 0;
		int j = 0;
		int ser_num = 0;
		byte[] ser_com;
		Long pixel_int;
		byte m = 0x10;
		ser_com = new byte[22];
		ser_com[0] = (byte) ( 0x80 + 17 );
		ser_com[1] = 0x40;
		ser_com[2] = 0x0; // soros adatátvitel előkészítés 17 byte átvitelére
		i = ser_com[0];
		i = i + ser_com[1];
		i = i + ser_com[2];
		ser_com[3] = (byte) ( ( ~ ( i ) ) & 255 );
		for ( i = 4; i <= 21; i++ ) {
			ser_com[i] = 0x0;
		}
		ser_com[4] = 0x55;
		ser_com[5] = m; // utasítás az egységeknek=m
		ser_com[6] = (byte) Math.floor(g * fenyero);
		ser_com[7] = (byte) Math.floor(r * fenyero);
		ser_com[8] = (byte) Math.floor(b * fenyero);
		pixel_int = Long.parseLong(pixeltomb, 2);
		for ( i = 9; i <= 17; i++ ) { // a mátrix átirása
			ser_com[i] = (byte) ( pixel_int & 255 );
			pixel_int = pixel_int / 256;
		}
		j = 0;
		for ( i = 4; i <= 18; i++ ) { // adat crc
			j = j + ser_com[i];
		}
		ser_com[19] = (byte) ( ( ~ ( j ) ) & 255 );
		ser_com[20] = 0x4; // adás utasítás
		j = 0;
		for ( i = 4; i <= 20; i++ ) { // összes crc
			ser_num = ( ser_com[i] );
			j = j + ser_num;
		}
		ser_com[21] = (byte) ( ( ~ ( j ) ) & 255 );
		this.socket_write(ser_com);
		StringBuilder sb = new StringBuilder();
		for ( byte bb : ser_com ) {
			sb.append(String.format("%02X ", bb & 0xff));
		}
		return(sb.toString());
	}

	public byte[] socket_read() {
		byte[] buffer = null;
		try {
			dataInputStream.read(buffer);
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
		return buffer;
	}

	public void socket_clear() {
		String pixeltomb;
		pixeltomb = "0000000000000000000000000000000000000000000000000";
		this.socket_write(0, 0, 0, 0, pixeltomb);
	}

	public void socket_stop() {
		byte[] ser_com;
		ser_com = new byte[8];
		ser_com[0] = 0x0;
		ser_com[1] = 0x0;
		ser_com[2] = 0x0;
		ser_com[3] = 0x0;
		ser_com[4] = 0x0;
		ser_com[5] = 0x0;
		ser_com[6] = 0x0;
		ser_com[7] = 0x0;
		this.socket_write(ser_com);
	}

	public void socket_close() {
		if ( socket != null ) {
			try {
				socket.close();
				isConnect = false;
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
		}

		if ( dataOutputStream != null ) {
			try {
				dataOutputStream.close();
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
		}

		if ( dataInputStream != null ) {
			try {
				dataInputStream.close();
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.socket_close();
		super.finalize();
	}

	public boolean isConnect() {
		return isConnect;
	}

	public String GetLog() {
		return log;
	}
}
