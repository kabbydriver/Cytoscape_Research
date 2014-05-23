package mySimpleApp;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;

import java.awt.event.ActionEvent;
import java.util.*;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.model.*;
import org.cytoscape.view.model.*;

public class MenuAction extends AbstractCyAction 
{    
  private static final long serialVersionUID = 1L;
  private final CyAppAdapter adapter;
  private SparseDoubleMatrix2D adjacency;
  private SparseDoubleMatrix2D degree;
  private SparseDoubleMatrix2D laplacian;
  private SparseDoubleMatrix2D inverse;
  private int size = 0;

  public MenuAction(CyAppAdapter adapter) {
    super("laplacianv1.6.1",
	adapter.getCyApplicationManager(),
	"network",
	adapter.getCyNetworkViewManager());
    this.adapter = adapter;
    setPreferredMenu("Apps");
  }



  public void getLaplacian( CyNetwork network, List<CyNode> nodeList )
  {
    degree = new SparseDoubleMatrix2D(size,size);
    adjacency = new SparseDoubleMatrix2D( size, size );
    laplacian = new SparseDoubleMatrix2D( size, size );

    //for testing execution time
    long startTime = System.currentTimeMillis();

    for( int i =0; i < size; i++ )
    {
      degree.setQuick(i, i, network.getNeighborList( nodeList.get(i), CyEdge.Type.ANY).size() );
      CyNode currentNode = nodeList.get(i);
      
      for( int j = 0; j < size; j++ )
      {
    	CyNode targetNode = nodeList.get(j);
	    if( network.containsEdge( currentNode, targetNode ) )
      	{
	      adjacency.setQuick(i, j, 1 );
	    }
	    else
     	{
	      adjacency.setQuick(i, j, 0);
	    }
        
	    //laplacian matrix = degree - adjacency
	    double laplacianValue = degree.getQuick(i, j) - adjacency.getQuick(i, j);
	    laplacian.setQuick(i, j, laplacianValue);
      }
    }

    //done traversing graph
    long endTime = System.currentTimeMillis();
    System.out.println( "Total execution time: " + ((endTime - startTime)/1000.0) );
  }

  public void newNetworkView( CyApplicationManager manager )
  {
	
    CyNetworkManager myNetworkManager = adapter.getCyNetworkManager();
    CyNetworkViewManager myNetworkViewManager = adapter.getCyNetworkViewManager();
    CyNetworkFactory networkFactory = adapter.getCyNetworkFactory();
    CyTable myTable = manager.getCurrentTable();
    CyTableManager myTableManager = adapter.getCyTableManager();
    myTableManager.addTable( myTable );
	    
	CyNetwork myNetwork = networkFactory.createNetwork();
    myNetworkManager.addNetwork( myNetwork );
	    	    
	CyNetworkView myNetworkView = adapter.getCyNetworkViewFactory().createNetworkView( myNetwork );
	myNetworkViewManager.addNetworkView( myNetworkView );
	manager.setCurrentNetwork( myNetwork );
	    
	manager.setCurrentTable( myTable );
	manager.setCurrentNetworkView( myNetworkView );
	myNetworkView.updateView();
  }
  public void actionPerformed(ActionEvent e) 
  {    
    final CyApplicationManager manager = adapter.getCyApplicationManager();
   
    //final CyNetworkView networkView = manager.getCurrentNetworkView();
    final CyNetwork network = manager.getCurrentNetwork();
    if( network != null )
    {
    	newNetworkView( manager );
    }
    //networkView.updateView();
    
   
    
    List<CyNode> nodeList = network.getNodeList();
    size = nodeList.size();
    getLaplacian(network, nodeList);
    
  
    
    /*inverse = alg.inverse(laplacian);
    System.out.println( degree );
    System.out.println();
    System.out.println( adjacency );
    System.out.println();
    System.out.println( laplacian );*/
  }
}  


