(ns gl.ex1
  (:import [org.lwjgl LWJGLException])
  (:import [org.lwjgl.opengl Display DisplayMode]))

(defn init
  []
  (try
    (Display/setDisplayMode (DisplayMode. 800 600))
    (Display/create)
    (catch LWJGLException e
      (.printStackTrace e)
      (System/exit 0))))

(defn run
  []
  (while (not (Display/isCloseRequested))
    (Display/update))
  (Display/destroy))

(defn -main
  [& args]
  (init)
  (run))

;
;public class DisplayExample {
;    public void start() {
;        try {
;            org.lwjgl.opengl.Display.setDisplayMode(new DisplayMode(800,600));
;            Display.create();
;        } catch (org.lwjgl.LWJGLException e) {
;            e.printStackTrace();
;            System.exit(0);
;        }
;        
;        // init OpenGL here
;        
;        while (!Display.isCloseRequested()) {
;            
;            // render OpenGL here
;            
;            Display.update();
;        }
;        
;        Display.destroy();
;    }
;    
;    public static void main(String[] argv) {
;        DisplayExample displayExample = new DisplayExample();
;        displayExample.start();
;    }
;}
