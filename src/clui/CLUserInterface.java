package clui;

import java.io.IOException;

import core.*;
import exceptions.*;

/**
 * Command line user interface.
 * A program using VFSApp.
 * @author ayoub
 *
 */
public class CLUserInterface {
	/** current cuser command */
	static String command;
	/** standard input scanner */
	static java.util.Scanner input = null;
	/** {@link VFSApp} instance */
	static VFSApp vfsApp = null;
	/** prompt string */
	static String prompt = "";
	/** show prompt boolean */
	static boolean showPrompt = false;
	/** string tokenizer to split input string */
	static java.util.StringTokenizer st = null;
	
	public static void main(String[] args) {
		try{
			input = new java.util.Scanner(System.in);
			vfsApp = new VFSApp();
			Runtime.getRuntime().addShutdownHook((new OnExit()));
			if(!vfsApp.getVFSDataBase().isEmpty()){
				showPrompt = true;
				prompt = vfsApp.getPrompt(vfsApp.getVFSDataBase().get(0).name);
			}
				
		} catch (RuntimeException e) {
			System.out.println(e.getClass().getSimpleName()+"\n"+e.getMessage());
			System.exit(0);
		}
		do{
			try{
				if(showPrompt){
					prompt = vfsApp.getPrompt();
					System.out.print(prompt);
				}
				String lineIn = input.nextLine();
				st = new java.util.StringTokenizer(lineIn);
				
				if(!st.hasMoreTokens()) continue;
				
				command = st.nextToken();
				
				if(command.equalsIgnoreCase("ls")) ls();
		
				else if(command.equalsIgnoreCase("cd")) cd();
				
				else if(command.equalsIgnoreCase("md")) md();
				
				else if(command.equalsIgnoreCase("mf")) mf();
				
				else if(command.equalsIgnoreCase("mv")) mv();

				else if(command.equalsIgnoreCase("cp")) cp();
				
				else if(command.equalsIgnoreCase("rm")) rm();
				
				else if(command.equalsIgnoreCase("crvfs")) crvfs();
				
				else if(command.equalsIgnoreCase("rmvfs")) rmvfs();
				
				else if(command.equalsIgnoreCase("format")) format();
				
				else if(command.equalsIgnoreCase("impvfs")) impvfs();
				
				else if(command.equalsIgnoreCase("expvfs")) expvfs();
				
				else if(command.equalsIgnoreCase("free")) free();
				
				else if(command.equalsIgnoreCase("tree")) tree();
					
				else if(command.equalsIgnoreCase("find")) find();
				
				else if(command.equalsIgnoreCase("help")) help();
				
				else if(command.equals("prompt")) prompt();
				
				else if(command.equals("exit")) break;
				
				else{
					if(st.hasMoreTokens())
						throw new InvalidCommandException("\""+command + "\" is not a valid command");
					if(showPrompt){
						vfsApp.changeDir("", command);
						prompt = vfsApp.getPrompt("");
					}
				}

			} catch (InvalidCommandException e){
				System.out.println("Syntax error.\n"+e.getMessage());
			} catch (VFSOperationFailException e){
				System.out.println("Operation failed.\n"+e.getMessage());
			} catch (NameAlreadyExistsException e){
				System.out.println("Name already exists.\n"+e.getMessage());
			} catch (NoFreeSpaceLeftException e){
				System.out.println("No free space left.\n"+e.getMessage());
			} catch (NoSuchDirectoryException e){
				System.out.println("No such directory.\n"+e.getMessage());
			} catch (NoSuchFileException e){
				System.out.println("No such file.\n"+e.getMessage());
			} catch (NoSuchFileOrDirectoryException e){
				System.out.println("No such file or directory.\n"+e.getMessage());
			} catch (NoSuchVirtualFileSystemException e){
				System.out.println("No such virtual file system.\n"+e.getMessage());
			} catch (SizeNotAllowedException e){
				System.out.println("Dimension not allowed.");
			}
			
		}while(!command.equalsIgnoreCase("exit"));	

	}
	

	/**
	 * list
	 */
	static void ls(){
		if(st.countTokens()>3)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(showPrompt){
			if(st.countTokens()==0)
				System.out.print(vfsApp.list("", "", ""));
			else if(st.countTokens()==1){
				String nextArg = st.nextToken();
				if(nextArg.equals("-l"))
					System.out.print(vfsApp.list("","-l",""));
				else
					System.out.print(vfsApp.list("","",nextArg));
			}
			else if(st.countTokens()==2){
				System.out.print(vfsApp.list("",st.nextToken(),st.nextToken()));
			}
			else if(st.countTokens()==3)
				System.out.print(vfsApp.list(st.nextToken(),st.nextToken(),st.nextToken()));
		}
		else{
			if(st.countTokens()==0)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			else if(st.countTokens()==1)
				System.out.print(vfsApp.list(st.nextToken(), "", ""));
			else if(st.countTokens()==2){
				String vfsName = st.nextToken();
				String nextArg = st.nextToken();
				if(nextArg.equals("-l"))
					System.out.print(vfsApp.list(vfsName,"-l",""));
				else
					System.out.print(vfsApp.list(vfsName,"",nextArg));
			}
			else if(st.countTokens()==3){
				System.out.print(vfsApp.list(st.nextToken(),st.nextToken(),st.nextToken()));
			}
		}
	}
	
	
	/**
	 * change dir
	 */
	static void cd(){
		if(st.countTokens()>2)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(showPrompt){
			if(st.countTokens()<1)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			if(st.countTokens()==2)
				vfsApp.changeDir(st.nextToken(), st.nextToken());
			else vfsApp.changeDir("", st.nextToken());
		}
		else{
			if(st.countTokens()<2)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			vfsApp.changeDir(st.nextToken(), st.nextToken());
		}
		prompt = vfsApp.getPrompt("");
	}
	
	
	/**
	 * make dir
	 */
	static void md(){
		if(st.countTokens()>2)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(showPrompt){
			if(st.countTokens()<1)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			if(st.countTokens()==2)
				vfsApp.makeDir(st.nextToken(), st.nextToken());
			else vfsApp.makeDir("", st.nextToken());
		}
		else{
			if(st.countTokens()<2)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			vfsApp.makeDir(st.nextToken(), st.nextToken());
		}
	}
	
	
	/**
	 * make file
	 */
	static void mf(){
		if(st.countTokens()>3)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(showPrompt){
			if(st.countTokens()<2)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			if(st.countTokens()==3)
				vfsApp.makeFile(st.nextToken(), st.nextToken(), st.nextToken());
			else vfsApp.makeFile("", st.nextToken(),st.nextToken());
		}
		else{
			if(st.countTokens()<2)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			vfsApp.makeFile(st.nextToken(), st.nextToken(),st.nextToken());
		}
	}
	
	
	/**
	 * move
	 */
	static void mv(){
		String vfsName,oldPath,newPath;
		if(st.countTokens()>3)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(showPrompt){
			if(st.countTokens()<2)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			if(st.countTokens()==3){
				vfsName = st.nextToken();
				oldPath = st.nextToken();
				newPath = st.nextToken();
			}
			else{
				vfsName = "";
				oldPath = st.nextToken();
				newPath = st.nextToken();
			}
		}
		else{
			if(st.countTokens()<3)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			vfsName = st.nextToken();
			oldPath = st.nextToken();
			newPath = st.nextToken();
		}
		try{
			vfsApp.move(vfsName, oldPath, newPath);
		} catch (NameAlreadyExistsException e){
			System.out.println(e.getMessage());
			System.out.print("do you want to overwrite it? [y|n] ");
			String lineIn;
			do{
				lineIn = input.nextLine();
			}while(!lineIn.equalsIgnoreCase("y") && !lineIn.equalsIgnoreCase("n"));
			if(lineIn.equalsIgnoreCase("y")){
				vfsApp.moveAndOverwrite(vfsName, oldPath, newPath);
			}
		}
	}
	
	
	/**
	 * copy
	 */
	static void cp(){
		String vfsName,oldPath,newPath;
		if(st.countTokens()>3)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(showPrompt){
			if(st.countTokens()<2)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			if(st.countTokens()==3){
				vfsName = st.nextToken();
				oldPath = st.nextToken();
				newPath = st.nextToken();
			}
			else{
				vfsName = "";
				oldPath = st.nextToken();
				newPath = st.nextToken();
			}
		}
		else{
			if(st.countTokens()<3)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			vfsName = st.nextToken();
			oldPath = st.nextToken();
			newPath = st.nextToken();
		}
		try{
			vfsApp.copy(vfsName, oldPath, newPath);
		} catch (NameAlreadyExistsException e){
			System.out.println(e.getMessage());
			System.out.print("do you want to overwrite it? [y|n] ");
			String lineIn;
			do{
				lineIn = input.nextLine();
			}while(!lineIn.equalsIgnoreCase("y") && !lineIn.equalsIgnoreCase("n"));
			if(lineIn.equalsIgnoreCase("y")){
				vfsApp.copyAndOverwrite(vfsName, oldPath, newPath);
			}
		}
	}
	
	
	/**
	 * remove
	 */
	static void rm(){
		if(st.countTokens()>2)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(showPrompt){
			if(st.countTokens()<1)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			if(st.countTokens()==2)
				vfsApp.remove(st.nextToken(), st.nextToken());
			else vfsApp.remove("", st.nextToken());
		}
		else{
			if(st.countTokens()<2)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			vfsApp.remove(st.nextToken(), st.nextToken());
		}
	
	}
	
	
	/**
	 * create new virtual file system
	 */
	static void crvfs(){
		if(st.countTokens()<2)
			throw new InvalidCommandException("\""+command+"\" needs more arguments");
		vfsApp.createVFS(st.nextToken(), Long.parseLong(st.nextToken()));
	}
	
	
	/**
	 * remove virtual file system
	 */
	static void rmvfs(){
		if(st.countTokens()>1)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(st.countTokens()==0 && showPrompt){
			showPrompt = false;
			prompt = "";
			vfsApp.removeVFS("");
			return;
		}
		else if(st.countTokens()==0 && !showPrompt)
			throw new InvalidCommandException("\""+command+"\" needs more arguments");
		String vfsName = st.nextToken();
		if(vfsName.equals(vfsApp.getCurrentWorkingVFS().name)){
			showPrompt = false;
			prompt = "";
		}
		vfsApp.removeVFS(vfsName);
	}
	
	
	
	/**
	 * format virtual file system
	 */
	static void format(){
		if(st.countTokens()>1)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		String vfsName;
		if(st.countTokens()==0 && showPrompt){
			vfsName = "";
		}
		else if(st.countTokens()==0 && !showPrompt)
			throw new InvalidCommandException("\""+command+"\" needs more arguments");
		else vfsName = st.nextToken();
		if(vfsName.equals(vfsApp.getCurrentWorkingVFS().name)){
			showPrompt = false;
			prompt = "";
		}
		vfsApp.formatVFS(vfsName);
	}
	

	
	/**
	 * import to virtual file system
	 */
	static void impvfs(){
		if(st.countTokens()>3)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(showPrompt) {
			if(st.countTokens()<1)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			if(st.countTokens()>2)
				throw new InvalidCommandException("\""+command+"\" cannot take more than 2 arguments while prompt is shown.");
			if(st.countTokens()==1)
				vfsApp.importToVFS(st.nextToken(),"", ".");
			else
				vfsApp.importToVFS(st.nextToken(),"", st.nextToken());
		}
		else {
			if(st.countTokens()<3)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			vfsApp.importToVFS(st.nextToken(), st.nextToken(),st.nextToken());
		}
	}
	
	
	/**
	 * export virtual file system
	 */
	static void expvfs(){
		if(st.countTokens()>2)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(showPrompt){
			if(st.countTokens()>1)
				throw new InvalidCommandException("\""+command+"\" takes less arguments");
			if(st.countTokens()==0)
				vfsApp.exportVFS("", System.getProperty("user.dir"));
			else
				vfsApp.exportVFS("", st.nextToken());
		}
		else{
			if(st.countTokens()<1)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			if(st.countTokens()==1)
				vfsApp.exportVFS(st.nextToken(), System.getProperty("user.dir"));
			else
				vfsApp.exportVFS(st.nextToken(),st.nextToken());
		}
	}
	
	
	/**
	 * free/occupied space
	 */
	static void free(){
		if(st.countTokens()>1)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(showPrompt){
			if(st.hasMoreTokens())
				System.out.println(vfsApp.free(st.nextToken()));
			else System.out.println(vfsApp.free(""));
		}
		else{
			if(st.countTokens()<1)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			System.out.println(vfsApp.free(st.nextToken()));
		}
	}
	
	
	/**
	 * tree 
	 */
	static void tree(){
		if(st.countTokens()>1)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(showPrompt){
			if(st.hasMoreTokens()) System.out.print(vfsApp.tree(st.nextToken()));
			else System.out.print(vfsApp.tree(""));
		}
		else{
			if(st.countTokens()<1)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			System.out.print(vfsApp.tree(st.nextToken()));
		}
	}
	
	
	/**
	 * find
	 */
	static void find(){
		if(st.countTokens()>2)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(showPrompt){
			if(st.countTokens()<1)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			if(st.countTokens()==2)
				System.out.print(vfsApp.find(st.nextToken(), st.nextToken()));
			else System.out.print(vfsApp.find("", st.nextToken()));
		}
		else{
			if(st.countTokens()<2)
				throw new InvalidCommandException("\""+command+"\" needs more arguments");
			System.out.print(vfsApp.find(st.nextToken(), st.nextToken()));
		}
	}
	
	
	/**
	 * activate/deactivate prompt
	 */
	static void prompt(){
		if(st.countTokens()==0){
			prompt = "";
			showPrompt = false;
			return;
		}
		if(st.countTokens()>1)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		String nextArg = st.nextToken();
		if(nextArg.equals("-off")){
			prompt = "";
			showPrompt = false;
		}
		else{
			prompt = vfsApp.getPrompt(nextArg);
			showPrompt = true;
		}	
	}
	
	
	/**
	 * help
	 */
	static void help(){
		if(st.countTokens()>1)
			throw new InvalidCommandException("\""+command+"\" takes less arguments");
		if(st.countTokens()==1){
			String commandHelp = st.nextToken();
			int lineOfCommand = 1;
			int startLine = 1;
			if(commandHelp.equalsIgnoreCase("prompt")) lineOfCommand = 1+startLine;
			else if(commandHelp.equalsIgnoreCase("ls")) lineOfCommand = 2+startLine;
			else if(commandHelp.equalsIgnoreCase("cd")) lineOfCommand = 3+startLine;
			else if(commandHelp.equalsIgnoreCase("mv")) lineOfCommand = 4+startLine;
			else if(commandHelp.equalsIgnoreCase("cp")) lineOfCommand = 5+startLine;
			else if(commandHelp.equalsIgnoreCase("rm")) lineOfCommand = 6+startLine;
			else if(commandHelp.equalsIgnoreCase("crvfs")) lineOfCommand = 7+startLine;
			else if(commandHelp.equalsIgnoreCase("rmvfs")) lineOfCommand = 8+startLine;
			else if(commandHelp.equalsIgnoreCase("impvfs")) lineOfCommand = 9+startLine;
			else if(commandHelp.equalsIgnoreCase("expvfs")) lineOfCommand = 10+startLine;
			else if(commandHelp.equalsIgnoreCase("free")) lineOfCommand = 11+startLine;
			else if(commandHelp.equalsIgnoreCase("tree")) lineOfCommand = 12+startLine;
			else if(commandHelp.equalsIgnoreCase("find")) lineOfCommand = 13+startLine;
			else if(commandHelp.equalsIgnoreCase("help")) lineOfCommand = 14+startLine;
			else if(commandHelp.equalsIgnoreCase("md")) lineOfCommand = 15+startLine;
			else if(commandHelp.equalsIgnoreCase("mf")) lineOfCommand = 16+startLine;
			else if(commandHelp.equalsIgnoreCase("format")) lineOfCommand = 17+startLine;
			else throw new InvalidCommandException("\""+commandHelp+"\" is not an available command");
			int countLines = 1;
			java.io.BufferedReader br = null;
			try {
				br = new java.io.BufferedReader(new java.io.FileReader("help.txt"));
				while(true){
					String line = br.readLine();
					if(countLines==lineOfCommand){
						System.out.println(line.substring(1));
						try {
							br.close();
						} catch (IOException e) {
							throw new VFSOperationFailException(e.getMessage());
						}
						return;
					}
					if(line==null) break;
					countLines++;
				}
			} catch (java.io.IOException e) {
				throw new VFSOperationFailException("cannot read help file");
			} finally {
				if(br!=null)
					try {
						br.close();
					} catch (IOException e) {
						throw new VFSOperationFailException(e.getMessage());
					}
			}
		}
		else System.out.print(getHelp());
	}
	
	static String getHelp(){
		String helpText = "";
		java.io.BufferedReader br = null;
		try {
			br = new java.io.BufferedReader(new java.io.FileReader("help.txt"));
			while(true){
				String line = br.readLine();
				if(line==null) break;
				helpText += line + "\n";
			}
		} catch (java.io.IOException e) {
			throw new VFSOperationFailException("cannot read help file");
		} finally {
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					throw new VFSOperationFailException(e.getMessage());
				}
		}
		
		return helpText;
	}

}
