package com.yayetee.yeti

import scala.swing._
import scala.swing.event.{ButtonClicked, SelectionChanged}
import javax.swing.UIManager

object SystemProperties {
	def set(props: (String, String)*) {
		props.foreach {p => System.setProperty(p._1, p._2)}
	}
}

object Yeti extends SwingApplication {
	val tabs = Piast :: Sumo :: Nil

//	var serial :Serial

	def log(s: String) {
		logTextArea.append("[INFO] " + s + "\n")
	}
  
	lazy val logTextArea = new TextArea {
		rows = 10
		editable = false
	}

	lazy val tabbedPane = new TabbedPane {
		tabs.foreach { a => pages += new TabbedPane.Page(a.title, a.gui) }

		val originalPreferredSize = preferredSize
		
		lazy val maxPreferredWidth = pages.map {_.self.preferredSize.width}.max

		lazy val maxPreferredHeight = pages.map {_.self.preferredSize.height}.max

		def updatePreferredSize {
			val size = selection.page.self.preferredSize
			preferredSize = (
					originalPreferredSize.width - (maxPreferredWidth - size.width),
					originalPreferredSize.height - (maxPreferredHeight - size.height)
			)
		}
	}

	def top = new MainFrame {
		title = "Yeti"

		contents = new BoxPanel(Orientation.Vertical) {
			contents += tabbedPane
			contents += new ScrollPane(logTextArea)
		}

		listenTo(tabbedPane.selection)

		reactions += {
			case SelectionChanged(`tabbedPane`) =>
				tabbedPane.updatePreferredSize
				pack
		}
	}

	override def startup(args: Array[String]) {

		// Quaqua properties (MacOS X only)
		SystemProperties.set()

		try {
			UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel")
		} catch {
			case e => println(e)
		}

		// taken directly from SimpleSwingApplication.scala (scala 2.8.0.Beta1-RC8)
		val t = top
		t.pack()
		t.visible = true
	}


	def info(s: String) {
		logTextArea.append("[INFO] " + s + "\n")
	}
}

