package mySimpleApp;

import org.cytoscape.app.swing.AbstractCySwingApp;
import org.cytoscape.app.swing.CySwingAppAdapter;

public class HideSingletonNodesApp extends AbstractCySwingApp 
{
	public HideSingletonNodesApp(CySwingAppAdapter adapter)
	{
		super(adapter);
		adapter.getCySwingApplication().addAction(new MenuAction(adapter));
	}
}