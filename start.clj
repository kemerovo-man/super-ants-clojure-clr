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

;---------------------------------------- DEBUG ---------------------------------------------
(defn agent-error-log [agent err] (prn agent "error: " err))
	
;---------------------------------------- LOADING FILES ---------------------------------------------
(load "init")
(load "logic")
(load "bitmaps")
(load "render")
(load "behave")
(load "form")
(load "main")

;---------------------------------------- MAIN RUN ---------------------------------------------
(main)


