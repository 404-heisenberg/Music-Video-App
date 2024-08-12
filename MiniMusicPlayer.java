import javax.sound.midi.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

/*
App that draws random rectangles on the screen with the beat of the music.
The program listens for controller events which indicate that a beat has been hit.
When the event is received, the program sets a flag to true and draws a random rectangle.
 */

public class MiniMusicPlayer {
    static JFrame f = new JFrame("My First Music Video");
    static MyDrawPanel ml;

    public static void main(String[] args) {
        MiniMusicPlayer mini = new MiniMusicPlayer();
        mini.go();
    }

    public void setUpGui() {
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ml = new MyDrawPanel();
        f.setContentPane(ml);
        f.setBounds(30,30, 300,300);
        f.setVisible(true);
    }

    public void go() {
        setUpGui();
        try {
            // Sequencer is the thing that plays the sequence
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            // Register for events with the sequencer
            sequencer.addControllerEventListener(ml, new int[] {127});
            Sequence seq = new Sequence(Sequence.PPQ, 4);
            Track track = seq.createTrack();
            int r = 0;
            // Create a bunch of events (i is the beat of the music)
            for (int i = 0; i < 60; i+= 4) {
                // Play random piano note
                r = (int) ((Math.random() * 50) + 1);
                // Add a note on (144) event
                track.add(makeEvent(144,1,r,100,i));
                // Add a controller event (176) that will fire when the note is played
                track.add(makeEvent(176,1,127,0,i));
                // Add a note off (128) event, which closes the note on event (144) and stops the sound from playing 2 beats later
                track.add(makeEvent(128,1,r,100,i + 2));
            }
            // Start the sequencer
            sequencer.setSequence(seq);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception ex) {ex.printStackTrace();}
    }

    // Make an event
    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        // comd is the command, chan is the channel, one and two are the data values
        // tick is the time in the music where the event should happen
        MidiEvent event = null;
        try {
            // Create a message
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        }catch(Exception e) { }
        return event;
    }

    // Inner ActionListener class to listen for controller events
    class MyDrawPanel extends JPanel implements ControllerEventListener {
        boolean msg = false;

        public void controlChange(ShortMessage event) {
            // Only repaint if we get a message event
            msg = true;
            repaint();
        }

        public void paintComponent(Graphics g) {
            if (msg) {
                // get random colours
                int r = (int) (Math.random() * 250);
                int gr = (int) (Math.random() * 250);
                int b = (int) (Math.random() * 250);
                g.setColor(new Color(r, gr, b));

                // Get random size and position for rectangles
                int ht = (int) ((Math.random() * 120) + 10);
                int width = (int) ((Math.random() * 120) + 10);
                int x = (int) ((Math.random() * 40) + 10);
                int y = (int) ((Math.random() * 40) + 10);
                g.fillRect(x, y, ht, width);
                msg = false;
            }
        }
    }
}
