package com.app.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class ChatWindow extends JFrame implements ActionListener {

	private JButton btnSend;
	private JLabel lblCurrentUser;
	@SuppressWarnings("unused")
	private JLabel lblUsers;
	private JLabel lblUsername;
	public static JComboBox<Object> cbUser;
	static JTextArea txtArea;
	private static String currentUser;
	private static JTextField txtField;

	private static String service = "";
	private static String overInternet = "bankaijunior.ddns.net";

	private static Object[] listOfUser = { "--- Select Friend ---" };

	private JPanel panCenter, panTop, panSend, panOnline, panUser;

	private static ObjectOutputStream toServer = null;
	private static ObjectInputStream fromServer = null;
	private Socket socket;
	@SuppressWarnings("unused")
	private int port = 5000;

	public ChatWindow() throws UnknownHostException, IOException,
			ClassNotFoundException {
		super("SkypeChat");
		this.setCurrentUser(currentUser = JOptionPane
				.showInputDialog("Enter Username"));
		this.initializeComponents();
		this.setLayout(new BorderLayout());
		this.addComponentsToPanel();
		this.addPanelToWindow();
		this.setWindowProperties();
		this.registerListener();
		socket = new Socket(InetAddress.getLocalHost(), 5000);
		System.out.println("...Connect to Server");
		new Thread(new ThreadedClass(socket)).start();
	}

	private void initializeComponents() {
		btnSend = new JButton("Send");
		lblCurrentUser = new JLabel("                        Username: "
				+ currentUser);
		lblUsername = new JLabel("Online: ");
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsername.setForeground(Color.green);

		cbUser = new JComboBox<Object>(listOfUser);

		txtField = new JTextField((int) 31.5);
	    Font bigFont = txtField.getFont().deriveFont(Font.PLAIN, 17f);
	    txtField.setFont(bigFont);
		txtArea = new JTextArea(50, 20);
		txtArea.setEditable(false);
		// scrollPane = new JScrollPane(txtArea);

		panTop = new JPanel(new GridLayout(2, 1));
		panCenter = new JPanel(new GridLayout(1, 1));
		panOnline = new JPanel(new GridLayout(1, 2));
		panUser = new JPanel();
		panSend = new JPanel();
	}

	private void addComponentsToPanel() {
		// TODO Auto-generated method stub
		panOnline.add(lblUsername);
		panOnline.add(cbUser);
		panUser.add(lblCurrentUser);
		panCenter.add(new JScrollPane(txtArea));
		panSend.add(txtField);
		panSend.add(btnSend);

		panTop.add(panUser);
		panTop.add(panOnline);
	}

	private void addPanelToWindow() {
		// TODO Auto-generated method stub
		this.add(panTop, BorderLayout.NORTH);
		this.add(panCenter, BorderLayout.CENTER);
		this.add(panSend, BorderLayout.SOUTH);
	}

	private void setWindowProperties() {

		this.setSize(500, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
	}

	private void registerListener() {
		btnSend.addActionListener(this);
		txtField.addActionListener(this);
		addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                try {
                	if(toServer != null){
                		toServer.writeObject("END SESSION");
                		toServer.writeObject(currentUser);
                	}
					e.getWindow().dispose();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(txtField)){
			try {
				if(!txtField.getText().equals("")){
					//if(cbUser.getSelectedItem().toString().equals(""))
					ThreadedClass.sendMessage();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (event.getSource().equals(btnSend)) {
			try {
				ThreadedClass.sendMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setCurrentUser(String name) {
		this.currentUser = name;
	}

	public static void main(String[] args) {
		try {
			new ChatWindow();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("I am here");
			e.printStackTrace();
		}
	}

	static class ThreadedClass implements Runnable {

		private Socket thisSocket;

		ThreadedClass(Socket socket) {
			thisSocket = socket;
		}

		private void getMessage() throws ClassNotFoundException, IOException {
			// Loop for listening to when server send message
			fromServer = new ObjectInputStream(thisSocket.getInputStream());
			toServer = new ObjectOutputStream(thisSocket.getOutputStream());

			// Adds user to online list
			this.addMe();

			// Listens for when Server sends Updates for online list and
			// Also listens for when Server delivers message
			while (true) {
				System.out.println("In loop....");
				service = (String) fromServer.readObject();

				// Recieve service: Gets the user and the message that was sent
				// Displays it on the screen
				if (service.equals("Recieve")) {
					System.out.println("I was called : System");
					char nl = '\n';
					String fromUser = (String) fromServer.readObject();
					txtArea.append(fromUser + ": "
							+ (String) fromServer.readObject() + nl);
				}

				if (service.equals("UpdateList")) {
					System.out.println("I was called : Update");
					// this.listUpdate();
					listOfUser = (Object[]) fromServer.readObject();
					for (Object s : listOfUser) {

						if (!s.equals(currentUser)) {
							cbUser.addItem(s);
						}
					}
				}

			}

		}

		public void listUpdate() throws ClassNotFoundException, IOException {
			listOfUser = (Object[]) fromServer.readObject();
			for (Object s : listOfUser) {

				if (!s.equals(currentUser))
					cbUser.addItem(s);
			}
		}

		public void addMe() throws IOException {
			toServer.writeObject("AddUser");
			toServer.writeObject(currentUser);
		}
		
		static void sendMessage() throws IOException {
			// TODO Auto-generated method stub
			char nl = '\n';
			toServer.writeObject("PM");
			toServer.writeObject(currentUser);
			toServer.writeObject(cbUser.getSelectedItem());
			System.out.println(cbUser.getSelectedItem());
			toServer.writeObject(txtField.getText());
			txtArea.append(currentUser + ": " + (String) txtField.getText()
					+ nl);
			txtField.setText("");
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				getMessage();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
