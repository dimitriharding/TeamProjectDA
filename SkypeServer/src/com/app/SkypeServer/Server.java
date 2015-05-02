package com.app.SkypeServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/*I am wondering*/
public class Server {
	private static View view;
	private volatile Thread svr;
	static String userName = "";
	@SuppressWarnings("rawtypes")
	static List list = new ArrayList<String>();
	static HashMap<String, ObjectOutputStream> users = new HashMap<String, ObjectOutputStream>();
	private Socket socket;
	@SuppressWarnings("unused")
	private ServerSocket serverSocket;
	Server(View view, ServerSocket serverSocket) throws IOException{
		Server.view = view;
		Server.view.updateTextArea("Server Status: online...");
		this.serverSocket = serverSocket;
		Server.view.updateTextArea("Server status: " + serverSocket);
		while(true){
			try{
			this.socket = serverSocket.accept();
			}catch(IOException e){
				/*public void close()
				           throws IOException

				Closes this socket. Any thread currently blocked in accept() will throw a ---> SocketException. <---
				If this socket has an associated channel then the channel is closed as well.*/
				e.printStackTrace();
			}
			if(socket != null){
				svr = new Thread(new myServerThread(socket));
				svr.start();
			}
			else{
				break;
			}
		}
	}
	class myServerThread implements Runnable{
		private Socket thisSocket;
		private ObjectOutputStream out = null;
		private ObjectInputStream in = null;
		myServerThread(Socket socket){
			thisSocket = socket;
			if(thisSocket != null){
			try {
				out = new ObjectOutputStream(thisSocket.getOutputStream());
				in = new ObjectInputStream(thisSocket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
		}
		public void handler() throws IOException, ClassNotFoundException{
			userName = "";
		}
		@Override
		public void run() {
			try {
				while(true){
					String request = (String) in.readObject();
					if(request.equals("AddUser")){
						try {
							userName = (String) in.readObject();
							Server.view.updateTextArea(userName+" connected @:" + socket);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						users.put(userName, out);
						Set<String> set = (Set<String>)users.keySet();
						Object[] listofUser = set.toArray();
						ObjectOutputStream localStream1 = null;
						for (Object contact : listofUser ){
							localStream1 = users.get((String)contact);
							localStream1.writeObject("UpdateList");
							localStream1.writeObject(set.toArray());
						}
					}else if(request.equals("PM")){
						//System.out.println("sending a message");
						String sender = (String) in.readObject();
						//System.out.println("Sender :" + sender);
						String contact = (String) in.readObject();
						//System.out.println("Contact :" + contact);
						String message = (String) in.readObject();
						//System.out.println("Message :" + message);
						ObjectOutputStream localStream = users.get(contact);
						localStream.writeObject("Recieve");
						//System.out.println("Service : Recieve");
						localStream.writeObject(sender);
						//System.out.println("1");
						localStream.writeObject(message);
						//System.out.println("2) Message sent to stream: "+users.get((String)contact));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
