package com.yayetee.yeti

import scala.swing._
import event.SelectionChanged
import javax.swing.{SwingUtilities, UIManager}
import java.awt.Dimension

object SystemProperties {
	def set(props: (String, String)*) {
		props.foreach {p => System.setProperty(p._1, p._2)}
	}
}

object Yeti extends GUIApplication {
	val tabs = List(Piast, Sumo, Preferences)

	def top = new MainFrame {
		title = "Yeti"

		val tabbedPane = new TabbedPane {
			tabs.foreach {a => pages += new TabbedPane.Page(a.title, a.gui)}
		}

		contents = new BoxPanel(Orientation.Vertical) {
			contents += tabbedPane
		}

		tabbedPane.size = new Dimension(500, 500)

		//			contents += new BorderPanel {
		//				add(new TextArea { rows = 15 }, BorderPanel.Position.Center)
		//			}
		listenTo(tabbedPane.selection)

		reactions += {
			case SelectionChanged(pane) =>
				// change frame size
				val s = tabs(tabbedPane.selection.index).size
				tabbedPane.selection.page.self.size = s
				tabbedPane.selection.page.self.repaint
				size = s
				repaint
				peer.validate
		}
	}

	def main(args: Array[String]) {

		// Quaqua properties (MacOS X only)
		SystemProperties.set()

		try {
			UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel")
		} catch {
			case e => println(e)
		}

		// taken directly from SimpleGUIApplication.scala (scala 2.7.7)
		SwingUtilities.invokeLater {
			new Runnable {
				def run {
					init;
					top.pack;
					top.visible = true
				}
			}
		}
	}
}

object Preferences extends App {
	def title = "Preferences"

	object ports extends ComboBox[String](Serial.portList) {
	}

	object buttonConnect extends Button {
		text = "Connect"
	}

	object buttonRefresh extends Button {
		text = "Refresh"
	}

	def gui = new BoxPanel(Orientation.Vertical) {
		contents += ports
		contents += new BoxPanel(Orientation.Horizontal) {
			contents += buttonConnect
			contents += buttonRefresh
		}
	}

	def size = new Dimension(500, 200)
}
