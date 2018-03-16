import javafx.application.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
public class chatPane extends VBox{
	private chat chat;
	private VBox messages;
	private TextField text;
	private Button send;

	//mettre directement la feuille de style alert.java et ajouter juste les détails pour limiter les disparitées?
	public chatPane(){
		super();
		//getStylesheets().add("chat.css");
		messages=new VBox();
		text=new TextField();
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
		bas.setAlignment(Pos.BOTTOM_CENTER);
		bas.getChildren().addAll(text,send);
		/*send.setLayoutX(250);
		send.setLayoutY(250);*/
		getChildren().addAll(messages,bas);
	}

	public void initHost(String name){
		chat.initHost(name);
	}

	public void initClient(String name, String addr){
		chat.initClient(name,addr);
	}


	public void addMessage(String str){
		Label lab = new Label(str);
		//mettre id pour label
		messages.getChildren().add(lab);
	}


}