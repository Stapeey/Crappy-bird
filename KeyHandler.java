import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed;

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
      
        int code = e.getKeyCode();

        if (code==KeyEvent.VK_W){
            upPressed = true;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        
        int code = e.getKeyCode();

        if (code==KeyEvent.VK_W){
            upPressed = false;
        }
    }
    
}

