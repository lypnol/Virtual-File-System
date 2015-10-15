package clui;


public class OnExit extends Thread {
	@Override
	public void run(){
		CLUserInterface.vfsApp.saveAllVFS();
	}
}