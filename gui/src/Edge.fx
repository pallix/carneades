/*
Carneades Argumentation Library and Tools.
Copyright (C) 2008 Matthias Grabmair

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3 (GPL-3)
as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for details.
 
You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


package GraphSketch1.Graph;

import javafx.gui.*;
import GraphSketch1.Graph.*;
import java.lang.System;
import java.lang.Math;
import GraphSketch1.Control.AbstractGraphControl;

public class Edge extends GraphElement {
	attribute producer: Vertex; // The vertex from which the directed edge is originating ...
	attribute recipient: Vertex; // ... and to which vertex it is going.
	attribute x1Shift: Number = 0; 
	attribute x2Shift: Number = 0; 
	attribute y1Shift: Number = 0; // Shift to where the edge LINE should stop
	attribute y2Shift: Number = 0;
	attribute yHeadShift: Number = 0; // Additional Shift for the HEAD (Arrow, etc.) size to adjust additionally
	attribute xHeadShift: Number = 0;
	attribute x1: Number = bind producer.x + x1Shift on replace { if (turnHead) setAngle() };
	attribute y1: Number = bind producer.y + y1Shift on replace { if (turnHead) setAngle() };
	attribute x2: Number = bind recipient.x + x2Shift + xHeadShift on replace { if (turnHead) setAngle() };
	attribute y2: Number = bind recipient.y + y2Shift + yHeadShift on replace { if (turnHead) setAngle() };
	attribute direction: Number = GC.BOTTOM; // it is assumed the premise hits the node on its bottom side
	attribute stroke: Color = Color.BLACK;
	attribute strokeWidth: Number = GC.edgeStrokeWidth;

	attribute control: AbstractGraphControl;

	// attributes for optional heads
	attribute turnHead: Boolean = false;
	attribute angle: Number = 0;

	// main line of the edge
	attribute edgeLine: Line = Line {
					x1: bind x1
					y1: bind y1
					x2: bind x2
					y2: bind y2
					stroke: bind stroke
					strokeWidth: bind strokeWidth
				}
	
	// invisible wider line for easier selection
	attribute selectLine = Line {
					x1: bind x1
					y1: bind y1
					x2: bind x2
					y2: bind y2
					stroke: bind GC.transparent
					strokeWidth: bind strokeWidth + GC.edgeSelectionWidth;

					onMouseClicked: function(e: MouseEvent) {
						control.unSelectAll();
						selected = true;
						control.processSelection();
					}
				}

	// newly colored line once selected
	attribute selection: Line = Line {
					x1: bind x1
					y1: bind y1
					x2: bind x2
					y2: bind y2
					stroke: bind GC.selectionColor
					strokeWidth: bind GC.selectedEdgeWidth;
					visible: bind selected
				}

	attribute line: Group = Group {
		content: [
			edgeLine, selection, selectLine
		]
	}


	private function setAngle():Void {
		// determine arrowhead angle
		// This code is highly redundant and suboptimal, but it was the only way which worked for "Transform.rotate"
		angle = 0;

		if (x1 == x2) {
			if (y2 > y1) { // vertical arrow
				angle = 90; // arrow top down
			} else {
				angle = -90; // arrow bottom up
			}
		} else 
		if (y1 == y2) { // horizontal arrow
			if (x2 > x1) { // arrow pointing right
				angle = 0;
			} else { // arrow pointing left
				angle = 180;
			}
		} else // tilted arrow
		if (x1 < x2) {
			// arrow pointing right
			angle = ((Math.atan((Math.abs(y2-y1))/(x2-x1)) / (Math.PI/2)) * 90);
			if (y2 < y1) angle *= -1;
		} else {
			angle = ((Math.atan((Math.abs(y2-y1))/(x2-x1)) / (Math.PI/2)) * 90 + 180);
			if (y2 < y1) angle *= -1;
		}
	}

	public function create():Node {
		Group {
			content: [
				line,
				/*Circle {
					fill: Color.RED
					radius: 3
					cx: bind x1
					cy: bind y1
				},
				Circle {
					fill: Color.GREEN
					radius: 3
					cx: bind x2
					cy: bind y2
				},*/
			] // content
		} // Group
	} // composeNode

	public function print():Void {
		System.out.println(producer.content + " -> " + recipient.content + ": " + x1 + " / " + y1 + " -> " + x2 + " / " + y2 + " D: " + direction + " y2S: " + y2Shift);
	}
}
