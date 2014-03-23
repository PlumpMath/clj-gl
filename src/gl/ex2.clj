(ns gl.ex2
  (:import [org.lwjgl LWJGLException])
  (:import [org.lwjgl.opengl Display DisplayMode])
  (:import [org.lwjgl.input Mouse Keyboard]))

(defn poll-input
  []
  (when (Mouse/isButtonDown 0)
    (let [x (Mouse/getX)
          y (Mouse/getY)]
      (println "MOUSE DOWN @ X:" x "Y:" y)))
  (when (Keyboard/isKeyDown Keyboard/KEY_SPACE)
    (println "SPACE KEY IS DOWN"))
  (while (Keyboard/next)
    (let [key-state (Keyboard/getEventKey)]
      (if (Keyboard/getEventKeyState)
        (condp = key-state
          Keyboard/KEY_A (println "A Key Pressed")
          Keyboard/KEY_S (println "S Key Pressed")
          Keyboard/KEY_D (println "D Key Pressed")
          nil)
        (condp = key-state
          Keyboard/KEY_A (println "A Key Released")
          Keyboard/KEY_S (println "S Key Released")
          Keyboard/KEY_D (println "D Key Released")
          nil)))))

(defn run
  []
  (while (not (Display/isCloseRequested))
    (poll-input)
    (Display/update))
  (Display/destroy))

(defn init
  []
  (try
    (Display/setDisplayMode (DisplayMode. 800 600))
    (Display/create)
    (catch LWJGLException e
      (.printStackTrace e)
      (System/exit 0))))

(defn -main
  [& args]
  (init)
  (run))




;import org.lwjgl.LWJGLException;
;import org.lwjgl.input.Keyboard;
;import org.lwjgl.input.Mouse;
;import org.lwjgl.opengl.Display;
;import org.lwjgl.opengl.DisplayMode;
;
;public class InputExample {
;
;    public void start() {
;        try {
;       Display.setDisplayMode(new DisplayMode(800, 600));
;       Display.create();
;   } catch (LWJGLException e) {
;       e.printStackTrace();
;       System.exit(0);
;   }
;
;   // init OpenGL here
;
;        while (!Display.isCloseRequested()) {
;
;       // render OpenGL here
;           
;       pollInput();
;       Display.update();
;   }
;
;   Display.destroy();
;    }
;
;    public void pollInput() {
;       
;        if (Mouse.isButtonDown(0)) {
;       int x = Mouse.getX();
;       int y = Mouse.getY();
;           
;       System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);
;   }
;       
;   if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
;       System.out.println("SPACE KEY IS DOWN");
;   }
;       
;   while (Keyboard.next()) {
;       if (Keyboard.getEventKeyState()) {
;           if (Keyboard.getEventKey() == Keyboard.KEY_A) {
;           System.out.println("A Key Pressed");
;       }
;       if (Keyboard.getEventKey() == Keyboard.KEY_S) {
;           System.out.println("S Key Pressed");
;       }
;       if (Keyboard.getEventKey() == Keyboard.KEY_D) {
;           System.out.println("D Key Pressed");
;       }
;       } else {
;           if (Keyboard.getEventKey() == Keyboard.KEY_A) {
;           System.out.println("A Key Released");
;           }
;           if (Keyboard.getEventKey() == Keyboard.KEY_S) {
;           System.out.println("S Key Released");
;       }
;       if (Keyboard.getEventKey() == Keyboard.KEY_D) {
;           System.out.println("D Key Released");
;       }
;       }
;   }
;    }
;
;    public static void main(String[] argv) {
;        InputExample inputExample = new InputExample();
;   inputExample.start();
;    }
;}


