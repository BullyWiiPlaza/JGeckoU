package components;

/**
 * This application that requires the following additional files:
 * TreeDemoHelp.html
 * arnold.html
 * bloch.html
 * chan.html
 * jls.html
 * swingtutorial.html
 * tutorial.html
 * tutorialcont.html
 * vm.html
 */

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class TreeDemo extends JPanel
		implements TreeSelectionListener
{
	private JEditorPane htmlPane;
	private JTree tree;
	private URL helpURL;
	private static boolean DEBUG = false;

	//Optionally play with line styles.  Possible values are
	//"Angled" (the default), "Horizontal", and "None".
	private static boolean playWithLineStyle = false;
	private static String lineStyle = "Horizontal";

	public TreeDemo()
	{
		super(new GridLayout(1, 0));

		//Create the nodes.
		DefaultMutableTreeNode top =
				new DefaultMutableTreeNode("The Java Series");
		createNodes(top);

		//Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode
				(TreeSelectionModel.SINGLE_TREE_SELECTION);

		//Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		if (playWithLineStyle)
		{
			System.out.println("line style = " + lineStyle);
			tree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		//Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);

		//Create the HTML viewing pane.
		htmlPane = new JEditorPane();
		htmlPane.setEditable(false);
		initHelp();
		JScrollPane htmlView = new JScrollPane(htmlPane);

		//Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(htmlView);

		Dimension minimumSize = new Dimension(100, 50);
		htmlView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(100);
		splitPane.setPreferredSize(new Dimension(500, 300));

		//Add the split pane to this panel.
		add(splitPane);
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				tree.getLastSelectedPathComponent();

		if (node == null)
		{
			return;
		}

		Object nodeInfo = node.getUserObject();
		if (node.isLeaf())
		{
			BookInfo book = (BookInfo) nodeInfo;
			displayURL(book.bookURL);
			if (DEBUG)
			{
				System.out.print(book.bookURL + ":  \n    ");
			}
		} else
		{
			displayURL(helpURL);
		}
		if (DEBUG)
		{
			System.out.println(nodeInfo.toString());
		}
	}

	private class BookInfo
	{
		public String bookName;
		public URL bookURL;

		public BookInfo(String book, String filename)
		{
			bookName = book;
			bookURL = getClass().getResource(filename);
			if (bookURL == null)
			{
				System.err.println("Couldn't find file: "
						+ filename);
			}
		}

		public String toString()
		{
			return bookName;
		}
	}

	private void initHelp()
	{
		String s = "TreeDemoHelp.html";
		helpURL = getClass().getResource(s);
		if (helpURL == null)
		{
			System.err.println("Couldn't open help file: " + s);
		} else if (DEBUG)
		{
			System.out.println("Help URL is " + helpURL);
		}

		displayURL(helpURL);
	}

	private void displayURL(URL url)
	{
		try
		{
			if (url != null)
			{
				htmlPane.setPage(url);
			} else
			{ //null url
				htmlPane.setText("File Not Found");
				if (DEBUG)
				{
					System.out.println("Attempted to display a null URL.");
				}
			}
		} catch (IOException e)
		{
			System.err.println("Attempted to read a bad URL: " + url);
		}
	}

	private void createNodes(DefaultMutableTreeNode top)
	{
		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode book = null;

		category = new DefaultMutableTreeNode("Books for Java Programmers");
		top.add(category);

		//original Tutorial
		book = new DefaultMutableTreeNode(new BookInfo
				("The Java Tutorial: A Short Course on the Basics",
						"tutorial.html"));
		category.add(book);

		//Tutorial Continued
		book = new DefaultMutableTreeNode(new BookInfo
				("The Java Tutorial Continued: The Rest of the JDK",
						"tutorialcont.html"));
		category.add(book);

		//JFC Swing Tutorial
		book = new DefaultMutableTreeNode(new BookInfo
				("The JFC Swing Tutorial: A Guide to Constructing GUIs",
						"swingtutorial.html"));
		category.add(book);

		//Bloch
		book = new DefaultMutableTreeNode(new BookInfo
				("Effective Java Programming Language Guide",
						"bloch.html"));
		category.add(book);

		//Arnold/Gosling
		book = new DefaultMutableTreeNode(new BookInfo
				("The Java Programming Language", "arnold.html"));
		category.add(book);

		//Chan
		book = new DefaultMutableTreeNode(new BookInfo
				("The Java Developers Almanac",
						"chan.html"));
		category.add(book);

		category = new DefaultMutableTreeNode("Books for Java Implementers");
		top.add(category);

		//VM
		book = new DefaultMutableTreeNode(new BookInfo
				("The Java Virtual Machine Specification",
						"vm.html"));
		category.add(book);

		//Language Spec
		book = new DefaultMutableTreeNode(new BookInfo
				("The Java Language Specification",
						"jls.html"));
		category.add(book);
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI()
	{
		boolean useSystemLookAndFeel = true;

		if (useSystemLookAndFeel)
		{
			try
			{
				UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e)
			{
				System.err.println("Couldn't use system look and feel.");
			}
		}

		//Create and set up the window.
		JFrame frame = new JFrame("TreeDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add content to the window.
		frame.add(new TreeDemo());

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		SwingUtilities.invokeLater(() -> createAndShowGUI());
	}
}