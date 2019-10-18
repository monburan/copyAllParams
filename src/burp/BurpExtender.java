package burp;

import java.util.List;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.PrintStream;
import java.io.OutputStream;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;


public class BurpExtender implements IBurpExtender, IContextMenuFactory {
	
	private PrintStream logStream;
	private IExtensionHelpers helpers;
	private final static String ExtenderName = "Copy All Params";
	private final static String ExtenderVersion = "V0.1";
	
	
	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		
		helpers = callbacks.getHelpers();
		callbacks.setExtensionName(ExtenderName);
		callbacks.registerContextMenuFactory(this);
		callbacks.printOutput(ExtenderName);
		callbacks.printOutput("Version: " + ExtenderVersion);

        OutputStream stdOut = callbacks.getStdout();
        logStream = new PrintStream(stdOut);
	}
	
    private void Outputlog(String text) {
        logStream.println(text);
        logStream.flush();
    }
	
	@Override
	public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation){
		IHttpRequestResponse[] messages = invocation.getSelectedMessages();
		if (messages.length == 0 | messages == null) {
			return null;
		}
		JMenuItem acttion1 = new JMenuItem("Copy all params to clipboard");
		acttion1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				copyAllParams(messages);
			}
		});
		return Arrays.asList(acttion1);
		
	}
	
	private List<IParameter> getRequestParameters(IHttpRequestResponse message) {
		
		List<IParameter> parameters = helpers.analyzeRequest(message).getParameters();
		
		return parameters;
		
	}
	
	public static void setclipboardString(String paramnames) {
		
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		 
		Transferable trans = new StringSelection(paramnames);
	    
		clipboard.setContents(trans, null);
		
	}
	
	private void copyAllParams(IHttpRequestResponse[] messages) {
		try {
			for(IHttpRequestResponse message : messages) {
				
				List<String> pnamelist = new ArrayList<>();
				List<IParameter> params = getRequestParameters(message);
				for (IParameter param : params) {
					pnamelist.add(param.getName());
				}
				String paramnames = String.join(",", pnamelist);
				Outputlog("[!]get all param name: " + paramnames);
				setclipboardString(paramnames);
				
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}

