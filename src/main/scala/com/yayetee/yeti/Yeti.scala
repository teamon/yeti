package com.yayetee.yeti

import scala.swing._
import event.{ButtonClicked, SelectionChanged}
import javax.swing.{UIManager}
import java.awt.Dimension

object SystemProperties {
	def set(props: (String, String)*) {
		props.foreach {p => System.setProperty(p._1, p._2)}
	}
}

object Yeti extends SwingApplication {
	val tabs = Piast :: Sumo :: Preferences :: Nil

	lazy val logTextArea = new TextArea {
		rows = 10
		editable = false
	}

	lazy val tabbedPane = new TabbedPane {
		tabs.foreach { a =>
			pages += new TabbedPane.Page(a.title, a.gui){
//				enabled = false
			}
		}
	}

	lazy val maxWidth  = tabs.map { _.gui.peer.getPreferredSize.width }.max
	lazy val maxHeight = tabs.map{  _.gui.peer.getPreferredSize.height }.max

	def top = new MainFrame {
		title = "Yeti"

		contents = new BoxPanel(Orientation.Vertical) {
			contents += tabbedPane
			contents += new ScrollPane(logTextArea)
		}

//		tabbedPane.pages(tabs.length-1).enabled = true

//		tabbedPane.selection.index = tabs.length-1

		val originalTabsDim = tabbedPane.preferredSize

		listenTo(tabbedPane.selection)

		reactions += {
			case SelectionChanged(pane) =>
				val panelDim = tabbedPane.selection.page.self.preferredSize

		    println(panelDim)

		    tabbedPane.preferredSize = new Dimension(
		      originalTabsDim.width - (maxWidth - panelDim.width),
		      originalTabsDim.height - (maxHeight - panelDim.height)
			  )

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

	lazy val gui = new BoxPanel(Orientation.Vertical) {
		contents += ports
		contents += new BoxPanel(Orientation.Horizontal) {
			contents += buttonConnect
			contents += buttonRefresh
		}

		listenTo(buttonConnect, buttonRefresh)

//		reactions += {
////			case ButtonClicked(btn) => btn match {
////				case buttonConnect => println("connect")
////				case buttonRefresh => println("refresh")
////			}
//		}
	}
}
