package org.springframework.ide.eclipse.boot.restart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.texteditor.IUpdate;

@SuppressWarnings("restriction")
public class RestartAction extends Action implements IUpdate {

	private static final String[] CLASSPATH_PREFIX = { "-classpath ", "-cp " };

	private ProcessConsole console;

	public RestartAction(ProcessConsole console) {
		super("Trigger Restart");
		setToolTipText("Trigger Restart of Spring Boot Application");
		setImageDescriptor(RestartPluginImages
				.getImageDescriptor(RestartConstants.IMG_RESTART_ICON));
		this.console = console;
		update();
	}

	public void update() {
		setEnabled(!this.console.getProcess().isTerminated());
	}

	@Override
	public void run() {
		if (!this.console.getProcess().isTerminated()) {
			IProcess process = this.console.getProcess();
			String commandLine = process
					.getAttribute("org.eclipse.debug.core.ATTR_CMDLINE");
			String classPath = getClassPath(commandLine);
			File folder = getFolder(classPath);
			if (folder != null) {
				writeTriggerFile(new File(folder, ".reloadtrigger"));
				System.out.println(folder);
			}
		}
	}

	private String getClassPath(String commandLine) {
		for (String prefix : CLASSPATH_PREFIX) {
			int startIndex = commandLine.indexOf(prefix);
			if (startIndex != -1) {
				return commandLine.substring(startIndex + prefix.length()).trim();
			}
		}
		return null;
	}

	private File getFolder(String classPath) {
		if (classPath == null) {
			return null;
		}
		int index = classPath.indexOf(File.pathSeparator);
		String element = (index == -1 ? classPath : classPath.substring(0, index));
		if ("".equals(element)) {
			return null;
		}
		File file = new File(element);
		if (file.isDirectory() && file.exists()) {
			return file;
		}
		return (index == -1 ? null : getFolder(classPath.substring(index + 1)));
	}

	private void writeTriggerFile(File file) {
		try {
			OutputStream outputStream = new FileOutputStream(file);
			try {
				Date date = new Date();
				String content = date.toString() + " " + date.getTime();
				outputStream.write(content.getBytes());
			}
			finally {
				outputStream.close();
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException("Unable to write trigger file", ex);
		}
	}

	public void dispose() {
		this.console = null;
	}

}
