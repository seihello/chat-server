import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class ChatClientHandler extends Thread {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private List clients; 
    private String name;
    public List rejectNames = new ArrayList();
    static List groups = new ArrayList(); 
    static int count = 0; 

    ChatClientHandler(Socket socket, List clients) throws IOException {
	count ++;
	this.socket = socket;
	this.clients = clients;
	setClientName("undefined" + count); 
    }

    public void run() {
	try{
	    open();
	    while(true) {
		String message = receive();
		String[] commands = message.split(" ");
		
		if(commands[0].equalsIgnoreCase("name")) {
		    setClientName(commands[1]); 
		    send(getClientName());
		}
	    }
	} catch(IOException e) {
	    e.printStackTrace();
	} finally{
	    close(); 
	}
    }

    public String getClientName() {
	return name;
    }
 
    public void setClientName(String name) throws IOException {

	for(int i = 0; i < clients.size(); i++) {
	    ChatClientHandler handler = (ChatClientHandler)clients.get(i);
	    if(name.equals(handler.getClientName())) { 
		send("この名前は使用できません.");
		return; 
	    }
	}
	
	for(int i = 0; i < clients.size(); i++) {
	    ChatClientHandler handler = (ChatClientHandler)clients.get(i);
	    for(int j = 0; j < handler.rejectNames.size(); j++) {
		if(handler.rejectNames.get(j).equals(this.name)) {
		    handler.rejectNames.set(j, name);
		}
	    }
	}
	
	this.name = name; 
    }


    void open() throws IOException {
	InputStream socketIn = socket.getInputStream();
	OutputStream socketOut = socket.getOutputStream();
	in = new BufferedReader(new InputStreamReader(socketIn));
	out = new BufferedWriter(new OutputStreamWriter(socketOut));
    }
    
    String receive() throws IOException {
	String line = in.readLine();
	System.out.print(this.name + ": ");
	System.out.println(line);
	return line;
    }
  
    void close() {
	if(in != null) {
	    try{
		in.close();
	    } catch(IOException e) {}
	}
	if(out != null) {
	    try{
		out.close();
	    } catch(IOException e) {}
	}
	if(socket != null) {
	    try{
		socket.close();
	    } catch(IOException e) {}
	}
	System.out.println("クライアントが接続を切断しました.");
    }

}
