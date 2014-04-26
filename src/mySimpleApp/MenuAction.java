package mySimpleApp;

import Jama.Matrix;
import cern.colt.matrix.*;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.*;

import java.awt.event.ActionEvent;
import java.util.*;
import java.awt.color.*;

import javax.swing.JOptionPane;

import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.model.*;
import org.cytoscape.view.model.CyNetworkViewFactory;

public class MenuAction extends AbstractCyAction 
{    
  private static final long serialVersionUID = 1L;
  private final CyAppAdapter adapter;
  private DoubleMatrix2D adjacency;
  private DoubleMatrix2D degree;
  private DoubleMatrix2D laplacian;
  private DoubleMatrix2D inverse;
  private Algebra alg = new Algebra(); //Use algebra object for linalg calculations on matrix
  private int size;

  public MenuAction(CyAppAdapter adapter) {
    super("laplacianv1",
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
      degree.setQuick(i,i, network.getNeighborList( nodeList.get(i), CyEdge.Type.ANY).size());

      for( int j = 0; j < size; j++ )
      {
	if( degree.getQuick(i, j) > 0 )
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
    System.out.println( "Total execution time: " + (endTime - startTime ) );
  }


  public void actionPerformed(ActionEvent e) 
  {    
    final CyApplicationManager manager = adapter.getCyApplicationManager();
    final CyNetworkView networkView = manager.getCurrentNetworkView();
    final CyNetwork network = networkView.getModel();
    networkView.updateView();
    List<CyNode> nodeList = network.getNodeList();
    size = nodeList.size();
    getLaplacian(network, nodeList);
    inverse = alg.inverse(laplacian);
    System.out.println( adjacency );

  }
}  


