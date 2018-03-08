import javafx.application.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
public class chatPane extends VBox{
	private chat chat;
	private VBox messages;
	private TextField text;
	private Button send;

	public chatPane(){
		super();
		messages=new VBox();
		text=new TextField();
		send = new Button();
		send.setText("Send");
		chat = new chat(this);
		send.setOnMouseClicked(e->{
			chat.sendMessage(text.getText());
			text.setText("");
		});
		HBox bas = new HBox();
		bas.getChildren().addAll(text,send);
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
		messages.getChildren().add(lab);
	}


}