import java.awt.*;

import javax.swing.*;
import javax.sound.midi.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;
import java.awt.event.*;
/**
 *
 * @author ASHUTOSH CHITRANSHI
 */
public class Main {
	JPanel mainPanel;
	ArrayList<JCheckBox> checkBoxList;
	Sequencer sequencer;
	Sequence sequence;
	Track track;
	JFrame theFrame;
	JTextArea textField;
	String instrumentNames[] = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo",
			"Marcas","Whistle", "Low Conga", "Cowbell", "Vibraslap", "Low-mid Tom","High Agogo","Open Hi Conga"};
	int instrument[] = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    	new Main().buildGUI();
    }
    public void buildGUI()
    {
    	theFrame = new JFrame("BeatBox");
    	theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	BorderLayout layout = new BorderLayout();
    	JPanel background = new JPanel(layout);
    	background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	
    	checkBoxList = new ArrayList<JCheckBox>();
    	Box buttonBox = new Box(BoxLayout.Y_AXIS);
    	
    	JButton start = new JButton("Start");
    	start.addActionListener(new MyStatListener());
    	buttonBox.add(start);
    	
    	JButton stop = new JButton("Stop");
    	stop.addActionListener(new MyStopListener());
    	buttonBox.add(stop);
    	
    	JButton upTempo = new JButton("Tempo Up");
    	upTempo.addActionListener(new MyUpTempoListener());
    	buttonBox.add(upTempo);
    	
    	JButton downTempo = new JButton("Tempo Down");
    	downTempo.addActionListener(new MyDownTempoListener());
    	buttonBox.add(downTempo);
    	
    	JButton save = new JButton("Save");
    	save.addActionListener(new MySendListener());
    	buttonBox.add(save);
    	
    	textField = new JTextArea();
    	buttonBox.add(textField);
    	
    	Box nameBox = new Box(BoxLayout.Y_AXIS);
    	for(int i=0;i<16;i++)
    		nameBox.add(new Label(instrumentNames[i]));
    	
    	background.add(BorderLayout.EAST,buttonBox);
    	background.add(BorderLayout.WEST,nameBox);
    	
    	theFrame.getContentPane().add(background);
    	
    	GridLayout grid = new GridLayout(16,16);
    	grid.setVgap(1);
    	grid.setHgap(2);
    	mainPanel = new JPanel(grid);
    	background.add(BorderLayout.CENTER,mainPanel);
    	
    	for(int i=0;i<256;i++)
    	{
    		JCheckBox c = new JCheckBox();
    		c.setSelected(false);
    		checkBoxList.add(c);
    		mainPanel.add(c);
    	}
    	
    	setUpMidi();
    	
    	theFrame.setBounds(50,50,300,300);
    	theFrame.pack();
    	theFrame.setVisible(true);
    }
	public void setUpMidi() {
		// TODO Auto-generated method stub
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequence = new Sequence(Sequence.PPQ,4);
			track = sequence.createTrack();
			sequencer.setTempoInBPM(120);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void buildTrackAndStart()
	{
		int [] trackList =null;
		sequence.deleteTrack(track);
		track = sequence.createTrack();
		
		for(int i=0;i<16;i++)
		{
			trackList = new int[16];
			int key = instrument[i];
			for(int j=0;j<16;j++)
			{
				JCheckBox jc = (JCheckBox)checkBoxList.get(j+16*i);
				if(jc.isSelected()) {
					trackList[j] = key;
				}
				else
				{
					trackList[j] = 0;
				}
			}
			makeTracks(trackList);
			track.add(makeEvent(176,1,127,0,16));
		}
		track.add(makeEvent(192,9,1,0,15));
		try {
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
			sequencer.start();
			sequencer.setTempoInBPM(120);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
		// TODO Auto-generated method stub
		MidiEvent event = null;
		try {
			ShortMessage a = new ShortMessage();
			a.setMessage(comd,chan,one,two);
			event = new MidiEvent(a,tick);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return event;
	}
	public void makeTracks(int[] list) {
		// TODO Auto-generated method stub
		for(int i=0;i<16;i++)
		{
			int key = list[i];
			if(key!=0)
			{
				track.add(makeEvent(144,9,key,100,i));
				track.add(makeEvent(128,9,key,100,i+1));
			}
		}
	}
	public class MyStopListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			sequencer.stop();
		}
		
	}
	public class MyStatListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			buildTrackAndStart();
		}
		
	}
	public class MyUpTempoListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor*1.03));
		}
		
	}
	public class MyDownTempoListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor*0.97));
		}
	}
	public class MySendListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			char[] checkboxState = new char[256];
			for(int i=0;i<256;i++)
			{
				JCheckBox check = (JCheckBox)checkBoxList.get(i);
				if(check.isSelected())
				{
					checkboxState[i] = 'T';
				}
				else
				{
					checkboxState[i] = 'F';
				}
			}
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/music?user=root&password=ashutosh");
				String query = "insert into song values(?,?)";
				PreparedStatement st = cn.prepareStatement(query);
				st.setString(1,textField.getText());
				st.setString(2, checkboxState.toString());
				st.executeUpdate();
				cn.close();
				JOptionPane.showMessageDialog(null, "Saved");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}	
	}
}
