;---------------------------------------- FORM ---------------------------------------------
(def current-wins (atom nil))
(def win-app (agent nil))
(def winforms-app-inited? (atom false))

(def chart (atom nil))
(def series (atom nil))

(defn get-series [series-name]
	(first (filter 
		(fn [x] 
			(if (= (. x Name) series-name) true false)) @series)))
			
(defn add-xy [series-name x y]
	(let [series (get-series series-name)]
	(when series
		(.AddXY (.Points series) x y)
		(when (> (.Count (.Points series)) 500) (.RemoveAt (.Points series) 0)))))			

(defn create-series [chart]	
	(let 
		[series1 (. chart Series)]
	(.Add series1 "herb")
	(.Add series1 "sugar")
	(.Add series1 "anthill-sugar")
	(.Add series1 "aphises")

	(doseq [s series1]
		(doto s 
			(.set_ChartType SeriesChartType/Spline)
			(.set_IsVisibleInLegend true)
			))
	(reset! series series1)	
	(doto (get-series "herb")
		(.set_Color Color/Green)
		(.set_LegendText "Herb")
		)
	(doto (get-series "sugar")
		(.set_Color Color/White)
		(.set_LegendText "Free sugar")
		)
	(doto (get-series "anthill-sugar")
		(.set_Color (Color/FromArgb 255 115 61 0))
		(.set_LegendText "Anthill sugar")
		)  
	(doto (get-series "aphises")
		(.set_Color (ControlPaint/Light Color/Green))
		(.set_LegendText "Aphises")
		)))  	

(defn chart-update[chart]
	(add-xy "anthill-sugar" @world-time (anthill-sugar-calc))
	(add-xy "herb" @world-time (herb-calc))
	(add-xy "sugar" @world-time (free-sugar-calc))
	(add-xy "aphises" @world-time (count @aphises))
	
	(let [chart-areas (. chart ChartAreas)
		  chart-area (first chart-areas)
		  axis-x (. chart-area AxisX)]
		(doto axis-x 
			(.set_Minimum (if (> @world-time 500) (- @world-time 500) 0))
			(.set_Maximum (if (> @world-time 500) @world-time 500)))))
		
(defn create-form []
	(let [form (Form.)
		  panel (Panel.)
		  animation-timer (Timer.)
		  world-timer (Timer.)
		  chart1 (Chart.)
		  series1 (. chart1 Series)]
	(doto chart1
		(.set_Name "chart1")
		(.set_Location (new Point size 0))
		(.set_Size (Size. size size))
		(.set_BackColor (ControlPaint/Light  bgcolor)))

	(.Add (. chart1 ChartAreas) "MainChartArea")
	(.Add (. chart1 Legends) "Legend")
	
	(doto (first (. chart1 ChartAreas))
		(.set_BackColor bgcolor))
	
	(doto (first (. chart1 Legends))
		(.set_BackColor bgcolor))
		
	(create-series chart1)
	(reset! chart chart1)
	(chart-update chart1)
	
	(let [chart-areas (. chart1 ChartAreas)
		  chart-area (first chart-areas)
		  axis-x (. chart-area AxisX)
		  axis-y (. chart-area AxisY)]
	(doto axis-x (.set_IsStartedFromZero true))
	(doto axis-y (.set_IsStartedFromZero true)))

	(doto panel
		(.set_Location (new Point 0 0))
		(.set_Name "panel1")
		(.set_Size (Size. size size))
		(.add_Click 
			(gen-delegate EventHandler [sender args]
				(when (= (.Button args) MouseButtons/Right)
					(swap! show-lens? (fn [x] (not x))))
				(when (= (.Button args) MouseButtons/Left)
					(let   [mouse-x (@mouse-pos 0)
							mouse-y (@mouse-pos 1)
							x (/ mouse-x scale)
							y (/ mouse-y scale)
							p (place [x y])]
					(prn [x y] @p)
					(.Focus panel)))))
		(.add_MouseMove 
			(gen-delegate MouseEventHandler [sender args]
				(reset! mouse-pos [
					(* (quot (.X args) scale) scale)
					(* (quot (.Y args) scale) scale)])))
		(.add_MouseWheel 
			(gen-delegate MouseEventHandler [sender args]
				(let [f (fn [x] 
							(let 
								[new-sleep  (+ x (* 50 (/ (.Delta args) 120)))]
							(if (> new-sleep 0) new-sleep 0)))]
				(swap! ant-sleep-ms f)
				(swap! ladybug-sleep-ms f)
				(swap! aphis-sleep-ms f)
				(prn @ant-sleep-ms)))))
				
	(doto animation-timer
		(.set_Interval animation-sleep-ms)
		(.set_Enabled true)
		(.add_Tick (gen-delegate EventHandler [sender args]
			(do 			
				(when @buf-graph 
					(.Render (@buf-graph 0) (@buf-graph 1)))
				(reset! rectangles-in-cells [])
				(reset! rendered-bitmaps [])
				(let [v (vec (for [x (range dim) y (range dim)] 
								@(place [x y])))]
				(dorun 
					(for [x (range dim) y (range dim)]
						(render-place (v (+ (* x dim) y)) x y)))
				(reset! buf-graph (render panel))
				(when @show-lens? 
					(reset! buf-graph (render-lens))))))))
					
	(doto world-timer
		(.set_Interval 5000)
		(.set_Enabled true)
		(.add_Tick (gen-delegate EventHandler [sender args]
			(swap! world-time inc)
			(chart-update chart1))))			
					
	(doto (.Controls form)
		(.Add panel)
		(.Add chart1))
	(doto form
		(.set_ClientSize (Size. (* size 2)  size))
		(.set_Text "Super Ants"))
	form))

(defn init-winforms-app []
    (when-not @winforms-app-inited?
	   (Application/EnableVisualStyles)
	   (Application/SetCompatibleTextRenderingDefault false)
       (reset! winforms-app-inited? true)))
	   
(defn start-gui [x]
    (init-winforms-app)
    (reset! current-wins (create-form))
    (Application/Run @current-wins))
