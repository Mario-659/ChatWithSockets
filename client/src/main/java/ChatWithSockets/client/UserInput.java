package ChatWithSockets.client;

import java.util.Scanner;

public class UserInput extends Thread{
    Controller controller;

    public UserInput(Controller controller){
        this.controller = controller;
    }

    @Override
    public void run(){
        Scanner scanner = new Scanner(System.in);
        String input;
        while(true){
            input = scanner.nextLine();
            controller.processInput(input);
        }
    }
}
