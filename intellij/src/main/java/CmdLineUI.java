import java.io.PrintStream;
import java.util.Scanner;

public class CmdLineUI implements UI{
    private final Scanner iscanner = new Scanner(System.in);
    private final PrintStream ostream = System.out;
    private Listener listener;

    public CmdLineUI() { }

    @Override
    public void setListener(Listener listener) { this.listener = listener; }

    @Override
    public void runMainMenu() {
        this.ostream.print("~~~~~~ Welcome to EcoSCOOP! ~~~~~~\n\n");

    }

    @Override
    public void runClickArticle(Article article) {

    }

    @Override
    public void runCancel(){

    }

}
