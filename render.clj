;---------------------------------------- RENDER ---------------------------------------------
(defn positions [pred coll]
	(keep-indexed (fn [idx x]
					(when (pred x)
                    idx))
                coll))	
				
(defn render-bitmap [bitmap bit color]
	(let [bit-pos (positions #{bit} bitmap)
		  rendered-bitmap (Bitmap. bitmaps-dim bitmaps-dim)]
	(doseq [b bit-pos] 
		(let [dy (quot b bitmaps-dim)
			  dx (rem b bitmaps-dim)]
		(.SetPixel rendered-bitmap dx dy color)))
	rendered-bitmap))

(defn render-lens-bitmap [bitmap bit color]
	(let [bit-pos (positions #{bit} bitmap)
		  rendered-bitmap (Bitmap. (* bitmaps-dim 2) (* bitmaps-dim 2))]
	(doseq [b bit-pos] 
		(let [dx (quot b (* bitmaps-dim 2))
			  dy (rem b (* bitmaps-dim 2))]
		(.SetPixel rendered-bitmap dx dy color)
		(.SetPixel rendered-bitmap (+ dx 1) dy color)
		(.SetPixel rendered-bitmap dx (+ dy 1) color)
		(.SetPixel rendered-bitmap (+ dx 1) (+ dy 1) color)))
	rendered-bitmap))	
				
(defn ant-render-colored-bitmap [dir]
	(let [c1 Color/Black
  	  	  c2 Color/White
		  c3 (Color/FromArgb 255 150 150 150)
		  c4 (Color/FromArgb 255 100 100 100)
		  c5 Color/Orange
		  c6 Color/White]
	{:contur (render-bitmap (ant-dir-bitmaps dir) 1 c1)
	 :fill-default (render-bitmap (ant-dir-bitmaps dir) 5 c1)
	 :hotspot-default (render-bitmap (ant-dir-bitmaps dir) 4 c4)
	 :fill-super-pher (render-bitmap (ant-dir-bitmaps dir) 5 c5)
	 :hotspot-super-pher (render-bitmap (ant-dir-bitmaps dir) 4 c6)
	 :sugar (render-bitmap (ant-dir-bitmaps dir) 2 c2)	
	 :sugar-hotspot (render-bitmap (ant-dir-bitmaps dir) 3 c3)	
	 :alter-contur (render-bitmap (alter-ant-dir-bitmaps dir) 1 c1)
	 :alter-fill-default (render-bitmap (alter-ant-dir-bitmaps dir) 5 c1)
	 :alter-hotspot-default (render-bitmap (alter-ant-dir-bitmaps dir) 4 c4)
	 :alter-fill-super-pher (render-bitmap (alter-ant-dir-bitmaps dir) 5 c5)
	 :alter-hotspot-super-pher (render-bitmap (alter-ant-dir-bitmaps dir) 4 c6)
	 :alter-sugar (render-bitmap (alter-ant-dir-bitmaps dir) 2 c2)
	 :alter-sugar-hotspot (render-bitmap (alter-ant-dir-bitmaps dir) 3 c3)}))

(defn aphis-render-colored-bitmap [dir]
	(let [c1 Color/Black
		  c2 (ControlPaint/Light Color/Green) 	  
		  c3 (-> Color/Green ControlPaint/Light ControlPaint/Light ControlPaint/Light)]
	{:contur (render-bitmap (aphis-dir-bitmaps dir) 1 c1)
	 :alter-contur (render-bitmap (alter-aphis-dir-bitmaps dir) 1 c1)
	 :fill (render-bitmap (aphis-dir-bitmaps dir) 5 c2)
	 :alter-fill (render-bitmap (alter-aphis-dir-bitmaps dir) 5 c2)
	 :hotspot (render-bitmap (aphis-dir-bitmaps dir) 4 c3)
	 :alter-hotspot (render-bitmap (alter-aphis-dir-bitmaps dir) 4 c3)}))

(defn aphis-eggs-render-colored-bitmap []
	(let [c1 Color/Black
		  c2 (ControlPaint/Light Color/Green) 	  
		  c3 (-> Color/Green ControlPaint/Light ControlPaint/Light ControlPaint/Light)]
	{:contur (render-bitmap aphis-eggs-bitmap 1 c1)
	 :fill (render-bitmap aphis-eggs-bitmap 5 c2)
	 :hotspot (render-bitmap aphis-eggs-bitmap 4 c3)}))
		
(defn ladybug-render-colored-bitmap [dir]
	(let [c1 Color/Black
		  c2 (Color/FromArgb 255 100 100 100)
		  c3 Color/Red
		  c4 Color/White
		  c5 (Color/FromArgb 255 255 100 100)]
    {:contur (render-bitmap (ladybug-dir-bitmaps dir) 1 c1)
	 :alter-contur (render-bitmap (alter-ladybug-dir-bitmaps dir) 1 c1)
	 :fill-red (render-bitmap (ladybug-dir-bitmaps dir) 6 c3)
	 :fill-white (render-bitmap (ladybug-dir-bitmaps dir) 7 c4)
	 :alter-fill-red (render-bitmap (alter-ladybug-dir-bitmaps dir) 6 c3)
	 :alter-fill-white (render-bitmap (alter-ladybug-dir-bitmaps dir) 7 c4)
	 :hotspot-red (render-bitmap (ladybug-dir-bitmaps dir) 8 c5)
	 :alter-hotspot-red (render-bitmap (alter-ladybug-dir-bitmaps dir) 8 c5)
	 :hotspot-black (render-bitmap (ladybug-dir-bitmaps dir) 4 c2)
	 :alter-hotspot-black (render-bitmap (alter-ladybug-dir-bitmaps dir) 4 c2)}))
	
(defn cached-bitmaps [render-colored-bitmap] 
			(reduce (fn [x y] (conj x {y (render-colored-bitmap y)})) {} (range 8)))
			
(def ant-cached-bitmaps  
	(cached-bitmaps ant-render-colored-bitmap))
(def aphis-cached-bitmaps  
	(cached-bitmaps aphis-render-colored-bitmap))
(def ladybug-cached-bitmaps  
	(cached-bitmaps ladybug-render-colored-bitmap))
(def aphis-eggs-cached-bitmaps  
	(aphis-eggs-render-colored-bitmap))
	
(defn add-rectangle-to-cell [x y dx dy size #^Color c]
	(swap! rectangles-in-cells conj {:x x :y y :dx dx :dy dy :size size :c c}))

(defn add-rendered-bitmap [point rendered-bitmap]
	(swap! rendered-bitmaps conj {:point point :rendered-bitmap rendered-bitmap}))	

(defn alter-bitmap [dir cached-bitmaps keyword alter-keyword]
	(let [render-colored-bitmap (cached-bitmaps dir)]
		(if (chance? 2) (render-colored-bitmap keyword) (render-colored-bitmap alter-keyword))))
	
(defn alter-ant-bitmap [ant keyword alter-keyword]
	(alter-bitmap (:dir ant) ant-cached-bitmaps keyword alter-keyword))

(defn alter-aphis-bitmap [aphis keyword alter-keyword]
	(alter-bitmap (:dir aphis) aphis-cached-bitmaps keyword alter-keyword))

(defn alter-ladybug-bitmap [ladybug keyword alter-keyword]
	(alter-bitmap (:dir ladybug) ladybug-cached-bitmaps keyword alter-keyword))	

(defn render-ant [ant x y]
	(let [	point (Point. (* x scale) (* y scale))
			contur (alter-ant-bitmap ant :contur :alter-contur)
			fill-default (alter-ant-bitmap ant :fill-default :alter-fill-default)
			hotspot-default (alter-ant-bitmap ant :hotspot-default :alter-hotspot-default)
			fill-super-pher (alter-ant-bitmap ant :fill-super-pher :alter-fill-super-pher)
			hotspot-super-pher (alter-ant-bitmap ant :hotspot-super-pher :alter-hotspot-super-pher)
			sugar (alter-ant-bitmap ant :sugar :alter-sugar)
			sugar-hotspot (alter-ant-bitmap ant :sugar-hotspot :alter-sugar-hotspot)
			fill (if (:super-pher ant) fill-super-pher fill-default)
			hotspot (if (:super-pher ant) hotspot-super-pher hotspot-default)]
	(when ant
		(add-rendered-bitmap point contur)
		(when (:sugar ant)
			(do
				(add-rendered-bitmap point sugar)
				(add-rendered-bitmap point sugar-hotspot)))
		(add-rendered-bitmap point fill)
		(add-rendered-bitmap point hotspot))))
		
	
(defn render-aphis [aphis x y]
	(let [	point (Point. (* x scale) (* y scale))
			contur (alter-aphis-bitmap aphis :contur :alter-contur)
			fill (alter-aphis-bitmap aphis :fill :alter-fill)
			hotspot (alter-aphis-bitmap aphis :hotspot :alter-hotspot)]
	(when aphis
		(add-rendered-bitmap point contur)
		(add-rendered-bitmap point fill)
		(add-rendered-bitmap point hotspot))))
		
(defn render-aphis-eggs [aphis-eggs x y]
	(let [	point (Point. (* x scale) (* y scale))
			contur (aphis-eggs-cached-bitmaps :contur)
			fill (aphis-eggs-cached-bitmaps :fill)
			hotspot (aphis-eggs-cached-bitmaps :hotspot)]
	(when aphis-eggs
		(add-rendered-bitmap point contur)
		(add-rendered-bitmap point fill)
		(add-rendered-bitmap point hotspot))))		
	
(defn render-ladybug [ladybug x y]
	(let [	point (Point. (* x scale) (* y scale))
			contur (alter-ladybug-bitmap ladybug :contur :alter-contur)
			fill-red (alter-ladybug-bitmap ladybug :fill-red :alter-fill-red)
			fill-white (alter-ladybug-bitmap ladybug :fill-white :alter-fill-white)
			hotspot-red (alter-ladybug-bitmap ladybug :hotspot-red :alter-hotspot-red)
			hotspot-black (alter-ladybug-bitmap ladybug :hotspot-black :alter-hotspot-black)]
	(when ladybug
		(add-rendered-bitmap point contur)
		(add-rendered-bitmap point fill-red)
		(add-rendered-bitmap point fill-white)
		(add-rendered-bitmap point hotspot-red)
		(add-rendered-bitmap point hotspot-black))))

		
(defn render-place [p x y]
	(when (:anthill p)
		(add-rectangle-to-cell x y 0 0 scale (Color/FromArgb 255 115 61 0)))	
	(when (pos? (:sugar p))
		(add-rectangle-to-cell x y 0 0 scale 
			(Color/FromArgb 50 (Color/White)))
		(add-rectangle-to-cell x y 1 1 (- scale 2)
			(Color/FromArgb (min 255 (+ 5 (* 25 (int (/ (* (:sugar p) 10) sugar-scale))))) (Color/White))))
	(when (pos? (:herb p))
		(add-rectangle-to-cell x y 0 0 scale 
			(Color/FromArgb 50 (Color/Green)))
		(add-rectangle-to-cell x y 1 1 (- scale 2)
			(Color/FromArgb (min 255 (+ 5 (* 25 (int (/ (* (:herb p) 10) 100))))) (Color/Green))))

	(when (:problem p)
		(add-rectangle-to-cell x y 0 0 scale (Color/Red)))
		
	(when (:aphis-eggs p)
		(render-aphis-eggs (:aphis-eggs p) x y))
	(let 
		[bug (:bug p)]
	(when bug
		(when (= (:type bug) :ant)
			(render-ant bug x y))
		(when (= (:type bug) :aphis) 
			(render-aphis bug x y))
		(when (= (:type bug) :ladybug)
			(render-ladybug bug x y))))
	(when (pos? (:pher p))
			(let [c (if (> (:pher p) 1000)
						Color/Yellow
						(Color/FromArgb (min 255 (:pher p)) (Color/Blue)))]
			(add-rectangle-to-cell x y 8 8 4 c))))

(defn render [panel]
	(let [graphics (.CreateGraphics panel)
		  context (BufferedGraphicsManager/Current)
		  rect (new Rectangle 0 0 (* scale dim) (* scale dim)) 
		  bg-brush (SolidBrush. bgcolor)
		  image (Bitmap. size size)
		  image-graphics (Graphics/FromImage image)
		  buffer (.Allocate context graphics rect)
		  buffer-graphics (.Graphics buffer)]
	(.FillRectangle buffer-graphics bg-brush rect) 
	(.FillRectangle image-graphics bg-brush rect) 
	(doseq [v @rectangles-in-cells] 
		(let [brush (SolidBrush. (v :c))]
		(.FillRectangle buffer-graphics brush (+ (* (v :x) scale) (v :dx)) (+ (* (v :y) scale) (v :dy)) (v :size) (v :size))
		(.FillRectangle image-graphics brush (+ (* (v :x) scale) (v :dx)) (+ (* (v :y) scale) (v :dy)) (v :size) (v :size))))
	(doseq [v @rendered-bitmaps] 
		(.DrawImage buffer-graphics (v :rendered-bitmap) (v :point))
		(.DrawImage image-graphics (v :rendered-bitmap) (v :point)))
	[buffer graphics image]))  
	
(def buf-graph (atom nil))	
(def mouse-pos (atom [0 0]))
(def show-lens? (atom false))

(defn render-lens [] 
			(when @buf-graph 
				(let [pen (Pen. Color/Black)
					  mouse-x (@mouse-pos 0)
					  mouse-y (@mouse-pos 1)
					  x (/ mouse-x scale)
					  y (/ mouse-y scale)
					  lens-x (* (- x 4) scale) 
					  lens-y (* (- y 4) scale)
					  rect (Rectangle. (- lens-x 2) (- lens-y 2) (+ 2 (* scale 9)) (+ 2 (* scale 9)))
					  p (place [x y])
					  buffer-graphics (.Graphics (@buf-graph 0))
					  lens-bitmap (Bitmap. (* scale 6) (* scale 6))
					  image (Bitmap. (* scale 9) (* scale 9))
					  image-graphics (Graphics/FromImage image)
					  point (Point. lens-x  lens-y)]
				(when (and (> x 0) (> y 0) (<= x (- dim 2)) (<= y (- dim 2)))
					(.DrawRectangle buffer-graphics pen rect)
					(doseq [i (range (* (- x 1) scale) (+ (* (- x 1) scale) (* scale 3)))
							j (range (* (- y 1) scale) (+ (* (- y 1) scale) (* scale 3)))]
						(let [color (.GetPixel (@buf-graph 2) i j)
							  brush (SolidBrush. color)]
						(.FillRectangle image-graphics brush 
							(Rectangle. (* (- i (* (- x 1) scale)) 3) (* 3 (- j (* (- y 1) scale))) 3 3))))
					(.DrawImage buffer-graphics image point))
				[(@buf-graph 0) (@buf-graph 1) (@buf-graph 2)])))
