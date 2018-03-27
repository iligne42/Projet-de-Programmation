import javafx.application.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.LinkedList;

public class chatPane extends BorderPane{
	private chat chat;
	private VBox messages;
	private TextField text;
	private Button send;

	public void close(){
		chat.close();
	}

	public chatPane(){
		super();
		//getStylesheets().add("chat.css");
		messages=new VBox();
		text=new TextField();
		text.getStyleClass().add("text");
		send = new Button();
		//send.setAlignment(Pos.BOTTOM_RIGHT); //cela me bouge la partie écrite à l'intérieur du bouton
		send.setText("Send");
		chat = new chat(this);
		send.setOnMouseClicked(e->{
			chat.sendMessage(text.getText());
			text.setText("");
		});
		HBox bas = new HBox();
		bas.getStyleClass().add("hbox");
		//bas.setAlignment(Pos.BOTTOM_CENTER);
		bas.getChildren().addAll(text,send);
		setBottom(bas);
		//setAlignment(bas,Pos.BOTTOM_CENTER);
		setTop(messages);
		//bas.setTranslateX(100);
		/*send.setLayoutX(250);
		send.setLayoutY(250);*/
		//getChildren().addAll(messages,bas);
		this.setOnKeyPressed(event -> {
			if(event.getCode()== KeyCode.ENTER){
				chat.sendMessage(text.getText());
				text.setText("");
			}
		});
	}

	public void initHost(String name){
		chat.initHost(name);
	}

	public void initClient(String name, String addr){
		chat.initClient(name,addr);
	}


	public void addMessage(String str){
		Label lab = new Label(str);
		lab.getStyleClass().add("messages");
		messages.getChildren().add(lab);
	}

	public void styleMessage(String str){
		String debut="";
		String fin="";
		boolean flag = true;
		for(int i=0; i<str.length(); i++){
			if(flag){
				if(str.charAt(i)==':') flag=false;
				debut+=str.charAt(i)+"";
			}else fin+=str.charAt(i)+"";
		}
		Label deb=new Label(debut);
		deb.getStyleClass().add("deb");
		Label end=new Label(fin);
		end.getStyleClass().add("end");
		HBox align=new HBox();
		align.getChildren().addAll(deb,end);
		messages.getChildren().add(align);
	}

	public void miseAjour(){
		messages.getChildren().clear();
		LinkedList<String> m=chat.getMessages();
		for(int i=Math.max(0,m.size()-20);i<m.size();i++ ){
			styleMessage(m.get(i));
		}
	}
}