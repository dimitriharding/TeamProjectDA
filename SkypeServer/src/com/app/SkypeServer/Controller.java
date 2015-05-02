package com.app.SkypeServer;

import java.io.IOException;
import java.net.ServerSocket;

public class Controller {
	private Server server = null;
	private volatile Thread svrThread = null;
	private volatile RunnableSvr runSvr = null;
	private View view;
	public Controller(){
		view = new View(this);
	}
	public void startSvr(){
		if(svrThread == null){
			svrThread = new Thread(runSvr = new RunnableSvr(view,server));
			svrThread.start();
		}else{
			long threadID = Thread.currentThread().getId();
			view.updateTextArea("Server Status: Server is already running @ thread :"+Long.toString(threadID));
		}
	}
	public void stopSvr(){
		if(svrThread == null){
			view.updateTextArea("Server Status: offline...");
		}else{
			runSvr.destroy();
			server = null;
			svrThread.interrupt();
			svrThread = null;
		}
	}
	public static void main(String[] args) {
		new Controller();
	}
}
class RunnableSvr implements Runnable{
	@SuppressWarnings("unused")
	private Server chatSvr;
	private View view;
	private ServerSocket serverSocket;
	
	
	public RunnableSvr(View view, Server server){
		this.view = view;
		this.chatSvr = server;
		try {
			serverSocket = new ServerSocket(5000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void handle(){
		try {
			chatSvr = new Server(view,this.serverSocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		handle();
	}
	public void destroy(){
		try {
			serverSocket.close();
			view.updateTextArea("Server Status: offline...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}