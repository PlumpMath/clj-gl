(ns gl.ex5
  (:import [org.lwjgl LWJGLException Sys])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl.opengl Display DisplayMode GL11]))

(def x 400)
(def y 300)
(def rotation 0)
(def last-frame 0)
(def fps 0)
(def last-fps 0)
(def vsync true)

(defn init
  []
  (try
    (Display/setDisplayMode (DisplayMode. 800 600))
    (Display/create)
    (catch LWJGLException e
      (.printStackTrace e)
      (System/exit 0)))
  (init-gl)
  (get-delta)
  (def last-fps (get-time)))

(defn run
  []
  (while (Display/isCloseRequested)
    (Display/destroy)
    (update (get-delta))
    (render-gl)
    (Display/update)
    (Display/sync 60))
  (Display/destroy))

(defn update
  [delta]
  ; rotate quad
  (def rotation (+ rotation (* 0.15 delta)))
  (when (Keyboard/isKeyDown Keyboard/KEY_LEFT)  (def x (- x (* 0.35 delta))))
  (when (Keyboard/isKeyDown Keyboard/KEY_RIGHT) (def x (+ x (* 0.35 delta))))
  (when (Keyboard/isKeyDown Keyboard/KEY_UP)    (def y (- y (* 0.35 delta))))
  (when (Keyboard/isKeyDown Keyboard/KEY_DOWN)  (def y (+ y (* 0.35 delta))))
  (while (Keyboard/next)
    (if (Keyboard/getEventKeyState)
      (let [k (Keyboard/getEventKeyState)]
        (condp = k
          Keyboard/KEY_F (set-display-mode 800 600 (not (Display/isFullscreen)))
          Keyboard/KEY_V (do
                           (def vsync (not vsync))
                           (Display/setVSyncEnabled vsync))
          nil))))
  ; keep quad on the screen
  (when (< x 0)   (def x 0))
  (when (> x 800) (def x 800))
  (when (< y 0)   (def y 0))
  (when (> y 600) (def y 600))
  (update-fps))

;; Set the display mode to be used 
;; @param width The width of the display required
;; @param height The height of the display required
;; @param fullscreen True if we want fullscreen mode

(defn set-display-mode
  [width height fullscreen]
  ; return if requested DisplayMode is already set
  (when-not (and (= (.. Display/getDisplayMode getWidth) width)
                 (= (.. Display/getDisplayMode getHeight) height)
                 (= (Display/isFullscreen) fullscreen))
    (try
      (let [target-display-mode nil]
        (if fullscreen
          (let [modes (Display/getAvailableDisplayModes)]
            (doseq [current modes]
              (when (and (= (.getWidth current) width)
                         (= (.getHeight current) height))
                (when (or (nil? target-display-mode)
                          (>= (.getFrequency current) freq))
                  (when (or (nil? target-display-mode)
                            (> (.getBitsPerPixel current) (.getBitsPerPixel target-display-mode)))
                    (def target-display-mode current)
                    (def freq (.getFrequency target-display-mode)))))

						// if we've found a match for bpp and frequence against the 
						// original display mode then it's probably best to go for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
						    (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width,height);
			}
			
			if (targetDisplayMode == null) {
				System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
				return;
			}

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);
			
		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
		}
	}
	
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public int getDelta() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
 
	    return delta;
	}
 
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
 
	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
 
	public void initGL() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
 
	public void renderGL() {
		// Clear The Screen And The Depth Buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
 
		// R,G,B,A Set The Color To Blue One Time Only
		GL11.glColor3f(0.5f, 0.5f, 1.0f);
 
		// draw quad
		GL11.glPushMatrix();
			GL11.glTranslatef(x, y, 0);
			GL11.glRotatef(rotation, 0f, 0f, 1f);
			GL11.glTranslatef(-x, -y, 0);
 
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(x - 50, y - 50);
				GL11.glVertex2f(x + 50, y - 50);
				GL11.glVertex2f(x + 50, y + 50);
				GL11.glVertex2f(x - 50, y + 50);
			GL11.glEnd();
		GL11.glPopMatrix();
	}
 
	public static void main(String[] argv) {
		FullscreenExample fullscreenExample = new FullscreenExample();
		fullscreenExample.start();
	}
}
