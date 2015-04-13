(System.Reflection.Assembly/LoadWithPartialName "System.Windows.Forms")
(System.Reflection.Assembly/LoadWithPartialName "System.Windows.Forms.DataVisualization")

(ns super-ants
	(:use [clojure.repl])
	(:import [System.Drawing Graphics Bitmap Brush SolidBrush Color Pen Rectangle Size Point BufferedGraphicsManager])
	(:import [System.Windows.Forms ControlPaint ControlStyles MouseButtons Timer Button Panel Form DockStyle Application PaintEventHandler PaintEventArgs MouseEventHandler MouseEventArgs])
	(:import [System.Windows.Forms.DataVisualization.Charting Chart SeriesCollection SeriesChartType ChartArea])
	(:gen-class)
)
(require '[clojure.string :as str])
;(require '[clojure.reflect :as refl])

;---------------------------------------- INIT ---------------------------------------------
(def dim 40) 
(def scale 20)
(def size (* dim scale))
(def nants-sqrt 6) 
(def ladybugs-count 5)
(def aphis-count 50)
(def sugar-places 35) 
(def sugar-range 100)
(def pher-scale 20.0)
(def sugar-scale 30.0)
(def herb-scale 3)
(def evap-rate 0.8)
(def animation-sleep-ms 50) 
(def ant-sleep-ms (atom 200))
(def ladybug-sleep-ms (atom 1000))
(def aphis-sleep-ms (atom 100))
(def running true)
(def anthill-off (rand-int (- dim nants-sqrt)))
(def anthill-range 
	(range anthill-off (+ nants-sqrt anthill-off)))
(def bgcolor Color/Gray)

;---------------------------------------- DEBUG ---------------------------------------------
(defn agent-error-log [agent err] (prn agent "error: " err))
	
;---------------------------------------- LOADING FILES ---------------------------------------------
(load-file "D:/clojure/ants2/logic.clj")
(load-file "D:/clojure/ants2/bitmaps.clj")
(load-file "D:/clojure/ants2/render.clj")
(load-file "D:/clojure/ants2/behave.clj")
(load-file "D:/clojure/ants2/form.clj")

(defn newborn-aphises-process [_]
	(let [nb-aphises (aphises-born)]
	(when (> (count nb-aphises) 0)
			(dorun (map #(set-error-handler! % agent-error-log) nb-aphises))
			(dorun (map #(send-off % behave-aphis) nb-aphises)))
	(sleep 5000))
	(send-off *agent* newborn-aphises-process)
	nil)

;---------------------------------------- MAIN ---------------------------------------------
(defn main []
	(let [s (setup)]
		(reset! ants (:ants s))
		(reset! ladybugs (:ladybugs s))
		(reset! aphises (:aphises s)))
			
	(dorun (map #(set-error-handler! % agent-error-log) @ants))
	(dorun (map #(set-error-handler! % agent-error-log) @ladybugs))
	(dorun (map #(set-error-handler! % agent-error-log) @aphises))

	(dorun (map #(send-off % behave-ant 0) @ants))
	(dorun (map #(send-off % behave-ladybug) @ladybugs))
	(dorun (map #(send-off % behave-aphis) @aphises))

	(send-off newborn-aphises-agent newborn-aphises-process)
	(set-error-handler! newborn-aphises-agent agent-error-log)

	(send-off win-app start-gui)
	(set-error-handler! win-app agent-error-log))

;---------------------------------------- MAIN RUN ---------------------------------------------
(main)

