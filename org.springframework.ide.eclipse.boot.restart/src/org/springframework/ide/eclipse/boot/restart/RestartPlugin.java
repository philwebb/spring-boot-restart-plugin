package org.springframework.ide.eclipse.boot.restart;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class RestartPlugin extends AbstractUIPlugin {

	@Override
	protected ImageRegistry createImageRegistry() {
		return RestartPluginImages.initializeImageRegistry();
	}

}
