(ns gl.ex3
  (:import [org.lwjgl LWJGLException])
  (:import [org.lwjgl.opengl Display DisplayMode GL11]))

(defn draw-quad
  []
  ; Clear the screen and depth buffer
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))

  ; set the color of the quad R G B A
  (GL11/glColor3f 0.5 0.5 1.0)

  ; draw quad
  (GL11/glBegin GL11/GL_QUADS)
  (GL11/glVertex2f 100 100)
  (GL11/glVertex2f (+ 100 200) 100)
  (GL11/glVertex2f (+ 100 200) (+ 100 200))
  (GL11/glVertex2f 100 (+ 100 200))
  (GL11/glEnd)

  (Display/update))

(defn run
  (while (not (Display/isCloseRequested))
    (draw-quad))
  (Display/destroy))

(defn init
  []
  (try
    (Display/setDisplayMode
      (DisplayMode. 800 600))
    (Display/create)
    (catch LWJGLException e
      (.printStackTrace e)
      (System/exit 0)))
  ; init OpenGL
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glOrtho 0 800 0 600 1 -1)
  (GL11/glMatrixMode GL11/GL_MODELVIEW))

(defn -main
  [& args]
  (init)
  (run))


;public class QuadExample {
; 
;    public void start() {
;        try {
;	    Display.setDisplayMode(new DisplayMode(800,600));
;	    Display.create();
;	} catch (LWJGLException e) {
;	    e.printStackTrace();
;	    System.exit(0);
;	}
; 
;	// init OpenGL
;	GL11.glMatrixMode(GL11.GL_PROJECTION);
;	GL11.glLoadIdentity();
;	GL11.glOrtho(0, 800, 0, 600, 1, -1);
;	GL11.glMatrixMode(GL11.GL_MODELVIEW);
; 
;	while (!Display.isCloseRequested()) {
;	    // Clear the screen and depth buffer
;	    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	
;		
;	    // set the color of the quad (R,G,B,A)
;	    GL11.glColor3f(0.5f,0.5f,1.0f);
;	    	
;	    // draw quad
;	    GL11.glBegin(GL11.GL_QUADS);
;	        GL11.glVertex2f(100,100);
;		GL11.glVertex2f(100+200,100);
;		GL11.glVertex2f(100+200,100+200);
;		GL11.glVertex2f(100,100+200);
;	    GL11.glEnd();
; 
;	    Display.update();
;	}
; 
;	Display.destroy();
;    }
; 
;    public static void main(String[] argv) {
;        QuadExample quadExample = new QuadExample();
;        quadExample.start();
;    }
;}


