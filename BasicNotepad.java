package notepad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import javax.swing.event.*;
import javax.swing.undo.*;

/**
*	@author Sunil Kunnakkat
**/
/*
	Basic notepad application, featuring New, Open, Save & Save As, and
	Undo/Redo features.
*/

public class BasicNotepad extends JFrame implements ActionListener {

	UndoManager undo_manager = new UndoManager();
	
	//All elements that will be used for creating the applet.
	JMenuBar menubar;
	JMenu file, edit, format;
	JMenuItem new_file, open_file, save_file, save_as_file, 
			close_file, undo_item, redo_item;
	JFileChooser file_chooser;
	JTextArea text_area;
	JComboBox font_combobox, font_size_combobox, font_style_combobox;
	ArrayList<String> fonts_sizes;
	JPanel font_panel;
	boolean boldy = false;
	boolean italicy = false;
	String frame_name;


	public BasicNotepad () {
		
		// Create definitions for all elements.
		menubar = new JMenuBar();
		file = new JMenu("File",true);
		edit = new JMenu("Edit",true);
		new_file = new JMenuItem("New");
		open_file = new JMenuItem("Open");
		save_file = new JMenuItem("Save");
		save_as_file = new JMenuItem("Save As");
		close_file = new JMenuItem("Close");
		undo_item = new JMenuItem("Undo");
		redo_item = new JMenuItem("Redo");
		file_chooser = new JFileChooser();
		text_area = new JTextArea();
		frame_name = new String();

		setTitle("Notepad");
		/*
			There are only two major menu items to add to the menu bar.
			Within each one are more menu items.
				File - New, Open, Save, Close
				Edit - Undo, Redo

			Then officially set up the menu bar.
		*/
		menubar.add(file);
		menubar.add(edit);
		setJMenuBar(menubar);
		

		// Set up font for notepad.
		this.text_area.setFont(new Font("Times New Roman", Font.PLAIN, 12));		
				
		/*
			Add ActionListeners for New, Open, Save, Save As and Close.
			Then assign a Keystroke and add each menu item into
			the "File" menu item.
		*/
			new_file.addActionListener(this);
			new_file.setAccelerator( KeyStroke.getKeyStroke("ctrl N") );
			file.add(new_file);
			
			open_file.addActionListener(this);
			open_file.setAccelerator( KeyStroke.getKeyStroke("ctrl O") );
			file.add(open_file);
			
			save_file.addActionListener(this);
			save_file.setAccelerator( KeyStroke.getKeyStroke("ctrl S") );
			file.add(save_file);

			save_as_file.addActionListener(this);
			save_as_file.setAccelerator( KeyStroke.getKeyStroke("ctrl shift S") );
			file.add(save_as_file);
			
			close_file.addActionListener(this);
			close_file.setAccelerator( KeyStroke.getKeyStroke("ctrl Q") );
			file.add(close_file);

		/*
			Add ActionListeners for Undo and Redo.
			Then assign a Keystroke and add each menu item into
			the "Edit" menu item.
		*/
			undo_item.addActionListener(this);
			undo_item.setAccelerator( KeyStroke.getKeyStroke("ctrl Z") );
			edit.add(undo_item);

			redo_item.addActionListener(this);
			redo_item.setAccelerator( KeyStroke.getKeyStroke("ctrl Y") );

			edit.add(redo_item);

		/*
			Disable Undo and Redo, for they should only be enabled once
			there is an action available to undo or redo.
		*/
		undo_item.setEnabled(false);
		redo_item.setEnabled(false);

		/*
			Add an Undoable Edit Listener to the text area for the notepad.
			Within it, add an edit for each change in the text area, and enable
			the Undo and Redo button based on if there is an undoable or redoable
			action available.
		*/
		text_area.getDocument().addUndoableEditListener(
			new UndoableEditListener() {
				public void undoableEditHappened(UndoableEditEvent e) {
					undo_manager.addEdit(e.getEdit());
					undo_item.setEnabled(undo_manager.canUndo());
					redo_item.setEnabled(undo_manager.canRedo());
				}
			});

		/*
			If the user closes the app normally, then the application completely
			closes, rather than just closing down an instance of the app.
		*/
		setDefaultCloseOperation(EXIT_ON_CLOSE);	

		/*
			Set up the BorderLayout to make it fill automatically, then add the
			text area into the content pane.
		*/
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(text_area);

		/*
			700 x 500 size for the applet window.
		*/
		this.setSize(700, 500);
	}

	public void actionPerformed ( ActionEvent event ) {
		/*
			Check the event source for each action performed in the application,
			and confirm what menu item was clicked.
		*/
		if ( event.getSource() == this.save_as_file ) {
	
			if (JFileChooser.APPROVE_OPTION == file_chooser.showSaveDialog(this)) {
				File file = file_chooser.getSelectedFile();
				/*
					First check if the JFileChooser can create the Save dialog. Then create a
					buffered writer for writing the file. Set the title of the frame as
					the name of the file, and close the writer/file stream.

					The string frame_name is used for storing the file name for later.

					Always be sure to catch exceptions.
				*/
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter(file.getPath()));
					setTitle(file.getName());
					frame_name = file.getName();
					out.write(this.text_area.getText());
					out.close();
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		}
		else if ( event.getSource() == this.new_file ) {

			String message = "Do you want to save this file first?";

			/*
				The first step before creating a new file is determining whether
				the user wants to save whatever work has been done.
			*/
			int reply = JOptionPane.showConfirmDialog( null, message, "Save?", JOptionPane.YES_NO_OPTION );
			/*
				If the user chooses the save option, follow the same steps
				as a "Save As" option.

				If he does not, create a new blank space within the text area,
				and rename the title and frame_name to the original "Notepad"
			*/
			if (reply == JOptionPane.YES_OPTION) {

				if (JFileChooser.APPROVE_OPTION == file_chooser.showSaveDialog(this)) {
					File file = file_chooser.getSelectedFile();
					try {
						BufferedWriter out = new BufferedWriter(new FileWriter(file.getPath()));
						out.write(this.text_area.getText());
						out.close();
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}
				}

			}
			else {
				text_area.setText("");
				setTitle("Notepad");
				frame_name = "Notepad";
			}		
		}
		else if ( event.getSource() == this.save_file ) {
			/*
				Check if the title of the frame has "txt" in it based on frame_name,
				indicating there has already been a previous Save As. If so, then you
				can perform the save option based on the file_chooser.
			*/
			if (frame_name.toLowerCase().contains("txt".toLowerCase())) {
				try (FileWriter fw = new FileWriter(file_chooser.getSelectedFile())) {
					fw.write(this.text_area.getText());
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "You must use save as first!", "Error",
										JOptionPane.ERROR_MESSAGE);
			}				
		}		
		else if ( event.getSource() == this.open_file ) {
			/*
				First check if the JFileChooser can create the Open dialog. Reset the 
				text of the file, and read in the selected file to open line by line.

				Be sure to set the title to the name of the new file.
			*/
			if (JFileChooser.APPROVE_OPTION == file_chooser.showOpenDialog(this)) {
				File file = file_chooser.getSelectedFile();
				text_area.setText("");
				Scanner in = null;
				try {
					in = new Scanner(file);
					while(in.hasNext()) {
						String line = in.nextLine();
						text_area.append(line+"\n");
					}
					setTitle(file.getName());
					frame_name = file.getName();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					in.close();
				}
			}
		}
		else if ( event.getSource() == this.close_file ) {
			/*
				If the close button is clicked, close the entire program.
			*/
			System.exit(0);
		}
		/*
			Use the undo manager for both undo and redo features. You can undo
			or redo one character change at a time.
		*/
		else if ( event.getSource() == this.undo_item ) {
			try {
				undo_manager.undo();
			} catch (CannotRedoException cre) {
				cre.printStackTrace();
			}
			undo_item.setEnabled(undo_manager.canUndo());
			redo_item.setEnabled(undo_manager.canRedo());
		}
		else if ( event.getSource() == this.redo_item ) {
			try {
				undo_manager.redo();
			} catch (CannotRedoException cre) {
				cre.printStackTrace();
			}
			undo_item.setEnabled(undo_manager.canUndo());
			redo_item.setEnabled(undo_manager.canRedo());
		}

	}


	public static void main(String[] args) {
		/*
			Initialize the BasicNotepad class and set it to be visible.
		*/
		BasicNotepad my_notepad = new BasicNotepad();
		my_notepad.setVisible(true);

	}
}
