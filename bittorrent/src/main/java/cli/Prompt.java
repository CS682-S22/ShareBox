package cli;

import java.util.Scanner;

/**
 * @author Alberto Delgado on 5/17/22
 * @project bittorrent
 */
public class Prompt {
    Scanner scanner = new Scanner(System.in);
   
    String readLine(String msg) {
        System.out.println(msg);
        return scanner.nextLine().trim();
    }
}
